"use strict";

var SpaceViewerController = (function() {

	var m_canvas;
	var m_camera;
	var m_jqCanvas;
	// var m_trackball;

	var m_cameraLookingAt = {
		x : 0,
		y : 0,
		z : 0
	};
	var m_cameraTween = null;
	var m_mouseButton = 0;
	var m_mouseDownPosition = null;
	var m_mouseDownCameraTranslation = null;

	// *********************************
	// **
	// ** PRIVATE
	// **
	// *********************************

	var initialize = function(canvas, camera) {
		m_canvas = canvas;
		m_camera = camera;

		m_jqCanvas = $(m_canvas);

		m_jqCanvas.on('wheel', onMouseWheel);
		m_jqCanvas.on('mousedown', onMouseDown);
		m_jqCanvas.on('mousemove', onMouseMove);
		m_jqCanvas.on('mouseup', onMouseUp);

		// m_trackball = new THREE.TrackballControls(m_camera, m_canvas);
		// m_trackball.rotateSpeed = 1.0;
		// m_trackball.zoomSpeed = 1.2;
		// m_trackball.panSpeed = 0.8;
		// m_trackball.noZoom = false;
		// m_trackball.noPan = false;
		// m_trackball.staticMoving = false;
		// m_trackball.dynamicDampingFactor = 0.3;
		// m_trackball.keys = [ 65, 83, 68 ];
	};

	var onMouseWheel = function(e) {
		e.preventDefault();
		m_camera.translateZ(e.originalEvent.deltaY * 2);
	}

	var onMouseDown = function(e) {

		m_mouseButton = e.which;
		m_mouseDownPosition = {
			x : e.pageX,
			y : e.pageY
		};
		m_mouseDownCameraTranslation = {
			x : 0,
			y : 0
		};
	}

	var onMouseMove = function(e) {
		if (m_mouseButton !== 0) {
			e.preventDefault();

			var dx = -(e.pageX - m_mouseDownPosition.x);
			var dy = e.pageY - m_mouseDownPosition.y;

			m_camera.translateX(dx - m_mouseDownCameraTranslation.x);
			m_camera.translateY(dy - m_mouseDownCameraTranslation.y);

			m_mouseDownCameraTranslation.x = dx;
			m_mouseDownCameraTranslation.y = dy;
		}
	}

	var onMouseUp = function(e) {

		m_mouseButton = 0;

	}

	var onWindowResize = function() {
		m_camera.aspect = m_jqCanvas.width() / m_jqCanvas.height();
		m_camera.updateProjectionMatrix();
		// m_trackball.handleResize();
	}

	var animate = function() {
		// m_trackball.update();
	}

	var lookAtMapItem = function(position) {
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

		m_cameraLookingAt = {
			x : position.x,
			y : position.y,
			z : position.z
		}
	}

	var lookAtSector = function(position) {
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

		m_cameraLookingAt = {
			x : position.x,
			y : position.y,
			z : position.z
		}
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

		LookAtMapItem : function(position) {
			lookAtMapItem(position);
		},

		LookAtSector : function(position) {
			lookAtSector(position);
		}
	}

})();
