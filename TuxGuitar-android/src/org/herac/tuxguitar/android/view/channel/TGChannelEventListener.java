package org.herac.tuxguitar.android.view.channel;

import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;

public class TGChannelEventListener implements TGEventListener {

	private TGChannelListView channelList;
	
	public TGChannelEventListener(TGChannelListView channelList) {
		this.channelList = channelList;
	}
	
	public void processEvent(TGEvent event) {
		if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
			if( type == TGUpdateEvent.SELECTION ){
				this.channelList.fireUpdateProcess();
			}
		}
	}
}
