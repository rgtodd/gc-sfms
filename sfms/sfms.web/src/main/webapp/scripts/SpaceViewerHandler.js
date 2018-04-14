"use strict";

var SpaceViewerHandler = (function() {

	var SECTOR_MIN_X = -1000;
	var SECTOR_MIN_Y = -1000;
	var SECTOR_MIN_Z = -1000;
	var SECTOR_MAX_X = 1000;
	var SECTOR_MAX_Y = 1000;
	var SECTOR_MAX_Z = 1000;
	var SECTOR_SIZE = 200;

	// *********************************
	// **
	// ** PRIVATE
	// **
	// *********************************

	var getStarsAsync = function(callback) {

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

	var getSectorsAsync = function(callback) {

		var sectors = [];

		for (var x = SECTOR_MIN_X; x < SECTOR_MAX_X; x += SECTOR_SIZE) {
			for (var y = SECTOR_MIN_Y; y < SECTOR_MAX_Y; y += SECTOR_SIZE) {
				for (var z = SECTOR_MIN_Z; z < SECTOR_MAX_Z; z += SECTOR_SIZE) {

					var key = x.toString() + "," + y.toString() + ","
							+ z.toString;

					var sector = {
						key : key,
						minimumX : x,
						minimumY : y,
						minimumZ : z,
						maximumX : x + SECTOR_SIZE,
						maximumY : y + SECTOR_SIZE,
						maximumZ : z + SECTOR_SIZE
					};

					sectors.push(sector);
				}
			}
		}

		callback(sectors);
	};

	// *********************************
	// **
	// ** PUBLIC
	// **
	// *********************************

	return {

		GetStarsAsync : function(callback) {
			getStarsAsync(callback);
		},

		GetSectorsAsync : function(callback) {
			getSectorsAsync(callback);
		}

	}

})();
