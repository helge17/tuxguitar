/*
 * Created on 18-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.app.actions.caret.GoDownAction;
import org.herac.tuxguitar.app.actions.caret.GoLeftAction;
import org.herac.tuxguitar.app.actions.caret.GoRightAction;
import org.herac.tuxguitar.app.actions.caret.GoUpAction;
import org.herac.tuxguitar.app.actions.composition.ChangeClefAction;
import org.herac.tuxguitar.app.actions.composition.ChangeInfoAction;
import org.herac.tuxguitar.app.actions.composition.ChangeKeySignatureAction;
import org.herac.tuxguitar.app.actions.composition.ChangeTempoAction;
import org.herac.tuxguitar.app.actions.composition.ChangeTimeSignatureAction;
import org.herac.tuxguitar.app.actions.composition.ChangeTripletFeelAction;
import org.herac.tuxguitar.app.actions.duration.ChangeDivisionTypeAction;
import org.herac.tuxguitar.app.actions.duration.ChangeDottedDurationAction;
import org.herac.tuxguitar.app.actions.duration.ChangeDoubleDottedDurationAction;
import org.herac.tuxguitar.app.actions.duration.DecrementDurationAction;
import org.herac.tuxguitar.app.actions.duration.IncrementDurationAction;
import org.herac.tuxguitar.app.actions.duration.SetEighthDurationAction;
import org.herac.tuxguitar.app.actions.duration.SetHalfDurationAction;
import org.herac.tuxguitar.app.actions.duration.SetQuarterDurationAction;
import org.herac.tuxguitar.app.actions.duration.SetSixteenthDurationAction;
import org.herac.tuxguitar.app.actions.duration.SetSixtyFourthDurationAction;
import org.herac.tuxguitar.app.actions.duration.SetThirtySecondDurationAction;
import org.herac.tuxguitar.app.actions.duration.SetWholeDurationAction;
import org.herac.tuxguitar.app.actions.edit.RedoAction;
import org.herac.tuxguitar.app.actions.edit.SetMouseModeEditionAction;
import org.herac.tuxguitar.app.actions.edit.SetMouseModeSelectionAction;
import org.herac.tuxguitar.app.actions.edit.SetNaturalKeyAction;
import org.herac.tuxguitar.app.actions.edit.SetVoice1Action;
import org.herac.tuxguitar.app.actions.edit.SetVoice2Action;
import org.herac.tuxguitar.app.actions.edit.UndoAction;
import org.herac.tuxguitar.app.actions.effects.ChangeAccentuatedNoteAction;
import org.herac.tuxguitar.app.actions.effects.ChangeBendNoteAction;
import org.herac.tuxguitar.app.actions.effects.ChangeDeadNoteAction;
import org.herac.tuxguitar.app.actions.effects.ChangeFadeInAction;
import org.herac.tuxguitar.app.actions.effects.ChangeGhostNoteAction;
import org.herac.tuxguitar.app.actions.effects.ChangeGraceNoteAction;
import org.herac.tuxguitar.app.actions.effects.ChangeHammerNoteAction;
import org.herac.tuxguitar.app.actions.effects.ChangeHarmonicNoteAction;
import org.herac.tuxguitar.app.actions.effects.ChangeHeavyAccentuatedNoteAction;
import org.herac.tuxguitar.app.actions.effects.ChangeLetRingAction;
import org.herac.tuxguitar.app.actions.effects.ChangePalmMuteAction;
import org.herac.tuxguitar.app.actions.effects.ChangePoppingAction;
import org.herac.tuxguitar.app.actions.effects.ChangeSlappingAction;
import org.herac.tuxguitar.app.actions.effects.ChangeSlideNoteAction;
import org.herac.tuxguitar.app.actions.effects.ChangeStaccatoAction;
import org.herac.tuxguitar.app.actions.effects.ChangeTappingAction;
import org.herac.tuxguitar.app.actions.effects.ChangeTremoloBarAction;
import org.herac.tuxguitar.app.actions.effects.ChangeTremoloPickingAction;
import org.herac.tuxguitar.app.actions.effects.ChangeTrillNoteAction;
import org.herac.tuxguitar.app.actions.effects.ChangeVibratoNoteAction;
import org.herac.tuxguitar.app.actions.file.ExitAction;
import org.herac.tuxguitar.app.actions.file.ExportSongAction;
import org.herac.tuxguitar.app.actions.file.ImportSongAction;
import org.herac.tuxguitar.app.actions.file.NewFileAction;
import org.herac.tuxguitar.app.actions.file.OpenFileAction;
import org.herac.tuxguitar.app.actions.file.OpenURLAction;
import org.herac.tuxguitar.app.actions.file.PrintAction;
import org.herac.tuxguitar.app.actions.file.PrintPreviewAction;
import org.herac.tuxguitar.app.actions.file.SaveAsFileAction;
import org.herac.tuxguitar.app.actions.file.SaveFileAction;
import org.herac.tuxguitar.app.actions.help.ShowAboutAction;
import org.herac.tuxguitar.app.actions.help.ShowDocAction;
import org.herac.tuxguitar.app.actions.insert.InsertChordAction;
import org.herac.tuxguitar.app.actions.insert.InsertTextAction;
import org.herac.tuxguitar.app.actions.insert.RepeatAlternativeAction;
import org.herac.tuxguitar.app.actions.insert.RepeatCloseAction;
import org.herac.tuxguitar.app.actions.insert.RepeatOpenAction;
import org.herac.tuxguitar.app.actions.layout.SetChordDiagramEnabledAction;
import org.herac.tuxguitar.app.actions.layout.SetChordNameEnabledAction;
import org.herac.tuxguitar.app.actions.layout.SetCompactViewAction;
import org.herac.tuxguitar.app.actions.layout.SetLinearLayoutAction;
import org.herac.tuxguitar.app.actions.layout.SetMultitrackViewAction;
import org.herac.tuxguitar.app.actions.layout.SetPageLayoutAction;
import org.herac.tuxguitar.app.actions.layout.SetScoreEnabledAction;
import org.herac.tuxguitar.app.actions.layout.SetTablatureEnabledAction;
import org.herac.tuxguitar.app.actions.marker.AddMarkerAction;
import org.herac.tuxguitar.app.actions.marker.GoFirstMarkerAction;
import org.herac.tuxguitar.app.actions.marker.GoLastMarkerAction;
import org.herac.tuxguitar.app.actions.marker.GoNextMarkerAction;
import org.herac.tuxguitar.app.actions.marker.GoPreviousMarkerAction;
import org.herac.tuxguitar.app.actions.marker.ListMarkersAction;
import org.herac.tuxguitar.app.actions.measure.AddMeasureAction;
import org.herac.tuxguitar.app.actions.measure.CleanMeasureAction;
import org.herac.tuxguitar.app.actions.measure.CopyMeasureAction;
import org.herac.tuxguitar.app.actions.measure.GoFirstMeasureAction;
import org.herac.tuxguitar.app.actions.measure.GoLastMeasureAction;
import org.herac.tuxguitar.app.actions.measure.GoNextMeasureAction;
import org.herac.tuxguitar.app.actions.measure.GoPreviousMeasureAction;
import org.herac.tuxguitar.app.actions.measure.PasteMeasureAction;
import org.herac.tuxguitar.app.actions.measure.RemoveMeasureAction;
import org.herac.tuxguitar.app.actions.note.ChangeNoteAction;
import org.herac.tuxguitar.app.actions.note.ChangeTiedNoteAction;
import org.herac.tuxguitar.app.actions.note.ChangeVelocityAction;
import org.herac.tuxguitar.app.actions.note.CleanBeatAction;
import org.herac.tuxguitar.app.actions.note.DecrementNoteSemitoneAction;
import org.herac.tuxguitar.app.actions.note.IncrementNoteSemitoneAction;
import org.herac.tuxguitar.app.actions.note.InsertNoteAction;
import org.herac.tuxguitar.app.actions.note.MoveBeatsCustomAction;
import org.herac.tuxguitar.app.actions.note.MoveBeatsLeftAction;
import org.herac.tuxguitar.app.actions.note.MoveBeatsRightAction;
import org.herac.tuxguitar.app.actions.note.RemoveNoteAction;
import org.herac.tuxguitar.app.actions.note.RemoveUnusedVoiceAction;
import org.herac.tuxguitar.app.actions.note.SetNoteFretNumber0Action;
import org.herac.tuxguitar.app.actions.note.SetNoteFretNumber1Action;
import org.herac.tuxguitar.app.actions.note.SetNoteFretNumber2Action;
import org.herac.tuxguitar.app.actions.note.SetNoteFretNumber3Action;
import org.herac.tuxguitar.app.actions.note.SetNoteFretNumber4Action;
import org.herac.tuxguitar.app.actions.note.SetNoteFretNumber5Action;
import org.herac.tuxguitar.app.actions.note.SetNoteFretNumber6Action;
import org.herac.tuxguitar.app.actions.note.SetNoteFretNumber7Action;
import org.herac.tuxguitar.app.actions.note.SetNoteFretNumber8Action;
import org.herac.tuxguitar.app.actions.note.SetNoteFretNumber9Action;
import org.herac.tuxguitar.app.actions.note.SetStrokeDownAction;
import org.herac.tuxguitar.app.actions.note.SetStrokeUpAction;
import org.herac.tuxguitar.app.actions.note.SetVoiceAutoAction;
import org.herac.tuxguitar.app.actions.note.SetVoiceDownAction;
import org.herac.tuxguitar.app.actions.note.SetVoiceUpAction;
import org.herac.tuxguitar.app.actions.note.ShiftNoteDownAction;
import org.herac.tuxguitar.app.actions.note.ShiftNoteUpAction;
import org.herac.tuxguitar.app.actions.settings.EditConfigAction;
import org.herac.tuxguitar.app.actions.settings.EditKeyBindingsAction;
import org.herac.tuxguitar.app.actions.settings.EditPluginsAction;
import org.herac.tuxguitar.app.actions.system.DisposeAction;
import org.herac.tuxguitar.app.actions.tools.ScaleAction;
import org.herac.tuxguitar.app.actions.tools.TGBrowserAction;
import org.herac.tuxguitar.app.actions.tools.TransposeAction;
import org.herac.tuxguitar.app.actions.track.AddTrackAction;
import org.herac.tuxguitar.app.actions.track.ChangeTrackMuteAction;
import org.herac.tuxguitar.app.actions.track.ChangeTrackSoloAction;
import org.herac.tuxguitar.app.actions.track.CloneTrackAction;
import org.herac.tuxguitar.app.actions.track.EditLyricsAction;
import org.herac.tuxguitar.app.actions.track.GoFirstTrackAction;
import org.herac.tuxguitar.app.actions.track.GoLastTrackAction;
import org.herac.tuxguitar.app.actions.track.GoNextTrackAction;
import org.herac.tuxguitar.app.actions.track.GoPreviousTrackAction;
import org.herac.tuxguitar.app.actions.track.GoToTrackAction;
import org.herac.tuxguitar.app.actions.track.MoveTrackDownAction;
import org.herac.tuxguitar.app.actions.track.MoveTrackUpAction;
import org.herac.tuxguitar.app.actions.track.RemoveTrackAction;
import org.herac.tuxguitar.app.actions.track.TrackPropertiesAction;
import org.herac.tuxguitar.app.actions.transport.TransportCountDownAction;
import org.herac.tuxguitar.app.actions.transport.TransportMetronomeAction;
import org.herac.tuxguitar.app.actions.transport.TransportModeAction;
import org.herac.tuxguitar.app.actions.transport.TransportPlayAction;
import org.herac.tuxguitar.app.actions.transport.TransportSetLoopEHeaderAction;
import org.herac.tuxguitar.app.actions.transport.TransportSetLoopSHeaderAction;
import org.herac.tuxguitar.app.actions.transport.TransportStopAction;
import org.herac.tuxguitar.app.actions.view.ShowFretBoardAction;
import org.herac.tuxguitar.app.actions.view.ShowInstrumentsAction;
import org.herac.tuxguitar.app.actions.view.ShowMatrixAction;
import org.herac.tuxguitar.app.actions.view.ShowPianoAction;
import org.herac.tuxguitar.app.actions.view.ShowToolbarsAction;
import org.herac.tuxguitar.app.actions.view.ShowTransportAction;

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
		addAction(new ShowToolbarsAction());
		addAction(new ShowFretBoardAction());
		addAction(new ShowPianoAction());
		addAction(new ShowInstrumentsAction());
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
		addAction(new ChangeTrackMuteAction());
		addAction(new ChangeTrackSoloAction());
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
		addAction(new SetNoteFretNumber0Action());
		addAction(new SetNoteFretNumber1Action());
		addAction(new SetNoteFretNumber2Action());
		addAction(new SetNoteFretNumber3Action());
		addAction(new SetNoteFretNumber4Action());
		addAction(new SetNoteFretNumber5Action());
		addAction(new SetNoteFretNumber6Action());
		addAction(new SetNoteFretNumber7Action());
		addAction(new SetNoteFretNumber8Action());
		addAction(new SetNoteFretNumber9Action());
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
		addAction(new ChangeLetRingAction());
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
		addAction(new TransportCountDownAction());
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
