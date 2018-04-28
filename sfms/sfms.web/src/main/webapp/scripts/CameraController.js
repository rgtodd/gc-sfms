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
 *        mouse is moved.     
 * 
 * Drag   The controller enters Drag state when the mouse button is pressed.
 *        In this state, mouse movements cause the camera to be repositioned.
 *        No events are raised to the caller when the camera is being dragged.
 *
 * When the user first depresses the mouse button, we need to 
 * Internally, the Drag state has three sub-states:
 * 
 * Drag - Mouse Down  The initial state when the user first depresses the mouse.
 * 
 * Drag - Mouse Up
 * 
 * Drag - Active
 * 
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

	var onWindowResize = function() {
		m_camera.aspect = m_jqCanvas.width() / m_jqCanvas.height();
		m_camera.updateProjectionMatrix();
	}

	var animate = function() {
		// No action required.
	}

	var dragTo = function(position) {

		var dx = -(position.x - m_mouseDownPosition.x);
		var dy = position.y - m_mouseDownPosition.y;

		var positionBefore = m_camera.getWorldPosition();

		m_camera.translateX(dx - m_mouseDownCameraTranslation.x);
		m_camera.translateY(dy - m_mouseDownCameraTranslation.y);

		var positionAfter = m_camera.getWorldPosition();
		var positionDelta = positionAfter.clone().sub(positionBefore);

		m_cameraLookingAt.add(positionDelta);

		m_mouseDownCameraTranslation.x = dx;
		m_mouseDownCameraTranslation.y = dy;

		// m_cameraLookingAt.x += dx;
		// m_cameraLookingAt.y += dy;
	}

	var zoom = function(delta) {
		var positionBefore = m_camera.getWorldPosition();

		m_camera.translateZ(delta * 2);

		var positionAfter = m_camera.getWorldPosition();
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
				TWEEN.Easing.Quadratic.Out).onUpdate(
				function() {
					m_camera.position.set(tweenState.x, tweenState.y + 150,
							tweenState.z + 250);
					m_camera.lookAt(tweenState.x, tweenState.y, tweenState.z);
				}).start();

		m_cameraLookingAt.set(position.x, position.y, position.z);
	}

	// **
	// ** EVENT HANDLERS
	// **

	var onMouseEnter = function(e) {

		m_state = HOVER;

	}

	var onMouseMove = function(e) {
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
		switch (m_state) {
		case HOVER:
			e.preventDefault();
			zoom(e.originalEvent.deltaY);
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
