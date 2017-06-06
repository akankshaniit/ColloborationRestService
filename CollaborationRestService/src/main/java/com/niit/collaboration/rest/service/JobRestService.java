package com.niit.collaboration.rest.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.niit.collaboration.dao.JobApplicationDAO;
import com.niit.collaboration.dao.JobDAO;
import com.niit.collaboration.model.Blog;
import com.niit.collaboration.model.Job;
import com.niit.collaboration.model.JobApplication;
import com.niit.collaboration.model.User;

@RestController
public class JobRestService {

	 private static final String jobID = null;

	private static Logger log = LoggerFactory.getLogger(JobRestService.class);
	
	 @Autowired
		private  Job job;
	 @Autowired
		private  JobApplication jobApplication;
		
	
	 
		@Autowired
		private  JobDAO jobDAO;
		
		@Autowired
		HttpSession httpSession;
		
		@GetMapping("/jobs")
		public ResponseEntity< List<Job>> getAllJob()
		{
		     System.out.println("JobID"+jobDAO);
			List<Job> jobList =  jobDAO.list();
			
			//ResponseEntity:  we can send the data + HTTP status codes + error message
			// like 200 - success
			// 404 - page not found
			return   new ResponseEntity<List<Job>>(jobList, HttpStatus.OK);
		}
	
		@GetMapping("/getMyAppliedJobs/")
		public ResponseEntity<List<Job>> getMyAppliedJobs()
		{
			log.debug("Starting method getMyAppliedJobs");
			String loggedInUserID =(String)httpSession.getAttribute("loggedInUserId");
			List<Job> jobs= new ArrayList<Job>();

			if(loggedInUserID==null || loggedInUserID.isEmpty() )
			{
				job.setErrorCode("404");
				job.setErrorMessage("you have to login to see Applied Job");
				jobs.add(job);
			}
			else
			{
				jobs=jobDAO.getMyAppliedJobs(loggedInUserID);
			}
			
			
			
			return  new ResponseEntity<List<Job>>(jobs, HttpStatus.OK);
			
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
		
		
		
		
		
		@PostMapping("/updatejob/")
		
		public Job updateJob(@RequestBody Job updateJob)
		{
			
			//check whether the id exist or not
			log.debug("Starting UpdateJob Method");
			job=  jobDAO.get(updateJob.getId());
			
			
			if(job!=null)
			{
				jobDAO.update(updateJob);
				updateJob.setErrorCode("200");
				updateJob.setErrorMessage("Successfully updated the details of Job");
			}
			else
			{
				updateJob.setErrorCode("800");
				updateJob.setErrorMessage("Could not updated. Job does not exist with thid id " + updateJob.getId());;
			}
			log.debug("Ending UpdateJob Method");
			return updateJob;
			
		}
		
		@PostMapping("/postAjob/")
		public Job postAJob(@RequestBody Job newJob)
		{
			log.debug("starting a method of postAJob method");
	        job = jobDAO.get(newJob.getId());
		//	job.setStatus('V');
			
			if(job==null)
			{
				log.debug("trying to create new Job.....");
				//id does not exist in the db
				jobDAO.save(newJob);
				//NLP - NullPointerException
				//Whenever you call any method/variable on null object - you will get NLP
				newJob.setErrorCode("200");
				newJob.setErrorMessage("Thank you for Posting Job.");
				
			}
			else
			{
				log.debug("Please choose another id as it is existed");
				//id alredy exist in db.
				newJob.setErrorCode("800");
				newJob.setErrorMessage("Please choose another id as it is exist");
				
			}
			log.debug("Endig of the  PostJob method ");
			return newJob;

			
		}
		
		@PostMapping("/applyForJob/{JobID}")
		public ResponseEntity<JobApplication> applyForJob(@PathVariable("JobID") String JobID)
		{
			log.debug("Starting method applyForJob");
		String loggedInUserID=	(String) httpSession.getAttribute("loggedInUserID");
		
		
		if(loggedInUserID==null || loggedInUserID.isEmpty() )
		{
			jobApplication.setErrorCode("404");
			jobApplication.setErrorMessage("you have to login to see Applied Job");
			
		}
		else
			if(isUserAppliedForTheJob(loggedInUserID,JobID)==false){
				jobApplication.setJob_id(JobID);
				jobApplication.setUser_id(loggedInUserID);
				jobApplication.setStatus('N');
				jobApplication.setDateApplied(new Date(System.currentTimeMillis()));
				
				jobDAO.save(jobApplication);
				

					jobApplication.setErrorCode("200");
					jobApplication.setErrorMessage("Successfully Apply For Job...HR will be getBack to u");
					log.debug("Susseccfully Apply For Job...HR will be getBack to u");
			}
			else
			{
				jobApplication.setErrorCode("404");
				jobApplication.setErrorMessage("you are already aaplied for this job ");
				log.debug("Not able to apply for Job");
			}
		return  new ResponseEntity<JobApplication>(jobApplication,HttpStatus.OK);
		}

		
		
		@PutMapping("/selectuser/{userId}/{remarks}")
		public ResponseEntity <JobApplication> selectUser(@PathVariable("userID") String userID,
				@PathVariable("jobID") String jobID,@PathVariable("remark") String remarks )
		{
			
			log.debug("Starting Method SelectUser method");
			jobApplication= updateJobApplicationStatus(userID,jobID,'S',remarks);
			return new ResponseEntity<JobApplication>(jobApplication,HttpStatus.OK);
			
		}
		@PutMapping("/callForInterview/{userID}/{remarks}")
		public ResponseEntity <JobApplication> callForInterview(@PathVariable("userID") String userID,
				@PathVariable("jobID") String jobID,@PathVariable("remark") String remarks )
		{
			
			log.debug("Starting Method SelectUser method");
			jobApplication= updateJobApplicationStatus(userID,jobID,'C',remarks);
			return new ResponseEntity<JobApplication>(jobApplication,HttpStatus.OK);
			
		}
		@PutMapping("/rejectJobApplication/{userId}/{remarks}")
		public ResponseEntity <JobApplication> rejectJobApplication(@PathVariable("userID") String userID,
				@PathVariable("jobID") String jobID,@PathVariable("remark") String remarks )
		{
			
			log.debug("Starting Method SelectUser method");
			jobApplication= updateJobApplicationStatus(userID,jobID,'R',remarks);
			return new ResponseEntity<JobApplication>(jobApplication,HttpStatus.OK);
			
		}

		private JobApplication updateJobApplicationStatus(String userID, String jobID2,char status, String remarks) {
			log.debug("starting of updateJobApplicationStatus method ");
			if(isUserAppliedForTheJob(userID, jobID)==false)
			{
				jobApplication.setErrorCode("404");
				jobApplication.setErrorMessage("not applied for the job");
				return jobApplication;
						
			}
			String loggedInUserRole=(String) httpSession.getAttribute("loggedInUserRole");
			if(loggedInUserRole==null ||loggedInUserRole.isEmpty())
			{
				jobApplication.setErrorCode("404");
				jobApplication.setErrorMessage("you are not logged In");
				return jobApplication;
			}
			if(loggedInUserRole.equalsIgnoreCase("admin")){
				jobApplication.setErrorCode("404");
				jobApplication.setErrorMessage("you are not Admin you cannot do this ");
				return jobApplication;
			}
			jobApplication=jobDAO.getJobApplication(userID,userID);
			jobApplication.setStatus(status);
			jobApplication.setRemark(remarks);
			
			if(jobDAO.updateJob(jobApplication)){
				jobApplication.setErrorCode("200");
				jobApplication.setErrorMessage("Successfully Updated Status"+status);
			}
			else
			{
				jobApplication.setErrorCode("404");
				jobApplication.setErrorMessage("Not able to update Status");
			}
			return jobApplication;
		}
		
		private boolean isUserAppliedForTheJob(String userID,String jobId)
		{
			if(jobDAO.getJobApplication(userID, jobId)==null)
			{
				return false;
			}
			return true ;
		}
		
}
