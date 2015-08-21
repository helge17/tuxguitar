package org.herac.tuxguitar.io.gervill;

import org.herac.tuxguitar.io.base.TGRawExporter;
import org.herac.tuxguitar.io.plugin.TGExporterPlugin;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public class MidiToAudioPlugin extends TGExporterPlugin{
	
	public static final String MODULE_ID = "tuxguitar-gervill";
	
	private boolean available;
	
	public MidiToAudioPlugin(){
		this.available = MidiToAudioSynth.instance().isAvailable();
	}
	
	public void connect(TGContext context) throws TGPluginException {
		if( this.available ){
			super.connect(context);
		}
	}
	
	public void disconnect(TGContext context) throws TGPluginException {
		if( this.available ){
			super.disconnect(context);
		}
	}
//	public void init(TGContext context) throws TGPluginException {
//		if( this.available ){
//			super.init(context);
//		}
//	}
//	
//	public void close() throws TGPluginException {
//		if( this.available ){
//			super.close();
//		}
//	}
//	
//	public void setEnabled( boolean enabled ) throws TGPluginException {
//		if( this.available ){
//			super.setEnabled( enabled );
//		}
//	}
	
	protected TGRawExporter createExporter(TGContext context) throws TGPluginException {
		if( this.available ){
			return new MidiToAudioExporter();
		}
		return null;
	}
	
	public String getModuleId(){
		return MODULE_ID;
	}
}
