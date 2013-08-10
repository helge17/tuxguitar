package org.herac.tuxguitar.community;

import org.herac.tuxguitar.community.auth.TGCommunityAuth;
import org.herac.tuxguitar.util.configuration.TGConfigManager;

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
		if( this.config == null ){ 
			this.config = new TGConfigManager("tuxguitar-community");
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
