package app.tuxguitar.ui.qt.widget;

import app.tuxguitar.ui.qt.resource.QTImage;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.widget.UIImageView;
import org.qtjambi.qt.widgets.QLabel;

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