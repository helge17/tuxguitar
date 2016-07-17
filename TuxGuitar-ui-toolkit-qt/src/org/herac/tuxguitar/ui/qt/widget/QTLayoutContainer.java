package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.layout.UILayout;
import org.herac.tuxguitar.ui.resource.UIRectangle;
import org.herac.tuxguitar.ui.resource.UISize;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;

import com.trolltech.qt.core.QSize;
import com.trolltech.qt.gui.QContentsMargins;
import com.trolltech.qt.gui.QWidget;

public abstract class QTLayoutContainer<T extends QWidget> extends QTAbstractContainer<T> implements UILayoutContainer {
	
	private UILayout layout;
	private UISize packedContentSize;
	
	public QTLayoutContainer(T control, QTContainer parent) {
		super(control, parent);
		
		this.packedContentSize = new UISize();
	}
	
	public QWidget getContainerControl() {
		return this.getControl();
	}
	
	public QSize getContainerSize() {
		return this.getContainerControl().size();
	}
	
	public QContentsMargins getContainerMargins() {
		return this.getContainerControl().getContentsMargins();
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
		QSize size = this.getContainerSize();
		QContentsMargins margins = this.getContainerMargins();
		
		return new UIRectangle(margins.left, margins.top, size.width() - (margins.left + margins.right), size.height() - (margins.top + margins.bottom));
	}
	
	public void computePackedSize() {
		for(UIControl uiControl : this.getChildren()) {
			uiControl.computePackedSize();
		}
		
		if( this.layout != null ) {
			UISize packedContentSize = this.layout.computePackedSize(this);
			
			this.setPackedContentSize(packedContentSize);
			this.computePackedSizeFor(packedContentSize);
		}
	}
	
	public void computePackedSizeFor(UISize packedContentSize) {
		QContentsMargins margins = this.getContainerMargins();
		
		UISize packedSize = new UISize();
		packedSize.setWidth((float) (margins.left + margins.right + packedContentSize.getWidth()));
		packedSize.setHeight((float) (margins.top + margins.bottom + packedContentSize.getHeight()));
		
		this.setPackedSize(packedSize);
	}
	
	public void setBounds(UIRectangle bounds) {
		super.setBounds(bounds);
		
		if( this.layout != null ) {
			this.layout.setBounds(this, this.getChildArea());
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
		this.computePackedSize();
		this.setBounds(bounds);
	}

	public void pack() {
		this.computePackedSize();
		this.setBounds(new UIRectangle(this.getBounds().getPosition(), this.getPackedSize()));
	}
}
