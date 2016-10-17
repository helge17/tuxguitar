package org.herac.tuxguitar.android.browser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.android.browser.config.TGBrowserProperties;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactory;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactoryHandler;
import org.herac.tuxguitar.android.browser.model.TGBrowserSession;
import org.herac.tuxguitar.android.browser.model.TGBrowserSettings;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGBrowserManager {
	
	private TGContext context;
	private TGBrowserProperties properties;
	private TGBrowserSession session;
	private List<TGBrowserFactory> factories;
	private List<TGBrowserCollection> collections;
	
	private TGBrowserManager(TGContext context){
		this.context = context;
		this.properties = new TGBrowserProperties(context);
		this.session = new TGBrowserSession();
		this.factories = new ArrayList<TGBrowserFactory>();
		this.collections = new ArrayList<TGBrowserCollection>();
	}
	
	public TGBrowserSession getSession() {
		return session;
	}

	public TGBrowserProperties getProperties() {
		return properties;
	}

	public Iterator<TGBrowserFactory> getFactories(){
		return this.factories.iterator();
	}
	
	public TGBrowserFactory getFactory(String type){
		Iterator<TGBrowserFactory> factories = getFactories();
		while(factories.hasNext()){
			TGBrowserFactory factory = factories.next();
			if(factory.getType().equals(type)){
				return factory;
			}
		}
		return null;
	}
	
	public void addFactory(TGBrowserFactory factory){
		this.factories.add(factory);
	}
	
	public void removeFactory(TGBrowserFactory factory){
		this.factories.remove(factory);
	}
	
	public Iterator<TGBrowserCollection> getCollections(){
		return this.collections.iterator();
	}
	
	public int countCollections(){
		return this.collections.size();
	}
	
	public void removeCollection(TGBrowserCollection collection){
		this.collections.remove(collection);
	}
	
	public TGBrowserCollection getCollection(int index){
		if(index >= 0 && index < countCollections()){
			return (TGBrowserCollection)this.collections.get(index);
		}
		return null;
	}
	
	public TGBrowserCollection getCollection(String type, TGBrowserSettings settings){
		Iterator<TGBrowserCollection> it = this.getCollections();
		while( it.hasNext() ){
			TGBrowserCollection collection = it.next();
			if( collection.getType().equals(type) && collection.getSettings().getTitle().equals(settings.getTitle()) && collection.getSettings().getData().equals(settings.getData()) ){
				return collection;
			}
		}
		return null;
	}
	
	public TGBrowserCollection addCollection(TGBrowserCollection collection){
		if( collection.getSettings() != null ){
			TGBrowserCollection existent = getCollection(collection.getType(), collection.getSettings());
			if( existent != null ){
				return existent;
			}
			this.collections.add(collection);
		}
		return collection;
	}

	public TGBrowserCollection createCollection(String type, TGBrowserSettings settings){
		TGBrowserCollection collection = new TGBrowserCollection();
		collection.setType(type);
		collection.setSettings(settings);
		
		return collection;
	}
	
	public void createBrowser(TGBrowserFactoryHandler handler, TGBrowserCollection collection) throws TGBrowserException {
		TGBrowserFactory factory = this.getFactory(collection.getType());
		if( factory != null ) {
			factory.createBrowser(handler, collection.getSettings());
		}
	}
	
	public void closeSession() throws TGBrowserException {
		this.storeDefaultCollection();
		this.getSession().setBrowser(null);
		this.getSession().setCollection(null);
		this.getSession().setCurrentElement(null);
		this.getSession().setCurrentElements(null);
	}
	
	public void openSession(TGBrowserCollection collection) throws TGBrowserException {
		this.createBrowser(new TGBrowserSessionHandler(this.context, this.getSession(), collection), collection);
	}
	
	public void storeCollections() throws TGBrowserException {
		this.properties.setCollections(this.collections);
		this.properties.save();
	}
	
	public void restoreCollections() throws TGBrowserException {
		this.collections.clear();
		
		List<TGBrowserCollection> collections = this.properties.getCollections();
		for(TGBrowserCollection collection : collections) {
			addCollection(collection);
		}
	}
	
	public void storeDefaultCollection() throws TGBrowserException {
		int index = -1;
		if( this.getSession().getCollection() != null ) {
			index = this.collections.indexOf(this.getSession().getCollection());
		}
		this.properties.setDefaultCollectionIndex(index);
		this.properties.save();
	}
	
	public TGBrowserCollection getDefaultCollection() {
		int count = this.countCollections();
		if( count > 0 ) {
			int index = this.properties.getDefaultCollectionIndex();
			if( index < 0 || index >= count ) {
				index = 0;
			}
			return this.getCollection(index);
		}
		return null;
	}
	
	public static TGBrowserManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGBrowserManager.class.getName(), new TGSingletonFactory<TGBrowserManager>() {
			public TGBrowserManager createInstance(TGContext context) {
				return new TGBrowserManager(context);
			}
		});
	}
}
