// "Make 'SealedClass' public" "true"
// ACTION: Create test
// ACTION: Do not show return expression hints
// ACTION: Introduce import alias
// ACTION: Make 'SealedClass' public
// ACTION: Make 'Test' private

private sealed class SealedClass

class Test : <caret>SealedClass()