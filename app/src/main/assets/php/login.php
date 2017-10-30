<?php

//sleep(1);

$username = $_POST['username'];
$password = $_POST['password'];

$con = mysqli_connect('localhost', 'leapkh', '123', 'mobile_app_db');
$query ="select * from tblUser where _username='$username' and _password='$password'";
$resultSet = $con->query($query);
if($row = $resultSet->fetch_assoc()){
	$user = new stdClass();
	$user->_id = intval($row['_id']);
	$user->_name = $row['_name'];
	$user->_username = $row['_username'];
	$user->_profile_picture = $row['_profile_picture'];
	$user->_token = md5($username);
	$json = json_encode($user);
	$response = new stdClass();
	$response->_code = 0;
	$response->_message = 'Login success';
	$response->_data = $user;
	//echo json_encode($response);
	echo json_encode($user);
}else{
	$error = new stdClass();
	$error->_code = -1;
	$error->_message = 'Incorrect username or password';
	$json = json_encode($error);
	echo $json;
}

$con->close();

?>