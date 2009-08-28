package org.herac.tuxguitar.gui.editors;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.actions.measure.SelectMeasureAction;
import org.herac.tuxguitar.gui.editors.tab.Tablature;
import org.herac.tuxguitar.gui.system.config.TGConfig;

public class TablatureEditor extends Canvas{
	
	private Tablature tablature;
	private TGScrollBar scrollBar;
	private Dimension size;
	private Image bufferImage;
	private Graphics2D bufferGraphics;
	private boolean loaded;
	private boolean started;
	
	public TablatureEditor(){
		this.loaded = false;
		this.started = false;
		this.size = new Dimension();
		this.setFocusable(false);
	}
	
	public Component getPanel(){
		this.scrollBar = new TGScrollBar();
		this.tablature = new Tablature(this, this.scrollBar);
		this.addMouseListener( TuxGuitar.instance().getAction(SelectMeasureAction.NAME) );
		return this;
	}
	
	public Component getScrollBar(){
		return this.scrollBar.getComponent();
	}
	
	public Tablature getTablature(){		
		return this.tablature;
	}
	
	public void repaint(){
		if(this.loaded && this.tablature != null){
			this.tablature.beforeRedraw();
		}
		super.repaint();
	}
	
	public void paint(Graphics g){
		// Check the offcreen buffer.
		Dimension size = getSize();
		if(this.bufferImage == null || this.size.width != size.width || this.size.height != size.height){
			this.size.setSize(size);
			this.bufferImage = createImage(this.size.width,this.size.height);
			this.bufferGraphics = (Graphics2D)this.bufferImage.getGraphics();
		}

		// Paint the tablature into the buffer.
		if(this.loaded && this.started){
			this.bufferGraphics.setBackground( this.tablature.getViewLayout().getResources().getBackgroundColor());
			this.bufferGraphics.clearRect(0, 0, this.size.width, this.size.height);
			this.tablature.paintTablature(new TGPainter( this.bufferGraphics ));
		}

		// Paint a "loading" message.
		else{
			this.bufferGraphics.setBackground( Color.WHITE );
			this.bufferGraphics.clearRect(0, 0, this.size.width, this.size.height);
			this.paintLoading(new TGPainter( this.bufferGraphics ));
		}
		
		// Draw the buffer image
		g.drawImage(this.bufferImage,0,0,this);
	}
	
	public void update(Graphics g){
		this.paint(g);
	}
	
	public void loadTablature(){
		this.tablature.setSongManager( TuxGuitar.instance().getSongManager() );
		this.tablature.initDefaults();
		this.tablature.reloadViewLayout();
		this.tablature.updateTablature();
		this.tablature.initCaret();
		this.loaded = true;
	}
	
	public void dispose(){
		if(this.loaded && this.tablature != null){
			this.loaded = false;
			this.tablature.dispose();
		}
	}
	
	public void start(){
		this.started = true;
		this.repaint();
	}
	
	public boolean isStarted(){
		return this.started;
	}
	
	private void paintLoading(TGPainter painter){
		painter.setFont( TGConfig.FONT_LOADING_MESSAGE );
		painter.setForeground( TGConfig.COLOR_LOADING_MESSAGE );
		String msg = "Loading ...";
		Point p = painter.getStringExtent( msg );
		painter.drawString(msg, ((this.size.width / 2) - (p.x / 2)), (this.size.height / 2) - (p.y / 2));
	}
}
