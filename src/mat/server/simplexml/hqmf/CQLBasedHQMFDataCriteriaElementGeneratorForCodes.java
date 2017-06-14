
package mat.server.simplexml.hqmf;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import mat.model.clause.MeasureExport;
import mat.server.util.MATPropertiesService;
import mat.server.util.XmlProcessor;
import mat.shared.UUIDUtilClient;

// TODO: Auto-generated Javadoc
/**
 * The Class CQLBasedHQMFDataCriteriaElementGenerator.
 */
public class CQLBasedHQMFDataCriteriaElementGeneratorForCodes implements Generator {

	protected String extensionValue = null;

	/** The Constant logger. */
	private static final Log logger = LogFactory.getLog(CQLBasedHQMFDataCriteriaElementGeneratorForCodes.class);

	/**
	 * Generate hqm for measure.
	 *
	 * @param me
	 *            the me
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public String generate(MeasureExport me) throws Exception {

		String dataCriteria = "";
		 dataCriteria = getHQMFXmlString(me);
		return dataCriteria;
	}

	
	/**
	 * Gets the HQMF xml string.
	 * 
	 * @param me
	 *            the me
	 * @return the HQMF xml string
	 */
	private String getHQMFXmlString(MeasureExport me) {
		getExtensionValueBasedOnVersion(me);
		XmlProcessor dataCriteriaXMLProcessor = me.getHQMFXmlProcessor();
		//me.setHQMFXmlProcessor(dataCriteriaXMLProcessor);
		
		/*String simpleXMLStr = me.getSimpleXML();
		XmlProcessor simpleXmlprocessor = new XmlProcessor(simpleXMLStr);
		me.setSimpleXMLProcessor(simpleXmlprocessor);*/
		
	//	prepHQMF(me);
		
		createDataCriteriaForQDMELements(me, dataCriteriaXMLProcessor, me.getSimpleXMLProcessor());
	//	addDataCriteriaComment(dataCriteriaXMLProcessor);
		return dataCriteriaXMLProcessor.transform(dataCriteriaXMLProcessor.getOriginalDoc(), true);
	}
	
	
	private String getDataCriteriaExtValueBasedOnVersion(MeasureExport me) {
		if (me != null) {
			String releaseVersion = me.getMeasure().getReleaseVersion();
			if (releaseVersion.equalsIgnoreCase("v4")) {
				return VERSION_4_1_2_ID;
			} else if (releaseVersion.equalsIgnoreCase(MATPropertiesService.get().getCurrentReleaseVersion())) {
				return VERSION_5_0_ID;
			} else {
				return VERSION_4_3_ID;
			}
		}
		return "";
	}

	/**
	 * Creates the data criteria for qdm elements.
	 *
	 * @param me
	 *            the me
	 * @param dataCriteriaXMLProcessor
	 *            the data criteria xml processor
	 * @param simpleXmlprocessor
	 *            the simple xmlprocessor
	 * @return the string
	 */
	private void createDataCriteriaForQDMELements(MeasureExport me, XmlProcessor dataCriteriaXMLProcessor,
			XmlProcessor simpleXmlprocessor) {

		String xPathForDirectReferenceCodes = "/measure/elementLookUp/qdm[@datatype and @code ='true']";

		try {
			NodeList directReferenceCodeNodeList = simpleXmlprocessor.findNodeList(simpleXmlprocessor.getOriginalDoc(),
					xPathForDirectReferenceCodes);
			generateCQLDRCNodeEntries(dataCriteriaXMLProcessor, simpleXmlprocessor, directReferenceCodeNodeList);

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}

	protected void getExtensionValueBasedOnVersion(MeasureExport me) {
		if (me != null) {
			extensionValue = getDataCriteriaExtValueBasedOnVersion(me);
		}
	}

	private void generateCQLDRCNodeEntries(XmlProcessor dataCriteriaXMLProcessor, XmlProcessor simpleXmlprocessor,
			NodeList qdmNoAttributeNodeList) throws XPathExpressionException {

		if (qdmNoAttributeNodeList == null) {
			return;
		}

		for (int i = 0; i < qdmNoAttributeNodeList.getLength(); i++) {
			Node qdmNode = qdmNoAttributeNodeList.item(i);
			createXmlForDataCriteria(qdmNode, dataCriteriaXMLProcessor, simpleXmlprocessor);
		}
	}

	/**
	 * Create xml for data criteria.
	 *
	 * @param qdmNode
	 *            the qdm node
	 * @param dataCriteriaXMLProcessor
	 *            the data criteria xml processor
	 * @param simpleXmlprocessor
	 *            the simple xmlprocessor
	 * @param attributeQDMNode
	 *            the attribute qdm node
	 * @return void
	 */
	private void createXmlForDataCriteria(Node qdmNode, XmlProcessor dataCriteriaXMLProcessor,
			XmlProcessor simpleXmlprocessor) {
		String dataType = qdmNode.getAttributes().getNamedItem("datatype").getNodeValue();

		XmlProcessor templateXMLProcessor = CQLBasedHQMFTemplateXMLSingleton.getTemplateXmlProcessor();
		String xPathForTemplate = "/templates/template[text()='" + dataType.toLowerCase() + "']";
		String actNodeStr = "";
		try {

			Node templateNode = templateXMLProcessor.findNode(templateXMLProcessor.getOriginalDoc(), xPathForTemplate);
			if (templateNode != null) {
				String attrClass = templateNode.getAttributes().getNamedItem(CLASS).getNodeValue();
				String xpathForAct = "/templates/acts/act[@a_id='" + attrClass + "']";
				Node actNode = templateXMLProcessor.findNode(templateXMLProcessor.getOriginalDoc(), xpathForAct);
				if (actNode != null) {
					actNodeStr = actNode.getTextContent();
				}

				createDataCriteriaElementTag(actNodeStr, templateNode, qdmNode, dataCriteriaXMLProcessor,
						simpleXmlprocessor, templateXMLProcessor);
			}

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the creates the data create element tag.
	 *
	 * @param actNodeStr
	 *            the act node str
	 * @param templateNode
	 *            the template node
	 * @param qdmNode
	 *            the qdm node
	 * @param dataCriteriaXMLProcessor
	 *            the data criteria xml processor
	 * @param simpleXmlprocessor
	 *            the simple xmlprocessor
	 * @param templateXMLProcessor
	 *            - templateXmlProcessor
	 * @param attributeQDMNode
	 *            - Attribute QDM Node.
	 * @throws XPathExpressionException
	 *             the x path expression exception
	 */
	private void createDataCriteriaElementTag(String actNodeStr, Node templateNode, Node qdmNode,
			XmlProcessor dataCriteriaXMLProcessor, XmlProcessor simpleXmlprocessor, XmlProcessor templateXMLProcessor)
			throws XPathExpressionException {
		String oidValue = templateNode.getAttributes().getNamedItem(OID).getNodeValue();
		String classCodeValue = templateNode.getAttributes().getNamedItem(CLASS).getNodeValue();
		String moodValue = templateNode.getAttributes().getNamedItem(MOOD).getNodeValue();
		String statusValue = templateNode.getAttributes().getNamedItem("status").getNodeValue();
		String rootValue = qdmNode.getAttributes().getNamedItem(ID).getNodeValue();
		String dataType = qdmNode.getAttributes().getNamedItem("datatype").getNodeValue();
		String qdmOidValue = qdmNode.getAttributes().getNamedItem(OID).getNodeValue();

		// String isCodeType =
		// qdmNode.getAttributes().getNamedItem("code").getNodeValue();

		//String qdmName = qdmNode.getAttributes().getNamedItem(NAME).getNodeValue();
		Node actionNegInd = templateNode.getAttributes().getNamedItem("actionNegationInd");
	//	String entryCommentText = dataType;
		String codeOID = qdmNode.getAttributes().getNamedItem("oid").getNodeValue();
		// Stan wants to generate unique id ( extension and root combination
		// which is different from Value set).
		String qdmLocalVariableName = codeOID + "_" + dataType;
		// String localVariableName = qdmLocalVariableName;
		

		qdmLocalVariableName = StringUtils.deleteWhitespace(qdmLocalVariableName);
		// localVariableName = StringUtils.deleteWhitespace(localVariableName);

		Element dataCriteriaSectionElem = (Element) dataCriteriaXMLProcessor.getOriginalDoc()
				.getElementsByTagName("dataCriteriaSection").item(0);
		Element componentElem = (Element) dataCriteriaXMLProcessor.getOriginalDoc().getElementsByTagName("component")
				.item(0);
		Attr nameSpaceAttr = dataCriteriaXMLProcessor.getOriginalDoc().createAttribute("xmlns:xsi");
		nameSpaceAttr.setNodeValue(nameSpace);
		componentElem.setAttributeNodeNS(nameSpaceAttr);
		// xmlns:qdm="urn:hhs-qdm:hqmf-r2-extensions:v1"
		Attr qdmNameSpaceAttr = dataCriteriaXMLProcessor.getOriginalDoc().createAttribute("xmlns:cql-ext");
		qdmNameSpaceAttr.setNodeValue("urn:hhs-cql:hqmf-n1-extensions:v1");
		componentElem.setAttributeNodeNS(qdmNameSpaceAttr);
		// creating Entry Tag
		Element entryElem = dataCriteriaXMLProcessor.getOriginalDoc().createElement("entry");
		entryElem.setAttribute(TYPE_CODE, "DRIV");
		

		Element dataCriteriaElem = dataCriteriaXMLProcessor.getOriginalDoc().createElement(actNodeStr);
		entryElem.appendChild(dataCriteriaElem);
		dataCriteriaElem.setAttribute(CLASS_CODE, classCodeValue);
		dataCriteriaElem.setAttribute(MOOD_CODE, moodValue);
		// adding actionNegationInd for Negative Datatypes
		if (actionNegInd != null) {
			dataCriteriaElem.setAttribute(ACTION_NEGATION_IND, actionNegInd.getNodeValue());
		}
		Element templateId = dataCriteriaXMLProcessor.getOriginalDoc().createElement(TEMPLATE_ID);
		dataCriteriaElem.appendChild(templateId);
		Element itemChild = dataCriteriaXMLProcessor.getOriginalDoc().createElement(ITEM);
		itemChild.setAttribute(ROOT, oidValue);
		if (templateNode.getAttributes().getNamedItem("addExtensionInTemplate") == null) {
			itemChild.setAttribute("extension", extensionValue);
		}
		templateId.appendChild(itemChild);
		Element idElem = dataCriteriaXMLProcessor.getOriginalDoc().createElement(ID);
		idElem.setAttribute(ROOT, rootValue);
		idElem.setAttribute("extension", qdmLocalVariableName);
		dataCriteriaElem.appendChild(idElem);

		//boolean appendEntryElem = false;

		String isAddCodeTag = templateNode.getAttributes().getNamedItem("addCodeTag").getNodeValue();
		if ("true".equalsIgnoreCase(isAddCodeTag)) { // Add Code Element to
														// DataCriteria Element.
			addCodeElementToDataCriteriaElement(templateNode, dataCriteriaXMLProcessor, qdmNode, dataCriteriaElem);
		}
		Element titleElem = dataCriteriaXMLProcessor.getOriginalDoc().createElement(TITLE);
		titleElem.setAttribute(VALUE, dataType);
		dataCriteriaElem.appendChild(titleElem);
		Element statusCodeElem = dataCriteriaXMLProcessor.getOriginalDoc().createElement("statusCode");
		statusCodeElem.setAttribute(CODE, statusValue);
		dataCriteriaElem.appendChild(statusCodeElem);
		// Add value tag in entry element.
		String addValueSetElement = templateNode.getAttributes().getNamedItem("addValueTag").getNodeValue();
		if ("true".equalsIgnoreCase(addValueSetElement)) {
			Element valueElem = dataCriteriaXMLProcessor.getOriginalDoc().createElement(VALUE);

			Node valueTypeAttr = templateNode.getAttributes().getNamedItem("valueType");
			if (valueTypeAttr != null) {
				valueElem.setAttribute(XSI_TYPE, valueTypeAttr.getNodeValue());
			}

			Node valueCodeSystem = templateNode.getAttributes().getNamedItem("valueCodeSystem");
			Node valueCode = templateNode.getAttributes().getNamedItem("valueCode");
			Node valueCodeSystemName = templateNode.getAttributes().getNamedItem("valueCodeSystemName");

			if ((valueCode != null) && (valueCodeSystem != null)) {
				valueElem.setAttribute("code", valueCode.getNodeValue());
				valueElem.setAttribute("codeSystem", valueCodeSystem.getNodeValue());
				if (valueCodeSystemName != null) {
					valueElem.setAttribute("codeSystemName", valueCodeSystemName.getNodeValue());
				}
			} else {
				String codeSystemOID = qdmNode.getAttributes().getNamedItem("codeSystemOID").getNodeValue();
				String codeSystemName = qdmNode.getAttributes().getNamedItem("taxonomy").getNodeValue();
				String codeSystemVersion = qdmNode.getAttributes().getNamedItem("codeSystemVersion").getNodeValue();
				valueElem.setAttribute("code", codeOID);
				valueElem.setAttribute("codeSystem", codeSystemOID);
				valueElem.setAttribute("codeSystemName", codeSystemName);
				valueElem.setAttribute("codeSystemVersion", codeSystemVersion);

			}

			dataCriteriaElem.appendChild(valueElem);

			
			/*appendEntryElem = true;*/
		}
		if (templateNode.getAttributes().getNamedItem("includeSubTemplate") != null) {
			appendSubTemplateNode(templateNode, dataCriteriaXMLProcessor, templateXMLProcessor, dataCriteriaElem,
					qdmNode);
		}
		/*if (appendEntryElem) {*/
			dataCriteriaSectionElem.appendChild(entryElem);

		//}

	}

	/**
	 * Add Code Element To data Criteria Element based on condition.
	 *
	 * @param templateNode
	 *            - Node
	 * @param dataCriteriaXMLProcessor
	 *            - XmlProcessor
	 * @param qdmNode
	 *            the qdm node
	 * @param dataCriteriaElem
	 *            - Element
	 */
	private void addCodeElementToDataCriteriaElement(Node templateNode, XmlProcessor dataCriteriaXMLProcessor,
			Node qdmNode, Element dataCriteriaElem) {
		String dataType = qdmNode.getAttributes().getNamedItem("datatype").getNodeValue();
		

		// Patient Characteristic data type - contains code tag with valueSetId
		// attribute and no title and value set tag.
		boolean isPatientChar = templateNode.getAttributes().getNamedItem("valueSetId") != null;
		boolean isAddValueSetInCodeTrue = templateNode.getAttributes().getNamedItem("addValueSetInCode") != null;
		boolean isIntervention = ("Intervention, Order".equalsIgnoreCase(dataType)
				|| "Intervention, Performed".equalsIgnoreCase(dataType)
				|| "Intervention, Recommended".equalsIgnoreCase(dataType)
				|| "Intervention, Not Ordered".equalsIgnoreCase(dataType)
				|| "Intervention, Not Performed".equalsIgnoreCase(dataType)
				|| "Intervention, Not Recommended".equalsIgnoreCase(dataType));
		if (isAddValueSetInCodeTrue) {
			Element codeElem = dataCriteriaXMLProcessor.getOriginalDoc().createElement(CODE);
			Node valueTypeAttr = templateNode.getAttributes().getNamedItem("valueType");
			if (valueTypeAttr != null) {
				codeElem.setAttribute(XSI_TYPE, valueTypeAttr.getNodeValue());
			}
			String codeOID = qdmNode.getAttributes().getNamedItem("oid").getNodeValue();
			String codeSystemOID = qdmNode.getAttributes().getNamedItem("codeSystemOID").getNodeValue();
			String codeSystemName = qdmNode.getAttributes().getNamedItem("taxonomy").getNodeValue();
			String codeSystemVersion = qdmNode.getAttributes().getNamedItem("codeSystemVersion").getNodeValue();

			codeElem.setAttribute("code", codeOID);
			codeElem.setAttribute("codeSystem", codeSystemOID);
			codeElem.setAttribute("codeSystemName", codeSystemName);
			codeElem.setAttribute("codeSystemVersion", codeSystemVersion);

			// }
			dataCriteriaElem.appendChild(codeElem);

		} else if (isPatientChar) {
			Element codeElem = dataCriteriaXMLProcessor.getOriginalDoc().createElement(CODE);
			
			String codeOID = qdmNode.getAttributes().getNamedItem("oid").getNodeValue();
			String codeSystemOID = qdmNode.getAttributes().getNamedItem("codeSystemOID").getNodeValue();
			String codeSystemName = qdmNode.getAttributes().getNamedItem("taxonomy").getNodeValue();
			String codeSystemVersion = qdmNode.getAttributes().getNamedItem("codeSystemVersion").getNodeValue();

			codeElem.setAttribute("code", codeOID);
			codeElem.setAttribute("codeSystem", codeSystemOID);
			codeElem.setAttribute("codeSystemName", codeSystemName);
			codeElem.setAttribute("codeSystemVersion", codeSystemVersion);
			
			dataCriteriaElem.appendChild(codeElem);
		} else if (isIntervention) {
			Element codeElem = dataCriteriaXMLProcessor.getOriginalDoc().createElement(CODE);
			String codeOID = qdmNode.getAttributes().getNamedItem("oid").getNodeValue();
			String codeSystemOID = qdmNode.getAttributes().getNamedItem("codeSystemOID").getNodeValue();
			String codeSystemName = qdmNode.getAttributes().getNamedItem("taxonomy").getNodeValue();
			String codeSystemVersion = qdmNode.getAttributes().getNamedItem("codeSystemVersion").getNodeValue();

			codeElem.setAttribute("code", codeOID);
			codeElem.setAttribute("codeSystem", codeSystemOID);
			codeElem.setAttribute("codeSystemName", codeSystemName);
			codeElem.setAttribute("codeSystemVersion", codeSystemVersion);

			dataCriteriaElem.appendChild(codeElem);
		} else {
			Element codeElement = createCodeForDatatype(templateNode, dataCriteriaXMLProcessor);
			if (codeElement != null) {
				dataCriteriaElem.appendChild(codeElement);
			}
		}
	}

	
	/**
	 * Add SubTemplate defined in Template.xml to data criteria Element.
	 *
	 * @param templateNode
	 *            - Node
	 * @param dataCriteriaXMLProcessor
	 *            - XmlProcessor for Data Criteria
	 * @param templateXMLProcessor
	 *            -XmlProcessor for Template Xml.
	 * @param dataCriteriaElem
	 *            - Element
	 * @param qdmNode
	 *            the qdm node
	 * @throws XPathExpressionException
	 *             the x path expression exception
	 */
	private void appendSubTemplateNode(Node templateNode, XmlProcessor dataCriteriaXMLProcessor,
			XmlProcessor templateXMLProcessor, Element dataCriteriaElem, Node qdmNode) throws XPathExpressionException {
		String subTemplateName = templateNode.getAttributes().getNamedItem("includeSubTemplate").getNodeValue();
		Node subTemplateNode = templateXMLProcessor.findNode(templateXMLProcessor.getOriginalDoc(),
				"/templates/subtemplates/" + subTemplateName);
		NodeList subTemplateNodeChilds = templateXMLProcessor.findNodeList(templateXMLProcessor.getOriginalDoc(),
				"/templates/subtemplates/" + subTemplateName + "/child::node()");
		String qdmOidValue = qdmNode.getAttributes().getNamedItem(OID).getNodeValue();
		String qdmName = qdmNode.getAttributes().getNamedItem(NAME).getNodeValue();
		String qdmNameDataType = qdmNode.getAttributes().getNamedItem("datatype").getNodeValue();
		String qdmTaxonomy = qdmNode.getAttributes().getNamedItem(TAXONOMY).getNodeValue();
		if (subTemplateNode.getAttributes().getNamedItem("changeAttribute") != null) {
			String[] attributeToBeModified = subTemplateNode.getAttributes().getNamedItem("changeAttribute")
					.getNodeValue().split(",");
			for (String changeAttribute : attributeToBeModified) {
				NodeList attributedToBeChangedInNode = null;
				attributedToBeChangedInNode = templateXMLProcessor.findNodeList(templateXMLProcessor.getOriginalDoc(),
						"/templates/subtemplates/" + subTemplateName + "//" + changeAttribute);
				if (changeAttribute.equalsIgnoreCase(ID)) {
					String rootId = qdmNode.getAttributes().getNamedItem("uuid").getNodeValue();
					attributedToBeChangedInNode.item(0).getAttributes().getNamedItem("root").setNodeValue(rootId);
					attributedToBeChangedInNode.item(0).getAttributes().getNamedItem("extension")
							.setNodeValue(UUIDUtilClient.uuid());
				} else if (changeAttribute.equalsIgnoreCase(CODE)) {
						String codeOID = qdmNode.getAttributes().getNamedItem("oid").getNodeValue();
						String codeSystemOID = qdmNode.getAttributes().getNamedItem("codeSystemOID").getNodeValue();
						String codeSystemName = qdmNode.getAttributes().getNamedItem("taxonomy").getNodeValue();
						String codeSystemVersion = qdmNode.getAttributes().getNamedItem("codeSystemVersion")
								.getNodeValue();

						if (attributedToBeChangedInNode.item(0).getAttributes()
								.getNamedItem("valueSetVersion") != null) {
							attributedToBeChangedInNode.item(0).getAttributes().removeNamedItem("valueSetVersion");
						}

						if (attributedToBeChangedInNode.item(0).getAttributes().getNamedItem("valueSet") != null) {
							attributedToBeChangedInNode.item(0).getAttributes().removeNamedItem("valueSet");
						}

						Attr attrNodeCode = attributedToBeChangedInNode.item(0).getOwnerDocument()
								.createAttribute("code");
						attrNodeCode.setNodeValue(codeOID);
						attributedToBeChangedInNode.item(0).getAttributes().setNamedItem(attrNodeCode);

						Attr attrNodeCodeSystem = attributedToBeChangedInNode.item(0).getOwnerDocument()
								.createAttribute("codeSystem");
						attrNodeCodeSystem.setNodeValue(codeSystemOID);
						attributedToBeChangedInNode.item(0).getAttributes().setNamedItem(attrNodeCodeSystem);

						Attr attrNodeCodeSystemName = attributedToBeChangedInNode.item(0).getOwnerDocument()
								.createAttribute("codeSystemName");
						attrNodeCodeSystemName.setNodeValue(codeSystemName);
						attributedToBeChangedInNode.item(0).getAttributes().setNamedItem(attrNodeCodeSystemName);

						Attr attrNodeCodeSystemVersion = attributedToBeChangedInNode.item(0).getOwnerDocument()
								.createAttribute("codeSystemVersion");
						attrNodeCodeSystemVersion.setNodeValue(codeSystemVersion);
						attributedToBeChangedInNode.item(0).getAttributes().setNamedItem(attrNodeCodeSystemVersion);
					

				} else if (changeAttribute.equalsIgnoreCase(DISPLAY_NAME)) {
					attributedToBeChangedInNode.item(0).getAttributes().getNamedItem("value")
							.setNodeValue(HQMFDataCriteriaGenerator.removeOccurrenceFromName(qdmName) + " "
									+ qdmTaxonomy + " value set");
				} else if (changeAttribute.equalsIgnoreCase(TITLE)) {
					attributedToBeChangedInNode.item(0).getAttributes().getNamedItem("value")
							.setNodeValue(qdmNameDataType);
				} else if (changeAttribute.equalsIgnoreCase(ITEM)) {
					for (int count = 0; count < attributedToBeChangedInNode.getLength(); count++) {
						Node itemNode = attributedToBeChangedInNode.item(count);
						itemNode.getAttributes().getNamedItem("extension").setNodeValue(extensionValue);
					}

				}
			}
		}
		for (int i = 0; i < subTemplateNodeChilds.getLength(); i++) {
			Node childNode = subTemplateNodeChilds.item(i);
			Node nodeToAttach = dataCriteriaXMLProcessor.getOriginalDoc().importNode(childNode, true);
			XmlProcessor.clean(nodeToAttach);
			dataCriteriaElem.appendChild(nodeToAttach);
		}
	}

	/**
	 * Creates the code for datatype.
	 *
	 * @param templateNode
	 *            the template node
	 * @param dataCriteriaXMLProcessor
	 *            the data criteria xml processor
	 * @return the element
	 */
	protected Element createCodeForDatatype(Node templateNode, XmlProcessor dataCriteriaXMLProcessor) {
		Node codeAttr = templateNode.getAttributes().getNamedItem(CODE);
		Node codeSystemAttr = templateNode.getAttributes().getNamedItem(CODE_SYSTEM);
		Node codeSystemNameAttr = templateNode.getAttributes().getNamedItem(CODE_SYSTEM_NAME);
		Node codeDisplayNameAttr = templateNode.getAttributes().getNamedItem(CODE_SYSTEM_DISPLAY_NAME);
		Element codeElement = null;
		if ((codeAttr != null) || (codeSystemAttr != null) || (codeSystemNameAttr != null)
				|| (codeDisplayNameAttr != null)) {
			codeElement = dataCriteriaXMLProcessor.getOriginalDoc().createElement(CODE);
			if (codeAttr != null) {
				codeElement.setAttribute(CODE, codeAttr.getNodeValue());
			}
			if (codeSystemAttr != null) {
				codeElement.setAttribute(CODE_SYSTEM, codeSystemAttr.getNodeValue());
			}
			if (codeSystemNameAttr != null) {
				codeElement.setAttribute(CODE_SYSTEM_NAME, codeSystemNameAttr.getNodeValue());
			}
			if (codeDisplayNameAttr != null) {
				Element displayNameElem = dataCriteriaXMLProcessor.getOriginalDoc().createElement(DISPLAY_NAME);
				displayNameElem.setAttribute(VALUE, codeDisplayNameAttr.getNodeValue());
				codeElement.appendChild(displayNameElem);
			}
		}
		return codeElement;
	}

}
