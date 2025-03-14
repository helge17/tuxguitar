package app.tuxguitar.android.storage;

import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.properties.TGProperties;
import app.tuxguitar.util.properties.TGPropertiesManager;
import app.tuxguitar.util.properties.TGPropertiesUtil;

public class TGStorageProperties {

	public static final String MODULE = "tuxguitar";
	public static final String RESOURCE = "settings";

	public static final String PROPERTY_SAF_PROVIDER = "storage.use.saf.provider";

	private TGContext context;
	private TGProperties properties;

	public TGStorageProperties(TGContext context){
		this.context = context;
		this.properties = TGPropertiesManager.getInstance(this.context).createProperties();
		this.load();
	}

	public void load(){
		TGPropertiesManager.getInstance(this.context).readProperties(this.properties, RESOURCE, MODULE);
	}

	public boolean isUseSafProvider() {
		return TGPropertiesUtil.getBooleanValue(this.properties, PROPERTY_SAF_PROVIDER);
	}
}