package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.event.UIPaintListener;
import org.herac.tuxguitar.ui.qt.event.QTPaintListenerManager;
import org.herac.tuxguitar.ui.widget.UICanvas;

import com.trolltech.qt.core.QEvent.Type;
import com.trolltech.qt.gui.QFrame;
import com.trolltech.qt.gui.QPaintDeviceInterface;
import com.trolltech.qt.gui.QFrame.Shape;

public class QTCanvas extends QTWidget<QFrame> implements UICanvas {
	
	private QTPaintListenerManager paintListener;
	
	public QTCanvas(QTContainer parent, boolean bordered) {
		super(new QFrame(parent.getContainerControl()), parent);
		
		this.paintListener = new QTPaintListenerManager(this);
		this.getControl().setAutoFillBackground(true);
		this.getControl().setFrameShape(bordered ? Shape.StyledPanel : Shape.NoFrame);
	}
	
	public QPaintDeviceInterface getPaintDeviceInterface() {
		return this.getControl();
	}
	
	public void addPaintListener(UIPaintListener listener) {
		if( this.paintListener.isEmpty() ) {
			this.getEventFilter().connect(Type.Paint, this.paintListener);
		}
		this.paintListener.addListener(listener);
	}
	
	public void removePaintListener(UIPaintListener listener) {
		this.paintListener.removeListener(listener);
		if( this.paintListener.isEmpty() ) {
			this.getEventFilter().disconnect(Type.Paint, this.paintListener);
		}
	}
}