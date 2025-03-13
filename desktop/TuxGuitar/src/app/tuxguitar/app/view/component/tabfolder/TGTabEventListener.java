package app.tuxguitar.app.view.component.tabfolder;

import app.tuxguitar.app.view.component.tab.TGControl;
import app.tuxguitar.editor.event.TGRedrawEvent;
import app.tuxguitar.editor.util.TGSyncProcessLocked;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.util.TGContext;

public class TGTabEventListener implements TGEventListener {

	private TGContext context;
	private TGSyncProcessLocked redrawProcess;
	private TGSyncProcessLocked redrawPlayModeProcess;

	public TGTabEventListener(TGContext context) {
		this.context = context;
		this.createSyncProcesses();
	}

	public TGControl findTabControl() {
		return TGTabFolder.getInstance(this.context).findSelectedControl();
	}

	public void redraw() {
		TGControl control = findTabControl();
		if( control != null ) {
			control.redraw();
		}
	}

	public void redrawPlayingMode() {
		TGControl control = findTabControl();
		if( control != null ) {
			control.redrawPlayingMode();
		}
	}

	public void createSyncProcesses() {
		this.redrawProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				redraw();
			}
		});

		this.redrawPlayModeProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				redrawPlayingMode();
			}
		});
	}

	public void processRedrawEvent(final TGEvent event) {
		int type = ((Integer)event.getAttribute(TGRedrawEvent.PROPERTY_REDRAW_MODE)).intValue();
		if( type == TGRedrawEvent.NORMAL ){
			this.redrawProcess.process();
		}else if( type == TGRedrawEvent.PLAYING_NEW_BEAT ){
			this.redrawPlayModeProcess.process();
		}
	}

	public void processEvent(final TGEvent event) {
		if( TGRedrawEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processRedrawEvent(event);
		}
	}
}
