API
===

Matrix
------
Base: Two dimensional array of doubles.

- constructor function: matrix( collection of equal sized collections )
						matrix( vector )
- mult:
	- matrix * matrix = matrix
	- matrix * vector = matrix
	- matrix * number = matrix
	- number * matrix = matrix
- sum / minus:
	- matrix +/- matrix = matrix
	- matrix +/- vector = matrix
- pow:
	- matrix ** int = matrix
- functions:
	- matrixdeterminate( matrix ) = number
	- matrixsolve( matrix, matrix) = matrix
		- solves A * X = B for X : X := matrixsolve(A,B)
	- singularValueDecomposition( matrix ) = [ matrix, matrix, matrix ] // [ U, S, V ]
	- eigenValue( matrix ) = [ number, number, ... ]
	- eigenVectors( matrix ) = matrix

Vector
------
Base: Array of NumberValues (potentially arbitrary precision)

- constructor function: vector( collection )
						vector( single row or single column matrix )
- mult:
	- vector * vector = number // (scalar product)
	- vector * matrix = number // (scalar product)
	- vector * number = vector
	- number * vector = vector
- product:
	- vector ** vector = vector // (cross product)
- sum / minus:
	- vector +/- vector = vector
	- vector +/- matrix = vector
