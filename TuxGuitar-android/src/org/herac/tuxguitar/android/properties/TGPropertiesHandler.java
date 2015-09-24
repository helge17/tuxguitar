package org.herac.tuxguitar.android.properties;

import java.util.Iterator;
import java.util.Map;

import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesException;
import org.herac.tuxguitar.util.properties.TGPropertiesReader;
import org.herac.tuxguitar.util.properties.TGPropertiesWriter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class TGPropertiesHandler implements TGPropertiesReader, TGPropertiesWriter {
	
	private Activity activity;
	private String module;
	private String resource;
	
	public TGPropertiesHandler(Activity activity, String module, String resource) {
		this.activity = activity;
		this.module = module;
		this.resource = resource;
	}
	
	public SharedPreferences getSharedPreferences() {
		return this.activity.getSharedPreferences(this.module + "-" + this.resource, 0);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void readProperties(TGProperties properties, String module) throws TGPropertiesException {
		Map<String, String> map = ((TGPropertiesImpl) properties).getMap();
		map.clear();
		
		SharedPreferences sharedPreferences = getSharedPreferences();
		Iterator<?> it = sharedPreferences.getAll().entrySet().iterator();
		while( it.hasNext() ) {
			Map.Entry<String, ?> entry = (Map.Entry<String, ?>) it.next();
			map.put(entry.getKey(), (entry.getValue() != null ? entry.getValue().toString() : null));
		}
	}
	
	@Override
	public void writeProperties(TGProperties properties, String module) throws TGPropertiesException {
		Map<String, String> map = ((TGPropertiesImpl) properties).getMap();
		
		SharedPreferences sharedPreferences = getSharedPreferences();
		Editor editor = sharedPreferences.edit();
		
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		while( it.hasNext() ) {
			Map.Entry<String, String> entry = it.next();
			
			editor.putString(entry.getKey(), entry.getValue());
		}
		
		editor.commit();
	}
}
