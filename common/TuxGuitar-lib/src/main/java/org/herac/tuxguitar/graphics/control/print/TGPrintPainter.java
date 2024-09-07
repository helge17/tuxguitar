package org.herac.tuxguitar.graphics.control.print;

import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;

public class TGPrintPainter implements UIPainter {
	
	private UIPainter handle;
	
	public TGPrintPainter() {
		super();
	}
	
	public void setHandle(UIPainter handle){
		this.handle = handle;
	}
	
	public void dispose() {
		this.handle.dispose();
	}

	public boolean isDisposed() {
		return this.handle.isDisposed();
	}

	public void initPath(int style) {
		this.handle.initPath(style);
	}

	public void initPath() {
		this.handle.initPath();
	}

	public void closePath() {
		this.handle.closePath();
	}

	public void drawString(String string, float x, float y) {
		this.handle.drawString(string, x, y);
	}

	public void drawImage(UIImage image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight) {
		this.handle.drawImage(image, srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight);
	}

	public void drawImage(UIImage image, float x, float y) {
		this.handle.drawImage(image, x, y);
	}

	public void cubicTo(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
		this.handle.cubicTo(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	public void lineTo(float arg0, float arg1) {
		this.handle.lineTo(arg0, arg1);
	}

	public void moveTo(float arg0, float arg1) {
		this.handle.moveTo(arg0, arg1);
	}

	public void addCircle(float x, float y, float width) {
		this.handle.addCircle(x, y, width);
	}

	public void addRectangle(float x, float y, float width, float height) {
		this.handle.addRectangle(x, y, width, height);
	}

	public void setFont(UIFont font) {
		this.handle.setFont(font);
	}

	public void setForeground(UIColor color) {
		this.handle.setForeground(color);
	}

	public void setBackground(UIColor color) {
		this.handle.setBackground(color);
	}

	public void setLineWidth(float lineWidth) {
		this.handle.setLineWidth(lineWidth);
	}

	public void setLineStyleSolid() {
		this.handle.setLineStyleSolid();
	}

	public void setLineStyleDot() {
		this.handle.setLineStyleDot();
	}

	public void setLineStyleDash() {
		this.handle.setLineStyleDash();
	}

	public void setLineStyleDashDot() {
		this.handle.setLineStyleDashDot();
	}

	public void setAlpha(int alpha) {
		this.handle.setAlpha(alpha);
	}

	public void setAntialias(boolean enabled) {
		this.handle.setAntialias(enabled);
	}

	public float getFontSize() {
		return this.handle.getFontSize();
	}

	public float getFMBaseLine() {
		return this.handle.getFMBaseLine();
	}

	public float getFMTopLine() {
		return this.handle.getFMTopLine();
	}

	public float getFMMiddleLine() {
		return this.handle.getFMMiddleLine();
	}

	public float getFMHeight() {
		return this.handle.getFMHeight();
	}
	
	public float getFMWidth(String text) {
		return this.handle.getFMWidth(text);
	}
}
