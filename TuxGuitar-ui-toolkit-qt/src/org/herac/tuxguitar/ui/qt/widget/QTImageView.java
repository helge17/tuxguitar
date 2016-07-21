package org.herac.tuxguitar.ui.qt.widget;

import org.herac.tuxguitar.ui.qt.resource.QTImage;
import org.herac.tuxguitar.ui.resource.UIImage;
import org.herac.tuxguitar.ui.widget.UIImageView;

import com.trolltech.qt.gui.QLabel;

public class QTImageView extends QTWidget<QLabel> implements UIImageView {
	
	private UIImage image;
	
	public QTImageView(QTContainer parent) {
		super(new QLabel(parent.getContainerControl()), parent);
	}

	public UIImage getImage() {
		return this.image;
	}

	public void setImage(UIImage image) {
		this.image = image;
		this.getControl().setPixmap(this.image != null ? ((QTImage) this.image).createPixmap() : null);
	}
}