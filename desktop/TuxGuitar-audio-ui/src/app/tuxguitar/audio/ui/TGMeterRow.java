package app.tuxguitar.audio.ui;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.audio.TGAudioStatus;
import app.tuxguitar.audio.action.TGResetClipIndicatorAction;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIProgressBar;
import app.tuxguitar.util.TGContext;

/**
 * An armed track's row of the Recording window: its name, its input meter (0 to 100: -60 dBFS to 0 dBFS), the level,
 * "CLIP" once it has clipped, and a button that resets the clip indicator.
 */
final class TGMeterRow {

	private final UILabel name;
	private final UIProgressBar bar;
	private final UILabel level;
	private final UILabel clip;

	TGMeterRow(TGContext context, UIFactory factory, UIPanel panel, int row, TGTrack track) {
		UITableLayout layout = (UITableLayout) panel.getLayout();
		this.name = factory.createLabel(panel);
		layout.set(this.name, row, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, true, false, 1, 1, 120f, null, null);
		this.bar = factory.createProgressBar(panel);
		this.bar.setMinimum(0);
		this.bar.setMaximum(100);
		layout.set(this.bar, row, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false, 1, 1, 200f, null, null);
		this.level = factory.createLabel(panel);
		layout.set(this.level, row, 3, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false, 1, 1, 80f, null, null);
		this.clip = factory.createLabel(panel);
		layout.set(this.clip, row, 4, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false, 1, 1, 40f, null, null);
		UIButton reset = factory.createButton(panel);
		reset.setText(TuxGuitar.getProperty("audio.status.reset-clip"));
		reset.addSelectionListener(event -> resetClip(context, track));
		layout.set(reset, row, 5, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, false, false);
	}

	private static void resetClip(TGContext context, TGTrack track) {
		TGActionProcessor processor = new TGActionProcessor(context, TGResetClipIndicatorAction.NAME);
		processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		processor.process();
	}

	void show(TGAudioStatus.Meter meter) {
		this.name.setText(meter.getTrack().getName());
		this.bar.setValue(meter.getPercent());
		this.level.setText(meter.getLevel());
		this.clip.setText(meter.hasClipped() ? TuxGuitar.getProperty("audio.status.clip") : "");
	}
}
