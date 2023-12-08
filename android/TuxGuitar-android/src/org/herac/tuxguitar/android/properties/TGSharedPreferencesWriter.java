package org.herac.tuxguitar.android.properties;

import java.util.Iterator;
import java.util.Map;

import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesException;
import org.herac.tuxguitar.util.properties.TGPropertiesWriter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class TGSharedPreferencesWriter extends TGSharedPreferencesHandler implements TGPropertiesWriter {
	
	public TGSharedPreferencesWriter(Activity activity, String module, String resource) {
		super(activity, module, resource);
	}
	
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
