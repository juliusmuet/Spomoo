<?php 
/*
 * Author: Julius Müther
 * License: MIT License
 * Backend of Spomoo application
 * Contains all constants
 */

	//database parameters
    define('DB_HOST', 'TODO');
    define('DB_USER', 'TODO');
    define('DB_PASSWORD', 'TODO');
    define('DB_NAME', 'TODO');

	//return values of SQL operations
    define('REGISTER_SUCCESS', 101);
    define('USER_EXISTS', 102);
    define('REGISTER_FAILURE', 103); 
	
	define('VERIFICATION_NOT_EXISTS', 104); 
	define('VERIFICATION_PASSWORD_MISMATCH', 105);
	define('VERIFICATION_FAILURE', 106);
	define('VERIFICATION_SUCCESS', 107);
	
	define('UPDATE_DATA_EMAIL_TAKEN', 108);
	define('UPDATE_DATA_FAILURE', 109);
	define('UPDATE_DATA_SUCCESS', 110);
	define('UPDATE_PASSWORD_TEMPORARY_PASSWORD_INVALID', 111);
	
	define('DATABASE_INSERTION_FAILURE', 112);
	define('DATABASE_INSERTION_SUCCESS', 113);

    define('USER_AUTHENTICATED', 201);
    define('USER_NOT_FOUND', 202); 
    define('USER_PASSWORD_DO_NOT_MATCH', 203);

    define('PASSWORD_CHANGED', 301);
    define('PASSWORD_DO_NOT_MATCH', 302);
    define('PASSWORD_NOT_CHANGED', 303);

    