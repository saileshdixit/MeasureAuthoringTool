package mat.client.measure.measuredetails.navigation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.NavPills;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.constants.IconType;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import mat.client.measure.measuredetails.MeasureDetailsObserver;
import mat.client.shared.MatDetailItem;
import mat.client.shared.MeasureDetailsConstants.MeasureDetailsItems;
import mat.client.shared.MeasureDetailsConstants.PopulationItems;

public class MeasureDetailsNavigation {
	private FlowPanel mainPanel = new FlowPanel();
	private Map<MatDetailItem, AnchorListItem> menuItemMap;
	private MeasureDetailsObserver observer;
	private PanelCollapse populationsCollapse;
	private String scoringType;
	private boolean isComposite;
	//TODO add ids to dom objects
	public MeasureDetailsNavigation(String scoringType, boolean isCompositeMeasure) {
		buildNavigationMenu(scoringType, isComposite);
		mainPanel.setWidth("250px");
	}

	public void buildNavigationMenu(String scoringType, boolean isCompositeMeasure) {
		mainPanel.clear();
		this.isComposite = isCompositeMeasure;
		this.scoringType = scoringType;
		menuItemMap = new HashMap<>();
		populationsCollapse = buildPopulationCollapse();
		NavPills navPills = buildNavPills();
		mainPanel.add(navPills);
	}
	
	private PanelCollapse buildPopulationCollapse() {
		populationsCollapse = new PanelCollapse();
		VerticalPanel nestedNavPanel = new VerticalPanel(); 
		nestedNavPanel.setWidth("250px");
		NavPills nestedNavPills = new NavPills();
		nestedNavPills.setStacked(true);
		buildPopulationNavPills(nestedNavPills);
		nestedNavPanel.add(nestedNavPills);
		populationsCollapse.add(nestedNavPanel);
		return populationsCollapse;
	}
	
	public void buildPopulationNavPills(NavPills navPills) {
		List<PopulationItems> populationsList = Arrays.asList(PopulationItems.values());
		for(PopulationItems populationDetail: populationsList) {
			List<String> applicableScoringTypes = populationDetail.getApplicableMeasureTypes();
			if(applicableScoringTypes.contains(scoringType)) {
				AnchorListItem anchorListItem = new AnchorListItem(populationDetail.abbreviatedName());
				menuItemMap.put(populationDetail, anchorListItem);
				navPills.add(anchorListItem);
				anchorListItem.addClickHandler(event -> anchorListItemClicked(populationDetail));
			}
		}
	}

	public Widget getWidget() {
		return mainPanel;
	}

	private NavPills buildNavPills() {
		NavPills navPills = new NavPills();
		List<MeasureDetailsItems> detailsList = Arrays.asList(MeasureDetailsItems.values());
		for(MeasureDetailsItems measureDetail: detailsList) {
			AnchorListItem anchorListItem = new AnchorListItem(measureDetail.abbreviatedName());
			if(measureDetail == MeasureDetailsItems.POPULATIONS) {
				anchorListItem.setIcon(IconType.PLUS);
				anchorListItem.add(populationsCollapse);
				anchorListItem.addClickHandler(event -> populationItemClicked(anchorListItem));
				navPills.add(anchorListItem);
			} else {
				if(measureDetail == MeasureDetailsItems.COMPONENT_MEASURES) {
					if(!isComposite) {
						continue;
					}
				}
				menuItemMap.put(measureDetail, anchorListItem);
				navPills.add(anchorListItem);
				anchorListItem.addClickHandler(event -> anchorListItemClicked(measureDetail));
			}
		}
		navPills.setStacked(true);
		return navPills;
	}
	
	private void populationItemClicked(AnchorListItem anchorListItem) {
		deselectAllMenuItems();
		if (populationsCollapse.getElement().getClassName().equalsIgnoreCase("panel-collapse collapse in")) {
			populationsCollapse.getElement().setClassName("panel-collapse collapse");
			anchorListItem.setIcon(IconType.PLUS);
		} else {
			populationsCollapse.getElement().setClassName("panel-collapse collapse in");
			anchorListItem.setIcon(IconType.MINUS);
		}
	}

	private void anchorListItemClicked(MatDetailItem menuItem) {
		observer.onMenuItemClicked(menuItem);
		setActiveMenuItem(menuItem);
	}

	public void setActiveMenuItem(MatDetailItem activeMenuItem) {
		deselectAllMenuItems();
		menuItemMap.get(activeMenuItem).setActive(true);
	}

	private void deselectAllMenuItems() {
		Iterator<AnchorListItem>menuItems = menuItemMap.values().iterator();
		while(menuItems.hasNext()) {
			AnchorListItem anchorListItem = menuItems.next();
			anchorListItem.setActive(false);
		}
	}
	
	public void setObserver(MeasureDetailsObserver observer) {
		this.observer = observer;
	}
}
