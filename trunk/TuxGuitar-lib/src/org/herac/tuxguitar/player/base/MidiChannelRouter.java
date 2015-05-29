package org.herac.tuxguitar.player.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MidiChannelRouter implements MidiReceiver{
	
	private Map<Integer, MidiChannel> midiChannels;
	
	public MidiChannelRouter(){
		this.midiChannels = new HashMap<Integer, MidiChannel>();
	}
	
	public String getId(){
		return MidiChannelRouter.class.getName();
	}
	
	public void sendParameter(int channelId, String key, String value) throws MidiPlayerException{
		MidiChannel midiChannel = getMidiChannel(channelId);
		if( midiChannel != null ){
			midiChannel.sendParameter(key, value);
		}
	}
	
	public void sendNoteOn(int channelId, int key, int velocity, int voice, boolean bendMode) throws MidiPlayerException {
		MidiChannel midiChannel = getMidiChannel(channelId);
		if( midiChannel != null ){
			midiChannel.sendNoteOn(key, velocity, voice, bendMode);
		}
	}

	public void sendNoteOff(int channelId, int key, int velocity, int voice, boolean bendMode) throws MidiPlayerException {
		MidiChannel midiChannel = getMidiChannel(channelId);
		if( midiChannel != null ){
			midiChannel.sendNoteOff(key, velocity, voice, bendMode);
		}
	}
	
	public void sendPitchBend(int channelId, int value, int voice, boolean bendMode) throws MidiPlayerException {
		MidiChannel midiChannel = getMidiChannel(channelId);
		if( midiChannel != null ){
			midiChannel.sendPitchBend(value, voice, bendMode);
		}
	}
	
	public void sendProgramChange(int channelId, int value) throws MidiPlayerException {
		MidiChannel midiChannel = getMidiChannel(channelId);
		if( midiChannel != null ){
			midiChannel.sendProgramChange(value);
		}
	}

	public void sendControlChange(int channelId, int controller, int value) throws MidiPlayerException {
		MidiChannel midiChannel = getMidiChannel(channelId);
		if( midiChannel != null ){
			midiChannel.sendControlChange(controller, value);
		}
	}
	
	public void sendAllNotesOff() throws MidiPlayerException {
		List<Integer> midiChannelIds = getMidiChannelIds();
		for(int i = 0; i < midiChannelIds.size(); i ++){
			this.sendControlChange(((Integer)midiChannelIds.get(i)).intValue(),MidiControllers.ALL_NOTES_OFF,0);
		}
	}
	
	public void sendPitchBendReset() throws MidiPlayerException {
		List<Integer> midiChannelIds = getMidiChannelIds();
		for(int i = 0; i < midiChannelIds.size(); i ++){
			this.sendPitchBend(((Integer)midiChannelIds.get(i)).intValue(), 64, -1, false);
		}
	}
	
	public MidiChannel getMidiChannel(int channelId){
		Integer key = new Integer(channelId);
		if( this.midiChannels.containsKey(key) ){
			return (MidiChannel)this.midiChannels.get(key);
		}
		return null;
	}
	
	public void removeMidiChannel(int channelId){
		Integer key = new Integer(channelId);
		if( this.midiChannels.containsKey(key) ){
			this.midiChannels.remove(key);
		}
	}
	
	public void addMidiChannel(int channelId, MidiChannel midiChannel){
		Integer key = new Integer(channelId);
		if( this.midiChannels.containsKey(key) ){
			this.midiChannels.remove(key);
		}
		this.midiChannels.put(key, midiChannel);
	}
	
	public List<Integer> getMidiChannelIds(){
		List<Integer> midiChannelIds = new ArrayList<Integer>();
		
		Iterator<Integer> iterator = this.midiChannels.keySet().iterator();
		while (iterator.hasNext()) {
			midiChannelIds.add(iterator.next());
		}
		
		return midiChannelIds;
	}
}
