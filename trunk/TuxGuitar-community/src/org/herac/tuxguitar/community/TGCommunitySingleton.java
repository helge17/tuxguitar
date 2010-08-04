package org.herac.tuxguitar.community;

import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.app.system.plugins.TGPluginConfigManager;
import org.herac.tuxguitar.community.auth.TGCommunityAuth;

public class TGCommunitySingleton {
	
	private static TGCommunitySingleton instance;
	
	private TGConfigManager config;
	private TGCommunityAuth auth;
	
	private TGCommunitySingleton(){
		this.auth = new TGCommunityAuth();
	}
	
	public static TGCommunitySingleton getInstance() {
		synchronized ( TGCommunitySingleton.class ) {
			if( instance == null ){
				instance = new TGCommunitySingleton();
			}
		}
		return instance;
	}
	
	public TGConfigManager getConfig(){
		if(this.config == null){ 
			this.config = new TGPluginConfigManager("tuxguitar-community");
			this.config.init();
		}
		return this.config;
	}
	
	public void loadSettings(){
		TGConfigManager config = getConfig();
		this.auth.load( config );
	}
	
	public void saveSettings(){
		TGConfigManager config = getConfig();
		this.auth.save( config );
		config.save();
	}
	
	public TGCommunityAuth getAuth(){
		return this.auth;
	}
}
