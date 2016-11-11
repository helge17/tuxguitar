package org.herac.tuxguitar.android.properties;

import android.app.Activity;

import org.herac.tuxguitar.android.browser.config.TGBrowserProperties;
import org.herac.tuxguitar.android.transport.TGTransportProperties;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.properties.TGPropertiesManager;
import org.herac.tuxguitar.util.properties.TGPropertiesReader;
import org.herac.tuxguitar.util.properties.TGPropertiesWriter;

public class TGPropertiesAdapter {

	public static void initialize(TGContext context, Activity activity) {
		addFactory(context);
		addReader(context, TGBrowserProperties.RESOURCE, createSharedPreferencesReader(context, activity, TGBrowserProperties.MODULE, TGBrowserProperties.RESOURCE));
		addWriter(context, TGBrowserProperties.RESOURCE, createSharedPreferencesWriter(activity, TGBrowserProperties.MODULE, TGBrowserProperties.RESOURCE));
		addReader(context, TGTransportProperties.RESOURCE, createSharedPreferencesReader(context, activity, TGTransportProperties.MODULE, TGTransportProperties.RESOURCE));
		addWriter(context, TGTransportProperties.RESOURCE, createSharedPreferencesWriter(activity, TGTransportProperties.MODULE, TGTransportProperties.RESOURCE));
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

	public static TGPropertiesReader createSharedPreferencesReader(TGContext context, Activity activity, String module, String resource) {
		return new TGSharedPreferencesReader(activity, module, resource, new TGResourcePropertiesReader(context, resource));
	}

	public static TGPropertiesWriter createSharedPreferencesWriter(Activity activity, String module, String resource) {
		return new TGSharedPreferencesWriter(activity, module, resource);
	}
}
