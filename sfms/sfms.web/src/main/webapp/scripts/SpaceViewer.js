"use strict";

var SpaceViewer = (function() {

	// *********************************
	// **
	// ** PRIVATE
	// **
	// *********************************

	var g_scene;
	var g_camera;
	var g_stats;
	var g_renderer;
	var g_container;
	var g_trackball;
	var g_handler;

	// **
	// ** INITIALZATION
	// **

	var initialize = function(containerId) {

		// g_scene
		//
		initCreateScene();
		initAddDefaultObjectsToScene();

		// g_camera
		//
		initCreateCamera();

		// g_stats
		//
		initCreateStats();

		// g_renderer
		//
		initCreateRenderer();

		// g_container
		//
		initCreateContainer(containerId);

		// g_trackball
		//
		initCreateTrackball();

		$(window).on('resize', onWindowResize);
		render();
		animate();
	};

	var initCreateScene = function() {

		g_scene = new THREE.Scene();
	};

	var initAddDefaultObjectsToScene = function() {

		var gridHelper = new THREE.GridHelper(100, 10);
		g_scene.add(gridHelper);

		var dirX = new THREE.Vector3(1, 0, 0);
		var dirY = new THREE.Vector3(0, 1, 0);
		var dirZ = new THREE.Vector3(0, 0, 1);
		var origin = new THREE.Vector3(0, 0, 0);
		var length = 90;
		g_scene.add(new THREE.ArrowHelper(dirX, origin, length, 0xff0000));
		g_scene.add(new THREE.ArrowHelper(dirY, origin, length, 0x00ff00));
		g_scene.add(new THREE.ArrowHelper(dirZ, origin, length, 0x0000ff));
	};

	var initCreateCamera = function() {
		g_camera = new THREE.PerspectiveCamera(75, window.innerWidth
				/ window.innerHeight, 1, 3000);
		g_camera.position.z = 1000;
	}

	var initCreateStats = function() {
		g_stats = new Stats();
	}

	var initCreateRenderer = function() {
		g_renderer = new THREE.WebGLRenderer();
		g_renderer.setPixelRatio(window.devicePixelRatio);
	}

	var initCreateContainer = function(containerId) {
		g_container = $(containerId);
		g_container.append(g_renderer.domElement);
		g_container.append(g_stats.dom);
		g_renderer.setSize(g_container.width(), g_container.height());
		g_camera.aspect = g_container.width() / g_container.height();
		g_camera.updateProjectionMatrix();
	}

	var initCreateTrackball = function() {
		g_trackball = new THREE.TrackballControls(g_camera, g_container[0]);
		g_trackball.rotateSpeed = 1.0;
		g_trackball.zoomSpeed = 1.2;
		g_trackball.panSpeed = 0.8;
		g_trackball.noZoom = false;
		g_trackball.noPan = false;
		g_trackball.staticMoving = true;
		g_trackball.dynamicDampingFactor = 0.3;
		g_trackball.keys = [ 65, 83, 68 ];
		g_trackball.addEventListener('change', render);
	}

	var registerHandler = function(handler) {

		g_handler = handler;

		if (g_scene !== undefined) {
			loadSectors();
			loadStars();
		}
	};

	// **
	// ** CALLBACK METHODS
	// **

	var loadSectors = function() {

		g_handler.GetSectorsAsync(onLoadSectorsComplete);

	}

	var onLoadSectorsComplete = function(sectors) {

		var sectorsLength = sectors.length;
		for (var idx = 0; idx < sectorsLength; ++idx) {
			var sector = sectors[idx];
			var minimum = new THREE.Vector3(sector.minimumX, sector.minimumY,
					sector.minimumZ);
			var maximum = new THREE.Vector3(sector.maximumX, sector.maximumY,
					sector.maximumZ);
			var box = new THREE.Box3(minimum, maximum);
			var helper = new THREE.Box3Helper(box, 0x444400);
			g_scene.add(helper);
		}

		render();
	}

	var loadStars = function() {
		g_handler.GetStarsAsync(onLoadStarsComplete);
	}

	var onLoadStarsComplete = function(stars) {

		var loader = new THREE.TextureLoader();
		loader.load("textures/sfms_star_texture_25.png", function(texture) {
			var geometryPath = new THREE.Geometry();

			var geometry = new THREE.BufferGeometry();
			geometry.addAttribute('position', new THREE.Float32BufferAttribute(
					stars, 3));
			geometry.computeBoundingSphere();

			var material = new THREE.PointsMaterial({
				size : 1,
				sizeAttenuation : true,
				map : texture,
				alphaTest : 0.5,
				transparent : false
			});
			material.color.setHSL(1.0, 0.3, 0.7);

			var points = new THREE.Points(geometry, material);
			g_scene.add(points);

			g_scene.add(new THREE.Line(geometryPath));

			var box = new THREE.BoxHelper(points, 0xffff00);
			g_scene.add(box);

			render();
		});
	}

	// **
	// ** EVENT HANDLERS
	// **

	var onWindowResize = function(e) {
		g_camera.aspect = g_container.width() / g_container.height();
		g_camera.updateProjectionMatrix();
		g_renderer.setSize(g_container.width(), g_container.height());
		g_trackball.handleResize();
		render();
	};

	// **
	// ** CORE
	// **

	var animate = function() {
		requestAnimationFrame(animate);
		g_trackball.update();
	};

	var render = function() {
		g_renderer.render(g_scene, g_camera);
		g_stats.update();
	}

	// *********************************
	// **
	// ** PUBLIC
	// **
	// *********************************

	return {

		Initialize : function(containerId) {
			initialize(containerId);
		},

		RegisterHandler : function(handler) {
			registerHandler(handler);
		}

	}

})();
