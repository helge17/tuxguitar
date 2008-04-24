package org.herac.tuxguitar.gui.editors.tab;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.herac.tuxguitar.gui.editors.TGPainter;

public class TGMeasureBuffer {
	
	private Device device;
	
	private Image buffer;
	
	private TGPainter painter;
	
	private int width;
	
	private int height;
	
	public TGMeasureBuffer(Device device){
		this.device = device;
	}
	
	public void makeBuffer(int width,int height,Color background){
		this.dispose();
		this.buffer = new Image(this.device,width,height);
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
		if(this.painter == null || this.painter.getGC().isDisposed()){
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
		if(this.painter != null && !this.painter.getGC().isDisposed()){
			this.painter.dispose();
		}
		if(this.buffer != null && !this.buffer.isDisposed()){
			this.buffer.dispose();
		}
	}
	
	public boolean isDisposed(){
		return (this.buffer == null || this.buffer.isDisposed());
	}
}
