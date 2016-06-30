package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.jfx.event.JFXSelectionListenerChangeManager;
import org.herac.tuxguitar.ui.jfx.resource.JFXSnapshotImage;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.widget.UICheckBox;

public class JFXCheckBox extends JFXControl<CheckBox> implements UICheckBox {
	
	private JFXSelectionListenerChangeManager<Boolean> selectionListener;
	
	private UIImage image;
	
	public JFXCheckBox(JFXContainer<? extends Region> parent) {
		super(new CheckBox(), parent);
		
		this.selectionListener = new JFXSelectionListenerChangeManager<Boolean>(this);
	}

	public String getText() {
		return this.getControl().getText();
	}

	public void setText(String text) {
		this.getControl().setText(text);
	}

	public UIImage getImage() {
		return this.image;
	}

	public void setImage(UIImage image) {
		this.image = image;
		this.getControl().setGraphic(this.image != null ? new ImageView(((JFXSnapshotImage) this.image).getHandle()) : null);
	}
	
	public boolean isSelected() {
		return this.getControl().isSelected();
	}

	public void setSelected(boolean selected) {
		this.getControl().setSelected(selected);
	}

	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().selectedProperty().addListener(this.selectionListener);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().selectedProperty().removeListener(this.selectionListener);
		}
	}
}
