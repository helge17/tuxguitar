package app.tuxguitar.io.gtp;

import app.tuxguitar.song.factory.TGFactory;

public class GTPFileFormat {

	public static final String DEFAULT_TG_CHARSET = "UTF-8";
	public static final String DEFAULT_VERSION_CHARSET = "UTF-8";

	private GTPSettings settings;
	private TGFactory factory;

	public GTPFileFormat(GTPSettings settings){
		this.settings = settings;
	}

	public void init(TGFactory factory) {
		this.factory = factory;
	}

	protected GTPSettings getSettings(){
		return this.settings;
	}

	protected TGFactory getFactory(){
		return this.factory;
	}
}
