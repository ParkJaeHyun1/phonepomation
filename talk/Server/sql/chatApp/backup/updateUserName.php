<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);

include('dbConnect.php');


//POST 값을 읽어온다.
$userID =  $_POST['userID'];
$name = $_POST['name'];

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
$stmt = $con->prepare("update user set name = '$name' where id = '$userID'");
if($stmt->execute())
    echo 'TRUE';
else
    echo 'FALSE';

?>
