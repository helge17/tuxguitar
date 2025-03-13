package app.tuxguitar.ui.qt.widget;

import app.tuxguitar.ui.event.UILinkListener;
import app.tuxguitar.ui.qt.event.QTLinkListenerManager;
import app.tuxguitar.ui.widget.UILinkLabel;

public class QTLinkLabel extends QTWrapLabel implements UILinkLabel {

	private QTLinkListenerManager linkListener;

	public QTLinkLabel(QTContainer parent) {
		super(parent);

		this.linkListener = new QTLinkListenerManager(this);
	}

	@Override
	public void addLinkListener(UILinkListener listener) {
		if( this.linkListener.isEmpty() ) {
			this.getControl().linkActivated.connect(this.linkListener, QTLinkListenerManager.SIGNAL_METHOD);
		}
		this.linkListener.addListener(listener);
	}

	@Override
	public void removeLinkListener(UILinkListener listener) {
		if( this.linkListener.isEmpty() ) {
			this.getControl().linkActivated.disconnect();
		}
		this.linkListener.addListener(listener);
	}
}