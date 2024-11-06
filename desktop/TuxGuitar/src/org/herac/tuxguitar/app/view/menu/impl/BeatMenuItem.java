package org.herac.tuxguitar.app.view.menu.impl;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenTextDialogAction;
import org.herac.tuxguitar.app.action.impl.note.TGOpenBeatMoveDialogAction;
import org.herac.tuxguitar.app.action.impl.note.TGOpenStrokeDownDialogAction;
import org.herac.tuxguitar.app.action.impl.note.TGOpenStrokeUpDialogAction;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.app.view.component.tab.Tablature;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.editor.action.note.TGInsertRestBeatAction;
import org.herac.tuxguitar.editor.action.note.TGDeleteNoteOrRestAction;
import org.herac.tuxguitar.editor.action.note.TGCleanBeatAction;
import org.herac.tuxguitar.editor.action.note.TGRemoveUnusedVoiceAction;
import org.herac.tuxguitar.editor.action.note.TGSetVoiceAutoAction;
import org.herac.tuxguitar.editor.action.note.TGSetVoiceUpAction;
import org.herac.tuxguitar.editor.action.note.TGSetVoiceDownAction;
import org.herac.tuxguitar.editor.action.note.TGIncrementNoteSemitoneAction;
import org.herac.tuxguitar.editor.action.note.TGDecrementNoteSemitoneAction;
import org.herac.tuxguitar.editor.action.note.TGShiftNoteUpAction;
import org.herac.tuxguitar.graphics.control.TGTrackImpl;
import org.herac.tuxguitar.editor.action.note.TGShiftNoteDownAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsLeftAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsRightAction;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGStroke;
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.menu.UIMenuCheckableItem;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;
import org.herac.tuxguitar.util.TGNoteRange;

public class BeatMenuItem extends TGMenuItem {

	private UIMenuSubMenuItem noteMenuItem;
	private UIMenuActionItem insertRestBeat;
	private UIMenuActionItem deleteNoteOrRest;
	private UIMenuActionItem cleanBeat;
	private UIMenuActionItem removeVoice;
	private UIMenuActionItem insertText;
	private UIMenuActionItem voiceAuto;
	private UIMenuActionItem voiceUp;
	private UIMenuActionItem voiceDown;
	private UIMenuCheckableItem strokeUp;
	private UIMenuCheckableItem strokeDown;
	private UIMenuActionItem shiftUp;
	private UIMenuActionItem shiftDown;
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
		//--INSERT REST BEAT--
		this.insertRestBeat = this.noteMenuItem.getMenu().createActionItem();
		this.insertRestBeat.addSelectionListener(this.createActionProcessor(TGInsertRestBeatAction.NAME));

		//--DELETE NOTE OR REST--
		this.deleteNoteOrRest = this.noteMenuItem.getMenu().createActionItem();
		this.deleteNoteOrRest.addSelectionListener(this.createActionProcessor(TGDeleteNoteOrRestAction.NAME));

		//--CLEAN BEAT--
		this.cleanBeat = this.noteMenuItem.getMenu().createActionItem();
		this.cleanBeat.addSelectionListener(this.createActionProcessor(TGCleanBeatAction.NAME));

		//--SEPARATOR--
		this.noteMenuItem.getMenu().createSeparator();

		//--REMOVE VOICE--
		this.removeVoice = this.noteMenuItem.getMenu().createActionItem();
		this.removeVoice.addSelectionListener(this.createActionProcessor(TGRemoveUnusedVoiceAction.NAME));

		//--SEPARATOR--
		this.noteMenuItem.getMenu().createSeparator();

		//--DURATION--
		this.durationMenuItem = new DurationMenuItem(this.noteMenuItem.getMenu().createSubMenuItem());
		this.durationMenuItem.showItems();

		//--DYNAMIC--
		this.dynamicMenuItem = new DynamicMenuItem(this.noteMenuItem.getMenu().createSubMenuItem());
		this.dynamicMenuItem.showItems();

		//--EFFECTS--
		this.effectMenuItem = new NoteEffectsMenuItem(this.noteMenuItem.getMenu().createSubMenuItem());
		this.effectMenuItem.showItems();

		//--CHORD--
		this.chordMenuItem = new ChordMenuItem(this.noteMenuItem.getMenu().createSubMenuItem());
		this.chordMenuItem.showItems();

		//--SEPARATOR--
		this.noteMenuItem.getMenu().createSeparator();

		this.insertText = this.noteMenuItem.getMenu().createActionItem();
		this.insertText.addSelectionListener(this.createActionProcessor(TGOpenTextDialogAction.NAME));

		//--SEPARATOR--
		this.noteMenuItem.getMenu().createSeparator();

		//--SEMITONE DOWN--
		this.voiceAuto = this.noteMenuItem.getMenu().createActionItem();
		this.voiceAuto.addSelectionListener(this.createActionProcessor(TGSetVoiceAutoAction.NAME));

		//--SEMITONE UP--
		this.voiceUp = this.noteMenuItem.getMenu().createActionItem();
		this.voiceUp.addSelectionListener(this.createActionProcessor(TGSetVoiceUpAction.NAME));

		//--SEMITONE DOWN--
		this.voiceDown = this.noteMenuItem.getMenu().createActionItem();
		this.voiceDown.addSelectionListener(this.createActionProcessor(TGSetVoiceDownAction.NAME));

		//--SEPARATOR--
		this.noteMenuItem.getMenu().createSeparator();

		//--SEMITONE UP--
		this.strokeUp = this.noteMenuItem.getMenu().createCheckItem();
		this.strokeUp.addSelectionListener(this.createActionProcessor(TGOpenStrokeUpDialogAction.NAME));

		//--Semitone DOWN--
		this.strokeDown = this.noteMenuItem.getMenu().createCheckItem();
		this.strokeDown.addSelectionListener(this.createActionProcessor(TGOpenStrokeDownDialogAction.NAME));

		//--SEPARATOR--
		this.noteMenuItem.getMenu().createSeparator();

		//--SEMITONE UP--
		this.semitoneUp = this.noteMenuItem.getMenu().createActionItem();
		this.semitoneUp.addSelectionListener(this.createActionProcessor(TGIncrementNoteSemitoneAction.NAME));

		//--SEMITONE DOWN--
		this.semitoneDown = this.noteMenuItem.getMenu().createActionItem();
		this.semitoneDown.addSelectionListener(this.createActionProcessor(TGDecrementNoteSemitoneAction.NAME));

		//--SEPARATOR--
		this.noteMenuItem.getMenu().createSeparator();

		//--SHIFT UP--
		this.shiftUp = this.noteMenuItem.getMenu().createActionItem();
		this.shiftUp.addSelectionListener(this.createActionProcessor(TGShiftNoteUpAction.NAME));

		//--SHIFT DOWN--
		this.shiftDown = this.noteMenuItem.getMenu().createActionItem();
		this.shiftDown.addSelectionListener(this.createActionProcessor(TGShiftNoteDownAction.NAME));

		//--SEPARATOR--
		this.noteMenuItem.getMenu().createSeparator();

		//--MOVE BEATS LEFT--
		this.moveBeatsLeft = this.noteMenuItem.getMenu().createActionItem();
		this.moveBeatsLeft.addSelectionListener(this.createActionProcessor(TGMoveBeatsLeftAction.NAME));

		//--MOVE BEATS RIGHT--
		this.moveBeatsRight = this.noteMenuItem.getMenu().createActionItem();
		this.moveBeatsRight.addSelectionListener(this.createActionProcessor(TGMoveBeatsRightAction.NAME));

		//--MOVE BEATS CUSTOM--
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

		this.insertRestBeat.setEnabled(!running);
		this.deleteNoteOrRest.setEnabled(!running);
		this.cleanBeat.setEnabled(!running);
		this.removeVoice.setEnabled(!running);
		this.voiceAuto.setEnabled(!running && !restBeat);
		this.voiceUp.setEnabled(!running && !restBeat);
		this.voiceDown.setEnabled(!running && !restBeat);
		this.strokeUp.setEnabled(!running && !restBeat && !track.isPercussion());
		this.strokeUp.setChecked( beat != null && beat.getStroke().getDirection() == TGStroke.STROKE_UP );
		this.strokeDown.setEnabled(!running && !restBeat && !track.isPercussion());
		this.strokeDown.setChecked( beat != null && beat.getStroke().getDirection() == TGStroke.STROKE_DOWN );
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
		setMenuItemTextAndAccelerator(this.removeVoice, "beat.voice.remove-unused", TGRemoveUnusedVoiceAction.NAME);
		setMenuItemTextAndAccelerator(this.insertRestBeat, "beat.insert-rest", TGInsertRestBeatAction.NAME);
		setMenuItemTextAndAccelerator(this.deleteNoteOrRest, "beat.delete-note-or-rest", TGDeleteNoteOrRestAction.NAME);
		setMenuItemTextAndAccelerator(this.voiceAuto, "beat.voice-auto", TGSetVoiceAutoAction.NAME);
		setMenuItemTextAndAccelerator(this.voiceUp, "beat.voice-up", TGSetVoiceUpAction.NAME);
		setMenuItemTextAndAccelerator(this.voiceDown, "beat.voice-down", TGSetVoiceDownAction.NAME);
		setMenuItemTextAndAccelerator(this.strokeUp, "beat.stroke-up", TGOpenStrokeUpDialogAction.NAME);
		setMenuItemTextAndAccelerator(this.strokeDown, "beat.stroke-down", TGOpenStrokeDownDialogAction.NAME);
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
		this.insertText.setImage(TuxGuitar.getInstance().getIconManager().getText());
		this.strokeUp.setImage(TuxGuitar.getInstance().getIconManager().getStrokeUp());
		this.strokeDown.setImage(TuxGuitar.getInstance().getIconManager().getStrokeDown());
	}
}
