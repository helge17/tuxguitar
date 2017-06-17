package org.herac.tuxguitar.gm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GMChannelRouter {
	
	public static final short MAX_CHANNELS = 16;
	public static final short PERCUSSION_CHANNEL = 9;
	
	private List<GMChannelRoute> midiChannels;
	
	public GMChannelRouter(){
		this.midiChannels = new ArrayList<GMChannelRoute>();
	}
	
	public void resetRoutes() {
		this.midiChannels.clear();
	}
	
	public void removeRoute(GMChannelRoute route) {
		GMChannelRoute existingRoute = this.getRoute(route.getChannelId());
		if( existingRoute != null ){
			this.midiChannels.remove(existingRoute);
		}
	}
	
	public GMChannelRoute getRoute(int channelId) {
		Iterator<GMChannelRoute> channelIt = this.midiChannels.iterator();
		while( channelIt.hasNext() ){
			GMChannelRoute midiChannel = (GMChannelRoute) channelIt.next();
			if( midiChannel.getChannelId() == channelId ){
				return midiChannel;
			}
		}
		return null;
	}
	
	public void configureRoutes(GMChannelRoute route, boolean percussionChannel) {
		List<GMChannelRoute> conflictingRoutes = null;
		
		this.removeRoute(route);
		
		// Allways channel 9 for percussions
		if( percussionChannel ){
			route.setChannel1(PERCUSSION_CHANNEL);
			route.setChannel2(PERCUSSION_CHANNEL);
		}
		
		else {
			// Use custom routes 
			if( route.getChannel1() >= 0 ){
				if( route.getChannel2() < 0 ){
					route.setChannel2( route.getChannel1() );
				}
				conflictingRoutes = findConflictingRoutes(route);
			}
			
			// Add default routes
			else {
				List<Integer> freeChannels = getFreeChannels();
				route.setChannel1(( freeChannels.size() > 0 ? ((Integer)freeChannels.get(0)).intValue() : GMChannelRoute.NULL_VALUE ) );
				route.setChannel2(( freeChannels.size() > 1 ? ((Integer)freeChannels.get(1)).intValue() : route.getChannel1() ) );
			}
		}
		
		this.midiChannels.add( route );
		
		// Reconfigure conflicting routes
		if( conflictingRoutes != null ){
			for(GMChannelRoute conflictingRoute : conflictingRoutes) {
				conflictingRoute.setChannel1(GMChannelRoute.NULL_VALUE);
				conflictingRoute.setChannel2(GMChannelRoute.NULL_VALUE);
				configureRoutes(conflictingRoute, false);
			}
		}
		
		// Reconfigure orphan routes
		List<GMChannelRoute> orphanRoutes = findOrphanRoutes();
		for(GMChannelRoute orphanRoute : orphanRoutes) {
			if(!this.getFreeChannels().isEmpty()) {
				this.configureRoutes(orphanRoute, false);
			}
		}
	}
	
	public List<GMChannelRoute> findOrphanRoutes() {
		List<GMChannelRoute> routes = new ArrayList<GMChannelRoute>();
		for(GMChannelRoute route : this.midiChannels) {
			if( route.getChannel1() == GMChannelRoute.NULL_VALUE || route.getChannel2() == GMChannelRoute.NULL_VALUE ) {
				routes.add(route);
			}
		}
		return routes;
	}
	
	public List<GMChannelRoute> findConflictingRoutes(GMChannelRoute gmChannelRoute) {
		List<GMChannelRoute> routes = new ArrayList<GMChannelRoute>();
		for(GMChannelRoute route : this.midiChannels) {
			if(!route.equals(gmChannelRoute) ){
				if( route.getChannel1() == gmChannelRoute.getChannel1() || 
					route.getChannel1() == gmChannelRoute.getChannel2() ||
					route.getChannel2() == gmChannelRoute.getChannel1() || 
					route.getChannel2() == gmChannelRoute.getChannel2() ){
					
					routes.add( route );
				}
			}
		}
		return routes;
	}
	
	public List<Integer> getFreeChannels() {
		return getFreeChannels(null);
	}
	
	public List<Integer> getFreeChannels(GMChannelRoute forRoute) {
		List<Integer> freeChannels = new ArrayList<Integer>();
		
		for( int ch = 0 ; ch < MAX_CHANNELS ; ch ++ ){
			if( ch != PERCUSSION_CHANNEL ){
				boolean isFreeChannel = true;
				
				Iterator<GMChannelRoute> channelIt = this.midiChannels.iterator();
				while( channelIt.hasNext() ){
					GMChannelRoute route = (GMChannelRoute) channelIt.next();
					if( forRoute == null || !forRoute.equals( route ) ){
						if( route.getChannel1() == ch || route.getChannel2() == ch){
							isFreeChannel = false;
						}
					}
				}
				
				if( isFreeChannel ){
					freeChannels.add(new Integer(ch));
				}
			}
		}
		return freeChannels;
	}
}
