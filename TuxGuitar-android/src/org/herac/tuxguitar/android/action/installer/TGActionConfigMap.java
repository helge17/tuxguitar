package org.herac.tuxguitar.android.action.installer;

import org.herac.tuxguitar.android.action.TGActionMap;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserAddCollectionAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserCdElementAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserCdRootAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserCdUpAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserCloseAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserCloseSessionAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserLoadSessionAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserOpenElementAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserOpenSessionAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserPrepareForReadAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserPrepareForWriteAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserRefreshAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserRemoveCollectionAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserRunnableAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserSaveCurrentElementAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserSaveElementAction;
import org.herac.tuxguitar.android.action.impl.browser.TGBrowserSaveNewElementAction;
import org.herac.tuxguitar.android.action.impl.caret.TGGoDownAction;
import org.herac.tuxguitar.android.action.impl.caret.TGGoLeftAction;
import org.herac.tuxguitar.android.action.impl.caret.TGGoRightAction;
import org.herac.tuxguitar.android.action.impl.caret.TGGoUpAction;
import org.herac.tuxguitar.android.action.impl.caret.TGMoveToAction;
import org.herac.tuxguitar.android.action.impl.caret.TGMoveToAxisPositionAction;
import org.herac.tuxguitar.android.action.impl.edit.TGSetVoice1Action;
import org.herac.tuxguitar.android.action.impl.edit.TGSetVoice2Action;
import org.herac.tuxguitar.android.action.impl.gui.TGBackAction;
import org.herac.tuxguitar.android.action.impl.gui.TGExitAction;
import org.herac.tuxguitar.android.action.impl.gui.TGFinishAction;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenFragmentAction;
import org.herac.tuxguitar.android.action.impl.gui.TGOpenMenuAction;
import org.herac.tuxguitar.android.action.impl.gui.TGRequestPermissionsAction;
import org.herac.tuxguitar.android.action.impl.gui.TGStartActivityForResultAction;
import org.herac.tuxguitar.android.action.impl.intent.TGProcessIntentAction;
import org.herac.tuxguitar.android.action.impl.layout.TGSetChordDiagramEnabledAction;
import org.herac.tuxguitar.android.action.impl.layout.TGSetChordNameEnabledAction;
import org.herac.tuxguitar.android.action.impl.layout.TGSetLayoutScaleAction;
import org.herac.tuxguitar.android.action.impl.layout.TGSetScoreEnabledAction;
import org.herac.tuxguitar.android.action.impl.measure.TGGoFirstMeasureAction;
import org.herac.tuxguitar.android.action.impl.measure.TGGoLastMeasureAction;
import org.herac.tuxguitar.android.action.impl.measure.TGGoNextMeasureAction;
import org.herac.tuxguitar.android.action.impl.measure.TGGoPreviousMeasureAction;
import org.herac.tuxguitar.android.action.impl.storage.TGOpenDocumentAction;
import org.herac.tuxguitar.android.action.impl.storage.TGSaveDocumentAction;
import org.herac.tuxguitar.android.action.impl.storage.TGSaveDocumentAsAction;
import org.herac.tuxguitar.android.action.impl.storage.TGStorageLoadSettingsAction;
import org.herac.tuxguitar.android.action.impl.storage.uri.TGUriReadAction;
import org.herac.tuxguitar.android.action.impl.storage.uri.TGUriWriteAction;
import org.herac.tuxguitar.android.action.impl.track.TGGoFirstTrackAction;
import org.herac.tuxguitar.android.action.impl.track.TGGoLastTrackAction;
import org.herac.tuxguitar.android.action.impl.track.TGGoNextTrackAction;
import org.herac.tuxguitar.android.action.impl.track.TGGoPreviousTrackAction;
import org.herac.tuxguitar.android.action.impl.track.TGGoToTrackAction;
import org.herac.tuxguitar.android.action.impl.transport.TGTransportLoadSettingsAction;
import org.herac.tuxguitar.android.action.impl.transport.TGTransportPlayAction;
import org.herac.tuxguitar.android.action.impl.transport.TGTransportStopAction;
import org.herac.tuxguitar.android.action.impl.view.TGToggleTabKeyboardAction;
import org.herac.tuxguitar.android.action.listener.cache.TGUpdateController;
import org.herac.tuxguitar.android.action.listener.cache.controller.TGUpdateAddedMeasureController;
import org.herac.tuxguitar.android.action.listener.cache.controller.TGUpdateAddedTrackController;
import org.herac.tuxguitar.android.action.listener.cache.controller.TGUpdateItemsController;
import org.herac.tuxguitar.android.action.listener.cache.controller.TGUpdateLoadedSongController;
import org.herac.tuxguitar.android.action.listener.cache.controller.TGUpdateMeasureController;
import org.herac.tuxguitar.android.action.listener.cache.controller.TGUpdateModifiedChannelController;
import org.herac.tuxguitar.android.action.listener.cache.controller.TGUpdateModifiedDurationController;
import org.herac.tuxguitar.android.action.listener.cache.controller.TGUpdateModifiedNoteController;
import org.herac.tuxguitar.android.action.listener.cache.controller.TGUpdateModifiedVelocityController;
import org.herac.tuxguitar.android.action.listener.cache.controller.TGUpdatePlayerTracksController;
import org.herac.tuxguitar.android.action.listener.cache.controller.TGUpdateRemovedMeasureController;
import org.herac.tuxguitar.android.action.listener.cache.controller.TGUpdateRemovedTrackController;
import org.herac.tuxguitar.android.action.listener.cache.controller.TGUpdateSavedSongController;
import org.herac.tuxguitar.android.action.listener.cache.controller.TGUpdateShiftedNoteController;
import org.herac.tuxguitar.android.action.listener.cache.controller.TGUpdateSongController;
import org.herac.tuxguitar.android.action.listener.cache.controller.TGUpdateTransportPositionController;
import org.herac.tuxguitar.editor.action.channel.TGAddChannelAction;
import org.herac.tuxguitar.editor.action.channel.TGAddNewChannelAction;
import org.herac.tuxguitar.editor.action.channel.TGRemoveChannelAction;
import org.herac.tuxguitar.editor.action.channel.TGSetChannelsAction;
import org.herac.tuxguitar.editor.action.channel.TGUpdateChannelAction;
import org.herac.tuxguitar.editor.action.composition.TGChangeClefAction;
import org.herac.tuxguitar.editor.action.composition.TGChangeInfoAction;
import org.herac.tuxguitar.editor.action.composition.TGChangeKeySignatureAction;
import org.herac.tuxguitar.editor.action.composition.TGChangeTempoAction;
import org.herac.tuxguitar.editor.action.composition.TGChangeTempoRangeAction;
import org.herac.tuxguitar.editor.action.composition.TGChangeTimeSignatureAction;
import org.herac.tuxguitar.editor.action.composition.TGChangeTripletFeelAction;
import org.herac.tuxguitar.editor.action.composition.TGRepeatAlternativeAction;
import org.herac.tuxguitar.editor.action.composition.TGRepeatCloseAction;
import org.herac.tuxguitar.editor.action.composition.TGRepeatOpenAction;
import org.herac.tuxguitar.editor.action.duration.TGChangeDottedDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGChangeDoubleDottedDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGDecrementDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGIncrementDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetDivisionTypeDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetEighthDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetHalfDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetQuarterDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetSixteenthDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetSixtyFourthDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetThirtySecondDurationAction;
import org.herac.tuxguitar.editor.action.duration.TGSetWholeDurationAction;
import org.herac.tuxguitar.editor.action.edit.TGRedoAction;
import org.herac.tuxguitar.editor.action.edit.TGUndoAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeAccentuatedNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeBendNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeDeadNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeFadeInAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeGhostNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeGraceNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeHammerNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeHarmonicNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeHeavyAccentuatedNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeLetRingAction;
import org.herac.tuxguitar.editor.action.effect.TGChangePalmMuteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangePoppingAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeSlappingAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeSlideNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeStaccatoAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeTappingAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeTremoloBarAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeTremoloPickingAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeTrillNoteAction;
import org.herac.tuxguitar.editor.action.effect.TGChangeVibratoNoteAction;
import org.herac.tuxguitar.editor.action.file.TGLoadSongAction;
import org.herac.tuxguitar.editor.action.file.TGLoadTemplateAction;
import org.herac.tuxguitar.editor.action.file.TGNewSongAction;
import org.herac.tuxguitar.editor.action.file.TGReadSongAction;
import org.herac.tuxguitar.editor.action.file.TGWriteSongAction;
import org.herac.tuxguitar.editor.action.measure.TGAddMeasureAction;
import org.herac.tuxguitar.editor.action.measure.TGAddMeasureListAction;
import org.herac.tuxguitar.editor.action.measure.TGCleanMeasureAction;
import org.herac.tuxguitar.editor.action.measure.TGCleanMeasureListAction;
import org.herac.tuxguitar.editor.action.measure.TGCopyMeasureFromAction;
import org.herac.tuxguitar.editor.action.measure.TGInsertMeasuresAction;
import org.herac.tuxguitar.editor.action.measure.TGRemoveMeasureAction;
import org.herac.tuxguitar.editor.action.measure.TGRemoveMeasureRangeAction;
import org.herac.tuxguitar.editor.action.note.TGChangeNoteAction;
import org.herac.tuxguitar.editor.action.note.TGChangeStrokeAction;
import org.herac.tuxguitar.editor.action.note.TGChangeTiedNoteAction;
import org.herac.tuxguitar.editor.action.note.TGChangeVelocityAction;
import org.herac.tuxguitar.editor.action.note.TGCleanBeatAction;
import org.herac.tuxguitar.editor.action.note.TGDecrementNoteSemitoneAction;
import org.herac.tuxguitar.editor.action.note.TGDeleteNoteOrRestAction;
import org.herac.tuxguitar.editor.action.note.TGIncrementNoteSemitoneAction;
import org.herac.tuxguitar.editor.action.note.TGInsertRestBeatAction;
import org.herac.tuxguitar.editor.action.note.TGInsertTextAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsLeftAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsRightAction;
import org.herac.tuxguitar.editor.action.note.TGRemoveTextAction;
import org.herac.tuxguitar.editor.action.note.TGRemoveUnusedVoiceAction;
import org.herac.tuxguitar.editor.action.note.TGSetVoiceAutoAction;
import org.herac.tuxguitar.editor.action.note.TGSetVoiceDownAction;
import org.herac.tuxguitar.editor.action.note.TGSetVoiceUpAction;
import org.herac.tuxguitar.editor.action.note.TGShiftNoteDownAction;
import org.herac.tuxguitar.editor.action.note.TGShiftNoteUpAction;
import org.herac.tuxguitar.editor.action.song.TGClearSongAction;
import org.herac.tuxguitar.editor.action.song.TGCopySongFromAction;
import org.herac.tuxguitar.editor.action.track.TGAddNewTrackAction;
import org.herac.tuxguitar.editor.action.track.TGAddTrackAction;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackMuteAction;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackPropertiesAction;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackSoloAction;
import org.herac.tuxguitar.editor.action.track.TGChangeTrackTuningAction;
import org.herac.tuxguitar.editor.action.track.TGCloneTrackAction;
import org.herac.tuxguitar.editor.action.track.TGCopyTrackFromAction;
import org.herac.tuxguitar.editor.action.track.TGMoveTrackDownAction;
import org.herac.tuxguitar.editor.action.track.TGMoveTrackUpAction;
import org.herac.tuxguitar.editor.action.track.TGRemoveTrackAction;
import org.herac.tuxguitar.editor.action.track.TGSetTrackChannelAction;
import org.herac.tuxguitar.editor.action.track.TGSetTrackInfoAction;
import org.herac.tuxguitar.editor.action.track.TGSetTrackMuteAction;
import org.herac.tuxguitar.editor.action.track.TGSetTrackNameAction;
import org.herac.tuxguitar.editor.action.track.TGSetTrackSoloAction;
import org.herac.tuxguitar.editor.undo.TGUndoableActionController;
import org.herac.tuxguitar.editor.undo.impl.channel.TGUndoableChannelGenericController;
import org.herac.tuxguitar.editor.undo.impl.channel.TGUndoableModifyChannelController;
import org.herac.tuxguitar.editor.undo.impl.custom.TGUndoableAltRepeatController;
import org.herac.tuxguitar.editor.undo.impl.custom.TGUndoableClefController;
import org.herac.tuxguitar.editor.undo.impl.custom.TGUndoableCloseRepeatController;
import org.herac.tuxguitar.editor.undo.impl.custom.TGUndoableKeySignatureController;
import org.herac.tuxguitar.editor.undo.impl.custom.TGUndoableOpenRepeatController;
import org.herac.tuxguitar.editor.undo.impl.custom.TGUndoableSongInfoController;
import org.herac.tuxguitar.editor.undo.impl.custom.TGUndoableTempoController;
import org.herac.tuxguitar.editor.undo.impl.custom.TGUndoableTimeSignatureController;
import org.herac.tuxguitar.editor.undo.impl.custom.TGUndoableTripletFeelController;
import org.herac.tuxguitar.editor.undo.impl.measure.TGUndoableAddMeasureController;
import org.herac.tuxguitar.editor.undo.impl.measure.TGUndoableMeasureGenericController;
import org.herac.tuxguitar.editor.undo.impl.measure.TGUndoableRemoveMeasureController;
import org.herac.tuxguitar.editor.undo.impl.song.TGUndoableSongGenericController;
import org.herac.tuxguitar.editor.undo.impl.track.TGUndoableAddTrackController;
import org.herac.tuxguitar.editor.undo.impl.track.TGUndoableCloneTrackController;
import org.herac.tuxguitar.editor.undo.impl.track.TGUndoableMoveTrackDownController;
import org.herac.tuxguitar.editor.undo.impl.track.TGUndoableMoveTrackUpController;
import org.herac.tuxguitar.editor.undo.impl.track.TGUndoableRemoveTrackController;
import org.herac.tuxguitar.editor.undo.impl.track.TGUndoableTrackGenericController;
import org.herac.tuxguitar.editor.undo.impl.track.TGUndoableTrackInfoController;
import org.herac.tuxguitar.editor.undo.impl.track.TGUndoableTrackSoloMuteController;

public class TGActionConfigMap extends TGActionMap<TGActionConfig> {
	
	public static final int LOCKABLE = 0x01;
	public static final int SYNC_THREAD = 0x02;
	public static final int DISABLE_ON_PLAY = 0x04;
	public static final int STOP_TRANSPORT = 0x08;
	public static final int DISABLE_PROCESSING = 0x10;
	
	private static final TGUpdateController UPDATE_ITEMS_CTL = new TGUpdateItemsController();
	private static final TGUpdateController UPDATE_MEASURE_CTL = new TGUpdateMeasureController();
	private static final TGUpdateController UPDATE_SONG_CTL = new TGUpdateSongController();
	private static final TGUpdateController UPDATE_SONG_LOADED_CTL = new TGUpdateLoadedSongController();
	private static final TGUpdateController UPDATE_SONG_SAVED_CTL = new TGUpdateSavedSongController();
	
	private static final TGUndoableActionController UNDOABLE_SONG_GENERIC = new TGUndoableSongGenericController();
	private static final TGUndoableActionController UNDOABLE_MEASURE_GENERIC = new TGUndoableMeasureGenericController();
	private static final TGUndoableActionController UNDOABLE_TRACK_GENERIC = new TGUndoableTrackGenericController();
	private static final TGUndoableActionController UNDOABLE_CHANNEL_GENERIC = new TGUndoableChannelGenericController();
	
	public TGActionConfigMap() {
		this.createConfigMap();
	}
	
	public void createConfigMap() {
		//file actions
		this.map(TGLoadSongAction.NAME, LOCKABLE | STOP_TRANSPORT | DISABLE_ON_PLAY, UPDATE_SONG_LOADED_CTL);
		this.map(TGNewSongAction.NAME, LOCKABLE | STOP_TRANSPORT | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGLoadTemplateAction.NAME, LOCKABLE | STOP_TRANSPORT | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGReadSongAction.NAME, LOCKABLE | STOP_TRANSPORT | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGWriteSongAction.NAME, LOCKABLE, UPDATE_SONG_SAVED_CTL);
		
		//edit actions
		this.map(TGUndoAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGRedoAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGSetVoice1Action.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGSetVoice2Action.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		
		//caret actions
		this.map(TGMoveToAction.NAME, LOCKABLE, new TGUpdateTransportPositionController());
		this.map(TGGoRightAction.NAME, LOCKABLE |  DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGGoLeftAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGGoUpAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGGoDownAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGMoveToAxisPositionAction.NAME, LOCKABLE, null);
		
		
		//song actions
		this.map(TGCopySongFromAction.NAME, LOCKABLE, UPDATE_SONG_CTL, UNDOABLE_SONG_GENERIC);
		this.map(TGClearSongAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		
		//track actions
		this.map(TGAddTrackAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateAddedTrackController(), new TGUndoableAddTrackController());
		this.map(TGAddNewTrackAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateAddedTrackController(), new TGUndoableAddTrackController());
		this.map(TGSetTrackMuteAction.NAME, LOCKABLE, new TGUpdatePlayerTracksController(), new TGUndoableTrackSoloMuteController());
		this.map(TGSetTrackSoloAction.NAME, LOCKABLE, new TGUpdatePlayerTracksController(), new TGUndoableTrackSoloMuteController());
		this.map(TGChangeTrackMuteAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGChangeTrackSoloAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGCloneTrackAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, new TGUndoableCloneTrackController());
		this.map(TGGoFirstTrackAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGGoLastTrackAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGGoNextTrackAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGGoPreviousTrackAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGGoToTrackAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGMoveTrackDownAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, new TGUndoableMoveTrackDownController());
		this.map(TGMoveTrackUpAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, new TGUndoableMoveTrackUpController());
		this.map(TGRemoveTrackAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateRemovedTrackController(), new TGUndoableRemoveTrackController());
		this.map(TGSetTrackInfoAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL, new TGUndoableTrackInfoController());
		this.map(TGSetTrackNameAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGSetTrackChannelAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC);
		this.map(TGChangeTrackTuningAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC);
		this.map(TGChangeTrackPropertiesAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGCopyTrackFromAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC);
		
		//measure actions
		this.map(TGAddMeasureAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateAddedMeasureController(), new TGUndoableAddMeasureController());
		this.map(TGAddMeasureListAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGCleanMeasureAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGCleanMeasureListAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGGoFirstMeasureAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGGoLastMeasureAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGGoNextMeasureAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGGoPreviousMeasureAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGRemoveMeasureAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateRemovedMeasureController(), new TGUndoableRemoveMeasureController());
		this.map(TGRemoveMeasureRangeAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGCopyMeasureFromAction.NAME, LOCKABLE, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGInsertMeasuresAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_SONG_GENERIC);
		
		//beat actions
		this.map(TGChangeNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateModifiedNoteController(), UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeTiedNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeVelocityAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateModifiedVelocityController(), UNDOABLE_MEASURE_GENERIC);
		this.map(TGCleanBeatAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGDecrementNoteSemitoneAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGDeleteNoteOrRestAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGIncrementNoteSemitoneAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGInsertRestBeatAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGMoveBeatsAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC);
		this.map(TGMoveBeatsLeftAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGMoveBeatsRightAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGRemoveUnusedVoiceAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGSetVoiceAutoAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGSetVoiceDownAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGSetVoiceUpAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGShiftNoteDownAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateShiftedNoteController(), UNDOABLE_MEASURE_GENERIC);
		this.map(TGShiftNoteUpAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateShiftedNoteController(), UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeStrokeAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGInsertTextAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGRemoveTextAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		
		//effect actions
		this.map(TGChangeAccentuatedNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeBendNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeDeadNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeFadeInAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeGhostNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeGraceNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeHammerNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeHarmonicNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeHeavyAccentuatedNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeLetRingAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangePalmMuteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangePoppingAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeSlappingAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeSlideNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeStaccatoAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeTappingAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeTremoloBarAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeTremoloPickingAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeTrillNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeVibratoNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		
		//duration actions
		this.map(TGSetDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateModifiedDurationController(), UNDOABLE_MEASURE_GENERIC);
		this.map(TGSetWholeDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGSetHalfDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGSetQuarterDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGSetEighthDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGSetSixteenthDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGSetThirtySecondDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGSetSixtyFourthDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGSetDivisionTypeDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGChangeDottedDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGChangeDoubleDottedDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGIncrementDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGDecrementDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		
		//composition actions
		this.map(TGChangeTempoAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, new TGUndoableTempoController());
		this.map(TGChangeTempoRangeAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGChangeClefAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, new TGUndoableClefController());
		this.map(TGChangeTimeSignatureAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, new TGUndoableTimeSignatureController());
		this.map(TGChangeKeySignatureAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, new TGUndoableKeySignatureController());
		this.map(TGChangeTripletFeelAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, new TGUndoableTripletFeelController());
		this.map(TGChangeInfoAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL, new TGUndoableSongInfoController());
		this.map(TGRepeatOpenAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, new TGUndoableOpenRepeatController());
		this.map(TGRepeatCloseAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, new TGUndoableCloseRepeatController());
		this.map(TGRepeatAlternativeAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, new TGUndoableAltRepeatController());
		
		//channel actions
		this.map(TGSetChannelsAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL, UNDOABLE_CHANNEL_GENERIC);
		this.map(TGAddChannelAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL, UNDOABLE_CHANNEL_GENERIC);
		this.map(TGAddNewChannelAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL, UNDOABLE_CHANNEL_GENERIC);
		this.map(TGRemoveChannelAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL, UNDOABLE_CHANNEL_GENERIC);
		this.map(TGUpdateChannelAction.NAME, LOCKABLE, new TGUpdateModifiedChannelController(), new TGUndoableModifyChannelController());
		
		//transport actions
		this.map(TGTransportPlayAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGTransportStopAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGTransportLoadSettingsAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		
		//layout actions
		this.map(TGSetLayoutScaleAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL);
		this.map(TGSetScoreEnabledAction.NAME, LOCKABLE, UPDATE_SONG_CTL);
		this.map(TGSetChordNameEnabledAction.NAME, LOCKABLE, UPDATE_SONG_CTL);
		this.map(TGSetChordDiagramEnabledAction.NAME, LOCKABLE, UPDATE_SONG_CTL);
		
		//view actions
		this.map(TGToggleTabKeyboardAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_ITEMS_CTL);
		
		//browser actions
		this.map(TGBrowserCloseAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGBrowserCdRootAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGBrowserCdUpAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGBrowserCdElementAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGBrowserRefreshAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGBrowserOpenElementAction.NAME, LOCKABLE | STOP_TRANSPORT | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGBrowserSaveElementAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGBrowserSaveNewElementAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGBrowserSaveCurrentElementAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGBrowserPrepareForReadAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGBrowserPrepareForWriteAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGBrowserLoadSessionAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGBrowserOpenSessionAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGBrowserCloseSessionAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGBrowserAddCollectionAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGBrowserRemoveCollectionAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGBrowserRunnableAction.NAME, LOCKABLE, null);

		//storage actions
		this.map(TGUriReadAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGUriWriteAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGOpenDocumentAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGSaveDocumentAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGSaveDocumentAsAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGStorageLoadSettingsAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);

		//intent actions
		this.map(TGProcessIntentAction.NAME, LOCKABLE | STOP_TRANSPORT | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		
		//gui actions
		this.map(TGOpenDialogAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_ITEMS_CTL);
		this.map(TGOpenMenuAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_ITEMS_CTL);
		this.map(TGOpenFragmentAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_ITEMS_CTL);
		this.map(TGStartActivityForResultAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_ITEMS_CTL);
		this.map(TGRequestPermissionsAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_ITEMS_CTL);
		this.map(TGBackAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_ITEMS_CTL);
		this.map(TGExitAction.NAME, LOCKABLE, null);
		this.map(TGFinishAction.NAME, LOCKABLE | SYNC_THREAD, null);
	}
	
	private void map(String actionId, int flags, TGUpdateController updateController) {
		this.map(actionId, flags, updateController, null);
	}
	
	private void map(String actionId, int flags, TGUpdateController updateController, TGUndoableActionController undoableController) {
		TGActionConfig tgActionConfig = new TGActionConfig();
		tgActionConfig.setUpdateController(updateController);
		tgActionConfig.setUndoableController(undoableController);
		tgActionConfig.setLockableAction((flags & LOCKABLE) != 0);
		tgActionConfig.setDisableOnPlaying((flags & DISABLE_ON_PLAY) != 0);
		tgActionConfig.setStopTransport((flags & STOP_TRANSPORT) != 0);
		tgActionConfig.setSyncThread((flags & SYNC_THREAD) != 0);
		tgActionConfig.setDocumentModifier(undoableController != null);
		
		this.set(actionId, tgActionConfig);
	}
}
