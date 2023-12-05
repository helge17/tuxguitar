package org.herac.tuxguitar.ui;

public interface UIComponent {
	
	<T> T getData(String key);
	
	<T> void setData(String key, T data);
	
	void dispose();
	
	boolean isDisposed();
}
