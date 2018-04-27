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

	var MAP_ITEM_TYPE_STAR = 0;
	var MAP_ITEM_TYPE_SHIP = 1;
	var MAP_ITEM_TYPES = [ MAP_ITEM_TYPE_STAR, MAP_ITEM_TYPE_SHIP ];

	var MAP_ITEM_NAME_STAR = "STAR";
	var MAP_ITEM_NAME_SHIP = "SHIP";

	var RAYCASTER_THRESHOLD = 1;

	// Core objects
	//
	var m_scene;
	var m_stats;
	var m_canvas;
	var m_renderer;
	var m_camera;
	var m_container;
	var m_raycaster;
	// var m_trackball;
	var m_starTexture;
	var m_shipTexture;

	// var m_cameraTween = null;

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
	var m_mapItemHighlight = {
		object : null,
		mapItemType : null,
		mapItemKey : null
	}
	var m_mapItemCursor = {
		object : null,
		mapItemType : null,
		mapItemKey : null
	}
	var m_objectGroup; // parent Object3D of all selectable objects.

	// UI state objects
	//
	var m_workQueue = [];
	var m_mapItemKeysByBufferName = new Map();
	var m_mouse = new THREE.Vector2();

	// Module events
	//
	var m_getSectorsHandler = null;
	var m_getSectorByLocationHandler = null;
	var m_getSectorByKeyHandler = null;
	var m_getMapItemsBySectorHandler = null;
	var m_getMapItemsByRankHandler = null;
	var m_onSectorClickHandler = null;
	var m_onMapItemClickHandler = null;
	var m_onMapItemHoverHandler = null;

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
		// initCreateTrackball(); // m_trackball
		initCreateTextures(); // m_starTexture, m_shipTexture

		SpaceViewerController.Initialize(m_canvas[0], m_camera);

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

		// Map item cursor
		{
			var geometry = new THREE.SphereGeometry(0.75, 4, 4);
			var material = new THREE.MeshBasicMaterial({
				color : 0xff0000,
				wireframe : true
			});
			m_mapItemCursor.object = new THREE.Mesh(geometry, material);
			m_mapItemCursor.object.visible = false;
			m_scene.add(m_mapItemCursor.object);
		}

		// Map item highlight
		{
			var geometry = new THREE.SphereGeometry(0.75, 4, 4);
			var material = new THREE.MeshBasicMaterial({
				color : 0x00ff00,
				wireframe : true
			});
			m_mapItemHighlight.object = new THREE.Mesh(geometry, material);
			m_mapItemHighlight.object.visible = false;
			m_scene.add(m_mapItemHighlight.object);
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
		m_raycaster.params.Points.threshold = RAYCASTER_THRESHOLD;
	}

	// var initCreateTrackball = function() {
	// m_trackball = new THREE.TrackballControls(m_camera, m_canvas[0]);
	// m_trackball.rotateSpeed = 1.0;
	// m_trackball.zoomSpeed = 1.2;
	// m_trackball.panSpeed = 0.8;
	// m_trackball.noZoom = false;
	// m_trackball.noPan = false;
	// m_trackball.staticMoving = true;
	// m_trackball.dynamicDampingFactor = 0.3;
	// m_trackball.keys = [ 65, 83, 68 ];
	// // m_trackball.addEventListener('change', render);
	// }

	var initCreateTextures = function() {
		var loader = new THREE.TextureLoader();
		m_starTexture = loader.load("textures/sfms_star.png");
		m_shipTexture = loader.load("textures/sfms_ship.png");
	}

	// **
	// ** EVENT HANDLERS
	// **

	var onDocumentMouseClick = function(e) {

		if (m_mapItemHighlight.mapItemType === null) {
			hideMapItemCursor();
		} else {
			moveMapItemCursor(m_mapItemHighlight.mapItemType,
					m_mapItemHighlight.mapItemKey,
					m_mapItemHighlight.object.position);
			hideMapItemHighlight();
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
			var mapItemKeys = m_mapItemKeysByBufferName.get(buffer.name);
			var mapItemKey = mapItemKeys[index];

			if (m_mapItemCursor.mapItemType !== parsedBufferName.mapItemType
					|| m_mapItemCursor.mapItemKey !== mapItemKey) {

				var x = buffer.geometry.attributes.position.array[index * 3];
				var y = buffer.geometry.attributes.position.array[index * 3 + 1];
				var z = buffer.geometry.attributes.position.array[index * 3 + 2];
				var mapItemPosition = new THREE.Vector3(x, y, z);

				moveMapItemHighlight(parsedBufferName.mapItemType, mapItemKey,
						mapItemPosition);
			} else {
				// Object is already selected. Don't highlight it.
				//
				hideMapItemHighlight();
			}
		} else {
			hideMapItemHighlight();
		}
	}

	var onWindowResize = function(e) {
		m_renderer.setSize(m_canvas.width(), m_canvas.height(), false);
		// m_camera.aspect = m_canvas.width() / m_canvas.height();
		// m_camera.updateProjectionMatrix();
		// m_trackball.handleResize();
		SpaceViewerController.OnWindowResize();
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

			populateWorkQueue();
			processWorkQueue();
		});
	}

	var animate = function() {
		requestAnimationFrame(animate);
		TWEEN.update(); // required by SpaceViewerController
		animateMapItemHighlight();
		animateMapItemCursor();
		// m_trackball.update();
		SpaceViewerController.Animate();
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

	var registerGetMapItemsBySectorHandler = function(handler) {
		m_getMapItemsBySectorHandler = handler;
	}

	var raiseGetMapItemsBySector = function(sectorKey, mapItemType, callback) {
		if (m_getMapItemsBySectorHandler !== null) {
			m_getMapItemsBySectorHandler(sectorKey, mapItemType, callback);
		}
	}

	var registerGetMapItemsByRankHandler = function(handler) {
		m_getMapItemsByRankHandler = handler;
	}

	var raiseGetMapItemsByRank = function(rank, callback) {
		if (m_getMapItemsByRankHandler !== null) {
			m_getMapItemsByRankHandler(rank, callback);
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

	var registerOnMapItemClickHandler = function(handler) {
		m_onMapItemClickHandler = handler;
	}

	var raiseOnMapItemClick = function(mapItemType, mapItemKey) {
		if (m_onMapItemClickHandler !== null) {
			m_onMapItemClickHandler(mapItemType, mapItemKey);
		}
	}

	var registerOnMapItemHoverHandler = function(handler) {
		m_onMapItemHoverHandler = handler;
	}

	var raiseOnMapItemHover = function(mapItemType, mapItemKey) {
		if (m_onMapItemHoverHandler !== null) {
			m_onMapItemHoverHandler(mapItemType, mapItemKey);
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

		hideMapItemHighlight();
		hideMapItemCursor();

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

		// MAP_ITEM_TYPES.forEach(function(mapItemType) {
		//
		// var bufferName = createBufferName(sector.key, mapItemType);
		// if (!m_mapItemKeysByBufferName.has(bufferName)) {
		// m_mapItemKeysByBufferName.set(bufferName, "TEMP");
		//
		// raiseGetMapItemsBySector(sector.key, mapItemType, function(
		// mapItemKeys, mapItemPoints) {
		// m_mapItemKeysByBufferName.set(bufferName, mapItemKeys);
		// var buffer = createBuffer(mapItemType, mapItemPoints);
		// buffer.name = bufferName;
		// m_objectGroup.add(buffer);
		// });
		// }
		// });
	}

	var createBuffer = function(mapItemType, mapItemPoints) {

		var geometry = new THREE.BufferGeometry();
		geometry.addAttribute('position', new THREE.Float32BufferAttribute(
				mapItemPoints, 3));

		var material = new THREE.PointsMaterial({
			size : 1,
			sizeAttenuation : true,
			map : getTextureForMapItemType(mapItemType),
			alphaTest : 0.5,
			transparent : false
		});
		material.color.setHSL(1.0, 0.3, 0.7);

		var points = new THREE.Points(geometry, material);

		return points;

	}

	var moveMapItemCursor = function(mapItemType, mapItemKey, mapItemPosition) {
		if (m_mapItemCursor.mapItemType !== mapItemType
				|| m_mapItemCursor.mapItemKey !== mapItemKey) {
			m_mapItemCursor.mapItemType = mapItemType;
			m_mapItemCursor.mapItemKey = mapItemKey;
			m_mapItemCursor.object.visible = true;
			m_mapItemCursor.object.position.copy(mapItemPosition);

			lookAtMapItem(mapItemPosition);

			raiseOnMapItemClick(mapItemType, mapItemKey);
		}
	}

	var hideMapItemCursor = function() {
		if (m_mapItemCursor.mapItemType !== null) {
			m_mapItemCursor.mapItemType = null;
			m_mapItemCursor.mapItemKey = null;
			m_mapItemCursor.object.visible = false;

			raiseOnMapItemClick(null, null);
		}
	}

	var animateMapItemCursor = function() {
		m_mapItemCursor.object.rotation.y += 0.015;
	}

	var moveMapItemHighlight = function(mapItemType, mapItemKey,
			mapItemPosition) {
		if (m_mapItemHighlight.mapItemType !== mapItemType
				|| m_mapItemHighlight.mapItemKey !== mapItemKey) {
			m_mapItemHighlight.mapItemType = mapItemType;
			m_mapItemHighlight.mapItemKey = mapItemKey;
			m_mapItemHighlight.object.visible = true;
			m_mapItemHighlight.object.position.copy(mapItemPosition);

			raiseOnMapItemHover(mapItemType, mapItemKey);
		}
	}

	var hideMapItemHighlight = function() {
		if (m_mapItemHighlight.mapItemType !== null) {
			m_mapItemHighlight.mapItemType = null;
			m_mapItemHighlight.mapItemKey = null;
			m_mapItemHighlight.object.visible = false;

			raiseOnMapItemHover(null, null);
		}
	}

	var animateMapItemHighlight = function() {
		m_mapItemHighlight.object.rotation.y += 0.015;
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

		SpaceViewerController.LookAtSector({
			x : xMidpoint,
			y : yMidpoint,
			z : zMidpoint
		});

		// if (m_cameraTween !== null) {
		// m_cameraTween.stop();
		// m_cameraTween = null;
		// }
		//
		// m_camera.up.set(0, 1, 0);
		// var tweenState = {
		// x : m_trackball.target.x,
		// y : m_trackball.target.y,
		// z : m_trackball.target.z
		// };
		// m_cameraTween = new TWEEN.Tween(tweenState).to({
		// x : xMidpoint,
		// y : yMidpoint,
		// z : zMidpoint
		// }, 1000).easing(TWEEN.Easing.Quadratic.Out).onUpdate(
		// function() {
		// m_camera.position.set(tweenState.x, tweenState.y + 150,
		// tweenState.z + 250);
		// m_trackball.target.set(tweenState.x, tweenState.y,
		// tweenState.z);
		// }).start();
	}

	var lookAtMapItem = function(mapItemPosition) {
		SpaceViewerController.LookAtMapItem({
			x : mapItemPosition.x,
			y : mapItemPosition.y,
			z : mapItemPosition.z
		});
		// if (m_cameraTween !== null) {
		// m_cameraTween.stop();
		// m_cameraTween = null;
		// }
		//
		// var tweenState = {
		// x : m_trackball.target.x,
		// y : m_trackball.target.y,
		// z : m_trackball.target.z
		// };
		// m_cameraTween = new TWEEN.Tween(tweenState).to({
		// x : mapItemPosition.x,
		// y : mapItemPosition.y,
		// z : mapItemPosition.z
		// }, 1000).easing(TWEEN.Easing.Quadratic.Out).onUpdate(function() {
		// m_trackball.target.set(tweenState.x, tweenState.y, tweenState.z);
		// }).start();
	}

	// **
	// ** WORK QUEUE
	// **

	var populateWorkQueue = function() {
		for (var rank = 0; rank < 10; ++rank) {
			m_workQueue.push({
				rank : rank
			});
		}
	}

	var processWorkQueue = function() {

		var entry = m_workQueue.pop();
		console.log(entry);
		if (entry === undefined) {
			return;
		}

		raiseGetMapItemsByRank(entry.rank, function(mapItemSets) {
			mapItemSets.forEach(function(mapItemSet) {

				var bufferName = createBufferName(mapItemSet.sectorKey,
						mapItemSet.mapItemType);

				if (!m_mapItemKeysByBufferName.has(bufferName)) {
					if (mapItemSet.mapItemKeys.length > 0) {

						m_mapItemKeysByBufferName.set(bufferName,
								mapItemSet.mapItemKeys);
						var buffer = createBuffer(mapItemSet.mapItemType,
								mapItemSet.mapItemPoints);
						buffer.name = bufferName;
						m_objectGroup.add(buffer);
					}
				}
			});

			processWorkQueue();
		});

		// var bufferName = createBufferName(entry.sectorKey,
		// entry.mapItemType);
		// if (!m_mapItemKeysByBufferName.has(bufferName)) {
		// m_mapItemKeysByBufferName.set(bufferName, "TEMP");
		//
		// raiseGetMapItemsBySector(entry.sectorKey, entry.mapItemType,
		// function(mapItemSets) {
		// mapItemSets.forEach(function(mapItemSet) {
		//
		// if (mapItemSet.mapItemKeys.length > 0) {
		// m_mapItemKeysByBufferName.set(bufferName,
		// mapItemSet.mapItemKeys);
		// var buffer = createBuffer(entry.mapItemType,
		// mapItemSet.mapItemPoints);
		// buffer.name = bufferName;
		// m_objectGroup.add(buffer);
		// }
		//
		// processWorkQueue();
		// });
		// });
		// }

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

	var getTextureForMapItemType = function(mapItemType) {
		switch (mapItemType) {
		case MAP_ITEM_TYPE_STAR:
			return m_starTexture;
		case MAP_ITEM_TYPE_SHIP:
			return m_shipTexture;
		default:
			throw "Unknown mapItemType" + mapItemType;
		}
	}

	var getMapItemNameFromType = function(mapItemType) {
		switch (mapItemType) {
		case MAP_ITEM_TYPE_STAR:
			return MAP_ITEM_NAME_STAR;
		case MAP_ITEM_TYPE_SHIP:
			return MAP_ITEM_NAME_SHIP;
		default:
			throw "Unknown mapItemType " + mapItemType;
		}
	}

	var getMapItemTypeFromName = function(mapItemName) {
		switch (mapItemName) {
		case MAP_ITEM_NAME_STAR:
			return MAP_ITEM_TYPE_STAR;
		case MAP_ITEM_NAME_SHIP:
			return MAP_ITEM_TYPE_SHIP;
		default:
			throw "Unknown mapItemName " + mapItemName;
		}
	}

	var createBufferName = function(sectorKey, mapItemType) {
		return sectorKey + "|" + getMapItemNameFromType(mapItemType);
	}

	var parseBufferName = function(bufferName) {
		var indexDelimiter = bufferName.indexOf("|");
		return {
			sectorKey : bufferName.substring(0, indexDelimiter),
			mapItemType : getMapItemTypeFromName(bufferName
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
		// handler = function(sectorKey, mapItemType, callback) where:
		//
		// sectorKey = String.
		//
		// mapItemType = Number where:
		// * 1 = Star
		// * 2 = Ship
		//
		// callback = function(mapItemKeys, mapItemPoints) where:
		//
		// mapItemSets = array of mapItemSet
		//
		// mapItemSet = object of:
		// * sectorKey = String
		// * mapItemType = Number
		// * mapItemKeys = array of String.
		// * mapItemPoints = array of Number. Specifies location of map items in
		// denormalized format (i.e. X1, Y1, Z1, X2, Y2, Z2, ...)
		//
		RegisterGetMapItemsBySectorHandler : function(handler) {
			registerGetMapItemsBySectorHandler(handler);
		},

		// RegisterGetObjectsByRankHandler - raised to retrieve objects within
		// the specified rank.
		//
		// handler = function(rank, callback) where:
		//
		// rank = Number.
		//
		// callback = function(mapItemKeys, mapItemPoints) where:
		//
		// mapItemSets = array of mapItemSet
		//
		// mapItemSet = object of:
		// * sectorKey = String
		// * mapItemType = Number
		// * mapItemKeys = array of String.
		// * mapItemPoints = array of Number. Specifies location of map items in
		// denormalized format (i.e. X1, Y1, Z1, X2, Y2, Z2, ...)
		//
		RegisterGetMapItemsByRankHandler : function(handler) {
			registerGetMapItemsByRankHandler(handler);
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
		// handler = function(mapItemType, mapItemKey) where:
		//
		// mapItemType = Number where:
		// * 1 = Star
		// * 2 = Ship
		//
		// mapItemKey = String.
		//
		RegisterOnMapItemClickHandler : function(handler) {
			registerOnMapItemClickHandler(handler);
		},

		// RegisterOnObjectHoverHandler - raised when the user hovers over an
		// object.
		//
		// handler = function(mapItemType, mapItemKey) where:
		//
		// mapItemType = Number where:
		// * 1 = Star
		// * 2 = Ship
		//
		// mapItemKey = String.
		//
		RegisterOnMapItemHoverHandler : function(handler) {
			registerOnMapItemHoverHandler(handler);
		}
	}

})();
