package org.herac.tuxguitar.app.tools.browser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.tools.browser.base.TGBrowserData;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserFactory;
import org.herac.tuxguitar.app.tools.browser.filesystem.TGBrowserFactoryImpl;
import org.herac.tuxguitar.app.tools.browser.xml.TGBrowserReader;
import org.herac.tuxguitar.app.tools.browser.xml.TGBrowserWriter;
import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGBrowserManager {
	
	private TGContext context;
	private List<TGBrowserFactory> factories;
	private List<TGBrowserCollection> collections;
	private List<TGBrowserCollectionInfo> collectionInfos;
	private boolean changes;
	
	private TGBrowserFactoryHandler handler;
	
	private TGBrowserManager(TGContext context){
		this.context = context;
		this.factories = new ArrayList<TGBrowserFactory>();
		this.collections = new ArrayList<TGBrowserCollection>();
		this.collectionInfos = new ArrayList<TGBrowserCollectionInfo>();
		this.readCollections();
		this.addDefaultFactory();
	}
	
	public void setFactoryHandler(TGBrowserFactoryHandler handler){
		this.handler = handler;
	}
	
	public Iterator<TGBrowserFactory> getFactories(){
		return this.factories.iterator();
	}
	
	public TGBrowserFactory getFactory(String type){
		Iterator<TGBrowserFactory> factories = getFactories();
		while(factories.hasNext()){
			TGBrowserFactory factory = (TGBrowserFactory)factories.next();
			if(factory.getType().equals(type)){
				return factory;
			}
		}
		return null;
	}
	
	public void addFactory(TGBrowserFactory factory){
		this.factories.add(factory);
		
		Iterator<TGBrowserCollectionInfo> it = this.collectionInfos.iterator();
		while(it.hasNext()){
			TGBrowserCollectionInfo info = (TGBrowserCollectionInfo)it.next();
			if(info.getType().equals(factory.getType())){
				TGBrowserCollection collection = new TGBrowserCollection();
				collection.setType(factory.getType());
				collection.setData(factory.parseData(info.getData()));
				addCollection(collection);
			}
		}
		
		if(this.handler != null){
			this.handler.notifyAdded();
		}
	}
	
	public void removeFactory(TGBrowserFactory factory){
		this.factories.remove(factory);
		
		int index = 0;
		while(index < this.collections.size()){
			TGBrowserCollection collection = (TGBrowserCollection)this.collections.get(index);
			if(collection.getType().equals(factory.getType())){
				removeCollection(collection);
				continue;
			}
			index ++;
		}
		if(this.handler != null){
			this.handler.notifyRemoved();
		}
	}
	
	public void addInfo(TGBrowserCollectionInfo info){
		this.collectionInfos.add(info);
	}
	
	public Iterator<TGBrowserCollection> getCollections(){
		return this.collections.iterator();
	}
	
	public int countCollections(){
		return this.collections.size();
	}
	
	public void removeCollection(TGBrowserCollection collection){
		this.collections.remove(collection);
		this.changes = true;
	}
	
	public TGBrowserCollection addCollection(TGBrowserCollection collection){
		if(collection.getData() != null ){
			TGBrowserCollection existent = getCollection(collection.getType(), collection.getData());
			if( existent != null ){
				return existent;
			}
			this.collections.add(collection);
			this.changes = true;
		}
		return collection;
	}
	
	public TGBrowserCollection getCollection(String type, TGBrowserData data ){
		Iterator<TGBrowserCollection> it = this.getCollections();
		while( it.hasNext() ){
			TGBrowserCollection collection = ( TGBrowserCollection ) it.next();
			if( collection.getType().equals(type) && collection.getData().equals(data) ){
				return collection;
			}
		}
		return null;
	}
	
	public TGBrowserCollection getCollection(int index){
		if(index >= 0 && index < countCollections()){
			return (TGBrowserCollection)this.collections.get(index);
		}
		return null;
	}
	
	public void readCollections(){
		new TGBrowserReader().loadCollections(this,new File(getCollectionsFileName()));
		this.changes = false;
	}
	
	public void writeCollections(){
		if(this.changes){
			new TGBrowserWriter().saveCollections(this,getCollectionsFileName());
		}
		this.changes = false;
	}
	
	private String getCollectionsFileName(){
		return TGFileUtils.PATH_USER_CONFIG + File.separator + "browser-collections.xml";
	}
	
	private void addDefaultFactory(){
		this.addFactory(new TGBrowserFactoryImpl(this.context));
	}
	
	public TGContext getContext() {
		return context;
	}

	public static TGBrowserManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGBrowserManager.class.getName(), new TGSingletonFactory<TGBrowserManager>() {
			public TGBrowserManager createInstance(TGContext context) {
				return new TGBrowserManager(context);
			}
		});
	}
}
