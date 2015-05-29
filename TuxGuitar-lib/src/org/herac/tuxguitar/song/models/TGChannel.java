package org.herac.tuxguitar.song.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.song.factory.TGFactory;

public abstract class TGChannel {
	
	public static final short DEFAULT_PERCUSSION_CHANNEL = 9;
	public static final short DEFAULT_PERCUSSION_PROGRAM = 0;
	public static final short DEFAULT_PERCUSSION_BANK = 128;
	
	public static final short DEFAULT_BANK = 0;
	public static final short DEFAULT_PROGRAM = 25;
	public static final short DEFAULT_VOLUME = 127;
	public static final short DEFAULT_BALANCE = 64;
	public static final short DEFAULT_CHORUS = 0;
	public static final short DEFAULT_REVERB = 0;
	public static final short DEFAULT_PHASER = 0;
	public static final short DEFAULT_TREMOLO = 0;
	
	private int channelId;
	private short bank;
	private short program;
	private short volume;
	private short balance;
	private short chorus;
	private short reverb;
	private short phaser;
	private short tremolo;
	private String name;
	private List<TGChannelParameter> parameters;
	
	public TGChannel() {
		this.channelId = 0;
		this.bank = DEFAULT_BANK;
		this.program = DEFAULT_PROGRAM;
		this.volume = DEFAULT_VOLUME;
		this.balance = DEFAULT_BALANCE;
		this.chorus = DEFAULT_CHORUS;
		this.reverb = DEFAULT_REVERB;
		this.phaser = DEFAULT_PHASER;
		this.tremolo = DEFAULT_TREMOLO;
		this.name = new String();
		this.parameters = new ArrayList<TGChannelParameter>();
	}
	
	public int getChannelId() {
		return this.channelId;
	}
	
	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}
	
	public short getBalance() {
		return this.balance;
	}
	
	public void setBalance(short balance) {
		this.balance = balance;
	}
	
	public short getChorus() {
		return this.chorus;
	}
	
	public void setChorus(short chorus) {
		this.chorus = chorus;
	}
	
	public short getBank() {
		return this.bank;
	}
	
	public void setBank(short bank) {
		this.bank = bank;
	}
	
	public short getProgram() {
		return this.program;
	}
	
	public void setProgram(short program) {
		this.program = program;
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
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Iterator<TGChannelParameter> getParameters() {
		return this.parameters.iterator();
	}
	
	public void addParameter(TGChannelParameter parameter){
		this.parameters.add(parameter);
	}
	
	public void addParameter(int index,TGChannelParameter parameter){
		this.parameters.add(index, parameter);
	}
	
	public TGChannelParameter getParameter(int index){
		if(index >= 0 && index < countParameters()){
			return this.parameters.get(index);
		}
		return null;
	}
	
	public void removeParameter(int index){
		this.parameters.remove(index);
	}
	
	public int countParameters(){
		return this.parameters.size();
	}
	
	public boolean isPercussionChannel(){
		return (this.getBank() == DEFAULT_PERCUSSION_BANK);
	}
	
	public TGChannel clone(TGFactory factory){
		TGChannel tgChannel = factory.newChannel();
		tgChannel.copyFrom(factory, this);
		return tgChannel; 
	}
	
	public void copyFrom(TGFactory factory, TGChannel channel){
		this.setChannelId(channel.getChannelId());
		this.setBank(channel.getBank());
		this.setProgram(channel.getProgram());
		this.setVolume(channel.getVolume());
		this.setBalance(channel.getBalance());
		this.setChorus(channel.getChorus());
		this.setReverb(channel.getReverb());
		this.setPhaser(channel.getPhaser());
		this.setTremolo(channel.getTremolo());
		this.setName(channel.getName());
		
		this.parameters.clear();
		for(int i = 0; i < channel.countParameters(); i ++){
			this.addParameter(channel.getParameter(i).clone(factory));
		}
	}
}
