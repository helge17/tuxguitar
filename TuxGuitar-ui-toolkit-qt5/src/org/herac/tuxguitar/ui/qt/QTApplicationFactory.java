package org.herac.tuxguitar.ui.qt;

import org.herac.tuxguitar.ui.UIApplication;
import org.herac.tuxguitar.ui.UIApplicationFactory;

public class QTApplicationFactory implements UIApplicationFactory {
	
	public UIApplication createApplication(String name) {
		return new QTApplication(name);
	}
}
