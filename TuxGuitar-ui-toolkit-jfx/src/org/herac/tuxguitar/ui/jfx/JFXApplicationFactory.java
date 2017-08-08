package org.herac.tuxguitar.ui.jfx;

import org.herac.tuxguitar.ui.UIApplication;
import org.herac.tuxguitar.ui.UIApplicationFactory;

public class JFXApplicationFactory implements UIApplicationFactory {
	
	public UIApplication createApplication(String name) {
		return new JFXApplication();
	}
}
