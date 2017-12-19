package mat.client.clause.cqlworkspace;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;

import mat.client.Mat;
import mat.client.MatPresenter;
import mat.client.MeasureComposerPresenter;
import mat.client.clause.clauseworkspace.model.SortedClauseMapResult;
import mat.client.clause.cqlworkspace.model.PopulationClauseObject;
import mat.client.clause.cqlworkspace.model.PopulationDataModel;
import mat.client.clause.cqlworkspace.model.PopulationsObject;
import mat.client.clause.cqlworkspace.model.StrataDataModel;
import mat.client.clause.cqlworkspace.model.StratificationsObject;
import mat.client.measure.service.MeasureServiceAsync;
import mat.client.shared.MatContext;

/**
 * The Class CQLPopulationWorkSpacePresenter.
 */
public class CQLPopulationWorkSpacePresenter implements MatPresenter {

	/** The panel. */
	private SimplePanel panel = new SimplePanel();

	/** The clicked menu. */
	private String currentSection = CQLWorkSpaceConstants.CQL_VIEWPOPULATIONS;
	/** The next clicked menu. */
	private String nextSection = CQLWorkSpaceConstants.CQL_VIEWPOPULATIONS;

	/** The search display. */
	private ViewDisplay searchDisplay;

	/** The service. */
	private MeasureServiceAsync service = MatContext.get().getMeasureService();

	/**
	 * The Interface ViewDisplay.
	 */
	public static interface ViewDisplay {

		/**
		 * Top Main panel of CQL Workspace Tab.
		 * 
		 * @return HorizontalPanel
		 */
		VerticalPanel getMainPanel();

		/**
		 * Generates View for CQLWorkSpace tab.
		 * 
		 * @param document
		 */
		void buildView(Document document, PopulationDataModel populationDataModel);

		/**
		 * Gets the main v panel.
		 *
		 * @return the main v panel
		 */
		Widget asWidget();

		/**
		 * Gets the clicked menu.
		 *
		 * @return the clicked menuF
		 */
		String getClickedMenu();

		/**
		 * Sets the clicked menu.
		 *
		 * @param clickedMenu
		 *            the new clicked menu
		 */
		void setClickedMenu(String clickedMenu);

		/**
		 * Gets the main h panel.
		 *
		 * @return the main h panel
		 */
		HorizontalPanel getMainHPanel();

		/**
		 * Sets the next clicked menu.
		 *
		 * @param nextClickedMenu
		 *            the new next clicked menu
		 */
		void setNextClickedMenu(String nextClickedMenu);

		/**
		 * Gets the next clicked menu.
		 *
		 * @return the next clicked menu
		 */
		Object getNextClickedMenu();

		/**
		 * Gets the main flow panel.
		 *
		 * @return the main flow panel
		 */
		FlowPanel getMainFlowPanel();

		/**
		 * Gets the cql left nav bar panel view.
		 *
		 * @return the cql left nav bar panel view
		 */
		CQLPopulationLeftNavBarPanelView getCqlLeftNavBarPanelView();

		/**
		 * Reset message display.
		 */
		void resetMessageDisplay();

		/**
		 * Reset all.
		 */
		void resetAll();

		void setHeadingBasedOnCurrentSection(String headingText, String panelId);

		CQLViewPopulationsDisplay getCqlViewPopulationsDisplay();

		void buildViewPopulations();

		void displayMeasureObservations();

		void displayStratification();

		void displayPopulationDetailView(String populationType);

		void setObserver(CQLPopulationObserver cqlPopulationObserver);

		CQLStratificationDetailView getCqlStratificationDetailView();

		CQLMeasureObservationDetailView getCqlMeasureObservationDetailView();

		CQLPopulationDetail getCqlPopulationDetailView();

		void setCqlStratificationDetailView(CQLStratificationDetailView cqlStratificationDetailView);

		void setCqlMeasureObservationDetailView(CQLMeasureObservationDetailView cqlMeasureObservationDetailView);

		void setCqlPopulationDetailView(CQLPopulationDetail cqlPopulationDetailView);
	}

	/**
	 * Instantiates a new CQL presenter nav bar with list.
	 *
	 * @param srchDisplay
	 *            the srch display
	 */
	public CQLPopulationWorkSpacePresenter(final ViewDisplay srchDisplay) {
		searchDisplay = srchDisplay;
		addEventHandlers();
	}

	private void addEventHandlers() {
		searchDisplay.setObserver(new CQLPopulationObserver() {
			
			private PopulationClauseObject buildStratum(int sequenceNumber) {
				PopulationClauseObject stratum = new PopulationClauseObject();
				stratum.setDisplayName(CQLWorkSpaceConstants.CQL_STRATUM + " " + sequenceNumber);
				stratum.setSequenceNumber(sequenceNumber);
				return stratum;
			}
			
			@Override
			public void onViewHRClick(PopulationClauseObject population) {
				if (population.getCqlExpressionDisplayName().equals("")) {
					showHumanReadableDialogBox("<html></html>", population.getDisplayName());
				}

				else {
					MatContext.get().getMeasureService().getHumanReadableForNode(MatContext.get().getCurrentMeasureId(),
							population.toXML(), new AsyncCallback<String>() {
								@Override
								public void onSuccess(String result) {
									showHumanReadableDialogBox(result, population.getDisplayName());
								}

								@Override
								public void onFailure(Throwable caught) {
									Window.alert(MatContext.get().getMessageDelegate().getGenericErrorMessage());
								}
							});
				}

			}

			@Override
			public void onSaveClick(PopulationDataModel populationDataModel) {
				if (currentSection.equalsIgnoreCase(CQLWorkSpaceConstants.CQL_MEASUREOBSERVATIONS)) {
					searchDisplay.getCqlMeasureObservationDetailView().setIsDirty(false);
				} else {
					searchDisplay.getCqlPopulationDetailView().setIsDirty(false);
				}

			}

			@Override
			public void onDeleteClick(Grid grid, PopulationClauseObject clauseObject, int rowIndex) {
				if (currentSection.equalsIgnoreCase(CQLWorkSpaceConstants.CQL_MEASUREOBSERVATIONS)) {
					grid.removeRow(rowIndex);
					searchDisplay.getCqlMeasureObservationDetailView().getPopulationsObject().getPopulationClauseObjectList().remove(clauseObject);
					if (searchDisplay.getCqlMeasureObservationDetailView().getPopulationsObject().getPopulationClauseObjectList().size() == 1) {
						((Button) grid.getWidget(0, 3)).setEnabled(false);
					}
				} else {
					searchDisplay.getCqlPopulationDetailView().setIsDirty(true);
				}
			}

			@Override
			public void onAddNewClick(Grid populationGrid, PopulationsObject populationsObject) {
				int sequenceNumber = populationsObject.getLastClauseSequenceNumber() + 1;
				String displayName = populationsObject.getPopulationType() + " " + (sequenceNumber);
				PopulationClauseObject popClause = new PopulationClauseObject();
				popClause.setDisplayName(displayName);
				popClause.setSequenceNumber(sequenceNumber);
				populationsObject.getPopulationClauseObjectList().add(popClause);
				if (currentSection.equalsIgnoreCase(CQLWorkSpaceConstants.CQL_MEASUREOBSERVATIONS)) {
					searchDisplay.getCqlMeasureObservationDetailView().populateGrid(populationsObject.getPopulationClauseObjectList(), populationGrid,
							populationsObject.getPopulationClauseObjectList().size() - 1);
					if (searchDisplay.getCqlMeasureObservationDetailView().getPopulationsObject().getPopulationClauseObjectList().size() > 1) {
						((Button) populationGrid.getWidget(0, 3)).setEnabled(true);
					}
				} else {
					searchDisplay.getCqlPopulationDetailView().populateGrid(populationsObject.getPopulationClauseObjectList(), populationGrid,
							populationsObject.getPopulationClauseObjectList().size() - 1);
				}
			}
			
			@Override
			public void onAddNewStratificationClick(StrataDataModel strataDataModel) {
				int sequenceNumber = strataDataModel.getLastPopulationSequenceNumber() + 1;
				String displayName = "Stratification " + sequenceNumber;
				StratificationsObject stratificationsObject = new StratificationsObject(CQLWorkSpaceConstants.CQL_STRATIFICATIONS);
				stratificationsObject.setSequenceNumber(sequenceNumber);
				stratificationsObject.setDisplayName(displayName);
				
				if(strataDataModel.getStratificationObjectList().size() <= 1) {
					// enable the first delete button
					Button deleteButton = (Button) searchDisplay.getCqlStratificationDetailView().getParentGridList().get(0).getWidget(0, 2);
					deleteButton.setEnabled(true);
				}
				
				strataDataModel.getStratificationObjectList().add(stratificationsObject);
				searchDisplay.getCqlStratificationDetailView().addStratificationGrid(stratificationsObject);
				PopulationClauseObject popClause = buildStratum(1);
				stratificationsObject.getPopulationClauseObjectList().add(popClause);
				searchDisplay.getCqlStratificationDetailView().addStratumGrid(stratificationsObject);				
			}

			@Override
			public void onAddNewStratumClick(StratificationsObject stratificationsObject) {
				int sequenceNumber = stratificationsObject.getLastClauseSequenceNumber() + 1;
				PopulationClauseObject popClause = buildStratum(sequenceNumber);
				stratificationsObject.getPopulationClauseObjectList().add(popClause);
				searchDisplay.getCqlStratificationDetailView().addStratumGrid(stratificationsObject);
			}

			@Override
			public void onSaveClick() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDeleteStratificationClick(Grid stratificationGrid, StratificationsObject stratification) {
				// get the view
				CQLStratificationDetailView view = searchDisplay.getCqlStratificationDetailView();
				
				// remove from the view
				stratificationGrid.removeFromParent(); 
				Grid stratumGrid = view.getParentToChildGridMap().get(stratification.getDisplayName()); 
				stratumGrid.removeFromParent();
	
				// remove from the list
				view.getParentGridList().remove(stratificationGrid);
				view.getParentToChildGridMap().remove(stratification.getDisplayName());
				
				// remove from the model
				view.getStrataDataModel().getStratificationObjectList().remove(stratification);
				
				// disable delete button if there is only one left after delete
				if(view.getStrataDataModel().getStratificationObjectList().size() <= 1) {
					Button deleteButton = (Button) view.getParentGridList().get(0).getWidget(0, 2);
					deleteButton.setEnabled(false);
				}
			}

			@Override
			public void onDeleteStratumClick(Grid stratumGrid, StratificationsObject stratification, PopulationClauseObject stratum) { 
				int index = stratification.getPopulationClauseObjectList().indexOf(stratum);
				
				// get the view
				CQLStratificationDetailView view = searchDisplay.getCqlStratificationDetailView();
				
				// remove from the view
				stratumGrid.removeRow(index);
				
				// remove from the model	
				stratification.getPopulationClauseObjectList().remove(stratum);
			}
		});

		addWarningConfirmationHandlers();
	}

	private void addWarningConfirmationHandlers() {

		searchDisplay.getCqlLeftNavBarPanelView().getWarningConfirmationYesButton()
				.addClickHandler(event -> handleYesConfirmation());
		searchDisplay.getCqlLeftNavBarPanelView().getWarningConfirmationNoButton()
				.addClickHandler(event -> handleNoConfirmation());
	}

	private void handleYesConfirmation() {
		setPageDirty(false);
		setActiveMenuItem(currentSection, false);
		setActiveMenuItem(nextSection, true);
		searchDisplay.getCqlLeftNavBarPanelView().getWarningConfirmationMessageAlert().clearAlert();
		if (nextSection.equalsIgnoreCase(CQLWorkSpaceConstants.CQL_MEASUREOBSERVATIONS)) {
			searchDisplay.displayMeasureObservations();
		} else if (nextSection.equalsIgnoreCase(CQLWorkSpaceConstants.CQL_STRATIFICATIONS)) {
			searchDisplay.displayStratification();
		} else if(nextSection.equalsIgnoreCase(CQLWorkSpaceConstants.CQL_VIEWPOPULATIONS)){
			viewPopulationsEvent();
		} else {
			searchDisplay.displayPopulationDetailView(nextSection);
		}
		currentSection = nextSection;
	}

	private void handleNoConfirmation() {
		setActiveMenuItem(currentSection, true);
		setActiveMenuItem(nextSection, false);
		searchDisplay.getCqlLeftNavBarPanelView().getWarningConfirmationMessageAlert().clearAlert();
	}

	

	/**
	 * Before closing display.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see mat.client.MatPresenter#beforeClosingDisplay()
	 */
	@Override
	public void beforeClosingDisplay() {
		panel.clear();
		searchDisplay.setCqlPopulationDetailView(null);
		searchDisplay.setCqlMeasureObservationDetailView(null);
		searchDisplay.setCqlStratificationDetailView(null);
	}

	/**
	 * Before display.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see mat.client.MatPresenter#beforeDisplay()
	 */
	@Override
	public void beforeDisplay() {
		currentSection = CQLWorkSpaceConstants.CQL_VIEWPOPULATIONS;
		nextSection = CQLWorkSpaceConstants.CQL_VIEWPOPULATIONS;

		final String currentMeasureId = MatContext.get().getCurrentMeasureId();
		if ((currentMeasureId != null) && !"".equals(currentMeasureId)) {
			service.getMeasureXmlForMeasureAndSortedSubTreeMap(currentMeasureId,
					new AsyncCallback<SortedClauseMapResult>() { // Loading the measure's SimpleXML from the Measure_XML
																	// table
						@Override
						public void onSuccess(SortedClauseMapResult result) {
							buildPopulationWorkspace(result);
						}

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(MatContext.get().getMessageDelegate().getGenericErrorMessage());

						}
					});

			MeasureComposerPresenter.setSubSkipEmbeddedLink("CQLPopulationWorkspaceView.containerPanel");
			Mat.focusSkipLists("MeasureComposer");

		}
	}

	private void buildPopulationWorkspace(SortedClauseMapResult result) {
		try {
			String xml = result != null ? result.getMeasureXmlModel().getXml() : null;
			Document document = XMLParser.parse(xml);

			// create a Populations Data model object from the Measure XML.
			PopulationDataModel populationDataModel = new PopulationDataModel(document);

			searchDisplay.buildView(document, populationDataModel);
			addLeftNavEventHandler();
			searchDisplay.resetMessageDisplay();
			panel.add(searchDisplay.asWidget());
			viewPopulationsEvent();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adding handlers for Anchor Items.
	 */
	private void addLeftNavEventHandler() {

		searchDisplay.getCqlLeftNavBarPanelView().getInitialPopulation().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (isDirty()) {
					nextSection = CQLWorkSpaceConstants.CQL_INITIALPOPULATION;
					searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
					event.stopPropagation();
				} else {
					initialPopulationEvent();
				}

			}
		});

		searchDisplay.getCqlLeftNavBarPanelView().getNumerator().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (isDirty()) {
					nextSection = CQLWorkSpaceConstants.CQL_NUMERATOR;
					searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
					event.stopPropagation();
				} else {
					numeratorEvent();
				}
			}
		});

		searchDisplay.getCqlLeftNavBarPanelView().getNumeratorExclusions().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (isDirty()) {
					nextSection = CQLWorkSpaceConstants.CQL_NUMERATOREXCLUSIONS;
					searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
					event.stopPropagation();
				} else {
					numeratorExclusionEvent();
				}
			}
		});

		searchDisplay.getCqlLeftNavBarPanelView().getDenominator().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(isDirty()) {
					nextSection = CQLWorkSpaceConstants.CQL_DENOMINATOR;
					searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
					event.stopPropagation();
				} else {
					denominatorEvent();
				}
			}
		});

		searchDisplay.getCqlLeftNavBarPanelView().getDenominatorExclusions().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(isDirty()) {
					nextSection = CQLWorkSpaceConstants.CQL_DENOMINATOREXCLUSIONS;
					searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
					event.stopPropagation();
				} else {
					denominatorExclusionsEvent();
				}
			}
		});

		searchDisplay.getCqlLeftNavBarPanelView().getMeasurePopulations().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(isDirty()) {
					nextSection = CQLWorkSpaceConstants.CQL_MEASUREPOPULATIONS;
					searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
					event.stopPropagation();
				} else {
					measurePopulationsEvent();
				}
			}
		});

		searchDisplay.getCqlLeftNavBarPanelView().getMeasurePopulationExclusions().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(isDirty()) {
					nextSection = CQLWorkSpaceConstants.CQL_MEASUREPOPULATIONEXCLUSIONS;
					searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
					event.stopPropagation();
				} else {
					measurePopulationExclusionsEvent();
				}
			}
		});

		searchDisplay.getCqlLeftNavBarPanelView().getDenominatorExceptions().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(isDirty()) {
					nextSection = CQLWorkSpaceConstants.CQL_DENOMINATOREXCEPTIONS;
					searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
					event.stopPropagation();
				} else {
					denominatorExceptionsEvent();
				}
			}
		});

		searchDisplay.getCqlLeftNavBarPanelView().getStratifications().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(isDirty()) {
					nextSection = CQLWorkSpaceConstants.CQL_STRATIFICATIONS;
					searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
					event.stopPropagation();
				} else {
					stratificationsEvent();
				}
			}
		});

		searchDisplay.getCqlLeftNavBarPanelView().getMeasureObservations().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(isDirty()) {
					nextSection = CQLWorkSpaceConstants.CQL_MEASUREOBSERVATIONS;
					searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
					event.stopPropagation();
				} else {
					measureObservationsEvent();
					
				}
			}
		});

		searchDisplay.getCqlLeftNavBarPanelView().getViewPopulations().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(isDirty()) {
					nextSection = CQLWorkSpaceConstants.CQL_VIEWPOPULATIONS;
					searchDisplay.getCqlLeftNavBarPanelView().showUnsavedChangesWarning();
					event.stopPropagation();
				} else {
					viewPopulationsEvent();
				}
			}
		});

	}
	/**
	 * Build View for Initial Population when Initial Population AnchorList item is
	 * clicked.
	 */
	private void initialPopulationEvent() {
		setNextActiveMenuItem(currentSection,CQLWorkSpaceConstants.CQL_INITIALPOPULATION );
		searchDisplay.displayPopulationDetailView(CQLWorkSpaceConstants.CQL_INITIALPOPULATION);
		Mat.focusSkipLists("MeasureComposer");
	}

	
	/**
	 * Build View for Numerator when Numerator AnchorList item is clicked.
	 */
	private void numeratorEvent() {
		setNextActiveMenuItem(currentSection,CQLWorkSpaceConstants.CQL_NUMERATOR );
		searchDisplay.displayPopulationDetailView(CQLWorkSpaceConstants.CQL_NUMERATOR);
		Mat.focusSkipLists("MeasureComposer");
	}

	/**
	 * Build View for Numerator Exclusions when Numerator Exclusions AnchorList item
	 * is clicked.
	 */
	private void numeratorExclusionEvent() {
		setNextActiveMenuItem(currentSection,CQLWorkSpaceConstants.CQL_NUMERATOREXCLUSIONS );
		
		searchDisplay.displayPopulationDetailView(CQLWorkSpaceConstants.CQL_NUMERATOREXCLUSIONS);

		Mat.focusSkipLists("MeasureComposer");
	}

	/**
	 * Build View for Denominator when Denominator AnchorList item is clicked.
	 */
	private void denominatorEvent() {
		setNextActiveMenuItem(currentSection,CQLWorkSpaceConstants.CQL_DENOMINATOR );
		searchDisplay.displayPopulationDetailView(CQLWorkSpaceConstants.CQL_DENOMINATOR);

		Mat.focusSkipLists("MeasureComposer");
	}

	/**
	 * Build View for Denominator Exclusions when Denominator Exclusions AnchorList
	 * item is clicked.
	 */
	private void denominatorExclusionsEvent() {
		setNextActiveMenuItem(currentSection,CQLWorkSpaceConstants.CQL_DENOMINATOREXCLUSIONS );
		
		searchDisplay.displayPopulationDetailView(CQLWorkSpaceConstants.CQL_DENOMINATOREXCLUSIONS);
		Mat.focusSkipLists("MeasureComposer");
	}

	/**
	 * Build View for Denominator Exceptions when Denominator Exceptions AnchorList
	 * item is clicked.
	 */
	private void denominatorExceptionsEvent() {
		setNextActiveMenuItem(currentSection,CQLWorkSpaceConstants.CQL_DENOMINATOREXCEPTIONS );
		searchDisplay.displayPopulationDetailView(CQLWorkSpaceConstants.CQL_DENOMINATOREXCEPTIONS);

		Mat.focusSkipLists("MeasureComposer");
	}

	/**
	 * Build View for Measure Populations when Measure Populations AnchorList item
	 * is clicked.
	 */
	private void measurePopulationsEvent() {

		setNextActiveMenuItem(currentSection,CQLWorkSpaceConstants.CQL_MEASUREPOPULATIONS );
		searchDisplay.displayPopulationDetailView(CQLWorkSpaceConstants.CQL_MEASUREPOPULATIONS);

		searchDisplay.setHeadingBasedOnCurrentSection("Population Workspace > Measure Populations", "headingPanel");
		Mat.focusSkipLists("MeasureComposer");
	}

	/**
	 * Build View for Measure Populations Exclusions when Measure Populations
	 * Exclusions AnchorList item is clicked.
	 */
	private void measurePopulationExclusionsEvent() {
		setNextActiveMenuItem(currentSection,CQLWorkSpaceConstants.CQL_MEASUREPOPULATIONEXCLUSIONS );
		searchDisplay.displayPopulationDetailView(CQLWorkSpaceConstants.CQL_MEASUREPOPULATIONEXCLUSIONS);
		searchDisplay.setHeadingBasedOnCurrentSection("Population Workspace > Measure Population Exclusions",
				"headingPanel");
		Mat.focusSkipLists("MeasureComposer");
	}

	/**
	 * Build View for Stratifications when Stratifications AnchorList item is clicked.
	 */
	private void stratificationsEvent() {
		setNextActiveMenuItem(currentSection,CQLWorkSpaceConstants.CQL_STRATIFICATIONS );
		searchDisplay.displayStratification();
		searchDisplay.setHeadingBasedOnCurrentSection("Population Workspace > Stratification", "headingPanel");
		Mat.focusSkipLists("MeasureComposer");
	}

	/**
	 * Build View for Measure Observations when Measure Observations AnchorList item is
	 * clicked.
	 */
	private void measureObservationsEvent() {
		setNextActiveMenuItem(currentSection,CQLWorkSpaceConstants.CQL_MEASUREOBSERVATIONS );
		searchDisplay.displayMeasureObservations();
		searchDisplay.setHeadingBasedOnCurrentSection("Population Workspace > Measure Observations", "headingPanel");
		Mat.focusSkipLists("MeasureComposer");
	}

	/**
	 * Build View for View Populations when View Populations AnchorList item is
	 * clicked.
	 */
	private void viewPopulationsEvent() {
		setNextActiveMenuItem(currentSection,CQLWorkSpaceConstants.CQL_VIEWPOPULATIONS );
		searchDisplay.buildViewPopulations();
		searchDisplay.setHeadingBasedOnCurrentSection("Population Workspace > View Populations", "headingPanel");
		Mat.focusSkipLists("MeasureComposer");
	}

	
	private void setNextActiveMenuItem(String currSection, String nexSection) {
		setActiveMenuItem(currSection, false);
		setActiveMenuItem(nexSection, true);
		this.currentSection= nexSection;
	}
	/**
	 * Method to unset Anchor List Item selection for previous selection and set for
	 * new selections.
	 *
	 * @param menuClickedBefore
	 *            the menu clicked before
	 */
	private void setActiveMenuItem(String menuClickedBefore, boolean isSet) {
		if (!isDirty()) {
			searchDisplay.resetMessageDisplay();
			switch (menuClickedBefore) {
			case CQLWorkSpaceConstants.CQL_INITIALPOPULATION:
				searchDisplay.getCqlLeftNavBarPanelView().getInitialPopulation().setActive(isSet);
				break;
			case CQLWorkSpaceConstants.CQL_DENOMINATOR:
				searchDisplay.getCqlLeftNavBarPanelView().getDenominator().setActive(isSet);
				break;
			case CQLWorkSpaceConstants.CQL_NUMERATOR:
				searchDisplay.getCqlLeftNavBarPanelView().getNumerator().setActive(isSet);
				break;
			case CQLWorkSpaceConstants.CQL_NUMERATOREXCLUSIONS:
				searchDisplay.getCqlLeftNavBarPanelView().getNumeratorExclusions().setActive(isSet);
				break;
			case CQLWorkSpaceConstants.CQL_DENOMINATOREXCLUSIONS:
				searchDisplay.getCqlLeftNavBarPanelView().getDenominatorExclusions().setActive(isSet);
				break;
			case CQLWorkSpaceConstants.CQL_DENOMINATOREXCEPTIONS:
				searchDisplay.getCqlLeftNavBarPanelView().getDenominatorExceptions().setActive(isSet);
				break;
			case CQLWorkSpaceConstants.CQL_MEASUREPOPULATIONS:
				searchDisplay.getCqlLeftNavBarPanelView().getMeasurePopulations().setActive(isSet);
				break;
			case CQLWorkSpaceConstants.CQL_MEASUREPOPULATIONEXCLUSIONS:
				searchDisplay.getCqlLeftNavBarPanelView().getMeasurePopulationExclusions().setActive(isSet);
				break;
			case CQLWorkSpaceConstants.CQL_STRATIFICATIONS:
				searchDisplay.getCqlLeftNavBarPanelView().getStratifications().setActive(isSet);
				break;
			case CQLWorkSpaceConstants.CQL_MEASUREOBSERVATIONS:
				searchDisplay.getCqlLeftNavBarPanelView().getMeasureObservations().setActive(isSet);
				break;
			case CQLWorkSpaceConstants.CQL_VIEWPOPULATIONS:
				searchDisplay.getCqlLeftNavBarPanelView().getViewPopulations().setActive(isSet);
				break;
			}
		}
	}

	/**
	 * Gets the widget.
	 *
	 * @return the widget
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see mat.client.MatPresenter#getWidget()
	 */
	@Override
	public Widget getWidget() {
		panel.setStyleName("contentPanel");
		return panel;
	}

	/**
	 * returns the searchDisplay.
	 * 
	 * @return ViewDisplay.
	 */
	public ViewDisplay getSearchDisplay() {
		return searchDisplay;
	}

	/**
	 * Show human readable dialog box.
	 *
	 * @param result
	 *            the result
	 * @param populationName
	 *            the population name
	 */
	public static native void showHumanReadableDialogBox(String result, String populationName) /*-{
		var dummyURL = window.location.protocol + "//"
				+ window.location.hostname + ":" + window.location.port + "/"
				+ "mat/humanreadable.html";
		var humanReadableWindow = window.open(dummyURL, "",
				"width=1200,height=700,scrollbars=yes,resizable=yes");

		if (humanReadableWindow && humanReadableWindow.top) {
			humanReadableWindow.document.write(result);
			humanReadableWindow.document.title = populationName;
		}
	}-*/;

	/**
	 * Show searching busy.
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
		if (MatContext.get().getMeasureLockService().checkForEditPermission()) {

		}
		searchDisplay.getCqlLeftNavBarPanelView().setIsLoading(busy);

	}

	/**
	 * This method identifies current section and finds if that section has unsaved changes or not.
	 * @return boolean
	 */
	public boolean isDirty() {
		boolean isViewDirty = false;
		if (currentSection.equalsIgnoreCase(CQLWorkSpaceConstants.CQL_MEASUREOBSERVATIONS)) {
			isViewDirty = searchDisplay.getCqlMeasureObservationDetailView().isDirty();
		} else if (currentSection.equalsIgnoreCase(CQLWorkSpaceConstants.CQL_STRATIFICATIONS)) {
			isViewDirty = searchDisplay.getCqlStratificationDetailView().isDirty();
		} else if (currentSection.equalsIgnoreCase(CQLWorkSpaceConstants.CQL_VIEWPOPULATIONS)) {
			isViewDirty = false;
		} else {
			isViewDirty = searchDisplay.getCqlPopulationDetailView().isDirty();
		}

		return isViewDirty;
	}

	/**
	 * This method set the page dirty if there are unsaved changes.
	 * @param isPageDirty
	 */
	private void setPageDirty(boolean isPageDirty) {
		if (currentSection.equalsIgnoreCase(CQLWorkSpaceConstants.CQL_MEASUREOBSERVATIONS)) {
			searchDisplay.getCqlMeasureObservationDetailView().setIsDirty(isPageDirty);
		} else if (currentSection.equalsIgnoreCase(CQLWorkSpaceConstants.CQL_STRATIFICATIONS)) {
			searchDisplay.getCqlStratificationDetailView().setIsDirty(isPageDirty);
		} else if (!currentSection.equalsIgnoreCase(CQLWorkSpaceConstants.CQL_VIEWPOPULATIONS)) {
			searchDisplay.getCqlPopulationDetailView().setIsDirty(isPageDirty);
		}

	}
}
