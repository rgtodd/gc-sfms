"use strict";

/*-
 * Logger
 * 
 * Provides a connection between logging producers and consumers.
 * 
 */
var Logger = (function() {

	// *********************************
	// **
	// ** PRIVATE
	// **
	// *********************************

	// Module events
	//
	var m_onRestStartHandler = null;
	var m_onRestCompleteHandler = null;
	var m_onRestErrorHandler = null;
	var m_onLogMessageHandler = null;

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

	var registerOnRestErrorHandler = function(handler) {
		m_onRestErrorHandler = handler;
	}

	var raiseOnRestError = function(url, jqXHR, textStatus) {
		if (m_onRestErrorHandler !== null) {
			m_onRestErrorHandler(url, jqXHR, textStatus);
		}
	}

	var registerOnLogMessageHandler = function(handler) {
		m_onLogMessageHandler = handler;
	}

	var raiseOnLogMessage = function(message) {
		if (m_onLogMessageHandler !== null) {
			m_onLogMessageHandler(message);
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

		// RegisterOnRestErrorHandler - raised when a REST request has failed.
		//
		// handler = function(callback) where:
		//
		// callback = function(url) where:
		//
		// url = String
		//
		RegisterOnRestErrorHandler : function(handler) {
			registerOnRestErrorHandler(handler);
		},

		// RegisterOnLogMessageHandler - raised when log entry is created.
		//
		// handler = function(callback) where:
		//
		// callback = function(message) where:
		//
		// message = String
		//
		RegisterOnLogMessageHandler : function(handler) {
			registerOnLogMessageHandler(handler);
		},

		// RestStart - indicate a REST request has started.
		//
		// url = String
		//
		RestStart : function(url) {
			raiseOnRestStart(url);
		},

		// RestComplete - indicate a REST request has completed.
		//
		// url = String
		//
		RestComplete : function(url) {
			raiseOnRestComplete(url);
		},

		RestError : function(url, jqXHR, textStatus) {
			raiseOnRestError(url, jqXHR, textStatus);
		},

		// RaiseOnLogMessage - logs a new message.
		//
		// message = String
		//
		LogMessage : function(message) {
			raiseOnLogMessage(message);
		}
	}

})();
