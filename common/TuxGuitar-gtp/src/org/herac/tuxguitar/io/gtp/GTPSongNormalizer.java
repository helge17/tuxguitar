package org.herac.tuxguitar.io.gtp;

import org.herac.tuxguitar.gm.GMChannelRoute;
import org.herac.tuxguitar.song.factory.TGFactory;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChannelParameter;
import org.herac.tuxguitar.song.models.TGSong;

public class GTPSongNormalizer {

	public static final String PERCUSSION_CHANNEL_INDEX = "9";
	
	private TGFactory factory;
	private TGSong song;
	
	public GTPSongNormalizer(TGFactory factory, TGSong song) {
		this.factory = factory;
		this.song = song;
	}
	
	public void process() {
		this.normalizeChannels();
	}
	
	private void normalizeChannels() {
		for(int i = 0; i < this.song.countChannels(); i ++) {
			TGChannel channel = this.song.getChannel(i);
			
			// Sometimes percussion channels don't have correctly setted percussion bank
			if(!channel.isPercussionChannel()) {
				for( int n = 0 ; n < channel.countParameters() ; n ++ ){
					TGChannelParameter channelParameter = channel.getParameter( n );
					if( channelParameter.getKey().equals(GMChannelRoute.PARAMETER_GM_CHANNEL_1) && PERCUSSION_CHANNEL_INDEX.equals(channelParameter.getValue())){
						channel.setBank(TGChannel.DEFAULT_PERCUSSION_BANK);
					}
				}
			}
			
			if( channel.isPercussionChannel() ) {
				this.ensurePercussionAttributes(channel);
			}
		}
	}
	
	private void ensurePercussionAttributes(TGChannel channel) {
		channel.setProgram((short) 0);
		channel.setBank(TGChannel.DEFAULT_PERCUSSION_BANK);
		
		this.ensureChannelParameter(channel, GMChannelRoute.PARAMETER_GM_CHANNEL_1, PERCUSSION_CHANNEL_INDEX);
		this.ensureChannelParameter(channel, GMChannelRoute.PARAMETER_GM_CHANNEL_2, PERCUSSION_CHANNEL_INDEX);
	}
	
	private void ensureChannelParameter(TGChannel channel, String key, String value) {
		boolean present = false;
		for( int i = 0 ; i < channel.countParameters() ; i ++ ){
			TGChannelParameter channelParameter = channel.getParameter( i );
			if( channelParameter.getKey().equals(key) ) {
				present = true;
				if( channelParameter.getValue() == null || !channelParameter.getValue().equals(value)) {
					channelParameter.setValue(value);
				}
			}
		}
		if(!present) {
			TGChannelParameter channelParam = this.factory.newChannelParameter();
			channelParam.setKey(key);
			channelParam.setValue(value);
			channel.addParameter(channelParam);
		}
	}
}
