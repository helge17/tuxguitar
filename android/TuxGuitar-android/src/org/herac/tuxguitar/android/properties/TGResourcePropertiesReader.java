package org.herac.tuxguitar.android.properties;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesException;
import org.herac.tuxguitar.util.properties.TGPropertiesReader;

public class TGResourcePropertiesReader implements TGPropertiesReader {
	
	private TGContext context;
	private String modulePrefix;
	private String moduleSuffix;
	
	public TGResourcePropertiesReader(TGContext context, String modulePrefix, String moduleSuffix) {
		this.context = context;
		this.modulePrefix = modulePrefix;
		this.moduleSuffix = moduleSuffix;
	}

	@SuppressWarnings("unchecked")
	public void readProperties(TGProperties targetProperties, String module) throws TGPropertiesException {
		try {
			StringBuilder resourceName = new StringBuilder();
			if( this.modulePrefix != null ) {
				resourceName.append(this.modulePrefix);
			}
			resourceName.append(module);
			if( this.moduleSuffix != null ) {
				resourceName.append(this.moduleSuffix);
			}
			resourceName.append(".cfg");
			InputStream inputStream = TGResourceManager.getInstance(this.context).getResourceAsStream(resourceName.toString());
			if( inputStream != null ){
				Properties properties = new Properties();
				properties.load(inputStream);
				Iterator<?> it = properties.entrySet().iterator();
				while( it.hasNext() ) {
					Map.Entry<String, ?> entry = (Map.Entry<String, ?>) it.next();
					targetProperties.setValue(entry.getKey(), (entry.getValue() != null ? entry.getValue().toString() : null));
				}
			}
		} catch (Throwable throwable) {
			throw new TGPropertiesException(throwable);
		}
	}
}
