package app.tuxguitar.app.view.widgets;

import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.event.UIPaintEvent;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.widget.UICanvas;
import app.tuxguitar.ui.widget.UILayoutContainer;

import java.util.ArrayList;
import java.util.List;

public abstract class TGIconButton {

	private List<UISelectionListener> listeners;
	private UICanvas canvas;

	private UIImage icon;
	private UIImage hoveredIcon;
	private boolean hovered;

	public TGIconButton(UIFactory factory, UILayoutContainer parent) {
		this.listeners = new ArrayList<>();
		this.canvas = factory.createCanvas(parent, false);
		this.canvas.addPaintListener(this::paint);
		this.canvas.addMouseUpListener(this::clicked);
		this.canvas.addMouseEnterListener(event -> setHovered(true));
		this.canvas.addMouseExitListener(event -> setHovered(false));
	}

	private void setHovered(boolean hovered) {
		if (this.hovered != hovered) {
			this.hovered = hovered;
			this.canvas.redraw();
		}
	}

	protected boolean isHovered() {
		return this.hovered;
	}

	protected UIImage getDisplayedImage() {
		if (this.icon == null || (this.hovered && this.hoveredIcon != null)) {
			return this.hoveredIcon;
		}
		return this.icon;
	}

	private void paint(UIPaintEvent event) {
		UIPainter painter = event.getPainter();
		UIImage image = getDisplayedImage();
		if (image != null) {
			resizeTo(image);
			painter.drawImage(image, 0, 0);
		}
	}

	private void clicked(UIMouseEvent event) {
		if (this.canvas.isEnabled() && event.getButton() == 1) {
			for (UISelectionListener listener : this.listeners) {
				listener.onSelect(new UISelectionEvent(this.canvas));
			}
		}
	}

	public void setIcon(UIImage icon) {
		if (icon != this.icon) {
			this.icon = icon;
			if (getDisplayedImage() == icon) {
				resizeTo(icon);
				this.canvas.redraw();
			}
		}
	}

	public void setHoveredIcon(UIImage icon) {
		if (icon != this.hoveredIcon) {
			this.hoveredIcon = icon;
			if (getDisplayedImage() == icon) {
				resizeTo(icon);
				this.canvas.redraw();
			}
		}
	}

	protected void resizeTo(UIImage image) {
		final UIRectangle area = this.canvas.getBounds();
		float w = image.getWidth();
		float h = image.getHeight();
		if (w != area.getWidth() || h != area.getHeight()) {
			UILayoutContainer parent = (UILayoutContainer) this.canvas.getParent();
			if (parent != null) {
				UITableLayout layout = (UITableLayout) parent.getLayout();
				layout.set(this.canvas, UITableLayout.PACKED_WIDTH, w);
				layout.set(this.canvas, UITableLayout.PACKED_HEIGHT, h);
				parent.layout();
			}
		}
	}

	public UICanvas getControl() {
		return this.canvas;
	}

	public void addSelectionListener(UISelectionListener listener) {
		this.listeners.add(listener);
	}
}
