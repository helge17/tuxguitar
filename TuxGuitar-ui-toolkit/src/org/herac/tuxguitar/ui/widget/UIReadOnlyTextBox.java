package org.herac.tuxguitar.ui.widget;

public interface UIReadOnlyTextBox extends UIControl {
	
	String getText();
	
	void setText(String text);
	
	void append(String text);
}
