package org.herac.tuxguitar.android.view.dialog.message;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.fragment.TGDialogFragment;

import androidx.appcompat.app.AlertDialog;

public class TGMessageDialog extends TGDialogFragment {
	
	public TGMessageDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
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
