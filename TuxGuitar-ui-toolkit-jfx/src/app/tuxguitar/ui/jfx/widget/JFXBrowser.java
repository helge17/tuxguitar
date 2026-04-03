package app.tuxguitar.ui.jfx.widget;

import java.net.URL;

import app.tuxguitar.ui.event.UIResizeListener;
import app.tuxguitar.ui.jfx.event.JFXResizeListenerManager;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.widget.UIBrowser;

import javafx.scene.layout.Region;

import javafx.scene.web.WebView;

public class JFXBrowser extends JFXNode<WebView> implements UIBrowser {

	private UIRectangle bounds;
	private JFXResizeListenerManager resizeListener;

	public JFXBrowser(JFXContainer<? extends Region> parent) {
		super(new WebView(), parent);
		this.resizeListener = new JFXResizeListenerManager(this);
	}

	public void loadUrl(URL url) {
		this.getControl().getEngine().load(url.toString());
	}

	@Override
	public UIRectangle getBounds() {
		return this.bounds;
	}

	@Override
	public void setBounds(UIRectangle bounds) {
		this.bounds = bounds;
		this.getControl().resize(bounds.getWidth(), bounds.getHeight());
	}

	@Override
	public void redraw() {
		this.getControl().getEngine().reload();
	}

	@Override
	public void addResizeListener(UIResizeListener listener) {
		if( this.resizeListener.isEmpty() ) {
			this.getControl().widthProperty().addListener(this.resizeListener);
			this.getControl().heightProperty().addListener(this.resizeListener);
		}
		this.resizeListener.addListener(listener);
	}

	@Override
	public void removeResizeListener(UIResizeListener listener) {
		this.resizeListener.removeListener(listener);
		if( this.resizeListener.isEmpty() ) {
			this.getControl().widthProperty().removeListener(this.resizeListener);
			this.getControl().heightProperty().removeListener(this.resizeListener);
		}
	}
}
