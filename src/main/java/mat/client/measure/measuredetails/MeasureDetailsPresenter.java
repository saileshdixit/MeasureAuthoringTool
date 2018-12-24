package mat.client.measure.measuredetails;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import mat.client.Mat;
import mat.client.MatPresenter;
import mat.client.event.BackToMeasureLibraryPage;
import mat.client.event.MeasureDeleteEvent;
import mat.client.event.MeasureSelectedEvent;
import mat.client.measure.ManageCompositeMeasureDetailModel;
import mat.client.measure.ManageMeasureDetailModel;
import mat.client.measure.measuredetails.navigation.MeasureDetailsAnchorListItem;
import mat.client.measure.measuredetails.navigation.MeasureDetailsNavigation;
import mat.client.measure.service.SaveMeasureResult;
import mat.client.shared.ConfirmationDialogBox;
import mat.client.shared.MatContext;
import mat.client.shared.MatDetailItem;
import mat.client.shared.MeasureDetailsConstants;
import mat.client.shared.MeasureDetailsConstants.MeasureDetailsItems;
import mat.client.shared.MeasureDetailsConstants.PopulationItems;
import mat.client.shared.ui.DeleteConfirmDialogBox;
import mat.shared.ConstantMessages;
import mat.shared.error.AuthenticationException;
import mat.shared.error.measure.DeleteMeasureException;
import mat.shared.measure.measuredetails.models.MeasureDetailsModel;
import mat.shared.measure.measuredetails.models.MeasureDetailsRichTextAbstractModel;
import mat.shared.measure.measuredetails.models.MeasureStewardDeveloperModel;
import mat.shared.measure.measuredetails.translate.ManageMeasureDetailModelMapper;
import mat.shared.measure.measuredetails.validate.GeneralInformationValidator;

public class MeasureDetailsPresenter implements MatPresenter, MeasureDetailsObserver {
	private MeasureDetailsView measureDetailsView;
	private MeasureDetailsNavigation navigationPanel;
	private String scoringType;
	private boolean isCompositeMeasure;
	private boolean isMeasureEditable;
	private boolean isPatientBased;
	private long lastRequestTime;
	private DeleteConfirmDialogBox dialogBox;
	MeasureDetailsModel measureDetailsModel;

	public MeasureDetailsPresenter() {
		navigationPanel = new MeasureDetailsNavigation(scoringType, isPatientBased, isCompositeMeasure);
		navigationPanel.setObserver(this);
		measureDetailsModel = new MeasureDetailsModel();
		measureDetailsView = new MeasureDetailsView(measureDetailsModel, MeasureDetailsConstants.MeasureDetailsItems.GENERAL_MEASURE_INFORMATION, navigationPanel);
		navigationPanel.setActiveMenuItem(MeasureDetailsConstants.MeasureDetailsItems.GENERAL_MEASURE_INFORMATION);
		addEventHandlers();
	}
	
	@Override
	public void beforeClosingDisplay() {
		Mat.hideLoadingMessage();
		navigationPanel.updateState(MeasureDetailState.BLANK);
		this.scoringType = null;
		isPatientBased = false;
		isCompositeMeasure = false;
		isMeasureEditable = true;
	}

	@Override
	public void beforeDisplay() {
		clearAlerts();
		setIsLoading();
		MatContext.get().getMeasureService().getMeasureDetailsAndLogRecentMeasure(MatContext.get().getCurrentMeasureId(), MatContext.get().getLoggedinUserId(),getAsyncCallBackForMeasureAndLogRecentMeasure());
	}

	private void setIsLoading() {
		measureDetailsView.clear();
		measureDetailsView.setReadOnly(true);
		Mat.showLoadingMessage();
	}

	@Override
	public Widget getWidget() {
		return measureDetailsView.getWidget();
	}

	public MeasureDetailsView getView() {
		return measureDetailsView;
	}
	
	@Override
	public void handleMenuItemClick(MatDetailItem menuItem) {
		clearAlerts();
		if(isDirty()) {
			measureDetailsView.displayDirtyCheck();
			measureDetailsView.getMessagePanel().getWarningConfirmationNoButton().addClickHandler(event -> handleWarningConfirmationNoClick());
			measureDetailsView.getMessagePanel().getWarningConfirmationYesButton().addClickHandler(event -> handleWarningConfirmationYesClick(menuItem));
			measureDetailsView.getMessagePanel().getWarningConfirmationYesButton().setFocus(true);
		} else {
			navigateTo(menuItem);
		}
	}

	private void navigateTo(MatDetailItem menuItem) {		
		measureDetailsView.buildDetailView(menuItem);
		navigationPanel.setActiveMenuItem(menuItem);
		measureDetailsView.setFocusOnHeader();
	}
	
	private void handleWarningConfirmationYesClick(MatDetailItem menuItem) {
		clearAlerts();
		navigateTo(menuItem);
	}

	private void handleWarningConfirmationNoClick() {
		clearAlerts();
	}

	public boolean isDirty() {
		if(measureDetailsView.getMeasureDetailsComponentModel() != null) {
			return measureDetailsView.getMeasureDetailsComponentModel().isDirty(measureDetailsModel);
		}
		
		return false; 
	}
	
	@Override
	public void handleDeleteMeasureButtonClick() {
		if(isDeletable()) {
			clearAlerts();
			dialogBox = new DeleteConfirmDialogBox();
			dialogBox.showDeletionConfimationDialog(MatContext.get().getMessageDelegate().getDELETE_MEASURE_WARNING_MESSAGE());
			dialogBox.getConfirmButton().addClickHandler(event -> deleteMeasure());
		}
	}
	
	@Override
	public void handleStateChanged() {
		updateNavPillStates();
	}
	
	private void deleteMeasure() {
		MatContext.get().getMeasureService().deleteMeasure(MatContext.get().getCurrentMeasureId(), MatContext.get().getLoggedinLoginId(), dialogBox.getPasswordEntered(), deleteMeasureCallback());
	}
	
	public AsyncCallback<Void> deleteMeasureCallback() {
		return new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				if(caught instanceof AuthenticationException) {
					dialogBox.setMessage(caught.getMessage());
					dialogBox.getPassword().setText("");
				} else if(caught instanceof DeleteMeasureException) {
					showErrorAlert(caught.getMessage());
					dialogBox.closeDialogBox();
				} else {
					showErrorAlert(MatContext.get().getMessageDelegate().getGenericErrorMessage());
					dialogBox.closeDialogBox();
				}
			}

			@Override
			public void onSuccess(Void result) {				
				dialogBox.closeDialogBox();
				MatContext.get().recordTransactionEvent(MatContext.get().getCurrentMeasureId(), null, "MEASURE_DELETE_EVENT", "Measure Successfully Deleted", ConstantMessages.DB_LOG);
				MatContext.get().setMeasureDeleted(true);
				fireBackToMeasureLibraryEvent();
				fireMeasureDeletionEvent(true, MatContext.get().getMessageDelegate().getMeasureDeletionSuccessMgs());	
			}
		};
	}
	
	private void fireMeasureDeletionEvent(boolean isSuccess, String message){
		MeasureDeleteEvent deleteEvent = new MeasureDeleteEvent(isSuccess, message);
		MatContext.get().getEventBus().fireEvent(deleteEvent);
	}
	
	private void fireBackToMeasureLibraryEvent() {
		BackToMeasureLibraryPage backToMeasureLibraryPage = new BackToMeasureLibraryPage();
		MatContext.get().getEventBus().fireEvent(backToMeasureLibraryPage);
	}

	private void displayMeasureDetailsView() {
		this.scoringType = measureDetailsModel.getGeneralInformationModel().getScoringMethod();
		this.isPatientBased = measureDetailsModel.getGeneralInformationModel().isPatientBased();
		navigationPanel.buildNavigationMenu(scoringType, isPatientBased, isCompositeMeasure);
		measureDetailsView.buildDetailView(measureDetailsModel, MeasureDetailsConstants.MeasureDetailsItems.GENERAL_MEASURE_INFORMATION, navigationPanel);
		isMeasureEditable = !MatContext.get().getMeasureLockService().checkForEditPermission();
		measureDetailsView.setReadOnly(isMeasureEditable);
		measureDetailsView.getDeleteMeasureButton().setEnabled(isDeletable());
		navigationPanel.setActiveMenuItem(MeasureDetailsConstants.MeasureDetailsItems.GENERAL_MEASURE_INFORMATION);
		updateNavPillStates();
	}


	private void addEventHandlers() {
		HandlerManager eventBus = MatContext.get().getEventBus();
		eventBus.addHandler(MeasureSelectedEvent.TYPE, new MeasureSelectedEvent.Handler() {
			@Override
			public void onMeasureSelected(MeasureSelectedEvent event) {
				MatContext.get().fireMeasureEditEvent();
			}
		});
		measureDetailsView.getDeleteMeasureButton().addClickHandler(event -> handleDeleteMeasureButtonClick());
		measureDetailsView.getSaveButton().addClickHandler(event -> handleSaveButtonClick());
	}

	protected AsyncCallback<ManageCompositeMeasureDetailModel> getAsyncCallBackForCompositeMeasureAndLogRecentMeasure() {
		return new AsyncCallback<ManageCompositeMeasureDetailModel>() {
			final long callbackRequestTime = lastRequestTime;
			@Override
			public void onFailure(Throwable caught) {
				handleAsyncFailure(caught);
			}
			
			@Override
			public void onSuccess(ManageCompositeMeasureDetailModel result) {
				if (callbackRequestTime == lastRequestTime) {
					ManageMeasureDetailModelMapper manageMeasureDetailModelMapper = new ManageMeasureDetailModelMapper(result);
					measureDetailsModel = manageMeasureDetailModelMapper.getMeasureDetailsModel(isCompositeMeasure);					
					MatContext.get().fireMeasureEditEvent();
				}
			}
		};
	}

	private void clearAlerts() {
		measureDetailsView.getMessagePanel().clearAlerts();
	}
	
	private void showErrorAlert(String message) {
		clearAlerts();
		measureDetailsView.getErrorMessageAlert().createAlert(message);
	}	
	
	private boolean isDeletable() {
		return isMeasureOwner() && !MatContext.get().isCurrentMeasureLocked();
	}
	
	private boolean isMeasureOwner() {
		return measureDetailsModel.getOwnerUserId() == MatContext.get().getLoggedinUserId();
	}
	
	private AsyncCallback<MeasureDetailsModel> getAsyncCallBackForMeasureAndLogRecentMeasure() {
		return new AsyncCallback<MeasureDetailsModel>() {
			final long callbackRequestTime = lastRequestTime;
			@Override
			public void onFailure(Throwable caught) {
				handleAsyncFailure(caught);
			}
			@Override
			public void onSuccess(MeasureDetailsModel result) {
				setCompositeMeasure(result.isComposite());
				handleAsyncSuccess(result, callbackRequestTime);
			}
			
			private void handleAsyncSuccess(MeasureDetailsModel result, long callbackRequestTime) {
				Mat.hideLoadingMessage();
				if (callbackRequestTime == lastRequestTime) {
					measureDetailsModel = result;
					displayMeasureDetailsView();
				}
			}
		};
	}
	
	private void handleAsyncFailure(Throwable caught) {
		Mat.hideLoadingMessage();
		showErrorAlert(caught.getMessage());
		MatContext.get().recordTransactionEvent(null, null, null, "Unhandled Exception: " +caught.getLocalizedMessage(), 0);
	}
	
	public boolean isCompositeMeasure() {
		return isCompositeMeasure;
	}

	public void setCompositeMeasure(boolean isCompositeMeasure) {
		this.isCompositeMeasure = isCompositeMeasure;
	}

	@Override
	public void handleSaveButtonClick() {
		List<String> validationErrors = measureDetailsView.getMeasureDetailsComponentModel().validateModel(measureDetailsModel);
		if(validationErrors == null || validationErrors.isEmpty()) {
			ConfirmationDialogBox confirmationDialog = measureDetailsView.getSaveConfirmation();
			if(confirmationDialog != null) {
				showSaveConfirmationDialog(confirmationDialog);
			} else {
				saveMeasureDetails();
			}
		} else {
			String validationErrorMessage = validationErrors.stream().collect(Collectors.joining("\n"));
			measureDetailsView.displayErrorMessage(validationErrorMessage);
		}
	}

	private void showSaveConfirmationDialog(ConfirmationDialogBox confirmationDialog) {
		confirmationDialog.getYesButton().addClickHandler(event -> saveMeasureDetails());
		confirmationDialog.getNoButton().addClickHandler(event -> measureDetailsView.resetForm());
		confirmationDialog.show();
		confirmationDialog.getYesButton().setFocus(true);
	}

	private void saveMeasureDetails() {
		measureDetailsView.getComponentDetailView().getObserver().handleValueChanged();
		measureDetailsView.getMeasureDetailsComponentModel().update(measureDetailsModel);
		ManageMeasureDetailModelMapper mapper = new ManageMeasureDetailModelMapper(measureDetailsModel);
		ManageMeasureDetailModel manageMeasureDetails = mapper.convertMeasureDetailsToManageMeasureDetailModel();
		if(measureDetailsModel.isComposite()) {
			MatContext.get().getMeasureService().saveCompositeMeasure((ManageCompositeMeasureDetailModel) manageMeasureDetails, getSaveCallback());
		} else {
			MatContext.get().getMeasureService().saveMeasureDetails(manageMeasureDetails, getSaveCallback());
		}
	}

	private AsyncCallback<SaveMeasureResult> getSaveCallback() {
		return new AsyncCallback<SaveMeasureResult>() {
			@Override
			public void onFailure(Throwable caught) {
				measureDetailsView.displayErrorMessage(MatContext.get().getMessageDelegate().getGenericErrorMessage());
			}

			@Override
			public void onSuccess(SaveMeasureResult result) {
				MatDetailItem activeMenuItem = navigationPanel.getActiveMenuItem();
				scoringType = measureDetailsModel.getGeneralInformationModel().getScoringMethod();
				isPatientBased = measureDetailsModel.getGeneralInformationModel().isPatientBased();
				MatContext.get().setCurrentMeasureScoringType(scoringType);
				navigationPanel.buildNavigationMenu(scoringType, isPatientBased, isCompositeMeasure);
				measureDetailsView.buildDetailView(measureDetailsModel, navigationPanel.getActiveMenuItem(), navigationPanel);
				isMeasureEditable = !MatContext.get().getMeasureLockService().checkForEditPermission();
				measureDetailsView.setReadOnly(isMeasureEditable);
				measureDetailsView.getDeleteMeasureButton().setEnabled(isDeletable());
				measureDetailsView.displaySuccessMessage("Changes for the " +  measureDetailsView.getCurrentMeasureDetail().displayName() + " section have been successfully saved.");
				handleStateChanged();
				navigationPanel.setActiveMenuItem(activeMenuItem);
			}
		};
	}
	
	private void updateNavPillStates() {
		navigationPanel.getMenuItemMap().keySet().forEach(k -> {
			MeasureDetailState navPillState = getStateForModelByKey(k);
			MeasureDetailsAnchorListItem anchorListItem = navigationPanel.getMenuItemMap().get(k);
			if(anchorListItem != null) {
				anchorListItem.setState(navPillState);
			}
			
		});
	}

	private MeasureDetailState getStateForModelByKey(MatDetailItem k) {
		if(k instanceof MeasureDetailsItems) {
			switch((MeasureDetailsItems) k) {
			case GENERAL_MEASURE_INFORMATION:
				return getGeneralInformationState(measureDetailsModel);
			case STEWARD:
				return getMeasureStewardAndDeveloperState(measureDetailsModel.getMeasureStewardDeveloperModel());
			case DESCRIPTION:
				return getRichTextEditableTabState(measureDetailsModel.getDescriptionModel());
			case COPYRIGHT:
				return getRichTextEditableTabState(measureDetailsModel.getCopyrightModel());
			case DISCLAIMER:
				return getRichTextEditableTabState(measureDetailsModel.getDisclaimerModel());
			case MEASURE_TYPE:
				return getMeasureTypeState(measureDetailsModel);
			case STRATIFICATION:
				return getRichTextEditableTabState(measureDetailsModel.getStratificationModel());
			case RISK_ADJUSTMENT:
				return getRichTextEditableTabState(measureDetailsModel.getRiskAdjustmentModel());
			case RATE_AGGREGATION:
				return getRichTextEditableTabState(measureDetailsModel.getRateAggregationModel());
			case RATIONALE:
				return getRichTextEditableTabState(measureDetailsModel.getRationaleModel());
			case CLINICAL_RECOMMENDATION:
				return getRichTextEditableTabState(measureDetailsModel.getClinicalRecommendationModel());
			case IMPROVEMENT_NOTATION:
				return getRichTextEditableTabState(measureDetailsModel.getImprovementNotationModel());
			case DEFINITION:
				return getRichTextEditableTabState(measureDetailsModel.getDefinitionModel());
			case GUIDANCE:
				return getRichTextEditableTabState(measureDetailsModel.getGuidanceModel());
			case TRANSMISSION_FORMAT:
				return getRichTextEditableTabState(measureDetailsModel.getTransmissionFormatModel());
			case SUPPLEMENTAL_DATA_ELEMENTS:
				return getRichTextEditableTabState(measureDetailsModel.getSupplementalDataElementsModel());
			case MEASURE_SET:
				return getRichTextEditableTabState(measureDetailsModel.getMeasureSetModel());
			case POPULATIONS:
				return getPopulationsState(measureDetailsModel);
			default: 
				return MeasureDetailState.BLANK;
			}
		} else if (k instanceof PopulationItems) {
			switch((PopulationItems) k) {
			case INITIAL_POPULATION:
				return getRichTextEditableTabState(measureDetailsModel.getInitialPopulationModel());
			case MEASURE_POPULATION:
				return getRichTextEditableTabState(measureDetailsModel.getMeasurePopulationModel());
			case MEASURE_POPULATION_EXCLUSIONS:
				return getRichTextEditableTabState(measureDetailsModel.getMeasurePopulationExclusionsModel());
			case DENOMINATOR:
				return getRichTextEditableTabState(measureDetailsModel.getDenominatorModel());
			case DENOMINATOR_EXCLUSIONS:
				return getRichTextEditableTabState(measureDetailsModel.getDenominatorExclusionsModel());
			case NUMERATOR:
				return getRichTextEditableTabState(measureDetailsModel.getNumeratorModel());
			case NUMERATOR_EXCLUSIONS:
				return getRichTextEditableTabState(measureDetailsModel.getNumeratorExclusionsModel());
			case DENOMINATOR_EXCEPTIONS:
				return getRichTextEditableTabState(measureDetailsModel.getDenominatorExceptionsModel());
			case MEASURE_OBSERVATIONS:
				return getRichTextEditableTabState(measureDetailsModel.getMeasureObservationsModel());
			
			default: 
				return MeasureDetailState.BLANK;
			}		
		}
		
		return MeasureDetailState.BLANK;
	}
	
	private MeasureDetailState getMeasureStewardAndDeveloperState(MeasureStewardDeveloperModel model) {
		if ((model.getStewardId() == null || model.getStewardId().isEmpty()) 
			&& (model.getSelectedDeveloperList() == null || model.getSelectedDeveloperList().isEmpty())) {
			return MeasureDetailState.BLANK;
		} else {
			return MeasureDetailState.COMPLETE;
		}
	}
	
	private MeasureDetailState getGeneralInformationState(MeasureDetailsModel measureDetailsModel) {
		GeneralInformationValidator modelValidator = new GeneralInformationValidator();
		return modelValidator.getModelState(measureDetailsModel.getGeneralInformationModel(), measureDetailsModel.isComposite());
		
	}

	private MeasureDetailState getMeasureTypeState(MeasureDetailsModel model) {
		
		if(model.getMeasureTypeModeModel().getMeasureTypeList() == null) {
			return MeasureDetailState.BLANK;
		}
		
		// composite measures always have at least one measure type selected. So we should only show a green checkmark
		// if the composite measure has more than two in its measure type list. 
		if(model.isComposite()) {
			if(model.getMeasureTypeModeModel().getMeasureTypeList().size() > 1) {
				return MeasureDetailState.COMPLETE;
			}
		} else {
			if(model.getMeasureTypeModeModel().getMeasureTypeList().size() > 0) {
				return MeasureDetailState.COMPLETE;
			}
		}
		
		return MeasureDetailState.BLANK;
	}

	private MeasureDetailState getRichTextEditableTabState(MeasureDetailsRichTextAbstractModel model) {
		if(model != null) {
			if(model.getPlainText() == null || model.getPlainText().isEmpty()) {
				return MeasureDetailState.BLANK;
			} else {
				return MeasureDetailState.COMPLETE;
			}
		}
		return MeasureDetailState.BLANK;
	}
	
	private MeasureDetailState getPopulationsState(MeasureDetailsModel measureDetailsModel) {
		List<MeasureDetailsRichTextAbstractModel> applicableModels = new ArrayList<>();
		if(scoringType.equals(MeasureDetailsConstants.getCohort())) {
			applicableModels.add(measureDetailsModel.getInitialPopulationModel());
		} else if (scoringType.equals(MeasureDetailsConstants.getContinuousVariable())) {
			applicableModels.add(measureDetailsModel.getInitialPopulationModel());
			applicableModels.add(measureDetailsModel.getMeasurePopulationModel());
			applicableModels.add(measureDetailsModel.getMeasurePopulationExclusionsModel());
			applicableModels.add(measureDetailsModel.getMeasureObservationsModel());
		} else if(scoringType.equals(MeasureDetailsConstants.getProportion())) {
			applicableModels.add(measureDetailsModel.getInitialPopulationModel());
			applicableModels.add(measureDetailsModel.getDenominatorModel());
			applicableModels.add(measureDetailsModel.getDenominatorExclusionsModel());
			applicableModels.add(measureDetailsModel.getNumeratorModel());
			applicableModels.add(measureDetailsModel.getNumeratorExclusionsModel());
			applicableModels.add(measureDetailsModel.getDenominatorExceptionsModel());
		} else if (scoringType.equals(MeasureDetailsConstants.getRatio())) {
			applicableModels.add(measureDetailsModel.getInitialPopulationModel());
			applicableModels.add(measureDetailsModel.getDenominatorModel());
			applicableModels.add(measureDetailsModel.getDenominatorExclusionsModel());
			applicableModels.add(measureDetailsModel.getNumeratorModel());
			applicableModels.add(measureDetailsModel.getNumeratorExclusionsModel());
		}
		
		return calculateStateOffOfList(applicableModels);
	}
	
	private MeasureDetailState calculateStateOffOfList(List<MeasureDetailsRichTextAbstractModel> applicableModels) {
		int completedPopulationCount = 0;
		for(MeasureDetailsRichTextAbstractModel model : applicableModels) {
			if(getRichTextEditableTabState(model) == MeasureDetailState.COMPLETE) {
				completedPopulationCount++;
			}
		}
		
		if(completedPopulationCount == 0) {
			return MeasureDetailState.BLANK;
		} else if(completedPopulationCount == applicableModels.size()) {
			return MeasureDetailState.COMPLETE;
		} else {
			return MeasureDetailState.INCOMPLETE;
		}
	}
}
