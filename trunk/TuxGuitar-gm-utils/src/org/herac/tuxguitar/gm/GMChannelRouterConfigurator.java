package org.herac.tuxguitar.gm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChannelParameter;

public class GMChannelRouterConfigurator {
	
	private GMChannelRouter router;
	
	public GMChannelRouterConfigurator(GMChannelRouter router){
		this.router = router;
	}
	
	public void configureRouter(Iterator<TGChannel> iterator) {
		List<TGChannel> channels = new ArrayList<TGChannel>();
		while(iterator.hasNext()) {
			channels.add(iterator.next());
		}
		this.configureRouter(channels);
	}
	
	public void configureRouter(List<TGChannel> channels) {
		List<GMChannelRoute> routes = this.findCurrentRoutes(channels);
		
		this.router.resetRoutes();
		for(TGChannel channel : channels) {
			this.router.configureRoutes(this.findOrCreateCurrentRoute(routes, channel), channel.isPercussionChannel());
		}
		
		for(TGChannel channel : channels) {
			int channel1 = this.getIntegerChannelParameter(channel, GMChannelRoute.PARAMETER_GM_CHANNEL_1, GMChannelRoute.NULL_VALUE);
			int channel2 = this.getIntegerChannelParameter(channel, GMChannelRoute.PARAMETER_GM_CHANNEL_2, GMChannelRoute.NULL_VALUE);
			if( channel1 >= 0 ) {
				GMChannelRoute route = this.router.getRoute(channel.getChannelId());
				if( route == null ) {
					route = new GMChannelRoute(channel.getChannelId());
				}
				route.setChannel1(channel1);
				route.setChannel2(channel2);
				
				this.router.configureRoutes(route, channel.isPercussionChannel());
			}
		}
	}
	
	private List<GMChannelRoute> findCurrentRoutes(List<TGChannel> channels) {
		List<GMChannelRoute> routes = new ArrayList<GMChannelRoute>();
		for(TGChannel channel : channels) {
			GMChannelRoute route = this.router.getRoute(channel.getChannelId());
			if( route != null ) {
				routes.add(route);
			}
		}
		return routes;
	}
	
	private GMChannelRoute findCurrentRoute(List<GMChannelRoute> routes, TGChannel channel) {
		for(GMChannelRoute route : routes) {
			if( route.getChannelId() == channel.getChannelId() ){
				return route;
			}
		}
		return null;
	}
	
	private GMChannelRoute findOrCreateCurrentRoute(List<GMChannelRoute> routes, TGChannel channel) {
		GMChannelRoute cmChannelRoute = findCurrentRoute(routes, channel);
		if( cmChannelRoute == null ) {
			cmChannelRoute = new GMChannelRoute(channel.getChannelId());
		}
		return cmChannelRoute;
	}
	
	private int getIntegerChannelParameter( TGChannel tgChannel, String key , int nullValue){
		TGChannelParameter tgChannelParameter = findChannelParameter(tgChannel, key);
		if( tgChannelParameter != null && tgChannelParameter.getValue() != null ){
			return Integer.parseInt( tgChannelParameter.getValue() );
		}
		return nullValue;
	}
	
	private TGChannelParameter findChannelParameter( TGChannel tgChannel, String key ){
		Iterator<TGChannelParameter> it = tgChannel.getParameters();
		while( it.hasNext() ){
			TGChannelParameter parameter = (TGChannelParameter)it.next();
			if( parameter.getKey().equals( key ) ){
				return parameter;
			}
		}
		return null;
	}
}
