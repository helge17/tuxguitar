package org.herac.tuxguitar.app.view.toolbar;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
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

public class TGToolBarSectionDuration implements TGToolBarSection {
	
	private ToolItem dotted;
	private ToolItem doubleDotted;
	private ToolItem durationItem;
	private ToolItem divisionTypeItem;
	
	private Map<Integer, String> durationNameKeys;
	private Map<Integer, String> durationActions;
	
	private Integer durationValue;
	
	public TGToolBarSectionDuration() {
		this.createDurationNames();
		this.createDurationActions();
	}
	
	public void createSection(final TGToolBar toolBar) {
		this.durationItem = new ToolItem(toolBar.getControl(), SWT.PUSH);
		this.durationItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				createDurationMenu(toolBar, (ToolItem) event.widget);
			}
		});
		
		this.dotted = new ToolItem(toolBar.getControl(), SWT.CHECK);
		this.dotted.addSelectionListener(toolBar.createActionProcessor(TGChangeDottedDurationAction.NAME));
		
		this.doubleDotted = new ToolItem(toolBar.getControl(), SWT.CHECK);
		this.doubleDotted.addSelectionListener(toolBar.createActionProcessor(TGChangeDoubleDottedDurationAction.NAME));
		
		this.divisionTypeItem = new ToolItem(toolBar.getControl(), SWT.DROP_DOWN);
		this.divisionTypeItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				processDivisionTypeSelection(toolBar, event);
			}
		});
		
		this.loadIcons(toolBar);
		this.loadProperties(toolBar);
	}
	
	public void loadProperties(TGToolBar toolBar){
		this.durationItem.setToolTipText(toolBar.getText("duration"));
		this.dotted.setToolTipText(toolBar.getText("duration.dotted"));
		this.doubleDotted.setToolTipText(toolBar.getText("duration.doubledotted"));
		this.divisionTypeItem.setToolTipText(toolBar.getText("duration.division-type"));
	}
	
	public void loadIcons(TGToolBar toolBar){
		this.dotted.setImage(toolBar.getIconManager().getDurationDotted());
		this.doubleDotted.setImage(toolBar.getIconManager().getDurationDoubleDotted());
		this.divisionTypeItem.setImage(toolBar.getIconManager().getDivisionType());
		this.loadDurationIcon(toolBar, true);
	}
	
	public void loadDurationIcon(TGToolBar toolBar, boolean force) {
		int durationValue = TGDuration.QUARTER;
		
		Tablature tablature = toolBar.getTablature();
		if( tablature != null ) {
			durationValue = tablature.getCaret().getDuration().getValue();
		}
		
		if( force || (this.durationValue == null || !this.durationValue.equals(durationValue))) {
			Image icon = this.findDurationIcon(toolBar, durationValue);
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
		this.dotted.setSelection(duration.isDotted());
		this.dotted.setEnabled(!running);
		this.doubleDotted.setSelection(duration.isDoubleDotted());
		this.doubleDotted.setEnabled(!running);
		this.divisionTypeItem.setEnabled(!running);
	}
	
	public void createDurationMenu(TGToolBar toolBar, ToolItem item) {
		TGDuration duration = TablatureEditor.getInstance(toolBar.getContext()).getTablature().getCaret().getDuration();
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		
		Menu menu = new Menu(item.getParent().getShell());
		
		this.createDurationMenuItem(toolBar, menu, TGDuration.WHOLE, duration.getValue(), running);
		this.createDurationMenuItem(toolBar, menu, TGDuration.HALF, duration.getValue(), running);
		this.createDurationMenuItem(toolBar, menu, TGDuration.QUARTER, duration.getValue(), running);
		this.createDurationMenuItem(toolBar, menu, TGDuration.EIGHTH, duration.getValue(), running);
		this.createDurationMenuItem(toolBar, menu, TGDuration.SIXTEENTH, duration.getValue(), running);
		this.createDurationMenuItem(toolBar, menu, TGDuration.THIRTY_SECOND, duration.getValue(), running);
		this.createDurationMenuItem(toolBar, menu, TGDuration.SIXTY_FOURTH, duration.getValue(), running);
		
		Rectangle rect = item.getBounds();
		Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
		
		menu.setLocation(pt.x, pt.y + rect.height);
		menu.setVisible(true);
	}
	
	private void createDurationMenuItem(TGToolBar toolBar, Menu menu, int value, int selection, boolean running) {
		Image icon = this.findDurationIcon(toolBar, value);
		String action = this.findDurationAction(value);
		String nameKey = this.findDurationNameKey(value);
		if( icon != null && action != null && nameKey != null ) {
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setEnabled(!running);
			menuItem.addSelectionListener(toolBar.createActionProcessor(action));
			menuItem.setText(toolBar.getText(nameKey, (value == selection)));
			menuItem.setImage(icon);
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
	
	private Image findDurationIcon(TGToolBar toolBar, int value) {
		return toolBar.getIconManager().getDuration(value);
	}
	
	public void processDivisionTypeSelection(TGToolBar toolBar, SelectionEvent event) {
		if (event.detail == SWT.ARROW) {
			this.createDivisionTypeMenu(toolBar, (ToolItem) event.widget);
		}else{
			this.toggleDivisionType(toolBar);
		}
	}
	
	private void toggleDivisionType(TGToolBar toolBar) {
		TGDuration duration = TablatureEditor.getInstance(toolBar.getContext()).getTablature().getCaret().getDuration();
		TGDivisionType divisionType = null;
		if(duration.getDivision().isEqual(TGDivisionType.NORMAL)){
			divisionType = this.createDivisionType(toolBar, TGDivisionType.TRIPLET);
		}else{
			divisionType = this.createDivisionType(toolBar, TGDivisionType.NORMAL);
		}
		
		this.createDivisionTypeAction(toolBar, divisionType).process();
	}
	
	private void createDivisionTypeMenu(TGToolBar toolBar, ToolItem item) {
		TGDuration duration = TablatureEditor.getInstance(toolBar.getContext()).getTablature().getCaret().getDuration();
		Menu menu = new Menu(item.getParent().getShell());
		
		for( int i = 0 ; i < TGDivisionType.ALTERED_DIVISION_TYPES.length ; i ++ ){
			TGDivisionType divisionType = TGDivisionType.ALTERED_DIVISION_TYPES[i];
			
			MenuItem menuItem = new MenuItem(menu, SWT.CHECK);
			menuItem.setText(toolBar.toCheckString(Integer.toString(TGDivisionType.ALTERED_DIVISION_TYPES[i].getEnters()), (divisionType.isEqual(duration.getDivision()))));
			menuItem.addSelectionListener(this.createDivisionTypeAction(toolBar, divisionType));
		}
		
		Rectangle rect = item.getBounds();
		Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
		
		menu.setLocation(pt.x, pt.y + rect.height);
		menu.setVisible(true);
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
