package mat.client.measurepackage;

import java.util.List;
import mat.model.QualityDataSetDTO;
import com.google.gwt.user.client.rpc.IsSerializable;

// TODO: Auto-generated Javadoc
/**
 * The Class MeasurePackageClauseDetail.
 */
public class MeasurePackageClauseDetail implements IsSerializable, Comparable<MeasurePackageClauseDetail> {
	
	/** The id. */
	private String id;
	
	/** The name. */
	private String name;
	
	/** The type. */
	private String type;
	
	/** The item count list. */
	private List<QualityDataSetDTO> itemCountList;
	
	/** The associated population. */
	private String  associatedPopulationUUID;
	
	private boolean isAssociatedPopulation;
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the new id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the new type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Gets the item count list.
	 *
	 * @return the item count list
	 */
	public List<QualityDataSetDTO> getItemCountList() {
		return itemCountList;
	}
	
	/**
	 * Sets the item count list.
	 *
	 * @param itemCountList the new item count list
	 */
	public void setItemCountList(List<QualityDataSetDTO> itemCountList) {
		this.itemCountList = itemCountList;
	}
	
	
	/**
	 * @return the associatedPopulationUUID
	 */
	public String getAssociatedPopulationUUID() {
		return associatedPopulationUUID;
	}
	
	/**
	 * @param associatedPopulationUUID the associatedPopulationUUID to set
	 */
	public void setAssociatedPopulationUUID(String associatedPopulationUUID) {
		this.associatedPopulationUUID = associatedPopulationUUID;
	}
	
	/**
	 * @return the isAssociatedPopulation
	 */
	public boolean isAssociatedPopulation() {
		return isAssociatedPopulation;
	}
	
	/**
	 * @param isAssociatedPopulation the isAssociatedPopulation to set
	 */
	public void setAssociatedPopulation(boolean isAssociatedPopulation) {
		this.isAssociatedPopulation = isAssociatedPopulation;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MeasurePackageClauseDetail oth) {
		Integer groupingOrder = Integer.parseInt(MeasureGroupingOrder.valueOf(type).getStatusCode());
		Integer otherGroupingOrder = Integer.parseInt(MeasureGroupingOrder.valueOf(oth.type).getStatusCode());
		return groupingOrder.compareTo(otherGroupingOrder);
	}
	
	
	
}
