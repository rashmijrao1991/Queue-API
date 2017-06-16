package com.aspect.pqueue.dao;

import java.math.BigInteger;
import java.util.List;

import com.aspect.pqueue.model.UserDetails;

public interface UserDao {
	
	List<UserDetails> getUserDetails();

	UserDetails getUserById(BigInteger id);

	UserDetails addNewUser(BigInteger id,BigInteger time);

	void resetRanks(List<UserDetails> allusers);
	
	public UserDetails getLastRank();

	BigInteger deleteUserById(BigInteger id);


}
