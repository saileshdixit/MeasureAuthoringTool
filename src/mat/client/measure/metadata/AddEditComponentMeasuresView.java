package mat.client.measure.metadata;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import mat.client.CustomPager;
import mat.client.codelist.HasListBox;
import mat.client.measure.ManageMeasureSearchModel;
import mat.client.measure.MeasureSearchView;
import mat.client.measure.ManageMeasureSearchModel.Result;
import mat.client.measure.MeasureSearchView.Observer;
import mat.client.resource.CellTableResource;
import mat.client.shared.ErrorMessageDisplayInterface;
import mat.client.shared.LabelBuilder;
import mat.client.shared.MatButtonCell;
import mat.client.shared.MatCheckBoxCell;
import mat.client.shared.MatContext;
import mat.client.shared.MatSafeHTMLCell;
import mat.client.shared.MatSimplePager;
import mat.client.shared.MeasureSearchFilterWidget;
import mat.client.shared.PrimaryButton;
import mat.client.shared.SearchWidget;
import mat.client.shared.SpacerWidget;
import mat.client.shared.SuccessMessageDisplay;
import mat.client.shared.SuccessMessageDisplayInterface;
import mat.client.shared.search.SearchResults;
import mat.client.shared.search.SearchView;
import mat.client.util.CellTableUtility;
import mat.model.QualityDataSetDTO;
import mat.shared.ClickableSafeHtmlCell;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableCaptionElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.cellview.client.CellTable.Resources;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.Range;

// TODO: Auto-generated Javadoc
/**
 * The Class AddEditComponentMeasuresView.
 */
public class AddEditComponentMeasuresView implements MetaDataPresenter.AddEditComponentMeasuresDisplay{

	
	/** The search button. */
	private Button searchButton = new PrimaryButton("Search","primaryGreyLeftButton");
	
	/** The search input. */
	private TextBox searchInput = new TextBox();
	
	/** The main panel. */
	private FlowPanel mainPanel = new FlowPanel();
	
	/** The cell table panel. */
	private VerticalPanel cellTablePanel = new VerticalPanel();
	
	/** The Constant PAGE_SIZE. */
	private static final int PAGE_SIZE = 25;
	
	/** The table. */
	private CellTable<ManageMeasureSearchModel.Result> table;
	
	/** The selected measure list. */
	private List<ManageMeasureSearchModel.Result> selectedMeasureList;
	
	/** The measure search filter widget. */
	private MeasureSearchFilterWidget measureSearchFilterWidget = new MeasureSearchFilterWidget("measureLibrarySearchWidget",
			"measureLibraryFilterDisclosurePanel");
	private SearchWidget searchWidget = new SearchWidget();
	
	/** The even. */
	private Boolean even;
	/** The cell table css style. */
	private List<String> cellTableCssStyle;
	/** The cell table even row. */
	private String cellTableEvenRow = "cellTableEvenRow";
	/** The cell table odd row. */
	private String cellTableOddRow = "cellTableOddRow";
	
	private Observer observer;
	
	private  List<ManageMeasureSearchModel.Result> componentMeasureSelectedList;
	
	@Override
	public List<ManageMeasureSearchModel.Result> getComponentMeasureSelectedList() {
		return componentMeasureSelectedList;
	}

    @Override
	public void setComponentMeasureSelectedList(
			List<ManageMeasureSearchModel.Result> componentMeasureSelectedList) {
		this.componentMeasureSelectedList = componentMeasureSelectedList;
	}

	/** The selection model. */
	private MultiSelectionModel<ManageMeasureSearchModel.Result> selectionModel;
	
	/** The return button. */
	protected Button returnButton = new PrimaryButton("Return to Previous");
	
	protected Button addtoComponentMeasures = new PrimaryButton("Add to ComponentMeasures List");
	
	private SuccessMessageDisplay successMessages = new SuccessMessageDisplay();
	
	
	public static interface Observer {
		
		void onClearAllCheckBoxesClicked();
		
		void onExportSelectedClicked(ManageMeasureSearchModel.Result result, boolean  isCBChecked);
	}
	/**
	 * Instantiates a new adds the edit component measures view.
	 */
	public AddEditComponentMeasuresView(){
		mainPanel.clear();
		successMessages.setMessage("");
		HorizontalPanel mainHorizontalPanel = new HorizontalPanel();
		mainHorizontalPanel.getElement().setId("panel_MainHorizontalPanel");
		searchInput.getElement().setId("searchInput_TextBox");
		searchButton.getElement().setId("searchButton_Button");
		mainPanel.setStyleName("contentPanel");
		SimplePanel panel = new SimplePanel();
		panel.setWidth("550px");
		VerticalPanel measureFilterVP = new VerticalPanel();
		measureFilterVP.setWidth("100px");
		measureFilterVP.getElement().setId("panel_measureFilterVP");
		measureFilterVP.add(searchWidget);
		mainHorizontalPanel.add(measureFilterVP);
		mainPanel.add(mainHorizontalPanel);
		mainPanel.add(new SpacerWidget());
		mainPanel.add(new SpacerWidget());
        mainPanel.add(getAddtoComponentMeasuresBtn());
	}
	
	
	/* (non-Javadoc)
	 * @see mat.client.measure.metadata.BaseMetaDataPresenter.BaseAddEditDisplay#buildDataTable(mat.client.shared.search.SearchResults)
	 */
	@Override
	public void buildDataTable(
			SearchResults<ManageMeasureSearchModel> searchResults) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Adds the column to table.
	 *
	 * @return the cell table
	 */
	private CellTable<ManageMeasureSearchModel.Result> addColumnToTable() {
		Label measureSearchHeader = new Label("All Measures");
		measureSearchHeader.getElement().setId("measureSearchHeader_Label");
		measureSearchHeader.setStyleName("recentSearchHeader");
		com.google.gwt.dom.client.TableElement elem = table.getElement().cast();
		measureSearchHeader.getElement().setAttribute("tabIndex", "0");
		TableCaptionElement caption = elem.createCaption();
		caption.appendChild(measureSearchHeader.getElement());
		selectionModel = new MultiSelectionModel<ManageMeasureSearchModel.Result>();
		table.setSelectionModel(selectionModel);
		Header<SafeHtml> selectionColumnHeader = new Header<SafeHtml>(new ClickableSafeHtmlCell()) {
			private String cssClass = "transButtonWidth";
			private String title = "Click to Clear All";
			@Override
			public SafeHtml getValue() {
				SafeHtmlBuilder sb = new SafeHtmlBuilder();
				sb.appendHtmlConstant("<span>Select</span><button type=\"button\" title='"
						+ title + "' tabindex=\"0\" class=\" " + cssClass + "\">"
						+ "<span class='textCssStyle'>(Clear)</span></button>");
				return sb.toSafeHtml();
			}
		};
		selectionColumnHeader.setUpdater(new ValueUpdater<SafeHtml>() {
			@Override
			public void update(SafeHtml value) {
				clearAllCheckBoxes();
			}
		});
		MatCheckBoxCell chbxCell = new MatCheckBoxCell(false, true);
		
		Column<ManageMeasureSearchModel.Result, Boolean> selectColumn = new Column<ManageMeasureSearchModel.Result, Boolean>(chbxCell) {
			
			@Override
			public Boolean getValue(Result object) {
				boolean isSelected = false;
				if (componentMeasureSelectedList!=null && componentMeasureSelectedList.size() > 0) {
					for (int i = 0; i < componentMeasureSelectedList.size(); i++) {
						if (componentMeasureSelectedList.get(i).getId().equalsIgnoreCase(object.getId())) {
							isSelected = true;
							selectionModel.setSelected(object, isSelected);
							break;
						}
					}
				} else {
					isSelected = false;
					}
				return isSelected;
			}
		};  
		
		selectColumn.setFieldUpdater(new FieldUpdater<ManageMeasureSearchModel.Result, Boolean>() {
			
			@Override
			public void update(int index, Result object, Boolean value) {
				selectionModel.setSelected(object, value);
				if(value){
					componentMeasureSelectedList.add(object);
				}
				else{
					for (int i = 0; i < componentMeasureSelectedList.size(); i++) {
						if (componentMeasureSelectedList.get(i).getId().equalsIgnoreCase(object.getId())) {
							componentMeasureSelectedList.remove(i);
							break;
						}
					}
				}
				
			}
		});
		
		//table.addColumn(selectColumn, SafeHtmlUtils.fromSafeConstant("<span title='Select Column'>"
			//	+ "Select" + "</span>"));
		table.addColumn(selectColumn, selectionColumnHeader);
		
		Column<ManageMeasureSearchModel.Result, SafeHtml> measureName = new Column<ManageMeasureSearchModel.Result, SafeHtml>(
				new ClickableSafeHtmlCell()) {
			@Override
			public SafeHtml getValue(ManageMeasureSearchModel.Result object) {
				return CellTableUtility.getColumnToolTip(object.getName());
			}
		};
		table.addColumn(measureName, SafeHtmlUtils.fromSafeConstant("<span title='Measure Name Column'>"
				+ "Measure Name" + "</span>"));
		// Version Column
		Column<ManageMeasureSearchModel.Result, SafeHtml> version = new Column<ManageMeasureSearchModel.Result, SafeHtml>(
				new MatSafeHTMLCell()) {
			@Override
			public SafeHtml getValue(ManageMeasureSearchModel.Result object) {
				return CellTableUtility.getColumnToolTip(object.getVersion());
			}
		};
		table.addColumn(version, SafeHtmlUtils
				.fromSafeConstant("<span title='Version'>" + "Version"
						+ "</span>"));
		//Finalized Date
		Column<ManageMeasureSearchModel.Result, SafeHtml> finalizedDate = new Column<ManageMeasureSearchModel.Result, SafeHtml>(
				new MatSafeHTMLCell()) {
			@Override
			public SafeHtml getValue(ManageMeasureSearchModel.Result object) {
				if (object.getFinalizedDate() != null) {
					return CellTableUtility.getColumnToolTip(convertTimestampToString(object.getFinalizedDate()));
				} 
				return null;
			}
		};
		table.addColumn(finalizedDate, SafeHtmlUtils
				.fromSafeConstant("<span title='Finalized Date'>" + "Finalized Date"
						+ "</span>"));
				return table;
	}

	/* (non-Javadoc)
	 * @see mat.client.measure.metadata.BaseMetaDataPresenter.BaseAddEditDisplay#buildCellTable(mat.client.measure.ManageMeasureSearchModel)
	 */
	@Override
	public void buildCellTable(ManageMeasureSearchModel result) {
		cellTablePanel.clear();
		cellTablePanel.setStyleName("cellTablePanel");
		if((result.getData()!=null) && (result.getData().size() > 0)){
			table = new CellTable<ManageMeasureSearchModel.Result>(PAGE_SIZE,
					(Resources) GWT.create(CellTableResource.class));
			table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
			ListDataProvider<ManageMeasureSearchModel.Result> sortProvider = new ListDataProvider<ManageMeasureSearchModel.Result>();
			selectedMeasureList = new ArrayList<Result>();
			selectedMeasureList.addAll(result.getData());
			table.setPageSize(PAGE_SIZE);
			table.redraw();
			if((componentMeasureSelectedList!=null) && (componentMeasureSelectedList.size()>0)){
				updateComponentMeasuresSelectedList(result.getData());
				List<ManageMeasureSearchModel.Result> componentMeasureSelectedList = new ArrayList<ManageMeasureSearchModel.Result>();
				componentMeasureSelectedList.addAll(swapComponentMeasuresList(result.getData()));
				table.setRowData(componentMeasureSelectedList);
				table.setRowCount(componentMeasureSelectedList.size(), true);
				sortProvider.refresh();
				sortProvider.getList().addAll(componentMeasureSelectedList);
			}
			else{
				table.setRowData(selectedMeasureList);
				table.setRowCount(selectedMeasureList.size(), true);
				sortProvider.refresh();
				sortProvider.getList().addAll(result.getData());
			}
			table = addColumnToTable();
			sortProvider.addDataDisplay(table);
			CustomPager.Resources pagerResources = GWT.create(CustomPager.Resources.class);
			MatSimplePager spager = new MatSimplePager(CustomPager.TextLocation.CENTER, pagerResources, false, 0, true);
			spager.setPageStart(0);
			buildCellTableCssStyle();
			spager.setDisplay(table);
			spager.setPageSize(PAGE_SIZE);
			table.setWidth("100%");
			table.setColumnWidth(0, 20.0, Unit.PCT);
			table.setColumnWidth(1, 40.0, Unit.PCT);
			table.setColumnWidth(2, 20.0, Unit.PCT);
			table.setColumnWidth(3, 20.0, Unit.PCT);
			Label invisibleLabel = (Label) LabelBuilder.buildInvisibleLabel("measureSearchSummary",
					"In the following Measure List table, Measure Name is given in first column,"
							+ " Version in second column, Finalized Date in third column,"
							+ "History in fourth column, Edit in fifth column, Share in sixth column"
							+ "Clone in seventh column and Export in eight column.");
			table.getElement().setAttribute("id", "MeasureSearchCellTable");
			table.getElement().setAttribute("aria-describedby", "measureSearchSummary");
			cellTablePanel.add(invisibleLabel);
			cellTablePanel.add(table);
			cellTablePanel.add(new SpacerWidget());
			cellTablePanel.add(spager);
			mainPanel.add(cellTablePanel);
			
		} else {
			Label measureSearchHeader = new Label("My Measures");
			measureSearchHeader.getElement().setId("measureSearchHeader_Label");
			measureSearchHeader.setStyleName("recentSearchHeader");
			measureSearchHeader.getElement().setAttribute("tabIndex", "0");
			HTML desc = new HTML("<p> No My Measures. </p>");
			cellTablePanel.add(measureSearchHeader);
			cellTablePanel.add(new SpacerWidget());
			cellTablePanel.add(desc);
			mainPanel.add(cellTablePanel);
		}
		mainPanel.add(successMessages);
		mainPanel.add(getAddtoComponentMeasuresBtn());
	}
	
	/**
	 * Builds the cell table css style.
	 */
	private void buildCellTableCssStyle() {
		cellTableCssStyle = new ArrayList<String>();
		for (int i = 0; i < selectedMeasureList.size(); i++) {
			cellTableCssStyle.add(i, null);
		}
		table.setRowStyles(new RowStyles<ManageMeasureSearchModel.Result>() {
			@Override
			public String getStyleNames(ManageMeasureSearchModel.Result rowObject, int rowIndex) {
				if (rowIndex != 0) {
					if (cellTableCssStyle.get(rowIndex) == null) {
						if (even) {
							if (rowObject.getMeasureSetId().equalsIgnoreCase(
									selectedMeasureList.get(rowIndex - 1).getMeasureSetId())) {
								even = true;
								cellTableCssStyle.add(rowIndex, cellTableOddRow);
								return cellTableOddRow;
							} else {
								even = false;
								cellTableCssStyle.add(rowIndex, cellTableEvenRow);
								return cellTableEvenRow;
							}
						} else {
							if (rowObject.getMeasureSetId().equalsIgnoreCase(
									selectedMeasureList.get(rowIndex - 1).getMeasureSetId())) {
								even = false;
								cellTableCssStyle.add(rowIndex, cellTableEvenRow);
								return cellTableEvenRow;
							} else {
								even = true;
								cellTableCssStyle.add(rowIndex, cellTableOddRow);
								return cellTableOddRow;
							}
						}
					} else {
						return cellTableCssStyle.get(rowIndex);
					}
				} else {
					if (cellTableCssStyle.get(rowIndex) == null) {
						even = true;
						cellTableCssStyle.add(rowIndex, cellTableOddRow);
						return cellTableOddRow;
					} else {
						return cellTableCssStyle.get(rowIndex);
					}
				}
			}
		});
	}
	
	public void clearAllCheckBoxes(){
		List<Result> displayedItems = new ArrayList<Result>();
		displayedItems.addAll(componentMeasureSelectedList);
		componentMeasureSelectedList.clear();
		for (ManageMeasureSearchModel.Result msg : displayedItems) {
			selectionModel.setSelected(msg, false);
		}
		//observer.onClearAllCheckBoxesClicked();
	}
	
	private  List<ManageMeasureSearchModel.Result> swapComponentMeasuresList(List<ManageMeasureSearchModel.Result> componentMeasureList){
		List<ManageMeasureSearchModel.Result> measuresSelectedList = new ArrayList<ManageMeasureSearchModel.Result>();
		measuresSelectedList.addAll(componentMeasureSelectedList);
		for(int i=0;i<componentMeasureList.size();i++){
			if(!componentMeasureSelectedList.contains(componentMeasureList.get(i))){
				measuresSelectedList.add(componentMeasureList.get(i));
			}
		}
		
		return measuresSelectedList;
	}
	
	private void updateComponentMeasuresSelectedList(List<ManageMeasureSearchModel.Result> componentMeasureList) {
		if (componentMeasureSelectedList.size() != 0) {
			for (int i = 0; i < componentMeasureSelectedList.size(); i++) {
				for (int j = 0; j < componentMeasureList.size(); j++) {
					if (componentMeasureSelectedList.get(i).getId().equalsIgnoreCase(componentMeasureList.get(j).getId())) {
						componentMeasureSelectedList.set(i, componentMeasureList.get(j));
						break;
					}
				}
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see mat.client.measure.metadata.BaseMetaDataPresenter.BaseAddEditDisplay#getSaveButton()
	 */
	@Override
	public HasClickHandlers getSaveButton() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see mat.client.measure.metadata.BaseMetaDataPresenter.BaseAddEditDisplay#getCancelButton()
	 */
	@Override
	public HasClickHandlers getCancelButton() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see mat.client.measure.metadata.BaseMetaDataPresenter.BaseAddEditDisplay#getRemoveButton()
	 */
	@Override
	public HasClickHandlers getRemoveButton() {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public HasClickHandlers getReturnButton() {
//		return returnButton;
//	}

	/* (non-Javadoc)
 * @see mat.client.measure.metadata.BaseMetaDataPresenter.BaseAddEditDisplay#setOptions(java.util.List)
 */
@Override
	public void setOptions(List<? extends HasListBox> texts) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see mat.client.measure.metadata.BaseMetaDataPresenter.BaseAddEditDisplay#getErrorMessageDisplay()
	 */
	@Override
	public ErrorMessageDisplayInterface getErrorMessageDisplay() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see mat.client.measure.metadata.BaseMetaDataPresenter.BaseAddEditDisplay#getSuccessMessageDisplay()
	 */
	@Override
	public SuccessMessageDisplayInterface getSuccessMessageDisplay() {
		// TODO Auto-generated method stub
		return successMessages;
	}

	/* (non-Javadoc)
	 * @see mat.client.measure.metadata.BaseMetaDataPresenter.BaseAddEditDisplay#showTextBox()
	 */
	@Override
	public void showTextBox() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see mat.client.measure.metadata.BaseMetaDataPresenter.BaseAddEditDisplay#hideTextBox()
	 */
	@Override
	public void hideTextBox() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see mat.client.measure.metadata.MetaDataPresenter.AddEditComponentMeasuresDisplay#getMeasureType()
	 */
	@Override
	public String getMeasureType() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see mat.client.measure.metadata.MetaDataPresenter.AddEditComponentMeasuresDisplay#getMeasureTypeInputBox()
	 */
	@Override
	public HasValue<String> getMeasureTypeInputBox() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see mat.client.measure.metadata.MetaDataPresenter.AddEditComponentMeasuresDisplay#getOtherMeasureType()
	 */
	@Override
	public HasValue<String> getOtherMeasureType() {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	protected Widget getValueInput() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	protected String getValueInputLabel() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	protected SearchView<?> getSearchView() {
//		// TODO Auto-generated method stub
//		return null;
//	}


	/* (non-Javadoc)
 * @see mat.client.measure.metadata.MetaDataPresenter.AddEditComponentMeasuresDisplay#getMeasureSearchFilterWidget()
 */
@Override
	public MeasureSearchFilterWidget getMeasureSearchFilterWidget() {
		return measureSearchFilterWidget;
	}

	/* (non-Javadoc)
	 * @see mat.client.measure.metadata.BaseMetaDataPresenter.BaseAddEditDisplay#asWidget()
	 */
	@Override
	public Widget asWidget() {
		// TODO Auto-generated method stub
		return mainPanel;
	}

	/* (non-Javadoc)
	 * @see mat.client.measure.metadata.BaseMetaDataPresenter.BaseAddEditDisplay#getReturnButton()
	 */
	@Override
	public HasClickHandlers getReturnButton() {
		// TODO Auto-generated method stub
		return returnButton;
	}

	/* (non-Javadoc)
	 * @see mat.client.measure.metadata.BaseMetaDataPresenter.BaseAddEditDisplay#setReturnToLink(java.lang.String)
	 */
	@Override
	public void setReturnToLink(String s) {	
		returnButton.setText(s);
		returnButton.setTitle(s);
		mainPanel.add(returnButton);
	}
	
	/**
	 * Convert timestamp to string.
	 *
	 * @param ts the ts
	 * @return the string
	 */
	private String convertTimestampToString(Timestamp ts) {
		String tsStr;
		if (ts == null) {
			tsStr = "";
		} else {
			int hours = ts.getHours();
			String ap = hours < 12 ? "AM" : "PM";
			int modhours = hours % 12;
			String mins = ts.getMinutes() + "";
			if (mins.length() == 1) {
				mins = "0" + mins;
			}
			String hoursStr = modhours == 0 ? "12" : modhours+"";
			tsStr = (ts.getMonth() + 1) + "/" + ts.getDate() + "/" + (ts.getYear() + 1900) + " "
					+ hoursStr + ":" + mins + " "+ap;
		}
		return tsStr;
	}
	
	public Observer getObserver() {
		return observer;
	}

	@Override
	public void setObserver(Observer observer) {
		this.observer = observer;
	}
	/* (non-Javadoc)
	 * @see mat.client.measure.metadata.MetaDataPresenter.AddEditComponentMeasuresDisplay#getSelectedFilter()
	 */
	@Override
	public int getSelectedFilter() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public Button getRetButton(){
		return returnButton;
	}
	
//	@Override
//	public HasClickHandlers getReturnButtonHandlers(){
//		return returnButton;
//	}
	
	public Button getAddtoComponentMeasuresBtn(){
		return addtoComponentMeasures;
	}
	
	@Override
	public HasClickHandlers getAddtoComponentMeasuresButtonHandler(){
		return addtoComponentMeasures;
	}
	
	@Override
	public HasClickHandlers getSearchButton(){
		return searchWidget.getSearchButton();
	}
	
	@Override
	public HasValue<String> getSearchString(){
		return searchWidget.getSearchInput();
	}

}
