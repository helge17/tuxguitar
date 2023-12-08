package org.herac.tuxguitar.awt.graphics;

import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIPainter;

public abstract class AWTAbstractPainter extends AWTResourceFactory implements UIPainter {

	private AWTFont font;
	private boolean disposed;
	
	public AWTAbstractPainter() {
		super();
	}
	
	public void dispose() {
		this.disposed = true;
	}

	public boolean isDisposed() {
		return this.disposed;
	}
	
	public void setFont(UIFont font) {
		this.font = ((AWTFont) font);
	}

	public AWTFont getFont() {
		return this.font;
	}
	
	public float getFontSize() {
		return (this.font != null ? this.font.getHeight() : 0f);
	}
	
	public float getFMTopLine() {
		return (this.font != null ? this.font.getFontMetrics().getTopLine() : 0f);
	}
	
	public float getFMMiddleLine(){
		return (this.font != null ? this.font.getFontMetrics().getMiddleLine() : 0f);
	}
	
	public float getFMBaseLine() {
		return (this.font != null ? this.font.getFontMetrics().getBaseLine() : 0f);
	}
	
	public float getFMHeight() {
		return (this.font != null ? this.font.getFontMetrics().getHeight() : 0f);
	}
	
	public float getFMWidth(String text) {
		return (this.font != null ? this.font.getFontMetrics().getWidth(text) : 0f);
	}
}
