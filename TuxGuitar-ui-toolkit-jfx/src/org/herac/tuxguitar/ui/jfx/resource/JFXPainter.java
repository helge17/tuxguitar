package org.herac.tuxguitar.ui.jfx.resource;

import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;

public class JFXPainter extends JFXAbstractPainter<GraphicsContext> implements UIPainter {
	
	private int style;
	private boolean antialias;
	private boolean pathEmpty;
	
	public JFXPainter(GraphicsContext handle){
		super(handle);
	}
	
	public void initPath(int style){
		this.style = style;
		this.pathEmpty = true;
		this.getControl().beginPath();
		this.setAntialias(true);
	}
	
	public void initPath(){
		this.initPath(PATH_DRAW);
	}
	
	public void closePath(){
		if(!this.pathEmpty ){
			if( (this.style & PATH_DRAW) != 0){
				this.getControl().stroke();
			}
			if( (this.style & PATH_FILL) != 0){
				this.getControl().fill();
			}
		}
		this.style = 0;
		this.pathEmpty = true;
		this.getControl().closePath();
		this.setAntialias(false);
	}
	
	public void clearArea(float x, float y, float width, float height) {
		this.getControl().clearRect(lpx(x), lpx(y), lpx(width), lpx(height));
	}
	
	public void drawString(String string, float x, float y) {
		Paint fill = this.getControl().getFill();
		this.setAdvanced(false);
		this.getControl().setFill(this.getControl().getStroke());
		this.getControl().fillText(string, lpx(x), lpx(y));
		this.getControl().setFill(fill);
	}
	
	public void drawImage(UIImage image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight) {
		this.toAbstractImage(image).paint(this, srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight);
	}
	
	public void drawImage(UIImage image, float x, float y) {
		this.toAbstractImage(image).paint(this, x, y);
	}
	
	public void drawNativeImage(Image image, float x, float y) {
		this.getControl().drawImage(image, ipx(x), ipx(y));
	}
	
	public void drawNativeImage(Image image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight) {
		this.getControl().drawImage(image, ipx(srcX), ipx(srcY), ipx(srcWidth), ipx(srcHeight), ipx(destX), ipx(destY), ipx(destWidth), ipx(destHeight));
	}
	
	public void cubicTo(float xc1, float yc1, float xc2, float yc2, float x1, float y1) {
		this.getControl().bezierCurveTo(lpx(xc1), lpx(yc1), lpx(xc2), lpx(yc2), lpx(x1), lpx(y1));
		this.pathEmpty = false;
	}
	
	public void lineTo(float x, float y) {
		this.getControl().lineTo(lpx(x), lpx(y));
		this.pathEmpty = false;
	}
	
	public void moveTo(float x, float y) {
		this.getControl().moveTo(lpx(x), lpx(y));
		this.pathEmpty = false;
	}
	
	public void addCircle(float x, float y, float width) {
		this.getControl().arc(lpx(x), lpx(y), lpx(width / 2f), lpx(width / 2f), 0, 360);
		this.pathEmpty = false;
	}
	
	public void addRectangle(float x,float y,float width,float height) {
		this.getControl().rect(lpx(x), lpx(y), lpx(width), lpx(height));
		this.pathEmpty = false;
	}
	
	public void setFont(UIFont font) {
		super.setFont(font);
		this.getControl().setFont(toFont(font));
	}
	
	public void setForeground(UIColor color) {
		this.getControl().setStroke(toColor(color));
	}
	
	public void setBackground(UIColor color) {
		this.getControl().setFill(toColor(color));
	}
	
	public void setLineWidth(float width) {
		this.getControl().setLineWidth((width == UIPainter.THINNEST_LINE_WIDTH ? 1 : width));
	}
	
	public void setLineStyleSolid() {
		//TODO
	}
	
	public void setLineStyleDot(){
		//TODO
	}
	
	public void setLineStyleDash(){
		//TODO
	}
	
	public void setLineStyleDashDot(){
		//TODO
	}
	
	public void setAlpha(int alpha) {
		//TODO
	}
	
	public void setAntialias(boolean antialias){
		this.antialias = antialias;
	}
	
	public void setAdvanced(boolean advanced){
		this.setAntialias(advanced);
	}
	
	public double lpx(double value) {
		return (this.antialias ? value : Math.round(value) - ((this.getControl().getLineWidth() / 2d) % 1));
	}
	
	public double ipx(double value) {
		return (this.antialias ? value : Math.round(value));
	}
}
