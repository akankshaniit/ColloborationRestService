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

import com.niit.collaboration.dao.ChatDAO;
import com.niit.collaboration.model.Chat;
import com.niit.collaboration.model.User;

@RestController
public class ChatRestService {

	 private static Logger log = LoggerFactory.getLogger(ChatRestService.class);
	 
	 @Autowired
		private static Chat chat;
		
		@Autowired
		private static ChatDAO chatDAO;
		
		
		@GetMapping("/chat")
		public ResponseEntity< List<Chat>> getAllChat()
		{
			List<Chat> chatList =  chatDAO.list();
			
			//ResponseEntity:  we can send the data + HTTP status codes + error message
			// like 200 - success
			// 404 - page not found
			return   new ResponseEntity<List<Chat>>(chatList, HttpStatus.OK);
		}
	
		//http://localhost:8080/CollaborationResetService/user/niit
		@GetMapping("/chat/{id}")
		public ResponseEntity<Chat> getUserByID(@PathVariable("id") String id)
		{
			log.debug("**************Starting of the method getChatByID");
			log.info("***************Trying to get userdetails of the id " + id);
			chat = chatDAO.get(id);
			
			if(chat==null)
			{
				chat = new Chat();
				chat.setErrorCode("404");
				chat.setErrorMessage("Chat id does not exist  :" + id);
			}
			else
			{
				chat.setErrorCode("200");
				chat.setErrorMessage("success");
			}
			
			log.info("**************** Name of the user is " + chat.getId());
			log.debug("**************Ending of the method getUserByID");
		  return	new ResponseEntity<Chat>(chat , HttpStatus.OK);
		}
		
		
		@PostMapping("/chat/")
		public Chat createChat(@RequestBody Chat newChat)
		{
			log.debug("Calling createUser method ");
			//before creating user, check whether the id exist in the db or not
			
			chat = chatDAO.get(newChat.getId());
			if( chat ==null)
			{
				log.debug("User does not exist...trying to create new user");
				//id does not exist in the db
				chatDAO.save(newChat);
				//NLP - NullPointerException
				//Whenever you call any method/variable on null object - you will get NLP
				newChat.setErrorCode("200");
				newChat.setErrorMessage("Thank you fo registration.");
				
			}
			else
			{
				log.debug("Please choose another id as it is existed");
				//id alredy exist in db.
				newChat.setErrorCode("800");
				newChat.setErrorMessage("Please choose another id as it is exist");
				
			}
			log.debug("Endig of the  createUser method ");
			return newChat;
			
	}
		
		@PostMapping("/updateUser/")
		
		public Chat updateUserDetails(@RequestBody Chat updateChat)
		{
			
			//check whether the id exist or not
			
			chat=  chatDAO.get(updateChat.getId());
			
			
			if(chat!=null)
			{
				chatDAO.update(updateChat);
				updateChat.setErrorCode("200");
				updateChat.setErrorMessage("Successfully updated the details");
			}
			else
			{
				updateChat.setErrorCode("800");
				updateChat.setErrorMessage("Could not updated. User does not exist with thid id " + updateChat.getId());;
			}
			
			return updateChat;
			
		}
		

		@DeleteMapping("chat/{id}")
		public Chat deleteChat(@PathVariable("id") String id)
		{
			
			//whether record exist with this id or not
			
			
		    if(	chatDAO.get(id)  ==null)
		    {
		    	chat.setErrorCode("404");
		    	chat.setErrorMessage("Could not delete.  User does not exist with this id " + id);
		    }
		    else
		    {
		    	  if (chatDAO.delete(id) )
		    	  {
		    		  chat.setErrorCode("200");
		  	    	chat.setErrorMessage("Successfully deleted");
		    	  }
		    	  else
		    	  {
		    	    	chat.setErrorCode("404");
		    	    	chat.setErrorMessage("Could not delete. Please contact administrator");
		    	
		    	  }
		    	
		    	
		    	
		    	
		    	
		    }
		    
		    return chat;
			
		}
		
		
		}
