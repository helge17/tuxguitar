package org.herac.tuxguitar.ui.jfx.widget;

import org.herac.tuxguitar.ui.event.UICloseListener;
import org.herac.tuxguitar.ui.jfx.appearance.JFXAppearance;
import org.herac.tuxguitar.ui.jfx.event.JFXCloseListenerManager;
import org.herac.tuxguitar.ui.jfx.menu.JFXMenuBar;
import org.herac.tuxguitar.ui.jfx.resource.JFXImage;
import org.herac.tuxguitar.ui.jfx.util.JFXSyncProcess;
import org.herac.tuxguitar.ui.menu.UIMenuBar;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIInset;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIWindow;

import javafx.application.Platform;
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
	
	private boolean packing;
	private Stage stage;
	private UIImage image;
	private UIMenuBar menuBar;
	private UIInset margin;
	private JFXCloseListenerManager closeListener;
	private JFXSceneResizeListener sceneResizeListener;
	private JFXStageResizeListener stageResizeListener;
	private JFXSyncProcess layoutProcess;
	
	public JFXWindow(Stage stage, JFXContainer<? extends Pane> parent) {
		super(new Pane(), parent);
		
		this.packing = false;
		this.stage = stage;
		this.stage.setScene(new Scene(getControl()));
		this.stage.getScene().getStylesheets().add(JFXAppearance.CSS_RESOURCE);
		this.stage.setWidth(DEFAULT_WINDOW_WIDTH);
		this.stage.setHeight(DEFAULT_WINDOW_HEIGHT);
		this.margin = new UIInset();
		
		this.layoutProcess = new JFXSyncProcess(new JFXLayoutProcess(this));
		this.closeListener = new JFXCloseListenerManager(this);
		this.sceneResizeListener = new JFXSceneResizeListener(this);
		this.stageResizeListener = new JFXStageResizeListener(this);
		this.stage.widthProperty().addListener(this.stageResizeListener);
		this.stage.heightProperty().addListener(this.stageResizeListener);
		this.stage.getScene().widthProperty().addListener(this.sceneResizeListener);
		this.stage.getScene().heightProperty().addListener(this.sceneResizeListener);
		this.stage.setOnCloseRequest(new JFXWindowCloseListener(this));
	}
	
	public JFXWindow(JFXWindow parent, boolean modal, boolean resizable) {
		this(new Stage(), parent);
		
		this.stage.initOwner(parent.getStage());
	//	this.stage.setResizable(resizable);
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
	
	public void setImageFromParent() {
		if( this.getParent() != null ) {
			UIImage parentImage = ((JFXWindow) this.getParent()).getImage();
			if( parentImage != null ) {
				this.setImage(parentImage);
			}
		}
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
	
	public void pack() {
		this.packing = true;
		
		super.pack();
	}
	
	public void repack() {
		this.packing = true;
		
		final UIInset emptyMargins = new UIInset();
		
		Platform.runLater(new Runnable() {
			public void run() {
				UIInset currentMargins = JFXWindow.this.getMargin();
				
				JFXWindow.this.computeMargin();
				if( JFXWindow.this.getMargin().equals(currentMargins) && !emptyMargins.equals(currentMargins)) {
					JFXWindow.this.getStage().setResizable(true);
					JFXWindow.this.pack();
					JFXWindow.this.packing = false;
				} else {
					Platform.runLater(this);
				}
			}
		});
	}
	
	public void open() {
		if( this.getImage() == null ) {
			this.setImageFromParent();
		}
		
		this.getStage().show();
		
		if( this.packing ) {
			this.repack();
		}
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
		
		if( this.getMenuBar() != null && !this.getMenuBar().isDisposed()) {
			this.getMenuBar().dispose();
		}
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
		
		this.sceneResizeListener.setSceneBounds(sceneBounds);
		
		super.setBounds(sceneBounds);
	}
	
	@Override
	public UIRectangle getBounds() {
		UIRectangle bounds = new UIRectangle();
		bounds.getPosition().setX((float)(this.getStage().getX()));
		bounds.getPosition().setY((float)(this.getStage().getY()));
		bounds.getSize().setWidth((float)(this.getStage().getWidth()));
		bounds.getSize().setHeight((float)(this.getStage().getHeight()));
		
		return bounds;
	}
	
	public UIRectangle getSceneBounds() {
		UIRectangle bounds = new UIRectangle();
		bounds.getSize().setWidth((float)(this.getStage().getScene().getWidth()));
		bounds.getSize().setHeight((float)(this.getStage().getScene().getHeight()));
		
		return bounds;
	}
	
	public UIRectangle getChildArea() {
		UIRectangle childArea = this.getChildArea(this.getSceneBounds().getSize());
		if( this.getMenuBar() != null && !this.getMenuBar().isDisposed()) {
			childArea.getPosition().setY((float)((JFXMenuBar) this.menuBar).getControl().getHeight());
		}
		return childArea;
	}
	
	public Insets getPadding() {
		Insets padding = super.getPadding();
		if( this.getMenuBar() != null && !this.getMenuBar().isDisposed()) {
			MenuBar menuBar = ((JFXMenuBar) this.menuBar).getControl();
			
			padding = new Insets((padding.getTop() + menuBar.getHeight()), padding.getRight(), padding.getBottom(), padding.getLeft());
		}
		return padding;
	}
	
	public UIInset getMargin() {
		return new UIInset(this.margin.getTop(), this.margin.getLeft(), this.margin.getRight(), this.margin.getBottom());
	}
	
	public void setMargin(UIInset margin) {
		this.margin.setTop(margin != null ? margin.getTop() : 0f);
		this.margin.setLeft(margin != null ? margin.getLeft() : 0f);
		this.margin.setRight(margin != null ? margin.getRight() : 0f);
		this.margin.setBottom(margin != null ? margin.getBottom() : 0f);
	}
	
	public void computeMargin() {
		if( this.margin.equals(new UIInset())) {
			float stateWidth = (float) this.getStage().getWidth();
			float stateHeight = (float) this.getStage().getHeight();
			
			float sceneX = (float) this.getStage().getScene().getX();
			float sceneY = (float) this.getStage().getScene().getY();
			float sceneWidth = (float) this.getStage().getScene().getWidth();
			float sceneHeight = (float) this.getStage().getScene().getHeight();
	
			if( sceneWidth > 1 && stateWidth >= sceneWidth  && sceneHeight > 1 && stateHeight >= sceneHeight) {
				this.margin.setLeft(sceneX);
				this.margin.setTop(sceneY);
				this.margin.setRight(stateWidth - sceneWidth - sceneX);
				this.margin.setBottom(stateHeight - sceneHeight - sceneY);
			}
		}
	}
	
	@Override
	public void computePackedSize(Float fixedWidth, Float fixedHeight) {
		super.computePackedSize(null, null);
		
		UIInset margin = this.getMargin();
		UISize packedSize = this.getPackedSize();
		packedSize.setWidth(fixedWidth != null ? fixedWidth : packedSize.getWidth() + margin.getLeft() + margin.getRight());
		packedSize.setHeight(fixedHeight != null ? fixedHeight : packedSize.getHeight() + margin.getTop() + margin.getBottom());
		this.setPackedSize(packedSize);
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
	
	public void syncLayout() {
		this.layoutProcess.process();
	}
	
	public void addCloseListener(UICloseListener listener) {
		this.closeListener.addListener(listener);
	}

	public void removeCloseListener(UICloseListener listener) {
		this.closeListener.removeListener(listener);
	}
	
	private class JFXStageResizeListener implements ChangeListener<Number> {
		
		private JFXWindow window;
		
		public JFXStageResizeListener(JFXWindow window) {
			this.window = window;
		}
		
	    public void changed(ObservableValue<? extends Number> observableValue, Number oldSize, Number newSize) {
			this.window.computeMargin();
		}
	}
	
	private class JFXSceneResizeListener implements ChangeListener<Number> {
		
		private UIRectangle bounds;
		private JFXWindow window;
		
		public JFXSceneResizeListener(JFXWindow window) {
			this.window = window;
		}
		
	    public void changed(ObservableValue<? extends Number> observableValue, Number oldSize, Number newSize) {
			this.window.computeMargin();
			
			UIRectangle bounds = this.window.getSceneBounds();
			if( this.bounds == null || !this.bounds.equals(bounds)) {
				this.bounds = bounds;
				this.window.syncLayout();
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
	    	this.window.syncLayout();
		}
	}
	
	private class JFXLayoutProcess implements Runnable {
		
		private JFXWindow window;
		
		public JFXLayoutProcess(JFXWindow window) {
			this.window = window;
		}
		
		public void run() {
			this.window.layout();
		}
	}
}
