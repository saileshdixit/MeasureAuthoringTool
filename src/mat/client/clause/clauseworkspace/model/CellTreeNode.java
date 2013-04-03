package mat.client.clause.clauseworkspace.model;

import java.util.List;


public interface CellTreeNode {

	short MASTER_ROOT_NODE = 1;
	
	short ROOT_NODE = 2;
	
	short CLAUSE_NODE = 3;
	
	public CellTreeNode appendChild(CellTreeNode child);
	
	public CellTreeNode removeChild(CellTreeNode child);
	
	public CellTreeNode createChild(String name, String label, short nodeType);
	
	public short getNodeType();
	
	public void setNodeType(short nodeType);
	
	public CellTreeNode getParent();
	
	public void setParent(CellTreeNode treeNode);
	
	public boolean hasChildren();
	
	public CellTreeNode cloneNode();
	
	public boolean isOpen();
	
	public void setOpen(boolean isOpen);
	
	public String getName();
	
	public void setName(String name);
	
	public String getLabel();
	
	public void setLabel(String label);
	
	public boolean isRemovable();
	
	public void setRemovable(boolean isRemovable);
	
	public boolean isEditable();
	
	public boolean setEditable(boolean isEditable);
	
	public List<CellTreeNode> getChilds();
	
	public void setChilds(List<CellTreeNode> childs);
	
}
