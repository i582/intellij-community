// "Create local variable 'foo'" "true"
// ACTION: Create local variable 'foo'
// ACTION: Create object 'foo'
// ACTION: Create parameter 'foo'
// ACTION: Create property 'foo'
// ACTION: Do not show return expression hints
// ACTION: Rename reference
// ACTION: Split property declaration

fun test() {
    val u: Unit = <caret>foo
}