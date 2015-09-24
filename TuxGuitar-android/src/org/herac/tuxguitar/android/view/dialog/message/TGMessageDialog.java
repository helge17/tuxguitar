package org.herac.tuxguitar.android.view.dialog.message;

import org.herac.tuxguitar.android.activity.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.android.view.dialog.TGDialogContext;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class TGMessageDialog extends TGDialog {
	
	public TGMessageDialog(TGDialogContext dialogContext) {
		super(dialogContext);
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String title = this.getAttribute(TGMessageDialogController.ATTRIBUTE_TITLE);
		String message = this.getAttribute(TGMessageDialogController.ATTRIBUTE_MESSAGE);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		return builder.create();
	}
}
