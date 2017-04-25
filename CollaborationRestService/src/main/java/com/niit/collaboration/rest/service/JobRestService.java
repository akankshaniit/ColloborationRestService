package com.niit.collaboration.rest.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.niit.collaboration.dao.JobDAO;
import com.niit.collaboration.model.Job;
import com.niit.collaboration.model.User;

@RestController
public class JobRestService {

	 private static Logger log = LoggerFactory.getLogger(UserService.class);
		
	 @Autowired
		private static Job job;
		
		@Autowired
		private static JobDAO jobDAO;
		
		@GetMapping("/jobs")
		public ResponseEntity< List<Job>> getAllJob()
		{
			List<Job> jobList =  jobDAO.list();
			
			//ResponseEntity:  we can send the data + HTTP status codes + error message
			// like 200 - success
			// 404 - page not found
			return   new ResponseEntity<List<Job>>(jobList, HttpStatus.OK);
		}
	
		
		@GetMapping("/job/{id}")
		public ResponseEntity<Job> getJobByID(@PathVariable("id") String id)
		{
			log.debug("**************Starting of the method getJobByID");
			log.info("***************Trying to get Jobdetails of the id " + id);
			job = jobDAO.get(id);
			
			if(job==null)
			{
				job = new Job();
				job.setErrorCode("404");
				job.setErrorMessage("Job does not exist with the id :" + id);
			}
			else
			{
				job.setErrorCode("200");
				job.setErrorMessage("success");
			}
			
			log.info("**************** Name of the Job is " + job.getTitle());
			log.debug("**************Ending of the method getJobByID");
		  return	new ResponseEntity<Job>(job , HttpStatus.OK);
		}
}
