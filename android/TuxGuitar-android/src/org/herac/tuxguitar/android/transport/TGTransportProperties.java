package org.herac.tuxguitar.android.transport;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesManager;
import org.herac.tuxguitar.util.properties.TGPropertiesUtil;

public class TGTransportProperties {
	
	public static final String MODULE = "tuxguitar";
	public static final String RESOURCE = "settings";

	public static final String PROPERTY_MIDI_OUTPUT_PORT = "midi.output.port";

	private TGContext context;
	private TGProperties properties;
	
	public TGTransportProperties(TGContext context){
		this.context = context;
		this.properties = TGPropertiesManager.getInstance(this.context).createProperties();
		this.load();
	}
	
	public void load(){
		TGPropertiesManager.getInstance(this.context).readProperties(this.properties, RESOURCE, MODULE);
	}

	public String getMidiOutputPort() {
		return TGPropertiesUtil.getStringValue(this.properties, PROPERTY_MIDI_OUTPUT_PORT);
	}
}