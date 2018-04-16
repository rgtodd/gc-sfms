"use strict";

var SpaceViewer = (function() {

	// *********************************
	// **
	// ** PRIVATE
	// **
	// *********************************

	var MIN_SECTOR_COORDINATE = 0;
	var MAX_SECTOR_COORDINATE = 9;

	var OBJECT_TYPE_STAR = 0;
	var OBJECT_TYPE_SHIP = 1;
	var OBJECT_TYPES = [ OBJECT_TYPE_STAR, OBJECT_TYPE_SHIP ];

	var OBJECT_NAME_STAR = "STAR";
	var OBJECT_NAME_SHIP = "SHIP";

	// Core objects
	//
	var m_scene;
	var m_camera;
	var m_stats;
	var m_renderer;
	var m_container;
	var m_raycaster;
	var m_trackball;
	var m_starTexture;
	var m_shipTexture;

	// Scene objects
	//
	var m_sectorCursor = {
		object : null,
		sectorKey : null,
		coordinates : {
			x : 0,
			y : 0,
			z : 0
		}
	};
	var m_objectHighlight = {
		object : null,
		objectType : null,
		objectKey : null
	}
	var m_objectCursor = {
		object : null,
		objectType : null,
		objectKey : null
	}
	var m_objectGroup; // parent Object3D of all selectable objects.

	// UI state objects
	//
	var m_objectKeysByType = new Map();
	var m_mouse = new THREE.Vector2();

	// Module Events
	//
	var m_getSectorsHandler = null;
	var m_getSectorByLocationHandler = null;
	var m_getSectorByKeyHandler = null;
	var m_getObjectsBySectorHandler = null;

	// **
	// ** INITIALZATION
	// **

	var initialize = function(containerId) {

		initCreateScene(); // m_scene
		initAddDefaultObjectsToScene();
		initCreateCamera(); // m_camera
		initCreateStats(); // m_stats
		initCreateRenderer(); // m_renderer
		initCreateContainer(containerId); // m_container
		initCreateRaycaster(); // m_raycaster
		initCreateTrackball(); // m_trackball
		initCreateTextures(); // m_starTexture, m_shipTexture

		// Register event handlers.
		//
		var w = $(window);
		w.on('resize', onWindowResize);
		w.on('keydown', onWindowKeyDown);
		var d = $(document);
		m_container.on('mousemove', onDocumentMouseMove);
		m_container.on('click', onDocumentMouseClick);

		// render();
		animate();

		loadSectors();
	};

	var initCreateScene = function() {

		m_scene = new THREE.Scene();
	};

	var initAddDefaultObjectsToScene = function() {

		var gridHelper = new THREE.GridHelper(100, 10);
		m_scene.add(gridHelper);

		m_objectGroup = new THREE.Group();
		m_scene.add(m_objectGroup);

		var dirX = new THREE.Vector3(1, 0, 0);
		var dirY = new THREE.Vector3(0, 1, 0);
		var dirZ = new THREE.Vector3(0, 0, 1);
		var origin = new THREE.Vector3(0, 0, 0);
		var length = 90;
		m_scene.add(new THREE.ArrowHelper(dirX, origin, length, 0xff0000));
		m_scene.add(new THREE.ArrowHelper(dirY, origin, length, 0x00ff00));
		m_scene.add(new THREE.ArrowHelper(dirZ, origin, length, 0x0000ff));

		// Sector cursor
		{
			var minimum = new THREE.Vector3(0, 0, 0);
			var maximum = new THREE.Vector3(200, 200, 200);
			var box = new THREE.Box3(minimum, maximum);
			m_sectorCursor.object = new THREE.Box3Helper(box, 0xff0000);
			m_scene.add(m_sectorCursor.object);
		}

		// Object cursor
		{
			var geometry = new THREE.SphereGeometry(1.5, 3, 2);
			var material = new THREE.MeshBasicMaterial({
				color : 0xff0000,
				wireframe : true
			});
			m_objectCursor.object = new THREE.Mesh(geometry, material);
			m_objectCursor.object.visible = false;
			m_scene.add(m_objectCursor.object);
		}

		// Object highlight
		{
			var geometry = new THREE.SphereGeometry(1.5, 3, 2);
			var material = new THREE.MeshBasicMaterial({
				color : 0x00ff00,
				wireframe : true
			});
			m_objectHighlight.object = new THREE.Mesh(geometry, material);
			m_objectHighlight.object.visible = false;
			m_scene.add(m_objectHighlight.object);
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
		m_raycaster.params.Points.threshold = 3;
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
		// m_trackball.addEventListener('change', render);
	}

	var initCreateTextures = function() {
		var loader = new THREE.TextureLoader();
		m_starTexture = loader.load("textures/sfms_star.png");
		m_shipTexture = loader.load("textures/sfms_ship.png");
	}

	// **
	// ** EVENT HANDLERS
	// **

	var onDocumentMouseClick = function(e) {

		if (m_objectHighlight.objectType === null) {
			hideObjectCursor();
		} else {
			moveObjectCursor(m_objectHighlight.objectType,
					m_objectHighlight.objectKey,
					m_objectHighlight.object.position);
			hideObjectHighlight();
		}

	}

	var onDocumentMouseMove = function(e) {

		if (e.buttons == 0) {
			onDocumentMouseMoveHover(e);
		}

	}

	var onDocumentMouseMoveHover = function(e) {

		// Determine normalized mouse position used for ray-casting.
		//
		var containerPosition = m_container.position();
		var containerX = e.pageX - containerPosition.left;
		var containerY = e.pageY - containerPosition.top;
		m_mouse.x = (containerX / m_container.width()) * 2 - 1;
		m_mouse.y = -(containerY / m_container.height()) * 2 + 1;

		// Determine objects that intersect mouse position.
		//
		m_raycaster.setFromCamera(m_mouse, m_camera);
		var intersections = m_raycaster.intersectObjects(
				m_objectGroup.children, true);

		if (intersections.length > 0) {
			var intersection = intersections[0];
			var index = intersection.index;
			var object = intersection.object;

			var objectType = getObjectTypeFromName(object.name);
			var objectKey = m_objectKeysByType.get(objectType)[index];

			if (m_objectCursor.objectType !== objectType
					|| m_objectCursor.objectKey !== objectKey) {

				var x = object.geometry.attributes.position.array[index * 3];
				var y = object.geometry.attributes.position.array[index * 3 + 1];
				var z = object.geometry.attributes.position.array[index * 3 + 2];
				var objectPosition = new THREE.Vector3(x, y, z);

				moveObjectHighlight(objectType, objectKey, objectPosition);
			} else {
				// Object is already selected. Don't highlight it.
				//
				hideObjectHighlight();
			}
		} else {
			hideObjectHighlight();
		}
	}

	var onWindowResize = function(e) {
		m_camera.aspect = m_container.width() / m_container.height();
		m_camera.updateProjectionMatrix();
		m_renderer.setSize(m_container.width(), m_container.height());
		m_trackball.handleResize();
		// render();
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

	// **
	// ** CORE
	// **

	var loadSectors = function() {

		raiseGetSectors(function(sectors) {

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

			raiseGetSectorByLocation(0, 0, 0, function(sector) {

				m_sectorCursor.sectorKey = sector.key;

				OBJECT_TYPES.forEach(function(objectType) {
					raiseGetObjectsBySector(sector.key, objectType, function(
							objectKeys, objectPoints) {

						m_objectKeysByType.set(objectType, objectKeys);

						var geometry = new THREE.BufferGeometry();
						geometry.addAttribute('position',
								new THREE.Float32BufferAttribute(objectPoints,
										3));

						var material = new THREE.PointsMaterial({
							size : 3,
							sizeAttenuation : true,
							map : getTextureForObjectType(objectType),
							alphaTest : 0.5,
							transparent : false
						});
						material.color.setHSL(1.0, 0.3, 0.7);

						var points = new THREE.Points(geometry, material);
						points.name = getObjectNameFromType(objectType);
						m_objectGroup.add(points);

					});
				});
			});
		});
	}

	var animate = function() {
		requestAnimationFrame(animate);
		animateObjectHighlight();
		animateObjectCursor();
		m_trackball.update();
		render();
	};

	var render = function() {
		m_renderer.render(m_scene, m_camera);
		m_stats.update();
	}

	// **
	// ** MODULE EVENTS
	// **

	var registerGetSectorsHandler = function(handler) {
		m_getSectorsHandler = handler;
	}

	var raiseGetSectors = function(callback) {
		if (m_getSectorsHandler !== null) {
			m_getSectorsHandler(callback);
		}
	}

	var registerGetSectorByLocationHandler = function(handler) {
		m_getSectorByLocationHandler = handler;
	}

	var raiseGetSectorByLocation = function(x, y, z, callback) {
		if (m_getSectorByLocationHandler !== null) {
			m_getSectorByLocationHandler(x, y, z, callback);
		}
	}

	var registerGetSectorByKeyHandler = function(handler) {
		m_getSectorByKeyHandler = handler;
	}

	var raiseGetSectorByKey = function(key, callback) {
		if (m_getSectorByKeyHandler !== null) {
			m_getSectorByKeyHandler(key, callback);
		}
	}

	var registerGetObjectsBySectorHandler = function(handler) {
		m_getObjectsBySectorHandler = handler;
	}

	var raiseGetObjectsBySector = function(sectorKey, objectType, callback) {
		if (m_getObjectsBySectorHandler !== null) {
			m_getObjectsBySectorHandler(sectorKey, objectType, callback);
		}
	}

	// **
	// ** UI
	// **

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

		// render();

		console.log("moveSectorCursor", m_sectorCursor.object.position);
	}

	var moveObjectCursor = function(objectType, objectKey, objectPosition) {
		if (m_objectCursor.objectType !== objectType
				|| m_objectCursor.objectKey !== objectKey) {
			m_objectCursor.objectType = objectType;
			m_objectCursor.objectKey = objectKey;
			m_objectCursor.object.visible = true;
			m_objectCursor.object.position.copy(objectPosition);
		}
	}

	var hideObjectCursor = function() {
		if (m_objectCursor.objectType !== null) {
			m_objectCursor.objectType = null;
			m_objectCursor.objectKey = null;
			m_objectCursor.object.visible = false;
		}
	}

	var animateObjectCursor = function() {
		m_objectCursor.object.rotation.y += 0.015;
	}

	var moveObjectHighlight = function(objectType, objectKey, objectPosition) {
		if (m_objectHighlight.objectType !== objectType
				|| m_objectHighlight.objectKey !== objectKey) {
			m_objectHighlight.objectType = objectType;
			m_objectHighlight.objectKey = objectKey;
			m_objectHighlight.object.visible = true;
			m_objectHighlight.object.position.copy(objectPosition);
		}
	}

	var hideObjectHighlight = function() {
		if (m_objectHighlight.objectType !== null) {
			m_objectHighlight.objectType = null;
			m_objectHighlight.objectKey = null;
			m_objectHighlight.object.visible = false;
		}
	}

	var animateObjectHighlight = function() {
		m_objectHighlight.object.rotation.y += 0.015;
	}

	// **
	// ** UTILITY
	// **

	var clamp = function(value, minimum, maximum) {
		if (value < minimum)
			return minimum;
		if (value > maximum)
			return maximum;
		return value;
	}

	var getTextureForObjectType = function(objectType) {
		switch (objectType) {
		case OBJECT_TYPE_STAR:
			return m_starTexture;
		case OBJECT_TYPE_SHIP:
			return m_shipTexture;
		default:
			throw "Unknown objectType" + objectType;
		}
	}

	var getObjectNameFromType = function(objectType) {
		switch (objectType) {
		case OBJECT_TYPE_STAR:
			return OBJECT_NAME_STAR;
		case OBJECT_TYPE_SHIP:
			return OBJECT_NAME_SHIP;
		default:
			throw "Unknown objectType " + objectType;
		}
	}

	var getObjectTypeFromName = function(objectName) {
		switch (objectName) {
		case OBJECT_NAME_STAR:
			return OBJECT_TYPE_STAR;
		case OBJECT_NAME_SHIP:
			return OBJECT_TYPE_SHIP;
		default:
			throw "Unknown objectName " + objectName;
		}
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

		Initialize : function(containerId) {
			initialize(containerId);
		},

		// **
		// ** MODULE EVENTS
		// **

		// RegisterGetSectorsHandler - raised to retrieve all sectors.
		//
		// handler = function(callback) where:
		//
		// callback = function(sectors) where:
		//
		// sectors = array of sector
		//
		// sector = object of:
		// * key = String
		// * minimumX = Number
		// * maximumX = Number
		// * minimumY = Number
		// * maximumY = Number
		// * minimumZ = Number
		// * maximumZ = Number
		//
		RegisterGetSectorsHandler : function(handler) {
			registerGetSectorsHandler(handler);
		},

		// RegisterGetSectorByLocationHandler - raised to retrieve sector
		// containing the specified location.
		//
		// handler = function(x, y, z, callback) where:
		//
		// x, y and z = Number. Specify a location within the desired sector.
		//
		// callback = function(sector) where:
		//
		// sector = object of:
		// * key = String
		// * minimumX = Number
		// * maximumX = Number
		// * minimumY = Number
		// * maximumY = Number
		// * minimumZ = Number
		// * maximumZ = Number
		//		
		RegisterGetSectorByLocationHandler : function(handler) {
			registerGetSectorByLocationHandler(handler);
		},

		// RegisterGetSectorByKeyHandler - raised to retrieve specified sector.
		//
		// handler = function(key, callback) where:
		//
		// key = String.
		//
		// callback = function(sector) where:
		//
		// sector = object of:
		// * key = String
		// * minimumX = Number
		// * maximumX = Number
		// * minimumY = Number
		// * maximumY = Number
		// * minimumZ = Number
		// * maximumZ = Number
		//
		RegisterGetSectorByKeyHandler : function(handler) {
			registerGetSectorByKeyHandler(handler);
		},

		// RegisterGetObjectsBySectorHandler - raised to retrieve objects within
		// the specified sector.
		//
		// handler = function(sectorKey, objectType, callback) where:
		//
		// sectorKey = String.
		//
		// objectType = Number where:
		// * 1 = Star
		// * 2 = Ship
		//
		// callback = function(objectKeys, objectPoints) where:
		//
		// objectKeys = array of String.
		//
		// objectPoints = array of Number. Specifies location of objects in
		// sector in a denormalized format (i.e. X1, Y1, Z1, X2, Y2, Z2, ...)
		//
		RegisterGetObjectsBySectorHandler : function(handler) {
			registerGetObjectsBySectorHandler(handler);
		}
	}

})();
