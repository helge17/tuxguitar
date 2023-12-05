package org.herac.tuxguitar.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TGActivityPermissionResultManager {
	
	private int requestCode;
	private Map<Object, List<TGActivityPermissionResultHandler>> handlers;
	
	public TGActivityPermissionResultManager() {
		this.handlers = new HashMap<Object, List<TGActivityPermissionResultHandler>>();
	}

	public void initialize() {
		this.requestCode = 1;
		this.handlers.clear();
	}

	public int createRequestCode() {
		synchronized (TGActivityPermissionResultManager.this) {
			return (this.requestCode ++);
		}
	}
	
	public List<TGActivityPermissionResultHandler> getHandlers(Integer requestCode) {
		if( this.handlers.containsKey(requestCode)) {
			return this.handlers.get(requestCode);
		}
		
		this.handlers.put(requestCode, new ArrayList<TGActivityPermissionResultHandler>());
		
		return this.getHandlers(requestCode);
	}
	
	public void addHandler(Integer requestCode, TGActivityPermissionResultHandler handler) {
		List<TGActivityPermissionResultHandler> handlers = this.getHandlers(requestCode);
		if(!handlers.contains(handler)) {
			handlers.add(handler);
		}
	}
	
	public void removeHandler(Integer requestCode, TGActivityPermissionResultHandler handler) {
		List<TGActivityPermissionResultHandler> handlers = this.getHandlers(requestCode);
		if( handlers.contains(handler)) {
			handlers.remove(handler);
		}
	}
	
	public void onRequestPermissionsResult(Integer requestCode, String[] permissions, int[] grantResults) {
		List<TGActivityPermissionResultHandler> handlers = new ArrayList<TGActivityPermissionResultHandler>(this.getHandlers(requestCode));
		for(TGActivityPermissionResultHandler handler : handlers) {
			handler.onRequestPermissionsResult(permissions, grantResults);
		}
	}
}
