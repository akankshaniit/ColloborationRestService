package com.niit.collaboration.rest.service;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.niit.collaboration.dao.FriendDAO;
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
	
	@Autowired
	FriendDAO friendDAO;
	
	@Autowired
	HttpSession session;
	
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
		
		@PostMapping("/validate")
		public ResponseEntity<User> validateCredentials(@RequestBody User user)
		{
			log.debug("->->->->calling method authenticate"+user.getEmail()+user.getPassword());
			user = userDAO.isValidate(user.getEmail(), user.getPassword());
			log.debug("user"+user);
			
			if (user == null) {
				user = new User(); // Do wee need to create new user?
				user.setErrorCode("404");
				user.setErrorMessage("Invalid Credentials.  Please enter valid credentials");
				log.debug("->->->->InValid Credentials");

			 }
			 else
			 {
				 
						user.setErrorCode("200");
						user.setErrorMessage("You have successfully logged in.");
					user.setIsOnline('Y');
						log.debug("->->->->Valid Credentials");
						/*session.setAttribute("loggedInUser", user);*/
						session.setAttribute("loggedInUserID", user.getId());
						session.setAttribute("loggedInUserRole", user.getRole());
					
						log.debug("You are loggin with the role : " +session.getAttribute("loggedInUserRole"));

					friendDAO.setOnline(user.getId());
					userDAO.setOnline(user.getId());
			 }

					return new ResponseEntity<User>(user, HttpStatus.OK);
			 
			
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
		
		public User updateUser(@RequestBody User updateUser)
		{
			
			//check whether the id exist or not
			
			user=  userDAO.get(updateUser.getId());
			System.out.println("hello"+user);
			
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
			
		//Admin should able to make one of the employee as admin
				@PutMapping("/makeAdmin/{id}")
				public ResponseEntity<User> makeAdmin(@PathVariable("id") String empID) {

					log.debug("calling the method makeAdmin");
					log.debug("with the id :" + empID);
					user = userDAO.get(empID);

					if (user == null) {
						log.debug("Employee does not exist with the id : " + empID);
						user = new User();
						user.setErrorCode("404");
						user.setErrorMessage("Employee does not exist");
						return new ResponseEntity<User>(user, HttpStatus.OK); // 200

					}
					
					if(user.getRole()!="Employee")
					{
						log.debug("We cannot make this user as admin: " + empID);
						user = new User();
						user.setErrorCode("404");
						user.setErrorMessage("We cannot make thhis user as admin: " + empID);
						return new ResponseEntity<User>(user, HttpStatus.OK); // 200
						
					}
						
					user.setRole("Admin");
					userDAO.update(user);
					user.setErrorCode("200");
					user.setErrorMessage("Successfully assign Admin role to the employy :" +user.getName());
					log.debug("Employee role updated as admin successfully " + empID);

					return new ResponseEntity<User>(user, HttpStatus.OK); // 200

				}
				
				@GetMapping("/listAllUsersNotFriends")
				public ResponseEntity<List<User>> listAllUsersNotFriends() {

					log.debug("->->->->calling method listAllUsers");
					
					String loggedInUserID = (String) session.getAttribute("loggedInUserID");
					
					log.debug("Loggined in user id is : " + loggedInUserID);
					
					
					List<User> users = userDAO.notMyFriendList(loggedInUserID);

					// errorCode :200 :404
					// errorMessage :Success :Not found

					if (users.isEmpty()) {
						user.setErrorCode("404");
						user.setErrorMessage("No users are available");
						users.add(user);
					}

					return new ResponseEntity<List<User>>(users, HttpStatus.OK);
				}
				
				@GetMapping( "/accept/{id}")
				public ResponseEntity<User> accept(@PathVariable("id") String id) {
					log.debug("Starting of the method accept" +id);

					user = updateStatus(id, 'A', "");
					log.debug("Ending of the method accept");
					return new ResponseEntity<User>(user, HttpStatus.OK);

				}
				
				@GetMapping( "/reject/{id}/{reason}")
				public ResponseEntity<User> reject(@PathVariable("id") String id, @PathVariable("reason") String reason) {
					log.debug("Starting of the method reject");

					user = updateStatus(id, 'R', reason);
					log.debug("Ending of the method reject");
					return new ResponseEntity<User>(user, HttpStatus.OK);

				}
				
				private User updateStatus(String id, char status, String reason) {
					log.debug("Starting of the method updateStatus");

					log.debug("status: " + status);
					user = userDAO.get(id);

					if (user == null) {
						user = new User();
						user.setErrorCode("404");
						user.setErrorMessage("Could not update the status to " + status);
					} else {

					//	user.setStatus(status);
					//	user.setReason(reason);
						
						userDAO.update(user);
						
						user.setErrorCode("200");
						user.setErrorMessage("Updated the status successfully");
					}
					log.debug("Ending of the method updateStatus");
					return user;

				}
				
				@GetMapping("/myProfile")
				public ResponseEntity<User> myProfile() {
					log.debug("->->calling method myProfile");
					String loggedInUserID = (String) session.getAttribute("loggedInUserID");
					User user = userDAO.get(loggedInUserID);
					if (user == null) {
						log.debug("->->->-> User does not exist wiht id" + loggedInUserID);
						user = new User(); // It does not mean that we are inserting new row
						user.setErrorCode("404");
						user.setErrorMessage("User does not exist");
						return new ResponseEntity<User>(user, HttpStatus.NOT_FOUND);
					}
					log.debug("->->->-> User exist with id" + loggedInUserID);
					log.debug(user.getName());
					return new ResponseEntity<User>(user, HttpStatus.OK);
				}
				
				
@GetMapping("/logout")
public ResponseEntity<User> logout(HttpSession session) {
	log.debug("->->->->calling method logout");
	String loggedInUserID = (String) session.getAttribute("loggedInUserID");
	
	 user = userDAO.get(loggedInUserID);
	 
/*	 SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyy : hh:ss");
	 sdf.parse(arg0)*/
	 
	 user.setLastSeenTime(new Date(System.currentTimeMillis()));
	 userDAO.update(user);
	 
	
	friendDAO.setOffLine(loggedInUserID);
	userDAO.setOffLine(loggedInUserID);

	session.invalidate();

	user.setErrorCode("200");
	user.setErrorMessage("You have successfully logged");
	return new ResponseEntity<User>(user, HttpStatus.OK);
};


}
	