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
import mat.model.MatValueSetTransferObject;
import mat.model.QualityDataSetDTO;
import mat.shared.ConstantMessages;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * QDMAvailableValueSetPresenter class.
 */
public class QDMAvailableValueSetPresenter  implements MatPresenter {
	
	/**
	 * QDMAvailableValueSetPresenter's view interface.
	 */
	interface SearchDisplay {
		
		/**
		 * As widget.
		 * 
		 * @return {@link Widget}
		 */
		Widget asWidget();
		
		/**
		 * Builds the value set details widget.
		 * 
		 * @param matValueSets
		 *            - ArrayList of {@link MatValueSet}
		 */
		void buildValueSetDetailsWidget(ArrayList<MatValueSet> matValueSets);
		
		/**
		 * Remove all Success and failure messages.
		 */
		void clearVSACValueSetMessages();
		
		/**
		 * Gets the all data type input.
		 * 
		 * @return {@link ListBoxMVP}
		 */
		ListBoxMVP getAllDataTypeInput();
		
		/**
		 * Gets the apply to measure button.
		 * 
		 * @return {@link Button}
		 */
		Button getApplyToMeasureButton();
		
		/**
		 * Gets the apply to measure success msg.
		 * 
		 * @return {@link SuccessMessageDisplayInterface}
		 */
		SuccessMessageDisplayInterface getApplyToMeasureSuccessMsg();
		
		/**
		 * Gets the current mat value set.
		 * 
		 * @return {@link MatValueSet}
		 */
		MatValueSet getCurrentMatValueSet();
		
		/**
		 * Gets the data types list box.
		 * 
		 * @return {@link ListBoxMVP}
		 */
		ListBoxMVP getDataTypesListBox();
		
		/**
		 * Gets the data type text.
		 * 
		 * @param inputListBox
		 *            - {@link ListBoxMVP}
		 * @return {@link String}
		 */
		String getDataTypeText(ListBoxMVP inputListBox);
		
		/**
		 * Gets the data type value.
		 * 
		 * @param inputListBox
		 *            - {@link ListBoxMVP}
		 * @return {@link String}
		 */
		String getDataTypeValue(ListBoxMVP inputListBox);
		
		/**
		 * Gets the disclosure panel.
		 * 
		 * @return {@link DisclosurePanel}
		 */
		DisclosurePanel getDisclosurePanel();
		
		/**
		 * Gets the disclosure panel vsac.
		 * 
		 * @return {@link DisclosurePanel}
		 */
		DisclosurePanel getDisclosurePanelVSAC();
		
		/**
		 * Gets the error message display.
		 * 
		 * @return {@link ErrorMessageDisplayInterface}
		 */
		ErrorMessageDisplayInterface getErrorMessageDisplay();
		
		/**
		 * Gets the error message user defined panel.
		 * 
		 * @return {@link ErrorMessageDisplay}
		 */
		ErrorMessageDisplay getErrorMessageUserDefinedPanel();
		
		/**
		 * Gets the oID input.
		 * 
		 * @return {@link TextBox}
		 */
		TextBox getOIDInput();
		
		/**
		 * Gets the psuedo qdm to measure.
		 * 
		 * @return {@link Button}
		 */
		Button getPsuedoQDMToMeasure();
		
		/**
		 * Gets the retrieve button.
		 * 
		 * @return {@link Button}
		 */
		Button getRetrieveButton();
		
		/**
		 * Gets the specific occurrence input.
		 * 
		 * @return {@link CustomCheckBox}
		 */
		CustomCheckBox getSpecificOccurrenceInput();
		
		/**
		 * Gets the success message display.
		 * 
		 * @return {@link SuccessMessageDisplay}
		 */
		SuccessMessageDisplay getSuccessMessageDisplay();
		
		/**
		 * Gets the success message user defined panel.
		 * 
		 * @return {@link SuccessMessageDisplay}
		 */
		SuccessMessageDisplay getSuccessMessageUserDefinedPanel();
		
		/**
		 * Gets the user defined input.
		 * 
		 * @return {@link TextBox}
		 */
		TextBox getUserDefinedInput();
		
		/**
		 * Gets the value set details panel.
		 * 
		 * @return {@link VerticalPanel}
		 */
		VerticalPanel getValueSetDetailsPanel();
		
		/**
		 * Gets the version input.
		 * 
		 * @return {@link DateBoxWithCalendar}
		 */
		DateBoxWithCalendar getVersionInput();
		
		/**
		 * Reset VSACValueSetWidget - Clear's OID and version input's.
		 */
		void resetVSACValueSetWidget();
		
		/**
		 * Sets the all data type options.
		 * 
		 * @param texts
		 *            List of {@link HasListBox}
		 */
		void setAllDataTypeOptions(List<? extends HasListBox> texts);
		
		/**
		 * Sets the data types list box options.
		 * 
		 * @param texts
		 *            - {@link List} of {@link HasListBox}
		 */
		void setDataTypesListBoxOptions(List<? extends HasListBox> texts);
	}
	
	/**
	 * QualityDataSet List.
	 */
	private ArrayList<QualityDataSetDTO> appliedQDMList = new ArrayList<QualityDataSetDTO>();
	
	/**
	 * When retrieving value set from VSAC, "Loading Please Wait..." message is displayed.
	 * busyLoading is set true when retrieving value set from VSAC otherwise it is set false.
	 */
	private boolean busyLoading;
	/**
	 * MatValueSet instance.
	 */
	private MatValueSet currentMatValueSet;
	
	/**
	 * Measure Service instance.
	 */
	private final MeasureServiceAsync measureService = MatContext.get()
			.getMeasureService();
	
	/** The modify value set dto. {@link QualityDataSetDTO} instance. */
	private final QualityDataSetDTO modifyValueSetDTO;
	
	/**
	 * The qds applied list presenter display. {@link QDSAppliedListPresenter}
	 * search display instance.
	 */
	private final mat.client.clause.QDSAppliedListPresenter.SearchDisplay qdsAppliedListPresenterDisplay;
	/**
	 * SearchDisplay instance.
	 */
	private final SearchDisplay searchDisplay;
	
	/** The vsacapi service. {@link VSACAPIServiceAsync} instance. */
	private final VSACAPIServiceAsync vsacapiService = MatContext.get()
			.getVsacapiServiceAsync();
	
	/**
	 * Constructor.
	 * @param sDisplayArg
	 *            - {@link SearchDisplay}
	 * @param dataSetDTO
	 *            - {@link QualityDataSetDTO}
	 * @param qdsAppliedListPresenterDisplay
	 *            S-
	 *            {@link mat.client.clause.QDSAppliedListPresenter.SearchDisplay}
	 */
	public QDMAvailableValueSetPresenter(SearchDisplay sDisplayArg , QualityDataSetDTO dataSetDTO,
			final mat.client.clause.QDSAppliedListPresenter.SearchDisplay qdsAppliedListPresenterDisplay) {
		searchDisplay = sDisplayArg;
		modifyValueSetDTO = dataSetDTO;
		this.qdsAppliedListPresenterDisplay = qdsAppliedListPresenterDisplay;
		appliedQDMList = (ArrayList<QualityDataSetDTO>) qdsAppliedListPresenterDisplay.getAllAppliedQDMList();
		
		//Element without VSAC value set - OPEN Handler
		searchDisplay.getDisclosurePanel().addOpenHandler(new OpenHandler<DisclosurePanel>() {
			@Override
			public void onOpen(OpenEvent<DisclosurePanel> event) {
				if (isBusyLoading()) {
					event.getTarget().setOpen(false);
				} else {
					displaySearch();
					searchDisplay.getDisclosurePanelVSAC().setOpen(false);
				}
			}
		});
		
		//Element without VSAC value set - CLOSE Handler
		searchDisplay.getDisclosurePanel().addCloseHandler(new CloseHandler<DisclosurePanel>() {
			@Override
			public void onClose(CloseEvent<DisclosurePanel> event) {
				if (!isBusyLoading()) {
					searchDisplay.getUserDefinedInput().setText("");
					searchDisplay.getAllDataTypeInput().setItemSelected(0, true);
					displaySearch();
					searchDisplay.getDisclosurePanelVSAC().setOpen(true);
				}
			}
		});
		
		//Element with VSAC value set - OPEN Handler.
		searchDisplay.getDisclosurePanelVSAC().addOpenHandler(new OpenHandler<DisclosurePanel>() {
			@Override
			public void onOpen(OpenEvent<DisclosurePanel> event) {
				if (!isBusyLoading()) {
					displaySearch();
					searchDisplay.getDisclosurePanel().setOpen(false);
				}
			}
		});
		
		//Element with VSAC value set - CLOSE Handler.
		searchDisplay.getDisclosurePanelVSAC().addCloseHandler(new CloseHandler<DisclosurePanel>() {
			@Override
			public void onClose(CloseEvent<DisclosurePanel> event) {
				if (isBusyLoading()) {
					event.getTarget().setOpen(true);
				} else {
					searchDisplay.getUserDefinedInput().setText("");
					displaySearch();
					searchDisplay.getDisclosurePanel().setOpen(true);
				}
			}
		});
		
		searchDisplay.getUserDefinedInput().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				searchDisplay.getSuccessMessageUserDefinedPanel().clear();
				searchDisplay.getErrorMessageUserDefinedPanel().clear();
			}
		});
		
		searchDisplay.getAllDataTypeInput().addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(final FocusEvent event) {
				searchDisplay.getSuccessMessageUserDefinedPanel().clear();
				searchDisplay.getErrorMessageUserDefinedPanel().clear();
				
			}
		});
		searchDisplay.getPsuedoQDMToMeasure().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				modifyQDM(true);
			}
		});
		
		searchDisplay.getApplyToMeasureButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				currentMatValueSet = searchDisplay.getCurrentMatValueSet();
				modifyQDM(false);
			}
		});
		
		searchDisplay.getRetrieveButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				searchDisplay.clearVSACValueSetMessages();
				searchValueSetInVsac(searchDisplay.getOIDInput().getValue(),
						searchDisplay.getVersionInput().getValue());
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see mat.client.MatPresenter#beforeClosingDisplay()
	 */
	@Override
	public void beforeClosingDisplay() {
		
	}
	
	/* (non-Javadoc)
	 * @see mat.client.MatPresenter#beforeDisplay()
	 */
	@Override
	public void beforeDisplay() {
		displaySearch();
	}
	
	/**
	 * Convert message.
	 * 
	 * @param id
	 *            the id
	 * @return String
	 */
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
	
	/**
	 * This method shows AvailableValueSet Widget in pop up.
	 * */
	private void displaySearch() {
		ModifyQDMDialogBox.showModifyDialogBox(searchDisplay.asWidget(), modifyValueSetDTO, this);
		populateAllDataType();
		searchDisplay.resetVSACValueSetWidget();
		searchDisplay.clearVSACValueSetMessages();
		searchDisplay.getSuccessMessageUserDefinedPanel().clear();
		searchDisplay.getErrorMessageUserDefinedPanel().clear();
	}
	
	/**
	 * Filter timing qdms.
	 * 
	 * @param result
	 *            - {@link ArrayList} of {@link QualityDataSetDTO}
	 */
	private void filterTimingQDMs(
			ArrayList<QualityDataSetDTO> result) {
		List<QualityDataSetDTO> timingQDMs = new ArrayList<QualityDataSetDTO>();
		for (QualityDataSetDTO qdsDTO : result) {
			if ("Timing Element".equals(qdsDTO
					.getDataType())) {
				timingQDMs.add(qdsDTO);
			}
		}
		result.removeAll(timingQDMs);
	}
	
	/* (non-Javadoc)
	 * @see mat.client.MatPresenter#getWidget()
	 */
	@Override
	public Widget getWidget() {
		return searchDisplay.asWidget();
	}
	
	/**
	 * When retrieving value set from VSAC, "Loading Please Wait..." message is displayed.
	 * @return true if "Loading Please Wait..." message is displaying(In other words, when retrieving value set from VSAC)
	 * 	    else returns false;
	 */
	public final boolean isBusyLoading() {
		return busyLoading;
	}
	
	/**
	 * Method to find if selected Available value set is a valid modifiable selection.
	 *  If yes, then call to updateAppliedQDMList method is made.
	 * @param isUserDefined - Boolean.
	 **/
	protected final void modifyQDM(final boolean isUserDefined) {
		if (!isUserDefined) { //Normal Available QDM Flow
			modifyValueSetQDM();
		} else { //Pseudo QDM Flow
			modifyQDMWithOutValueSet();
		}
	}
	
	/**
	 * Server call to modify QDM without VSAC value set.
	 */
	private void modifyQDMWithOutValueSet() {
		//Pseudo QDM Flow
		searchDisplay.getSuccessMessageUserDefinedPanel().clear();
		searchDisplay.getErrorMessageUserDefinedPanel().clear();
		if ((searchDisplay.getUserDefinedInput().getText().trim().length() > 0)
				&& !searchDisplay.getDataTypeText(searchDisplay.getAllDataTypeInput()).
				equalsIgnoreCase(MatContext.PLEASE_SELECT)) {
			CodeListSearchDTO modifyWithDTO = new CodeListSearchDTO();
			modifyWithDTO.setName(searchDisplay.getUserDefinedInput().getText());
			String dataType = searchDisplay.getDataTypeValue(searchDisplay.getAllDataTypeInput());
			String dataTypeText = searchDisplay.getDataTypeText(searchDisplay.getAllDataTypeInput());
			if (modifyValueSetDTO.getDataType().equalsIgnoreCase(ConstantMessages.ATTRIBUTE)
					|| dataTypeText.equalsIgnoreCase(ConstantMessages.ATTRIBUTE)) {
				if (dataTypeText.equalsIgnoreCase(modifyValueSetDTO.getDataType())) {
					updateAppliedQDMList(null, modifyWithDTO, modifyValueSetDTO, dataType, false, true);
				} else {
					if (ConstantMessages.ATTRIBUTE.equalsIgnoreCase(dataTypeText)) {
						searchDisplay.getErrorMessageUserDefinedPanel().setMessage(
								MatContext.get().
								getMessageDelegate().getMODIFY_QDM_NON_ATTRIBUTE_VALIDATION());
					} else {
						searchDisplay.getErrorMessageUserDefinedPanel().setMessage(
								MatContext.get().
								getMessageDelegate().getMODIFY_QDM_ATTRIBUTE_VALIDATION());
					}
				}
			} else {
				updateAppliedQDMList(null, modifyWithDTO, modifyValueSetDTO, dataType, false, true);
			}
		} else {
			searchDisplay.getErrorMessageUserDefinedPanel().setMessage(
					MatContext.get().getMessageDelegate().getVALIDATION_MSG_ELEMENT_WITHOUT_VSAC());
		}
	}
	
	/**
	 * Server call to modify QDM with VSAC value set.
	 */
	private void modifyValueSetQDM() {
		//Normal Available QDM Flow
		MatValueSet modifyWithDTO = currentMatValueSet;
		searchDisplay.getErrorMessageDisplay().clear();
		searchDisplay.getApplyToMeasureSuccessMsg().clear();
		if ((modifyValueSetDTO != null) && (modifyWithDTO != null)) {
			String dataType;
			String dataTypeText;
			Boolean isSpecificOccurrence = false;
			
			dataType = searchDisplay.getDataTypeValue(searchDisplay.getDataTypesListBox());
			dataTypeText = searchDisplay.getDataTypeText(searchDisplay.getDataTypesListBox());
			isSpecificOccurrence = searchDisplay.getSpecificOccurrenceInput().getValue();
			
			if (modifyValueSetDTO.getDataType().equalsIgnoreCase(ConstantMessages.ATTRIBUTE)
					|| dataTypeText.equalsIgnoreCase(ConstantMessages.ATTRIBUTE)) {
				if (dataTypeText.equalsIgnoreCase(modifyValueSetDTO.getDataType())) {
					updateAppliedQDMList(modifyWithDTO, null, modifyValueSetDTO, dataType, isSpecificOccurrence, false);
				} else {
					if (ConstantMessages.ATTRIBUTE.equalsIgnoreCase(dataTypeText)) {
						searchDisplay.getErrorMessageDisplay().setMessage(MatContext.get().
								getMessageDelegate().getMODIFY_QDM_NON_ATTRIBUTE_VALIDATION()
								);
					} else {
						searchDisplay.getErrorMessageDisplay().setMessage(MatContext.get().
								getMessageDelegate().getMODIFY_QDM_ATTRIBUTE_VALIDATION());
					}
				}
			} else {
				updateAppliedQDMList(modifyWithDTO, null, modifyValueSetDTO, dataType, isSpecificOccurrence, false);
			}
		} else {
			searchDisplay.getErrorMessageDisplay().setMessage(MatContext.get().
					getMessageDelegate().getMODIFY_QDM_SELECT_ATLEAST_ONE());
		}
	}
	
	/**
	 * Get All data types from DB and populates in AllDataTypeOptions and
	 * DataTypeListBoxOptions.
	 */
	private void populateAllDataType() {
		MatContext.get().getListBoxCodeProvider().getAllDataType(new AsyncCallback<List<? extends HasListBox>>() {
			
			@Override
			public void onFailure(final Throwable caught) {
				
			}
			
			@Override
			public void onSuccess(final List<? extends HasListBox> result) {
				Collections.sort(result, new HasListBox.Comparator());
				searchDisplay.setAllDataTypeOptions(result);
				searchDisplay.setDataTypesListBoxOptions(result);
			}
		});
	}
	
	/**
	 * This method is used to reload Applied QDM List.
	 **/
	public final void reloadAppliedQDMList() {
		QDSAppliedListModel appliedListModel = new QDSAppliedListModel();
		filterTimingQDMs(appliedQDMList);
		appliedListModel.setAppliedQDMs(appliedQDMList);
		qdsAppliedListPresenterDisplay.buildCellList(appliedListModel);
		
		/*
		 * Setting appliedQDMList in qdsAppliedListPresenterDisplay. Whenever
		 * this modify pop up is opened this.appliedQDMList is set with
		 * qdsAppliedListPresenterDisplay.appliedQDMList in this presenter. So,
		 * qdsAppliedListPresenterDisplay.appliedQDMList is updated here.
		 */
		qdsAppliedListPresenterDisplay.setAppliedQDMList(appliedQDMList);
	}
	
	/**
	 * Search value set in vsac.
	 * 
	 * @param oid
	 *            - {@link String}
	 * @param version
	 *            - {@link String}
	 */
	private void searchValueSetInVsac(String oid, String version) {
		if (!MatContext.get().isUMLSLoggedIn()) { //UMLS Login Validation
			searchDisplay.getErrorMessageDisplay().setMessage(
					MatContext.get().getMessageDelegate().getUMLS_NOT_LOGGEDIN());
			return;
		}
		//OID validation.
		if ((oid == null) || oid.trim().isEmpty()) {
			searchDisplay.getErrorMessageDisplay().setMessage(MatContext.get().getMessageDelegate().getUMLS_OID_REQUIRED());
			searchDisplay.getValueSetDetailsPanel().setVisible(false);
			return;
		}
		showSearchingBusy(true);
		vsacapiService.getValueSetByOIDAndVersion(oid, version, new AsyncCallback<VsacApiResult>() {
			@Override
			public void onFailure(final Throwable caught) {
				searchDisplay.getErrorMessageDisplay().setMessage(
						MatContext.get().getMessageDelegate().getVSAC_RETRIEVE_FAILED());
				searchDisplay.getValueSetDetailsPanel().setVisible(false);
				showSearchingBusy(false);
			}
			
			@Override
			public void onSuccess(final VsacApiResult result) {
				if (result.isSuccess()) {
					searchDisplay.buildValueSetDetailsWidget(result.getVsacResponse());
					searchDisplay.getValueSetDetailsPanel().setVisible(true);
				} else {
					String message = convertMessage(result.getFailureReason());
					searchDisplay.getErrorMessageDisplay().setMessage(message);
					searchDisplay.getValueSetDetailsPanel().setVisible(false);
				}
				showSearchingBusy(false);
			}
		});
	}
	
	/**
	 * This method is used in searching all available Value sets for pop up.
	 * 
	 * @param busy
	 *            the busy
	 */
	private void showSearchingBusy(final boolean busy) {
		if (busy) {
			Mat.showLoadingMessage();
		} else {
			Mat.hideLoadingMessage();
		}
		busyLoading = busy;
		searchDisplay.getRetrieveButton().setEnabled(!busy);
		searchDisplay.getOIDInput().setEnabled(!busy);
		searchDisplay.getVersionInput().setEnabled(!busy);
	}
	
	/**
	 * This method is used to update QDM element selected for modification. All
	 * check's for attributes and non attributes , Occurrence and non
	 * occurrences. are done in this method. This method returns modified and
	 * ordered list of all applied QDM elements. This method also makes call to
	 * updateMeasureXML method.
	 * @param matValueSet
	 *            - {@link MatValueSet}
	 * @param codeListSearchDTO
	 *            - {@link CodeListSearchDTO}
	 * @param qualityDataSetDTO
	 *            - {@link QualityDataSetDTO}
	 * @param dataType
	 *            - {@link String}
	 * @param isSpecificOccurrence
	 *            - {@link Boolean}
	 * @param isUSerDefined
	 *            - {@link Boolean}
	 */
	private void updateAppliedQDMList(final MatValueSet matValueSet , final CodeListSearchDTO codeListSearchDTO ,
			final QualityDataSetDTO qualityDataSetDTO, final String dataType, final Boolean isSpecificOccurrence,
			final boolean isUSerDefined) {
		MatValueSetTransferObject matValueSetTransferObject = new MatValueSetTransferObject();
		matValueSetTransferObject.setDatatype(dataType);
		matValueSetTransferObject.setMatValueSet(matValueSet);
		matValueSetTransferObject.setCodeListSearchDTO(codeListSearchDTO);
		matValueSetTransferObject.setQualityDataSetDTO(qualityDataSetDTO);
		matValueSetTransferObject.setAppliedQDMList(appliedQDMList);
		matValueSetTransferObject.setSpecificOccurrence(isSpecificOccurrence);
		matValueSetTransferObject.setVersion(searchDisplay.getVersionInput().getValue());
		MatContext.get().getCodeListService().updateCodeListToMeasure(matValueSetTransferObject,
				new AsyncCallback<SaveUpdateCodeListResult>() {
			@Override
			public void onFailure(final Throwable caught) {
				if (!isUSerDefined) {
					searchDisplay.getErrorMessageDisplay().setMessage(
							MatContext.get().getMessageDelegate().getGenericErrorMessage());
				} else {
					searchDisplay.getErrorMessageUserDefinedPanel().setMessage(
							MatContext.get().getMessageDelegate().getGenericErrorMessage());
				}
			}
			@Override
			public void onSuccess(final SaveUpdateCodeListResult result) {
				if (result.getFailureReason() == SaveUpdateCodeListResult.ALREADY_EXISTS) {
					if (!isUSerDefined) {
						searchDisplay.getErrorMessageDisplay().setMessage(
								MatContext.get().getMessageDelegate().getDuplicateAppliedQDMMsg());
					} else {
						searchDisplay.getErrorMessageUserDefinedPanel().setMessage(
								MatContext.get().getMessageDelegate().getDuplicateAppliedQDMMsg());
					}
				} else {
					appliedQDMList = result.getAppliedQDMList();
					updateMeasureXML(result.getDataSetDTO() , qualityDataSetDTO, isUSerDefined);
				}
			}
		});
		
	}
	
	/**
	 * This method updates MeasureXML - ElementLookUpNode,ElementRef's under
	 * Population Node and Stratification Node, SupplementDataElements. It also
	 * removes attributes nodes if there is mismatch in data types of newly
	 * selected QDM and already applied QDM. *
	 * 
	 * @param modifyWithDTO
	 *            - {@link QualityDataSetDTO}
	 * @param modifyableDTO
	 *            - {@link QualityDataSetDTO}
	 * @param isUserDefined
	 *            - {@link Boolean}
	 */
	private void updateMeasureXML(final QualityDataSetDTO modifyWithDTO,
			final QualityDataSetDTO modifyableDTO, final boolean isUserDefined) {
		measureService.updateMeasureXML(modifyWithDTO, modifyableDTO,
				MatContext.get().getCurrentMeasureId(), new AsyncCallback<Void>() {
			
			@Override
			public void onFailure(final Throwable caught) {
				if (!isUserDefined) {
					searchDisplay.getErrorMessageDisplay().setMessage(
							MatContext.get().getMessageDelegate().getGenericErrorMessage());
				} else {
					searchDisplay.getErrorMessageUserDefinedPanel().setMessage(
							MatContext.get().getMessageDelegate().getGenericErrorMessage());
				}
			}
			
			@Override
			public void onSuccess(final Void result) {
				if (!isUserDefined) {
					searchDisplay.getApplyToMeasureSuccessMsg().setMessage(
							MatContext.get().getMessageDelegate().getSuccessfulModifyQDMMsg());
				} else {
					searchDisplay.getSuccessMessageUserDefinedPanel().setMessage(
							MatContext.get().getMessageDelegate().getSuccessfulModifyQDMMsg());
					searchDisplay.getUserDefinedInput().setText("");
					searchDisplay.getAllDataTypeInput().setSelectedIndex(0);
				}
			}
		});
		
	}
}
