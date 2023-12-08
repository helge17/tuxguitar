package org.herac.tuxguitar.ui.jfx;

import javafx.application.Application;
import javafx.stage.Stage;

public class JFXApplicationLauncher extends Application {
	
	@Override
	public void start(Stage stage) throws Exception {
		JFXApplicationHandle.getInstance().start(stage);
	}
	
	public static void launch() {
		Application.launch(JFXApplicationLauncher.class);
	}
}
