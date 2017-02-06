package mat.server;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.xpath.XPathExpressionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import mat.dao.UserDAO;
import mat.dao.clause.CQLLibraryDAO;
import mat.dao.clause.CQLLibrarySetDAO;
import mat.server.CQLServiceImpl;
import mat.model.LockedUserInfo;
import mat.model.User;
import mat.model.clause.CQLLibrary;
import mat.model.clause.CQLLibrarySet;
import mat.model.clause.MeasureSet;
import mat.model.cql.CQLLibraryDataSetObject;
import mat.model.cql.CQLModel;
import mat.server.service.CQLLibraryServiceInterface;
import mat.server.service.UserService;
import mat.server.util.MATPropertiesService;
import mat.server.util.MeasureUtility;
import mat.server.util.ResourceLoader;
import mat.server.util.XmlProcessor;
import mat.shared.SaveUpdateCQLResult;
import mat.shared.UUIDUtilClient;

public class CQLLibraryService implements CQLLibraryServiceInterface {
	@Autowired
	private CQLLibraryDAO cqlLibraryDAO;
	@Autowired
	private CQLServiceImpl cqlService;
	
	@Autowired
	private CQLLibrarySetDAO cqlLibrarySetDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	/** The context. */
	@Autowired
	private ApplicationContext context;
	
	/** The lock threshold. */
	private final long lockThreshold = 3 * 60 * 1000; // 3 minutes

	@Override
	public List<CQLLibraryDataSetObject> search(String searchText, String searchFrom) {
	//	List<CQLLibraryModel> cqlLibraries = new ArrayList<CQLLibraryModel>();
		List<CQLLibraryDataSetObject> allLibraries = new ArrayList<CQLLibraryDataSetObject>();
		List<CQLLibrary> list = cqlLibraryDAO.search(searchText,searchFrom);
		for(CQLLibrary cqlLibrary : list){
			CQLLibraryDataSetObject object = extractCQLLibraryDataObject(cqlLibrary);
			allLibraries.add(object);
		}
		return allLibraries;
	}
	
	@Override
	public void save(String libraryName, String measureId, User owner, MeasureSet measureSet, String version, String releaseVersion, 
			Timestamp finalizedDate, byte[] cqlByteArray) {
		
		CQLLibrary cqlLibrary = new CQLLibrary(); 
		if(libraryName.length() >200){
			libraryName = libraryName.substring(0, 199);
		}
		cqlLibrary.setName(libraryName);
		cqlLibrary.setMeasureId(measureId);
		cqlLibrary.setOwnerId(owner);
		cqlLibrary.setMeasureSet(measureSet);
		cqlLibrary.setVersion(version);
		cqlLibrary.setReleaseVersion(releaseVersion);
		// TODO CQL SET
		// cqlLibrary.setCqlSetId(cqlSetId);
		cqlLibrary.setDraft(false);
		cqlLibrary.setFinalizedDate(finalizedDate);
		cqlLibrary.setCQLByteArray(cqlByteArray);
		
		this.cqlLibraryDAO.save(cqlLibrary);
	}
	
	/**
	 * Method to extract from DB CQLLibrary object to Client side DTO.
	 * @param cqlLibrary
	 * @return
	 */
	private CQLLibraryDataSetObject extractCQLLibraryDataObject(CQLLibrary cqlLibrary){
		
		CQLLibraryDataSetObject dataSetObject = new CQLLibraryDataSetObject();
		dataSetObject.setId(cqlLibrary.getId());
		dataSetObject.setCqlName(cqlLibrary.getName());
		dataSetObject.setDraft(cqlLibrary.isDraft());
		dataSetObject.setReleaseVersion(cqlLibrary.getReleaseVersion());
		dataSetObject.setFinalizedDate(cqlLibrary.getFinalizedDate());
		dataSetObject.setMeasureId(cqlLibrary.getMeasureId());
		boolean isLocked =isLocked(cqlLibrary.getLockedOutDate());
		
		if (isLocked && (cqlLibrary.getLockedUserId() != null)) {
			LockedUserInfo lockedUserInfo = new LockedUserInfo();
			lockedUserInfo.setUserId(cqlLibrary.getLockedUserId().getUserId());
			lockedUserInfo.setEmailAddress(cqlLibrary.getLockedUserId()
					.getEmailAddress());
			lockedUserInfo.setFirstName(cqlLibrary.getLockedUserId().getFirstName());
			lockedUserInfo.setLastName(cqlLibrary.getLockedUserId().getLastName());
			dataSetObject.setLockedUserInfo(lockedUserInfo);
		}
		
		dataSetObject.setLocked(isLocked);
		dataSetObject.setLockedUserInfo(cqlLibrary.getLockedUserId());
		User user = getUserService().getById(cqlLibrary.getOwnerId().getId());
		dataSetObject.setOwnerFirstName(user.getFirstName());
		dataSetObject.setOwnerLastName(user.getLastName());
		dataSetObject.setOwnerEmailAddress(user.getEmailAddress());
		
		String formattedVersion = MeasureUtility.getVersionTextWithRevisionNumber(cqlLibrary.getVersion(), "", cqlLibrary.isDraft());
		dataSetObject.setVersion(formattedVersion);
		
		
		CQLModel cqlModel = new CQLModel();
		byte[] bdata;
		try {
			bdata = cqlLibrary.getCqlXML().getBytes(1, (int) cqlLibrary.getCqlXML().length());
			String data = new String(bdata);
			cqlModel = CQLUtilityClass.getCQLStringFromMeasureXML(data,"");
			String cqlFileString = CQLUtilityClass.getCqlString(cqlModel,"").toString();
			//SaveUpdateCQLResult result = cqlService.parseCQLStringForError(cqlFileString);
			dataSetObject.setCqlText(cqlFileString);
			//dataSetObject.setCqlModel(cqlModel);
			//dataSetObject.setCqlErrors(result.getCqlErrors());;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		
		return dataSetObject;
		
	}

	private boolean isLocked(Date lockedOutDate) {
			
			boolean locked = false;
			if (lockedOutDate == null) {
				return locked;
			}
			long currentTime = System.currentTimeMillis();
			long lockedOutTime = lockedOutDate.getTime();
			long timeDiff = currentTime - lockedOutTime;
			locked = timeDiff < lockThreshold;
			
			return locked;
		}
	
	private UserService getUserService() {
		return (UserService) context.getBean("userService");
	}
	
	@Override
	public CQLLibraryDataSetObject findCQLLibraryByID(String cqlLibraryId){
		return extractCQLLibraryDataObject(cqlLibraryDAO.find(cqlLibraryId)); 
	}

	@Override
	public CQLModel save(CQLLibraryDataSetObject cqlLibraryDataSetObject) {
		CQLModel returnModel = new CQLModel();
		CQLLibrary library = new CQLLibrary();
		library.setDraft(true);
		library.setName(cqlLibraryDataSetObject.getCqlName());
		
		CQLLibrarySet cqlLibrarySet = new CQLLibrarySet();
		cqlLibrarySet.setId(UUID.randomUUID().toString());	
		cqlLibrarySetDAO.save(cqlLibrarySet);
		
		library.setCqlSet(cqlLibrarySet);
		library.setReleaseVersion(MATPropertiesService.get().getCurrentReleaseVersion());
		library.setRevisionNumber("000");
		library.setVersion("0.0");
		if (LoggedInUserUtil.getLoggedInUser() != null) {
			User currentUser = userDAO.find(LoggedInUserUtil.getLoggedInUser());
			library.setOwnerId(currentUser);
		}
		XmlProcessor xmlProcessor = loadCQLXmlTemplateFile();
		String cqlLookUpString = getCQLLookUpXml(cqlLibraryDataSetObject,xmlProcessor);
		if(cqlLookUpString != null && !cqlLookUpString.isEmpty()){
			byte[] cqlByteArray = cqlLookUpString.getBytes(); 
			library.setCQLByteArray(cqlByteArray);;
			cqlLibraryDAO.save(library);
		}
		
		return returnModel;
	}

	/**
	 * @param cqlLibraryDataSetObject
	 */
	private String getCQLLookUpXml(CQLLibraryDataSetObject cqlLibraryDataSetObject,XmlProcessor xmlProcessor) {
		String cqlLookUp = null;
		try {
			Node cqlTemplateNode = xmlProcessor.findNode(xmlProcessor.getOriginalDoc(), "/cqlTemplate");
			Node cqlLookUpNode = xmlProcessor.findNode(xmlProcessor.getOriginalDoc(), "//cqlLookUp");
			String xPath_ID ="//cqlLookUp/child::node()/*[@id]";
			String xPath_UUID ="//cqlLookUp/child::node()/*[@uuid]";
			if(cqlTemplateNode !=null){
				
				if (cqlTemplateNode.getAttributes().getNamedItem("changeAttribute") != null) {
					String[] attributeToBeModified = cqlTemplateNode.getAttributes().
							getNamedItem("changeAttribute").getNodeValue().split(",");
					for (String changeAttribute : attributeToBeModified) {
						if(changeAttribute.equalsIgnoreCase("id")){
							NodeList nodesForId = xmlProcessor.findNodeList(xmlProcessor.getOriginalDoc(),xPath_ID);
							for(int i=0;i<nodesForId.getLength();i++){
								nodesForId.item(0).getAttributes().getNamedItem("id").
								setNodeValue(UUIDUtilClient.uuid());
							}
						} else if(changeAttribute.equalsIgnoreCase("uuid")){
							NodeList nodesForUUId = xmlProcessor.findNodeList(xmlProcessor.getOriginalDoc(),xPath_UUID);
							for(int i=0;i<nodesForUUId.getLength();i++){
								nodesForUUId.item(0).getAttributes().getNamedItem("uuid").
								setNodeValue(UUIDUtilClient.uuid());
							}
						}
					}
				} 
				
				if(cqlTemplateNode.getAttributes().getNamedItem("changeNodeTextContent") != null){
					String[] nodeTextToBeModified = cqlTemplateNode.getAttributes().
							getNamedItem("changeNodeTextContent").getNodeValue().split(",");
					for (String nodeTextToChange : nodeTextToBeModified) {
						if(nodeTextToChange.equalsIgnoreCase("library")){
							Node libraryNode = xmlProcessor.findNode(xmlProcessor.getOriginalDoc(), "//"+nodeTextToChange);
							if(libraryNode != null){
								libraryNode.setTextContent(cqlLibraryDataSetObject.getCqlName());
							}
						} else if(nodeTextToChange.equalsIgnoreCase("version")){
							Node versionNode = xmlProcessor.findNode(xmlProcessor.getOriginalDoc(), "//"+nodeTextToChange);
							if(versionNode != null){
								versionNode.setTextContent("0.0.000");
							}
						} else if(nodeTextToChange.equalsIgnoreCase("usingModelVersion")){
							Node usingModelVersionNode = xmlProcessor.findNode(xmlProcessor.getOriginalDoc(), "//"+nodeTextToChange);
							if(usingModelVersionNode != null){
								usingModelVersionNode.setTextContent(MATPropertiesService.get().getQmdVersion());
							}
						}
					}
				}
					
			}
			System.out.println(xmlProcessor.transform(cqlLookUpNode,true));
			cqlLookUp = xmlProcessor.transform(cqlLookUpNode,true);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cqlLookUp;
	}
	
	private XmlProcessor loadCQLXmlTemplateFile(){
		String fileName = "CQLXmlTemplate.xml";
		URL templateFileUrl = new ResourceLoader().getResourceAsURL(fileName);
		File templateFile = null;
		try {
			templateFile = new File(templateFileUrl.toURI());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		XmlProcessor templateXMLProcessor = new XmlProcessor(templateFile);
		return templateXMLProcessor;
	}
	
	
}
