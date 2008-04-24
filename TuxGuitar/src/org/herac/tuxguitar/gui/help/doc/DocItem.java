package org.herac.tuxguitar.gui.help.doc;

import java.util.ArrayList;
import java.util.List;

public class DocItem {
	
	private String name;
	private String url;
	private List children;
	
	public DocItem(String name, String url) {
		this.name = name;
		this.url = url;
		this.children = new ArrayList();
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public List getChildren(){
		return this.children;
	}
	
}
