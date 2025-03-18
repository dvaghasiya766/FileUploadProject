<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>File Upload (Servlet & WebSocket)</title>
<script>
    var socket;
    function connectWebSocket() {
        socket = new WebSocket("ws://localhost:8082/FileUploadProject/FileUploadWebSocket");

        socket.onopen = function () {
            console.log("WebSocket connection established");
        };

        socket.onmessage = function (event) {
            document.getElementById("status").innerHTML = event.data;
        };

        socket.onerror = function (event) {
            console.log("WebSocket error:", event);
        };

        socket.onclose = function () {
            console.log("WebSocket connection closed");
        };
    }

    function uploadFile() {
        var fileInput = document.getElementById("fileinput").files[0];
        if (!fileInput) {
            alert("Please select a file.");
            return;
        }

        var reader = new FileReader();
        reader.onload = function (event) {
            socket.send(fileInput.name); // Send file name
            socket.send(event.target.result); // Send file data
        };

        reader.readAsArrayBuffer(fileInput);
    }

    window.onload = connectWebSocket;
</script>
</head>
<body>
    <h2>File Upload (Servlet & WebSocket)</h2>
    <form action="FileUploadServlet" method="post" enctype="multipart/form-data">
        <input type="file" id="fileinput" name="file">
        <button type="button" onclick="uploadFile()">Upload via WebSocket</button>
        <button type="submit">Upload via Servlet</button>
    </form>
    <p id="status"></p>
</body>
</html>