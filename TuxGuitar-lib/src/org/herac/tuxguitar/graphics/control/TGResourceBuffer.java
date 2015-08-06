package org.herac.tuxguitar.graphics.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.herac.tuxguitar.graphics.TGResource;

public class TGResourceBuffer {
	
	private Map<Object, TGResource> buffer;
	
	public TGResourceBuffer() {
		this.buffer = new HashMap<Object, TGResource>();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends TGResource> T getResource(Object key) {
		if( this.buffer.containsKey(key) ) {
			return (T) this.buffer.get(key);
		}
		return null;
	}
	
	public void setResource(Object key, TGResource resource) {
		if( this.buffer.containsKey(key) ) {
			this.disposeResource(key);
		}
		
		this.buffer.put(key, resource);
	}
	
	public void disposeResource(Object key) {
		TGResource resource = this.getResource(key);
		if( resource != null && !resource.isDisposed() ) {
			resource.dispose();
		}
		this.buffer.remove(key);
	}
	
	public void disposeAllResources() {
		List<Object> keys = new ArrayList<Object>(this.buffer.keySet());
		for(Object key : keys) {
			this.disposeResource(key);
		}
	}
	
	public boolean isResourceDisposed(Object key) {
		TGResource resource = this.getResource(key);
		
		return (resource == null || resource.isDisposed());
	}
}
