package org.herac.tuxguitar.ui.chooser;

import java.util.ArrayList;
import java.util.List;

public class UIFileChooserFormat {
	
	private String name;
	private List<String> extensions;
	
	public UIFileChooserFormat(String name, List<String> extensions) {
		this.name = name;
		this.extensions = extensions;
	}

	public UIFileChooserFormat(String name) {
		this(name, new ArrayList<String>());
	}
	
	public String getName() {
		return name;
	}
	
	public List<String> getExtensions() {
		return extensions;
	}
}
