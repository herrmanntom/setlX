function genMatrix(n = rand(), m = rand()) {
	var result = [];
	for(int i  in [1 .. n]) {
		var row = [];
		for(int j in [1 .. m]) {
			row.add(rand());
		}
		result.add(row);
	}
	return matrix(result);
}

function calcSave(matrix) {
	var result = {
		'original': matrix
	};
	/************************
	 * TODO:
	 * EigenValue/Vector
	 * SVD-Decomposition
	 * solve
	 * difference/sum
	 * product
	 * skalarproduct
	 *
	 * both directions!
	 */
	if(isSquare(matrix)) {
		result.transpose = transpose(matrix);
		result.det = det(matrix);
		for(int i in [-10 .. 10]) {
			result.power.add({
				'exponent': i,
				'value': matrix ^ i
			});
		}
	}


	
	saveToFile(result);
}

// TODO fixed cases (borders, spezial cases);
// TODO Should fails
// TODO Vector
