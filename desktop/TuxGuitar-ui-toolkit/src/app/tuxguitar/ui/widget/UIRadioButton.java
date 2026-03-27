package app.tuxguitar.ui.widget;

import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.resource.UIImage;

public interface UIRadioButton extends UIControl {

	String getText();

	void setText(String text);

	UIImage getImage();

	void setImage(UIImage image);

	void setToolTipText(String text);

	boolean isSelected();

	void setSelected(boolean selected);

	void addSelectionListener(UISelectionListener listener);

	void removeSelectionListener(UISelectionListener listener);
}
