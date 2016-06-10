package org.herac.tuxguitar.app.view.toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.action.duration.TGChangeDottedDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGChangeDoubleDottedDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetDivisionTypeDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetEighthDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetHalfDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetQuarterDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetSixteenthDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetSixtyFourthDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetThirtySecondDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetWholeDurationAction;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.menu.UIMenuCheckableItem;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.toolbar.UIToolActionMenuItem;
import org.herac.tuxguitar.ui.toolbar.UIToolCheckableItem;
import org.herac.tuxguitar.ui.toolbar.UIToolMenuItem;

public class TGToolBarSectionDuration implements TGToolBarSection {
	
	private static final String DURATION_VALUE = "duration";
	
	private UIToolCheckableItem dotted;
	private UIToolCheckableItem doubleDotted;
	private UIToolMenuItem durationItem;
	private UIToolActionMenuItem divisionTypeItem;
	
	private List<UIMenuActionItem> durationMenuItems;
	private List<UIMenuCheckableItem> divisionTypeMenuItems;
	
	private Map<Integer, String> durationNameKeys;
	private Map<Integer, String> durationActions;
	
	private Integer durationValue;
	
	public TGToolBarSectionDuration() {
		this.createDurationNames();
		this.createDurationActions();
	}
	
	public void createSection(final TGToolBar toolBar) {
		this.durationItem = toolBar.getControl().createMenuItem();
		
		this.durationMenuItems = new ArrayList<UIMenuActionItem>();
		this.durationMenuItems.add(this.createDurationMenuItem(toolBar, TGDuration.WHOLE));
		this.durationMenuItems.add(this.createDurationMenuItem(toolBar, TGDuration.HALF));
		this.durationMenuItems.add(this.createDurationMenuItem(toolBar, TGDuration.QUARTER));
		this.durationMenuItems.add(this.createDurationMenuItem(toolBar, TGDuration.EIGHTH));
		this.durationMenuItems.add(this.createDurationMenuItem(toolBar, TGDuration.SIXTEENTH));
		this.durationMenuItems.add(this.createDurationMenuItem(toolBar, TGDuration.THIRTY_SECOND));
		this.durationMenuItems.add(this.createDurationMenuItem(toolBar, TGDuration.SIXTY_FOURTH));
		
		this.dotted = toolBar.getControl().createCheckItem();
		this.dotted.addSelectionListener(toolBar.createActionProcessor(TGChangeDottedDurationAction.NAME));
		
		this.doubleDotted = toolBar.getControl().createCheckItem();
		this.doubleDotted.addSelectionListener(toolBar.createActionProcessor(TGChangeDoubleDottedDurationAction.NAME));
		
		this.divisionTypeItem = toolBar.getControl().createActionMenuItem();
		this.divisionTypeItem.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				toggleDivisionType(toolBar);
			}
		});
		
		this.divisionTypeMenuItems = new ArrayList<UIMenuCheckableItem>();
		for( int i = 0 ; i < TGDivisionType.ALTERED_DIVISION_TYPES.length ; i ++ ){
			this.divisionTypeMenuItems.add(this.createDivisionTypeMenuItem(toolBar, TGDivisionType.ALTERED_DIVISION_TYPES[i]));
		}
		
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
	}
	
	public void loadProperties(TGToolBar toolBar){
		TGDuration duration = toolBar.getTablature().getCaret().getDuration();
		
		this.durationItem.setToolTipText(toolBar.getText("duration"));
		this.dotted.setToolTipText(toolBar.getText("duration.dotted"));
		this.doubleDotted.setToolTipText(toolBar.getText("duration.doubledotted"));
		this.divisionTypeItem.setToolTipText(toolBar.getText("duration.division-type"));
		this.loadDurationMenuProperties(toolBar, duration.getValue());
		this.loadDivisionTypeMenuProperties(toolBar);
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.dotted.setImage(toolBar.getIconManager().getDurationDotted());
		this.doubleDotted.setImage(toolBar.getIconManager().getDurationDoubleDotted());
		this.divisionTypeItem.setImage(toolBar.getIconManager().getDivisionType());
		this.loadDurationIcon(toolBar, true);
		this.loadDurationMenuIcons(toolBar);
	}
	
	public void loadDurationIcon(TGToolBar toolBar, boolean force) {
		int durationValue = TGDuration.QUARTER;
		
		Tablature tablature = toolBar.getTablature();
		if( tablature != null ) {
			durationValue = tablature.getCaret().getDuration().getValue();
		}
		
		if( force || (this.durationValue == null || !this.durationValue.equals(durationValue))) {
			UIImage icon = this.findDurationIcon(toolBar, durationValue);
			if( icon != null ) { 
				this.durationItem.setImage(icon);
				this.durationValue = durationValue;
			}
		}
	}
	
	public void updateItems(TGToolBar toolBar) {
		TGDuration duration = toolBar.getTablature().getCaret().getDuration();
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		
		this.loadDurationIcon(toolBar, false);
		this.durationItem.setEnabled( !running );
		this.dotted.setChecked(duration.isDotted());
		this.dotted.setEnabled(!running);
		this.doubleDotted.setChecked(duration.isDoubleDotted());
		this.doubleDotted.setEnabled(!running);
		this.divisionTypeItem.setEnabled(!running);
		this.updateDurationMenuItems(toolBar, duration.getValue(), running);
		this.updateDivisionTypeMenuItems(toolBar, duration.getDivision(), running);
	}
	
	private UIMenuActionItem createDurationMenuItem(TGToolBar toolBar, int value) {
		UIMenuActionItem menuItem = this.durationItem.getMenu().createActionItem();
		menuItem.setData(DURATION_VALUE, value);
		
		String action = this.findDurationAction(value);
		if( action != null ) {
			menuItem.addSelectionListener(toolBar.createActionProcessor(action));
		}
		return menuItem;
	}
	
	private void updateDurationMenuItems(TGToolBar toolBar, int selection, boolean running) {
		for(UIMenuActionItem uiMenuItem : this.durationMenuItems) {
			Integer value = uiMenuItem.getData(DURATION_VALUE);
			String nameKey = this.findDurationNameKey(value);
			if( nameKey != null ) {
				uiMenuItem.setEnabled(!running);
				uiMenuItem.setText(toolBar.getText(nameKey, (value == selection)));
			}
		}
	}
	
	private void loadDurationMenuIcons(TGToolBar toolBar) {
		for(UIMenuActionItem uiMenuItem : this.durationMenuItems) {
			Integer value = uiMenuItem.getData(DURATION_VALUE);
			UIImage icon = this.findDurationIcon(toolBar, value);
			if( icon != null ) {
				uiMenuItem.setImage(icon);
			}
		}
	}
	
	private void loadDurationMenuProperties(TGToolBar toolBar, int selection) {
		for(UIMenuActionItem uiMenuItem : this.durationMenuItems) {
			Integer value = (Integer) uiMenuItem.getData(DURATION_VALUE);
			String nameKey = this.findDurationNameKey(value);
			if( nameKey != null ) {
				uiMenuItem.setText(toolBar.getText(nameKey, (value == selection)));
			}
		}
	}
	
	private void createDurationNames() {
		this.durationNameKeys = new HashMap<Integer, String>();
		this.durationNameKeys.put(TGDuration.WHOLE, "duration.whole");
		this.durationNameKeys.put(TGDuration.HALF, "duration.half");
		this.durationNameKeys.put(TGDuration.QUARTER, "duration.quarter");
		this.durationNameKeys.put(TGDuration.EIGHTH, "duration.eighth");
		this.durationNameKeys.put(TGDuration.SIXTEENTH, "duration.sixteenth");
		this.durationNameKeys.put(TGDuration.THIRTY_SECOND, "duration.thirtysecond");
		this.durationNameKeys.put(TGDuration.SIXTY_FOURTH, "duration.sixtyfourth");
	}
	
	private void createDurationActions() {
		this.durationActions = new HashMap<Integer, String>();
		this.durationActions.put(TGDuration.WHOLE, TGSetWholeDurationAction.NAME);
		this.durationActions.put(TGDuration.HALF, TGSetHalfDurationAction.NAME);
		this.durationActions.put(TGDuration.QUARTER, TGSetQuarterDurationAction.NAME);
		this.durationActions.put(TGDuration.EIGHTH, TGSetEighthDurationAction.NAME);
		this.durationActions.put(TGDuration.SIXTEENTH, TGSetSixteenthDurationAction.NAME);
		this.durationActions.put(TGDuration.THIRTY_SECOND, TGSetThirtySecondDurationAction.NAME);
		this.durationActions.put(TGDuration.SIXTY_FOURTH, TGSetSixtyFourthDurationAction.NAME);
	}
	
	private String findDurationNameKey(int value) {
		if( this.durationNameKeys.containsKey(value) ) {
			return this.durationNameKeys.get(value);
		}
		return null;
	}
	
	private String findDurationAction(int value) {
		if( this.durationActions.containsKey(value) ) {
			return this.durationActions.get(value);
		}
		return null;
	}
	
	private UIImage findDurationIcon(TGToolBar toolBar, int value) {
		return toolBar.getIconManager().getDuration(value);
	}
	
	private void toggleDivisionType(TGToolBar toolBar) {
		TGDuration duration = TablatureEditor.getInstance(toolBar.getContext()).getTablature().getCaret().getDuration();
		TGDivisionType divisionType = null;
		if( duration.getDivision().isEqual(TGDivisionType.NORMAL)){
			divisionType = this.createDivisionType(toolBar, TGDivisionType.TRIPLET);
		}else{
			divisionType = this.createDivisionType(toolBar, TGDivisionType.NORMAL);
		}
		
		this.createDivisionTypeAction(toolBar, divisionType).process();
	}
	
	private UIMenuCheckableItem createDivisionTypeMenuItem(TGToolBar toolBar, TGDivisionType divisionType) {
		UIMenuCheckableItem uiMenuItem = this.divisionTypeItem.getMenu().createCheckItem();
		uiMenuItem.setData(TGDivisionType.class.getName(), divisionType);
		uiMenuItem.addSelectionListener(this.createDivisionTypeAction(toolBar, divisionType));
		
		return uiMenuItem;
	}
	
	private void updateDivisionTypeMenuItems(TGToolBar toolBar, TGDivisionType selection, boolean running) {
		for(UIMenuCheckableItem uiMenuItem : this.divisionTypeMenuItems) {
			TGDivisionType divisionType = uiMenuItem.getData(TGDivisionType.class.getName());
			uiMenuItem.setChecked(divisionType.isEqual(selection));
		}
	}
	
	private void loadDivisionTypeMenuProperties(TGToolBar toolBar) {
		for(UIMenuCheckableItem uiMenuItem : this.divisionTypeMenuItems) {
			TGDivisionType divisionType = uiMenuItem.getData(TGDivisionType.class.getName());
			uiMenuItem.setText(Integer.toString(divisionType.getEnters()));
		}
	}
	
	private TGDivisionType createDivisionType(TGToolBar toolBar, TGDivisionType tgDivisionTypeSrc) {
		TGFactory tgFactory = TGDocumentManager.getInstance(toolBar.getContext()).getSongManager().getFactory();
		TGDivisionType tgDivisionTypeDst = tgFactory.newDivisionType();
		tgDivisionTypeDst.copyFrom(tgDivisionTypeSrc);
		return tgDivisionTypeDst;
	}
	
	private TGActionProcessorListener createDivisionTypeAction(TGToolBar toolBar, TGDivisionType tgDivisionType){
		TGActionProcessorListener tgActionProcessor = toolBar.createActionProcessor(TGSetDivisionTypeDurationAction.NAME);
		tgActionProcessor.setAttribute(TGSetDivisionTypeDurationAction.PROPERTY_DIVISION_TYPE, createDivisionType(toolBar, tgDivisionType));
		return tgActionProcessor;
	}
}
