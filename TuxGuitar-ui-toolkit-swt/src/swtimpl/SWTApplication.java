package swtimpl;

import java.net.URL;

import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.herac.tuxguitar.ui.UIApplication;
import org.herac.tuxguitar.ui.UIFactory;

public class SWTApplication extends SWTComponent<Display> implements UIApplication {
	
	private UIFactory uiFactory;
	
	public SWTApplication() {
		super(new Display());
		
		this.uiFactory = new SWTFactory(this.getControl());
	}
	
	public void dispose() {
		this.getControl().dispose();
	}
	
	public boolean isDisposed() {
		return this.getControl().isDisposed();
	}
	
	public UIFactory getFactory() {
		return this.uiFactory;
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
}
