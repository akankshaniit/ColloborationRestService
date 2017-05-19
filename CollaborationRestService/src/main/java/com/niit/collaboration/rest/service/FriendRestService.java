package com.niit.collaboration.rest.service;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RestController;

import com.niit.collaboration.dao.FriendDAO;
import com.niit.collaboration.dao.UserDAO;
import com.niit.collaboration.model.Friend;
import com.niit.collaboration.model.User;

@RestController
public class FriendRestService {

	 private static Logger log = LoggerFactory.getLogger(FriendRestService.class);
	
	@Autowired
	private  Friend friend;
	
	@Autowired
	private FriendDAO friendDAO;
	
	@Autowired
	HttpSession httpSession;
	
	@Autowired
	UserDAO userDAO;
	
	@GetMapping("/myfriends")
	public ResponseEntity< List<Friend>> getAllFriend()
	{
		
		log.debug("->->->->calling method getMyFriends");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		List<Friend> myFriends = new ArrayList<Friend>();
		if(loggedInUserID == null)
		{
			friend.setErrorCode("404");
			friend.setErrorMessage("Please login to know your friends");
			myFriends.add(friend);
			return new ResponseEntity<List<Friend>>(myFriends, HttpStatus.OK);
			
		}
       
		log.debug("getting friends of : " + loggedInUserID);
		 myFriends = friendDAO.getMyFriends(loggedInUserID);

		if (myFriends.isEmpty()) {
			log.debug("Friends does not exsit for the user : " + loggedInUserID);
			friend.setErrorCode("404");
			friend.setErrorMessage("You does not have any friends");
			myFriends.add(friend);
		}
		log.debug("Send the friend list ");
		return new ResponseEntity<List<Friend>>(myFriends, HttpStatus.OK);	}
	
	@GetMapping("/addFriend/{friendID}")
	public ResponseEntity<Friend> sendFriendRequest(@PathVariable("friendID") String friendID) {
		log.debug("->->->->calling method sendFriendRequest");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		friend.setUser_id(loggedInUserID);
		friend.setFriend_id(friendID);
		friend.setStatus('N'); // N - New, R->Rejected, A->Accepted
		friend.setIsOnline('N');
		// Is the user already sent the request previous?
		
		//check whether the friend exist in user table or not
		if(isUserExist(friendID)==false)
		{
			friend.setErrorCode("404");
			friend.setErrorMessage("No user exist with the id :" + friendID);
		}
		
		else

		if (friendDAO.get(loggedInUserID, friendID) != null) {
			friend.setErrorCode("404");
			friend.setErrorMessage("You already sent the friend request to " + friendID);

		} else {
			friendDAO.save(friend);

			friend.setErrorCode("200");
			friend.setErrorMessage("Friend request successfull.." + friendID);
		}

		return new ResponseEntity<Friend>(friend, HttpStatus.OK);

	}
	
	private boolean isUserExist(String id)
	{
		if(userDAO.get(id)==null)
			return false;
		else
			return true;
	}
	
	private boolean isFriendRequestAvailabe(String friendID)
	{
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		
		if(friendDAO.get(loggedInUserID,friendID)==null)
			return false;
		else
			return true;
	}
	
	@PutMapping("/unFriend/{friendID}")
	public ResponseEntity<Friend> unFriend(@PathVariable("friendID") String friendID) {
		log.debug("->->->->calling method unFriend");
		updateRequest(friendID, 'U');
		return new ResponseEntity<Friend>(friend, HttpStatus.OK);

	}
	
	@PutMapping("/rejectFriend/{friendID}")
	public ResponseEntity<Friend> rejectFriendFriendRequest(@PathVariable("friendID") String friendID) {
		log.debug("->->->->calling method rejectFriendFriendRequest");

		updateRequest(friendID, 'R');
		return new ResponseEntity<Friend>(friend, HttpStatus.OK);

	}
	
	@PutMapping("/accepttFriend/{friendID}")
	public ResponseEntity<Friend> acceptFriendFriendRequest(@PathVariable("friendID") String friendID) {
		log.debug("->->->->calling method acceptFriendFriendRequest");
        
		friend = updateRequest(friendID, 'A');
		return new ResponseEntity<Friend>(friend, HttpStatus.OK);

	}
	
	private Friend updateRequest(String friendID, Character status) {
		log.debug("Starting of the method updateRequest");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		log.debug("loggedInUserID : " + loggedInUserID);
		
		if(isFriendRequestAvailabe(friendID)==false)
		{
			friend.setErrorCode("404");
			friend.setErrorMessage("The request does not exist.  So you can not update to "+status);
		}
		
		if (status.equals('A') || status.equals('R'))
			friend = friendDAO.get(friendID, loggedInUserID);
		else
			friend = friendDAO.get(loggedInUserID, friendID);
		friend.setStatus(status); // N - New, R->Rejected, A->Accepted

		friendDAO.update(friend);

		friend.setErrorCode("200");
		friend.setErrorMessage(
				"Request from   " + friend.getUser_id() + " To " + friend.getFriend_id() + " has updated to :" + status);
		log.debug("Ending of the method updateRequest");
		return friend;

	}

	@GetMapping("/getMyFriendRequests/")
	public ResponseEntity<List<Friend>> getMyFriendRequests() {
		log.debug("->->->->calling method getMyFriendRequests");
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		List<Friend> myFriendRequests = friendDAO.getNewFriendRequests(loggedInUserID);
		return new ResponseEntity<List<Friend>>(myFriendRequests, HttpStatus.OK);

	}
	
	@RequestMapping("/getRequestsSendByMe")
	public ResponseEntity<List<Friend>>  getRequestsSendByMe()
	{
		log.debug("->->->->calling method getRequestsSendByMe");
		
		String loggedInUserID = (String) httpSession.getAttribute("loggedInUserID");
		List<Friend> requestSendByMe = friendDAO.getRequestsSendByMe(loggedInUserID);
		
		log.debug("->->->->Ending method getRequestsSendByMe");
		
		return new ResponseEntity<List<Friend>>(requestSendByMe, HttpStatus.OK);
		
	}
	
	
	
	
	@PostMapping("/createfriend/")
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
	
	@DeleteMapping("deletefriend/{id}")
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
	    		  System.out.println("jjjj");
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
