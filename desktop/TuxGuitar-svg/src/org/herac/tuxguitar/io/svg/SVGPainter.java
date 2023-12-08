package org.herac.tuxguitar.io.svg;

import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;

public class SVGPainter extends SVGResourceFactory implements UIPainter {
	
	public static final float SVG_THINNEST_LINE_WIDTH = 0.75f;
	
	private int svgPathStyle;
	private float svgStrokeWidth;
	private SVGFont svgFont;
	private SVGColor svgBackground;
	private SVGColor svgForeground;
	private StringBuffer svgPath;
	private StringBuffer svgBuffer;
	
	private boolean disposed;
	
	public SVGPainter( StringBuffer svgBuffer ){
		this.svgBuffer = svgBuffer;
		this.svgPath = null;
		this.svgPathStyle = 0;
		this.svgStrokeWidth = 1;
		this.disposed = false;
		this.svgFont = new SVGFont("none", 10, false, false);
		this.svgBackground = new SVGColor(0xff, 0xff, 0xff);
		this.svgForeground = new SVGColor(0x00, 0x00, 0x00);
	}
	
	public void dispose(){
		this.disposed = true;
	}
	
	public boolean isDisposed(){
		return this.disposed;
	}
	
	public void initPath(int style){
		this.svgPath = new StringBuffer();
		this.svgPathStyle = style;
		
		this.setAntialias(true);
	}
	
	public void initPath(){
		this.initPath( PATH_DRAW );
	}
	
	public void closePath(){
		if( this.svgPath != null && this.svgPath.length() > 0 ){
			this.svgBuffer.append("\r\n");
			this.svgBuffer.append("<path ");
			this.svgBuffer.append("d=\"" + this.svgPath.toString() + "Z\" ");
			this.svgBuffer.append("fill=\"" + ((this.svgPathStyle & PATH_FILL) != 0 ? this.svgBackground.toHexString() : "none") +"\" ");
			this.svgBuffer.append("stroke=\"" + ((this.svgPathStyle & PATH_DRAW) != 0 ? this.svgForeground.toHexString() : "none") +"\" ");
			this.svgBuffer.append("stroke-width=\"" + this.svgStrokeWidth + "\" ");
			this.svgBuffer.append("/>");
		}
		this.svgPath = null;
		this.svgPathStyle = 0;
		this.setAntialias(false);
	}
	
	public void drawString(String string, float x, float y) {
		this.setAdvanced(false);
		this.svgBuffer.append("\r\n");
		this.svgBuffer.append("<text ");
		this.svgBuffer.append("x='" + x + "' ");
		this.svgBuffer.append("y='" + y + "' ");
		this.svgBuffer.append("font-family=\""+ this.svgFont.getName() + "\" ");
		this.svgBuffer.append("font-size=\"" + this.svgFont.getHeight() + "\" ");
		this.svgBuffer.append("fill=\"" + this.svgForeground.toHexString() +"\" ");
		this.svgBuffer.append(">");
		this.svgBuffer.append( string );
		this.svgBuffer.append("</text>");
	}
	
	public void drawImage(UIImage image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight) {
		if( image instanceof SVGImage ){
			this.svgBuffer.append("<g transform=\"translate(" + destX + "," + destY + ")\">");
			this.svgBuffer.append( ((SVGImage)image).getBuffer() );
			this.svgBuffer.append("</g>");
		}
	}
	
	public void drawImage(UIImage image, float x, float y) {
		if( image instanceof SVGImage ){
			this.svgBuffer.append("<g transform=\"translate(" + x + "," + y + ")\">");
			this.svgBuffer.append( ((SVGImage)image).getBuffer() );
			this.svgBuffer.append("</g>");
		}
	}
	
	public void cubicTo(float cx1, float cy1, float cx2, float cy2, float x, float y) {
		this.svgPath.append("C " + cx1 + " " + cy1 + " " + cx2 + " " + cy2 + " "+ x + " " + y + " ");
	}
	
	public void lineTo(float x, float y) {
		this.svgPath.append("L " + x + " " + y + " ");
	}
	
	public void moveTo(float x, float y) {
		this.svgPath.append("M " + x + " " + y + " ");
	}
	
	public void addCircle(float x, float y, float w) {
		this.svgPath.append("M " + (x - (w / 2f)) + " " + y + " a ");
		this.svgPath.append((w / 2f) + " " + (w / 2f) + " 0 1 0 " + w + " 0 ");
		this.svgPath.append((w / 2f) + " " + (w / 2f) + " 0 1 0 -" + w + " 0 ");
	}
	
	public void addRectangle(float x,float y,float width,float height) {
		this.svgPath.append("M " + x + " " + y + " ");
		this.svgPath.append("L " + (x + width) + " " + y + " ");
		this.svgPath.append("L " + (x + width) + " " + (y + height) + " ");
		this.svgPath.append("L " + x + " " + (y + height) + " ");
		this.svgPath.append("L " + x + " " + y + " ");
	}
	
	public void setFont(UIFont font) {
		if( font instanceof SVGFont ){
			this.svgFont = (SVGFont)font;
		}
	}
	
	public void setBackground(UIColor color) {
		if( color instanceof SVGColor ){
			this.svgBackground = (SVGColor)color;
		}
	}
	
	public void setForeground(UIColor color) {
		if( color instanceof SVGColor ){
			this.svgForeground = (SVGColor)color;
		}
	}
	
	public void setLineWidth(float width) {
		this.svgStrokeWidth = (width == UIPainter.THINNEST_LINE_WIDTH ? SVG_THINNEST_LINE_WIDTH : width);
	}
	
	public void setLineStyleSolid(){
		// Not Implemented
	}
	
	public void setLineStyleDot(){
		// Not Implemented
	}
	
	public void setLineStyleDash(){
		// Not Implemented
	}
	
	public void setLineStyleDashDot(){
		// Not Implemented
	}
	
	public void setAntialias(boolean enabled){
		// Not Implemented
	}
	
	public void setAdvanced(boolean advanced){
		// Not Implemented
	}
	
	public float getFontSize() {
		return this.svgFont.getHeight();
	}
	
	public float getFMBaseLine() {
		return 0f;
	}

	public float getFMTopLine() {
		return (getFMAscent());
	}

	public float getFMMiddleLine() {
		return (getFMAscent() / 2f);
	}
	
	public float getFMAscent() {
		return ((getFontSize() / 10f) * 7.5f);
	}
	
	public float getFMDescent() {
		return 0f;
	}
	
	public float getFMHeight() {
		return (getFMAscent() + getFMDescent());
	}
	
	public float getFMWidth( String text ){
		return ( text != null ? Math.round( text.length() * (0.75f * getFontSize() ) ) : 0 );
	}
	
	public void setAlpha(int alpha) {
		// Not Implemented
	}
}
