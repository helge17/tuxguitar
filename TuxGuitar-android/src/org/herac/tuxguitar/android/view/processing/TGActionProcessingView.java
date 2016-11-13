package org.herac.tuxguitar.android.view.processing;

import android.app.ProgressDialog;

import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGActionProcessingView {

	private TGActivity activity;
	private ProgressDialog dialog;

	private boolean updating;
	private boolean destroyed;
	
	public TGActionProcessingView(TGActivity activity){
		this.activity = activity;
	}
	
	private void createProgressDialog() {
		if(!this.isDestroyed() && !this.isVisible()) {
			this.dialog = new ProgressDialog(this.activity);
			this.dialog.setMessage("Processing");
			this.dialog.setIndeterminate(true);
			this.dialog.setCancelable(false);
			this.dialog.show();
		}
	}
	
	private void dismissProgressDialog() {
		if(!this.isDestroyed() && this.isVisible()) {
			this.dialog.dismiss();
			this.dialog = null;
		}
	}
	
	private void updateProgressDialog(boolean visible) {
		if( visible ) {
			this.createProgressDialog();
		} else {
			this.dismissProgressDialog();
		}
	}
	
	public void postUpdateProgressDialog(final boolean visible) {
		if(!this.isDestroyed()) {
			this.updating = true;
			
			TGSynchronizer.getInstance(this.activity.findContext()).executeLater(new Runnable() {
				public void run() throws TGException {
					TGActionProcessingView.this.updateProgressDialog(visible);
					TGActionProcessingView.this.updating = false;
				}
			});
		}
	}

	public void destroy() {
		this.dismissProgressDialog();
		this.destroyed = true;
	}
	
	public boolean isVisible() {
		return (this.dialog != null);
	}

	public boolean isUpdating() {
		return updating;
	}
	
	public boolean isDestroyed() {
		return (this.destroyed || this.activity.isDestroyed());
	}
}
