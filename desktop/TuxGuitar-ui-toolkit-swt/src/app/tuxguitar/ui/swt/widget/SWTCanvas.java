package app.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import app.tuxguitar.ui.event.UIPaintListener;
import app.tuxguitar.ui.resource.UISize;
import app.tuxguitar.ui.swt.event.SWTPaintListenerManager;
import app.tuxguitar.ui.widget.UICanvas;

public class SWTCanvas extends SWTControl<Composite> implements UICanvas {

	private SWTPaintListenerManager selectionListener;

	public SWTCanvas(SWTContainer<? extends Composite> parent, boolean bordered) {
		super(new Composite(parent.getControl(), SWT.DOUBLE_BUFFERED | (bordered ? SWT.BORDER : 0)), parent);

		this.selectionListener = new SWTPaintListenerManager(this);
	}

	public void computePackedSize(Float fixedWidth, Float fixedHeight) {
		UISize size = this.getPackedSize();

		this.setPackedSize(new UISize(fixedWidth != null ? fixedWidth : size.getWidth(), fixedHeight != null ? fixedHeight : size.getHeight()));
	}

	public void addPaintListener(UIPaintListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().addPaintListener(this.selectionListener);
		}
		this.selectionListener.addListener(listener);
	}

	public void removePaintListener(UIPaintListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().removePaintListener(this.selectionListener);
		}
	}
}
