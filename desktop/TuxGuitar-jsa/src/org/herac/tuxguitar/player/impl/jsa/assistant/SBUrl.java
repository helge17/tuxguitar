package org.herac.tuxguitar.player.impl.jsa.assistant;

import java.net.URL;

public class SBUrl {

	private URL url;
	private String name;
	
	public SBUrl(URL url, String name){
		this.url = url;
		this.name = name;
	}
	
	public URL getUrl() {
		return this.url;
	}
	
	public String getName() {
		return this.name;
	}
}
