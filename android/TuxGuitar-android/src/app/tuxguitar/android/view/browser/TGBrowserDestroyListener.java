package app.tuxguitar.android.view.browser;

import app.tuxguitar.android.browser.model.TGBrowserException;
import app.tuxguitar.editor.event.TGDestroyEvent;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventException;
import app.tuxguitar.event.TGEventListener;

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
