package org.herac.tuxguitar.android.view.dialog;

import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.application.TGApplicationUtil;
import org.herac.tuxguitar.util.TGContext;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public abstract class TGDialog extends DialogFragment {
	
	public TGDialog() {
		super();
	}
	
	public abstract Dialog onCreateDialog();
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if( this.getDialogContext() != null ) {
			return this.onCreateDialog();
		}
		return null;
	}
	
	public TGDialogContext getDialogContext() {
		return this.findContext().getAttribute(TGDialogContext.class.getName());
	}
	
	public <T> T getAttribute(String key){
		TGDialogContext dialogContext = this.getDialogContext();
		if( dialogContext != null ) {
			return dialogContext.getAttribute(key);
		}
		return null;
	}
	
	public TGActivity findActivity() { 
		return (TGActivity) getActivity();
	}
	
	public TGContext findContext() { 
		return TGApplicationUtil.findContext(getActivity());
	}
}
