// "Create member property 'foo' as constructor parameter" "false"
// ACTION: Create member property 'J.foo'
// ACTION: Do not show return expression hints
// ACTION: Rename reference
// ERROR: Unresolved reference: foo

fun test() {
    val a: Int = J.<caret>foo
}

