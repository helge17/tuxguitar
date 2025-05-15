package app.tuxguitar.app.view.menu.impl;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.measure.TGGoFirstMeasureAction;
import app.tuxguitar.app.action.impl.measure.TGGoLastMeasureAction;
import app.tuxguitar.app.action.impl.measure.TGGoNextMeasureAction;
import app.tuxguitar.app.action.impl.measure.TGGoPreviousMeasureAction;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasureAddDialogAction;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasureCleanDialogAction;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasureCopyDialogAction;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasurePasteDialogAction;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasureRemoveDialogAction;
import app.tuxguitar.app.action.impl.measure.TGToggleLineBreakAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.view.menu.TGMenuItem;
import app.tuxguitar.editor.action.measure.TGRemoveUnusedVoiceAction;
import app.tuxguitar.editor.clipboard.TGClipboard;
import app.tuxguitar.graphics.control.TGMeasureImpl;
import app.tuxguitar.ui.menu.UIMenu;
import app.tuxguitar.ui.menu.UIMenuActionItem;
import app.tuxguitar.ui.menu.UIMenuCheckableItem;
import app.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class MeasureMenuItem extends TGMenuItem {

	private UIMenuSubMenuItem measureMenuItem;
	private UIMenuCheckableItem lineBreak;
	private UIMenuActionItem first;
	private UIMenuActionItem last;
	private UIMenuActionItem next;
	private UIMenuActionItem previous;
	private UIMenuActionItem addMeasure;
	private UIMenuActionItem cleanMeasure;
	private UIMenuActionItem removeMeasure;
	private UIMenuActionItem copyMeasure;
	private UIMenuActionItem pasteMeasure;
	private UIMenuActionItem removeVoice;

	public MeasureMenuItem(UIMenu parent) {
		this.measureMenuItem = parent.createSubMenuItem();
	}

	public void showItems(){
		//--LINEBREAK--
		this.lineBreak = this.measureMenuItem.getMenu().createCheckItem();
		this.lineBreak.addSelectionListener(this.createActionProcessor(TGToggleLineBreakAction.NAME));

		//--SEPARATOR--
		this.measureMenuItem.getMenu().createSeparator();

		//--FIRST--
		this.first = this.measureMenuItem.getMenu().createActionItem();
		this.first.addSelectionListener(this.createActionProcessor(TGGoFirstMeasureAction.NAME));

		//--PREVIOUS--
		this.previous = this.measureMenuItem.getMenu().createActionItem();
		this.previous.addSelectionListener(this.createActionProcessor(TGGoPreviousMeasureAction.NAME));

		//--NEXT--
		this.next = this.measureMenuItem.getMenu().createActionItem();
		this.next.addSelectionListener(this.createActionProcessor(TGGoNextMeasureAction.NAME));

		//--LAST--
		this.last = this.measureMenuItem.getMenu().createActionItem();
		this.last.addSelectionListener(this.createActionProcessor(TGGoLastMeasureAction.NAME));

		//--SEPARATOR--
		this.measureMenuItem.getMenu().createSeparator();

		//--ADD--
		this.addMeasure = this.measureMenuItem.getMenu().createActionItem();
		this.addMeasure.addSelectionListener(this.createActionProcessor(TGOpenMeasureAddDialogAction.NAME));

		//--CLEAN--
		this.cleanMeasure = this.measureMenuItem.getMenu().createActionItem();
		this.cleanMeasure.addSelectionListener(this.createActionProcessor(TGOpenMeasureCleanDialogAction.NAME));

		//--REMOVE--
		this.removeMeasure = this.measureMenuItem.getMenu().createActionItem();
		this.removeMeasure.addSelectionListener(this.createActionProcessor(TGOpenMeasureRemoveDialogAction.NAME));

		//--SEPARATOR--
		this.measureMenuItem.getMenu().createSeparator();

		//--COPY--
		this.copyMeasure = this.measureMenuItem.getMenu().createActionItem();
		this.copyMeasure.addSelectionListener(this.createActionProcessor(TGOpenMeasureCopyDialogAction.NAME));

		//--PASTE--
		this.pasteMeasure = this.measureMenuItem.getMenu().createActionItem();
		this.pasteMeasure.addSelectionListener(this.createActionProcessor(TGOpenMeasurePasteDialogAction.NAME));

		//--SEPARATOR--
		this.measureMenuItem.getMenu().createSeparator();

		// -- REMOVE UNUSED VOICE --
		this.removeVoice = this.measureMenuItem.getMenu().createActionItem();
		this.removeVoice.addSelectionListener(this.createActionProcessor(TGRemoveUnusedVoiceAction.NAME));

		this.loadIcons();
		this.loadProperties();
	}

	public void update(){
		TGMeasureImpl measure = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getMeasure();
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		boolean isFirst = (measure.getNumber() == 1);
		boolean isLast = (measure.getNumber() == measure.getTrack().countMeasures());
		this.lineBreak.setEnabled(!running);
		this.lineBreak.setChecked(measure.isLineBreak());
		this.first.setEnabled(!isFirst);
		this.previous.setEnabled(!isFirst);
		this.next.setEnabled(!isLast);
		this.last.setEnabled(!isLast);
		this.addMeasure.setEnabled(!running);
		this.cleanMeasure.setEnabled(!running);
		this.removeMeasure.setEnabled(!running);
		this.copyMeasure.setEnabled(!running);
		this.pasteMeasure.setEnabled(!running && TGClipboard.getInstance(findContext()).getSegment() != null);
		this.removeVoice.setEnabled(!running);
	}

	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.measureMenuItem, "measure", null);
		setMenuItemTextAndAccelerator(this.lineBreak, "measure.linebreak", TGToggleLineBreakAction.NAME);
		setMenuItemTextAndAccelerator(this.first, "measure.first", TGGoFirstMeasureAction.NAME);
		setMenuItemTextAndAccelerator(this.last, "measure.last", TGGoLastMeasureAction.NAME);
		setMenuItemTextAndAccelerator(this.previous, "measure.previous", TGGoPreviousMeasureAction.NAME);
		setMenuItemTextAndAccelerator(this.next, "measure.next", TGGoNextMeasureAction.NAME);
		setMenuItemTextAndAccelerator(this.addMeasure, "measure.add", TGOpenMeasureAddDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.cleanMeasure, "measure.clean", TGOpenMeasureCleanDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.removeMeasure, "measure.remove", TGOpenMeasureRemoveDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.copyMeasure, "measure.copy", TGOpenMeasureCopyDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.pasteMeasure, "measure.paste", TGOpenMeasurePasteDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.removeVoice, "measure.voice.remove-unused", TGRemoveUnusedVoiceAction.NAME);
	}

	public void loadIcons(){
		this.first.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.MEASURE_FIRST));
		this.last.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.MEASURE_LAST));
		this.previous.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.MEASURE_PREVIOUS));
		this.next.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.MEASURE_NEXT));
		this.addMeasure.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.MEASURE_ADD));
		this.cleanMeasure.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.MEASURE_CLEAN));
		this.removeMeasure.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.MEASURE_REMOVE));
		this.copyMeasure.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.MEASURE_COPY));
		this.pasteMeasure.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.MEASURE_PASTE));
	}
}
