package org.herac.tuxguitar.song.models;

import org.herac.tuxguitar.song.factory.TGFactory;

public abstract class TGChannelParameter {
	
	private String key;
	private String value;
	
	public TGChannelParameter(){
		super();
	}
	
	public String getKey() {
		return this.key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public void copy(TGChannelParameter channelParameter){
		channelParameter.setKey(getKey());
		channelParameter.setValue(getValue());
	}
	
	public TGChannelParameter clone(TGFactory factory){
		TGChannelParameter tgChannelParameter = factory.newChannelParameter();
		copy(tgChannelParameter);
		return tgChannelParameter;
	}
}
