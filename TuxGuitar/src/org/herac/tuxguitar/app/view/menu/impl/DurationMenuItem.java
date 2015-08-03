/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.view.menu.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
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
import org.herac.tuxguitar.song.models.TGDuration;
/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DurationMenuItem  extends TGMenuItem{
	private MenuItem durationMenuItem;
	private Menu menu;
	private MenuItem whole;
	private MenuItem half;
	private MenuItem quarter;
	private MenuItem eighth;
	private MenuItem sixteenth;
	private MenuItem thirtySecond;
	private MenuItem sixtyFourth;
	private MenuItem dotted;
	private MenuItem doubleDotted;
	private MenuItem division;
	
	public DurationMenuItem(Shell shell,Menu parent, int style) {
		this.durationMenuItem = new MenuItem(parent, style);
		this.menu = new Menu(shell, SWT.DROP_DOWN);
	}
	
	public void showItems(){
		//--whole--
		this.whole = new MenuItem(this.menu, SWT.PUSH);
		this.whole.addSelectionListener(this.createActionProcessor(TGSetWholeDurationAction.NAME));
		//--half--
		this.half = new MenuItem(this.menu, SWT.PUSH);
		this.half.addSelectionListener(this.createActionProcessor(TGSetHalfDurationAction.NAME));
		//--quarter--
		this.quarter = new MenuItem(this.menu, SWT.PUSH);
		this.quarter.addSelectionListener(this.createActionProcessor(TGSetQuarterDurationAction.NAME));
		//--Eighth--
		this.eighth = new MenuItem(this.menu, SWT.PUSH);
		this.eighth.addSelectionListener(this.createActionProcessor(TGSetEighthDurationAction.NAME));
		//--sixteenth--
		this.sixteenth = new MenuItem(this.menu, SWT.PUSH);
		this.sixteenth.addSelectionListener(this.createActionProcessor(TGSetSixteenthDurationAction.NAME));
		//--thirtySecond--
		this.thirtySecond = new MenuItem(this.menu, SWT.PUSH);
		this.thirtySecond.addSelectionListener(this.createActionProcessor(TGSetThirtySecondDurationAction.NAME));
		//--sixtyFourth--
		this.sixtyFourth = new MenuItem(this.menu, SWT.PUSH);
		this.sixtyFourth.addSelectionListener(this.createActionProcessor(TGSetSixtyFourthDurationAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--dotted---
		this.dotted = new MenuItem(this.menu, SWT.PUSH);
		this.dotted.addSelectionListener(this.createActionProcessor(TGChangeDottedDurationAction.NAME));
		
		this.doubleDotted = new MenuItem(this.menu, SWT.PUSH);
		this.doubleDotted.addSelectionListener(this.createActionProcessor(TGChangeDoubleDottedDurationAction.NAME));
		
		//--division---
		this.division = new MenuItem(this.menu, SWT.PUSH);
		this.division.addSelectionListener(this.createActionProcessor(TGSetDivisionTypeDurationAction.NAME));
		
		this.durationMenuItem.setMenu(this.menu);
		
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
}
