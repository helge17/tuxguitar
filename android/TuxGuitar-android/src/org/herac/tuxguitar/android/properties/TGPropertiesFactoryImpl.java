package org.herac.tuxguitar.android.properties;

import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesException;
import org.herac.tuxguitar.util.properties.TGPropertiesFactory;

public class TGPropertiesFactoryImpl implements TGPropertiesFactory{
	
	public TGProperties createProperties() throws TGPropertiesException {
		return new TGPropertiesImpl();
	}
}
