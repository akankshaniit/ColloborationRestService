package com.niit.collaboration.rest.service;

import java.util.Date;
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
import com.niit.collaboration.dao.CommentDAO;
import com.niit.collaboration.model.Blog;
import com.niit.collaboration.model.Comments;
import com.niit.collaboration.model.User;

@RestController
public class BlogRestService {

	private static Logger log = LoggerFactory.getLogger(BlogRestService.class);
	
	@Autowired
	private Blog blog;
	
	@Autowired
	private BlogDAO blogDAO;

	
	@Autowired
	private CommentDAO commentDAO;
	
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
		
		log.info("**************** Id of the Blog is " + blog.getId());
		log.debug("**************Ending of the method getBlogByID");
	  return	new ResponseEntity<Blog>(blog , HttpStatus.OK);
	}
	
	@PostMapping("/createblog/")
	public Blog createBlog(@RequestBody Blog newBlog)
	{
		log.debug("Calling createBlog method ");
		//before creating user, check whether the id exist in the db or not
		
		blog = blogDAO.get(newBlog.getId());
		if( blog ==null)
		{
			log.debug("User does not exist...trying to create new user");
			//id does not exist in the db
			
			//blog.setDateTime(new Date());
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
	
	public Blog updateBlog(@RequestBody Blog updateBlog)
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
	
	@DeleteMapping("/deleteblog/{id}")
	public Blog deleteBlog(@PathVariable("id") String id)
	{
		
		//whether record exist with this id or not
		log.debug("DeleteBlog Method Start");
		
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
	    log.debug("DeleteBlog Method Ending");
	    return blog;
		
	}
	
	

	@GetMapping( "/acceptblog/{id}")
	public ResponseEntity<Blog> accept(@PathVariable("id") String id) {
		log.debug("Starting of the method Blogaccept");

		blog = updateStatus(id, 'A', "");
		log.debug("Ending of the method accept");
		return new ResponseEntity<Blog>(blog, HttpStatus.OK);

	}
	@GetMapping( "/rejectblog/{id}/{reason}")
	public ResponseEntity<Blog> reject(@PathVariable("id") String id, @PathVariable("reason") String reason) {
		log.debug("Starting of the method reject");

		blog = updateStatus(id, 'R', reason);
		log.debug("Ending of the method reject");
		return new ResponseEntity<Blog>(blog, HttpStatus.OK);

	}
	
	private Blog updateStatus(String id, char status, String reason) {
		log.debug("Starting of the method updateStatus");

		log.debug("status: " + status);
		blog = blogDAO.get(id);

		if (blog == null) {
			blog = new Blog();
			blog.setErrorCode("404");
			blog.setErrorMessage("Could not update the status to " + status);
		} else {

			blog.setStatus(status);
			blog.setReason(reason);
			
			blogDAO.update(blog);
			
			blog.setErrorCode("200");
			blog.setErrorMessage("Updated the status Successfully");
		}
		log.debug("Ending of the method updateStatus");
		return blog;

	}
	

@GetMapping(value = "/comments/{id}")	
	
	public ResponseEntity< List<Comments>> getComments(@PathVariable("id") String blogId) {	
		System.out.println("helooooooo.....");
		
		List<Comments> comments = this.commentDAO.getComments(blogId);
		
		return new ResponseEntity<List<Comments>>(comments, HttpStatus.OK);		
	}
@PostMapping("/addComment/")
public Comments addComment(@RequestBody Comments newComment)
{
	log.debug("Calling createComment method ");
	//before creating user, check whether the id exist in the db or not
	log.debug("blog id : " +newComment.getUser_id());
//	console.log("user id : "+comment.user_id );
//	console.log("content : "+comment.content );
	     
		commentDAO.add(newComment);
		//NLP - NullPointerException
		//Whenever you call any method/variable on null object - you will get NLP
		newComment.setErrorCode("200");
		newComment.setErrorMessage("Thank you For register in Blog.");
		
	log.debug("Endig of the  createComment method ");
	return newComment;
	
}
	
}
