package org.herac.tuxguitar.ui.jfx.widget;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.herac.tuxguitar.ui.event.UICloseListener;
import org.herac.tuxguitar.ui.jfx.event.JFXCloseListenerManager;
import org.herac.tuxguitar.ui.jfx.menu.JFXMenuBar;
import org.herac.tuxguitar.ui.menu.UIMenuBar;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIWindow;

import com.sun.javafx.tk.Toolkit;

public class JFXWindow extends JFXLayoutContainer<Pane> implements UIWindow {
	
	private static final float DEFAULT_DECORATION_WIDTH = 2f;
	private static final float DEFAULT_DECORATION_HEIGHT = 24f;
	
	private boolean joined;
	private Stage stage;
	private UIImage image;
	private UIMenuBar menuBar;
	private UISize decorationSize;
	private JFXCloseListenerManager closeListener;
	private JFXWindowResizeListener resizeListener;
	
	public JFXWindow(Stage stage, JFXContainer<? extends Pane> parent) {
		super(new Pane(), parent);
		
		this.stage = stage;
		this.stage.setScene(new Scene(getControl()));
		this.stage.getScene().getStylesheets().add("styles/styles.css");
		this.decorationSize = new UISize(DEFAULT_DECORATION_WIDTH, DEFAULT_DECORATION_HEIGHT);
		
		this.closeListener = new JFXCloseListenerManager(this);
		this.resizeListener = new JFXWindowResizeListener(this);
		this.stage.widthProperty().addListener(this.resizeListener);
		this.stage.heightProperty().addListener(this.resizeListener);
		this.stage.setOnCloseRequest(new JFXWindowCloseListener(this));
	}
	
	public JFXWindow(JFXWindow parent, boolean modal, boolean resizable) {
		this(new Stage(), parent);
		
		this.setDecorationSize(parent.getDecorationSize());
		this.stage.initOwner(parent.getStage());
		this.stage.setResizable(resizable);
		if( modal ) {
			this.stage.initModality(Modality.APPLICATION_MODAL);
		}
	}
	
	public void addChild(JFXNode<? extends Node> uiControl) {
		if(!(uiControl instanceof JFXWindow)) {
			super.addChild(uiControl);
		}
	}
	
	public Stage getStage() {
		return stage;
	}

	public String getText() {
		return this.getStage().getTitle();
	}

	public void setText(String text) {
		this.getStage().setTitle(text);
	}

	public UIImage getImage() {
		return this.image;
	}

	public void setImage(UIImage image) {
		this.image = image;
	}
	
	public UIMenuBar getMenuBar() {
		return this.menuBar;
	}

	public void setMenuBar(UIMenuBar menuBar) {
		if( menuBar != null ) {
			JFXMenuBar jfxMenuBar = ((JFXMenuBar) menuBar);
			jfxMenuBar.getControl().prefWidthProperty().bind(this.getStage().getScene().widthProperty());
			jfxMenuBar.getControl().heightProperty().addListener(new JFXMenuResizeListener(this));
			
			this.getControl().getChildren().add(jfxMenuBar.getControl());
		} else if(this.menuBar != null ) {
			this.getControl().getChildren().remove(((JFXMenuBar) this.menuBar).getControl());
		}
		this.menuBar = menuBar;
	}
	
	public void open() {
		this.getStage().show();
	}

	public void close() {
		if(!this.closeListener.isEmpty() ) {
			this.closeListener.fireEvent();
		} else {
			this.dispose();
		}
	}
	
	public void dispose() {
		this.getStage().close();
		
		super.dispose();
		
		this.leave();
	}
	
	public boolean isVisible() {
		return this.getStage().isShowing();
	}
	
	@Override
	public void setVisible(boolean visible) {
		if(!this.isVisible() && visible) {
			this.open();
		} else if(this.isVisible() && !visible) {
			this.close();
		}
	}
	
	public void setBounds(UIRectangle bounds) {
		if(!this.getBounds().equals(bounds)) {
			this.resizeListener.setBounds(bounds);
			this.getStage().setX(bounds.getX());
			this.getStage().setY(bounds.getY());
			this.getStage().setWidth(bounds.getWidth());
			this.getStage().setHeight(bounds.getHeight());
		}
		super.setBounds(this.getSceneBounds());
	}
	
	@Override
	public UIRectangle getBounds() {
		UIRectangle bounds = new UIRectangle();
		bounds.getPosition().setX(Math.round(this.getStage().getX()));
		bounds.getPosition().setY(Math.round(this.getStage().getY()));
		bounds.getSize().setWidth(Math.round(this.getStage().getWidth()));
		bounds.getSize().setHeight(Math.round(this.getStage().getHeight()));
		
		return bounds;
	}
	
	public UIRectangle getSceneBounds() {
		UIRectangle bounds = new UIRectangle();
		bounds.getSize().setWidth(Math.round(this.getStage().getScene().getWidth()));
		bounds.getSize().setHeight(Math.round(this.getStage().getScene().getHeight()));
		
		return bounds;
	}
	
	public UIRectangle getChildArea() {
		UIRectangle childArea = this.getChildArea(this.getSceneBounds().getSize());
		if( this.getMenuBar() != null ) {
			childArea.getPosition().setY((float)((JFXMenuBar) this.menuBar).getControl().getHeight());
		}
		return childArea;
	}
	
	public Insets getPadding() {
		Insets padding = super.getPadding();
		if( this.getMenuBar() != null ) {
			MenuBar menuBar = ((JFXMenuBar) this.menuBar).getControl();
			
			padding = new Insets((padding.getTop() + menuBar.getHeight()), padding.getRight(), padding.getBottom(), padding.getLeft());
		}
		return padding;
	}
	
	public UISize getDecorationSize() {
		double stageWidth = this.getStage().getWidth();
		double stageHeight = this.getStage().getHeight();
		double sceneWidth = this.getStage().getScene().getWidth();
		double sceneHeight = this.getStage().getScene().getHeight();
		if(!Double.isNaN(stageWidth) && !Double.isNaN(stageHeight) && sceneWidth > 0 && sceneHeight > 0) {
			this.decorationSize.setWidth((float) (stageWidth - sceneWidth));
			this.decorationSize.setHeight((float) (stageHeight - sceneHeight));
		}
		return new UISize(this.decorationSize.getWidth(), this.decorationSize.getHeight());
	}
	
	public void setDecorationSize(UISize decorationSize) {
		this.decorationSize.setWidth(decorationSize != null ? decorationSize.getWidth() : 0f);
		this.decorationSize.setHeight(decorationSize != null ? decorationSize.getHeight() : 0f);
	}
	
	@Override
	public void computePackedSize() {
		super.computePackedSize();
		
		UISize decorationSize = this.getDecorationSize();
		UISize packedSize = this.getPackedSize();
		packedSize.setWidth(packedSize.getWidth() + decorationSize.getWidth());
		packedSize.setHeight(packedSize.getHeight() + decorationSize.getHeight());
		this.setPackedSize(packedSize);
	}
	
	public void join() {
		this.joined = true;
		Toolkit.getToolkit().enterNestedEventLoop(this.getStage());
	}
	
	public void leave() {
		if( this.joined ) {
			this.joined = false;
			Toolkit.getToolkit().exitNestedEventLoop(this.getStage(), null);
		}
	}
	
	public void minimize() {
		this.getStage().setIconified(true);
	}
	
	public void maximize() {
		this.getStage().setMaximized(true);
	}
	
	public boolean isMaximized() {
		return this.getStage().isMaximized();
	}
	
	public void moveToTop() {
		this.getStage().setAlwaysOnTop(true);
	}
	
	public void addCloseListener(UICloseListener listener) {
		this.closeListener.addListener(listener);
	}

	public void removeCloseListener(UICloseListener listener) {
		this.closeListener.removeListener(listener);
	}
	
	private class JFXWindowResizeListener implements ChangeListener<Number> {
		
		private UIRectangle bounds;
		private JFXWindow window;
		
		public JFXWindowResizeListener(JFXWindow window) {
			this.window = window;
		}
		
	    public void changed(ObservableValue<? extends Number> observableValue, Number oldSize, Number newSize) {
			UIRectangle bounds = this.window.getBounds();
			if( this.bounds == null || !this.bounds.equals(bounds)) {
				this.bounds = bounds;
				this.window.layout();
			}
		}
	    
		public void setBounds(UIRectangle bounds) {
			this.bounds = bounds;
		}
	}
	
	private class JFXWindowCloseListener implements EventHandler<WindowEvent> {
		
		private JFXWindow window;
		
		public JFXWindowCloseListener(JFXWindow window) {
			this.window = window;
		}
		
		public void handle(WindowEvent event) {
			event.consume();
			
			this.window.close();
		}
	}
	
	private class JFXMenuResizeListener implements ChangeListener<Number> {
		
		private JFXWindow window;
		
		public JFXMenuResizeListener(JFXWindow window) {
			this.window = window;
		}
		
	    public void changed(ObservableValue<? extends Number> observableValue, Number oldSize, Number newSize) {
	    	this.window.layout();
		}
	}
}
