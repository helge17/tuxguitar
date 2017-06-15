package org.herac.tuxguitar.ui.qt;

import java.net.URL;

import org.herac.tuxguitar.ui.UIApplication;
import org.herac.tuxguitar.ui.UIFactory;

import com.trolltech.qt.core.QUrl;
import com.trolltech.qt.gui.QDesktopServices;

public class QTApplication extends QTComponent<QTApplicationHandle> implements UIApplication {
	
	private Thread uiThread;
	private UIFactory uiFactory;
	private QTEnvironment environment;
	
	public QTApplication() {
		super(new QTApplicationHandle());
		
		this.environment = new QTEnvironment();
		this.uiFactory = new QTFactory();
	}
	
	public void dispose() {
		this.getControl().quit();
		
		super.dispose();
	}
	
	public UIFactory getFactory() {
		return this.uiFactory;
	}

	public void setApplicationName(String name) {
		this.getControl().setApplicationName(name);
	}
	
	public void openUrl(URL url) {
		QDesktopServices.openUrl(new QUrl(url.toExternalForm()));
	}
	
	public void runInUiThread(Runnable runnable) {
		this.getControl().invokeLater(runnable);
	}
	
	public boolean isInUiThread() {
		return (this.uiThread == Thread.currentThread());
	}

	public void start(Runnable runnable) {
		this.uiThread = Thread.currentThread();
		this.getControl().initialize();
		
		String qtStyle = this.environment.findStyle();
		if( qtStyle != null && qtStyle.length() > 0 ) {
			this.getControl().setStyle(qtStyle);
		}
		
		this.runInUiThread(runnable);
		
		this.getControl().exec();
	}
}
