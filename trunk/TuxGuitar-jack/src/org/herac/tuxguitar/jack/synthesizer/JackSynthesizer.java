package org.herac.tuxguitar.jack.synthesizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.jack.JackClient;
import org.herac.tuxguitar.jack.JackPort;
import org.herac.tuxguitar.player.base.MidiChannel;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.base.MidiSynthesizer;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.util.TGContext;

public class JackSynthesizer implements MidiSynthesizer{
	
	private TGContext context;
	private JackClient jackClient;
	private JackGmPort jackGmPort;
	private List<JackChannelProxy> jackChannelProxies;
	
	public JackSynthesizer(TGContext context, JackClient jackClient){
		this.context = context;
		this.jackClient = jackClient;
		this.jackChannelProxies = new ArrayList<JackChannelProxy>();
	}
	
	public MidiChannel openChannel(int channelId) throws MidiPlayerException{
		return this.openChannel(channelId, false);
	}
	
	public MidiChannel openChannel(int channelId, boolean exclusive) throws MidiPlayerException{
		JackChannelProxy jackChannelProxy = findChannel(channelId);
		if( jackChannelProxy != null ){
			if( jackChannelProxy.isExclusive() == exclusive ){
				return jackChannelProxy;
			}
			
			JackPort jackPort = jackChannelProxy.getJackPort();
			if( jackPort != null ){
				jackChannelProxy.setJackPort(null);
				this.closeUnusedPort(jackPort);
			}
		}
		
		if( jackChannelProxy == null ){
			jackChannelProxy = new JackChannelProxy(channelId, this);
		}
		jackChannelProxy.setMidiChannel(null);
		jackChannelProxy.setJackPort(null);
		jackChannelProxy.setExclusive(exclusive);
		
		boolean channelLoaded = ( exclusive ? this.loadExclusiveChannel(jackChannelProxy) : this.loadGmChannel(jackChannelProxy) );
		
		if( channelLoaded ){
			if(!this.jackChannelProxies.contains(jackChannelProxy)){
				this.jackChannelProxies.add( jackChannelProxy );
			}
			return jackChannelProxy;
		}
		return null;
	}
	
	public boolean loadGmChannel(JackChannelProxy jackChannelProxy) throws MidiPlayerException{
		if( this.jackGmPort != null && !this.jackClient.isPortOpen(this.jackGmPort.getJackPort()) ) {
			this.jackGmPort = null;
		}
		if( this.jackGmPort == null ) {
			JackPort jackPort = this.jackClient.openPort(createJackPortName(jackChannelProxy));
			if( jackPort != null ){
				this.jackGmPort = new JackGmPort(this.jackClient, jackPort);
			}
		}
		
		if( this.jackGmPort != null ){
			jackChannelProxy.setExclusive(false);
			jackChannelProxy.setMidiChannel(this.jackGmPort.getSynthesizer().openChannel(jackChannelProxy.getJackChannelId()));
			jackChannelProxy.setJackPort(this.jackGmPort.getJackPort());
			
			return true;
		}
		return false;
	}
	
	public boolean loadExclusiveChannel(JackChannelProxy jackChannelProxy) throws MidiPlayerException{
		JackPort jackPort = this.jackClient.openPort(createJackPortName(jackChannelProxy));
		if( jackPort != null ){
			jackChannelProxy.setExclusive(true);
			jackChannelProxy.setMidiChannel(new JackChannel(this.jackClient, jackPort));
			jackChannelProxy.setJackPort(jackPort);
			
			return true;
		}
		return false;
	}
	
	public void closeChannel(MidiChannel midiChannel){
		JackChannelProxy jackChannelProxy = findChannel(((JackChannelProxy)midiChannel).getJackChannelId());
		if( jackChannelProxy != null ){
			this.jackChannelProxies.remove(jackChannelProxy);
			if( jackChannelProxy.getJackPort() != null ){
				this.closeUnusedPort( jackChannelProxy.getJackPort() );
			}
		}
	}
	
	public void closeAllChannels(){
		List<JackChannelProxy> jackChannelProxies = new ArrayList<JackChannelProxy>(this.jackChannelProxies);
		Iterator<JackChannelProxy> it = jackChannelProxies.iterator();
		while( it.hasNext() ){
			this.closeChannel((JackChannelProxy) it.next());
		}
	}
	
	public boolean isChannelOpen(MidiChannel midiChannel) {
		JackChannelProxy jackChannelProxy = findChannel(((JackChannelProxy)midiChannel).getJackChannelId());
		if( jackChannelProxy != null && jackChannelProxy.getJackPort() != null && jackChannelProxy == midiChannel ){
			if( this.jackClient.isPortOpen( jackChannelProxy.getJackPort() ) ){
				return jackChannelProxy.getJackPort().getJackPortName().equals(createJackPortName(jackChannelProxy));
			}
		}
		return false;
	}
	
	public JackChannelProxy findChannel(int channelId){
		Iterator<JackChannelProxy> it = this.jackChannelProxies.iterator();
		while( it.hasNext() ){
			JackChannelProxy jackChannelProxy = (JackChannelProxy) it.next();
			if( jackChannelProxy.getJackChannelId() == channelId ){
				return jackChannelProxy;
			}
		}
		return null;
	}
	
	public void closeUnusedPort(JackPort jackPort) {
		if(!isJackPortInUse( jackPort )){
			this.jackClient.closePort( jackPort );
		}
	}
	
	public boolean isJackPortInUse(JackPort jackPort){
		Iterator<JackChannelProxy> it = this.jackChannelProxies.iterator();
		while( it.hasNext() ){
			JackChannelProxy jackChannelProxy = (JackChannelProxy) it.next();
			if( jackChannelProxy.getJackPort() != null && jackChannelProxy.getJackPort().equals(jackPort) ){
				return true;
			}
		}
		return false;
	}
	
	public String createJackPortName(JackChannelProxy jackChannelProxy){
		if(!jackChannelProxy.isExclusive() ){
			return ("GM Port");
		}
		MidiPlayer midiPlayer = MidiPlayer.getInstance(this.context);
		TGChannel tgChannel = midiPlayer.getSongManager().getChannel(midiPlayer.getSong(), jackChannelProxy.getJackChannelId());
		if( tgChannel != null ){
			return tgChannel.getName();
		}
		return ("Channel-" + jackChannelProxy.getJackChannelId());
	}
}
