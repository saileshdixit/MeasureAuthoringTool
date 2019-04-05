package mat.client.measure.measuredetails.views;

import org.gwtbootstrap3.client.ui.gwt.FlowPanel;

import com.google.gwt.user.client.ui.Widget;

import mat.client.measure.measuredetails.observers.InitialPopulationObserver;
import mat.client.measure.measuredetails.observers.MeasureDetailsComponentObserver;
import mat.client.shared.ConfirmationDialogBox;
import mat.client.shared.editor.RichTextEditor;
import mat.shared.measure.measuredetails.models.InitialPopulationModel;
import mat.shared.measure.measuredetails.models.MeasureDetailsComponentModel;

public class InitialPopulationView implements MeasureDetailViewInterface {

	private FlowPanel mainPanel = new FlowPanel();
	private MeasureDetailsRichTextEditor measureDetailsRichTextEditor;
	private InitialPopulationModel model;
	private InitialPopulationModel originalModel;
	private InitialPopulationObserver observer;

	public InitialPopulationView() {

	}
	
	public InitialPopulationView(InitialPopulationModel initialPopulationModel) {
		this.originalModel = initialPopulationModel;
		buildInitialPopulationModel(this.originalModel);
		buildDetailView();
	}

	public Widget getWidget() {
		return mainPanel;
	}

	@Override
	public boolean hasUnsavedChanges() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void buildDetailView() {
		measureDetailsRichTextEditor = new MeasureDetailsRichTextEditor(mainPanel);
		measureDetailsRichTextEditor.getRichTextEditor().setTitle("Initial Population Editor");
		measureDetailsRichTextEditor.getRichTextEditor().setEditorText(this.model.getFormattedText());	
		addEventHandlers();
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		measureDetailsRichTextEditor.setReadOnly(readOnly);
	}

	@Override
	public ConfirmationDialogBox getSaveConfirmation() {
		return null;
	}

	@Override
	public void resetForm() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MeasureDetailsComponentModel getMeasureDetailsComponentModel() {
		return this.model;
	}

	@Override
	public RichTextEditor getRichTextEditor() {
		return measureDetailsRichTextEditor.getRichTextEditor();
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
	}
	
	public void setObserver(InitialPopulationObserver observer) {
		this.observer = observer; 
	}
		
	private void addEventHandlers() {
		measureDetailsRichTextEditor.getRichTextEditor().addKeyUpHandler(event -> observer.handleValueChanged());
	}
	
	private void buildInitialPopulationModel(InitialPopulationModel initialPopulationModel) {
		this.model = new InitialPopulationModel(initialPopulationModel);
	}

	@Override
	public void setMeasureDetailsComponentModel(MeasureDetailsComponentModel model) {
		this.model = (InitialPopulationModel) model; 	
		this.originalModel = this.model;
		buildInitialPopulationModel(this.originalModel);
	}

	@Override
	public void setObserver(MeasureDetailsComponentObserver observer) {
		this.observer = (InitialPopulationObserver) observer; 
	}
	
	@Override
	public MeasureDetailsComponentObserver getObserver() {
		return observer;
	}

	@Override
	public Widget getFirstElement() {
		return null;
	}
}
