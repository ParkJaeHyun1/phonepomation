<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);
include('sqlFunction.php');
include('dbConnect.php');


//POST 값을 읽어온다.
$userID =  $_POST['userID'];
$friendID = $_POST['friendID'];
$chattingRoomNo = (int)$_POST['chattingRoomNo'];

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

return updateFriendChattingroomNo($chattingRoomNo,$userID,$friendID,$con);

?>
