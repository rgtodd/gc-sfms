"use strict";

var SpaceViewerRestLogger = (function() {

	// *********************************
	// **
	// ** PRIVATE
	// **
	// *********************************

	// Module events
	//
	var m_onRestStartHandler = null;
	var m_onRestCompleteHandler = null;

	// **
	// ** MODULE EVENTS
	// **

	var registerOnRestStartHandler = function(handler) {
		m_onRestStartHandler = handler;
	}

	var raiseOnRestStart = function(url) {
		if (m_onRestStartHandler !== null) {
			m_onRestStartHandler(url);
		}
	}

	var registerOnRestCompleteHandler = function(handler) {
		m_onRestCompleteHandler = handler;
	}

	var raiseOnRestComplete = function(url) {
		if (m_onRestCompleteHandler !== null) {
			m_onRestCompleteHandler(url);
		}
	}

	// *********************************
	// **
	// ** PUBLIC
	// **
	// *********************************

	return {

		// RegisterOnRestStartHandler - raised when a REST request has started.
		//
		// handler = function(callback) where:
		//
		// callback = function(url) where:
		//
		// url = String
		//
		RegisterOnRestStartHandler : function(handler) {
			registerOnRestStartHandler(handler);
		},

		// RegisterOnRestCompleteHandler - raised when a REST request has
		// completed.
		//
		// handler = function(callback) where:
		//
		// callback = function(url) where:
		//
		// url = String
		//
		RegisterOnRestCompleteHandler : function(handler) {
			registerOnRestCompleteHandler(handler);
		},

		// RaiseOnRestStart - indicate a REST request has started.
		//
		// url = String
		//
		RaiseOnRestStart : function(url) {
			raiseOnRestStart(url);
		},

		// RaiseOnRestComplete - indicate a REST request has completed.
		//
		// url = String
		//
		RaiseOnRestComplete : function(url) {
			raiseOnRestComplete(url);
		},
	}

})();
