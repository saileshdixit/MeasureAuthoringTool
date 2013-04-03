package mat.client.clause.clauseworkspace.presenter;

import mat.client.clause.clauseworkspace.model.CellTreeNode;
import mat.client.shared.ErrorMessageDisplay;
import mat.client.shared.SuccessMessageDisplay;

import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

public interface XmlTreeDisplay {
		
		public CellTree getXmlTree();

		public Button getSaveButton();

		public Widget asWidget();
		
		public SuccessMessageDisplay getSuccessMessageDisplay();
		
		public ErrorMessageDisplay getErrorMessageDisplay();
		
		public void clearMessages();
		
		public void setEnabled(boolean enable);
		
		public CellTreeNode getSelectedNode();
		
		public void addNode(String name, String label, short nodeType);

		public void removeNode();
		
		public void copy();
		
		public void paste(String name, String label);
		
		public CellTreeNode getCopiedNode();
		
	}