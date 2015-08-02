package org.herac.tuxguitar.app.graphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGDimension;
import org.herac.tuxguitar.graphics.TGFont;
import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.graphics.TGPoint;

public class TGPainterImpl extends TGResourceFactoryImpl implements TGPainter {
	
	private boolean pathEmpty;
	
	private int style;
	
	private GC gc;
	
	private Path path;
	
	public TGPainterImpl(){
		super();
	}
	
	public TGPainterImpl(GC gc){
		this.init(gc);
	}
	
	public TGPainterImpl(Image image){
		this.init(image);
	}
	
	public void init(Image image){
		this.init(new GC(image));
	}
	
	public void init(GC gc){
		this.setDevice(gc.getDevice());
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
	
	public boolean isDisposed(){
		return this.gc.isDisposed();
	}
	
	public void drawString(String string, float x, float y) {
		this.setAdvanced(false);
		this.gc.drawString(string, toInt(x), toInt(y));
	}
	
	public void drawString(String string, float x, float y, boolean isTransparent) {
		this.setAdvanced(false);
		this.gc.drawString(string, toInt(x), toInt(y), isTransparent);
	}
	
	public void drawImage(TGImage image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight) {
		this.setAdvanced(false);
		this.gc.drawImage(getImage(image), toInt(srcX), toInt(srcY), toInt(srcWidth), toInt(srcHeight), toInt(destX), toInt(destY), toInt(destWidth), toInt(destHeight));
	}
	
	public void drawImage(TGImage image, float x, float y) {
		this.setAdvanced(false);
		this.gc.drawImage(getImage(image), toInt(x), toInt(y));
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
	
	public void addString(String arg0, float arg1, float arg2, TGFont font) {
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
	
	public void setFont(TGFont font) {
		this.gc.setFont(getFont(font));
	}
	
	public void setForeground(TGColor color) {
		this.gc.setForeground(getColor(color));
	}
	
	public void setBackground(TGColor color) {
		this.gc.setBackground(getColor(color));
	}
	
	public void setLineWidth(float width) {
		this.gc.setLineAttributes(new LineAttributes((width == THINNEST_LINE_WIDTH ? 1f : width)));
	}
	
	public void setLineStyleSolid(){
		this.gc.setLineStyle(SWT.LINE_SOLID);
	}
	
	public void setLineStyleDot(){
		this.gc.setLineStyle(SWT.LINE_DOT);
	}
	
	public void setLineStyleDash(){
		this.gc.setLineStyle(SWT.LINE_DASH);
	}
	
	public void setLineStyleDashDot(){
		this.gc.setLineStyle(SWT.LINE_DASHDOT);
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
	
	public float getFontSize(){
		FontData[] fd = this.gc.getFont().getFontData();
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
		
		FontMetrics fm = this.gc.getFontMetrics();
		return (fm.getAscent() + fm.getLeading());
	}
	
	public float getFMDescent(){
		this.setAdvanced(false);
		return this.gc.getFontMetrics().getDescent();
	}
	
	public float getFMHeight(){
		this.setAdvanced(false);
		return this.gc.getFontMetrics().getHeight();
	}
	
	public float getFMWidth( String text ){
		this.setAdvanced(false);
		return this.gc.stringExtent( text ).x;
	}
	
	public TGPoint createPoint( Point point ){
		return new TGPoint( point.x , point.y );
	}
	
	public TGDimension createDimension( Point point ){
		return new TGDimension( point.x , point.y );
	}
	
	public Image getImage(TGImage image){
		if( image instanceof TGImageImpl ){
			return ((TGImageImpl)image).getHandle();
		}
		return null;
	}
	
	public Color getColor(TGColor color){
		if( color instanceof TGColorImpl ){
			return ((TGColorImpl)color).getHandle();
		}
		return null;
	}
	
	public Font getFont(TGFont font){
		if( font instanceof TGFontImpl ){
			return ((TGFontImpl)font).getHandle();
		}
		return null;
	}
	
	public int toInt(float value) {
		return Math.round(value);
	}
}
