package app.tuxguitar.app.view.toolbar.main;


/**
 * A map of all items that can be included in the main tool bar
 * 
 * TODO (05/2025): the behavior of MANY user-accessible controls is defined in this class:
 * text, icon, action to be performed and associated parameters, condition to enable and to check
* 
 * this is redundant with other definitions: in menus, and in edit toolBar
 * rationalization should be possible
 * 
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.tuxguitar.app.TuxGuitar;
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
import app.tuxguitar.app.action.impl.effects.TGOpenBendDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenGraceDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenHarmonicDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenTremoloBarDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenTremoloPickingDialogAction;
import app.tuxguitar.app.action.impl.effects.TGOpenTrillDialogAction;
import app.tuxguitar.app.action.impl.file.TGCloseCurrentDocumentAction;
import app.tuxguitar.app.action.impl.file.TGOpenFileAction;
import app.tuxguitar.app.action.impl.file.TGPrintAction;
import app.tuxguitar.app.action.impl.file.TGPrintPreviewAction;
import app.tuxguitar.app.action.impl.file.TGSaveAsFileAction;
import app.tuxguitar.app.action.impl.file.TGSaveFileAction;
import app.tuxguitar.app.action.impl.help.TGHelpGoHomeAction;
import app.tuxguitar.app.action.impl.help.TGOpenAboutDialogAction;
import app.tuxguitar.app.action.impl.help.TGOpenDocumentationDialogAction;
import app.tuxguitar.app.action.impl.insert.TGOpenChordDialogAction;
import app.tuxguitar.app.action.impl.insert.TGOpenRepeatAlternativeDialogAction;
import app.tuxguitar.app.action.impl.insert.TGOpenRepeatCloseDialogAction;
import app.tuxguitar.app.action.impl.insert.TGOpenTextDialogAction;
import app.tuxguitar.app.action.impl.layout.TGSetCompactViewAction;
import app.tuxguitar.app.action.impl.layout.TGSetLayoutScaleDecrementAction;
import app.tuxguitar.app.action.impl.layout.TGSetLayoutScaleIncrementAction;
import app.tuxguitar.app.action.impl.layout.TGSetLayoutScaleResetAction;
import app.tuxguitar.app.action.impl.layout.TGSetLinearLayoutAction;
import app.tuxguitar.app.action.impl.layout.TGSetMultitrackViewAction;
import app.tuxguitar.app.action.impl.layout.TGSetPageLayoutAction;
import app.tuxguitar.app.action.impl.layout.TGSetScoreEnabledAction;
import app.tuxguitar.app.action.impl.layout.TGSetTablatureEnabledAction;
import app.tuxguitar.app.action.impl.layout.TGToggleContinuousScrollingAction;
import app.tuxguitar.app.action.impl.layout.TGToggleHighlightPlayedBeatAction;
import app.tuxguitar.app.action.impl.marker.TGGoFirstMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGGoLastMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGGoNextMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGGoPreviousMarkerAction;
import app.tuxguitar.app.action.impl.marker.TGOpenMarkerEditorAction;
import app.tuxguitar.app.action.impl.marker.TGToggleMarkerListAction;
import app.tuxguitar.app.action.impl.measure.TGGoFirstMeasureAction;
import app.tuxguitar.app.action.impl.measure.TGGoLastMeasureAction;
import app.tuxguitar.app.action.impl.measure.TGGoNextMeasureAction;
import app.tuxguitar.app.action.impl.measure.TGGoPreviousMeasureAction;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasureAddDialogAction;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasureCleanDialogAction;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasureCopyDialogAction;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasurePasteDialogAction;
import app.tuxguitar.app.action.impl.measure.TGOpenMeasureRemoveDialogAction;
import app.tuxguitar.app.action.impl.note.TGOpenStrokeDownDialogAction;
import app.tuxguitar.app.action.impl.note.TGOpenStrokeUpDialogAction;
import app.tuxguitar.app.action.impl.settings.TGOpenKeyBindingEditorAction;
import app.tuxguitar.app.action.impl.settings.TGOpenPluginListDialogAction;
import app.tuxguitar.app.action.impl.settings.TGOpenSettingsEditorAction;
import app.tuxguitar.app.action.impl.track.TGGoFirstTrackAction;
import app.tuxguitar.app.action.impl.track.TGGoLastTrackAction;
import app.tuxguitar.app.action.impl.track.TGGoNextTrackAction;
import app.tuxguitar.app.action.impl.track.TGGoPreviousTrackAction;
import app.tuxguitar.app.action.impl.transport.TGOpenTransportModeDialogAction;
import app.tuxguitar.app.action.impl.transport.TGTransportPlayPauseAction;
import app.tuxguitar.app.action.impl.transport.TGTransportSetLoopEHeaderAction;
import app.tuxguitar.app.action.impl.transport.TGTransportSetLoopSHeaderAction;
import app.tuxguitar.app.action.impl.transport.TGTransportStopAction;
import app.tuxguitar.app.action.impl.view.TGOpenMainToolBarSettingsDialogAction;
import app.tuxguitar.app.action.impl.view.TGToggleChannelsDialogAction;
import app.tuxguitar.app.action.impl.view.TGToggleEditToolbarAction;
import app.tuxguitar.app.action.impl.view.TGToggleFretBoardEditorAction;
import app.tuxguitar.app.action.impl.view.TGToggleMatrixEditorAction;
import app.tuxguitar.app.action.impl.view.TGTogglePianoEditorAction;
import app.tuxguitar.app.action.impl.view.TGToggleTableViewerAction;
import app.tuxguitar.app.action.impl.view.TGToggleTransportDialogAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.view.component.tab.Caret;
import app.tuxguitar.app.view.component.tab.Tablature;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.view.component.tab.edit.EditorKit;
import app.tuxguitar.app.view.component.table.TGTableViewer;
import app.tuxguitar.app.view.dialog.channel.TGChannelManagerDialog;
import app.tuxguitar.app.view.dialog.fretboard.TGFretBoardEditor;
import app.tuxguitar.app.view.dialog.transport.TGTransportDialog;
import app.tuxguitar.app.view.toolbar.edit.TGEditToolBar;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.action.composition.TGRepeatOpenAction;
import app.tuxguitar.editor.action.duration.TGChangeDottedDurationAction;
import app.tuxguitar.editor.action.duration.TGChangeDoubleDottedDurationAction;
import app.tuxguitar.editor.action.duration.TGSetDivisionTypeDurationAction;
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
import app.tuxguitar.editor.action.effect.TGChangeDeadNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeFadeInAction;
import app.tuxguitar.editor.action.effect.TGChangeGhostNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeHammerNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeHeavyAccentuatedNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeLetRingAction;
import app.tuxguitar.editor.action.effect.TGChangePalmMuteAction;
import app.tuxguitar.editor.action.effect.TGChangePoppingAction;
import app.tuxguitar.editor.action.effect.TGChangeSlappingAction;
import app.tuxguitar.editor.action.effect.TGChangeSlideNoteAction;
import app.tuxguitar.editor.action.effect.TGChangeStaccatoAction;
import app.tuxguitar.editor.action.effect.TGChangeTappingAction;
import app.tuxguitar.editor.action.effect.TGChangeVibratoNoteAction;
import app.tuxguitar.editor.action.file.TGLoadTemplateAction;
import app.tuxguitar.editor.action.note.TGChangePickStrokeDownAction;
import app.tuxguitar.editor.action.note.TGChangePickStrokeUpAction;
import app.tuxguitar.editor.action.note.TGChangeTiedNoteAction;
import app.tuxguitar.editor.action.note.TGChangeVelocityAction;
import app.tuxguitar.editor.action.note.TGToggleNoteEnharmonicAction;
import app.tuxguitar.editor.action.track.TGAddNewTrackAction;
import app.tuxguitar.editor.action.track.TGChangeTrackMuteAction;
import app.tuxguitar.editor.action.track.TGChangeTrackSoloAction;
import app.tuxguitar.editor.action.track.TGCloneTrackAction;
import app.tuxguitar.editor.action.track.TGMoveTrackDownAction;
import app.tuxguitar.editor.action.track.TGMoveTrackUpAction;
import app.tuxguitar.editor.action.track.TGRemoveTrackAction;
import app.tuxguitar.editor.action.transport.TGTransportCountDownAction;
import app.tuxguitar.editor.action.transport.TGTransportMetronomeAction;
import app.tuxguitar.editor.clipboard.TGClipboard;
import app.tuxguitar.editor.undo.TGUndoableManager;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.graphics.control.TGLayoutHorizontal;
import app.tuxguitar.graphics.control.TGLayoutVertical;
import app.tuxguitar.graphics.control.TGTrackImpl;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGDivisionType;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGNoteEffect;
import app.tuxguitar.song.models.TGPickStroke;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGStroke;
import app.tuxguitar.song.models.TGVelocities;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

public class TGMainToolBarConfigMap {
	private Map<String, TGMainToolBarItemConfig> mapItems;
	// ordered list of control names, for display in dialog
	private List<String> itemNames;
	
	private String groupName;	// stored as a member for readability: avoid repeating it when registering items
	private List<String> groupNames;

	public TGMainToolBarConfigMap() {
		this.mapItems = new HashMap<String, TGMainToolBarItemConfig>();
		this.itemNames = new ArrayList<String>();
		this.groupNames = new ArrayList<String>();

		// a generic updater
		TGMainToolBarItemUpdater DISABLE_ON_PLAY = new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				return (!isRunning);
			}
		};

		// SEPARATOR
		this.groupName = "";
		registerItem("toolbar.separator", TGMainToolBarItem.SEPARATOR, TGMainToolBarSection.TYPE_TOOLITEMS, TGIconManager.SEPARATOR);

		//------- FILE --------
		this.groupName = "file";
		registerButton("file.new", TGLoadTemplateAction.NAME, TGIconManager.FILE_NEW);
		registerButton("file.open", TGOpenFileAction.NAME, TGIconManager.FILE_OPEN);
		registerButton("file.save", TGSaveFileAction.NAME, TGIconManager.FILE_SAVE);
		registerButton("file.save-as", TGSaveAsFileAction.NAME, TGIconManager.FILE_SAVE_AS);
		registerButton("file.print-preview", TGPrintPreviewAction.NAME, TGIconManager.PRINT_PREVIEW);
		registerButton("file.print", TGPrintAction.NAME, TGIconManager.PRINT);
		registerButton("file.close", TGCloseCurrentDocumentAction.NAME, TGIconManager.FILE_CLOSE);
		
		//------- EDIT --------
		this.groupName = "edit";
		registerButton("edit.cut", TGCutAction.NAME, TGIconManager.EDIT_CUT, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				return !isRunning && TuxGuitar.getInstance().getTablatureEditor().getTablature().getSelector().isActive();
			}
		});
		
		registerButton("edit.copy", TGCopyAction.NAME, TGIconManager.EDIT_COPY, DISABLE_ON_PLAY);
		registerButton("edit.paste", TGPasteAction.NAME, TGIconManager.EDIT_PASTE, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				return (!isRunning && TGClipboard.getInstance(context).hasContents());
			}
		});
		registerButton("edit.repeat", TGRepeatAction.NAME, TGIconManager.EDIT_REPEAT, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				return (!isRunning && TuxGuitar.getInstance().getTablatureEditor().getTablature().getSelector().isActive());
			}
		});
		registerButton("edit.undo", TGUndoAction.NAME, TGIconManager.UNDO,
				new TGMainToolBarItemUpdater() {
					@Override
					public boolean enabled(TGContext context, boolean isRunning) {
						return (!isRunning && TGUndoableManager.getInstance(context).canUndo());
					}
				});
		registerButton("edit.redo", TGRedoAction.NAME, TGIconManager.REDO,
				new TGMainToolBarItemUpdater() {
					@Override
					public boolean enabled(TGContext context, boolean isRunning) {
						return (!isRunning && TGUndoableManager.getInstance(context).canRedo());
					}
				});
		registerCheckable("edit.mouse-mode-selection", TGSetMouseModeSelectionAction.NAME, TGIconManager.EDIT_MODE_SELECTION, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				return (!isRunning);
			}
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				return TuxGuitar.getInstance().getTablatureEditor().getTablature().getEditorKit().getMouseMode() == EditorKit.MOUSE_MODE_SELECTION;
			}
		});
		registerCheckable("edit.mouse-mode-edition", TGSetMouseModeEditionAction.NAME, TGIconManager.EDIT_MODE_EDITION, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				return (!isRunning);
			}
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				return TuxGuitar.getInstance().getTablatureEditor().getTablature().getEditorKit().getMouseMode() == EditorKit.MOUSE_MODE_EDITION;
			}
		});
		registerCheckable("edit.not-natural-key", TGSetNaturalKeyAction.NAME, TGIconManager.EDIT_MODE_EDITION_NO_NATURAL, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				return !isRunning && TuxGuitar.getInstance().getTablatureEditor().getTablature().getEditorKit().getMouseMode() == EditorKit.MOUSE_MODE_EDITION;
			}
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				return !TuxGuitar.getInstance().getTablatureEditor().getTablature().getEditorKit().isNatural();
			}
		});
		registerCheckable("edit.free-edition-mode", TGToggleFreeEditionModeAction.NAME, TGIconManager.EDIT_MODE_FREE, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				return !isRunning;
			}
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				Tablature tablature = TuxGuitar.getInstance().getTablatureEditor().getTablature();
				return tablature.getSongManager().isFreeEditionMode(tablature.getCaret().getMeasure());
			}
		});
		registerButton("edit.measure-errors-dialog", TGOpenMeasureErrorsDialogAction.NAME, TGIconManager.EDIT_MEASURE_STATUS_CHECK, DISABLE_ON_PLAY);
		registerCheckable("edit.voice-1", TGSetVoice1Action.NAME, TGIconManager.EDIT_VOICE_1, new TGMainToolBarItemUpdater() {
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				return TuxGuitar.getInstance().getTablatureEditor().getTablature().getEditorKit().getTablature().getCaret().getVoice() == 0;
			}
		});
		registerCheckable("edit.voice-2", TGSetVoice2Action.NAME, TGIconManager.EDIT_VOICE_2, new TGMainToolBarItemUpdater() {
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				return TuxGuitar.getInstance().getTablatureEditor().getTablature().getEditorKit().getTablature().getCaret().getVoice() == 1;
			}
		});

		//------- VIEW --------
		this.groupName = "view";
		registerCheckable("view.show-edit-toolbar", TGToggleEditToolbarAction.NAME, TGIconManager.TOOLBAR_EDIT,
				new TGMainToolBarItemUpdater() {
					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						return TGEditToolBar.getInstance(context).isVisible();
					}
				});
		registerCheckable("view.show-table-viewer", TGToggleTableViewerAction.NAME, TGIconManager.TABLE_VIEWER,
				new TGMainToolBarItemUpdater() {
					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						return TGTableViewer.getInstance(context).isVisible();
					}
				});
		registerCheckable("view.show-fretboard", TGToggleFretBoardEditorAction.NAME, TGIconManager.FRETBOARD,
				new TGMainToolBarItemUpdater() {
					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						return TGFretBoardEditor.getInstance(context).isVisible();
					}
				});
		registerCheckable("view.show-instruments", TGToggleChannelsDialogAction.NAME, TGIconManager.INSTRUMENTS,
				new TGMainToolBarItemUpdater() {
					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						return !TGChannelManagerDialog.getInstance(context).isDisposed();
					}
				});

		registerCheckable("view.layout.page", TGSetPageLayoutAction.NAME,
				TGIconManager.LAYOUT_PAGE, new TGMainToolBarItemUpdater() {
					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						TGLayout layout = TablatureEditor.getInstance(context).getTablature().getViewLayout();
						return (layout instanceof TGLayoutVertical);
					}
				});

		registerCheckable("view.layout.linear", TGSetLinearLayoutAction.NAME,
				TGIconManager.LAYOUT_LINEAR, new TGMainToolBarItemUpdater() {
					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						TGLayout layout = TablatureEditor.getInstance(context).getTablature().getViewLayout();
						return (layout instanceof TGLayoutHorizontal);
					}
				});

		registerCheckable("view.layout.multitrack", TGSetMultitrackViewAction.NAME,
				TGIconManager.LAYOUT_MULTITRACK, new TGMainToolBarItemUpdater() {
					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						int style = TablatureEditor.getInstance(context).getTablature().getViewLayout().getStyle();
						return ((style & TGLayout.DISPLAY_MULTITRACK) != 0);
					}
				});

		registerCheckable("view.layout.score-enabled", TGSetScoreEnabledAction.NAME,
				TGIconManager.LAYOUT_SCORE, new TGMainToolBarItemUpdater() {
					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						int style = TablatureEditor.getInstance(context).getTablature().getViewLayout().getStyle();
						return ((style & TGLayout.DISPLAY_SCORE) != 0);
					}
				});
		registerCheckable("view.layout.tablature-enabled", TGSetTablatureEnabledAction.NAME, TGIconManager.LAYOUT_TABLATURE, new TGMainToolBarItemUpdater() {
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				int style = TablatureEditor.getInstance(context).getTablature().getViewLayout().getStyle();
				return ((style & TGLayout.DISPLAY_TABLATURE) != 0);
			}
		});
		registerCheckable("view.layout.compact", TGSetCompactViewAction.NAME,
				TGIconManager.LAYOUT_COMPACT, new TGMainToolBarItemUpdater() {
					@Override
					public boolean enabled(TGContext context, boolean isRunning) {
						int style = TablatureEditor.getInstance(context).getTablature().getViewLayout().getStyle();
						TGSong song = TGDocumentManager.getInstance(context).getSong();
						return ((style & TGLayout.DISPLAY_MULTITRACK) == 0 || song.countTracks() == 1);
					}
					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						int style = TablatureEditor.getInstance(context).getTablature().getViewLayout().getStyle();
						return (this.enabled(context,isRunning) && (style & TGLayout.DISPLAY_COMPACT) != 0);
					}
				});
		registerCheckable("view.show-transport", TGToggleTransportDialogAction.NAME, TGIconManager.TRANSPORT, new TGMainToolBarItemUpdater() {
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				return (!TGTransportDialog.getInstance(context).isDisposed());
			}
		});
		registerCheckable("view.show-piano", TGTogglePianoEditorAction.NAME, TGIconManager.PIANO, new TGMainToolBarItemUpdater() {
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				return (!TuxGuitar.getInstance().getPianoEditor().isDisposed());
			}
		});
		registerCheckable("view.show-matrix", TGToggleMatrixEditorAction.NAME, TGIconManager.MATRIX, new TGMainToolBarItemUpdater() {
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				return (!TuxGuitar.getInstance().getMatrixEditor().isDisposed());
			}
		});
		// View menu
		TGMainToolBarItemConfig menuLayout = newMenu("view.layout", TGIconManager.LAYOUT_SCORE, false);
		menuLayout.addSubItem(mapItems.get("view.layout.page"));
		menuLayout.addSubItem(mapItems.get("view.layout.linear"));
		menuLayout.addSubItem(mapItems.get("view.layout.multitrack"));
		menuLayout.addSubItem(mapItems.get("view.layout.score-enabled"));
		menuLayout.addSubItem(mapItems.get("view.layout.compact"));
		registerItem(menuLayout);

		registerButton("view.zoom.out", TGSetLayoutScaleDecrementAction.NAME, TGIconManager.ZOOM_OUT);
		registerButton("view.zoom.reset", TGSetLayoutScaleResetAction.NAME, TGIconManager.ZOOM_RESET);
		registerButton("view.zoom.in", TGSetLayoutScaleIncrementAction.NAME, TGIconManager.ZOOM_IN);

		//------- COMPOSITION --------
		this.groupName = "composition";
		registerButton("composition.timesignature", TGOpenTimeSignatureDialogAction.NAME, TGIconManager.TIME_SIGNATURE, DISABLE_ON_PLAY);
		registerButton("composition.tempo", TGOpenTempoDialogAction.NAME, TGIconManager.TEMPO, DISABLE_ON_PLAY);
		registerButton("composition.clef", TGOpenClefDialogAction.NAME, TGIconManager.CLEF, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				boolean isPercussion = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack().isPercussion();
				return (!isRunning && !isPercussion);
			}
		});
		registerButton("composition.keysignature", TGOpenKeySignatureDialogAction.NAME, TGIconManager.KEY_SIGNATURE, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				boolean isPercussion = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack().isPercussion();
				return (!isRunning && !isPercussion);
			}
		});
		registerButton("composition.tripletfeel", TGOpenTripletFeelDialogAction.NAME, TGIconManager.TRIPLET_FEEL, DISABLE_ON_PLAY);
		registerButton("repeat.open", TGRepeatOpenAction.NAME, TGIconManager.REPEAT_OPEN, DISABLE_ON_PLAY);
		registerButton("repeat.close", TGOpenRepeatCloseDialogAction.NAME, TGIconManager.REPEAT_CLOSE, DISABLE_ON_PLAY);
		registerButton("repeat.alternative", TGOpenRepeatAlternativeDialogAction.NAME, TGIconManager.REPEAT_ALTERNATIVE, DISABLE_ON_PLAY);
		registerButton("composition.properties", TGOpenSongInfoDialogAction.NAME, TGIconManager.SONG_PROPERTIES, DISABLE_ON_PLAY);

		//------- TRACK --------
		this.groupName = "track";
		registerButton("track.add", TGAddNewTrackAction.NAME, TGIconManager.TRACK_ADD, DISABLE_ON_PLAY);
		registerButton("track.remove", TGRemoveTrackAction.NAME, TGIconManager.TRACK_REMOVE, DISABLE_ON_PLAY);
		registerButton("track.first", TGGoFirstTrackAction.NAME, TGIconManager.TRACK_FIRST, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				TGTrackImpl track = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack();
				boolean isFirst = (track.getNumber() == 1);
				return (!isFirst);
			}
		});
		registerButton("track.previous", TGGoPreviousTrackAction.NAME, TGIconManager.TRACK_PREVIOUS, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				TGTrackImpl track = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack();
				boolean isFirst = (track.getNumber() == 1);
				return (!isFirst);
			}
		});
		registerButton("track.next", TGGoNextTrackAction.NAME, TGIconManager.TRACK_NEXT, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				TGTrackImpl track = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack();
				boolean isLast = (track.getNumber() == track.getSong().countTracks());
				return (!isLast);
			}
		});
		registerButton("track.last", TGGoLastTrackAction.NAME, TGIconManager.TRACK_LAST, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				TGTrackImpl track = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack();
				boolean isLast = (track.getNumber() == track.getSong().countTracks());
				return (!isLast);
			}
		});
		registerButton("track.clone", TGCloneTrackAction.NAME, TGIconManager.TRACK_CLONE, DISABLE_ON_PLAY);
		registerButton("track.move-up", TGMoveTrackUpAction.NAME, TGIconManager.ARROW_UP, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				TGTrackImpl track = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack();
				return (!isRunning && track.getSong().countTracks() > 1);
			}
		});
		registerButton("track.move-down", TGMoveTrackDownAction.NAME, TGIconManager.ARROW_DOWN, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				TGTrackImpl track = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack();
				return (!isRunning && track.getSong().countTracks() > 1);
			}
		});
		registerCheckable("track.solo", TGChangeTrackSoloAction.NAME, TGIconManager.TRACK_SOLO, new TGMainToolBarItemUpdater() {
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				return TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack().isSolo();
			}
		});
		registerCheckable("track.mute", TGChangeTrackMuteAction.NAME, TGIconManager.TRACK_MUTE, new TGMainToolBarItemUpdater() {
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				return TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack().isMute();
			}
		});

		//------- MEASURE --------
		this.groupName = "measure";
		registerButton("measure.add", TGOpenMeasureAddDialogAction.NAME, TGIconManager.MEASURE_ADD , DISABLE_ON_PLAY);
		registerButton("measure.clean", TGOpenMeasureCleanDialogAction.NAME, TGIconManager.MEASURE_CLEAN , DISABLE_ON_PLAY);
		registerButton("measure.remove", TGOpenMeasureRemoveDialogAction.NAME, TGIconManager.MEASURE_REMOVE , DISABLE_ON_PLAY);
		registerButton("measure.copy", TGOpenMeasureCopyDialogAction.NAME, TGIconManager.MEASURE_COPY , DISABLE_ON_PLAY);
		registerButton("measure.paste", TGOpenMeasurePasteDialogAction.NAME, TGIconManager.MEASURE_PASTE, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				return (!isRunning && TGClipboard.getInstance(context).getSegment() != null);
			}
		});

		//------- Beat --------
		this.groupName = "beat";
		registerButton("insert.chord", TGOpenChordDialogAction.NAME, TGIconManager.CHORD, DISABLE_ON_PLAY);
		registerButton("text.insert", TGOpenTextDialogAction.NAME, TGIconManager.TEXT, DISABLE_ON_PLAY);
		registerCheckable("beat.stroke-down", TGOpenStrokeDownDialogAction .NAME, TGIconManager.STROKE_DOWN, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
				boolean restBeat = caret.isRestBeatSelected();
				return (!isRunning && !restBeat && !caret.getTrack().isPercussion());
			}
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				TGBeat beat = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getSelectedBeat();
				return (beat != null && beat.getStroke().getDirection() == TGStroke.STROKE_DOWN);
			}
		});
		registerCheckable("beat.stroke-up", TGOpenStrokeUpDialogAction .NAME, TGIconManager.STROKE_UP, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
				boolean restBeat = caret.isRestBeatSelected();
				return (!isRunning && !restBeat && !caret.getTrack().isPercussion());
			}
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				TGBeat beat = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getSelectedBeat();
				return (beat != null && beat.getStroke().getDirection() == TGStroke.STROKE_UP);
			}
		});
		registerCheckable("beat.pick-stroke-down", TGChangePickStrokeDownAction.NAME, TGIconManager.PICK_STROKE_DOWN, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				boolean restBeat = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().isRestBeatSelected();
				return (!isRunning && !restBeat);
			}
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				TGBeat beat = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getSelectedBeat();
				return (beat != null && beat.getPickStroke().getDirection() == TGPickStroke.PICK_STROKE_DOWN);
			}
		});
		registerCheckable("beat.pick-stroke-up", TGChangePickStrokeUpAction .NAME, TGIconManager.PICK_STROKE_UP, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				boolean restBeat = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().isRestBeatSelected();
				return (!isRunning && !restBeat);
			}
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				TGBeat beat = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getSelectedBeat();
				return (beat != null && beat.getPickStroke().getDirection() == TGPickStroke.PICK_STROKE_UP);
			}
		});
		registerCheckable("note.alternative-enharmonic", TGToggleNoteEnharmonicAction.NAME, TGIconManager.ALTERNATIVE_ENHARMONIC, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				boolean restBeat = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().isRestBeatSelected();
				int style = TuxGuitar.getInstance().getTablatureEditor().getTablature().getViewLayout().getStyle();
				return (!isRunning && ((style & TGLayout.DISPLAY_SCORE) != 0) && !restBeat);
			}
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				TGNote note = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getSelectedNote();
				return ((note!=null) && (note.isAltEnharmonic()));
			}
		});

		//------- DURATION --------
		this.groupName = "duration";
		registerCheckable("duration.whole", TGSetWholeDurationAction.NAME, TGIconManager.WHOLE, getDurationUpdater(TGDuration.WHOLE));
		registerCheckable("duration.half", TGSetHalfDurationAction.NAME, TGIconManager.HALF, getDurationUpdater(TGDuration.HALF));
		registerCheckable("duration.quarter", TGSetQuarterDurationAction.NAME, TGIconManager.QUARTER, getDurationUpdater(TGDuration.QUARTER));
		registerCheckable("duration.eighth", TGSetEighthDurationAction.NAME, TGIconManager.EIGHTH, getDurationUpdater(TGDuration.EIGHTH));
		registerCheckable("duration.sixteenth", TGSetSixteenthDurationAction.NAME, TGIconManager.SIXTEENTH, getDurationUpdater(TGDuration.SIXTEENTH));
		registerCheckable("duration.thirtysecond", TGSetThirtySecondDurationAction.NAME, TGIconManager.THIRTYSECOND, getDurationUpdater(TGDuration.THIRTY_SECOND));
		registerCheckable("duration.sixtyfourth", TGSetSixtyFourthDurationAction.NAME, TGIconManager.SIXTYFOURTH, getDurationUpdater(TGDuration.SIXTY_FOURTH));
		// Duration menu
		TGMainToolBarItemConfig menuDuration = newMenu("duration", TGIconManager.DURATION, true);
		menuDuration.addSubItem(mapItems.get("duration.whole"));
		menuDuration.addSubItem(mapItems.get("duration.half"));
		menuDuration.addSubItem(mapItems.get("duration.quarter"));
		menuDuration.addSubItem(mapItems.get("duration.eighth"));
		menuDuration.addSubItem(mapItems.get("duration.sixteenth"));
		menuDuration.addSubItem(mapItems.get("duration.thirtysecond"));
		menuDuration.addSubItem(mapItems.get("duration.sixtyfourth"));
		registerItem(menuDuration);

		registerCheckable("duration.dotted", TGChangeDottedDurationAction .NAME, TGIconManager.DOTTED, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				return (!isRunning);
			}
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				return TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getDuration().isDotted();
			}
		});
		registerCheckable("duration.doubledotted", TGChangeDoubleDottedDurationAction .NAME, TGIconManager.DOUBLE_DOTTED, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				return (!isRunning);
			}
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				return TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getDuration().isDoubleDotted();
			}
		});
		registerCheckable("duration.tiednote", TGChangeTiedNoteAction.NAME, TGIconManager.TIED_NOTE, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				return (!isRunning);
			}
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				TGNote note = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getSelectedNote();
				return (note != null && note.isTiedNote());
			}
		});
		// duration: division types
		TGMainToolBarItemConfig menuDivisionType = newMenu("duration.division-type", TGIconManager.DIVISION_TYPE, true);
		for (int i = 0; i < TGDivisionType.DIVISION_TYPES.length; i++) {
			registerItem(getCheckableDivision(i, menuDivisionType));
		}
		registerItem(menuDivisionType);
		
		//------- DYNAMIC --------
		this.groupName = "dynamic";
		registerItem(getCheckableVelocity("dynamic.piano-pianissimo", TGVelocities.PIANO_PIANISSIMO, TGIconManager.DYNAMIC_PPP));
		registerItem(getCheckableVelocity("dynamic.pianissimo", TGVelocities.PIANISSIMO, TGIconManager.DYNAMIC_PP));
		registerItem(getCheckableVelocity("dynamic.piano", TGVelocities.PIANO, TGIconManager.DYNAMIC_P));
		registerItem(getCheckableVelocity("dynamic.mezzo-piano", TGVelocities.MEZZO_PIANO, TGIconManager.DYNAMIC_MP));
		registerItem(getCheckableVelocity("dynamic.mezzo-forte", TGVelocities.MEZZO_FORTE, TGIconManager.DYNAMIC_MF));
		registerItem(getCheckableVelocity("dynamic.forte", TGVelocities.FORTE, TGIconManager.DYNAMIC_F));
		registerItem(getCheckableVelocity("dynamic.fortissimo", TGVelocities.FORTISSIMO, TGIconManager.DYNAMIC_FF));
		registerItem(getCheckableVelocity("dynamic.forte-fortissimo", TGVelocities.FORTE_FORTISSIMO, TGIconManager.DYNAMIC_FFF));
		// Dynamic menu
		TGMainToolBarItemConfig menuDynamic = newMenu("dynamic", TGIconManager.DYNAMIC, true);
		menuDynamic.addSubItem(mapItems.get("dynamic.piano-pianissimo"));
		menuDynamic.addSubItem(mapItems.get("dynamic.pianissimo"));
		menuDynamic.addSubItem(mapItems.get("dynamic.piano"));
		menuDynamic.addSubItem(mapItems.get("dynamic.mezzo-piano"));
		menuDynamic.addSubItem(mapItems.get("dynamic.mezzo-forte"));
		menuDynamic.addSubItem(mapItems.get("dynamic.forte"));
		menuDynamic.addSubItem(mapItems.get("dynamic.fortissimo"));
		menuDynamic.addSubItem(mapItems.get("dynamic.forte-fortissimo"));
		registerItem(menuDynamic);

		//------- EFFECTS --------
		this.groupName = "effects";
		registerItem(getCheckableEffect("effects.vibrato", TGChangeVibratoNoteAction.NAME, TGIconManager.EFFECT_VIBRATO, false, e -> e.isVibrato()));
		registerItem(getCheckableEffect("effects.bend", TGOpenBendDialogAction.NAME, TGIconManager.EFFECT_BEND , false, e -> e.isBend()));
		registerItem(getCheckableEffect("effects.tremolo-bar", TGOpenTremoloBarDialogAction.NAME, TGIconManager.EFFECT_TREMOLO_BAR , false, e -> e.isTremoloBar()));
		registerItem(getCheckableEffect("effects.deadnote", TGChangeDeadNoteAction.NAME, TGIconManager.EFFECT_DEAD , false, e -> e.isDeadNote()));
		registerItem(getCheckableEffect("effects.slide", TGChangeSlideNoteAction.NAME, TGIconManager.EFFECT_SLIDE , false, e -> e.isSlide()));
		registerItem(getCheckableEffect("effects.hammer", TGChangeHammerNoteAction.NAME, TGIconManager.EFFECT_HAMMER , false, e -> e.isHammer()));
		registerItem(getCheckableEffect("effects.ghostnote", TGChangeGhostNoteAction.NAME, TGIconManager.EFFECT_GHOST , true, e -> e.isGhostNote()));
		registerItem(getCheckableEffect("effects.accentuatednote", TGChangeAccentuatedNoteAction.NAME, TGIconManager.EFFECT_ACCENTUATED , true, e -> e.isAccentuatedNote()));
		registerItem(getCheckableEffect("effects.heavyaccentuatednote", TGChangeHeavyAccentuatedNoteAction.NAME, TGIconManager.EFFECT_HEAVY_ACCENTUATED , true, e -> e.isHeavyAccentuatedNote()));
		registerItem(getCheckableEffect("effects.let-ring", TGChangeLetRingAction.NAME, TGIconManager.EFFECT_LET_RING , false, e -> e.isLetRing()));
		registerItem(getCheckableEffect("effects.harmonic", TGOpenHarmonicDialogAction.NAME, TGIconManager.EFFECT_HARMONIC , false, e -> e.isHarmonic()));
		registerItem(getCheckableEffect("effects.grace", TGOpenGraceDialogAction.NAME, TGIconManager.EFFECT_GRACE , true, e -> e.isGrace()));
		registerItem(getCheckableEffect("effects.trill", TGOpenTrillDialogAction.NAME, TGIconManager.EFFECT_TRILL , false, e -> e.isTrill()));
		registerItem(getCheckableEffect("effects.tremolo-picking", TGOpenTremoloPickingDialogAction.NAME, TGIconManager.EFFECT_TREMOLO_PICKING , true, e -> e.isTremoloPicking()));
		registerItem(getCheckableEffect("effects.palm-mute", TGChangePalmMuteAction.NAME, TGIconManager.EFFECT_PALM_MUTE , false, e -> e.isPalmMute()));
		registerItem(getCheckableEffect("effects.staccato", TGChangeStaccatoAction.NAME, TGIconManager.EFFECT_STACCATO , false, e -> e.isStaccato()));
		registerItem(getCheckableEffect("effects.tapping", TGChangeTappingAction.NAME, TGIconManager.EFFECT_TAPPING , false, e -> e.isTapping()));
		registerItem(getCheckableEffect("effects.slapping", TGChangeSlappingAction.NAME, TGIconManager.EFFECT_SLAPPING , false, e -> e.isSlapping()));
		registerItem(getCheckableEffect("effects.popping", TGChangePoppingAction.NAME, TGIconManager.EFFECT_POPPING , false, e -> e.isPopping()));
		registerItem(getCheckableEffect("effects.fade-in", TGChangeFadeInAction.NAME, TGIconManager.EFFECT_FADE_IN, false, e -> e.isFadeIn()));

		//------- MARKER --------
		this.groupName = "marker";
		registerButton("marker.add", TGOpenMarkerEditorAction.NAME, TGIconManager.MARKER_ADD);
		registerButton("marker.list", TGToggleMarkerListAction.NAME, TGIconManager.MARKER_LIST);
		registerButton("marker.first", TGGoFirstMarkerAction.NAME, TGIconManager.MARKER_FIRST);
		registerButton("marker.previous", TGGoPreviousMarkerAction.NAME, TGIconManager.MARKER_PREVIOUS);
		registerButton("marker.next", TGGoNextMarkerAction.NAME, TGIconManager.MARKER_NEXT);
		registerButton("marker.last", TGGoLastMarkerAction.NAME, TGIconManager.MARKER_LAST);
		// Marker menu
		TGMainToolBarItemConfig menuMarker = newMenu("marker",TGIconManager.MARKER_LIST, false);
		menuMarker.addSubItem(mapItems.get("marker.add"));
		menuMarker.addSubItem(mapItems.get("marker.list"));
		menuMarker.addSubItem(mapItems.get("toolbar.separator"));
		menuMarker.addSubItem(mapItems.get("marker.first"));
		menuMarker.addSubItem(mapItems.get("marker.previous"));
		menuMarker.addSubItem(mapItems.get("marker.next"));
		menuMarker.addSubItem(mapItems.get("marker.last"));
		mapItems.put(menuMarker.getText(), menuMarker);
		registerItem(menuMarker);

		//------- TRANSPORT --------
		this.groupName = "transport";
		registerButton("transport.first", TGGoFirstMeasureAction.NAME, TGIconManager.TRANSPORT_ICON_FIRST);
		registerButton("transport.previous", TGGoPreviousMeasureAction.NAME, TGIconManager.TRANSPORT_ICON_PREVIOUS);
		registerButton("transport.start", TGTransportPlayPauseAction.NAME, TGIconManager.TRANSPORT_ICON_PLAY, new TGMainToolBarItemUpdater() {
			@Override
			public String getIconName(TGContext context, boolean isRunning) {
				return isRunning ? TGIconManager.TRANSPORT_ICON_PAUSE : TGIconManager.TRANSPORT_ICON_PLAY;
			}
			@Override
			public String getText(TGContext context, boolean isRunning) {
				return isRunning ? "transport.pause": "transport.start";
			}
		});
		registerButton("transport.stop", TGTransportStopAction.NAME, TGIconManager.TRANSPORT_ICON_STOP,
				new TGMainToolBarItemUpdater() {
					@Override
					public boolean enabled(TGContext context, boolean isRunning) {
						return (isRunning);
					}
				});
		registerButton("transport.next", TGGoNextMeasureAction.NAME, TGIconManager.TRANSPORT_ICON_NEXT);
		registerButton("transport.last", TGGoLastMeasureAction.NAME, TGIconManager.TRANSPORT_ICON_LAST);
		// TIME COUNTER
		registerItem("toolbar.timeCounter", TGMainToolBarItem.TIME_COUNTER, TGMainToolBarSection.TYPE_GENERIC, TGIconManager.TRANSPORT_TIME_COUNTER);
		// TEMPO INDICATOR
		registerItem("toolbar.tempoIndicator", TGMainToolBarItem.TEMPO_INDICATOR, TGMainToolBarSection.TYPE_TEMPO, TGIconManager.TEMPO_INDICATOR_ICON);
		registerCheckable("transport.metronome", TGTransportMetronomeAction.NAME, TGIconManager.TRANSPORT_METRONOME,
				new TGMainToolBarItemUpdater() {
					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						return (MidiPlayer.getInstance(context).isMetronomeEnabled());
					}
				});
		registerCheckable("transport.count-down", TGTransportCountDownAction.NAME, TGIconManager.TRANSPORT_COUNT_IN,
				new TGMainToolBarItemUpdater() {
					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						return (MidiPlayer.getInstance(context).getCountDown().isEnabled());
					}
				});
		registerButton("transport.mode", TGOpenTransportModeDialogAction.NAME, TGIconManager.TRANSPORT_MODE);
		registerCheckable("transport.set-loop-start", TGTransportSetLoopSHeaderAction.NAME, TGIconManager.TRANSPORT_LOOP_START, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				return (TuxGuitar.getInstance().getPlayer().getMode().isLoop());
			}
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				TGMeasure measure = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getMeasure();
				return ((measure != null) && 
						(measure.getNumber() == TuxGuitar.getInstance().getPlayer().getMode().getLoopSHeader()));
			}
		});
		registerCheckable("transport.set-loop-end", TGTransportSetLoopEHeaderAction.NAME, TGIconManager.TRANSPORT_LOOP_END, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				return (TuxGuitar.getInstance().getPlayer().getMode().isLoop());
			}
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				TGMeasure measure = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getMeasure();
				return ((measure != null) && 
						(measure.getNumber() == TuxGuitar.getInstance().getPlayer().getMode().getLoopEHeader()));
			}
		});
		registerCheckable("transport.highlight-played-beat", TGToggleHighlightPlayedBeatAction.NAME, TGIconManager.TRANSPORT_HIGHLIGHT_PLAYED_BEAT, new TGMainToolBarItemUpdater() {
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				int style = TablatureEditor.getInstance(context).getTablature().getViewLayout().getStyle();
				return ((style & TGLayout.HIGHLIGHT_PLAYED_BEAT) != 0);
			}
		});
		registerCheckable("transport.continuous-scrolling", TGToggleContinuousScrollingAction.NAME, TGIconManager.TRANSPORT_CONTINUOUS_SCROLLING, new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				return !isRunning;
			}
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				int style = TablatureEditor.getInstance(context).getTablature().getViewLayout().getStyle();
				return ((style & TGLayout.CONTINUOUS_SCROLL) != 0);
			}
		});

		//------- tools --------
		this.groupName = "tools";
		registerButton("tools.plugins", TGOpenPluginListDialogAction.NAME, TGIconManager.TOOLS_PLUGINS);
		registerButton("tools.shortcuts", TGOpenKeyBindingEditorAction.NAME, TGIconManager.TOOLS_SHORTCUTS);
		registerButton("tools.settings", TGOpenSettingsEditorAction.NAME, TGIconManager.TOOLS_SETTINGS);

		//------- help --------
		this.groupName = "help";
		registerButton("help.doc", TGOpenDocumentationDialogAction.NAME, TGIconManager.HELP_DOC);
		registerButton("help.about", TGOpenAboutDialogAction.NAME, TGIconManager.HELP_ABOUT);
		registerButton("help.goHome", TGHelpGoHomeAction.NAME, TGIconManager.GO_HOME);

		//------- settings --------
		this.groupName = "settings";
		registerButton("toolbar.settings", TGOpenMainToolBarSettingsDialogAction.NAME, TGIconManager.SETTINGS, DISABLE_ON_PLAY);
	}

	private TGMainToolBarItemUpdater getDurationUpdater(final int value) {
		return new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				return (!isRunning);
			}
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				TGDuration duration = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getDuration();
				return (duration.getValue() == value);
			}
		};
	}

	private TGMainToolBarItemConfig getCheckableEffect(final String text, final String actionName, final String iconFileName, boolean forPercussion, EffectApplied applied) {
		TGMainToolBarItemUpdater updater = new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				TGNoteRange noteRange = TablatureEditor.getInstance(context).getTablature().getCurrentNoteRange();
				boolean isPercussion = TablatureEditor.getInstance(context).getTablature().getCaret().getTrack().isPercussion();
				return (!isRunning && !noteRange.isEmpty() && (forPercussion ||!isPercussion));
			}
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				TGNote note = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getSelectedNote();
				return ((note != null) && (note.getEffect() != null) && applied.isEffectApplied(note.getEffect()));
			}
		};
		TGMainToolBarItemConfig config = new TGMainToolBarItemConfig(this.groupName, text, TGMainToolBarItem.CHECKABLE_ITEM, TGMainToolBarSection.TYPE_TOOLITEMS,
				actionName, iconFileName, updater, false);
		return config;
	}

	private interface EffectApplied {
		boolean isEffectApplied(TGNoteEffect effect);
	}

	private TGMainToolBarItemConfig getCheckableVelocity(final String text, final int value, final String iconFileName) {
		String actionName = TGChangeVelocityAction.NAME;
		TGMainToolBarItemUpdater updater = new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				return (!isRunning);
			}
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				TGNote note = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getSelectedNote();
				int velocity = ((note != null)?note.getVelocity():TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getVelocity());
				return (velocity == value);
			}
		};
		TGMainToolBarItemConfig config = new TGMainToolBarItemConfig(this.groupName, text, TGMainToolBarItemConfig.CHECKABLE_ITEM, TGMainToolBarSection.TYPE_TOOLITEMS,
				actionName, iconFileName, updater, false);
		config.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY, value);
		return config;
	}

	private TGMainToolBarItemConfig getCheckableDivision(final int index, TGMainToolBarItemConfig menuDivisionType) {
		int enters = TGDivisionType.DIVISION_TYPES[index].getEnters();
		String text = "duration.division-type." + Integer.toString(enters);
		String actionName = TGSetDivisionTypeDurationAction.NAME;
		String iconFileName = TGIconManager.getDivisionTypeIconFileName(enters);
		TGMainToolBarItemUpdater updater = new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				return (!isRunning);
			}
			@Override
			public boolean checked(TGContext context, boolean isRunning) {
				TGDuration duration = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getDuration();
				return (duration.getDivision().isEqual(TGDivisionType.DIVISION_TYPES[index]));
			}
		};
		TGMainToolBarItemConfig config = new TGMainToolBarItemConfig(this.groupName, text, TGMainToolBarItemConfig.CHECKABLE_ITEM, TGMainToolBarSection.TYPE_TOOLITEMS,
				actionName, iconFileName, updater, false);
		config.setAttribute(TGSetDivisionTypeDurationAction.PROPERTY_DIVISION_TYPE, TGDivisionType.DIVISION_TYPES[index]);
		menuDivisionType.addSubItem(config);
		return config;
	}

	private void registerItem(TGMainToolBarItemConfig toolBarItemConfig) {
		this.mapItems.put(toolBarItemConfig.getText(), toolBarItemConfig);
		this.itemNames.add(toolBarItemConfig.getText());
		if (!this.groupNames.contains(this.groupName)) {
			this.groupNames.add(this.groupName);
		}
	}

	// not checkable button
	private void registerButton(String text, String actionName, String iconFileName, TGMainToolBarItemUpdater updater) {
		registerItem(new TGMainToolBarItemConfig(this.groupName, text, TGMainToolBarItemConfig.ACTION_ITEM, TGMainToolBarSection.TYPE_TOOLITEMS,
				actionName, iconFileName, updater, false));
	}

	// not checkable, not updatable button
	private void registerButton(String text, String actionName, String iconFileName) {
		registerItem(new TGMainToolBarItemConfig(this.groupName, text, TGMainToolBarItemConfig.ACTION_ITEM, TGMainToolBarSection.TYPE_TOOLITEMS,
				actionName, iconFileName, null, false));
	}

	// checkable button
	private void registerCheckable(String text, String actionName, String iconFileName, TGMainToolBarItemUpdater updater) {
		registerItem(new TGMainToolBarItemConfig(this.groupName, text, TGMainToolBarItemConfig.CHECKABLE_ITEM, TGMainToolBarSection.TYPE_TOOLITEMS,
				actionName, iconFileName, updater, false));
	}

	// menu
	private TGMainToolBarItemConfig newMenu(String text, String iconFileName, boolean displaySelectedItemIcon) {
		return new TGMainToolBarItemConfig(this.groupName, text, TGMainToolBarItem.MENU, TGMainToolBarSection.TYPE_TOOLITEMS, null,
				iconFileName, null, displaySelectedItemIcon);
	}

	// a few specific items
	private void registerItem(String text, int type, int sectionType, String iconFileName) {
		registerItem(new TGMainToolBarItemConfig(this.groupName, text, type, sectionType, iconFileName));
	}

	public List<String> getToolBarGroupsNames() {
		return this.groupNames;
	}

	public List<String> getToolBarItemNames() {
		return this.itemNames;
	}

	public TGMainToolBarItemConfig getToolBarItemConfig(String name) {
		return this.mapItems.get(name);
	}
}
