package mat.dao.impl;

import java.util.ArrayList;
import java.util.List;
import mat.dao.search.GenericDAO;
import mat.model.Organization;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
public class OrganizationDAO extends GenericDAO<Organization, Long> implements
mat.dao.OrganizationDAO {
	@Override
	public int countSearchResults(String text) {
		Criteria criteria = createSearchCriteria(text);
		criteria.setProjection(Projections.rowCount());
		return ((Long) criteria.uniqueResult()).intValue();
	}
	/**
	 * Creates the search criteria.
	 * 
	 * @param text
	 *            the text
	 * @return the criteria
	 */
	private Criteria createSearchCriteria(String text) {
		Session session = getSessionFactory().getCurrentSession();
		Criteria criteria = session.createCriteria(Organization.class);
		criteria.add(Restrictions.ilike("organizationName", "%" + text + "%"));
		return criteria;
	}
	@Override
	public Organization findByOid(String oid) {
		Session session = getSessionFactory().getCurrentSession();
		Criteria criteria = session.createCriteria(Organization.class);
		criteria.add(Restrictions.eq("organizationOID", oid));
		if (criteria.list().size() != 0) {
			return (Organization) criteria.list().get(0);
		} else {
			return null;
		}
	}
	/* (non-Javadoc)
	 * @see mat.dao.OrganizationDAO#getAllOrganizations()
	 */
	@Override
	public List<Organization> getAllOrganizations() {
		List<Organization> organizations = new ArrayList<Organization>();
		Session session = getSessionFactory().getCurrentSession();
		Criteria criteria = session.createCriteria(Organization.class);
		if (!criteria.list().isEmpty()) {
			organizations = criteria.list();
		}
		return organizations;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see mat.dao.OrganizationDAO#saveOrganization(mat.model.Organization)
	 */
	@Override
	public void saveOrganization(Organization entity) {
		Session session = null;
		Transaction transaction = null;
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			session.saveOrUpdate(entity);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rollbackUncommitted(transaction);
			closeSession(session);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see mat.dao.OrganizationDAO#searchOrganization(java.lang.String, int, int)
	 */
	@Override
	public List<Organization> searchOrganization(String name, int startIndex, int numResults) {
		Criteria criteria = createSearchCriteria(name);
		criteria.addOrder(Order.asc("organizationName"));
		criteria.setFirstResult(startIndex);
		if (numResults > 0) {
			criteria.setMaxResults(numResults);
		}
		return criteria.list();
	}
}
