package mat.shared;

import mat.client.shared.GenericResult;
import mat.model.cql.CQLDefinition;
import mat.model.cql.CQLModel;
import mat.model.cql.CQLParameter;

// TODO: Auto-generated Javadoc
/**
 * The Class SaveUpdateCQLResult.
 */
public class SaveUpdateCQLResult extends GenericResult{
	
	CQLModel cqlModel;
	
	public CQLModel getCqlModel() {
		return cqlModel;
	}
	
	public void setCqlModel(CQLModel cqlModel) {
		this.cqlModel = cqlModel;
	}
	
	/** The definition. */
	CQLDefinition definition;
	
	
	/** The parameter. */
	CQLParameter parameter;
	
	public static final int NAME_NOT_UNIQUE = 1;
	
	
	
	
	/**
	 * Gets the parameter.
	 *
	 * @return the parameter
	 */
	public CQLParameter getParameter() {
		return parameter;
	}
	
	/**
	 * Sets the parameter.
	 *
	 * @param parameter the new parameter
	 */
	public void setParameter(CQLParameter parameter) {
		this.parameter = parameter;
	}
	
	
}
