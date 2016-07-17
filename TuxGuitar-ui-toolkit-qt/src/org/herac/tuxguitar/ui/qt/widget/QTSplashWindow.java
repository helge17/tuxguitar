package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.qt.QTComponent;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.widget.UISplashWindow;

import com.trolltech.qt.gui.QWidget;

public class QTSplashWindow extends QTComponent<QWidget> implements UISplashWindow {
	
	private UIImage image;
	private UIImage splashImage;
	
	public QTSplashWindow() {
		super(null);
	}
	
	public String getText() {
//		return this.getControl().windowTitle();
		return null;
	}

	public void setText(String text) {
//		this.getControl().setWindowTitle(text);
	}

	public UIImage getImage() {
		return this.image;
	}

	public void setImage(UIImage image) {
		this.image = image;
	}
	
	public UIImage getSplashImage() {
		return splashImage;
	}

	public void setSplashImage(UIImage splashImage) {
		this.splashImage = splashImage;
	}

	public void dispose() {
//		this.getControl().close();
		
		super.dispose();
	}
	
	public void open() {
		// TODO
	}
}
