package org.herac.tuxguitar.io.gtp;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.system.plugins.base.TGPluginList;

public class GTPPluginList extends TGPluginList {
	
	protected List getPlugins() {
		GTPSettingsUtil.instance().load();
		
		List plugins = new ArrayList();
		plugins.add(new GTPInputStreamPlugin(new GP5InputStream(GTPSettingsUtil.instance().getSettings())));
		plugins.add(new GTPInputStreamPlugin(new GP4InputStream(GTPSettingsUtil.instance().getSettings())));
		plugins.add(new GTPInputStreamPlugin(new GP3InputStream(GTPSettingsUtil.instance().getSettings())));
		plugins.add(new GTPInputStreamPlugin(new GP2InputStream(GTPSettingsUtil.instance().getSettings())));
		plugins.add(new GTPInputStreamPlugin(new GP1InputStream(GTPSettingsUtil.instance().getSettings())));
		plugins.add(new GTPOutputStreamPlugin(new GP5OutputStream(GTPSettingsUtil.instance().getSettings())));
		plugins.add(new GTPOutputStreamPlugin(new GP4OutputStream(GTPSettingsUtil.instance().getSettings())));
		plugins.add(new GTPOutputStreamPlugin(new GP3OutputStream(GTPSettingsUtil.instance().getSettings())));
		
		return plugins;
	}
	
	public String getModuleId() {
		return GTPPlugin.MODULE_ID;
	}
}
