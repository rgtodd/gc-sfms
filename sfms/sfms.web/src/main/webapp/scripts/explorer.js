"use strict";

var Explorer = (function() {

	// *********************************
	// **
	// ** PRIVATE
	// **
	// *********************************

	var m_restCounter = 0;

	// Module events
	//
	var m_getMapItemHandler = null;

	var onRestStart = function(url) {

		++m_restCounter;

		$("#spanRestCounter").text(m_restCounter.toString());

		$("#divRestBodyContent").append(
				getCurrentTimeFormatted() + " - " + "Start: " + url + "<br>");
	}

	var onRestComplete = function(url) {
		$("#divRestBodyContent")
				.append(
						getCurrentTimeFormatted() + " - " + "Complete: " + url
								+ "<br>");
	}

	var getCurrentTimeFormatted = function() {
		var now = new Date();

		var year = now.getFullYear();
		var month = now.getMonth();
		var day = now.getDate();
		var hour = now.getHours();
		var minute = now.getMinutes();
		var second = now.getSeconds();

		return zeroPad(year, 4) + "-" + //
		zeroPad(month, 2) + "-" + //
		zeroPad(day, 2) + " " + //
		zeroPad(hour, 2) + ":" + //
		zeroPad(minute, 2) + ":" + //
		zeroPad(second, 2);
	}

	var zeroPad = function(number, length) {
		var result = number.toString();
		while (result.length < length) {
			result = "0" + result;
		}
		return result;
	}

	var onMapItemClick = function(mapItemType, mapItemKey) {

		raiseGetMapItem(
				mapItemType,
				mapItemKey,
				function(mapItem) {

					$("#divMapItemTitle").html(mapItem.mapItemName);

					var propertyHtml = "";
					mapItem.propertyGroups
							.forEach(function(propertyGroup) {
								propertyHtml += "<h6>" + propertyGroup.title
										+ "</h6>";
								propertyHtml += "<table class='table table-sm'>";
								propertyHtml += "<tbody>";
								propertyGroup.properties
										.forEach(function(property) {
											propertyHtml += "<tr>";
											propertyHtml += "<th scope='row'>";
											propertyHtml += property.title;
											if (property.description !== null) {
												propertyHtml += " <i class='material-icons tooltip-description' data-toggle='tooltip' title='"
														+ property.description
														+ "'>help</i>";
											}
											propertyHtml += "</th>";
											propertyHtml += "<td>"
													+ property.value + "</td>";
											propertyHtml += "</tr>";
										});
								propertyHtml += "</tbody>";
								propertyHtml += "</table>";
							});

					$("#divMapItemProperties").html(propertyHtml);

				});
	}

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

		SpaceViewerRestLogger.RegisterOnRestStartHandler(onRestStart);

		SpaceViewerRestLogger.RegisterOnRestCompleteHandler(onRestComplete);

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

		SpaceViewer.RegisterOnMapItemClickHandler(onMapItemClick);

		SpaceViewer.RegisterOnMapItemHoverHandler(function(mapItemType,
				mapItemKey) {
			$('#ctrlHover').html(mapItemType + ' ' + mapItemKey);
		});

		SpaceViewer.Initialize("#divSpaceViewer");

		$(".card-body-panel").hide();
		$("#divMapItemBody").show();

		$(".nav-link-tab").on("click", function(e) {
			$(".nav-link-tab").removeClass("active");
			$(this).addClass("active");

			$(".card-body-panel:visible").hide();
			$($(this).data("body")).show();
		});

		$("body").tooltip({
			selector : ".tooltip-description",
			trigger : "hover"
		});
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
