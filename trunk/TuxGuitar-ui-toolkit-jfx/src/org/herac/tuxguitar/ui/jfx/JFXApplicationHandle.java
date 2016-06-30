package org.herac.tuxguitar.ui.jfx;

import javafx.application.Application;
import javafx.stage.Stage;

public class JFXApplicationHandle extends Application {
	
	private static JFXApplicationHandle instance;
	
	private boolean started;
	private Stage stage;
	private Runnable runnable;
	
	public void start(Runnable runnable) {
		if(!this.isStarted()) {
			this.runnable = runnable;
			
			JFXApplicationLauncher.launch();
		}
	}
	
	public void start(Stage stage) {
		this.stage = stage;
		this.runnable.run();
		this.started = true;
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public Runnable getRunnable() {
		return runnable;
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public static JFXApplicationHandle getInstance() {
		synchronized (JFXApplicationHandle.class) {
			if( instance == null ) {
				instance = new JFXApplicationHandle();
			}
		}
		return instance;
	}
}
