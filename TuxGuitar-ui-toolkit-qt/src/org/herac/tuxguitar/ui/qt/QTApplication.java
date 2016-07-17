package org.herac.tuxguitar.ui.qt;

import java.net.URL;

import org.herac.tuxguitar.ui.UIApplication;
import org.herac.tuxguitar.ui.UIFactory;

public class QTApplication extends QTComponent<QTApplicationHandle> implements UIApplication {
	
	private Thread uiThread;
	private UIFactory uiFactory;
	
	public QTApplication() {
		super(new QTApplicationHandle());
		
		this.uiFactory = new QTFactory();
	}
	
	public void dispose() {
		this.getControl().getHandle().dispose();
		
		super.dispose();
	}
	
	public UIFactory getFactory() {
		return this.uiFactory;
	}

	public void setApplicationName(String name) {
		this.getControl().setApplicationName(name);
	}
	
	public void openUrl(URL url) {
		//TODO
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
		
		this.runInUiThread(runnable);
		
		this.getControl().exec();
	}
}
