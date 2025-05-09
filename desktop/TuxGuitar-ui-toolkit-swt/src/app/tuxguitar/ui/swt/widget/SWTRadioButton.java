package app.tuxguitar.ui.swt.widget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.swt.event.SWTSelectionListenerManager;
import app.tuxguitar.ui.swt.resource.SWTImage;
import app.tuxguitar.ui.widget.UIRadioButton;

public class SWTRadioButton extends SWTControl<Button> implements UIRadioButton {

	private SWTSelectionListenerManager selectionListener;

	private UIImage image;

	public SWTRadioButton(SWTContainer<? extends Composite> parent) {
		super(new Button(parent.getControl(), SWT.RADIO), parent);

		this.selectionListener = new SWTSelectionListenerManager(this);
	}

	public String getText() {
		return this.getControl().getText();
	}

	public void setText(String text) {
		this.getControl().setText(text);
	}

	public UIImage getImage() {
		return this.image;
	}

	public void setImage(UIImage image) {
		this.image = image;

		this.getControl().setImage(this.image != null ? ((SWTImage) this.image).getHandle() : null);
	}

	public boolean isSelected() {
		return this.getControl().getSelection();
	}

	public void setSelected(boolean selected) {
		this.getControl().setSelection(selected);
	}

	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().addSelectionListener(this.selectionListener);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().removeSelectionListener(this.selectionListener);
		}
	}

	// 01/2024, workaround for an external bug visible in Linux SWT/KDE configuration
	// to be deleted one day
	// see https://github.com/helge17/tuxguitar/issues/52
	@Override
	public void computePackedSize(Float fixedWidth, Float fixedHeight) {
		super.computePackedSize(fixedWidth, fixedHeight);
		this.packedSize.setWidth(this.packedSize.getWidth() + 10.0f);
	}

}
