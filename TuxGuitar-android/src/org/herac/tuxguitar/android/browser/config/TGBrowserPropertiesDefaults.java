package org.herac.tuxguitar.android.browser.config;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.browser.TGBrowserCollectionInfo;
import org.herac.tuxguitar.android.browser.filesystem.TGBrowserFactoryImpl;
import org.herac.tuxguitar.android.browser.filesystem.TGBrowserSettingsImpl;
import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesException;
import org.herac.tuxguitar.util.properties.TGPropertiesReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Environment;

public class TGBrowserPropertiesDefaults implements TGPropertiesReader {
	
	private Activity activity;
	
	public TGBrowserPropertiesDefaults(Activity activity) {
		this.activity = activity;
	}
	
	@Override
	public void readProperties(TGProperties properties, String module) throws TGPropertiesException {
		properties.setValue(TGBrowserProperties.PROPERTY_COLLECTIONS, createJsonObject(createDefaultCollection()));
	}
	
	public TGBrowserCollectionInfo createDefaultCollection() {
		String defaultTitle = this.activity.getString(R.string.browser_default_external_storage_title);
		String defaultPath = Environment.getExternalStorageDirectory().getPath();
		
		TGBrowserCollectionInfo info = new TGBrowserCollectionInfo();
		info.setType(TGBrowserFactoryImpl.BROWSER_TYPE);
		info.setSettings(new TGBrowserSettingsImpl(defaultTitle, defaultPath).toString());
		
		return info;
	}
	
	public String createJsonObject(TGBrowserCollectionInfo collection) throws TGPropertiesException {
		try {
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(TGBrowserProperties.COLLECTION_TYPE, collection.getType());
			jsonObject.put(TGBrowserProperties.COLLECTION_SETTINGS, collection.getSettings());
			jsonArray.put(jsonObject);
			
			return jsonArray.toString();
		} catch (JSONException e) {
			throw new TGPropertiesException(e);
		}
	}
}
