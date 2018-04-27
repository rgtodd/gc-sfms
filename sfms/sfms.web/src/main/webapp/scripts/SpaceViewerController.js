"use strict";

var SpaceViewerController = (function() {

	var m_canvas;
	var m_camera;
	var m_jqCanvas;
	var m_trackball;

	var m_cameraTween = null;

	// *********************************
	// **
	// ** PRIVATE
	// **
	// *********************************

	var initialize = function(canvas, camera) {
		m_canvas = canvas;
		m_camera = camera;

		m_jqCanvas = $(m_canvas);

		m_trackball = new THREE.TrackballControls(m_camera, m_canvas);
		m_trackball.rotateSpeed = 1.0;
		m_trackball.zoomSpeed = 1.2;
		m_trackball.panSpeed = 0.8;
		m_trackball.noZoom = false;
		m_trackball.noPan = false;
		m_trackball.staticMoving = false;
		m_trackball.dynamicDampingFactor = 0.3;
		m_trackball.keys = [ 65, 83, 68 ];
	};

	var onWindowResize = function() {
		m_camera.aspect = m_jqCanvas.width() / m_jqCanvas.height();
		m_camera.updateProjectionMatrix();
		m_trackball.handleResize();
	}

	var animate = function() {
		m_trackball.update();
	}

	var lookAtMapItem = function(position) {
		if (m_cameraTween !== null) {
			m_cameraTween.stop();
			m_cameraTween = null;
		}

		var tweenState = {
			x : m_trackball.target.x,
			y : m_trackball.target.y,
			z : m_trackball.target.z
		};
		m_cameraTween = new TWEEN.Tween(tweenState).to(position, 1000).easing(
				TWEEN.Easing.Quadratic.Out).onUpdate(function() {
			m_trackball.target.set(tweenState.x, tweenState.y, tweenState.z);
		}).start();
	}

	var lookAtSector = function(position) {
		if (m_cameraTween !== null) {
			m_cameraTween.stop();
			m_cameraTween = null;
		}

		m_camera.up.set(0, 1, 0);
		var tweenState = {
			x : m_trackball.target.x,
			y : m_trackball.target.y,
			z : m_trackball.target.z
		};
		m_cameraTween = new TWEEN.Tween(tweenState).to(position, 1000).easing(
				TWEEN.Easing.Quadratic.Out).onUpdate(
				function() {
					m_camera.position.set(tweenState.x, tweenState.y + 150,
							tweenState.z + 250);
					m_trackball.target.set(tweenState.x, tweenState.y,
							tweenState.z);
				}).start();
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
