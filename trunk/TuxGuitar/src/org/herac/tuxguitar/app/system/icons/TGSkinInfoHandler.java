package org.herac.tuxguitar.app.system.icons;

import org.herac.tuxguitar.app.system.properties.TGResourcePropertiesReader;
import org.herac.tuxguitar.util.TGContext;

public class TGSkinInfoHandler extends TGResourcePropertiesReader {
	
	public static final String RESOURCE = "tuxguitar-skin-info";
	public static final String RESOURCE_PREFIX = "skins/";
	public static final String RESOURCE_SUFFIX = "/skin.info";
	
	public TGSkinInfoHandler(TGContext context) {
		super(context, RESOURCE_PREFIX, RESOURCE_SUFFIX);
	}
}
