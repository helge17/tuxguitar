package app.tuxguitar.resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;

import app.tuxguitar.util.TGContext;

public class TGResourceBundle {

	private Locale locale;
	private Properties properties;

	public TGResourceBundle(Locale locale, Properties properties){
		this.locale = locale;
		this.properties = properties;
	}

	public Locale getLocale() {
		return this.locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getString(String key) {
		return this.properties.getProperty(key);
	}

	public static TGResourceBundle getBundle(TGContext context, String baseName, Locale locale){
		Properties properties = new Properties();

		String bundleName = baseName.replace('.','/');
		String bundleExtension = ".properties";

		// load default
		TGResourceBundle.loadResources(context, (bundleName + bundleExtension ), properties);

		// load language, country and variant: each part that is set overrides the ones before
		// (Locale returns "" for a part that is not set, never null)
		for(String part : new String[] {locale.getLanguage(), locale.getCountry(), locale.getVariant()}){
			bundleName += ("_" + part);
			if( part.length() > 0 ){
				TGResourceBundle.loadResources(context, (bundleName + bundleExtension ), properties);
			}
		}

		return new TGResourceBundle(locale, properties);
	}

	private static void loadResources(TGContext context, String name, Properties p){
		try {
			Enumeration<URL> enumeration = TGResourceManager.getInstance(context).getResources(name);
			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();
				Properties properties = new Properties();
				try (InputStreamReader reader = new InputStreamReader(url.openStream(),StandardCharsets.UTF_8)) {
					properties.load( reader );
				}
				p.putAll(properties);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
