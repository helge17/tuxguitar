package org.herac.tuxguitar.ui.swt;

import org.herac.tuxguitar.ui.UIApplication;
import org.herac.tuxguitar.ui.UIApplicationFactory;

public class SWTApplicationFactory implements UIApplicationFactory {
	
	public UIApplication createApplication(String name) {
		return new SWTApplication(name);
	}
}
