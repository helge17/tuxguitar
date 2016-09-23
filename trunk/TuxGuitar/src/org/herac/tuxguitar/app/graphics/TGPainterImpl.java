package org.herac.tuxguitar.app.graphics;

import org.herac.tuxguitar.graphics.TGColor;
import org.herac.tuxguitar.graphics.TGFont;
import org.herac.tuxguitar.graphics.TGImage;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.resource.UIResourceFactory;

public class TGPainterImpl extends TGResourceFactoryImpl implements TGPainter {
	
	private UIPainter handle;
	
	public TGPainterImpl(UIResourceFactory factory){
		super(factory);
	}
	
	public TGPainterImpl(UIResourceFactory factory, UIPainter handle){
		this(factory);
		this.setHandle(handle);
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

	public void drawString(String string, float x, float y, boolean isTransparent) {
		this.handle.drawString(string, x, y, isTransparent);
	}

	public void drawImage(TGImage image, float srcX, float srcY, float srcWidth, float srcHeight, float destX, float destY, float destWidth, float destHeight) {
		this.handle.drawImage(((TGImageImpl) image).getHandle(), srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight);
	}

	public void drawImage(TGImage image, float x, float y) {
		this.handle.drawImage(((TGImageImpl) image).getHandle(), x, y);
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

	public void addString(String arg0, float arg1, float arg2, TGFont font) {
		this.handle.addString(arg0, arg1, arg2, ((TGFontImpl) font).getHandle());
	}

	public void addArc(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
		this.handle.addArc(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	public void addOval(float arg0, float arg1, float arg2, float arg3) {
		this.handle.addOval(arg0, arg1, arg2, arg3);
	}

	public void addRectangle(float x, float y, float width, float height) {
		this.handle.addRectangle(x, y, width, height);
	}

	public void setFont(TGFont font) {
		this.handle.setFont(((TGFontImpl) font).getHandle());
	}

	public void setForeground(TGColor color) {
		this.handle.setForeground(((TGColorImpl) color).getHandle());
	}

	public void setBackground(TGColor color) {
		this.handle.setBackground(((TGColorImpl) color).getHandle());
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

	public void setAdvanced(boolean advanced) {
		this.handle.setAdvanced(advanced);
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
