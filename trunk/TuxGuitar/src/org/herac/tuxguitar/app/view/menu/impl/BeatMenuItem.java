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

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BeatMenuItem extends TGMenuItem{
	
	private MenuItem noteMenuItem;
	private Menu menu;
	private MenuItem tiedNote;
	private MenuItem insertRestBeat;
	private MenuItem deleteNoteOrRest;
	private MenuItem cleanBeat;
	private MenuItem removeVoice;
	private MenuItem insertText;
	private MenuItem voiceAuto;
	private MenuItem voiceUp;
	private MenuItem voiceDown;
	private MenuItem strokeUp;
	private MenuItem strokeDown;
	private MenuItem shiftUp;
	private MenuItem shiftDown;
	private MenuItem semitoneUp;
	private MenuItem semitoneDown;
	private MenuItem moveBeatsLeft;
	private MenuItem moveBeatsRight;
	private MenuItem moveBeatsCustom;
	private DurationMenuItem durationMenuItem;
	private ChordMenuItem chordMenuItem;
	private NoteEffectsMenuItem effectMenuItem;
	private DynamicMenuItem dynamicMenuItem;
	
	public BeatMenuItem(Shell shell,Menu parent, int style) {
		this.noteMenuItem = new MenuItem(parent, style);
		this.menu = new Menu(shell, SWT.DROP_DOWN);
	}
	
	public void showItems(){
		//--Tied Note
		this.tiedNote = new MenuItem(this.menu, SWT.CHECK);
		this.tiedNote.addSelectionListener(this.createActionProcessor(TGChangeTiedNoteAction.NAME));
		
		//--Insert Rest Beat
		this.insertRestBeat = new MenuItem(this.menu, SWT.PUSH);
		this.insertRestBeat.addSelectionListener(this.createActionProcessor(TGInsertRestBeatAction.NAME));
		
		//--Delete Note or Rest
		this.deleteNoteOrRest = new MenuItem(this.menu, SWT.PUSH);
		this.deleteNoteOrRest.addSelectionListener(this.createActionProcessor(TGDeleteNoteOrRestAction.NAME));
		
		//--Clean Beat
		this.cleanBeat = new MenuItem(this.menu, SWT.PUSH);
		this.cleanBeat.addSelectionListener(this.createActionProcessor(TGCleanBeatAction.NAME));
		
		//--Remove Voice
		this.removeVoice = new MenuItem(this.menu, SWT.PUSH);
		this.removeVoice.addSelectionListener(this.createActionProcessor(TGRemoveUnusedVoiceAction.NAME));
		
		//--Duration--
		this.durationMenuItem = new DurationMenuItem(this.menu.getShell(),this.menu,SWT.CASCADE);
		this.durationMenuItem.showItems();
		
		//--Chord--
		this.chordMenuItem = new ChordMenuItem(this.menu.getShell(),this.menu,SWT.CASCADE);
		this.chordMenuItem.showItems();
		
		//--Effects--
		this.effectMenuItem = new NoteEffectsMenuItem(this.menu.getShell(),this.menu,SWT.CASCADE);
		this.effectMenuItem.showItems();
		
		//--Dynamic--
		this.dynamicMenuItem = new DynamicMenuItem(this.menu.getShell(),this.menu,SWT.CASCADE);
		this.dynamicMenuItem.showItems();
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		this.insertText = new MenuItem(this.menu, SWT.PUSH);
		this.insertText.addSelectionListener(this.createActionProcessor(TGOpenTextDialogAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--Semitone Down
		this.voiceAuto = new MenuItem(this.menu, SWT.PUSH);
		this.voiceAuto.addSelectionListener(this.createActionProcessor(TGSetVoiceAutoAction.NAME));
		
		//--Semitone Up
		this.voiceUp = new MenuItem(this.menu, SWT.PUSH);
		this.voiceUp.addSelectionListener(this.createActionProcessor(TGSetVoiceUpAction.NAME));
		
		//--Semitone Down
		this.voiceDown = new MenuItem(this.menu, SWT.PUSH);
		this.voiceDown.addSelectionListener(this.createActionProcessor(TGSetVoiceDownAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--Semitone Up
		this.strokeUp = new MenuItem(this.menu, SWT.CHECK);
		this.strokeUp.addSelectionListener(this.createActionProcessor(TGOpenStrokeUpDialogAction.NAME));
		
		//--Semitone Down
		this.strokeDown = new MenuItem(this.menu, SWT.CHECK);
		this.strokeDown.addSelectionListener(this.createActionProcessor(TGOpenStrokeDownDialogAction.NAME));
				
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--Semitone Up
		this.semitoneUp = new MenuItem(this.menu, SWT.PUSH);
		this.semitoneUp.addSelectionListener(this.createActionProcessor(TGIncrementNoteSemitoneAction.NAME));
		
		//--Semitone Down
		this.semitoneDown = new MenuItem(this.menu, SWT.PUSH);
		this.semitoneDown.addSelectionListener(this.createActionProcessor(TGDecrementNoteSemitoneAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--Shift Up
		this.shiftUp = new MenuItem(this.menu, SWT.PUSH);
		this.shiftUp.addSelectionListener(this.createActionProcessor(TGShiftNoteUpAction.NAME));
		
		//--Shift Down
		this.shiftDown = new MenuItem(this.menu, SWT.PUSH);
		this.shiftDown.addSelectionListener(this.createActionProcessor(TGShiftNoteDownAction.NAME));
		
		//--SEPARATOR--
		new MenuItem(this.menu, SWT.SEPARATOR);
		
		//--Move Beats Left
		this.moveBeatsLeft = new MenuItem(this.menu, SWT.PUSH);
		this.moveBeatsLeft.addSelectionListener(this.createActionProcessor(TGMoveBeatsLeftAction.NAME));
		
		//--Move Beats Right
		this.moveBeatsRight = new MenuItem(this.menu, SWT.PUSH);
		this.moveBeatsRight.addSelectionListener(this.createActionProcessor(TGMoveBeatsRightAction.NAME));
		
		//--Move Beats Custom
		this.moveBeatsCustom = new MenuItem(this.menu, SWT.PUSH);
		this.moveBeatsCustom.addSelectionListener(this.createActionProcessor(TGOpenBeatMoveDialogAction.NAME));
		
		this.noteMenuItem.setMenu(this.menu);
		
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
		this.tiedNote.setSelection(note != null && note.isTiedNote());
		this.insertRestBeat.setEnabled(!running);
		this.deleteNoteOrRest.setEnabled(!running);
		this.cleanBeat.setEnabled(!running);
		this.removeVoice.setEnabled(!running);
		this.voiceAuto.setEnabled(!running && !restBeat);
		this.voiceUp.setEnabled(!running && !restBeat);
		this.voiceDown.setEnabled(!running && !restBeat);
		this.strokeUp.setEnabled(!running && !restBeat);
		this.strokeUp.setSelection( beat != null && beat.getStroke().getDirection() == TGStroke.STROKE_UP );
		this.strokeDown.setEnabled(!running && !restBeat);
		this.strokeDown.setSelection( beat != null && beat.getStroke().getDirection() == TGStroke.STROKE_DOWN );
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
