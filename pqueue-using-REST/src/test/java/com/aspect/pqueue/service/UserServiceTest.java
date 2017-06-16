package com.aspect.pqueue.service;

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


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("test")
@Transactional
public class UserServiceTest extends AbstractTest{
	
	@Autowired
	private UserService userService;
	
	static final int normal=4, high_priority=3, VIP=2, mgmt_override=1;
	static List<UserDetails> user;
	static BigInteger highest_rank_id;
	int[] ids;
	long time;
	@Before
	public void setUp(){
		user=new ArrayList<UserDetails>();
		
		time=Long.parseLong("1497613391000");//  CURRENT TIME
		long testid=(1);
		//Initializing normal ids
		time=time+1000;
		user.add(initialize(testid,new Long(time).longValue()));
		time=time+1000;
		testid=2;
		user.add(initialize(testid,new Long(time).longValue()));
		//Initializing high priority ids
		time=time+1000;
		testid=3;
		user.add(initialize(testid,new Long(time).longValue()));
		time=time+1000;
		testid=6;
		user.add(initialize(testid,new Long(time).longValue()));
		//Initializing VIP ids
		time=time+1000;
		testid=5;
		user.add(initialize(testid,new Long(time).longValue()));
		time=time+1000;
		testid=10;
		user.add(initialize(testid,new Long(time).longValue()));
		//Initializing mgmt_override ids
		time=time+1000;
		testid=15;
		user.add(initialize(testid,new Long(time).longValue()));
		time=time+1000;
		testid=30;
		user.add(initialize(testid,new Long(time).longValue()));
		highest_rank_id=BigInteger.valueOf(15);
		ids= new int[]{15,30,5,3,6,1,2,10};
			
	}
	public UserDetails initialize(long testid,long testtime){
		UserDetails newuser=new UserDetails();
		newuser.setId(BigInteger.valueOf(testid));
		newuser.setTimestamp(BigInteger.valueOf(testtime));
		return newuser;
	}
	/*
	 * To test:
	public List<UserDetails> getUserDetails();

	public UserDetails addNewUser(BigInteger id,BigInteger time);

	public UserDetails removeTop();

	public UserDetails deleteUserById(BigInteger id);

	public BigInteger getPositionById(BigInteger id);

	public Double getAvgQwaitTime(BigInteger time);

	 */
	
	
	@SuppressWarnings("deprecation")
	@Test
	public void testA(){
		//addNewUser
		long curtime=time+1000;
		for(UserDetails ud:user){
			UserDetails ret=userService.addNewUser(ud.getId(), ud.getTimestamp(),curtime);
			assertNotSame("Not created",ud, null);
			ud.setPriority(ret.getPriority());
			ud.setRank(ret.getRank());
			ud.setTimespent(ret.getTimespent());
		}
		
	}
	
	@Test
	public void testB(){
		//getUserDetails
		List<UserDetails> gotusers=userService.getUserDetails();
		assertEquals(gotusers.size(), user.size());

	}
	@Test
	public void testC(){
		//getPositionById
		int i=0;
		for(UserDetails ud:user){
			BigInteger pos=userService.getPositionById(ud.getId());
			assertEquals(ud.getId(), BigInteger.valueOf(ids[pos.intValue()]));
		}
		
	}

	@Test
	public void testD(){
		//getAvgQWaitTime
		int i=0;
		time=time+2000;
		Double avg= userService.getAvgQwaitTime(BigInteger.valueOf(time));		
		Double sum=0.0;
		for(UserDetails ud:user){
			sum=sum+(time-ud.getTimestamp().doubleValue())/1000;
		}
		assertEquals(avg.doubleValue(), sum/user.size(),0.01);
	}
	@Test
	public void testE(){
		//removeTop

		UserDetails ud=userService.removeTop();
		assertEquals(ud.getId().intValue(),15);
		
	}
	

	
	@Test
	public void testF(){
		//deleteUserById
		
		for(UserDetails ud:user){
			if(ud.getId()!=highest_rank_id){
			UserDetails ret=userService.deleteUserById(ud.getId());
			assertEquals(ud.getId(), ret.getId());
			}
		}
		
	}
	
	@After
	public void tearDown(){
		
	}
	


}