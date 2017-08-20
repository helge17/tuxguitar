package org.herac.tuxguitar.ui.jfx.widget;

import org.herac.tuxguitar.ui.event.UICloseListener;
import org.herac.tuxguitar.ui.jfx.event.JFXCloseListenerManager;
import org.herac.tuxguitar.ui.jfx.menu.JFXMenuBar;
import org.herac.tuxguitar.ui.jfx.resource.JFXImage;
import org.herac.tuxguitar.ui.menu.UIMenuBar;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIInset;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIWindow;

import com.sun.javafx.tk.Toolkit;

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

public class JFXWindow extends JFXPaneContainer<Pane> implements UIWindow {
	
	private static final float DEFAULT_WINDOW_WIDTH = 640f;
	private static final float DEFAULT_WINDOW_HEIGHT = 480f;
	
	private boolean joined;
	private Stage stage;
	private UIImage image;
	private UIMenuBar menuBar;
	private UIInset margin;
	private JFXCloseListenerManager closeListener;
	private JFXWindowResizeListener resizeListener;
	
	public JFXWindow(Stage stage, JFXContainer<? extends Pane> parent) {
		super(new Pane(), parent);
		
		this.stage = stage;
		this.stage.setScene(new Scene(getControl()));
		this.stage.getScene().getStylesheets().add("styles/styles.css");
		this.stage.setWidth(DEFAULT_WINDOW_WIDTH);
		this.stage.setHeight(DEFAULT_WINDOW_HEIGHT);
		this.margin = new UIInset();
		
		this.closeListener = new JFXCloseListenerManager(this);
		this.resizeListener = new JFXWindowResizeListener(this);
		this.stage.getScene().widthProperty().addListener(this.resizeListener);
		this.stage.getScene().heightProperty().addListener(this.resizeListener);
		this.stage.setOnCloseRequest(new JFXWindowCloseListener(this));
	}
	
	public JFXWindow(JFXWindow parent, boolean modal, boolean resizable) {
		this(new Stage(), parent);
		
		this.setMargin(parent.getMargin());
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
		this.getStage().getIcons().clear();
		this.getStage().getIcons().add(((JFXImage) image).getHandle());
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
			this.getStage().setX(bounds.getX());
			this.getStage().setY(bounds.getY());
			this.getStage().setWidth(bounds.getWidth());
			this.getStage().setHeight(bounds.getHeight());
		}
		UIRectangle sceneBounds = this.getSceneBounds();
		
		this.resizeListener.setSceneBounds(sceneBounds);
		
		super.setBounds(sceneBounds);
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
	
	public UIInset getMargin() {
		float x = (float) this.getStage().getScene().getX();
		float y = (float) this.getStage().getScene().getY();
		if( x > 0 ) {
			this.margin.setLeft(x);
			this.margin.setRight(x);
			this.margin.setBottom(x);
		}
		if( y > 0 ) {
			this.margin.setTop(y);
		}
		return new UIInset(this.margin.getTop(), this.margin.getLeft(), this.margin.getRight(), this.margin.getBottom());
	}
	
	public void setMargin(UIInset margin) {
		this.margin.setTop(margin != null ? margin.getTop() : 0f);
		this.margin.setLeft(margin != null ? margin.getLeft() : 0f);
		this.margin.setRight(margin != null ? margin.getRight() : 0f);
		this.margin.setBottom(margin != null ? margin.getBottom() : 0f);
	}
	
	@Override
	public void computePackedSize() {
		super.computePackedSize();
		
		UIInset margin = this.getMargin();
		UISize packedSize = this.getPackedSize();
		packedSize.setWidth(packedSize.getWidth() + margin.getLeft() + margin.getRight());
		packedSize.setHeight(packedSize.getHeight() + margin.getTop() + margin.getBottom());
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
			UIRectangle bounds = this.window.getSceneBounds();
			if( this.bounds == null || !this.bounds.equals(bounds)) {
				this.bounds = bounds;
				this.window.layout();
			}
		}
	    
		public void setSceneBounds(UIRectangle bounds) {
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
