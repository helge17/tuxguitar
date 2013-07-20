/*
 * Created on 02-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.items.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessor;
import org.herac.tuxguitar.app.actions.measure.AddMeasureAction;
import org.herac.tuxguitar.app.actions.measure.CleanMeasureAction;
import org.herac.tuxguitar.app.actions.measure.CopyMeasureAction;
import org.herac.tuxguitar.app.actions.measure.GoFirstMeasureAction;
import org.herac.tuxguitar.app.actions.measure.GoLastMeasureAction;
import org.herac.tuxguitar.app.actions.measure.GoNextMeasureAction;
import org.herac.tuxguitar.app.actions.measure.GoPreviousMeasureAction;
import org.herac.tuxguitar.app.actions.measure.PasteMeasureAction;
import org.herac.tuxguitar.app.actions.measure.RemoveMeasureAction;
import org.herac.tuxguitar.app.items.MenuItems;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MeasureMenuItem extends MenuItems{
	
	private MenuItem measureMenuItem;
	private Menu menu; 
	private MenuItem first;
	private MenuItem last;
	private MenuItem next;
	private MenuItem previous;
	private MenuItem addMeasure;
	private MenuItem cleanMeasure;
	private MenuItem removeMeasure;
	private MenuItem copyMeasure;
	private MenuItem pasteMeasure;
	
	public MeasureMenuItem(Shell shell,Menu parent, int style) {
		this.measureMenuItem = new MenuItem(parent, style);
		this.menu = new Menu(shell, SWT.DROP_DOWN);
	}
	
	public void showItems(){
		//--first--
		this.first = new MenuItem(this.menu, SWT.PUSH);
		this.first.addSelectionListener(new TGActionProcessor(GoFirstMeasureAction.NAME));
		//--previous--
		this.previous = new MenuItem(this.menu, SWT.PUSH);
		this.previous.addSelectionListener(new TGActionProcessor(GoPreviousMeasureAction.NAME));
		//--next--
		this.next = new MenuItem(this.menu, SWT.PUSH);
		this.next.addSelectionListener(new TGActionProcessor(GoNextMeasureAction.NAME));
		//--last--
		this.last = new MenuItem(this.menu, SWT.PUSH);
		this.last.addSelectionListener(new TGActionProcessor(GoLastMeasureAction.NAME));
		
		//--SEPARATOR
		new MenuItem(this.menu, SWT.SEPARATOR);
		//--add--
		this.addMeasure = new MenuItem(this.menu, SWT.PUSH);
		this.addMeasure.addSelectionListener(new TGActionProcessor(AddMeasureAction.NAME));
		//--clean--
		this.cleanMeasure = new MenuItem(this.menu, SWT.PUSH);
		this.cleanMeasure.addSelectionListener(new TGActionProcessor(CleanMeasureAction.NAME));
		//--remove--
		this.removeMeasure = new MenuItem(this.menu, SWT.PUSH);
		this.removeMeasure.addSelectionListener(new TGActionProcessor(RemoveMeasureAction.NAME));
		
		//--SEPARATOR
		new MenuItem(this.menu, SWT.SEPARATOR);
		//--copy--
		this.copyMeasure = new MenuItem(this.menu, SWT.PUSH);
		this.copyMeasure.addSelectionListener(new TGActionProcessor(CopyMeasureAction.NAME));
		//--paste--
		this.pasteMeasure = new MenuItem(this.menu, SWT.PUSH);
		
		this.pasteMeasure.addSelectionListener(new TGActionProcessor(PasteMeasureAction.NAME));
		
		this.measureMenuItem.setMenu(this.menu);
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		TGMeasureImpl measure = TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().getMeasure();
		boolean running = TuxGuitar.instance().getPlayer().isRunning();
		boolean isFirst = (measure.getNumber() == 1);
		boolean isLast = (measure.getNumber() == measure.getTrack().countMeasures());
		this.first.setEnabled(!isFirst);
		this.previous.setEnabled(!isFirst);
		this.next.setEnabled(!isLast);
		this.last.setEnabled(!isLast);
		this.addMeasure.setEnabled(!running);
		this.cleanMeasure.setEnabled(!running);
		this.removeMeasure.setEnabled(!running);
		this.copyMeasure.setEnabled(!running);
		this.pasteMeasure.setEnabled(!running && !TuxGuitar.instance().getTablatureEditor().getClipBoard().isEmpty());
	}
	
	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.measureMenuItem, "measure", null);
		setMenuItemTextAndAccelerator(this.first, "measure.first", GoFirstMeasureAction.NAME);
		setMenuItemTextAndAccelerator(this.last, "measure.last", GoLastMeasureAction.NAME);
		setMenuItemTextAndAccelerator(this.previous, "measure.previous", GoPreviousMeasureAction.NAME);
		setMenuItemTextAndAccelerator(this.next, "measure.next", GoNextMeasureAction.NAME);
		setMenuItemTextAndAccelerator(this.addMeasure, "measure.add", AddMeasureAction.NAME);
		setMenuItemTextAndAccelerator(this.cleanMeasure, "measure.clean", CleanMeasureAction.NAME);
		setMenuItemTextAndAccelerator(this.removeMeasure, "measure.remove", RemoveMeasureAction.NAME);
		setMenuItemTextAndAccelerator(this.copyMeasure, "measure.copy", CopyMeasureAction.NAME);
		setMenuItemTextAndAccelerator(this.pasteMeasure, "measure.paste", PasteMeasureAction.NAME);
	}
	
	public void loadIcons(){
		//Nothing to do
	}
}
