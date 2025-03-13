package app.tuxguitar.ui.widget;

import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.resource.UIImage;

public interface UIToggleButton extends UIControl {

	String getText();

	void setText(String text);

	String getToolTipText();

	void setToolTipText(String text);

	boolean isSelected();

	void setSelected(boolean selected);

	UIImage getImage();

	void setImage(UIImage image);

	void setDefaultButton();

	void addSelectionListener(UISelectionListener listener);

	void removeSelectionListener(UISelectionListener listener);
}
