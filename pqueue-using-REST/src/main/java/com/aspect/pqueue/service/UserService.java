/**
 * 
 */
package com.aspect.pqueue.service;

import java.math.BigInteger;
import java.util.List;

import com.aspect.pqueue.model.UserDetails;

public interface UserService {

	public List<UserDetails> getUserDetails();

	public UserDetails addNewUser(BigInteger id,BigInteger time,long curtime);

	public UserDetails removeTop();

	public UserDetails deleteUserById(BigInteger id);

	public BigInteger getPositionById(BigInteger id);

	public Double getAvgQwaitTime(BigInteger time);

}
