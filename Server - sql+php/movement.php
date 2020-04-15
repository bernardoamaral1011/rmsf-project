<?php session_start(); ?>
<?php
		
	$host = "db.ist.utl.pt";
	$user = "ist181216";
	$password = "xsxn7474";
	$dsn = "mysql:host=$host;dbname=$user";
		
	try{
		$connection = new PDO($dsn, $user, $password);
	}
	catch(PDOException $exception){
		echo("<p>Error: ");
		echo($exception->getMessage());
		echo("</p>");
		exit();
	}
	
	$json = array();

	// Receive the movement to be made and the raspberry
	$move = htmlentities($_GET['move'], ENT_QUOTES);
	$name = htmlentities($_GET['name'], ENT_QUOTES);

	$result = $connection->prepare("SELECT INET_NTOA(ipAddress) FROM PiAddress WHERE name =:name");		
	$result->bindParam(':name', $name);
	$result->execute();

	foreach($result as $row){
		$remote_ip = $row["INET_NTOA(ipAddress)"];
	}

	if(!($sock = socket_create(AF_INET, SOCK_DGRAM, 0)))
	{
   		$errorcode = socket_last_error();
 		$errormsg = socket_strerror($errorcode);
    	die("Couldn't create socket: [$errorcode] $errormsg \n");
	}
 
	$remote_port="3001";

	//Send back the data to the client
	socket_sendto($sock, $move, strlen($move), 0, $remote_ip , $remote_port);
	
	$json['success'] = 1;
	
	echo json_encode($json);
	
	socket_close($sock);
	$connection = null;
?>
