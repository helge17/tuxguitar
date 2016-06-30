package org.herac.tuxguitar.ui.swt;

import java.net.URL;

import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.herac.tuxguitar.ui.UIApplication;
import org.herac.tuxguitar.ui.UIFactory;

public class SWTApplication extends SWTComponent<Display> implements UIApplication {
	
	private UIFactory factory;
	
	public SWTApplication() {
		super(new Display());
		
		this.factory = new SWTFactory(this.getControl());
	}
	
	public void dispose() {
		this.getControl().dispose();
	}
	
	public boolean isDisposed() {
		return this.getControl().isDisposed();
	}
	
	public UIFactory getFactory() {
		return this.factory;
	}

	public Display getDisplay() {
		return getControl();
	}

	public void runInUiThread(Runnable runnable) {
		this.getControl().asyncExec(runnable);
	}

	public void setApplicationName(String name) {
		Display.setAppName(name);
	}
	
	public void openUrl(URL url) {
		Program.launch(url.toExternalForm());
	}
	
	public boolean isInUiThread() {
		Thread uiThread = this.getControl().getThread();
		Thread currentThread = Thread.currentThread();
		return (currentThread == uiThread);
	}
	
	public void start(Runnable runnable) {
		SWTEnvironment.getInstance().start(this.getDisplay());
		
		this.runInUiThread(runnable);
		
		while(!this.isDisposed()) {
			if(!this.getDisplay().readAndDispatch()) {
				this.getControl().sleep();
			}
		}
	}
}
