package app.tuxguitar.android.action.installer;

import app.tuxguitar.android.action.TGActionMap;
import app.tuxguitar.android.action.impl.browser.TGBrowserAddCollectionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserCdElementAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserCdRootAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserCdUpAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserCloseAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserCloseSessionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserLoadSessionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserOpenElementAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserOpenSessionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserPrepareForReadAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserPrepareForWriteAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserRefreshAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserRemoveCollectionAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserRunnableAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserSaveCurrentElementAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserSaveElementAction;
import app.tuxguitar.android.action.impl.browser.TGBrowserSaveNewElementAction;
import app.tuxguitar.android.action.impl.caret.TGGoDownAction;
import app.tuxguitar.android.action.impl.caret.TGGoLeftAction;
import app.tuxguitar.android.action.impl.caret.TGGoRightAction;
import app.tuxguitar.android.action.impl.caret.TGGoUpAction;
import app.tuxguitar.android.action.impl.caret.TGMoveToAction;
import app.tuxguitar.android.action.impl.caret.TGMoveToAxisPositionAction;
import app.tuxguitar.android.action.impl.edit.TGSetVoice1Action;
import app.tuxguitar.android.action.impl.edit.TGSetVoice2Action;
import app.tuxguitar.android.action.impl.gui.TGBackAction;
import app.tuxguitar.android.action.impl.gui.TGExitAction;
import app.tuxguitar.android.action.impl.gui.TGFinishAction;
import app.tuxguitar.android.action.impl.gui.TGOpenCabMenuAction;
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import app.tuxguitar.android.action.impl.gui.TGOpenFragmentAction;
import app.tuxguitar.android.action.impl.gui.TGOpenMenuAction;
import app.tuxguitar.android.action.impl.gui.TGRequestPermissionsAction;
import app.tuxguitar.android.action.impl.gui.TGStartActivityForResultAction;
import app.tuxguitar.android.action.impl.intent.TGProcessIntentAction;
import app.tuxguitar.android.action.impl.layout.TGSetChordDiagramEnabledAction;
import app.tuxguitar.android.action.impl.layout.TGSetChordNameEnabledAction;
import app.tuxguitar.android.action.impl.layout.TGSetLayoutScaleAction;
import app.tuxguitar.android.action.impl.layout.TGSetScoreEnabledAction;
import app.tuxguitar.android.action.impl.layout.TGToggleHighlightPlayedBeatAction;
import app.tuxguitar.android.action.impl.measure.TGGoFirstMeasureAction;
import app.tuxguitar.android.action.impl.measure.TGGoLastMeasureAction;
import app.tuxguitar.android.action.impl.measure.TGGoNextMeasureAction;
import app.tuxguitar.android.action.impl.measure.TGGoPreviousMeasureAction;
import app.tuxguitar.android.action.impl.storage.TGOpenDocumentAction;
import app.tuxguitar.android.action.impl.storage.TGSaveDocumentAction;
import app.tuxguitar.android.action.impl.storage.TGSaveDocumentAsAction;
import app.tuxguitar.android.action.impl.storage.TGStorageLoadSettingsAction;
import app.tuxguitar.android.action.impl.storage.uri.TGUriReadAction;
import app.tuxguitar.android.action.impl.storage.uri.TGUriWriteAction;
import app.tuxguitar.android.action.impl.track.TGGoFirstTrackAction;
import app.tuxguitar.android.action.impl.track.TGGoLastTrackAction;
import app.tuxguitar.android.action.impl.track.TGGoNextTrackAction;
import app.tuxguitar.android.action.impl.track.TGGoPreviousTrackAction;
import app.tuxguitar.android.action.impl.track.TGGoToTrackAction;
import app.tuxguitar.android.action.impl.transport.TGTransportLoadSettingsAction;
import app.tuxguitar.android.action.impl.transport.TGTransportPlayAction;
import app.tuxguitar.android.action.impl.transport.TGTransportStopAction;
import app.tuxguitar.android.action.impl.view.TGShowSmartMenuAction;
import app.tuxguitar.android.action.impl.view.TGToggleTabKeyboardAction;
import app.tuxguitar.android.action.listener.cache.TGUpdateController;
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateAddedMeasureController;
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateAddedTrackController;
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateChannelsController;
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateItemsController;
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateLoadedSongController;
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateMeasureController;
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateModifiedChannelController;
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateModifiedDurationController;
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateModifiedNoteController;
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateModifiedVelocityController;
import app.tuxguitar.android.action.listener.cache.controller.TGUpdatePlayerTracksController;
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateRemovedMeasureController;
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateRemovedTrackController;
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateSavedSongController;
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateShiftedNoteController;
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateSongController;
import app.tuxguitar.android.action.listener.cache.controller.TGUpdateTransportPositionController;
import app.tuxguitar.editor.action.channel.TGAddChannelAction;
import app.tuxguitar.editor.action.channel.TGAddNewChannelAction;
import app.tuxguitar.editor.action.channel.TGRemoveChannelAction;
import app.tuxguitar.editor.action.channel.TGSetChannelsAction;
import app.tuxguitar.editor.action.channel.TGUpdateChannelAction;
import app.tuxguitar.editor.action.composition.TGChangeClefAction;
import app.tuxguitar.editor.action.composition.TGChangeInfoAction;
import app.tuxguitar.editor.action.composition.TGChangeKeySignatureAction;
import app.tuxguitar.editor.action.composition.TGChangeTempoAction;
import app.tuxguitar.editor.action.composition.TGChangeTempoRangeAction;
import app.tuxguitar.editor.action.composition.TGChangeTimeSignatureAction;
import app.tuxguitar.editor.action.composition.TGChangeTripletFeelAction;
import app.tuxguitar.editor.action.composition.TGRepeatAlternativeAction;
import app.tuxguitar.editor.action.composition.TGRepeatCloseAction;
import app.tuxguitar.editor.action.composition.TGRepeatOpenAction;
import app.tuxguitar.editor.action.duration.TGChangeDottedDurationAction;
import app.tuxguitar.editor.action.duration.TGChangeDoubleDottedDurationAction;
import app.tuxguitar.editor.action.duration.TGDecrementDurationAction;
import app.tuxguitar.editor.action.duration.TGIncrementDurationAction;
import app.tuxguitar.editor.action.duration.TGSetDivisionTypeDurationAction;
import app.tuxguitar.editor.action.duration.TGSetDurationAction;
import app.tuxguitar.editor.action.duration.TGSetEighthDurationAction;
import app.tuxguitar.editor.action.duration.TGSetHalfDurationAction;
import app.tuxguitar.editor.action.duration.TGSetQuarterDurationAction;
import app.tuxguitar.editor.action.duration.TGSetSixteenthDurationAction;
import app.tuxguitar.editor.action.duration.TGSetSixtyFourthDurationAction;
import app.tuxguitar.editor.action.duration.TGSetThirtySecondDurationAction;
import app.tuxguitar.editor.action.duration.TGSetWholeDurationAction;
import app.tuxguitar.editor.action.edit.TGRedoAction;
import app.tuxguitar.editor.action.edit.TGUndoAction;
import app.tuxguitar.editor.action.effect.TGChangeAccentuatedNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeBendNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeDeadNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeFadeInAction;
import app.tuxguitar.editor.action.effect.TGChangeGhostNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeGraceNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeHammerNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeHarmonicNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeHeavyAccentuatedNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeLetRingAction;
import app.tuxguitar.editor.action.effect.TGChangePalmMuteAction;
import app.tuxguitar.editor.action.effect.TGChangePoppingAction;
import app.tuxguitar.editor.action.effect.TGChangeSlappingAction;
import app.tuxguitar.editor.action.effect.TGChangeSlideNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeStaccatoAction;
import app.tuxguitar.editor.action.effect.TGChangeTappingAction;
import app.tuxguitar.editor.action.effect.TGChangeTremoloBarAction;
import app.tuxguitar.editor.action.effect.TGChangeTremoloPickingAction;
import app.tuxguitar.editor.action.effect.TGChangeTrillNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeVibratoNoteAction;
import app.tuxguitar.editor.action.file.TGLoadSongAction;
import app.tuxguitar.editor.action.file.TGLoadTemplateAction;
import app.tuxguitar.editor.action.file.TGNewSongAction;
import app.tuxguitar.editor.action.file.TGReadSongAction;
import app.tuxguitar.editor.action.file.TGWriteSongAction;
import app.tuxguitar.editor.action.measure.TGAddMeasureAction;
import app.tuxguitar.editor.action.measure.TGAddMeasureListAction;
import app.tuxguitar.editor.action.measure.TGCleanMeasureAction;
import app.tuxguitar.editor.action.measure.TGCleanMeasureListAction;
import app.tuxguitar.editor.action.measure.TGCopyMeasureAction;
import app.tuxguitar.editor.action.measure.TGCopyMeasureFromAction;
import app.tuxguitar.editor.action.measure.TGFixMeasureVoiceAction;
import app.tuxguitar.editor.action.measure.TGInsertMeasuresAction;
import app.tuxguitar.editor.action.measure.TGPasteMeasureAction;
import app.tuxguitar.editor.action.measure.TGRemoveMeasureAction;
import app.tuxguitar.editor.action.measure.TGRemoveMeasureRangeAction;
import app.tuxguitar.editor.action.measure.TGRemoveUnusedVoiceAction;
import app.tuxguitar.editor.action.note.TGChangeNoteAction;
import app.tuxguitar.editor.action.note.TGChangeStrokeAction;
import app.tuxguitar.editor.action.note.TGChangePickStrokeDownAction;
import app.tuxguitar.editor.action.note.TGChangePickStrokeUpAction;
import app.tuxguitar.editor.action.note.TGChangeTiedNoteAction;
import app.tuxguitar.editor.action.note.TGChangeVelocityAction;
import app.tuxguitar.editor.action.note.TGCleanBeatAction;
import app.tuxguitar.editor.action.note.TGDecrementNoteSemitoneAction;
import app.tuxguitar.editor.action.note.TGDeleteNoteOrRestAction;
import app.tuxguitar.editor.action.note.TGIncrementNoteSemitoneAction;
import app.tuxguitar.editor.action.note.TGInsertRestBeatAction;
import app.tuxguitar.editor.action.note.TGInsertTextAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsLeftAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsRightAction;
import app.tuxguitar.editor.action.note.TGRemoveTextAction;
import app.tuxguitar.editor.action.note.TGSetVoiceAutoAction;
import app.tuxguitar.editor.action.note.TGSetVoiceDownAction;
import app.tuxguitar.editor.action.note.TGSetVoiceUpAction;
import app.tuxguitar.editor.action.note.TGShiftNoteDownAction;
import app.tuxguitar.editor.action.note.TGShiftNoteUpAction;
import app.tuxguitar.editor.action.song.TGClearSongAction;
import app.tuxguitar.editor.action.song.TGCopySongFromAction;
import app.tuxguitar.editor.action.track.TGAddNewTrackAction;
import app.tuxguitar.editor.action.track.TGAddTrackAction;
import app.tuxguitar.editor.action.track.TGChangeTrackMuteAction;
import app.tuxguitar.editor.action.track.TGChangeTrackPropertiesAction;
import app.tuxguitar.editor.action.track.TGChangeTrackSoloAction;
import app.tuxguitar.editor.action.track.TGChangeTrackTuningAction;
import app.tuxguitar.editor.action.track.TGCloneTrackAction;
import app.tuxguitar.editor.action.track.TGCopyTrackFromAction;
import app.tuxguitar.editor.action.track.TGMoveTrackDownAction;
import app.tuxguitar.editor.action.track.TGMoveTrackUpAction;
import app.tuxguitar.editor.action.track.TGRemoveTrackAction;
import app.tuxguitar.editor.action.track.TGSetTrackChannelAction;
import app.tuxguitar.editor.action.track.TGSetTrackInfoAction;
import app.tuxguitar.editor.action.track.TGSetTrackMuteAction;
import app.tuxguitar.editor.action.track.TGSetTrackNameAction;
import app.tuxguitar.editor.action.track.TGSetTrackSoloAction;
import app.tuxguitar.editor.action.track.TGSetTrackStringCountAction;
import app.tuxguitar.editor.action.transport.TGTransportCountDownAction;
import app.tuxguitar.editor.action.transport.TGTransportMetronomeAction;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.impl.channel.TGUndoableChannelGenericController;
import app.tuxguitar.editor.undo.impl.channel.TGUndoableModifyChannelController;
import app.tuxguitar.editor.undo.impl.custom.TGUndoableAltRepeatController;
import app.tuxguitar.editor.undo.impl.custom.TGUndoableClefController;
import app.tuxguitar.editor.undo.impl.custom.TGUndoableCloseRepeatController;
import app.tuxguitar.editor.undo.impl.custom.TGUndoableKeySignatureController;
import app.tuxguitar.editor.undo.impl.custom.TGUndoableOpenRepeatController;
import app.tuxguitar.editor.undo.impl.custom.TGUndoableSongInfoController;
import app.tuxguitar.editor.undo.impl.custom.TGUndoableTempoController;
import app.tuxguitar.editor.undo.impl.custom.TGUndoableTimeSignatureController;
import app.tuxguitar.editor.undo.impl.custom.TGUndoableTripletFeelController;
import app.tuxguitar.editor.undo.impl.measure.TGUndoableAddMeasureController;
import app.tuxguitar.editor.undo.impl.measure.TGUndoableMeasureGenericController;
import app.tuxguitar.editor.undo.impl.measure.TGUndoableRemoveMeasureController;
import app.tuxguitar.editor.undo.impl.song.TGUndoableSongGenericController;
import app.tuxguitar.editor.undo.impl.track.TGUndoableAddTrackController;
import app.tuxguitar.editor.undo.impl.track.TGUndoableCloneTrackController;
import app.tuxguitar.editor.undo.impl.track.TGUndoableMoveTrackDownController;
import app.tuxguitar.editor.undo.impl.track.TGUndoableMoveTrackUpController;
import app.tuxguitar.editor.undo.impl.track.TGUndoableRemoveTrackController;
import app.tuxguitar.editor.undo.impl.track.TGUndoableTrackGenericController;
import app.tuxguitar.editor.undo.impl.track.TGUndoableTrackInfoController;
import app.tuxguitar.editor.undo.impl.track.TGUndoableTrackSoloMuteController;

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
	private static final TGUpdateController UPDATE_CHANNELS_CTL = new TGUpdateChannelsController();

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
		this.map(TGSetTrackStringCountAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC);
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
		this.map(TGCopyMeasureAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL);
		this.map(TGPasteMeasureAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_SONG_GENERIC);
		this.map(TGFixMeasureVoiceAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);

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
		this.map(TGChangePickStrokeDownAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangePickStrokeUpAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
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
		this.map(TGSetChannelsAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_CHANNELS_CTL, UNDOABLE_CHANNEL_GENERIC);
		this.map(TGAddChannelAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_CHANNELS_CTL, UNDOABLE_CHANNEL_GENERIC);
		this.map(TGAddNewChannelAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_CHANNELS_CTL, UNDOABLE_CHANNEL_GENERIC);
		this.map(TGRemoveChannelAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_CHANNELS_CTL, UNDOABLE_CHANNEL_GENERIC);
		this.map(TGUpdateChannelAction.NAME, LOCKABLE, new TGUpdateModifiedChannelController(), new TGUndoableModifyChannelController());

		//transport actions
		this.map(TGTransportPlayAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGTransportStopAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGTransportMetronomeAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGTransportCountDownAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);
		this.map(TGTransportLoadSettingsAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL);

		//layout actions
		this.map(TGSetLayoutScaleAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL);
		this.map(TGSetScoreEnabledAction.NAME, LOCKABLE, UPDATE_SONG_CTL);
		this.map(TGSetChordNameEnabledAction.NAME, LOCKABLE, UPDATE_SONG_CTL);
		this.map(TGSetChordDiagramEnabledAction.NAME, LOCKABLE, UPDATE_SONG_CTL);
		this.map(TGToggleHighlightPlayedBeatAction.NAME, LOCKABLE, UPDATE_SONG_CTL);

		//view actions
		this.map(TGToggleTabKeyboardAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_ITEMS_CTL);
		this.map(TGShowSmartMenuAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_ITEMS_CTL);

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
		this.map(TGOpenCabMenuAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_ITEMS_CTL);
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
