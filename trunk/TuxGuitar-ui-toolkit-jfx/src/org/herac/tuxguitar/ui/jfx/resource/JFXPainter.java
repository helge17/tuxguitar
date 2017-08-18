package org.herac.tuxguitar.ui.jfx.resource;

import org.herac.tuxguitar.ui.jfx.JFXComponent;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class JFXPainter extends JFXComponent<GraphicsContext> implements UIPainter {
	
	private int style;
	private boolean antialias;
	private boolean pathEmpty;
	private int alpha;
	private Font font;
	private FontMetrics fontMetrics;
	private JFXColor background;
	private JFXColor foreground;
	
	public JFXPainter(GraphicsContext handle){
		super(handle);
		
		this.alpha = 255;
		this.background = new JFXColor(0xff, 0xff, 0xff);
		this.foreground = new JFXColor(0x00, 0x00, 0x00);
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
			if((this.style & PATH_DRAW) != 0){
				this.getControl().setStroke(toColor(this.foreground));
				this.getControl().stroke();
			}
			if((this.style & PATH_FILL) != 0){
				this.getControl().setFill(toColor(this.background));
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
		this.setAdvanced(false);
		this.getControl().setStroke(toColor(this.foreground));
		this.getControl().setFill(toColor(this.foreground));
		this.getControl().fillText(string, lpx(x), lpx(y));
	}
	
	public void drawImage(UIImage image, float x, float y) {
		this.getControl().drawImage(toImage(image), ipx(x), ipx(y));
	}
	
	public void drawImage(UIImage image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight) {
		this.getControl().drawImage(toImage(image), ipx(srcX), ipx(srcY), ipx(srcWidth), ipx(srcHeight), ipx(destX), ipx(destY), ipx(destWidth), ipx(destHeight));
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
		this.getControl().moveTo(lpx(x + (width / 2f)), lpx(y));
		this.getControl().arc(lpx(x), lpx(y), lpx(width / 2f), lpx(width / 2f), 0, 360);
		this.pathEmpty = false;
	}
	
	public void addRectangle(float x,float y,float width,float height) {
		this.getControl().moveTo(lpx(x), lpx(y));
		this.getControl().lineTo(lpx(x + width), lpx(y));
		this.getControl().lineTo(lpx(x + width), lpx(y + height));
		this.getControl().lineTo(lpx(x), lpx(y + height));
		this.getControl().lineTo(lpx(x), lpx(y));
		this.pathEmpty = false;
	}
	
	public void setFont(UIFont font) {
		this.font = this.toFont(font);
		if( this.font != null ) {
			this.fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(this.font);
		}
		this.getControl().setFont(this.font);
	}
	
	public void setForeground(UIColor color) {
		this.foreground = (JFXColor) color;
	}
	
	public void setBackground(UIColor color) {
		this.background = (JFXColor) color;
	}
	
	public void setLineWidth(float width) {
		this.getControl().setLineWidth((width == UIPainter.THINNEST_LINE_WIDTH ? 1 : width));
	}
	
	public void setLineStyleSolid() {
		this.getControl().setLineDashes();
	}
	
	public void setLineStyleDot(){
		this.getControl().setLineDashes(1d, 5d);
	}
	
	public void setLineStyleDash(){
		this.getControl().setLineDashes(5d, 5d);
	}
	
	public void setLineStyleDashDot(){
		this.getControl().setLineDashes(5d, 5d, 1d, 5d);
	}
	
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
	
	public void setAntialias(boolean antialias){
		this.antialias = antialias;
	}
	
	public void setAdvanced(boolean advanced){
		this.setAntialias(advanced);
	}
	
	public float getFontSize(){
		return (this.font != null ? (float)this.font.getSize() : 0);
	}
	
	public float getFMTopLine() {
		return (((this.getFMAscent() + 1f) / 10f) * 8f);
	}
	
	public float getFMMiddleLine(){
		return ((this.getFMTopLine() - this.getFMBaseLine()) / 2f);
	}
	
	public float getFMBaseLine() {
		return (this.fontMetrics != null ? this.fontMetrics.getBaseline() : 0);
	}
	
	public float getFMAscent() {
		return (this.fontMetrics != null ? this.fontMetrics.getAscent() : 0);
	}
	
	public float getFMDescent() {
		return (this.fontMetrics != null ? this.fontMetrics.getDescent() : 0);
	}
	
	public float getFMHeight() {
		return (this.fontMetrics != null ? this.fontMetrics.getLineHeight() : 0);
	}
	
	public float getFMWidth( String text ) {
		return (this.fontMetrics != null ? this.fontMetrics.computeStringWidth(text) : 0);
	}
	
	public Image toImage(UIImage image){
		if( image instanceof JFXImage ){
			return ((JFXImage) image).getHandle();
		}
		return null;
	}
	
	public Font toFont(UIFont font){
		if( font instanceof JFXFont ){
			return ((JFXFont)font).getHandle();
		}
		return null;
	}
	
	public Color toColor(UIColor color){
		if( color instanceof JFXColor ){
			return ((JFXColor)color).getHandle(this.alpha / 255d);
		}
		return null;
	}
	
	public double lpx(double value) {
		if( this.antialias ) {
			return value;
		}
		
		double rnd = Math.round(value);
		if((this.style & PATH_DRAW) != 0) {
			rnd += ((this.getControl().getLineWidth() / 2d) % 1);
		}
		return rnd;
	}
	
	public double ipx(double value) {
		return (this.antialias ? value : Math.round(value));
	}
}
