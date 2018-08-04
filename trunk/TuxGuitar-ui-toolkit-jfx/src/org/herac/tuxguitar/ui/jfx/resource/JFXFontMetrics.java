package org.herac.tuxguitar.ui.jfx.resource;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class JFXFontMetrics {

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
		return (float) -this.text.getLayoutBounds().getMinY();
	}
	
	public float getDescent() {
		return (float) this.text.getLayoutBounds().getMaxY();
	}
	
	public float getHeight() {
		return (float) this.text.getLayoutBounds().getHeight();
	}
	
	public float getWidth( String text ) {
		this.text.setText(text);
		
		return (float) this.text.getLayoutBounds().getWidth();
	}
}
