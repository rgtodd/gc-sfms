"use strict";

/*-
 * CameraController
 * 
 * A basic camera controller for a three.js (WebGL) scene.
 * 
 * From the caller's perspective, the controller has two states:
 * 
 * Hover  The default state.  In this state, mouse movements do not affect 
 *        the state of the controller.  Hover events are raised when the 
 *        mouse is moved.  The SpaceViewer module responds to these events by 
 *        updating the "focus" cursor.     
 * 
 * Drag   The controller enters Drag state when the mouse button is pressed.
 *        In this state, mouse movements cause the camera to be repositioned.
 *        No events are raised to the caller when the camera is being dragged.
 *
 * Internally, the Drag state has three sub-states:
 * 
 * Drag-Mouse-Down  The initial state when the user first depresses the mouse.
 * 
 * Drag-Mouse-Up    State after user releases the mouse UNLESS the user has
 *                  started dragging the mouse.
 * 
 * Drag-Active      State if the user has started dragging the mouse.
 * 
 * These sub-states are required to properly handle the click event.  This
 * event occurs AFTER the mouseup event.  When a click event is received,
 * these sub-states are used as follows: 
 * 
 * Drag-Mouse Up  A Click controller event is raised.  The controller returns to
 *                the Hover state.
 *                  
 * Drag-Active    The click event is ignored.  THe controller remains in Drag-Active
 *                state.
 * 
 */

var CameraController = (function() {

	// *********************************
	// **
	// ** PRIVATE
	// **
	// *********************************

	var m_canvas;
	var m_camera;
	var m_jqCanvas;

	// Controller states
	//
	var HOVER = 0;
	var MOUSE_DOWN = 1;
	var MOUSE_UP = 2;
	var DRAG = 3;
	var m_state = HOVER;

	var m_cameraLookingAt = new THREE.Vector3();
	var m_cameraTween = null;

	var m_mouseDownPosition = null;
	var m_mouseDownCameraTranslation = null;

	var m_onHoverHandler = null;
	var m_onClickHandler = null;

	// **
	// ** INITIALZATION
	// **

	var initialize = function(canvas, camera) {
		m_canvas = canvas;
		m_camera = camera;

		m_jqCanvas = $(m_canvas);

		m_jqCanvas.on('mouseenter', onMouseEnter);
		m_jqCanvas.on('mousemove', onMouseMove);
		m_jqCanvas.on('mousedown', onMouseDown);
		m_jqCanvas.on('mouseup', onMouseUp);
		m_jqCanvas.on('wheel', onMouseWheel);
		m_jqCanvas.on('click', onClick);
	};

	// **
	// ** PUBLIC METHODS
	// **

	var onWindowResize = function() {
		m_camera.aspect = m_jqCanvas.width() / m_jqCanvas.height();
		m_camera.updateProjectionMatrix();
	}

	var animate = function() {
		// No action required.
	}

	// **
	// ** EVENT HANDLERS
	// **

	var onMouseEnter = function(e) {

		Logger.LogMessage("onMouseEnter");

		m_state = HOVER;

	}

	var onMouseMove = function(e) {

		Logger.LogMessage("onMouseMove");

		switch (m_state) {
		case HOVER:
			raiseOnHover(getPosition(e));
			break;

		case MOUSE_DOWN:
			m_state = DRAG;
			dragTo(getPosition(e));
			break;

		case MOUSE_UP:
			m_state = HOVER;
			raiseOnHover(getPosition(e));
			break;

		case DRAG:
			dragTo(getPosition(e));
		}
	}

	var onMouseDown = function(e) {

		Logger.LogMessage("onMouseDown");

		switch (m_state) {
		case HOVER:
			m_state = MOUSE_DOWN;
			m_mouseDownPosition = getPosition(e);
			m_mouseDownCameraTranslation = {
				x : 0,
				y : 0
			};
			break;

		case MOUSE_DOWN:
			// No action required.
			break;

		case MOUSE_UP:
			m_state = MOUSE_DOWN;
			m_mouseDownPosition = getPosition(e);
			m_mouseDownCameraTranslation = {
				x : 0,
				y : 0
			};
			break;

		case DRAG:
			m_state = MOUSE_DOWN;
			m_mouseDownPosition = getPosition(e);
			m_mouseDownCameraTranslation = {
				x : 0,
				y : 0
			};
			break;
		}
	}

	var onMouseUp = function(e) {

		Logger.LogMessage("onMouseUp");

		switch (m_state) {
		case HOVER:
			// No action required.
			break;

		case MOUSE_DOWN:
			m_state = MOUSE_UP;
			break;

		case MOUSE_UP:
			// No action required.
			break;

		case DRAG:
			m_state = HOVER;
			break;
		}
	}

	var onClick = function(e) {

		Logger.LogMessage("onClick");

		switch (m_state) {
		case HOVER:
			// No action required.
			break;

		case MOUSE_DOWN:
			m_state = HOVER;
			break;

		case MOUSE_UP:
			raiseOnClick(getPosition(e));
			m_state = HOVER;
			break;

		case DRAG:
			m_state = HOVER;
			break;
		}
	}

	var onMouseWheel = function(e) {

		Logger.LogMessage("onMouseWheel");

		switch (m_state) {
		case HOVER:
			e.preventDefault();
			zoom(e.originalEvent.deltaMode, e.originalEvent.deltaY);
			break;

		case MOUSE_DOWN:
			// No action required.
			break;

		case MOUSE_UP:
			// No action required.
			break;

		case DRAG:
			// No action required.
			break;
		}
	}

	// **
	// ** MODULE EVENTS
	// **

	var registerOnHoverHandler = function(handler) {
		m_onHoverHandler = handler;
	}

	var raiseOnHover = function(position) {
		if (m_onHoverHandler !== null) {
			m_onHoverHandler(position);
		}
	}

	var registerOnClickHandler = function(handler) {
		m_onClickHandler = handler;
	}

	var raiseOnClick = function() {
		if (m_onClickHandler !== null) {
			m_onClickHandler();
		}
	}

	// **
	// ** CAMERA METHODS
	// **

	var dragTo = function(position) {

		var dx = -(position.x - m_mouseDownPosition.x) * 0.1;
		var dy = (position.y - m_mouseDownPosition.y) * 0.1;

		var positionBefore = m_camera.getWorldPosition(new THREE.Vector3());

		m_camera.translateX(dx - m_mouseDownCameraTranslation.x);
		m_camera.translateY(dy - m_mouseDownCameraTranslation.y);

		var positionAfter = m_camera.getWorldPosition(new THREE.Vector3());
		var positionDelta = positionAfter.clone().sub(positionBefore);

		m_cameraLookingAt.add(positionDelta);

		m_mouseDownCameraTranslation.x = dx;
		m_mouseDownCameraTranslation.y = dy;

		// m_cameraLookingAt.x += dx;
		// m_cameraLookingAt.y += dy;
	}

	var zoom = function(deltaMode, deltaY) {

		// console.log("deltaY = " + deltaY + " deltaMode = " + deltaMode);

		// Some/most browsers specify +/- 120 for the values associated with a
		// single mouse wheel "notch". Other browsers specify +/- 3. Normalize
		// to +/- 3.
		//
		if (deltaY >= 120 || deltaY <= -120) {
			deltaY /= 40;
		}

		// Apply deltaMode. Do not update if PIXEL (0) is specified.
		//
		switch (deltaMode) {
		case 1: // LINE
			deltaY *= 3;
			break;

		case 2: // PAGE
			deltaY *= 10;
			break;
		}

		var positionBefore = m_camera.getWorldPosition(new THREE.Vector3());

		m_camera.translateZ(deltaY);

		var positionAfter = m_camera.getWorldPosition(new THREE.Vector3());
		var positionDelta = positionAfter.clone().sub(positionBefore);

		m_cameraLookingAt.add(positionDelta);
	}

	var pivotTo = function(position) {
		if (m_cameraTween !== null) {
			m_cameraTween.stop();
			m_cameraTween = null;
		}

		var tweenState = {
			x : m_cameraLookingAt.x,
			y : m_cameraLookingAt.y,
			z : m_cameraLookingAt.z
		};
		m_cameraTween = new TWEEN.Tween(tweenState).to(position, 1000).easing(
				TWEEN.Easing.Quadratic.Out).onUpdate(function() {
			m_camera.lookAt(tweenState.x, tweenState.y, tweenState.z);
		}).start();

		m_cameraLookingAt.set(position.x, position.y, position.z);
	}

	var panTo = function(position) {
		if (m_cameraTween !== null) {
			m_cameraTween.stop();
			m_cameraTween = null;
		}

		m_camera.up.set(0, 1, 0);
		var tweenState = {
			x : m_cameraLookingAt.x,
			y : m_cameraLookingAt.y,
			z : m_cameraLookingAt.z
		};
		m_cameraTween = new TWEEN.Tween(tweenState).to(position, 1000).easing(
				TWEEN.Easing.Quadratic.Out).onUpdate(function() {
			m_camera.position.set(tweenState.x, tweenState.y, // + 150,
			tweenState.z + 250);
			m_camera.lookAt(tweenState.x, tweenState.y, tweenState.z);
		}).start();

		m_cameraLookingAt.set(position.x, position.y, position.z);
	}

	// **
	// ** UTILITY
	// **

	var getPosition = function(e) {
		return {
			x : e.pageX,
			y : e.pageY
		};
	}

	// *********************************
	// **
	// ** PUBLIC
	// **
	// *********************************

	return {

		Initialize : function(canvas, camera) {
			initialize(canvas, camera);
		},

		OnWindowResize : function() {
			onWindowResize();
		},

		Animate : function() {
			animate();
		},

		PivotTo : function(position) {
			pivotTo(position);
		},

		PanTo : function(position) {
			panTo(position);
		},

		RegisterOnHoverHandler : function(handler) {
			registerOnHoverHandler(handler);
		},

		RegisterOnClickHandler : function(handler) {
			registerOnClickHandler(handler);
		}
	}

})();
