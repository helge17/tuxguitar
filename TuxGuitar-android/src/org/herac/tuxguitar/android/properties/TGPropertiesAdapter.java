package org.herac.tuxguitar.android.properties;

import org.herac.tuxguitar.android.browser.config.TGBrowserProperties;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.properties.TGPropertiesManager;
import org.herac.tuxguitar.util.properties.TGPropertiesReader;
import org.herac.tuxguitar.util.properties.TGPropertiesWriter;

import android.app.Activity;

public class TGPropertiesAdapter {
	
	public static void initialize(TGContext context, Activity activity) {
		addFactory(context);
		addReader(context, TGBrowserProperties.RESOURCE, createBrowserPropertiesReader(context, activity));
		addWriter(context, TGBrowserProperties.RESOURCE, createBrowserPropertiesWriter(context, activity));
	}
	
	public static void addFactory(TGContext context) {
		TGPropertiesManager.getInstance(context).setPropertiesFactory(new TGPropertiesFactoryImpl());
	}
	
	public static void addReader(TGContext context, String resource, TGPropertiesReader reader) {
		TGPropertiesManager.getInstance(context).addPropertiesReader(resource, reader);
	}
	
	public static void addWriter(TGContext context, String resource, TGPropertiesWriter writer) {
		TGPropertiesManager.getInstance(context).addPropertiesWriter(resource, writer);
	}
	
	public static TGPropertiesReader createBrowserPropertiesReader(TGContext context, Activity activity) {
		return new TGSharedPreferencesReader(activity, TGBrowserProperties.MODULE, TGBrowserProperties.RESOURCE, new TGResourcePropertiesReader(context, TGBrowserProperties.RESOURCE));
	}
	
	public static TGPropertiesWriter createBrowserPropertiesWriter(TGContext context, Activity activity) {
		return new TGSharedPreferencesWriter(activity, TGBrowserProperties.MODULE, TGBrowserProperties.RESOURCE);
	}
}
