package org.herac.tuxguitar.app.system.icons;

import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.ui.resource.UIImage;

public class TGIconTheme {
	
	private String name;
	private Map<String, UIImage> resources;
	
	public TGIconTheme(String name) {
		this.name = name;
		this.resources = new HashMap<String, UIImage>();
	}

	public String getName() {
		return name;
	}

	public UIImage setResource(String name, UIImage resource) {
		return this.resources.put(name, resource);
	}
	
	public UIImage getResource(String name) {
		if( this.resources.containsKey(name) ) {
			return this.resources.get(name);
		}
		return null;
	}
	
	public Map<String, UIImage> getResources() {
		return resources;
	}
}
