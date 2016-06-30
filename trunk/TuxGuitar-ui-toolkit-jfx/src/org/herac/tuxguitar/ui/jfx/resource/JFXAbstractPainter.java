package org.herac.tuxguitar.ui.jfx.resource;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import org.herac.tuxguitar.ui.jfx.JFXComponent;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

public abstract class JFXAbstractPainter<T> extends JFXComponent<T> implements UIPainter {
	
	private Font font;
	private FontMetrics fontMetrics;
	
	public JFXAbstractPainter(T component) {
		super(component);
	}
	
	public void setFont(UIFont font) {
		this.font = this.toFont(font);
		if( this.font != null ) {
			this.fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(this.font);
		}
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
	
	public JFXAbstractImage<?> toAbstractImage(UIImage image){
		if( image instanceof JFXAbstractImage ){
			return ((JFXAbstractImage<?>) image);
		}
		return null;
	}
	
	public Color toColor(UIColor color){
		if( color instanceof JFXColor ){
			return ((JFXColor)color).getHandle();
		}
		return null;
	}
	
	public Font toFont(UIFont font){
		if( font instanceof JFXFont ){
			return ((JFXFont)font).getHandle();
		}
		return null;
	}
	
	public abstract void clearArea(float x, float y, float width, float height);
	
	public abstract void drawNativeImage(Image image, float x, float y);
	
	public abstract void drawNativeImage(Image image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight);
}
