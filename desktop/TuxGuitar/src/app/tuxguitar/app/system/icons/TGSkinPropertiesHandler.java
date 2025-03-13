package app.tuxguitar.app.system.icons;

import app.tuxguitar.app.system.properties.TGResourcePropertiesReader;
import app.tuxguitar.util.TGContext;

public class TGSkinPropertiesHandler extends TGResourcePropertiesReader {

	public static final String RESOURCE = "tuxguitar-skin-properties";
	public static final String RESOURCE_PREFIX = "skins/";
	public static final String RESOURCE_SUFFIX = "/skin.prop";

	public TGSkinPropertiesHandler(TGContext context) {
		super(context, RESOURCE_PREFIX, RESOURCE_SUFFIX);
	}
}
