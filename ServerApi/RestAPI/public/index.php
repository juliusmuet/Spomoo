<?php

/* API of Spomoo application/json
 * Restful PHP script using Slim Framework
 * Implements Http Basic Authentication
 */

use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;

require '../vendor/autoload.php';
require '../includes/DbOperations.php';

//setup slim framework
$app = new \Slim\App([
    'settings'=>[
        'displayErrorDetails'=>true
    ]
]);

//setup http basic authentication
$app->add(new Tuupola\Middleware\HttpBasicAuthentication([
    "secure"=>false,
    "users" => [
        "TODO" => "TODO",
    ]
]));

/* 
	url: /registeruser
    method: POST
	parameters: username, email, birthday, gender, height, weight
    description: stores user's data temporarily and sends an email with a temorary password to the given address
*/
$app->post('/registeruser', function(Request $request, Response $response){
    //check if parameters are missing
	if(hasMissingParameters(array('username', 'email', 'gender', 'birthday', 'height', 'weight'), $request, $response)){
			//returns response if not all parameters are set containing these missing parameters
		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(422); 
	}			
        
	//get parameters
	$request_data = $request->getParsedBody(); 
	$username = $request_data['username'];
	$email = $request_data['email'];
	$gender = $request_data['gender'];
	$birthday = $request_data['birthday'];
	$height = $request_data['height']; 
	$weight = $request_data['weight'];

	//store in temp database table
	$db = new DbOperations; 
	$result = $db->registerUser($username, $email, $gender, $birthday, $height, $weight);
	
	//on success
	if($result == REGISTER_SUCCESS){
		$message = array(); 
		$message['error'] = false; 
		$message['message'] = 'User registered temporarily';

		$response->write(json_encode($message));
		return $response
					->withHeader('Content-type', 'application/json')
					->withStatus(200);

	//user already registered
	}else if($result == USER_EXISTS){
		$message = array(); 
		$message['error'] = true; 
		$message['message'] = '1';

		$response->write(json_encode($message));
		return $response
					->withHeader('Content-type', 'application/json')
					->withStatus(422);    

	//on failure
	}else if($result == REGISTER_FAILURE){
		$message = array(); 
		$message['error'] = true; 
		$message['message'] = '2';

		$response->write(json_encode($message));
		return $response
					->withHeader('Content-type', 'application/json')
					->withStatus(422); 
	}					
	  
});

/* 
	url: /registeruser/deletetempusers
    method: DELETE
	parameters: none
    description: deletes all temporary users with registration date older than 3 days
*/
$app->delete('/registeruser/deletetempusers', function(Request $request, Response $response, array $args){
    $db = new DbOperations; 

    $response_data = array();

	//on success
    if($db->deleteTempUsers()){
        $response_data['error'] = false; 
        $response_data['message'] = 'Successful deletion';   
		$response->write(json_encode($response_data));
		return $response
					->withHeader('Content-type', 'application/json')
					->withStatus(200);		
	//on failure
	}else{
        $response_data['error'] = true; 
        $response_data['message'] = 'Deletion failed';
		$response->write(json_encode($message));
		return $response
					->withHeader('Content-type', 'application/json')
					->withStatus(422); 
    }

});

/* 
	url: /registeruser/verifyuser
    method: POST
	parameters: email, temppassword, password
    description: checks if temppassword and email are in tempusers table and stores user data in user table
*/
$app->post('/registeruser/verifyuser', function(Request $request, Response $response){
    //check if parameters are missing
	if(hasMissingParameters(array('email', 'temppassword', 'password'), $request, $response)){
		//returns response if not all parameters are set containing these missing parameters
		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(422); 
	}
	
	//get parameters
	$request_data = $request->getParsedBody(); 
	$email = $request_data['email'];
	$temppassword = $request_data['temppassword'];
	$password = $request_data['password'];

	//verify email and temppassword in tempusers and store user data in users table
	$db = new DbOperations; 
	$result = $db->verifyUser($email, $temppassword, $password);
	
	//on success
	if($result == VERIFICATION_SUCCESS){
		$message = array(); 
		$message['error'] = false; 
		$message['message'] = 'User verified';
		$message['user'] = $db->getUser($email, $password);

		$response->write(json_encode($message));
		return $response
					->withHeader('Content-type', 'application/json')
					->withStatus(200);

	//temporary user already deleted
	}else if($result == VERIFICATION_NOT_EXISTS){
		$message = array(); 
		$message['error'] = true; 
		$message['message'] = '1';

		$response->write(json_encode($message));
		return $response
					->withHeader('Content-type', 'application/json')
					->withStatus(422);    
	
	//temporary password does not match
	}else if($result == VERIFICATION_PASSWORD_MISMATCH){
		$message = array(); 
		$message['error'] = true; 
		$message['message'] = '2';

		$response->write(json_encode($message));
		return $response
					->withHeader('Content-type', 'application/json')
					->withStatus(422);    

	//other error
	}else if($result == VERIFICATION_FAILURE){
		$message = array(); 
		$message['error'] = true; 
		$message['message'] = 'Some error occured';

		$response->write(json_encode($message));
		return $response
					->withHeader('Content-type', 'application/json')
					->withStatus(422);  
	}
   
});

/* 
	url: /registeruser/sendtemppassword
    method: POST
	parameters: email
    description: sends email with the temporary password of unverified users again to the given email
*/
$app->post('/registeruser/sendtemppassword', function(Request $request, Response $response){
    //check if all parameters are set
	if(hasMissingParameters(array('email'), $request, $response)){
		//returns response if not all parameters are set containing these missing parameters
		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(422); 
	}
	
	//get parameters
	$request_data = $request->getParsedBody(); 
	$email = $request_data['email'];

	//verify email and temppassword in tempusers and store user data in users table
	$db = new DbOperations; 
	$result = $db->sendTempPassword($email);
	
	//temporary user already deleted
	if($result == VERIFICATION_NOT_EXISTS){
		$message = array(); 
		$message['error'] = true; 
		$message['message'] = '1';

		$response->write(json_encode($message));
		return $response
					->withHeader('Content-type', 'application/json')
					->withStatus(422);    
	}

	//return success response
	$message = array(); 
    $message['error'] = false; 
    $message['message'] = 'eMail sent';
	$response->write(json_encode($message));
    return $response
        ->withHeader('Content-type', 'application/json')
        ->withStatus(200);    
});

/* 
	url: /userlogin
    method: POST
	parameters: email, password
    description: verifies email-password combination and returns user on success
*/
$app->post('/userlogin', function(Request $request, Response $response){
	//check if all parameters are set
    if(hasMissingParameters(array('email', 'password'), $request, $response)){
		//returns response if not all parameters are set containing these missing parameters
		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(422); 
	}
	
	//get data
	$request_data = $request->getParsedBody(); 
	$email = $request_data['email'];
	$password = $request_data['password'];
	
	//verify email-password combination
	$db = new DbOperations; 
	$result = $db->userLogin($email, $password);

	//successful login
	if($result == USER_AUTHENTICATED){
		$response_data = array();
		$response_data['error'] = false; 
		$response_data['message'] = 'Login Successful';
		
		//get all uploaded data of user
		$user = $db->getUser($email, $password);
		$response_data['user'] = $user; 
		$response_data['accelerometer'] = $db->getAccelerometerData($user['id'], $password);
		$response_data['rotation'] = $db->getRotationData($user['id'], $password);
		$response_data['steps'] = $db->getStepsData($user['id'], $password);
		$response_data['sport'] = $db->getSportData($user['id'], $password);
		$response_data['questionnaire'] = $db->getQuestionnaireData($user['id'], $password);

		$response->write(json_encode($response_data));
		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(200);    

	//user does not exist
	}else if($result == USER_NOT_FOUND){
		$response_data = array();
		$response_data['error'] = true; 
		$response_data['message'] = '1';

		$response->write(json_encode($response_data));
		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(422);    

	//password is incorrect
	}else if($result == USER_PASSWORD_DO_NOT_MATCH){
		$response_data = array();
		$response_data['error'] = true; 
		$response_data['message'] = '2';

		$response->write(json_encode($response_data));
		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(422);  
	}
   
});

/* 
	url: /updateuser/personaldata
    method: POST
	parameters: id, username, email, gender, birthday, height, weight, password
    description: updates the given parameters after verifying the id-password combination and asserts that email addresses
	             are only used once
*/
$app->post('/updateuser/personaldata', function(Request $request, Response $response, array $args){
	//check if all parameters are set
    if(hasMissingParameters(array('id', 'username','email','gender', 'birthday', 'height', 'weight', 'password'), $request, $response)){
		//returns response if not all parameters are set containing these missing parameters
		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(422); 
	}

	//get data
	$request_data = $request->getParsedBody(); 
	$id = $request_data['id'];
	$username = $request_data['username'];
	$email = $request_data['email'];
	$gender = $request_data['gender'];
	$birthday = $request_data['birthday']; 
	$height = $request_data['height']; 
	$weight = $request_data['weight'];
	$password = $request_data['password']; 	
 
	//update given data in table based on the id
	$db = new DbOperations; 
	$result = $db->updateUser($id, $username, $email, $gender, $birthday, $height, $weight, $password);

	//password is incorrect
	if($result == VERIFICATION_PASSWORD_MISMATCH){
		$response_data = array(); 
		$response_data['error'] = true; 
		$response_data['message'] = '1';

		$response->write(json_encode($response_data));
		return $response
		->withHeader('Content-type', 'application/json')
		->withStatus(422);  
	
	//new email is already taken
	}else if($result == UPDATE_DATA_EMAIL_TAKEN){
		$response_data = array(); 
		$response_data['error'] = true; 
		$response_data['message'] = '2';

		$response->write(json_encode($response_data));
		return $response
		->withHeader('Content-type', 'application/json')
		->withStatus(422);  
	
	//other error
	} else if($result == UPDATE_DATA_FAILURE){
		$response_data = array(); 
		$response_data['error'] = true; 
		$response_data['message'] = '3';

		$response->write(json_encode($response_data));
		return $response
		->withHeader('Content-type', 'application/json')
		->withStatus(422);  
	}
	
	//on success
	$response_data = array(); 
	$response_data['error'] = false; 
	$response_data['message'] = 'User data changed successfully';
	$response_data['user'] = $db->getUser($email, $password);	//return updated user data

	$response->write(json_encode($response_data));
	return $response
	->withHeader('Content-type', 'application/json')
	->withStatus(200); 
});

/* 
	url: /updateuser/sendtemppassword
    method: POST
	parameters: email
    description: sends email with a temporary password to the given email
*/
$app->post('/updateuser/sendtemppassword', function(Request $request, Response $response){
    //check if all parameters are set
	if(hasMissingParameters(array('email'), $request, $response)){
		//returns response if not all parameters are set containing these missing parameters
		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(422); 
	}
	
	//get parameters
	$request_data = $request->getParsedBody(); 
	$email = $request_data['email'];

	//create and store temporary password and send an email containing the temporary password
	$db = new DbOperations; 
	$result = $db->updatePasswordSendTemporaryPassword($email);
	
	//user does not exist
	if($result == VERIFICATION_NOT_EXISTS){
		$message = array(); 
		$message['error'] = true; 
		$message['message'] = '1';

		$response->write(json_encode($message));
		return $response
					->withHeader('Content-type', 'application/json')
					->withStatus(422);    
	}

	//return success response
	$message = array(); 
    $message['error'] = false; 
    $message['message'] = 'eMail sent';
	$response->write(json_encode($message));
    return $response
        ->withHeader('Content-type', 'application/json')
        ->withStatus(200);    
});

/* 
	url: /updateuser/updatepassword
    method: POST
	parameters: email, temppassword, newpassword
    description: updates the password after verifying the email-temppassword combination
*/
$app->post('/updateuser/updatepassword', function(Request $request, Response $response){
    //check if all parameters are set
	if(hasMissingParameters(array('email', 'temppassword', 'newpassword'), $request, $response)){
		//returns response if not all parameters are set containing these missing parameters
		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(422); 
	}
        
	//get parameters
	$request_data = $request->getParsedBody(); 
	$email = $request_data['email'];
	$temppassword = $request_data['temppassword'];
	$newpassword = $request_data['newpassword']; 

	//verify temporary password and update the password
	$db = new DbOperations; 
	$result = $db->updatePassword($email, $temppassword, $newpassword);

	//temporary password is incorrect
	if($result == UPDATE_PASSWORD_TEMPORARY_PASSWORD_INVALID){
		$response_data = array(); 
		$response_data['error'] = true;
		$response_data['message'] = '1';
		$response->write(json_encode($response_data));
		return $response->withHeader('Content-type', 'application/json')
						->withStatus(422);
	}

    //return success response
	$message = array(); 
    $message['error'] = false; 
    $message['message'] = 'Password changed';
	$response->write(json_encode($message));
    return $response
        ->withHeader('Content-type', 'application/json')
        ->withStatus(200); 
});

/* 
	url: /senddata/accelerometer
    method: POST
	parameters: JSON String of array of accelerometer objects
    description: inserts the given accelerometer data into the database
*/
$app->post('/senddata/accelerometer', function(Request $request, Response $response){
    //check if all parameters are set
	if(hasMissingParameters(array('accelerometer'), $request, $response)){
		//returns response if not all parameters are set containing these missing parameters
		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(422); 
	}
        
	//get parameters
	$request_data = $request->getParsedBody(); 
	$accelerometer = $request_data['accelerometer'];
	$accelerometer = json_decode($accelerometer, TRUE);	//convert into array of objects
	
	//if no data given
	if(sizeof($accelerometer) < 1){
		$message = array(); 
		$message['error'] = false; 
		$message['message'] = 'Data inserted';
		$response->write(json_encode($message));
		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(200); 
	}

	//insert data into accelerometer table
	$db = new DbOperations; 
	$result = $db->insertAccelerometer($accelerometer);

	//SQL error
	if($result == DATABASE_INSERTION_FAILURE){
		$response_data = array(); 
		$response_data['error'] = true;
		$response_data['message'] = '1';
		$response->write(json_encode($response_data));
		return $response->withHeader('Content-type', 'application/json')
						->withStatus(422);
	}

    //return success response
	$message = array(); 
    $message['error'] = false; 
    $message['message'] = 'Data inserted';
	$response->write(json_encode($message));
    return $response
        ->withHeader('Content-type', 'application/json')
        ->withStatus(200); 
});

/* 
	url: /senddata/rotation
    method: POST
	parameters: JSON String of array of rotation objects
    description: inserts the given rotation data into the database
*/
$app->post('/senddata/rotation', function(Request $request, Response $response){
    //check if all parameters are set
	if(hasMissingParameters(array('rotation'), $request, $response)){
		//returns response if not all parameters are set containing these missing parameters
		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(422); 
	}
        
	//get parameters
	$request_data = $request->getParsedBody(); 
	$rotation = $request_data['rotation'];
	$rotation = json_decode($rotation, TRUE);	//convert into array of objects
	
	//if no data given
	if(sizeof($rotation) < 1){
		$message = array(); 
		$message['error'] = false; 
		$message['message'] = 'Data inserted';
		$response->write(json_encode($message));
		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(200); 
	}

	//insert data into rotation table
	$db = new DbOperations; 
	$result = $db->insertRotation($rotation);

	//SQL error
	if($result == DATABASE_INSERTION_FAILURE){
		$response_data = array(); 
		$response_data['error'] = true;
		$response_data['message'] = '1';
		$response->write(json_encode($response_data));
		return $response->withHeader('Content-type', 'application/json')
						->withStatus(422);
	}

    //return success response
	$message = array(); 
    $message['error'] = false; 
    $message['message'] = 'Data inserted';
	$response->write(json_encode($message));
    return $response
        ->withHeader('Content-type', 'application/json')
        ->withStatus(200); 
});

/* 
	url: /senddata/steps
    method: POST
	parameters: JSON String of array of steps objects
    description: inserts the given steps data into the database
*/
$app->post('/senddata/steps', function(Request $request, Response $response){
    //check if all parameters are set
	if(hasMissingParameters(array('steps'), $request, $response)){
		//returns response if not all parameters are set containing these missing parameters
		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(422); 
	}
        
	//get parameters
	$request_data = $request->getParsedBody(); 
	$steps = $request_data['steps'];
	$steps = json_decode($steps, TRUE);	//convert into array of objects
	
	//if no data given
	if(sizeof($steps) < 1){
		$message = array(); 
		$message['error'] = false; 
		$message['message'] = 'Data inserted';
		$response->write(json_encode($message));
		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(200); 
	}

	//insert data into steps table
	$db = new DbOperations; 
	$result = $db->insertSteps($steps);

	//SQL error
	if($result == DATABASE_INSERTION_FAILURE){
		$response_data = array(); 
		$response_data['error'] = true;
		$response_data['message'] = '1';
		$response->write(json_encode($response_data));
		return $response->withHeader('Content-type', 'application/json')
						->withStatus(422);
	}

    //return success response
	$message = array(); 
    $message['error'] = false; 
    $message['message'] = 'Data inserted';
	$response->write(json_encode($message));
    return $response
        ->withHeader('Content-type', 'application/json')
        ->withStatus(200); 
});

/* 
	url: /senddata/sport
    method: POST
	parameters: JSON String of array of sport objects
    description: inserts the given sport data into the database
*/
$app->post('/senddata/sport', function(Request $request, Response $response){
    //check if all parameters are set
	if(hasMissingParameters(array('sport'), $request, $response)){
		//returns response if not all parameters are set containing these missing parameters
		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(422); 
	}
        
	//get parameters
	$request_data = $request->getParsedBody(); 
	$sport = $request_data['sport'];
	$sport = json_decode($sport, TRUE);	//convert into array of objects
	
	//if no data given
	if(sizeof($sport) < 1){
		$message = array(); 
		$message['error'] = false; 
		$message['message'] = 'Data inserted';
		$response->write(json_encode($message));
		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(200); 
	}

	//insert data into sport table
	$db = new DbOperations; 
	$result = $db->insertSport($sport);

	//SQL error
	if($result == DATABASE_INSERTION_FAILURE){
		$response_data = array(); 
		$response_data['error'] = true;
		$response_data['message'] = '1';
		$response->write(json_encode($response_data));
		return $response->withHeader('Content-type', 'application/json')
						->withStatus(422);
	}

    //return success response
	$message = array(); 
    $message['error'] = false; 
    $message['message'] = 'Data inserted';
	$response->write(json_encode($message));
    return $response
        ->withHeader('Content-type', 'application/json')
        ->withStatus(200); 
});

/* 
	url: /senddata/questionnaire
    method: POST
	parameters: JSON String of array of questionnaire objects
    description: inserts the given questionnaire data into the database
*/
$app->post('/senddata/questionnaire', function(Request $request, Response $response){
    //check if all parameters are set
	if(hasMissingParameters(array('questionnaire'), $request, $response)){
		//returns response if not all parameters are set containing these missing parameters
		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(422); 
	}
        
	//get parameters
	$request_data = $request->getParsedBody(); 
	$questionnaire = $request_data['questionnaire'];
	$questionnaire = json_decode($questionnaire, TRUE);	//convert into array of objects
	
	//if no data given
	if(sizeof($questionnaire) < 1){
		$message = array(); 
		$message['error'] = false; 
		$message['message'] = 'Data inserted';
		$response->write(json_encode($message));
		return $response
			->withHeader('Content-type', 'application/json')
			->withStatus(200); 
	}

	//insert data into sport table
	$db = new DbOperations; 
	$result = $db->insertQuestionnaire($questionnaire);

	//SQL error
	if($result == DATABASE_INSERTION_FAILURE){
		$response_data = array(); 
		$response_data['error'] = true;
		$response_data['message'] = '1';
		$response->write(json_encode($response_data));
		return $response->withHeader('Content-type', 'application/json')
						->withStatus(422);
	}

    //return success response
	$message = array(); 
    $message['error'] = false; 
    $message['message'] = 'Data inserted';
	$response->write(json_encode($message));
    return $response
        ->withHeader('Content-type', 'application/json')
        ->withStatus(200); 
});

//check if all required parameters are in request
function hasMissingParameters($required_params, $request, $response){
    //initialise return values
	$error = false; 
    $error_params = '';
	
	//get parameters
    $request_params = $request->getParsedBody(); 

	//check each parameter and add missing parameters to error string
    foreach($required_params as $param){
        if(!isset($request_params[$param])){
            $error = true; 
            $error_params .= $param . ', ';
        }
    }

	//if parameters are missing, write these in the response
    if($error){
        $error_detail = array();
        $error_detail['error'] = true; 
        $error_detail['message'] = 'Required parameters ' . substr($error_params, 0, -2) . ' are missing or empty';
        $response->write(json_encode($error_detail));
    }
    return $error; 
}

$app->run();