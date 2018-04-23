"use strict";

var SpaceViewerRestHandler = (function() {

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

		var url = "/ajax/getSectors";

		SpaceViewerRestLogger.RaiseOnRestStart(url);
		$.ajax({
			type : 'GET',
			url : url
		}).then(function(response) {
			SpaceViewerRestLogger.RaiseOnRestComplete(url);
			callback(response.sectors);
		});
	};

	var getSectorByLocationAsync = function(x, y, z, callback) {

		var url = "/ajax/getSectorByLocation?x=" + x + "&y=" + y + "&z=" + z;

		SpaceViewerRestLogger.RaiseOnRestStart(url);
		$.ajax({
			type : 'GET',
			url : url
		}).then(function(response) {
			SpaceViewerRestLogger.RaiseOnRestComplete(url);
			callback(response.sector);
		});
	}

	var getSectorByKeyAsync = function(key, callback) {

		var url = "/ajax/getSectorByKey?key=" + key;

		SpaceViewerRestLogger.RaiseOnRestStart(url);
		$.ajax({
			type : 'GET',
			url : url
		}).then(function(response) {
			SpaceViewerRestLogger.RaiseOnRestComplete(url);
			callback(response.sector);
		});
	}

	var getMapItemsBySectorAsync = function(sectorKey, mapItemType, callback) {

		var url = "/ajax/getMapItemsBySector?sectorKey=" + sectorKey
				+ "&mapItemType=" + mapItemType;

		SpaceViewerRestLogger.RaiseOnRestStart(url);
		$.ajax({
			type : 'GET',
			url : url
		}).then(function(response) {
			SpaceViewerRestLogger.RaiseOnRestComplete(url);
			callback(response.mapItemSets);
		});
	};

	var getMapItemsByRankAsync = function(rank, callback) {

		var url = "/ajax/getMapItemsByRank?rank=" + rank;

		SpaceViewerRestLogger.RaiseOnRestStart(url);
		$.ajax({
			type : 'GET',
			url : url
		}).then(function(response) {
			SpaceViewerRestLogger.RaiseOnRestComplete(url);
			callback(response.mapItemSets);
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
		GetMapItemsBySectorAsync : function(sectorKey, mapItemType, callback) {
			getMapItemsBySectorAsync(sectorKey, mapItemType, callback);
		},

		// GetObjectsBySector event handler for SpaceViewer.
		//
		// See SpaceViewer::RegisterGetObjectsBySectorHandler for more
		// information.
		//
		GetMapItemsByRankAsync : function(rank, callback) {
			getMapItemsByRankAsync(rank, callback);
		}
	}

})();
