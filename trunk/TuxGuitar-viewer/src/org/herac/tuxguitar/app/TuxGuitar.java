/*
 * Created on 25-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app;

import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.app.action.TGActionAdapterManager;
import org.herac.tuxguitar.app.editors.EditorCache;
import org.herac.tuxguitar.app.editors.TablatureEditor;
import org.herac.tuxguitar.app.system.config.TGConfig;
import org.herac.tuxguitar.app.system.keybindings.KeyBindingActionManager;
import org.herac.tuxguitar.app.toolbar.TGToolBar;
import org.herac.tuxguitar.app.transport.TGTransport;
import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.impl.sequencer.MidiSequencerProviderImpl;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGLock;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class TuxGuitar {
	
	private static TuxGuitar instance;
	
	private TGLock lock;
	
	private TGContext context;
	
	private TGApplet shell;
	
	private MidiPlayer player;
	
	private KeyBindingActionManager keyBindingManager;
	
	private EditorCache editorCache;
	
	private TablatureEditor tablatureEditor;
	
	private TGToolBar toolBar;
	
	private TGTransport transport;
	
	private TuxGuitar() {
		this.lock = new TGLock();
		this.context = new TGContext();
	}
	
	public static TuxGuitar instance() {
		if (instance == null) {
			synchronized (TuxGuitar.class) {
				instance = new TuxGuitar();
			}
		}
		return instance;
	}
	
	public TGDocumentManager getDocumentManager(){
		return TGDocumentManager.getInstance(this.context);
	}
	
	public TGSongManager getSongManager(){
		return getDocumentManager().getSongManager();
	}
	
	public TGActionManager getActionManager(){
		return TGActionManager.getInstance(this.context);
	}
	
	public TGActionAdapterManager getActionAdapterManager() {
		return TGActionAdapterManager.getInstance(this.context);
	}
	
	public TablatureEditor getTablatureEditor(){
		if( this.tablatureEditor == null ){
			this.tablatureEditor = new TablatureEditor(this.context);
		}
		return this.tablatureEditor;
	}
	
	public TGToolBar getToolBar(){
		if( this.toolBar == null ){
			this.toolBar = new TGToolBar(this.context);
		}
		return this.toolBar;
	}
	
	public TGTransport getTransport(){
		if( this.transport == null ){
			this.transport = new TGTransport(this.context);
		}
		return this.transport;
	}
	
	public EditorCache getEditorCache(){
		if( this.editorCache == null ){
			this.editorCache = new EditorCache();
		}
		return this.editorCache;
	}
	
	public KeyBindingActionManager getkeyBindingManager(){
		if( this.keyBindingManager == null ){
			this.keyBindingManager = new KeyBindingActionManager(this.context);
		}
		return this.keyBindingManager;
	}
	
	public MidiPlayer getPlayer(){
		if( this.player == null){
			this.player = MidiPlayer.getInstance(this.context);
			this.player.init(getDocumentManager());
			try {
				getPlayer().addSequencerProvider(new MidiSequencerProviderImpl());
				getPlayer().addOutputPortProvider(new org.herac.tuxguitar.player.impl.jsa.midiport.MidiPortProviderImpl());
				
				//check midi sequencer
				getPlayer().openSequencer(TGConfig.MIDI_SEQUENCER, true);
				
				//check midi port
				getPlayer().openOutputPort(TGConfig.MIDI_PORT, true);
			} catch (MidiPlayerException e) {
				e.printStackTrace();
			}
		}
		return this.player;
	}
	
	public void updateCache(boolean updateItems){
		this.lock();
		this.getEditorCache().updateEditMode();
		if( updateItems ){
			this.getToolBar().updateItems();
		}
		this.unlock();
		this.redraw();
	}
	
	protected void redraw(){
		this.lock();
		this.getTablatureEditor().repaint();
		this.unlock();
	}
	
	public void redrawPayingMode(){
		this.lock();
		this.getEditorCache().updatePlayMode();
		if( this.getEditorCache().shouldRedraw() ){
			this.getTablatureEditor().repaint();
		}
		this.unlock();
	}
	
	public void newSong(){
		fireNewSong(getSongManager().newSong());
	}
	
	public void fireNewSong(TGSong song){
		this.lock();
		
		getDocumentManager().setSong(song);

		getPlayer().reset();
		getEditorCache().reset();
		getTablatureEditor().getTablature().updateTablature();
		getTablatureEditor().getTablature().resetScroll();
		getTablatureEditor().getTablature().initCaret();
		
		this.unlock();
		
		updateCache(true);
	}
	
	public void fireUpdate(){
		this.lock();
		
		this.getEditorCache().reset();
		this.getTablatureEditor().getTablature().updateTablature();
		
		this.unlock();
	}
	
	public void setFocus(){
		if(getShell() != null ){
			getShell().setFocus();
		}
	}
	
	public TGApplet getShell() {
		return this.shell;
	}

	public void setShell(TGApplet shell) {
		this.shell = shell;
	}
	
	public TGContext getContext() {
		return context;
	}

	public void lock(){
		this.lock.lock();
	}
	
	public void unlock(){
		this.lock.unlock();
	}
	
	public boolean tryLock(){
		return this.lock.tryLock();
	}
	
	public boolean isLocked(){
		return this.lock.isLocked();
	}
}