package app.tuxguitar.app.io.stream;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGSongStreamAdapterManager {

	private TGContext context;
	private List<TGSongStreamSettingsHandler> handlers;

	private TGSongStreamAdapterManager(TGContext context){
		this.context = context;
		this.handlers = new ArrayList<TGSongStreamSettingsHandler>();
		this.appendListeners();
	}

	public TGContext getContext() {
		return context;
	}

	public void addSettingsHandler(TGSongStreamSettingsHandler handler) {
		this.handlers.add(handler);
	}

	public void removeSettingsHandler(TGSongStreamSettingsHandler handler) {
		if( this.handlers.contains(handler) ) {
			this.handlers.remove(handler);
		}
	}

	public TGSongStreamSettingsHandler findSettingsHandler(String providerId) {
		for(TGSongStreamSettingsHandler handler : this.handlers) {
			if( handler.getProviderId().equals(providerId) ) {
				return handler;
			}
		}
		return null;
	}

	public void appendListeners() {
		TGActionManager.getInstance(this.getContext()).addInterceptor(new TGSongStreamActionInterceptor(this.context));
	}

	public static TGSongStreamAdapterManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGSongStreamAdapterManager.class.getName(), new TGSingletonFactory<TGSongStreamAdapterManager>() {
			public TGSongStreamAdapterManager createInstance(TGContext context) {
				return new TGSongStreamAdapterManager(context);
			}
		});
	}
}
