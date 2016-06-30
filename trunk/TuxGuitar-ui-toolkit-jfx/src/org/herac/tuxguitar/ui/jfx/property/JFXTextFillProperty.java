package org.herac.tuxguitar.ui.jfx.property;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import org.herac.tuxguitar.ui.jfx.resource.JFXColor;
import org.herac.tuxguitar.ui.resource.UIColor;

public class JFXTextFillProperty {
	
	private Paint defaultValue;
	private ObjectProperty<Paint> target;
	private ObjectProperty<Paint> bind;
	
	public JFXTextFillProperty(ObjectProperty<Paint> target) {
		this.defaultValue = target.get();
		this.target = target;
		this.bind = new SimpleObjectProperty<Paint>(this.defaultValue);
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
	
	public void set(UIColor color) {
		if( color == null ) {
			this.unbind();
		} else {
			this.bind();
			this.bind.set(((JFXColor) color).getHandle());
		}
	}
	
	public UIColor get() {
		Paint textFill = this.target.get();
		if( textFill instanceof Color ) {
			return new JFXColor((Color) textFill);
		}
		return null;
	}
}
