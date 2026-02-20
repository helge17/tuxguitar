package app.tuxguitar.app.view.dialog.transport;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.TGActionProcessorListener;
import app.tuxguitar.app.action.impl.measure.TGGoFirstMeasureAction;
import app.tuxguitar.app.action.impl.measure.TGGoLastMeasureAction;
import app.tuxguitar.app.action.impl.measure.TGGoNextMeasureAction;
import app.tuxguitar.app.action.impl.measure.TGGoPreviousMeasureAction;
import app.tuxguitar.app.action.impl.transport.TGOpenTransportModeDialogAction;
import app.tuxguitar.app.action.impl.transport.TGTransportPlayPauseAction;
import app.tuxguitar.app.action.impl.transport.TGTransportStopAction;
import app.tuxguitar.app.system.icons.TGColorManager;
import app.tuxguitar.app.system.icons.TGColorManager.TGSkinnableColor;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.system.icons.TGSkinEvent;
import app.tuxguitar.app.system.language.TGLanguageEvent;
import app.tuxguitar.app.transport.TGTransport;
import app.tuxguitar.app.transport.TGTransportCache;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.action.transport.TGTransportCountDownAction;
import app.tuxguitar.editor.action.transport.TGTransportMetronomeAction;
import app.tuxguitar.editor.event.TGRedrawEvent;
import app.tuxguitar.editor.event.TGUpdateEvent;
import app.tuxguitar.editor.util.TGProcess;
import app.tuxguitar.editor.util.TGSyncProcess;
import app.tuxguitar.editor.util.TGSyncProcessLocked;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventListener;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIDisposeEvent;
import app.tuxguitar.ui.event.UIDisposeListener;
import app.tuxguitar.ui.event.UIMouseDownListener;
import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.event.UIMouseMoveListener;
import app.tuxguitar.ui.event.UIMouseUpListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UIColor;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.ui.resource.UICursor;
import app.tuxguitar.ui.resource.UIFont;
import app.tuxguitar.ui.toolbar.UIToolActionItem;
import app.tuxguitar.ui.toolbar.UIToolBar;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UILayoutContainer;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIProgressBar;
import app.tuxguitar.ui.widget.UISpinner;
import app.tuxguitar.ui.widget.UIToggleButton;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGTransportDialog implements TGEventListener {

	private static final int PLAY_MODE_DELAY = 250;

	private static final String COLOR_BACKGROUND = "widget.transport.backgroundColor";
	private static final String COLOR_FOREGROUND = "widget.transport.foregroundColor";

	private static final TGSkinnableColor[] SKINNABLE_COLORS = new TGSkinnableColor[] {
			new TGSkinnableColor(COLOR_BACKGROUND, new UIColorModel(0x00, 0x00, 0x00)),
			new TGSkinnableColor(COLOR_FOREGROUND, new UIColorModel(0x00, 0x00, 0xFF)), };

	private TGContext context;
	private UIWindow dialog;
	private UILabel label;
	private UIProgressBar tickProgress;
	private UIToggleButton metronome;
	private UIToggleButton countInToggle;
	private UISpinner countInTicks;
	private UIButton mode;
	private UIToolBar toolBar;
	private UIToolActionItem first;
	private UIToolActionItem last;
	private UIToolActionItem previous;
	private UIToolActionItem next;
	private UIToolActionItem stop;
	private UIToolActionItem play;
	private TGProcess loadPropertiesProcess;
	private TGProcess loadIconsProcess;
	private TGProcess updateItemsProcess;
	private TGProcess redrawPlayModeProcess;
	private boolean editingTickScale;
	private long redrawTime;
	private boolean isRunning;
	private TreeMap<Long, TGMeasureHeader> headerMap;

	public TGTransportDialog(TGContext context) {
		this.context = context;
		this.createSyncProcesses();
		this.headerMap = new TreeMap<>();
	}

	public void show() {
		UIFactory factory = this.getUIFactory();

		this.dialog = factory.createWindow(TGWindow.getInstance(this.context).getWindow(), false, false);
		this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
		this.dialog.setLayout(new UITableLayout());
		this.dialog.setText(TuxGuitar.getProperty("transport"));
		this.initComposites();
		this.initToolBar();
		this.redrawProgress();

		this.addListeners();
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				removeListeners();
				TuxGuitar.getInstance().updateCache(true);
			}
		});
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	public void addListeners() {
		TuxGuitar.getInstance().getSkinManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getEditorManager().addRedrawListener(this);
		TuxGuitar.getInstance().getEditorManager().addUpdateListener(this);
	}

	public void removeListeners() {
		TuxGuitar.getInstance().getSkinManager().removeLoader(this);
		TuxGuitar.getInstance().getLanguageManager().removeLoader(this);
		TuxGuitar.getInstance().getEditorManager().removeRedrawListener(this);
		TuxGuitar.getInstance().getEditorManager().removeUpdateListener(this);
	}

	private void initComposites() {
		UIPanel composite = getUIFactory().createPanel(this.dialog, true);
		composite.setLayout(new UITableLayout(0f));

		UITableLayout parentLayout = (UITableLayout) this.dialog.getLayout();
		parentLayout.set(composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.initOptions(composite);
		this.initProgress(composite);
	}

	private void initOptions(UILayoutContainer parent) {
		UIFactory factory = this.getUIFactory();
		UITableLayout parentLayout = (UITableLayout) parent.getLayout();
		UITableLayout compositeLayout = new UITableLayout();
		UIPanel composite = factory.createPanel(parent, false);
		composite.setLayout(compositeLayout);
		parentLayout.set(composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true, null, null,
				null, null, 0f);

		this.metronome = factory.createToggleButton(composite);
		this.metronome
				.addSelectionListener(new TGActionProcessorListener(this.context, TGTransportMetronomeAction.NAME));
		compositeLayout.set(this.metronome, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true);
		compositeLayout.set(this.metronome, UITableLayout.MINIMUM_PACKED_WIDTH, 100f);

		this.mode = factory.createButton(composite);
		this.mode.addSelectionListener(
				new TGActionProcessorListener(this.context, TGOpenTransportModeDialogAction.NAME));
		compositeLayout.set(this.mode, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true);

		MidiPlayer player = MidiPlayer.getInstance(this.context);

		this.countInToggle = factory.createToggleButton(composite);
		this.countInToggle
				.addSelectionListener(new TGActionProcessorListener(this.context, TGTransportCountDownAction.NAME));

		this.countInTicks = factory.createSpinner(composite);
		this.countInTicks.setMinimum(1);
		this.countInTicks.addSelectionListener(new UISelectionListener() {
			@Override
			public void onSelect(UISelectionEvent event) {
				player.getCountDown().setTickCount(countInTicks.getValue());
			}
		});

		compositeLayout.set(this.countInToggle, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true);
		compositeLayout.set(countInTicks, 2, 2, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);

		this.loadOptionIcons();
	}

	private void initProgress(UILayoutContainer parent) {
		UIPanel composite = getUIFactory().createPanel(parent, false);
		composite.setLayout(new UITableLayout());

		UITableLayout parentLayout = (UITableLayout) parent.getLayout();
		parentLayout.set(composite, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, null, null,
				null, null, 0f);

		initLabel(composite);
		initScale(composite);
	}

	private void initLabel(UILayoutContainer parent) {
		final UIFactory factory = this.getUIFactory();
		final UIFont font = factory.createFont("Minisystem", 24, false, false);

		TGColorManager tgColorManager = TGColorManager.getInstance(this.context);
		tgColorManager.appendSkinnableColors(SKINNABLE_COLORS);
		UIColor background = tgColorManager.getColor(COLOR_BACKGROUND);
		UIColor foreground = tgColorManager.getColor(COLOR_FOREGROUND);

		UITableLayout parentLayout = (UITableLayout) parent.getLayout();

		UITableLayout labelContainerLayout = new UITableLayout();
		UIPanel labelContainer = factory.createPanel(parent, false);
		labelContainer.setLayout(labelContainerLayout);
		labelContainer.setBgColor(background);
		parentLayout.set(labelContainer, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.label = factory.createLabel(labelContainer);
		this.label.setBgColor(background);
		this.label.setFgColor(foreground);
		this.label.setFont(font);

		labelContainer.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				font.dispose();
			}
		});
		labelContainerLayout.set(this.label, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
	}

	private void initScale(UILayoutContainer parent) {
		UIFactory factory = this.getUIFactory();

		this.tickProgress = factory.createProgressBar(parent);
		this.tickProgress.setCursor(UICursor.HAND);
		this.tickProgress.setValue((int) TGDuration.QUARTER_TIME);
		this.tickProgress.addMouseDownListener(new UIMouseDownListener() {
			public void onMouseDown(UIMouseEvent event) {
				TGTransportDialog.this.setEditingTickScale(true);
				TGTransportDialog.this.updateProgressBar(event.getPosition().getX());
			}
		});
		this.tickProgress.addMouseUpListener(new UIMouseUpListener() {
			public void onMouseUp(final UIMouseEvent event) {
				new TGSyncProcessLocked(TGTransportDialog.this.context, new Runnable() {
					public void run() {
						TGTransportDialog.this.gotoMeasure(getSongManager().getMeasureHeaderAt(
								getDocumentManager().getSong(), TGTransportDialog.this.tickProgress.getValue()), true);
						TGTransportDialog.this.setEditingTickScale(false);
					}
				}).process();
			}
		});
		this.tickProgress.addMouseMoveListener(new UIMouseMoveListener() {
			public void onMouseMove(UIMouseEvent event) {
				TGTransportDialog.this.updateProgressBar(event.getPosition().getX());
			}
		});

		UITableLayout parentLayout = (UITableLayout) parent.getLayout();
		parentLayout.set(this.tickProgress, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		parentLayout.set(this.tickProgress, UITableLayout.PACKED_HEIGHT, 10f);
	}

	private void updateProgressBar(float x) {
		if (this.isEditingTickScale()) {
			float width = this.tickProgress.getBounds().getWidth();
			float selection = (this.tickProgress.getMinimum()
					+ ((x * (this.tickProgress.getMaximum() - this.tickProgress.getMinimum())) / width));
			this.tickProgress.setValue(Math.round(Math.max(TGDuration.QUARTER_TIME, selection)));
			this.redrawProgress();
		}
	}

	private void initToolBar() {
		if (this.toolBar != null && !this.toolBar.isDisposed()) {
			this.toolBar.dispose();
		}
		this.toolBar = getUIFactory().createHorizontalToolBar(this.dialog);

		this.first = this.toolBar.createActionItem();
		this.first.addSelectionListener(new TGActionProcessorListener(this.context, TGGoFirstMeasureAction.NAME));

		this.previous = this.toolBar.createActionItem();
		this.previous.addSelectionListener(new TGActionProcessorListener(this.context, TGGoPreviousMeasureAction.NAME));

		this.stop = this.toolBar.createActionItem();
		this.stop.addSelectionListener(new TGActionProcessorListener(this.context, TGTransportStopAction.NAME));

		this.play = this.toolBar.createActionItem();
		this.play.addSelectionListener(new TGActionProcessorListener(this.context, TGTransportPlayPauseAction.NAME));

		this.next = this.toolBar.createActionItem();
		this.next.addSelectionListener(new TGActionProcessorListener(this.context, TGGoNextMeasureAction.NAME));

		this.last = this.toolBar.createActionItem();
		this.last.addSelectionListener(new TGActionProcessorListener(this.context, TGGoLastMeasureAction.NAME));

		UITableLayout uiLayout = (UITableLayout) this.dialog.getLayout();
		uiLayout.set(this.toolBar, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.updateItems(true);
		this.loadProperties();
	}

	public void updateItems() {
		this.updateItems(false);
	}

	public void updateItems(boolean force) {
		if (!isDisposed()) {
			boolean lastStatusRunning = isRunning;

			MidiPlayer player = MidiPlayer.getInstance(this.context);
			isRunning = player.isRunning();

			if (force || lastStatusRunning != isRunning) {
				updateHeaderMap();
				this.first.setImage(TuxGuitar.getInstance().getIconManager().getTransportFirst());
				this.last.setImage(TuxGuitar.getInstance().getIconManager().getTransportLast());
				this.previous.setImage(TuxGuitar.getInstance().getIconManager().getTransportPrevious());
				this.next.setImage(TuxGuitar.getInstance().getIconManager().getTransportNext());
				this.stop.setImage(TuxGuitar.getInstance().getIconManager().getTransportStop());
				if (isRunning) {
					this.play.setImage(TuxGuitar.getInstance().getIconManager().getTransportPause());
					this.stop.setEnabled(true);
				} else {
					this.play.setImage(TuxGuitar.getInstance().getIconManager().getTransportPlay());
					this.metronome.setText("");
					this.stop.setEnabled(false);
				}
				this.loadPlayText();
			}
			TGMeasureHeader first = getSongManager().getFirstMeasureHeader(getDocumentManager().getSong());
			TGMeasureHeader last = getSongManager().getLastMeasureHeader(getDocumentManager().getSong());
			this.tickProgress.setMinimum((int) first.getStart());
			this.tickProgress.setMaximum((int) (last.getStart() + last.getLength()) - 1);
			this.metronome.setSelected(player.isMetronomeEnabled());
			this.countInToggle.setSelected(player.getCountDown().isEnabled());

			// Set default spinner's value to first measure time signature or current
			// count-in ticks.
			// Default value will automatically update to correct count when user changes
			// song,
			// or play a section with different time signature.

			if (player.getCountDown().getTickCount() == 0) {
				TGMeasureHeader currentHeader = getSongManager().getMeasureHeaderAt(player.getSong(),
						TablatureEditor.getInstance(this.context).getTablature().getCaret().getPosition());
				this.countInTicks.setValue(currentHeader.getTimeSignature().getNumerator());
			} else {
				this.countInTicks.setValue(player.getCountDown().getTickCount());
			}
			this.countInTicks.setEnabled(player.getCountDown().isEnabled());

			this.redrawProgress();
		}
	}

	public void loadProperties() {
		if (!isDisposed()) {
			this.dialog.setText(TuxGuitar.getProperty("transport"));
			this.stop.setToolTipText(TuxGuitar.getProperty("transport.stop"));
			this.first.setToolTipText(TuxGuitar.getProperty("transport.first"));
			this.last.setToolTipText(TuxGuitar.getProperty("transport.last"));
			this.previous.setToolTipText(TuxGuitar.getProperty("transport.previous"));
			this.next.setToolTipText(TuxGuitar.getProperty("transport.next"));
			this.metronome.setToolTipText(TuxGuitar.getProperty("transport.metronome"));
			this.mode.setToolTipText(TuxGuitar.getProperty("transport.mode"));
			this.countInToggle.setToolTipText(TuxGuitar.getProperty("transport.count-down"));
			this.countInTicks.setToolTipText(TuxGuitar.getProperty("transport.count-down-ticks"));
			this.loadPlayText();
		}
	}

	public void loadPlayText() {
		String property = TuxGuitar.getProperty((isRunning ? "transport.pause" : "transport.start"));
		this.play.setToolTipText(property);
	}

	public void loadIcons() {
		if (!isDisposed()) {
			this.initToolBar();
			this.loadOptionIcons();
			this.dialog.setImage(TuxGuitar.getInstance().getIconManager().getAppIcon());
			this.dialog.layout();
		}
	}

	private void loadOptionIcons() {
		this.metronome
				.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TRANSPORT_METRONOME));
		this.mode.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TRANSPORT_MODE));
		this.countInToggle
				.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.TRANSPORT_COUNT_IN));
	}

	public void dispose() {
		if (!isDisposed()) {
			this.dialog.dispose();
		}
	}

	public boolean isDisposed() {
		return (this.dialog == null || this.dialog.isDisposed());
	}

	public boolean isEditingTickScale() {
		return this.editingTickScale;
	}

	public void setEditingTickScale(boolean editingTickScale) {
		this.editingTickScale = editingTickScale;
	}

	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}

	public TGDocumentManager getDocumentManager() {
		return TGDocumentManager.getInstance(this.context);
	}

	public TGSongManager getSongManager() {
		return getDocumentManager().getSongManager();
	}

	public void gotoMeasure(TGMeasureHeader header, boolean moveCaret) {
		TGTransport.getInstance(this.context).gotoMeasure(header, moveCaret);
	}

	public void updateTickLabel(String value) {
		String oldValue = this.label.getText();

		this.label.setText(value);

		if (oldValue == null || oldValue.length() != value.length()) {
			UIPanel uiPanel = (UIPanel) this.label.getParent();
			uiPanel.layout();
		}
	}

	public void redrawProgress() {
		if (!isDisposed() && !TuxGuitar.getInstance().isLocked()) {
			if (isEditingTickScale()) {
				TGTransportDialog.this.updateTickLabel(Long.toString(TGTransportDialog.this.tickProgress.getValue()));
			} else if (!MidiPlayer.getInstance(this.context).isRunning()) {
				long tickPosition = TablatureEditor.getInstance(this.context).getTablature().getCaret().getPosition();

				TGTransportDialog.this.updateTickLabel(Long.toString(tickPosition));
				TGTransportDialog.this.tickProgress.setValue((int) tickPosition);
			}
		}
	}

	public void redrawPlayingMode() {
		if (!isDisposed()) {
			MidiPlayer player = MidiPlayer.getInstance(this.context);
			if (!isEditingTickScale() && player.isRunning()) {
				TGTransportCache transportCache = TGTransport.getInstance(this.context).getCache();

				long time = System.currentTimeMillis();
				if (time > this.redrawTime + PLAY_MODE_DELAY) {
					long position = (transportCache.getPlayStart()
							+ (player.getTickPosition() - transportCache.getPlayTick()));
					this.updateTickLabel(Long.toString(position));
					this.tickProgress.setValue((int) position);
					this.redrawTime = time;

					TGSong song = TGDocumentManager.getInstance(context).getSong();
					TGMeasureHeader first = song != null
							? TablatureEditor.getInstance(context).getTablature().getSongManager()
									.getFirstMeasureHeader(song)
							: null;
					Map.Entry<Long, TGMeasureHeader> entry = headerMap.floorEntry(position);
					TGMeasureHeader current = entry != null ? entry.getValue() : first;
					Integer currentTempo = Math.round((float) current.getTempo().getRawValue()
							* (float) player.getMode().getCurrentPercent() / 100.0f);
					this.metronome.setText(currentTempo.toString());
				}
			}
		}
	}

	public void createSyncProcesses() {
		this.loadPropertiesProcess = new TGSyncProcess(this.context, new Runnable() {
			public void run() {
				loadProperties();
			}
		});

		this.loadIconsProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				loadIcons();
			}
		});

		this.updateItemsProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				updateItems();
			}
		});

		this.redrawPlayModeProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				redrawPlayingMode();
			}
		});
	}

	public void processRedrawEvent(TGEvent event) {
		int type = ((Integer) event.getAttribute(TGRedrawEvent.PROPERTY_REDRAW_MODE)).intValue();
		if (type == TGRedrawEvent.PLAYING_THREAD || type == TGRedrawEvent.PLAYING_NEW_BEAT) {
			this.redrawPlayModeProcess.process();
		}
	}

	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer) event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if (type == TGUpdateEvent.SELECTION) {
			this.updateItemsProcess.process();
		}
	}

	public void processEvent(final TGEvent event) {
		if (TGSkinEvent.EVENT_TYPE.equals(event.getEventType())) {
			this.loadIconsProcess.process();
		} else if (TGLanguageEvent.EVENT_TYPE.equals(event.getEventType())) {
			this.loadPropertiesProcess.process();
		} else if (TGRedrawEvent.EVENT_TYPE.equals(event.getEventType())) {
			this.processRedrawEvent(event);
		} else if (TGUpdateEvent.EVENT_TYPE.equals(event.getEventType())) {
			this.processUpdateEvent(event);
		}
	}

	public static TGTransportDialog getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGTransportDialog.class.getName(),
				new TGSingletonFactory<TGTransportDialog>() {
					public TGTransportDialog createInstance(TGContext context) {
						return new TGTransportDialog(context);
					}
				});
	}

	private void updateHeaderMap() {
		final TGSong song = TGDocumentManager.getInstance(context).getSong();
		headerMap.clear();
		SortedSet<TGMeasureHeader> headers = new TreeSet<>(Comparator.comparingLong(TGMeasureHeader::getStart));
		for (int i = 0; i < song.countMeasureHeaders(); i++) {
			headers.add(song.getMeasureHeader(i));
		}
		for (TGMeasureHeader header : headers) {
			headerMap.put(header.getStart(), header);
		}
	}
}
