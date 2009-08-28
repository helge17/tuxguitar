package org.herac.tuxguitar.gui;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.swing.SwingUtilities;

import org.herac.tuxguitar.gui.system.config.TGConfig;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.TGSynchronizer.TGSynchronizerTask;

public class TGApplet extends Applet{
	
	public TGApplet(){
		super();
	}
	
	public void init(){
		applicationInit();
	}
	
	public void destroy(){
		applicationDestroy();
	}
	
	public void start(){
		this.setFocus();
		this.repaint();
	}
	
	public void repaint(){
		super.repaint();
	}
	
	public void update(Graphics g){
		this.paint(g);
	}
	
	public void applicationInit() {
		this.initConfig();
		this.initSynchronizer();
		
		TuxGuitar.instance().setShell(this);
		
	    this.setLayout(new BorderLayout());
	    this.add( TuxGuitar.instance().getToolBar().getPanel() ,BorderLayout.NORTH);
	    this.add( TuxGuitar.instance().getTablatureEditor().getPanel(),BorderLayout.CENTER);
	    this.add( TuxGuitar.instance().getTablatureEditor().getScrollBar(),BorderLayout.EAST);
	    this.setVisible(true);
	    this.setFocus();
	    
	    this.load();
	}
	
	public void applicationDestroy(){
		TuxGuitar.instance().lock();
		TuxGuitar.instance().getPlayer().close();
		TuxGuitar.instance().getTablatureEditor().dispose();
		TuxGuitar.instance().getSongManager().clearSong();
		TuxGuitar.instance().unlock();
		this.removeAll();
	}
	
	public Frame getFrame(){ 
		Container parent = this;
		while(parent != null){
			if (parent instanceof Frame){
				return (Frame)parent;
			}
			parent = parent.getParent();
		}
		return null;
	}
	
	public void setFocus(){
		this.setFocusable(true);
		this.requestFocus();
	}
	
	private void initSynchronizer(){
		TGSynchronizer.instance().setController(new TGSynchronizer.TGSynchronizerController() {
			public void execute(final TGSynchronizer.TGSynchronizerTask task) {
				try{
					// Just excecute the task if it is on the same thread.
					if( SwingUtilities.isEventDispatchThread() ){
						task.run();
					}else{
						SwingUtilities.invokeAndWait(new Runnable() {
							public void run() {
								task.run();
							}
						});
					}
				}catch(Throwable throwable){
					throwable.printStackTrace();
				}
			}
			public void executeLater(final TGSynchronizerTask task) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						task.run();
					}
				});
			}
		});
	}
	
	private void initConfig(){
		TGConfig.SONG_URL = getParameter("song_url_download");
		TGConfig.SOUNDBANK_URL = getParameter("soundbank_url_download");
		TGConfig.LOOK_FEEL = getParameter("look_and_feel");
		TGConfig.MIDI_PORT = getParameter("midi_port");
	}
	
	public void load(){
		new Thread(new Runnable() {
			public void run() {
				TuxGuitar.instance().getTablatureEditor().loadTablature();
				TuxGuitar.instance().getkeyBindingManager().appendListenersTo( TGApplet.this );
				
				loadSong();
				
				TuxGuitar.instance().getTablatureEditor().start();
			}
		}).start();
	}
	
	public void loadSong(){
		try{
			if(TGConfig.SONG_URL != null){
				URL url = new URL(TGConfig.SONG_URL);
				InputStream stream = getInputStream(url.openStream());
				TGSong song = TGFileFormatManager.instance().getLoader().load(TuxGuitar.instance().getSongManager().getFactory(),stream);
				TuxGuitar.instance().fireNewSong(song);
			}
		}catch(Throwable t){
			t.printStackTrace();
			TuxGuitar.instance().newSong();
		}
	}
	
	private InputStream getInputStream(InputStream in)throws Throwable {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read = 0;
		while((read = in.read()) != -1){
			out.write(read);
		}
		byte[] bytes = out.toByteArray();
		in.close();
		out.close();
		out.flush();
		return new ByteArrayInputStream(bytes);
	}
}
