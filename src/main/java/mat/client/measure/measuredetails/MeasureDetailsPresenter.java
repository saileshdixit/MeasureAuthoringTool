package mat.client.measure.measuredetails;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import mat.client.MatPresenter;
import mat.client.event.BackToMeasureLibraryPage;
import mat.client.event.MeasureDeleteEvent;
import mat.client.shared.ui.DeleteConfirmDialogBox;
import mat.shared.ConstantMessages;
import mat.shared.error.AuthenticationException;
import mat.shared.error.measure.DeleteMeasureException;
import mat.client.measure.measuredetails.navigation.MeasureDetailsNavigation;
import mat.client.shared.MatContext;
import mat.client.shared.MatDetailItem;
import mat.client.shared.MeasureDetailsConstants;

public class MeasureDetailsPresenter implements MatPresenter, MeasureDetailsObserver {
	private MeasureDetailsView measureDetailsView;
	private MeasureDetailsNavigation navigationPanel;
	private String scoringType;
	private boolean isCompositeMeasure;
	private DeleteConfirmDialogBox dialogBox;
	
	public MeasureDetailsPresenter() {
		navigationPanel = new MeasureDetailsNavigation(scoringType, isCompositeMeasure);
		navigationPanel.setObserver(this);
		measureDetailsView = new MeasureDetailsView(MeasureDetailsConstants.MeasureDetailsItems.GENERAL_MEASURE_INFORMATION, navigationPanel);
		navigationPanel.setActiveMenuItem(MeasureDetailsConstants.MeasureDetailsItems.GENERAL_MEASURE_INFORMATION);
		
		measureDetailsView.getDeleteMeasureButton().addClickHandler(event -> handleDeleteMeasureButtonClick());
	}
	
	@Override
	public void beforeClosingDisplay() {
		this.scoringType = null;
		isCompositeMeasure = false;
	}

	@Override
	public void beforeDisplay() {
		populateMeasureDetailsModelAndDisplayNavigationMenu();
	}

	@Override
	public Widget getWidget() {
		return measureDetailsView.getWidget();
	}

	@Override
	public void onMenuItemClicked(MatDetailItem menuItem) {
		measureDetailsView.buildDetailView(menuItem);
	}
	
	@Override
	public void handleDeleteMeasureButtonClick() {
		onDeleteMeasureButtonClick();
	}
	
	private void onDeleteMeasureButtonClick() {
		clearAlerts();
		dialogBox = new DeleteConfirmDialogBox();
		dialogBox.showDeletionConfimationDialog(MatContext.get().getMessageDelegate().getDELETE_MEASURE_WARNING_MESSAGE());
		dialogBox.getConfirmButton().addClickHandler(event -> deleteMeasure());
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
				} else {
					showErrorAlert(MatContext.get().getMessageDelegate().getGenericErrorMessage());
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

	private void populateMeasureDetailsModelAndDisplayNavigationMenu() {
		this.scoringType = MatContext.get().getCurrentMeasureScoringType();
		MatContext.get().getMeasureService().isCompositeMeasure(MatContext.get().getCurrentMeasureId(), isCompositeMeasureCallBack());
	}

	private AsyncCallback<Boolean> isCompositeMeasureCallBack() {
		return new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(MatContext.get().getMessageDelegate().getGenericErrorMessage());
			}

			@Override
			public void onSuccess(Boolean isCompositeMeasure) {
				navigationPanel.buildNavigationMenu(scoringType, isCompositeMeasure);
			}
		};
	}
	
	private void clearAlerts() {
		measureDetailsView.getErrorMessageAlert().clear();

	}
	
	private void showErrorAlert(String message) {
		measureDetailsView.getErrorMessageAlert().clear();
		measureDetailsView.getErrorMessageAlert().createAlert(message);
	}
}