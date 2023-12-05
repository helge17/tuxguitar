package org.herac.tuxguitar.ui.jfx.widget;

import javafx.event.ActionEvent;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.jfx.event.JFXSelectionListenerManager;
import org.herac.tuxguitar.ui.jfx.resource.JFXColor;
import org.herac.tuxguitar.ui.resource.UIColor;

public class JFXColorPicker extends JFXControl<ColorPicker> {
	
	private JFXSelectionListenerManager<ActionEvent> selectionListener;
	
	public JFXColorPicker(JFXContainer<? extends Region> parent) {
		super(new ColorPicker(), parent);
		
		this.selectionListener = new JFXSelectionListenerManager<ActionEvent>(this);
	}
	
	public UIColor getValue() {
		Color value = this.getControl().getValue();
		return (value != null ? new JFXColor(value) : null);
	}
	
	public void setValue(UIColor value) {
		this.getControl().setValue(value != null ? ((JFXColor) value).getHandle() : null);
	}
	
	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().setOnAction(this.selectionListener);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().setOnAction(null);
		}
	}
}
