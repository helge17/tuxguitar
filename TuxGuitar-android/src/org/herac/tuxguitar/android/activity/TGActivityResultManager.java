package org.herac.tuxguitar.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;

public class TGActivityResultManager {
	
	private int requestCode;
	private Map<Object, List<TGActivityResultHandler>> handlers;
	
	public TGActivityResultManager() {
		this.handlers = new HashMap<Object, List<TGActivityResultHandler>>();
	}

	public void initialize() {
		this.requestCode = 1;
		this.handlers.clear();
	}

	public int createRequestCode() {
		synchronized (TGActivityResultManager.this) {
			return (this.requestCode ++);
		}
	}
	
	public List<TGActivityResultHandler> getHandlers(Integer requestCode) {
		if( this.handlers.containsKey(requestCode)) {
			return this.handlers.get(requestCode);
		}
		
		this.handlers.put(requestCode, new ArrayList<TGActivityResultHandler>());
		
		return this.getHandlers(requestCode);
	}
	
	public void addHandler(Integer requestCode, TGActivityResultHandler handler) {
		List<TGActivityResultHandler> handlers = this.getHandlers(requestCode);
		if(!handlers.contains(handler)) {
			handlers.add(handler);
		}
	}
	
	public void removeHandler(Integer requestCode, TGActivityResultHandler handler) {
		List<TGActivityResultHandler> handlers = this.getHandlers(requestCode);
		if( handlers.contains(handler)) {
			handlers.remove(handler);
		}
	}
	
	public void onActivityResult(Integer requestCode, Integer resultCode, Intent data) {
		List<TGActivityResultHandler> handlers = new ArrayList<TGActivityResultHandler>(this.getHandlers(requestCode));
		for(TGActivityResultHandler handler : handlers) {
			handler.onActivityResult(resultCode, data);
		}
	}
}
