/*
 * Created on 17-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.action.impl.system;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.TypedEvent;
import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionBase;
import org.herac.tuxguitar.app.action.TGActionLock;
import org.herac.tuxguitar.app.action.TGActionProcessor;
import org.herac.tuxguitar.app.action.impl.file.FileActionUtils;
import org.herac.tuxguitar.app.marker.MarkerList;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.util.ConfirmDialog;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.configuration.TGConfigManager;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class DisposeAction extends TGActionBase {
	
	public static final String NAME = "action.system.dispose";
	
	public DisposeAction() {
		super(NAME, AUTO_LOCK);
	}
	
	protected void processAction(TGActionContext context){
		TypedEvent e = (TypedEvent)context.getAttribute(TGActionProcessor.PROPERTY_TYPED_EVENT);
		
		if(e instanceof ShellEvent){
			TuxGuitar.getInstance().getPlayer().reset();
			
			if(TuxGuitar.getInstance().getFileHistory().isUnsavedFile()){
				ConfirmDialog confirm = new ConfirmDialog(TuxGuitar.getProperty("file.save-changes-question"));
				confirm.setDefaultStatus( ConfirmDialog.STATUS_CANCEL );
				int status = confirm.confirm(ConfirmDialog.BUTTON_YES | ConfirmDialog.BUTTON_NO | ConfirmDialog.BUTTON_CANCEL, ConfirmDialog.BUTTON_YES);
				if(status == ConfirmDialog.STATUS_CANCEL){
					TGActionLock.unlock();
					return;
				}
				if(status == ConfirmDialog.STATUS_YES){
					final String fileName = FileActionUtils.getFileName();
					if(fileName == null){
						TGActionLock.unlock();
						return;
					}
					TuxGuitar.getInstance().loadCursor(SWT.CURSOR_WAIT);
					new Thread(new Runnable() {
						public void run() throws TGException {
							if(!TuxGuitar.isDisposed()){
								FileActionUtils.save(fileName);
								TuxGuitar.getInstance().loadCursor(SWT.CURSOR_ARROW);
								
								exit();
							}
						}
					}).start();
					return;
				}
			}
			exit();
		}
	}
	
	protected void exit(){
		try {
			TGSynchronizer.instance().executeLater(new TGSynchronizer.TGRunnable() {
				public void run() throws TGException {
					TuxGuitar.getInstance().lock();
					closeModules();
					saveConfig();
					dispose();
					TuxGuitar.getInstance().unlock();
				}
			});
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}
	
	protected void saveConfig(){
		TGConfigManager config = TuxGuitar.getInstance().getConfig();
		
		config.setValue(TGConfigKeys.LAYOUT_MODE,getEditor().getTablature().getViewLayout().getMode());
		config.setValue(TGConfigKeys.LAYOUT_STYLE,getEditor().getTablature().getViewLayout().getStyle());
		config.setValue(TGConfigKeys.SHOW_PIANO,!TuxGuitar.getInstance().getPianoEditor().isDisposed());
		config.setValue(TGConfigKeys.SHOW_MATRIX,!TuxGuitar.getInstance().getMatrixEditor().isDisposed());
		config.setValue(TGConfigKeys.SHOW_FRETBOARD,TuxGuitar.getInstance().getFretBoardEditor().isVisible());
		config.setValue(TGConfigKeys.SHOW_INSTRUMENTS,!TuxGuitar.getInstance().getChannelManager().isDisposed());
		config.setValue(TGConfigKeys.SHOW_TRANSPORT,!TuxGuitar.getInstance().getTransport().isDisposed());
		config.setValue(TGConfigKeys.SHOW_MARKERS,!MarkerList.instance().isDisposed());
		config.setValue(TGConfigKeys.MAXIMIZED,TuxGuitar.getInstance().getShell().getMaximized());
		config.setValue(TGConfigKeys.WIDTH,TuxGuitar.getInstance().getShell().getClientArea().width);
		config.setValue(TGConfigKeys.HEIGHT,TuxGuitar.getInstance().getShell().getClientArea().height);
		config.setValue(TGConfigKeys.EDITOR_MOUSE_MODE,getEditor().getTablature().getEditorKit().getMouseMode());
		config.setValue(TGConfigKeys.MATRIX_GRIDS,TuxGuitar.getInstance().getMatrixEditor().getGrids());
		
		TuxGuitar.getInstance().getConfig().save();
	}
	
	protected void closeModules(){
		TuxGuitar.getInstance().getPlayer().close();
		TuxGuitar.getInstance().getPluginManager().closePlugins();
	}
	
	protected void dispose(){
		TuxGuitar.getInstance().getTable().dispose();
		TuxGuitar.getInstance().getSongManager().clearSong();
		TuxGuitar.getInstance().getFretBoardEditor().dispose();
		TuxGuitar.getInstance().getTablatureEditor().getTablature().dispose();
		TuxGuitar.getInstance().getIconManager().disposeIcons();
		TuxGuitar.getInstance().getShell().dispose();
	}
}