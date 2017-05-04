package com.niit.collaboration.rest.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.niit.collaboration.dao.UserDAO;
import com.niit.collaboration.model.User;

@RestController
public class UserService {

	 private static Logger log = LoggerFactory.getLogger(UserService.class);
		

	//getUser  -  @GetMapping
		//createUser -  @PostMapping
		//updateUser  - @PutMapping
		//getAllUsers - @GetMapping
		//validateCredentials  -  @PostMapping

	@Autowired 
	private User user;
	
	@Autowired
	private UserDAO userDAO;
	
	
	
	//Simple Test whether restcontroller is working or not
	
	
	
	
	//http://localhost:8080/CollaborationRestSerivce/hello
		@GetMapping("/hello")
		public String sayHello()
		{
			return "  Hello from User rest service Modifed message";
		}
		
	
	//http://localhost:8080/CollaborationRestService/users
		@GetMapping("/users")
		public ResponseEntity< List<User>> getAllUser()
		{
			List<User> userList =  userDAO.list();
			
			//ResponseEntity:  we can send the data + HTTP status codes + error message
			// like 200 - success
			// 404 - page not found
			return   new ResponseEntity<List<User>>(userList, HttpStatus.OK);
		}
	
		//http://localhost:8080/CollaborationResetService/user/niit
		@GetMapping("/user/{id}")
		public ResponseEntity<User> getUserByID(@PathVariable("id") String id)
		{
			log.debug("**************Starting of the method getUserByID");
			log.info("***************Trying to get userdetails of the id " + id);
			user = userDAO.get(id);
			
			if(user==null)
			{
				user = new User();
				user.setErrorCode("404");
				user.setErrorMessage("User does not exist with the id :" + id);
			}
			else
			{
				user.setErrorCode("200");
				user.setErrorMessage("success");
			}
			
			log.info("**************** Name of the user is " + user.getName());
			log.debug("**************Ending of the method getUserByID");
		  return	new ResponseEntity<User>(user , HttpStatus.OK);
		}
		
		@GetMapping("/validate/{id}/{password}")
		public User validateCredentials(@PathVariable("id") String id, @PathVariable("password") String pwd )
		{
			
			 if (userDAO.isValidate(id, pwd))
			 {
				user =  userDAO.get(id);
				 user.setErrorCode("200");
				 user.setErrorMessage("Valid credentials");
			 }
			 else
			 {
				 user.setErrorCode("404");
				 user.setErrorMessage("Invalid credentials");
			 }
			 
			 return user;
			
		}
		
	

		@PostMapping("/createuser/")
		public User createUser(@RequestBody User newUser)
		{
			log.debug("Calling createUser method ");
			//before creating user, check whether the id exist in the db or not
			
			user = userDAO.get(newUser.getId());
			if( user ==null)
			{
				log.debug("User does not exist...trying to create new user");
				//id does not exist in the db
				userDAO.save(newUser);
				//NLP - NullPointerException
				//Whenever you call any method/variable on null object - you will get NLP
				newUser.setErrorCode("200");
				newUser.setErrorMessage("Thank you for registration.");
				
			}
			else
			{
				log.debug("Please choose another id as it is existed");
				//id alredy exist in db.
				newUser.setErrorCode("800");
				newUser.setErrorMessage("Please choose another id as it is exist");
				
			}
			log.debug("Endig of the  createUser method ");
			return newUser;
			
	}
		
		@PostMapping("/updateUser/")
		
		public User updateUserDetails(@RequestBody User updateUser)
		{
			
			//check whether the id exist or not
			
			user=  userDAO.get(updateUser.getId());
			
			
			if(user!=null)
			{
				userDAO.update(updateUser);
				updateUser.setErrorCode("200");
				updateUser.setErrorMessage("Successfully updated the details");
			}
			else
			{
				updateUser.setErrorCode("800");
				updateUser.setErrorMessage("Could not updated. User does not exist with thid id " + updateUser.getId());;
			}
			
			return updateUser;
			
		}
		

		@DeleteMapping("deleteuser/{id}")
		public User deleteUser(@PathVariable("id") String id)
		{
			
			//whether record exist with this id or not
			
			
		    if(	userDAO.get(id)  ==null)
		    {
		    	user.setErrorCode("404");
		    	user.setErrorMessage("Could not delete.  User does not exist with this id " + id);
		    }
		    else
		    {
		    	  if (userDAO.delete(id) )
		    	  {
		    		  user.setErrorCode("200");
		  	    	user.setErrorMessage("Successfully deleted");
		    	  }
		    	  else
		    	  {
		    	    	user.setErrorCode("404");
		    	    	user.setErrorMessage("Could not delete. Please contact administrator");
		    	
		    	  }
		    	
		    	
		    	
		    	
		    	
		    }
		    
		    return user;
			
		}
}
