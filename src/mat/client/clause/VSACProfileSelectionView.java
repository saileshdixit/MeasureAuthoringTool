package mat.client.clause;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import mat.client.CustomPager;
import mat.client.measure.metadata.CustomCheckBox;
import mat.client.shared.ErrorMessageDisplay;
import mat.client.shared.ErrorMessageDisplayInterface;
import mat.client.shared.LabelBuilder;
import mat.client.shared.ListBoxMVP;
import mat.client.shared.MatButtonCell;
import mat.client.shared.MatCheckBoxCell;
import mat.client.shared.MatContext;
import mat.client.shared.MatEditTextCell;
import mat.client.shared.MatSelectionCell;
import mat.client.shared.MatSimplePager;
import mat.client.shared.SpacerWidget;
import mat.client.shared.SuccessMessageDisplay;
import mat.client.shared.SuccessMessageDisplayInterface;
import mat.client.umls.service.VSACAPIServiceAsync;
import mat.client.util.CellTableUtility;
import mat.model.QualityDataSetDTO;
import mat.shared.ClickableSafeHtmlCell;
import mat.shared.ConstantMessages;
import mat.shared.UUIDUtilClient;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCaptionElement;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

// TODO: Auto-generated Javadoc
/**
 * The Class VSACProfileSelectionView.
 */
public class VSACProfileSelectionView implements
		VSACProfileSelectionPresenter.SearchDisplay,
		HasSelectionHandlers<Boolean> {

	/**
	 * The Interface Observer.
	 */
	public static interface Observer {

		/**
		 * On name edit clicked.
		 *
		 * @param result            the result
		 * @param value the value
		 */
		void onNameEditClicked(QualityDataSetDTO result, String value);

		/**
		 * On oid edit clicked.
		 * 
		 * @param result
		 *            the result
		 * @param value
		 *            the value
		 */
		void onOIDEditClicked(QualityDataSetDTO result, String value);

		/**
		 * On save clicked.
		 * 
		 * @param result
		 *            the result
		 */
		void onSaveClicked(QualityDataSetDTO result);

		/**
		 * On modify clicked.
		 * 
		 * @param result
		 *            the result
		 */
		void onModifyClicked(QualityDataSetDTO result);

		/**
		 * On delete clicked.
		 * 
		 * @param result
		 *            the result
		 */
		void onDeleteClicked(QualityDataSetDTO result);
	}

	/** The observer. */
	private Observer observer;

	/** The profile sel. */
	private CustomCheckBox profileSel = new CustomCheckBox("Select a Profile",
			"Use a default Expansion Profile ?", 1);

	/** The vsac profile list box. */
	private ListBoxMVP vsacProfileListBox = new ListBoxMVP();

	/** The container panel. */
	private SimplePanel containerPanel = new SimplePanel();

	/** The vsac profile selection list. */
	private List<String> vsacProfileSelectionList = new ArrayList<String>();

	/** The vsacapi service async. */
	VSACAPIServiceAsync vsacapiServiceAsync = MatContext.get()
			.getVsacapiServiceAsync();

	/** The error message panel. */
	private ErrorMessageDisplay errorMessagePanel = new ErrorMessageDisplay();

	/** The handler manager. */
	private HandlerManager handlerManager = new HandlerManager(this);

	/** The cell table panel. */
	private VerticalPanel cellTablePanel = new VerticalPanel();

	/** The v cell table panel. */
	private VerticalPanel vCellTablePanel = new VerticalPanel();

	/** Cell Table Row Count. */
	private static final int TABLE_ROW_COUNT = 15;

	/** The table. */
	private CellTable<QualityDataSetDTO> table;

	/** The cell table. */
	private CellTable<QualityDataSetDTO> cellTable;

	/** The sort provider. */
	private ListDataProvider<QualityDataSetDTO> sortProvider;

	/** The list provider. */
	private ListDataProvider<QualityDataSetDTO> listProvider;

	/** The Constant PLEASE_SELECT. */
	private static final String PLEASE_SELECT = "--Select--";

	/** The Constant PLEASE_ENTER_NAME. */
	private static final String PLEASE_ENTER_NAME = "Enter Name";

	/** The Constant PLEASE_ENTER_OID. */
	private static final String PLEASE_ENTER_OID = "Enter OID";

	/** The update button. */
	private Button updateVSACButton = new Button("Update");

	/** The add new button. */
	private Button addNewButton = new Button("Add New");

	/** The version list. */
	private List<String> versionList = new ArrayList<String>();

	/** The profile list. */
	private List<String> profileList = new ArrayList<String>();

	/** The success message panel. */
	private SuccessMessageDisplay successMessagePanel;

	/** The last selected object. */
	private QualityDataSetDTO lastSelectedObject;

	/**
	 * Instantiates a new VSAC profile selection view.
	 */
	public VSACProfileSelectionView() {
		successMessagePanel = new SuccessMessageDisplay();
		successMessagePanel.clear();
		HorizontalPanel mainPanel = new HorizontalPanel();
		mainPanel.getElement().setId("mainPanel_HorizontalPanel");
		HorizontalPanel buttonLayout = new HorizontalPanel();
		buttonLayout.getElement().setId("buttonLayout_HorizontalPanel");
		buttonLayout.setStylePrimaryName("continueButton");
		addNewButton.setEnabled(!checkForEnable());	
		updateVSACButton.setEnabled(!checkForEnable());
		addNewButton.setTitle("Add New");
		updateVSACButton.setTitle("Update");
		addNewButton.getElement().setId("modify_Button");
		addNewButton.setStyleName("rightAlignSecondaryButton");
		updateVSACButton.setStylePrimaryName("rightAlignSecondaryButton");
		updateVSACButton.setTitle("Retrieve the most recent versions of applied value sets from VSAC");
		updateVSACButton.getElement().setId("updateVsacButton_Button");
		buttonLayout.add(addNewButton);
		buttonLayout.add(updateVSACButton);
		SimplePanel sp = new SimplePanel();
		sp.add(errorMessagePanel);
		sp.setHeight("50px");
		SimplePanel simplePanel = new SimplePanel();
		simplePanel.setWidth("5px");
		HorizontalPanel hp = new HorizontalPanel();
		hp.getElement().setId("hp_HorizonalPanel");
		hp.add(profileSel);
		vsacProfileListBox.setWidth("200px");
		hp.add(simplePanel);
		hp.add(vsacProfileListBox);
		vsacProfileListBox.addItem("--Select--");
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setStylePrimaryName("qdmCellList");
		verticalPanel.add(new SpacerWidget());
		verticalPanel.add(sp);
		errorMessagePanel.getElement().setId(
				"errorMessagePanel_ErrorMessageDisplay");
		verticalPanel.add(new SpacerWidget());
		verticalPanel.add(hp);
		verticalPanel.add(new SpacerWidget());
		verticalPanel.add(buttonLayout);
		verticalPanel.add(new SpacerWidget());
		verticalPanel.add(vCellTablePanel);
		verticalPanel.add(new SpacerWidget());
		verticalPanel.add(new SpacerWidget());
//		verticalPanel.add(addNewButton);
		verticalPanel.add(new SpacerWidget());
		verticalPanel.add(cellTablePanel);
		verticalPanel.add(new SpacerWidget());
		//updateVSACButtonverticalPanel.add(updateButton);
		mainPanel.add(verticalPanel);
		containerPanel.getElement().setAttribute("id",
				"subQDMAPPliedListContainerPanel");
		containerPanel.add(mainPanel);
		containerPanel.setStyleName("qdsContentPanel");
		MatContext.get().setVSACProfileView(this);
	}

	/**
	 * Builds the cell table.
	 * 
	 * @param appliedListModel
	 *            the applied list model
	 */
	@Override
	public void buildAppliedQDMCellTable(QDSAppliedListModel appliedListModel) {
		cellTablePanel.clear();
		cellTablePanel.setStyleName("cellTablePanel");
		if ((appliedListModel.getAppliedQDMs() != null)
				&& (appliedListModel.getAppliedQDMs().size() > 0)) {
			table = new CellTable<QualityDataSetDTO>();
			table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
			sortProvider = new ListDataProvider<QualityDataSetDTO>();
			// table.setSelectionModel(addSelectionHandlerOnTable(appliedListModel));
			table.setPageSize(TABLE_ROW_COUNT);
			table.redraw();
			sortProvider.refresh();
			// sortProvider.getList().add(addNewRow());
			sortProvider.getList().addAll(appliedListModel.getAppliedQDMs());
			ListHandler<QualityDataSetDTO> sortHandler = new ListHandler<QualityDataSetDTO>(
					sortProvider.getList());
			table.addColumnSortHandler(sortHandler);
			table = addColumnToTable(table, sortHandler);
			sortProvider.addDataDisplay(table);
			MatSimplePager spager;
			CustomPager.Resources pagerResources = GWT
					.create(CustomPager.Resources.class);
			spager = new MatSimplePager(CustomPager.TextLocation.CENTER,
					pagerResources, false, 0, true);
			spager.setDisplay(table);
			spager.setPageStart(0);
			/* spager.setToolTipAndTabIndex(spager); */
			Label invisibleLabel = (Label) LabelBuilder
					.buildInvisibleLabel(
							"appliedQDMTableSummary",
							"In the Following Applied QDM Elements table Name in First Column"
									+ "OID in Second Column, DataType in Third Column, Expansion Profile in Fourth Column,"
									+ "Version in Fifth Column and Modify in Sixth Column where the user can Edit and Delete "
									+ "the existing QDM. The Applied QDM elements are listed alphabetically in a table.");
			table.getElement().setAttribute("id", "AppliedQDMTable");
			table.getElement().setAttribute("aria-describedby",
					"appliedQDMTableSummary");
			cellTablePanel.add(invisibleLabel);
			cellTablePanel.add(table);
			cellTablePanel.add(new SpacerWidget());
			cellTablePanel.add(spager);

		} else {
			Label searchHeader = new Label("Applied QDM Elements");
			searchHeader.getElement().setId("searchHeader_Label");
			searchHeader.setStyleName("recentSearchHeader");
			searchHeader.getElement().setAttribute("tabIndex", "0");
			HTML desc = new HTML("<p> No Applied QDM Elements.</p>");
			cellTablePanel.add(searchHeader);
			cellTablePanel.add(new SpacerWidget());
			cellTablePanel.add(desc);
		}
	}

	/**
	 * Adds the column to table.
	 *
	 * @param table the table
	 * @param sortHandler the sort handler
	 * @return the cell table
	 */
	private CellTable<QualityDataSetDTO> addColumnToTable(
			final CellTable<QualityDataSetDTO> table,
			ListHandler<QualityDataSetDTO> sortHandler) {
		if (table.getColumnCount() != 25) {
			Label searchHeader = new Label("Applied QDM Elements");
			searchHeader.getElement().setId("searchHeader_Label");
			searchHeader.setStyleName("recentSearchHeader");
			searchHeader.getElement().setAttribute("tabIndex", "0");
			com.google.gwt.dom.client.TableElement elem = table.getElement()
					.cast();
			TableCaptionElement caption = elem.createCaption();
			caption.appendChild(searchHeader.getElement());

			// Name Column
			Column<QualityDataSetDTO, SafeHtml> nameColumn = new Column<QualityDataSetDTO, SafeHtml>(
					new SafeHtmlCell()) {
				@Override
				public SafeHtml getValue(QualityDataSetDTO object) {
					StringBuilder title = new StringBuilder();
					String value = null;
					if ((object.getOccurrenceText() != null)
							&& !object.getOccurrenceText().equals("")) {
						value = object.getOccurrenceText() + " of "
								+ object.getCodeListName();
						title = title.append("Name : ").append(value);
					} else {
						value = object.getCodeListName();
						title = title.append("Name : ").append(value);
					}
					return CellTableUtility.getColumnToolTip(value,
							title.toString());
				}
			};
			table.addColumn(nameColumn, SafeHtmlUtils
					.fromSafeConstant("<span title=\"Name\">" + "Name"
							+ "</span>"));

			// OID Column
			Column<QualityDataSetDTO, SafeHtml> oidColumn = new Column<QualityDataSetDTO, SafeHtml>(
					new SafeHtmlCell()) {
				@Override
				public SafeHtml getValue(QualityDataSetDTO object) {
					StringBuilder title = new StringBuilder();
					String oid = null;
					if (object.getOid().equalsIgnoreCase(
							ConstantMessages.USER_DEFINED_QDM_OID)) {
						title = title.append("OID : ").append(
								ConstantMessages.USER_DEFINED_QDM_NAME);
						oid = ConstantMessages.USER_DEFINED_CONTEXT_DESC;
					} else {
						title = title.append("OID : ").append(object.getOid());
						oid = object.getOid();
					}
					return getOIDColumnToolTip(oid, title,
							object.getHasModifiedAtVSAC(),
							object.isNotFoundInVSAC());
				}
			};
			table.addColumn(oidColumn, SafeHtmlUtils
					.fromSafeConstant("<span title=\"OID\">" + "OID"
							+ "</span>"));

			// DataType Column

			Column<QualityDataSetDTO, SafeHtml> dataTypeColumn = new Column<QualityDataSetDTO, SafeHtml>(
					new SafeHtmlCell()) {
				@Override
				public SafeHtml getValue(QualityDataSetDTO object) {
					StringBuilder title = new StringBuilder();
					title = title.append("Datatype : ").append(
							object.getDataType());
					return getDataTypeColumnToolTip(object.getDataType(),
							title, object.getHasModifiedAtVSAC(),
							object.isDataTypeHasRemoved());
				}
			};
			table.addColumn(dataTypeColumn, SafeHtmlUtils
					.fromSafeConstant("<span title=\"Datatype\">" + "Datatype"
							+ "</span>"));

			// Expansion Profile Column
			Column<QualityDataSetDTO, SafeHtml> expansionColumn = new Column<QualityDataSetDTO, SafeHtml>(
					new SafeHtmlCell()) {
				@Override
				public SafeHtml getValue(QualityDataSetDTO object) {
					if (object.getExpansionProfile() != null) {
						StringBuilder title = new StringBuilder();
						title = title.append("Expansion Profile : ").append(
								object.getExpansionProfile());
						return CellTableUtility.getColumnToolTip(
								object.getExpansionProfile(), title.toString());
					}

					return null;
				}
			};
			table.addColumn(expansionColumn, SafeHtmlUtils
					.fromSafeConstant("<span title=\"Expansion Profile\">"
							+ "Expansion Profile" + "</span>"));

			// Version Column
			Column<QualityDataSetDTO, SafeHtml> versionColumn = new Column<QualityDataSetDTO, SafeHtml>(
					new SafeHtmlCell()) {
				@Override
				public SafeHtml getValue(QualityDataSetDTO object) {
					StringBuilder title = new StringBuilder();
					String version = null;
					if (!object.getOid().equalsIgnoreCase(
							ConstantMessages.USER_DEFINED_QDM_OID)) {
						if (object.getVersion().equalsIgnoreCase("1.0")
								|| object.getVersion().equalsIgnoreCase("1")) {
							title = title.append("Version : ").append(
									"Most Recent");
							version = "Most Recent";
						} else {
							if (object.getEffectiveDate() == null) {
								title = title.append("Version : ").append(
										object.getVersion());
								version = object.getVersion();
							} else {
								version = "";
							}
						}
					} else {
						version = "";
					}
					return CellTableUtility.getColumnToolTip(version,
							title.toString());
				}
			};
			table.addColumn(versionColumn, SafeHtmlUtils
					.fromSafeConstant("<span title=\"Version\">" + "Version"
							+ "</span>"));
			// Modify by Delete Column
			table.addColumn(new Column<QualityDataSetDTO, QualityDataSetDTO>(
					getCompositeCellForQDMModifyAndDelete()) {

				@Override
				public QualityDataSetDTO getValue(QualityDataSetDTO object) {
					return object;
				}
			}, SafeHtmlUtils.fromSafeConstant("<span title='Modify'>"
					+ "Modify" + "</span>"));

			table.setColumnWidth(0, 25.0, Unit.PCT);
			table.setColumnWidth(1, 25.0, Unit.PCT);
			table.setColumnWidth(2, 20.0, Unit.PCT);
			table.setColumnWidth(3, 14.0, Unit.PCT);
			table.setColumnWidth(4, 14.0, Unit.PCT);
			table.setColumnWidth(5, 2.0, Unit.PCT);
		}

		return table;
	}

	/**
	 * Adds the selection handler on table.
	 * 
	 * @param appliedListModel
	 *            the applied list model
	 * @return the single selection model
	 */
	public SingleSelectionModel<QualityDataSetDTO> addSelectionHandlerOnTable(
			final QDSAppliedListModel appliedListModel) {
		final SingleSelectionModel<QualityDataSetDTO> selectionModel = new SingleSelectionModel<QualityDataSetDTO>();
		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					@Override
					public void onSelectionChange(SelectionChangeEvent event) {
						QualityDataSetDTO qualityDataSetDTO = selectionModel
								.getSelectedObject();
						if (qualityDataSetDTO != null) {
							errorMessagePanel.clear();
							appliedListModel.setLastSelected(selectionModel
									.getSelectedObject());
							System.out
									.println("appliedListModel.getLastSelected() =======>>>>"
											+ appliedListModel
													.getLastSelected());
						}
					}
				});
		return selectionModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mat.client.clause.VSACProfileSelectionPresenter.SearchDisplay#asWidget()
	 */
	@Override
	public Widget asWidget() {
		return containerPanel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mat.client.clause.VSACProfileSelectionPresenter.SearchDisplay#
	 * getVSACProfileInput()
	 */
	@Override
	public HasValueChangeHandlers<Boolean> getVSACProfileInput() {
		return profileSel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mat.client.clause.VSACProfileSelectionPresenter.SearchDisplay#
	 * getVSACProfileListBox()
	 */
	@Override
	public ListBoxMVP getVSACProfileListBox() {
		return vsacProfileListBox;
	}

	/**
	 * Gets the vsac profile selection list.
	 * 
	 * @return the vsacProfileSelectionList
	 */
	public List<String> getVsacProfileSelectionList() {
		return vsacProfileSelectionList;
	}

	/**
	 * Check for enable.
	 * 
	 * @return true, if successful
	 */
	private boolean checkForEnable() {
		return MatContext.get().getMeasureLockService()
				.checkForEditPermission();
	}

	/*
	 * 
	 * /** Sets the vsac profile selection list.
	 * 
	 * @param vsacProfileSelectionList the vsacProfileSelectionList to set
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see mat.client.clause.VSACProfileSelectionPresenter.SearchDisplay#
	 * setVsacProfileSelectionList(java.util.List)
	 */
	@Override
	public void setVsacProfileSelectionList(
			List<String> vsacProfileSelectionList) {
		this.vsacProfileSelectionList = vsacProfileSelectionList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mat.client.clause.VSACProfileSelectionPresenter.SearchDisplay#
	 * getErrorMessageDisplay()
	 */
	@Override
	public ErrorMessageDisplayInterface getErrorMessageDisplay() {
		return errorMessagePanel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mat.client.clause.VSACProfileSelectionPresenter.SearchDisplay#
	 * setVSACProfileListBox()
	 */
	@Override
	public void setVSACProfileListBox() {
		vsacProfileListBox.clear();
		vsacProfileListBox.addItem("--Select--");
		for (int i = 0; i < profileList.size()
				&& profileList != null; i++) {
			vsacProfileListBox.addItem(profileList.get(i));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mat.client.clause.VSACProfileSelectionPresenter.SearchDisplay#
	 * resetVSACValueSetWidget()
	 */
	@Override
	public void resetVSACValueSetWidget() {
		profileSel.setValue(false);
		vsacProfileListBox.clear();
		vsacProfileListBox.setEnabled(false);
		vsacProfileListBox.addItem("--Select--");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.event.shared.HasHandlers#fireEvent(com.google.gwt.event
	 * .shared.GwtEvent)
	 */
	@Override
	public void fireEvent(GwtEvent<?> event) {
		handlerManager.fireEvent(event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.event.logical.shared.HasSelectionHandlers#addSelectionHandler
	 * (com.google.gwt.event.logical.shared.SelectionHandler)
	 */
	@Override
	public HandlerRegistration addSelectionHandler(
			SelectionHandler<Boolean> handler) {
		return handlerManager.addHandler(SelectionEvent.getType(), handler);
	}

	/**
	 * Gets the observer.
	 * 
	 * @return the observer
	 */
	public Observer getObserver() {
		return observer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mat.client.clause.VSACProfileSelectionPresenter.SearchDisplay#setObserver
	 * (mat.client.clause.VSACProfileSelectionView.Observer)
	 */
	@Override
	public void setObserver(Observer observer) {
		this.observer = observer;
	}

	/**
	 * Adds the new row.
	 * 
	 * @return the quality data set dto
	 */
	public QualityDataSetDTO addNewRow() {
		QualityDataSetDTO detail = new QualityDataSetDTO();
		detail.setDataType(PLEASE_SELECT);
		detail.setCodeListName(PLEASE_ENTER_NAME);
		detail.setOid(PLEASE_ENTER_OID);
		detail.setVersion("");
		detail.setId(UUIDUtilClient.uuid());
		detail.setSpecificOccurrence(false);
		return detail;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mat.client.clause.VSACProfileSelectionPresenter.SearchDisplay#
	 * getListDataProvider()
	 */
	@Override
	public ListDataProvider<QualityDataSetDTO> getListDataProvider() {
		return listProvider;
	}

	/**
	 * Gets the composite cell for bulk export.
	 * 
	 * @return the composite cell for bulk export
	 */
	private CompositeCell<QualityDataSetDTO> getCompositeCellForQDMModifyAndDelete() {
		final List<HasCell<QualityDataSetDTO, ?>> cells = new LinkedList<HasCell<QualityDataSetDTO, ?>>();
		cells.add(getModifyQDMButtonCell());
		cells.add(getDeleteQDMButtonCell());
		CompositeCell<QualityDataSetDTO> cell = new CompositeCell<QualityDataSetDTO>(
				cells) {
			@Override
			public void render(Context context, QualityDataSetDTO object,
					SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<table><tbody><tr>");
				for (HasCell<QualityDataSetDTO, ?> hasCell : cells) {
					render(context, object, sb, hasCell);
				}
				sb.appendHtmlConstant("</tr></tbody></table>");
			}

			@Override
			protected <X> void render(Context context,
					QualityDataSetDTO object, SafeHtmlBuilder sb,
					HasCell<QualityDataSetDTO, X> hasCell) {
				Cell<X> cell = hasCell.getCell();
				sb.appendHtmlConstant("<td class='emptySpaces'>");
				if ((object != null)) {
					cell.render(context, hasCell.getValue(object), sb);
				} else {
					sb.appendHtmlConstant("<span tabindex=\"-1\"></span>");
				}
				sb.appendHtmlConstant("</td>");
			}

			@Override
			protected Element getContainerElement(Element parent) {
				return parent.getFirstChildElement().getFirstChildElement()
						.getFirstChildElement();
			}
		};
		return cell;
	}

	/**
	 * Gets the modify qdm button cell.
	 * 
	 * @return the modify qdm button cell
	 */
	private HasCell<QualityDataSetDTO, SafeHtml> getModifyQDMButtonCell() {

		HasCell<QualityDataSetDTO, SafeHtml> hasCell = new HasCell<QualityDataSetDTO, SafeHtml>() {

			ClickableSafeHtmlCell modifyButonCell = new ClickableSafeHtmlCell();

			@Override
			public Cell<SafeHtml> getCell() {
				return modifyButonCell;
			}

			@Override
			public FieldUpdater<QualityDataSetDTO, SafeHtml> getFieldUpdater() {

				return new FieldUpdater<QualityDataSetDTO, SafeHtml>() {
					@Override
					public void update(int index, QualityDataSetDTO object,
							SafeHtml value) {
						if ((object != null)) {
							observer.onModifyClicked(object);
						}
					}
				};
			}

			@Override
			public SafeHtml getValue(QualityDataSetDTO object) {
				SafeHtmlBuilder sb = new SafeHtmlBuilder();
				String title = "Click to Save QDM";
				String cssClass = "customEditButton";
				sb.appendHtmlConstant("<button type=\"button\" title='" + title
						+ "' tabindex=\"0\" class=\" " + cssClass + "\"/>");
				return sb.toSafeHtml();
			}
		};

		return hasCell;
	}

	/**
	 * Gets the delete qdm button cell.
	 * 
	 * @return the delete qdm button cell
	 */
	private HasCell<QualityDataSetDTO, SafeHtml> getDeleteQDMButtonCell() {

		HasCell<QualityDataSetDTO, SafeHtml> hasCell = new HasCell<QualityDataSetDTO, SafeHtml>() {

			ClickableSafeHtmlCell deleteButonCell = new ClickableSafeHtmlCell();

			@Override
			public Cell<SafeHtml> getCell() {
				return deleteButonCell;
			}

			@Override
			public FieldUpdater<QualityDataSetDTO, SafeHtml> getFieldUpdater() {

				return new FieldUpdater<QualityDataSetDTO, SafeHtml>() {
					@Override
					public void update(int index, QualityDataSetDTO object,
							SafeHtml value) {
						if ((object != null) && !object.isUsed()) {
							lastSelectedObject = object;
							observer.onDeleteClicked(object);
						}
					}
				};
			}

			@Override
			public SafeHtml getValue(QualityDataSetDTO object) {
				SafeHtmlBuilder sb = new SafeHtmlBuilder();
				String title = "Click to Delete QDM";
				String cssClass;
				if (object.isUsed()) {
					cssClass = "customDeleteDisableButton";
					sb.appendHtmlConstant("<button type=\"button\" title='"
							+ title + "' tabindex=\"0\" class=\" " + cssClass
							+ "\"/>");
				} else {
					cssClass = "customDeleteButton";
					sb.appendHtmlConstant("<button type=\"button\" title='"
							+ title + "' tabindex=\"0\" class=\" " + cssClass
							+ "\" disabled/>");
				}
				return sb.toSafeHtml();
			}
		};

		return hasCell;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mat.client.clause.VSACProfileSelectionPresenter.SearchDisplay#
	 * buildAddNewQDMCellTable()
	 */
	@Override
	public void buildAddByModifyQDMCellTable(
			QDSAppliedListModel qdsAppliedListModel) {
		vCellTablePanel.clear();
		vCellTablePanel.setWidth("100%");
		cellTable = new CellTable<QualityDataSetDTO>();
		cellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		listProvider = new ListDataProvider<QualityDataSetDTO>();
		cellTable.redraw();
		listProvider.refresh();
		if (qdsAppliedListModel.getAppliedQDMs() != null) {
			listProvider.getList().addAll(qdsAppliedListModel.getAppliedQDMs());
		} else {
			listProvider.getList().add(addNewRow());
		}
		cellTable = addByModifyColumnToTable(cellTable);
		listProvider.addDataDisplay(cellTable);
		Label invisibleLabel = (Label) LabelBuilder
				.buildInvisibleLabel(
						"addAppliedQDMTableSummary",
						"In the Following Add/Modify QDM Elements Name in the First Column"
								+ "OID in the Second Column, DataType in the Third Column, "
								+ "Expansion Profile in Fourth Column, Version in Fifth Column,"
								+ "Specific Occurrence in Sixth Column and Save in Seventh Column.");
		cellTable.getElement().setAttribute("id", "AddAppliedQDMTable");
		cellTable.getElement().setAttribute("aria-describedby",
				"addAppliedQDMTableSummary");
		vCellTablePanel.addStyleName("valueSetSearchPanel");
		vCellTablePanel.add(invisibleLabel);
		vCellTablePanel.add(cellTable);
	}

	/**
	 * Adds the new column to table.
	 * 
	 * @param cellTable
	 *            the cell table
	 * @return the cell table
	 */
	public CellTable<QualityDataSetDTO> addByModifyColumnToTable(
			CellTable<QualityDataSetDTO> cellTable) {
		Label searchHeader = new Label("Add/Modify QDM Element");
		searchHeader.getElement().setId("searchHeader_Label");
		searchHeader.setStyleName("recentSearchHeader");
		searchHeader.getElement().setAttribute("tabIndex", "0");
		com.google.gwt.dom.client.TableElement elem = cellTable.getElement()
				.cast();
		TableCaptionElement caption = elem.createCaption();
		caption.appendChild(searchHeader.getElement());

		// Name Column in QDM Elements
		Column<QualityDataSetDTO, String> nameColumn = new Column<QualityDataSetDTO, String>(
				new MatEditTextCell()) {
			@Override
			public String getValue(QualityDataSetDTO object) {
				return object.getCodeListName();
			}
		};
		nameColumn
				.setFieldUpdater(new FieldUpdater<QualityDataSetDTO, String>() {
					@Override
					public void update(int index, QualityDataSetDTO object,
							String value) {
						sortProvider.refresh();
						if (!value.equals(PLEASE_ENTER_NAME))
							observer.onNameEditClicked(object, value);

					}
				});
		cellTable.addColumn(nameColumn, SafeHtmlUtils
				.fromSafeConstant("<span title=\"Select Name\">Name</span>"));

		// OID Column in QDM Elements
		Column<QualityDataSetDTO, String> oidColumn = new Column<QualityDataSetDTO, String>(
				new MatEditTextCell()) {
			@Override
			public String getValue(QualityDataSetDTO object) {
				return object.getOid();
			}
		};
		oidColumn
				.setFieldUpdater(new FieldUpdater<QualityDataSetDTO, String>() {
					@Override
					public void update(int index, QualityDataSetDTO object,
							String value) {
						sortProvider.refresh();
						if (!value.equals(PLEASE_ENTER_OID))
							observer.onOIDEditClicked(object, value);

					}
				});
		cellTable.addColumn(oidColumn, SafeHtmlUtils
				.fromSafeConstant("<span title=\"Select OID\">OID</span>"));

		// DataType QDM Elements
		MatSelectionCell dataTypeSel = new MatSelectionCell(MatContext.get()
				.getDataTypeList());
		Column<QualityDataSetDTO, String> dataTypeColumn = new Column<QualityDataSetDTO, String>(
				dataTypeSel) {
			@Override
			public String getValue(QualityDataSetDTO object) {
				return object.getDataType();
			}
		};

		dataTypeColumn
				.setFieldUpdater(new FieldUpdater<QualityDataSetDTO, String>() {
					@Override
					public void update(int index, QualityDataSetDTO object,
							String value) {
						//sortProvider.refresh();
					}
				});

		cellTable
				.addColumn(
						dataTypeColumn,
						SafeHtmlUtils
								.fromSafeConstant("<span title=\"Select Name\">DataType</span>"));
		
		// Expansion Column

		List<String> expProfileList = new ArrayList<String>();
		expProfileList.add(MatContext.PLEASE_SELECT);
		if (profileList != null && profileList.size() > 0) {
			expProfileList.addAll(getProfileList());
		}
		MatSelectionCell expansionSelCell = new MatSelectionCell(expProfileList);
		Column<QualityDataSetDTO, String> expansionColumn = new Column<QualityDataSetDTO, String>(
				expansionSelCell) {

			@Override
			public String getValue(QualityDataSetDTO object) {
				return object.getExpansionProfile();
			}
		};
		cellTable.addColumn(expansionColumn, SafeHtmlUtils
				.fromSafeConstant("<span title=\"Expansion Profile\">"
						+ "Expansion Profile" + "</span>"));

		// Version Column
		List<String> versionList = new ArrayList<String>();
		versionList.add(MatContext.PLEASE_SELECT);
		versionList.addAll(getVersionList());
		MatSelectionCell versionSelCell = new MatSelectionCell(versionList);

		Column<QualityDataSetDTO, String> versionColumn = new Column<QualityDataSetDTO, String>(
				versionSelCell) {

			@Override
			public String getValue(QualityDataSetDTO object) {
				String version = "";
				if (!object.getOid().equalsIgnoreCase(
						ConstantMessages.USER_DEFINED_QDM_OID)) {
					if (object.getVersion()!=null && (object.getVersion().equalsIgnoreCase("1.0")
							|| object.getVersion().equalsIgnoreCase("1"))) {
						version = "Most Recent";
					} 
				} 

				return version;
			}
		};

		cellTable.addColumn(versionColumn, SafeHtmlUtils
				.fromSafeConstant("<span title=\"Version\">" + "Version"
						+ "</span>"));

		// Specific Occurrence CheckBox
		MatCheckBoxCell chckBoxCell = new MatCheckBoxCell();
		Column<QualityDataSetDTO, Boolean> OccurColumn = new Column<QualityDataSetDTO, Boolean>(
				chckBoxCell) {
			@Override
			public Boolean getValue(QualityDataSetDTO object) {
				return object.isSpecificOccurrence();
			}
		};

		cellTable.addColumn(OccurColumn, SafeHtmlUtils
				.fromSafeConstant("<span title=\"Specific Occurence\">"
						+ "Specific Occurence" + "</span>"));

		MatButtonCell saveButtonCell = new MatButtonCell(
				"Click to Save QDM Element", "customFloppyDiskButton");
		Column<QualityDataSetDTO, String> saveColumn = new Column<QualityDataSetDTO, String>(
				saveButtonCell) {

			@Override
			public String getValue(QualityDataSetDTO object) {
				return "Save";
			}
		};

		cellTable
				.addColumn(saveColumn, SafeHtmlUtils
						.fromSafeConstant("<span title=\"Save\">" + "Save"
								+ "</span>"));

		cellTable.setColumnWidth(0, 25.0, Unit.PCT);
		cellTable.setColumnWidth(1, 28.0, Unit.PCT);
		cellTable.setColumnWidth(2, 15.0, Unit.PCT);
		cellTable.setColumnWidth(3, 15.0, Unit.PCT);
		cellTable.setColumnWidth(4, 15.0, Unit.PCT);
		cellTable.setColumnWidth(5, 1.0, Unit.PCT);
		cellTable.setColumnWidth(6, 1.0, Unit.PCT);

		return cellTable;
	}

	/**
	 * Gets the OID column tool tip.
	 * 
	 * @param columnText
	 *            the column text
	 * @param title
	 *            the title
	 * @param hasImage
	 *            the has image
	 * @param isUserDefined
	 *            the is user defined
	 * @return the OID column tool tip
	 */
	private SafeHtml getOIDColumnToolTip(String columnText,
			StringBuilder title, boolean hasImage, boolean isUserDefined) {
		if (hasImage && !isUserDefined) {
			String htmlConstant = "<html>"
					+ "<head> </head> <Body><img src =\"images/bullet_tick.png\" alt=\"QDM Updated From VSAC.\""
					+ "title = \"QDM Updated From VSAC.\"/>"
					+ "<span tabIndex = \"0\" title='" + title + "'>"
					+ columnText + "</span></body>" + "</html>";
			return new SafeHtmlBuilder().appendHtmlConstant(htmlConstant)
					.toSafeHtml();
		} else if (hasImage && isUserDefined) {
			String htmlConstant = "<html>"
					+ "<head> </head> <Body><img src =\"images/userDefinedWarning.png\""
					+ "alt=\"Warning : QDM not available in VSAC.\""
					+ " title=\"QDM not available in VSAC.\"/>"
					+ "<span tabIndex = \"0\" title='" + title + "'>"
					+ columnText + "</span></body>" + "</html>";
			return new SafeHtmlBuilder().appendHtmlConstant(htmlConstant)
					.toSafeHtml();
		} else {
			String htmlConstant = "<html>"
					+ "<head> </head> <Body><span tabIndex = \"0\" title='"
					+ title + "'>" + columnText + "</span></body>" + "</html>";
			return new SafeHtmlBuilder().appendHtmlConstant(htmlConstant)
					.toSafeHtml();
		}
	}

	/**
	 * Gets the data type column tool tip.
	 * 
	 * @param columnText
	 *            the column text
	 * @param title
	 *            the title
	 * @param hasImage
	 *            the has image
	 * @param dataTypeHasRemoved
	 *            the data type has removed
	 * @return the data type column tool tip
	 */
	private SafeHtml getDataTypeColumnToolTip(String columnText,
			StringBuilder title, boolean hasImage, boolean dataTypeHasRemoved) {
		if (hasImage && !dataTypeHasRemoved) {
			String htmlConstant = "<html>"
					+ "<head> </head> <Body><img src =\"images/bullet_tick.png\" alt=\"DataType is Valid.\""
					+ "title = \"DataType is Valid.\"/>"
					+ "<span tabIndex = \"0\" title='" + title + "'>"
					+ columnText + "</span></body>" + "</html>";
			return new SafeHtmlBuilder().appendHtmlConstant(htmlConstant)
					.toSafeHtml();
		} else if (hasImage && dataTypeHasRemoved) {
			String htmlConstant = "<html>"
					+ "<head> </head> <Body><img src =\"images/userDefinedWarning.png\""
					+ "alt=\"Warning : DataType is not Valid.\""
					+ " title=\"DataType is not Valid.\"/>"
					+ "<span tabIndex = \"0\" title='" + title
					+ "' class='clauseWorkSpaceInvalidNode'>" + columnText
					+ "</span></body>" + "</html>";
			return new SafeHtmlBuilder().appendHtmlConstant(htmlConstant)
					.toSafeHtml();
		} else {
			String htmlConstant = "<html>"
					+ "<head> </head> <Body><span tabIndex = \"0\" title='"
					+ title + "'>" + columnText + "</span></body>" + "</html>";
			return new SafeHtmlBuilder().appendHtmlConstant(htmlConstant)
					.toSafeHtml();
		}
	}

	/**
	 * Gets the version list.
	 *
	 * @return the version list
	 */
	public List<String> getVersionList() {
		return versionList;
	}

	/* (non-Javadoc)
	 * @see mat.client.clause.VSACProfileSelectionPresenter.SearchDisplay#setVersionList(java.util.List)
	 */
	@Override
	public void setVersionList(List<String> versionList) {
		this.versionList = versionList;
	}

	/* (non-Javadoc)
	 * @see mat.client.clause.VSACProfileSelectionPresenter.SearchDisplay#getCellTable()
	 */
	@Override
	public CellTable<QualityDataSetDTO> getCellTable() {
		return cellTable;
	}

	/* (non-Javadoc)
	 * @see mat.client.clause.VSACProfileSelectionPresenter.SearchDisplay#getAddNewQDMButton()
	 */
	@Override
	public HasClickHandlers getAddNewQDMButton() {
		return addNewButton;
	}

	/**
	 * Gets the profile list.
	 *
	 * @return the profile list
	 */
	public List<String> getProfileList() {
		return profileList;
	}

	/* (non-Javadoc)
	 * @see mat.client.clause.VSACProfileSelectionPresenter.SearchDisplay#setProfileList(java.util.List)
	 */
	@Override
	public void setProfileList(List<String> profileList) {
		this.profileList = profileList;
	}

	/* (non-Javadoc)
	 * @see mat.client.clause.VSACProfileSelectionPresenter.SearchDisplay#getApplyToMeasureSuccessMsg()
	 */
	@Override
	public SuccessMessageDisplayInterface getApplyToMeasureSuccessMsg() {
		return successMessagePanel;
	}

	/* (non-Javadoc)
	 * @see mat.client.clause.VSACProfileSelectionPresenter.SearchDisplay#getSelectedElementToRemove()
	 */
	@Override
	public QualityDataSetDTO getSelectedElementToRemove() {
		return lastSelectedObject;
	}

}
