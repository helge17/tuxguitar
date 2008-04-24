/*
 * Created on 09-ene-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.system.plugins;

import java.io.File;
import java.util.Properties;

import org.herac.tuxguitar.gui.system.config.TGConfigManager;
import org.herac.tuxguitar.gui.util.TGFileUtils;

/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class TGPluginProperties  extends TGConfigManager{
	
	private static TGPluginProperties instance;
	
	public static TGPluginProperties instance(){
		if(instance == null){
			instance = new TGPluginProperties();
			instance.init();
		}
		return instance;
	}
	
	private TGPluginProperties(){
		super();
	}
	
	public String getName() {
		return "TuxGuitar Plugin Properties";
	}
	
	public String getFileName(){
		return TGFileUtils.PATH_USER_CONFIG + File.separator + "plugin.properties";
	}
	
	public Properties getDefaults() {
		return new Properties();
	}
	
}