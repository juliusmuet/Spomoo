<?php 
/*
 * Author: Julius Müther
 * License: MIT License
 * Backend of Spomoo application
 * Contains methods for SQL operations
 */
    class DbOperations{

        private $con; 

		//constructor
        function __construct(){
            require_once dirname(__FILE__) . '/DbConnect.php';
            $db = new DbConnect; 
            $this->con = $db->connect();	//establish database connection
        }

		//store registered user temporarily
        public function registerUser($username, $email, $gender, $birthday, $height, $weight){
           //check if user is already registered
			if($this->emailExists($email)){
				return USER_EXISTS;
			}
			
			//get current date
			$regdate = date("Y-m-d");
			
			//create temporary password
			$temppassword = $this->createTempPassword();
			
			//avoid sql injection with this pattern
			$stmt = $this->con->prepare("INSERT INTO tempusers (username, email, gender, birthday, height, weight, regdate, temppassword) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			$stmt->bind_param("ssssiiss", $username, $email, $gender, $birthday, $height, $weight, $regdate, $temppassword);
			
			//execute SQL query
			$result = $stmt->execute();
			$stmt->fetch();
			$stmt->close();
			if($result){
				//send email with temppassword to given email address
				$emailtext = "Your temporary password: " . $temppassword;
				mail($email, "Spomoo temporary password", $emailtext);
				
				//delete too old temporarily stored users
				$this->deleteTempUsers();
				
				return REGISTER_SUCCESS; 
			}else{
				return REGISTER_FAILURE;
			}
            
        }
		
		//delete temporarily stored users older than 2 days
		public function deleteTempUsers(){
			//calculate deletion date
			$d=strtotime("-2 days");
			$deletiondate = date("Y-m-d", $d);
			
			//delete temporary users
            $stmt = $this->con->prepare("DELETE FROM tempusers WHERE regdate <= ?");
            $stmt->bind_param("s", $deletiondate);
			$stmt->execute();
			$stmt->fetch();
			$stmt->close();
        }
		
		//verify temporarily stored user
		public function verifyUser($email, $temppassword, $password){
			//check if user is still stored in tempusers table
			if(!$this->tempUserExists($email)){
				return VERIFICATION_NOT_EXISTS;
			}
			
			//check if email and temppassword match
			if(!$this->verifyTempPassword($email, $temppassword)){
				return VERIFICATION_PASSWORD_MISMATCH;
			}
			
			//copy user data from tempusers to users table
			if(!$this->transferUserToVerified($email, $password)){
				return VERIFICATION_FAILURE;
			}
			
			//delete user from tempusers table
			$this->deleteTempUser($email);
			
			return VERIFICATION_SUCCESS;
		}
		
		//send email with temppassword for unverfied user to given email
		public function sendTempPassword($email){
			if(!$this->tempUserExists($email)){
				return VERIFICATION_NOT_EXISTS;
			}
			//get temporary password
			$stmt = $this->con->prepare("SELECT temppassword FROM tempusers WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
			$stmt->bind_result($temppassword);
			$stmt->fetch();
			$stmt->close();
			//send eMail
			$emailtext = "Your temporary password: " . $temppassword;
			mail($email, "Spomoo temporary password", $emailtext);
		}
		
		//verify email and temporary password
        private function verifyTempPassword($email, $temppassword){
			$stmt = $this->con->prepare("SELECT username FROM tempusers WHERE email = ? AND temppassword = ?");
            $stmt->bind_param("ss", $email, $temppassword);
            $stmt->execute(); 
            $stmt->store_result();
			$amount = $stmt->num_rows;
			$stmt->fetch();
			$stmt->close();
            return $amount > 0;  
        }
	
		//copy user data from tempusers to users table
		private function transferUserToVerified($email, $password){
			//get user data from temporarily stored users
			$stmt = $this->con->prepare("SELECT username, email, gender, birthday, height, weight, regdate FROM tempusers WHERE email = ?");
            $stmt->bind_param("s", $email);
			if(!$stmt->execute()){
				return false;
			}
            $stmt->bind_result($username, $email, $gender, $birthday, $height, $weight, $regdate);
			$stmt->fetch();
			$stmt->close();
            
			//create hash of password
			$hashpassword = password_hash($password, PASSWORD_ARGON2ID, ['memory_cost' => 65536, 'time_cost' => 4, 'threads' => 1]);
			
			//store date in users table
			$stmt = $this->con->prepare("INSERT INTO users (username, email, gender, birthday, height, weight, regdate, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            $stmt->bind_param("ssssiiss", $username, $email, $gender, $birthday, $height, $weight, $regdate, $hashpassword);
			$result = $stmt->execute();
			$stmt->fetch();
			$stmt->close();
            if(!$result){
				return false;
            }
			
			return true;
		}
		
		//delete temporary user with email
		private function deleteTempUser($email){
            $stmt = $this->con->prepare("DELETE FROM tempusers WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
			$stmt->fetch();
			$stmt->close();
        }
		
		//check if email and password match
        public function userLogin($email, $password){
            //check if user exists
			if($this->verifiedUserExists($email)){
                if($this->verifyPasswordOfMail($email, $password)){
                    return USER_AUTHENTICATED;
                }else{
                    return USER_PASSWORD_DO_NOT_MATCH; 
                }
            }else{
                return USER_NOT_FOUND; 
            }
        }
		
		//return user with email and password
		public function getUser($email, $password){
			//verify email-password combination
			if(!$this->verifyPasswordOfMail($email, $password)){
				return false;
			}
			
			//get data from email
			$stmt = $this->con->prepare("SELECT id, username, email, gender, birthday, height, weight FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute(); 
            $stmt->bind_result($id, $username, $emailuser, $gender, $birthday, $height, $weight);
            $stmt->fetch(); 
			$stmt->close();
            
			//store data in array
			$user = array(); 
			$user['id'] = $id; 
			$user['username']=$username; 
			$user['email'] = $emailuser; 
			$user['gender'] = $gender; 
			$user['birthday'] = $birthday; 
			$user['height'] = $height; 
			$user['weight'] = $weight; 
			return $user;
		}
		
		//return accelerometer data of user with id and password
		public function getAccelerometerData($id, $password){
			//verify id-password combination
			if(!$this->verifyPasswordOfId($id, $password)){
				return false;
			}
			
			//get data
			$stmt = $this->con->prepare("SELECT xaxis, yaxis, zaxis, acceleration, date, time, userid FROM accelerometer WHERE userid = ?");
            $stmt->bind_param("i", $id);
            $stmt->execute(); 
            $stmt->bind_result($xaxis, $yaxis, $zaxis, $acceleration, $date, $time, $userid);
           
            //store data in array
			$acceleromter = array(); 
			while($stmt->fetch()){ 
                $data = array(); 
                $data['xaxis'] = $xaxis; 
                $data['yaxis']=$yaxis; 
                $data['zaxis'] = $zaxis; 
                $data['acceleration'] = $acceleration;
				$data['date'] = $date; 
				$data['time'] = $time; 
				$data['userid'] = $userid; 
				$data['beensent'] = 1;
                array_push($acceleromter, $data);
            }  
			$stmt->close();			
            return $acceleromter; 
		}
		
		//return rotation data of user with id and password
		public function getRotationData($id, $password){
			//verify id-password combination
			if(!$this->verifyPasswordOfId($id, $password)){
				return false;
			}
			
			//get data
			$stmt = $this->con->prepare("SELECT xrotation, yrotation, zrotation, scalar, date, time, userid FROM rotation WHERE userid = ?");
            $stmt->bind_param("i", $id);
            $stmt->execute(); 
            $stmt->bind_result($xrotation, $yrotation, $zrotation, $scalar, $date, $time, $userid);
           
            //store data in array
			$rotation = array(); 
			while($stmt->fetch()){ 
                $data = array(); 
                $data['xrotation'] = $xrotation; 
                $data['yrotation']=$yrotation; 
                $data['zrotation'] = $zrotation; 
                $data['scalar'] = $scalar;
				$data['date'] = $date; 
				$data['time'] = $time; 
				$data['userid'] = $userid; 
				$data['beensent'] = 1;
                array_push($rotation, $data);
            }  
			$stmt->close();			
            return $rotation; 
		}
		
		//return steps data of user with id and password
		public function getStepsData($id, $password){
			//verify id-password combination
			if(!$this->verifyPasswordOfId($id, $password)){
				return false;
			}
			
			//get data
			$stmt = $this->con->prepare("SELECT steps, date, userid FROM steps WHERE userid = ?");
            $stmt->bind_param("i", $id);
            $stmt->execute(); 
            $stmt->bind_result($steps, $date, $userid);
           
            //store data in array
			$stepsdata = array(); 
			while($stmt->fetch()){ 
                $data = array(); 
                $data['steps'] = $steps; 
                $data['date']= $date; 
                $data['userid'] = $userid; 
				$data['beensent'] = 1;
                array_push($stepsdata, $data);
            }  
			$stmt->close();			
            return $stepsdata; 
		}
		
		//return sport data of user with id and password
		public function getSportData($id, $password){
			//verify id-password combination
			if(!$this->verifyPasswordOfId($id, $password)){
				return false;
			}
			
			//get data
			$stmt = $this->con->prepare("SELECT type, start, duration, intensity, date, userid FROM sport WHERE userid = ?");
            $stmt->bind_param("i", $id);
            $stmt->execute(); 
            $stmt->bind_result($type, $start, $duration, $intensity, $date, $userid);
           
            //store data in array
			$sport = array(); 
			while($stmt->fetch()){ 
                $data = array(); 
                $data['type'] = $type; 
                $data['start']=$start; 
                $data['duration'] = $duration; 
				$data['intensity'] = $intensity; 
                $data['date']=$date; 
                $data['userid'] = $userid; 
				$data['beensent'] = 1;
                array_push($sport, $data);
            }  
			$stmt->close();			
            return $sport; 
		}
		
		//return questionnaire data of user with id and password
		public function getQuestionnaireData($id, $password){
			//verify id-password combination
			if(!$this->verifyPasswordOfId($id, $password)){
				return false;
			}
			
			//get data
			$stmt = $this->con->prepare("SELECT mdbf_satisfied, mdbf_calm, mdbf_well, mdbf_relaxed, mdbf_energetic, 
			mdbf_awake, event_negative, event_positive, social_alone, social_dislike, social_people, location, rumination_properties, 
			rumination_rehash, rumination_turnoff, rumination_dispute, selfworth_satisfied, selfworth_dissatisfied, impulsive, 
			impulsive_angry, message, date, time, userid FROM questionnaire WHERE userid = ?");
            $stmt->bind_param("i", $id);
            $stmt->execute(); 
            $stmt->bind_result($mdbf_satisfied, $mdbf_calm, $mdbf_well, $mdbf_relaxed, $mdbf_energetic, $mdbf_awake, 
			$event_negative, $event_positive, $social_alone, $social_dislike, $social_people, $location,
			$rumination_properties, $rumination_rehash, $rumination_turnoff, $rumination_dispute, $selfworth_satisfied, $selfworth_dissatisfied,
			$impulsive, $impulsive_angry, $message, $date, $time, $userid);
           
            //store data in array
			$questionnaire = array(); 
			while($stmt->fetch()){ 
                $data = array(); 
                $data['mdbf_satisfied'] = $mdbf_satisfied; 
                $data['mdbf_calm']=$mdbf_calm; 
                $data['mdbf_well'] = $mdbf_well; 
				$data['mdbf_relaxed'] = $mdbf_relaxed; 
                $data['mdbf_energetic']=$mdbf_energetic; 
                $data['mdbf_awake'] = $mdbf_awake; 
				$data['event_negative'] = $event_negative; 
                $data['event_positive']=$event_positive; 
                $data['social_alone'] = $social_alone; 
				$data['social_dislike'] = $social_dislike; 
                $data['social_people']=$social_people; 
                $data['location'] = $location; 
				$data['rumination_properties'] = $rumination_properties; 
                $data['rumination_rehash']=$rumination_rehash; 
                $data['rumination_turnoff'] = $rumination_turnoff; 
				$data['rumination_dispute'] = $rumination_dispute; 
                $data['selfworth_satisfied']=$selfworth_satisfied; 
                $data['selfworth_dissatisfied'] = $selfworth_dissatisfied; 
				$data['impulsive'] = $impulsive; 
                $data['impulsive_angry']=$impulsive_angry; 
                $data['message'] = $message; 
				$data['date'] = $date; 
                $data['time']=$time; 
                $data['userid'] = $userid; 
				$data['questionnaireid'] = 0; 
				$data['expanded'] = false; 
				$data['beensent'] = 1;
                array_push($questionnaire, $data);
            }  
			$stmt->close();			
            return $questionnaire; 
		}
		
		//updates the given parameter after verifying the password and ensuring that email address is only used once
		public function updateUser($id, $username, $email, $gender, $birthday, $height, $weight, $password){
            //if password is incorrect
			if(!$this->verifyPasswordOfId($id, $password))
				return VERIFICATION_PASSWORD_MISMATCH;
			
			//if email is already taken by another user
			if(!$this->isEmailUsable($id, $email))
				return UPDATE_DATA_EMAIL_TAKEN;
			
			//update data in user table
			$stmt = $this->con->prepare("UPDATE users SET username = ?, email = ?, gender = ?, birthday = ?, height = ?, weight = ? WHERE id = ?");
            $stmt->bind_param("ssssiii", $username, $email, $gender, $birthday, $height, $weight, $id);
			$result = $stmt->execute();
			$stmt->fetch();
			$stmt->close();
            if($result)
                return UPDATE_DATA_SUCCESS; 
            return UPDATE_DATA_FAILURE; 
        }
		
		//send email with temppassword to given email for updating the password
		public function updatePasswordSendTemporaryPassword($email){
			if(!$this->verifiedUserExists($email)){
				return VERIFICATION_NOT_EXISTS;
			}
			
			//create and store temporary password
			$temppassword = $this->createTempPassword();
			$stmt = $this->con->prepare("UPDATE users SET temppassword = ? WHERE email = ?");
            $stmt->bind_param("ss", $temppassword, $email);
            $stmt->execute();
			$stmt->fetch();
			$stmt->close();
			
			//send email
			$emailtext = "Your temporary password: " . $temppassword;
			mail($email, "Spomoo temporary password", $emailtext);
		}
		
		//updates the password after verifying the temppassword
		public function updatePassword($email, $temppassword, $newpassword){
            //verify temporary password
			if(!$this->updatePasswordVerifyTempPassword($email, $temppassword))
                return UPDATE_PASSWORD_TEMPORARY_PASSWORD_INVALID;
       
			//create hash of password
			$hashpassword = password_hash($newpassword, PASSWORD_ARGON2ID, ['memory_cost' => 65536, 'time_cost' => 4, 'threads' => 1]);
			
            //update password in user table
			$stmt = $this->con->prepare("UPDATE users SET password = ? WHERE email = ?");
			$stmt->bind_param("ss",$hashpassword, $email);
			$stmt->execute();
			$stmt->fetch();
			$stmt->close();
        }
		
		//checks if email-temppassword combination does exist
		private function updatePasswordVerifyTempPassword($email, $temppassword){
			$stmt = $this->con->prepare("SELECT username FROM users WHERE email = ? AND temppassword = ?");
            $stmt->bind_param("ss", $email, $temppassword);
            $stmt->execute(); 
            $stmt->store_result();
			$amount = $stmt->num_rows;
			$stmt->fetch();
			$stmt->close();
            return $amount > 0;  
		}
		
		//check if temporary user is still stored
        private function tempUserExists($email){
            $stmt = $this->con->prepare("SELECT username FROM tempusers WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute(); 
            $stmt->store_result();
			$amount = $stmt->num_rows;
			$stmt->fetch();
			$stmt->close();
            return $amount > 0;  
        }
		
		//check if email is used by a verified user
        private function verifiedUserExists($email){
            $stmt = $this->con->prepare("SELECT username FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute(); 
            $stmt->store_result();
			$amount = $stmt->num_rows;
			$stmt->fetch();
			$stmt->close();
            return $amount > 0;  
        }
		
		//check if email is already used by another user
        private function emailExists($email){
            return ($this->verifiedUserExists($email) || $this->tempUserExists($email));
        }
		
		//checks an id-password combination
		private function verifyPasswordOfId($id, $password){
			$stmt = $this->con->prepare("SELECT password FROM users WHERE id = ?");
            $stmt->bind_param("i", $id);
            $stmt->execute(); 
			$stmt->bind_result($passwordhash);
			$stmt->fetch();
			$stmt->close();
			return password_verify($password, $passwordhash);
		}
		
		//checks an email-password combination
		private function verifyPasswordOfMail($email, $password){
			$stmt = $this->con->prepare("SELECT password FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute(); 
			$stmt->bind_result($passwordhash);
			$stmt->fetch();
			$stmt->close();
			return password_verify($password, $passwordhash);
		}
		
		//checks if email address can be used by user
		private function isEmailUsable($id, $email){
			//if email is not used by anybody
			if(!$this->emailExists($email))
				return true;
			
			//check if email is used by this user and not another user
			$stmt = $this->con->prepare("SELECT id FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute(); 
			$stmt->bind_result($otherid);
			$stmt->fetch();
			$stmt->close();
			if($id == $otherid)
				return true;
			return false;
		}
		
		//create a random string with length 8
		private function createTempPassword(){
			$str = '';
			$length = 8;
			$keyspace = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ%#+-_?&';
			$max = strlen($keyspace);
			for ($i = 0; $i < $length; ++$i) {
				$str .= $keyspace[random_int(0, $max)];
			}
			return $str;
		}
		
		//insert array of accelerometer data into accelerometer table
		public function insertAccelerometer($accelerometer){
			//prepare insert statement based on length of array
			$stmt = $this->con->prepare("INSERT INTO accelerometer(xaxis, yaxis, zaxis, acceleration, date, time, userid) VALUES (?,?,?,?,?,?,?)" . str_repeat(",(?,?,?,?,?,?,?)", sizeof($accelerometer)-1));
            $parameterTypes = str_repeat("ddddssi", sizeof($accelerometer));
			
			//collect the values
			$parameterValues = array();
            foreach ($accelerometer as $data) {
                $values = array();
                $values['xaxis'] = $data['xaxis'];
                $values['yaxis'] = $data['yaxis'];
                $values['zaxis'] = $data['zaxis'];
                $values['acceleration'] = $data['acceleration'];
                $values['date'] = $data['date'];
                $values['time'] = $data['time'];
                $values['userid'] = $data['userid'];
                array_push($parameterValues, array_values($values));
            }

			//insert the values
            $stmt->bind_param($parameterTypes, ...array_merge(...$parameterValues));
            $result = $stmt->execute();
            $stmt->close();
			
			if($result)
				return DATABASE_INSERTION_SUCCESS;
			return DATABASE_INSERTION_FAILURE;
		}
		
		//insert array of rotation data into rotation table
		public function insertRotation($rotation){
			//prepare insert statement based on length of array
			$stmt = $this->con->prepare("INSERT INTO rotation(xrotation, yrotation, zrotation, scalar, date, time, userid) VALUES (?,?,?,?,?,?,?)" . str_repeat(",(?,?,?,?,?,?,?)", sizeof($rotation)-1));
            $parameterTypes = str_repeat("ddddssi", sizeof($rotation));
			
			//collect the values
			$parameterValues = array();
            foreach ($rotation as $data) {
                $values = array();
                $values['xrotation'] = $data['xrotation'];
                $values['yrotation'] = $data['yrotation'];
                $values['zrotation'] = $data['zrotation'];
                $values['scalar'] = $data['scalar'];
                $values['date'] = $data['date'];
                $values['time'] = $data['time'];
                $values['userid'] = $data['userid'];
                array_push($parameterValues, array_values($values));
            }

			//insert the values
            $stmt->bind_param($parameterTypes, ...array_merge(...$parameterValues));
            $result = $stmt->execute();
            $stmt->close();
			
			if($result)
				return DATABASE_INSERTION_SUCCESS;
			return DATABASE_INSERTION_FAILURE;
		}
		
		//insert array of steps data into rotation table
		public function insertSteps($steps){
			//get current date and initialise variable whether values have been updated
			$todayDate = date('Y-m-d', time());
			$updated = 0;
			//collect the values
			$parameterValues = array();
            foreach ($steps as $data) {
                $values = array();
                $values['steps'] = $data['steps'];
                $values['date'] = $data['date'];
                $values['userid'] = $data['userid'];
				
				//if it is today's date, check if data has already been inserted
				if(strcmp($todayDate, $values['date']) == 0){
					if(!$this->updateTodaysSteps($values['steps'], $values['date'], $values['userid'])){
						array_push($parameterValues, array_values($values));
					} else $updated += 1;
				} else array_push($parameterValues, array_values($values));
            }
			
			if(sizeof($steps)-1-$updated < 0)
                return DATABASE_INSERTION_SUCCESS;
			
			//prepare insert statement based on length of array and execute it
			$stmt = $this->con->prepare("INSERT INTO steps(steps, date, userid) VALUES (?,?,?)" . str_repeat(",(?,?,?)", sizeof($steps)-1-$updated));
            $parameterTypes = str_repeat("dsi", sizeof($steps)-$updated);
            $stmt->bind_param($parameterTypes, ...array_merge(...$parameterValues));
            $result = $stmt->execute();
            $stmt->close();
			
			if($result)
				return DATABASE_INSERTION_SUCCESS;
			return DATABASE_INSERTION_FAILURE;
		}
		
		//check if steps for date and user have already been inserted and update steps if so
        private function updateTodaysSteps($steps, $date, $userid){
            $stmt = $this->con->prepare("SELECT steps FROM steps WHERE date = ? AND userid = ?");
            $stmt->bind_param("si", $date, $userid);
            $stmt->execute(); 
            $stmt->store_result();
			$amount = $stmt->num_rows;
			$stmt->fetch();
			$stmt->close();
            
			if($amount > 0){
				//update steps
				$stmt = $this->con->prepare("UPDATE steps SET steps = ? WHERE date = ? AND userid = ?");
				$stmt->bind_param("isi",$steps, $date, $userid);
				$stmt->execute();
				$stmt->fetch();
				$stmt->close();
				return true;
			} else return false;	//return false if no steps value has been inserted
        }
		
		//insert array of sport data into sport table
		public function insertSport($sport){
			//prepare insert statement based on length of array
			$stmt = $this->con->prepare("INSERT INTO sport(type, start, duration, intensity, date, userid) VALUES (?,?,?,?,?,?)" . str_repeat(",(?,?,?,?,?,?)", sizeof($sport)-1));
            $parameterTypes = str_repeat("sssisi", sizeof($sport));
			
			//collect the values
			$parameterValues = array();
            foreach ($sport as $data) {
                $values = array();
                $values['type'] = $data['type'];
                $values['start'] = $data['start'];
                $values['duration'] = $data['duration'];
                $values['intensity'] = $data['intensity'];
                $values['date'] = $data['date'];
                $values['userid'] = $data['userid'];
                array_push($parameterValues, array_values($values));
            }

			//insert the values
            $stmt->bind_param($parameterTypes, ...array_merge(...$parameterValues));
            $result = $stmt->execute();
            $stmt->close();
			
			if($result)
				return DATABASE_INSERTION_SUCCESS;
			return DATABASE_INSERTION_FAILURE;
		}
		
		//insert array of questionnaire data into questionnaire table
		public function insertQuestionnaire($questionnaire){
			//prepare insert statement based on length of array
			$stmt = $this->con->prepare("INSERT INTO questionnaire(mdbf_satisfied, mdbf_calm, mdbf_well, mdbf_relaxed, mdbf_energetic, 
			mdbf_awake, event_negative, event_positive, social_alone, social_dislike, social_people, location, rumination_properties, 
			rumination_rehash, rumination_turnoff, rumination_dispute, selfworth_satisfied, selfworth_dissatisfied, impulsive, 
			impulsive_angry, message, date, time, userid) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" 
			. str_repeat(",(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", sizeof($questionnaire)-1));
            $parameterTypes = str_repeat("iiiiiiiiiissiiiiiiiisssi", sizeof($questionnaire));
			
			//collect the values
			$parameterValues = array();
            foreach ($questionnaire as $data) {
                $values = array();
                $values['mdbf_satisfied'] = $data['mdbf_satisfied'];
                $values['mdbf_calm'] = $data['mdbf_calm'];
                $values['mdbf_well'] = $data['mdbf_well'];
                $values['mdbf_relaxed'] = $data['mdbf_relaxed'];
                $values['mdbf_energetic'] = $data['mdbf_energetic'];
                $values['mdbf_awake'] = $data['mdbf_awake'];
				$values['event_negative'] = $data['event_negative'];
                $values['event_positive'] = $data['event_positive'];
                $values['social_alone'] = $data['social_alone'];
                $values['social_dislike'] = $data['social_dislike'];
                $values['social_people'] = $data['social_people'];
                $values['location'] = $data['location'];
				$values['rumination_properties'] = $data['rumination_properties'];
                $values['rumination_rehash'] = $data['rumination_rehash'];
                $values['rumination_turnoff'] = $data['rumination_turnoff'];
                $values['rumination_dispute'] = $data['rumination_dispute'];
                $values['selfworth_satisfied'] = $data['selfworth_satisfied'];
                $values['selfworth_dissatisfied'] = $data['selfworth_dissatisfied'];
				$values['impulsive'] = $data['impulsive'];
                $values['impulsive_angry'] = $data['impulsive_angry'];
                $values['message'] = $data['message'];
                $values['date'] = $data['date'];
                $values['time'] = $data['time'];
                $values['userid'] = $data['userid'];
                array_push($parameterValues, array_values($values));
            }

			//insert the values
            $stmt->bind_param($parameterTypes, ...array_merge(...$parameterValues));
            $result = $stmt->execute();
            $stmt->close();
			
			if($result)
				return DATABASE_INSERTION_SUCCESS;
			return DATABASE_INSERTION_FAILURE;
		}
		
    }