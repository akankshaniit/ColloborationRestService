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
	 private static Logger log = LoggerFactory.getLogger(JobApplicationRestService.class);
	
 
	 @Autowired
		private static JobApplication jobApplication;
		
		@Autowired
		private static JobApplicationDAO jobApplicationDAO;
		
		@GetMapping("/jobapp")
		public ResponseEntity< List<JobApplication>> getAllJobApp()
		{
			List<JobApplication> jobApplicationList =  jobApplicationDAO.list();
			
			//ResponseEntity:  we can send the data + HTTP status codes + error message
			// like 200 - success
			// 404 - page not found
			return   new ResponseEntity<List<JobApplication>>(jobApplicationList, HttpStatus.OK);
		}
		
		
		@GetMapping("/jobapp/{id}")
		public ResponseEntity<JobApplication> getJobApplicationByID(@PathVariable("id") String id)
		{
			log.debug("**************Starting of the method getJobApplicationByID");
			log.info("***************Trying to get userdetails of the id " + id);
			jobApplication = jobApplicationDAO.get(id);
			
			if(jobApplication==null)
			{
				jobApplication = new JobApplication();
				jobApplication.setErrorCode("404");
				jobApplication.setErrorMessage("User does not exist with the id :" + id);
			}
			else
			{
				jobApplication.setErrorCode("200");
				jobApplication.setErrorMessage("success");
			}
			
			log.info("**************** Job Application userId " + jobApplication.getUserid());
			log.debug("**************Ending of the method getjobapplicationByID");
		  return	new ResponseEntity<JobApplication>(jobApplication , HttpStatus.OK);
		}
		
		
		@PostMapping("/createjob/")
		public JobApplication createJobApplication(@RequestBody JobApplication newJobApplication)
		{
			log.debug("Calling createjobapplication method ");
			//before creating user, check whether the id exist in the db or not
			
			jobApplication = jobApplicationDAO.get(newJobApplication.getId());
			if( jobApplication ==null)
			{
				log.debug("User does not exist...trying to create new user");
				//id does not exist in the db
				jobApplicationDAO.save(newJobApplication);
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
			log.debug("Endig of the  createJobApplication method ");
			return newJobApplication;
			
	}
		
		

		@PostMapping("/updatejobapp/")
		
		public JobApplication updateJobApplicationDetails(@RequestBody JobApplication updateJobApp)
		{
			
			//check whether the id exist or not
			
			jobApplication=  jobApplicationDAO.get(updateJobApp.getId());
			
			
			if(jobApplication!=null)
			{
				jobApplicationDAO.update(updateJobApp);
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
		
		
		
		@DeleteMapping("deletejob/{id}")
		public JobApplication deleteJobApplication(@PathVariable("id") String id)
		{
			
			//whether record exist with this id or not
			
			
		    if(	jobApplicationDAO.get(id)  ==null)
		    {
		    	jobApplication.setErrorCode("404");
		    	jobApplication.setErrorMessage("Could not delete.  User does not exist with this id " + id);
		    }
		    else
		    {
		    	  if (	jobApplicationDAO.delete(id) )
		    	  {
		    			jobApplication.setErrorCode("200");
		    			jobApplication.setErrorMessage("Successfully deleted");
		    	  }
		    	  else
		    	  {
		    			jobApplication.setErrorCode("404");
		    			jobApplication.setErrorMessage("Could not delete. Please contact administrator");
		    	
		    	  }
		    	
		      }
		    
		    return 	jobApplication;
			
		}
}
