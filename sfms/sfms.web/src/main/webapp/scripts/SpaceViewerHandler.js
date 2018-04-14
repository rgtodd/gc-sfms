var SpaceViewerHandler = (function() {

	// *********************************
	// **
	// ** PRIVATE
	// **
	// *********************************

	var getPointsAsync = function(callback) {

		var positions = [];
		for (var x = 0; x < 30; ++x) {
			for (var y = 0; y < 30; ++y) {
				for (var z = 0; z < 30; ++z) {
					var starX = x * 25;
					var starY = y * 25;
					var starZ = z * 25;
					positions.push(starX, starY, starZ);
				}
			}
		}

		callback(positions);
	};

	// *********************************
	// **
	// ** PUBLIC
	// **
	// *********************************

	return {

		GetPointsAsync : function(callback) {
			getPointsAsync(callback);
		}

	}

})();
