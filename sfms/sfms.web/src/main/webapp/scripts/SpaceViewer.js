"use strict";

var SpaceViewer = (function() {

	// *********************************
	// **
	// ** PRIVATE
	// **
	// *********************************

	var MIN_SECTOR_COORDINATE = 0;
	var MAX_SECTOR_COORDINATE = 9;

	// Core objects
	//
	var m_scene;
	var m_camera;
	var m_stats;
	var m_renderer;
	var m_container;
	var m_raycaster;
	var m_trackball;
	var m_handler;

	// Scene objects
	//
	var m_starGroup;
	var m_sectorCursor = {
		object : null,
		coordinates : {
			x : 0,
			y : 0,
			z : 0
		}
	};

	// UI state objects
	//
	var m_mouse = new THREE.Vector2();

	// **
	// ** INITIALZATION
	// **

	var initialize = function(containerId) {

		// m_scene
		//
		initCreateScene();
		initAddDefaultObjectsToScene();

		// m_camera
		//
		initCreateCamera();

		// m_stats
		//
		initCreateStats();

		// m_renderer
		//
		initCreateRenderer();

		// m_container
		//
		initCreateContainer(containerId);

		// m_raycaster
		//
		initCreateRaycaster();

		// m_trackball
		//
		initCreateTrackball();

		// Register event handlers.
		//
		var w = $(window);
		w.on('resize', onWindowResize);
		w.on('keydown', onWindowKeyDown);
		var d = $(document);
		m_container.on('mousemove', onDocumentMouseMove);

		render();
		animate();
	};

	var initCreateScene = function() {

		m_scene = new THREE.Scene();
	};

	var initAddDefaultObjectsToScene = function() {

		var gridHelper = new THREE.GridHelper(100, 10);
		m_scene.add(gridHelper);

		m_starGroup = new THREE.Group();
		m_scene.add(m_starGroup);

		var dirX = new THREE.Vector3(1, 0, 0);
		var dirY = new THREE.Vector3(0, 1, 0);
		var dirZ = new THREE.Vector3(0, 0, 1);
		var origin = new THREE.Vector3(0, 0, 0);
		var length = 90;
		m_scene.add(new THREE.ArrowHelper(dirX, origin, length, 0xff0000));
		m_scene.add(new THREE.ArrowHelper(dirY, origin, length, 0x00ff00));
		m_scene.add(new THREE.ArrowHelper(dirZ, origin, length, 0x0000ff));

		{
			var minimum = new THREE.Vector3(0, 0, 0);
			var maximum = new THREE.Vector3(200, 200, 200);
			var box = new THREE.Box3(minimum, maximum);
			m_sectorCursor.object = new THREE.Box3Helper(box, 0xFF0000);
			m_scene.add(m_sectorCursor.object);

			// var geometry = new THREE.BoxGeometry(200, 200, 200);
			// var material = new THREE.MeshBasicMaterial({
			// color : 0xff0000
			// });
			// m_sectorCursor.object = new THREE.Mesh(geometry, material);
			// m_scene.add(m_sectorCursor.object);
		}
	};

	var initCreateCamera = function() {
		m_camera = new THREE.PerspectiveCamera(75, window.innerWidth
				/ window.innerHeight, 1, 3000);
		m_camera.position.z = 1000;
	}

	var initCreateStats = function() {
		m_stats = new Stats();
	}

	var initCreateRenderer = function() {
		m_renderer = new THREE.WebGLRenderer();
		m_renderer.setPixelRatio(window.devicePixelRatio);
	}

	var initCreateContainer = function(containerId) {
		m_container = $(containerId);
		m_container.append(m_renderer.domElement);
		m_container.append(m_stats.dom);
		m_renderer.setSize(m_container.width(), m_container.height());
		m_camera.aspect = m_container.width() / m_container.height();
		m_camera.updateProjectionMatrix();
	}

	var initCreateRaycaster = function() {
		m_raycaster = new THREE.Raycaster();
		m_raycaster.linePrecision = 3;
	}

	var initCreateTrackball = function() {
		m_trackball = new THREE.TrackballControls(m_camera, m_container[0]);
		m_trackball.rotateSpeed = 1.0;
		m_trackball.zoomSpeed = 1.2;
		m_trackball.panSpeed = 0.8;
		m_trackball.noZoom = false;
		m_trackball.noPan = false;
		m_trackball.staticMoving = true;
		m_trackball.dynamicDampingFactor = 0.3;
		m_trackball.keys = [ 65, 83, 68 ];
		m_trackball.addEventListener('change', render);
	}

	var registerHandler = function(handler) {

		m_handler = handler;

		if (m_scene !== undefined) {
			loadSectors();
		}
	};

	// **
	// ** CALLBACK METHODS
	// **

	var loadSectors = function() {

		m_handler.GetSectorsAsync(function(sectors) {

			var sectorsLength = sectors.length;
			for (var idx = 0; idx < sectorsLength; ++idx) {
				var sector = sectors[idx];
				var minimum = new THREE.Vector3(sector.minimumX,
						sector.minimumY, sector.minimumZ);
				var maximum = new THREE.Vector3(sector.maximumX,
						sector.maximumY, sector.maximumZ);
				var box = new THREE.Box3(minimum, maximum);
				var helper = new THREE.Box3Helper(box, 0x444400);
				// m_scene.add(helper);
			}

			m_handler.GetSectorByLocationAsync(0, 0, 0, function(sector) {
				m_handler.GetStarsBySectorAsync(sector.key, function(stars) {
					var loader = new THREE.TextureLoader();
					loader.load("textures/sfms_star_texture_25.png", function(
							texture) {
						var geometryPath = new THREE.Geometry();

						var geometry = new THREE.BufferGeometry();
						geometry.addAttribute('position',
								new THREE.Float32BufferAttribute(stars, 3));
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
						m_starGroup.add(points);

						m_scene.add(new THREE.Line(geometryPath));

						var box = new THREE.BoxHelper(points, 0xffff00);
						m_scene.add(box);

						render();
					});
				});
			});
		});
	}

	// **
	// ** EVENT HANDLERS
	// **

	var onDocumentMouseMove = function(e) {

		var containerPosition = m_container.position();
		var containerX = e.pageX - containerPosition.left;
		var containerY = e.pageY - containerPosition.top;
		m_mouse.x = (containerX / m_container.width()) * 2 - 1;
		m_mouse.y = -(containerY / m_container.height()) * 2 + 1;

		// console.log(m_mouse);

		m_raycaster.setFromCamera(m_mouse, m_camera);

		var intersects = m_raycaster.intersectObjects(m_starGroup.children,
				true);
		if (intersects.length > 0) {
			console.log("onDocumentMouseMove", intersects[0].index);
			// intersects[0].object);
			// if (currentIntersected !== undefined) {
			// currentIntersected.material.linewidth = 1;
			// }
			// currentIntersected = intersects[0].object;
			// currentIntersected.material.linewidth = 5;
			// sphereInter.visible = true;
			// sphereInter.position.copy(intersects[0].point);
		} else {
			// if (currentIntersected !== undefined) {
			// currentIntersected.material.linewidth = 1;
			// }
			// currentIntersected = undefined;
			// sphereInter.visible = false;
		}

	}

	var onWindowResize = function(e) {
		m_camera.aspect = m_container.width() / m_container.height();
		m_camera.updateProjectionMatrix();
		m_renderer.setSize(m_container.width(), m_container.height());
		m_trackball.handleResize();
		render();
	};

	var onWindowKeyDown = function(e) {
		switch (e.which) {
		case 37: // LEFT
			moveSectorCursor(m_sectorCursor.coordinates.x - 1,
					m_sectorCursor.coordinates.y, m_sectorCursor.coordinates.z);
			break;
		case 39: // RIGHT
			moveSectorCursor(m_sectorCursor.coordinates.x + 1,
					m_sectorCursor.coordinates.y, m_sectorCursor.coordinates.z);
			break;
		case 38: // UP
			if (e.ctrlKey) {
				moveSectorCursor(m_sectorCursor.coordinates.x,
						m_sectorCursor.coordinates.y,
						m_sectorCursor.coordinates.z - 1);
			} else {
				moveSectorCursor(m_sectorCursor.coordinates.x,
						m_sectorCursor.coordinates.y + 1,
						m_sectorCursor.coordinates.z);
			}
			break;
		case 40: // DOWN
			if (e.ctrlKey) {
				moveSectorCursor(m_sectorCursor.coordinates.x,
						m_sectorCursor.coordinates.y,
						m_sectorCursor.coordinates.z + 1);
			} else {
				moveSectorCursor(m_sectorCursor.coordinates.x,
						m_sectorCursor.coordinates.y - 1,
						m_sectorCursor.coordinates.z);
			}
			break;
		}
	}

	var moveSectorCursor = function(x, y, z) {

		x = clamp(x, MIN_SECTOR_COORDINATE, MAX_SECTOR_COORDINATE);
		y = clamp(y, MIN_SECTOR_COORDINATE, MAX_SECTOR_COORDINATE);
		z = clamp(z, MIN_SECTOR_COORDINATE, MAX_SECTOR_COORDINATE);

		if (x === m_sectorCursor.coordinates.x
				&& y === m_sectorCursor.coordinates.y
				&& z === m_sectorCursor.coordinates.z) {
			return;
		}

		m_sectorCursor.coordinates.x = x;
		m_sectorCursor.coordinates.y = y;
		m_sectorCursor.coordinates.z = z;

		m_sectorCursor.object.box.min.set(-1000 + x * 200, -1000 + y * 200,
				-1000 + z * 200);
		m_sectorCursor.object.box.max.set(-1000 + x * 200 + 200, -1000 + y
				* 200 + 200, -1000 + z * 200 + 200);

		render();

		console.log("moveSectorCursor", m_sectorCursor.object.position);
	}

	var clamp = function(value, minimum, maximum) {
		if (value < minimum)
			return minimum;
		if (value > maximum)
			return maximum;
		return value;
	}

	// **
	// ** CORE
	// **

	var animate = function() {
		requestAnimationFrame(animate);
		m_trackball.update();
	};

	var render = function() {
		m_renderer.render(m_scene, m_camera);
		m_stats.update();
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
