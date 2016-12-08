package org.herac.tuxguitar.app.view.toolbar.main;

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

public class TGMainToolBarSectionDuration extends TGMainToolBarSection {
	
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
	
	public TGMainToolBarSectionDuration(TGMainToolBar toolBar) {
		super(toolBar);
		
		this.createDurationNames();
		this.createDurationActions();
	}
	
	public void createSection() {
		this.durationItem = this.getToolBar().getControl().createMenuItem();
		
		this.durationMenuItems = new ArrayList<UIMenuActionItem>();
		this.durationMenuItems.add(this.createDurationMenuItem(TGDuration.WHOLE));
		this.durationMenuItems.add(this.createDurationMenuItem(TGDuration.HALF));
		this.durationMenuItems.add(this.createDurationMenuItem(TGDuration.QUARTER));
		this.durationMenuItems.add(this.createDurationMenuItem(TGDuration.EIGHTH));
		this.durationMenuItems.add(this.createDurationMenuItem(TGDuration.SIXTEENTH));
		this.durationMenuItems.add(this.createDurationMenuItem(TGDuration.THIRTY_SECOND));
		this.durationMenuItems.add(this.createDurationMenuItem(TGDuration.SIXTY_FOURTH));
		
		this.dotted = this.getToolBar().getControl().createCheckItem();
		this.dotted.addSelectionListener(this.createActionProcessor(TGChangeDottedDurationAction.NAME));
		
		this.doubleDotted = this.getToolBar().getControl().createCheckItem();
		this.doubleDotted.addSelectionListener(this.createActionProcessor(TGChangeDoubleDottedDurationAction.NAME));
		
		this.divisionTypeItem = this.getToolBar().getControl().createActionMenuItem();
		this.divisionTypeItem.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				toggleDivisionType();
			}
		});
		
		this.divisionTypeMenuItems = new ArrayList<UIMenuCheckableItem>();
		for( int i = 0 ; i < TGDivisionType.ALTERED_DIVISION_TYPES.length ; i ++ ){
			this.divisionTypeMenuItems.add(this.createDivisionTypeMenuItem(TGDivisionType.ALTERED_DIVISION_TYPES[i]));
		}
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties(){
		TGDuration duration = this.getTablature().getCaret().getDuration();
		
		this.durationItem.setToolTipText(this.getText("duration"));
		this.dotted.setToolTipText(this.getText("duration.dotted"));
		this.doubleDotted.setToolTipText(this.getText("duration.doubledotted"));
		this.divisionTypeItem.setToolTipText(this.getText("duration.division-type"));
		this.loadDurationMenuProperties(duration.getValue());
		this.loadDivisionTypeMenuProperties();
	}
	
	public void loadIcons(){
		this.dotted.setImage(this.getIconManager().getDurationDotted());
		this.doubleDotted.setImage(this.getIconManager().getDurationDoubleDotted());
		this.divisionTypeItem.setImage(this.getIconManager().getDivisionType());
		this.loadDurationIcon(true);
		this.loadDurationMenuIcons();
	}
	
	public void loadDurationIcon(boolean force) {
		int durationValue = TGDuration.QUARTER;
		
		Tablature tablature = this.getTablature();
		if( tablature != null ) {
			durationValue = tablature.getCaret().getDuration().getValue();
		}
		
		if( force || (this.durationValue == null || !this.durationValue.equals(durationValue))) {
			UIImage icon = this.findDurationIcon(durationValue);
			if( icon != null ) { 
				this.durationItem.setImage(icon);
				this.durationValue = durationValue;
			}
		}
	}
	
	public void updateItems() {
		TGDuration duration = this.getTablature().getCaret().getDuration();
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		
		this.loadDurationIcon(false);
		this.durationItem.setEnabled( !running );
		this.dotted.setChecked(duration.isDotted());
		this.dotted.setEnabled(!running);
		this.doubleDotted.setChecked(duration.isDoubleDotted());
		this.doubleDotted.setEnabled(!running);
		this.divisionTypeItem.setEnabled(!running);
		this.updateDurationMenuItems(duration.getValue(), running);
		this.updateDivisionTypeMenuItems(duration.getDivision(), running);
	}
	
	private UIMenuActionItem createDurationMenuItem(int value) {
		UIMenuActionItem menuItem = this.durationItem.getMenu().createActionItem();
		menuItem.setData(DURATION_VALUE, value);
		
		String action = this.findDurationAction(value);
		if( action != null ) {
			menuItem.addSelectionListener(this.createActionProcessor(action));
		}
		return menuItem;
	}
	
	private void updateDurationMenuItems(int selection, boolean running) {
		for(UIMenuActionItem uiMenuItem : this.durationMenuItems) {
			Integer value = uiMenuItem.getData(DURATION_VALUE);
			String nameKey = this.findDurationNameKey(value);
			if( nameKey != null ) {
				uiMenuItem.setEnabled(!running);
				uiMenuItem.setText(this.getText(nameKey, (value == selection)));
			}
		}
	}
	
	private void loadDurationMenuIcons() {
		for(UIMenuActionItem uiMenuItem : this.durationMenuItems) {
			Integer value = uiMenuItem.getData(DURATION_VALUE);
			UIImage icon = this.findDurationIcon(value);
			if( icon != null ) {
				uiMenuItem.setImage(icon);
			}
		}
	}
	
	private void loadDurationMenuProperties(int selection) {
		for(UIMenuActionItem uiMenuItem : this.durationMenuItems) {
			Integer value = (Integer) uiMenuItem.getData(DURATION_VALUE);
			String nameKey = this.findDurationNameKey(value);
			if( nameKey != null ) {
				uiMenuItem.setText(this.getText(nameKey, (value == selection)));
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
	
	private UIImage findDurationIcon(int value) {
		return this.getIconManager().getDuration(value);
	}
	
	private void toggleDivisionType() {
		TGDuration duration = TablatureEditor.getInstance(this.getToolBar().getContext()).getTablature().getCaret().getDuration();
		TGDivisionType divisionType = null;
		if( duration.getDivision().isEqual(TGDivisionType.NORMAL)){
			divisionType = this.createDivisionType(TGDivisionType.TRIPLET);
		}else{
			divisionType = this.createDivisionType(TGDivisionType.NORMAL);
		}
		
		this.createDivisionTypeAction(divisionType).process();
	}
	
	private UIMenuCheckableItem createDivisionTypeMenuItem(TGDivisionType divisionType) {
		UIMenuCheckableItem uiMenuItem = this.divisionTypeItem.getMenu().createCheckItem();
		uiMenuItem.setData(TGDivisionType.class.getName(), divisionType);
		uiMenuItem.addSelectionListener(this.createDivisionTypeAction(divisionType));
		
		return uiMenuItem;
	}
	
	private void updateDivisionTypeMenuItems(TGDivisionType selection, boolean running) {
		for(UIMenuCheckableItem uiMenuItem : this.divisionTypeMenuItems) {
			TGDivisionType divisionType = uiMenuItem.getData(TGDivisionType.class.getName());
			uiMenuItem.setChecked(divisionType.isEqual(selection));
		}
	}
	
	private void loadDivisionTypeMenuProperties() {
		for(UIMenuCheckableItem uiMenuItem : this.divisionTypeMenuItems) {
			TGDivisionType divisionType = uiMenuItem.getData(TGDivisionType.class.getName());
			uiMenuItem.setText(Integer.toString(divisionType.getEnters()));
		}
	}
	
	private TGDivisionType createDivisionType(TGDivisionType tgDivisionTypeSrc) {
		TGFactory tgFactory = TGDocumentManager.getInstance(this.getToolBar().getContext()).getSongManager().getFactory();
		TGDivisionType tgDivisionTypeDst = tgFactory.newDivisionType();
		tgDivisionTypeDst.copyFrom(tgDivisionTypeSrc);
		return tgDivisionTypeDst;
	}
	
	private TGActionProcessorListener createDivisionTypeAction(TGDivisionType tgDivisionType){
		TGActionProcessorListener tgActionProcessor = this.createActionProcessor(TGSetDivisionTypeDurationAction.NAME);
		tgActionProcessor.setAttribute(TGSetDivisionTypeDurationAction.PROPERTY_DIVISION_TYPE, createDivisionType(tgDivisionType));
		return tgActionProcessor;
	}
}
