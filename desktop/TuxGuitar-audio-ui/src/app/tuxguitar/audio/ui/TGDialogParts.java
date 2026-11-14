package app.tuxguitar.audio.ui;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UIWindow;

/**
 * Parts of the audio dialogs, named by their message keys, at a cell of the dialog's table layout: a row's label
 * (column 1) and a button (OK, Cancel).
 */
final class TGDialogParts {

	private TGDialogParts() {
	}

	static void label(UIFactory factory, UIWindow dialog, int row, String key) {
		UILabel label = factory.createLabel(dialog);
		label.setText(TuxGuitar.getProperty(key) + ":");
		((UITableLayout) dialog.getLayout()).set(label, row, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
	}

	static void button(UIFactory factory, UIWindow dialog, int row, int column, String key, Runnable action) {
		UIButton button = factory.createButton(dialog);
		button.setText(TuxGuitar.getProperty(key));
		button.addSelectionListener(event -> action.run());
		((UITableLayout) dialog.getLayout()).set(button, row, column, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false, 1, 1, 80f, 25f, null);
	}
}
