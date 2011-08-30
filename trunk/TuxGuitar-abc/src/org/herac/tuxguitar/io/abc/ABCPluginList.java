package org.herac.tuxguitar.io.abc;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.system.plugins.base.TGPluginList;

public class ABCPluginList extends TGPluginList{
	
	protected List getPlugins() {
		List plugins = new ArrayList();
		plugins.add(new ABCPluginImporter());
		plugins.add(new ABCPluginExporter());
		return plugins;
	}
	
	public String getAuthor() {
		return "Peter Grootswagers <pgrootswagers@planet.nl>";
	}
	
	public String getDescription() {
		return "ABC file format import-export plugin";
	}
	
	public String getName() {
		return "ABC file format import-export plugin";
	}
	
	public String getVersion() {
		return "1.0";
	}
}
