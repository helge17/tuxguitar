package org.herac.tuxguitar.app.view.menu.impl;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
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
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class DurationMenuItem  extends TGMenuItem {
	
	private UIMenuSubMenuItem durationMenuItem;
	private UIMenuActionItem whole;
	private UIMenuActionItem half;
	private UIMenuActionItem quarter;
	private UIMenuActionItem eighth;
	private UIMenuActionItem sixteenth;
	private UIMenuActionItem thirtySecond;
	private UIMenuActionItem sixtyFourth;
	private UIMenuActionItem dotted;
	private UIMenuActionItem doubleDotted;
	private UIMenuActionItem division;
	
	public DurationMenuItem(UIMenuSubMenuItem durationMenuItem) {
		this.durationMenuItem = durationMenuItem;
	}
	
	public DurationMenuItem(UIMenu parent) {
		this(parent.createSubMenuItem());
	}
	
	public void showItems(){
		//--whole--
		this.whole = this.durationMenuItem.getMenu().createActionItem();
		this.whole.addSelectionListener(this.createActionProcessor(TGSetWholeDurationAction.NAME));
		
		//--half--
		this.half = this.durationMenuItem.getMenu().createActionItem();
		this.half.addSelectionListener(this.createActionProcessor(TGSetHalfDurationAction.NAME));
		
		//--quarter--
		this.quarter = this.durationMenuItem.getMenu().createActionItem();
		this.quarter.addSelectionListener(this.createActionProcessor(TGSetQuarterDurationAction.NAME));
		
		//--Eighth--
		this.eighth = this.durationMenuItem.getMenu().createActionItem();
		this.eighth.addSelectionListener(this.createActionProcessor(TGSetEighthDurationAction.NAME));
		
		//--sixteenth--
		this.sixteenth = this.durationMenuItem.getMenu().createActionItem();
		this.sixteenth.addSelectionListener(this.createActionProcessor(TGSetSixteenthDurationAction.NAME));
		
		//--thirtySecond--
		this.thirtySecond = this.durationMenuItem.getMenu().createActionItem();
		this.thirtySecond.addSelectionListener(this.createActionProcessor(TGSetThirtySecondDurationAction.NAME));
		
		//--sixtyFourth--
		this.sixtyFourth = this.durationMenuItem.getMenu().createActionItem();
		this.sixtyFourth.addSelectionListener(this.createActionProcessor(TGSetSixtyFourthDurationAction.NAME));
		
		//--SEPARATOR--
		this.durationMenuItem.getMenu().createSeparator();
		
		//--dotted---
		this.dotted = this.durationMenuItem.getMenu().createActionItem();
		this.dotted.addSelectionListener(this.createActionProcessor(TGChangeDottedDurationAction.NAME));
		
		this.doubleDotted = this.durationMenuItem.getMenu().createActionItem();
		this.doubleDotted.addSelectionListener(this.createActionProcessor(TGChangeDoubleDottedDurationAction.NAME));
		
		//--division---
		this.division = this.durationMenuItem.getMenu().createActionItem();
		this.division.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				DurationMenuItem.this.toggleDivisionType();
			}
		});
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		this.whole.setEnabled(!running);
		this.half.setEnabled(!running);
		this.quarter.setEnabled(!running);
		this.eighth.setEnabled(!running);
		this.sixteenth.setEnabled(!running);
		this.thirtySecond.setEnabled(!running);
		this.sixtyFourth.setEnabled(!running);
		this.dotted.setEnabled(!running);
		this.doubleDotted.setEnabled(!running);
		this.division.setEnabled(!running);
	}
	
	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.durationMenuItem, "duration", null);
		setMenuItemTextAndAccelerator(this.whole, "duration.whole", TGSetWholeDurationAction.NAME);
		setMenuItemTextAndAccelerator(this.half, "duration.half", TGSetHalfDurationAction.NAME);
		setMenuItemTextAndAccelerator(this.quarter, "duration.quarter", TGSetQuarterDurationAction.NAME);
		setMenuItemTextAndAccelerator(this.eighth, "duration.eighth", TGSetEighthDurationAction.NAME);
		setMenuItemTextAndAccelerator(this.sixteenth, "duration.sixteenth", TGSetSixteenthDurationAction.NAME);
		setMenuItemTextAndAccelerator(this.thirtySecond, "duration.thirtysecond", TGSetThirtySecondDurationAction.NAME);
		setMenuItemTextAndAccelerator(this.sixtyFourth, "duration.sixtyfourth", TGSetSixtyFourthDurationAction.NAME);
		setMenuItemTextAndAccelerator(this.dotted, "duration.dotted", TGChangeDottedDurationAction.NAME);
		setMenuItemTextAndAccelerator(this.doubleDotted, "duration.doubledotted", TGChangeDoubleDottedDurationAction.NAME);
		setMenuItemTextAndAccelerator(this.division, "duration.division-type", TGSetDivisionTypeDurationAction.NAME);
	}
	
	public void loadIcons() {
		this.whole.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.WHOLE));
		this.half.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.HALF));
		this.quarter.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.QUARTER));
		this.eighth.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.EIGHTH));
		this.sixteenth.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.SIXTEENTH));
		this.thirtySecond.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.THIRTY_SECOND));
		this.sixtyFourth.setImage(TuxGuitar.getInstance().getIconManager().getDuration(TGDuration.SIXTY_FOURTH));
		this.dotted.setImage(TuxGuitar.getInstance().getIconManager().getDurationDotted());
		this.doubleDotted.setImage(TuxGuitar.getInstance().getIconManager().getDurationDoubleDotted());
		this.division.setImage(TuxGuitar.getInstance().getIconManager().getDivisionType());
	}
	
	private void toggleDivisionType() {
		TGDuration duration = TablatureEditor.getInstance(this.findContext()).getTablature().getCaret().getDuration();
		TGDivisionType divisionType = (duration.getDivision().isEqual(TGDivisionType.NORMAL) ? TGDivisionType.TRIPLET : TGDivisionType.NORMAL);
		
		this.createDivisionTypeAction(this.createDivisionType(divisionType)).process();
	}
	
	private TGDivisionType createDivisionType(TGDivisionType tgDivisionTypeSrc) {
		TGFactory tgFactory = TGDocumentManager.getInstance(this.findContext()).getSongManager().getFactory();
		TGDivisionType tgDivisionTypeDst = tgFactory.newDivisionType();
		tgDivisionTypeDst.copyFrom(tgDivisionTypeSrc);
		return tgDivisionTypeDst;
	}
	
	private TGActionProcessorListener createDivisionTypeAction(TGDivisionType tgDivisionType){
		TGActionProcessorListener tgActionProcessor = this.createActionProcessor(TGSetDivisionTypeDurationAction.NAME);
		tgActionProcessor.setAttribute(TGSetDivisionTypeDurationAction.PROPERTY_DIVISION_TYPE, tgDivisionType);
		return tgActionProcessor;
	}
}
