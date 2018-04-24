"use strict";

var Explorer = (function() {

	// *********************************
	// **
	// ** PRIVATE
	// **
	// *********************************

	// Module events
	//
	var m_getMapItemHandler = null;

	// **
	// ** MODULE EVENTS
	// **

	var registerGetMapItemHandler = function(handler) {
		m_getMapItemHandler = handler;
	}

	var raiseGetMapItem = function(mapItemType, mapItemKey, callback) {
		if (m_getMapItemHandler !== null) {
			m_getMapItemHandler(mapItemType, mapItemKey, callback);
		}
	}

	// **
	// ** INITIALZATION
	// **

	var initialize = function() {

		SpaceViewerRestLogger.RegisterOnRestStartHandler(function(url) {
			$("#ctrlRestLog").append("Start: " + url + "<br>");
		});

		SpaceViewerRestLogger.RegisterOnRestCompleteHandler(function(url) {
			$("#ctrlRestLog").append("Complete: " + url + "<br>");
		});

		Explorer.RegisterGetMapItemHandler(SpaceViewerRestHandler.GetMapItem);

		SpaceViewer
				.RegisterGetSectorsHandler(SpaceViewerRestHandler.GetSectors);
		SpaceViewer
				.RegisterGetSectorByLocationHandler(SpaceViewerRestHandler.GetSectorByLocation);
		SpaceViewer
				.RegisterGetSectorByKeyHandler(SpaceViewerRestHandler.GetSectorByKey);
		SpaceViewer
				.RegisterGetMapItemsBySectorHandler(SpaceViewerRestHandler.GetMapItemsBySector);
		SpaceViewer
				.RegisterGetMapItemsByRankHandler(SpaceViewerRestHandler.GetMapItemsByRank);

		SpaceViewer.RegisterOnSectorClickHandler(function(sectorKey) {
			var coordinates = sectorKey.split(",");
			$('#spanSectorX').html(coordinates[0]);
			$('#spanSectorY').html(coordinates[1]);
			$('#spanSectorZ').html(coordinates[2]);
		});

		SpaceViewer.RegisterOnMapItemClickHandler(function(mapItemType,
				mapItemKey) {

			raiseGetMapItem(mapItemType, mapItemKey, function(mapItem) {

				var propertyHtml = "";
				mapItem.propertyGroups.forEach(function(propertyGroup) {
					propertyHtml += "<h6>" + propertyGroup.title + "</h6>";
					propertyHtml += "<table class='table table-sm'>";
					propertyHtml += "<tbody>";
					propertyGroup.properties.forEach(function(property) {
						propertyHtml += "<tr>"
						propertyHtml += "<th scope='row'>" + property.title
								+ "</th>";
						propertyHtml += "<td>" + property.value + "</td>";
						propertyHtml += "</tr>";
					});
					propertyHtml += "</tbody>";
					propertyHtml += "</table>";
				});

				$("#divMapItemProperties").html(propertyHtml);

			});
		});

		SpaceViewer.RegisterOnMapItemHoverHandler(function(mapItemType,
				mapItemKey) {
			$('#ctrlHover').html(mapItemType + ' ' + mapItemKey);
		});

		SpaceViewer.Initialize("#divSpaceViewer");
	};

	// *********************************
	// **
	// ** PUBLIC
	// **
	// *********************************

	return {

		// **
		// ** INITIALZATION
		// **

		Initialize : function() {
			initialize();
		},

		// RegisterGetMapItemHandler - raised to retrieve details for a specific
		// map item.
		//
		// handler = function(mapItemType, mapItemKey, callback) where:
		//
		// mapItemType = Number.
		//
		// mapItemKey = String.
		//
		// callback = function(mapItem) where:
		//
		// mapItem = object of:
		// * sectorKey = String
		// * mapItemType = Number
		// * mapItemKey = String
		// * propertyGroups = array of propertyGroup
		//
		// propertyGroup = object of:
		// * title = String
		// * properties = array of property
		//
		// property = object of:
		// * title = String
		// * description = String
		// * value = String
		//
		RegisterGetMapItemHandler : function(handler) {
			registerGetMapItemHandler(handler);
		}
	}

})();
