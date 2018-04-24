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

	var getSectors = function(callback) {

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

	var getSectorByLocation = function(x, y, z, callback) {

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

	var getSectorByKey = function(key, callback) {

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

	var getMapItemsBySector = function(sectorKey, mapItemType, callback) {

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

	var getMapItemsByRank = function(rank, callback) {

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

	var getMapItem = function(mapItemType, mapItemKey, callback) {

		var url = "/ajax/getMapItem?mapItemType=" + mapItemType
				+ "&mapItemKey=" + mapItemKey;

		SpaceViewerRestLogger.RaiseOnRestStart(url);
		$.ajax({
			type : 'GET',
			url : url
		}).then(function(response) {
			SpaceViewerRestLogger.RaiseOnRestComplete(url);
			callback(response);
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
		GetSectors : function(callback) {
			getSectors(callback);
		},

		// GetSectorByLocation event handler for SpaceViewer.
		//
		// See SpaceViewer::RegisterGetSectorByLocationHandler for more
		// information.
		//
		GetSectorByLocation : function(x, y, z, callback) {
			getSectorByLocation(x, y, z, callback);
		},

		// GetSectorByKey event handler for SpaceViewer.
		//
		// See SpaceViewer::RegisterGetSectorByKeyHandler for more information.
		//
		GetSectorByKey : function(key, callback) {
			getSectorByKey(key, callback);
		},

		// GetObjectsBySector event handler for SpaceViewer.
		//
		// See SpaceViewer::RegisterGetObjectsBySectorHandler for more
		// information.
		//
		GetMapItemsBySector : function(sectorKey, mapItemType, callback) {
			getMapItemsBySector(sectorKey, mapItemType, callback);
		},

		// GetObjectsBySector event handler for SpaceViewer.
		//
		// See SpaceViewer::RegisterGetObjectsBySectorHandler for more
		// information.
		//
		GetMapItemsByRank : function(rank, callback) {
			getMapItemsByRank(rank, callback);
		},

		// GetMapItems event handler for SpaceViewer.
		//
		// See SpaceViewer::RegisterGetMapItemHandler for more
		// information.
		//
		GetMapItem : function(mapItemType, mapItemKey, callback) {
			getMapItem(mapItemType, mapItemKey, callback);
		}
	}

})();
