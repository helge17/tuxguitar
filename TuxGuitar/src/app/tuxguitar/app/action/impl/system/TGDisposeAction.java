package app.tuxguitar.app.action.impl.system;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.icons.TGSkinManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.view.component.table.TGTableViewer;
import app.tuxguitar.app.view.dialog.fretboard.TGFretBoardEditor;
import app.tuxguitar.app.view.dialog.marker.TGMarkerList;
import app.tuxguitar.app.view.dialog.transport.TGTransportDialog;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.app.view.toolbar.edit.TGEditToolBar;
import app.tuxguitar.app.view.toolbar.main.TGMainToolBar;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.thread.TGThreadManager;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.configuration.TGConfigManager;
import app.tuxguitar.util.plugin.TGPluginManager;

public class TGDisposeAction extends TGActionBase {

	public static final String NAME = "action.system.dispose";

	public TGDisposeAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		this.closeModules();
		this.saveConfig();
		this.dispose();
	}

	protected void saveConfig(){
		TGWindow tgWindow = TGWindow.getInstance(getContext());
		TGConfigManager config = TuxGuitar.getInstance().getConfig();

		config.setValue(TGConfigKeys.LAYOUT_MODE,TablatureEditor.getInstance(getContext()).getTablature().getViewLayout().getMode());
		config.setValue(TGConfigKeys.LAYOUT_STYLE,TablatureEditor.getInstance(getContext()).getTablature().getViewLayout().getStyle());
		config.setValue(TGConfigKeys.SHOW_PIANO,!TuxGuitar.getInstance().getPianoEditor().isDisposed());
		config.setValue(TGConfigKeys.SHOW_MATRIX,!TuxGuitar.getInstance().getMatrixEditor().isDisposed());
		config.setValue(TGConfigKeys.SHOW_FRETBOARD,TuxGuitar.getInstance().getFretBoardEditor().isVisible());
		config.setValue(TGConfigKeys.SHOW_INSTRUMENTS,!TuxGuitar.getInstance().getChannelManager().isDisposed());
		config.setValue(TGConfigKeys.SHOW_TRANSPORT,!TGTransportDialog.getInstance(getContext()).isDisposed());
		config.setValue(TGConfigKeys.SHOW_MARKERS,!TGMarkerList.getInstance(getContext()).isDisposed());
		config.setValue(TGConfigKeys.SHOW_MAIN_TOOLBAR, TGMainToolBar.getInstance(getContext()).isVisible());
		config.setValue(TGConfigKeys.SHOW_EDIT_TOOLBAR, TGEditToolBar.getInstance(getContext()).isVisible());
		config.setValue(TGConfigKeys.SHOW_TRACKS, TGTableViewer.getInstance(getContext()).isVisible());
		config.setValue(TGConfigKeys.MAXIMIZED, tgWindow.getWindow().isMaximized());
		config.setValue(TGConfigKeys.WIDTH, tgWindow.getWindow().getBounds().getWidth());
		config.setValue(TGConfigKeys.HEIGHT, tgWindow.getWindow().getBounds().getHeight());
		config.setValue(TGConfigKeys.EDITOR_MOUSE_MODE,TablatureEditor.getInstance(getContext()).getTablature().getEditorKit().getMouseMode());
		config.setValue(TGConfigKeys.MATRIX_GRIDS,TuxGuitar.getInstance().getMatrixEditor().getGrids());

		TuxGuitar.getInstance().getConfig().save();
	}

	protected void closeModules(){
		MidiPlayer.getInstance(getContext()).close();
		TGPluginManager.getInstance(getContext()).disconnectAll();
	}

	protected void dispose(){
		TGTableViewer.getInstance(getContext()).dispose();
		TGFretBoardEditor.getInstance(getContext()).dispose();
		TablatureEditor.getInstance(getContext()).getTablature().dispose();
		TGWindow.getInstance(getContext()).getWindow().dispose();
		TGSkinManager.getInstance(getContext()).dispose();
		TGApplication.getInstance(getContext()).dispose();
		TGThreadManager.getInstance(getContext()).dispose();
	}
}