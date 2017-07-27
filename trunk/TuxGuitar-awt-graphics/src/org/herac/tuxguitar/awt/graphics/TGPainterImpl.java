package org.herac.tuxguitar.awt.graphics;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;

public class TGPainterImpl extends TGResourceFactoryImpl implements UIPainter{
	
	private boolean pathEmpty;
	
	private int style;
	
	private int alpha;
	
	private Graphics2D gc;
	
	private GeneralPath path;
	
	private BasicStroke stroke;
	
	private TGColorImpl background;
	
	private TGColorImpl foreground;
	
	public TGPainterImpl(){
		super();
	}
	
	public TGPainterImpl(Graphics2D gc){
		this.init(gc);
	}
	
	public TGPainterImpl(Image image){
		this.init((Graphics2D)image.getGraphics());
	}
	
	public void init(Graphics2D gc){
		if(this.gc != null){
			this.gc.dispose();
		}
		this.gc = gc;
		this.alpha = 255;
		this.background = new TGColorImpl(0xff, 0xff, 0xff);
		this.foreground = new TGColorImpl(0x00, 0x00, 0x00);
		this.stroke = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	}
	
	public void initPath(int style){
		this.style = style;
		this.path = new GeneralPath();
		this.pathEmpty = true;
		this.setAntialias( true );
	}
	
	public void initPath(){
		this.initPath( PATH_DRAW );
	}
	
	public void closePath(){
		if(this.pathEmpty){
			System.out.println("Warning: Empty Path!");
		}
		if( (this.style & PATH_DRAW) != 0){
			//this.gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			this.gc.setColor(this.foreground.getHandle(this.alpha));
			this.gc.draw(this.path);
		}
		if( (this.style & PATH_FILL) != 0){
			//this.gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			this.gc.setColor(this.background.getHandle(this.alpha));
			this.gc.fill(this.path);
		}
		this.style = 0;
		this.path = null;
		this.pathEmpty = true;
		this.setAntialias( false );
	}
	
	public Graphics2D getGC(){
		return this.gc;
	}
	
	public void dispose(){
		this.gc.dispose();
		this.gc = null;
	}
	
	public void setFont(Font arg0) {
		this.gc.setFont(arg0);
	}
	
	public void setFont(UIFont font) {
		this.gc.setFont( ((TGFontImpl)font).getHandle() );
	}
	
	public void setBackground(UIColor color) {
		this.background = (TGColorImpl) color;
	}
	
	public void setForeground(UIColor color) {
		this.foreground = (TGColorImpl) color;
	}
	
	public void setLineWidth(float lineWidth) {
		this.stroke = new BasicStroke(lineWidth, this.stroke.getEndCap(), this.stroke.getLineJoin(), this.stroke.getMiterLimit(), this.stroke.getDashArray(), this.stroke.getDashPhase());
		this.gc.setStroke(this.stroke);
	}
	
	public void setLineStyleSolid() {
		// TODO Auto-generated method stub
	}

	public void setLineStyleDot() {
		// TODO Auto-generated method stub
	}

	public void setLineStyleDash() {
		// TODO Auto-generated method stub
	}

	public void setLineStyleDashDot() {
		// TODO Auto-generated method stub
	}
	
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
	
	public void setAntialias(boolean enabled){
		this.gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, (enabled ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF));
	}
	
	public void setAdvanced(boolean advanced) {
		this.gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, (advanced ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF));
		this.gc.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, (advanced ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF));
	}
	
	public void drawString(String string, float x, float y) {
		this.setAntialias(true);
		this.gc.setColor(this.foreground.getHandle());
		this.gc.drawString(string, x, (y + this.gc.getFont().getSize()));
	}
	
	public void drawImage(UIImage image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight) {
		this.setAntialias(false);
		this.gc.drawImage(((TGImageImpl)image).getHandle(), toInt(destX), toInt(destY), toInt(destX + destWidth), toInt(destY + destHeight), toInt(srcX), toInt(srcY), toInt(srcX + srcWidth), toInt(srcY + srcHeight), null);		
	}

	public void drawImage(UIImage image, float x, float y) {
		float width = image.getWidth();
		float height = image.getHeight();
		this.setAntialias(false);
		this.gc.drawImage(((TGImageImpl)image).getHandle(), toInt(x), toInt(y), toInt(x + width), toInt(y + height), 0, 0, toInt(width), toInt(height), null);		
	}
	
	public void cubicTo(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
		this.path.curveTo(arg0, arg1, arg2, arg3, arg4, arg5);
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
	
	public void addCircle(float x, float y, float width) {
		this.path.append(new Arc2D.Float((x - (width / 2f)), (y - (width / 2f)), width, width, 0,360, Arc2D.OPEN), true);
		this.pathEmpty = false;
	}
	
	public void addRectangle(float x,float y,float width,float height) {
		this.path.append(new Rectangle2D.Float(x, y, width, height), true);
		this.pathEmpty = false;
	}
	
	public float getFontSize() {
		return this.gc.getFont().getSize();
	}
	
	public float getFMTopLine() {
		return -(((this.getFMAscent() + 1f) / 10f) * 3f);
	}
	
	public float getFMMiddleLine(){
		return -(((this.getFMAscent() + 1f) / 10f) * 6.5f);
	}
	
	public float getFMBaseLine() {
		return -(this.getFMAscent() + 1f);
	}
	
	public float getFMHeight() {
		return this.gc.getFontMetrics().getHeight();
	}
	
	public float getFMAscent() {
		return this.gc.getFontMetrics().getAscent();
	}
	
	public float getFMWidth(String text) {
		return this.gc.getFontMetrics().stringWidth(text);
	}

	public boolean isDisposed() {
		return (this.gc == null);
	}
	
	public int toInt(float value) {
		return Math.round(value);
	}
}
