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

import com.niit.collaboration.dao.ChatForumCommentDAO;
import com.niit.collaboration.model.ChatForumComment;
import com.niit.collaboration.model.User;

@RestController
public class ChatForumRestService {

	private static Logger log = LoggerFactory.getLogger(ChatForumRestService.class);
	
	@Autowired
	private static ChatForumComment chatForumComment;
	

	@Autowired
	private static ChatForumCommentDAO chatForumCommentDAO;
	
	@GetMapping("/chatforum")
	public ResponseEntity< List<ChatForumComment>> getAllChatForumComment()
	{
		List<ChatForumComment> chatForumCommentList = chatForumCommentDAO.list();
		
		//ResponseEntity:  we can send the data + HTTP status codes + error message
		// like 200 - success
		// 404 - page not found
		return   new ResponseEntity<List<ChatForumComment>>(chatForumCommentList, HttpStatus.OK);
	}
	@GetMapping("/chatForumComment/{id}")
	public ResponseEntity<ChatForumComment> getChatForumCommentByID(@PathVariable("id") String id)
	{
		log.debug("**************Starting of the method getUserByID");
		log.info("***************Trying to get 	chatForumCommentdetails of the id " + id);
		chatForumComment = chatForumCommentDAO.get(id);
		
		if(chatForumComment==null)
		{
			chatForumComment = new ChatForumComment();
			chatForumComment.setErrorCode("404");
			chatForumComment.setErrorMessage("	chatForumComment does not exist with the id :" + id);
		}
		else
		{
			chatForumComment.setErrorCode("200");
			chatForumComment.setErrorMessage("success");
		}
		
		log.info("**************** id of the 	chatForumComment is " + 	chatForumComment.getId());
		log.debug("**************Ending of the method get	chatForumCommentByID");
	  return	new ResponseEntity<ChatForumComment>(chatForumComment , HttpStatus.OK);
	}
	
	@PostMapping("/createchatForum/")
	public ChatForumComment createChatForum(@RequestBody ChatForumComment newChatForumComment)
	{
		log.debug("Calling createUser method ");
		//before creating user, check whether the id exist in the db or not
		
		chatForumComment = chatForumCommentDAO.get(newChatForumComment.getId());
		if( chatForumComment ==null)
		{
			log.debug("User does not exist...trying to create new user");
			//id does not exist in the db
			chatForumCommentDAO.save(newChatForumComment);
			//NLP - NullPointerException
			//Whenever you call any method/variable on null object - you will get NLP
			newChatForumComment.setErrorCode("200");
			newChatForumComment.setErrorMessage("Thank you fo registration.");
			
		}
		else
		{
			log.debug("Please choose another id as it is existed");
			//id alredy exist in db.
			newChatForumComment.setErrorCode("800");
			newChatForumComment.setErrorMessage("Please choose another id as it is exist");
			
		}
		log.debug("Endig of the  createchatForumComment method ");
		return newChatForumComment;
		
	}
	@DeleteMapping("deleteChatForum/{id}")
	public ChatForumComment deleteChatForum(@PathVariable("id") String id)
	{
		
		//whether record exist with this id or not
		
		
	    if(	chatForumCommentDAO.get(id)  ==null)
	    {
	    	chatForumComment.setErrorCode("404");
	    	chatForumComment.setErrorMessage("Could not delete.  User does not exist with this id " + id);
	    }
	    else
	    {
	    	  if (chatForumCommentDAO.delete(id) )
	    	  {
	    		  chatForumComment.setErrorCode("200");
	    		  chatForumComment.setErrorMessage("Successfully deleted");
	    	  }
	    	  else
	    	  {
	    		  chatForumComment.setErrorCode("404");
	    		  chatForumComment.setErrorMessage("Could not delete. Please contact administrator");
	    	
	    	  }
	    	
	    	 }
	    
	    return chatForumComment;
		
	}
	
	
}
