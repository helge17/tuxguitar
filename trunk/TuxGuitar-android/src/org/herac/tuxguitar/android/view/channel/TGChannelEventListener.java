package org.herac.tuxguitar.android.view.channel;

import org.herac.tuxguitar.android.editor.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGChannelEventListener implements TGEventListener {

	private TGChannelListView channelList;
	
	public TGChannelEventListener(TGChannelListView channelList) {
		this.channelList = channelList;
	}
	
	public void processUpdateItems() {
		this.channelList.updateItems();
	}
	
	public void processEvent(TGEvent event) {
		if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
			if( type == TGUpdateEvent.SELECTION ){
				TGSynchronizer.getInstance(this.channelList.findContext()).executeLater(new Runnable() {
					public void run() throws TGException {
						processUpdateItems();
					}
				});
			}
		}
	}
}
