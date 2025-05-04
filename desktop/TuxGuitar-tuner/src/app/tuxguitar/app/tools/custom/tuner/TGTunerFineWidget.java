/**
 *
 */
package app.tuxguitar.app.tools.custom.tuner;

import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.system.config.TGConfigManager;
import app.tuxguitar.app.system.icons.TGColorManager;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIPaintEvent;
import app.tuxguitar.ui.event.UIPaintListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UIFont;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.widget.UICanvas;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.ui.widget.UILayoutContainer;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGMusicKeyUtils;

/**
 * @author Nikola Kolarovic <johnny47ns@yahoo.com>
 *
 */
public class TGTunerFineWidget {

	private static final float BOTTOM_Y = 10.0f;

	private TGContext context;
	private UIPanel panel;
	private UICanvas composite = null;
	protected String currentNoteString = null;
	protected int currentNoteValue = -1;
	protected double currentFrequency = 0.0f;
	protected UIFont letterFont = null;
	protected final float FINE_TUNING_RANGE = 1.5f;

	public TGTunerFineWidget(TGContext context, UIFactory factory, UILayoutContainer parent) {
		this.context = context;

		this.init(factory, parent);
	}

	private void init(final UIFactory factory, UILayoutContainer parent) {
		UITableLayout layout = new UITableLayout();

		this.panel = factory.createPanel(parent, false);
		this.panel.setLayout(layout);
		this.panel.setEnabled(false);

		this.composite = factory.createCanvas(this.panel, true);
		this.composite.setBgColor(TGColorManager.getInstance(this.context).getColor(TGColorManager.COLOR_WHITE));
		this.composite.addPaintListener(new UIPaintListener() {
			public void onPaint(UIPaintEvent event) {
				TGTunerFineWidget.this.paintWidget(event.getPainter());
			}
		});
		layout.set(this.composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		this.letterFont = factory.createFont(TGConfigManager.getInstance(this.context).getFontModelConfigValue(TGConfigKeys.MATRIX_FONT).getName(), 14, true, false);
	}


	public void paintWidget(UIPainter painter) {
		TGColorManager colorManager = TGColorManager.getInstance(this.context);

		UIRectangle compositeSize = this.composite.getBounds();

		// margins & stuff

		painter.setForeground(colorManager.getColor(TGColorManager.COLOR_BLACK));
		painter.initPath();
		painter.setLineWidth(2);
		float height = compositeSize.getHeight() - BOTTOM_Y-25;
		painter.moveTo(compositeSize.getWidth()/2, compositeSize.getHeight() - BOTTOM_Y);
		painter.lineTo(compositeSize.getWidth()/2, 25);
		painter.closePath();
		painter.initPath();
		height = Math.min(height, compositeSize.getWidth()/2);
		painter.moveTo(compositeSize.getWidth()/2-height, compositeSize.getHeight()-BOTTOM_Y);
		painter.lineTo(compositeSize.getWidth()/2+height, compositeSize.getHeight()-BOTTOM_Y);
		painter.closePath();

		if (this.panel.isEnabled()) {
			// tone name
			painter.setForeground(colorManager.getColor(TGColorManager.COLOR_BLUE));
			painter.setFont(this.letterFont);
			painter.drawString(this.currentNoteString, compositeSize.getWidth()*12/15, 10);

			// pointer
			if (this.currentFrequency!=-1) {
				painter.setLineWidth(3);
				painter.setForeground(colorManager.getColor(TGColorManager.COLOR_RED));
				painter.initPath();
				painter.moveTo(compositeSize.getWidth()/2, compositeSize.getHeight()-BOTTOM_Y);
				painter.lineTo((float) (compositeSize.getWidth()/2 + height*Math.cos(this.getAngleRad())),(float) ( compositeSize.getHeight()-BOTTOM_Y-height*Math.sin(this.getAngleRad())));
			painter.closePath();
			}
		}




	}


	public void setWantedTone(int tone) {
		this.panel.setEnabled(true);
		this.currentNoteValue = tone;
		this.currentNoteString = TGMusicKeyUtils.sharpNoteFullName(tone);
		this.redraw();

	}

	public void setCurrentFrequency(double freq) {
		this.currentFrequency = freq;
		this.redraw();
	}

	public void redraw() {
		this.composite.redraw();
	}

	protected double getAngleRad() {
		return Math.PI*( 1 - (this.stickDistance(this.getTone(this.currentFrequency) - this.currentNoteValue) + this.FINE_TUNING_RANGE  )/(2*this.FINE_TUNING_RANGE) );
	}


	private float getTone(double frequency) {
		return (float) (45+12*(Math.log(frequency/110)/Math.log(2)));
	}

	private double stickDistance(double diff) {
		if (Math.abs(diff) > this.FINE_TUNING_RANGE)
			if (diff > 0)
				return this.FINE_TUNING_RANGE;
			else
				return -this.FINE_TUNING_RANGE;
		return diff;
	}

	public UIControl getControl() {
		return this.panel;
	}

	public boolean isDisposed() {
		return (this.panel == null || this.panel.isDisposed());
	}
}