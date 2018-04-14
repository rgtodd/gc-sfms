var SpaceViewer = (function() {

	// *********************************
	// **
	// ** PRIVATE
	// **
	// *********************************

	var g_SpaceViewerHandler;
	var g_parameters;
	var g_materials;
	var g_scene;
	var g_camera;
	var g_controls;
	var g_renderer;
	var g_stats;
	var i;
	var h;
	var mouseX;
	var mouseY;
	var windowHalfX;
	var windowHalfY;

	var registerHandler = function(handler) {

		g_SpaceViewerHandler = handler;

	};

	var construct = function() {
		g_parameters = createParameters();
		g_materials = createMaterials;
		g_scene = createScene();
		init();
		animate();
	};

	var createParameters = function() {

		var parameters = [ //
		[ [ 1.00, 1, 0.5 ], 5 ], // Five
		[ [ 0.95, 1, 0.5 ], 4 ], // Four
		[ [ 0.90, 1, 0.5 ], 3 ], // Three
		[ [ 0.85, 1, 0.5 ], 2 ], // Two
		[ [ 0.80, 1, 0.5 ], 1 ] ]; // One

		return parameters;
	};

	var createMaterials = function() {

		var materials = []

		for (i = 0; i < g_parameters.length; i++) {
			var size = g_parameters[i][1];

			materials[i] = new THREE.PointsMaterial({
				size : size
			});
		}

		return materials;
	};

	var addPoints = function(scene) {

		var sprite = new THREE.TextureLoader()
				.load("textures/sfms_star_texture_25.png");

		var geometryPath = new THREE.Geometry();

		// var positions = [];
		// // var colors = [];
		// for (var x = 0; x < 30; ++x) {
		// for (var y = 0; y < 30; ++y) {
		// for (var z = 0; z < 30; ++z) {
		// var starX = x * 25;
		// var starY = y * 25;
		// var starZ = z * 25;
		// positions.push(starX, starY, starZ);
		//
		// if (Math.random() < 0.01) {
		// geometryPath.vertices.push(new THREE.Vector3(starX,
		// starY, starZ));
		// }
		// // colors.push(0, 1, 0);
		// }
		// }
		// }

		var positions;
		g_SpaceViewerHandler.GetPointsAsync(function(points) {
			positions = points
		});

		var geometry = new THREE.BufferGeometry();
		geometry.addAttribute('position', new THREE.Float32BufferAttribute(
				positions, 3));
		// geometry.addAttribute('color', new
		// THREE.Float32BufferAttribute(colors,
		// 3));
		geometry.computeBoundingSphere();

		// var material = new THREE.PointsMaterial({
		// size : 1,
		// vertexColors : THREE.VertexColors
		// });
		var material = new THREE.PointsMaterial({
			size : 1,
			sizeAttenuation : true,
			map : sprite,
			alphaTest : 0.5,
			transparent : false
		});
		material.color.setHSL(1.0, 0.3, 0.7);

		var points = new THREE.Points(geometry, material);
		scene.add(points);

		scene.add(new THREE.Line(geometryPath));

		var box = new THREE.BoxHelper(points, 0xffff00);
		scene.add(box);

	};

	var createScene = function() {

		var scene = new THREE.Scene();

		var geometry = new THREE.Geometry();
		geometry.vertices.push( //
		new THREE.Vector3(0, 0, 0), //
		new THREE.Vector3(100, 0, 0), //
		new THREE.Vector3(0, 100, 0), //
		new THREE.Vector3(0, 0, 100));

		var particles = new THREE.Points(geometry);
		scene.add(particles);

		var gridHelper = new THREE.GridHelper(100, 10);
		scene.add(gridHelper);

		var dirX = new THREE.Vector3(1, 0, 0);
		var dirY = new THREE.Vector3(0, 1, 0);
		var dirZ = new THREE.Vector3(0, 0, 1);
		var origin = new THREE.Vector3(0, 0, 0);
		var length = 90;
		scene.add(new THREE.ArrowHelper(dirX, origin, length, 0xff0000));
		scene.add(new THREE.ArrowHelper(dirY, origin, length, 0x00ff00));
		scene.add(new THREE.ArrowHelper(dirZ, origin, length, 0x0000ff));

		addPoints(scene);

		return scene;
	};

	var init = function() {
		g_camera = new THREE.PerspectiveCamera(75, window.innerWidth
				/ window.innerHeight, 1, 3000);
		g_camera.position.z = 1000;

		g_controls = new THREE.TrackballControls(g_camera);
		g_controls.rotateSpeed = 1.0;
		g_controls.zoomSpeed = 1.2;
		g_controls.panSpeed = 0.8;
		g_controls.noZoom = false;
		g_controls.noPan = false;
		g_controls.staticMoving = true;
		g_controls.dynamicDampingFactor = 0.3;
		g_controls.keys = [ 65, 83, 68 ];
		g_controls.addEventListener('change', render);

		g_renderer = new THREE.WebGLRenderer();
		g_renderer.setPixelRatio(window.devicePixelRatio);
		g_renderer.setSize(window.innerWidth, window.innerHeight);

		g_stats = new Stats();

		var container = document.getElementById('container');
		container.appendChild(g_renderer.domElement);
		container.appendChild(g_stats.dom);

		// document.addEventListener('mousemove', onDocumentMouseMove, false);
		// document.addEventListener('touchstart', onDocumentTouchStart, false);
		// document.addEventListener('touchmove', onDocumentTouchMove, false);
		window.addEventListener('resize', onWindowResize, false);
	};

	var onWindowResize = function() {
		windowHalfX = window.innerWidth / 2;
		windowHalfY = window.innerHeight / 2;
		g_camera.aspect = window.innerWidth / window.innerHeight;
		g_camera.updateProjectionMatrix();
		g_renderer.setSize(window.innerWidth, window.innerHeight);
		g_controls.handleResize();
	};

	var onDocumentMouseMove = function(event) {
		mouseX = event.clientX - windowHalfX;
		mouseY = event.clientY - windowHalfY;
		render();
	};

	var onDocumentTouchStart = function(event) {
		if (event.touches.length === 1) {
			event.preventDefault();
			mouseX = event.touches[0].pageX - windowHalfX;
			mouseY = event.touches[0].pageY - windowHalfY;
		}
	};
	var onDocumentTouchMove = function(event) {
		if (event.touches.length === 1) {
			event.preventDefault();
			mouseX = event.touches[0].pageX - windowHalfX;
			mouseY = event.touches[0].pageY - windowHalfY;
		}
	};

	var animate = function() {
		requestAnimationFrame(animate);
		g_stats.update();
		g_controls.update();
	};

	var render = function() {
		// var time = Date.now() * 0.00005;

		// g_camera.position.x += (mouseX - g_camera.position.x) * 0.05;
		// g_camera.position.y += (-mouseY - g_camera.position.y) * 0.05;
		// g_camera.lookAt(g_scene.position);

		// for (i = 0; i < g_scene.children.length; i++) {
		// var object = g_scene.children[i];
		// if (object instanceof THREE.Points) {
		// object.rotation.y = time * (i < 4 ? i + 1 : -(i + 1));
		// }
		// }
		//
		// for (i = 0; i < g_materials.length; i++) {
		// var color = g_parameters[i][0];
		// h = (360 * (color[0] + time) % 360) / 360;
		// g_materials[i].color.setHSL(h, color[1], color[2]);
		// }

		g_renderer.render(g_scene, g_camera);
	}

	// *********************************
	// **
	// ** PUBLIC
	// **
	// *********************************

	return {

		Construct : function() {
			construct();
		},

		RegisterHandler : function(handler) {
			registerHandler(handler);
		}

	}

})();
