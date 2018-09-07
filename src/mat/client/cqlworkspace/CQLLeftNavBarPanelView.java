package mat.client.cqlworkspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Badge;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.NavPills;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.constants.Toggle;

import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import mat.client.shared.CQLSuggestOracle;
import mat.client.shared.ErrorMessageAlert;
import mat.client.shared.MatContext;
import mat.client.shared.MessageAlert;
import mat.client.shared.SuccessMessageAlert;
import mat.client.shared.WarningConfirmationMessageAlert;
import mat.client.shared.WarningMessageAlert;
import mat.client.util.MatTextBox;
import mat.model.ComponentMeasureTabObject;
import mat.model.clause.QDSAttributes;
import mat.model.cql.CQLCode;
import mat.model.cql.CQLDefinition;
import mat.model.cql.CQLFunctions;
import mat.model.cql.CQLIncludeLibrary;
import mat.model.cql.CQLLibraryDataSetObject;
import mat.model.cql.CQLParameter;
import mat.model.cql.CQLQualityDataSetDTO;

public class CQLLeftNavBarPanelView {
	
	private VerticalPanel rightHandNavPanel = new VerticalPanel();
	
	private Map<String, String> defineNameMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

	private Map<String, String> funcNameMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

	private Map<String, String> includeLibraryNameMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

	private HashMap<String, CQLDefinition> definitionMap = new HashMap<String, CQLDefinition>();

	private HashMap<String, CQLFunctions> functionMap = new HashMap<String, CQLFunctions>();

	private HashMap<String, CQLIncludeLibrary> includeLibraryMap = new HashMap<String, CQLIncludeLibrary>();

	private Badge includesBadge = new Badge();
	
	private Badge valueSetBadge = new Badge();
	
	private Badge codesBadge = new Badge();

	private Badge paramBadge = new Badge();

	private Badge defineBadge = new Badge();

	private Badge functionBadge = new Badge();

	private Label includesLabel = new Label("Includes");
	
	private Label valueSetLabel = new Label("Value Sets");
	
	private Label codesLabel = new Label("Codes");

	private Label paramLabel = new Label("Parameter");

	private Label defineLabel = new Label("Definition");

	private Label functionLibLabel = new Label("Function");

	PanelCollapse paramCollapse = new PanelCollapse();

	PanelCollapse defineCollapse = new PanelCollapse();
	
	PanelCollapse functionCollapse = new PanelCollapse();
	
	PanelCollapse includesCollapse = new PanelCollapse();
	
	PanelCollapse codesCollapse = new PanelCollapse();
	
	PanelCollapse valueSetCollapse = new PanelCollapse();
	
	private AnchorListItem viewCQL;

	private AnchorListItem appliedQDM;
	
	private AnchorListItem codesLibrary;

	private AnchorListItem generalInformation;

	private AnchorListItem includesLibrary;

	private AnchorListItem parameterLibrary;

	private AnchorListItem definitionLibrary;

	private AnchorListItem functionLibrary;
	
	private List<CQLIncludeLibrary> viewIncludeLibrarys = new ArrayList<CQLIncludeLibrary>();
	
	private List<CQLParameter> viewParameterList = new ArrayList<CQLParameter>();
	
	private List<CQLDefinition> viewDefinitions = new ArrayList<CQLDefinition>();

	private List<CQLCode> codesList = new ArrayList<CQLCode>();
	
	private List<CQLQualityDataSetDTO> appliedQdmTableList = new ArrayList<CQLQualityDataSetDTO>();
	
	private List<CQLCode> appliedCodeTableList = new ArrayList<CQLCode>();

	private List<CQLLibraryDataSetObject> includeLibraryList = new ArrayList<CQLLibraryDataSetObject>();

	private List<CQLFunctions> viewFunctions = new ArrayList<CQLFunctions>();

	private ListBox includesNameListbox = new ListBox();

	private ListBox defineNameListBox = new ListBox();

	private ListBox funcNameListBox = new ListBox();

	private SuggestBox searchSuggestDefineTextBox;

	private SuggestBox searchSuggestIncludeTextBox;

	private SuggestBox searchSuggestFuncTextBox;

	private SuggestBox searchSuggestParamTextBox;

	private Map<String, String> parameterNameMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

	private HashMap<String, CQLParameter> parameterMap = new HashMap<String, CQLParameter>();

	private ListBox parameterNameListBox = new ListBox();
	
	private VerticalPanel messagePanel = new VerticalPanel();
	
	private MessageAlert successMessageAlert = new SuccessMessageAlert();

	private MessageAlert warningMessageAlert = new WarningMessageAlert();

	private MessageAlert errorMessageAlert = new ErrorMessageAlert();

	private WarningConfirmationMessageAlert warningConfirmationMessageAlert = new WarningConfirmationMessageAlert();

	DeleteConfirmationDialogBox deleteConfirmationDialogBox = new DeleteConfirmationDialogBox();

	private WarningConfirmationMessageAlert globalWarningConfirmationMessageAlert;// = new WarningConfirmationMessageAlert();
	
	private Boolean isPageDirty = false;

	private Boolean isDoubleClick = false;

	private Boolean isNavBarClick = false;
	
	private Boolean isLoading = false;
	
	private String currentSelectedDefinitionObjId = null;

	private String currentSelectedParamerterObjId = null;

	private String currentSelectedFunctionObjId = null;
	
	private String currentSelectedFunctionArgumentObjId = null;
	
	private String currentSelectedFunctionArgumentName = null;

	private String currentSelectedIncLibraryObjId = null;
	
	private String currentSelectedValueSetObjId = null;
	
	private String currentSelectedCodesObjId = null;
	
	private List<QDSAttributes> availableQDSAttributeList;
	
	//Component tab info
	private Badge badge = new Badge();
	private ListBox componentNameListBox = new ListBox();
	private Label componentLabel = new Label("Components");
	private PanelCollapse collapse = new PanelCollapse();
	private SuggestBox searchAliasTextBox;
	private AnchorListItem anchor;
	
	private List<ComponentMeasureTabObject> componentObjectsList = new ArrayList<>();
	private Map<String, ComponentMeasureTabObject> componentObjectsMap = new HashMap<String, ComponentMeasureTabObject>();
	private Map<String, String> aliases = new HashMap<String,String>();
	
	private PanelCollapse buildComponentCollapsablePanel() {
		collapse.clear();
		collapse.setId("collapseComponent");
		PanelBody componentCollapseBody = new PanelBody();
		HorizontalPanel componentHP = new HorizontalPanel();
		VerticalPanel rightVerticalPanel = new VerticalPanel();
		rightVerticalPanel.setSpacing(10);
		rightVerticalPanel.getElement().setId("rhsVerticalPanel_VerticalPanelComponent");
		rightVerticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		Label componentsLabel = new Label("Components");
		
		Map<String, String> aliases = new HashMap<String,String>();
		aliases.clear();
		for(ComponentMeasureTabObject obj : componentObjectsList) {
			aliases.put(obj.getComponentId(), obj.getAlias());
		}
		
		buildSearchAliasBox();
		
		componentNameListBox.clear();
		componentNameListBox.setWidth("180px");
		componentNameListBox.setVisibleItemCount(10);
		componentNameListBox.getElement().setAttribute("id", "componentsListBox");
		
		rightVerticalPanel.add(searchAliasTextBox);
		rightVerticalPanel.add(componentNameListBox);
		
		addSuggestHandler(searchAliasTextBox, componentNameListBox);
		addListBoxHandler(componentNameListBox, searchAliasTextBox);
		
		rightVerticalPanel.setCellHorizontalAlignment(componentsLabel, HasHorizontalAlignment.ALIGN_LEFT);
		componentHP.add(rightVerticalPanel);
		componentCollapseBody.add(componentHP);

		collapse.add(componentCollapseBody);
		return collapse;
	}
	
	private ClickHandler createClickHandler(SuggestBox suggestBox) {
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if ("Search".equals(suggestBox.getText())) {
					suggestBox.setText("");
				}
			}
		};
	}

	private void setBadgeNumber(int size, Badge badge) {
		if (size < 10) {
			badge.setText("0" + size);
		} else {
			badge.setText("" + size);
		}
	}
	
	private void buildComponentsTab() {
		this.anchor.setIcon(IconType.PENCIL);
		this.anchor.setTitle("Component");
		this.anchor.setId("component_Anchor");
		componentLabel.setStyleName("transparentLabel");
		componentLabel.setId("componentsLabel_Label");
		setBadgeNumber(0, badge);
		badge.setPull(Pull.RIGHT);
		badge.setMarginLeft(45);
		badge.setId("componentsBadge_Badge");
		Anchor anchor = (Anchor) (this.anchor.getWidget(0));
		anchor.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				event.stopPropagation();
			}
		});
		anchor.add(componentLabel);
		anchor.add(badge);
		anchor.setDataParent("#navGroup");
		this.anchor.setDataToggle(Toggle.COLLAPSE);
		this.anchor.setHref("#collapseComponent");
		this.anchor.add(collapse);
	}
	

	public VerticalPanel buildMeasureLibCQLView(){
		globalWarningConfirmationMessageAlert = new WarningConfirmationMessageAlert();
		collapse = buildComponentCollapsablePanel();
		includesCollapse = createIncludesCollapsablePanel();
		paramCollapse = createParameterCollapsablePanel();
		defineCollapse = createDefineCollapsablePanel();
		functionCollapse = createFunctionCollapsablePanel();
		buildLeftHandNavBar();
		return rightHandNavPanel;
	}

	private void buildLeftHandNavBar() {
		
		rightHandNavPanel.clear();
		NavPills navPills = new NavPills();
		navPills.setStacked(true);
		navPills.setWidth("200px");

		generalInformation = new AnchorListItem();
		anchor = new AnchorListItem();
		includesLibrary = new AnchorListItem();
		appliedQDM = new AnchorListItem();
		codesLibrary = new AnchorListItem();
		parameterLibrary = new AnchorListItem();
		definitionLibrary = new AnchorListItem();
		functionLibrary = new AnchorListItem();
		viewCQL = new AnchorListItem();

		
		buildGeneralInfoTab();
		buildComponentsTab();
		buildIncludesTab();
		buildValueSetsTab();
		buildCodesTab();
		buildParameterTab();
		buildDefinitionsTab();
		buildFunctionsTab();
		buildViewCQLTab();
		buildMessagePanel();
		
		navPills.add(generalInformation);
		navPills.add(anchor);
		navPills.add(includesLibrary);
		navPills.add(appliedQDM);
		navPills.add(codesLibrary);
		navPills.add(parameterLibrary);
		navPills.add(definitionLibrary);
		navPills.add(functionLibrary);
		navPills.add(viewCQL);

		rightHandNavPanel.add(navPills);
	}
	
	private void buildMessagePanel() {
		messagePanel.add(successMessageAlert);
		messagePanel.add(warningMessageAlert);
		messagePanel.add(errorMessageAlert);
		messagePanel.add(warningConfirmationMessageAlert);
		messagePanel.add(globalWarningConfirmationMessageAlert);
		messagePanel.setStyleName("marginLeft15px");
	}
	
	private void buildGeneralInfoTab() {
		generalInformation.setIcon(IconType.INFO);
		generalInformation.setText("General Information");
		generalInformation.setTitle("General Information");
		generalInformation.setActive(true);
		generalInformation.setId("generatalInformation_Anchor");
	}
	
	
	private void buildIncludesTab() {
		includesLibrary.setIcon(IconType.PENCIL);
		includesLibrary.setTitle("Includes");
		includesBadge.setText("0" + viewIncludeLibrarys.size());
		Anchor includesAnchor = (Anchor) (includesLibrary.getWidget(0));
		// Double Click causing issues.So Event is not propogated
		includesAnchor.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				event.stopPropagation();
			}
		});
		includesLabel.setStyleName("transparentLabel");
		includesLabel.setId("includesLabel_Label");
		includesAnchor.add(includesLabel);
		includesBadge.setPull(Pull.RIGHT);
		includesBadge.setMarginLeft(52);
		includesBadge.setId("includesBadge_Badge");
		includesAnchor.add(includesBadge);
		includesAnchor.setDataParent("#navGroup");
		includesLibrary.setDataToggle(Toggle.COLLAPSE);
		includesLibrary.setHref("#collapseIncludes");
		includesLibrary.setId("includesLibrary_Anchor");
		includesLibrary.add(includesCollapse);
	}
	
	private void buildValueSetsTab() {
		appliedQDM.setIcon(IconType.PENCIL);
		appliedQDM.setTitle("Value Sets");
		valueSetBadge.setText("0" + appliedQdmTableList.size());
		Anchor valueSetAnchor = (Anchor) (appliedQDM.getWidget(0));
		// Double Click causing issues.So Event is not propogated
		valueSetAnchor.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				event.stopPropagation();
			}
		});
		valueSetLabel.setStyleName("transparentLabel");
		valueSetLabel.setId("valueSetLabel_Label");
		valueSetAnchor.add(valueSetLabel);
		valueSetBadge.setPull(Pull.RIGHT);
		valueSetBadge.setMarginLeft(52);
		valueSetBadge.setId("valueSetBadge_Badge");
		valueSetAnchor.add(valueSetBadge);
		valueSetAnchor.setDataParent("#navGroup");
	}
	
	private void buildCodesTab() {
		codesLibrary.setIcon(IconType.PENCIL);
		codesLibrary.setTitle("Codes");
		codesBadge.setText("0" + codesList.size());
		Anchor codesAnchor = (Anchor) (codesLibrary.getWidget(0));
		// Double Click causing issues.So Event is not propogated
		codesAnchor.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				event.stopPropagation();
			}
		});
		codesLabel.setStyleName("transparentLabel");
		codesLabel.setId("codesLabel_Label");
		codesAnchor.add(codesLabel);
		codesBadge.setPull(Pull.RIGHT);
		codesBadge.setMarginLeft(52);
		codesBadge.setId("codesBadge_Badge");
		codesAnchor.add(codesBadge);
		codesAnchor.setDataParent("#navGroup");
	}
	
	private void buildParameterTab() {
		parameterLibrary.setIcon(IconType.PENCIL);
		parameterLibrary.setTitle("Parameter");
		paramBadge.setText("" + viewParameterList.size());
		Anchor paramAnchor = (Anchor) (parameterLibrary.getWidget(0));
		// Double Click causing issues.So Event is not propogated
		paramAnchor.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				event.stopPropagation();
			}
		});
		paramLabel.setStyleName("transparentLabel");
		paramLabel.setId("paramLabel_Label");
		paramAnchor.add(paramLabel);
		paramBadge.setPull(Pull.RIGHT);
		paramBadge.setId("paramBadge_Badge");
		// paramBadge.setMarginLeft(45);
		paramAnchor.add(paramBadge);
		paramAnchor.setDataParent("#navGroup");
		paramAnchor.setDataToggle(Toggle.COLLAPSE);
		parameterLibrary.setHref("#collapseParameter");
		parameterLibrary.setId("parameterLibrary_Anchor");
		parameterLibrary.add(paramCollapse);
	}
	
	private void buildDefinitionsTab() {
		definitionLibrary.setIcon(IconType.PENCIL);
		definitionLibrary.setTitle("Define");
		definitionLibrary.setId("definitionLibrary_Anchor");
		defineBadge.setText("" + viewDefinitions.size());
		Anchor defineAnchor = (Anchor) (definitionLibrary.getWidget(0));
		// Double Click causing issues.So Event is not propogated
		defineAnchor.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				event.stopPropagation();
			}
		});
		defineLabel.setStyleName("transparentLabel");
		defineLabel.setId("defineLabel_Label");
		defineAnchor.add(defineLabel);
		defineBadge.setPull(Pull.RIGHT);
		defineAnchor.add(defineBadge);
		defineBadge.setId("defineBadge_Badge");
		defineAnchor.setDataParent("#navGroup");
		definitionLibrary.setDataToggle(Toggle.COLLAPSE);
		definitionLibrary.setHref("#collapseDefine");
		definitionLibrary.add(defineCollapse);

	}
	
	private void buildFunctionsTab() {
		functionLibrary.setIcon(IconType.PENCIL);
		functionLibrary.setId("functionLibrary_Anchor");
		functionLibrary.setTitle("Functions");

		functionBadge.setText("" + viewFunctions.size());
		Anchor funcAnchor = (Anchor) (functionLibrary.getWidget(0));
		// Double Click causing issues.So Event is not propogated
		funcAnchor.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				event.stopPropagation();
			}
		});
		functionLibLabel.setStyleName("transparentLabel");
		functionLibLabel.setId("functionLibLabel_label");
		funcAnchor.add(functionLibLabel);
		functionBadge.setPull(Pull.RIGHT);

		funcAnchor.add(functionBadge);
		functionBadge.setId("functionBadge_Badge");
		funcAnchor.setDataParent("#navGroup");
		functionLibrary.setDataToggle(Toggle.COLLAPSE);
		functionLibrary.setHref("#collapseFunction");
		functionLibrary.add(functionCollapse);
	}
	
	private void buildViewCQLTab() {
		viewCQL.setIcon(IconType.BOOK);
		viewCQL.setText("View CQL");
		viewCQL.setTitle("View CQL");
		viewCQL.setId("viewCQL_Anchor");
	}
	
	

	private PanelCollapse createIncludesCollapsablePanel() {

		includesCollapse.setId("collapseIncludes");

		PanelBody includesCollapseBody = new PanelBody();

		HorizontalPanel includesFP = new HorizontalPanel();

		VerticalPanel rightVerticalPanel = new VerticalPanel();
		rightVerticalPanel.setSpacing(10);

		rightVerticalPanel.getElement().setId("rhsVerticalPanel_VerticalPanelIncludes");
		rightVerticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		Label includesLibraryLabel = new Label("Includes Library");
		buildSearchSuggestIncludeBox();

		includesNameListbox.clear();
		includesNameListbox.setWidth("180px");
		includesNameListbox.setVisibleItemCount(10);
		includesNameListbox.getElement().setAttribute("id", "includesListBox");

		clearAndAddAliasNamesToListBox();

		addSuggestHandler(searchSuggestIncludeTextBox, includesNameListbox);
		addListBoxHandler(includesNameListbox, searchSuggestIncludeTextBox);

		searchSuggestIncludeTextBox.getElement().setAttribute("id", "searchSuggestIncludesTextBox");
		rightVerticalPanel.add(searchSuggestIncludeTextBox);
		searchSuggestIncludeTextBox.getElement().setId("searchSuggesIncludestTextBox_SuggestBox");
		rightVerticalPanel.add(includesNameListbox);
		includesNameListbox.getElement().setId("includesNameListBox_ListBox");
		rightVerticalPanel.setCellHorizontalAlignment(includesLibraryLabel, HasHorizontalAlignment.ALIGN_LEFT);
		includesFP.add(rightVerticalPanel);
		includesCollapseBody.add(includesFP);

		includesCollapse.add(includesCollapseBody);
		return includesCollapse;

	}

	private void buildSearchSuggestIncludeBox() {
		searchSuggestIncludeTextBox = new SuggestBox(getSuggestOracle(includeLibraryNameMap.values()));
		searchSuggestIncludeTextBox.setWidth("180px");
		searchSuggestIncludeTextBox.setText("Search");
		searchSuggestIncludeTextBox.setTitle("Search Included Alias");
		searchSuggestIncludeTextBox.getElement().setId("searchTextBox_TextBoxIncludesLib");
		searchSuggestIncludeTextBox.getValueBox().addClickHandler(createClickHandler(searchSuggestIncludeTextBox));
	}
	
	private void buildSearchSuggestParamBox() {
		searchSuggestParamTextBox = new SuggestBox(getSuggestOracle(parameterNameMap.values()));
		searchSuggestParamTextBox.setWidth("180px");
		searchSuggestParamTextBox.setText("Search");
		searchSuggestParamTextBox.setTitle("Search Parameter");
		searchSuggestParamTextBox.getElement().setId("searchTextBox_TextBoxParameterLib");
		searchSuggestParamTextBox.getValueBox().addClickHandler(createClickHandler(searchSuggestParamTextBox));
	}

	/**
	 * Creates the parameter collapsable panel.
	 *
	 * @return the panel collapse
	 */
	private PanelCollapse createParameterCollapsablePanel() {
		paramCollapse.setId("collapseParameter");

		PanelBody parameterCollapseBody = new PanelBody();

		HorizontalPanel parameterFP = new HorizontalPanel();

		VerticalPanel rightVerticalPanel = new VerticalPanel();
		rightVerticalPanel.setSpacing(10);

		rightVerticalPanel.getElement().setId("rhsVerticalPanel_VerticalPanelParam");
		rightVerticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		Label paramLibraryLabel = new Label("Parameter Library");
		buildSearchSuggestParamBox();
		
		parameterNameListBox.clear();
		parameterNameListBox.setWidth("180px");
		parameterNameListBox.setVisibleItemCount(10);
		parameterNameListBox.getElement().setAttribute("id", "paramListBox");

		clearAndAddParameterNamesToListBox();

		addSuggestHandler(searchSuggestParamTextBox, parameterNameListBox);
		addListBoxHandler(parameterNameListBox, searchSuggestParamTextBox);

		searchSuggestParamTextBox.getElement().setAttribute("id", "searchSuggestTextBox");
		rightVerticalPanel.add(searchSuggestParamTextBox);
		searchSuggestParamTextBox.getElement().setId("searchSuggestTextBox_SuggestBox");
		rightVerticalPanel.add(parameterNameListBox);
		parameterNameListBox.getElement().setId("paramNameListBox_ListBox");
		rightVerticalPanel.setCellHorizontalAlignment(paramLibraryLabel, HasHorizontalAlignment.ALIGN_LEFT);
		parameterFP.add(rightVerticalPanel);
		parameterCollapseBody.add(parameterFP);
		paramCollapse.add(parameterCollapseBody);
		return paramCollapse;

	}

	/**
	 * Creates the define collapsable panel.
	 *
	 * @return the panel collapse
	 */
	private PanelCollapse createDefineCollapsablePanel() {
		PanelCollapse defineCollapsePanel = new PanelCollapse();
		defineCollapsePanel.setId("collapseDefine");

		PanelBody defineCollapseBody = new PanelBody();

		HorizontalPanel defineFP = new HorizontalPanel();

		VerticalPanel rightVerticalPanel = new VerticalPanel();
		rightVerticalPanel.setSpacing(10);

		rightVerticalPanel.getElement().setId("rhsVerticalPanel_VerticalPanelParam");
		rightVerticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		Label defineLibraryLabel = new Label("Definition Library");
		buildSearchSuggestDefineBox();

		defineNameListBox.clear();
		defineNameListBox.setWidth("180px");
		defineNameListBox.setVisibleItemCount(10);
		defineNameListBox.getElement().setAttribute("id", "defineListBox");

		clearAndAddDefinitionNamesToListBox();

		addSuggestHandler(searchSuggestDefineTextBox, defineNameListBox);
		addListBoxHandler(defineNameListBox, searchSuggestDefineTextBox);

		searchSuggestDefineTextBox.getElement().setAttribute("id", "searchSuggestTextBox");
		rightVerticalPanel.add(searchSuggestDefineTextBox);
		searchSuggestDefineTextBox.getElement().setId("searchSuggestTextBox_SuggestBox");
		rightVerticalPanel.add(defineNameListBox);
		searchSuggestDefineTextBox.getElement().setId("DefineListBox_ListBox");
		rightVerticalPanel.setCellHorizontalAlignment(defineLibraryLabel, HasHorizontalAlignment.ALIGN_LEFT);
		defineFP.add(rightVerticalPanel);
		defineCollapseBody.add(defineFP);
		defineCollapsePanel.add(defineCollapseBody);
		return defineCollapsePanel;

	}

	private void buildSearchAliasBox() {
		aliases.clear();
		for(ComponentMeasureTabObject obj : componentObjectsList) {
			aliases.put(obj.getComponentId(), obj.getAlias());
		}
		
		searchAliasTextBox = new SuggestBox(getSuggestOracle(aliases.values()));
		searchAliasTextBox.setWidth("180px");
		searchAliasTextBox.setText("Search");
		searchAliasTextBox.setTitle("Search Component Alias");
		searchAliasTextBox.getElement().setId("searchSuggesComponentTextBox_SuggestBox");
		searchAliasTextBox.getValueBox().addClickHandler(createClickHandler(searchAliasTextBox));
	}
	
	private void buildSearchSuggestDefineBox() {
		searchSuggestDefineTextBox = new SuggestBox(getSuggestOracle(defineNameMap.values()));
		searchSuggestDefineTextBox.setWidth("180px");
		searchSuggestDefineTextBox.setText("Search");
		searchSuggestDefineTextBox.setTitle("Search Definition");
		searchSuggestDefineTextBox.getElement().setId("searchSuggestDefineTextBox");
		searchSuggestDefineTextBox.getValueBox().addClickHandler(createClickHandler(searchSuggestDefineTextBox));
	}
	
	private void buildSearchSuggestFuncBox() {
		searchSuggestFuncTextBox = new SuggestBox(getSuggestOracle(funcNameMap.values()));
		searchSuggestFuncTextBox.setWidth("180px");
		searchSuggestFuncTextBox.setText("Search");
		searchSuggestFuncTextBox.setTitle("Search Function");
		searchSuggestFuncTextBox.getElement().setId("searchTextBox_TextBoxFuncLib");
		searchSuggestFuncTextBox.getValueBox().addClickHandler(createClickHandler(searchSuggestFuncTextBox));
	}

	/**
	 * Creates the Function collapsable panel.
	 *
	 * @return the panel collapse
	 */
	private PanelCollapse createFunctionCollapsablePanel() {
		PanelCollapse funcCollapsePanel = new PanelCollapse();
		funcCollapsePanel.setId("collapseFunction");

		PanelBody funcCollapseBody = new PanelBody();

		HorizontalPanel funcFP = new HorizontalPanel();

		VerticalPanel rightVerticalPanel = new VerticalPanel();
		rightVerticalPanel.setSpacing(10);

		rightVerticalPanel.getElement().setId("rhsVerticalPanel_VerticalPanelFunc");
		rightVerticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		Label functionLibraryLabel = new Label("Function Library");
		buildSearchSuggestFuncBox();

		funcNameListBox.clear();
		funcNameListBox.setWidth("180px");
		funcNameListBox.setVisibleItemCount(10);
		funcNameListBox.getElement().setAttribute("id", "funcListBox");

		clearAndAddFunctionsNamesToListBox();

		addSuggestHandler(searchSuggestFuncTextBox, funcNameListBox);
		addListBoxHandler(funcNameListBox, searchSuggestFuncTextBox);

		searchSuggestFuncTextBox.getElement().setAttribute("id", "searchSuggestTextBoxFunc");
		rightVerticalPanel.add(searchSuggestFuncTextBox);
		rightVerticalPanel.add(funcNameListBox);

		rightVerticalPanel.setCellHorizontalAlignment(functionLibraryLabel, HasHorizontalAlignment.ALIGN_LEFT);
		funcFP.add(rightVerticalPanel);
		funcCollapseBody.add(funcFP);
		funcCollapsePanel.add(funcCollapseBody);
		return funcCollapsePanel;

	}	
	
	
	/**
	 * Gets the suggest oracle.
	 *
	 * @param values the values
	 * @return the suggest oracle
	 */
	private SuggestOracle getSuggestOracle(Collection<String> values) {
		return new CQLSuggestOracle(values);
	}
	
	/**
	 * Clear and add alias names to list box.
	 */
	public void clearAndAddAliasNamesToListBox() {
		if (includesNameListbox != null) {
			includesNameListbox.clear();
			viewIncludeLibrarys = sortAliasList(viewIncludeLibrarys);
			for (CQLIncludeLibrary incl : viewIncludeLibrarys) {
				includesNameListbox.addItem(incl.getAliasName(), incl.getId());
			}
			// Set tooltips for each element in listbox
			SelectElement selectElement = SelectElement.as(includesNameListbox.getElement());
			com.google.gwt.dom.client.NodeList<OptionElement> options = selectElement.getOptions();
			for (int i = 0; i < options.getLength(); i++) {
				String title = options.getItem(i).getText();
				OptionElement optionElement = options.getItem(i);
				optionElement.setTitle(title);
			}
		}
	}

	/**
	 * Sort alias list.
	 *
	 * @param viewAliasList the view alias list
	 * @return the list
	 */
	private List<CQLIncludeLibrary> sortAliasList(List<CQLIncludeLibrary> viewAliasList) {
		Collections.sort(viewAliasList, new Comparator<CQLIncludeLibrary>() {
			@Override
			public int compare(final CQLIncludeLibrary object1, final CQLIncludeLibrary object2) {
				return object1.getAliasName().compareToIgnoreCase(object2.getAliasName());
			}
		});
		return viewAliasList;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see mat.client.clause.CQLWorkSpacePresenter.ViewDisplay#
	 * clearAndAddDefinitionNamesToListBox()
	 */
	/**
	 * Clear and add definition names to list box.
	 */
	public void clearAndAddDefinitionNamesToListBox() {
		if (defineNameListBox != null) {
			defineNameListBox.clear();
			viewDefinitions = sortDefinitionNames(viewDefinitions);
			for (CQLDefinition define : viewDefinitions) {
				defineNameListBox.addItem(define.getName(), define.getId());
			}
			// Set tooltips for each element in listbox
			SelectElement selectElement = SelectElement.as(defineNameListBox.getElement());
			com.google.gwt.dom.client.NodeList<OptionElement> options = selectElement.getOptions();
			for (int i = 0; i < options.getLength(); i++) {
				String title = options.getItem(i).getText();
				OptionElement optionElement = options.getItem(i);
				optionElement.setTitle(title);
			}
		}
	}

	/**
	 * Sort definition names.
	 *
	 * @param viewDef the view def
	 * @return the list
	 */
	private List<CQLDefinition> sortDefinitionNames(List<CQLDefinition> viewDef) {
		Collections.sort(viewDef, new Comparator<CQLDefinition>() {
			@Override
			public int compare(final CQLDefinition object1, final CQLDefinition object2) {
				return object1.getName().compareToIgnoreCase(object2.getName());
			}
		});
		return viewDef;
	}

	/**
	 * Clear and add functions names to list box.
	 */
	public void clearAndAddFunctionsNamesToListBox() {
		if (funcNameListBox != null) {
			funcNameListBox.clear();
			// sort functions
			viewFunctions = sortFunctionNames(viewFunctions);
			for (CQLFunctions func : viewFunctions) {
				funcNameListBox.addItem(func.getName(), func.getId());
			}
			// Set tooltips for each element in listbox
			SelectElement selectElement = SelectElement.as(funcNameListBox.getElement());
			com.google.gwt.dom.client.NodeList<OptionElement> options = selectElement.getOptions();
			for (int i = 0; i < options.getLength(); i++) {
				String title = options.getItem(i).getText();
				OptionElement optionElement = options.getItem(i);
				optionElement.setTitle(title);
			}
		}
	}

	/**
	 * Sort function names.
	 *
	 * @param viewFunc the view func
	 * @return the list
	 */
	private List<CQLFunctions> sortFunctionNames(List<CQLFunctions> viewFunc) {
		Collections.sort(viewFunc, new Comparator<CQLFunctions>() {
			@Override
			public int compare(final CQLFunctions object1, final CQLFunctions object2) {
				return object1.getName().compareToIgnoreCase(object2.getName());
			}
		});
		return viewFunc;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see mat.client.clause.CQLWorkSpacePresenter.ViewDisplay#
	 * clearAndAddParameterNamesToListBox()
	 */
	/**
	 * Clear and add parameter names to list box.
	 */
	public void clearAndAddParameterNamesToListBox() {
		if (parameterNameListBox != null) {
			parameterNameListBox.clear();
			viewParameterList = sortParamList(viewParameterList);
			for (CQLParameter param : viewParameterList) {
				parameterNameListBox.addItem(param.getName(), param.getId());
			}
			// Set tooltips for each element in listbox
			SelectElement selectElement = SelectElement.as(parameterNameListBox.getElement());
			com.google.gwt.dom.client.NodeList<OptionElement> options = selectElement.getOptions();
			for (int i = 0; i < options.getLength(); i++) {
				String title = options.getItem(i).getText();
				OptionElement optionElement = options.getItem(i);
				optionElement.setTitle(title);
			}
		}
	}

	/**
	 * Sort param list.
	 *
	 * @param viewParamList the view param list
	 * @return the list
	 */
	private List<CQLParameter> sortParamList(List<CQLParameter> viewParamList) {
		Collections.sort(viewParamList, new Comparator<CQLParameter>() {
			@Override
			public int compare(final CQLParameter object1, final CQLParameter object2) {
				return object1.getName().compareToIgnoreCase(object2.getName());
			}
		});
		return viewParamList;
	}
	
	/**
	 * Add Suggest Handler.
	 * 
	 * @param suggestBox
	 *            - {@link SuggestBox}
	 * @param listBox
	 *            - {@link ListBox}
	 */
	private void addSuggestHandler(final SuggestBox suggestBox, final ListBox listBox) {
		suggestBox.addSelectionHandler(new SelectionHandler<Suggestion>() {

			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				String selectedQDMName = event.getSelectedItem().getReplacementString();
				for (int i = 0; i < listBox.getItemCount(); i++) {
					if (selectedQDMName.equals(listBox.getItemText(i))) {
						listBox.setItemSelected(i, true);
						listBox.setFocus(true);
						break;
					}
				}
			}
		});
	}
	
	/**
	 * Add Handler to ListBox.
	 * 
	 * @param listBox
	 *            -ListBox
	 * @param suggestBox
	 *            - {@link SuggestBox}
	 */
	private void addListBoxHandler(final ListBox listBox, final SuggestBox suggestBox) {
		listBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				System.out.println("listbox change event:" + event.getAssociatedType().getName());
				int selectedIndex = listBox.getSelectedIndex();
				String selectedItem = listBox.getItemText(selectedIndex);
				suggestBox.setText(selectedItem);
			}
		});

	}
	
	/**
	 * Update valueset values.
	 * @param appliedValueSetTableList 
	 */
	public void updateValueSetMap(List<CQLQualityDataSetDTO> appliedValueSetTableList) {
		if (getAppliedQdmTableList().size() < 10) {
			getValueSetBadge().setText("0" + appliedValueSetTableList.size());
		} else {
			getValueSetBadge().setText("" + appliedValueSetTableList.size());
		}

	}
	
	
	/**
	 * Update valueset values.
	 * @param appliedValueSetTableList 
	 */
	public void setCodeBadgeValue(List<CQLCode> appliedCodeTableList) {
		if (appliedCodeTableList.size() < 10) {
			getCodesBadge().setText("0" + appliedCodeTableList.size());
		} else {
			getCodesBadge().setText("" + appliedCodeTableList.size());
		}

	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see mat.client.clause.CQLWorkSpacePresenter.ViewDisplay#updateParamMap()
	 */
	/**
	 * Update param map.
	 */
	public void updateParamMap() {
		getParameterMap().clear();
		getParameterNameMap().clear();
		for (CQLParameter parameter : getViewParameterList()) {
			getParameterNameMap().put(parameter.getId(), parameter.getName());
			getParameterMap().put(parameter.getId(), parameter);
		}

		buildSearchSuggestParamBox();
		if (getViewParameterList().size() < 10) {
			getParamBadge().setText("0" + getViewParameterList().size());
		} else {
			getParamBadge().setText("" + getViewParameterList().size());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mat.client.clause.CQLWorkSpacePresenter.ViewDisplay#updateDefineMap()
	 */
	/**
	 * Update define map.
	 */
	public void updateDefineMap() {
		getDefinitionMap().clear();
		getDefineNameMap().clear();
		for (CQLDefinition define : getViewDefinitions()) {
			getDefineNameMap().put(define.getId(), define.getName());
			getDefinitionMap().put(define.getId(), define);
		}

		buildSearchSuggestDefineBox();
		if (getViewDefinitions().size() < 10) {
			getDefineBadge().setText("0" + getViewDefinitions().size());
		} else {
			getDefineBadge().setText("" + getViewDefinitions().size());
		}

	}

	/**
	 * Update function map.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see mat.client.clause.cqlworkspace.CQLWorkSpacePresenter.ViewDisplay#
	 * updateFunctionMap()
	 */
	public void updateFunctionMap() {
		functionMap.clear();
		funcNameMap.clear();
		for (CQLFunctions function : viewFunctions) {
			funcNameMap.put(function.getId(), function.getName());
			functionMap.put(function.getId(), function);
		}
		buildSearchSuggestFuncBox();
		if (viewFunctions.size() < 10) {
			functionBadge.setText("0" + viewFunctions.size());
		} else {
			functionBadge.setText("" + viewFunctions.size());
		}
	}

	/**
	 * Udpate include library map.
	 */
	public void udpateIncludeLibraryMap() {
		includeLibraryMap.clear();
		includeLibraryNameMap.clear();
		for (CQLIncludeLibrary incLibrary : viewIncludeLibrarys) {
			includeLibraryNameMap.put(incLibrary.getId(), incLibrary.getAliasName());
			includeLibraryMap.put(incLibrary.getId(), incLibrary);
		}

		buildSearchSuggestIncludeBox();
		if (viewIncludeLibrarys.size() < 10) {
			includesBadge.setText("0" + viewIncludeLibrarys.size());
		} else {
			includesBadge.setText("" + viewIncludeLibrarys.size());
		}

	}
	
	/**
	 * Update suggest oracle.
	 */
	public void updateSuggestOracle() {
		if (searchSuggestParamTextBox != null) {
			MultiWordSuggestOracle multiWordSuggestOracle = (MultiWordSuggestOracle) searchSuggestParamTextBox
					.getSuggestOracle();
			multiWordSuggestOracle.clear();
			multiWordSuggestOracle.addAll(parameterNameMap.values());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see mat.client.clause.CQLWorkSpacePresenter.ViewDisplay#
	 * updateSuggestDefineOracle()
	 */
	/**
	 * Update suggest define oracle.
	 */
	public void updateSuggestDefineOracle() {
		if (searchSuggestDefineTextBox != null) {
			MultiWordSuggestOracle multiWordSuggestOracle = (MultiWordSuggestOracle) searchSuggestDefineTextBox
					.getSuggestOracle();
			multiWordSuggestOracle.clear();
			multiWordSuggestOracle.addAll(defineNameMap.values());
		}
	}

	/**
	 * Update suggest func oracle.
	 */
	public void updateSuggestFuncOracle() {
		if (searchSuggestFuncTextBox != null) {
			MultiWordSuggestOracle multiWordSuggestOracle = (MultiWordSuggestOracle) searchSuggestFuncTextBox
					.getSuggestOracle();
			multiWordSuggestOracle.clear();
			multiWordSuggestOracle.addAll(funcNameMap.values());
		}
	}

	/**
	 * Gets the define name map.
	 *
	 * @return the define name map
	 */
	public Map<String, String> getDefineNameMap() {
		return defineNameMap;
	}


	/**
	 * Sets the define name map.
	 *
	 * @param defineNameMap the define name map
	 */
	public void setDefineNameMap(Map<String, String> defineNameMap) {
		this.defineNameMap = defineNameMap;
	}


	/**
	 * Gets the func name map.
	 *
	 * @return the func name map
	 */
	public Map<String, String> getFuncNameMap() {
		return funcNameMap;
	}


	/**
	 * Sets the func name map.
	 *
	 * @param funcNameMap the func name map
	 */
	public void setFuncNameMap(Map<String, String> funcNameMap) {
		this.funcNameMap = funcNameMap;
	}


	/**
	 * Gets the include library name map.
	 *
	 * @return the include library name map
	 */
	public Map<String, String> getIncludeLibraryNameMap() {
		return includeLibraryNameMap;
	}


	/**
	 * Sets the include library name map.
	 *
	 * @param includeLibraryNameMap the include library name map
	 */
	public void setIncludeLibraryNameMap(Map<String, String> includeLibraryNameMap) {
		this.includeLibraryNameMap = includeLibraryNameMap;
	}


	/**
	 * Gets the definition map.
	 *
	 * @return the definition map
	 */
	public HashMap<String, CQLDefinition> getDefinitionMap() {
		return definitionMap;
	}


	/**
	 * Sets the definition map.
	 *
	 * @param definitionMap the definition map
	 */
	public void setDefinitionMap(HashMap<String, CQLDefinition> definitionMap) {
		this.definitionMap = definitionMap;
	}


	/**
	 * Gets the function map.
	 *
	 * @return the function map
	 */
	public HashMap<String, CQLFunctions> getFunctionMap() {
		return functionMap;
	}


	/**
	 * Sets the function map.
	 *
	 * @param functionMap the function map
	 */
	public void setFunctionMap(HashMap<String, CQLFunctions> functionMap) {
		this.functionMap = functionMap;
	}
	
	public AnchorListItem getComponentsTab() {
		return anchor;
	}


	/**
	 * Gets the include library map.
	 *
	 * @return the include library map
	 */
	public HashMap<String, CQLIncludeLibrary> getIncludeLibraryMap() {
		return includeLibraryMap;
	}


	/**
	 * Sets the include library map.
	 *
	 * @param includeLibraryMap the include library map
	 */
	public void setIncludeLibraryMap(HashMap<String, CQLIncludeLibrary> includeLibraryMap) {
		this.includeLibraryMap = includeLibraryMap;
	}


	/**
	 * Gets the parameter name map.
	 *
	 * @return the parameter name map
	 */
	public Map<String, String> getParameterNameMap() {
		return parameterNameMap;
	}


	/**
	 * Sets the parameter name map.
	 *
	 * @param parameterNameMap the parameter name map
	 */
	public void setParameterNameMap(Map<String, String> parameterNameMap) {
		this.parameterNameMap = parameterNameMap;
	}

	public HashMap<String, CQLParameter> getParameterMap() {
		return parameterMap;
	}

	public void setParameterMap(HashMap<String, CQLParameter> parameterMap) {
		this.parameterMap = parameterMap;
	}


	/**
	 * Gets the view include librarys.
	 *
	 * @return the view include librarys
	 */
	public List<CQLIncludeLibrary> getViewIncludeLibrarys() {
		return viewIncludeLibrarys;
	}


	/**
	 * Sets the view include librarys.
	 *
	 * @param viewIncludeLibrarys the new view include librarys
	 */
	public void setViewIncludeLibrarys(List<CQLIncludeLibrary> viewIncludeLibrarys) {
		this.viewIncludeLibrarys = viewIncludeLibrarys;
	}


	/**
	 * Gets the view parameter list.
	 *
	 * @return the view parameter list
	 */
	public List<CQLParameter> getViewParameterList() {
		return viewParameterList;
	}


	/**
	 * Sets the view parameter list.
	 *
	 * @param viewParameterList the new view parameter list
	 */
	public void setViewParameterList(List<CQLParameter> viewParameterList) {
		this.viewParameterList = viewParameterList;
	}


	/**
	 * Gets the view definitions.
	 *
	 * @return the view definitions
	 */
	public List<CQLDefinition> getViewDefinitions() {
		return viewDefinitions;
	}


	/**
	 * Sets the view definitions.
	 *
	 * @param viewDefinitions the new view definitions
	 */
	public void setViewDefinitions(List<CQLDefinition> viewDefinitions) {
		this.viewDefinitions = viewDefinitions;
	}


	/**
	 * Gets the view functions.
	 *
	 * @return the view functions
	 */
	public List<CQLFunctions> getViewFunctions() {
		return viewFunctions;
	}


	/**
	 * Sets the view functions.
	 *
	 * @param viewFunctions the new view functions
	 */
	public void setViewFunctions(List<CQLFunctions> viewFunctions) {
		this.viewFunctions = viewFunctions;
	}


	/**
	 * @return the valueSetBadge
	 */
	public Badge getValueSetBadge() {
		return valueSetBadge;
	}


	/**
	 * @param valueSetBadge the valueSetBadge to set
	 */
	public void setValueSetBadge(Badge valueSetBadge) {
		this.valueSetBadge = valueSetBadge;
	}


	/**
	 * @return the codesBadge
	 */
	public Badge getCodesBadge() {
		return codesBadge;
	}


	/**
	 * @param codesBadge the codesBadge to set
	 */
	public void setCodesBadge(Badge codesBadge) {
		this.codesBadge = codesBadge;
	}


	/**
	 * Gets the includes badge.
	 *
	 * @return the includes badge
	 */
	public Badge getIncludesBadge() {
		return includesBadge;
	}


	/**
	 * Sets the includes badge.
	 *
	 * @param includesBadge the new includes badge
	 */
	public void setIncludesBadge(Badge includesBadge) {
		this.includesBadge = includesBadge;
	}


	/**
	 * Gets the param badge.
	 *
	 * @return the param badge
	 */
	public Badge getParamBadge() {
		return paramBadge;
	}


	/**
	 * Sets the param badge.
	 *
	 * @param paramBadge the new param badge
	 */
	public void setParamBadge(Badge paramBadge) {
		this.paramBadge = paramBadge;
	}


	/**
	 * Gets the define badge.
	 *
	 * @return the define badge
	 */
	public Badge getDefineBadge() {
		return defineBadge;
	}


	/**
	 * Sets the define badge.
	 *
	 * @param defineBadge the new define badge
	 */
	public void setDefineBadge(Badge defineBadge) {
		this.defineBadge = defineBadge;
	}


	/**
	 * Gets the function badge.
	 *
	 * @return the function badge
	 */
	public Badge getFunctionBadge() {
		return functionBadge;
	}


	/**
	 * Sets the function badge.
	 *
	 * @param functionBadge the new function badge
	 */
	public void setFunctionBadge(Badge functionBadge) {
		this.functionBadge = functionBadge;
	}


	/**
	 * Gets the right hand nav panel.
	 *
	 * @return the right hand nav panel
	 */
	public VerticalPanel getRightHandNavPanel() {
		return rightHandNavPanel;
	}


	/**
	 * Gets the includes name listbox.
	 *
	 * @return the includes name listbox
	 */
	public ListBox getIncludesNameListbox() {
		return includesNameListbox;
	}


	/**
	 * Sets the includes name listbox.
	 *
	 * @param includesNameListbox the new includes name listbox
	 */
	public void setIncludesNameListbox(ListBox includesNameListbox) {
		this.includesNameListbox = includesNameListbox;
	}


	/**
	 * Gets the define name list box.
	 *
	 * @return the define name list box
	 */
	public ListBox getDefineNameListBox() {
		return defineNameListBox;
	}


	/**
	 * Sets the define name list box.
	 *
	 * @param defineNameListBox the new define name list box
	 */
	public void setDefineNameListBox(ListBox defineNameListBox) {
		this.defineNameListBox = defineNameListBox;
	}


	/**
	 * Gets the func name list box.
	 *
	 * @return the func name list box
	 */
	public ListBox getFuncNameListBox() {
		return funcNameListBox;
	}


	/**
	 * Sets the func name list box.
	 *
	 * @param funcNameListBox the new func name list box
	 */
	public void setFuncNameListBox(ListBox funcNameListBox) {
		this.funcNameListBox = funcNameListBox;
	}


	/**
	 * Gets the parameter name list box.
	 *
	 * @return the parameter name list box
	 */
	public ListBox getParameterNameListBox() {
		return parameterNameListBox;
	}


	/**
	 * Sets the parameter name list box.
	 *
	 * @param parameterNameListBox the new parameter name list box
	 */
	public void setParameterNameListBox(ListBox parameterNameListBox) {
		this.parameterNameListBox = parameterNameListBox;
	}


	/**
	 * Gets the general information.
	 *
	 * @return the general information
	 */
	public AnchorListItem getGeneralInformation() {
		return generalInformation;
	}


	/**
	 * Sets the general information.
	 *
	 * @param generalInformation the new general information
	 */
	public void setGeneralInformation(AnchorListItem generalInformation) {
		this.generalInformation = generalInformation;
	}


	/**
	 * Gets the includes library.
	 *
	 * @return the includes library
	 */
	public AnchorListItem getIncludesLibrary() {
		return includesLibrary;
	}


	/**
	 * Sets the includes library.
	 *
	 * @param includesLibrary the new includes library
	 */
	public void setIncludesLibrary(AnchorListItem includesLibrary) {
		this.includesLibrary = includesLibrary;
	}


	/**
	 * Gets the parameter library.
	 *
	 * @return the parameter library
	 */
	public AnchorListItem getParameterLibrary() {
		return parameterLibrary;
	}


	/**
	 * Sets the parameter library.
	 *
	 * @param parameterLibrary the new parameter library
	 */
	public void setParameterLibrary(AnchorListItem parameterLibrary) {
		this.parameterLibrary = parameterLibrary;
	}


	/**
	 * Gets the definition library.
	 *
	 * @return the definition library
	 */
	public AnchorListItem getDefinitionLibrary() {
		return definitionLibrary;
	}


	/**
	 * Sets the definition library.
	 *
	 * @param definitionLibrary the new definition library
	 */
	public void setDefinitionLibrary(AnchorListItem definitionLibrary) {
		this.definitionLibrary = definitionLibrary;
	}


	/**
	 * Gets the function library.
	 *
	 * @return the function library
	 */
	public AnchorListItem getFunctionLibrary() {
		return functionLibrary;
	}


	/**
	 * Sets the function library.
	 *
	 * @param functionLibrary the new function library
	 */
	public void setFunctionLibrary(AnchorListItem functionLibrary) {
		this.functionLibrary = functionLibrary;
	}


	/**
	 * Gets the view CQL.
	 *
	 * @return the view CQL
	 */
	public AnchorListItem getViewCQL() {
		return viewCQL;
	}


	/**
	 * Sets the view CQL.
	 *
	 * @param viewCQL the new view CQL
	 */
	public void setViewCQL(AnchorListItem viewCQL) {
		this.viewCQL = viewCQL;
	}


	/**
	 * Gets the applied qdm list.
	 *
	 * @return the applied qdm list
	 *//*
	public List<CQLQualityDataSetDTO> getAppliedQdmList() {
		return appliedQdmList;
	}


	*//**
	 * Sets the applied qdm list.
	 *
	 * @param appliedQdmList the new applied qdm list
	 *//*
	public void setAppliedQdmList(List<CQLQualityDataSetDTO> appliedQdmList) {
		this.appliedQdmList = appliedQdmList;
	}*/


	/**
	 * Gets the applied qdm table list.
	 *
	 * @return the applied qdm table list
	 */
	public List<CQLQualityDataSetDTO> getAppliedQdmTableList() {
		return appliedQdmTableList;
	}


	/**
	 * Sets the applied qdm table list.
	 *
	 * @param appliedQdmTableList the new applied qdm table list
	 */
	public void setAppliedQdmTableList(List<CQLQualityDataSetDTO> appliedQdmTableList) {
		this.appliedQdmTableList = appliedQdmTableList;
	}


	/**
	 * Gets the include library list.
	 *
	 * @return the include library list
	 */
	public List<CQLLibraryDataSetObject> getIncludeLibraryList() {
		return includeLibraryList;
	}


	/**
	 * Sets the include library list.
	 *
	 * @param includeLibraryList the new include library list
	 */
	public void setIncludeLibraryList(List<CQLLibraryDataSetObject> includeLibraryList) {
		this.includeLibraryList = includeLibraryList;
	}


	/**
	 * Gets the applied QDM.
	 *
	 * @return the applied QDM
	 */
	public AnchorListItem getAppliedQDM() {
		return appliedQDM;
	}


	/**
	 * Sets the applied QDM.
	 *
	 * @param appliedQDM the new applied QDM
	 */
	public void setAppliedQDM(AnchorListItem appliedQDM) {
		this.appliedQDM = appliedQDM;
	}


	/**
	 * @return the codesLibrary
	 */
	public AnchorListItem getCodesLibrary() {
		return codesLibrary;
	}


	/**
	 * @param codesLibrary the codesLibrary to set
	 */
	public void setCodesLibrary(AnchorListItem codesLibrary) {
		this.codesLibrary = codesLibrary;
	}


	/**
	 * Gets the param collapse.
	 *
	 * @return the param collapse
	 */
	public PanelCollapse getParamCollapse() {
		return paramCollapse;
	}


	/**
	 * Gets the define collapse.
	 *
	 * @return the define collapse
	 */
	public PanelCollapse getDefineCollapse() {
		return defineCollapse;
	}


	/**
	 * Gets the function collapse.
	 *
	 * @return the function collapse
	 */
	public PanelCollapse getFunctionCollapse() {
		return functionCollapse;
	}


	/**
	 * Gets the includes collapse.
	 *
	 * @return the includes collapse
	 */
	public PanelCollapse getIncludesCollapse() {
		return includesCollapse;
	}

	
	/**
	 * Gets the search suggest define text box.
	 *
	 * @return the search suggest define text box
	 */
	public SuggestBox getSearchSuggestDefineTextBox() {
		return searchSuggestDefineTextBox;
	}


	/**
	 * Sets the search suggest define text box.
	 *
	 * @param searchSuggestDefineTextBox the new search suggest define text box
	 */
	public void setSearchSuggestDefineTextBox(SuggestBox searchSuggestDefineTextBox) {
		this.searchSuggestDefineTextBox = searchSuggestDefineTextBox;
	}


	/**
	 * Gets the search suggest include text box.
	 *
	 * @return the search suggest include text box
	 */
	public SuggestBox getSearchSuggestIncludeTextBox() {
		return searchSuggestIncludeTextBox;
	}


	/**
	 * Sets the search suggest include text box.
	 *
	 * @param searchSuggestIncludeTextBox the new search suggest include text box
	 */
	public void setSearchSuggestIncludeTextBox(SuggestBox searchSuggestIncludeTextBox) {
		this.searchSuggestIncludeTextBox = searchSuggestIncludeTextBox;
	}


	/**
	 * Gets the search suggest func text box.
	 *
	 * @return the search suggest func text box
	 */
	public SuggestBox getSearchSuggestFuncTextBox() {
		return searchSuggestFuncTextBox;
	}


	/**
	 * Sets the search suggest func text box.
	 *
	 * @param searchSuggestFuncTextBox the new search suggest func text box
	 */
	public void setSearchSuggestFuncTextBox(SuggestBox searchSuggestFuncTextBox) {
		this.searchSuggestFuncTextBox = searchSuggestFuncTextBox;
	}


	/**
	 * Gets the search suggest param text box.
	 *
	 * @return the search suggest param text box
	 */
	public SuggestBox getSearchSuggestParamTextBox() {
		return searchSuggestParamTextBox;
	}


	/**
	 * Sets the search suggest param text box.
	 *
	 * @param searchSuggestParamTextBox the new search suggest param text box
	 */
	public void setSearchSuggestParamTextBox(SuggestBox searchSuggestParamTextBox) {
		this.searchSuggestParamTextBox = searchSuggestParamTextBox;
	}


	/**
	 * Gets the message panel.
	 *
	 * @return the message panel
	 */
	public VerticalPanel getMessagePanel() {
		return messagePanel;
	}


	/**
	 * Sets the message panel.
	 *
	 * @param messagePanel the new message panel
	 */
	public void setMessagePanel(VerticalPanel messagePanel) {
		this.messagePanel = messagePanel;
	}


	/**
	 * Gets the delete confirmation dialog box.
	 *
	 * @return the delete confirmation dialog box
	 */
	public DeleteConfirmationDialogBox getDeleteConfirmationDialogBox() {
		return deleteConfirmationDialogBox;
	}


	/**
	 * Sets the delete confirmation dialog box.
	 *
	 * @param deleteConfirmationDialogBox the new delete confirmation dialog box
	 */
	public void setDeleteConfirmationDialogBox(DeleteConfirmationDialogBox deleteConfirmationDialogBox) {
		this.deleteConfirmationDialogBox = deleteConfirmationDialogBox;
	}


	/**
	 * Gets the success message alert.
	 *
	 * @return the success message alert
	 */
	public MessageAlert getSuccessMessageAlert() {
		return successMessageAlert;
	}


	/**
	 * Gets the warning message alert.
	 *
	 * @return the warning message alert
	 */
	public MessageAlert getWarningMessageAlert() {
		return warningMessageAlert;
	}

	public MessageAlert getErrorMessageAlert() {
		return errorMessageAlert;
	}

	public WarningConfirmationMessageAlert getWarningConfirmationMessageAlert() {
		return warningConfirmationMessageAlert;
	}

	public WarningConfirmationMessageAlert getGlobalWarningConfirmationMessageAlert() {
		return globalWarningConfirmationMessageAlert;
	}

	public String getCurrentSelectedDefinitionObjId() {
		return currentSelectedDefinitionObjId;
	}

	public void setCurrentSelectedDefinitionObjId(String currentSelectedDefinitionObjId) {
		this.currentSelectedDefinitionObjId = currentSelectedDefinitionObjId;
	}

	public String getCurrentSelectedParamerterObjId() {
		return currentSelectedParamerterObjId;
	}

	public void setCurrentSelectedParamerterObjId(String currentSelectedParamerterObjId) {
		this.currentSelectedParamerterObjId = currentSelectedParamerterObjId;
	}

	public void setIsPageDirty(Boolean isPageDirty) {
		this.isPageDirty = isPageDirty;
	}
	
	public void setIsDoubleClick(Boolean isDoubleClick) {
		this.isDoubleClick = isDoubleClick;
	}

	public Boolean isDoubleClick() {
		return isDoubleClick;
	}

	public void setIsNavBarClick(Boolean isNavBarClick) {
		this.isNavBarClick = isNavBarClick;
	}

	public Boolean isNavBarClick() {
		return isNavBarClick;
	}

	public Boolean getIsPageDirty() {
		return isPageDirty;
	}

	public void showUnsavedChangesWarning() {
		getWarningMessageAlert().clearAlert();
		getErrorMessageAlert().clearAlert();
		getSuccessMessageAlert().clearAlert();
		getGlobalWarningConfirmationMessageAlert().clearAlert();
		getWarningConfirmationMessageAlert().createAlert();
		getWarningConfirmationMessageAlert().getWarningConfirmationYesButton().setFocus(true);
	}

	public void showGlobalUnsavedChangesWarning() {
		getWarningMessageAlert().clearAlert();
		getErrorMessageAlert().clearAlert();
		getSuccessMessageAlert().clearAlert();
		getWarningConfirmationMessageAlert().clearAlert();
		getGlobalWarningConfirmationMessageAlert().createAlert();
		getGlobalWarningConfirmationMessageAlert().getWarningConfirmationYesButton().setFocus(true);
	}

	
	/**
	 * Show delete confirmation message alert.
	 *
	 * @param message the message
	 */
	public void showDeleteConfirmationMessageAlert(String message) {
		getWarningMessageAlert().clearAlert();
		getErrorMessageAlert().clearAlert();
		getSuccessMessageAlert().clearAlert();
		getWarningConfirmationMessageAlert().clearAlert();
	}
	
	
	/**
	 * Gets the owner name.
	 *
	 * @param cqlLibrary the cql library
	 * @return the owner name
	 */
	public String getOwnerName(CQLLibraryDataSetObject cqlLibrary) {
		StringBuilder owner = new StringBuilder();
		owner = owner.append(cqlLibrary.getOwnerFirstName()).append(" ").append(cqlLibrary.getOwnerLastName());
		return owner.toString();
	}

	/**
	 * Gets the current selected function obj id.
	 *
	 * @return the current selected function obj id
	 */
	public String getCurrentSelectedFunctionObjId() {
		return currentSelectedFunctionObjId;
	}


	/**
	 * Sets the current selected function obj id.
	 *
	 * @param currentSelectedFunctionObjId the new current selected function obj id
	 */
	public void setCurrentSelectedFunctionObjId(String currentSelectedFunctionObjId) {
		this.currentSelectedFunctionObjId = currentSelectedFunctionObjId;
	}


	/**
	 * @return the currentSelectedFunctionArgumentObjId
	 */
	public String getCurrentSelectedFunctionArgumentObjId() {
		return currentSelectedFunctionArgumentObjId;
	}


	/**
	 * @param currentSelectedFunctionArgumentObjId the currentSelectedFunctionArgumentObjId to set
	 */
	public void setCurrentSelectedFunctionArgumentObjId(String currentSelectedFunctionArgumentObjId) {
		this.currentSelectedFunctionArgumentObjId = currentSelectedFunctionArgumentObjId;
	}


	/**
	 * @return the currentSelectedFunctionArgumentName
	 */
	public String getCurrentSelectedFunctionArgumentName() {
		return currentSelectedFunctionArgumentName;
	}


	/**
	 * @param currentSelectedFunctionArgumentName the currentSelectedFunctionArgumentName to set
	 */
	public void setCurrentSelectedFunctionArgumentName(String currentSelectedFunctionArgumentName) {
		this.currentSelectedFunctionArgumentName = currentSelectedFunctionArgumentName;
	}


	/**
	 * Gets the current selected inc library obj id.
	 *
	 * @return the current selected inc library obj id
	 */
	public String getCurrentSelectedIncLibraryObjId() {
		return currentSelectedIncLibraryObjId;
	}


	/**
	 * Sets the current selected inc library obj id.
	 *
	 * @param currentSelectedIncLibraryObjId the new current selected inc library obj id
	 */
	public void setCurrentSelectedIncLibraryObjId(String currentSelectedIncLibraryObjId) {
		this.currentSelectedIncLibraryObjId = currentSelectedIncLibraryObjId;
	}


	/**
	 * Sets the warning message alert.
	 *
	 * @param warningMessageAlert the new warning message alert
	 */
	public void setWarningMessageAlert(MessageAlert warningMessageAlert) {
		this.warningMessageAlert = warningMessageAlert;
	}

	
	/**
	 * Gets the included list.
	 *
	 * @param includeMap the include map
	 * @return the included list
	 */
	public List<String> getIncludedList(Map<String, CQLIncludeLibrary> includeMap) {
		List<String> list = new ArrayList<String>();
		for (Map.Entry<String, CQLIncludeLibrary> entry : includeMap.entrySet()) {
			list.add(entry.getValue().getCqlLibraryId());
		}
		return list;
	}
	
	/**
	 * Gets the warning confirmation yes button.
	 *
	 * @return the warning confirmation yes button
	 */
	public Button getWarningConfirmationYesButton() {
		return warningConfirmationMessageAlert.getWarningConfirmationYesButton();
	}
	
	/**
	 * Gets the warning confirmation no button.
	 *
	 * @return the warning confirmation no button
	 */
	public Button getWarningConfirmationNoButton() {
		return warningConfirmationMessageAlert.getWarningConfirmationNoButton();
	}

	/**
	 * Gets the global warning confirmation yes button.
	 *
	 * @return the global warning confirmation yes button
	 */
	public Button getGlobalWarningConfirmationYesButton() {
		return globalWarningConfirmationMessageAlert.getWarningConfirmationYesButton();
	}

	/**
	 * Gets the global warning confirmation no button.
	 *
	 * @return the global warning confirmation no button
	 */
	public Button getGlobalWarningConfirmationNoButton() {
		return globalWarningConfirmationMessageAlert.getWarningConfirmationNoButton();
	}

	/**
	 * Gets the delete confirmation dialog box yes button.
	 *
	 * @return the delete confirmation dialog box yes button
	 */
	public Button getDeleteConfirmationDialogBoxYesButton() {
		return deleteConfirmationDialogBox.getYesButton();
	}

	/**
	 * Gets the delete confirmation dialog box no button.
	 *
	 * @return the delete confirmation dialog box no button
	 */
	public Button getDeleteConfirmationDialogBoxNoButton() {
		return deleteConfirmationDialogBox.getNoButton();
	}


	/**
	 * Sets the available QDS attribute list.
	 *
	 * @param result the new available QDS attribute list
	 */
	public void setAvailableQDSAttributeList(List<QDSAttributes> result) {
		availableQDSAttributeList = result;
	}
	
	/**
	 * Gets the available QDS attribute list.
	 *
	 * @return the available QDS attribute list
	 */
	public List<QDSAttributes> getAvailableQDSAttributeList(){
		return availableQDSAttributeList;
	}
	
	public void clearShotcutKeyList(){
		MatContext.get().getParameters().clear();
		MatContext.get().getDefinitions().clear();
		MatContext.get().getFuncs().clear();
		MatContext.get().getValuesets().clear();
		MatContext.get().getIncludes().clear();
		MatContext.get().getIncludedValueSetNames().clear();
		MatContext.get().getIncludedCodeNames().clear();
		MatContext.get().getIncludedParamNames().clear();
		MatContext.get().getIncludedDefNames().clear();
		MatContext.get().getIncludedFuncNames().clear();
		
	}
	
	public void buildInfoPanel(Widget sourceWidget) {

		PopupPanel panel = new PopupPanel();
		panel.setAutoHideEnabled(true);
		panel.setPopupPosition(sourceWidget.getAbsoluteLeft() + 40, sourceWidget.getAbsoluteTop() + 20);
		VerticalPanel dialogContents = new VerticalPanel();
		dialogContents.getElement().setId("dialogContents_VerticalPanel");
		panel.setWidget(dialogContents);

		HTML html1 = new HTML("Ctrl-Alt-a: attributes");
		HTML html2 = new HTML("Ctrl-Alt-y: datatypes");
		HTML html3 = new HTML("Ctrl-Alt-d: definitions");
		HTML html4 = new HTML("Ctrl-Alt-f: functions");
		HTML html5 = new HTML("Ctrl-Alt-k: keywords");
		HTML html6 = new HTML("Ctrl-Alt-p: parameters");
		HTML html7 = new HTML("Ctrl-Alt-t: timings");
		HTML html8 = new HTML("Ctrl-Alt-u: units");
		HTML html9 = new HTML("Ctrl-Alt-v: value sets & codes");
		HTML html10 = new HTML("Ctrl-Space: all");

		dialogContents.add(html1);
		dialogContents.add(html2);
		dialogContents.add(html3);
		dialogContents.add(html4);
		dialogContents.add(html5);
		dialogContents.add(html6);
		dialogContents.add(html7);
		dialogContents.add(html8);
		dialogContents.add(html9);
		dialogContents.add(html10);
		panel.show();
	}


	public Boolean getIsLoading() {
		return isLoading;
	}


	public void setIsLoading(Boolean isLoading) {
		this.isLoading = isLoading;
	}


	public List<CQLCode> getCodesTableList() {
		return codesList;
	}


	public List<CQLCode> getAppliedCodeTableList() {
		return appliedCodeTableList;
	}


	public void setAppliedCodeTableList(List<CQLCode> appliedCodeTableList) {
		this.appliedCodeTableList = appliedCodeTableList;
	}


	/**
	 * @return the currentSelectedCodesObjId
	 */
	public String getCurrentSelectedCodesObjId() {
		return currentSelectedCodesObjId;
	}


	/**
	 * @param currentSelectedCodesObjId the currentSelectedCodesObjId to set
	 */
	public void setCurrentSelectedCodesObjId(String currentSelectedCodesObjId) {
		this.currentSelectedCodesObjId = currentSelectedCodesObjId;
	}


	/**
	 * @return the currentSelectedValueSetObjId
	 */
	public String getCurrentSelectedValueSetObjId() {
		return currentSelectedValueSetObjId;
	}


	/**
	 * @param currentSelectedValueSetObjId the currentSelectedValueSetObjId to set
	 */
	public void setCurrentSelectedValueSetObjId(String currentSelectedValueSetObjId) {
		this.currentSelectedValueSetObjId = currentSelectedValueSetObjId;
	}
	
	public void setFocus(MatTextBox matTextBox){
		matTextBox.setFocus(true);
	}
	
	public void setFocus(AceEditor aceEditor){
		aceEditor.focus();
	}
	
	public void setFocus(FocusPanel focusPanel){
		focusPanel.setFocus(true);
	}

	public void setFocus(TextBox aliasNameTxtArea) {
		aliasNameTxtArea.setFocus(true);
	}
	
	public boolean checkForIncludedLibrariesQDMVersion(boolean isStandalone){
		boolean isValid = true;
		//if it is a measure we check if the measure is a Draft first because we only care about draft measures for invalid qdm versions.
		//Versioned measures have been versioned with valid QDM versioned libraries included. So we return if the measure is not a draft
		if(!isStandalone && !MatContext.get().isDraftMeasure()) {
			return isValid;
		}
		//if it is a stand alone library We check if the library is a Draft first because we only care about draft library for invalid qdm versions.
		//Versioned measures have been versioned with valid QDM versioned libraries included. So we return if the library is not a draft.
		if(isStandalone && !MatContext.get().isDraftLibrary()) {
			return isValid;
		}
		List<CQLIncludeLibrary> includedLibraryList = getViewIncludeLibrarys();
		if(includedLibraryList.size()==0){
			isValid = true;
		} else {
			for(CQLIncludeLibrary cqlIncludeLibrary : includedLibraryList){
				if(cqlIncludeLibrary.getQdmVersion()==null){
					continue;
				}else if(!cqlIncludeLibrary.getQdmVersion().equalsIgnoreCase(MatContext.get().getCurrentQDMVersion())){
					isValid = false;
					break;
				}
			}
		}
		return isValid;
	}
	
	public ListBox getComponentsListBox() {
		return componentNameListBox;
	}

	public Map<String, ComponentMeasureTabObject> getComponentMap() {
		return componentObjectsMap;
	}

	public UIObject getComponentsCollapse() {
		return collapse;
	}

	public void updateComponentInformation(List<ComponentMeasureTabObject> componentMeasures) {
		componentObjectsList.clear();
		componentObjectsMap.clear();
		aliases.clear();
		componentObjectsList.addAll(componentMeasures);
		if (componentNameListBox != null) {
			componentNameListBox.clear();
			sortComponentsList(componentObjectsList);
			for (ComponentMeasureTabObject object : componentObjectsList) {
				componentObjectsMap.put(object.getComponentId(), object);
				componentNameListBox.addItem(object.getAlias(), object.getComponentId());
				aliases.put(object.getComponentId(), object.getAlias());
			}
			// Set tooltips for each element in listbox
			SelectElement selectElement = SelectElement.as(componentNameListBox.getElement());
			com.google.gwt.dom.client.NodeList<OptionElement> options = selectElement.getOptions();
			for (int i = 0; i < options.getLength(); i++) {
				String title = options.getItem(i).getText();
				OptionElement optionElement = options.getItem(i);
				optionElement.setTitle(title);
			}
		}
		
		setBadgeNumber(componentObjectsList.size(), badge);
		updateComponentSearchBox();
	}
	
	public void updateComponentSearchBox() {
		buildSearchAliasBox();
	}
	
	private void sortComponentsList(List<ComponentMeasureTabObject> objectList) {
		Collections.sort(objectList, new Comparator<ComponentMeasureTabObject>() {
			@Override
			public int compare(final ComponentMeasureTabObject object1, final ComponentMeasureTabObject object2) {
				return (object1.getAlias()).compareToIgnoreCase(object2.getAlias());
			}
		});
	}


}