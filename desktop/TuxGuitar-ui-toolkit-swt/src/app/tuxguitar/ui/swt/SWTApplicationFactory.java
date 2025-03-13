package app.tuxguitar.ui.swt;

import app.tuxguitar.ui.UIApplication;
import app.tuxguitar.ui.UIApplicationFactory;

public class SWTApplicationFactory implements UIApplicationFactory {

	public UIApplication createApplication(String name) {
		return new SWTApplication(name);
	}
}
