package app.tuxguitar.community;

import app.tuxguitar.community.auth.TGCommunityAuth;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.configuration.TGConfigManager;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGCommunitySingleton {

	private TGContext context;
	private TGConfigManager config;
	private TGCommunityAuth auth;

	private TGCommunitySingleton(TGContext context){
		this.context = context;
		this.auth = new TGCommunityAuth();
	}

	public TGConfigManager getConfig(){
		if( this.config == null ){
			this.config = new TGConfigManager(this.context, "tuxguitar-community");
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

	public static TGCommunitySingleton getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGCommunitySingleton.class.getName(), new TGSingletonFactory<TGCommunitySingleton>() {
			public TGCommunitySingleton createInstance(TGContext context) {
				return new TGCommunitySingleton(context);
			}
		});
	}
}
