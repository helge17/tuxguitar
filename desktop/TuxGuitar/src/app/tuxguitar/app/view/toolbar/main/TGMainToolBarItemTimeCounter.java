package app.tuxguitar.app.view.toolbar.main;

/**
 * Time counter
 * a canvas with configurable font, dynamically updated by player
 * 
 * Warning if several time counters are defined in a toolBar: only 1 font configuration is stored
 * changing the font of one time counter will set this new font to all time counters after closing/reopening application
 */

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.app.system.icons.TGColorManager;
import app.tuxguitar.app.system.icons.TGColorManager.TGSkinnableColor;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.editor.event.TGRedrawEvent;
import app.tuxguitar.editor.util.TGSyncProcessLocked;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.chooser.UIFontChooser;
import app.tuxguitar.ui.chooser.UIFontChooserHandler;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.event.UIMouseDownListener;
import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.event.UIPaintEvent;
import app.tuxguitar.ui.event.UIPaintListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.ui.resource.UIFont;
import app.tuxguitar.ui.resource.UIFontModel;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UISize;
import app.tuxguitar.ui.widget.UICanvas;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class TGMainToolBarItemTimeCounter extends TGMainToolBarItem implements TGEventListener {
	private static final String COLOR_BACKGROUND = "widget.transport.backgroundColor";
	private static final String COLOR_FOREGROUND = "widget.transport.foregroundColor";
	private static final TGSkinnableColor[] SKINNABLE_COLORS = new TGSkinnableColor[] {
			new TGSkinnableColor(COLOR_BACKGROUND, new UIColorModel(0x00, 0x00, 0x00)),
			new TGSkinnableColor(COLOR_FOREGROUND, new UIColorModel(0xF0, 0xF0, 0xF0)), };
	private static final float TIMESTAMP_H_MARGIN = 8f;
	private static final float TIMESTAMP_V_MARGIN_FACTOR = 1.6f;
	// need to define a min size for timestamp canvas, or in Windows/SWT version the
	// .onPaint method of associated UIPaintListener is never called
	// these min size values are dummy values, > 0 and < any realistic value
	public static final float TIMESTAMP_MIN_WIDTH = 2f;
	public static final float TIMESTAMP_MIN_HEIGHT = 2f;

	private UICanvas timestampCanvas;
	private TGSyncProcessLocked redrawProcess;
	private UIPanel parentPanel;
	private UIWindow parentWindow;
	private UIColor backgroundColor;
	private UIColor foregroundColor;
	private UIFontModel fontModel;
	private UIFont font;
	private long timestamp = -1;
	private float yTimestamp;
	private boolean fontChanged;
	private TGContext context;

	public TGMainToolBarItemTimeCounter(TGMainToolBarItemConfig toolBarItemConfig, TGContext context, UIPanel parentPanel, UIWindow parentWindow) {
		super(toolBarItemConfig);
		this.parentPanel = parentPanel;
		this.parentWindow = parentWindow;
		this.context = context;

		UIFactory uiFactory = TGApplication.getInstance(context).getFactory();
		this.timestampCanvas = uiFactory.createCanvas(parentPanel, false);
		this.timestampCanvas.addPaintListener(new UIPaintListener() {
			public void onPaint(UIPaintEvent event) {
				TGMainToolBarItemTimeCounter.this.paintTimestamp(event.getPainter());
			}
		});
		this.timestampCanvas.addMouseDownListener(new UIMouseDownListener() {
			@Override
			public void onMouseDown(UIMouseEvent event) {
				if (!MidiPlayer.getInstance(context).isRunning()) {
					UIFactory uiFactory = TGApplication.getInstance(TGMainToolBarItemTimeCounter.this.context)
							.getFactory();
					UIFontChooser uiFontChooser = uiFactory.createFontChooser(parentWindow);
					uiFontChooser.setDefaultModel(TGMainToolBarItemTimeCounter.this.fontModel);
					uiFontChooser.choose(new UIFontChooserHandler() {
						public void onSelectFont(UIFontModel selection) {
							if (selection != null) {
								TGConfigManager.getInstance(TGMainToolBarItemTimeCounter.this.context)
										.setValue(TGConfigKeys.FONT_MAINTOOLBAR_TIMESTAMP, selection);
								TGMainToolBarItemTimeCounter.this.loadFont();
								TGMainToolBarItemTimeCounter.this.timestampCanvas.redraw();
							}
						}
					});
				}
			}
		});
		this.timestampCanvas.addDisposeListener(new UIDisposeListener() {
			@Override
			public void onDispose(UIDisposeEvent event) {
				if (TGMainToolBarItemTimeCounter.this.font != null) {
					TGMainToolBarItemTimeCounter.this.font.dispose();
				}
				TuxGuitar.getInstance().getEditorManager().removeRedrawListener(TGMainToolBarItemTimeCounter.this);
			}
		});

		TGColorManager tgColorManager = TGColorManager.getInstance(TGMainToolBarItemTimeCounter.this.context);
		tgColorManager.appendSkinnableColors(SKINNABLE_COLORS);
		this.loadFont();
		this.loadColors();

		this.createSyncProcesses();
		this.appendListeners();
	}

	@Override
	public UIControl getControl() {
		return this.timestampCanvas;
	}

	@Override
	public void setLayoutProperties(UITableLayout layout) {
		layout.set(this.timestampCanvas, UITableLayout.PACKED_WIDTH, TIMESTAMP_MIN_WIDTH);
		layout.set(this.timestampCanvas, UITableLayout.PACKED_HEIGHT, TIMESTAMP_MIN_HEIGHT);
	}

	private void createSyncProcesses() {
		this.redrawProcess = new TGSyncProcessLocked(TGMainToolBarItemTimeCounter.this.context, new Runnable() {
			public void run() {
				TGMainToolBarItemTimeCounter.this.timestampCanvas.redraw();
			}
		});
	}

	private void appendListeners() {
		TuxGuitar.getInstance().getEditorManager().addRedrawListener(this);
	}

	@Override
	public void processEvent(final TGEvent event) {
		if (TGRedrawEvent.EVENT_TYPE.equals(event.getEventType())) {
			int type = ((Integer) event.getAttribute(TGRedrawEvent.PROPERTY_REDRAW_MODE)).intValue();
			if (type == TGRedrawEvent.PLAYING_THREAD || type == TGRedrawEvent.PLAYING_NEW_BEAT) {
				MidiPlayer midiPlayer = MidiPlayer.getInstance(TGMainToolBarItemTimeCounter.this.context);
				if (midiPlayer.isRunning()) {
					long tMs = midiPlayer.getCurrentTimestamp();
					if (tMs / 100 != this.timestamp / 100) {
						this.redrawProcess.process();
						this.timestamp = tMs;
					}
				} else {
					this.redrawProcess.process();
				}
			}
		}
	}

	// called when skin changes
	@Override
	public void loadIcons(TGIconManager iconManager) {
		this.loadColors();
	}

	@Override
	public void loadProperties() {
		// TODO tooltiptexts
	}

	@Override
	public void update(TGContext context, boolean running) {
		this.timestampCanvas.redraw();
	}

	private void loadColors() {
		TGColorManager tgColorManager = TGColorManager.getInstance(TGMainToolBarItemTimeCounter.this.context);
		this.backgroundColor = tgColorManager.getColor(COLOR_BACKGROUND);
		this.foregroundColor = tgColorManager.getColor(COLOR_FOREGROUND);
	}

	private void loadFont() {
		if (this.font != null) {
			this.font.dispose();
		}
		UIFactory uiFactory = TGApplication.getInstance(TGMainToolBarItemTimeCounter.this.context).getFactory();
		this.fontModel = TGConfigManager.getInstance(TGMainToolBarItemTimeCounter.this.context)
				.getFontModelConfigValue(TGConfigKeys.FONT_MAINTOOLBAR_TIMESTAMP);
		this.font = uiFactory.createFont(this.fontModel);
		this.fontChanged = true;
	}

	private void paintTimestamp(UIPainter painter) {
		String time;

		MidiPlayer midiPlayer = MidiPlayer.getInstance(TGMainToolBarItemTimeCounter.this.context);
		if (midiPlayer.isRunning()) {
			long tMs = this.timestamp;
			time = String.format("%d:%02d:%02d.%01d", tMs / 3600000, (tMs / 60000) % 60, (tMs / 1000) % 60,
					(tMs / 100) % 10);
		} else {
			// don't display timestamp while not playing, it's meaningless (e.g. repeats not
			// considered, etc.)
			time = "-:--:--.-";
		}
		painter.setFont(this.font);
		float newWidth;
		float newHeight;
		UISize size = this.timestampCanvas.getBounds().getSize();
		// performance optimization: recalculate dimensions only if they change or were
		// never computed
		if (this.fontChanged || (size.getWidth() < TIMESTAMP_MIN_WIDTH + 2f)) {
			newWidth = painter.getFMWidth(time) + 2f * TIMESTAMP_H_MARGIN;
			newHeight = this.font.getHeight() * TIMESTAMP_V_MARGIN_FACTOR;
			boolean doLayout = false;
			boolean doWindowLayout = false;
			if (newWidth != size.getWidth()) {
				this.parentPanel.getLayout().set(this.timestampCanvas, UITableLayout.PACKED_WIDTH, newWidth);
				// warning, font may not be monospaced, avoid permanent layout
				if ((newWidth - size.getWidth()) > TIMESTAMP_H_MARGIN) {
					doLayout = true;
				}
			}
			if (Math.abs(newHeight - size.getHeight()) >= 2f) {
				this.parentPanel.getLayout().set(this.timestampCanvas, UITableLayout.PACKED_HEIGHT, newHeight);
				this.yTimestamp = painter.getFMMiddleLine() + newHeight / 2f;
				doWindowLayout = true;
			}
			if (doWindowLayout) {
				this.parentWindow.layout();
			} else if (doLayout) {
				this.parentPanel.layout();
			}
			this.fontChanged = false;
		} else {
			newWidth = size.getWidth();
			newHeight = size.getHeight();
		}
		if (this.backgroundColor.isDisposed() || this.foregroundColor.isDisposed()) {
			this.loadColors();
		}
		painter.setBackground(this.backgroundColor);
		painter.initPath(UIPainter.PATH_FILL);
		painter.addRectangle(0f, 0f, newWidth, newHeight);
		painter.closePath();
		painter.setForeground(this.foregroundColor);
		painter.drawString(time, TIMESTAMP_H_MARGIN, this.yTimestamp);
	}

}
