package app.tuxguitar.android.properties;

import android.app.Activity;

import app.tuxguitar.android.browser.config.TGBrowserProperties;
import app.tuxguitar.android.transport.TGTransportProperties;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.configuration.TGConfigManager;
import app.tuxguitar.util.properties.TGPropertiesManager;
import app.tuxguitar.util.properties.TGPropertiesReader;
import app.tuxguitar.util.properties.TGPropertiesWriter;

public class TGPropertiesAdapter {

	public static void initialize(TGContext context, Activity activity) {
		addFactory(context);
		addReader(context, TGBrowserProperties.RESOURCE, createSharedPreferencesReader(context, activity, TGBrowserProperties.MODULE, TGBrowserProperties.RESOURCE));
		addWriter(context, TGBrowserProperties.RESOURCE, createSharedPreferencesWriter(activity, TGBrowserProperties.MODULE, TGBrowserProperties.RESOURCE));
		addReader(context, TGTransportProperties.RESOURCE, createSharedPreferencesReader(context, activity, TGTransportProperties.MODULE, TGTransportProperties.RESOURCE));
		addWriter(context, TGTransportProperties.RESOURCE, createSharedPreferencesWriter(activity, TGTransportProperties.MODULE, TGTransportProperties.RESOURCE));
		addReader(context, TGConfigManager.RESOURCE, new TGResourcePropertiesReader(context, null, null));
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
		return new TGSharedPreferencesReader(activity, module, resource, new TGResourcePropertiesReader(context, null, ("-" + resource)));
	}

	public static TGPropertiesWriter createSharedPreferencesWriter(Activity activity, String module, String resource) {
		return new TGSharedPreferencesWriter(activity, module, resource);
	}
}
