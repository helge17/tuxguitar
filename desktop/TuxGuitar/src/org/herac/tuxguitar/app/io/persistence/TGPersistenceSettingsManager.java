package org.herac.tuxguitar.app.io.persistence;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.action.TGActionManager;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGPersistenceSettingsManager {
	
	private TGContext context;
	private List<TGPersistenceSettingsHandler> handlers;
	
	private TGPersistenceSettingsManager(TGContext context){
		this.context = context;
		this.handlers = new ArrayList<TGPersistenceSettingsHandler>();
		this.appendListeners();
	}
	
	public TGContext getContext() {
		return context;
	}
	
	public void addSettingsHandler(TGPersistenceSettingsHandler handler) {
		this.handlers.add(handler);
	}
	
	public void removeSettingsHandler(TGPersistenceSettingsHandler handler) {
		if( this.handlers.contains(handler) ) {
			this.handlers.remove(handler);
		}
	}
	
	public TGPersistenceSettingsHandler findSettingsHandler(TGFileFormat fileFormat, TGPersistenceSettingsMode mode) {
		for(TGPersistenceSettingsHandler handler : this.handlers) {
			if( handler.getFileFormat().equals(fileFormat) && handler.getMode().supports(mode)) {
				return handler;
			}
		}
		return null;
	}
	
	public void appendListeners() {
		TGActionManager.getInstance(this.getContext()).addInterceptor(new TGPersistenceSettingsInterceptor(this.context));
	}
	
	public static TGPersistenceSettingsManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGPersistenceSettingsManager.class.getName(), new TGSingletonFactory<TGPersistenceSettingsManager>() {
			public TGPersistenceSettingsManager createInstance(TGContext context) {
				return new TGPersistenceSettingsManager(context);
			}
		});
	}
}
