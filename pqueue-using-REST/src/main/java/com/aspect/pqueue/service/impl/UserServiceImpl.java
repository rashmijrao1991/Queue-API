/**
 * 
 */
package com.aspect.pqueue.service.impl;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aspect.pqueue.dao.UserDao;
import com.aspect.pqueue.model.UserDetails;
import com.aspect.pqueue.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserDao userDao;

	public List<UserDetails> getUserDetails() {
		return userDao.getUserDetails();

	}

	@Override
	public UserDetails addNewUser(BigInteger id,BigInteger time,long curtime) {
		//Check if there's already a request by the user
		if(userDao.getUserById(id)!=null){
			return null;
		}
		
		//Update the time spent in the queue in secs
		UserDetails ud=userDao.addNewUser(id,time);
		List<UserDetails> allusers=userDao.getUserDetails();
		if(curtime==0)
		 curtime=System.currentTimeMillis();
		for(UserDetails usr:allusers){
			usr.setTimespent((curtime-usr.getTimestamp().longValue())/1000);
		}
		//Change the ranks based on the updated time spent
		userDao.resetRanks(allusers);
		return ud;
		
	}

	@Override
	public UserDetails removeTop() {
		UserDetails top=userDao.getLastRank();
		if(top==null)
			return null;
		userDao.deleteUserById(top.getId());
		
		return top;
	}

	@Override
	public UserDetails deleteUserById(BigInteger id) {
		UserDetails delUd=userDao.getUserById(id);
		if(delUd==null){
			return null;
		}
		BigInteger retid=userDao.deleteUserById(id);
		return delUd;
	}

	@Override
	public BigInteger getPositionById(BigInteger id) {
		List<UserDetails> allusers=userDao.getUserDetails();
		if(allusers.size()==0)
			return null;
		BigInteger pos=BigInteger.valueOf(0);
		BigInteger one=BigInteger.valueOf(1);
		// As getUserDetails already returns the users in the order of the rank
		for(UserDetails ud:allusers){
			if(ud.getId().equals(id)){
				return pos;
			}
			pos=pos.add(one);
		}
		return null;
	}

	@Override
	public Double getAvgQwaitTime(BigInteger curtime) {
		List<UserDetails> allusers=userDao.getUserDetails();
		Double sum=0.0;
		for(UserDetails usr:allusers){
			sum +=(curtime.doubleValue()-usr.getTimestamp().longValue())/1000;
		}
		if(allusers.size()==0)
			return 0.0;
		return sum/allusers.size();
	}

}
