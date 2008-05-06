package org.herac.tuxguitar.gui.tools.custom.tuner;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
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
	Composite composite = null;
	int currentFrequency = 0;
	
	/** constants for drawing */
	private final int startA = 20;
	private final int endAb = 20;
	private final int boundaryHeight = 16;
	private final int markerHeight = 12;
	private final int markerWidth = 4;
	
	
	public TGTunerRoughWidget(Composite parent) {
		super(parent, SWT.NONE);
		this.init();
	}
	
	public void init() {
		this.setLayout(new GridLayout(1,true));
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		
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
		for (int i=this.startA; i<compositeSize.x+1-this.endAb; i+=increment) {
			painter.initPath();
			painter.moveTo(i,compositeSize.y/2-this.boundaryHeight/2);
			painter.lineTo(i, compositeSize.y/2+this.boundaryHeight/2);
			painter.closePath();
		}
		
		// marker
		if (this.currentFrequency!=-1) {
			painter.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLUE));
			painter.initPath();
			int markerPos = (int)Math.round(((compositeSize.x-this.startA-this.endAb) / 110.0) * (this.currentFrequency%110.0));
			System.out.println(markerPos+", "+this.currentFrequency);
			painter.moveTo(markerPos, compositeSize.y/2-this.markerHeight/2);
			painter.setLineWidth(this.markerWidth);
			painter.lineTo(markerPos, compositeSize.y/2+this.markerHeight/2);
			painter.closePath();
		}
		
	}

	public void setCurrentFrequency(int currentFrequency) {
		this.currentFrequency = currentFrequency;
	}

	public void redraw(){
		super.redraw();
		this.composite.redraw();
	}
	
}
