// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.kotlin.nj2k.conversions

import com.intellij.psi.PsiClass
import org.jetbrains.kotlin.nj2k.*
import org.jetbrains.kotlin.nj2k.tree.*

class RecordConversion(context: NewJ2kConverterContext) : RecursiveApplicableConversionBase(context) {
    override fun applyToElement(element: JKTreeElement): JKTreeElement {
        if (element is JKClass && element.classKind == JKClass.ClassKind.RECORD) {
            val recordHeader = element.classBody.declarations.filterIsInstance<JKKtRecordHeaderDeclaration>().firstOrNull()!!

            //recordHeader.parameters.map { it.detach(recordHeader) }
            recordHeader.parameters.forEach {
                it.detach(recordHeader)
            }

            recordHeader.fields.forEach {
                it.detach(recordHeader)
            }
            //recordHeader.invalidate()
            //element.classBody.detach(recordHeader)

            element.classBody.declarations += recordHeader.fields

            val pseudoConstructor = JKConstructorImpl(
                JKNameIdentifier(element.name.value),
                recordHeader.parameters,
                JKBlockImpl(
                    recordHeader.fields.map {
                        JKKtAssignmentStatement(
                            JKFieldAccessExpression(JKThisExpression(), it.name),
                            it.initializer
                        )
                    }
                )
                JKStubExpression(),
                JKAnnotationList(),
                emptyList(),
                JKVisibilityModifierElement(Visibility.PUBLIC),
                JKModalityModifierElement(Modality.FINAL),
            )

            element.classBody.declarations += pseudoConstructor
            element.classBody.declarations -= recordHeader

            element.classKind = JKClass.ClassKind.CLASS

            return recurse(element)
        }

        return recurse(element)
    }
}
