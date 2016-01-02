package org.herac.tuxguitar.app;

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

import org.herac.tuxguitar.app.system.config.TGConfig;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGSongLoaderHandle;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGApplet extends Applet{
	
	private static final long serialVersionUID = -5282932001028049828L;

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
		TuxGuitar.instance().getDocumentManager().setSong(null);
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
		TGSynchronizer.getInstance(TuxGuitar.instance().getContext()).setController(new TGSynchronizer.TGSynchronizerController() {
			
			public void executeLater(final Runnable runnable) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						runnable.run();
					}
				});
			}
		});
	}
	
	private void initConfig(){
		TGConfig.SONG_URL = getParameter("song_url_download");
		TGConfig.LOOK_FEEL = getParameter("look_and_feel");
		TGConfig.MIDI_PORT = getParameter("midi_port");
	}
	
	public void load(){
		new Thread(new Runnable() {
			public void run() {
				TuxGuitar.instance().getActionAdapterManager().initialize();
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
				TGContext context = TuxGuitar.instance().getContext();
				TGFileFormatManager.getInstance(context).addInputStream(new org.herac.tuxguitar.io.tg.v10.TGInputStream());
				TGFileFormatManager.getInstance(context).addInputStream(new org.herac.tuxguitar.io.tg.v11.TGInputStream());
				TGFileFormatManager.getInstance(context).addInputStream(new org.herac.tuxguitar.io.tg.v12.TGInputStream());
				
				URL url = new URL(TGConfig.SONG_URL);
				
				TGSongLoaderHandle tgSongLoaderHandle = new TGSongLoaderHandle();
				tgSongLoaderHandle.setFactory(TuxGuitar.instance().getSongManager().getFactory());
				tgSongLoaderHandle.setInputStream(getInputStream(url.openStream()));
				TGFileFormatManager.getInstance(context).getLoader().load(tgSongLoaderHandle);
				
				TuxGuitar.instance().fireNewSong(tgSongLoaderHandle.getSong());
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
