package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

public abstract class TGChannel {
	public static final short DEFAULT_PERCUSSION_CHANNEL = 9;
	
	public static final short DEFAULT_INSTRUMENT = 25;
	public static final short DEFAULT_VOLUME = 127;
	public static final short DEFAULT_BALANCE = 64;
	public static final short DEFAULT_CHORUS = 0;
	public static final short DEFAULT_REVERB = 0;
	public static final short DEFAULT_PHASER = 0;
	public static final short DEFAULT_TREMOLO = 0;
	
	private short channel;
	private short effectChannel;
	private short instrument;
	private short volume;
	private short balance;
	private short chorus;
	private short reverb;
	private short phaser;
	private short tremolo;
	
	public TGChannel() {
		this.channel = 0;
		this.effectChannel = 0;
		this.instrument = DEFAULT_INSTRUMENT;
		this.volume = DEFAULT_VOLUME;
		this.balance = DEFAULT_BALANCE;
		this.chorus = DEFAULT_CHORUS;
		this.reverb = DEFAULT_REVERB;
		this.phaser = DEFAULT_PHASER;
		this.tremolo = DEFAULT_TREMOLO;
	}
	
	public short getBalance() {
		return this.balance;
	}
	
	public void setBalance(short balance) {
		this.balance = balance;
	}
	
	public short getChannel() {
		return this.channel;
	}
	
	public void setChannel(short channel) {
		this.channel = channel;
	}
	
	public short getEffectChannel() {
		return this.effectChannel;
	}
	
	public void setEffectChannel(short effectChannel) {
		this.effectChannel = effectChannel;
	}
	
	public short getChorus() {
		return this.chorus;
	}
	
	public void setChorus(short chorus) {
		this.chorus = chorus;
	}
	
	public short getInstrument() {
		return (!this.isPercussionChannel()?this.instrument:0);
	}
	
	public void setInstrument(short instrument) {
		this.instrument = instrument;
	}
	
	public short getPhaser() {
		return this.phaser;
	}
	
	public void setPhaser(short phaser) {
		this.phaser = phaser;
	}
	
	public short getReverb() {
		return this.reverb;
	}
	
	public void setReverb(short reverb) {
		this.reverb = reverb;
	}
	
	public short getTremolo() {
		return this.tremolo;
	}
	
	public void setTremolo(short tremolo) {
		this.tremolo = tremolo;
	}
	
	public short getVolume() {
		return this.volume;
	}
	
	public void setVolume(short volume) {
		this.volume = volume;
	}
	
	public boolean isPercussionChannel(){
		return TGChannel.isPercussionChannel(this.getChannel());
	}
	
	public static boolean isPercussionChannel(int channel){
		return (channel == DEFAULT_PERCUSSION_CHANNEL);
	}
	
	public static void setPercussionChannel(TGChannel channel){
		channel.setChannel(DEFAULT_PERCUSSION_CHANNEL);
		channel.setEffectChannel(DEFAULT_PERCUSSION_CHANNEL);
	}
	
	public static TGChannel newPercussionChannel(TGFactory factory){
		TGChannel channel = factory.newChannel();
		TGChannel.setPercussionChannel(channel);
		return channel;
	}
	
	public TGChannel clone(TGFactory factory){
		TGChannel channel = factory.newChannel();
		copy(channel);
		return channel; 
	}
	
	public void copy(TGChannel channel){
		channel.setChannel(getChannel());
		channel.setEffectChannel(getEffectChannel());
		channel.setInstrument(getInstrument());
		channel.setVolume(getVolume());
		channel.setBalance(getBalance());
		channel.setChorus(getChorus());
		channel.setReverb(getReverb());
		channel.setPhaser(getPhaser());
		channel.setTremolo(getTremolo());
	}
}
