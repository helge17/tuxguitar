package app.tuxguitar.android.properties;

import app.tuxguitar.util.properties.TGProperties;
import app.tuxguitar.util.properties.TGPropertiesException;
import app.tuxguitar.util.properties.TGPropertiesFactory;

public class TGPropertiesFactoryImpl implements TGPropertiesFactory{

	public TGProperties createProperties() throws TGPropertiesException {
		return new TGPropertiesImpl();
	}
}
