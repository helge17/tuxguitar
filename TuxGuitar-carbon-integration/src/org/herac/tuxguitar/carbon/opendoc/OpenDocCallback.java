package org.herac.tuxguitar.carbon.opendoc;

public interface OpenDocCallback {
	
	public int appleEventProc(int nextHandler, int theEvent, int userData);
	
	public int openDocProc(int theAppleEvent, int reply, int handlerRefcon);
	
}
