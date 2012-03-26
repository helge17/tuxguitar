package org.herac.tuxguitar.gm;

public class GMChannelRoute {
	
	public static final String PARAMETER_GM_CHANNEL_1 = "gm-channel-1";
	public static final String PARAMETER_GM_CHANNEL_2 = "gm-channel-2";
	
	public static final int NULL_VALUE = -1;
	
	private int channelId;
	private int channel1;
	private int channel2;
	
	public GMChannelRoute(int channelId){
		this.channelId = channelId;
	}
	
	public int getChannelId() {
		return this.channelId;
	}
	
	public int getChannel1() {
		return this.channel1;
	}

	public void setChannel1(int channel1) {
		this.channel1 = channel1;
	}

	public int getChannel2() {
		return this.channel2;
	}

	public void setChannel2(int channel2) {
		this.channel2 = channel2;
	}
}
