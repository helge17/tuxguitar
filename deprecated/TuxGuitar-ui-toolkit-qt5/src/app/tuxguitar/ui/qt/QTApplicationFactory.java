package app.tuxguitar.ui.qt;

import app.tuxguitar.ui.UIApplication;
import app.tuxguitar.ui.UIApplicationFactory;

public class QTApplicationFactory implements UIApplicationFactory {

	public UIApplication createApplication(String name) {
		return new QTApplication(name);
	}
}
