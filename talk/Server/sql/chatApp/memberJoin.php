<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);
include('sqlFunction.php');
include('dbConnect.php');


//POST 값을 읽어온다.
$id =  $_POST['id'];
$password = $_POST['password'];
$name =  $_POST['name'];
$phone = $_POST['phone'];

echo memberJoin($id,$password,$name,$phone,$con);

?>
