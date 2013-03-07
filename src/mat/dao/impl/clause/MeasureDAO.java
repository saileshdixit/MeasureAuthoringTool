package mat.dao.impl.clause;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import mat.client.measure.ManageMeasureDetailModel;
import mat.client.measure.MeasureSearchFilterPanel;
import mat.dao.MetadataDAO;
import mat.dao.QualityDataSetDAO;
import mat.dao.UserDAO;
import mat.dao.search.GenericDAO;
import mat.dao.service.DAOService;
import mat.model.LockedUserInfo;
import mat.model.SecurityRole;
import mat.model.User;
import mat.model.clause.Clause;
import mat.model.clause.Measure;
import mat.model.clause.MeasureSet;
import mat.model.clause.MeasureShare;
import mat.model.clause.MeasureShareDTO;
import mat.model.clause.Metadata;
import mat.model.clause.ShareLevel;
import mat.server.LoggedInUserUtil;
import mat.shared.ConstantMessages;
import mat.shared.StringUtility;
import mat.shared.model.Decision;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.ApplicationContext;

public class MeasureDAO extends GenericDAO<Measure, String> implements mat.dao.clause.MeasureDAO {
	private static final Log logger = LogFactory.getLog(MeasureDAO.class);
	private final long lockThreshold = 3*60*1000; //3 minutes   
	
	private DAOService dAOService = null;
	private ApplicationContext context = null;
	
	public MeasureDAO () {
		
	}
	
	public MeasureDAO (DAOService dAOService) {
		//allow to test using DAOService
		this.dAOService = dAOService;
	}
	
	public MeasureDAO(ApplicationContext context) {
		this.context = context;
	}
	
	private Measure cloner(ManageMeasureDetailModel currentDetails, ApplicationContext context, Measure m) {
		
		if (m==null) return null;
		
		Measure cloned = new Measure();
		//new measure ID
		//cloned.setId(UUID.randomUUID().toString());//auto generated by hbm file
		if (m.getaBBRName()!=null) cloned.setaBBRName(currentDetails.getShortName());
		//if (m.getClauses()!=null) cloned.setClauses(m.getClauses());
		if (m.getDescription()!=null) cloned.setDescription(currentDetails.getName());
		if (m.getMeasureStatus()!=null) cloned.setMeasureStatus(m.getMeasureStatus());
		if (m.getOwner()!=null) cloned.setOwner(m.getOwner());
		//if (m.getShares()!=null) cloned.setShares(m.getShares());
		if (m.getVersion()!=null) cloned.setVersion(m.getVersion());
		cloned.setDraft(true);
		if(currentDetails.getMeasScoring() != null) cloned.setMeasureScoring(currentDetails.getMeasScoring());
		
		return cloned;
	}
	
	public MeasureShareDTO clone(ManageMeasureDetailModel currentDetails, String loggedinUserId, boolean creatingDraft,ApplicationContext context) {
		
		ClauseManagerDAO cm = null;
		QualityDataSetDAO qdsDAO = null;
		mat.dao.clause.MeasureDAO mDAO = null;
		mat.dao.clause.ClauseDAO cDAO = null;
		UserDAO uDAO = null;
		mat.dao.clause.MeasureSetDAO mSetDAO = null;
		List<Metadata> mdl = null;
		List<Metadata> cmdl = new ArrayList<Metadata>();
		MetadataDAO mdDAO = null;
		
		Measure originalMeasure = null;
		Measure cloned = null;
		
		if (dAOService!=null) {
			originalMeasure = dAOService.getMeasureDAO().find(currentDetails.getId());
			mDAO = dAOService.getMeasureDAO();
			qdsDAO = dAOService.getQualityDataSetDAO();
			cDAO = dAOService.getClauseDAO();
			cm = new ClauseManagerDAO(dAOService);
			uDAO = dAOService.getUserDAO();
			mSetDAO = dAOService.getMeasureSetDAO();
			mdDAO = dAOService.getMetadataDAO();
		} else {
			originalMeasure = ((mat.dao.clause.MeasureDAO)context.getBean("measureDAO")).find(currentDetails.getId());
			mDAO = (mat.dao.clause.MeasureDAO)context.getBean("measureDAO");
			qdsDAO = (QualityDataSetDAO)context.getBean("qualityDataSetDAO");
			cDAO = (mat.dao.clause.ClauseDAO)context.getBean("clauseDAO");
			cm = new ClauseManagerDAO(context);
			uDAO = (UserDAO) context.getBean("userDAO");
			mSetDAO = (mat.dao.clause.MeasureSetDAO) context.getBean("measureSetDAO");
			mdDAO =   (MetadataDAO)context.getBean("metadataDAO");
			
		}

		//cloning measure only 
		cloned = cloner(currentDetails, context, originalMeasure);
		
		//setting new measure owner for clone and original measure owner for draft
		User cloneOwner = null;
		if(creatingDraft){
			cloned.seteMeasureId(originalMeasure.geteMeasureId());
			cloneOwner = originalMeasure.getOwner();
		}else{
			cloneOwner = uDAO.find(loggedinUserId);
		}
		
		cloned.setOwner(cloneOwner);
	
		if(creatingDraft){
			//When creating draft of existing version, the measureset is same as OriginalMeasure.
			cloned.setMeasureSet(originalMeasure.getMeasureSet());
			
			//get the list of metadata
			
			mdl =mdDAO.getMeasureDetails(originalMeasure.getId());
			
			for(Metadata m: mdl){
				Metadata mCopy = new Metadata();
				mCopy.setMeasure(cloned);
				mCopy.setName(m.getName());
				mCopy.setValue(m.getValue());
				cmdl.add(mCopy);
//				m.setMeasure(cloned);
			}
		}else{
			//When creating a clone.New Measure-set needs to be created.
			MeasureSet mSet = new MeasureSet();
			mSet.setId(UUID.randomUUID().toString());
			mSetDAO.save(mSet);
			cloned.setMeasureSet(mSet);
		}
		//this will insert a new row in measure table
		mDAO.save(cloned);
		
		//clone Quality Data Set
		qdsDAO.cloneQDSElements(currentDetails.getId(), cloned);
		
		//get measure phrases
		List<Clause> clauseList = cDAO.findByMeasureId(currentDetails.getId(), null);
		HashMap<String, Clause> clauseMap = new HashMap<String,Clause>();
		
		for (Clause clause : clauseList) {
			System.out.println(clause.getName());
			//do not care about system clauses
			if(clause.getContextId().equalsIgnoreCase(ConstantMessages.MEASURE_PHRASE_CONTEXT_ID)){
				clauseMap.put(clause.getName(), clause);
			}
		}
		
		//get priority list clause names: these need to be processed first... children first then parents
		ArrayList<String> clauseNames = new ArrayList<String>();
		for (Clause clause : clauseList) {
			if(clause.getContextId().equalsIgnoreCase(ConstantMessages.MEASURE_PHRASE_CONTEXT_ID)){
				Decision d = cm.loadClause(clause.getDecision().getId());
				cm.addAllUnique(clauseNames,cm.getDependencyTree(d));
			}
		}
		
			
		for(int i = clauseNames.size()-1; i>=0; i--){
			String clauseName = clauseNames.get(i);
			Clause clause = clauseMap.get(clauseName);
			processClause(clause, cm, cloned, currentDetails.getShortName());
			//do not process clause later
			clauseList.remove(clause);
		}
		
		//clone clause, terms, and attributes
		
		//process phrases first
		for (Clause clause : clauseList) {
			if(clause.getContextId().equalsIgnoreCase(ConstantMessages.MEASURE_PHRASE_CONTEXT_ID)){
				processClause(clause, cm, cloned, currentDetails.getShortName());
			}
		}	
		//process system clauses last
		for (Clause clause : clauseList) {
			if(!clause.getContextId().equalsIgnoreCase(ConstantMessages.MEASURE_PHRASE_CONTEXT_ID)){
				processClause(clause, cm, cloned, currentDetails.getShortName());
			}
		}
		if(creatingDraft){
			//Probably need to have saved the new Measure before committing the metadata for it.
			mdDAO.batchSave(cmdl);
		}
		
		clauseList.clear();
		clauseMap.clear();
		clauseList = null;
		clauseMap = null;
		clauseNames.clear();
		clauseNames = null;
		
		return extractDTOFromMeasure(cloned);
	}
	
	private void processClause(Clause clause, ClauseManagerDAO cm, Measure cloned, String newClonedMeasureName){
		Decision d = cm.loadClause(clause.getDecision().getId());
		
		Clause c = (Clause) d;
		List<Decision> listOfDecisions = c.getDecisions();
		if (listOfDecisions!=null && !listOfDecisions.isEmpty()) {
			c.setName(clause.getName());
			c.setContextId(clause.getContextId());
			cm.cloneClause(c, cloned, newClonedMeasureName, false, true, true);
		}
	}
	
	public void saveMeasure(Measure measure) {
		if (dAOService!=null) {
			//allow to test using DAOService
			dAOService.getMeasureDAO().save(measure);
			
			ClauseManagerDAO cm = new ClauseManagerDAO(dAOService);
			String id = measure.getId();
			for (Clause clause : measure.getClauses()) {
				clause.setMeasureId(id);
				dAOService.getClauseDAO().save(clause);
				cm.saveClause(clause);
			}
			
		} else {
			super.save(measure);
			ClauseManagerDAO cm = new ClauseManagerDAO(context);
			
			for (Clause clause : measure.getClauses()) {
				ClauseDAO clauseDAO = new ClauseDAO();
				clauseDAO.save(clause);				
				cm.saveClause(clause);
			}
		}
	}

	@Override
	public int countUsersForMeasureShare() {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(User.class);
		criteria.add(Restrictions.eq("securityRole.id", "3"));
		criteria.add(Restrictions.ne("id", LoggedInUserUtil.getLoggedInUser()));
		criteria.setProjection(Projections.rowCount());
		return ((Long)criteria.uniqueResult()).intValue();
	}

	/**
	 * This method returns a List of MeasureShareDTO objects which have userId,firstname,lastname
	 * and sharelevel for the given measureId.
	 */
	@Override
	public List<MeasureShareDTO> getMeasureShareInfoForMeasure(
			String measureId, int startIndex, int pageSize) {
		Criteria userCriteria = getSessionFactory().getCurrentSession().createCriteria(User.class);
		userCriteria.add(Restrictions.eq("securityRole.id", "3"));
		userCriteria.add(Restrictions.ne("id", LoggedInUserUtil.getLoggedInUser()));
		userCriteria.setFirstResult(startIndex);
		//userCriteria.setMaxResults(pageSize);
		userCriteria.addOrder(Order.asc("lastName"));
		
		List<User> userResults = userCriteria.list();
		HashMap<String,MeasureShareDTO> userIdDTOMap = new HashMap<String,MeasureShareDTO>();
		ArrayList<MeasureShareDTO> orderedDTOList = new ArrayList<MeasureShareDTO>();
		List<MeasureShareDTO> dtoList = new ArrayList<MeasureShareDTO>();
		for(User user : userResults) {
			MeasureShareDTO dto = new MeasureShareDTO();
			dtoList.add(dto);
			dto.setUserId(user.getId());
			dto.setFirstName(user.getFirstName());
			dto.setLastName(user.getLastName());
			dto.setOrganizationName(user.getOrganizationName());
			userIdDTOMap.put(user.getId(), dto);
			orderedDTOList.add(dto);
		}
		
		if(dtoList.size() > 0) {
			Criteria shareCriteria = getSessionFactory().getCurrentSession().createCriteria(MeasureShare.class);
			shareCriteria.add(Restrictions.in("shareUser.id", userIdDTOMap.keySet()));
			shareCriteria.add(Restrictions.eq("measure.id", measureId));
			List<MeasureShare> shareList = shareCriteria.list();
			for(MeasureShare share : shareList) {
				User shareUser = share.getShareUser();
				MeasureShareDTO dto = userIdDTOMap.get(shareUser.getId());
				dto.setShareLevel(share.getShareLevel().getId());
			}
		}
		if(pageSize < orderedDTOList.size())
			return orderedDTOList.subList(0, pageSize);
		else
			return orderedDTOList;
	}
	
	
	@Override
	public List<MeasureShare> getMeasureShareForMeasure(String measureId) {
		List<MeasureShare> measureShare = new ArrayList<MeasureShare>();
		if(measureId==null)
			return null;
		Criteria shareCriteria = getSessionFactory().getCurrentSession().createCriteria(MeasureShare.class);
		shareCriteria.add(Restrictions.eq("measure.id", measureId));
		measureShare = shareCriteria.list();
		return measureShare;
	}
	
	
	private Criteria buildMeasureShareForUserCriteria(User user) {
		Criteria mCriteria = getSessionFactory().getCurrentSession().createCriteria(Measure.class);
		/*if(user.getSecurityRole().getId().equals("3")) { 
			mCriteria.add(Restrictions.or(Restrictions.eq("owner.id", user.getId()),
					Restrictions.eq("share.shareUser.id", user.getId())));
			mCriteria.createAlias("shares", "share", Criteria.LEFT_JOIN);
		}*/
		
		mCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return mCriteria;
	}
	
	
	private Criteria buildMeasureShareForUserCriteriaWithFilter(User user, int filter) {
		Criteria mCriteria = getSessionFactory().getCurrentSession().createCriteria(Measure.class);
		if(filter == MeasureSearchFilterPanel.MY_MEASURES){
			mCriteria.add(Restrictions.or(Restrictions.eq("owner.id", user.getId()),
					Restrictions.eq("share.shareUser.id", user.getId())));
			mCriteria.createAlias("shares", "share", Criteria.LEFT_JOIN);
		}
		
		mCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return mCriteria;
	}
	
	
	@Override
	public int countMeasureShareInfoForUser(User user) {
		Criteria mCriteria = buildMeasureShareForUserCriteria(user);
		List<Measure> measureList = mCriteria.list();
		measureList = getAllMeasuresInSet(measureList);
		return measureList.size();
	}
	
	@Override
	public int countMeasureForVersion(User user) {
		List<MeasureShareDTO> dtoList = getMeasuresForVersion(user);
		return dtoList.size();
	}
	
	@Override
	public int countMeasureForDraft(User user) {
		List<MeasureShareDTO> dtoList = getMeasuresForDraft(user);
		return dtoList.size();
	}
	
	@Override
	public List<MeasureShareDTO> getMeasureShareInfoForUser(User user, int startIndex, int pageSize) {
		return getMeasureShareInfoForUser("", null, user, startIndex, pageSize);
	}
	
	private MeasureShareDTO extractDTOFromMeasure(Measure measure) {
		MeasureShareDTO dto = new MeasureShareDTO();
		
		dto.setMeasureId(measure.getId());
		dto.setMeasureName(measure.getDescription());
		dto.setScoringType(measure.getMeasureScoring());
		dto.setShortName(measure.getaBBRName());
		dto.setStatus(measure.getMeasureStatus());
		dto.setPackaged(measure.getExportedDate() != null);
		dto.setOwnerUserId(measure.getOwner().getId());
		
		/*US501*/
		dto.setDraft(measure.isDraft());
		dto.setVersion(measure.getVersion());
		dto.setFinalizedDate(measure.getFinalizedDate());
		dto.setMeasureSetId(measure.getMeasureSet().getId());

		boolean isLocked = isLocked(measure.getLockedOutDate());
		dto.setLocked(isLocked);
		if(isLocked && measure.getLockedUser() != null){
			LockedUserInfo lockedUserInfo = new LockedUserInfo();
			lockedUserInfo.setUserId(measure.getLockedUser().getId());
			lockedUserInfo.setEmailAddress(measure.getLockedUser().getEmailAddress());
			lockedUserInfo.setFirstName(measure.getLockedUser().getFirstName());
			lockedUserInfo.setLastName(measure.getLockedUser().getLastName());
			dto.setLockedUserInfo(lockedUserInfo);
		}
		return dto;
	}
	
	/**
	 * 
	 * @param lockedOutDate
	 * @return false if current time - lockedOutDate < the lock threshold
	 */
	private boolean isLocked(Date lockedOutDate){
		
		boolean locked = false;
		if(lockedOutDate == null){
			return locked;
		}
		
		long currentTime = System.currentTimeMillis();
		long lockedOutTime = lockedOutDate.getTime();
		long timeDiff = currentTime - lockedOutTime;
		locked = timeDiff < lockThreshold;

		return locked;
	}
	
	public java.util.List<Measure> findByOwnerId(String measureOwnerId) {
		Session session = getSessionFactory().getCurrentSession();
		Criteria criteria = session.createCriteria(Measure.class);
		criteria.add(Restrictions.eq("owner.id", measureOwnerId));
		return criteria.list();
	}
	
	
	
	@Override
	public List<MeasureShareDTO> getMeasureShareInfoForUser(String searchText, mat.dao.MetadataDAO metadataDAO,
			User user, int startIndex, int pageSize) {
	
		String searchTextLC = searchText.toLowerCase().trim();
		
		Criteria mCriteria = buildMeasureShareForUserCriteria(user);
		mCriteria.addOrder(Order.desc("measureSet.id")).addOrder(Order.desc("draft")).addOrder(Order.desc("version"));
		mCriteria.setFirstResult(startIndex);
		
		Map<String, MeasureShareDTO> measureIdDTOMap = new HashMap<String, MeasureShareDTO>();
		ArrayList<MeasureShareDTO> orderedDTOList = new ArrayList<MeasureShareDTO>();
		List<Measure> measureResultList = mCriteria.list();
		
		if(!user.getSecurityRole().getId().equals("2")) { 
			measureResultList = getAllMeasuresInSet(measureResultList);
		}
		measureResultList = sortMeasureList(measureResultList);
		
		StringUtility su = new StringUtility();
		for(Measure measure : measureResultList) {
			
			boolean matchesSearch = su.isEmptyOrNull(searchTextLC) ? true : 
				//measure name
				measure.getDescription().toLowerCase().contains(searchTextLC) ? true :
					//abbreviated measure name
					measure.getaBBRName().toLowerCase().contains(searchTextLC) ? true :
						//measure owner first name
						measure.getOwner().getFirstName().toLowerCase().contains(searchTextLC) ? true :
							//measure owner last name
							measure.getOwner().getLastName().toLowerCase().contains(searchTextLC) ? true :
								false;
			
			//measure steward (only check if necessary)
			if(!matchesSearch && !su.isEmptyOrNull(searchTextLC)){
				List<Metadata> mdList = metadataDAO.getMeasureDetails(measure.getId(), "MeasureSteward");
				for(Metadata md : mdList)
					if(md.getName().equalsIgnoreCase("MeasureSteward") && md.getValue().toLowerCase().contains(searchTextLC)){
						matchesSearch = true;
						break;
					}
			}
			
			if(matchesSearch){
				MeasureShareDTO dto = extractDTOFromMeasure(measure);
				measureIdDTOMap.put(measure.getId(), dto);
				orderedDTOList.add(dto);
			}
		}
		
		if(orderedDTOList.size() > 0) {
			Criteria shareCriteria = getSessionFactory().getCurrentSession().createCriteria(MeasureShare.class);
			shareCriteria.add(Restrictions.eq("shareUser.id", user.getId()));
			shareCriteria.add(Restrictions.in("measure.id", measureIdDTOMap.keySet()));
			List<MeasureShare> shareList = shareCriteria.list();
			//get share level for each measure set and set it on each dto
			HashMap<String, String> measureSetIdToShareLevel = new HashMap<String,String>();
			for(MeasureShare share : shareList) {
				String msid = share.getMeasure().getMeasureSet().getId();
				String shareLevel = share.getShareLevel().getId();
				
				String existingShareLevel = measureSetIdToShareLevel.get(msid);
				if(existingShareLevel == null || ShareLevel.VIEW_ONLY_ID.equals(existingShareLevel))				
					measureSetIdToShareLevel.put(msid, shareLevel);
			}
			for(MeasureShareDTO dto : orderedDTOList){
				String msid = dto.getMeasureSetId();
				String shareLevel = measureSetIdToShareLevel.get(msid);
				if(shareLevel != null){
					dto.setShareLevel(shareLevel);
				}
			}
		}
		if(pageSize < orderedDTOList.size())
			return orderedDTOList.subList(0, pageSize);
		else
			return orderedDTOList;
	}
	
	@Override
	public List<MeasureShareDTO> getMeasureShareInfoForUserWithFilter(String searchText, mat.dao.MetadataDAO metadataDAO,
			User user, int startIndex, int pageSize ,int filter) {
	
		String searchTextLC = searchText.toLowerCase().trim();
		
		Criteria mCriteria = buildMeasureShareForUserCriteriaWithFilter(user, filter);
		
		mCriteria.addOrder(Order.desc("measureSet.id")).addOrder(Order.desc("draft")).addOrder(Order.desc("version"));
		mCriteria.setFirstResult(startIndex);
		
		Map<String, MeasureShareDTO> measureIdDTOMap = new HashMap<String, MeasureShareDTO>();
		ArrayList<MeasureShareDTO> orderedDTOList = new ArrayList<MeasureShareDTO>();
		List<Measure> measureResultList = mCriteria.list();
		
		if(!user.getSecurityRole().getId().equals("2")) { 
			measureResultList = getAllMeasuresInSet(measureResultList);
		}
		measureResultList = sortMeasureList(measureResultList);
		
		StringUtility su = new StringUtility();
		for(Measure measure : measureResultList) {
			
			boolean matchesSearch = su.isEmptyOrNull(searchTextLC) ? true : 
				//measure name
				measure.getDescription().toLowerCase().contains(searchTextLC) ? true :
					//abbreviated measure name
					measure.getaBBRName().toLowerCase().contains(searchTextLC) ? true :
						//measure owner first name
						measure.getOwner().getFirstName().toLowerCase().contains(searchTextLC) ? true :
							//measure owner last name
							measure.getOwner().getLastName().toLowerCase().contains(searchTextLC) ? true :
								false;
			
			//measure steward (only check if necessary)
			if(!matchesSearch && !su.isEmptyOrNull(searchTextLC)){
				List<Metadata> mdList = metadataDAO.getMeasureDetails(measure.getId(), "MeasureSteward");
				for(Metadata md : mdList)
					if(md.getName().equalsIgnoreCase("MeasureSteward") && md.getValue().toLowerCase().contains(searchTextLC)){
						matchesSearch = true;
						break;
					}
			}
			
			if(matchesSearch){
				MeasureShareDTO dto = extractDTOFromMeasure(measure);
				measureIdDTOMap.put(measure.getId(), dto);
				orderedDTOList.add(dto);
			}
		}
		
		if(orderedDTOList.size() > 0) {
			Criteria shareCriteria = getSessionFactory().getCurrentSession().createCriteria(MeasureShare.class);
			shareCriteria.add(Restrictions.eq("shareUser.id", user.getId()));
			shareCriteria.add(Restrictions.in("measure.id", measureIdDTOMap.keySet()));
			List<MeasureShare> shareList = shareCriteria.list();
			//get share level for each measure set and set it on each dto
			HashMap<String, String> measureSetIdToShareLevel = new HashMap<String,String>();
			for(MeasureShare share : shareList) {
				String msid = share.getMeasure().getMeasureSet().getId();
				String shareLevel = share.getShareLevel().getId();
				
				String existingShareLevel = measureSetIdToShareLevel.get(msid);
				if(existingShareLevel == null || ShareLevel.VIEW_ONLY_ID.equals(existingShareLevel))				
					measureSetIdToShareLevel.put(msid, shareLevel);
			}
			for(MeasureShareDTO dto : orderedDTOList){
				String msid = dto.getMeasureSetId();
				String shareLevel = measureSetIdToShareLevel.get(msid);
				if(shareLevel != null){
					dto.setShareLevel(shareLevel);
				}
			}
		}
		if(pageSize < orderedDTOList.size())
			return orderedDTOList.subList(0, pageSize);
		else
			return orderedDTOList;
	}
	
	@Override
	public int countMeasureShareInfoForUser(String searchText, User user) {
		
		String searchTextLC = searchText.toLowerCase().trim();
		
		Criteria mCriteria = buildMeasureShareForUserCriteria(user);
		List<Measure> ms = mCriteria.list();
		
		ms = getAllMeasuresInSet(ms);
		
		int count = 0;
		for(Measure m : ms){
			boolean increment = m.getaBBRName().toLowerCase().contains(searchTextLC) ? true :
				m.getDescription().toLowerCase().contains(searchTextLC) ? true :
					false;
			if(increment)
				count++;
		}
		return count;
	}
	
	@Override
	public int countMeasureShareInfoForUser(int filter, User user) {
		Criteria mCriteria = buildMeasureShareForUserCriteriaWithFilter(user, filter);
		List<Measure> ms = mCriteria.list();
		ms = getAllMeasuresInSet(ms);
		return ms.size();
	}
	
	/*
	 * (non-Javadoc)
	 * @see mat.dao.clause.MeasureDAO#resetLockDate(mat.model.clause.Measure)
	 * This method opens a new Session and new transaction to update only the lockedDate column in the database.
	 * After updating the table,transaction has been committed and session has been closed.
	 */
	//TODO:- We need to follow the same logic/concept while settingtheLockedDate.
	@Override
	public void resetLockDate(Measure m){
		Session session = getSessionFactory().openSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		String sql = "update mat.model.clause.Measure m set lockedOutDate  = :lockDate, lockedUser = :lockedUserId  where id = :measureId";
		Query query = session.createQuery(sql);
		query.setString("lockDate",null);
		query.setString("lockedUserId", null);
		query.setString("measureId",m.getId());
		int rowCount = query.executeUpdate();
		System.out.println("Rows affected: while releasing lock " + rowCount);
		tx.commit();
		session.close();
	
	}
	
	public String findMaxVersion(String measureSetId){
		Criteria mCriteria = getSessionFactory().getCurrentSession().createCriteria(Measure.class);
		mCriteria.add(Restrictions.eq("measureSet.id", measureSetId));
		mCriteria.setProjection(Projections.max("version")); 
		String maxVersion = (String) mCriteria.list().get(0);
		return maxVersion;
	}
	
	public String findMaxOfMinVersion(String measureSetId, String version){
		logger.info("In MeasureDao.findMaxOfMinVersion()");
		String maxOfMinVersion = version;
		double minVal = 0;
		double maxVal = 0;
		if(StringUtils.isNotBlank(version)){
			int decimalIndex = version.indexOf('.');
			minVal = Integer.valueOf(version.substring(0, decimalIndex)).intValue();
			logger.info("Min value: "+ minVal);
			maxVal = minVal + 1;
			logger.info("Max value: "+ maxVal);
		}
		Criteria mCriteria = getSessionFactory().getCurrentSession().createCriteria(Measure.class);
		//mCriteria.add(Restrictions.and(Restrictions.eq("measureSet.id", measureSetId),
		//		Restrictions.and(Restrictions.sizeGt("version", minVal),Restrictions.sizeLt("version", maxVal))));
		//mCriteria.setProjection(Projections.max("version")); 
		logger.info("Query Using Measure Set Id:" + measureSetId);
		mCriteria.add(Restrictions.eq("measureSet.id", measureSetId));
		mCriteria.addOrder(Order.asc("version"));
		List<Measure> measureList = mCriteria.list();
		double tempVersion = 0;
		if(measureList != null && measureList.size() > 0){
			logger.info("Finding max of min version from the Measure List. Size:" + measureList.size());
			for(Measure measure : measureList){
				logger.info("Looping through Measures Id: "+ measure.getId() +" Version: " + measure.getVersion());
				if(measure.getVersionNumber() > minVal && measure.getVersionNumber() < maxVal){
					if(tempVersion < measure.getVersionNumber()){
						logger.info(tempVersion + "<" + measure.getVersionNumber()+"="+ (tempVersion < measure.getVersionNumber()));
						maxOfMinVersion = measure.getVersion();
						logger.info("maxOfMinVersion: "+ maxOfMinVersion);
					}
					tempVersion = measure.getVersionNumber();
					logger.info("tempVersion: "+ tempVersion);
				}
			}
		}
		logger.info("Returned maxOfMinVersion: " +  maxOfMinVersion);
		return maxOfMinVersion;
	}

	/* (non-Javadoc)
	 * @see mat.dao.clause.MeasureDAO#getMeasuresForDraft(mat.model.User, int, int)
	 */
	@Override
	public List<MeasureShareDTO> getMeasuresForDraft(User user, int startIndex, int pageSize) {
		List<MeasureShareDTO> dtoList = getMeasuresForDraft(user);
		if(pageSize < dtoList.size())
			return dtoList.subList(startIndex, Math.min(startIndex+pageSize, dtoList.size()));
		else
			return dtoList;
	}

	public List<MeasureShareDTO> getMeasuresForDraft(User user){
		List<MeasureShareDTO> orderedDTOList = getMeasureShareInfoForUser(user, 0, Integer.MAX_VALUE);
		
		HashSet<String> hasDraft = new HashSet<String>();
		for(MeasureShareDTO dto : orderedDTOList){
			if(dto.isDraft()){
				String setId = dto.getMeasureSetId();
				hasDraft.add(setId);
			}
		}
		List<MeasureShareDTO> dtoList = new ArrayList<MeasureShareDTO>();
		for(MeasureShareDTO dto : orderedDTOList){
			
			boolean canDraft = dto.isDraft() ? false : 
				hasDraft.contains(dto.getMeasureSetId()) ? false : 
					dto.isLocked() ? false :
						user.getSecurityRole().getDescription().equalsIgnoreCase(SecurityRole.SUPER_USER_ROLE) ? true :
							dto.getOwnerUserId().equalsIgnoreCase(user.getId()) ? true :
								dto.getShareLevel() == null ? false :
									dto.getShareLevel().equalsIgnoreCase(ShareLevel.MODIFY_ID) ? true :
										false;
			
			if(canDraft)
				dtoList.add(dto);
		}
		return dtoList;
	}

	@Override
	public List<MeasureShareDTO> getMeasuresForVersion(User user, int startIndex, int pageSize) {
		List<MeasureShareDTO> dtoList = getMeasuresForVersion(user);
		if(pageSize < dtoList.size())
			return dtoList.subList(startIndex, Math.min(startIndex+pageSize, dtoList.size()));
		else
			return dtoList;
	}
	
	public List<MeasureShareDTO> getMeasuresForVersion(User user) {
		List<MeasureShareDTO> orderedDTOList = getMeasureShareInfoForUser(user, 0, Integer.MAX_VALUE);
		List<MeasureShareDTO> dtoList = new ArrayList<MeasureShareDTO>();
		for(MeasureShareDTO dto: orderedDTOList){
			boolean canVersion = !dto.isDraft() ? false :
				dto.isLocked() ? false :
					user.getSecurityRole().getDescription().equalsIgnoreCase(SecurityRole.SUPER_USER_ROLE) ? true :
						dto.getOwnerUserId().equalsIgnoreCase(user.getId()) ? true :
							dto.getShareLevel() == null ? false :
								dto.getShareLevel().equalsIgnoreCase(ShareLevel.MODIFY_ID) ? true :
									false;
			if(canVersion)
				dtoList.add(dto);
		}
		
		return dtoList;
	}
	
	private List<Measure> sortMeasureList(List<Measure> measureResultList){
		//generate sortable lists
		List<List<Measure>> measureLists = new ArrayList<List<Measure>>(); 
		for(Measure m : measureResultList){
			boolean hasList = false;
			for(List<Measure> mlist : measureLists){
				String msetId = mlist.get(0).getMeasureSet().getId();
				if(m.getMeasureSet().getId().equalsIgnoreCase(msetId)){
					mlist.add(m);
					hasList = true;
					break;
				}
			}
			if(!hasList){
				List<Measure> mlist = new ArrayList<Measure>();
				mlist.add(m);
				measureLists.add(mlist);
			}
		}
	  	//sort
		for(List<Measure> mlist: measureLists){
			Collections.sort(mlist, new MeasureComparator());
		}
		Collections.sort(measureLists, new MeasureListComparator());
		//compile list
		List<Measure> retList = new ArrayList<Measure>();
		for(List<Measure> mlist: measureLists){
			for(Measure m : mlist){
				retList.add(m);
			}
		}
		return retList;
	}
 
	/*
	 * assumption: measures here are all part of the same measure set
	 */
	class MeasureComparator implements Comparator<Measure>{
		@Override
		public int compare(Measure o1, Measure o2) {
			//1 if either isDraft
			//2 version
			int ret = o1.isDraft() ? -1 : 
			o2.isDraft() ? 1 : 
			compareDoubleStrings(o1.getVersion(), o2.getVersion());
			return ret;
		}
		private int compareDoubleStrings(String s1, String s2){
			Double d1 = Double.parseDouble(s1);
			Double d2 = Double.parseDouble(s2);
			return d2.compareTo(d1);
		}
	}
		 
	/*
	 *assumption: each measure in this list is part of the same measure set
	 *
	 */
	class MeasureListComparator implements Comparator<List<Measure>>{
		@Override
		public int compare(List<Measure> o1, List<Measure> o2) {
			String v1 = o1.get(0).getDescription();
			String v2 = o2.get(0).getDescription();
			return v1.compareToIgnoreCase(v2);
		}
	}
	
	/**
	 * for all measure sets referenced in measures in ms, return all measures that are members of the set 
	 * @param ms
	 * @return
	 */
	@Override
	public List<Measure> getAllMeasuresInSet(List<Measure> ms){
		if(!ms.isEmpty()){
			Set<String> measureSetIds = new HashSet<String>();
			for(Measure m : ms){
				measureSetIds.add(m.getMeasureSet().getId());
			}
			
			Criteria setCriteria = getSessionFactory().getCurrentSession().createCriteria(Measure.class);
			setCriteria.add(Restrictions.in("measureSet.id", measureSetIds));
			ms = setCriteria.list();
		}
		return ms;
	}
	
	

	@Override
	public boolean isMeasureLocked(String measureId) {
		Session session = getSessionFactory().openSession();
		String sql = "select lockedOutDate from mat.model.clause.Measure m  where id = '"+measureId+"'";
		Query query = session.createQuery(sql);
		List<Timestamp> result = query.list();
		session.close();
		
		Timestamp lockedOutDate = null;
		if(!result.isEmpty()){
			lockedOutDate = result.get(0);
		}
		
		boolean locked = isLocked(lockedOutDate);
		return locked;
	}
	
	@Override
	public int getMaxEMeasureId() {
		Session session = getSessionFactory().openSession();
		String sql = "select max(eMeasureId) from mat.model.clause.Measure";
		Query query = session.createQuery(sql);
		List<Integer> result = query.list();
		if(!result.isEmpty()){
			return result.get(0).intValue();
		}else{
			return 0;
		}
		
	}
	
	@Override
	public int saveandReturnMaxEMeasureId(Measure measure) {
		int eMeasureId = getMaxEMeasureId() + 1;
		MeasureSet ms = measure.getMeasureSet();
		Session session = getSessionFactory().getCurrentSession();
		SQLQuery query = session.createSQLQuery("update MEASURE m set m.EMEASURE_ID = "+eMeasureId+" where m.MEASURE_SET_ID = '"+ms.getId()+"';");
		query.executeUpdate();
		return eMeasureId;
	}

	

}
