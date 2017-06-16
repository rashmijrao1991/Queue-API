package com.aspect.pqueue.controller;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.aspect.pqueue.model.UserDetails;
import com.aspect.pqueue.service.UserService;

@Controller
@RequestMapping("work")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping(value = "list")
	public ResponseEntity<List<UserDetails>> userDetails() {
        
		List<UserDetails> userDetails = userService.getUserDetails();
		return new ResponseEntity<List<UserDetails>>(userDetails, HttpStatus.OK);
	}

	/*	Endpoint 1:
	 * 	An endpoint for adding a ID to queue (enqueue).
	 *  This endpoint should accept two parameters, the ID 
	 *  to enqueue and the time at which the ID was 
	 *  added to the queue.
	 * 
	 */
	
	@PostMapping(value = "addUser/{id}/{time}")
	public ResponseEntity<String> addUser(@PathVariable("id") BigInteger id,@PathVariable("time") long time) {
		if(id==null){
			return new ResponseEntity<String>("Please provide the id",HttpStatus.BAD_REQUEST); 
		}
		
		if(!validTime(time)){
			return new ResponseEntity<String>("Invalid Time format. Please provide the Epoch time in milliseconds",HttpStatus.BAD_REQUEST); 
		}
		
		UserDetails userDetails = userService.addNewUser(id,BigInteger.valueOf(time),0);
		if(userDetails==null)
			return new ResponseEntity<String>("Error inserting",HttpStatus.BAD_REQUEST);
		return new ResponseEntity<String>("Success",HttpStatus.OK);
	}

	private boolean validTime(Long time) {
		if(time.toString().length()!=13 || time>System.currentTimeMillis())
			return false;
		else
			return true;
		
	}
	
	/*
	 * 	Endpoint 2:
	 * 	An endpoint for getting the top ID from 
	 * 	the queue and removing it (de- queue). 
	 * 	This endpoint should return the highest ranked ID and 
	 * 	the time it was entered into the queue.
	 */
	
	@DeleteMapping(value = "getTop")
	public ResponseEntity<Map<String,BigInteger>> deQueueTop() {
		Map<String,BigInteger> response=new HashMap<String,BigInteger>();
		UserDetails userDetails = userService.removeTop();
		if(userDetails==null)
			return new ResponseEntity<Map<String,BigInteger>>(response,HttpStatus.BAD_REQUEST);
		
		response.put("highest_id", userDetails.getId());
		response.put("entry_time", userDetails.getTimestamp());
		return new ResponseEntity<Map<String,BigInteger>>(response,HttpStatus.OK);
	}

	/* Endpoint 3:
	 * An endpoint for getting the list of IDs in the queue. 
	 * This endpoint should return a list of IDs sorted from 
	 * highest ranked to lowest.
	 */
	@GetMapping(value = "getIds")
	public ResponseEntity<List<BigInteger>> getAllIds() {
		List<BigInteger> response=new ArrayList<BigInteger>();
		List<UserDetails> userDetails = userService.getUserDetails();
		
		for(UserDetails ud:userDetails){
			response.add(ud.getId());
		}
		return new ResponseEntity<List<BigInteger>>(response,HttpStatus.OK);
	}

	/*
	 * Endpoint 4:
	 * An endpoint for removing a specific ID from the queue. 
	 * This endpoint should accept a single parameter, the ID to remove.
	 * 
	 */
	@DeleteMapping(value = "/deleteUser/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable("id") BigInteger id) {
		UserDetails deleteUd = userService.deleteUserById(id);
		if(deleteUd==null)
			return new ResponseEntity<String>("Unable to delete User",HttpStatus.BAD_REQUEST);
		return new ResponseEntity<String>("Successfully deleted",HttpStatus.OK);
	}
	
	/*
	 * Endpoint 5:
	 * An endpoint to get the position of a specific ID in the queue.
	 * This endpoint should accept one parameter, the ID to get the position of.
	 * It should return the position of the ID in the queue indexed from 0.
	 */
	@GetMapping(value = "/getPosition/{id}")
	public ResponseEntity<BigInteger> getPosition(@PathVariable("id") BigInteger id) {
		BigInteger pos = userService.getPositionById(id);
		if(pos==null)
			return new ResponseEntity<BigInteger>(BigInteger.valueOf(-1),HttpStatus.BAD_REQUEST);
		return new ResponseEntity<BigInteger>(pos,HttpStatus.OK);
	}
	
	
	/*
	 * Endpoint 6:
	 * An endpoint to get the average wait time. 
	 * This endpoint should accept a single parameter, 
	 * the current time, and should return the average (mean) 
	 * number of seconds that each ID has been waiting in the queue.
	 * 
	 */
	@GetMapping(value = "/getAvgWait/{time}")
	public ResponseEntity<Double> getAvgWait(@PathVariable("time") BigInteger time) {
		Double avg = userService.getAvgQwaitTime(time);
		return new ResponseEntity<Double>(avg,HttpStatus.OK);
	}
		

}
