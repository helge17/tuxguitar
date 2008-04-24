package org.herac.tuxguitar.gui.system.plugins;

import java.io.File;
import java.util.Properties;

import org.herac.tuxguitar.gui.system.config.TGConfigManager;
import org.herac.tuxguitar.gui.util.TGFileUtils;

public class TGPluginConfigManager extends TGConfigManager{
	
	private String name;
	
	public TGPluginConfigManager(String name){
		this.name = name;
	}
	
	public String getName() {
		return "Plugin Configuration";
	}
	
	public String getFileName(){
		return TGFileUtils.PATH_USER_PLUGINS_CONFIG + File.separator + this.name + ".cfg";
	}
	
	public Properties getDefaults() {
		return new Properties();
	}
	
}
