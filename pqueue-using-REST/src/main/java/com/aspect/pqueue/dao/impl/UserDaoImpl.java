package com.aspect.pqueue.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.transaction.Transactional;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aspect.pqueue.dao.UserDao;
import com.aspect.pqueue.model.UserDetails;

@Component
@Transactional
public class UserDaoImpl implements UserDao {

	@Autowired
	private SessionFactory sessionFactory;
	static final int normal=4, high_priority=3, VIP=2, mgmt_override=1;
	static final BigInteger three=BigInteger.valueOf(3),five=BigInteger.valueOf(5),zero=BigInteger.valueOf(0);

	public List<UserDetails> getUserDetails() {
		// Returns list of all the users in the descending order of their rank
		Session sess=sessionFactory.openSession();
		Transaction tx = null;
		UserDetails last=null;
		sess.getTransaction().begin();
		List<UserDetails> ud = sess.createCriteria(UserDetails.class).addOrder(Order.desc("rank")).list();
		
		sess.getTransaction().commit();
		sess.close();
		return ud;
	
	}

	@Override
	public UserDetails getUserById(BigInteger id) {
		/*// TODO Auto-generated method stub
		Criteria criteria = sessionFactory.openSession().createCriteria(UserDetails.class).add(Restrictions.eq("id", id));
		//return criteria.list()
		return (UserDetails)criteria.uniqueResult();*/
		Session sess=sessionFactory.openSession();
		Transaction tx = null;
		UserDetails last=null;
		try {
		    sess.getTransaction().begin();
	        String sql = "FROM UserDetails where id=?";
	        Object temp=sess.createQuery(sql).setParameter(0, id).uniqueResult();
		    if(temp!=null){
		    	
		    	last=(UserDetails)temp;
	
		    }
	        sess.getTransaction().commit();
		}
		catch (RuntimeException e) {
		    sess.getTransaction().rollback();
		    throw e; // or display error message
		}
		finally {
		    sess.close();
		} 
		 
			return last;
		

	}

	@Override
	public UserDetails addNewUser(BigInteger id,BigInteger time) {
		UserDetails pq=new UserDetails();
		Session sess=sessionFactory.openSession();
	      Transaction tx = null;
			pq.setId(id);
			pq.setTimestamp(time);
			//Setting the priority
			int priority_class=getPriority(id);
			pq.setPriority(priority_class);
			pq.setTimespent(0);
						
			try {
			    sess.getTransaction().begin();
			    sess.save(pq);			    
			    sess.getTransaction().commit();
			}
			catch (RuntimeException e) {
			    sess.getTransaction().rollback();
			    throw e; // or display error message
			}
			finally {
			    sess.close();
			}
				
	      return pq;

	}
	
	private int getPriority(BigInteger id){
		/*	IDs that are evenly divisible by 3 are priority IDs.
		 *	IDs that are evenly divisible by 5 are VIP IDs.
		 *	IDs that are evenly divisible by both 3 and 5 are management override IDs.
		 *	IDs that are not evenly divisible by 3 or 5 are normal IDs.
		 */
		if(id.mod(three)==zero && id.mod(five)==zero){
			return mgmt_override;
		}
		else if(id.mod(three)==zero){
			return high_priority;
		}
		else if(id.mod(five)==zero){
			return VIP;
		}
		else{
			return normal;
		}
	}

	@Override
	public UserDetails getLastRank(){
		//Gets the record with the highest rank
		Session sess=sessionFactory.openSession();
		Transaction tx = null;
		UserDetails last=null;
		try {
		    sess.getTransaction().begin();
	        String sql = "FROM UserDetails ORDER BY rank DESC";
	        Object temp=sess.createQuery(sql).setMaxResults(1).uniqueResult();
		    if(temp!=null){
		    	
		    	last=(UserDetails)temp;
	
		    }
	        sess.getTransaction().commit();
		}
		catch (RuntimeException e) {
		    sess.getTransaction().rollback();
		    throw e;
		}
		finally {
		    sess.close();
		} 
		 
			return last;
		
		
	}
	

	@Override
	public void resetRanks(List<UserDetails> allusers) {
		//Once any new user is added, the ranks of all are revised based on the time they've spent in the queue.
		//Special care is taken for mgmt_override as its ranking should be higher than all. So initially the ranks of all 
		//non mgmt_override is set
		Session sess=sessionFactory.openSession();
		UserDetails last=null;
		Transaction tx = null;
		try {
		sess.getTransaction().begin();
		double hrank=0;
		for(UserDetails curr:allusers){
			if(curr.getPriority()!=mgmt_override){
			 curr.setRank(findRank(curr));
			 sess.update(curr);	
			 if(hrank<curr.getRank())
				 hrank=curr.getRank();
			}
			
		}
		// Sorting the records in the ascending order helps to set the rank of the  mgmt_override class 
		Collections.sort(allusers, new Comparator<UserDetails>() {
	        @Override
	        public int compare(UserDetails o1, UserDetails o2) {
	            return Double.compare(o1.getTimespent(), o2.getTimespent());
	        }
	    });
		for(UserDetails curr:allusers){
			if(curr.getPriority()==mgmt_override){
			 curr.setRank(++hrank);
			 sess.update(curr);	
			}
		}
		
		sess.getTransaction().commit();
		}
		catch (RuntimeException e) {
		    sess.getTransaction().rollback();
		    throw e;
		}
		finally {
		    sess.close();
		} 
			
	}

	private double findRank(UserDetails curr) {
		int p_class=curr.getPriority();
		long tspent;
		switch(p_class){
		case normal:
			/*
			 *  Normal IDs are given a rank equal to 
			 *  the number of seconds they’ve been in the queue.
			 */
			return curr.getTimespent();
			
		case high_priority:
			/*
			 * Priority IDs are given a rank equal to the result
			 * of applying the fol- lowing formula to the number of 
			 * seconds they’ve been in the queue: max(3, n log n)
			 */
			tspent=curr.getTimespent();
			if(curr.getTimespent()==0)
				return 3;
			return Math.max(3, tspent*Math.log(tspent));
		case VIP:
			/*
			 * VIP IDs are given a rank equal to the result of 
			 * applying the following formula to the number of 
			 * seconds they’ve been in the queue: max(4, 2n log n)
			 */
			
			tspent=curr.getTimespent();
			if(curr.getTimespent()==0)
				return 4;
			return Math.max(4, 2*tspent*Math.log(tspent));
		default:
			return 0;
			
		}
		
	}

	@Override
	public BigInteger deleteUserById(BigInteger id) {
		
		Session sess=sessionFactory.openSession();
		Transaction tx = null;
		UserDetails last=null;
		sess.getTransaction().begin();
		UserDetails ud = (UserDetails ) sess.createCriteria(UserDetails.class)
                .add(Restrictions.eq("id", id)).uniqueResult();
		
		sess.delete(ud);
		sess.getTransaction().commit();
		sess.close();
		return id;
	}


	
	

}
