package org.herac.tuxguitar.android.view.dialog;

import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.application.TGApplication;
import org.herac.tuxguitar.util.TGContext;

import android.app.DialogFragment;

public abstract class TGDialog extends DialogFragment {

	private TGDialogContext dialogContext;
	
	public TGDialog(TGDialogContext dialogContext) {
		this.dialogContext = dialogContext;
	}

	public TGDialogContext getDialogContext() {
		return dialogContext;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key){
		Object value = this.getDialogContext().getAttribute(key);
		
		return (T) value;
	}
	
	public TGActivity findActivity() { 
		return (TGActivity) getActivity();
	}
	
	public TGContext findContext() { 
		return ((TGApplication) getActivity().getApplicationContext()).getContext();
	}
}
