"use strict";

var SpaceViewerMockHandler = (function() {

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
			var sx = -1;
			for (var x = SECTOR_MIN_X; x < SECTOR_MAX_X; x += SECTOR_SIZE) {
				++sx;
				var sy = -1
				for (var y = SECTOR_MIN_Y; y < SECTOR_MAX_Y; y += SECTOR_SIZE) {
					++sy;
					var sz = -1
					for (var z = SECTOR_MIN_Z; z < SECTOR_MAX_Z; z += SECTOR_SIZE) {
						++sz;

						var key = sx.toString() + "," + sy.toString() + ","
								+ sz.toString();

						var sector = {
							key : key,
							sx : sx,
							sy : sy,
							sz : sz,
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

	var getObjectsBySectorAsync = function(sectorKey, objectType, callback) {
		getSectorByKeyAsync(
				sectorKey,
				function(sector) {
					var objectKeys = [];
					var objectPoints = [];
					var idx = 0;
					for (var x = sector.minimumX; x < sector.maximumX; x += 25) {
						for (var y = sector.minimumY; y < sector.maximumY; y += 25) {
							for (var z = sector.minimumZ; z < sector.maximumZ; z += 25) {
								if (++idx % 7 == objectType) {
									var starId = sectorKey + "-" + x.toString()
											+ "," + y.toString() + ","
											+ z.toString();
									objectKeys.push(starId);
									objectPoints.push(x, y, z);
								}
							}
						}
					}
					callback(objectKeys, objectPoints);
				});
	};

	// *********************************
	// **
	// ** PUBLIC
	// **
	// *********************************

	return {

		// GetSectors event handler for SpaceViewer.
		//
		// See SpaceViewer::RegisterGetSectorsHandler for more information.
		//
		GetSectorsAsync : function(callback) {
			getSectorsAsync(callback);
		},

		// GetSectorByLocation event handler for SpaceViewer.
		//
		// See SpaceViewer::RegisterGetSectorByLocationHandler for more
		// information.
		//
		GetSectorByLocationAsync : function(x, y, z, callback) {
			getSectorByLocationAsync(x, y, z, callback);
		},

		// GetSectorByKey event handler for SpaceViewer.
		//
		// See SpaceViewer::RegisterGetSectorByKeyHandler for more information.
		//
		GetSectorByKeyAsync : function(key, callback) {
			getSectorByKeyAsync(key, callback);
		},

		// GetObjectsBySector event handler for SpaceViewer.
		//
		// See SpaceViewer::RegisterGetObjectsBySectorHandler for more
		// information.
		//
		GetObjectsBySectorAsync : function(sectorKey, objectType, callback) {
			getObjectsBySectorAsync(sectorKey, objectType, callback);
		}
	}

})();
