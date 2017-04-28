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

import com.niit.collaboration.dao.EventDAO;
import com.niit.collaboration.model.Event;
import com.niit.collaboration.model.User;

@RestController
public class EventRestService {

	private static Logger log = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private static Event event;
	
	@Autowired
	private static EventDAO eventDAO;
	
	@GetMapping("/events")
	public ResponseEntity< List<Event>> getAllEvent()
	{
		List<Event> eventList =  eventDAO.list();
		
		//ResponseEntity:  we can send the data + HTTP status codes + error message
		// like 200 - success
		// 404 - page not found
		return   new ResponseEntity<List<Event>>(eventList, HttpStatus.OK);
	}
	
	
	@GetMapping("/getevent/{id}")
	public ResponseEntity<Event> getUserByID(@PathVariable("id") String id)
	{
		log.debug("**************Starting of the method getEventByID");
		log.info("***************Trying to get eventdetails of the id " + id);
		event = eventDAO.get(id);
		
		if(event==null)
		{
			event = new Event();
			event.setErrorCode("404");
			event.setErrorMessage("Event does not exist with the id :" + id);
		}
		else
		{
			event.setErrorCode("200");
			event.setErrorMessage("success");
		}
		
		log.info("**************** Name of the event is " + event.getName());
		log.debug("**************Ending of the method getEventrByID");
	  return	new ResponseEntity<Event>(event , HttpStatus.OK);
	}
	
	@PostMapping("/createevent/")
	public Event createEvent(@RequestBody Event newEvent)
	{
		log.debug("Calling createEvent method ");
		//before creating user, check whether the id exist in the db or not
		
		event = eventDAO.get(newEvent.getId());
		if( event ==null)
		{
			log.debug("Event does not exist...trying to create new user");
			//id does not exist in the db
			eventDAO.save(newEvent);
			//NLP - NullPointerException
			//Whenever you call any method/variable on null object - you will get NLP
			newEvent.setErrorCode("200");
			newEvent.setErrorMessage("Thank you fo registration.");
			
		}
		else
		{
			log.debug("Please choose another id as it is existed");
			//id alredy exist in db.
			newEvent.setErrorCode("800");
			newEvent.setErrorMessage("Please choose another id as it is exist");
			
		}
		log.debug("Endig of the  createUser method ");
		return newEvent;
		
     }
	
	
	@PostMapping("/updateEvents/")
	
	public Event updateEventDetails(@RequestBody Event updateEvent)
	{
		
		//check whether the id exist or not
		
		event=  eventDAO.get(updateEvent.getId());
		
		
		if(event!=null)
		{
			eventDAO.update(updateEvent);
			updateEvent.setErrorCode("200");
			updateEvent.setErrorMessage("Successfully updated the details");
		}
		else
		{
			updateEvent.setErrorCode("800");
			updateEvent.setErrorMessage("Could not updated. Event does not exist with thid id " + updateEvent.getId());;
		}
		
		return updateEvent;
		
	}
	

	@DeleteMapping("deletevent/{id}")
	public Event deleteUser(@PathVariable("id") String id)
	{
		
		//whether record exist with this id or not
		
		
	    if(	 eventDAO.get(id)  ==null)
	    {
	    	event.setErrorCode("404");
	    	event.setErrorMessage("Could not delete.Event does not exist with this id " + id);
	    }
	    else
	    {
	    	  if (eventDAO.delete(id) )
	    	  {
	    		  event.setErrorCode("200");
	    		  event.setErrorMessage("Successfully deleted");
	    	  }
	    	  else
	    	  {
	    		  event.setErrorCode("404");
	    		  event.setErrorMessage("Could not delete. Please contact administrator");
	    	
	    	  }
	    	 	
	    }
	    
	    return event;
		
	}
	
}
