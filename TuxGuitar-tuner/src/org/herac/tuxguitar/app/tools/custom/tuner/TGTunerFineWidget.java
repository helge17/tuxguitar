/**
 * 
 */
package org.herac.tuxguitar.app.tools.custom.tuner;

import org.herac.tuxguitar.app.graphics.TGColorImpl;
import org.herac.tuxguitar.app.graphics.TGFontImpl;
import org.herac.tuxguitar.app.graphics.TGPainterImpl;
import org.herac.tuxguitar.app.system.color.TGColorManager;
import org.herac.tuxguitar.app.system.config.TGConfigKeys;
import org.herac.tuxguitar.app.system.config.TGConfigManager;
import org.herac.tuxguitar.graphics.TGPainter;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIPaintEvent;
import org.herac.tuxguitar.ui.event.UIPaintListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIFont;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.widget.UICanvas;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.util.TGContext;

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
				TGPainterImpl painter = new TGPainterImpl(factory, event.getPainter());
				TGTunerFineWidget.this.paintWidget(painter);
			}
		});
		layout.set(this.composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.letterFont = factory.createFont(TGConfigManager.getInstance(this.context).getFontModelConfigValue(TGConfigKeys.MATRIX_FONT).getName(), 14, true, false);
	}
	
	
	public void paintWidget(TGPainter painter) {
		TGColorManager colorManager = TGColorManager.getInstance(this.context);
		
		UIRectangle compositeSize = this.composite.getBounds();
		
		// margins & stuff
		
		painter.setForeground(new TGColorImpl(colorManager.getColor(TGColorManager.COLOR_BLACK)));
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
			painter.setForeground(new TGColorImpl(colorManager.getColor(TGColorManager.COLOR_BLUE)));
			painter.setFont(new TGFontImpl(this.letterFont));
			painter.drawString(this.currentNoteString, compositeSize.getWidth()*12/15, 10);

			// pointer
			if (this.currentFrequency!=-1) {
				painter.setLineWidth(3);
				painter.setForeground(new TGColorImpl(colorManager.getColor(TGColorManager.COLOR_RED)));
				painter.initPath();
				painter.moveTo(compositeSize.getWidth()/2, compositeSize.getHeight()-BOTTOM_Y);
				painter.lineTo((float)(compositeSize.getWidth()/2 + height*Math.cos(this.getAngleRad())),(float)( compositeSize.getHeight()-BOTTOM_Y-height*Math.sin(this.getAngleRad())));
			painter.closePath();
			}
		}
		
		
		
	
	}
	
	
	public void setWantedTone(int tone) {
		this.panel.setEnabled(true);
		this.currentNoteValue = tone;
		this.currentNoteString = TGTunerRoughWidget.TONESSTRING[tone%12]+(int)Math.floor(tone/12);
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
		return (float)(45+12*(Math.log(frequency/110)/Math.log(2)));
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