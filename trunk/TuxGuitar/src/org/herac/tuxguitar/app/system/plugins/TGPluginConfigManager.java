package org.herac.tuxguitar.app.system.plugins;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.util.TGFileUtils;

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
		Properties properties = new Properties();
		try {
			InputStream is = TGFileUtils.getResourceAsStream(this.name + ".cfg");
			if(is != null){
				properties.load(is);
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		return properties;
	}
	
}
