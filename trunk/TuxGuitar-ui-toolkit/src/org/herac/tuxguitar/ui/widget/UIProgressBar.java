package org.herac.tuxguitar.ui.widget;

public interface UIProgressBar extends UIControl {
	
	void setValue(int value);
	
	int getValue();
	
	void setMaximum(int maximum);
	
	int getMaximum();
	
	void setMinimum(int minimum);
	
	int getMinimum();
}
