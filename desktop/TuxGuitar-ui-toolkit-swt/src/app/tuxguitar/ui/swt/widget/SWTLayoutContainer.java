package app.tuxguitar.ui.swt.widget;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import app.tuxguitar.ui.layout.UILayout;
import app.tuxguitar.ui.resource.UIRectangle;
import app.tuxguitar.ui.resource.UISize;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.ui.widget.UILayoutContainer;

public abstract class SWTLayoutContainer<T extends Composite> extends SWTControl<T> implements SWTContainer<T>, UILayoutContainer {

	private List<UIControl> children;
	private UILayout layout;
	private UISize packedContentSize;

	public SWTLayoutContainer(T control, SWTContainer<? extends Composite> parent) {
		super(control, parent);

		this.children = new ArrayList<UIControl>();
		this.packedContentSize = new UISize();
	}

	public List<UIControl> getChildren() {
		List<UIControl> children = new ArrayList<UIControl>(this.children);
		for(UIControl child : children) {
			if( child.isDisposed()) {
				this.removeChild(child);
			}
		}

		return this.children;
	}

	public void addChild(UIControl uiControl) {
		this.children.add(uiControl);
	}

	public void removeChild(UIControl uiControl) {
		this.children.remove(uiControl);
	}

	public UILayout getLayout() {
		return layout;
	}

	public void setLayout(UILayout layout) {
		this.layout = layout;
	}

	public void setPackedContentSize(UISize packedContentSize) {
		this.packedContentSize.setWidth(packedContentSize.getWidth());
		this.packedContentSize.setHeight(packedContentSize.getHeight());
	}

	public UISize getPackedContentSize() {
		return new UISize(this.packedContentSize.getWidth(), this.packedContentSize.getHeight());
	}

	public UIRectangle getChildArea() {
		Rectangle area = this.getControl().getClientArea();
		return new UIRectangle(area.x, area.y, area.width, area.height);
	}

	public void computePackedSize(Float fixedWidth, Float fixedHeight) {
		if( this.layout != null ) {
			UISize packedContentSize = this.layout.computePackedSize(this);
			Rectangle trim = this.getControl().computeTrim(0, 0, Math.round(packedContentSize.getWidth()), Math.round(packedContentSize.getHeight()));

			this.setPackedSize(new UISize(trim.width, trim.height));
			this.setPackedContentSize(packedContentSize);
		} else {
			for(UIControl uiControl : this.getChildren()) {
				uiControl.computePackedSize(null, null);
			}
		}

		UISize packedSize = this.getPackedSize();
		if( fixedWidth != null && fixedWidth != packedSize.getWidth() ) {
			packedSize.setWidth(fixedWidth);
		}
		if( fixedHeight != null && fixedHeight != packedSize.getHeight() ) {
			packedSize.setHeight(fixedHeight);
		}
		this.setPackedSize(packedSize);
	}

	public void setBounds(UIRectangle bounds) {
		super.setBounds(bounds);

		if( this.layout != null ) {
			// workaround for a macOS specific issue, leading to incorrect dialog display
			// https://github.com/helge17/tuxguitar/issues/361
			UIRectangle childArea = this.getChildArea();
			if ((childArea.getPosition().getY()<0f) && (this.getControl().getParent() != null)) {
				// TODO, delete log
				try {
					throw new Exception("Fixing negative value Y=" + String.valueOf(childArea.getPosition().getY()));
				}
				catch (Throwable e) {
					e.printStackTrace();
				}
				
				// a layout container should not be drawn outside of its parent
				childArea.getPosition().setY(0f);
			}
			this.layout.setBounds(this, childArea);
		}
		else {
			for(UIControl uiControl : this.getChildren()) {
				uiControl.setBounds(uiControl.getBounds());
			}
		}
	}

	public void layout() {
		this.layout(this.getBounds());
	}

	public void layout(UIRectangle bounds) {
		this.computePackedSize(null, null);
		this.setBounds(bounds);
	}

	public void pack() {
		this.computePackedSize(null, null);
		this.setBounds(new UIRectangle(this.getBounds().getPosition(), this.getPackedSize()));
	}
}
