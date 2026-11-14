package app.tuxguitar.audio.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.audio.TGAudioStatus;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGSynchronizer;

/**
 * The Recording window (not modal): whether a recording is under way (or why there is no device), Record (greyed
 * while it cannot start one) and a row per armed track with its input meter. It shows TGAudioStatus, read on the UI
 * thread every 50 ms while the window is open: what it shows is decided there.
 */
public class TGRecordingWindow {

	private static final long POLL_MILLISECONDS = 50;

	private final TGContext context;
	private final UIFactory factory;
	private final ScheduledExecutorService poll = TGAudioPlugin.scheduler("TuxGuitar audio status");
	private UIWindow window;
	private UILabel state;
	private UIButton record;
	private UIPanel meters;
	// the tracks the rows show, in order
	private List<TGTrack> shown = new ArrayList<TGTrack>();
	private final List<TGMeterRow> rows = new ArrayList<TGMeterRow>();

	public TGRecordingWindow(TGContext context) {
		this.context = context;
		this.factory = TGApplication.getInstance(context).getFactory();
	}

	public void open() {
		UITableLayout layout = new UITableLayout();
		this.window = this.factory.createWindow(TGWindow.getInstance(this.context).getWindow(), false, true);
		this.window.setLayout(layout);
		this.window.setText(TuxGuitar.getProperty("audio.status.title"));
		this.state = this.factory.createLabel(this.window);
		layout.set(this.state, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, false, 1, 1, 200f, null, null);
		this.record = this.factory.createButton(this.window);
		this.record.setText(TuxGuitar.getProperty("audio.record"));
		this.record.addSelectionListener(event -> TGRecordToolPlugin.record(this.context));
		layout.set(this.record, 1, 2, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, false, 1, 1, 80f, 25f, null);
		this.meters = this.factory.createPanel(this.window, false);
		this.window.addDisposeListener(event -> this.poll.shutdownNow());
		this.update();
		TGDialogUtil.openDialog(this.window, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
		this.poll.scheduleAtFixedRate(() -> TGSynchronizer.getInstance(this.context).executeLater(this::update), POLL_MILLISECONDS, POLL_MILLISECONDS, TimeUnit.MILLISECONDS);
	}

	// on the UI thread
	private void update() {
		if (!this.window.isDisposed()) {
			this.show(TGAudioStatus.of(this.context));
		}
	}

	private void show(TGAudioStatus status) {
		this.state.setText(TuxGuitar.getProperty(status.getState()));
		this.record.setEnabled(status.canRecord());
		List<TGTrack> tracks = status.getMeters().stream().map(TGAudioStatus.Meter::getTrack).collect(Collectors.toList());
		if (!tracks.equals(this.shown)) {
			this.rebuild(tracks);
		}
		IntStream.range(0, this.rows.size()).forEach(row -> this.rows.get(row).show(status.getMeters().get(row)));
	}

	// a row per track armed now, on a panel of their own
	private void rebuild(List<TGTrack> tracks) {
		this.meters.dispose();
		this.meters = this.factory.createPanel(this.window, false);
		this.meters.setLayout(new UITableLayout());
		((UITableLayout) this.window.getLayout()).set(this.meters, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 2);
		this.rows.clear();
		IntStream.range(0, tracks.size()).forEach(row -> this.rows.add(new TGMeterRow(this.context, this.factory, this.meters, row + 1, tracks.get(row))));
		this.shown = tracks;
		this.window.pack();
		this.window.layout();
	}
}
