<?php 
	
	//Constants for database connection

	define('DB_HOST','localhost');
	define('DB_USER','root');
	define('DB_PASS','');
	define('DB_NAME','sewubuku');

	//We will upload files to this folder
	//So one thing don't forget, also create a folder named uploads inside your project folder i.e. MyApi folder
	define('READER_UPLOAD_PATH', 'readers/');
	define('COVER_UPLOAD_PATH', 'covers/');
	
	//connecting to database 
	$conn = new mysqli(DB_HOST,DB_USER,DB_PASS,DB_NAME) or die('Unable to connect');
	

	//An array to display the response
	$response = array();

	// works!
	function reg_number($s, $conn){
		$si = $s.rand(101,10101);
		$sql = "SELECT reg_number FROM readers WHERE reg_number=?";
		$stmt = $conn->prepare($sql);
		$stmt->bind_param("s", $si);
		$stmt->execute();
		$result = $stmt->bind_result($reg_number);

		while($stmt->fetch()){
			while(md5($si)==$reg_number){
				$si = reg_number($s);
			}
		}
		return md5($si);
	}

	// likely works!
	function save_base64_image($base64_image_string, $output_file_without_extension, $path_with_end_slash="" ) {
	    $splited = explode(',', substr( $base64_image_string , 5 ) , 2);
	    $mime=$splited[0];
	    $data=$splited[1];

	    $mime_split_without_base64=explode(';', $mime,2);
	    $mime_split=explode('/', $mime_split_without_base64[0],2);
	    if(count($mime_split)==2)
	    {
	        $extension=$mime_split[1];
	        if($extension=='jpeg')$extension='jpg';
	        $output_file_with_extension=$output_file_without_extension.'.'.$extension;
	    }
	    file_put_contents( $path_with_end_slash . $output_file_with_extension, base64_decode($data) );
	    return $output_file_with_extension;
	}

	function __json_encode( $data ) {            
    if( is_array($data) || is_object($data) ) { 
        $islist = is_array($data) && ( empty($data) || array_keys($data) === range(0,count($data)-1) ); 
        
        if( $islist ) {
        	//var_dump($data); 
            $json = '[' . implode(',', array_map('__json_encode', $data) ) . ']'; 
        } else { 
            $items = Array(); 
            foreach( $data as $key => $value ) { 
                $items[] = __json_encode("$key") . ':' . __json_encode($value); 
            } 
            $json = '{' . implode(',', $items) . '}'; 
        } 
    } elseif( is_string($data) ) { 
        # Escape non-printable or Non-ASCII characters. 
        # I also put the \\ character first, as suggested in comments on the 'addclashes' page. 
        $string = '"' . addcslashes($data, "\\\"\n\r\t/" . chr(8) . chr(12)) . '"'; 
        $json    = ''; 
        $len    = strlen($string); 
        # Convert UTF-8 to Hexadecimal Codepoints. 
        for( $i = 0; $i < $len; $i++ ) { 
            
            $char = $string[$i]; 
            $c1 = ord($char); 
            
            # Single byte; 
            if( $c1 <128 ) { 
                $json .= ($c1 > 31) ? $char : sprintf("\\u%04x", $c1); 
                continue; 
            } 
            
            # Double byte 
            $c2 = ord($string[++$i]); 
            if ( ($c1 & 32) === 0 ) { 
                $json .= sprintf("\\u%04x", ($c1 - 192) * 64 + $c2 - 128); 
                continue; 
            } 
            
            # Triple 
            $c3 = ord($string[++$i]); 
            if( ($c1 & 16) === 0 ) { 
                $json .= sprintf("\\u%04x", (($c1 - 224) <<12) + (($c2 - 128) << 6) + ($c3 - 128)); 
                continue; 
            } 
                
            # Quadruple 
            $c4 = ord($string[++$i]); 
            if( ($c1 & 8 ) === 0 ) { 
                $u = (($c1 & 15) << 2) + (($c2>>4) & 3) - 1; 
            
                $w1 = (54<<10) + ($u<<6) + (($c2 & 15) << 2) + (($c3>>4) & 3); 
                $w2 = (55<<10) + (($c3 & 15)<<6) + ($c4-128); 
                $json .= sprintf("\\u%04x\\u%04x", $w1, $w2); 
            } 
        } 
    } else { 
        # int, floats, bools, null 
        $json = strtolower(var_export( $data, true )); 
    } 
    return $json; 
} 

	// READER
	if(isset($_GET['apireader'])){
		//switching the api call 
		switch($_GET['apireader']){
			
			// done
			case 'postreader':
				
				//first confirming that we have the image and tags in the request parameter
				if(isset($_POST['name'])){
					$name = $_POST['name'];
					$reg_number = reg_number($name, $conn);
					$password = $_POST['password'];
					$email = $_POST['email'];
					$location = $_POST['location'];
					$phone = $_POST['phone'];
					$photo = "";
					$date = date("Y-m-d H:i:s");
					$stmt = $conn->prepare("INSERT INTO readers (reg_number, name, email, password, location, phone, photo, created_at, updated_at) 
						VALUES (?,?,?,?,?,?,?,?,?)");
					if(isset($_FILES['photo']['name'])){
						//uploading file and storing it to database as well 
						try{
							$photo = $reg_number."".$_FILES['photo']['name'];
							move_uploaded_file($_FILES['photo']['tmp_name'], READER_UPLOAD_PATH . $photo);
							
							$stmt->bind_param("sssssssss", $reg_number, $name, $email, $password, $location, $phone, $photo,$date, $date);
							
						}catch(Exception $e){
							$response['error'] = true;
							$response['message'] = 'Could not upload file';
						}
					}else{
						$stmt->bind_param("sssssssss", $reg_number, $name, $email, $password, $location, $phone, $photo, $date, $date);
					}

					if($stmt->execute()){
						$last_id = $conn->insert_id;
						$response['reader_id'] = $last_id;
						$response['error'] = false;
						$response['reg_number'] = $reg_number;
						$response['photo'] = $photo;
						$response['message'] = 'File uploaded successfully';
					}else{
						throw new Exception("Could not upload file");
					}
				}else{
					$response['error'] = true;
					$response['message'] = "Required params not available";
				}
			
				break;
			//login and retrieve data from server
			case "login":
				$server_ip = gethostbyname(gethostname());
				$password = $_POST['password'];
				$email = $_POST['email'];
				$stmt = $conn->prepare("SELECT id, reg_number, name, location, phone, photo FROM readers WHERE email=? AND password=?");
				$stmt->bind_param("ss", $email, $password);
				if($stmt->execute()){
					$stmt->bind_result($id, $reg_number, $name, $location, $phone, $photo);
				
					$reader = array();
					$count = 0;
 
					while($stmt->fetch()){
						
						$reader['reader_id'] = $id;
						$reader['reg_number'] = $reg_number;
						$reader['name'] = $name;
						$reader['location'] = $location;
						$reader['phone'] = $phone;
						$reader['photo'] = 'http://' . $server_ip . '/apibook/'. READER_UPLOAD_PATH . $photo; 
						
						$count++;
					}

					if($count>0){
						$sql = "SELECT id, title, review, author, publisher, get_from, rating, cover, created_at FROM books WHERE reader_id=
								(SELECT id FROM readers WHERE email=? AND password=?)";
						$stmt1 = $conn->prepare($sql);
						$stmt1->bind_param("ss", $email, $password);
						$stmt1->execute();
						$stmt1->bind_result($id, $title, $review, $author, $publisher, $get_from, $rating, $cover, $created_at);
						$books = array();
						while ($stmt1->fetch()) {
							$temp = array();
							$temp['id'] = $id;
							$temp['title'] = $title;
							$temp['author'] = $author;
							$temp['publisher'] = $publisher;
							$temp['review'] = $review;
							$temp['rating'] = $rating;
							$temp['get_from'] = $get_from;
							$temp['cover'] = 'http://' . $server_ip . '/apibook/'. COVER_UPLOAD_PATH . $cover; 
							$temp['created_at'] = $created_at;
							
							array_push($books, $temp);
						}
						//pushing the array in response 
						$response['error'] = false;
						$response['books'] = $books;
						$response['reader'] = $reader;
					}else{
						$response['error'] = true;
						$response['message'] = 'Invalid email or password';
					}
				}
				break;
			
			// done id = reg_number
			case 'getreader':
		
				//getting server ip for building image url 
				$server_ip = gethostbyname(gethostname());
				$id = $_GET['id'];
				//query to get images from database
				$stmt = $conn->prepare("SELECT id, reg_number, name, location, phone, photo FROM readers WHERE reg_number='".$id."'");
				$stmt->execute();
				$stmt->bind_result($id, $reg_number, $name, $location, $phone, $photo);
				
				$readers = array();

				while($stmt->fetch()){
					$temp = array();
					$temp['id'] = $id;
					$temp['reg_number'] = $reg_number;
					$temp['name'] = $name;
					$temp['location'] = $location;
					$temp['phone'] = $phone;
					$temp['photo'] = 'http://' . $server_ip . '/apibook/'. READER_UPLOAD_PATH . $photo; 
					
					array_push($readers, $temp);
				}
			
				//pushing the array in response 
				$response['error'] = false;
				$response['readers'] = $readers; 
				break;
			// done id = reg_number
			case 'getreaderbybook':
		
				//getting server ip for building image url 
				$server_ip = gethostbyname(gethostname());
				$id = $_GET['id'];
				//query to get images from database
				$stmt = $conn->prepare("SELECT a.id, a.reg_number, a.name, a.location, a.phone, a.photo FROM readers a left join books b on a.id=b.reader_id 
					WHERE b.id='".$id."'");
				
				$stmt->execute();
				$stmt->bind_result($id, $reg_number, $name, $location, $phone, $photo);
				
				$readers = array();

				//fetching all the images from database
				//and pushing it to array 
				while($stmt->fetch()){
					$readers['id'] = $id;
					$readers['reg_number'] = $reg_number;
					$readers['name'] = $name;
					$readers['location'] = $location;
					$readers['phone'] = $phone;
					$readers['photo'] = 'http://' . $server_ip . '/apibook/'. READER_UPLOAD_PATH . $photo; 
					
					//array_push($readers, $temp);
				}
			
				//pushing the array in response 
				$response['error'] = false;
				$response['readers'] = $readers; 
				break;
			// done id = reg_number
			case 'editreader':
			 	if(isset($_POST['id'])){
			 		$server_ip = gethostbyname(gethostname());
			 		$id = $_POST['id']; 
					$name = $_POST['name']; //should be not included
					$password = $_POST['password'];
					$location = $_POST['location'];
					$phone = $_POST['phone'];
					$photo = "";
					//echo $id.$name.$location.$phone;
					$date = date("Y-m-d H:i:s");
					
					if(isset($_FILES['photo']['name'])){
						$photo = $id."".$_FILES['photo']['name'];
						//uploading file and storing it to database as well
						if($password!="" && $phone!=""){
							$stmt = $conn->prepare("UPDATE readers SET name=?, password=?, location=?, phone=?, photo=?
					 			 WHERE reg_number=?"); 
							$stmt->bind_param("ssssss", $name, $password, $location, $phone, $photo, $id);
						}else if($password=="" && $phone==""){
							$stmt = $conn->prepare("UPDATE readers SET name=?, location=?, photo=?
					 			 WHERE reg_number=?"); 
							$stmt->bind_param("ssss", $name, $location, $photo, $id);
						}else if($password==""){
							$stmt = $conn->prepare("UPDATE readers SET name=?, location=?, phone=?, photo=?
					 			 WHERE reg_number=?"); 
							$stmt->bind_param("sssss", $name, $location, $phone, $photo, $id);
						}else if($phone==""){
							$stmt = $conn->prepare("UPDATE readers SET name=?, password=?, location=?, photo=?
					 			 WHERE reg_number=?"); 
							$stmt->bind_param("sssss", $name, $password, $location, $photo, $id);
						}
						
					}else{
						$stmt = $conn->prepare("UPDATE readers SET name=?, password=?, location=?, phone=?
					 			 WHERE reg_number=?");
						$stmt->bind_param("sssss", $name, $password, $location, $phone, $id);
					}

					if($stmt->execute()){
						try{
							move_uploaded_file($_FILES['photo']['tmp_name'], READER_UPLOAD_PATH . $photo);
							$stmt = $conn->prepare("SELECT id, reg_number, name, location, phone, photo FROM readers WHERE reg_number='".$id."'");
							$stmt->execute();
							$stmt->bind_result($id, $reg_number, $name, $location, $phone, $photo);
							
							$readers = array();

							//fetching all the images from database
							//and pushing it to array 
							while($stmt->fetch()){
								$temp = array();
								$temp['id'] = $id;
								$temp['reg_number'] = $reg_number;
								$temp['name'] = $name;
								$temp['location'] = $location;
								$temp['phone'] = $phone;
								$temp['photo'] = 'http://' . $server_ip . '/apibook/'. READER_UPLOAD_PATH . $photo; 
								
								//array_push($readers, $temp);
								$readers = $temp;
							}
							$response['readers'] = $readers; 
							$response['error'] = false;
							$response['message'] = 'Profile updated successfully';
						}catch(Exception $e){
							$response['error'] = true;
							$response['message'] = 'Could not update your profile';
						}
					}else{
						throw new Exception("Could not upload file");
					}
				}else{
					$response['error'] = true;
					$response['message'] = "Required params not available";
				}
			 	break; 
			
			default: 
				$response['error'] = true;
				$response['message'] = 'Invalid api call';
		}
		
	}else if(isset($_GET['apibook'])){
		$apibook = $_GET['apibook'];
		switch ($apibook) {
			//
			case 'postbook':
				
				if(isset($_POST['title'])){
					$reader_id = $_POST['reader_id'];
					$title = $_POST['title'];
					$review = $_POST['review'];
					$author = $_POST['author'];
					$publisher = $_POST['publisher'];
					$get_from = $_POST['get_from'];
					$rating = $_POST['rating'];
					$date = date("Y-m-d H:i:s");
					$stmt = $conn->prepare("INSERT INTO books (reader_id, title, review, author, publisher, 
						get_from, rating, cover, created_at, updated_at) 
						VALUES (?,?,?,?,?,?,?,?,?,?)");
					
					try{
						$md5 = md5($title);
						$cover = $md5."_".$reader_id."".$_FILES['cover']['name'];
						move_uploaded_file($_FILES['cover']['tmp_name'], COVER_UPLOAD_PATH . $cover);
						//file_put_contents(COVER_UPLOAD_PATH.$cover_name, base64_decode($cover));
						$stmt->bind_param("isssssisss", $reader_id, $title, $review, $author, $publisher, $get_from, $rating, $cover,$date, $date);
						
					}catch(Exception $e){
						$response['error'] = true;
						$response['message'] = 'Could not add your book';
					}
					

					if($stmt->execute()){
						$last_id = $conn->insert_id;
						$response['error'] = false;
						$response['book_id'] = $last_id;
						$response['message'] = 'Book recorded successfully';
					}else{
						throw new Exception("Could not add your book");
					}
				}else{
					$response['error'] = true;
					$response['message'] = "Required params not available";
				}
				break;
			// done
			case 'search':
				$keyword = $_GET['keyword'];
				$sql = "SELECT a.id, a.title, a.author, a.publisher, 
						a.review, a.get_from, a.rating, a.cover, b.name, a.created_at FROM books a left join readers b ON a.reader_id = b.id ";
				if(isset($_GET["filter"])){
					$sql .= "WHERE a.".$_GET['filter']." LIKE '%".$keyword."%'";
				}else{
					$sql .= "WHERE a.title LIKE '%".$keyword."%'";
				}

				if(isset($_GET['id'])){
					$sql .= " AND a.reader_id<>".$_GET['id'];
				}
		
				//echo $sql;
				//getting server ip for building image url 
				$server_ip = gethostbyname(gethostname());
				//query to get images from database
				$stmt = $conn->prepare($sql);
				$stmt->execute();
				$stmt->bind_result($id, $title, $author, $publisher, $review, $get_from, $rating, $cover, $name, $created_at);
				
				$books = array();

				//and pushing it to array 
				while($stmt->fetch()){
					$temp = array();
					$temp['id'] = $id;
					$temp['title'] = $title;
					$temp['author'] = $author;
					$temp['publisher'] = $publisher;
					$temp['review'] = $review;
					$temp['rating'] = $rating;
					$temp['get_from'] = $get_from;
					$temp['cover'] = 'http://' . $server_ip . '/apibook/'. COVER_UPLOAD_PATH . $cover; 
					$temp['reader_name'] = $name;
					$temp['created_at'] = $created_at;
					
					array_push($books, $temp);
				}
				//var_dump($books);
				//pushing the array in response 
				$response['error'] = false;
				$response['books'] = $books;
				//var_dump($response);
				break;
			// done
			case 'each':
				$id = $_GET['id'];
				$sql = "SELECT a.id, a.title, a.author, a.publisher, a.rating, a.review, a.cover, b.name, b.photo, a.reader_id FROM books a left join readers b ON a.reader_id = b.id WHERE a.id=".$id;
				//getting server ip for building image url 
				$server_ip = gethostbyname(gethostname());
				//query to get images from database
				$stmt = $conn->prepare($sql);
				$stmt->execute();
				$stmt->bind_result($id, $title, $author, $publisher, $rating, $review, $cover, $name, $photo, $reader_id);
				
				$book = array();

				while($stmt->fetch()){
					//$temp = array();
					$book['id'] = $id;
					$book['title'] = $title;
					$book['author'] = $author;
					$book['publisher'] = $publisher;
					$book['rating'] = $rating;
					$book['review'] = $review;
					$book['cover'] = 'http://' . $server_ip . '/apibook/'. COVER_UPLOAD_PATH . $cover; 
					$book['reader_name'] = $name;
					$book['reader_id'] = $reader_id;
					$book['reader_photo'] = 'http://' . $server_ip . '/apibook/'. READER_UPLOAD_PATH . $photo; 
					
					//array_push($books, $temp);
				}

				$sql = "SELECT a.id, a.comment, a.review_rating, a.created_at, b.name, b.photo FROM comments a left join readers b on a.reader_id=b.id WHERE a.book_id=".$id." ORDER BY a.id DESC";

				$stmt = $conn->prepare($sql);
				$stmt->execute();
				$stmt->bind_result($id, $comment, $review_rating, $created_at, $name, $photo);
				
				$comments = array();

				while($stmt->fetch()){
					$temp = array();
					$temp['id'] = $id;
					$temp['comment'] = $comment;
					$temp['review_rating'] = $review_rating;
					$temp['name'] = $name;
					$temp['created_at'] = $created_at;
					$temp['reader_photo'] = 'http://' . $server_ip . '/apibook/'. READER_UPLOAD_PATH . $photo; 
					
					array_push($comments, $temp);
				}
				//pushing the array in response 
				$response['error'] = false;
				$response['books'] = $book;
				$response['comments'] = $comments;
				break;

			case 'gplus':
				if(isset($_POST['id'])){
			 		
					$gplus = 1;
					$id = $_POST['id'];
					$stmt = $conn->prepare("UPDATE books SET gplus=?
				 			 WHERE id=?");
					$stmt->bind_param("ss", $gplus, $id);
					

					if($stmt->execute()){
						
						$response['error'] = false;
						$response['message'] = 'Book updated successfully';
						
					}
				}else{
					$response['error'] = true;
					$response['message'] = "Required params not available";
				}
			 	break; 
			
			default:
				# code...
				break;

		}

	}else if(isset($_GET['apicomment'])){
		$apicomment = $_GET['apicomment'];
		switch ($apicomment) {
			// done
			case 'postcomment':
				if(isset($_POST['comment'])){
					$server_ip = gethostbyname(gethostname());
					$comment = $_POST['comment'];
					$book_id = $_POST['book_id'];
					$reader_id = $_POST['reader_id'];
					$review_rating = $_POST['review_rating'];
					$date = date("Y-m-d H:i:s");
					$stmt = $conn->prepare("INSERT INTO comments (book_id, reader_id, comment, review_rating, created_at, updated_at) 
						VALUES (?,?,?,?,?,?)");
					
					$stmt->bind_param("iisiss", $book_id, $reader_id, $comment, $review_rating, $date, $date);
					

					if($stmt->execute()){
						$sql = "SELECT a.id, a.comment, a.review_rating, a.created_at, b.name, b.photo FROM comments a left join readers b on a.reader_id=b.id WHERE a.book_id=".$book_id." ORDER BY a.id DESC";
						$stmt = $conn->prepare($sql);
						$stmt->execute();
						$stmt->bind_result($id, $comment, $review_rating, $created_at, $name, $photo);
						
						$comments = array();

						while($stmt->fetch()){
							$temp = array();
							$temp['id'] = $id;
							$temp['comment'] = $comment;
							$temp['review_rating'] = $review_rating;
							$temp['created_at'] = $created_at;
							$temp['name'] = $name;
							$temp['reader_photo'] = 'http://' . $server_ip . '/apibook/'. READER_UPLOAD_PATH . $photo; 
							
							array_push($comments, $temp);
						}
						$response['error'] = false;
						$response['comments'] = $comments;
						$response['message'] = 'Comment added successfully';
					}else{
						throw new Exception("Could not add comments");
					}
				}else{
					$response['error'] = true;
					$response['message'] = "Required params not available";
				}
				break;
			
			default:
				# code...
				break;
		}
		
	}else if(isset($_GET['apimail'])){
		$apimail = $_GET['apimail'];
		switch ($apimail) {
			case 'postmessage':
				# code...
				break;

			case 'retrieve':
				$id = $_GET['id'];
				$sqlsend = "SELECT id, sender_id, receiver_id, book_id, message, is_read, updated_at FROM mails WHERE sender_id=".$id;
				$sqlget = "SELECT id, sender_id, receiver_id, book_id, message, is_read, updated_at FROM mails WHERE receiver_id=".$id;
				$stmt = $conn->prepare($sql);
				$stmt->execute();
				$stmt->bind_result($id, $sender, $receiver, $book, $message, $is_read, $updated_at);
				
				$mails = array();

				while($stmt->fetch()){
					$temp = array();
					$temp['id'] = $id;
					$temp['sender'] = $sender;
					$temp['receiver'] = $receiver;
					$temp['book'] = $book;
					$temp['message'] = $message;
					$temp['is_read'] = $is_read; 
					$temp['updated_at'] = $updated_at;
					
					array_push($mails, $temp);
				}
				$response['send'] = $mails;
				break;
			default:
				# code...
				break;
		}
		/*}else{
			$response['error'] = true;
			$response['message'] = "Required params not available";
		}*/

	}else{
		header("HTTP/1.0 404 Not Found");
		echo "<h1>404 Not Found</h1>";
		echo "The page that you have requested could not be found.";
		exit();
	}
	
	//displaying the response in json 
	header('Content-Type: application/json');
	//echo json_encode($response);
	echo __json_encode($response);
