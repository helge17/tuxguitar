package org.herac.tuxguitar.app.editors.channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.player.base.MidiDevice;
import org.herac.tuxguitar.song.models.TGChannel;

public class TGChannelSettingsHandlerManager {
	
	private List channelSettingsHandler;
	
	public TGChannelSettingsHandlerManager(){
		this.channelSettingsHandler = new ArrayList();
	}
	
	public void addChannelSettingsHandler(TGChannelSettingsHandler handler){
		if(!this.channelSettingsHandler.contains( handler ) ){
			this.channelSettingsHandler.add( handler );
		}
	}
	
	public void removeChannelSettingsHandler(TGChannelSettingsHandler handler){
		if( this.channelSettingsHandler.contains( handler ) ){
			this.channelSettingsHandler.remove( handler );
		}
	}
	
	public TGChannelSettingsHandler findSupportedChannelSettingsHandler(){
		MidiDevice midiDevice = getMidiDevice();
		
		Iterator it = this.channelSettingsHandler.iterator();
		while( it.hasNext() ){
			TGChannelSettingsHandler channelSettingsHandler = (TGChannelSettingsHandler)it.next();
			if( channelSettingsHandler.isMidiDeviceSupported(midiDevice) ){
				return channelSettingsHandler;
			}
		}
		return null;
	}
	
	public TGChannelSettingsDialog findChannelSettingsDialog(TGChannel channel) {
		TGChannelSettingsHandler channelSettingsHandler = findSupportedChannelSettingsHandler();
		if( channelSettingsHandler != null ){
			return channelSettingsHandler.createChannelSettingsDialog(getMidiDevice() ,channel);
		}
		return null;
	}
	
	public boolean isChannelSettingsHandlerAvailable(){
		return ( findSupportedChannelSettingsHandler() != null );
	}
	
	private MidiDevice getMidiDevice(){
		return TuxGuitar.getInstance().getPlayer().getOutputPort();
	}
}
