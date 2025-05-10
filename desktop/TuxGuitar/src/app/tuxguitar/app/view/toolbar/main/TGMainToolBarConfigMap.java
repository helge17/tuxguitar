package app.tuxguitar.app.view.toolbar.main;


/**
 * A map of all items that can be included in the main tool bar
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.tuxguitar.app.action.impl.composition.TGOpenSongInfoDialogAction;
import app.tuxguitar.app.action.impl.file.TGOpenFileAction;
import app.tuxguitar.app.action.impl.file.TGPrintAction;
import app.tuxguitar.app.action.impl.file.TGPrintPreviewAction;
import app.tuxguitar.app.action.impl.file.TGSaveAsFileAction;
import app.tuxguitar.app.action.impl.file.TGSaveFileAction;
import app.tuxguitar.app.action.impl.layout.TGSetCompactViewAction;
import app.tuxguitar.app.action.impl.layout.TGSetLayoutScaleDecrementAction;
import app.tuxguitar.app.action.impl.layout.TGSetLayoutScaleIncrementAction;
import app.tuxguitar.app.action.impl.layout.TGSetLayoutScaleResetAction;
import app.tuxguitar.app.action.impl.layout.TGSetLinearLayoutAction;
import app.tuxguitar.app.action.impl.layout.TGSetMultitrackViewAction;
import app.tuxguitar.app.action.impl.layout.TGSetPageLayoutAction;
import app.tuxguitar.app.action.impl.layout.TGSetScoreEnabledAction;
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
import app.tuxguitar.app.action.impl.transport.TGOpenTransportModeDialogAction;
import app.tuxguitar.app.action.impl.transport.TGTransportCountDownAction;
import app.tuxguitar.app.action.impl.transport.TGTransportMetronomeAction;
import app.tuxguitar.app.action.impl.transport.TGTransportStopAction;
import app.tuxguitar.app.action.impl.view.TGOpenMainToolBarSettingsDialogAction;
import app.tuxguitar.app.action.impl.view.TGToggleChannelsDialogAction;
import app.tuxguitar.app.action.impl.view.TGToggleEditToolbarAction;
import app.tuxguitar.app.action.impl.view.TGToggleFretBoardEditorAction;
import app.tuxguitar.app.action.impl.view.TGToggleTableViewerAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.view.component.table.TGTableViewer;
import app.tuxguitar.app.view.dialog.channel.TGChannelManagerDialog;
import app.tuxguitar.app.view.dialog.fretboard.TGFretBoardEditor;
import app.tuxguitar.app.view.toolbar.edit.TGEditToolBar;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.action.edit.TGRedoAction;
import app.tuxguitar.editor.action.edit.TGUndoAction;
import app.tuxguitar.editor.action.file.TGLoadTemplateAction;
import app.tuxguitar.editor.action.track.TGAddNewTrackAction;
import app.tuxguitar.editor.action.track.TGRemoveTrackAction;
import app.tuxguitar.editor.undo.TGUndoableManager;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.graphics.control.TGLayoutHorizontal;
import app.tuxguitar.graphics.control.TGLayoutVertical;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

public class TGMainToolBarConfigMap {
	private Map<String, TGMainToolBarItem> mapItems;
	// ordered list of control names, for display in dialof
	private List<String> itemNames;

	public TGMainToolBarConfigMap() {
		this.mapItems = new HashMap<String, TGMainToolBarItem>();
		this.itemNames = new ArrayList<String>();

		// a generic updater
		TGMainToolBarItemUpdater DISABLE_ON_PLAY = new TGMainToolBarItemUpdater() {
			@Override
			public boolean enabled(TGContext context, boolean isRunning) {
				return (!isRunning);
			}
		};

		// SEPARATOR
		registerItem(new TGMainToolBarItem("toolbar.separator", TGMainToolBarItem.SEPARATOR, null, null, null));

		// TIME COUNTER
		registerItem(new TGMainToolBarItem("toolbar.timeCounter", TGMainToolBarItem.TIME_COUNTER, null, null, null));

		// TEMPO INDICATOR
		registerItem(new TGMainToolBarItem("toolbar.tempoIndicator", TGMainToolBarItem.TEMPO_INDICATOR, null, TGIconManager.TRANSPORT_METRONOME, null));

		// ACTION ITEMS (checkable or not)
		registerItem(new TGMainToolBarItemButton("file.new", TGLoadTemplateAction.NAME, TGIconManager.FILE_NEW, null));
		registerItem(new TGMainToolBarItemButton("file.open", TGOpenFileAction.NAME, TGIconManager.FILE_OPEN, null));
		registerItem(new TGMainToolBarItemButton("file.save", TGSaveFileAction.NAME, TGIconManager.FILE_SAVE, null));
		registerItem(
				new TGMainToolBarItemButton("file.save-as", TGSaveAsFileAction.NAME, TGIconManager.FILE_SAVE_AS, null));
		registerItem(new TGMainToolBarItemButton("file.print-preview", TGPrintPreviewAction.NAME,
				TGIconManager.PRINT_PREVIEW, null));
		registerItem(new TGMainToolBarItemButton("file.print", TGPrintAction.NAME, TGIconManager.PRINT, null));

		registerItem(new TGMainToolBarItemButton("edit.undo", TGUndoAction.NAME, TGIconManager.UNDO,
				new TGMainToolBarItemUpdater() {
					@Override
					public boolean enabled(TGContext context, boolean isRunning) {
						TGUndoableManager undoableManager = TGUndoableManager.getInstance(context);
						return (!isRunning && undoableManager.canUndo());
					}
				}));
		registerItem(new TGMainToolBarItemButton("edit.redo", TGRedoAction.NAME, TGIconManager.REDO,
				new TGMainToolBarItemUpdater() {
					@Override
					public boolean enabled(TGContext context, boolean isRunning) {
						TGUndoableManager undoableManager = TGUndoableManager.getInstance(context);
						return (!isRunning && undoableManager.canRedo());
					}
				}));

		registerItem(new TGMainToolBarItemButton("view.show-edit-toolbar", true, TGToggleEditToolbarAction.NAME,
				TGIconManager.TOOLBAR_EDIT, new TGMainToolBarItemUpdater() {
					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						return TGEditToolBar.getInstance(context).isVisible();
					}
				}));
		registerItem(new TGMainToolBarItemButton("view.show-table-viewer", true, TGToggleTableViewerAction.NAME,
				TGIconManager.TABLE_VIEWER, new TGMainToolBarItemUpdater() {
					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						return TGTableViewer.getInstance(context).isVisible();
					}
				}));
		registerItem(new TGMainToolBarItemButton("view.show-fretboard", true, TGToggleFretBoardEditorAction.NAME,
				TGIconManager.FRETBOARD, new TGMainToolBarItemUpdater() {
					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						return TGFretBoardEditor.getInstance(context).isVisible();
					}
				}));
		registerItem(new TGMainToolBarItemButton("view.show-instruments", true, TGToggleChannelsDialogAction.NAME,
				TGIconManager.INSTRUMENTS, new TGMainToolBarItemUpdater() {
					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						return !TGChannelManagerDialog.getInstance(context).isDisposed();
					}
				}));

		registerItem(new TGMainToolBarItemButton("view.layout.page", TGSetPageLayoutAction.NAME,
				TGIconManager.LAYOUT_PAGE, new TGMainToolBarItemUpdater() {
					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						TGLayout layout = TablatureEditor.getInstance(context).getTablature().getViewLayout();
						return (layout instanceof TGLayoutVertical);
					}
				}));

		registerItem(new TGMainToolBarItemButton("view.layout.linear", TGSetLinearLayoutAction.NAME,
				TGIconManager.LAYOUT_LINEAR, new TGMainToolBarItemUpdater() {
					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						TGLayout layout = TablatureEditor.getInstance(context).getTablature().getViewLayout();
						return (layout instanceof TGLayoutHorizontal);
					}
				}));

		registerItem(new TGMainToolBarItemButton("view.layout.multitrack", TGSetMultitrackViewAction.NAME,
				TGIconManager.LAYOUT_MULTITRACK, new TGMainToolBarItemUpdater() {
					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						TGLayout layout = TablatureEditor.getInstance(context).getTablature().getViewLayout();
						int style = layout.getStyle();
						return ((style & TGLayout.DISPLAY_MULTITRACK) != 0);
					}
				}));

		registerItem(new TGMainToolBarItemButton("view.layout.score-enabled", TGSetScoreEnabledAction.NAME,
				TGIconManager.LAYOUT_SCORE, new TGMainToolBarItemUpdater() {
					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						TGLayout layout = TablatureEditor.getInstance(context).getTablature().getViewLayout();
						int style = layout.getStyle();
						return ((style & TGLayout.DISPLAY_SCORE) != 0);
					}
				}));

		registerItem(new TGMainToolBarItemButton("view.layout.compact", TGSetCompactViewAction.NAME,
				TGIconManager.LAYOUT_COMPACT, new TGMainToolBarItemUpdater() {
					@Override
					public boolean enabled(TGContext context, boolean isRunning) {
						TGLayout layout = TablatureEditor.getInstance(context).getTablature().getViewLayout();
						int style = layout.getStyle();
						TGSong song = TGDocumentManager.getInstance(context).getSong();
						return ((style & TGLayout.DISPLAY_MULTITRACK) == 0 || song.countTracks() == 1);
					}

					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						TGLayout layout = TablatureEditor.getInstance(context).getTablature().getViewLayout();
						int style = layout.getStyle();
						return ((style & TGLayout.DISPLAY_COMPACT) != 0);
					}
				}));

		registerItem(new TGMainToolBarItemButton("view.zoom.out", TGSetLayoutScaleDecrementAction.NAME,
				TGIconManager.ZOOM_OUT, null));
		registerItem(new TGMainToolBarItemButton("view.zoom.reset", TGSetLayoutScaleResetAction.NAME,
				TGIconManager.ZOOM_RESET, null));
		registerItem(new TGMainToolBarItemButton("view.zoom.in", TGSetLayoutScaleIncrementAction.NAME,
				TGIconManager.ZOOM_IN, null));
		registerItem(new TGMainToolBarItemButton("composition.properties", TGOpenSongInfoDialogAction.NAME,
				TGIconManager.SONG_PROPERTIES, null));
		registerItem(new TGMainToolBarItemButton("track.add", TGAddNewTrackAction.NAME, TGIconManager.TRACK_ADD,
				DISABLE_ON_PLAY));
		registerItem(new TGMainToolBarItemButton("track.remove", TGRemoveTrackAction.NAME, TGIconManager.TRACK_REMOVE,
				DISABLE_ON_PLAY));
		registerItem(new TGMainToolBarItemButton("marker.add", TGOpenMarkerEditorAction.NAME, TGIconManager.MARKER_ADD,
				null));
		registerItem(new TGMainToolBarItemButton("marker.list", TGToggleMarkerListAction.NAME,
				TGIconManager.MARKER_LIST, null));
		registerItem(new TGMainToolBarItemButton("marker.first", TGGoFirstMarkerAction.NAME, TGIconManager.MARKER_FIRST,
				null));
		registerItem(new TGMainToolBarItemButton("marker.previous", TGGoPreviousMarkerAction.NAME,
				TGIconManager.MARKER_PREVIOUS, null));
		registerItem(
				new TGMainToolBarItemButton("marker.next", TGGoNextMarkerAction.NAME, TGIconManager.MARKER_NEXT, null));
		registerItem(
				new TGMainToolBarItemButton("marker.last", TGGoLastMarkerAction.NAME, TGIconManager.MARKER_LAST, null));
		registerItem(new TGMainToolBarItemButton("transport.first", TGGoFirstMeasureAction.NAME,
				TGIconManager.TRANSPORT_ICON_FIRST, null));
		registerItem(new TGMainToolBarItemButton("transport.previous", TGGoPreviousMeasureAction.NAME,
				TGIconManager.TRANSPORT_ICON_PREVIOUS, null));
		TGMainToolBarItemPlay itemPlay = new TGMainToolBarItemPlay("transport.start");
		mapItems.put(itemPlay.getText(), itemPlay);
		registerItem(new TGMainToolBarItemButton("transport.stop", TGTransportStopAction.NAME,
				TGIconManager.TRANSPORT_ICON_STOP, null));
		registerItem(new TGMainToolBarItemButton("transport.next", TGGoNextMeasureAction.NAME,
				TGIconManager.TRANSPORT_ICON_NEXT, null));
		registerItem(new TGMainToolBarItemButton("transport.last", TGGoLastMeasureAction.NAME,
				TGIconManager.TRANSPORT_ICON_LAST, null));

		registerItem(new TGMainToolBarItemButton("transport.metronome", true, TGTransportMetronomeAction.NAME,
				TGIconManager.TRANSPORT_METRONOME, new TGMainToolBarItemUpdater() {
					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						MidiPlayer player = MidiPlayer.getInstance(context);
						return (player.isMetronomeEnabled());
					}
				}));

		registerItem(new TGMainToolBarItemButton("transport.count-down", true, TGTransportCountDownAction.NAME,
				TGIconManager.TRANSPORT_COUNT_IN, new TGMainToolBarItemUpdater() {
					@Override
					public boolean checked(TGContext context, boolean isRunning) {
						MidiPlayer player = MidiPlayer.getInstance(context);
						return (player.getCountDown().isEnabled());
					}
				}));

		registerItem(new TGMainToolBarItemButton("transport.mode", TGOpenTransportModeDialogAction.NAME,
				TGIconManager.TRANSPORT_MODE, null));

		registerItem(new TGMainToolBarItemButton("toolbar.settings", TGOpenMainToolBarSettingsDialogAction.NAME, TGIconManager.SETTINGS, DISABLE_ON_PLAY));

		// MENUS
		TGMainToolBarItemMenu menuLayout = new TGMainToolBarItemMenu("view.layout", TGIconManager.LAYOUT_SCORE);
		menuLayout.addMenuItem(mapItems.get("view.layout.page"));
		menuLayout.addMenuItem(mapItems.get("view.layout.linear"));
		menuLayout.addMenuItem(mapItems.get("view.layout.multitrack"));
		menuLayout.addMenuItem(mapItems.get("view.layout.score-enabled"));
		menuLayout.addMenuItem(mapItems.get("view.layout.compact"));
		mapItems.put(menuLayout.getText(), menuLayout);

		TGMainToolBarItemMenu menuMarker = new TGMainToolBarItemMenu("marker", TGIconManager.MARKER_LIST);
		menuMarker.addMenuItem(mapItems.get("marker.add"));
		menuMarker.addMenuItem(mapItems.get("marker.list"));
		menuMarker.addMenuItem(mapItems.get("toolbar.separator"));
		menuMarker.addMenuItem(mapItems.get("marker.first"));
		menuMarker.addMenuItem(mapItems.get("marker.previous"));
		menuMarker.addMenuItem(mapItems.get("marker.next"));
		menuMarker.addMenuItem(mapItems.get("marker.last"));
		mapItems.put(menuMarker.getText(), menuMarker);
	}

	private void registerItem(TGMainToolBarItem toolBarItem) {
		this.mapItems.put(toolBarItem.getText(), toolBarItem);
		this.itemNames.add(toolBarItem.getText());
	}

	public List<String> getToolBarItemNames() {
		return this.itemNames;
	}

	public TGMainToolBarItem getToolBarItem(String name) {
		return this.mapItems.get(name);
	}
}
