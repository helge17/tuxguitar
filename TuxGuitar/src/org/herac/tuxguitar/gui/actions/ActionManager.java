/*
 * Created on 18-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.gui.actions.caret.GoDownAction;
import org.herac.tuxguitar.gui.actions.caret.GoLeftAction;
import org.herac.tuxguitar.gui.actions.caret.GoRightAction;
import org.herac.tuxguitar.gui.actions.caret.GoUpAction;
import org.herac.tuxguitar.gui.actions.composition.ChangeClefAction;
import org.herac.tuxguitar.gui.actions.composition.ChangeInfoAction;
import org.herac.tuxguitar.gui.actions.composition.ChangeKeySignatureAction;
import org.herac.tuxguitar.gui.actions.composition.ChangeTempoAction;
import org.herac.tuxguitar.gui.actions.composition.ChangeTimeSignatureAction;
import org.herac.tuxguitar.gui.actions.composition.ChangeTripletFeelAction;
import org.herac.tuxguitar.gui.actions.duration.ChangeDottedDurationAction;
import org.herac.tuxguitar.gui.actions.duration.ChangeDoubleDottedDurationAction;
import org.herac.tuxguitar.gui.actions.duration.ChangeDivisionTypeAction;
import org.herac.tuxguitar.gui.actions.duration.DecrementDurationAction;
import org.herac.tuxguitar.gui.actions.duration.IncrementDurationAction;
import org.herac.tuxguitar.gui.actions.duration.SetEighthDurationAction;
import org.herac.tuxguitar.gui.actions.duration.SetHalfDurationAction;
import org.herac.tuxguitar.gui.actions.duration.SetQuarterDurationAction;
import org.herac.tuxguitar.gui.actions.duration.SetSixteenthDurationAction;
import org.herac.tuxguitar.gui.actions.duration.SetSixtyFourthDurationAction;
import org.herac.tuxguitar.gui.actions.duration.SetThirtySecondDurationAction;
import org.herac.tuxguitar.gui.actions.duration.SetWholeDurationAction;
import org.herac.tuxguitar.gui.actions.edit.RedoAction;
import org.herac.tuxguitar.gui.actions.edit.SetMouseModeEditionAction;
import org.herac.tuxguitar.gui.actions.edit.SetMouseModeSelectionAction;
import org.herac.tuxguitar.gui.actions.edit.SetNaturalKeyAction;
import org.herac.tuxguitar.gui.actions.edit.SetVoice1Action;
import org.herac.tuxguitar.gui.actions.edit.SetVoice2Action;
import org.herac.tuxguitar.gui.actions.edit.UndoAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeAccentuatedNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeBendNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeDeadNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeFadeInAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeGhostNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeGraceNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeHammerNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeHarmonicNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeHeavyAccentuatedNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangePalmMuteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangePoppingAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeSlappingAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeSlideNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeStaccatoAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeTappingAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeTremoloBarAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeTremoloPickingAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeTrillNoteAction;
import org.herac.tuxguitar.gui.actions.effects.ChangeVibratoNoteAction;
import org.herac.tuxguitar.gui.actions.file.ExitAction;
import org.herac.tuxguitar.gui.actions.file.ExportSongAction;
import org.herac.tuxguitar.gui.actions.file.ImportSongAction;
import org.herac.tuxguitar.gui.actions.file.NewFileAction;
import org.herac.tuxguitar.gui.actions.file.OpenFileAction;
import org.herac.tuxguitar.gui.actions.file.OpenURLAction;
import org.herac.tuxguitar.gui.actions.file.PrintAction;
import org.herac.tuxguitar.gui.actions.file.PrintPreviewAction;
import org.herac.tuxguitar.gui.actions.file.SaveAsFileAction;
import org.herac.tuxguitar.gui.actions.file.SaveFileAction;
import org.herac.tuxguitar.gui.actions.help.ShowAboutAction;
import org.herac.tuxguitar.gui.actions.help.ShowDocAction;
import org.herac.tuxguitar.gui.actions.insert.InsertChordAction;
import org.herac.tuxguitar.gui.actions.insert.InsertTextAction;
import org.herac.tuxguitar.gui.actions.insert.RepeatAlternativeAction;
import org.herac.tuxguitar.gui.actions.insert.RepeatCloseAction;
import org.herac.tuxguitar.gui.actions.insert.RepeatOpenAction;
import org.herac.tuxguitar.gui.actions.layout.SetChordDiagramEnabledAction;
import org.herac.tuxguitar.gui.actions.layout.SetChordNameEnabledAction;
import org.herac.tuxguitar.gui.actions.layout.SetCompactViewAction;
import org.herac.tuxguitar.gui.actions.layout.SetLinearLayoutAction;
import org.herac.tuxguitar.gui.actions.layout.SetMultitrackViewAction;
import org.herac.tuxguitar.gui.actions.layout.SetPageLayoutAction;
import org.herac.tuxguitar.gui.actions.layout.SetScoreEnabledAction;
import org.herac.tuxguitar.gui.actions.layout.SetTablatureEnabledAction;
import org.herac.tuxguitar.gui.actions.marker.AddMarkerAction;
import org.herac.tuxguitar.gui.actions.marker.GoFirstMarkerAction;
import org.herac.tuxguitar.gui.actions.marker.GoLastMarkerAction;
import org.herac.tuxguitar.gui.actions.marker.GoNextMarkerAction;
import org.herac.tuxguitar.gui.actions.marker.GoPreviousMarkerAction;
import org.herac.tuxguitar.gui.actions.marker.ListMarkersAction;
import org.herac.tuxguitar.gui.actions.measure.AddMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.CleanMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.CopyMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.GoFirstMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.GoLastMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.GoNextMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.GoPreviousMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.PasteMeasureAction;
import org.herac.tuxguitar.gui.actions.measure.RemoveMeasureAction;
import org.herac.tuxguitar.gui.actions.note.ChangeNoteAction;
import org.herac.tuxguitar.gui.actions.note.ChangeTiedNoteAction;
import org.herac.tuxguitar.gui.actions.note.ChangeVelocityAction;
import org.herac.tuxguitar.gui.actions.note.CleanBeatAction;
import org.herac.tuxguitar.gui.actions.note.DecrementNoteSemitoneAction;
import org.herac.tuxguitar.gui.actions.note.IncrementNoteSemitoneAction;
import org.herac.tuxguitar.gui.actions.note.InsertNoteAction;
import org.herac.tuxguitar.gui.actions.note.MoveBeatsCustomAction;
import org.herac.tuxguitar.gui.actions.note.MoveBeatsLeftAction;
import org.herac.tuxguitar.gui.actions.note.MoveBeatsRightAction;
import org.herac.tuxguitar.gui.actions.note.RemoveNoteAction;
import org.herac.tuxguitar.gui.actions.note.RemoveUnusedVoiceAction;
import org.herac.tuxguitar.gui.actions.note.SetStrokeDownAction;
import org.herac.tuxguitar.gui.actions.note.SetStrokeUpAction;
import org.herac.tuxguitar.gui.actions.note.SetVoiceAutoAction;
import org.herac.tuxguitar.gui.actions.note.SetVoiceDownAction;
import org.herac.tuxguitar.gui.actions.note.SetVoiceUpAction;
import org.herac.tuxguitar.gui.actions.note.ShiftNoteDownAction;
import org.herac.tuxguitar.gui.actions.note.ShiftNoteUpAction;
import org.herac.tuxguitar.gui.actions.settings.EditConfigAction;
import org.herac.tuxguitar.gui.actions.settings.EditKeyBindingsAction;
import org.herac.tuxguitar.gui.actions.settings.EditPluginsAction;
import org.herac.tuxguitar.gui.actions.system.DisposeAction;
import org.herac.tuxguitar.gui.actions.tools.ScaleAction;
import org.herac.tuxguitar.gui.actions.tools.TGBrowserAction;
import org.herac.tuxguitar.gui.actions.tools.TransposeAction;
import org.herac.tuxguitar.gui.actions.track.AddTrackAction;
import org.herac.tuxguitar.gui.actions.track.CloneTrackAction;
import org.herac.tuxguitar.gui.actions.track.EditLyricsAction;
import org.herac.tuxguitar.gui.actions.track.GoFirstTrackAction;
import org.herac.tuxguitar.gui.actions.track.GoLastTrackAction;
import org.herac.tuxguitar.gui.actions.track.GoNextTrackAction;
import org.herac.tuxguitar.gui.actions.track.GoPreviousTrackAction;
import org.herac.tuxguitar.gui.actions.track.GoToTrackAction;
import org.herac.tuxguitar.gui.actions.track.MoveTrackDownAction;
import org.herac.tuxguitar.gui.actions.track.MoveTrackUpAction;
import org.herac.tuxguitar.gui.actions.track.RemoveTrackAction;
import org.herac.tuxguitar.gui.actions.track.TrackPropertiesAction;
import org.herac.tuxguitar.gui.actions.transport.TransportMetronomeAction;
import org.herac.tuxguitar.gui.actions.transport.TransportModeAction;
import org.herac.tuxguitar.gui.actions.transport.TransportPlayAction;
import org.herac.tuxguitar.gui.actions.transport.TransportSetLoopEHeaderAction;
import org.herac.tuxguitar.gui.actions.transport.TransportSetLoopSHeaderAction;
import org.herac.tuxguitar.gui.actions.transport.TransportStopAction;
import org.herac.tuxguitar.gui.actions.view.ShowFretBoardAction;
import org.herac.tuxguitar.gui.actions.view.ShowMatrixAction;
import org.herac.tuxguitar.gui.actions.view.ShowMixerAction;
import org.herac.tuxguitar.gui.actions.view.ShowPianoAction;
import org.herac.tuxguitar.gui.actions.view.ShowTransportAction;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ActionManager {
	
	private Map actions;
	
	public ActionManager(){
		this.actions = new HashMap();
		this.init();
	}
	
	public void init(){
		//file actions
		addAction(new NewFileAction());
		addAction(new OpenFileAction());
		addAction(new SaveFileAction());
		addAction(new SaveAsFileAction());
		addAction(new ImportSongAction());
		addAction(new ExportSongAction());
		addAction(new PrintAction());
		addAction(new PrintPreviewAction());
		addAction(new OpenURLAction());
		addAction(new ExitAction());
		
		//edit actions
		addAction(new UndoAction());
		addAction(new RedoAction());
		addAction(new SetMouseModeSelectionAction());
		addAction(new SetMouseModeEditionAction());
		addAction(new SetNaturalKeyAction());
		addAction(new SetVoice1Action());
		addAction(new SetVoice2Action());
		
		//layout actions
		addAction(new SetPageLayoutAction());
		addAction(new SetLinearLayoutAction());
		addAction(new SetMultitrackViewAction());
		addAction(new SetScoreEnabledAction());
		addAction(new SetTablatureEnabledAction());
		addAction(new SetCompactViewAction());
		addAction(new SetChordNameEnabledAction());
		addAction(new SetChordDiagramEnabledAction());
		
		//view actions
		addAction(new ShowFretBoardAction());
		addAction(new ShowPianoAction());
		addAction(new ShowMixerAction());
		addAction(new ShowTransportAction());
		addAction(new ShowMatrixAction());
		
		//composition actions
		addAction(new ChangeTimeSignatureAction());
		addAction(new ChangeTempoAction());
		addAction(new ChangeClefAction());
		addAction(new ChangeKeySignatureAction());
		addAction(new ChangeTripletFeelAction());
		addAction(new ChangeInfoAction());
		
		//track actions
		addAction(new AddTrackAction());
		addAction(new RemoveTrackAction());
		addAction(new CloneTrackAction());
		addAction(new GoFirstTrackAction());
		addAction(new GoLastTrackAction());
		addAction(new GoNextTrackAction());
		addAction(new GoToTrackAction());
		addAction(new GoPreviousTrackAction());
		addAction(new MoveTrackUpAction());
		addAction(new MoveTrackDownAction());
		addAction(new EditLyricsAction());
		addAction(new TrackPropertiesAction());
		
		//measure actions
		addAction(new AddMeasureAction());
		addAction(new RemoveMeasureAction());
		addAction(new CopyMeasureAction());
		addAction(new PasteMeasureAction());
		addAction(new GoFirstMeasureAction());
		addAction(new GoLastMeasureAction());
		addAction(new GoNextMeasureAction());
		addAction(new GoPreviousMeasureAction());
		addAction(new CleanMeasureAction());
		
		//note actions
		addAction(new ChangeNoteAction());
		addAction(new InsertNoteAction());
		addAction(new RemoveNoteAction());
		addAction(new RemoveUnusedVoiceAction());
		addAction(new CleanBeatAction());
		addAction(new ChangeTiedNoteAction());
		addAction(new ChangeVelocityAction());
		addAction(new ShiftNoteUpAction());
		addAction(new ShiftNoteDownAction());
		addAction(new IncrementNoteSemitoneAction());
		addAction(new DecrementNoteSemitoneAction());
		addAction(new SetStrokeUpAction());
		addAction(new SetStrokeDownAction());
		addAction(new MoveBeatsRightAction());
		addAction(new MoveBeatsLeftAction());
		addAction(new MoveBeatsCustomAction());
		
		//duration actions
		addAction(new SetWholeDurationAction());
		addAction(new SetHalfDurationAction());
		addAction(new SetQuarterDurationAction());
		addAction(new SetEighthDurationAction());
		addAction(new SetSixteenthDurationAction());
		addAction(new SetThirtySecondDurationAction());
		addAction(new SetSixtyFourthDurationAction());
		addAction(new ChangeDottedDurationAction());
		addAction(new ChangeDoubleDottedDurationAction());
		addAction(new ChangeDivisionTypeAction());
		addAction(new IncrementDurationAction());
		addAction(new DecrementDurationAction());
		
		//insert actions
		addAction(new RepeatOpenAction());
		addAction(new RepeatCloseAction());
		addAction(new RepeatAlternativeAction());
		addAction(new InsertChordAction());
		addAction(new InsertTextAction());
		
		//note effects action
		addAction(new ChangeVibratoNoteAction());
		addAction(new ChangeBendNoteAction());
		addAction(new ChangeDeadNoteAction());
		addAction(new ChangeSlideNoteAction());
		addAction(new ChangeHammerNoteAction());
		addAction(new ChangeGhostNoteAction());
		addAction(new ChangeAccentuatedNoteAction());
		addAction(new ChangeHeavyAccentuatedNoteAction());
		addAction(new ChangeHarmonicNoteAction());
		addAction(new ChangeGraceNoteAction());
		addAction(new ChangeTrillNoteAction());
		addAction(new ChangeTremoloPickingAction());
		addAction(new ChangePalmMuteAction());
		addAction(new ChangeStaccatoAction());
		addAction(new ChangeTappingAction());
		addAction(new ChangeSlappingAction());
		addAction(new ChangePoppingAction());
		addAction(new ChangeTremoloBarAction());
		addAction(new ChangeFadeInAction());
		addAction(new SetVoiceAutoAction());
		addAction(new SetVoiceUpAction());
		addAction(new SetVoiceDownAction());
		
		//marker actions
		addAction(new AddMarkerAction());
		addAction(new ListMarkersAction());
		addAction(new GoPreviousMarkerAction());
		addAction(new GoNextMarkerAction());
		addAction(new GoFirstMarkerAction());
		addAction(new GoLastMarkerAction());
		
		//player actions
		addAction(new TransportPlayAction());
		addAction(new TransportStopAction());
		addAction(new TransportMetronomeAction());
		addAction(new TransportModeAction());
		addAction(new TransportSetLoopSHeaderAction());
		addAction(new TransportSetLoopEHeaderAction());
		
		//setting actions
		addAction(new EditPluginsAction());
		addAction(new EditConfigAction()); 
		addAction(new EditKeyBindingsAction()); 
		
		//caret actions
		addAction(new GoRightAction());
		addAction(new GoLeftAction());
		addAction(new GoUpAction());
		addAction(new GoDownAction());
		
		//help actions
		addAction(new ShowDocAction());
		addAction(new ShowAboutAction());
		
		//tools
		addAction(new TransposeAction() );
		addAction(new ScaleAction());
		addAction(new TGBrowserAction());
		
		//exit
		addAction(new DisposeAction());
	}
	
	public void addAction(Action action){
		this.actions.put(action.getName(),action);
	}
	
	public void removeAction(String name){
		this.actions.remove(name);
	}
	
	public Action getAction(String name){
		return (Action)this.actions.get(name);
	}
	
	public List getAvailableKeyBindingActions(){
		List availableKeyBindingActions = new ArrayList();
		Iterator it = this.actions.keySet().iterator();
		while(it.hasNext()){
			String actionName = (String)it.next();
			if(getAction(actionName).isKeyBindingAvailable()){
				availableKeyBindingActions.add(actionName);
			}
		}
		return availableKeyBindingActions;
	}
}
