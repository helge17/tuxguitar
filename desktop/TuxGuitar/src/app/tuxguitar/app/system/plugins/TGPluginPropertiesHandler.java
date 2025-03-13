package app.tuxguitar.app.system.plugins;

import java.io.File;

import app.tuxguitar.app.system.properties.TGDefaultAndStoredPropertiesHandler;
import app.tuxguitar.app.util.TGFileUtils;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPluginProperties;

public class TGPluginPropertiesHandler extends TGDefaultAndStoredPropertiesHandler {

	private static final String RESOURCE = TGPluginProperties.RESOURCE;
	private static final String RESOURCE_PREFIX = "";
	private static final String RESOURCE_SUFFIX = "-" + RESOURCE + ".cfg";
	private static final String DEFAULTS_PREFIX = RESOURCE_PREFIX;
	private static final String DEFAULTS_SUFFIX = RESOURCE_SUFFIX;
	private static final String STORED_PREFIX = TGFileUtils.PATH_USER_CONFIG + File.separator + RESOURCE_PREFIX;
	private static final String STORED_SUFFIX = RESOURCE_SUFFIX;

	public TGPluginPropertiesHandler(TGContext context) {
		super(context, DEFAULTS_PREFIX, DEFAULTS_SUFFIX, STORED_PREFIX, STORED_SUFFIX);
	}
}
