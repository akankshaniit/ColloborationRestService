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

import com.niit.collaboration.dao.BlogDAO;
import com.niit.collaboration.model.Blog;
import com.niit.collaboration.model.User;

@RestController
public class BlogRestService {

	private static Logger log = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private Blog blog;
	
	@Autowired
	private BlogDAO blogDAO;
	
	@GetMapping("/blogs")
	public ResponseEntity< List<Blog>> getAllBlog()
	{
		List<Blog> blogList =  blogDAO.list();
		
		//ResponseEntity:  we can send the data + HTTP status codes + error message
		// like 200 - success
		// 404 - page not found
		return   new ResponseEntity<List<Blog>>(blogList, HttpStatus.OK);
	}

	
	@GetMapping("/blog/{id}")
	public ResponseEntity<Blog> getBlogByID(@PathVariable("id") String id)
	{
		log.debug("**************Starting of the method getBlogByID");
		log.info("***************Trying to get blogdetails of the id " + id);
		blog = blogDAO.get(id);
		
		if(blog==null)
		{
			blog = new Blog();
			blog.setErrorCode("404");
			blog.setErrorMessage("Blog does not exist with the id :" + id);
		}
		else
		{
			blog.setErrorCode("200");
			blog.setErrorMessage("success");
		}
		
		log.info("**************** Name of the Blog is " + blog.getUser_id());
		log.debug("**************Ending of the method getBlogByID");
	  return	new ResponseEntity<Blog>(blog , HttpStatus.OK);
	}
	
	@PostMapping("/blog/")
	public Blog createBlog(@RequestBody Blog newBlog)
	{
		log.debug("Calling createBlog method ");
		//before creating user, check whether the id exist in the db or not
		
		blog = blogDAO.get(newBlog.getId());
		if( blog ==null)
		{
			log.debug("User does not exist...trying to create new user");
			//id does not exist in the db
			blogDAO.save(newBlog);
			//NLP - NullPointerException
			//Whenever you call any method/variable on null object - you will get NLP
			newBlog.setErrorCode("200");
			newBlog.setErrorMessage("Thank you For register in Blog.");
			
		}
		else
		{
			log.debug("Please choose another id as it is existed");
			//id alredy exist in db.
			newBlog.setErrorCode("800");
			newBlog.setErrorMessage("Please choose another id as it is exist");
			
		}
		log.debug("Endig of the  createBlog method ");
		return newBlog;
		
}
	
	@PostMapping("/updateBlog/")
	
	public Blog updateBlogDetails(@RequestBody Blog updateBlog)
	{
		
		//check whether the id exist or not
		
		blog=  blogDAO.get(updateBlog.getId());
		
		
		if(blog!=null)
		{
			blogDAO.update(updateBlog);
			updateBlog.setErrorCode("200");
			updateBlog.setErrorMessage("Successfully updated the details of Blog");
		}
		else
		{
			updateBlog.setErrorCode("800");
			updateBlog.setErrorMessage("Could not updated. User does not exist with thid id " + updateBlog.getId());;
		}
		
		return updateBlog;
		
	}
	
	@DeleteMapping("blog/{id}")
	public Blog deleteBlog(@PathVariable("id") String id)
	{
		
		//whether record exist with this id or not
		
		
	    if(	blogDAO.get(id)  ==null)
	    {
	    	blog.setErrorCode("404");
	    	blog.setErrorMessage("Could not delete.  Blog does not exist with this id " + id);
	    }
	    else
	    {
	    	  if (blogDAO.delete(id) )
	    	  {
	    		  blog.setErrorCode("200");
	  	    	blog.setErrorMessage("Successfully deleted");
	    	  }
	    	  else
	    	  {
	    	    	blog.setErrorCode("404");
	    	    	blog.setErrorMessage("Could not delete. Please contact administrator");
	    	
	    	  }
	    	
	     }
	    
	    return blog;
		
	}
	
}
