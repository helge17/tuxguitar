package org.herac.tuxguitar.android.activity;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class TGActivityPermissionRequest {

	private TGActivity activity;
	private TGActivityPermissionResultHandler resultHandler;
	private String[] permissions;
	private Runnable onPermissionGranted;
	private Runnable onPermissionDenied;
	private int requestCode;

	public TGActivityPermissionRequest(TGActivity activity, String[] permissions, Runnable onPermissionGranted, Runnable onPermissionDenied) {
		this.activity = activity;
		this.permissions = permissions;
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
		this.checkPermissionsAsyncTask().execute((Void) null);
	}
	
	private void onPermissionGranted() {
		this.removeResultHandlers();
		if( this.onPermissionGranted != null ) {
			this.onPermissionGranted.run();
		}
	}
	
	private void onPermissionDenied() {
		this.removeResultHandlers();
		if( this.onPermissionDenied != null ) {
			this.onPermissionDenied.run();
		}
	}

	private void checkPermissions() {
		List<String> missingPermissions = new ArrayList<String>();
		for(String permission : this.permissions) {
			if (ContextCompat.checkSelfPermission(this.activity, permission) != PackageManager.PERMISSION_GRANTED) {
				missingPermissions.add(permission);
			}
		}

		if( missingPermissions.isEmpty()) {
			this.onPermissionGranted();

		} else {
			System.out.println("************** missingPermissions " + missingPermissions.size());

			ActivityCompat.requestPermissions(this.activity, missingPermissions.toArray(new String[missingPermissions.size()]), this.requestCode);
		}
	}

	private void processPermissionRequestResult(int[] grantResults) {
		boolean permissionsGranted = true;
		for(int grantResult : grantResults) {
			if( grantResult != PackageManager.PERMISSION_GRANTED ) {
				permissionsGranted = false;
			}
		}
		
		if( permissionsGranted ) {
			this.onPermissionGranted();
		} else {
			this.onPermissionDenied();
		}
	}
	
	private AsyncTask<Void, Void, Void> checkPermissionsAsyncTask() {
		return new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
				checkPermissions();
				
				return null;
			}
		};
	}

	private TGActivityPermissionResultHandler createPermissionRequestResultHandler() {
		return new TGActivityPermissionResultHandler() {
			public void onRequestPermissionsResult(String[] permissions, int[] grantResults) {
				processPermissionRequestResult(grantResults);
			}
		};
	}
}
