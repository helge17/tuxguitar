package org.herac.tuxguitar.ui.jfx;

import java.net.URL;

import org.herac.tuxguitar.ui.UIApplication;
import org.herac.tuxguitar.ui.UIFactory;

import javafx.application.Platform;

public class JFXApplication extends JFXComponent<JFXApplicationHandle> implements UIApplication {
	
	private UIFactory uiFactory;
	
	public JFXApplication() {
		super(JFXApplicationHandle.getInstance());
		
		this.uiFactory = new JFXFactory(this);
	}
	
	public void dispose() {
		this.getControl().getStage().close();
	}
	
	public UIFactory getFactory() {
		return this.uiFactory;
	}

	public void openUrl(URL url) {
		this.getControl().getHostServices().showDocument(url.toExternalForm());
	}
	
	public void runInUiThread(Runnable runnable) {
		Platform.runLater(runnable);
	}
	
	public boolean isInUiThread() {
		return Platform.isFxApplicationThread();
	}

	public void start(Runnable runnable) {
		this.getControl().start(runnable);
	}
}
