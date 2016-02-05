package org.herac.tuxguitar.app.system.icons;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;

public class TGIconTheme {
	
	private String name;
	private Map<String, Image> resources;
	
	public TGIconTheme(String name) {
		this.name = name;
		this.resources = new HashMap<String, Image>();
	}

	public String getName() {
		return name;
	}

	public Map<String, Image> getResources() {
		return resources;
	}
	
	public Image setResource(String name, Image resource) {
		return this.resources.put(name, resource);
	}
	
	public Image getResource(String name) {
		if( this.resources.containsKey(name) ) {
			return this.resources.get(name);
		}
		return null;
	}
}
