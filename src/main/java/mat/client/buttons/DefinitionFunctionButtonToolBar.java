package mat.client.buttons;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

import mat.client.shared.MatContext;


/**
 * The Button Tool Bar for the Definition and Function sections
 * Includes 'Insert', 'Save', 'Edit', 'Delete', 'save', 'cancel', 'erase', and Expression Builder
 * 
 */
public class DefinitionFunctionButtonToolBar extends Composite {
	private Button insertButton, editButton, saveButton, deleteButton, eraseButton, cancelButton, infoButton, expressionBuilderButton;
	private Button timingExpIcon = new Button();
	private HorizontalPanel buttonLayout = new HorizontalPanel();
	private ButtonGroup infoButtonGroup = new ButtonGroup();
	private String sectionName;

	
	public DefinitionFunctionButtonToolBar(String sectionName) {
		this.sectionName = sectionName;
		buildInfoButtonGroup();
		
		buttonLayout.getElement().setId("cql_buttonLayout_HorizontalPanel");		

		addEditButton();
		addSaveButton();
		addEraseButton();
		addInsertButton();
		addDeleteButton();
		addCancelButton();
		
		MatContext.get().getCurrentReleaseVersion(new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				double version = Double.parseDouble(result.replace("v", ""));
				if(version >= 5.7) {
					addExpressionBuilderButton();
				}
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(MatContext.get().getMessageDelegate().GENERIC_ERROR_MESSAGE);
			}
		});
		
		
		initWidget(buttonLayout);	
	}
	
	private void addExpressionBuilderButton() {
		expressionBuilderButton = new ExpressionBuilderButton(sectionName);
		expressionBuilderButton.getElement().setAttribute("aria-label", "Click the Expression Builder Button to open the expression builder.");
		infoButtonGroup.add(expressionBuilderButton);
		
		if(!sectionName.equalsIgnoreCase("definition")) {
			expressionBuilderButton.setVisible(false);
		}
	}

	private void addCancelButton() {
		cancelButton = new CancelToolBarButton(sectionName);
		buttonLayout.add(cancelButton);
	}
	
	private void addEraseButton() {
		eraseButton = new EraseToolBarButton(sectionName);
		buttonLayout.add(eraseButton);
	}
	
	private void addDeleteButton() {
		deleteButton = new DeleteToolBarButton(sectionName);
		buttonLayout.add(deleteButton);
	}
	
	private void addEditButton() {
		editButton = new EditToolBarButton(sectionName);
		editButton.setVisible(false);
		buttonLayout.add(editButton);
	}
	
	private void addSaveButton() {
		saveButton = new SaveToolBarButton(sectionName);
		buttonLayout.add(saveButton);
	}
	
	private void addInsertButton() {
		insertButton = new InsertToolBarButton(sectionName);
		buttonLayout.add(insertButton);
	}


	private void buildInfoButtonGroup() {
		infoButton = new InfoToolBarButton(sectionName);
		DropDownMenu dropDownMenu = new InfoDropDownMenu();
		
		infoButtonGroup.getElement().setAttribute("class", "btn-group");
		infoButtonGroup.add(infoButton);
		infoButtonGroup.add(dropDownMenu);
		infoButtonGroup.getElement().setAttribute("style", "margin-top:-10px;");
	}
	
	public void setEnabled(boolean isEnabled){
		insertButton.setEnabled(isEnabled);
		saveButton.setEnabled(isEnabled);
		deleteButton.setEnabled(isEnabled);
		eraseButton.setEnabled(isEnabled);
		infoButton.setEnabled(isEnabled);
		cancelButton.setEnabled(isEnabled);
		expressionBuilderButton.setEnabled(isEnabled);
	}
	
	public Button getInsertButton() {
		return insertButton;
	}

	public Button getSaveButton() {
		return saveButton;
	}

	public Button getDeleteButton() {
		return deleteButton;
	}

	public Button getEraseButton() {
		return eraseButton;
	}

	public Button getInfoButton(){
		return infoButton;
	}
	
	public Button getExpressionBuilderButton() {
		return expressionBuilderButton;
	}

	public Button getTimingExpButton(){
		return timingExpIcon;
	}

	public Button getCloseButton(){
		return cancelButton;
	}

	public ButtonGroup getInfoButtonGroup() {
		return infoButtonGroup;
	}

	public Button getEditButton() {
		return editButton;
	}
	
	public HorizontalPanel getButtonLayout() {
		return buttonLayout;
	}
}
