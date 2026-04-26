package app.tuxguitar.ui.swt;

import java.net.URL;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import app.tuxguitar.ui.UIApplication;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.appearance.UIAppearance;
import app.tuxguitar.ui.swt.appearance.SWTAppearance;

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

	public float getDisplayScale() {
		if( this.isDisposed() ) {
			return 1.0f;
		}
		try {
			Display display = this.getDisplay();

			// Signal 1: Display DPI vs platform reference
			// macOS standard = 72, Windows/Linux = 96
			Point dpi = display.getDPI();
			String os = System.getProperty("os.name", "").toLowerCase();
			float referenceDpi = os.contains("mac") ? 72.0f : 96.0f;
			float dpiScale = dpi.x / referenceDpi;

			// Retina guard: on macOS, if DPI indicates 2x, SWT already handles
			// pixel doubling internally — don't double-scale
			if( os.contains("mac") && dpiScale >= 2.0f ) {
				return 1.0f;
			}

			if( dpiScale > 1.1f ) {
				return Math.min(dpiScale, 3.0f);
			}

			// Signal 2: Resolution heuristic
			// If DPI reports ~1.0 but monitor resolution is very high,
			// compute scale from resolution ratio (catches "More Space" on
			// non-HiDPI external displays)
			Monitor primaryMonitor = display.getPrimaryMonitor();
			if( primaryMonitor != null ) {
				Rectangle bounds = primaryMonitor.getClientArea();
				if( bounds.width > 2560 ) {
					double monitorPixels = Math.sqrt((double) bounds.width * bounds.height);
					double referencePixels = Math.sqrt(1920.0 * 1080.0);
					float heuristicScale = (float)(monitorPixels / referencePixels);
					heuristicScale = Math.max(1.0f, Math.min(heuristicScale, 3.0f));
					if( heuristicScale > 1.1f ) {
						return heuristicScale;
					}
				}
			}

			return Math.max(1.0f, dpiScale);
		} catch(Exception e) {
			return 1.0f;
		}
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
