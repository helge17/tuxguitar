package org.herac.tuxguitar.ui.jfx.widget;

import org.herac.tuxguitar.ui.jfx.JFXComponent;
import org.herac.tuxguitar.ui.jfx.resource.JFXImage;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.widget.UISplashWindow;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JFXSplashWindow extends JFXComponent<Stage> implements UISplashWindow {
	
	private UIImage image;
	private UIImage splashImage;
	
	public JFXSplashWindow(Stage parent) {
		super(new Stage());
		
		this.getControl().setScene(new Scene(new Pane()));
		this.getControl().initOwner(parent);
		this.getControl().initStyle(StageStyle.UNDECORATED);
		this.getControl().initModality(Modality.APPLICATION_MODAL);
	}
	
	public String getText() {
		return this.getControl().getTitle();
	}

	public void setText(String text) {
		this.getControl().setTitle(text);
	}

	public UIImage getImage() {
		return this.image;
	}

	public void setImage(UIImage image) {
		this.image = image;
		this.getControl().getIcons().clear();
		this.getControl().getIcons().add(((JFXImage) image).getHandle());
	}
	
	public UIImage getSplashImage() {
		return splashImage;
	}

	public void setSplashImage(UIImage splashImage) {
		this.splashImage = splashImage;
	}

	public void dispose() {
		this.getControl().close();
		
		super.dispose();
	}
	
	public void open() {
		ImageView imageView = new ImageView(((JFXImage)this.getSplashImage()).getHandle());
		
		Pane pane = (Pane) this.getControl().getScene().getRoot();
		pane.getChildren().add(imageView);
		
		this.getControl().show();
		this.waitUntilShow();
	}
	
	public void waitUntilShow() {
		final Object key = this;
		
		Platform.runLater(new Runnable() {
			public void run() {
				Platform.exitNestedEventLoop(key, null);
			}
		});
		Platform.enterNestedEventLoop(key);
	}
}
