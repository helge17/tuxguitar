package org.herac.tuxguitar.ui.swt.resource;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Rectangle;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.swt.SWTComponent;

public class SWTPainter extends SWTComponent<GC> implements UIPainter {
	
	/** On swt-carbon (and maybe another platform) advanced mode must be allways true **/
	private static final boolean FORCE_OS_DEFAULTS = Boolean.TRUE.toString().equals(System.getProperty("swt.painter.force-os-defaults"));
	
	private boolean pathEmpty;
	
	private int style;
	
	private Path path;
	
	public SWTPainter(){
		super(null);
	}
	
	public SWTPainter(GC gc){
		super(gc);
	}
	
	public SWTPainter(Image image){
		this(new GC(image));
	}
	
	public void initPath(int style){
		this.style = style;
		this.path = new Path(this.getControl().getDevice());
		this.pathEmpty = true;
		this.setAntialias(true);
	}
	
	public void initPath(){
		this.initPath( PATH_DRAW );
	}
	
	public void closePath(){
		if(! this.pathEmpty ){
			if( (this.style & PATH_DRAW) != 0){
				this.getControl().drawPath(this.path);
			}
			if( (this.style & PATH_FILL) != 0){
				this.getControl().fillPath(this.path);
			}
		}
		this.style = 0;
		this.path.dispose();
		this.pathEmpty = true;
		this.setAntialias(false);
	}
	
	public GC getGC(){
		return this.getControl();
	}
	
	public void dispose(){
		this.getControl().dispose();
	}
	
	public boolean isDisposed(){
		return this.getControl().isDisposed();
	}
	
	public void drawString(String string, float x, float y) {
		this.setAdvanced(false);
		this.getControl().drawString(string, toInt(x), toInt(y));
	}
	
	public void drawString(String string, float x, float y, boolean isTransparent) {
		this.setAdvanced(false);
		this.getControl().drawString(string, toInt(x), toInt(y), isTransparent);
	}
	
	public void drawImage(UIImage image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight) {
		this.setAdvanced(false);
		this.getControl().drawImage(getImage(image), toInt(srcX), toInt(srcY), toInt(srcWidth), toInt(srcHeight), toInt(destX), toInt(destY), toInt(destWidth), toInt(destHeight));
	}
	
	public void drawImage(UIImage image, float x, float y) {
		this.setAdvanced(false);
		this.getControl().drawImage(getImage(image), toInt(x), toInt(y));
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
	
	public void addString(String arg0, float arg1, float arg2, UIFont font) {
		this.path.addString(arg0, arg1, arg2, getFont(font));
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
	
	public void setFont(UIFont font) {
		this.getControl().setFont(getFont(font));
	}
	
	public void setForeground(UIColor color) {
		this.getControl().setForeground(getColor(color));
	}
	
	public void setBackground(UIColor color) {
		this.getControl().setBackground(getColor(color));
	}
	
	public void setLineWidth(float width) {
		this.getControl().setLineAttributes(new LineAttributes((width == THINNEST_LINE_WIDTH ? 1f : width)));
	}
	
	public void setLineStyleSolid(){
		this.getControl().setLineStyle(SWT.LINE_SOLID);
	}
	
	public void setLineStyleDot(){
		this.getControl().setLineStyle(SWT.LINE_DOT);
	}
	
	public void setLineStyleDash(){
		this.getControl().setLineStyle(SWT.LINE_DASH);
	}
	
	public void setLineStyleDashDot(){
		this.getControl().setLineStyle(SWT.LINE_DASHDOT);
	}
	
	public void setAlpha(int alpha) {
		this.getControl().setAlpha(alpha);
	}
	
	public void setAntialias(boolean enabled){
		if(!FORCE_OS_DEFAULTS){
			this.getControl().setAntialias(enabled ? SWT.ON : SWT.OFF );
		}
	}
	
	public void setAdvanced(boolean advanced){
		if(!FORCE_OS_DEFAULTS){
			this.getControl().setAdvanced(advanced);
		}
	}
	
	public float getFontSize(){
		FontData[] fd = this.getControl().getFont().getFontData();
		if( fd != null && fd.length > 0 ){
			return fd[0].getHeight();
		}
		return 0;
	}
	
	public float getFMTopLine() {
		return -((this.getFMAscent() / 10f) * 2f);
	}
	
	public float getFMMiddleLine(){
		return -((this.getFMAscent() / 10f) * 6f);
	}
	
	public float getFMBaseLine() {
		return -this.getFMAscent();
	}
	
	public float getFMAscent(){
		this.setAdvanced(false);
		
		FontMetrics fm = this.getControl().getFontMetrics();
		return (fm.getAscent() + fm.getLeading());
	}
	
	public float getFMHeight(){
		this.setAdvanced(false);
		return this.getControl().getFontMetrics().getHeight();
	}
	
	public float getFMWidth( String text ){
		this.setAdvanced(false);
		return this.getControl().stringExtent( text ).x;
	}
	
	public Image getImage(UIImage image){
		if( image instanceof SWTImage ){
			return ((SWTImage)image).getHandle();
		}
		return null;
	}
	
	public Color getColor(UIColor color){
		if( color instanceof SWTColor ){
			return ((SWTColor)color).getHandle();
		}
		return null;
	}
	
	public Font getFont(UIFont font){
		if( font instanceof SWTFont ){
			return ((SWTFont)font).getHandle();
		}
		return null;
	}
	
	public int toInt(float value) {
		return Math.round(value);
	}
}
