package org.herac.tuxguitar.android.view.processing;

import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.util.TGException;
import org.herac.tuxguitar.util.TGSynchronizer;

import android.app.ProgressDialog;

public class TGActionProcessingView {
	
	private TGActivity activity;
	private ProgressDialog dialog;
	
	private boolean visible;
	private boolean updating;
	
	public TGActionProcessingView(TGActivity activity){
		this.activity = activity;
		this.createProgressDialog();
	}
	
	private void createProgressDialog() {
		this.dialog = new ProgressDialog(this.activity);
		this.dialog.setMessage("Processing");
		this.dialog.setIndeterminate(true);
		this.dialog.setCancelable(false);
	}
	
	private void dismissProgressDialog() {
		if(!this.isDestroyed()) {
			this.dialog.dismiss();
			this.dialog = null;
		}
	}
	
	private void updateProgressDialog(boolean visible) {
		if(!this.isDestroyed()) {
			if( visible ) {
				this.dialog.show();
			} else {
				this.dialog.hide();
			}
			this.visible = visible;
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
	}
	
	public boolean isVisible() {
		return visible;
	}

	public boolean isUpdating() {
		return updating;
	}
	
	public boolean isDestroyed() {
		return (this.dialog == null || this.activity.isDestroyed());
	}
}
