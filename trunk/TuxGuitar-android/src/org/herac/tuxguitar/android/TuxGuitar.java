package org.herac.tuxguitar.android;

import org.herac.tuxguitar.android.action.TGActionAdapterManager;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.error.TGErrorHandlerImpl;
import org.herac.tuxguitar.android.properties.TGPropertiesAdapter;
import org.herac.tuxguitar.android.resource.TGResourceLoaderImpl;
import org.herac.tuxguitar.android.synchronizer.TGSynchronizerControllerImpl;
import org.herac.tuxguitar.android.transport.TGTransport;
import org.herac.tuxguitar.android.transport.TGTransportAdapter;
import org.herac.tuxguitar.android.variables.TGVarAdapter;
import org.herac.tuxguitar.android.view.tablature.TGSongViewController;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.editor.undo.TGUndoableManager;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.util.TGAbstractContext;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGLock;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.error.TGErrorManager;
import org.herac.tuxguitar.util.plugin.TGPluginManager;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TuxGuitar {
	
	private TGContext context;
	private TGLock lock;
	
	private TuxGuitar(TGContext context) {
		this.context = context;
		this.lock = new TGLock();
	}
	
	public void initialize(TGActivity activity) {
		TGSynchronizer.getInstance(this.context).setController(new TGSynchronizerControllerImpl(this.context));
		TGErrorManager.getInstance(this.context).addErrorHandler(new TGErrorHandlerImpl(activity));
		TGResourceManager.getInstance(this.context).setResourceLoader(new TGResourceLoaderImpl(activity));
		TGActionAdapterManager.getInstance(this.context).initialize(activity);
		TGEditorManager.getInstance(this.context).setLockControl(this.lock);
		TGVarAdapter.initialize(this.context);
		TGPropertiesAdapter.initialize(this.context, activity);
		TGTransportAdapter.getInstance(this.context).initialize();
	}
	
	public void destroy() {
		TGTransportAdapter.getInstance(this.context).destroy();
		TGSongViewController.getInstance(this.context).dispose();
		
		this.disconnectPlugins();
		this.context.clear();
	}
	
	public void connectPlugins() {
		TGPluginManager.getInstance(this.context).connectEnabled();
	}
	
	public void disconnectPlugins() {
		TGPluginManager.getInstance(this.context).disconnectAll();
	}
	
	public TGUndoableManager getUndoableManager(){
		return TGUndoableManager.getInstance(this.context);
	}
	
	public TGEditorManager getEditorManager() {
		return TGEditorManager.getInstance(this.context);
	}
	
	public TGSongManager getSongManager(){
		return TGDocumentManager.getInstance(this.context).getSongManager();
	}
	
	public TGTransport getTransport() {
		return TGTransport.getInstance(this.context);
	}
	
	public MidiPlayer getPlayer(){
		return MidiPlayer.getInstance(this.context);
	}
	
	public void updateSavedSong(){
		this.getEditorManager().updateSavedSong();
	}
	
	public void updateLoadedSong(){
		this.getEditorManager().updateLoadedSong();
	}
	
	public void updateMeasure(int number){
		this.getEditorManager().updateMeasure(number);
	}
	
	public void updateSong(){
		this.getEditorManager().updateSong();
	}
	
	public void updateCache(final boolean updateItems){
		this.updateCache(updateItems, null);
	}
	
	public void updateCache(final boolean updateItems, TGAbstractContext sourceContext){
		if( updateItems ){
			getEditorManager().updateSelection(sourceContext);
		}
		getEditorManager().redraw(sourceContext);
	}
	
	public void setUnsavedDocument() {
		// TODO
	}
	
	public TGContext getContext() {
		return this.context;
	}
	
	public static TuxGuitar getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TuxGuitar.class.getName(), new TGSingletonFactory<TuxGuitar>() {
			public TuxGuitar createInstance(TGContext context) {
				return new TuxGuitar(context);
			}
		});
	}
}
