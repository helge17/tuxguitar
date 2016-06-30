package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.herac.tuxguitar.ui.jfx.JFXComponent;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.widget.UISplashWindow;

public class JFXSplashWindow extends JFXComponent<Stage> implements UISplashWindow {
	
	private UIImage image;
	private UIImage splashImage;
	
	public JFXSplashWindow(Stage parent) {
		super(new Stage());
		
		this.getControl().setScene(new Scene(new Pane()));
		this.getControl().initOwner(parent);
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
//		ImageView imageView = new ImageView(((JFXAbstractImage<?>)this.getSplashImage()).getHandle());
//		
//		Pane pane = (Pane) this.getControl().getScene().getRoot();
//		pane.getChildren().add(imageView);
//		
//		this.getControl().show();
	}
}
