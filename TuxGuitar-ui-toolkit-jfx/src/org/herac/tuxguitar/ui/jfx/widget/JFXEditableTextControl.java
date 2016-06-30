package org.herac.tuxguitar.ui.jfx.widget;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.event.UIModifyListener;
import org.herac.tuxguitar.ui.jfx.event.JFXModifyListenerManager;

public class JFXEditableTextControl<T extends TextInputControl> extends JFXTextControl<T> {
	
	private Integer textLimit;
	private JFXModifyListenerManager<String> modifyListener;
	
	public JFXEditableTextControl(T control, JFXContainer<? extends Region> parent) {
		super(control, parent);
		
		this.modifyListener = new JFXModifyListenerManagerWrapper(this);
		
		this.getControl().textProperty().addListener(this.modifyListener);
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
	
	public Integer getTextLimit() {
		return this.textLimit;
	}
	
	public void setTextLimit(Integer textLimit) {
		this.textLimit = textLimit;
	}
	
	public void addModifyListener(UIModifyListener listener) {
		this.modifyListener.addListener(listener);
	}

	public void removeModifyListener(UIModifyListener listener) {
		this.modifyListener.removeListener(listener);
	}
	
	private class JFXModifyListenerManagerWrapper extends JFXModifyListenerManager<String> {
		
		private JFXEditableTextControl<?> control;
		
		public JFXModifyListenerManagerWrapper(JFXEditableTextControl<?> control) {
			super(control);
			
			this.control = control;
		}
		
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			if( this.control.getTextLimit() == null || newValue == null || newValue.length() <= this.control.getTextLimit() ) {
				super.fireEvent();
			} else {
				this.control.setText(oldValue);
			}
		}
	}
}
