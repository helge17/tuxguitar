package org.herac.tuxguitar.app.system.config;

import java.io.File;
import java.util.Properties;

import org.herac.tuxguitar.app.util.TGFileUtils;

public class TGConfigManagerImpl extends TGConfigManager{
	
	public TGConfigManagerImpl(){
		super();
	}
	
	public String getName() {
		return "System Configuration";
	}
	
	public String getFileName(){
		return TGFileUtils.PATH_USER_CONFIG + File.separator + "config.properties";
	}
	
	public Properties getDefaults() {
		return new TGConfigDefaults().getProperties();
	}
	
}
