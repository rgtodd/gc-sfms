"use strict";

/*-
 * Explorer
 * 
 * Functions associated with the explorer.html template.
 * 
 * This module's primary function is to orchestrate events between the following
 * modules:
 * 
 *  * SpaceViewer
 *  * SpaceViewerRestHandler
 *  * Logger
 * 
 * This module also updates the UI elements (e.g. the object properties tab) when
 * a map item is clicked in the SpaceViewer.
 * 
 */
var Explorer = (function() {

	// *********************************
	// **
	// ** PRIVATE
	// **
	// *********************************

	var MINIMUM_CONTENTS_HEIGHT = 200;

	var m_restCounter = 0;
	var m_logCounter = 0;
	var m_lastLogMessage = null;

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

		Logger.RegisterOnRestStartHandler(onRestStart);
		Logger.RegisterOnRestCompleteHandler(onRestComplete);
		Logger.RegisterOnRestErrorHandler(onRestError);
		Logger.RegisterOnLogMessageHandler(onLogMessage);

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

		SpaceViewer.RegisterLoadingProgressHandler(function(percent) {
			$(".loading__text").html("Loading...  " + percent + "% complete.");
		});

		SpaceViewer.RegisterLoadingCompleteHandler(function() {
			$(".loading").hide();
		});

		SpaceViewer.RegisterOnSectorClickHandler(function(sectorKey) {
			var coordinates = sectorKey.split("-");
			$('#spanSectorX').html(Number.parseInt(coordinates[0]));
			$('#spanSectorY').html(Number.parseInt(coordinates[1]));
			$('#spanSectorZ').html(Number.parseInt(coordinates[2]));
		});

		SpaceViewer.RegisterOnMapItemClickHandler(onMapItemClick);

		SpaceViewer.RegisterOnMapItemHoverHandler(function(mapItemType,
				mapItemKey) {
			$('#ctrlHover').html(mapItemType + ' ' + mapItemKey);
		});

		SpaceViewer.Initialize("#divSpaceViewer");

		onWindowResize();

		$(window).on('resize', onWindowResize);
		$("#btnRestClear").on("click", onRestClearClick);
		$("#btnLogClear").on("click", onLogClearClick);
		$(".nav-link-tab").on("click", function(e) {
			$(".nav-link-tab").removeClass("active");
			$(this).addClass("active");

			$(".card-body-panel:visible").hide();
			var panel = $($(this).data("body"));
			panel.show();
			scrollToBottom(panel.children("pre"));
		});

		$("body").tooltip({
			selector : ".tooltip-description",
			trigger : "hover"
		});

		$(".card-body-panel").hide();
		$("#divMapItemBody").show();
	};

	// **
	// ** Event Handlers
	// **

	var onRestStart = function(url) {
		addRestStartEntry(url);
	}

	var onRestComplete = function(url) {
		addRestCompleteEntry(url);
	}

	var onRestError = function(url, jqXHR, textStatus) {
		addRestErrorEntry(url, jqXHR, textStatus);
	}

	var onWindowResize = function() {
		var jqDivSpaceViewer = $("#divSpaceViewer");

		console.log("$(window).height() = " + $(window).height());
		console.log("jqDivSpaceViewer.offset().top = "
				+ jqDivSpaceViewer.offset().top);
		console.log("$('#tableLegend').height() = "
				+ $("#tableLegend").height());

		var height = Math.max(MINIMUM_CONTENTS_HEIGHT, $(window).height()
				- jqDivSpaceViewer.offset().top - $("#tableLegend").height()
				- 16);

		console.log("height  = " + height);

		jqDivSpaceViewer.height(height);

		SpaceViewer.OnWindowResize();
	}

	var onRestClearClick = function() {
		clearRest();
	}

	var onLogMessage = function(message) {
		addLogMessage(message);
	}

	var onLogClearClick = function() {
		clearLog();
	}

	var onMapItemClick = function(mapItemType, mapItemKey) {
		raiseGetMapItem(mapItemType, mapItemKey, updateMapItemDetail);
	}

	// **
	// ** UI
	// **

	var updateMapItemDetail = function(mapItem) {

		$("#divMapItemTitle").html(mapItem.mapItemName);

		var propertyHtml = "";
		mapItem.propertyGroups
				.forEach(function(propertyGroup) {
					propertyHtml += "<h5>" + propertyGroup.title + "</h5>";
					propertyHtml += "<table class='table table-sm'>";
					propertyHtml += "<tbody>";
					propertyGroup.properties
							.forEach(function(property) {
								propertyHtml += "<tr>";
								propertyHtml += "<td>";
								propertyHtml += property.title;
								if (property.description !== null) {
									propertyHtml += " <i class='material-icons tooltip-description' data-toggle='tooltip' title='"
											+ property.description
											+ "'>help</i>";
								}
								propertyHtml += "</td>";
								propertyHtml += "<td>";
								if (property.value !== null) {
									propertyHtml += property.value;
								}
								propertyHtml += "</td>";
								propertyHtml += "</tr>";
							});
					propertyHtml += "</tbody>";
					propertyHtml += "</table>";
				});

		$("#divMapItemProperties").html(propertyHtml);
	}

	var addRestStartEntry = function(url) {
		++m_restCounter;
		$("#spanRestCounter").text(m_restCounter.toString());

		var divContent = $("#divRestBodyContent");
		divContent.append(getCurrentTimeFormatted() + " - Start: " + url
				+ "<br>");
		scrollToBottom(divContent);
	}

	var addRestCompleteEntry = function(url) {
		var divContent = $("#divRestBodyContent");
		divContent.append(getCurrentTimeFormatted() + " - Complete: " + url
				+ "<br>");
		scrollToBottom(divContent);
	}

	var addRestErrorEntry = function(url, jqXHR, textStatus) {
		var divContent = $("#divRestBodyContent");
		divContent.append(getCurrentTimeFormatted() + " - " + jqXHR.statusText
				+ " (" + jqXHR.status + "): " + url + ")<br>");
		scrollToBottom(divContent);
	}

	var clearRest = function() {
		m_restCounter = 0;
		$("#spanRestCounter").text(m_restCounter.toString());

		var divContent = $("#divRestBodyContent");
		divContent.text("");
	}

	var addLogMessage = function(message) {

		if (message != m_lastLogMessage) {

			++m_logCounter;
			$("#spanLogCounter").text(m_logCounter.toString());

			m_lastLogMessage = message;
			var divContent = $("#divLogBodyContent");
			divContent.append(getCurrentTimeFormatted() + " - " + message
					+ "<br>");
			scrollToBottom(divContent);
		}
	}

	var clearLog = function() {
		m_logCounter = 0;
		$("#spanLogCounter").text(m_logCounter.toString());

		var divContent = $("#divLogBodyContent");
		divContent.text("");
	}

	var scrollToBottom = function(jq) {
		jq.scrollTop(jq[0].scrollHeight);
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
