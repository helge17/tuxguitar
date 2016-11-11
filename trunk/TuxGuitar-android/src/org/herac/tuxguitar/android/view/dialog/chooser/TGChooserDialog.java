package org.herac.tuxguitar.android.view.dialog.chooser;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import org.herac.tuxguitar.android.view.dialog.TGDialog;

import java.util.ArrayList;
import java.util.List;

public class TGChooserDialog<T> extends TGDialog {

	public TGChooserDialog() {
		super();
	}

	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		final String title = this.getAttribute(TGChooserDialogController.ATTRIBUTE_TITLE);
		final TGChooserDialogHandler<T> handler = this.getAttribute(TGChooserDialogController.ATTRIBUTE_HANDLER);
		final List<TGChooserDialogOption<T>> options = this.getAttribute(TGChooserDialogController.ATTRIBUTE_OPTIONS);

		List<CharSequence> items = new ArrayList<CharSequence>();
		for(TGChooserDialogOption<T> option : options) {
			items.add(option.getLabel());
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title);
		builder.setItems(items.toArray(new CharSequence[items.size()]) , new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if( which >= 0 && which < options.size() ) {
					onChooseInNewThread(handler, options.get(which).getValue());
				}
			}
		});

		return builder.create();
	}

	public void onChooseInNewThread(final TGChooserDialogHandler<T> handler, final T value) {
		new Thread(new Runnable() {
			public void run() {
				handler.onChoose(value);
			}
		}).start();
	}
}
