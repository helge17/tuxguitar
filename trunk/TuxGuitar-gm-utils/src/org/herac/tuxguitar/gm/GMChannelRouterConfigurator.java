package org.herac.tuxguitar.gm;

import java.util.Iterator;

import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChannelParameter;
import org.herac.tuxguitar.song.models.TGSong;

public class GMChannelRouterConfigurator {
	
	private GMChannelRouter router;
	
	public GMChannelRouterConfigurator(GMChannelRouter router){
		this.router = router;
	}
	
	public void configureRouter(TGSong tgSong){
		this.router.resetRoutes();
		
		Iterator tgChannels = tgSong.getChannels();
		while( tgChannels.hasNext() ){
			TGChannel tgChannel = (TGChannel) tgChannels.next();
			
			GMChannelRoute cmChannelRoute = new GMChannelRoute(tgChannel.getChannelId());
			cmChannelRoute.setChannel1(getIntegerChannelParameter(tgChannel, GMChannelRoute.PARAMETER_GM_CHANNEL_1, GMChannelRoute.NULL_VALUE));
			cmChannelRoute.setChannel2(getIntegerChannelParameter(tgChannel, GMChannelRoute.PARAMETER_GM_CHANNEL_2, GMChannelRoute.NULL_VALUE));
			
			this.router.configureRoutes(cmChannelRoute, tgChannel.isPercussionChannel());
		}
	}
	
	private int getIntegerChannelParameter( TGChannel tgChannel, String key , int nullValue){
		TGChannelParameter tgChannelParameter = findChannelParameter(tgChannel, key);
		if( tgChannelParameter != null && tgChannelParameter.getValue() != null ){
			return Integer.parseInt( tgChannelParameter.getValue() );
		}
		return nullValue;
	}
	
	private TGChannelParameter findChannelParameter( TGChannel tgChannel, String key ){
		Iterator it = tgChannel.getParameters();
		while( it.hasNext() ){
			TGChannelParameter parameter = (TGChannelParameter)it.next();
			if( parameter.getKey().equals( key ) ){
				return parameter;
			}
		}
		return null;
	}
}
