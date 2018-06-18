"use strict";

/*-
 * SpaceViewer
 * 
 * Manages a HTML5 WebGL canvas to visualize stars, space ships and other items in the SFMS database.
 * 
 * MAP ITEMS
 * =========
 * 
 * The data shown is obtained by the REST methods exposed by the AjaxController.  This controller abstracts
 * all viewable entities as map items.  A map item has the following properties:
 * 
 *  * sectorKey - Identifies the sector containing the map item.
 *  
 *  * mapItemKey - A unique key that allows details for the map item to be retrieved.
 *  
 *  * mapItemType - Identifies the type of map item.  Values include:
 *            
 *                  0 = Star
 *                  1 = Spaceship
 *                  
 *  * mapItemName - The name of the map item.
 *  
 *  * propertyGroups - A list of property groups that contain detail information for the map item.
 *   
 * For efficiency, map items are displayed as gl.POINTS WebGL objects.  There is a single gl.POINTS 
 * object for every sectorKey/mapItemType combination.  
 * 
 * Each gl.POINTS object has an associated bitmap image texture that corresponds to the mapItemType 
 * (e.g. sfms_star.png.)  These images are stored in the "textures" folder.
 * 
 * We divide map items in different sectors into separate gl.POINTS objects so that objects in 
 * the active sector can be highlighted.  gl.POINTS are assigned the appropriate texture 
 * (e.g. sfms_star.png vs. sfms_star_inactive.png) when a the sector cursor is moved.
 * 
 * CONTROLS
 * ========
 * 
 * In addition to the map items, the viewer manages several controls:
 * 
 * Sector Cursor - A box that highlights the current sector.  Only map item sin the current sector
 *                 can be highlighted or selected.
 *                 
 * Map Item Cursor - Identifies the map item selected (i.e. clicked) by the user.
 * 
 * Map Item Highlight - Highlights the map item currently under the cursor.  When a map item is 
 *                      highlighted, clicking it moves the cursor to the highlighted item.
 *                      
 * REST EVENTS
 * ===========
 * 
 * The SpaceViewer does not directly issues Ajax requests.  Instead, it raises events (e.g. GetSectors)
 * when data is required.  The appropriate event handlers must be registered to retrieve the
 * requested information.  See the SpaceViewerRestHandler module for additional details.
 * 
 */
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
	var m_starTexture;
	var m_starInactiveTexture;
	var m_shipTexture;
	var m_shipInactiveTexture;
	var m_mapItemHighlightTexture;
	var m_mapItemCursorTexture;

	// Scene objects
	//
	var m_sectorCursor = {
		object : null,
		xyShadow : null,
		xzShadow : null,
		yzShadow : null,
		sectorKey : null,
		buffers : null,
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
	var m_allMapItemObjects;
	var m_selectedMapItemObjects;

	// UI state objects
	//
	var m_workQueue = [];
	var m_mapItemKeysByName = new Map();
	var m_mouse = new THREE.Vector2();
	var m_cursorRotation = 0;

	// Module events
	//
	var m_getSectorsHandler = null;
	var m_getSectorByLocationHandler = null;
	var m_getSectorByKeyHandler = null;
	var m_getMapItemsBySectorHandler = null;
	var m_getMapItemsByRankHandler = null;
	var m_loadingProgressHandler = null;
	var m_loadingCompleteHandler = null;
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
		initCreateTextures(); // m_starTexture, m_shipTexture
		initAddDefaultObjectsToScene();
		// initCreateStats(); // m_stats
		initCreateRenderer(); // m_renderer
		initCreateCamera(); // m_camera
		initCreateContainer(containerId); // m_container
		initCreateRaycaster(); // m_raycaster

		CameraController.Initialize(m_canvas[0], m_camera);

		// Register event handlers.
		//
		$(window).on('keydown', onWindowKeyDown);
		CameraController.RegisterOnHoverHandler(onHover);
		CameraController.RegisterOnClickHandler(onClick);

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

		m_selectedMapItemObjects = []
		m_allMapItemObjects = new THREE.Group();
		m_scene.add(m_allMapItemObjects);

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
			var geometry = new THREE.BufferGeometry();
			geometry.addAttribute('position', new THREE.Float32BufferAttribute(
					[ 0, 0, 0 ], 3));

			var material = new THREE.PointsMaterial({
				size : 1.5,
				sizeAttenuation : true,
				map : m_mapItemCursorTexture,
				alphaTest : 0.5,
				transparent : false
			});

			m_mapItemCursor.object = new THREE.Points(geometry, material);
			m_mapItemCursor.object.visible = false;

			m_scene.add(m_mapItemCursor.object);
		}

		// Map item highlight
		{
			var geometry = new THREE.BufferGeometry();
			geometry.addAttribute('position', new THREE.Float32BufferAttribute(
					[ 0, 0, 0 ], 3));

			var material = new THREE.PointsMaterial({
				size : 1.5,
				sizeAttenuation : true,
				map : m_mapItemHighlightTexture,
				alphaTest : 0.5,
				transparent : false
			});

			m_mapItemHighlight.object = new THREE.Points(geometry, material);
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
		// m_container.append(m_stats.dom);
	}

	var initCreateRaycaster = function() {
		m_raycaster = new THREE.Raycaster();
		m_raycaster.params.Points.threshold = RAYCASTER_THRESHOLD;
	}

	var initCreateTextures = function() {
		var loader = new THREE.TextureLoader();
		m_starTexture = loader.load("textures/sfms_star.png");
		m_starInactiveTexture = loader.load("textures/sfms_star_inactive.png");
		m_shipTexture = loader.load("textures/sfms_ship.png");
		m_shipInactiveTexture = loader.load("textures/sfms_ship_inactive.png");
		m_mapItemHighlightTexture = loader.load("textures/focus_cursor2.png");
		m_mapItemCursorTexture = loader.load("textures/active_cursor2.png");

		m_mapItemHighlightTexture.center.set(0.5, 0.5);
		m_mapItemCursorTexture.center.set(0.5, 0.5);
	}

	var onWindowResize = function(e) {
		m_renderer.setSize(m_canvas.width(), m_canvas.height(), false);
		CameraController.OnWindowResize();
		// render();
	};

	// **
	// ** EVENT HANDLERS
	// **

	var onHover = function(position) {

		// Determine normalized mouse position used for ray-casting.
		//
		var containerPosition = m_canvas.offset();
		var containerX = position.x - containerPosition.left;
		var containerY = position.y - containerPosition.top;
		m_mouse.x = (containerX / m_canvas.width()) * 2 - 1;
		m_mouse.y = -(containerY / m_canvas.height()) * 2 + 1;

		// Determine objects that intersect mouse position.
		//
		m_raycaster.setFromCamera(m_mouse, m_camera);
		var intersections = m_raycaster.intersectObjects(
				m_selectedMapItemObjects, true);

		if (intersections.length > 0) {
			var intersection = intersections[0];
			var index = intersection.index;
			var mapItemObject = intersection.object;
			var parsedBufferName = parseMapItemObjectName(mapItemObject.name);
			var mapItemKeys = m_mapItemKeysByName.get(mapItemObject.name);
			var mapItemKey = mapItemKeys[index];

			if (m_mapItemCursor.mapItemType !== parsedBufferName.mapItemType
					|| m_mapItemCursor.mapItemKey !== mapItemKey) {

				var x = mapItemObject.geometry.attributes.position.array[index * 3];
				var y = mapItemObject.geometry.attributes.position.array[index * 3 + 1];
				var z = mapItemObject.geometry.attributes.position.array[index * 3 + 2];
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

	var onClick = function(e) {
		if (m_mapItemHighlight.mapItemType === null) {
			hideMapItemCursor();
		} else {
			moveMapItemCursor(m_mapItemHighlight.mapItemType,
					m_mapItemHighlight.mapItemKey,
					m_mapItemHighlight.object.position);
			hideMapItemHighlight();
		}
	}

	var onWindowKeyDown = function(e) {
		switch (e.which) {
		case 37: // LEFT
			e.preventDefault();
			moveSectorCursor(m_sectorCursor.coordinates.sx - 1,
					m_sectorCursor.coordinates.sy,
					m_sectorCursor.coordinates.sz);
			break;
		case 39: // RIGHT
			e.preventDefault();
			moveSectorCursor(m_sectorCursor.coordinates.sx + 1,
					m_sectorCursor.coordinates.sy,
					m_sectorCursor.coordinates.sz);
			break;
		case 38: // UP
			e.preventDefault();
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
			e.preventDefault();
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
			populateWorkQueue();
			processWorkQueue();
		});
	}

	var animate = function() {
		requestAnimationFrame(animate);
		TWEEN.update(); // required by CameraController
		m_cursorRotation += 0.015;
		animateMapItemHighlight();
		animateMapItemCursor();
		CameraController.Animate();
		render();
	};

	var render = function() {
		m_renderer.render(m_scene, m_camera);
		$("#ctrlDebug").html(
				"Position = " + m_camera.position.x + "," + m_camera.position.y
						+ "," + m_camera.position.z + "<br>FOV = "
						+ m_camera.fov);
		// m_stats.update();
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

	var registerLoadingProgressHandler = function(handler) {
		m_loadingProgressHandler = handler;
	}

	var raiseLoadingProgress = function(percent) {
		if (m_loadingProgressHandler !== null) {
			m_loadingProgressHandler(percent);
		}
	}

	var registerLoadingCompleteHandler = function(handler) {
		m_loadingCompleteHandler = handler;
	}

	var raiseLoadingComplete = function() {
		if (m_loadingCompleteHandler !== null) {
			m_loadingCompleteHandler();
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

	var setMapItemObjectActive = function(mapItemObject, active) {

		console.log(mapItemObject.name + " " + active);

		var parsedBufferName = parseMapItemObjectName(mapItemObject.name);
		mapItemObject.material.map = getTextureForMapItemType(
				parsedBufferName.mapItemType, active);
	}

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

		// Deactivate currently selected objects.
		//
		m_selectedMapItemObjects.forEach(function(mapItemObject) {
			setMapItemObjectActive(mapItemObject, false);
		});
		while (m_selectedMapItemObjects.length > 0) {
			m_selectedMapItemObjects.pop();
		}

		// Find and select objects in current sector.
		//
		m_allMapItemObjects.children.forEach(function(mapItemObject) {
			if (mapItemObject.name.startsWith(sector.key)) {
				setMapItemObjectActive(mapItemObject, true);
				m_selectedMapItemObjects.push(mapItemObject);
			}
		});

		m_sectorCursor.coordinates.sx = sx;
		m_sectorCursor.coordinates.sy = sy;
		m_sectorCursor.coordinates.sz = sz;
		m_sectorCursor.object.box.min.set(sector.minimumX, sector.minimumY,
				sector.minimumZ);
		m_sectorCursor.object.box.max.set(sector.maximumX, sector.maximumY,
				sector.maximumZ);

		lookAtSector(sector);

		raiseOnSectorClick(sector.key);
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
		m_mapItemCursorTexture.rotation += 0.015;
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
		m_mapItemHighlightTexture.rotation += 0.015;
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

		CameraController.PanTo({
			x : xMidpoint,
			y : yMidpoint,
			z : zMidpoint
		});
	}

	var lookAtMapItem = function(mapItemPosition) {
		CameraController.PivotTo({
			x : mapItemPosition.x,
			y : mapItemPosition.y,
			z : mapItemPosition.z
		});
	}

	// **
	// ** WORK QUEUE
	// **

	var populateWorkQueue = function() {
		for (var rank = 0; rank < 10; ++rank) {
			m_workQueue.push({
				percent : rank * 10,
				rank : rank
			});
		}
	}

	var processWorkQueue = function() {

		var entry = m_workQueue.pop();
		console.log(entry);
		if (entry === undefined) {
			moveSectorCursor(5, 5, 5);
			raiseLoadingComplete();
			return;
		}

		raiseLoadingProgress(entry.percent);

		raiseGetMapItemsByRank(entry.rank, function(mapItemSets) {
			mapItemSets.forEach(function(mapItemSet) {

				var mapItemObjectName = createMapItemObjectName(
						mapItemSet.sectorKey, mapItemSet.mapItemType);

				if (!m_mapItemKeysByName.has(mapItemObjectName)) {
					if (mapItemSet.mapItemKeys.length > 0) {

						m_mapItemKeysByName.set(mapItemObjectName,
								mapItemSet.mapItemKeys);
						var mapItemObject = createMapItemObject(
								mapItemSet.mapItemType,
								mapItemSet.mapItemPoints);
						mapItemObject.name = mapItemObjectName;
						m_allMapItemObjects.add(mapItemObject);
					}
				}
			});

			processWorkQueue();
		});
	}

	// **
	// ** UTILITY
	// **

	var createMapItemObject = function(mapItemType, mapItemPoints) {

		var geometry = new THREE.BufferGeometry();
		geometry.addAttribute('position', new THREE.Float32BufferAttribute(
				mapItemPoints, 3));

		var material = new THREE.PointsMaterial({
			size : 0.4,
			sizeAttenuation : true,
			map : getTextureForMapItemType(mapItemType, false),
			alphaTest : 0.5,
			transparent : false
		});
		material.color.setHSL(1.0, 0.3, 0.7);

		var points = new THREE.Points(geometry, material);

		return points;
	}

	var clamp = function(value, minimum, maximum) {
		if (value < minimum)
			return minimum;
		if (value > maximum)
			return maximum;
		return value;
	}

	var getTextureForMapItemType = function(mapItemType, active) {
		if (active) {
			switch (mapItemType) {
			case MAP_ITEM_TYPE_STAR:
				return m_starTexture;
			case MAP_ITEM_TYPE_SHIP:
				return m_shipTexture;
			default:
				throw "Unknown mapItemType" + mapItemType;
			}
		} else {
			switch (mapItemType) {
			case MAP_ITEM_TYPE_STAR:
				return m_starInactiveTexture;
			case MAP_ITEM_TYPE_SHIP:
				return m_shipInactiveTexture;
			default:
				throw "Unknown mapItemType" + mapItemType;
			}
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

	var createMapItemObjectName = function(sectorKey, mapItemType) {
		return sectorKey + "|" + getMapItemNameFromType(mapItemType);
	}

	var parseMapItemObjectName = function(mapItemObjectName) {
		var indexDelimiter = mapItemObjectName.indexOf("|");
		return {
			sectorKey : mapItemObjectName.substring(0, indexDelimiter),
			mapItemType : getMapItemTypeFromName(mapItemObjectName
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

		OnWindowResize : function() {
			onWindowResize();
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

		// RegisterLoadingProgressHandler - raised as map item data is being
		// loaded.
		//
		// handler = function(percent)
		//
		RegisterLoadingProgressHandler : function(handler) {
			registerLoadingProgressHandler(handler);
		},

		// RegisterLoadingCompleteHandler - raised when all map item data has
		// been loaded.
		//
		// handler = function()
		//
		RegisterLoadingCompleteHandler : function(handler) {
			registerLoadingCompleteHandler(handler);
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
