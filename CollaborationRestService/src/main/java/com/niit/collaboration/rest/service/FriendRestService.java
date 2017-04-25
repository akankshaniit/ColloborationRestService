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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.niit.collaboration.dao.FriendDAO;
import com.niit.collaboration.model.Friend;
import com.niit.collaboration.model.User;

@RestController
public class FriendRestService {


	 private static Logger log = LoggerFactory.getLogger(FriendRestService.class);
	
	@Autowired
	private static Friend friend;
	
	@Autowired
	private static FriendDAO friendDAO;
	
	
	@GetMapping("/friends")
	public ResponseEntity< List<Friend>> getAllFriend()
	{
		List<Friend> friendList = friendDAO.list();
		
		//ResponseEntity:  we can send the data + HTTP status codes + error message
		// like 200 - success
		// 404 - page not found
		return   new ResponseEntity<List<Friend>>(friendList, HttpStatus.OK);
	}
	
	@GetMapping("/friend/{id}")
	public ResponseEntity<Friend> getFriendByID(@PathVariable("id") String id)
	{
		log.debug("**************Starting of the method getUserByID");
		log.info("***************Trying to get userdetails of the id " + id);
		friend = friendDAO.get(id);
		
		if(friend==null)
		{
			friend = new Friend();
			friend.setErrorCode("404");
			friend.setErrorMessage("User does not exist with the id :" + id);
		}
		else
		{
			friend.setErrorCode("200");
			friend.setErrorMessage("success");
		}
		
		log.info("**************** Name of the Friend is " + friend.getId());
		log.debug("**************Ending of the method getUserByID");
	  return	new ResponseEntity<Friend>(friend, HttpStatus.OK);
	}
	
	@PostMapping("/friend/")
	public Friend createFriend(@RequestBody Friend newFriend)
	{
		log.debug("Calling createUser method ");
		//before creating user, check whether the id exist in the db or not
		
		friend = friendDAO.get(newFriend.getId());
		if( friend ==null)
		{
			log.debug("User does not exist...trying to create new user");
			//id does not exist in the db
			friendDAO.save(newFriend);
			//NLP - NullPointerException
			//Whenever you call any method/variable on null object - you will get NLP
			newFriend.setErrorCode("200");
			newFriend.setErrorMessage("Thank you fo registration.");
			
		}
		else
		{
			log.debug("Please choose another id as it is existed");
			//id alredy exist in db.
			newFriend.setErrorCode("800");
			newFriend.setErrorMessage("Please choose another id as it is exist");
			
		}
		log.debug("Endig of the  createUser method ");
		return newFriend;
		
}
	
	@DeleteMapping("friend/{id}")
	public Friend deleteFriend(@PathVariable("id") String id)
	{
		
		//whether record exist with this id or not
		
		
	    if(	friendDAO.get(id)  ==null)
	    {
	    	friend.setErrorCode("404");
	    	friend.setErrorMessage("Could not delete.  User does not exist with this id " + id);
	    }
	    else
	    {
	    	  if (friendDAO.delete(id) )
	    	  {
	    		  friend.setErrorCode("200");
	    		  friend.setErrorMessage("Successfully deleted");
	    	  }
	    	  else
	    	  {
	    		  friend.setErrorCode("404");
	    		  friend.setErrorMessage("Could not delete. Please contact administrator");
	    	
	    	  }
	    	
	    	
	    	
	    	
	    	
	    }
	    
	    return friend;
		
	}

}
