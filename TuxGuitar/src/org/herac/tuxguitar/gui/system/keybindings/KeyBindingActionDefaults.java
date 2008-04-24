package org.herac.tuxguitar.gui.system.keybindings;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.gui.actions.composition.ChangeInfoAction;
import org.herac.tuxguitar.gui.actions.duration.ChangeDottedDurationAction;
import org.herac.tuxguitar.gui.actions.duration.ChangeTupletoDurationAction;
import org.herac.tuxguitar.gui.actions.edit.RedoAction;
import org.herac.tuxguitar.gui.actions.edit.UndoAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeBendNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeDeadNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeFadeInAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeGhostNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeGraceNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeHammerNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangePalmMuteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeSlideNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeVibratoNoteAction;
import org.herac.tuxguitar.gui.actions.file.ExitAction;
import org.herac.tuxguitar.gui.actions.file.NewFileAction;
import org.herac.tuxguitar.gui.actions.file.OpenFileAction;
import org.herac.tuxguitar.gui.actions.file.PrintAction;
import org.herac.tuxguitar.gui.actions.file.SaveAsFileAction;
import org.herac.tuxguitar.gui.actions.file.SaveFileAction;
import org.herac.tuxguitar.gui.actions.help.ShowDocAction;
import org.herac.tuxguitar.gui.actions.insert.InsertChordAction;
import org.herac.tuxguitar.gui.actions.insert.InsertTextAction;
import org.herac.tuxguitar.gui.actions.marker.AddMarkerAction;
import org.herac.tuxguitar.gui.actions.marker.GoNextMarkerAction;
import org.herac.tuxguitar.gui.actions.marker.GoPreviousMarkerAction;
import org.herac.tuxguitar.gui.actions.measure.CopyMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.GoFirstMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.GoLastMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.GoNextMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.GoPreviousMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.PasteMeasureAction;
import org.herac.tuxguitar.gui.actions.note.ChangeTiedNoteAction;
import org.herac.tuxguitar.gui.actions.note.CleanBeatAction;
import org.herac.tuxguitar.gui.actions.note.DecrementNoteSemitoneAction;
import org.herac.tuxguitar.gui.actions.note.IncrementNoteSemitoneAction;
import org.herac.tuxguitar.gui.actions.note.ShiftNoteDownAction;
import org.herac.tuxguitar.gui.actions.note.ShiftNoteUpAction;
import org.herac.tuxguitar.gui.actions.settings.EditConfigAction;
import org.herac.tuxguitar.gui.actions.tools.TGBrowserAction;
import org.herac.tuxguitar.gui.actions.track.AddTrackAction;
import org.herac.tuxguitar.gui.actions.track.GoFirstTrackAction;
import org.herac.tuxguitar.gui.actions.track.GoLastTrackAction;
import org.herac.tuxguitar.gui.actions.track.GoNextTrackAction;
import org.herac.tuxguitar.gui.actions.track.GoPreviousTrackAction;
import org.herac.tuxguitar.gui.actions.track.RemoveTrackAction;
import org.herac.tuxguitar.gui.actions.transport.TransportModeAction;
import org.herac.tuxguitar.gui.actions.transport.TransportPlayAction;
import org.herac.tuxguitar.gui.actions.view.ShowMixerAction;
import org.herac.tuxguitar.gui.actions.view.ShowTransportAction;

public class KeyBindingActionDefaults {
	
	public static List getDefaultKeyBindings(){
		List list = new ArrayList();
		
		//New File: 'Ctrl + N'
		list.add(new KeyBindingAction(NewFileAction.NAME,new KeyBinding(110,KeyBindingConstants.CONTROL)));
		
		//Open File: 'Ctrl + O'
		list.add(new KeyBindingAction(OpenFileAction.NAME,new KeyBinding(111,KeyBindingConstants.CONTROL)));
		
		//Save File: 'Ctrl + S'
		list.add(new KeyBindingAction(SaveFileAction.NAME,new KeyBinding(115,KeyBindingConstants.CONTROL)));
		
		//Save File As: 'F12'
		list.add(new KeyBindingAction(SaveAsFileAction.NAME,new KeyBinding(KeyBindingConstants.F12,0)));
		
		//Exit: 'Alt + F4'
		list.add(new KeyBindingAction(ExitAction.NAME,new KeyBinding(KeyBindingConstants.F4,KeyBindingConstants.ALT)));
		
		//Print: 'Ctrl + P'
		list.add(new KeyBindingAction(PrintAction.NAME,new KeyBinding(112,KeyBindingConstants.CONTROL)));
		
		//Undo: 'Ctrl + Z'
		list.add(new KeyBindingAction(UndoAction.NAME,new KeyBinding(122,KeyBindingConstants.CONTROL)));
		
		//Redo: 'Ctrl + Y'
		list.add(new KeyBindingAction(RedoAction.NAME,new KeyBinding(121,KeyBindingConstants.CONTROL)));
		
		//Copy Measure: 'Ctrl + C'
		list.add(new KeyBindingAction(CopyMeasureAction.NAME,new KeyBinding(99,KeyBindingConstants.CONTROL)));
		
		//Paste Measure: 'Ctrl + P'
		list.add(new KeyBindingAction(PasteMeasureAction.NAME,new KeyBinding(118,KeyBindingConstants.CONTROL)));
		
		//Song Properties: 'F5'
		list.add(new KeyBindingAction(ChangeInfoAction.NAME,new KeyBinding(KeyBindingConstants.F5,0)));
		
		//Go Next Measure: 'Ctrl + RIGHT'
		list.add(new KeyBindingAction(GoNextMeasureAction.NAME,new KeyBinding(KeyBindingConstants.RIGHT,KeyBindingConstants.CONTROL)));
		
		//Go Previous Measure: 'Ctrl + LEFT'
		list.add(new KeyBindingAction(GoPreviousMeasureAction.NAME,new KeyBinding(KeyBindingConstants.LEFT,KeyBindingConstants.CONTROL)));
		
		//Go First Measure: 'Ctrl + SHIFT + RIGHT'
		list.add(new KeyBindingAction(GoFirstMeasureAction.NAME,new KeyBinding(KeyBindingConstants.LEFT,KeyBindingConstants.CONTROL + KeyBindingConstants.SHIFT)));
		
		//Go Last Measure: 'Ctrl + SHIFT + RIGHT'
		list.add(new KeyBindingAction(GoLastMeasureAction.NAME,new KeyBinding(KeyBindingConstants.RIGHT,KeyBindingConstants.CONTROL + KeyBindingConstants.SHIFT)));
		
		//Add Track: 'Ctrl + SHIFT + INSERT'
		list.add(new KeyBindingAction(AddTrackAction.NAME,new KeyBinding(KeyBindingConstants.INSERT,KeyBindingConstants.CONTROL + KeyBindingConstants.SHIFT)));
		
		//Remove Track: 'Ctrl + SHIFT + DELETE'
		list.add(new KeyBindingAction(RemoveTrackAction.NAME,new KeyBinding(KeyBindingConstants.DELETE,KeyBindingConstants.CONTROL + KeyBindingConstants.SHIFT)));
		
		//Go Next Track: 'Ctrl + DOWN'
		list.add(new KeyBindingAction(GoNextTrackAction.NAME,new KeyBinding(KeyBindingConstants.DOWN,KeyBindingConstants.CONTROL)));
		
		//Go Previous Track: 'Ctrl + UP'
		list.add(new KeyBindingAction(GoPreviousTrackAction.NAME,new KeyBinding(KeyBindingConstants.UP,KeyBindingConstants.CONTROL)));
		
		//Go First Track: 'Ctrl + UP'
		list.add(new KeyBindingAction(GoFirstTrackAction.NAME,new KeyBinding(KeyBindingConstants.UP,KeyBindingConstants.CONTROL | KeyBindingConstants.SHIFT)));
		
		//Go Last Track: 'Ctrl + DOWN'
		list.add(new KeyBindingAction(GoLastTrackAction.NAME,new KeyBinding(KeyBindingConstants.DOWN,KeyBindingConstants.CONTROL | KeyBindingConstants.SHIFT)));
		
		//Add Marker: 'SHIFT + INSERT'
		list.add(new KeyBindingAction(AddMarkerAction.NAME,new KeyBinding(KeyBindingConstants.INSERT,KeyBindingConstants.SHIFT)));
		
		//Go Next Marker: 'Alt + RIGHT'
		list.add(new KeyBindingAction(GoNextMarkerAction.NAME,new KeyBinding(KeyBindingConstants.RIGHT,KeyBindingConstants.ALT)));
		
		//Go Previous Marker: 'Alt + LEFT'
		list.add(new KeyBindingAction(GoPreviousMarkerAction.NAME,new KeyBinding(KeyBindingConstants.LEFT,KeyBindingConstants.ALT)));
		
		//Play-Pause: 'Space'
		list.add(new KeyBindingAction(TransportPlayAction.NAME,new KeyBinding(KeyBindingConstants.SPACE,0)));
		
		//Play Mode: 'F9'
		list.add(new KeyBindingAction(TransportModeAction.NAME,new KeyBinding(KeyBindingConstants.F9,0)));
		
		//Clean Beat: 'Ctrl + DELETE'
		list.add(new KeyBindingAction(CleanBeatAction.NAME,new KeyBinding(KeyBindingConstants.DELETE,KeyBindingConstants.CONTROL)));
		
		//Shift Up:  'SHIFT + UP'
		list.add(new KeyBindingAction(ShiftNoteUpAction.NAME,new KeyBinding(KeyBindingConstants.UP,KeyBindingConstants.SHIFT)));
		
		//Shift Up:  'SHIFT + DOWN'
		list.add(new KeyBindingAction(ShiftNoteDownAction.NAME,new KeyBinding(KeyBindingConstants.DOWN,KeyBindingConstants.SHIFT)));
		
		//Increment semitone: 'SHIFT + RIGHT'
		list.add(new KeyBindingAction(IncrementNoteSemitoneAction.NAME,new KeyBinding(KeyBindingConstants.RIGHT,KeyBindingConstants.SHIFT)));
		
		//Decrement semitone: 'SHIFT + LEFT'
		list.add(new KeyBindingAction(DecrementNoteSemitoneAction.NAME,new KeyBinding(KeyBindingConstants.LEFT,KeyBindingConstants.SHIFT)));
		
		//Dotted Duration: '*'
		list.add(new KeyBindingAction(ChangeDottedDurationAction.NAME,new KeyBinding(KeyBindingConstants.KEYPAD_MULTIPLY,0)));
		
		//Tupleto Duration: '/'
		list.add(new KeyBindingAction(ChangeTupletoDurationAction.NAME,new KeyBinding(KeyBindingConstants.KEYPAD_DIVIDE,0)));
		
		//Tied note: 'L'
		list.add(new KeyBindingAction(ChangeTiedNoteAction.NAME,new KeyBinding(108,0)));
		
		//Insert Chord: 'A'
		list.add(new KeyBindingAction(InsertChordAction.NAME,new KeyBinding(97,0)));
		
		//Insert Text: 'T'
		list.add(new KeyBindingAction(InsertTextAction.NAME,new KeyBinding(116,0)));
		
		//Bend: 'B'
		list.add(new KeyBindingAction(ChangeBendNoteAction.NAME,new KeyBinding(98,0)));
		
		//Ghost: 'O'
		list.add(new KeyBindingAction(ChangeGhostNoteAction.NAME,new KeyBinding(111,0)));
		
		//Hammer on/Pull off: 'H'
		list.add(new KeyBindingAction(ChangeHammerNoteAction.NAME,new KeyBinding(104,0)));
		
		//Slide up/down: 'S'
		list.add(new KeyBindingAction(ChangeSlideNoteAction.NAME,new KeyBinding(115,0)));
		
		//Vibrato: 'V'
		list.add(new KeyBindingAction(ChangeVibratoNoteAction.NAME,new KeyBinding(118,0)));
		
		//Dead note: 'X'
		list.add(new KeyBindingAction(ChangeDeadNoteAction.NAME,new KeyBinding(120,0)));
		
		//Palm mute: 'P'
		list.add(new KeyBindingAction(ChangePalmMuteAction.NAME,new KeyBinding(112,0)));
		
		//Grace note: 'G'
		list.add(new KeyBindingAction(ChangeGraceNoteAction.NAME,new KeyBinding(103,0)));
		
		//Fade in: 'F'
		list.add(new KeyBindingAction(ChangeFadeInAction.NAME,new KeyBinding(102,0)));
		
		//Browser: 'Ctrl + B'
		list.add(new KeyBindingAction(TGBrowserAction.NAME,new KeyBinding(98,KeyBindingConstants.CONTROL)));
		
		//Show Mixer: 'Ctrl + M'
		list.add(new KeyBindingAction(ShowMixerAction.NAME,new KeyBinding(109,KeyBindingConstants.CONTROL)));
		
		//Show Transport: 'Ctrl + T'
		list.add(new KeyBindingAction(ShowTransportAction.NAME,new KeyBinding(116,KeyBindingConstants.CONTROL)));
		
		//Show Settings: 'F1'
		list.add(new KeyBindingAction(EditConfigAction.NAME,new KeyBinding(KeyBindingConstants.F7,0)));
		
		//Show Help: 'F1'
		list.add(new KeyBindingAction(ShowDocAction.NAME,new KeyBinding(KeyBindingConstants.F1,0)));
		
		return list;
	}
}
