package app.tuxguitar.app.system.properties;

import java.io.InputStream;

import app.tuxguitar.resource.TGResourceManager;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.properties.TGProperties;
import app.tuxguitar.util.properties.TGPropertiesException;
import app.tuxguitar.util.properties.TGPropertiesReader;

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
