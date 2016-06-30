package org.herac.tuxguitar.ui.jfx.resource;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;

public class JFXAWTImagePainter extends JFXAbstractPainter<JFXAWTImage> implements UIPainter {
	
	private boolean pathEmpty;
	
	private int style;
	
	private Graphics2D graphics;
	
	private GeneralPath path;
	
	private BasicStroke stroke;
	
	private Color background;
	
	private Color foreground;
	
	public JFXAWTImagePainter(JFXAWTImage control){
		super(control);
		
		this.graphics = (Graphics2D) control.getControl().getGraphics();
		this.background = Color.WHITE;
		this.foreground = Color.BLACK;
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
			this.getGraphics().setColor(this.foreground);
			this.getGraphics().draw(this.path);
		}
		if( (this.style & PATH_FILL) != 0){
			this.getGraphics().setColor(this.background);
			this.getGraphics().fill(this.path);
		}
		this.style = 0;
		this.path = null;
		this.pathEmpty = true;
		this.setAntialias( false );
	}
	
	public Graphics2D getGraphics(){
		return this.graphics;
	}
	
	public void dispose() {
		this.getGraphics().dispose();
		this.getControl().refreshHandle();
		
		super.dispose();
	}
	
	public void setFont(UIFont font) {
		this.getGraphics().setFont(this.toAWTFont(font));
	}
	
	public void setBackground(UIColor color) {
		this.background = this.toAWTColor(color);
	}
	
	public void setForeground(UIColor color) {
		this.foreground = this.toAWTColor(color);
	}
	
	public void setLineWidth(float lineWidth) {
		this.stroke = new BasicStroke(lineWidth, this.stroke.getEndCap(), this.stroke.getLineJoin(), this.stroke.getMiterLimit(), this.stroke.getDashArray(), this.stroke.getDashPhase());
		this.getGraphics().setStroke(this.stroke);
	}
	
	public void setAntialias(boolean enabled){
		this.getGraphics().setRenderingHint(RenderingHints.KEY_ANTIALIASING, (enabled ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF));
	}
	
	public void setAdvanced(boolean advanced) {
		this.getGraphics().setRenderingHint(RenderingHints.KEY_ANTIALIASING, (advanced ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF));
		this.getGraphics().setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, (advanced ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF));
	}
	
	public void drawString(String string, float x, float y) {
		this.setAntialias(true);
		this.getGraphics().setColor(this.foreground);
		this.getGraphics().drawString(string, x, (y + this.getGraphics().getFont().getSize()) );
	}
	
	public void drawString(String string, float x, float y, boolean isTransparent) {
		this.setAntialias(true);
		this.getGraphics().setColor(this.foreground);
		this.getGraphics().drawString(string, x, (y + this.getGraphics().getFont().getSize()));
	}
	
	public void drawImage(BufferedImage image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight) {
		this.setAntialias(false);
		this.getGraphics().drawImage(image, toInt(destX), toInt(destY), toInt(destX + destWidth), toInt(destY + destHeight), toInt(srcX), toInt(srcY), toInt(srcX + srcWidth), toInt(srcY + srcHeight), null);
	}
	
	public void drawImage(BufferedImage image, float x, float y) {
		float width = image.getWidth();
		float height = image.getHeight();
		this.setAntialias(false);
		this.getGraphics().drawImage(image, toInt(x), toInt(y), toInt(x + width), toInt(y + height), 0, 0, toInt(width), toInt(height), null);
	}
	
	public void drawImage(UIImage image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight) {
		BufferedImage bufferedImage = this.toBufferedImage(image);
		if( bufferedImage != null ) {
			this.drawImage(bufferedImage, srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight);
		} else {
			this.toAbstractImage(image).paint(this, srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight);
		}
	}
	
	public void drawImage(UIImage image, float x, float y) {
		BufferedImage bufferedImage = this.toBufferedImage(image);
		if( bufferedImage != null ) {
			this.drawImage(bufferedImage, x, y);
		} else {
			this.toAbstractImage(image).paint(this, x, y);
		}
	}
	
	public void drawNativeImage(Image image, float x, float y) {
		this.drawImage(SwingFXUtils.fromFXImage(image, null), x, y);
	}

	public void drawNativeImage(Image image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight) {
		this.drawImage(SwingFXUtils.fromFXImage(image, null), srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight);
	}
	
	public void clearArea(float x, float y, float width, float height) {
		this.getGraphics().clearRect(toInt(x), toInt(y), toInt(width), toInt(height));
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
	
	public void addArc(float x, float y, float width, float height, float startAngle, float arcAngle) {
		this.path.append(new Arc2D.Float(x, y, width, height, startAngle,arcAngle, Arc2D.OPEN), true);
		this.pathEmpty = false;
	}
	
	public void addOval(float x, float y, float width, float height) {
		this.path.append(new Arc2D.Float(x, y, width, height, 0,360, Arc2D.OPEN), true);
		this.pathEmpty = false;
	}
	
	public void addRectangle(float x,float y,float width,float height) {
		this.path.append(new Rectangle2D.Float(x, y, width, height), true);
		this.pathEmpty = false;
	}
	
	public void addString(String arg0, float arg1, float arg2, UIFont arg3) {
		// TODO Auto-generated method stub
	}
	
	public float getFontSize() {
		return this.getGraphics().getFont().getSize();
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
		return this.getGraphics().getFontMetrics().getHeight();
	}
	
	public float getFMAscent() {
		return this.getGraphics().getFontMetrics().getAscent();
	}
	
	public float getFMDescent() {
		return this.getGraphics().getFontMetrics().getDescent();
	}

	public float getFMWidth(String text) {
		return this.getGraphics().getFontMetrics().stringWidth(text);
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
		// TODO Auto-generated method stub
	}
	
	public int toInt(float value) {
		return Math.round(value);
	}
	
	public Font toAWTFont(UIFont font) {
		return (font != null ? new Font(font.getName(), ((font.isBold() ? Font.BOLD : 0) | (font.isItalic() ? Font.ITALIC : 0)) , Math.round(font.getHeight())) : null);
	}
	
	public Color toAWTColor(UIColor color) {
		return (color != null ? new Color(color.getRed(), color.getGreen(), color.getBlue()) : null);
	}
	
	public BufferedImage toBufferedImage(UIImage image){
		if( image instanceof JFXAWTImage ){
			return ((JFXAWTImage) image).getControl();
		}
		return null;
	}
}
