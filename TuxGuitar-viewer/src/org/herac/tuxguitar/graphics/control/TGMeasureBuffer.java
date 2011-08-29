package org.herac.tuxguitar.graphics.control;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGPainter;

public class TGMeasureBuffer {
	
	private TGImage buffer;
	
	private TGPainter painter;
	
	private int width;
	
	private int height;
	
	public TGMeasureBuffer(){
		super();
	}
	
	public void createBuffer(TGPainter painter, int width,int height,TGColor background){
		this.dispose();
		this.width = width;
		this.height = height;
		this.buffer = painter.createImage(this.width, this.height);
		this.fillBuffer(background);
	}
	
	public void disposeBuffer(){
		if(this.buffer != null && !this.buffer.isDisposed()){
			this.buffer.dispose();
		}
	}
	
	private void fillBuffer(TGColor background){
		getPainter().setBackground(background);
		getPainter().initPath(TGPainter.PATH_FILL);
		getPainter().addRectangle(0,0,this.width,this.height);
		getPainter().closePath();
	}
	
	public void paintBuffer(TGPainter painter,int x,int y,int srcY){
		painter.drawImage(this.buffer,0,srcY, this.width, (this.height - srcY), x, (y + srcY), this.width, (this.height - srcY));
	}
	
	public void createPainter(){
		this.disposePainter();
		//this.painter = new TGPainterImpl(this.buffer);
		this.painter = this.buffer.createPainter();
	}
	
	public void disposePainter(){
		if(this.painter != null && !this.painter.isDisposed()){
			this.painter.dispose();
			this.painter = null;
		}
	}
	
	public TGPainter getPainter(){
		if(this.painter == null || this.painter.isDisposed()){
			this.createPainter();
		}
		return this.painter;
	}
	
	public TGImage getImage(){
		return this.buffer;
	}
	
	public void dispose(){
		this.disposePainter();
		this.disposeBuffer();
	}
	
	public boolean isDisposed(){
		return (this.buffer == null || this.buffer.isDisposed());
	}
}
