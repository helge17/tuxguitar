package org.herac.tuxguitar.thread;

public interface TGThreadHandler {
	
	void start(TGThreadPriority priority, Runnable runnable);
	
	void loop(TGThreadLoop loop);
	
	void yield();
	
	void dispose();
	
	Object getThreadId();
}
