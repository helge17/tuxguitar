package org.herac.tuxguitar.util.properties;

import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGPropertiesManager {
	
	private TGPropertiesFactory propertiesFactory;
	private Map<String, TGPropertiesReader> propertiesReaders;
	private Map<String, TGPropertiesWriter> propertiesWriters;
	
	private TGPropertiesManager(){
		this.propertiesFactory = null;
		this.propertiesReaders = new HashMap<String, TGPropertiesReader>();
		this.propertiesWriters = new HashMap<String, TGPropertiesWriter>();
	}
	
	public TGProperties createProperties() throws TGPropertiesException{
		if( this.propertiesFactory != null ){
			return this.propertiesFactory.createProperties();
		}
		return null;
	}
	
	public void readProperties(TGProperties properties, String resource, String module){
		if( this.propertiesReaders.containsKey(resource) ){
			TGPropertiesReader tgPropertiesReader = (TGPropertiesReader)this.propertiesReaders.get(resource);
			tgPropertiesReader.readProperties(properties, module);
		}
	}
	
	public void writeProperties(TGProperties properties, String resource, String module){
		if( this.propertiesWriters.containsKey(resource) ){
			TGPropertiesWriter tgPropertiesWriter = (TGPropertiesWriter)this.propertiesWriters.get(resource);
			tgPropertiesWriter.writeProperties(properties, module);
		}
	}
	
	public TGPropertiesFactory getPropertiesFactory() {
		return propertiesFactory;
	}

	public void setPropertiesFactory(TGPropertiesFactory propertiesFactory) {
		this.propertiesFactory = propertiesFactory;
	}

	public void addPropertiesReader(String resource, TGPropertiesReader propertiesReader){
		if(!this.propertiesReaders.containsKey(resource) ){
			this.propertiesReaders.put(resource, propertiesReader);
		}
	}
	
	public void removePropertiesReader(String resource){
		if( this.propertiesReaders.containsKey(resource) ){
			this.propertiesReaders.remove(resource);
		}
	}
	
	public void addPropertiesWriter(String resource, TGPropertiesWriter propertiesWriter){
		if(!this.propertiesWriters.containsKey(resource) ){
			this.propertiesWriters.put(resource, propertiesWriter);
		}
	}
	
	public void removePropertiesWriter(String resource){
		if( this.propertiesWriters.containsKey(resource) ){
			this.propertiesWriters.remove(resource);
		}
	}
	
	public static TGPropertiesManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGPropertiesManager.class.getName(), new TGSingletonFactory<TGPropertiesManager>() {
			public TGPropertiesManager createInstance(TGContext context) {
				return new TGPropertiesManager();
			}
		});
	}
}
