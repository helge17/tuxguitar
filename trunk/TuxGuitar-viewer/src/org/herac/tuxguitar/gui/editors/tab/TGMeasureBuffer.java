package org.herac.tuxguitar.gui.editors.tab;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import org.herac.tuxguitar.gui.editors.TGPainter;

public class TGMeasureBuffer {
	
	private Image buffer;
	
	private TGPainter painter;
	
	private int width;
	
	private int height;
	
	public TGMeasureBuffer(){
		super();
	}
	
	public void makeBuffer(int width,int height,Color background){
		this.dispose();
		this.buffer = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		this.width = width;
		this.height = height;
		this.fillBuffer(background);
	}
	
	private void fillBuffer(Color background){
		getPainter().setBackground(background);
		getPainter().initPath(TGPainter.PATH_FILL);
		getPainter().addRectangle(0,0,this.width,this.height);
		getPainter().closePath();
	}
	
	public TGPainter getPainter(){
		if(this.painter == null){
			this.painter = new TGPainter(this.buffer);
		}
		return this.painter;
	}
	
	public void paintImage(TGPainter painter,int x,int y,int srcY){
		painter.drawImage(this.buffer,0,srcY, this.width, (this.height - srcY), x, (y + srcY), this.width, (this.height - srcY));
	}
	
	public Image getImage(){
		return this.buffer;
	}
	
	public void dispose(){
		if(this.painter != null){
			this.painter.dispose();
			this.painter = null;
		}
		this.buffer = null;
	}
	
	public boolean isDisposed(){
		return (this.buffer == null);
	}
}
