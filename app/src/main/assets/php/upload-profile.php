<?php

// Get base64 from post request
$imageString = $_POST['_image_profile'];

// Decode base64 to image
$imageData = base64_decode($imageString);

// Save image to file
file_put_contents('./profile.jpg', $imageData);

echo "Upload success";
