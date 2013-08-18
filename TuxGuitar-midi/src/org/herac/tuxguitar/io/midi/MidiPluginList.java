package org.herac.tuxguitar.io.midi;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.system.plugins.base.TGPluginList;

public class MidiPluginList extends TGPluginList{
	
	public static final String MODULE_ID = "tuxguitar-midi";
	
	protected List getPlugins() {
		List plugins = new ArrayList();
		plugins.add(new MidiPluginImporter());
		plugins.add(new MidiPluginExporter());
		return plugins;
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
