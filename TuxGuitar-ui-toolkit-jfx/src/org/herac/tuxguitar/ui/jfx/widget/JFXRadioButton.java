package org.herac.tuxguitar.ui.jfx.widget;

import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.jfx.event.JFXSelectionListenerChangeManager;
import org.herac.tuxguitar.ui.jfx.resource.JFXSnapshotImage;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.widget.UIRadioButton;

public class JFXRadioButton extends JFXControl<RadioButton> implements UIRadioButton {
	
	private JFXSelectionListenerChangeManager<Boolean> selectionListener;
	
	private UIImage image;
	
	public JFXRadioButton(JFXContainer<? extends Region> parent) {
		super(new RadioButton(), parent);
		
		this.selectionListener = new JFXSelectionListenerChangeManager<Boolean>(this);
		this.getControl().setToggleGroup(this.findToggleGroup());
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

	public ToggleGroup findToggleGroup() {
		if( this.getParent() != null ) {
			ToggleGroup toggleGroup = this.getParent().getData(ToggleGroup.class.getName());
			if( toggleGroup != null ) {
				return toggleGroup;
			}
			this.getParent().setData(ToggleGroup.class.getName(), new ToggleGroup());
			
			return this.findToggleGroup();
		}
		return null;
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
