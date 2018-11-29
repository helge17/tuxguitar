package org.herac.tuxguitar.ui.swt;

import java.net.URL;

import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.herac.tuxguitar.ui.UIApplication;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.appearance.UIAppearance;
import org.herac.tuxguitar.ui.swt.appearance.SWTAppearance;

public class SWTApplication extends SWTComponent<Display> implements UIApplication {
	
	private UIFactory factory;
	private UIAppearance appearance;
	
	public SWTApplication(String name) {
		super(createDisplay(name));
		
		this.factory = new SWTFactory(this.getControl());
		this.appearance = new SWTAppearance(this.getDisplay());
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
	
	public UIAppearance getAppearance() {
		return this.appearance;
	}

	public Display getDisplay() {
		return getControl();
	}

	public void runInUiThread(Runnable runnable) {
		this.getControl().asyncExec(runnable);
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
	
	private static Display createDisplay(String name) {
		Display.setAppName(name);
		
		return new Display();
	}
}
