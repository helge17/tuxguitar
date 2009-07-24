/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.items.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.duration.ChangeDottedDurationAction;
import org.herac.tuxguitar.gui.actions.duration.ChangeDoubleDottedDurationAction;
import org.herac.tuxguitar.gui.actions.duration.ChangeDivisionTypeAction;
import org.herac.tuxguitar.gui.actions.duration.SetEighthDurationAction;
import org.herac.tuxguitar.gui.actions.duration.SetHalfDurationAction;
import org.herac.tuxguitar.gui.actions.duration.SetQuarterDurationAction;
import org.herac.tuxguitar.gui.actions.duration.SetSixteenthDurationAction;
import org.herac.tuxguitar.gui.actions.duration.SetSixtyFourthDurationAction;
import org.herac.tuxguitar.gui.actions.duration.SetThirtySecondDurationAction;
import org.herac.tuxguitar.gui.actions.duration.SetWholeDurationAction;
import org.herac.tuxguitar.gui.items.MenuItems;
import org.herac.tuxguitar.song.models.TGDuration;
/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DurationMenuItem  extends MenuItems{
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
		this.whole.addSelectionListener(TuxGuitar.instance().getAction(SetWholeDurationAction.NAME));
		//--half--
		this.half = new MenuItem(this.menu, SWT.PUSH);
		this.half.addSelectionListener(TuxGuitar.instance().getAction(SetHalfDurationAction.NAME));
		//--quarter--
		this.quarter = new MenuItem(this.menu, SWT.PUSH);
		this.quarter.addSelectionListener(TuxGuitar.instance().getAction(SetQuarterDurationAction.NAME));
		//--Eighth--
		this.eighth = new MenuItem(this.menu, SWT.PUSH);
		this.eighth.addSelectionListener(TuxGuitar.instance().getAction(SetEighthDurationAction.NAME));
		//--sixteenth--
		this.sixteenth = new MenuItem(this.menu, SWT.PUSH);
		this.sixteenth.addSelectionListener(TuxGuitar.instance().getAction(SetSixteenthDurationAction.NAME));
		//--thirtySecond--
		this.thirtySecond = new MenuItem(this.menu, SWT.PUSH);
		this.thirtySecond.addSelectionListener(TuxGuitar.instance().getAction(SetThirtySecondDurationAction.NAME));
		//--sixtyFourth--
		this.sixtyFourth = new MenuItem(this.menu, SWT.PUSH);
		this.sixtyFourth.addSelectionListener(TuxGuitar.instance().getAction(SetSixtyFourthDurationAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--dotted---
		this.dotted = new MenuItem(this.menu, SWT.PUSH);
		this.dotted.addSelectionListener(TuxGuitar.instance().getAction(ChangeDottedDurationAction.NAME));
		
		this.doubleDotted = new MenuItem(this.menu, SWT.PUSH);
		this.doubleDotted.addSelectionListener(TuxGuitar.instance().getAction(ChangeDoubleDottedDurationAction.NAME));
		
		//--division---
		this.division = new MenuItem(this.menu, SWT.PUSH);
		this.division.addSelectionListener(TuxGuitar.instance().getAction(ChangeDivisionTypeAction.NAME));
		
		this.durationMenuItem.setMenu(this.menu);
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		boolean running = TuxGuitar.instance().getPlayer().isRunning();
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
		setMenuItemTextAndAccelerator(this.whole, "duration.whole", SetWholeDurationAction.NAME);
		setMenuItemTextAndAccelerator(this.half, "duration.half", SetHalfDurationAction.NAME);
		setMenuItemTextAndAccelerator(this.quarter, "duration.quarter", SetQuarterDurationAction.NAME);
		setMenuItemTextAndAccelerator(this.eighth, "duration.eighth", SetEighthDurationAction.NAME);
		setMenuItemTextAndAccelerator(this.sixteenth, "duration.sixteenth", SetSixteenthDurationAction.NAME);
		setMenuItemTextAndAccelerator(this.thirtySecond, "duration.thirtysecond", SetThirtySecondDurationAction.NAME);
		setMenuItemTextAndAccelerator(this.sixtyFourth, "duration.sixtyfourth", SetSixtyFourthDurationAction.NAME);
		setMenuItemTextAndAccelerator(this.dotted, "duration.dotted", ChangeDottedDurationAction.NAME);
		setMenuItemTextAndAccelerator(this.doubleDotted, "duration.doubledotted", ChangeDoubleDottedDurationAction.NAME);
		setMenuItemTextAndAccelerator(this.division, "duration.division-type", ChangeDivisionTypeAction.NAME);
	}
	
	public void loadIcons() {
		this.whole.setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.WHOLE));
		this.half.setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.HALF));
		this.quarter.setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.QUARTER));
		this.eighth.setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.EIGHTH));
		this.sixteenth.setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.SIXTEENTH));
		this.thirtySecond.setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.THIRTY_SECOND));
		this.sixtyFourth.setImage(TuxGuitar.instance().getIconManager().getDuration(TGDuration.SIXTY_FOURTH));
		this.dotted.setImage(TuxGuitar.instance().getIconManager().getDurationDotted());
		this.doubleDotted.setImage(TuxGuitar.instance().getIconManager().getDurationDoubleDotted());
		this.division.setImage(TuxGuitar.instance().getIconManager().getDivisionType());
	}
}
