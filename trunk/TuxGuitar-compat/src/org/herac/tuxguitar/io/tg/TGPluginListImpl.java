package org.herac.tuxguitar.io.tg;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.system.plugins.base.TGPluginList;

public class TGPluginListImpl extends TGPluginList{
	
	protected List getPlugins() {
		List plugins = new ArrayList();
		
		plugins.add(new TGInputStreamPluginImpl(new org.herac.tuxguitar.io.tg.v12.TGInputStream()));
		plugins.add(new TGInputStreamPluginImpl(new org.herac.tuxguitar.io.tg.v11.TGInputStream()));
		plugins.add(new TGInputStreamPluginImpl(new org.herac.tuxguitar.io.tg.v10.TGInputStream()));
		plugins.add(new TGInputStreamPluginImpl(new org.herac.tuxguitar.io.tg.v09.TGInputStream()));
		plugins.add(new TGInputStreamPluginImpl(new org.herac.tuxguitar.io.tg.v08.TGInputStream()));
		plugins.add(new TGInputStreamPluginImpl(new org.herac.tuxguitar.io.tg.v07.TGInputStream()));
		plugins.add(new TGExporterPluginImpl(new org.herac.tuxguitar.io.tg.v12.TGOutputStream()));
		plugins.add(new TGExporterPluginImpl(new org.herac.tuxguitar.io.tg.v11.TGOutputStream()));
		plugins.add(new TGExporterPluginImpl(new org.herac.tuxguitar.io.tg.v10.TGOutputStream()));
		
		return plugins;
	}
	
	public String getModuleId(){
		return TGCompatPlugin.MODULE_ID;
	}
}
