package app.tuxguitar.app.view.component.tabfolder;

import app.tuxguitar.app.view.component.tab.TGControl;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.editor.event.TGRedrawEvent;
import app.tuxguitar.editor.util.TGSyncProcessLocked;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.graphics.control.TGLayout;
import app.tuxguitar.player.base.MidiPlayerEvent;
import app.tuxguitar.util.TGContext;

public class TGTabEventListener implements TGEventListener {

	private TGContext context;
	private TGSyncProcessLocked redrawProcess;
	private TGSyncProcessLocked redrawPlayModeProcess;
	private boolean continuousScroll;

	public TGTabEventListener(TGContext context) {
		this.context = context;
		this.getConfig();
		this.createSyncProcesses();
	}

	private void getConfig() {
		this.continuousScroll = (TablatureEditor.getInstance(this.context).getTablature().getViewLayout().getStyle() & TGLayout.CONTINUOUS_SCROLL) != 0;
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

	private void processRedrawEvent(final TGEvent event) {
		int type = ((Integer)event.getAttribute(TGRedrawEvent.PROPERTY_REDRAW_MODE)).intValue();
		if( type == TGRedrawEvent.NORMAL ){
			this.redrawProcess.process();
		}
		else if( (type == TGRedrawEvent.PLAYING_NEW_BEAT)
				|| (this.continuousScroll && type == TGRedrawEvent.PLAYING_THREAD)){
			this.redrawPlayModeProcess.process();
		}
	}

	private void processMidiPlayerEvent(final TGEvent event) {
		int type = ((Integer)event.getAttribute(MidiPlayerEvent.PROPERTY_NOTIFICATION_TYPE)).intValue();
		if( type == MidiPlayerEvent.NOTIFY_STARTED ){
			// continuous/discrete scrolling may have changed, update
			this.getConfig();
		}
	}

	public void processEvent(final TGEvent event) {
		if( TGRedrawEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processRedrawEvent(event);
		}
		else if (MidiPlayerEvent.EVENT_TYPE.equals(event.getEventType())) {
			this.processMidiPlayerEvent(event);
		}
	}
}
