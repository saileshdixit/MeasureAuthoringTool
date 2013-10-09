package mat.server.clause;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import mat.client.measure.ManageMeasureDetailModel;
import mat.client.measure.ManageMeasureSearchModel;
import mat.client.measure.service.MeasureCloningService;
import mat.client.shared.MatException;
import mat.dao.UserDAO;
import mat.dao.clause.MeasureDAO;
import mat.dao.clause.MeasureSetDAO;
import mat.dao.clause.MeasureXMLDAO;
import mat.model.MeasureNotes;
import mat.model.QualityDataModelWrapper;
import mat.model.User;
import mat.model.clause.Measure;
import mat.model.clause.MeasureSet;
import mat.model.clause.MeasureXML;
import mat.server.LoggedInUserUtil;
import mat.server.SpringRemoteServiceServlet;
import mat.server.service.MeasureNotesService;
import mat.server.util.MeasureUtility;
import mat.server.util.XmlProcessor;
import mat.shared.model.util.MeasureDetailsUtil;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.dom.ElementImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@SuppressWarnings("serial")
public class MeasureCloningServiceImpl extends SpringRemoteServiceServlet
		implements MeasureCloningService {

	@Autowired
	private MeasureDAO measureDAO;
	@Autowired
	private MeasureXMLDAO measureXmlDAO;
	@Autowired
	private MeasureSetDAO measureSetDAO;
	@Autowired
	private UserDAO userDAO;

	private static final Log logger = LogFactory
			.getLog(MeasureCloningServiceImpl.class);
	private static final String MEASURE_DETAILS = "measureDetails";
	private static final String MEASURE_GROUPING = "measureGrouping";
	private static final String UU_ID = "uuid";
	private static final String TITLE = "title";
	private static final String SHORT_TITLE = "shortTitle";
	private static final String GUID = "guid";
	private static final String VERSION = "version";
	private static final String MEASURE_STATUS = "status";
	private static final String MEASURE_SCORING = "scoring";
	private static final String SUPPLEMENTAL_DATA_ELEMENTS = "supplementalDataElements";
	private static final String VERSION_ZERO = "0.0";
	private static final boolean TRUE = true;
	private static final String XPATH_MEASURE_ELEMENT_LOOKUP_QDM = "/measure/elementLookUp/qdm [@suppDataElement='true']";

	private Document clonedDoc;
	Measure clonedMeasure;

	
	@Override
	public ManageMeasureSearchModel.Result clone(
			ManageMeasureDetailModel currentDetails, String loggedinUserId,
			boolean creatingDraft) throws MatException {
		logger.info("In MeasureCloningServiceImpl.clone() method..");
		measureDAO = (MeasureDAO) context.getBean("measureDAO");
		measureXmlDAO = (MeasureXMLDAO) context.getBean("measureXMLDAO");
		measureSetDAO = (MeasureSetDAO) context.getBean("measureSetDAO");
		userDAO = (UserDAO) context.getBean("userDAO");

		try {
			ManageMeasureSearchModel.Result result = new ManageMeasureSearchModel.Result();
			Measure measure = measureDAO.find(currentDetails.getId());
			MeasureXML xml = measureXmlDAO.findForMeasure(currentDetails
					.getId());
			clonedMeasure = new Measure();
			String originalXml = xml.getMeasureXMLAsString();
			InputSource oldXmlstream = new InputSource(new StringReader(
					originalXml));
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document originalDoc = docBuilder.parse(oldXmlstream);
			clonedDoc = originalDoc;
			clonedMeasure.setaBBRName(currentDetails.getShortName());
			clonedMeasure.setDescription(currentDetails.getName());
			clonedMeasure.setMeasureStatus("In Progress");
			clonedMeasure.setDraft(TRUE);
			if (currentDetails.getMeasScoring() != null) {
				clonedMeasure
						.setMeasureScoring(currentDetails.getMeasScoring());
			} else {
				clonedMeasure.setMeasureScoring(measure.getMeasureScoring());
			}
			if (LoggedInUserUtil.getLoggedInUser() != null) {
				User currentUser = userDAO.find(LoggedInUserUtil
						.getLoggedInUser());
				clonedMeasure.setOwner(currentUser);
			}
			if (creatingDraft) {
				clonedMeasure.setMeasureSet(measure.getMeasureSet());
				clonedMeasure.setVersion(measure.getVersion());
				measureDAO.saveMeasure(clonedMeasure);
				saveMeasureNotesInDraftMeasure(clonedMeasure.getId(), measure);
				createNewMeasureDetailsForDraft();
			} else {
				// Clear the measureDetails tag
				clearChildNodes(MEASURE_DETAILS);
				MeasureSet measureSet = new MeasureSet();
				measureSet.setId(UUID.randomUUID().toString());
				measureSetDAO.save(measureSet);
				clonedMeasure.setMeasureSet(measureSet);
				clonedMeasure.setVersion(VERSION_ZERO);
				measureDAO.saveMeasure(clonedMeasure);
				createNewMeasureDetails();
			}

			// Create the measureGrouping tag
			clearChildNodes(MEASURE_GROUPING);
			clearChildNodes(SUPPLEMENTAL_DATA_ELEMENTS);
			// create the default 4 CMS supplemental QDM
			QualityDataModelWrapper wrapper = measureXmlDAO
					.createSupplimentalQDM(clonedMeasure.getId(), TRUE,
							getSupplementalUUIds());
			ByteArrayOutputStream streamSuppDataEle = XmlProcessor
					.convertQDMOToSuppleDataXML(wrapper);
			// Remove <?xml> and then replace.
			String filteredStringSupp = removePatternFromXMLString(
					streamSuppDataEle.toString().substring(
							streamSuppDataEle.toString()
									.indexOf("<measure>", 0)), "<measure>", "");
			filteredStringSupp = removePatternFromXMLString(filteredStringSupp,
					"</measure>", "");

			String clonedXMLString = convertDocumenttoString(clonedDoc);
			MeasureXML clonedXml = new MeasureXML();
			clonedXml.setMeasureXMLAsByteArray(clonedXMLString);
			clonedXml.setMeasure_id(clonedMeasure.getId());
			XmlProcessor xmlProcessor = new XmlProcessor(
					clonedXml.getMeasureXMLAsString());
			String clonedXMLString2 = xmlProcessor.appendNode(
					filteredStringSupp, "elementRef",
					"/measure/supplementalDataElements");
			clonedXml.setMeasureXMLAsByteArray(clonedXMLString2);
			if (currentDetails.getMeasScoring() != null
					&& !currentDetails.getMeasScoring().equals(
							measure.getMeasureScoring())) {
				xmlProcessor = new XmlProcessor(
						clonedXml.getMeasureXMLAsString());
				String scoringTypeId = MeasureDetailsUtil
						.getScoringAbbr(clonedMeasure.getMeasureScoring());
				xmlProcessor.removeNodesBasedOnScoring(scoringTypeId);
				xmlProcessor.createNewNodesBasedOnScoring(scoringTypeId);
				clonedXml.setMeasureXMLAsByteArray(xmlProcessor
						.transform(xmlProcessor.getOriginalDoc()));
			}
			logger.info("Final XML after cloning/draft"
					+ clonedXml.getMeasureXMLAsString());
			measureXmlDAO.save(clonedXml);
			result.setId(clonedMeasure.getId());
			result.setName(currentDetails.getName());
			result.setShortName(currentDetails.getShortName());
			result.setScoringType(currentDetails.getMeasScoring());
			String formattedVersion = MeasureUtility.getVersionText(
					measure.getVersion(), measure.isDraft());
			result.setVersion(formattedVersion);
			result.setEditable(TRUE);
			result.setClonable(TRUE);
			return result;
		} catch (Exception e) {
			log(e.getMessage(), e);
			throw new MatException(e.getMessage());
		}
	}

	private void saveMeasureNotesInDraftMeasure(String draftMeasureId,
			Measure measure) {
		List<MeasureNotes> measureNotesList = getMeasureNotesService()
				.getAllMeasureNotesByMeasureID(measure.getId());
		if (measureNotesList != null && !measureNotesList.isEmpty()) {

			for (MeasureNotes measureNotes : measureNotesList) {
				if (measureNotes != null) {
					try {
						MeasureNotes measureNotesDraft = measureNotes
								.clone();
						measureNotesDraft.setMeasure_id(draftMeasureId);
						getMeasureNotesService().saveMeasureNote(
								measureNotesDraft);
						logger.info("MeasureNotes saved successfully on creating draft measure.");
					} catch (Exception e) {
						logger.info("Failed to save MeasureNotes on creating draft measure. Exception occured:"
								+ e.getMessage());
					}
				}
			}

		}
	}

	private MeasureNotesService getMeasureNotesService() {
		return ((MeasureNotesService) context.getBean("measureNotesService"));
	}

	private void clearChildNodes(String nodeName) {
		NodeList nodeList = clonedDoc.getElementsByTagName(nodeName);
		Node parentNode = nodeList.item(0);
		if (parentNode != null) {
			while (parentNode.hasChildNodes()) {
				parentNode.removeChild(parentNode.getFirstChild());

			}
		}
	}

	private HashMap<String, String> getSupplementalUUIds() {
		javax.xml.xpath.XPath xPath = XPathFactory.newInstance().newXPath();
		HashMap<String, String> supplementalUUIdMap = null;
		try {
			NodeList nodesElementLookUpAll = (NodeList) xPath.evaluate(
					XPATH_MEASURE_ELEMENT_LOOKUP_QDM,
					clonedDoc.getDocumentElement(), XPathConstants.NODESET);
			if (nodesElementLookUpAll != null) {
				supplementalUUIdMap = new HashMap<String, String>();
				for (int i = 0; i < nodesElementLookUpAll.getLength(); i++) {
					Node newNode = nodesElementLookUpAll.item(i);
					String nodeName = newNode.getAttributes()
							.getNamedItem("name").getNodeValue().toString();
					String uuid = newNode.getAttributes().getNamedItem("uuid")
							.getNodeValue().toString();
					supplementalUUIdMap.put(nodeName, uuid);
				}
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return supplementalUUIdMap;
	}

	private void createNewMeasureDetails() {
		NodeList nodeList = clonedDoc.getElementsByTagName(MEASURE_DETAILS);
		Node parentNode = nodeList.item(0);
		Node uuidNode = clonedDoc.createElement(UU_ID);
		uuidNode.setTextContent(clonedMeasure.getId());
		Node titleNode = clonedDoc.createElement(TITLE);
		titleNode.setTextContent(clonedMeasure.getDescription());
		Node shortTitleNode = clonedDoc.createElement(SHORT_TITLE);
		shortTitleNode.setTextContent(clonedMeasure.getaBBRName());
		Node guidNode = clonedDoc.createElement(GUID);
		guidNode.setTextContent(clonedMeasure.getMeasureSet().getId());
		Node versionNode = clonedDoc.createElement(VERSION);
		versionNode.setTextContent(clonedMeasure.getVersion());
		Node statusNode = clonedDoc.createElement(MEASURE_STATUS);
		statusNode.setTextContent(clonedMeasure.getMeasureStatus());
		Node measureScoringNode = clonedDoc.createElement(MEASURE_SCORING);
		String measureScoring = clonedMeasure.getMeasureScoring();
		ElementImpl element = (ElementImpl) measureScoringNode;
		element.setAttribute("id",
				MeasureDetailsUtil.getScoringAbbr(measureScoring));
		measureScoringNode.setTextContent(measureScoring);
		parentNode.appendChild(uuidNode);
		parentNode.appendChild(titleNode);
		parentNode.appendChild(shortTitleNode);
		parentNode.appendChild(guidNode);
		parentNode.appendChild(versionNode);
		parentNode.appendChild(statusNode);
		parentNode.appendChild(measureScoringNode);
	}

	private void createNewMeasureDetailsForDraft() {
		clonedDoc.getElementsByTagName(UU_ID).item(0)
				.setTextContent(clonedMeasure.getId());
		clonedDoc.getElementsByTagName(TITLE).item(0)
				.setTextContent(clonedMeasure.getDescription());
		clonedDoc.getElementsByTagName(SHORT_TITLE).item(0)
				.setTextContent(clonedMeasure.getaBBRName());
		clonedDoc.getElementsByTagName(MEASURE_STATUS).item(0)
				.setTextContent(clonedMeasure.getMeasureStatus());

	}

	private String convertDocumenttoString(Document doc) throws Exception {
		try {
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (Exception e) {
			log(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}

	}

	private String removePatternFromXMLString(String xmlString,
			String patternStart, String replaceWith) {
		String newString = xmlString;
		if (patternStart != null) {
			newString = newString.replaceAll(patternStart, replaceWith);
		}
		return newString;
	}
}
