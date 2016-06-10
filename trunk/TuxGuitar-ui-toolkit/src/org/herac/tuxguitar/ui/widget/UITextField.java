package org.herac.tuxguitar.ui.widget;

import org.herac.tuxguitar.ui.event.UIModifyListener;

public interface UITextField extends UIControl {
	
	String getText();

	void setText(String text);
	
	Integer getTextLimit();
	
	void setTextLimit(Integer limit);
	
	void addModifyListener(UIModifyListener listener);
	
	void removeModifyListener(UIModifyListener listener);
}
