package org.herac.tuxguitar.app.system.config;

import java.io.File;

import org.herac.tuxguitar.app.system.properties.TGFilePropertiesHandler;
import org.herac.tuxguitar.app.system.properties.TGResourcePropertiesReader;
import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesException;
import org.herac.tuxguitar.util.properties.TGPropertiesReader;
import org.herac.tuxguitar.util.properties.TGPropertiesWriter;

public class TGConfigPropertiesHandler implements TGPropertiesReader, TGPropertiesWriter{
	
	private static final String DEFAULT_MODULE = "tuxguitar";
	
	private static final String DEFAULT_FILE_PREFIX = TGFileUtils.PATH_USER_CONFIG + File.separator;
	private static final String DEFAULT_FILE_SUFFIX = ".cfg";
	
	private static final String DEFAULT_RESOURCE_PREFIX = "";
	private static final String DEFAULT_RESOURCE_SUFFIX = ".cfg";
	
	private static final String PLUGINS_FILE_PREFIX = TGFileUtils.PATH_USER_PLUGINS_CONFIG + File.separator;
	
	private TGContext context;
	
	public TGConfigPropertiesHandler(TGContext context) {
		this.context = context;
	}
	
	public void writeProperties(TGProperties properties, String module) throws TGPropertiesException {
		this.storeProperties(properties, module);
	}
	
	public void readProperties(TGProperties properties, String module) throws TGPropertiesException {
		this.readDefaultProperties(properties, module);
		this.readStoredProperties(properties, module);
	}
	
	private void readDefaultProperties(TGProperties properties, String module) {
		if( DEFAULT_MODULE.equals(module) ){
			TGConfigDefaults.loadProperties(properties);
		}
		TGPropertiesReader tgPropertiesReader = new TGResourcePropertiesReader(this.context, DEFAULT_RESOURCE_PREFIX, DEFAULT_RESOURCE_SUFFIX);
		tgPropertiesReader.readProperties(properties, module);
	}
	
	private void readStoredProperties(TGProperties properties, String module) {
		TGPropertiesReader tgPropertiesReader = new TGFilePropertiesHandler(getModulePrefix(module), getModuleSuffix(module));
		tgPropertiesReader.readProperties(properties, module);
	}
	
	private void storeProperties(TGProperties properties, String module) {
		TGPropertiesWriter tgPropertiesWriter = new TGFilePropertiesHandler(getModulePrefix(module), getModuleSuffix(module));
		tgPropertiesWriter.writeProperties(properties, module);
	}
	
	private String getModulePrefix(String module){
		return (DEFAULT_MODULE.equals(module) ? DEFAULT_FILE_PREFIX : PLUGINS_FILE_PREFIX);
	}
	
	private String getModuleSuffix(String module){
		return DEFAULT_FILE_SUFFIX;
	}
}
