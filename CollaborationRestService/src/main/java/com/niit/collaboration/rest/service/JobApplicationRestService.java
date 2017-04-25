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

import com.niit.collaboration.dao.JobApplicationDAO;
import com.niit.collaboration.model.JobApplication;
import com.niit.collaboration.model.User;

@RestController
public class JobApplicationRestService {
	 private static Logger log = LoggerFactory.getLogger(UserService.class);
	
	 
	 @Autowired
		private static JobApplication jobapplication;
		
		@Autowired
		private static JobApplicationDAO jobapplicationDAO;
		
		@GetMapping("/jobapp")
		public ResponseEntity< List<JobApplication>> getAllJobApp()
		{
			List<JobApplication> jobapplicationList =  jobapplicationDAO.list();
			
			//ResponseEntity:  we can send the data + HTTP status codes + error message
			// like 200 - success
			// 404 - page not found
			return   new ResponseEntity<List<JobApplication>>(jobapplicationList, HttpStatus.OK);
		}
		
		
		@GetMapping("/jobapp/{id}")
		public ResponseEntity<JobApplication> getJobApplicationByID(@PathVariable("id") String id)
		{
			log.debug("**************Starting of the method getJobApplicationByID");
			log.info("***************Trying to get userdetails of the id " + id);
			jobapplication = jobapplicationDAO.get(id);
			
			if(jobapplication==null)
			{
				jobapplication = new JobApplication();
				jobapplication.setErrorCode("404");
				jobapplication.setErrorMessage("User does not exist with the id :" + id);
			}
			else
			{
				jobapplication.setErrorCode("200");
				jobapplication.setErrorMessage("success");
			}
			
			log.info("**************** Job Application userId " + jobapplication.getUserid());
			log.debug("**************Ending of the method getjobapplicationByID");
		  return	new ResponseEntity<JobApplication>(jobapplication , HttpStatus.OK);
		}
		
		
		@PostMapping("/jobapplication/")
		public JobApplication createJobApplication(@RequestBody JobApplication newJobApplication)
		{
			log.debug("Calling createjobapplication method ");
			//before creating user, check whether the id exist in the db or not
			
			jobapplication = jobapplicationDAO.get(newJobApplication.getId());
			if( jobapplication ==null)
			{
				log.debug("User does not exist...trying to create new user");
				//id does not exist in the db
				jobapplicationDAO.save(newJobApplication);
				//NLP - NullPointerException
				//Whenever you call any method/variable on null object - you will get NLP
				newJobApplication.setErrorCode("200");
				newJobApplication.setErrorMessage("Thank you fo registration.");
				
			}
			else
			{
				log.debug("Please choose another id as it is existed");
				//id alredy exist in db.
				newJobApplication.setErrorCode("800");
				newJobApplication.setErrorMessage("Please choose another id as it is exist");
				
			}
			log.debug("Endig of the  createUser method ");
			return newJobApplication;
			
	}
		
		

		@PostMapping("/updatejobapp/")
		
		public JobApplication updateJobApplicationDetails(@RequestBody JobApplication updateJobApp)
		{
			
			//check whether the id exist or not
			
			jobapplication=  jobapplicationDAO.get(updateJobApp.getId());
			
			
			if(jobapplication!=null)
			{
				jobapplicationDAO.update(updateJobApp);
				updateJobApp.setErrorCode("200");
				updateJobApp.setErrorMessage("Successfully updated the details");
			}
			else
			{
				updateJobApp.setErrorCode("800");
				updateJobApp.setErrorMessage("Could not updated. User does not exist with thid id " + updateJobApp.getId());;
			}
			
			return updateJobApp;
		}
		
		
		
		@DeleteMapping("jobapplication/{id}")
		public JobApplication deleteJobApplication(@PathVariable("id") String id)
		{
			
			//whether record exist with this id or not
			
			
		    if(	jobapplicationDAO.get(id)  ==null)
		    {
		    	jobapplication.setErrorCode("404");
		    	jobapplication.setErrorMessage("Could not delete.  User does not exist with this id " + id);
		    }
		    else
		    {
		    	  if (	jobapplicationDAO.delete(id) )
		    	  {
		    			jobapplication.setErrorCode("200");
		    			jobapplication.setErrorMessage("Successfully deleted");
		    	  }
		    	  else
		    	  {
		    			jobapplication.setErrorCode("404");
		    			jobapplication.setErrorMessage("Could not delete. Please contact administrator");
		    	
		    	  }
		    	
		      }
		    
		    return 	jobapplication;
			
		}
}
