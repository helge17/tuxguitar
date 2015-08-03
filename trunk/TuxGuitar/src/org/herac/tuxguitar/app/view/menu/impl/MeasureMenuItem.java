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
import org.herac.tuxguitar.app.action.impl.measure.TGGoFirstMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.TGGoLastMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.TGGoNextMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.TGGoPreviousMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.TGOpenMeasureAddDialogAction;
import org.herac.tuxguitar.app.action.impl.measure.TGOpenMeasureCleanDialogAction;
import org.herac.tuxguitar.app.action.impl.measure.TGOpenMeasureCopyDialogAction;
import org.herac.tuxguitar.app.action.impl.measure.TGOpenMeasurePasteDialogAction;
import org.herac.tuxguitar.app.action.impl.measure.TGOpenMeasureRemoveDialogAction;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.graphics.control.TGMeasureImpl;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MeasureMenuItem extends TGMenuItem{
	
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
		this.first.addSelectionListener(this.createActionProcessor(TGGoFirstMeasureAction.NAME));
		//--previous--
		this.previous = new MenuItem(this.menu, SWT.PUSH);
		this.previous.addSelectionListener(this.createActionProcessor(TGGoPreviousMeasureAction.NAME));
		//--next--
		this.next = new MenuItem(this.menu, SWT.PUSH);
		this.next.addSelectionListener(this.createActionProcessor(TGGoNextMeasureAction.NAME));
		//--last--
		this.last = new MenuItem(this.menu, SWT.PUSH);
		this.last.addSelectionListener(this.createActionProcessor(TGGoLastMeasureAction.NAME));
		
		//--SEPARATOR
		new MenuItem(this.menu, SWT.SEPARATOR);
		//--add--
		this.addMeasure = new MenuItem(this.menu, SWT.PUSH);
		this.addMeasure.addSelectionListener(this.createActionProcessor(TGOpenMeasureAddDialogAction.NAME));
		//--clean--
		this.cleanMeasure = new MenuItem(this.menu, SWT.PUSH);
		this.cleanMeasure.addSelectionListener(this.createActionProcessor(TGOpenMeasureCleanDialogAction.NAME));
		//--remove--
		this.removeMeasure = new MenuItem(this.menu, SWT.PUSH);
		this.removeMeasure.addSelectionListener(this.createActionProcessor(TGOpenMeasureRemoveDialogAction.NAME));
		
		//--SEPARATOR
		new MenuItem(this.menu, SWT.SEPARATOR);
		//--copy--
		this.copyMeasure = new MenuItem(this.menu, SWT.PUSH);
		this.copyMeasure.addSelectionListener(this.createActionProcessor(TGOpenMeasureCopyDialogAction.NAME));
		//--paste--
		this.pasteMeasure = new MenuItem(this.menu, SWT.PUSH);
		
		this.pasteMeasure.addSelectionListener(this.createActionProcessor(TGOpenMeasurePasteDialogAction.NAME));
		
		this.measureMenuItem.setMenu(this.menu);
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		TGMeasureImpl measure = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getMeasure();
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
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
		this.pasteMeasure.setEnabled(!running && !TuxGuitar.getInstance().getTablatureEditor().getClipBoard().isEmpty());
	}
	
	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.measureMenuItem, "measure", null);
		setMenuItemTextAndAccelerator(this.first, "measure.first", TGGoFirstMeasureAction.NAME);
		setMenuItemTextAndAccelerator(this.last, "measure.last", TGGoLastMeasureAction.NAME);
		setMenuItemTextAndAccelerator(this.previous, "measure.previous", TGGoPreviousMeasureAction.NAME);
		setMenuItemTextAndAccelerator(this.next, "measure.next", TGGoNextMeasureAction.NAME);
		setMenuItemTextAndAccelerator(this.addMeasure, "measure.add", TGOpenMeasureAddDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.cleanMeasure, "measure.clean", TGOpenMeasureCleanDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.removeMeasure, "measure.remove", TGOpenMeasureRemoveDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.copyMeasure, "measure.copy", TGOpenMeasureCopyDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.pasteMeasure, "measure.paste", TGOpenMeasurePasteDialogAction.NAME);
	}
	
	public void loadIcons(){
		//Nothing to do
	}
}
