package org.herac.tuxguitar.app.action.impl.system;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.view.component.tab.TablatureEditor;
import org.herac.tuxguitar.app.view.dialog.marker.TGMarkerList;
import org.herac.tuxguitar.app.view.dialog.transport.TGTransportDialog;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.configuration.TGConfigManager;

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
		TGConfigManager config = TuxGuitar.getInstance().getConfig();
		
		config.setValue(TGConfigKeys.LAYOUT_MODE,TablatureEditor.getInstance(getContext()).getTablature().getViewLayout().getMode());
		config.setValue(TGConfigKeys.LAYOUT_STYLE,TablatureEditor.getInstance(getContext()).getTablature().getViewLayout().getStyle());
		config.setValue(TGConfigKeys.SHOW_PIANO,!TuxGuitar.getInstance().getPianoEditor().isDisposed());
		config.setValue(TGConfigKeys.SHOW_MATRIX,!TuxGuitar.getInstance().getMatrixEditor().isDisposed());
		config.setValue(TGConfigKeys.SHOW_FRETBOARD,TuxGuitar.getInstance().getFretBoardEditor().isVisible());
		config.setValue(TGConfigKeys.SHOW_INSTRUMENTS,!TuxGuitar.getInstance().getChannelManager().isDisposed());
		config.setValue(TGConfigKeys.SHOW_TRANSPORT,!TGTransportDialog.getInstance(getContext()).isDisposed());
		config.setValue(TGConfigKeys.SHOW_MARKERS,!TGMarkerList.getInstance(getContext()).isDisposed());
		config.setValue(TGConfigKeys.MAXIMIZED,TuxGuitar.getInstance().getShell().getMaximized());
		config.setValue(TGConfigKeys.WIDTH,TuxGuitar.getInstance().getShell().getClientArea().width);
		config.setValue(TGConfigKeys.HEIGHT,TuxGuitar.getInstance().getShell().getClientArea().height);
		config.setValue(TGConfigKeys.EDITOR_MOUSE_MODE,TablatureEditor.getInstance(getContext()).getTablature().getEditorKit().getMouseMode());
		config.setValue(TGConfigKeys.MATRIX_GRIDS,TuxGuitar.getInstance().getMatrixEditor().getGrids());
		
		TuxGuitar.getInstance().getConfig().save();
	}
	
	protected void closeModules(){
		TuxGuitar.getInstance().getPlayer().close();
		TuxGuitar.getInstance().getPluginManager().disconnectAll();
	}
	
	protected void dispose(){
		TuxGuitar.getInstance().getTable().dispose();
		TuxGuitar.getInstance().getFretBoardEditor().dispose();
		TuxGuitar.getInstance().getTablatureEditor().getTablature().dispose();
		TuxGuitar.getInstance().getIconManager().disposeIcons();
		TuxGuitar.getInstance().getShell().dispose();
	}
}