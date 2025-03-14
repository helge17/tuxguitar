package app.tuxguitar.ui.qt.toolbar;

import app.tuxguitar.ui.qt.resource.QTImage;
import app.tuxguitar.ui.resource.UIImage;
import io.qt.widgets.QAbstractButton;

public class QTToolAbstractButtonItem<T extends QAbstractButton> extends QTToolItem<T> {

	private UIImage image;

	public QTToolAbstractButtonItem(T button, QTToolBar parent) {
		super(button, parent);
	}

	public String getText() {
		return this.getControl().text();
	}

	public void setText(String text) {
		this.getControl().setText(text);
	}

	public UIImage getImage() {
		return this.image;
	}

	public void setImage(UIImage image) {
		this.image = image;
		this.getControl().setIcon(this.image != null ? ((QTImage) this.image).createIcon() : null);
	}
}
