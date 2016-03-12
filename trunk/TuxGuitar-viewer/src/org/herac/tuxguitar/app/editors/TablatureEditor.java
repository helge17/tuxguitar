package org.herac.tuxguitar.app.editors;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionProcessorListener;
import org.herac.tuxguitar.app.action.impl.measure.SelectMeasureAction;
import org.herac.tuxguitar.app.editors.tab.Tablature;
import org.herac.tuxguitar.app.system.config.TGConfig;
import org.herac.tuxguitar.awt.graphics.TGColorImpl;
import org.herac.tuxguitar.awt.graphics.TGPainterImpl;
import org.herac.tuxguitar.util.TGContext;

public class TablatureEditor extends Canvas {
	
	private static final long serialVersionUID = 3033840631768147247L;
	
	private TGContext context;
	private Tablature tablature;
	private TGScrollBar scrollBar;
	private Dimension size;
	private Image bufferImage;
	private Graphics2D bufferGraphics;
	private boolean loaded;
	private boolean started;
	
	public TablatureEditor(TGContext context){
		this.context = context;
		this.loaded = false;
		this.started = false;
		this.size = new Dimension();
		this.setFocusable(false);
	}
	
	public Component getPanel(){
		this.scrollBar = new TGScrollBar();
		this.tablature = new Tablature(this, this.scrollBar);
		this.addMouseListener(new TGActionProcessorListener(this.context, SelectMeasureAction.NAME));
		return this;
	}
	
	public Component getScrollBar(){
		return this.scrollBar.getComponent();
	}
	
	public Tablature getTablature(){		
		return this.tablature;
	}
	
	public void repaint(){
		if( this.loaded && this.tablature != null){
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
			this.bufferGraphics.setBackground( ((TGColorImpl)this.tablature.getViewLayout().getResources().getBackgroundColor()).getHandle());
			this.bufferGraphics.clearRect(0, 0, this.size.width, this.size.height);
			this.tablature.paintTablature(new TGPainterImpl( this.bufferGraphics ));
		}

		// Paint a "loading" message.
		else{
			this.bufferGraphics.setBackground( Color.WHITE );
			this.bufferGraphics.clearRect(0, 0, this.size.width, this.size.height);
			this.paintLoading(new TGPainterImpl( this.bufferGraphics ));
		}
		
		// Draw the buffer image
		g.drawImage(this.bufferImage,0,0,this);
	}
	
	public void update(Graphics g){
		this.paint(g);
	}
	
	public void loadTablature(){
		this.tablature.setDocumentManager(TuxGuitar.instance().getDocumentManager());
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
	
	private void paintLoading(TGPainterImpl painter){
		painter.setFont( TGConfig.FONT_LOADING_MESSAGE );
		painter.setForeground(painter.createColor(TGConfig.COLOR_LOADING_MESSAGE));
		String msg = "Loading ...";
		float width = painter.getFMWidth(msg);
		float height = painter.getFMHeight();
		painter.drawString(msg, ((this.size.width / 2) - (width / 2)), (this.size.height / 2) - (height / 2));
	}
}
