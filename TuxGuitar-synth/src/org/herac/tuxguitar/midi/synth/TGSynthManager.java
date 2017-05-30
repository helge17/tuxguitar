package org.herac.tuxguitar.midi.synth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGSynthManager {
	
	private Map<Class<?>, Object> extensions;
	
	private TGSynthManager(){
		this.extensions = new HashMap<Class<?>, Object>();
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getExtensions(Class<T> cls){
		if( this.extensions.containsKey(cls) ) {
			return (List<T>) this.extensions.get(cls);
		}
		this.extensions.put(cls, new ArrayList<T>());
		
		return this.getExtensions(cls);
	}
	
	public <T> void addExtension(Class<T> cls, T extension){
		this.getExtensions(cls).add(extension);
	}
	
	public <T> void removeExtension(Class<T> cls, T extension){
		List<T> extensions = this.getExtensions(cls);
		if( extensions.contains(extension) ) {
			extensions.remove(extension);
		}
	}
	
	public TGMidiProcessorFactory findMidiProcessorFactory(String type) {
		List<TGMidiProcessorFactory> factories = this.getExtensions(TGMidiProcessorFactory.class);
		for(TGMidiProcessorFactory factory : factories) {
			if( factory.getType().equals(type) ){
				return factory;
			}
		}
		return null;
	}
	
	public TGAudioProcessorFactory findAudioProcessorFactory(String type) {
		List<TGAudioProcessorFactory> factories = this.getExtensions(TGAudioProcessorFactory.class);
		for(TGAudioProcessorFactory factory : factories) {
			if( factory.getType().equals(type) ){
				return factory;
			}
		}
		return null;
	}
	
	public List<String> getAllSupportedMidiTypes(){
		return this.getAllSupportedTypes(this.getExtensions(TGMidiProcessorFactory.class));
	}
	
	public List<String> getAllSupportedAudioTypes() {
		return this.getAllSupportedTypes(this.getExtensions(TGAudioProcessorFactory.class));
	}
	
	private List<String> getAllSupportedTypes(List<? extends TGAbstractProcessorFactory<?>> factories){
		List<String> allSupportedTypes = new ArrayList<String>();
		for(TGAbstractProcessorFactory<?> factory : factories) {
			allSupportedTypes.add(factory.getType());
		}
		return allSupportedTypes;
	}
	
	public static TGSynthManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGSynthManager.class.getName(), new TGSingletonFactory<TGSynthManager>() {
			public TGSynthManager createInstance(TGContext context) {
				return new TGSynthManager();
			}
		});
	}
}
