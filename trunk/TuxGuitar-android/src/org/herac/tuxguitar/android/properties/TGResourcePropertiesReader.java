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
	private String resource;
	
	public TGResourcePropertiesReader(TGContext context, String resource) {
		this.context = context;
		this.resource = resource;
	}

	@SuppressWarnings("unchecked")
	public void readProperties(TGProperties targetProperties, String module) throws TGPropertiesException {
		try {
			String resourceName = (module + "-" + this.resource + ".cfg");
			InputStream inputStream = TGResourceManager.getInstance(this.context).getResourceAsStream(resourceName);
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
