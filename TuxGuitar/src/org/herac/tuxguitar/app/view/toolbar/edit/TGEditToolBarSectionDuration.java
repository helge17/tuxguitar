package org.herac.tuxguitar.app.view.toolbar.edit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
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
import org.herac.tuxguitar.editor.action.note.TGChangeTiedNoteAction;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.menu.UIMenuCheckableItem;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.toolbar.UIToolActionItem;
import org.herac.tuxguitar.ui.toolbar.UIToolActionMenuItem;
import org.herac.tuxguitar.ui.toolbar.UIToolBar;
import org.herac.tuxguitar.ui.toolbar.UIToolCheckableItem;

public class TGEditToolBarSectionDuration extends TGEditToolBarSection {
	
	private static final String SECTION_TITLE = "duration";
	
	private static final String DURATION_VALUE = "duration";
	
	private UIToolCheckableItem dotted;
	private UIToolCheckableItem doubleDotted;
	private UIToolActionMenuItem divisionTypeItem;
	private UIToolCheckableItem tiedNote;
	
	private List<UIToolCheckableItem> durationToolItems;
	private List<UIMenuCheckableItem> divisionTypeMenuItems;
	
	private Map<Integer, String> durationNameKeys;
	private Map<Integer, String> durationActions;
	
	public TGEditToolBarSectionDuration(TGEditToolBar toolBar) {
		super(toolBar, SECTION_TITLE);
		
		this.createDurationNames();
		this.createDurationActions();
	}
	
	public void createSectionToolBars() {
		UIToolBar toolBar1 = this.createToolBar();
		UIToolBar toolBar2 = this.createToolBar();
		
		this.durationToolItems = new ArrayList<UIToolCheckableItem>();
		this.durationToolItems.add(this.createDurationToolItem(toolBar1, TGDuration.WHOLE));
		this.durationToolItems.add(this.createDurationToolItem(toolBar1, TGDuration.HALF));
		this.durationToolItems.add(this.createDurationToolItem(toolBar1, TGDuration.QUARTER));
		this.durationToolItems.add(this.createDurationToolItem(toolBar1, TGDuration.EIGHTH));
		this.durationToolItems.add(this.createDurationToolItem(toolBar1, TGDuration.SIXTEENTH));
		this.durationToolItems.add(this.createDurationToolItem(toolBar1, TGDuration.THIRTY_SECOND));
		this.durationToolItems.add(this.createDurationToolItem(toolBar2, TGDuration.SIXTY_FOURTH));
		
		this.dotted = toolBar2.createCheckItem();
		this.dotted.addSelectionListener(this.createActionProcessor(TGChangeDottedDurationAction.NAME));
		
		this.doubleDotted = toolBar2.createCheckItem();
		this.doubleDotted.addSelectionListener(this.createActionProcessor(TGChangeDoubleDottedDurationAction.NAME));
		
		this.divisionTypeItem = toolBar2.createActionMenuItem();
		this.divisionTypeItem.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				toggleDivisionType();
			}
		});
		
		this.divisionTypeMenuItems = new ArrayList<UIMenuCheckableItem>();
		for( int i = 0 ; i < TGDivisionType.ALTERED_DIVISION_TYPES.length ; i ++ ){
			this.divisionTypeMenuItems.add(this.createDivisionTypeMenuItem(TGDivisionType.ALTERED_DIVISION_TYPES[i]));
		}
		
		this.tiedNote = toolBar2.createCheckItem();
		this.tiedNote.addSelectionListener(this.createActionProcessor(TGChangeTiedNoteAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void updateItems(){
		TGNote note = this.getTablature().getCaret().getSelectedNote();
		TGDuration duration = this.getTablature().getCaret().getDuration();
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		
		this.dotted.setChecked(duration.isDotted());
		this.dotted.setEnabled(!running);
		this.doubleDotted.setChecked(duration.isDoubleDotted());
		this.doubleDotted.setEnabled(!running);
		this.divisionTypeItem.setEnabled(!running);
		this.tiedNote.setEnabled(!running);
		this.tiedNote.setChecked(note != null && note.isTiedNote());
		this.updateDurationToolItems(duration.getValue(), running);
		this.updateDivisionTypeMenuItems(duration.getDivision(), running);
	}
	
	public void loadProperties(){
		TGDuration duration = this.getTablature().getCaret().getDuration();
		
		this.dotted.setToolTipText(this.getText("duration.dotted"));
		this.doubleDotted.setToolTipText(this.getText("duration.doubledotted"));
		this.divisionTypeItem.setToolTipText(this.getText("duration.division-type"));
		this.tiedNote.setToolTipText(this.getText("note.tiednote"));
		this.loadDurationToolProperties(duration.getValue());
		this.loadDivisionTypeMenuProperties();
	}
	
	public void loadIcons(){
		this.dotted.setImage(this.getIconManager().getDurationDotted());
		this.doubleDotted.setImage(this.getIconManager().getDurationDoubleDotted());
		this.divisionTypeItem.setImage(this.getIconManager().getDivisionType());
		this.tiedNote.setImage(this.getIconManager().getNoteTied());
		this.loadDurationToolIcons();
	}
	
	private UIToolCheckableItem createDurationToolItem(UIToolBar toolBar, int value) {
		UIToolCheckableItem toolItem = toolBar.createCheckItem();
		toolItem.setData(DURATION_VALUE, value);
		
		String action = this.findDurationAction(value);
		if( action != null ) {
			toolItem.addSelectionListener(this.createActionProcessor(action));
		}
		return toolItem;
	}
	
	private void updateDurationToolItems(int selection, boolean running) {
		for(UIToolCheckableItem uiToolItem : this.durationToolItems) {
			Integer value = uiToolItem.getData(DURATION_VALUE);
			String nameKey = this.findDurationNameKey(value);
			if( nameKey != null ) {
				uiToolItem.setEnabled(!running);
				uiToolItem.setChecked(value == selection);
			}
		}
	}
	
	private void loadDurationToolIcons() {
		for(UIToolActionItem uiToolItem : this.durationToolItems) {
			Integer value = uiToolItem.getData(DURATION_VALUE);
			UIImage icon = this.findDurationIcon(value);
			if( icon != null ) {
				uiToolItem.setImage(icon);
			}
		}
	}
	
	private void loadDurationToolProperties(int selection) {
		for(UIToolActionItem uiToolItem : this.durationToolItems) {
			Integer value = (Integer) uiToolItem.getData(DURATION_VALUE);
			String nameKey = this.findDurationNameKey(value);
			if( nameKey != null ) {
				uiToolItem.setToolTipText(this.getText(nameKey));
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
