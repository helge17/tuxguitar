package app.tuxguitar.ui.jfx;

import app.tuxguitar.ui.UIApplication;
import app.tuxguitar.ui.UIApplicationFactory;

public class JFXApplicationFactory implements UIApplicationFactory {

	public UIApplication createApplication(String name) {
		return new JFXApplication();
	}
}
