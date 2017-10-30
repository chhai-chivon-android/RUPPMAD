<?php

//sleep(2);
$con = mysqli_connect('localhost', 'leapkh', '123', 'mobile_app_db');

$query ="select * from tblArticle";
$resultSet = $con->query($query);
$articles = array();
while($row = $resultSet->fetch_assoc()){
	$article = new stdClass();
	$article->_id = intval($row['_id']);
	$article->_title = $row['_title'];
	$article->_content = $row['_content'];
	$article->_image = $row['_image'];
	$article->_author = $row['_author'];
	$article->_publish_date = $row['_publish_date'];
	$article->_view_count = intval($row['_view_count']);
	array_push($articles, $article);
}
$data = json_encode($articles);
echo $data;

$con->close();

?>