package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.control.Labeled;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.jfx.property.JFXFontProperty;
import org.herac.tuxguitar.ui.jfx.property.JFXTextFillProperty;
import org.herac.tuxguitar.ui.resource.UIColor;
import org.herac.tuxguitar.ui.resource.UIFont;

public class JFXLabeled<T extends Labeled> extends JFXControl<T> {
	
	private JFXFontProperty fontProperty;
	private JFXTextFillProperty textFillProperty;
	
	public JFXLabeled(T control, JFXContainer<? extends Region> parent) {
		super(control, parent);
		
		this.fontProperty = new JFXFontProperty(this.getControl().fontProperty());
		this.textFillProperty = new JFXTextFillProperty(this.getControl().textFillProperty());
	}
	
	public UIColor getFgColor() {
		if( super.getFgColor() == null ) {
			super.setFgColor(this.textFillProperty.get());
		}
		return super.getFgColor();
	}
	
	public void setFgColor(UIColor color) {
		super.setFgColor(color);
		
		this.textFillProperty.set(color);
	}
	
	public UIFont getFont() {
		if( super.getFont() == null ) {
			super.setFont(this.fontProperty.get());
		}
		return super.getFont();
	}
	
	public void setFont(UIFont font) {
		super.setFont(font);
		
		this.fontProperty.set(font);
	}
	
	public String getText() {
		return this.getControl().getText();
	}

	public void setText(String text) {
		this.getControl().setText(text);
	}
}
