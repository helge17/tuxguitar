package org.herac.tuxguitar.editor.template;

public class TGTemplate {

	private String name;
	private String resource;
	private boolean isUserTemplate;

	public TGTemplate(){
		super();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResource() {
		return this.resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public void setUserTemplate() {
		this.isUserTemplate = true;
	}

	public boolean isUserTemplate() {
		return this.isUserTemplate;
	}
}
