// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.kotlin.nj2k.conversions

import com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.idea.base.utils.fqname.getKotlinFqName
import org.jetbrains.kotlin.j2k.ast.Nullability
import org.jetbrains.kotlin.nj2k.NewJ2kConverterContext
import org.jetbrains.kotlin.nj2k.psi
import org.jetbrains.kotlin.nj2k.symbols.JKUnresolvedClassSymbol
import org.jetbrains.kotlin.nj2k.tree.*

import org.jetbrains.kotlin.nj2k.types.JKClassType
import org.jetbrains.kotlin.nj2k.types.JKJavaVoidType
import org.jetbrains.kotlin.nj2k.types.updateNullability

class JavaStandardMethodsConversion(context: NewJ2kConverterContext) : RecursiveApplicableConversionBase(context) {
    override fun applyToElement(element: JKTreeElement): JKTreeElement {
        if (element !is JKClass) return recurse(element)
        for (declaration in element.classBody.declarations) {
            if (declaration !is JKMethodImpl) continue
            if (fixToStringMethod(declaration)) continue
            if (fixFinalizeMethod(declaration, element)) continue
            if (fixCloneMethod(declaration)) {
                val hasNoCloneableInSuperClasses =
                    declaration.psi<PsiMethod>()
                        ?.findSuperMethods()
                        ?.all { superMethod ->
                            superMethod.containingClass?.getKotlinFqName()?.asString() == "java.lang.Object"
                        } == true
                if (hasNoCloneableInSuperClasses) {
                    element.inheritance.implements +=
                        JKTypeElement(
                            JKClassType(
                                JKUnresolvedClassSymbol("Cloneable", typeFactory),
                                emptyList(), Nullability.NotNull
                            )
                        )
                }
                continue
            }
        }
        return recurse(element)
    }

    private fun fixToStringMethod(method: JKMethodImpl): Boolean {
        if (method.name.value != "toString") return false
        if (method.parameters.isNotEmpty()) return false
        val type = (method.returnType.type as? JKClassType)
            ?.takeIf { it.classReference.name == "String" }
            ?.updateNullability(Nullability.NotNull) ?: return false
        method.returnType = JKTypeElement(type, method.returnType::annotationList.detached())
        return true
    }

    private fun fixCloneMethod(method: JKMethodImpl): Boolean {
        if (method.name.value != "clone") return false
        if (method.parameters.isNotEmpty()) return false
        val type = (method.returnType.type as? JKClassType)
            ?.takeIf { it.classReference.name == "Object" }
            ?.updateNullability(Nullability.NotNull) ?: return false
        method.returnType = JKTypeElement(type, method.returnType::annotationList.detached())
        return true
    }

    private fun fixFinalizeMethod(method: JKMethodImpl, containingClass: JKClass): Boolean {
        if (method.name.value != "finalize") return false
        if (method.parameters.isNotEmpty()) return false
        if (method.returnType.type != JKJavaVoidType) return false
        if (method.hasOtherModifier(OtherModifier.OVERRIDE)) {
            method.modality =
                if (containingClass.modality == Modality.OPEN) Modality.OPEN
                else Modality.FINAL
            method.otherModifierElements -= method.otherModifierElements.first { it.otherModifier == OtherModifier.OVERRIDE }
        }
        return true
    }
}