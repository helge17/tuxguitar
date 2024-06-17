package org.herac.tuxguitar.app.view.menu.impl;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
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
import org.herac.tuxguitar.editor.action.note.TGChangeTiedNoteAction;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGDivisionType;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuCheckableItem;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class DurationMenuItem  extends TGMenuItem {

	private UIMenuSubMenuItem durationMenuItem;
	private UIMenuCheckableItem whole;
	private UIMenuCheckableItem half;
	private UIMenuCheckableItem quarter;
	private UIMenuCheckableItem eighth;
	private UIMenuCheckableItem sixteenth;
	private UIMenuCheckableItem thirtySecond;
	private UIMenuCheckableItem sixtyFourth;
	private UIMenuCheckableItem dotted;
	private UIMenuCheckableItem doubleDotted;
	private UIMenuCheckableItem tiedNote;

	private DivisionMenuItem divisionMenuItem;

	public DurationMenuItem(UIMenuSubMenuItem durationMenuItem) {
		this.durationMenuItem = durationMenuItem;
	}

	public DurationMenuItem(UIMenu parent) {
		this(parent.createSubMenuItem());
	}

	public void showItems(){
		//--WHOLE--
		this.whole = this.durationMenuItem.getMenu().createCheckItem();
		this.whole.addSelectionListener(this.createActionProcessor(TGSetWholeDurationAction.NAME));

		//--HALF--
		this.half = this.durationMenuItem.getMenu().createCheckItem();
		this.half.addSelectionListener(this.createActionProcessor(TGSetHalfDurationAction.NAME));

		//--QUARTER--
		this.quarter = this.durationMenuItem.getMenu().createCheckItem();
		this.quarter.addSelectionListener(this.createActionProcessor(TGSetQuarterDurationAction.NAME));

		//--EIGHTH--
		this.eighth = this.durationMenuItem.getMenu().createCheckItem();
		this.eighth.addSelectionListener(this.createActionProcessor(TGSetEighthDurationAction.NAME));

		//--SIXTEENTH--
		this.sixteenth = this.durationMenuItem.getMenu().createCheckItem();
		this.sixteenth.addSelectionListener(this.createActionProcessor(TGSetSixteenthDurationAction.NAME));

		//--THIRTY SECOND--
		this.thirtySecond = this.durationMenuItem.getMenu().createCheckItem();
		this.thirtySecond.addSelectionListener(this.createActionProcessor(TGSetThirtySecondDurationAction.NAME));

		//--SIXTY FOURTH--
		this.sixtyFourth = this.durationMenuItem.getMenu().createCheckItem();
		this.sixtyFourth.addSelectionListener(this.createActionProcessor(TGSetSixtyFourthDurationAction.NAME));

		//--SEPARATOR--
		this.durationMenuItem.getMenu().createSeparator();

		//--DOTTED--
		this.dotted = this.durationMenuItem.getMenu().createCheckItem();
		this.dotted.addSelectionListener(this.createActionProcessor(TGChangeDottedDurationAction.NAME));

		//--DOUBLE-DOTTED--
		this.doubleDotted = this.durationMenuItem.getMenu().createCheckItem();
		this.doubleDotted.addSelectionListener(this.createActionProcessor(TGChangeDoubleDottedDurationAction.NAME));

		//--SEPARATOR--
		this.durationMenuItem.getMenu().createSeparator();

		//--TIED NOTE--
		this.tiedNote = this.durationMenuItem.getMenu().createCheckItem();
		this.tiedNote.addSelectionListener(this.createActionProcessor(TGChangeTiedNoteAction.NAME));

		//--SEPARATOR--
		this.durationMenuItem.getMenu().createSeparator();

		//--DIVISION--
		this.divisionMenuItem = new DivisionMenuItem(this.durationMenuItem.getMenu().createSubMenuItem());
		this.divisionMenuItem.showItems();

		this.loadIcons();
		this.loadProperties();
	}

	public void update(){
		Tablature tablature = TuxGuitar.getInstance().getTablatureEditor().getTablature();
		Caret caret = tablature.getCaret();
		TGDuration duration = caret.getDuration();
		TGNote note = caret.getSelectedNote();
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		this.whole.setChecked(duration.getValue() == TGDuration.WHOLE);
		this.whole.setEnabled(!running);
		this.half.setChecked(duration.getValue() == TGDuration.HALF);
		this.half.setEnabled(!running);
		this.quarter.setChecked(duration.getValue() == TGDuration.QUARTER);
		this.quarter.setEnabled(!running);
		this.eighth.setChecked(duration.getValue() == TGDuration.EIGHTH);
		this.eighth.setEnabled(!running);
		this.sixteenth.setChecked(duration.getValue() == TGDuration.SIXTEENTH);
		this.sixteenth.setEnabled(!running);
		this.thirtySecond.setChecked(duration.getValue() == TGDuration.THIRTY_SECOND);
		this.thirtySecond.setEnabled(!running);
		this.sixtyFourth.setChecked(duration.getValue() == TGDuration.SIXTY_FOURTH);
		this.sixtyFourth.setEnabled(!running);
		this.dotted.setChecked(duration.isDotted());
		this.dotted.setEnabled(!running);
		this.doubleDotted.setChecked(duration.isDoubleDotted());
		this.doubleDotted.setEnabled(!running);
		this.tiedNote.setChecked(note != null && note.isTiedNote());
		this.tiedNote.setEnabled(!running);

		this.divisionMenuItem.update();
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
		setMenuItemTextAndAccelerator(this.tiedNote, "duration.tiednote", TGChangeTiedNoteAction.NAME);

		this.divisionMenuItem.loadProperties();
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
		this.tiedNote.setImage(TuxGuitar.getInstance().getIconManager().getNoteTied());

		this.divisionMenuItem.loadIcons();
	}
}
