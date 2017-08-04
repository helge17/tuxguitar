package org.herac.tuxguitar.thread;

public interface TGThreadHandler {
	
	void start(Runnable runnable);
	
	void loop(TGThreadLoop loop);
	
	void yield();
	
	void dispose();
	
	Object getThreadId();
}
