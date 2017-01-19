package org.herac.tuxguitar.app.action.installer;

import org.herac.tuxguitar.app.action.TGActionMap;
import org.herac.tuxguitar.app.action.impl.caret.TGGoDownAction;
import org.herac.tuxguitar.app.action.impl.caret.TGGoLeftAction;
import org.herac.tuxguitar.app.action.impl.caret.TGGoRightAction;
import org.herac.tuxguitar.app.action.impl.caret.TGGoUpAction;
import org.herac.tuxguitar.app.action.impl.caret.TGMoveToAction;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenClefDialogAction;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenKeySignatureDialogAction;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenSongInfoDialogAction;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenTempoDialogAction;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenTimeSignatureDialogAction;
import org.herac.tuxguitar.app.action.impl.composition.TGOpenTripletFeelDialogAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetMouseModeEditionAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetMouseModeSelectionAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetNaturalKeyAction;
import org.herac.tuxguitar.app.action.impl.edit.TGSetVoice1Action;
import org.herac.tuxguitar.app.action.impl.edit.TGSetVoice2Action;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMenuShownAction;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMouseClickAction;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMouseExitAction;
import org.herac.tuxguitar.app.action.impl.edit.tablature.TGMouseMoveAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenBendDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenGraceDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenHarmonicDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenTremoloBarDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenTremoloPickingDialogAction;
import org.herac.tuxguitar.app.action.impl.effects.TGOpenTrillDialogAction;
import org.herac.tuxguitar.app.action.impl.file.TGCloseAllDocumentsAction;
import org.herac.tuxguitar.app.action.impl.file.TGCloseCurrentDocumentAction;
import org.herac.tuxguitar.app.action.impl.file.TGCloseDocumentAction;
import org.herac.tuxguitar.app.action.impl.file.TGCloseDocumentsAction;
import org.herac.tuxguitar.app.action.impl.file.TGCloseOtherDocumentsAction;
import org.herac.tuxguitar.app.action.impl.file.TGExitAction;
import org.herac.tuxguitar.app.action.impl.file.TGExportSongAction;
import org.herac.tuxguitar.app.action.impl.file.TGImportSongAction;
import org.herac.tuxguitar.app.action.impl.file.TGOpenFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGOpenURLAction;
import org.herac.tuxguitar.app.action.impl.file.TGPrintAction;
import org.herac.tuxguitar.app.action.impl.file.TGPrintPreviewAction;
import org.herac.tuxguitar.app.action.impl.file.TGReadURLAction;
import org.herac.tuxguitar.app.action.impl.file.TGSaveAsFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGSaveFileAction;
import org.herac.tuxguitar.app.action.impl.file.TGWriteFileAction;
import org.herac.tuxguitar.app.action.impl.help.TGOpenAboutDialogAction;
import org.herac.tuxguitar.app.action.impl.help.TGOpenDocumentationDialogAction;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenChordDialogAction;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenRepeatAlternativeDialogAction;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenRepeatCloseDialogAction;
import org.herac.tuxguitar.app.action.impl.insert.TGOpenTextDialogAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetChordDiagramEnabledAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetChordNameEnabledAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetCompactViewAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetLinearLayoutAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetMultitrackViewAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetPageLayoutAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetScoreEnabledAction;
import org.herac.tuxguitar.app.action.impl.layout.TGSetTablatureEnabledAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoFirstMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoLastMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoNextMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoPreviousMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGGoToMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGModifyMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGOpenMarkerEditorAction;
import org.herac.tuxguitar.app.action.impl.marker.TGRemoveMarkerAction;
import org.herac.tuxguitar.app.action.impl.marker.TGToggleMarkerListAction;
import org.herac.tuxguitar.app.action.impl.marker.TGUpdateMarkerAction;
import org.herac.tuxguitar.app.action.impl.measure.TGCopyMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.TGGoFirstMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.TGGoLastMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.TGGoNextMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.TGGoPreviousMeasureAction;
import org.herac.tuxguitar.app.action.impl.measure.TGOpenMeasureAddDialogAction;
import org.herac.tuxguitar.app.action.impl.measure.TGOpenMeasureCleanDialogAction;
import org.herac.tuxguitar.app.action.impl.measure.TGOpenMeasureCopyDialogAction;
import org.herac.tuxguitar.app.action.impl.measure.TGOpenMeasurePasteDialogAction;
import org.herac.tuxguitar.app.action.impl.measure.TGOpenMeasureRemoveDialogAction;
import org.herac.tuxguitar.app.action.impl.measure.TGPasteMeasureAction;
import org.herac.tuxguitar.app.action.impl.note.TGOpenBeatMoveDialogAction;
import org.herac.tuxguitar.app.action.impl.note.TGOpenStrokeDownDialogAction;
import org.herac.tuxguitar.app.action.impl.note.TGOpenStrokeUpDialogAction;
import org.herac.tuxguitar.app.action.impl.settings.TGOpenKeyBindingEditorAction;
import org.herac.tuxguitar.app.action.impl.settings.TGOpenPluginListDialogAction;
import org.herac.tuxguitar.app.action.impl.settings.TGOpenSettingsEditorAction;
import org.herac.tuxguitar.app.action.impl.settings.TGReloadIconsAction;
import org.herac.tuxguitar.app.action.impl.settings.TGReloadLanguageAction;
import org.herac.tuxguitar.app.action.impl.settings.TGReloadMidiDevicesAction;
import org.herac.tuxguitar.app.action.impl.settings.TGReloadSettingsAction;
import org.herac.tuxguitar.app.action.impl.settings.TGReloadStylesAction;
import org.herac.tuxguitar.app.action.impl.settings.TGReloadTableSettingsAction;
import org.herac.tuxguitar.app.action.impl.settings.TGReloadTitleAction;
import org.herac.tuxguitar.app.action.impl.system.TGDisposeAction;
import org.herac.tuxguitar.app.action.impl.tools.TGOpenScaleDialogAction;
import org.herac.tuxguitar.app.action.impl.tools.TGOpenTransposeDialogAction;
import org.herac.tuxguitar.app.action.impl.tools.TGSelectScaleAction;
import org.herac.tuxguitar.app.action.impl.tools.TGToggleBrowserAction;
import org.herac.tuxguitar.app.action.impl.tools.TGTransposeAction;
import org.herac.tuxguitar.app.action.impl.track.TGGoFirstTrackAction;
import org.herac.tuxguitar.app.action.impl.track.TGGoLastTrackAction;
import org.herac.tuxguitar.app.action.impl.track.TGGoNextTrackAction;
import org.herac.tuxguitar.app.action.impl.track.TGGoPreviousTrackAction;
import org.herac.tuxguitar.app.action.impl.track.TGGoToTrackAction;
import org.herac.tuxguitar.app.action.impl.track.TGOpenTrackPropertiesDialogAction;
import org.herac.tuxguitar.app.action.impl.track.TGToggleLyricEditorAction;
import org.herac.tuxguitar.app.action.impl.transport.TGOpenTransportModeDialogAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportCountDownAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportMetronomeAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportModeAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportPlayAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportSetLoopEHeaderAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportSetLoopSHeaderAction;
import org.herac.tuxguitar.app.action.impl.transport.TGTransportStopAction;
import org.herac.tuxguitar.app.action.impl.view.TGHideExternalBeatAction;
import org.herac.tuxguitar.app.action.impl.view.TGOpenViewAction;
import org.herac.tuxguitar.app.action.impl.view.TGShowExternalBeatAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleChannelsDialogAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleEditToolbarAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleFretBoardEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleMainToolbarAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleMatrixEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGTogglePianoEditorAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleTransportDialogAction;
import org.herac.tuxguitar.app.action.impl.view.TGToggleViewAction;
import org.herac.tuxguitar.app.action.listener.cache.TGUpdateController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateAddedMeasureController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateAddedTrackController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateItemsController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateItemsOnSuccessController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateLoadedSongController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateMeasureController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateModifiedChannelController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateModifiedDurationController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateModifiedMarkerController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateModifiedNoteController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateModifiedVelocityController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdatePlayerTracksController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateRemovedMeasureController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateRemovedTrackController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateSavedSongController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateShiftedNoteController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateSongController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateSongInfoController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateTransportPositionController;
import org.herac.tuxguitar.app.action.listener.cache.controller.TGUpdateWrittenFileController;
import org.herac.tuxguitar.app.undo.impl.marker.TGUndoableMarkerGenericController;
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
import org.herac.tuxguitar.editor.action.duration.TGChangeDivisionTypeDurationAction;
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
import org.herac.tuxguitar.editor.action.note.TGDeleteNoteAction;
import org.herac.tuxguitar.editor.action.note.TGDeleteNoteOrRestAction;
import org.herac.tuxguitar.editor.action.note.TGIncrementNoteSemitoneAction;
import org.herac.tuxguitar.editor.action.note.TGInsertChordAction;
import org.herac.tuxguitar.editor.action.note.TGInsertRestBeatAction;
import org.herac.tuxguitar.editor.action.note.TGInsertTextAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsLeftAction;
import org.herac.tuxguitar.editor.action.note.TGMoveBeatsRightAction;
import org.herac.tuxguitar.editor.action.note.TGRemoveChordAction;
import org.herac.tuxguitar.editor.action.note.TGRemoveTextAction;
import org.herac.tuxguitar.editor.action.note.TGRemoveUnusedVoiceAction;
import org.herac.tuxguitar.editor.action.note.TGSetNoteFretNumberAction;
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
import org.herac.tuxguitar.editor.action.track.TGSetTrackLyricsAction;
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
import org.herac.tuxguitar.editor.undo.impl.track.TGUndoableTrackLyricsController;
import org.herac.tuxguitar.editor.undo.impl.track.TGUndoableTrackSoloMuteController;

public class TGActionConfigMap extends TGActionMap<TGActionConfig> {
	
	public static final int LOCKABLE = 0x01;
	public static final int SYNC_THREAD = 0x02;
	public static final int SHORTCUT = 0x04;
	public static final int DISABLE_ON_PLAY = 0x08;
	public static final int STOP_TRANSPORT = 0x10;
	public static final int SAVE_BEFORE = 0x20;
	
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
		//system actions
		this.map(TGDisposeAction.NAME, LOCKABLE | SYNC_THREAD | SAVE_BEFORE);
		
		//file actions
		this.map(TGLoadSongAction.NAME, LOCKABLE | STOP_TRANSPORT, UPDATE_SONG_LOADED_CTL);
		this.map(TGNewSongAction.NAME, LOCKABLE | STOP_TRANSPORT | SHORTCUT);
		this.map(TGLoadTemplateAction.NAME, LOCKABLE | STOP_TRANSPORT);
		this.map(TGReadSongAction.NAME, LOCKABLE);
		this.map(TGWriteSongAction.NAME, LOCKABLE, UPDATE_SONG_SAVED_CTL);
		this.map(TGWriteFileAction.NAME, LOCKABLE, new TGUpdateWrittenFileController());
		this.map(TGSaveAsFileAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT);
		this.map(TGSaveFileAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT);
		this.map(TGReadURLAction.NAME, LOCKABLE | STOP_TRANSPORT, UPDATE_ITEMS_CTL/*new TGUpdateReadedURLController()*/);
		this.map(TGOpenFileAction.NAME, LOCKABLE | SYNC_THREAD | SHORTCUT);
		this.map(TGImportSongAction.NAME, LOCKABLE);
		this.map(TGExportSongAction.NAME, LOCKABLE);
		this.map(TGCloseDocumentsAction.NAME, LOCKABLE | SAVE_BEFORE, UPDATE_ITEMS_CTL);
		this.map(TGCloseDocumentAction.NAME, LOCKABLE | STOP_TRANSPORT);
		this.map(TGCloseCurrentDocumentAction.NAME, LOCKABLE | STOP_TRANSPORT);
		this.map(TGCloseOtherDocumentsAction.NAME, LOCKABLE);
		this.map(TGCloseAllDocumentsAction.NAME, LOCKABLE | STOP_TRANSPORT);
		this.map(TGExitAction.NAME, LOCKABLE | SYNC_THREAD);
		this.map(TGPrintAction.NAME, LOCKABLE | SHORTCUT);
		this.map(TGPrintPreviewAction.NAME, LOCKABLE | SHORTCUT);
		
		//edit actions
		this.map(TGUndoAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGRedoAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetMouseModeSelectionAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetMouseModeEditionAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetNaturalKeyAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetVoice1Action.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGSetVoice2Action.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		
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
		this.map(TGSetTrackInfoAction.NAME, LOCKABLE, UPDATE_ITEMS_CTL, new TGUndoableTrackInfoController());
		this.map(TGSetTrackNameAction.NAME, LOCKABLE);
		this.map(TGSetTrackChannelAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC);
		this.map(TGChangeTrackTuningAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC);
		this.map(TGCopyTrackFromAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC);
		this.map(TGSetTrackLyricsAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_ITEMS_CTL, new TGUndoableTrackLyricsController());
		this.map(TGChangeTrackPropertiesAction.NAME, LOCKABLE | DISABLE_ON_PLAY);
		
		//measure actions
		this.map(TGAddMeasureAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateAddedMeasureController(), new TGUndoableAddMeasureController());
		this.map(TGAddMeasureListAction.NAME, LOCKABLE | DISABLE_ON_PLAY);
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
		this.map(TGChangeTiedNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeVelocityAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateModifiedVelocityController(), UNDOABLE_MEASURE_GENERIC);
		this.map(TGCleanBeatAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGDecrementNoteSemitoneAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGDeleteNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGDeleteNoteOrRestAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGIncrementNoteSemitoneAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGInsertRestBeatAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGMoveBeatsAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_TRACK_GENERIC);
		this.map(TGMoveBeatsLeftAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGMoveBeatsRightAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGRemoveUnusedVoiceAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGSetVoiceAutoAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGSetVoiceDownAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGSetVoiceUpAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGShiftNoteDownAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, new TGUpdateShiftedNoteController(), UNDOABLE_MEASURE_GENERIC);
		this.map(TGShiftNoteUpAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, new TGUpdateShiftedNoteController(), UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeStrokeAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGInsertTextAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGRemoveTextAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGInsertChordAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGRemoveChordAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		for( int i = 0 ; i < 10 ; i ++ ){
			this.map(TGSetNoteFretNumberAction.getActionName(i), LOCKABLE | DISABLE_ON_PLAY | SHORTCUT);
		}
		
		//effect actions
		this.map(TGChangeAccentuatedNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeBendNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeDeadNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeFadeInAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeGhostNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeGraceNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeHammerNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeHarmonicNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeHeavyAccentuatedNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeLetRingAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangePalmMuteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangePoppingAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeSlappingAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeSlideNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeStaccatoAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeTappingAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeTremoloBarAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeTremoloPickingAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeTrillNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		this.map(TGChangeVibratoNoteAction.NAME, LOCKABLE | DISABLE_ON_PLAY | SHORTCUT, UPDATE_MEASURE_CTL, UNDOABLE_MEASURE_GENERIC);
		
		//duration actions
		this.map(TGSetDurationAction.NAME, LOCKABLE | DISABLE_ON_PLAY, new TGUpdateModifiedDurationController(), UNDOABLE_MEASURE_GENERIC);
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
		this.map(TGTransportPlayAction.NAME, SHORTCUT);
		this.map(TGTransportStopAction.NAME, SHORTCUT);
		this.map(TGTransportMetronomeAction.NAME, SHORTCUT);
		this.map(TGTransportCountDownAction.NAME, SHORTCUT);
		this.map(TGTransportModeAction.NAME);
		this.map(TGTransportSetLoopSHeaderAction.NAME, SHORTCUT);
		this.map(TGTransportSetLoopEHeaderAction.NAME, SHORTCUT);
		
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
		
		//tools
		this.map(TGSelectScaleAction.NAME);
		this.map(TGTransposeAction.NAME, LOCKABLE | DISABLE_ON_PLAY, UPDATE_SONG_CTL, UNDOABLE_SONG_GENERIC);
		this.map(TGShowExternalBeatAction.NAME);
		this.map(TGHideExternalBeatAction.NAME);
		
		//settings
		this.map(TGReloadSettingsAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGReloadTitleAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGReloadIconsAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGReloadLanguageAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGReloadMidiDevicesAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGReloadStylesAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGReloadTableSettingsAction.NAME, LOCKABLE | SYNC_THREAD, UPDATE_SONG_CTL);
		this.map(TGOpenSettingsEditorAction.NAME, SHORTCUT);
		this.map(TGOpenKeyBindingEditorAction.NAME, SHORTCUT);
		this.map(TGOpenPluginListDialogAction.NAME, SHORTCUT);
		
		//gui actions
		this.map(TGOpenViewAction.NAME, LOCKABLE | SYNC_THREAD);
		this.map(TGToggleViewAction.NAME, LOCKABLE | SYNC_THREAD);
		this.map(TGOpenSongInfoDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTempoDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenClefDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenKeySignatureDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTimeSignatureDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTripletFeelDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenStrokeUpDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenStrokeDownDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenBendDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTrillDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTremoloPickingDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTremoloBarDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenHarmonicDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenGraceDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenBeatMoveDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTextDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenChordDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenRepeatCloseDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenRepeatAlternativeDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenMeasureAddDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenMeasureCleanDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenMeasureRemoveDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenMeasureCopyDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenMeasurePasteDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenTrackPropertiesDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGOpenScaleDialogAction.NAME, SHORTCUT);
		this.map(TGOpenURLAction.NAME, SHORTCUT);
		this.map(TGOpenTransportModeDialogAction.NAME, SHORTCUT);
		this.map(TGOpenMarkerEditorAction.NAME, SHORTCUT);
		this.map(TGOpenTransposeDialogAction.NAME, DISABLE_ON_PLAY | SHORTCUT);
		this.map(TGToggleFretBoardEditorAction.NAME, SHORTCUT);
		this.map(TGTogglePianoEditorAction.NAME, SHORTCUT);
		this.map(TGToggleMatrixEditorAction.NAME, SHORTCUT);
		this.map(TGToggleLyricEditorAction.NAME, SHORTCUT);
		this.map(TGToggleChannelsDialogAction.NAME, SHORTCUT);
		this.map(TGToggleBrowserAction.NAME, SHORTCUT);
		this.map(TGToggleTransportDialogAction.NAME, SHORTCUT);
		this.map(TGToggleMarkerListAction.NAME, SHORTCUT);
		this.map(TGToggleMainToolbarAction.NAME, SHORTCUT);
		this.map(TGToggleEditToolbarAction.NAME, SHORTCUT);
		this.map(TGOpenDocumentationDialogAction.NAME, SHORTCUT);
		this.map(TGOpenAboutDialogAction.NAME, SHORTCUT);
	}
	
	private void map(String actionId) {
		this.map(actionId, 0, UPDATE_ITEMS_CTL, null);
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
		
		this.set(actionId, tgActionConfig);
	}
}
