package com.niit.collaboration.rest.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.niit.collaboration.dao.ChatDAO;
import com.niit.collaboration.model.Chat;
import com.niit.collaboration.model.Message;
import com.niit.collaboration.model.OutputMessage;
import com.niit.collaboration.model.User;


@RestController
@RequestMapping("/chat")
public class ChatRestService {

	
		
		  private Logger logger = LoggerFactory.getLogger(getClass());

		  @MessageMapping("/chat")
		  @SendTo("/topic/message")
		  public OutputMessage sendMessage(Message message) 
		  {
		    logger.info("Message sent");
		   return new OutputMessage(message, new Date());
		 	}
}