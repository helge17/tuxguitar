package org.herac.tuxguitar.ui.widget;

import org.herac.tuxguitar.ui.event.UISelectionListener;

public interface UISpinner extends UIControl {

	void setValue(int value);
	
	int getValue();
	
	void setMaximum(int maximum);
	
	int getMaximum();
	
	void setMinimum(int minimum);
	
	int getMinimum();
	
	void setIncrement(int increment);
	
	int getIncrement();
	
	void addSelectionListener(UISelectionListener listener);
	
	void removeSelectionListener(UISelectionListener listener);
}
