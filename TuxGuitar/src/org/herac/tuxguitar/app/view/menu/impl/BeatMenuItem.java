package org.herac.tuxguitar.app.view.menu.impl;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenTextDialogAction;
import org.herac.tuxguitar.app.action.impl.note.TGOpenBeatMoveDialogAction;
import org.herac.tuxguitar.app.action.impl.note.TGOpenStrokeDownDialogAction;
import org.herac.tuxguitar.app.action.impl.note.TGOpenStrokeUpDialogAction;
import org.herac.tuxguitar.app.view.component.tab.Caret;
import org.herac.tuxguitar.app.view.menu.TGMenuItem;
import org.herac.tuxguitar.editor.action.note.TGChangeTiedNoteAction;
import org.herac.tuxguitar.editor.action.note.TGCleanBeatAction;
import org.herac.tuxguitar.editor.action.note.TGDecrementNoteSemitoneAction;
import org.herac.tuxguitar.editor.action.note.TGDeleteNoteOrRestAction;
import org.herac.tuxguitar.editor.action.note.TGIncrementNoteSemitoneAction;
import org.herac.tuxguitar.editor.action.note.TGInsertRestBeatAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsLeftAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsRightAction;
import org.herac.tuxguitar.editor.action.note.TGRemoveUnusedVoiceAction;
import org.herac.tuxguitar.editor.action.note.TGSetVoiceAutoAction;
import org.herac.tuxguitar.editor.action.note.TGSetVoiceDownAction;
import org.herac.tuxguitar.editor.action.note.TGSetVoiceUpAction;
import org.herac.tuxguitar.editor.action.note.TGShiftNoteDownAction;
import org.herac.tuxguitar.editor.action.note.TGShiftNoteUpAction;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGStroke;
import org.herac.tuxguitar.ui.menu.UIMenu;
import org.herac.tuxguitar.ui.menu.UIMenuActionItem;
import org.herac.tuxguitar.ui.menu.UIMenuCheckableItem;
import org.herac.tuxguitar.ui.menu.UIMenuSubMenuItem;

public class BeatMenuItem extends TGMenuItem {
	
	private UIMenuSubMenuItem noteMenuItem;
	private UIMenuCheckableItem tiedNote;
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
	private ChordMenuItem chordMenuItem;
	private NoteEffectsMenuItem effectMenuItem;
	private DynamicMenuItem dynamicMenuItem;
	
	public BeatMenuItem(UIMenu parent) {
		this.noteMenuItem = parent.createSubMenuItem();
	}
	
	public void showItems(){
		//--Tied Note
		this.tiedNote = this.noteMenuItem.getMenu().createCheckItem();
		this.tiedNote.addSelectionListener(this.createActionProcessor(TGChangeTiedNoteAction.NAME));
		
		//--Insert Rest Beat
		this.insertRestBeat = this.noteMenuItem.getMenu().createActionItem();
		this.insertRestBeat.addSelectionListener(this.createActionProcessor(TGInsertRestBeatAction.NAME));
		
		//--Delete Note or Rest
		this.deleteNoteOrRest = this.noteMenuItem.getMenu().createActionItem();
		this.deleteNoteOrRest.addSelectionListener(this.createActionProcessor(TGDeleteNoteOrRestAction.NAME));
		
		//--Clean Beat
		this.cleanBeat = this.noteMenuItem.getMenu().createActionItem();
		this.cleanBeat.addSelectionListener(this.createActionProcessor(TGCleanBeatAction.NAME));
		
		//--Remove Voice
		this.removeVoice = this.noteMenuItem.getMenu().createActionItem();
		this.removeVoice.addSelectionListener(this.createActionProcessor(TGRemoveUnusedVoiceAction.NAME));
		
		//--Duration--
		this.durationMenuItem = new DurationMenuItem(this.noteMenuItem.getMenu().createSubMenuItem());
		this.durationMenuItem.showItems();
		
		//--Chord--
		this.chordMenuItem = new ChordMenuItem(this.noteMenuItem.getMenu().createSubMenuItem());
		this.chordMenuItem.showItems();
		
		//--Effects--
		this.effectMenuItem = new NoteEffectsMenuItem(this.noteMenuItem.getMenu().createSubMenuItem());
		this.effectMenuItem.showItems();
		
		//--Dynamic--
		this.dynamicMenuItem = new DynamicMenuItem(this.noteMenuItem.getMenu().createSubMenuItem());
		this.dynamicMenuItem.showItems();
		
		//--SEPARATOR--
		this.noteMenuItem.getMenu().createSeparator();
		
		this.insertText = this.noteMenuItem.getMenu().createActionItem();
		this.insertText.addSelectionListener(this.createActionProcessor(TGOpenTextDialogAction.NAME));
		
		//--SEPARATOR--
		this.noteMenuItem.getMenu().createSeparator();
		
		//--Semitone Down
		this.voiceAuto = this.noteMenuItem.getMenu().createActionItem();
		this.voiceAuto.addSelectionListener(this.createActionProcessor(TGSetVoiceAutoAction.NAME));
		
		//--Semitone Up
		this.voiceUp = this.noteMenuItem.getMenu().createActionItem();
		this.voiceUp.addSelectionListener(this.createActionProcessor(TGSetVoiceUpAction.NAME));
		
		//--Semitone Down
		this.voiceDown = this.noteMenuItem.getMenu().createActionItem();
		this.voiceDown.addSelectionListener(this.createActionProcessor(TGSetVoiceDownAction.NAME));
		
		//--SEPARATOR--
		this.noteMenuItem.getMenu().createSeparator();
		
		//--Semitone Up
		this.strokeUp = this.noteMenuItem.getMenu().createCheckItem();
		this.strokeUp.addSelectionListener(this.createActionProcessor(TGOpenStrokeUpDialogAction.NAME));
		
		//--Semitone Down
		this.strokeDown = this.noteMenuItem.getMenu().createCheckItem();
		this.strokeDown.addSelectionListener(this.createActionProcessor(TGOpenStrokeDownDialogAction.NAME));
				
		//--SEPARATOR--
		this.noteMenuItem.getMenu().createSeparator();
		
		//--Semitone Up
		this.semitoneUp = this.noteMenuItem.getMenu().createActionItem();
		this.semitoneUp.addSelectionListener(this.createActionProcessor(TGIncrementNoteSemitoneAction.NAME));
		
		//--Semitone Down
		this.semitoneDown = this.noteMenuItem.getMenu().createActionItem();
		this.semitoneDown.addSelectionListener(this.createActionProcessor(TGDecrementNoteSemitoneAction.NAME));
		
		//--SEPARATOR--
		this.noteMenuItem.getMenu().createSeparator();
		
		//--Shift Up
		this.shiftUp = this.noteMenuItem.getMenu().createActionItem();
		this.shiftUp.addSelectionListener(this.createActionProcessor(TGShiftNoteUpAction.NAME));
		
		//--Shift Down
		this.shiftDown = this.noteMenuItem.getMenu().createActionItem();
		this.shiftDown.addSelectionListener(this.createActionProcessor(TGShiftNoteDownAction.NAME));
		
		//--SEPARATOR--
		this.noteMenuItem.getMenu().createSeparator();
		
		//--Move Beats Left
		this.moveBeatsLeft = this.noteMenuItem.getMenu().createActionItem();
		this.moveBeatsLeft.addSelectionListener(this.createActionProcessor(TGMoveBeatsLeftAction.NAME));
		
		//--Move Beats Right
		this.moveBeatsRight = this.noteMenuItem.getMenu().createActionItem();
		this.moveBeatsRight.addSelectionListener(this.createActionProcessor(TGMoveBeatsRightAction.NAME));
		
		//--Move Beats Custom
		this.moveBeatsCustom = this.noteMenuItem.getMenu().createActionItem();
		this.moveBeatsCustom.addSelectionListener(this.createActionProcessor(TGOpenBeatMoveDialogAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void update(){
		Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
		TGBeat beat = caret.getSelectedBeat();
		TGNote note = caret.getSelectedNote();
		boolean restBeat = caret.isRestBeatSelected();
		boolean running = TuxGuitar.getInstance().getPlayer().isRunning();
		this.tiedNote.setEnabled(!running);
		this.tiedNote.setChecked(note != null && note.isTiedNote());
		this.insertRestBeat.setEnabled(!running);
		this.deleteNoteOrRest.setEnabled(!running);
		this.cleanBeat.setEnabled(!running);
		this.removeVoice.setEnabled(!running);
		this.voiceAuto.setEnabled(!running && !restBeat);
		this.voiceUp.setEnabled(!running && !restBeat);
		this.voiceDown.setEnabled(!running && !restBeat);
		this.strokeUp.setEnabled(!running && !restBeat);
		this.strokeUp.setChecked( beat != null && beat.getStroke().getDirection() == TGStroke.STROKE_UP );
		this.strokeDown.setEnabled(!running && !restBeat);
		this.strokeDown.setChecked( beat != null && beat.getStroke().getDirection() == TGStroke.STROKE_DOWN );
		this.semitoneUp.setEnabled(!running && note != null);
		this.semitoneDown.setEnabled(!running && note != null);
		this.shiftUp.setEnabled(!running && note != null);
		this.shiftDown.setEnabled(!running && note != null);
		this.insertText.setEnabled(!running);
		this.moveBeatsLeft.setEnabled(!running);
		this.moveBeatsRight.setEnabled(!running);
		this.moveBeatsCustom.setEnabled(!running);
		this.durationMenuItem.update();
		this.chordMenuItem.update();
		this.effectMenuItem.update();
		this.dynamicMenuItem.update();
	}
	
	public void loadProperties(){
		setMenuItemTextAndAccelerator(this.noteMenuItem, "beat", null);
		setMenuItemTextAndAccelerator(this.cleanBeat, "beat.clean", TGCleanBeatAction.NAME);
		setMenuItemTextAndAccelerator(this.removeVoice, "beat.voice.remove-unused", TGRemoveUnusedVoiceAction.NAME);
		setMenuItemTextAndAccelerator(this.tiedNote, "note.tiednote", TGChangeTiedNoteAction.NAME);
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
		this.tiedNote.setImage(TuxGuitar.getInstance().getIconManager().getNoteTied());
	}
}
