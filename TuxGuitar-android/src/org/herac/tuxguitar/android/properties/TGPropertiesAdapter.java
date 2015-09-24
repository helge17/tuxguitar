package org.herac.tuxguitar.android.properties;

import org.herac.tuxguitar.android.browser.config.TGBrowserProperties;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.properties.TGPropertiesManager;

import android.app.Activity;

public class TGPropertiesAdapter {
	
	public static void initialize(TGContext context, Activity activity) {
		addFactory(context);
		addDefaultReader(context, activity, TGBrowserProperties.RESOURCE, TGBrowserProperties.MODULE);
		addDefaultWriter(context, activity, TGBrowserProperties.RESOURCE, TGBrowserProperties.MODULE);
	}
	
	public static void addFactory(TGContext context) {
		TGPropertiesManager.getInstance(context).setPropertiesFactory(new TGPropertiesFactoryImpl());
	}
	
	public static void addDefaultReader(TGContext context, Activity activity, String resource, String module) {
		TGPropertiesManager.getInstance(context).addPropertiesReader(resource, new TGPropertiesHandler(activity, module, resource));
	}
	
	public static void addDefaultWriter(TGContext context, Activity activity, String resource, String module) {
		TGPropertiesManager.getInstance(context).addPropertiesWriter(resource, new TGPropertiesHandler(activity, module, resource));
	}
}
