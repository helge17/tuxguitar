package org.herac.tuxguitar.app.view.menu.impl;

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
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class MeasureMenuItem extends TGMenuItem {
	
	private UIMenuSubMenuItem measureMenuItem;
	private UIMenuActionItem first;
	private UIMenuActionItem last;
	private UIMenuActionItem next;
	private UIMenuActionItem previous;
	private UIMenuActionItem addMeasure;
	private UIMenuActionItem cleanMeasure;
	private UIMenuActionItem removeMeasure;
	private UIMenuActionItem copyMeasure;
	private UIMenuActionItem pasteMeasure;
	
	public MeasureMenuItem(UIMenu parent) {
		this.measureMenuItem = parent.createSubMenuItem();
	}
	
	public void showItems(){
		//--first--
		this.first = this.measureMenuItem.getMenu().createActionItem();
		this.first.addSelectionListener(this.createActionProcessor(TGGoFirstMeasureAction.NAME));
		
		//--previous--
		this.previous = this.measureMenuItem.getMenu().createActionItem();
		this.previous.addSelectionListener(this.createActionProcessor(TGGoPreviousMeasureAction.NAME));
		
		//--next--
		this.next = this.measureMenuItem.getMenu().createActionItem();
		this.next.addSelectionListener(this.createActionProcessor(TGGoNextMeasureAction.NAME));
		
		//--last--
		this.last = this.measureMenuItem.getMenu().createActionItem();
		this.last.addSelectionListener(this.createActionProcessor(TGGoLastMeasureAction.NAME));
		
		//--SEPARATOR
		this.measureMenuItem.getMenu().createSeparator();
		
		//--add--
		this.addMeasure = this.measureMenuItem.getMenu().createActionItem();
		this.addMeasure.addSelectionListener(this.createActionProcessor(TGOpenMeasureAddDialogAction.NAME));
		
		//--clean--
		this.cleanMeasure = this.measureMenuItem.getMenu().createActionItem();
		this.cleanMeasure.addSelectionListener(this.createActionProcessor(TGOpenMeasureCleanDialogAction.NAME));

		//--remove--
		this.removeMeasure = this.measureMenuItem.getMenu().createActionItem();
		this.removeMeasure.addSelectionListener(this.createActionProcessor(TGOpenMeasureRemoveDialogAction.NAME));
		
		//--SEPARATOR
		this.measureMenuItem.getMenu().createSeparator();
		
		//--copy--
		this.copyMeasure = this.measureMenuItem.getMenu().createActionItem();
		this.copyMeasure.addSelectionListener(this.createActionProcessor(TGOpenMeasureCopyDialogAction.NAME));
		
		//--paste--
		this.pasteMeasure = this.measureMenuItem.getMenu().createActionItem();
		
		this.pasteMeasure.addSelectionListener(this.createActionProcessor(TGOpenMeasurePasteDialogAction.NAME));
		
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
