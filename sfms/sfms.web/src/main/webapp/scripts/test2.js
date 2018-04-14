"use strict";

if (!Detector.webgl) {
	Detector.addGetWebGLMessage();
}

var g_parameters = createParameters();
var g_materials = createMaterials;
var g_scene = createScene();

var g_camera;
var g_controls;
var g_renderer;
var g_stats;
init();

var i, h;
var mouseX = 0, mouseY = 0;
var windowHalfX = window.innerWidth / 2;
var windowHalfY = window.innerHeight / 2;

animate();

function createParameters() {

	var parameters = [ //
	[ [ 1.00, 1, 0.5 ], 5 ], // Five
	[ [ 0.95, 1, 0.5 ], 4 ], // Four
	[ [ 0.90, 1, 0.5 ], 3 ], // Three
	[ [ 0.85, 1, 0.5 ], 2 ], // Two
	[ [ 0.80, 1, 0.5 ], 1 ] ]; // One

	return parameters;
}

function createMaterials() {

	var materials = []

	for (i = 0; i < g_parameters.length; i++) {
		var size = g_parameters[i][1];

		materials[i] = new THREE.PointsMaterial({
			size : size
		});
	}

	return materials;
}

function createScene() {

	var scene = new THREE.Scene();
	scene.fog = new THREE.FogExp2(0x000000, 0.0007);

	var geometry = new THREE.Geometry();
	for (i = 0; i < 200; i++) {
		var vertex = new THREE.Vector3();
		vertex.x = Math.random() * 2000 - 1000;
		vertex.y = Math.random() * 2000 - 1000;
		vertex.z = Math.random() * 2000 - 1000;
		geometry.vertices.push(vertex);
	}

	for (i = 0; i < g_parameters.length; i++) {
		var particles = new THREE.Points(geometry, g_materials[i]);
		particles.rotation.x = Math.random() * 6;
		particles.rotation.y = Math.random() * 6;
		particles.rotation.z = Math.random() * 6;

		scene.add(particles);
	}

	return scene;
}

function init() {
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
}

function onWindowResize() {
	windowHalfX = window.innerWidth / 2;
	windowHalfY = window.innerHeight / 2;
	g_camera.aspect = window.innerWidth / window.innerHeight;
	g_camera.updateProjectionMatrix();
	g_renderer.setSize(window.innerWidth, window.innerHeight);
	g_controls.handleResize();
}

function onDocumentMouseMove(event) {
	mouseX = event.clientX - windowHalfX;
	mouseY = event.clientY - windowHalfY;
	render();
}

function onDocumentTouchStart(event) {
	if (event.touches.length === 1) {
		event.preventDefault();
		mouseX = event.touches[0].pageX - windowHalfX;
		mouseY = event.touches[0].pageY - windowHalfY;
	}
}
function onDocumentTouchMove(event) {
	if (event.touches.length === 1) {
		event.preventDefault();
		mouseX = event.touches[0].pageX - windowHalfX;
		mouseY = event.touches[0].pageY - windowHalfY;
	}
}

function animate() {
	requestAnimationFrame(animate);
	g_stats.update();
	g_controls.update();
}

function render() {
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
