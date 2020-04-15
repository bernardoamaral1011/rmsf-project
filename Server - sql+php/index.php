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
	$name = htmlentities($_GET['name'], ENT_QUOTES);
   
	$result = $connection->prepare("SELECT name FROM PiAddress WHERE name =:name");		
	$result->bindParam(':name', $name);
	$result->execute();

    if( ($result->rowcount())!= 0 ){
        $json['success'] = 1; 
    }else{
        $json['success'] = 0; 
    }
	
	echo json_encode($json);
	$connection = null;
?>
