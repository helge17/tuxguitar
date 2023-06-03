package org.herac.tuxguitar.app.view.widgets;

import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UIPaintEvent;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.resource.UIPainter;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.widget.UICanvas;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;

import java.util.ArrayList;
import java.util.List;

public class TGIconCheckButton extends TGIconButton {

	private UIImage selectedIcon;
	private UIImage selectedHoveredIcon;
	private boolean selected;
	
	public TGIconCheckButton(UIFactory factory, UILayoutContainer parent) {
		super(factory, parent);
	}
	
	@Override
	protected UIImage getDisplayedImage() {
		UIImage image = null;
		if (selected) {
			if (this.selectedIcon == null || (this.isHovered() && this.selectedHoveredIcon != null)) {
				image = this.selectedHoveredIcon;
			} else {
				image = this.selectedIcon;
			}
		}
		if (image == null) {
			return super.getDisplayedImage();
		}
		return image;
	}
	
	public void setSelected(boolean selected) {
		if (this.selected != selected) {
			this.selected = selected;
			this.getControl().redraw();
		}
	}
	
	public void setSelectedIcon(UIImage icon) {
		if (icon != this.selectedIcon) {
			this.selectedIcon = icon;
			if (getDisplayedImage() == icon) {
				resizeTo(icon);
				this.getControl().redraw();
			}
		}
	}
	
	public void setSelectedHoveredIcon(UIImage icon) {
		if (icon != this.selectedHoveredIcon) {
			this.selectedHoveredIcon = icon;
			if (getDisplayedImage() == icon) {
				resizeTo(icon);
				this.getControl().redraw();
			}
		}
	}
}
