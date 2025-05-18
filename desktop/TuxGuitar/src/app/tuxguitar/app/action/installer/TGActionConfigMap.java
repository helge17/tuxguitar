package app.tuxguitar.app.action.installer;

import app.tuxguitar.app.action.TGActionMap;
import app.tuxguitar.app.action.impl.caret.TGGoDownAction;
import app.tuxguitar.app.action.impl.caret.TGGoLeftAction;
import app.tuxguitar.app.action.impl.caret.TGGoRightAction;
import app.tuxguitar.app.action.impl.caret.TGGoUpAction;
import app.tuxguitar.app.action.impl.caret.TGMoveToAction;
import app.tuxguitar.app.action.impl.composition.TGOpenClefDialogAction;
import app.tuxguitar.app.action.impl.composition.TGOpenKeySignatureDialogAction;
import app.tuxguitar.app.action.impl.composition.TGOpenSongInfoDialogAction;
import app.tuxguitar.app.action.impl.composition.TGOpenTempoDialogAction;
import app.tuxguitar.app.action.impl.composition.TGOpenTimeSignatureDialogAction;
import app.tuxguitar.app.action.impl.composition.TGOpenTripletFeelDialogAction;
import app.tuxguitar.app.action.impl.edit.TGCopyAction;
import app.tuxguitar.app.action.impl.edit.TGCutAction;
import app.tuxguitar.app.action.impl.edit.TGOpenMeasureErrorsDialogAction;
import app.tuxguitar.app.action.impl.edit.TGPasteAction;
import app.tuxguitar.app.action.impl.edit.TGRepeatAction;
import app.tuxguitar.app.action.impl.edit.TGSetMouseModeEditionAction;
import app.tuxguitar.app.action.impl.edit.TGSetMouseModeSelectionAction;
import app.tuxguitar.app.action.impl.edit.TGSetNaturalKeyAction;
import app.tuxguitar.app.action.impl.edit.TGSetVoice1Action;
import app.tuxguitar.app.action.impl.edit.TGSetVoice2Action;
import app.tuxguitar.app.action.impl.edit.TGToggleFreeEditionModeAction;
import app.tuxguitar.app.action.impl.edit.tablature.TGMenuShownAction;
import app.tuxguitar.app.action.impl.edit.tablature.TGMouseClickAction;
import app.tuxguitar.app.action.impl.edit.tablature.TGMouseExitAction;
import app.tuxguitar.app.action.impl.edit.tablature.TGMouseMoveAction;
import app.tuxguitar.app.action.impl.effects.TGOpenBendDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenGraceDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenHarmonicDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenTremoloBarDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenTremoloPickingDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenTrillDialogAction;
import app.tuxguitar.app.action.impl.file.TGCloseAllDocumentsAction;
import app.tuxguitar.app.action.impl.file.TGCloseCurrentDocumentAction;
import app.tuxguitar.app.action.impl.file.TGCloseDocumentAction;
import app.tuxguitar.app.action.impl.file.TGCloseDocumentsAction;
import app.tuxguitar.app.action.impl.file.TGCloseOtherDocumentsAction;
import app.tuxguitar.app.action.impl.file.TGCustomTemplateDeleteAction;
import app.tuxguitar.app.action.impl.file.TGCustomTemplateSelectAction;
import app.tuxguitar.app.action.impl.file.TGExitAction;
import app.tuxguitar.app.action.impl.file.TGExportSongAction;
import app.tuxguitar.app.action.impl.file.TGImportSongAction;
import app.tuxguitar.app.action.impl.file.TGOpenFileAction;
import app.tuxguitar.app.action.impl.file.TGOpenURLAction;
import app.tuxguitar.app.action.impl.file.TGPrintAction;
import app.tuxguitar.app.action.impl.file.TGPrintPreviewAction;
import app.tuxguitar.app.action.impl.file.TGReadURLAction;
import app.tuxguitar.app.action.impl.file.TGSaveAsFileAction;
import app.tuxguitar.app.action.impl.file.TGSaveFileAction;
import app.tuxguitar.app.action.impl.file.TGWriteFileAction;
import app.tuxguitar.app.action.impl.help.TGHelpGoHomeAction;
import app.tuxguitar.app.action.impl.help.TGOpenAboutDialogAction;
import app.tuxguitar.app.action.impl.help.TGOpenDocumentationDialogAction;
import app.tuxguitar.app.action.impl.insert.TGOpenChordDialogAction;
import app.tuxguitar.app.action.impl.insert.TGOpenRepeatAlternativeDialogAction;
import app.tuxguitar.app.action.impl.insert.TGOpenRepeatCloseDialogAction;
import app.tuxguitar.app.action.impl.insert.TGOpenTextDialogAction;
import app.tuxguitar.app.action.impl.layout.TGSetChordDiagramEnabledAction;
import app.tuxguitar.app.action.impl.layout.TGSetChordNameEnabledAction;
import app.tuxguitar.app.action.impl.layout.TGSetCompactViewAction;
import app.tuxguitar.app.action.impl.layout.TGSetLayoutScaleAction;
import app.tuxguitar.app.action.impl.layout.TGSetLayoutScaleDecrementAction;
import app.tuxguitar.app.action.impl.layout.TGSetLayoutScaleIncrementAction;
import app.tuxguitar.app.action.impl.layout.TGSetLayoutScaleResetAction;
import app.tuxguitar.app.action.impl.layout.TGSetLinearLayoutAction;
import app.tuxguitar.app.action.impl.layout.TGSetMultitrackViewAction;
import app.tuxguitar.app.action.impl.layout.TGSetPageLayoutAction;
import app.tuxguitar.app.action.impl.layout.TGSetScoreEnabledAction;
import app.tuxguitar.app.action.impl.layout.TGSetTablatureEnabledAction;
import app.tuxguitar.app.action.impl.layout.TGToggleHighlightPlayedBeatAction;
import app.tuxguitar.app.action.impl.marker.TGGoFirstMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGGoLastMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGGoNextMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGGoPreviousMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGGoToMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGModifyMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGOpenMarkerEditorAction;
import app.tuxguitar.app.action.impl.marker.TGRemoveMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGToggleMarkerListAction;
import app.tuxguitar.app.action.impl.marker.TGUpdateMarkerAction;
import app.tuxguitar.app.action.impl.measure.TGGoFirstMeasureAction;
import app.tuxguitar.app.action.impl.measure.TGGoLastMeasureAction;
import app.tuxguitar.app.action.impl.measure.TGGoNextMeasureAction;
import app.tuxguitar.app.action.impl.measure.TGGoPreviousMeasureAction;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasureAddDialogAction;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasureCleanDialogAction;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasureCopyDialogAction;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasurePasteDialogAction;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasureRemoveDialogAction;
import app.tuxguitar.app.action.impl.measure.TGToggleLineBreakAction;
import app.tuxguitar.app.action.impl.note.TGOpenBeatMoveDialogAction;
import app.tuxguitar.app.action.impl.note.TGOpenStrokeDownDialogAction;
import app.tuxguitar.app.action.impl.note.TGOpenStrokeUpDialogAction;
import app.tuxguitar.app.action.impl.selector.*;
import app.tuxguitar.app.action.impl.settings.TGOpenKeyBindingEditorAction;
import app.tuxguitar.app.action.impl.settings.TGOpenPluginListDialogAction;
import app.tuxguitar.app.action.impl.settings.TGOpenSettingsEditorAction;
import app.tuxguitar.app.action.impl.settings.TGReloadSkinAction;
import app.tuxguitar.app.action.impl.settings.TGReloadLanguageAction;
import app.tuxguitar.app.action.impl.settings.TGReloadMidiDevicesAction;
import app.tuxguitar.app.action.impl.settings.TGReloadSettingsAction;
import app.tuxguitar.app.action.impl.settings.TGReloadStylesAction;
import app.tuxguitar.app.action.impl.settings.TGReloadTableSettingsAction;
import app.tuxguitar.app.action.impl.settings.TGReloadTitleAction;
import app.tuxguitar.app.action.impl.system.TGDisposeAction;
import app.tuxguitar.app.action.impl.tools.TGOpenScaleDialogAction;
import app.tuxguitar.app.action.impl.tools.TGOpenTransposeDialogAction;
import app.tuxguitar.app.action.impl.tools.TGSelectScaleAction;
import app.tuxguitar.app.action.impl.tools.TGToggleBrowserAction;
import app.tuxguitar.app.action.impl.tools.TGTransposeAction;
import app.tuxguitar.app.action.impl.track.TGGoFirstTrackAction;
import app.tuxguitar.app.action.impl.track.TGGoLastTrackAction;
import app.tuxguitar.app.action.impl.track.TGGoNextTrackAction;
import app.tuxguitar.app.action.impl.track.TGGoPreviousTrackAction;
import app.tuxguitar.app.action.impl.track.TGGoToTrackAction;
import app.tuxguitar.app.action.impl.track.TGOpenTrackPropertiesDialogAction;
import app.tuxguitar.app.action.impl.track.TGOpenTrackTuningDialogAction;
import app.tuxguitar.app.action.impl.track.TGToggleLyricEditorAction;
import app.tuxguitar.app.action.impl.transport.TGOpenTransportModeDialogAction;
import app.tuxguitar.app.action.impl.transport.TGTransportCountDownAction;
import app.tuxguitar.app.action.impl.transport.TGTransportMetronomeAction;
import app.tuxguitar.app.action.impl.transport.TGTransportModeAction;
import app.tuxguitar.app.action.impl.transport.TGTransportPlayPauseAction;
import app.tuxguitar.app.action.impl.transport.TGTransportPlayStopAction;
import app.tuxguitar.app.action.impl.transport.TGTransportSetLoopEHeaderAction;
import app.tuxguitar.app.action.impl.transport.TGTransportSetLoopSHeaderAction;
import app.tuxguitar.app.action.impl.transport.TGTransportStopAction;
import app.tuxguitar.app.action.impl.view.TGHideExternalBeatAction;
import app.tuxguitar.app.action.impl.view.TGOpenMainToolBarSettingsDialogAction;
import app.tuxguitar.app.action.impl.view.TGOpenViewAction;
import app.tuxguitar.app.action.impl.view.TGShowExternalBeatAction;
import app.tuxguitar.app.action.impl.view.TGToggleChannelsDialogAction;
import app.tuxguitar.app.action.impl.view.TGToggleEditToolbarAction;
import app.tuxguitar.app.action.impl.view.TGToggleFretBoardEditorAction;
import app.tuxguitar.app.action.impl.view.TGToggleMainToolbarAction;
import app.tuxguitar.app.action.impl.view.TGToggleMatrixEditorAction;
import app.tuxguitar.app.action.impl.view.TGTogglePianoEditorAction;
import app.tuxguitar.app.action.impl.view.TGToggleTableViewerAction;
import app.tuxguitar.app.action.impl.view.TGToggleTransportDialogAction;
import app.tuxguitar.app.action.impl.view.TGToggleViewAction;
import app.tuxguitar.app.action.listener.cache.TGUpdateController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateAddedMeasureController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateAddedTrackController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateBeatRangeController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateChannelsController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateItemsController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateItemsOnSuccessController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateLoadedSongController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateMeasureController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateModifiedChannelController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateModifiedDurationController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateModifiedMarkerController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateModifiedNoteController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateModifiedVelocityController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateNoteRangeController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdatePlayerTracksController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateReadSongController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateRemovedMeasureController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateRemovedTrackController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateSavedSongController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateShiftedNoteController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateSongController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateSongInfoController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateTransportPositionController;
import app.tuxguitar.app.action.listener.cache.controller.TGUpdateWrittenFileController;
import app.tuxguitar.app.undo.impl.marker.TGUndoableMarkerGenericController;
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
import app.tuxguitar.editor.action.duration.TGChangeDivisionTypeDurationAction;
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
import app.tuxguitar.editor.action.note.TGChangePickStrokeUpAction;
import app.tuxguitar.editor.action.note.TGChangePickStrokeDownAction;
import app.tuxguitar.editor.action.note.TGChangeStrokeAction;
import app.tuxguitar.editor.action.note.TGChangeTiedNoteAction;
import app.tuxguitar.editor.action.note.TGChangeVelocityAction;
import app.tuxguitar.editor.action.note.TGCleanBeatAction;
import app.tuxguitar.editor.action.note.TGDecrementNoteSemitoneAction;
import app.tuxguitar.editor.action.note.TGDeleteNoteAction;
import app.tuxguitar.editor.action.note.TGDeleteNoteOrRestAction;
import app.tuxguitar.editor.action.note.TGIncrementNoteSemitoneAction;
import app.tuxguitar.editor.action.note.TGInsertChordAction;
import app.tuxguitar.editor.action.note.TGInsertRestBeatAction;
import app.tuxguitar.editor.action.note.TGInsertTextAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsLeftAction;
import app.tuxguitar.editor.action.note.TGMoveBeatsRightAction;
import app.tuxguitar.editor.action.note.TGRemoveChordAction;
import app.tuxguitar.editor.action.note.TGRemoveTextAction;
import app.tuxguitar.editor.action.note.TGSetNoteFretNumberAction;
import app.tuxguitar.editor.action.note.TGSetVoiceAutoAction;
import app.tuxguitar.editor.action.note.TGSetVoiceDownAction;
import app.tuxguitar.editor.action.note.TGSetVoiceUpAction;
import app.tuxguitar.editor.action.note.TGShiftNoteDownAction;
import app.tuxguitar.editor.action.note.TGShiftNoteUpAction;
import app.tuxguitar.editor.action.note.TGToggleNoteEnharmonicAction;
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
import app.tuxguitar.editor.action.track.TGSetTrackLyricsAction;
import app.tuxguitar.editor.action.track.TGSetTrackMuteAction;
import app.tuxguitar.editor.action.track.TGSetTrackNameAction;
import app.tuxguitar.editor.action.track.TGSetTrackSoloAction;
import app.tuxguitar.editor.action.track.TGSetTrackStringCountAction;
import app.tuxguitar.editor.undo.TGUndoableActionController;
import app.tuxguitar.editor.undo.impl.channel.TGUndoableChannelGenericController;
import app.tuxguitar.editor.undo.impl.channel.TGUndoableModifyChannelController;
import app.tuxguitar.editor.undo.impl.custom.TGUndoableAltRepeatController;
import app.tuxguitar.editor.undo.impl.custom.TGUndoableBeatRangeController;
import app.tuxguitar.editor.undo.impl.custom.TGUndoableClefController;
import app.tuxguitar.editor.undo.impl.custom.TGUndoableCloseRepeatController;
import app.tuxguitar.editor.undo.impl.custom.TGUndoableKeySignatureController;
import app.tuxguitar.editor.undo.impl.custom.TGUndoableNoteRangeController;
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
import app.tuxguitar.editor.undo.impl.track.TGUndoableTrackLyricsController;
import app.tuxguitar.editor.undo.impl.track.TGUndoableTrackSoloMuteController;


public class TGActionConfigMap extends TGActionMap<TGActionConfig> {

	public static final int LOCKABLE = 0x01;
	public static final int SYNC_THREAD = 0x02;
	public static final int SHORTCUT = 0x04;
	public static final int DISABLE_ON_PLAY = 0x08;
	public static final int STOP_TRANSPORT = 0x10;
	public static final int SAVE_BEFORE = 0x20;
	public static final int CONFIRM_IF_INVALID = 0x40;

	private static final TGUpdateController UPDATE_ITEMS_CTL = new TGUpdateItemsController();
	private static final TGUpdateController UPDATE_MEASURE_CTL = new TGUpdateMeasureController();
	private static final TGUpdateController UPDATE_SONG_CTL = new TGUpdateSongController();
	private static final TGUpdateController UPDATE_SONG_LOADED_CTL = new TGUpdateLoadedSongController();
	private static final TGUpdateController UPDATE_SONG_READ_CTL = new TGUpdateReadSongController();
	private static final TGUpdateController UPDATE_SONG_SAVED_CTL = new TGUpdateSavedSongController();
	private static final TGUpdateController UPDATE_CHANNELS_CTL = new TGUpdateChannelsController();
	private static final TGUpdateController UPDATE_NOTE_RANGE_CTL = new TGUpdateNoteRangeController();
	private static final TGUpdateController UPDATE_BEAT_RANGE_CTL = new TGUpdateBeatRangeController();

	private static final TGUndoableActionController UNDOABLE_SONG_GENERIC = new TGUndoableSongGenericController();
	private static final TGUndoableActionController UNDOABLE_MEASURE_GENERIC = new TGUndoableMeasureGenericController();
	private static final TGUndoableActionController UNDOABLE_TRACK_GENERIC = new TGUndoableTrackGenericController();
	private static final TGUndoableActionController UNDOABLE_CHANNEL_GENERIC = new TGUndoableChannelGenericController();
	private static final TGUndoableActionController UNDOABLE_NOTE_RANGE = new TGUndoableNoteRangeController();
	private static final TGUndoableActionController UNDOABLE_BEAT_RANGE_GENERIC = new TGUndoableBeatRangeController();

	public TGActionConfigMap() {
		this.createConfigMap();
	}

	public void createConfigMap() {
		//system actions
		this.map(TGDisposeAction.NAME, LOCKABLE | SYNC_THREAD | SAVE_BEFORE);

		//file actions
		this.map(TGLoadSongAction.NAME, LOCKABLE | STOP_TRANSPORT, UPDATE_SONG_LOADED_CTL);
		this.map(TGNewSongAction.NAME, LOCKABLE | STOP_TRANSPORT);
		this.map(TGLoadTemplateAction.NAME, LOCKABLE | STOP_TRANSPORT | SHORTCUT);
		this.map(TGReadSongAction.NAME, LOCKABLE, UPDATE_SONG_READ_CTL);
		this.map(TGWriteSongAction.NAME, LOCKABLE, UPDATE_SONG_SAVED_CTL);
		this.map(TGWriteFileAction.NAME, LOCKABLE, new TGUpdateWrittenFileController());
		this.map(TGSaveAsFileAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT | CONFIRM_IF_INVALID);
		this.map(TGSaveFileAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT | CONFIRM_IF_INVALID);
		this.map(TGReadURLAction.NAME, LOCKABLE | STOP_TRANSPORT, UPDATE_ITEMS_CTL);
		this.map(TGOpenFileAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT);
		this.map(TGImportSongAction.NAME, LOCKABLE);
		this.map(TGExportSongAction.NAME, LOCKABLE | CONFIRM_IF_INVALID);
		this.map(TGCloseDocumentsAction.NAME, LOCKABLE | SAVE_BEFORE, UPDATE_ITEMS_CTL);
		this.map(TGCloseDocumentAction.NAME, LOCKABLE | STOP_TRANSPORT);
		this.map(TGCloseCurrentDocumentAction.NAME, LOCKABLE | STOP_TRANSPORT | SHORTCUT);
		this.map(TGCloseOtherDocumentsAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGCloseAllDocumentsAction.NAME, LOCKABLE | STOP_TRANSPORT | SHORTCUT);
		this.map(TGExitAction.NAME, LOCKABLE | SYNC_THREAD);
		this.map(TGPrintAction.NAME, LOCKABLE | SHORTCUT | CONFIRM_IF_INVALID);
		this.map(TGPrintPreviewAction.NAME, LOCKABLE | SHORTCUT | CONFIRM_IF_INVALID);

		//edit actions
		this.map(TGCutAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGCopyAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGPasteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_SONG_CTL, UNDOABLE_SONG_GENERIC);
		this.map(TGRepeatAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_SONG_CTL, UNDOABLE_SONG_GENERIC);
		this.map(TGUndoAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGRedoAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetMouseModeSelectionAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetMouseModeEditionAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetNaturalKeyAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetVoice1Action.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetVoice2Action.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGToggleFreeEditionModeAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenMeasureErrorsDialogAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGFixMeasureVoiceAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);

		//tablature actions
		this.map(TGMouseClickAction.NAME, LOCKABLE);
		this.map(TGMouseMoveAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateItemsOnSuccessController());
		this.map(TGMouseExitAction.NAME, LOCKABLE | DISABLE_ON_PLAY);
		this.map(TGMenuShownAction.NAME, LOCKABLE);

		//caret actions
		this.map(TGMoveToAction.NAME, LOCKABLE, new TGUpdateTransportPositionController());
		this.map(TGGoRightAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGGoLeftAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGGoUpAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGGoDownAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);

		//selector actions
		this.map(TGExtendSelectionLeftAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGExtendSelectionRightAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGExtendSelectionPreviousAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGExtendSelectionNextAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGExtendSelectionFirstAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGExtendSelectionLastAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSelectAllAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGClearSelectionAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGStartDragSelectionAction.NAME, LOCKABLE | DISABLE_ON_PLAY);
		this.map(TGUpdateDragSelectionAction.NAME, LOCKABLE | DISABLE_ON_PLAY);

		//song actions
		this.map(TGCopySongFromAction.NAME, LOCKABLE, UPDATE_SONG_CTL, UNDOABLE_SONG_GENERIC);
		this.map(TGClearSongAction.NAME, LOCKABLE | DISABLE_ON_PLAY);

		//track actions
		this.map(TGAddNewTrackAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, new TGUpdateAddedTrackController(), new TGUndoableAddTrackController());
		this.map(TGAddTrackAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateAddedTrackController(), new TGUndoableAddTrackController());
		this.map(TGSetTrackMuteAction.NAME, LOCKABLE, new TGUpdatePlayerTracksController(), new TGUndoableTrackSoloMuteController());
		this.map(TGSetTrackSoloAction.NAME, LOCKABLE, new TGUpdatePlayerTracksController(), new TGUndoableTrackSoloMuteController());
		this.map(TGChangeTrackMuteAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGChangeTrackSoloAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGCloneTrackAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_SONG_CTL, new TGUndoableCloneTrackController());
		this.map(TGGoFirstTrackAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGGoLastTrackAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGGoNextTrackAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGGoPreviousTrackAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGGoToTrackAction.NAME, LOCKABLE);
		this.map(TGMoveTrackDownAction.NAME, LOCKABLE | SHORTCUT, UPDATE_SONG_CTL, new TGUndoableMoveTrackDownController());
		this.map(TGMoveTrackUpAction.NAME, LOCKABLE | SHORTCUT, UPDATE_SONG_CTL, new TGUndoableMoveTrackUpController());
		this.map(TGRemoveTrackAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, new TGUpdateRemovedTrackController(), new TGUndoableRemoveTrackController());
		this.map(TGSetTrackInfoAction.NAME, LOCKABLE, UPDATE_SONG_CTL, new TGUndoableTrackInfoController());
		this.map(TGSetTrackNameAction.NAME, LOCKABLE);
		this.map(TGSetTrackChannelAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC);
		this.map(TGSetTrackStringCountAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC);
		this.map(TGChangeTrackTuningAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC);
		this.map(TGCopyTrackFromAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC);
		this.map(TGSetTrackLyricsAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL, new TGUndoableTrackLyricsController());
		this.map(TGChangeTrackPropertiesAction.NAME, LOCKABLE | DISABLE_ON_PLAY);

		//measure actions
		this.map(TGToggleLineBreakAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL, UNDOABLE_SONG_GENERIC);
		this.map(TGAddMeasureAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateAddedMeasureController(), new TGUndoableAddMeasureController());
		this.map(TGAddMeasureListAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL);
		this.map(TGCleanMeasureAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGCleanMeasureListAction.NAME, LOCKABLE |DISABLE_ON_PLAY);
		this.map(TGGoFirstMeasureAction.NAME, LOCKABLE |SHORTCUT);
		this.map(TGGoLastMeasureAction.NAME, LOCKABLE |SHORTCUT);
		this.map(TGGoNextMeasureAction.NAME, LOCKABLE |SHORTCUT);
		this.map(TGGoPreviousMeasureAction.NAME, LOCKABLE |SHORTCUT);
		this.map(TGRemoveMeasureAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateRemovedMeasureController(), new TGUndoableRemoveMeasureController());
		this.map(TGRemoveMeasureRangeAction.NAME, LOCKABLE |DISABLE_ON_PLAY);
		this.map(TGCopyMeasureFromAction.NAME, LOCKABLE, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGInsertMeasuresAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_SONG_GENERIC);
		this.map(TGCopyMeasureAction.NAME, LOCKABLE |DISABLE_ON_PLAY);
		this.map(TGPasteMeasureAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_SONG_GENERIC);

		//beat actions
		this.map(TGChangeNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateModifiedNoteController(), UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangePickStrokeUpAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangePickStrokeDownAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeTiedNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeVelocityAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateModifiedVelocityController(), UNDOABLE_NOTE_RANGE);
		this.map(TGCleanBeatAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_BEAT_RANGE_CTL, UNDOABLE_BEAT_RANGE_GENERIC);
		this.map(TGDecrementNoteSemitoneAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGDeleteNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGDeleteNoteOrRestAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_BEAT_RANGE_CTL, UNDOABLE_BEAT_RANGE_GENERIC);
		this.map(TGIncrementNoteSemitoneAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGInsertRestBeatAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGMoveBeatsAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC);
		this.map(TGMoveBeatsLeftAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGMoveBeatsRightAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGRemoveUnusedVoiceAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_BEAT_RANGE_CTL, UNDOABLE_BEAT_RANGE_GENERIC);
		this.map(TGSetVoiceAutoAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGSetVoiceDownAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGSetVoiceUpAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGShiftNoteDownAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, new TGUpdateShiftedNoteController(), UNDOABLE_NOTE_RANGE);
		this.map(TGShiftNoteUpAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, new TGUpdateShiftedNoteController(), UNDOABLE_NOTE_RANGE);
		this.map(TGChangeStrokeAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGInsertTextAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGRemoveTextAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGInsertChordAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGRemoveChordAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		for( int i = 0 ; i < 10 ; i ++ ){
			this.map(TGSetNoteFretNumberAction.getActionName(i), LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		}
		this.map(TGToggleNoteEnharmonicAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);

		//effect actions
		this.map(TGChangeDeadNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_BEAT_RANGE_CTL, UNDOABLE_NOTE_RANGE); // beat range controller: can create new note
		this.map(TGChangeAccentuatedNoteAction.NAME,      LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeBendNoteAction.NAME,             LOCKABLE | DISABLE_ON_PLAY,            UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE); // no shortcut (need to use dialog)
		this.map(TGChangeFadeInAction.NAME,               LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeGhostNoteAction.NAME,            LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeGraceNoteAction.NAME,            LOCKABLE | DISABLE_ON_PLAY,            UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE); // no shortcut (need to use dialog)
		this.map(TGChangeHammerNoteAction.NAME,           LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeHarmonicNoteAction.NAME,         LOCKABLE | DISABLE_ON_PLAY,            UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE); // no shortcut (need to use dialog)
		this.map(TGChangeHeavyAccentuatedNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeLetRingAction.NAME,              LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangePalmMuteAction.NAME,             LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangePoppingAction.NAME,              LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeSlappingAction.NAME,             LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeSlideNoteAction.NAME,            LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeStaccatoAction.NAME,             LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeTappingAction.NAME,              LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);
		this.map(TGChangeTremoloBarAction.NAME,           LOCKABLE | DISABLE_ON_PLAY,            UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE); // no shortcut (need to use dialog)
		this.map(TGChangeTremoloPickingAction.NAME,       LOCKABLE | DISABLE_ON_PLAY,            UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE); // no shortcut (need to use dialog)
		this.map(TGChangeTrillNoteAction.NAME,            LOCKABLE | DISABLE_ON_PLAY,            UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE); // no shortcut (need to use dialog)
		this.map(TGChangeVibratoNoteAction.NAME,          LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_NOTE_RANGE_CTL, UNDOABLE_NOTE_RANGE);

		//duration actions
		this.map(TGSetDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateModifiedDurationController(), UNDOABLE_BEAT_RANGE_GENERIC);
		this.map(TGSetWholeDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetHalfDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetQuarterDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetEighthDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetSixteenthDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetThirtySecondDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetSixtyFourthDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetDivisionTypeDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY);
		this.map(TGChangeDivisionTypeDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGChangeDottedDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGChangeDoubleDottedDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGIncrementDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGDecrementDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGChangeTempoAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, new TGUndoableTempoController());
		this.map(TGChangeTempoRangeAction.NAME, LOCKABLE | DISABLE_ON_PLAY);
		this.map(TGChangeClefAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, new TGUndoableClefController());
		this.map(TGChangeTimeSignatureAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, new TGUndoableTimeSignatureController());
		this.map(TGChangeKeySignatureAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, new TGUndoableKeySignatureController());
		this.map(TGChangeTripletFeelAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, new TGUndoableTripletFeelController());

		//composition actions
		this.map(TGChangeInfoAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateSongInfoController(), new TGUndoableSongInfoController());
		this.map(TGRepeatOpenAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, new TGUndoableOpenRepeatController());
		this.map(TGRepeatCloseAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, new TGUndoableCloseRepeatController());
		this.map(TGRepeatAlternativeAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, new TGUndoableAltRepeatController());

		//channel actions
		this.map(TGSetChannelsAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_CHANNELS_CTL, UNDOABLE_CHANNEL_GENERIC);
		this.map(TGAddChannelAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_CHANNELS_CTL, UNDOABLE_CHANNEL_GENERIC);
		this.map(TGAddNewChannelAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_CHANNELS_CTL, UNDOABLE_CHANNEL_GENERIC);
		this.map(TGRemoveChannelAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_CHANNELS_CTL, UNDOABLE_CHANNEL_GENERIC);
		this.map(TGUpdateChannelAction.NAME, LOCKABLE, new TGUpdateModifiedChannelController(), new TGUndoableModifyChannelController());

		//transport actions
		this.map(TGTransportPlayPauseAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGTransportPlayStopAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGTransportStopAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGTransportMetronomeAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGTransportCountDownAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGTransportModeAction.NAME, LOCKABLE);
		this.map(TGTransportSetLoopSHeaderAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGTransportSetLoopEHeaderAction.NAME, LOCKABLE | SHORTCUT);

		//marker actions
		this.map(TGUpdateMarkerAction.NAME, LOCKABLE, new TGUpdateModifiedMarkerController(), new TGUndoableMarkerGenericController());
		this.map(TGRemoveMarkerAction.NAME, LOCKABLE, new TGUpdateModifiedMarkerController(), new TGUndoableMarkerGenericController());
		this.map(TGModifyMarkerAction.NAME, LOCKABLE);
		this.map(TGGoToMarkerAction.NAME, LOCKABLE);
		this.map(TGGoPreviousMarkerAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGGoNextMarkerAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGGoFirstMarkerAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGGoLastMarkerAction.NAME, LOCKABLE | SHORTCUT);

		//layout actions
		this.map(TGSetPageLayoutAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT, UPDATE_SONG_CTL);
		this.map(TGSetLinearLayoutAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT, UPDATE_SONG_CTL);
		this.map(TGSetMultitrackViewAction.NAME, LOCKABLE | SHORTCUT, UPDATE_SONG_CTL);
		this.map(TGSetScoreEnabledAction.NAME, LOCKABLE | SHORTCUT, UPDATE_SONG_CTL);
		this.map(TGSetTablatureEnabledAction.NAME, LOCKABLE | SHORTCUT, UPDATE_SONG_CTL);
		this.map(TGSetCompactViewAction.NAME, LOCKABLE | SHORTCUT, UPDATE_SONG_CTL);
		this.map(TGSetChordNameEnabledAction.NAME, LOCKABLE | SHORTCUT, UPDATE_SONG_CTL);
		this.map(TGSetChordDiagramEnabledAction.NAME, LOCKABLE | SHORTCUT, UPDATE_SONG_CTL);
		this.map(TGToggleHighlightPlayedBeatAction.NAME, LOCKABLE | SHORTCUT, UPDATE_SONG_CTL);

		this.map(TGSetLayoutScaleAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGSetLayoutScaleIncrementAction.NAME, LOCKABLE | SHORTCUT | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGSetLayoutScaleDecrementAction.NAME, LOCKABLE | SHORTCUT | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGSetLayoutScaleResetAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);

		//tools
		this.map(TGSelectScaleAction.NAME, LOCKABLE);
		this.map(TGTransposeAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_SONG_GENERIC);
		this.map(TGShowExternalBeatAction.NAME, LOCKABLE);
		this.map(TGHideExternalBeatAction.NAME, LOCKABLE);

		//settings
		this.map(TGReloadSettingsAction.NAME, LOCKABLE, UPDATE_SONG_CTL);
		this.map(TGReloadTitleAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGReloadSkinAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGReloadLanguageAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGReloadStylesAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGReloadTableSettingsAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGReloadMidiDevicesAction.NAME, LOCKABLE, UPDATE_SONG_CTL);
		this.map(TGOpenSettingsEditorAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT);
		this.map(TGOpenKeyBindingEditorAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT);
		this.map(TGOpenPluginListDialogAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT);

		//gui actions
		this.map(TGOpenViewAction.NAME, LOCKABLE | SYNC_THREAD);
		this.map(TGToggleViewAction.NAME, LOCKABLE | SYNC_THREAD);
		this.map(TGOpenSongInfoDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTempoDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenClefDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenKeySignatureDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTimeSignatureDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTripletFeelDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenStrokeUpDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenStrokeDownDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenBendDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTrillDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTremoloPickingDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTremoloBarDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenHarmonicDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenGraceDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenBeatMoveDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTextDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenChordDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenRepeatCloseDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenRepeatAlternativeDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenMeasureAddDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenMeasureCleanDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenMeasureRemoveDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenMeasureCopyDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenMeasurePasteDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTrackTuningDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTrackPropertiesDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenScaleDialogAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGOpenURLAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGCustomTemplateSelectAction.NAME, LOCKABLE | SYNC_THREAD);
		this.map(TGCustomTemplateDeleteAction.NAME, LOCKABLE | SYNC_THREAD);
		this.map(TGOpenTransportModeDialogAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGOpenMarkerEditorAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGOpenTransposeDialogAction.NAME, LOCKABLE | SYNC_THREAD |DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGToggleFretBoardEditorAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGTogglePianoEditorAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGToggleMatrixEditorAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGToggleLyricEditorAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGToggleChannelsDialogAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGToggleBrowserAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGToggleTransportDialogAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGToggleMarkerListAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGToggleMainToolbarAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGToggleEditToolbarAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGToggleTableViewerAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGOpenDocumentationDialogAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGOpenAboutDialogAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGHelpGoHomeAction.NAME, LOCKABLE | SYNC_THREAD |SHORTCUT);
		this.map(TGOpenMainToolBarSettingsDialogAction.NAME, LOCKABLE | SYNC_THREAD | DISABLE_ON_PLAY);
	}

	private void map(String actionId, int flags) {
		this.map(actionId, flags, UPDATE_ITEMS_CTL, null);
	}

	private void map(String actionId, int flags, TGUpdateController updateController) {
		this.map(actionId, flags, updateController, null);
	}

	private void map(String actionId, int flags, TGUpdateController updateController, TGUndoableActionController undoableController) {
		TGActionConfig tgActionConfig = new TGActionConfig();
		tgActionConfig.setUpdateController(updateController);
		tgActionConfig.setUndoableController(undoableController);
		tgActionConfig.setLockableAction((flags & LOCKABLE) != 0);
		tgActionConfig.setShortcutAvailable((flags & SHORTCUT) != 0);
		tgActionConfig.setDisableOnPlaying((flags & DISABLE_ON_PLAY) != 0);
		tgActionConfig.setStopTransport((flags & STOP_TRANSPORT) != 0);
		tgActionConfig.setSyncThread((flags & SYNC_THREAD) != 0);
		tgActionConfig.setUnsavedInterceptor((flags & SAVE_BEFORE) != 0);
		tgActionConfig.setDocumentModifier(undoableController != null);
		tgActionConfig.setInvalidSongInterceptor((flags & CONFIRM_IF_INVALID) != 0);

		this.set(actionId, tgActionConfig);
	}
}
