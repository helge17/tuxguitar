package org.herac.tuxguitar.android.view.dialog.confirm;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

public class TGConfirmDialog extends TGDialog {
	
	public TGConfirmDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.confirm_dlg_title);
		builder.setMessage(this.getMessage());
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				getRunnable().run();
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(R.string.global_button_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		
		return builder.create();
	}

	public String getMessage() {
		return this.getAttribute(TGConfirmDialogController.ATTRIBUTE_MESSAGE);
	}

	public Runnable getRunnable() {
		return this.getAttribute(TGConfirmDialogController.ATTRIBUTE_RUNNABLE);
	}
}
