// "Create enum constant 'A'" "false"
// ACTION: Create annotation 'A'
// ACTION: Create class 'A'
// ACTION: Create enum 'A'
// ACTION: Create interface 'A'
// ACTION: Create object 'A'
// ACTION: Do not show return expression hints
// ACTION: Rename reference
// ERROR: Unresolved reference: A
package p

import p.X.<caret>A

class X {

}