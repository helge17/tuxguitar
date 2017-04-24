package org.herac.tuxguitar.thread;

public interface TGThreadHandler {
	
	Object getThreadId();
	
	void yield();
	
	void start(Runnable runnable);
	
	void loop(TGThreadLoop loop);
}
