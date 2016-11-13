package org.herac.tuxguitar.android.activity;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.herac.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import org.herac.tuxguitar.android.action.impl.gui.TGRequestPermissionsAction;
import org.herac.tuxguitar.android.view.dialog.confirm.TGConfirmDialogController;
import org.herac.tuxguitar.editor.action.TGActionProcessor;

import java.util.ArrayList;
import java.util.List;

public class TGActivityPermissionRequest {

	private TGActivity activity;
	private TGActivityPermissionResultHandler resultHandler;
	private String[] permissions;
	private String permissionRationale;
	private Runnable onPermissionGranted;
	private Runnable onPermissionDenied;
	private int requestCode;

	public TGActivityPermissionRequest(TGActivity activity, String[] permissions, String permissionRationale, Runnable onPermissionGranted, Runnable onPermissionDenied) {
		this.activity = activity;
		this.permissions = permissions;
		this.permissionRationale = permissionRationale;
		this.onPermissionGranted = onPermissionGranted;
		this.onPermissionDenied = onPermissionDenied;
		this.requestCode = this.activity.getPermissionResultManager().createRequestCode();
		this.resultHandler = this.createPermissionRequestResultHandler();
	}

	private void addResultHandlers() {
		this.activity.getPermissionResultManager().addHandler(this.requestCode, this.resultHandler);
	}

	private void removeResultHandlers() {
		this.activity.getPermissionResultManager().removeHandler(this.requestCode, this.resultHandler);
	}

	public void process() {
		this.addResultHandlers();
		this.checkPermissionsAsyncTask(false).execute((Void) null);
	}
	
	private void onPermissionGranted() {
		this.removeResultHandlers();
		if( this.onPermissionGranted != null ) {
			this.createThreadRunnable(this.onPermissionGranted).run();
		}
	}
	
	private void onPermissionDenied() {
		this.removeResultHandlers();
		if( this.onPermissionDenied != null ) {
			this.createThreadRunnable(this.onPermissionDenied).run();
		}
	}

	private void checkPermissions(boolean ignoreRationale) {
		List<String> missingPermissions = new ArrayList<String>();
		for(String permission : this.permissions) {
			if (ContextCompat.checkSelfPermission(this.activity, permission) != PackageManager.PERMISSION_GRANTED) {
				missingPermissions.add(permission);
			}
		}

		if( missingPermissions.isEmpty()) {
			this.onPermissionGranted();
		} else {
			String[] requiredPermissions = missingPermissions.toArray(new String[missingPermissions.size()]);
			if( ignoreRationale || !this.isShowingRequestPermissionRationale(requiredPermissions) ) {
				this.callRequestPermissions(requiredPermissions);
			}
		}
	}

	private void processPermissionRequestResult(String[] permissions, int[] grantResults) {
		boolean permissionsGranted = true;
		for(int grantResult : grantResults) {
			if( grantResult != PackageManager.PERMISSION_GRANTED ) {
				permissionsGranted = false;
			}
		}

		if( permissionsGranted ) {
			this.onPermissionGranted();
		} else if(!this.isShowingRequestPermissionRationale(permissions)){
			this.onPermissionDenied();
		}
	}

	private boolean isShowingRequestPermissionRationale(String[] permissions) {
		if( this.permissionRationale != null ) {
			for (String permission : permissions) {
				if (ActivityCompat.shouldShowRequestPermissionRationale(this.activity, permission)) {
					showRequestPermissionRationale();

					return true;
				}
			}
		}
		return false;
	}

	private void callRequestPermissions(String[] requiredPermissions) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.activity.findContext(), TGRequestPermissionsAction.NAME);
		tgActionProcessor.setAttribute(TGRequestPermissionsAction.ATTRIBUTE_ACTIVITY, this.activity);
		tgActionProcessor.setAttribute(TGRequestPermissionsAction.ATTRIBUTE_PERMISSIONS, requiredPermissions);
		tgActionProcessor.setAttribute(TGRequestPermissionsAction.ATTRIBUTE_REQUEST_CODE, this.requestCode);
		tgActionProcessor.process();
	}

	private void showRequestPermissionRationale() {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.activity.findContext(), TGOpenDialogAction.NAME);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, new TGConfirmDialogController());
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, this.activity);
		tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_MESSAGE, this.permissionRationale);
		tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_RUNNABLE, new Runnable() {
			public void run() {
				checkPermissionsAsyncTask(true).execute((Void) null);
			}
		});
		tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_CANCEL_RUNNABLE, new Runnable() {
			public void run() {
				onPermissionDenied();
			}
		});
		tgActionProcessor.process();
	}

	public Runnable createThreadRunnable(final Runnable target) {
		return new Runnable() {
			public void run() {
				new Thread(target).start();
			}
		};
	}

	private AsyncTask<Void, Void, Void> checkPermissionsAsyncTask(final boolean ignoreRationale) {
		return new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
				checkPermissions(ignoreRationale);

				return null;
			}
		};
	}

	private TGActivityPermissionResultHandler createPermissionRequestResultHandler() {
		return new TGActivityPermissionResultHandler() {
			public void onRequestPermissionsResult(String[] permissions, int[] grantResults) {
				processPermissionRequestResult(permissions, grantResults);
			}
		};
	}
}
