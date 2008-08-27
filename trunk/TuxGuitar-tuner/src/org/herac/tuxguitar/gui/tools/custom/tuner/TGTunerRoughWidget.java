package org.herac.tuxguitar.gui.tools.custom.tuner;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.herac.tuxguitar.gui.editors.TGPainter;

/**
 * @author Nikola Kolarovic <johnny47ns@yahoo.com>
 *
 */
public class TGTunerRoughWidget extends Composite {

	private static final int MIN_HEIGHT = 25;
	protected Composite composite = null;
	protected float currentFrequency = 0;
	
	/** constants for drawing */
	private final int startA = 20;
	private final int endAb = 20;
	private final int boundaryHeight = 16;
	private final int markerHeight = 12;
	private final int markerWidth = 4;
	static String[] TONESSTRING = {"C","C#","D","D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
	
	
	public TGTunerRoughWidget(Composite parent) {
		super(parent, SWT.NONE);
		this.init();
	}
	
	public void init() {
		this.setLayout(new GridLayout(1,true));
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		((GridData)this.getLayoutData()).widthHint = 600;
		
		
		this.composite = new Composite(this,SWT.BORDER | SWT.DOUBLE_BUFFERED);
		this.composite.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		this.composite.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				TGPainter painter = new TGPainter(e.gc);
				TGTunerRoughWidget.this.paintWidget(painter);
			}

		});
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumHeight = MIN_HEIGHT;
		data.grabExcessHorizontalSpace = true;
		this.composite.setLayoutData(data);

	}
	
	
	private void paintWidget(TGPainter painter) {
		
		Point compositeSize = this.composite.getSize();
		
		// lines and tones
		painter.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		painter.initPath();
		painter.moveTo(this.startA, compositeSize.y/2);
		painter.lineTo(compositeSize.x-this.endAb, compositeSize.y/2);
		painter.closePath();
		int increment = (int)Math.round((compositeSize.x-this.startA-this.endAb) / 12.0);
		int currentTone = 0;
		for (int i=this.startA; i<compositeSize.x+1-this.endAb; i+=increment) {
			painter.initPath();
			painter.moveTo(i,compositeSize.y/2-this.boundaryHeight/2);
			painter.lineTo(i, compositeSize.y/2+this.boundaryHeight/2);
			painter.closePath();
			painter.drawString(TONESSTRING[currentTone%12], i, compositeSize.y/2-this.boundaryHeight/2-20);
			currentTone++;
		}
		
		// marker
		if (this.currentFrequency>0) {
			painter.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLUE));
			painter.initPath();
			int markerPos = this.markerWidth/2 + this.startA+(int)Math.round(((compositeSize.x-this.startA-this.endAb) / 240.0) * (this.getTone(this.currentFrequency)));
			painter.moveTo(markerPos, compositeSize.y/2-this.markerHeight/2);
			painter.setLineWidth(this.markerWidth);
			painter.lineTo(markerPos, compositeSize.y/2+this.markerHeight/2);
			painter.closePath();
		}
		
	}

	public void setCurrentFrequency(double currentFrequency) {
		this.currentFrequency = (float)currentFrequency;
		this.redraw();
	}

	public void redraw(){
		super.redraw();
		this.composite.redraw();
	}

	
	/** formula which gets fine tone position (on scale 0-240)
	 * in fact, it's inverse formula of inverse of TGTuner::getNoteFrequency() 
	 */
	protected int getTone(float frequency) {
		float midiTone = (float)(45+12*(Math.log(frequency/110)/Math.log(2)));
		return Math.round(  20 *   (midiTone % 12));
	}
	
}
