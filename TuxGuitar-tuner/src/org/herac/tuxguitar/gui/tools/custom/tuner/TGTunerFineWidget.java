/**
 * 
 */
package org.herac.tuxguitar.gui.tools.custom.tuner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.editors.TGPainter;
import org.herac.tuxguitar.gui.system.config.TGConfigKeys;

/**
 * @author Nikola Kolarovic <johnny47ns@yahoo.com>
 *
 */
public class TGTunerFineWidget extends Composite {

	private static final int MIN_HEIGHT = 60;
	private static final int MIN_WIDTH = 80;
	private final float bottomY = 10.0f;
	
	private Composite composite = null;
	protected String currentNoteString = null;
	protected int currentNoteValue = -1;
	protected double currentFrequency = 0.0f;
	protected Font letterFont = null;
	protected final float FINE_TUNING_RANGE = 1.5f;

	public TGTunerFineWidget(Composite parent) {
		super(parent, SWT.NONE);
		this.setEnabled(false);
		this.init();
	}

	protected void init() {
		this.setLayout(new GridLayout(1,true));
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		
		this.composite = new Composite(this,SWT.BORDER | SWT.DOUBLE_BUFFERED);
		this.composite.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		this.composite.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				TGPainter painter = new TGPainter(e.gc);
				TGTunerFineWidget.this.paintWidget(painter);
			}
		});
		
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumHeight = MIN_HEIGHT;
		data.minimumWidth = MIN_WIDTH;
		data.grabExcessHorizontalSpace=true;
		data.grabExcessVerticalSpace=true;
		this.composite.setLayoutData(data);
		this.letterFont = new Font(this.getDisplay(),
							TuxGuitar.instance().getConfig().getFontDataConfigValue(TGConfigKeys.MATRIX_FONT).getName(),
							14,
							SWT.BOLD
							);

	}
	
	
	public void paintWidget(TGPainter painter) {
		Point compositeSize = this.composite.getSize();
		
		// margins & stuff
		
		painter.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		painter.initPath();
		painter.setLineWidth(2);
		float height = compositeSize.y-this.bottomY-25;
		painter.moveTo(compositeSize.x/2, compositeSize.y-this.bottomY);
		painter.lineTo(compositeSize.x/2, 25);
		painter.closePath();
		painter.initPath();
		height = Math.min(height, compositeSize.x/2);
		painter.moveTo(compositeSize.x/2-height, compositeSize.y-this.bottomY);
		painter.lineTo(compositeSize.x/2+height, compositeSize.y-this.bottomY);
		painter.closePath();
		
		if (this.isEnabled()) {
			// tone name
			painter.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLUE));
			painter.setFont(this.letterFont);
			painter.drawString(this.currentNoteString, compositeSize.x*12/15, 10);

			// pointer
			if (this.currentFrequency!=-1) {
				painter.setLineWidth(3);
				painter.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
				painter.initPath();
				painter.moveTo(compositeSize.x/2, compositeSize.y-this.bottomY);
				painter.lineTo((float)(compositeSize.x/2 + height*Math.cos(this.getAngleRad())),(float)( compositeSize.y-this.bottomY-height*Math.sin(this.getAngleRad())));
			painter.closePath();
			}
		}
		
		
		
	
	}
	
	
	public void setWantedTone(int tone) {
		this.setEnabled(true);
		this.currentNoteValue = tone;
		this.currentNoteString = TGTunerRoughWidget.TONESSTRING[tone%12]+(int)Math.floor(tone/12);
		this.redraw();
		
	}
	
	public void setCurrentFrequency(double freq) {
		this.currentFrequency = freq;
		this.redraw();
	}

	public void redraw(){
		super.redraw();
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

	
}