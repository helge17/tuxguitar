package org.herac.tuxguitar.ui.jfx.resource;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class JFXFontMetrics {
	
	private static final String TEXT = "1234567890";
	private Text text;
	
	public JFXFontMetrics(Font font) {
		this.text = new Text();
		this.text.setFont(font);
	}
	
	public float getTopLine() {
		return (((this.getAscent() + 1f) / 10f) * 8f);
	}
	
	public float getMiddleLine(){
		return ((this.getTopLine() - this.getBaseLine()) / 2f);
	}
	
	public float getBaseLine() {
		return 0;
	}
	
	public float getAscent() {
		return (float) -this.getLayoutBounds(TEXT).getMinY();
	}
	
	public float getDescent() {
		return (float) this.getLayoutBounds(TEXT).getMaxY();
	}
	
	public float getHeight() {
		return (float) this.getLayoutBounds(TEXT).getHeight();
	}
	
	public float getWidth(String text) {
		return (float) this.getLayoutBounds(text).getWidth();
	}
	
	public Bounds getLayoutBounds(String text) {
		this.text.setText(text);
		
		return this.text.getLayoutBounds();
	}
}
