package org.herac.tuxguitar.android.view.browser;

import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.editor.event.TGDestroyEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventException;
import org.herac.tuxguitar.event.TGEventListener;

public class TGBrowserDestroyListener implements TGEventListener {
	
	private TGBrowserView browser;
	
	public TGBrowserDestroyListener(TGBrowserView browser) {
		this.browser = browser;
	}
	
	public void processEvent(TGEvent event) {
		if( TGDestroyEvent.EVENT_TYPE.equals(event.getEventType())) {
			try {
				this.browser.onDestroy();
			} catch (TGBrowserException e) {
				throw new TGEventException(e);
			}
		}
	}
}
