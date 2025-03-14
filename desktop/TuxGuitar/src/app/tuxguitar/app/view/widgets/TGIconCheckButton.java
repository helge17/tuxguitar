package app.tuxguitar.app.view.widgets;

import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.widget.UILayoutContainer;

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
