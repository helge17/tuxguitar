package app.tuxguitar.app.view.menu.impl;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.impl.insert.TGOpenTextDialogAction;
import app.tuxguitar.app.action.impl.note.TGOpenBeatMoveDialogAction;
import app.tuxguitar.app.action.impl.note.TGOpenStrokeDownDialogAction;
import app.tuxguitar.app.action.impl.note.TGOpenStrokeUpDialogAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.view.component.tab.Caret;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.menu.TGMenuItem;
import app.tuxguitar.editor.action.note.TGChangePickStrokeUpAction;
import app.tuxguitar.editor.action.note.TGChangePickStrokeDownAction;
import app.tuxguitar.editor.action.note.TGInsertRestBeatAction;
import app.tuxguitar.editor.action.note.TGDeleteNoteOrRestAction;
import app.tuxguitar.editor.action.note.TGCleanBeatAction;
import app.tuxguitar.editor.action.note.TGSetVoiceAutoAction;
import app.tuxguitar.editor.action.note.TGSetVoiceUpAction;
import app.tuxguitar.editor.action.note.TGSetVoiceDownAction;
import app.tuxguitar.editor.action.note.TGIncrementNoteSemitoneAction;
import app.tuxguitar.editor.action.note.TGDecrementNoteSemitoneAction;
import app.tuxguitar.editor.action.note.TGShiftNoteUpAction;
import app.tuxguitar.editor.action.note.TGToggleNoteEnharmonicAction;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.graphics.control.TGTrackImpl;
import app.tuxguitar.editor.action.note.TGShiftNoteDownAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsLeftAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsRightAction;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGPickStroke;
import app.tuxguitar.song.models.TGStroke;
import app.tuxguitar.ui.menu.UIMenu;
import app.tuxguitar.ui.menu.UIMenuActionItem;
import app.tuxguitar.ui.menu.UIMenuCheckableItem;
import app.tuxguitar.ui.menu.UIMenuSubMenuItem;
import app.tuxguitar.util.TGNoteRange;

public class BeatMenuItem extends TGMenuItem {

	private UIMenuSubMenuItem noteMenuItem;
	private UIMenuActionItem insertRestBeat;
	private UIMenuActionItem deleteNoteOrRest;
	private UIMenuActionItem cleanBeat;
	private UIMenuActionItem insertText;
	private UIMenuActionItem voiceAuto;
	private UIMenuActionItem voiceUp;
	private UIMenuActionItem voiceDown;
	private UIMenuCheckableItem strokeUp;
	private UIMenuCheckableItem strokeDown;
	private UIMenuCheckableItem pickStrokeUp;
	private UIMenuCheckableItem pickStrokeDown;
	private UIMenuActionItem shiftUp;
	private UIMenuActionItem shiftDown;
	private UIMenuCheckableItem altEnharmonic;
	private UIMenuActionItem semitoneUp;
	private UIMenuActionItem semitoneDown;
	private UIMenuActionItem moveBeatsLeft;
	private UIMenuActionItem moveBeatsRight;
	private UIMenuActionItem moveBeatsCustom;

	private DurationMenuItem durationMenuItem;
	private DynamicMenuItem dynamicMenuItem;
	private NoteEffectsMenuItem effectMenuItem;
	private ChordMenuItem chordMenuItem;

	public BeatMenuItem(UIMenu parent) {
		this.noteMenuItem = parent.createSubMenuItem();
	}

	public void showItems(){
		this.insertRestBeat = this.noteMenuItem.getMenu().createActionItem();
		this.insertRestBeat.addSelectionListener(this.createActionProcessor(TGInsertRestBeatAction.NAME));

		this.deleteNoteOrRest = this.noteMenuItem.getMenu().createActionItem();
		this.deleteNoteOrRest.addSelectionListener(this.createActionProcessor(TGDeleteNoteOrRestAction.NAME));

		this.cleanBeat = this.noteMenuItem.getMenu().createActionItem();
		this.cleanBeat.addSelectionListener(this.createActionProcessor(TGCleanBeatAction.NAME));

		this.noteMenuItem.getMenu().createSeparator();

		this.durationMenuItem = new DurationMenuItem(this.noteMenuItem.getMenu().createSubMenuItem());
		this.durationMenuItem.showItems();

		this.dynamicMenuItem = new DynamicMenuItem(this.noteMenuItem.getMenu().createSubMenuItem());
		this.dynamicMenuItem.showItems();

		this.effectMenuItem = new NoteEffectsMenuItem(this.noteMenuItem.getMenu().createSubMenuItem());
		this.effectMenuItem.showItems();

		this.chordMenuItem = new ChordMenuItem(this.noteMenuItem.getMenu().createSubMenuItem());
		this.chordMenuItem.showItems();

		this.noteMenuItem.getMenu().createSeparator();

		this.insertText = this.noteMenuItem.getMenu().createActionItem();
		this.insertText.addSelectionListener(this.createActionProcessor(TGOpenTextDialogAction.NAME));

		this.noteMenuItem.getMenu().createSeparator();

		this.voiceAuto = this.noteMenuItem.getMenu().createActionItem();
		this.voiceAuto.addSelectionListener(this.createActionProcessor(TGSetVoiceAutoAction.NAME));

		this.voiceUp = this.noteMenuItem.getMenu().createActionItem();
		this.voiceUp.addSelectionListener(this.createActionProcessor(TGSetVoiceUpAction.NAME));

		this.voiceDown = this.noteMenuItem.getMenu().createActionItem();
		this.voiceDown.addSelectionListener(this.createActionProcessor(TGSetVoiceDownAction.NAME));

		this.noteMenuItem.getMenu().createSeparator();

		this.strokeUp = this.noteMenuItem.getMenu().createCheckItem();
		this.strokeUp.addSelectionListener(this.createActionProcessor(TGOpenStrokeUpDialogAction.NAME));

		this.strokeDown = this.noteMenuItem.getMenu().createCheckItem();
		this.strokeDown.addSelectionListener(this.createActionProcessor(TGOpenStrokeDownDialogAction.NAME));

		this.pickStrokeDown = this.noteMenuItem.getMenu().createCheckItem();
		this.pickStrokeDown.addSelectionListener(this.createActionProcessor(TGChangePickStrokeDownAction.NAME));

		this.pickStrokeUp = this.noteMenuItem.getMenu().createCheckItem();
		this.pickStrokeUp.addSelectionListener(this.createActionProcessor(TGChangePickStrokeUpAction.NAME));

		this.noteMenuItem.getMenu().createSeparator();

		this.altEnharmonic = this.noteMenuItem.getMenu().createCheckItem();
		this.altEnharmonic.addSelectionListener(this.createActionProcessor(TGToggleNoteEnharmonicAction.NAME));

		this.noteMenuItem.getMenu().createSeparator();

		this.semitoneUp = this.noteMenuItem.getMenu().createActionItem();
		this.semitoneUp.addSelectionListener(this.createActionProcessor(TGIncrementNoteSemitoneAction.NAME));

		this.semitoneDown = this.noteMenuItem.getMenu().createActionItem();
		this.semitoneDown.addSelectionListener(this.createActionProcessor(TGDecrementNoteSemitoneAction.NAME));

		this.noteMenuItem.getMenu().createSeparator();

		this.shiftUp = this.noteMenuItem.getMenu().createActionItem();
		this.shiftUp.addSelectionListener(this.createActionProcessor(TGShiftNoteUpAction.NAME));

		this.shiftDown = this.noteMenuItem.getMenu().createActionItem();
		this.shiftDown.addSelectionListener(this.createActionProcessor(TGShiftNoteDownAction.NAME));

		this.noteMenuItem.getMenu().createSeparator();

		this.moveBeatsLeft = this.noteMenuItem.getMenu().createActionItem();
		this.moveBeatsLeft.addSelectionListener(this.createActionProcessor(TGMoveBeatsLeftAction.NAME));

		this.moveBeatsRight = this.noteMenuItem.getMenu().createActionItem();
		this.moveBeatsRight.addSelectionListener(this.createActionProcessor(TGMoveBeatsRightAction.NAME));

		this.moveBeatsCustom = this.noteMenuItem.getMenu().createActionItem();
		this.moveBeatsCustom.addSelectionListener(this.createActionProcessor(TGOpenBeatMoveDialogAction.NAME));

		this.loadIcons();
		this.loadProperties();
	}

	public void update(){
		Tablature tablature = TuxGuitar.getInstance().getTablatureEditor().getTablature();
		Caret caret = tablature.getCaret();
		TGBeat beat = caret.getSelectedBeat();
		TGNote note = caret.getSelectedNote();
		boolean restBeat = caret.isRestBeatSelected();
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		TGTrackImpl track = caret.getTrack();
		TGNoteRange noteRange = tablature.getCurrentNoteRange();
		boolean atLeastOneNoteSelected = (note != null) || (noteRange!=null && !noteRange.isEmpty());
		int style = tablature.getViewLayout().getStyle();

		this.insertRestBeat.setEnabled(!running);
		this.deleteNoteOrRest.setEnabled(!running);
		this.cleanBeat.setEnabled(!running);
		this.voiceAuto.setEnabled(!running && !restBeat);
		this.voiceUp.setEnabled(!running && !restBeat);
		this.voiceDown.setEnabled(!running && !restBeat);
		this.strokeUp.setEnabled(!running && !restBeat && !track.isPercussion());
		this.strokeUp.setChecked( beat != null && beat.getStroke().getDirection() == TGStroke.STROKE_UP );
		this.strokeDown.setEnabled(!running && !restBeat && !track.isPercussion());
		this.strokeDown.setChecked( beat != null && beat.getStroke().getDirection() == TGStroke.STROKE_DOWN );
		this.pickStrokeUp.setEnabled(!running && !restBeat);
		this.pickStrokeUp.setChecked( beat != null && beat.getPickStroke().getDirection() == TGPickStroke.PICK_STROKE_UP );
		this.pickStrokeDown.setEnabled(!running && !restBeat);
		this.pickStrokeDown.setChecked( beat != null && beat.getPickStroke().getDirection() == TGPickStroke.PICK_STROKE_DOWN );
		this.altEnharmonic.setEnabled(!running && ((style & TGLayout.DISPLAY_SCORE) != 0) && !restBeat);
		this.altEnharmonic.setChecked((note!=null) && (note.isAltEnharmonic()));
		this.semitoneUp.setEnabled(!running && atLeastOneNoteSelected);
		this.semitoneDown.setEnabled(!running && atLeastOneNoteSelected);
		this.shiftUp.setEnabled(!running && atLeastOneNoteSelected);
		this.shiftDown.setEnabled(!running && atLeastOneNoteSelected);
		this.insertText.setEnabled(!running);
		this.moveBeatsLeft.setEnabled(!running);
		this.moveBeatsRight.setEnabled(!running);
		this.moveBeatsCustom.setEnabled(!running);
		this.durationMenuItem.update();
		this.effectMenuItem.update();
		if (track.isPercussion()) {
			this.chordMenuItem.setEnabled(false);
		} else {
			this.chordMenuItem.setEnabled(true);
			this.chordMenuItem.update();
		}
		this.dynamicMenuItem.update();
	}

	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.noteMenuItem, "beat", null);
		setMenuItemTextAndAccelerator(this.cleanBeat, "beat.clean", TGCleanBeatAction.NAME);
		setMenuItemTextAndAccelerator(this.insertRestBeat, "beat.insert-rest", TGInsertRestBeatAction.NAME);
		setMenuItemTextAndAccelerator(this.deleteNoteOrRest, "beat.delete-note-or-rest", TGDeleteNoteOrRestAction.NAME);
		setMenuItemTextAndAccelerator(this.voiceAuto, "beat.voice-auto", TGSetVoiceAutoAction.NAME);
		setMenuItemTextAndAccelerator(this.voiceUp, "beat.voice-up", TGSetVoiceUpAction.NAME);
		setMenuItemTextAndAccelerator(this.voiceDown, "beat.voice-down", TGSetVoiceDownAction.NAME);
		setMenuItemTextAndAccelerator(this.strokeUp, "beat.stroke-up", TGOpenStrokeUpDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.strokeDown, "beat.stroke-down", TGOpenStrokeDownDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.pickStrokeUp, "beat.pick-stroke-up", TGChangePickStrokeUpAction.NAME);
		setMenuItemTextAndAccelerator(this.pickStrokeDown, "beat.pick-stroke-down", TGChangePickStrokeDownAction.NAME);
		setMenuItemTextAndAccelerator(this.altEnharmonic, "note.alternative-enharmonic", TGToggleNoteEnharmonicAction.NAME);
		setMenuItemTextAndAccelerator(this.semitoneUp, "note.semitone-up", TGIncrementNoteSemitoneAction.NAME);
		setMenuItemTextAndAccelerator(this.semitoneDown, "note.semitone-down", TGDecrementNoteSemitoneAction.NAME);
		setMenuItemTextAndAccelerator(this.shiftUp, "note.shift-up", TGShiftNoteUpAction.NAME);
		setMenuItemTextAndAccelerator(this.shiftDown, "note.shift-down", TGShiftNoteDownAction.NAME);
		setMenuItemTextAndAccelerator(this.insertText, "text.insert", TGOpenTextDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.moveBeatsLeft, "beat.move-left", TGMoveBeatsLeftAction.NAME);
		setMenuItemTextAndAccelerator(this.moveBeatsRight, "beat.move-right", TGMoveBeatsRightAction.NAME);
		setMenuItemTextAndAccelerator(this.moveBeatsCustom, "beat.move-custom", TGOpenBeatMoveDialogAction.NAME);

		this.durationMenuItem.loadProperties();
		this.chordMenuItem.loadProperties();
		this.effectMenuItem.loadProperties();
		this.dynamicMenuItem.loadProperties();
	}

	public void loadIcons(){
		this.insertText.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TEXT));
		this.strokeUp.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.STROKE_UP));
		this.strokeDown.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.STROKE_DOWN));
		this.pickStrokeUp.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.PICK_STROKE_UP));
		this.pickStrokeDown.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.PICK_STROKE_DOWN));
		this.altEnharmonic.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.ALTERNATIVE_ENHARMONIC));
	}
}
