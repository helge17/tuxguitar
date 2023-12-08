package org.herac.tuxguitar.android.browser.config;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.android.browser.TGBrowserCollection;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.android.browser.model.TGBrowserSettings;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesManager;
import org.herac.tuxguitar.util.properties.TGPropertiesUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TGBrowserProperties {
	
	public static final String MODULE = "tuxguitar";
	public static final String RESOURCE = "browser";
	
	public static final String PROPERTY_COLLECTIONS = "browser-collections";
	public static final String PROPERTY_DEFAULT_COLLECTION = "default-collection-index";
	
	public static final String COLLECTION_TYPE = "type";
	public static final String COLLECTION_TITLE = "title";
	public static final String COLLECTION_SETTINGS = "settings";
	
	private TGContext context;
	private TGProperties properties;
	
	public TGBrowserProperties(TGContext context){
		this.context = context;
		this.properties = TGPropertiesManager.getInstance(this.context).createProperties();
		this.load();
	}
	
	public void load(){
		TGPropertiesManager.getInstance(this.context).readProperties(this.properties, RESOURCE, MODULE);
	}
	
	public void save(){
		TGPropertiesManager.getInstance(this.context).writeProperties(this.properties, RESOURCE, MODULE);
	}
	
	public int getDefaultCollectionIndex() {
		return TGPropertiesUtil.getIntegerValue(this.properties, PROPERTY_DEFAULT_COLLECTION);
	}
	
	public void setDefaultCollectionIndex(int index) {
		TGPropertiesUtil.setValue(this.properties, PROPERTY_DEFAULT_COLLECTION, index);
	}
	
	public List<TGBrowserCollection> getCollections() throws TGBrowserException{
		try {
			List<TGBrowserCollection> collections =  new ArrayList<TGBrowserCollection>();
			
			String jsonCollections = this.properties.getValue(PROPERTY_COLLECTIONS);
			if( jsonCollections != null && jsonCollections.length() > 0 ) {
				JSONArray jsonArray = new JSONArray(jsonCollections);
				for(int i = 0; i< jsonArray.length() ; i++){
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					
					TGBrowserCollection collection = new TGBrowserCollection();
					collection.setType(jsonObject.getString(COLLECTION_TYPE));
					collection.setSettings(new TGBrowserSettings());
					collection.getSettings().setTitle(jsonObject.getString(COLLECTION_TITLE));
					collection.getSettings().setData(jsonObject.getString(COLLECTION_SETTINGS));
					collections.add(collection);
				}
			}
			
			return collections;
		} catch (JSONException e) {
			throw new TGBrowserException(e);
		}
	}
	
	public void setCollections(List<TGBrowserCollection> collections) throws TGBrowserException{
		try {
			JSONArray jsonArray = new JSONArray();
			for(TGBrowserCollection collection : collections) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(COLLECTION_TYPE, collection.getType());
				jsonObject.put(COLLECTION_TITLE, collection.getSettings().getTitle());
				jsonObject.put(COLLECTION_SETTINGS, collection.getSettings().getData());
				jsonArray.put(jsonObject);
			}
			
			this.properties.setValue(PROPERTY_COLLECTIONS, jsonArray.toString());
		} catch (JSONException e) {
			throw new TGBrowserException(e);
		}
	}
}