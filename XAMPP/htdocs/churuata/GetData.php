<?php
	$servername = "localhost";
	$username ="root";
	$password = "";
	$dbNamm = "churuata";

	$conn = new mysqli($servername,$username,$password,$dbNamm);

	if (!$conn) {
			die("connection failed". mysqli_connect_error());
	}
	else echo("Connection Succes");


?>