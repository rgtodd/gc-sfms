"use strict";

var SpaceViewerHandler = (function() {

	// Constants
	//
	var SECTOR_MIN_X = -1000;
	var SECTOR_MIN_Y = -1000;
	var SECTOR_MIN_Z = -1000;
	var SECTOR_MAX_X = 1000;
	var SECTOR_MAX_Y = 1000;
	var SECTOR_MAX_Z = 1000;
	var SECTOR_SIZE = 200;

	var m_sectors = null;

	// *********************************
	// **
	// ** PRIVATE
	// **
	// *********************************

	var getSectorsAsync = function(callback) {
		if (m_sectors === null) {
			m_sectors = [];
			var idxX = -1;
			for (var x = SECTOR_MIN_X; x < SECTOR_MAX_X; x += SECTOR_SIZE) {
				++idxX;
				var idxY = -1
				for (var y = SECTOR_MIN_Y; y < SECTOR_MAX_Y; y += SECTOR_SIZE) {
					++idxY;
					var idxZ = -1
					for (var z = SECTOR_MIN_Z; z < SECTOR_MAX_Z; z += SECTOR_SIZE) {
						++idxZ;

						var key = idxX.toString() + "," + idxY.toString() + ","
								+ idxZ.toString();

						var sector = {
							key : key,
							minimumX : x,
							minimumY : y,
							minimumZ : z,
							maximumX : x + SECTOR_SIZE,
							maximumY : y + SECTOR_SIZE,
							maximumZ : z + SECTOR_SIZE
						};

						m_sectors.push(sector);
					}
				}
			}
		}
		callback(m_sectors);
	};

	var getSectorByLocationAsync = function(x, y, z, callback) {
		getSectorsAsync(function(sectors) {
			var sectorsLength = sectors.length;
			for (var idx = 0; idx < sectorsLength; ++idx) {
				var sector = sectors[idx];
				if (sector.minimumX <= x && x < sector.maximumX
						&& sector.minimumY <= y && y < sector.maximumY
						&& sector.minimumZ <= z && z < sector.maximumZ) {

					console.log("getSectorByLocationAsync", x, y, z, sector);

					callback(sector);
					break;
				}
			}
		});
	}

	var getSectorByKeyAsync = function(key, callback) {
		getSectorsAsync(function(sectors) {
			var sectorsLength = sectors.length;
			for (var idx = 0; idx < sectorsLength; ++idx) {
				var sector = sectors[idx];
				if (sector.key == key) {

					console.log("getSectorByKeyAsync", key, sector);

					callback(sector);
					break;
				}
			}
		});
	}

	var getStarsBySectorAsync = function(sectorKey, callback) {
		getSectorByKeyAsync(
				sectorKey,
				function(sector) {
					var starPoints = [];
					var starIds = []
					for (var x = sector.minimumX; x < sector.maximumX; x += 25) {
						for (var y = sector.minimumY; y < sector.maximumY; y += 25) {
							for (var z = sector.minimumZ; z < sector.maximumZ; z += 25) {
								var starId = sectorKey + "-" + x.toString()
										+ "," + y.toString() + ","
										+ z.toString();
								starPoints.push(x, y, z);
								starIds.push(starId);

							}
						}
					}
					callback(starPoints, starIds);
				});
	};

	// *********************************
	// **
	// ** PUBLIC
	// **
	// *********************************

	return {

		// Retrieves all sectors.
		//
		// callback = function(sectors) where:
		//
		// sectors : array of sector
		//
		// sector : object of
		// * key
		// * minimumX
		// * maximumX
		// * minimumY
		// * maximumY
		// * minimumZ
		// * maximumZ
		//
		GetSectorsAsync : function(callback) {
			getSectorsAsync(callback);
		},

		// Retrieves sector containing the specified location.
		//
		// callback = function(sector) where:
		//
		// sector : object of
		// * key
		// * minimumX
		// * maximumX
		// * minimumY
		// * maximumY
		// * minimumZ
		// * maximumZ
		//
		GetSectorByLocationAsync : function(x, y, z, callback) {
			getSectorByLocationAsync(x, y, z, callback);
		},

		// Retrieves the specified sector.
		//
		// callback = function(sector) where:
		//
		// sector : object of
		// * key
		// * minimumX
		// * maximumX
		// * minimumY
		// * maximumY
		// * minimumZ
		// * maximumZ
		//
		GetSectorByKeyAsync : function(key, callback) {
			getSectorByKeyAsync(key, callback);
		},

		// Retrieves stars contained by the specified sector.
		//
		// callback = function(positions) where:
		//
		// starPoints : array of Number
		// starIds : array of String
		//
		GetStarsBySectorAsync : function(sectorKey, callback) {
			getStarsBySectorAsync(sectorKey, callback);
		}
	}

})();
