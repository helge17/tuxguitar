package org.herac.tuxguitar.ui.jfx.property;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.text.Font;

import org.herac.tuxguitar.ui.jfx.resource.JFXFont;
import org.herac.tuxguitar.ui.resource.UIFont;

public class JFXFontProperty {
	
	private Font defaultValue;
	private ObjectProperty<Font> target;
	private ObjectProperty<Font> bind;
	
	public JFXFontProperty(ObjectProperty<Font> target) {
		this.defaultValue = target.get();
		this.target = target;
		this.bind = new SimpleObjectProperty<Font>(this.defaultValue);
	}
	
	public void bind() {
		if(!this.target.isBound()) {
			this.target.bind(this.bind);
		}
	}
	
	public void unbind() {
		this.target.unbind();
		this.target.set(this.defaultValue);
	}
	
	public void set(UIFont font) {
		if( font == null ) {
			this.unbind();
		} else {
			this.bind();
			this.bind.set(((JFXFont) font).getHandle());
		}
	}
	
	public UIFont get() {
		Font font = this.target.get();
		if( font != null ) {
			return new JFXFont(font);
		}
		return null;
	}
}
