package org.herac.tuxguitar.android.properties;

import java.util.Iterator;
import java.util.Map;

import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesException;
import org.herac.tuxguitar.util.properties.TGPropertiesReader;

import android.app.Activity;
import android.content.SharedPreferences;

public class TGSharedPreferencesReader extends TGSharedPreferencesHandler implements TGPropertiesReader {
	
	private TGPropertiesReader defaultReader;
	
	public TGSharedPreferencesReader(Activity activity, String module, String resource, TGPropertiesReader defaultReader) {
		super(activity, module, resource);
		
		this.defaultReader = defaultReader;
	}
	
	public void readProperties(TGProperties properties, String module) throws TGPropertiesException {
		this.readDefaultProperties(properties, module);
		this.readStoredProperties(properties, module);
	}

	private void readDefaultProperties(TGProperties properties, String module) {
		if( this.defaultReader != null ) {
			this.defaultReader.readProperties(properties, module);
		}
	}

	@SuppressWarnings("unchecked")
	public void readStoredProperties(TGProperties properties, String module) throws TGPropertiesException {
		Map<String, String> map = ((TGPropertiesImpl) properties).getMap();

		SharedPreferences sharedPreferences = getSharedPreferences();
		Iterator<?> it = sharedPreferences.getAll().entrySet().iterator();
		while( it.hasNext() ) {
			Map.Entry<String, ?> entry = (Map.Entry<String, ?>) it.next();
			map.put(entry.getKey(), (entry.getValue() != null ? entry.getValue().toString() : null));
		}
	}
}
