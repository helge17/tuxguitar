/*
 * Created on 18-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action;

import org.herac.tuxguitar.action.TGAction;
import org.herac.tuxguitar.action.TGActionContextFactory;
import org.herac.tuxguitar.action.TGActionManager;
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
import org.herac.tuxguitar.app.actions.note.DeleteNoteOrRestAction;
import org.herac.tuxguitar.app.actions.note.IncrementNoteSemitoneAction;
import org.herac.tuxguitar.app.actions.note.InsertRestBeatAction;
import org.herac.tuxguitar.app.actions.note.MoveBeatsCustomAction;
import org.herac.tuxguitar.app.actions.note.MoveBeatsLeftAction;
import org.herac.tuxguitar.app.actions.note.MoveBeatsRightAction;
import org.herac.tuxguitar.app.actions.note.RemoveUnusedVoiceAction;
import org.herac.tuxguitar.app.actions.note.SetNoteFretNumberAction;
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
public class TGActionAdapterManager {
	
	private TGActionContextFactory actionContextFactory;
	private TGActionAccessInterceptor actionAccessInterceptor;
	private TGActionIdList autoLockActionIds;
	private TGActionIdList autoUnlockActionIds;
	private TGActionIdList autoUpdateActionIds;
	private TGActionIdList keyBindingActionIds;
	private TGActionIdList disableOnPlayingActionIds;
	
	public TGActionAdapterManager(){
		this.actionContextFactory = new TGActionContextFactoryImpl();
		this.actionAccessInterceptor = new TGActionAccessInterceptor(this);
		this.autoLockActionIds = new TGActionIdList();
		this.autoUnlockActionIds = new TGActionIdList();
		this.autoUpdateActionIds = new TGActionIdList();
		this.keyBindingActionIds = new TGActionIdList();
		this.disableOnPlayingActionIds = new TGActionIdList();
	}
	
	public void initialize(){
		this.initializeHandlers();
		this.initializeDefaultActions();
	}
	
	private void initializeHandlers(){
		TGActionManager.getInstance().setActionContextFactory(this.actionContextFactory);
		TGActionManager.getInstance().addInterceptor(this.actionAccessInterceptor);
		TGActionManager.getInstance().addPreExecutionListener(new TGActionAutoLockListener(this));
		TGActionManager.getInstance().addPostExecutionListener(new TGActionAutoUpdateListener(this));
		TGActionManager.getInstance().addPostExecutionListener(new TGActionAutoUnlockListener(this));
	}
	
	private void initializeDefaultActions(){
		//file actions
		installAction(new NewFileAction());
		installAction(new OpenFileAction());
		installAction(new SaveFileAction());
		installAction(new SaveAsFileAction());
		installAction(new ImportSongAction());
		installAction(new ExportSongAction());
		installAction(new PrintAction());
		installAction(new PrintPreviewAction());
		installAction(new OpenURLAction());
		installAction(new ExitAction());
		
		//edit actions
		installAction(new UndoAction());
		installAction(new RedoAction());
		installAction(new SetMouseModeSelectionAction());
		installAction(new SetMouseModeEditionAction());
		installAction(new SetNaturalKeyAction());
		installAction(new SetVoice1Action());
		installAction(new SetVoice2Action());
		
		//layout actions
		installAction(new SetPageLayoutAction());
		installAction(new SetLinearLayoutAction());
		installAction(new SetMultitrackViewAction());
		installAction(new SetScoreEnabledAction());
		installAction(new SetTablatureEnabledAction());
		installAction(new SetCompactViewAction());
		installAction(new SetChordNameEnabledAction());
		installAction(new SetChordDiagramEnabledAction());
		
		//view actions
		installAction(new ShowToolbarsAction());
		installAction(new ShowFretBoardAction());
		installAction(new ShowPianoAction());
		installAction(new ShowInstrumentsAction());
		installAction(new ShowTransportAction());
		installAction(new ShowMatrixAction());
		
		//composition actions
		installAction(new ChangeTimeSignatureAction());
		installAction(new ChangeTempoAction());
		installAction(new ChangeClefAction());
		installAction(new ChangeKeySignatureAction());
		installAction(new ChangeTripletFeelAction());
		installAction(new ChangeInfoAction());
		
		//track actions
		installAction(new AddTrackAction());
		installAction(new RemoveTrackAction());
		installAction(new CloneTrackAction());
		installAction(new GoFirstTrackAction());
		installAction(new GoLastTrackAction());
		installAction(new GoNextTrackAction());
		installAction(new GoToTrackAction());
		installAction(new GoPreviousTrackAction());
		installAction(new MoveTrackUpAction());
		installAction(new MoveTrackDownAction());
		installAction(new ChangeTrackMuteAction());
		installAction(new ChangeTrackSoloAction());
		installAction(new EditLyricsAction());
		installAction(new TrackPropertiesAction());
		
		//measure actions
		installAction(new AddMeasureAction());
		installAction(new RemoveMeasureAction());
		installAction(new CopyMeasureAction());
		installAction(new PasteMeasureAction());
		installAction(new GoFirstMeasureAction());
		installAction(new GoLastMeasureAction());
		installAction(new GoNextMeasureAction());
		installAction(new GoPreviousMeasureAction());
		installAction(new CleanMeasureAction());
		
		//note actions
		installAction(new ChangeNoteAction());
		installAction(new InsertRestBeatAction());
		installAction(new DeleteNoteOrRestAction());
		installAction(new RemoveUnusedVoiceAction());
		installAction(new CleanBeatAction());
		installAction(new ChangeTiedNoteAction());
		installAction(new ChangeVelocityAction());
		installAction(new ShiftNoteUpAction());
		installAction(new ShiftNoteDownAction());
		installAction(new IncrementNoteSemitoneAction());
		installAction(new DecrementNoteSemitoneAction());
		installAction(new SetStrokeUpAction());
		installAction(new SetStrokeDownAction());
		installAction(new MoveBeatsRightAction());
		installAction(new MoveBeatsLeftAction());
		installAction(new MoveBeatsCustomAction());
		for( int i = 0 ; i < 10 ; i ++ ){
			installAction(new SetNoteFretNumberAction(i));
		}
		
		//duration actions
		installAction(new SetWholeDurationAction());
		installAction(new SetHalfDurationAction());
		installAction(new SetQuarterDurationAction());
		installAction(new SetEighthDurationAction());
		installAction(new SetSixteenthDurationAction());
		installAction(new SetThirtySecondDurationAction());
		installAction(new SetSixtyFourthDurationAction());
		installAction(new ChangeDottedDurationAction());
		installAction(new ChangeDoubleDottedDurationAction());
		installAction(new ChangeDivisionTypeAction());
		installAction(new IncrementDurationAction());
		installAction(new DecrementDurationAction());
		
		//insert actions
		installAction(new RepeatOpenAction());
		installAction(new RepeatCloseAction());
		installAction(new RepeatAlternativeAction());
		installAction(new InsertChordAction());
		installAction(new InsertTextAction());
		
		//note effects action
		installAction(new ChangeVibratoNoteAction());
		installAction(new ChangeBendNoteAction());
		installAction(new ChangeDeadNoteAction());
		installAction(new ChangeSlideNoteAction());
		installAction(new ChangeHammerNoteAction());
		installAction(new ChangeGhostNoteAction());
		installAction(new ChangeAccentuatedNoteAction());
		installAction(new ChangeHeavyAccentuatedNoteAction());
		installAction(new ChangeHarmonicNoteAction());
		installAction(new ChangeGraceNoteAction());
		installAction(new ChangeTrillNoteAction());
		installAction(new ChangeTremoloPickingAction());
		installAction(new ChangePalmMuteAction());
		installAction(new ChangeLetRingAction());
		installAction(new ChangeStaccatoAction());
		installAction(new ChangeTappingAction());
		installAction(new ChangeSlappingAction());
		installAction(new ChangePoppingAction());
		installAction(new ChangeTremoloBarAction());
		installAction(new ChangeFadeInAction());
		installAction(new SetVoiceAutoAction());
		installAction(new SetVoiceUpAction());
		installAction(new SetVoiceDownAction());
		
		//marker actions
		installAction(new AddMarkerAction());
		installAction(new ListMarkersAction());
		installAction(new GoPreviousMarkerAction());
		installAction(new GoNextMarkerAction());
		installAction(new GoFirstMarkerAction());
		installAction(new GoLastMarkerAction());
		
		//player actions
		installAction(new TransportPlayAction());
		installAction(new TransportStopAction());
		installAction(new TransportMetronomeAction());
		installAction(new TransportCountDownAction());
		installAction(new TransportModeAction());
		installAction(new TransportSetLoopSHeaderAction());
		installAction(new TransportSetLoopEHeaderAction());
		
		//setting actions
		installAction(new EditPluginsAction());
		installAction(new EditConfigAction()); 
		installAction(new EditKeyBindingsAction()); 
		
		//caret actions
		installAction(new GoRightAction());
		installAction(new GoLeftAction());
		installAction(new GoUpAction());
		installAction(new GoDownAction());
		
		//help actions
		installAction(new ShowDocAction());
		installAction(new ShowAboutAction());
		
		//tools
		installAction(new TransposeAction() );
		installAction(new ScaleAction());
		installAction(new TGBrowserAction());
		
		//exit
		installAction(new DisposeAction());
	}
	
	private void installAction(TGActionBase action){
		String actionId = action.getName();
		int flags = action.getFlags();
		
		this.mapAction(actionId, action);
		this.mapActionIdToList(this.autoLockActionIds, actionId, flags, TGActionBase.AUTO_LOCK);
		this.mapActionIdToList(this.autoUnlockActionIds, actionId, flags, TGActionBase.AUTO_UNLOCK);
		this.mapActionIdToList(this.autoUpdateActionIds, actionId, flags, TGActionBase.AUTO_UPDATE);
		this.mapActionIdToList(this.keyBindingActionIds, actionId, flags, TGActionBase.KEY_BINDING_AVAILABLE);
		this.mapActionIdToList(this.disableOnPlayingActionIds, actionId, flags, TGActionBase.DISABLE_ON_PLAYING);
	}
	
	private void mapAction(String actionId, TGAction action){
		TGActionManager.getInstance().mapAction(actionId,action);
	}
	
	private void mapActionIdToList(TGActionIdList actionIdList, String actionId, int flags, int matchFlag){
		if((flags & matchFlag) != 0) {
			actionIdList.addActionId(actionId);
		}
	}
	
	public TGActionAccessInterceptor getActionAccessInterceptor() {
		return this.actionAccessInterceptor;
	}
	
	public TGActionIdList getAutoLockActionIds() {
		return this.autoLockActionIds;
	}

	public TGActionIdList getAutoUnlockActionIds() {
		return this.autoUnlockActionIds;
	}

	public TGActionIdList getAutoUpdateActionIds() {
		return this.autoUpdateActionIds;
	}

	public TGActionIdList getKeyBindingActionIds() {
		return this.keyBindingActionIds;
	}

	public TGActionIdList getDisableOnPlayingActionIds() {
		return this.disableOnPlayingActionIds;
	}
}
