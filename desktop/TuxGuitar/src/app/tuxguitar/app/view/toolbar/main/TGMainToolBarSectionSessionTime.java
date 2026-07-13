package app.tuxguitar.app.view.toolbar.main;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.editor.event.TGRedrawEvent;
import app.tuxguitar.editor.util.TGSyncProcessLocked;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerEvent;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.resource.UIFont;
import app.tuxguitar.ui.resource.UIFontModel;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.util.TGContext;

public class TGMainToolBarSectionSessionTime extends TGMainToolBarSection implements TGEventListener {

	private static final int STATE_STOPPED = 0;
	private static final int STATE_RUNNING = 1;
	private static final int STATE_PAUSED = 2;

	private UILabel sessionTimeLabel;
	private TGSyncProcessLocked redrawProcess;
	private UIPanel parentPanel;
	private UIFontModel fontModel;
	private UIFont font;

	private int state = STATE_STOPPED;
	private long sessionStartTime = 0;
	private long accumulatedTime = 0;

	public TGMainToolBarSectionSessionTime(TGContext context, UIPanel parentPanel) {
		super(context);
		UIFactory uiFactory = TGApplication.getInstance(this.getContext()).getFactory();

		this.parentPanel = parentPanel;
		this.sessionTimeLabel = uiFactory.createLabel(parentPanel);
		this.sessionTimeLabel.setToolTipText(TuxGuitar.getProperty("toolbar.playingSessionTime"));
		this.controls.add(this.sessionTimeLabel);

		this.loadFont();

		this.sessionTimeLabel.addDisposeListener(new UIDisposeListener() {
			@Override
			public void onDispose(UIDisposeEvent event) {
				if (TGMainToolBarSectionSessionTime.this.font != null) {
					TGMainToolBarSectionSessionTime.this.font.dispose();
				}
				TuxGuitar.getInstance().getEditorManager().removeRedrawListener(TGMainToolBarSectionSessionTime.this);
				MidiPlayer.getInstance(TGMainToolBarSectionSessionTime.this.getContext()).removeListener(TGMainToolBarSectionSessionTime.this);
			}
		});

		this.createSyncProcesses();
		this.appendListeners();
		loadProperties();
	}

	private void loadFont() {
		if (this.font != null) {
			this.font.dispose();
		}
		UIFactory uiFactory = TGApplication.getInstance(this.getContext()).getFactory();
		this.fontModel = TGConfigManager.getInstance(this.getContext())
				.getFontModelConfigValue(TGConfigKeys.FONT_MAINTOOLBAR_TIMESTAMP);
		this.font = uiFactory.createFont(this.fontModel);
		this.sessionTimeLabel.setFont(this.font);
	}

	private void createSyncProcesses() {
		this.redrawProcess = new TGSyncProcessLocked(getContext(), new Runnable() {
			public void run() {
				TGMainToolBarSectionSessionTime.this.updateItems();
			}
		});
	}

	private void appendListeners() {
		TuxGuitar.getInstance().getEditorManager().addRedrawListener(this);
		MidiPlayer.getInstance(getContext()).addListener(this);
	}

	@Override
	public void processEvent(final TGEvent event) {
		if (MidiPlayerEvent.EVENT_TYPE.equals(event.getEventType())) {
			int type = ((Integer) event.getAttribute(MidiPlayerEvent.PROPERTY_NOTIFICATION_TYPE)).intValue();
			if (type == MidiPlayerEvent.NOTIFY_STARTED) {
				if (state == STATE_STOPPED) {
					this.sessionStartTime = System.currentTimeMillis();
					this.accumulatedTime = 0;
					this.state = STATE_RUNNING;
				} else if (state == STATE_PAUSED) {
					this.sessionStartTime = System.currentTimeMillis();
					this.state = STATE_RUNNING;
				}
			} else if (type == MidiPlayerEvent.NOTIFY_STOPPED) {
				boolean paused = ((Boolean) event.getAttribute(MidiPlayerEvent.PROPERTY_PAUSED)).booleanValue();
				if (paused) {
					this.accumulatedTime = this.getCurrentElapsedTime();
					this.state = STATE_PAUSED;
				} else {
					this.state = STATE_STOPPED;
					this.accumulatedTime = 0;
				}
			}
		} else if (TGRedrawEvent.EVENT_TYPE.equals(event.getEventType())) {
			int type = ((Integer) event.getAttribute(TGRedrawEvent.PROPERTY_REDRAW_MODE)).intValue();
			if (type == TGRedrawEvent.PLAYING_THREAD || type == TGRedrawEvent.PLAYING_NEW_BEAT) {
				if (state == STATE_RUNNING) {
					this.redrawProcess.process();
				}
			}
		}
	}

	private long getCurrentElapsedTime() {
		if (state == STATE_RUNNING) {
			return this.accumulatedTime + (System.currentTimeMillis() - this.sessionStartTime);
		}
		return this.accumulatedTime;
	}

	@Override
	public void loadProperties() {
		this.updateLabel("0:00:00");
	}

	@Override
	public void updateItems() {
		String time;
		if (this.state == STATE_STOPPED) {
			time = "0:00:00";
		} else {
			long tMs = this.getCurrentElapsedTime();
			time = String.format("%d:%02d:%02d", tMs / 3600000, (tMs / 60000) % 60, (tMs / 1000) % 60);
		}
		this.updateLabel(time);
	}

	private void updateLabel(String text) {
		this.sessionTimeLabel.setText(text);
	}

	@Override
	public void addToolBarItem(TGMainToolBarItemConfig toolBarItemConfig) {
		// nothing to do, content of this section is not configurable
	}
}