package org.herac.tuxguitar.ui.jfx.util;

import javafx.application.Platform;

public class JFXSyncProcess {
	
	private Runnable runnable;
	private boolean pending;
	
	public JFXSyncProcess(Runnable runnable) {
		this.runnable = runnable;
		this.pending = false;
	}
	
	public void process() {
		if(!this.pending) {
			this.pending = true;
			this.processLater();
		}
	}
	
	private void processRunnable() {
		this.pending = false;
		this.runnable.run();
	}
	
	private void processLater() {
		Platform.runLater(new Runnable() {
			public void run() {
				processRunnable();
			}
		});
	}
}
