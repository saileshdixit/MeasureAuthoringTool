package mat.model;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MatValueSet implements IsSerializable {
	private String ID;
	private String displayName;
	private String version;
	private MatConceptList conceptList;
	private String source;
	private String type;
	private String binding;
	private String status;
	private String revisionDate;
	private String definition;
	private List<MatGroup> groupList;
	private List<MatValueSet> grouppedValueSet;
	
	public String toString(){
		return getID() +" - "+ getDisplayName()+ " - "+ getType();
	}
	public boolean isGrouping(){
		return (getType().equalsIgnoreCase("grouping"));
	}	
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public MatConceptList getConceptList() {
		return conceptList;
	}
	public void setConceptList(MatConceptList conceptList) {
		this.conceptList = conceptList;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBinding() {
		return binding;
	}
	public void setBinding(String binding) {
		this.binding = binding;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRevisionDate() {
		return revisionDate;
	}
	public void setRevisionDate(String revisionDate) {
		this.revisionDate = revisionDate;
	}
	public List<MatGroup> getGroupList() {
		return groupList;
	}
	public void setGroupList(List<MatGroup> groupList) {
		this.groupList = groupList;
	}
	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	public List<MatValueSet> getGrouppedValueSet() {
		return grouppedValueSet;
	}
	public void setGrouppedValueSet(List<MatValueSet> grouppedValueSet) {
		this.grouppedValueSet = grouppedValueSet;
	}
}