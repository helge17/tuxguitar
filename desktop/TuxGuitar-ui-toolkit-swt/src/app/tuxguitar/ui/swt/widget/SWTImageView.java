package app.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.swt.resource.SWTImage;
import app.tuxguitar.ui.widget.UIImageView;

public class SWTImageView extends SWTControl<Label> implements UIImageView {

	private UIImage image;

	public SWTImageView(SWTContainer<? extends Composite> parent) {
		super(new Label(parent.getControl(), SWT.NONE), parent);
	}

	public UIImage getImage() {
		return this.image;
	}

	public void setImage(UIImage image) {
		this.image = image;

		this.getControl().setImage(this.image != null ? ((SWTImage) this.image).getHandle() : null);
	}
}
