package com.aspect.pqueue.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.Assert.*;

import com.aspect.pqueue.AbstractTest;
import com.aspect.pqueue.model.UserDetails;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING) // To specify the order of execution
@ActiveProfiles("test")
@Transactional
public class UserDaoTest extends AbstractTest {
	@Autowired
	private UserDao userDao;
	
	static final int normal=4, high_priority=3, VIP=2, mgmt_override=1;
	static List<UserDetails> user;
	static BigInteger highest_rank_id;
	long time;
	@Before
	public void setUp(){
		user=new ArrayList<UserDetails>();
		time=System.currentTimeMillis();
		long testid=(1);
		//Initializing normal ids
		time=time+1000;
		user.add(initialize(testid,time));
		time=time+1000;
		testid=2;
		user.add(initialize(testid,time));
		//Initializing high priority ids
		time=time+1000;
		testid=3;
		user.add(initialize(testid,time));
		time=time+1000;
		testid=6;
		user.add(initialize(testid,time));
		//Initializing VIP ids
		time=time+1000;
		testid=5;
		user.add(initialize(testid,time));
		time=time+1000;
		testid=10;
		user.add(initialize(testid,time));
		//Initializing mgmt_override ids
		time=time+1000;
		testid=15;
		user.add(initialize(testid,time));
		time=time+1000;
		testid=30;
		user.add(initialize(testid,time));
		highest_rank_id=BigInteger.valueOf(15);
				
	}
	public UserDetails initialize(long testid,long testtime){
		UserDetails newuser=new UserDetails();
		newuser.setId(BigInteger.valueOf(testid));
		newuser.setTimestamp(BigInteger.valueOf(testtime));
		return newuser;
	}
	@After
	public void tearDown(){
		
	}
	
	/*
	 * To test:
	 * List<UserDetails> getUserDetails();

	UserDetails getUserById(BigInteger id);

	UserDetails addNewUser(BigInteger id,BigInteger time);

	void resetRanks(List<UserDetails> allusers);
	
	public UserDetails getLastRank();

	void deleteUserById(BigInteger id);

	 */
	
	
	@SuppressWarnings("deprecation")
	@Test
	public void testA(){
		//addNewUser
		for(UserDetails ud:user){
			UserDetails ret=userDao.addNewUser(ud.getId(), ud.getTimestamp());
			assertNotSame(ud, null);
			ud.setPriority(ret.getPriority());
			
		}
		long curtime=time+1000;
		for(UserDetails usr:user){
			usr.setTimespent((curtime-usr.getTimestamp().longValue())/1000);
			
		}
		userDao.resetRanks(user);
	}
	
	@Test
	public void testC(){
		//getUserDetails
		List<UserDetails> gotusers=userDao.getUserDetails();
		assertEquals(gotusers.size(), user.size());
	}
	@Test
	public void testB(){
		//getUserById
		
		for(UserDetails ud:user){
			UserDetails ret=userDao.getUserById(ud.getId());
			assertEquals(ret.getId(), ud.getId());
		}
	}
	@Test
	public void testD(){
		//getLastRank
		
		List<UserDetails> gotusers=userDao.getUserDetails();
		UserDetails ud=userDao.getLastRank();
		assertEquals(ud.getId(),highest_rank_id);
	}
	
	@Test
	public void testE(){
		//deleteUserById
		
		for(UserDetails ud:user){
			BigInteger ret=userDao.deleteUserById(ud.getId());
			assertEquals(ud.getId(), ret);
		}
		
	}
	
		

}
