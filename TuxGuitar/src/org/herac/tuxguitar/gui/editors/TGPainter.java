package org.herac.tuxguitar.gui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class TGPainter {
	
	public static final int PATH_DRAW = 0x01;
	
	public static final int PATH_FILL = 0x02;
	
	private boolean pathEmpty;
	
	private int style;
	
	private GC gc;
	
	private Path path;
	
	public TGPainter(){
		super();
	}
	
	public TGPainter(GC gc){
		this.init(gc);
	}
	
	public TGPainter(Image image){
		this.init(image);
	}
	
	public void init(Image image){
		this.init(new GC(image));
	}
	
	public void init(GC gc){
		if(this.gc != null && !this.gc.isDisposed()){
			this.gc.dispose();
		}
		this.gc = gc;
	}
	
	public void initPath(int style){
		this.style = style;
		this.path = new Path(this.gc.getDevice());
		this.pathEmpty = true;
		this.setAntialias(true);
	}
	
	public void initPath(){
		this.initPath( PATH_DRAW );
	}
	
	public void closePath(){
		if(! this.pathEmpty ){
			if( (this.style & PATH_DRAW) != 0){
				this.gc.drawPath(this.path);
			}
			if( (this.style & PATH_FILL) != 0){
				this.gc.fillPath(this.path);
			}
		}
		this.style = 0;
		this.path.dispose();
		this.pathEmpty = true;
		this.setAntialias(false);
	}
	
	public GC getGC(){
		return this.gc;
	}
	
	public void dispose(){
		this.gc.dispose();
	}
	
	public void copyArea(Image image, int x, int y) {
		this.gc.copyArea(image, x, y);
	}
	
	public Point getStringExtent(String string) {
		this.setAdvanced(false);
		return this.gc.stringExtent(string);
	}
	
	public void drawString(String string, int x, int y) {
		this.setAdvanced(false);
		this.gc.drawString(string, x, y);
	}
	
	public void drawString(String string, int x, int y, boolean isTransparent) {
		this.setAdvanced(false);
		this.gc.drawString(string, x, y, isTransparent);
	}
	
	public void drawImage(Image image, int srcX, int srcY, int srcWidth, int srcHeight, int destX, int destY, int destWidth, int destHeight) {
		this.setAdvanced(false);
		this.gc.drawImage(image, srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight);
	}
	
	public void drawImage(Image image, int x, int y) {
		this.setAdvanced(false);
		this.gc.drawImage(image, x, y);
	}
	
	public void drawPolygon(int[] arg0) {
		this.gc.drawPolygon(arg0);
	}
	
	public void fillPolygon(int[] arg0) {
		this.gc.fillPolygon(arg0);
	}
	
	public void cubicTo(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
		this.path.cubicTo(arg0, arg1, arg2, arg3, arg4, arg5);
		this.pathEmpty = false;
	}
	
	public void lineTo(float arg0, float arg1) {
		this.path.lineTo(arg0, arg1);
		this.pathEmpty = false;
	}
	
	public void moveTo(float arg0, float arg1) {
		this.path.moveTo(arg0, arg1);
		this.pathEmpty = false;
	}
	
	public void addString(String arg0, float arg1, float arg2, Font arg3) {
		this.path.addString(arg0, arg1, arg2, arg3);
		this.pathEmpty = false;
	}
	
	public void addArc(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
		this.path.addArc(arg0, arg1, arg2, arg3, arg4, arg5);
		this.pathEmpty = false;
	}
	
	public void addOval(float arg0, float arg1, float arg2, float arg3) {
		this.path.addArc(arg0, arg1, arg2, arg3, 0, 360);
		this.pathEmpty = false;
	}
	
	public void addRectangle(float x,float y,float width,float height) {
		this.path.addRectangle(x, y, width, height);
		this.pathEmpty = false;
	}
	
	public void addRectangle(Rectangle rectangle) {
		this.path.addRectangle(rectangle.x,rectangle.y,rectangle.width,rectangle.height);
		this.pathEmpty = false;
	}
	
	public void setBackground(Color arg0) {
		this.gc.setBackground(arg0);
	}
	
	public void setFont(Font arg0) {
		this.gc.setFont(arg0);
	}
	
	public void setForeground(Color arg0) {
		this.gc.setForeground(arg0);
	}
	
	public void setLineStyle(int arg0) {
		this.gc.setLineStyle(arg0);
	}
	
	public void setLineWidth(int arg0) {
		this.gc.setLineWidth(arg0);
	}
	
	public void setAlpha(int alpha) {
		this.gc.setAlpha(alpha);
	}
	
	public void setAntialias(boolean enabled){
		if( !TGPainterUtils.FORCE_OS_DEFAULTS ){
			this.gc.setAntialias(enabled ? SWT.ON : SWT.OFF );
		}
	}
	
	public void setAdvanced(boolean advanced){
		if( !TGPainterUtils.FORCE_OS_DEFAULTS ){
			this.gc.setAdvanced(advanced);
		}
	}
}
