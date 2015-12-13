package org.herac.tuxguitar.app.system.properties;

import java.io.InputStream;

import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesException;
import org.herac.tuxguitar.util.properties.TGPropertiesReader;

public class TGResourcePropertiesReader extends TGPropertiesBaseHandler implements TGPropertiesReader{
	
	private TGContext context;
	
	public TGResourcePropertiesReader(TGContext context, String prefix, String suffix) {
		super(prefix, suffix);
		
		this.context = context;
	}

	public void readProperties(TGProperties properties, String module) throws TGPropertiesException {
		try {
			InputStream inputStream = TGResourceManager.getInstance(this.context).getResourceAsStream(getPrefix() + module + getSuffix());
			if( inputStream != null ){
				((TGPropertiesImpl)properties).load(inputStream);
			}
		} catch (Throwable throwable) {
			throw new TGPropertiesException(throwable);
		}
	}
}
