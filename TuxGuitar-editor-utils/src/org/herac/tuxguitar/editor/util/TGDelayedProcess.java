package org.herac.tuxguitar.editor.util;

import org.herac.tuxguitar.thread.TGThreadLoop;
import org.herac.tuxguitar.thread.TGThreadManager;
import org.herac.tuxguitar.util.TGContext;

public class TGDelayedProcess implements TGProcess {
	
	private TGContext context;
	private Runnable runnable;
	private Integer timeOut;
	private Long timeIn;
	
	private boolean processing;
	
	public TGDelayedProcess(TGContext context, Integer timeOut, Runnable runnable) {
		this.context = context;
		this.runnable = runnable;
		this.timeOut = timeOut;
		this.timeIn = null;
	}
	
	public void process() {
		this.timeIn = System.currentTimeMillis();
		
		if(!this.processing ) {
			this.processing = true;
			this.processLater();
		}
	}
	
	private void processRunnable() {
		TGThreadManager.getInstance(this.context).loop(new TGThreadLoop() {
			public Long process() {
				TGDelayedProcess.this.processing = true;
				
				long remainingTime = ((TGDelayedProcess.this.timeIn + TGDelayedProcess.this.timeOut) - System.currentTimeMillis());
				if( remainingTime <= 0 ) {
					TGDelayedProcess.this.runnable.run();
					TGDelayedProcess.this.processing = false;
					
					return BREAK;
				}
				return remainingTime;
			}
		});
	}
	
	private void processLater() {
		TGThreadManager.getInstance(this.context).start(new Runnable() {
			public void run() {
				processRunnable();
			}
		});
	}
}
