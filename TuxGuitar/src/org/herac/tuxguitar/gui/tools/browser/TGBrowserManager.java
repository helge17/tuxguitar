package org.herac.tuxguitar.gui.tools.browser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserData;
import org.herac.tuxguitar.gui.tools.browser.base.TGBrowserFactory;
import org.herac.tuxguitar.gui.tools.browser.filesystem.TGBrowserFactoryImpl;
import org.herac.tuxguitar.gui.tools.browser.xml.TGBrowserReader;
import org.herac.tuxguitar.gui.tools.browser.xml.TGBrowserWriter;
import org.herac.tuxguitar.gui.util.TGFileUtils;

public class TGBrowserManager {
	
	private static TGBrowserManager instance;
	
	private List factories;
	private List collections;
	private List collectionInfos;
	private boolean changes;
	
	private TGBrowserFactoryHandler handler;
	
	private TGBrowserManager(){
		this.factories = new ArrayList();
		this.collections = new ArrayList();
		this.collectionInfos = new ArrayList();
		this.readCollections();
		this.addDefaultFactory();
	}
	
	public static TGBrowserManager instance(){
		if(instance == null){
			instance = new TGBrowserManager();
		}
		return instance;
	}
	
	public void setFactoryHandler(TGBrowserFactoryHandler handler){
		this.handler = handler;
	}
	
	public Iterator getFactories(){
		return this.factories.iterator();
	}
	
	public TGBrowserFactory getFactory(String type){
		Iterator factories = getFactories();
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
		
		Iterator it = this.collectionInfos.iterator();
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
	
	public Iterator getCollections(){
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
		Iterator it = this.getCollections();
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
		this.addFactory(new TGBrowserFactoryImpl());
	}
}
