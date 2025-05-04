package app.tuxguitar.app.tools.custom.tuner;

import app.tuxguitar.app.system.icons.TGColorManager;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIPaintEvent;
import app.tuxguitar.ui.event.UIPaintListener;
import app.tuxguitar.ui.layout.UITableLayout;
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
public class TGTunerRoughWidget {

	private static final float CANVAS_PACKED_WIDTH = 600f;
	private static final float CANVAS_PACKED_HEIGHT = 100f;

	private TGContext context;
	private UIPanel panel;
	protected UICanvas composite = null;
	protected float currentFrequency = 0;

	/** constants for drawing */
	private final int startA = 20;
	private final int endAb = 20;
	private final int boundaryHeight = 16;
	private final int markerHeight = 12;
	private final int markerWidth = 4;


	public TGTunerRoughWidget(TGContext context, UIFactory factory, UILayoutContainer parent) {
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
				TGTunerRoughWidget.this.paintWidget(event.getPainter());
			}

		});
		layout.set(this.composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, CANVAS_PACKED_WIDTH, CANVAS_PACKED_HEIGHT, null);
	}

	private void paintWidget(UIPainter painter) {
		TGColorManager colorManager = TGColorManager.getInstance(this.context);

		UIRectangle compositeSize = this.composite.getBounds();

		// lines and tones
		painter.setForeground(colorManager.getColor(TGColorManager.COLOR_BLACK));
		painter.initPath();
		painter.moveTo(this.startA, compositeSize.getHeight()/2);
		painter.lineTo(compositeSize.getWidth()-this.endAb, compositeSize.getHeight()/2);
		painter.closePath();
		int increment = (int) Math.round((compositeSize.getWidth()-this.startA-this.endAb) / 12.0);
		int currentTone = 0;
		for (int i=this.startA; i<compositeSize.getWidth()+1-this.endAb; i+=increment) {
			painter.initPath();
			painter.moveTo(i,compositeSize.getHeight()/2-this.boundaryHeight/2);
			painter.lineTo(i, compositeSize.getHeight()/2+this.boundaryHeight/2);
			painter.closePath();
			painter.drawString(TGMusicKeyUtils.sharpKeyNames[currentTone%12], i, compositeSize.getHeight()/2-this.boundaryHeight/2-20);
			currentTone++;
		}

		// marker
		if (this.currentFrequency>0) {
			painter.setForeground(colorManager.getColor(TGColorManager.COLOR_BLUE));
			painter.initPath();
			int markerPos = this.markerWidth/2 + this.startA+(int) Math.round(((compositeSize.getWidth()-this.startA-this.endAb) / 240.0) * (this.getTone(this.currentFrequency)));
			painter.moveTo(markerPos, compositeSize.getHeight()/2-this.markerHeight/2);
			painter.setLineWidth(this.markerWidth);
			painter.lineTo(markerPos, compositeSize.getHeight()/2+this.markerHeight/2);
			painter.closePath();
		}

	}

	public void setCurrentFrequency(double currentFrequency) {
		this.currentFrequency = (float) currentFrequency;
		this.redraw();
	}

	public void redraw(){
		this.composite.redraw();
	}


	/** formula which gets fine tone position (on scale 0-240)
	 * in fact, it's inverse formula of inverse of TGTuner::getNoteFrequency()
	 */
	protected int getTone(float frequency) {
		float midiTone = (float) (45+12*(Math.log(frequency/110)/Math.log(2)));
		return Math.round(  20 *   (midiTone % 12));
	}

	public UIControl getControl() {
		return this.panel;
	}

	public boolean isDisposed() {
		return (this.panel == null || this.panel.isDisposed());
	}
}
