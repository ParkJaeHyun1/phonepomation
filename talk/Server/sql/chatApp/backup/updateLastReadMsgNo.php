<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);

include('dbConnect.php');


//POST 값을 읽어온다.
$userID =  $_POST['userID'];
$lastReadMsgNo = $_POST['lastReadMsgNo'];
$chattingRoomNo = (int)$_POST['chattingRoomNo'];

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");
$stmt = $con->prepare("update chattingRoomMember set lastReadMsgNo = '$lastReadMsgNo', joinDate=joinDate where userID = '$userID' and chattingRoomNo = '$chattingRoomNo'");
if($stmt->execute())
    echo 'TRUE';
else
    echo 'FALSE';

?>
