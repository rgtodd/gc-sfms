"use strict";

var SpaceViewer = (function() {

	// *********************************
	// **
	// ** PRIVATE
	// **
	// *********************************

	var MIN_SPACE_COORDINATE = -1000;
	var MAX_SPACE_COORDINATE = 1000;
	var SECTOR_SIZE = 200;
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
	var m_stats;
	var m_canvas;
	var m_renderer;
	var m_camera;
	var m_container;
	var m_raycaster;
	var m_trackball;
	var m_starTexture;
	var m_shipTexture;

	var m_cameraTween = null;

	// Scene objects
	//
	var m_sectorCursor = {
		object : null,
		xyShadow : null,
		xzShadow : null,
		yzShadow : null,
		sectorKey : null,
		coordinates : {
			sx : 0,
			sy : 0,
			sz : 0
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
	var m_objectKeysByBufferName = new Map();
	var m_mouse = new THREE.Vector2();

	// Module events
	//
	var m_getSectorsHandler = null;
	var m_getSectorByLocationHandler = null;
	var m_getSectorByKeyHandler = null;
	var m_getObjectsBySectorHandler = null;
	var m_onSectorClickHandler = null;
	var m_onObjectClickHandler = null;
	var m_onObjectHoverHandler = null;

	// Retrieved data
	//
	var m_sectors;

	// **
	// ** INITIALZATION
	// **

	var initialize = function(containerId) {

		initCreateScene(); // m_scene
		initAddDefaultObjectsToScene();
		initCreateStats(); // m_stats
		initCreateRenderer(); // m_renderer
		initCreateCamera(); // m_camera
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

		// var gridHelper = new THREE.GridHelper(100, 10);
		// m_scene.add(gridHelper);

		m_objectGroup = new THREE.Group();
		m_scene.add(m_objectGroup);

		// var dirX = new THREE.Vector3(1, 0, 0);
		// var dirY = new THREE.Vector3(0, 1, 0);
		// var dirZ = new THREE.Vector3(0, 0, 1);
		// var origin = new THREE.Vector3(0, 0, 0);
		// var length = 90;
		// m_scene.add(new THREE.ArrowHelper(dirX, origin, length, 0xff0000));
		// m_scene.add(new THREE.ArrowHelper(dirY, origin, length, 0x00ff00));
		// m_scene.add(new THREE.ArrowHelper(dirZ, origin, length, 0x0000ff));

		// Sector cursor
		{
			var minimum = new THREE.Vector3(0, 0, 0);
			var maximum = new THREE.Vector3(SECTOR_SIZE, SECTOR_SIZE,
					SECTOR_SIZE);
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

		// Boundaries
		{
			var xyGeometry = new THREE.Geometry();
			var xzGeometry = new THREE.Geometry();
			var yzGeometry = new THREE.Geometry();
			for (var i = MIN_SPACE_COORDINATE; i <= MAX_SPACE_COORDINATE; i += SECTOR_SIZE) {
				xyGeometry.vertices.push(
						//
						new THREE.Vector3(i, MIN_SPACE_COORDINATE,
								MIN_SPACE_COORDINATE), //
						new THREE.Vector3(i, MAX_SPACE_COORDINATE,
								MIN_SPACE_COORDINATE), //
						new THREE.Vector3(MIN_SPACE_COORDINATE, i,
								MIN_SPACE_COORDINATE), //
						new THREE.Vector3(MAX_SPACE_COORDINATE, i,
								MIN_SPACE_COORDINATE));
				xzGeometry.vertices.push(
						//
						new THREE.Vector3(i, MIN_SPACE_COORDINATE,
								MIN_SPACE_COORDINATE), //
						new THREE.Vector3(i, MIN_SPACE_COORDINATE,
								MAX_SPACE_COORDINATE), //
						new THREE.Vector3(MIN_SPACE_COORDINATE,
								MIN_SPACE_COORDINATE, i), //
						new THREE.Vector3(MAX_SPACE_COORDINATE,
								MIN_SPACE_COORDINATE, i));
				yzGeometry.vertices.push(
						//
						new THREE.Vector3(MIN_SPACE_COORDINATE, i,
								MIN_SPACE_COORDINATE), //
						new THREE.Vector3(MIN_SPACE_COORDINATE, i,
								MAX_SPACE_COORDINATE), //
						new THREE.Vector3(MIN_SPACE_COORDINATE,
								MIN_SPACE_COORDINATE, i), //
						new THREE.Vector3(MIN_SPACE_COORDINATE,
								MAX_SPACE_COORDINATE, i));
			}

			var material = new THREE.MeshBasicMaterial({
				color : 0x0000ff
			});

			m_scene.add(new THREE.LineSegments(xyGeometry, material));
			m_scene.add(new THREE.LineSegments(xzGeometry, material));
			m_scene.add(new THREE.LineSegments(yzGeometry, material));
		}

		{
			var geometry = new THREE.PlaneGeometry(SECTOR_SIZE, SECTOR_SIZE);

			var material = new THREE.MeshBasicMaterial({
				color : 0x0000ff,
				side : THREE.DoubleSide
			});

			m_sectorCursor.xyShadow = new THREE.Mesh(geometry, material);
			m_scene.add(m_sectorCursor.xyShadow);

			m_sectorCursor.xzShadow = new THREE.Mesh(geometry, material);
			m_sectorCursor.xzShadow.rotation.set(Math.PI / 2.0, 0, 0);
			m_scene.add(m_sectorCursor.xzShadow);

			m_sectorCursor.yzShadow = new THREE.Mesh(geometry, material);
			m_sectorCursor.yzShadow.rotation.set(0, Math.PI / 2.0, 0);
			m_scene.add(m_sectorCursor.yzShadow);
		}

	};

	var initCreateStats = function() {
		m_stats = new Stats();
	}

	var initCreateRenderer = function() {
		m_canvas = $("#canvasSpaceViewer");

		m_renderer = new THREE.WebGLRenderer({
			canvas : m_canvas[0]
		});
		m_renderer.setPixelRatio(window.devicePixelRatio);
		m_renderer.setSize(m_canvas.width(), m_canvas.height(), false);
	}

	var initCreateCamera = function() {
		m_camera = new THREE.PerspectiveCamera(75, m_canvas.width()
				/ m_canvas.height(), 1, 3000);
		m_camera.position.z = 1250;
		m_camera.updateProjectionMatrix();
	}

	var initCreateContainer = function(containerId) {
		m_container = $(containerId);
		m_container.append(m_stats.dom);
	}

	var initCreateRaycaster = function() {
		m_raycaster = new THREE.Raycaster();
		m_raycaster.params.Points.threshold = 3;
	}

	var initCreateTrackball = function() {
		m_trackball = new THREE.TrackballControls(m_camera, m_canvas[0]);
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
		var containerPosition = m_canvas.offset();
		var containerX = e.pageX - containerPosition.left;
		var containerY = e.pageY - containerPosition.top;
		m_mouse.x = (containerX / m_canvas.width()) * 2 - 1;
		m_mouse.y = -(containerY / m_canvas.height()) * 2 + 1;

		// Determine objects that intersect mouse position.
		//
		m_raycaster.setFromCamera(m_mouse, m_camera);
		var intersections = m_raycaster.intersectObjects(
				m_objectGroup.children, true);

		if (intersections.length > 0) {
			var intersection = intersections[0];
			var index = intersection.index;
			var buffer = intersection.object;
			var parsedBufferName = parseBufferName(buffer.name);
			var objectKeys = m_objectKeysByBufferName.get(buffer.name);
			var objectKey = objectKeys[index];

			if (m_objectCursor.objectType !== parsedBufferName.objectType
					|| m_objectCursor.objectKey !== objectKey) {

				var x = buffer.geometry.attributes.position.array[index * 3];
				var y = buffer.geometry.attributes.position.array[index * 3 + 1];
				var z = buffer.geometry.attributes.position.array[index * 3 + 2];
				var objectPosition = new THREE.Vector3(x, y, z);

				moveObjectHighlight(parsedBufferName.objectType, objectKey,
						objectPosition);
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
		m_renderer.setSize(m_canvas.width(), m_canvas.height(), false);
		m_camera.aspect = m_canvas.width() / m_canvas.height();
		m_camera.updateProjectionMatrix();
		m_trackball.handleResize();
		// render();
	};

	var onWindowKeyDown = function(e) {
		switch (e.which) {
		case 37: // LEFT
			moveSectorCursor(m_sectorCursor.coordinates.sx - 1,
					m_sectorCursor.coordinates.sy,
					m_sectorCursor.coordinates.sz);
			break;
		case 39: // RIGHT
			moveSectorCursor(m_sectorCursor.coordinates.sx + 1,
					m_sectorCursor.coordinates.sy,
					m_sectorCursor.coordinates.sz);
			break;
		case 38: // UP
			if (e.ctrlKey) {
				moveSectorCursor(m_sectorCursor.coordinates.sx,
						m_sectorCursor.coordinates.sy,
						m_sectorCursor.coordinates.sz - 1);
			} else {
				moveSectorCursor(m_sectorCursor.coordinates.sx,
						m_sectorCursor.coordinates.sy + 1,
						m_sectorCursor.coordinates.sz);
			}
			break;
		case 40: // DOWN
			if (e.ctrlKey) {
				moveSectorCursor(m_sectorCursor.coordinates.sx,
						m_sectorCursor.coordinates.sy,
						m_sectorCursor.coordinates.sz + 1);
			} else {
				moveSectorCursor(m_sectorCursor.coordinates.sx,
						m_sectorCursor.coordinates.sy - 1,
						m_sectorCursor.coordinates.sz);
			}
			break;
		}
	}

	// **
	// ** CORE
	// **

	var loadSectors = function() {

		raiseGetSectors(function(sectors) {
			m_sectors = sectors;
			moveSectorCursor(5, 5, 5);
		});
	}

	var animate = function() {
		requestAnimationFrame(animate);
		TWEEN.update();
		animateObjectHighlight();
		animateObjectCursor();
		m_trackball.update();
		render();
	};

	var render = function() {
		m_renderer.render(m_scene, m_camera);
		$("#ctrlDebug").html(
				"Position = " + m_camera.position.x + "," + m_camera.position.y
						+ "," + m_camera.position.z + "<br>FOV = "
						+ m_camera.fov);
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

	var registerOnSectorClickHandler = function(handler) {
		m_onSectorClickHandler = handler;
	}

	var raiseOnSectorClick = function(sectorKey) {
		if (m_onSectorClickHandler !== null) {
			m_onSectorClickHandler(sectorKey);
		}
	}

	var registerOnObjectClickHandler = function(handler) {
		m_onObjectClickHandler = handler;
	}

	var raiseOnObjectClick = function(objectType, objectKey) {
		if (m_onObjectClickHandler !== null) {
			m_onObjectClickHandler(objectType, objectKey);
		}
	}

	var registerOnObjectHoverHandler = function(handler) {
		m_onObjectHoverHandler = handler;
	}

	var raiseOnObjectHover = function(objectType, objectKey) {
		if (m_onObjectHoverHandler !== null) {
			m_onObjectHoverHandler(objectType, objectKey);
		}
	}

	// **
	// ** UI
	// **

	var moveSectorCursor = function(sx, sy, sz) {

		sx = clamp(sx, MIN_SECTOR_COORDINATE, MAX_SECTOR_COORDINATE);
		sy = clamp(sy, MIN_SECTOR_COORDINATE, MAX_SECTOR_COORDINATE);
		sz = clamp(sz, MIN_SECTOR_COORDINATE, MAX_SECTOR_COORDINATE);

		if (sx === m_sectorCursor.coordinates.sx
				&& sy === m_sectorCursor.coordinates.sy
				&& sz === m_sectorCursor.coordinates.sz) {
			return;
		}

		var sector = m_sectors.find(function(element) {
			return element.sx === sx && element.sy === sy && element.sz === sz;
		});

		if (sector === null || sector === undefined) {
			return;
		}

		hideObjectHighlight();
		hideObjectCursor();

		m_sectorCursor.coordinates.sx = sx;
		m_sectorCursor.coordinates.sy = sy;
		m_sectorCursor.coordinates.sz = sz;
		m_sectorCursor.object.box.min.set(sector.minimumX, sector.minimumY,
				sector.minimumZ);
		m_sectorCursor.object.box.max.set(sector.maximumX, sector.maximumY,
				sector.maximumZ);

		lookAtSector(sector);

		raiseOnSectorClick(sector.key);

		// m_objectGroup.children.forEach(function(element) {
		// element.geometry.dispose();
		// element.material.dispose();
		// });
		// m_objectGroup.children = [];
		// m_objectKeysByType.clear();

		OBJECT_TYPES.forEach(function(objectType) {

			var bufferName = createBufferName(sector.key, objectType);
			if (!m_objectKeysByBufferName.has(bufferName)) {
			
				m_objectKeysByBufferName.set(bufferName, "TEMP");
				
				raiseGetObjectsBySector(sector.key, objectType, function(
						objectKeys, objectPoints) {
					m_objectKeysByBufferName.set(bufferName, objectKeys);
					var buffer = createBuffer(objectType, objectPoints);
					buffer.name = bufferName;
					m_objectGroup.add(buffer);
				});
			}
		});
	}

	var createBuffer = function(objectType, objectPoints) {

		var geometry = new THREE.BufferGeometry();
		geometry.addAttribute('position', new THREE.Float32BufferAttribute(
				objectPoints, 3));

		var material = new THREE.PointsMaterial({
			size : 1,
			sizeAttenuation : true,
			map : getTextureForObjectType(objectType),
			alphaTest : 0.5,
			transparent : false
		});
		material.color.setHSL(1.0, 0.3, 0.7);

		var points = new THREE.Points(geometry, material);

		return points;

	}

	var moveObjectCursor = function(objectType, objectKey, objectPosition) {
		if (m_objectCursor.objectType !== objectType
				|| m_objectCursor.objectKey !== objectKey) {
			m_objectCursor.objectType = objectType;
			m_objectCursor.objectKey = objectKey;
			m_objectCursor.object.visible = true;
			m_objectCursor.object.position.copy(objectPosition);

			lookAtObject(objectPosition);

			raiseOnObjectClick(objectType, objectKey);
		}
	}

	var hideObjectCursor = function() {
		if (m_objectCursor.objectType !== null) {
			m_objectCursor.objectType = null;
			m_objectCursor.objectKey = null;
			m_objectCursor.object.visible = false;

			raiseOnObjectClick(null, null);
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

			raiseOnObjectHover(objectType, objectKey);
		}
	}

	var hideObjectHighlight = function() {
		if (m_objectHighlight.objectType !== null) {
			m_objectHighlight.objectType = null;
			m_objectHighlight.objectKey = null;
			m_objectHighlight.object.visible = false;

			raiseOnObjectHover(null, null);
		}
	}

	var animateObjectHighlight = function() {
		m_objectHighlight.object.rotation.y += 0.015;
	}

	var lookAtSector = function(sector) {

		var xMidpoint = (sector.minimumX + sector.maximumX) / 2;
		var yMidpoint = (sector.minimumY + sector.maximumY) / 2;
		var zMidpoint = (sector.minimumZ + sector.maximumZ) / 2;
		m_sectorCursor.xyShadow.position.set(xMidpoint, yMidpoint,
				MIN_SPACE_COORDINATE);
		m_sectorCursor.xzShadow.position.set(xMidpoint, MIN_SPACE_COORDINATE,
				zMidpoint);
		m_sectorCursor.yzShadow.position.set(MIN_SPACE_COORDINATE, yMidpoint,
				zMidpoint);

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
		m_cameraTween = new TWEEN.Tween(tweenState).to({
			x : xMidpoint,
			y : yMidpoint,
			z : zMidpoint
		}, 1000).easing(TWEEN.Easing.Quadratic.Out).onUpdate(
				function() {
					m_camera.position.set(tweenState.x, tweenState.y + 150,
							tweenState.z + 250);
					m_trackball.target.set(tweenState.x, tweenState.y,
							tweenState.z);
				}).start();
	}

	var lookAtObject = function(objectPosition) {
		if (m_cameraTween !== null) {
			m_cameraTween.stop();
			m_cameraTween = null;
		}

		var tweenState = {
			x : m_trackball.target.x,
			y : m_trackball.target.y,
			z : m_trackball.target.z
		};
		m_cameraTween = new TWEEN.Tween(tweenState).to({
			x : objectPosition.x,
			y : objectPosition.y,
			z : objectPosition.z
		}, 1000).easing(TWEEN.Easing.Quadratic.Out).onUpdate(function() {
			m_trackball.target.set(tweenState.x, tweenState.y, tweenState.z);
		}).start();
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

	var createBufferName = function(sectorKey, objectType) {
		return sectorKey + "|" + getObjectNameFromType(objectType);
	}

	var parseBufferName = function(bufferName) {
		var indexDelimiter = bufferName.indexOf("|");
		return {
			sectorKey : bufferName.substring(0, indexDelimiter),
			objectType : getObjectTypeFromName(bufferName
					.substring(indexDelimiter + 1))
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
		// * sx = Number
		// * sy = Number
		// * sz = Number
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
		},

		// RegisterOnSectorClickHandler - raised when the user selects a new
		// sector.
		//
		// handler = function(sectorKey) where:
		//
		// sectorKey = String.
		//
		RegisterOnSectorClickHandler : function(handler) {
			registerOnSectorClickHandler(handler);
		},

		// RegisterOnObjectClickHandler - raised when the user selects a new
		// object.
		//
		// handler = function(objectType, objectKey) where:
		//
		// objectType = Number where:
		// * 1 = Star
		// * 2 = Ship
		//
		// objectKey = String.
		//
		RegisterOnObjectClickHandler : function(handler) {
			registerOnObjectClickHandler(handler);
		},

		// RegisterOnObjectHoverHandler - raised when the user hovers over an
		// object.
		//
		// handler = function(objectType, objectKey) where:
		//
		// objectType = Number where:
		// * 1 = Star
		// * 2 = Ship
		//
		// objectKey = String.
		//
		RegisterOnObjectHoverHandler : function(handler) {
			registerOnObjectHoverHandler(handler);
		}
	}

})();
