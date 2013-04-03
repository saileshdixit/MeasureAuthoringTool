package mat.client.clause.clauseworkspace.presenter;

import mat.client.Mat;
import mat.client.MeasureComposerPresenter;
import mat.client.clause.clauseworkspace.model.CellTreeNode;
import mat.client.clause.clauseworkspace.model.MeasureXmlModel;
import mat.client.clause.clauseworkspace.view.XmlTreeView;
import mat.client.measure.service.MeasureServiceAsync;
import mat.client.shared.MatContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;

public class XmlTreePresenter {
	
	
	interface TreeResources extends CellTree.Resources {
	    @Source("mat/client/images/addAllLeft.png")
	    ImageResource cellTreeClosedItem();

	    @Source("mat/client/images/addAllRight.png")
	    ImageResource cellTreeOpenItem();

	    @Source("mat/client/images/MyCellTree.css")
	    CellTree.Style cellTreeStyle();
	    
	    @Source("mat/client/images/cms_gov_footer.png")
	    @ImageOptions(repeatStyle = RepeatStyle.Horizontal, flipRtl = true)
	    ImageResource cellTreeSelectedBackground();
	} 

	XmlTreeDisplay xmlTreeDisplay;
	MeasureServiceAsync service = MatContext.get().getMeasureService();
	private static final String MEASURE = "measure";
	private String rootNode;
	
	public void loadXmlTree(final SimplePanel panel){
		if (MatContext.get().getCurrentMeasureId() != null
				&& !MatContext.get().getCurrentMeasureId().equals("")) {
			
			service.getMeasureXmlForMeasure(MatContext.get()
					.getCurrentMeasureId(),
					new AsyncCallback<MeasureXmlModel>() {// Loading the measure's SimpleXML from the Measure_XML table 

						@Override
						public void onSuccess(MeasureXmlModel result) {
							panel.clear();
							String xml = result != null ? result.getXml() : null;
							XmlTreeView xmlTreeView = new XmlTreeView(XmlConversionlHelper.createCellTreeNode(xml, rootNode));//converts XML to TreeModel Object and sets to XmlTreeView
//							CellTree cellTree = new CellTree(xmlTreeView, null);
							CellTree.Resources resource = GWT.create(TreeResources.class); 
							CellTree cellTree = new CellTree(xmlTreeView, null, resource);// CellTree Creation
							cellTree.setDefaultNodeSize(500);// this will get rid of the show more link on the bottom of the Tree
							xmlTreeView.createPageView(cellTree); // Page Layout
							xmlTreeDisplay = (XmlTreeDisplay) xmlTreeView;
							xmlTreeDisplay.setEnabled(MatContext.get().getMeasureLockService().checkForEditPermission());
							panel.add(xmlTreeDisplay.asWidget());
							invokeSaveHandler();
						}

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
						}
					});
		} else {
			Mat.hideLoadingMessage();
		}
		MeasureComposerPresenter.setSubSkipEmbeddedLink("ClauseWorkspaceTree");
		Mat.focusSkipLists("MeasureComposer");
		
	}
	
	private MeasureXmlModel createMeasureExportModel(String xml) {
		MeasureXmlModel exportModal = new MeasureXmlModel();
		exportModal.setMeasureId(MatContext.get().getCurrentMeasureId());
		exportModal.setToReplaceNode(rootNode);
		exportModal.setParentNode(MEASURE);
		exportModal.setXml(xml);
		return exportModal;
	}

	private void invokeSaveHandler() {
		xmlTreeDisplay.getSaveButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				xmlTreeDisplay.clearMessages();
				CellTreeNode cellTreeNode = (CellTreeNode) xmlTreeDisplay.getXmlTree().getRootTreeNode().getChildValue(0);
				MeasureXmlModel measureXmlModel = createMeasureExportModel(XmlConversionlHelper.createXmlFromTree(cellTreeNode));
				
				service.saveMeasureXml(measureXmlModel,
					new AsyncCallback<Void>() {
		
						@Override
						public void onFailure(Throwable caught) {
						}
		
						@Override
						public void onSuccess(Void result) {
							xmlTreeDisplay.getSuccessMessageDisplay().setMessage("Changes are successfully saved.");
						}
				});
			}
		});
	}

	/**
	 * @return the rootNode
	 */
	public String getRootNode() {
		return rootNode;
	}

	/**
	 * @param rootNode the rootNode to set
	 */
	public void setRootNode(String rootNode) {
		this.rootNode = rootNode;
	}
	
}
