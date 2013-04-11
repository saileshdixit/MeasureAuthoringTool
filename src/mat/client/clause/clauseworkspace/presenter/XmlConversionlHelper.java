package mat.client.clause.clauseworkspace.presenter;

import java.util.ArrayList;
import java.util.List;

import mat.client.clause.clauseworkspace.model.CellTreeNode;
import mat.client.clause.clauseworkspace.model.CellTreeNodeImpl;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class XmlConversionlHelper {

	private static final String NAMESPACE_XML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\r\n";



	/**
	 * Creates CellTreeNode object which has list of children objects and a parent object from the XML
	 * @param xml
	 * @return CellTreeNode
	 */ 
	public static CellTreeNode createCellTreeNode(String xml, String tagName){
		Node node = null;
		CellTreeNode mainNode = new CellTreeNodeImpl();
		List<CellTreeNode> childs = new ArrayList<CellTreeNode>();
		Document document = null;
		if(xml != null && xml.trim().length() > 0){
			document = XMLParser.parse(xml);
			NodeList nodeList = document.getElementsByTagName(tagName);
			if(nodeList.getLength() > 0){
				for (int i = 0; i < nodeList.getLength(); i++) {
					if("measure".equals(nodeList.item(i).getParentNode().getNodeName())){//Gets the node based on tag name passed and the parent is Measure
						node = nodeList.item(i);
					}
				}
			}
			if(node != null){
				mainNode.setName(tagName);
				createCellTreeNodeChilds(mainNode, node, childs);
			}
		}
		if(node == null){
			mainNode.setName(tagName);
			childs.add(createRootNode(tagName));
			mainNode.setChilds(childs);
		}
		
		//Process mainNode for Scoring Type.
		if(document != null){
			checkScoringType(mainNode,document);
		}
		
		return mainNode;
	}


	private static CellTreeNode createRootNode(String tagName){
		CellTreeNode parent = new CellTreeNodeImpl();
		List<CellTreeNode> childs = new ArrayList<CellTreeNode>();
		if(tagName.equalsIgnoreCase("populations")){
			parent.setName(ClauseConstants.get(tagName));
			parent.setLabel(ClauseConstants.get(tagName));
			parent.setNodeType(CellTreeNode.MASTER_ROOT_NODE);
			for (int i = 0; i < ClauseConstants.getPopulationsChildren().length; i++) {
				String nodeValue =ClauseConstants.getPopulationsChildren()[i];
				//Adding Root nodes under Population.
				CellTreeNode child = createChild(nodeValue,nodeValue,CellTreeNode.ROOT_NODE,parent);
				childs.add(child);
				//Clause Nodes should not have 's' in end. For example 'Numerators' child should be 'Numerator'.
				String name = nodeValue.substring(0,nodeValue.lastIndexOf('s')) + " "  + 1;
				//Adding Clause Nodes
				List<CellTreeNode> subChilds = new ArrayList<CellTreeNode>();
				subChilds.add(createChild(name, name, CellTreeNode.CLAUSE_NODE, child));
				// Adding First 'AND' under clause node.
				for(int j=0;j<subChilds.size();j++){
					List<CellTreeNode> logicalOp = new ArrayList<CellTreeNode>();
					logicalOp.add(createChild(ClauseConstants.AND, ClauseConstants.AND, CellTreeNode.LOGICAL_OP_NODE, subChilds.get(j)));
					subChilds.get(j).setChilds(logicalOp);
				}
				child.setChilds(subChilds);
			}
			parent.setChilds(childs);
		}else if("measureObservations".equals(tagName)){
			parent.setName(ClauseConstants.get(tagName));
			parent.setLabel(ClauseConstants.get(tagName));
			parent.setNodeType(CellTreeNode.ROOT_NODE);
			CellTreeNode clauseNode = createChild("Measure Observation 1", "Measure Observation 1", CellTreeNode.CLAUSE_NODE, parent);
			childs.add(clauseNode);
			parent.setChilds(childs);
			List<CellTreeNode> logicalOp = new ArrayList<CellTreeNode>();
			logicalOp.add(createChild(ClauseConstants.AND,ClauseConstants.AND, CellTreeNode.LOGICAL_OP_NODE, clauseNode));
			clauseNode.setChilds(logicalOp);
		}else if("strata".equalsIgnoreCase(tagName)){
			parent.setName(ClauseConstants.get(tagName));
			parent.setLabel(ClauseConstants.get(tagName));
			parent.setNodeType(CellTreeNode.ROOT_NODE);
			CellTreeNode clauseNode = createChild("Stratum 1", "Stratum 1", CellTreeNode.CLAUSE_NODE, parent);
			childs.add(clauseNode);
			parent.setChilds(childs);
			List<CellTreeNode> logicalOp = new ArrayList<CellTreeNode>();
			logicalOp.add(createChild(ClauseConstants.AND, ClauseConstants.AND, CellTreeNode.LOGICAL_OP_NODE, clauseNode));
			clauseNode.setChilds(logicalOp);
		}
		return parent;
	}


	private static CellTreeNode createChild(String name, String label, short nodeType, CellTreeNode parent){
		CellTreeNode child = new CellTreeNodeImpl();
		child.setName(name);
		child.setLabel(label);
		child.setParent(parent);
		child.setNodeType(nodeType);
		return child;
	}


	private static void createCellTreeNodeChilds(CellTreeNode parent, Node root, List<CellTreeNode> childs){
		String nodeName = root.getNodeName();
		String nodeValue = root.hasAttributes() ? root.getAttributes().getNamedItem(ClauseConstants.DISPLAY_NAME).getNodeValue() : nodeName;

		CellTreeNode child = new CellTreeNodeImpl();//child Object
		if(nodeValue.length() > 0){
			setCellTreeNodeValues(root, parent, child, childs);// Create complete child Object with parent and sub Childs
		}



		parent.setChilds(childs);// set parent's childs
		NodeList nodes = root.getChildNodes();// get Child nodes for the Processed node and repeat the process
		for(int i = 0; i < nodes.getLength(); i++){
			if(i == 0){
				if(child.getChilds() == null){ 
					childs = new ArrayList<CellTreeNode>();
				}else{
					childs  = child.getChilds();
				}
			}
			Node node = nodes.item(i);
			String name = node.getNodeName().replaceAll("\n\r", "").trim();
			if(!(name.equalsIgnoreCase("#text") && name.isEmpty())){	
				createCellTreeNodeChilds(child, node, childs);
			}
		}

	}


	/**
	 * Creating XML from GWT tree using GWT Document object
	 * @return XML String
	 */
	public static String createXmlFromTree(CellTreeNode model){
		Document doc = XMLParser.createDocument();
		if(model != null){
			String returnXml = NAMESPACE_XML + createXmlFromTree(model, doc, null);
			return returnXml;
		}

		return null;
	}



	/**
	 * Iterating through the Tree's Children to create the Document Element, Nodes and Attributes.
	 * @param treeItem
	 * @param doc
	 * @param node
	 * @param element
	 * @return
	 */
	private static String createXmlFromTree(CellTreeNode cellTreeNode, Document doc, Node node) {
		System.out.println(cellTreeNode.getName());
		Element element = getNodeName(cellTreeNode, doc);

		if(node != null){
			node = node.appendChild(element);
		}else{
			node = doc.appendChild(element);
		}

		if(cellTreeNode.getChilds() != null && cellTreeNode.getChilds().size() > 0){
			for (CellTreeNode model : cellTreeNode.getChilds()) {
				createXmlFromTree(model, doc, node);
			}
		}
		return doc.toString();
	}




	private static void setCellTreeNodeValues(Node node, CellTreeNode parent, CellTreeNode child, List<CellTreeNode> childs){
		String nodeName = node.getNodeName();
		String nodeValue = node.hasAttributes() ? node.getAttributes().getNamedItem(ClauseConstants.DISPLAY_NAME).getNodeValue() : nodeName;
		short cellTreeNodeType = 0;
		if(nodeName.equalsIgnoreCase(ClauseConstants.MASTER_ROOT_NODE_POPULATION)){
			cellTreeNodeType =  CellTreeNode.MASTER_ROOT_NODE;				
		}else if(ClauseConstants.ROOT_NODES.contains(nodeName)){
			cellTreeNodeType =  CellTreeNode.ROOT_NODE;				
		}else if(nodeName.equalsIgnoreCase(ClauseConstants.CLAUSE_TYPE)){
			cellTreeNodeType =  CellTreeNode.CLAUSE_NODE;
		}else if(nodeName.equalsIgnoreCase(ClauseConstants.LOG_OP)){
			cellTreeNodeType = CellTreeNode.LOGICAL_OP_NODE;			
		}

		child.setName(nodeValue);//set the name to Child
		child.setLabel(nodeValue);
		child.setNodeType(cellTreeNodeType);		
		child.setParent(parent);// set parent in child
		childs.add(child);// add child to child list
	}


	private static Element getNodeName(CellTreeNode cellTreeNode, Document document){
		Element element = null;
		switch (cellTreeNode.getNodeType()) {
		case CellTreeNode.MASTER_ROOT_NODE:
			element = document.createElement(ClauseConstants.get(cellTreeNode.getName()));
			element.setAttribute(ClauseConstants.DISPLAY_NAME, cellTreeNode.getName());
			break;
		case CellTreeNode.ROOT_NODE:
			element = document.createElement(ClauseConstants.get(cellTreeNode.getName()));
			element.setAttribute(ClauseConstants.DISPLAY_NAME, cellTreeNode.getName());
			break;
		case CellTreeNode.CLAUSE_NODE:
			element = document.createElement(ClauseConstants.CLAUSE_TYPE);
			element.setAttribute(ClauseConstants.DISPLAY_NAME, cellTreeNode.getName());
			element.setAttribute(ClauseConstants.TYPE, toCamelCase(cellTreeNode.getName().substring(0, cellTreeNode.getName().lastIndexOf(" "))));
			break;
		case CellTreeNode.LOGICAL_OP_NODE:
			element = document.createElement(ClauseConstants.LOG_OP);
			element.setAttribute(ClauseConstants.DISPLAY_NAME, cellTreeNode.getName());
			element.setAttribute(ClauseConstants.TYPE, toCamelCase(cellTreeNode.getName()));
			break;
		default:
			element = document.createElement(cellTreeNode.getName());
			break;
		}
		return element;
	}

	/**
	 * 
	 * Method to convert case of string into camel case.
	 * 
	 * */

	private static String toCamelCase(String name){
		name = name.toLowerCase();
		String[] parts = name.split(" ");
		String camelCaseString = parts[0].substring(0,1).toLowerCase() + parts[0].substring(1);
		for (int i = 1; i < parts.length; i++) {		   
			camelCaseString = camelCaseString + toProperCase(parts[i]);
		}
		return camelCaseString;
	}

	private static String toProperCase(String s) {
		return s.substring(0, 1).toUpperCase() +
		s.substring(1).toLowerCase();
	}
	
	private static void checkScoringType(CellTreeNode mainNode,
			Document document) {
		
		/*
		 * Find out the Scoring type for the measure by searching for the
		 * 'scoring' element and checking the id attribute.
		 * ex: <scoring id="CONTVAR"> or <scoring id="PROPOR"> or <scoring id="RATIO">
		 */
		
		NodeList nodeList = document.getElementsByTagName("scoring");
		
		if(nodeList != null && nodeList.getLength() > 0){
			Node scoringNode = nodeList.item(0);
			Node scoringIdAttribute = scoringNode.getAttributes().getNamedItem("id");
			String scoringIdAttributeValue = scoringIdAttribute.getNodeValue();
			List<CellTreeNode> nodesToBeRemoved = new ArrayList<CellTreeNode>();
			CellTreeNode populationsNode = mainNode.getChilds().get(0);
			
			if("CONTVAR".equals(scoringIdAttributeValue)){
				for(CellTreeNode childNode : populationsNode.getChilds()){
					String nodeName = childNode.getName();
					if(!("Initial Patient Populations".equals(nodeName)) && !("Measure Populations".equals(nodeName))){
						nodesToBeRemoved.add(childNode);
					}
				}
			}else if ("PROPOR".equals(scoringIdAttributeValue)){
				for(CellTreeNode childNode : populationsNode.getChilds()){
					String nodeName = childNode.getName();
					if("Numerator Exclusions".equals(nodeName) || "Measure Populations".equals(nodeName)){
						nodesToBeRemoved.add(childNode);
					}
				}
			}else if ("RATIO".equals(scoringIdAttributeValue)){
				for(CellTreeNode childNode : populationsNode.getChilds()){
					String nodeName = childNode.getName();
					if("Denominator Exceptions".equals(nodeName) || "Measure Populations".equals(nodeName)){
						nodesToBeRemoved.add(childNode);
					}
				}
			}
			
			for(CellTreeNode removeNode:nodesToBeRemoved){
				populationsNode.removeChild(removeNode);
			}
			createNewNodes(scoringIdAttributeValue,populationsNode);
		}
	}


	private static void createNewNodes(String scoringType,
			CellTreeNode populationsNode) {
		List<String> scoreBasedNodes = new ArrayList<String>();
		
		if("CONTVAR".equals(scoringType)){
			scoreBasedNodes.add("Initial Patient Populations");
			scoreBasedNodes.add("Measure Populations");
		}else if("PROPOR".equals(scoringType)){
			scoreBasedNodes.add("Initial Patient Populations");
			scoreBasedNodes.add("Numerators");
			scoreBasedNodes.add("Denominators");
			scoreBasedNodes.add("Denominator Exclusions");
			scoreBasedNodes.add("Denominator Exceptions");
		}else if("RATIO".equals(scoringType)){
			scoreBasedNodes.add("Initial Patient Populations");
			scoreBasedNodes.add("Numerators");
			scoreBasedNodes.add("Numerator Exclusions");
			scoreBasedNodes.add("Denominators");
			scoreBasedNodes.add("Denominator Exclusions");
		}
		
		for(String nodeName:scoreBasedNodes){
			boolean isNodePresent = false;
			for(CellTreeNode childNode : populationsNode.getChilds()){
				String childNodeName = childNode.getName();
				if(childNodeName.equals(nodeName)){
					isNodePresent = true;
					break;
				}
			}
				
			if(!isNodePresent){
				CellTreeNode child = createChild(nodeName,nodeName,CellTreeNode.ROOT_NODE,populationsNode);
				populationsNode.appendChild(child);
				
				String clauseName = nodeName.substring(0,nodeName.length()-1) + " 1";
				CellTreeNode clauseNode = createChild(clauseName, clauseName, CellTreeNode.CLAUSE_NODE, child);
				child.appendChild(clauseNode);
					
				CellTreeNode logicalOpNode = createChild(ClauseConstants.AND, ClauseConstants.AND, CellTreeNode.LOGICAL_OP_NODE, clauseNode);
				clauseNode.appendChild(logicalOpNode);
				
			}
		}
	}

}
