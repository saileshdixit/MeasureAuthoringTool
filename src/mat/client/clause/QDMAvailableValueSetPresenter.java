package mat.client.clause;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mat.client.Mat;
import mat.client.MatPresenter;
import mat.client.codelist.HasListBox;
import mat.client.codelist.service.SaveUpdateCodeListResult;
import mat.client.measure.metadata.CustomCheckBox;
import mat.client.measure.service.MeasureServiceAsync;
import mat.client.shared.DateBoxWithCalendar;
import mat.client.shared.ErrorMessageDisplay;
import mat.client.shared.ErrorMessageDisplayInterface;
import mat.client.shared.ListBoxMVP;
import mat.client.shared.MatContext;
import mat.client.shared.SuccessMessageDisplay;
import mat.client.shared.SuccessMessageDisplayInterface;
import mat.client.umls.service.VSACAPIServiceAsync;
import mat.client.umls.service.VsacApiResult;
import mat.model.CodeListSearchDTO;
import mat.model.MatValueSet;
import mat.model.QualityDataSetDTO;
import mat.shared.ConstantMessages;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosureEvent;
import com.google.gwt.user.client.ui.DisclosureHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("deprecation")
public class QDMAvailableValueSetPresenter  implements MatPresenter{

	private SearchDisplay searchDisplay;
	private MatValueSet currentMatValueSet;
	MeasureServiceAsync measureService = MatContext.get().getMeasureService();
	ArrayList<QualityDataSetDTO> appliedQDMList = new ArrayList<QualityDataSetDTO>();
	QualityDataSetDTO  modifyValueSetDTO;
	mat.client.clause.QDSAppliedListPresenter.SearchDisplay qdsAppliedListPresenterDisplay;
	VSACAPIServiceAsync vsacapiService  = MatContext.get().getVsacapiServiceAsync();
	
	public static interface SearchDisplay {		
		public CustomCheckBox getSpecificOccurrenceInput();
		public String getDataTypeValue(ListBoxMVP inputListBox);
		public SuccessMessageDisplayInterface getApplyToMeasureSuccessMsg();
		public ErrorMessageDisplayInterface getErrorMessageDisplay();
		public Button getCancel();
		public String getDataTypeText(ListBoxMVP inputListBox);
		public DisclosurePanel getDisclosurePanel();
		public Button getPsuedoQDMToMeasure();
		public TextBox getUserDefinedInput();
		public ListBoxMVP getAllDataTypeInput();
		void setAllDataTypeOptions(List<? extends HasListBox> texts);
		public DisclosurePanel getDisclosurePanelCellTable();
		public SuccessMessageDisplay getSuccessMessageUserDefinedPanel();
		public ErrorMessageDisplay getErrorMessageUserDefinedPanel();
		public Button getUserDefinedCancel();
				
		public TextBox getOIDInput();
		public DateBoxWithCalendar getVersionInput();
		public Button getRetrieveButton();
		public VerticalPanel getValueSetDetailsPanel();
		public ListBoxMVP getDataTypesListBox();
		public SuccessMessageDisplay getSuccessMessageDisplay();
		public void setDataTypesListBoxOptions(List<? extends HasListBox> texts);
		public void clearVSACValueSetMessages();
		public void buildValueSetDetailsWidget(ArrayList<MatValueSet> matValueSets);
		public Button getApplyToMeasureButton();
		public MatValueSet getCurrentMatValueSet();
		public void resetVSACValueSetWidget();
		public Button getCloseButton();
		public Widget asWidget();
	}
	
	
	public QDMAvailableValueSetPresenter(SearchDisplay sDisplayArg , QualityDataSetDTO dataSetDTO, mat.client.clause.QDSAppliedListPresenter.SearchDisplay qdsAppliedListPresenterDisplay){
		this.searchDisplay = sDisplayArg;
		this.modifyValueSetDTO = dataSetDTO;
		this.qdsAppliedListPresenterDisplay = qdsAppliedListPresenterDisplay;
		this.appliedQDMList = (ArrayList<QualityDataSetDTO>) qdsAppliedListPresenterDisplay.getAllAppliedQDMList();
		
		searchDisplay.getDisclosurePanel().addEventHandler(new DisclosureHandler()
	    {

	        public void onClose(DisclosureEvent event)
	        {
	        	searchDisplay.getUserDefinedInput().setText("");
	        	searchDisplay.getAllDataTypeInput().setItemSelected(0, true);
	        	displaySearch();
	        	searchDisplay.getDisclosurePanelCellTable().setOpen(true);
	        }

	        public void onOpen(DisclosureEvent event)
	        {
	        	populateAllDataType();
	            displaySearch();
	            searchDisplay.getDisclosurePanelCellTable().setOpen(false);
	        }
	    });
		
		
		searchDisplay.getDisclosurePanelCellTable().addEventHandler(new DisclosureHandler()
	    {

	        public void onClose(DisclosureEvent event)
	        {
	        	searchDisplay.getUserDefinedInput().setText("");
	        	displaySearch();
	        	searchDisplay.getDisclosurePanel().setOpen(true);
	        }

	        public void onOpen(DisclosureEvent event)
	        {
	        	displaySearch();
	            searchDisplay.getDisclosurePanel().setOpen(false);
	        }
	    });
				
		searchDisplay.getUserDefinedInput().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.getSuccessMessageUserDefinedPanel().clear();
				searchDisplay.getErrorMessageUserDefinedPanel().clear();
			}
		});
		
		searchDisplay.getAllDataTypeInput().addFocusHandler(new FocusHandler() {
			
			@Override
			public void onFocus(FocusEvent event) {
				searchDisplay.getSuccessMessageUserDefinedPanel().clear();
				searchDisplay.getErrorMessageUserDefinedPanel().clear();
				
			}
		});
		
		searchDisplay.getPsuedoQDMToMeasure().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				modifyQDM(true);
			}
		});
		
		searchDisplay.getApplyToMeasureButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MatContext.get().getMeasureService().getMeasureXMLForAppliedQDM(MatContext.get().getCurrentMeasureId(),true, new AsyncCallback<ArrayList<QualityDataSetDTO>>(){

					@Override
					public void onFailure(Throwable caught) {
						searchDisplay.getErrorMessageDisplay().setMessage(MatContext.get().getMessageDelegate().getGenericErrorMessage());
					}

					@Override
					public void onSuccess(ArrayList<QualityDataSetDTO> result) {
						appliedQDMList = result;
						currentMatValueSet = searchDisplay.getCurrentMatValueSet();
						modifyQDM(false);
					}
					
				});
				
			}
		});
		
		searchDisplay.getCloseButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ModifyQDMDialogBox.dialogBox.hide();
				//This is to reload applied QDM List.
				reloadAppliedQDMList();
			}
		});
		
		searchDisplay.getUserDefinedCancel().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ModifyQDMDialogBox.dialogBox.hide();
				//This is to reload applied QDM List.
				reloadAppliedQDMList();
			}
		});
					
		searchDisplay.getRetrieveButton().addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				searchDisplay.clearVSACValueSetMessages();
				searchValueSetInVsac(searchDisplay.getOIDInput().getValue(), searchDisplay.getVersionInput().getValue());				
			}
		});
		
	}
	
	private void searchValueSetInVsac(String oid, String version){				
		//OID validation.
		if (oid==null || oid.trim().isEmpty()) {
			searchDisplay.getErrorMessageDisplay().setMessage(MatContext.get().getMessageDelegate().getUMLS_OID_REQUIRED());
			searchDisplay.getValueSetDetailsPanel().setVisible(false);
			return;
		}
		
		showSearchingBusy(true);
		vsacapiService.getValueSetByOIDAndVersion(oid, new AsyncCallback<VsacApiResult>() {			
			@Override
			public void onFailure(Throwable caught) {
				searchDisplay.getErrorMessageDisplay().setMessage(MatContext.get().getMessageDelegate().getVSAC_RETRIEVE_FAILED());
				searchDisplay.getValueSetDetailsPanel().setVisible(false);
				showSearchingBusy(false);
			}

			@Override
			public void onSuccess(VsacApiResult result) {
				if(result.isSuccess()) {
					searchDisplay.buildValueSetDetailsWidget(result.getVsacResponse());
					searchDisplay.getValueSetDetailsPanel().setVisible(true);
				}else{					
					String message = convertMessage(result.getFailureReason());
					searchDisplay.getErrorMessageDisplay().setMessage(message);
					searchDisplay.getValueSetDetailsPanel().setVisible(false);
				}
				showSearchingBusy(false);
			}
		});	
	}
	
	private String convertMessage(int id) {
		String message;
		switch(id) {
			case VsacApiResult.UMLS_NOT_LOGGEDIN:
				message = MatContext.get().getMessageDelegate().getUMLS_NOT_LOGGEDIN();
				break;
			case VsacApiResult.OID_REQUIRED:
				message = MatContext.get().getMessageDelegate().getUMLS_OID_REQUIRED();
				break;
			
			default: message = MatContext.get().getMessageDelegate().getUnknownFailMessage();
		}
		return message;
	}
	
	private void populateDataTypesListBox(){
		MatContext.get().getListBoxCodeProvider().getAllDataType(new AsyncCallback<List<? extends HasListBox>>() {

			@Override
			public void onFailure(Throwable caught) {
				searchDisplay.clearVSACValueSetMessages();
				searchDisplay.getErrorMessageDisplay().setMessage(MatContext.get().getMessageDelegate().getGenericErrorMessage());
			}

			@Override
			public void onSuccess(List<? extends HasListBox> result) {
				Collections.sort(result, new HasListBox.Comparator());
				searchDisplay.setDataTypesListBoxOptions(result);
			}
		});

	}
	
	/***
	 * Method to find if selected Available value set is a valid modifiable selection. If yes, then call to updateAppliedQDMList method is made. 
	 * 
	 * */
	protected void modifyQDM(boolean isUserDefined) {		
		if(!isUserDefined){//Normal Available QDM Flow
			modifyValueSetQDM();
		}else{//Pseudo QDM Flow
			modifyQDMWithOutValueSet();		
		}
	}
	
	private void modifyValueSetQDM(){
		//Normal Available QDM Flow
		MatValueSet modifyWithDTO = currentMatValueSet;
		searchDisplay.getErrorMessageDisplay().clear();
		searchDisplay.getApplyToMeasureSuccessMsg().clear();
		if(modifyValueSetDTO!=null && modifyWithDTO!=null ){
			String dataType;
			String dataTypeText;
			Boolean isSpecificOccurrence=false;
		
			dataType = searchDisplay.getDataTypeValue(searchDisplay.getDataTypesListBox());
			dataTypeText = searchDisplay.getDataTypeText(searchDisplay.getDataTypesListBox());
		    isSpecificOccurrence = searchDisplay.getSpecificOccurrenceInput().getValue();
		     	
			if(modifyValueSetDTO.getDataType().equalsIgnoreCase(ConstantMessages.ATTRIBUTE) || dataTypeText.equalsIgnoreCase(ConstantMessages.ATTRIBUTE)){
				if(dataTypeText.equalsIgnoreCase(modifyValueSetDTO.getDataType())){
					updateAppliedQDMList(modifyWithDTO,null, modifyValueSetDTO,dataType,isSpecificOccurrence,false);
				}else{
					if(ConstantMessages.ATTRIBUTE.equalsIgnoreCase(dataTypeText)){
						searchDisplay.getErrorMessageDisplay().setMessage("A value set with a non-Attribute category must be used for this data element.");
					}else{
						searchDisplay.getErrorMessageDisplay().setMessage("A value set with an Attribute category must be used for this data element.");
					}
				}
			}else{
				updateAppliedQDMList(modifyWithDTO,null, modifyValueSetDTO,dataType,isSpecificOccurrence,false);
			}
		}else{
			searchDisplay.getErrorMessageDisplay().setMessage("Please select atleast one applied QDM to modify.");
		}
	}
	
	private void modifyQDMWithOutValueSet(){
		//Pseudo QDM Flow		
		searchDisplay.getSuccessMessageUserDefinedPanel().clear();
		searchDisplay.getErrorMessageUserDefinedPanel().clear();
		if((searchDisplay.getUserDefinedInput().getText().trim().length()>0) && !searchDisplay.getDataTypeText(searchDisplay.getAllDataTypeInput()).equalsIgnoreCase(MatContext.PLEASE_SELECT))
		{
			
			CodeListSearchDTO modifyWithDTO = new CodeListSearchDTO();
			modifyWithDTO.setName(searchDisplay.getUserDefinedInput().getText());
			String dataType = searchDisplay.getDataTypeValue(searchDisplay.getAllDataTypeInput());
			String dataTypeText = searchDisplay.getDataTypeText(searchDisplay.getAllDataTypeInput());
			if(modifyValueSetDTO.getDataType().equalsIgnoreCase(ConstantMessages.ATTRIBUTE) || dataTypeText.equalsIgnoreCase(ConstantMessages.ATTRIBUTE)){
				if(dataTypeText.equalsIgnoreCase(modifyValueSetDTO.getDataType())){
					updateAppliedQDMList(null,modifyWithDTO, modifyValueSetDTO,dataType,false,true);
				}else{
					if(ConstantMessages.ATTRIBUTE.equalsIgnoreCase(dataTypeText)){
						searchDisplay.getErrorMessageUserDefinedPanel().setMessage("A value set with a non-Attribute category must be used for this data element.");
					}else{
						searchDisplay.getErrorMessageUserDefinedPanel().setMessage("A value set with an Attribute category must be used for this data element.");
					}
				}
			}else{
				updateAppliedQDMList(null,modifyWithDTO, modifyValueSetDTO,dataType,false,true);
			}
		}else{
			searchDisplay.getErrorMessageUserDefinedPanel().setMessage("Please enter Value Set name and select a data type associated with it.");
		}
		
	}
	
	/**
	 * This method is used to update QDM element selected for modification. All check's for attributes and non attributes , Occurrence and non occurences
	 * are done in this method. This method returns modified and ordered list of all applied QDM elements.This method also makes call to updateMeasureXML method.
	 * 
	 * **/	
	private void updateAppliedQDMList(final MatValueSet matValueSet ,final CodeListSearchDTO codeListSearchDTO , final QualityDataSetDTO  qualityDataSetDTO, String dataType,  Boolean isSpecificOccurrence,final boolean isUSerDefined){
		MatContext.get().getCodeListService().updateCodeListToMeasure(dataType,matValueSet, codeListSearchDTO, qualityDataSetDTO,isSpecificOccurrence, appliedQDMList,new AsyncCallback<SaveUpdateCodeListResult>(){
			@Override
			public void onFailure(Throwable caught) {
				if(!isUSerDefined){
					searchDisplay.getErrorMessageDisplay().setMessage(MatContext.get().getMessageDelegate().getGenericErrorMessage());
				}else{
					searchDisplay.getErrorMessageUserDefinedPanel().setMessage(MatContext.get().getMessageDelegate().getGenericErrorMessage());
				}
				
			}
			@Override
			public void onSuccess(SaveUpdateCodeListResult result) {
				if(result.getFailureReason()==7){
					if(!isUSerDefined){
						searchDisplay.getErrorMessageDisplay().setMessage(MatContext.get().getMessageDelegate().getDuplicateAppliedQDMMsg());
					}else{
						searchDisplay.getErrorMessageUserDefinedPanel().setMessage(MatContext.get().getMessageDelegate().getDuplicateAppliedQDMMsg());
					}
				}
				else{
					appliedQDMList = result.getAppliedQDMList();
					updateMeasureXML( result.getDataSetDTO() , qualityDataSetDTO,isUSerDefined);
				}
			}
		});
		
	}
	
	/**
	 * This method updates MeasureXML - ElementLookUpNode,ElementRef's under Population Node and Stratification Node, SupplementDataElements. It also removes attributes nodes if
	 * there is mismatch in data types of newly selected QDM and already applied QDM.
	 * 
	 * **/	
	private void updateMeasureXML(QualityDataSetDTO qualityDataSetDTO2, QualityDataSetDTO qualityDataSetDTO,final boolean isUserDefined){
		MatContext.get().getMeasureService().updateMeasureXML(qualityDataSetDTO2, qualityDataSetDTO, MatContext.get().getCurrentMeasureId(), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				if(!isUserDefined){
					searchDisplay.getErrorMessageDisplay().setMessage(MatContext.get().getMessageDelegate().getGenericErrorMessage());
				}else{
					searchDisplay.getErrorMessageUserDefinedPanel().setMessage(MatContext.get().getMessageDelegate().getGenericErrorMessage());
				}
			}

			@Override
			public void onSuccess(Void result) {
				if(!isUserDefined){
					searchDisplay.getApplyToMeasureSuccessMsg().setMessage(MatContext.get().getMessageDelegate().getSuccessfulModifyQDMMsg());
				}else{
					searchDisplay.getSuccessMessageUserDefinedPanel().setMessage(MatContext.get().getMessageDelegate().getSuccessfulModifyQDMMsg());
					searchDisplay.getUserDefinedInput().setText("");
					searchDisplay.getAllDataTypeInput().setSelectedIndex(0);
				}
			}
		});
		
	}
	
	/**
	 * This method is used to reload Applied QDM List.
	 * 
	 * */
	private void reloadAppliedQDMList(){
		QDSAppliedListModel appliedListModel = new QDSAppliedListModel();
		appliedListModel.setAppliedQDMs(appliedQDMList);
		qdsAppliedListPresenterDisplay.buildCellList(appliedListModel);
	}
	
	private void showSearchingBusy(boolean busy){
		if(busy)
			Mat.showLoadingMessage();
		else
			Mat.hideLoadingMessage();
	}
	
	/**
	 * This method shows AvailableValueSet Widget in pop up.
	 * */
	private void displaySearch() {
		ModifyQDMDialogBox.showModifyDialogBox(searchDisplay.asWidget(),modifyValueSetDTO);
		populateDataTypesListBox();
		searchDisplay.resetVSACValueSetWidget();
		searchDisplay.clearVSACValueSetMessages();
	}	
	
	private void populateAllDataType(){
		MatContext.get().getListBoxCodeProvider().getAllDataType(new AsyncCallback<List<? extends HasListBox>>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(List<? extends HasListBox> result) {
				Collections.sort(result, new HasListBox.Comparator());
				searchDisplay.setAllDataTypeOptions(result);
			}
       });
		
	}
	
	public Widget getWidget() {
		return searchDisplay.asWidget();
	}
	
	public String getSortKey(int columnIndex) {
		String[] sortKeys = new String[] { "name", "taxnomy", "category"};
		return sortKeys[columnIndex];
	}	
	
	@Override
	public void beforeDisplay() {
		displaySearch();
	}

	@Override
	public void beforeClosingDisplay() {	
		
	}
}
