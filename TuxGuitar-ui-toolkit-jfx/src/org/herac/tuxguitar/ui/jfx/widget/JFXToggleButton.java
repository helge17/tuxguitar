package org.herac.tuxguitar.ui.jfx.widget;

import javafx.event.ActionEvent;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.jfx.event.JFXSelectionListenerManager;
import org.herac.tuxguitar.ui.jfx.resource.JFXSnapshotImage;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.widget.UIToggleButton;

public class JFXToggleButton extends JFXLabeled<ToggleButton> implements UIToggleButton {
	
	private JFXSelectionListenerManager<ActionEvent> selectionListener;
	
	private UIImage image;
	
	public JFXToggleButton(JFXContainer<? extends Region> parent) {
		super(new ToggleButton(), parent);
		
		this.selectionListener = new JFXSelectionListenerManager<ActionEvent>(this);
	}

	public boolean isSelected() {
		return this.getControl().isSelected();
	}

	public void setSelected(boolean selected) {
		this.getControl().setSelected(selected);
	}
	
	public UIImage getImage() {
		return this.image;
	}

	public void setImage(UIImage image) {
		this.image = image;
		this.getControl().setGraphic(this.image != null ? new ImageView(((JFXSnapshotImage) this.image).getHandle()) : null);
	}

	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().setOnAction(this.selectionListener);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().setOnAction(null);
		}
	}
	
	public void setDefaultButton() {
		// not supported
	}
}
