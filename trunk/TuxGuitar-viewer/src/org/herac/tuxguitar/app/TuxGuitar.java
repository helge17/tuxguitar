/*
 * Created on 25-nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app;

import org.herac.tuxguitar.app.actions.Action;
import org.herac.tuxguitar.app.actions.ActionManager;
import org.herac.tuxguitar.app.editors.EditorCache;
import org.herac.tuxguitar.app.editors.TablatureEditor;
import org.herac.tuxguitar.app.editors.tab.TGFactoryImpl;
import org.herac.tuxguitar.app.system.config.TGConfig;
import org.herac.tuxguitar.app.system.keybindings.KeyBindingActionManager;
import org.herac.tuxguitar.app.toolbar.TGToolBar;
import org.herac.tuxguitar.app.transport.TGTransport;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.impl.sequencer.MidiSequencerProviderImpl;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGLock;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class TuxGuitar {
	
	private static TuxGuitar instance;
	
	private TGLock lock;
	
	private TGApplet shell;
	
	private MidiPlayer player;
	
	private TGSongManager songManager;
	
	private KeyBindingActionManager keyBindingManager;
	
	private EditorCache editorCache;
	
	private TablatureEditor tablatureEditor;
	
	private TGToolBar toolBar;
	
	private TGTransport transport;
	
	private ActionManager actionManager;
	
	private TuxGuitar() {
		this.lock = new TGLock();
	}
	
	public static TuxGuitar instance() {
		if (instance == null) {
			synchronized (TuxGuitar.class) {
				instance = new TuxGuitar();
			}
		}
		return instance;
	}
	
	public TablatureEditor getTablatureEditor(){
		if(this.tablatureEditor == null){
			this.tablatureEditor = new TablatureEditor();
		}
		return this.tablatureEditor;
	}
	
	public TGToolBar getToolBar(){
		if(this.toolBar == null){
			this.toolBar = new TGToolBar();
		}
		return this.toolBar;
	}
	
	public TGSongManager getSongManager(){
		if(this.songManager == null){
			this.songManager = new TGSongManager(new TGFactoryImpl());
			this.songManager.setSong(this.songManager.newSong());
		}
		return this.songManager;
	}
	
	public TGTransport getTransport(){
		if(this.transport == null){
			this.transport = new TGTransport();
		}
		return this.transport;
	}
	
	public EditorCache getEditorCache(){
		if(this.editorCache == null){
			this.editorCache = new EditorCache();
		}
		return this.editorCache;
	}
	
	public ActionManager getActionManager() {
		if(this.actionManager == null){
			this.actionManager = new ActionManager();
		}
		return this.actionManager;
	}
	
	public KeyBindingActionManager getkeyBindingManager(){
		if(this.keyBindingManager == null){
			this.keyBindingManager = new KeyBindingActionManager();
		}
		return this.keyBindingManager;
	}
	
	public MidiPlayer getPlayer(){
		if(this.player == null){
			this.player = new MidiPlayer();
			this.player.init(getSongManager());
			try {
				getPlayer().addSequencerProvider(new MidiSequencerProviderImpl());
				getPlayer().addSequencerProvider(new org.herac.tuxguitar.player.impl.jsa.sequencer.MidiSequencerProviderImpl());
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
		if(this.getEditorCache().shouldRedraw()){
			this.getTablatureEditor().getTablature().redrawPlayingMode();
		}
		this.unlock();
	}
	
	public Action getAction(String name) {
		return this.getActionManager().getAction(name);
	}
	
	public void newSong(){
		fireNewSong(getSongManager().newSong());
	}
	
	public void fireNewSong(TGSong song){
		this.lock();
		
		getSongManager().setSong(song);

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
	
	public void lock(){
		this.lock.lock();
	}
	
	public void unlock(){
		this.lock.unlock();
	}
	
	public boolean isLocked(){
		return this.lock.isLocked();
	}
}