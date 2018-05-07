package mat.server;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import mat.model.CQLLibraryOwnerReportDTO;
import mat.model.MeasureOwnerReportDTO;
import mat.model.User;
import mat.model.clause.Measure;
import mat.server.service.MeasureLibraryService;
import mat.server.service.MeasurePackageService;
import mat.server.service.SimpleEMeasureService;
import mat.server.service.SimpleEMeasureService.ExportResult;
import mat.server.service.UserService;
import mat.server.service.impl.ZipPackager;
import mat.shared.CQLErrors;
import mat.shared.FileNameUtility;
import mat.shared.InCorrectUserRoleException;
import mat.shared.SaveUpdateCQLResult;
/**
 * The Class ExportServlet.
 */
public class ExportServlet extends HttpServlet {
	
	private static final String LIBRARY_ID = "libraryid";
	private static final String EXPORT_MEASURE_OWNER = "exportMeasureOwner";
	private static final String EXPORT_CQL_ERROR_FILE_FOR_STAND_ALONE = "errorFileStandAlone";
	private static final String EXPORT_CQL_ERROR_FILE_FOR_MEASURE = "errorFileMeasure";
	/** The Constant EXPORT_ACTIVE_NON_ADMIN_USERS_CSV. */
	private static final String EXPORT_ACTIVE_NON_ADMIN_USERS_CSV = "exportActiveNonAdminUsersCSV";
	private static final String EXPORT_ALL_USERS_CSV = "exportAllUsersCSV";
	
	/** The Constant EXPORT_ACTIVE_OID_CSV. */
	private static final String EXPORT_ACTIVE_OID_CSV = "exportActiveOIDCSV";
	
	private static final String EXPORT_ACTIVE_USER_CQL_LIBRARY_OWNERSHIP = "exportCQLLibraryOwner";
	
	/** The Constant ZIP. */
	private static final String ZIP = "zip";
	
	/** Human readable for Subtree Node *. */
	private static final String SUBTREE_HTML = "subtreeHTML";
	
	/** The Constant CODELIST. */
	private static final String CODELIST = "codelist";
	
	/** The Constant SAVE. */
	private static final String SAVE = "save";
	
	/** The Constant ATTACHMENT_FILENAME. */
	private static final String ATTACHMENT_FILENAME = "attachment; filename=";
	
	/** The Constant CONTENT_DISPOSITION. */
	private static final String CONTENT_DISPOSITION = "Content-Disposition";
	
	/** The Constant TEXT_XML. */
	private static final String TEXT_XML = "text/xml";
	
	/** The Constant APPLICATION_JSON. */
	private static final String APPLICATION_JSON = "application/json";
	
	/** The Constant TEXT_HTML. */
	private static final String TEXT_HTML = "text/html";
	
	/** The Constant TEST_PLAN */
	private static final String TEXT_PLAIN = "text/plain"; 
	
	/** The Constant CONTENT_TYPE. */
	private static final String CONTENT_TYPE = "Content-Type";
	
	private static final String HQMF = "hqmf";
	
	private static final String HUMAN_READABLE = "humanreadable";
	
	/** The Constant SIMPLEXML. */
	private static final String SIMPLEXML = "simplexml";
	
	/** The Constant TYPE_PARAM. */
	private static final String TYPE_PARAM = "type";
	
	/** The Constant XML_PARAM. */
	private static final String XML_PARAM = "xml";
	
	/** The Constant FORMAT_PARAM. */
	private static final String FORMAT_PARAM = "format";
	
	/** The Constant ID_PARAM. */
	private static final String ID_PARAM = "id";
	
	/** The Constant logger. */
	private static final Log logger = LogFactory.getLog(ExportServlet.class);
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4539514145289378238L;
	
	/** The context. */
	protected ApplicationContext context;
	
	private static final String CQL_LIBRARY = "cqlLibrary";
	private static final String ELM = "elm"; 
	private static final String JSON = "json"; 
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		MeasurePackageService service = getMeasurePackageService();
		MeasureLibraryService measureLibraryService = getMeasureLibraryService();
		
		String id = req.getParameter(ID_PARAM);
		String format = req.getParameter(FORMAT_PARAM);
		String type = req.getParameter(TYPE_PARAM);
		String libraryId = req.getParameter(LIBRARY_ID);
		Measure measure = null;
		Date exportDate = null;
		
		logger.info("FORMAT: " + format);
		
		if (id!= null) {
			measure = service.getById(id);
			exportDate = measure.getExportedDate();
		}

		try {
			if (SIMPLEXML.equals(format)) {
				exportSimpleXML(resp, id, type, measure);
			} else if (HQMF.equals(format)) {
				exportHQMFForNewMeasures(resp, id, type, measure);
			} else if (HUMAN_READABLE.equals(format)) {
				exportHumanReadableForNewMeasures(resp, id, type, measure);	
			} else if (CODELIST.equals(format)) {
				exportCodeListXLS(resp, id, measure);
			} else if (CQL_LIBRARY.equals(format)) {
				exportCQLLibraryFile(resp, id, type, measure);
			} else if(ELM.equals(format)) {
				exportELMFile(resp, id, type, measure); 
			} else if(JSON.equals(format)) {
				exportJSONFile(resp, id, type, measure); 
			}else if (ZIP.equals(format)) {
				exportEmeasureZip(resp, id, measure, exportDate);
			} else if (SUBTREE_HTML.equals(format)){
				exportSubTreeHumanReadable(req, resp, id);
			} else if (EXPORT_ACTIVE_NON_ADMIN_USERS_CSV.equals(format)) {
				exportActiveUserListCSV(resp);
			} else if (EXPORT_ACTIVE_OID_CSV.equals(format)) {
				exportActiveOrganizationListCSV(resp);
			} else if(EXPORT_MEASURE_OWNER.equalsIgnoreCase(format)){
				exportActiveUserMeasureOwnershipListCSV(resp);
			} else if (EXPORT_ALL_USERS_CSV.equals(format)) {
				exportAllUserCSV(resp);
			} else if(EXPORT_ACTIVE_USER_CQL_LIBRARY_OWNERSHIP.equals(format)) {
				exportActiveUserCQLLibraryOwnershipListCSV(resp);
			} else if(EXPORT_CQL_ERROR_FILE_FOR_MEASURE.equalsIgnoreCase(format)) {
				exportErrorFileForMeasure(resp, measureLibraryService, id);
			} else if(EXPORT_CQL_ERROR_FILE_FOR_STAND_ALONE.equalsIgnoreCase(format)) {
				exportErrorFileForStandAloneLib(resp, libraryId);
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}finally {
			if(resp!=null && resp.getOutputStream()!=null)
				resp.getOutputStream().close();
		}		
	}		
	
	private void exportErrorFileForMeasure(HttpServletResponse resp, MeasureLibraryService measureLibraryService, String id) throws IOException {
		SaveUpdateCQLResult result = measureLibraryService.getMeasureCQLLibraryData(id);
		addLineNumberAndErrorMessageToCQLErrorExport(resp, result);
	}
	
	private void exportErrorFileForStandAloneLib(HttpServletResponse resp,	String id) throws IOException {
		SaveUpdateCQLResult result = getCQLLibraryService().getCQLLibraryFileData(id);
		addLineNumberAndErrorMessageToCQLErrorExport(resp, result);
	}

	private void addLineNumberAndErrorMessageToCQLErrorExport(HttpServletResponse resp, SaveUpdateCQLResult result)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		String cqlString = result.getCqlString();
		String[] cqlLinesArray = cqlString.split("\n");
		for(int i=0;i<cqlLinesArray.length;i++) {			
			sb.append((i+1)).append(" ").append(cqlLinesArray[i]).append("\r\n");
		}
		if (!result.getCqlErrors().isEmpty() && result.getCqlErrors().size() > 0) {
			
			Collections.sort(result.getCqlErrors());
			sb.append("/*******************************************************************************************************************");
			for (CQLErrors error : result.getCqlErrors()) {
				StringBuilder errorMessage = new StringBuilder();
				errorMessage.append("Line ").append(error.getErrorInLine()).append(": ").append(error.getErrorMessage());				
				sb.append("\r\n").append(errorMessage).append("\r\n");								
			}
			sb.append("*******************************************************************************************************************/");
		}
		
		resp.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + result.getLibraryName() + ".txt");
		resp.getOutputStream().write(sb.toString().getBytes());
	}

	
	
	private void exportELMFile(HttpServletResponse resp, String id, String type, Measure measure) throws Exception {
		
		ExportResult export = getService().getELMFile(id); 
		
		if(export.getIncludedCQLExports().size() > 0){
			ZipPackager zp = new ZipPackager();
			zp.getCQLZipBarr(export, "xml");
			
			resp.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + FileNameUtility.getZipName(export.measureName + "_" + "ELM"));
			resp.setContentType("application/zip");
			resp.getOutputStream().write(export.zipbarr);
			export.zipbarr = null;
		}else if (SAVE.equals(type)) {
			String currentReleaseVersion = measure.getReleaseVersion();
			if(currentReleaseVersion.contains(".")){
				currentReleaseVersion = currentReleaseVersion.replace(".", "_");
			}
			System.out.println("Release version zip " + currentReleaseVersion);
			resp.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + export.getCqlLibraryName() + ".xml");
			resp.getOutputStream().write(export.export.getBytes());
		} else {
			resp.setHeader(CONTENT_TYPE, TEXT_XML);
			resp.getOutputStream().write(export.export.getBytes());
		}
	}
	
	private void exportJSONFile(HttpServletResponse resp, String id, String type, Measure measure) throws Exception {
		
		ExportResult export = getService().getJSONFile(id); 
		
		if(export.getIncludedCQLExports().size() > 0){
			ZipPackager zp = new ZipPackager();
			zp.getCQLZipBarr(export, "json");
			
			resp.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + FileNameUtility.getZipName(export.measureName + "_" + "JSON"));
			resp.setContentType("application/zip");
			resp.getOutputStream().write(export.zipbarr);
			export.zipbarr = null;
		}else if (SAVE.equals(type)) {
			String currentReleaseVersion = measure.getReleaseVersion();
			if(currentReleaseVersion.contains(".")){
				currentReleaseVersion = currentReleaseVersion.replace(".", "_");
			}
			System.out.println("Release version zip " + currentReleaseVersion);
			resp.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + export.getCqlLibraryName() + ".json");
			resp.getOutputStream().write(export.export.getBytes());
		} else {
			resp.setHeader(CONTENT_TYPE, APPLICATION_JSON);
			resp.getOutputStream().write(export.export.getBytes()); 
		}
	}

	
	
	private void exportCQLLibraryFile(HttpServletResponse resp, String id, String type, Measure measure) throws Exception {
		
		ExportResult export = getService().getCQLLibraryFile(id);
		
		if(export.getIncludedCQLExports().size() > 0){
			ZipPackager zp = new ZipPackager();
			zp.getCQLZipBarr(export, "cql");
			
			resp.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + FileNameUtility.getZipName(export.measureName + "_" + "CQL"));
			resp.setContentType("application/zip");
			resp.getOutputStream().write(export.zipbarr);
			export.zipbarr = null;
		}else if (SAVE.equals(type)) {
			String currentReleaseVersion = measure.getReleaseVersion();
			if(currentReleaseVersion.contains(".")){
				currentReleaseVersion = currentReleaseVersion.replace(".", "_");
			}
			System.out.println("Release version zip " + currentReleaseVersion);
			resp.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME
					+ export.getCqlLibraryName()+".cql");
			resp.getOutputStream().write(export.export.getBytes());
		} else {
			resp.setHeader(CONTENT_TYPE, TEXT_PLAIN);
			resp.getOutputStream().write(export.export.getBytes());
		}
	}
	
	private void exportActiveOrganizationListCSV(HttpServletResponse resp) throws IOException {
		String userRole = LoggedInUserUtil.getLoggedInUserRole();
		if ("Administrator".equalsIgnoreCase(userRole)) {
			String csvFileString = generateCSVOfActiveUserOIDs();
			Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String activeUserCSVDate = formatter.format(new Date());
			resp.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + FileNameUtility.getCSVFileName("activeOrganizationOids", activeUserCSVDate) + ";");
			resp.setContentType("text/csv");
			resp.getOutputStream().write(csvFileString.getBytes());
		}
	}
	
	private void exportActiveUserListCSV(HttpServletResponse resp) throws InCorrectUserRoleException, IOException {
		String userRole = LoggedInUserUtil.getLoggedInUserRole();
		if ("Administrator".equalsIgnoreCase(userRole)) {
			String csvFileString = generateCSVOfActiveUserEmails();
			Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String activeUserCSVDate = formatter.format(new Date());
			resp.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + FileNameUtility.getCSVFileName("activeUsers", activeUserCSVDate) + ";");
			resp.setContentType("text/csv");
			resp.getOutputStream().write(csvFileString.getBytes());
		}
	}
	
	private void exportAllUserCSV(HttpServletResponse resp) throws InCorrectUserRoleException, IOException {
		String userRole = LoggedInUserUtil.getLoggedInUserRole();
		if ("Administrator".equalsIgnoreCase(userRole)) {
			String csvFileString = generateCSVOfAllUser();
			Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String allUserCSVDate = formatter.format(new Date());
			resp.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + FileNameUtility.getCSVFileName("allUsersReport", allUserCSVDate) + ";");
			resp.setContentType("text/csv");
			resp.getOutputStream().write(csvFileString.getBytes());
		}
		
	}
	
	private void exportActiveUserMeasureOwnershipListCSV(HttpServletResponse resp) throws InCorrectUserRoleException, IOException, XPathExpressionException {
		String userRole = LoggedInUserUtil.getLoggedInUserRole();
		if ("Administrator".equalsIgnoreCase(userRole)) {
			String csvFileString = generateCSVOfMeasureOwnershipForActiveUser();
			Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String aCSVDate = formatter.format(new Date());
			resp.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + FileNameUtility.getCSVFileName("activeUsersMeasureOwnership", aCSVDate) + ";");
			resp.setContentType("text/csv");
			resp.getOutputStream().write(csvFileString.getBytes());
		}
	}
	
	private void exportActiveUserCQLLibraryOwnershipListCSV(HttpServletResponse resp) throws IOException {
		String userRole = LoggedInUserUtil.getLoggedInUserRole();
		if("Administrator".equalsIgnoreCase(userRole)) {
			String csvFileString = generateCSVOfCQLLibraryOwnershipForActiveUser();
			Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String aCSVDate = formatter.format(new Date());
			resp.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + FileNameUtility.getCSVFileName("activeUsersCQLLibraryOwnership", aCSVDate) + ";");
			resp.setContentType("text/csv");
			resp.getOutputStream().write(csvFileString.getBytes());
		}
	}
	
	private void exportSubTreeHumanReadable(HttpServletRequest req, HttpServletResponse resp, String id) throws Exception {
		String nodeXML = req.getParameter(XML_PARAM);
		System.out.println("Export servlet received node xml:"+nodeXML +" and Measure ID:"+id);
		ExportResult export = getService().getHumanReadableForNode(id,nodeXML);
		resp.setHeader(CONTENT_TYPE, TEXT_HTML);
		resp.getOutputStream().println(export.export);
	}
	
	private void exportEmeasureZip(HttpServletResponse resp, String id, Measure measure, Date exportDate) throws Exception, IOException {
		ExportResult export = getService().getEMeasureZIP(id,exportDate);
				
		String currentReleaseVersion = measure.getReleaseVersion();
		if(currentReleaseVersion.contains(".")){
			currentReleaseVersion = currentReleaseVersion.replace(".", "_");
		}
		System.out.println("Release version zip " + currentReleaseVersion);
		resp.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + FileNameUtility.getZipName(export.measureName + "_" + currentReleaseVersion));
		resp.setContentType("application/zip");
		resp.getOutputStream().write(export.zipbarr);
		export.zipbarr = null;
	}
	
	private void exportCodeListXLS(HttpServletResponse resp, String id, Measure measure) throws Exception, IOException {
		ExportResult export = getService().getEMeasureXLS(id);
		String currentReleaseVersion = measure.getReleaseVersion();
		if(currentReleaseVersion.contains(".")){
			currentReleaseVersion = currentReleaseVersion.replace(".", "_");
		}
		System.out.println("Release version zip " + currentReleaseVersion);
		resp.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + FileNameUtility.getEmeasureXLSName(export.measureName + "_" + currentReleaseVersion, export.packageDate));
		resp.setContentType("application/vnd.ms-excel");
		resp.getOutputStream().write(export.wkbkbarr);
		export.wkbkbarr = null;
	}
	
	private void exportHQMFForNewMeasures(HttpServletResponse resp, String id, String type, Measure measure) throws IOException {
		ExportResult export = getService().getNewEMeasureXML(id);
		if (SAVE.equals(type)) {
			String currentReleaseVersion = measure.getReleaseVersion();
			if(currentReleaseVersion.contains(".")){
				currentReleaseVersion = currentReleaseVersion.replace(".", "_");
			}
			resp.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + FileNameUtility.getEmeasureXMLName(export.measureName + "_" + currentReleaseVersion));
		} else {
			resp.setHeader(CONTENT_TYPE, TEXT_XML);
		}
		resp.getOutputStream().write(export.export.getBytes());
	}
	
	private void exportHumanReadableForNewMeasures(HttpServletResponse resp, String id, String type, Measure measure) throws Exception {
		ExportResult export = getService().getNewEMeasureHTML(id, measure.getReleaseVersion());
		if (SAVE.equals(type)) {
			String currentReleaseVersion = measure.getReleaseVersion();
			if(currentReleaseVersion.contains(".")){
				currentReleaseVersion = currentReleaseVersion.replace(".", "_");
			}
			resp.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + FileNameUtility.getEmeasureHumanReadableName(export.measureName + "_" + currentReleaseVersion));
		} else {
			resp.setHeader(CONTENT_TYPE, TEXT_HTML);
		}
		resp.getOutputStream().write(export.export.getBytes());
	}
	
	private void exportSimpleXML(HttpServletResponse resp, String id, String type, Measure measure ) throws Exception {
		ExportResult export = getService().getSimpleXML(id);
		if (SAVE.equals(type)) {
			String currentReleaseVersion = measure.getReleaseVersion();
			if(currentReleaseVersion.contains(".")){
				currentReleaseVersion = currentReleaseVersion.replace(".", "_");
			}
			System.out.println("Release version zip " + currentReleaseVersion);
			resp.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + FileNameUtility.getSimpleXMLName(export.measureName + "_" + currentReleaseVersion));
		} else {
			resp.setHeader(CONTENT_TYPE, TEXT_XML);
		}
		resp.getOutputStream().write(export.export.getBytes());
	}
	
	/**
	 * Generate csv of active user emails.
	 * 
	 * @return the string
	 * @throws InCorrectUserRoleException
	 *             the in correct user role exception
	 */
	private String generateCSVOfActiveUserEmails() throws InCorrectUserRoleException {
		logger.info("Generating CSV of email addrs for all Active Users...");
		//Get all the active users
		List<User> allNonAdminActiveUsersList = getUserService().getAllNonAdminActiveUsers();
		
		//Iterate through the 'allNonAdminActiveUsersList' and generate a csv
		return createCSVOfAllNonAdminActiveUsers(allNonAdminActiveUsersList);
	}
	
	private String generateCSVOfAllUser() throws InCorrectUserRoleException {
		logger.info("Generating CSV For All Users...");
		//Get all the active users
		List<User> allUsersList = getUserService().getAllUsers();
		
		//Iterate through the 'allNonAdminActiveUsersList' and generate a csv
		return createCSVOfUsers(allUsersList);
	}
	
	/**
	 * Generate csv of active user emails.
	 * 
	 * @return the string
	 * @throws InCorrectUserRoleException
	 *             the in correct user role exception
	 * @throws XPathExpressionException
	 */
	private String generateCSVOfMeasureOwnershipForActiveUser() throws InCorrectUserRoleException, XPathExpressionException {
		logger.info("Generating CSV of Measure Ownership for all Active Non Admin Users...");
		List<MeasureOwnerReportDTO> ownerReList = 	getMeasureLibraryService().getMeasuresForMeasureOwner();
		//Iterate through the 'allNonAdminActiveUsersList' and generate a csv
		return createCSVOfActiveUserMeasures(ownerReList);
	}
	
	/**
	 * Generate csv of cql library ownership for all active users
	 * @return the csv string
	 */
	private String generateCSVOfCQLLibraryOwnershipForActiveUser() {
		logger.info("Generating CSV of CQL Library Ownership for all Active Non Admin Users...");
		List<CQLLibraryOwnerReportDTO> ownerList = getCQLLibraryService().getCQLLibrariesForOwner();
		return createCSVOfActiveUserCQLLibrary(ownerList);
		
	}
	
	/**
	 * Generate csv of active OIDs.
	 * @return the string
	 */
	private String generateCSVOfActiveUserOIDs() {
		logger.info("Generating CSV of Active User OID's...");
		//Get all the active users
		List<User> allNonTerminatedUsersList = getUserService().searchForNonTerminatedUsers();
		Map<String, String> activeOidsMap = new TreeMap<String, String>();
		for (User user : allNonTerminatedUsersList) {
			activeOidsMap.put(user.getOrgOID(), user.getOrganizationName());
		}
		
		//Iterate through the 'allNonTerminatedUsersList' and generate a csv
		return createCSVOfAllActiveUsersOID(activeOidsMap);
	}
	
	/**
	 * Creates the csv of Active User's OIDs.
	 * 
	 * @param activeOidsMap
	 *            Map of Distinct OID's
	 * @return the string
	 */
	private String createCSVOfAllActiveUsersOID(final Map<String, String> activeOidsMap) {
		
		StringBuilder csvStringBuilder = new StringBuilder();
		//Add the header row
		csvStringBuilder.append("Organization,Organization Id");
		csvStringBuilder.append("\r\n");
		//Add data rows
		for (Map.Entry<String, String> entry : activeOidsMap.entrySet()) {
			csvStringBuilder.append("\"" + entry.getValue() + "\",\"" + entry.getKey() +  "\"");
			csvStringBuilder.append("\r\n");
		}
		return csvStringBuilder.toString();
	}
	
	/**
	 * Creates the csv of all non admin active users.
	 * 
	 * @param allNonAdminActiveUsersList
	 *            the all non admin active users list
	 * @return the string
	 */
	private String createCSVOfAllNonAdminActiveUsers(final List<User> allNonAdminActiveUsersList) {
		
		StringBuilder csvStringBuilder = new StringBuilder();
		//Add the header row
		csvStringBuilder.append("User ID,Last Name,First Name,Email Address,Organization,User Role,Organization Id");
		csvStringBuilder.append("\r\n");
		
		
		//Add data rows
		for (User user:allNonAdminActiveUsersList) {
			csvStringBuilder.append("\"" + user.getLoginId() 
					+ "\",\"" + user.getLastName() + "\",\"" + user.getFirstName()
					+ "\",\"" + user.getEmailAddress() + "\",\"" + user.getOrganizationName()
					+ "\",\"" + user.getSecurityRole().getDescription()
					+ "\",\"" + user.getOrgOID() + "\"");
			csvStringBuilder.append("\r\n");
		}
		return csvStringBuilder.toString();
	}
	
	private String createCSVOfUsers(final List<User> allNonAdminActiveUsersList) {
		
		StringBuilder csvStringBuilder = new StringBuilder();
		//Add the header row
		csvStringBuilder.append("User ID,Last Name,First Name,Organization,Organization Id,Email Address,User Status,Role,Date Of Termination");
		csvStringBuilder.append("\r\n");
		
		
		//Add data rows
		for (User user:allNonAdminActiveUsersList) {
			csvStringBuilder.append("\"" + user.getLoginId() 
					+ "\",\"" + user.getLastName() + "\",\"" + user.getFirstName()
					+ "\",\"" + user.getOrganizationName()
					+ "\",\"" + user.getOrgOID()
					+ "\",\"" + user.getEmailAddress()
					+ "\",\"" + user.getStatus().getDescription()
					+ "\",\"" + user.getSecurityRole().getDescription()
					+ "\",\"" + user.getTerminationDate() + "\"");
			csvStringBuilder.append("\r\n");
		}
		return csvStringBuilder.toString();
	}
	
	/**
	 * Generates Measure and Measure Owner report for Active Non Admin Users.
	 * @param ownerReList - List.
	 * @return CSV String
	 */
	private String createCSVOfActiveUserMeasures(final List<MeasureOwnerReportDTO> ownerReList) {
		
		StringBuilder csvStringBuilder = new StringBuilder();
		//Add the header row
		csvStringBuilder.append("Last Name,First Name,Organization,Measure Name,Emeasure Id , GUID ,NQF Number");
		csvStringBuilder.append("\r\n");
		for (MeasureOwnerReportDTO measureOwnerReportDTO : ownerReList) {
			csvStringBuilder.append("\"" + measureOwnerReportDTO.getLastName() + "\",\"" + measureOwnerReportDTO.getFirstName()
					+ "\",\"" + measureOwnerReportDTO.getOrganization()
					+ "\",\"" + measureOwnerReportDTO.getName() + "\",\"");
			if (measureOwnerReportDTO.getCmsNumber() != 0) {
				csvStringBuilder.append(measureOwnerReportDTO.getCmsNumber() + "\",\"");
			} else {
				csvStringBuilder.append("" + "\",\"");
			}
			if (measureOwnerReportDTO.getId() != null) {
				csvStringBuilder.append(measureOwnerReportDTO.getId() + "\"");
			} else {
				csvStringBuilder.append("" + "\"");
			}
			if (measureOwnerReportDTO.getNqfId() != null) {
				csvStringBuilder.append(",\"\t" + measureOwnerReportDTO.getNqfId() + "\"");
			} else {
				csvStringBuilder.append(",\"" + "" + "\"");
			}
			csvStringBuilder.append("\r\n");
		}
		return csvStringBuilder.toString();
	}
	
	/**
	 * Creates the csv string for the cql library ownership report
	 * @param ownerList the list of cql library owner reports
	 * @return the csv string
	 */
	private String createCSVOfActiveUserCQLLibrary(final List<CQLLibraryOwnerReportDTO> ownerList) {
		StringBuilder csvStringBuilder = new StringBuilder();
		
		// add the header
		csvStringBuilder.append("CQL Library Name,Type,Status,Version #,ID #,Set ID #,First Name,Last Name,Organization");
		csvStringBuilder.append("\r\n");
		
		// add data
		for(CQLLibraryOwnerReportDTO cqlLibraryOwnerReport : ownerList) {
			csvStringBuilder.append("\"" + cqlLibraryOwnerReport.getName() + "\",\"");
			csvStringBuilder.append(cqlLibraryOwnerReport.getType()+ "\",\"");
			csvStringBuilder.append(cqlLibraryOwnerReport.getStatus()+ "\",\"");
			csvStringBuilder.append(cqlLibraryOwnerReport.getVersionNumber()+ "\",\"");
			csvStringBuilder.append(cqlLibraryOwnerReport.getId() + "\",\"");
			csvStringBuilder.append(cqlLibraryOwnerReport.getSetId()+ "\",\"");
			csvStringBuilder.append(cqlLibraryOwnerReport.getFirstName()+ "\",\"");
			csvStringBuilder.append(cqlLibraryOwnerReport.getLastName()+ "\",\"");
			csvStringBuilder.append(cqlLibraryOwnerReport.getOrganization() + "" + "\"");
			csvStringBuilder.append("\r\n");
		}
		
		
		return csvStringBuilder.toString();
	}
	
	/**
	 * Gets the service.
	 * 
	 * @return the service
	 */
	private SimpleEMeasureService getService() {
		SimpleEMeasureService service = (SimpleEMeasureService) context.getBean("eMeasureService");
		return service;
	}
	
	/**
	 * Gets the user service.
	 * 
	 * @return the user service
	 */
	private UserService getUserService() {
		return (UserService) context.getBean("userService");
	}
	
	/**
	 * Gets the measure package service.
	 *
	 * @return the measure package service
	 */
	private MeasurePackageService getMeasurePackageService() {
		return (MeasurePackageService) context.getBean("measurePackageService");
	}
	
	/**
	 * Gets the measure library service.
	 *
	 * @return the measure library service
	 */
	private MeasureLibraryService getMeasureLibraryService(){
		return (MeasureLibraryService) context.getBean("measureLibraryService");
	}	
	
	/**
	 * Gets the cql library service
	 * @return the cql library service
	 */
	private CQLLibraryService getCQLLibraryService() {
		return (CQLLibraryService) context.getBean("cqlLibraryService");
	}
}
