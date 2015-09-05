package org.herac.tuxguitar.app.system.properties;

import java.io.InputStream;

import org.herac.tuxguitar.app.util.TGFileUtils;
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
			InputStream is = TGFileUtils.getResourceAsStream(this.context, getPrefix() + module + getSuffix());
			if(is != null){
				((TGPropertiesImpl)properties).load(is);
			}
		} catch (Throwable throwable) {
			throw new TGPropertiesException(throwable);
		}
	}
}
