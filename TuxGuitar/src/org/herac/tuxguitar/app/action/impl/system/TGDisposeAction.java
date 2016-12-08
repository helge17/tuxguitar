package org.herac.tuxguitar.app.action.impl.system;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.color.TGColorManager;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.component.table.TGTableViewer;
import org.herac.tuxguitar.app.view.dialog.fretboard.TGFretBoardEditor;
import org.herac.tuxguitar.app.view.dialog.marker.TGMarkerList;
import org.herac.tuxguitar.app.view.dialog.transport.TGTransportDialog;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.toolbar.edit.TGEditToolBar;
import org.herac.tuxguitar.app.view.toolbar.main.TGMainToolBar;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.configuration.TGConfigManager;
import org.herac.tuxguitar.util.plugin.TGPluginManager;

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
		TGIconManager.getInstance(getContext()).disposeIcons();
		TGColorManager.getInstance(getContext()).dispose();
		TGWindow.getInstance(getContext()).getWindow().dispose();
		TGApplication.getInstance(getContext()).dispose();
	}
}