package org.herac.tuxguitar.app.system.config;

import org.herac.tuxguitar.app.system.properties.TGPropertiesUIUtil;
import org.herac.tuxguitar.ui.resource.UIColorModel;
import org.herac.tuxguitar.ui.resource.UIFontModel;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGVersion;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGConfigManager extends org.herac.tuxguitar.util.configuration.TGConfigManager {
	
	public static final String CONFIGURATION_MODULE = "tuxguitar";
	
	private TGConfigManager(TGContext context, String[] obsoleteKeys){
		super(context, CONFIGURATION_MODULE, obsoleteKeys);
	}
	
	public void setValue(String key, UIColorModel model){
		TGPropertiesUIUtil.setValue(this.getProperties(), key, model);
	}
	
	public void setValue(String key, UIFontModel fm){
		TGPropertiesUIUtil.setValue(this.getProperties(), key, fm);
	}
	
	public UIFontModel getFontModelConfigValue(String key) {
		return TGPropertiesUIUtil.getFontModelValue(this.getProperties(), key);
	}
	
	public UIColorModel getColorModelConfigValue(String key){
		return TGPropertiesUIUtil.getColorModelValue(this.getContext(), this.getProperties(), key);
	}
	
	public void save(){
		setValue(TGConfigKeys.CONFIG_APP_VERSION, TGVersion.CURRENT.toString());
		super.save();
	}
	
	public static TGConfigManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGConfigManager.class.getName(), new TGSingletonFactory<TGConfigManager>() {
			public TGConfigManager createInstance(TGContext context) {
				return new TGConfigManager(context, TGConfigKeys.OBSOLETE_KEYS);
			}
		});
	}
}
