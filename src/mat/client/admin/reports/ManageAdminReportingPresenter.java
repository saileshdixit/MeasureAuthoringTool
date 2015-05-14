package mat.client.admin.reports;
import mat.client.MatPresenter;
import mat.client.admin.reports.ManageAdminReportingView.Observer;
import mat.client.shared.ContentWithHeadingWidget;
import mat.client.shared.ErrorMessageDisplayInterface;
import mat.client.shared.SuccessMessageDisplay;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
/** The Class ManageAdminPresenter. */
public class ManageAdminReportingPresenter implements MatPresenter {
	
	
	/** The Admin content widget. */
	private SimplePanel adminContentWidget = new SimplePanel();
	/** Flow Panel. */
	private FlowPanel fp = new FlowPanel();
	private Display display;
	public static interface Display {
		Widget asWidget();
		ContentWithHeadingWidget getContainerPanel();
		ErrorMessageDisplayInterface getErrorMessageDisplay();
		SuccessMessageDisplay getSuccessMessageDisplay();
		void setObserver(Observer observer);
	}
	public ManageAdminReportingPresenter(ManageAdminReportingView adminReportingView) {
		display = adminReportingView;
		addHandlers();
	}
	
	
	private void addHandlers(){
		display.setObserver(new Observer() {
			
			@Override
			public void generateReport(ReportModel model) {
				if (model.getToBeGenerated().equalsIgnoreCase("Org")) {
					generateCSVForActiveOids();
				} else if (model.getToBeGenerated().equalsIgnoreCase("User")) {
					generateCSVOfActiveUserEmails();
				} else if (model.getToBeGenerated().equalsIgnoreCase("Measure")) {
					
				}
				
			}
		});
	}
	
	private void generateCSVForActiveOids() {
		String url = GWT.getModuleBaseURL() + "export?format=exportActiveOIDCSV";
		Window.open(url + "&type=save", "_self", "");
	}
	
	private void generateCSVOfActiveUserEmails() {
		String url = GWT.getModuleBaseURL() + "export?format=exportActiveNonAdminUsersCSV";
		Window.open(url + "&type=save", "_self", "");
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see mat.client.MatPresenter#beforeClosingDisplay()
	 */
	@Override
	public void beforeClosingDisplay() {
		adminContentWidget.clear();
		fp.clear();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see mat.client.MatPresenter#beforeDisplay()
	 */
	@Override
	public void beforeDisplay() {
		fp.add(display.asWidget());
		adminContentWidget.add(fp);
	}
	/* (non-Javadoc)
	 * @see mat.client.MatPresenter#getWidget()
	 */
	@Override
	public Widget getWidget() {
		return adminContentWidget;
	}
	
}
