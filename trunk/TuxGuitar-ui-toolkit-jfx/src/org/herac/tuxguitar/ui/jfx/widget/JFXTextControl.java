package org.herac.tuxguitar.ui.jfx.widget;

import org.herac.tuxguitar.ui.jfx.property.JFXFontProperty;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UISize;

import javafx.scene.control.TextInputControl;
import javafx.scene.layout.Region;

public class JFXTextControl<T extends TextInputControl> extends JFXControl<T> {
	
	private JFXFontProperty fontProperty;
	
	public JFXTextControl(T control, JFXContainer<? extends Region> parent) {
		super(control, parent);
		
		this.fontProperty = new JFXFontProperty(this.getControl().fontProperty());
		this.computePackedSize(null, null);
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
	
	public void append(String text) {
		this.getControl().appendText(text);
	}
	
	public void computePackedSize(Float fixedWidth, Float fixedHeight) {
		UISize packedSize = this.getPackedSize();
		if( packedSize.getWidth() == 0f && packedSize.getHeight() == 0f ) {
			super.computePackedSize(fixedWidth, fixedHeight);
		} else {
			if( fixedWidth != null && fixedWidth != packedSize.getWidth() ) {
				packedSize.setWidth(fixedWidth);
			}
			if( fixedHeight != null && fixedHeight != packedSize.getHeight() ) {
				packedSize.setHeight(fixedHeight);
			}
			this.setPackedSize(packedSize);
		}
	}
}
