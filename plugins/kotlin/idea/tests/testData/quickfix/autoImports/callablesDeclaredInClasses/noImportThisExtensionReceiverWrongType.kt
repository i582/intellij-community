// "Import" "false"
// ACTION: Create function 'ext'
// ACTION: Do not show return expression hints
// ACTION: Rename reference
// ERROR: Unresolved reference: ext
package p

open class Foo {
    fun Int.ext() {}
}

object FooObject : Foo()

fun String.anotherExt() {
    <caret>ext()
}
