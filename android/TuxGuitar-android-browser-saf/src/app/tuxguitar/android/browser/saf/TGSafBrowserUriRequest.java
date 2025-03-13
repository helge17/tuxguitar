package org.herac.tuxguitar.android.browser.saf;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;

import org.herac.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import org.herac.tuxguitar.android.action.impl.gui.TGStartActivityForResultAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.activity.TGActivityController;
import org.herac.tuxguitar.android.view.dialog.confirm.TGConfirmDialogController;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;

import java.util.List;

@SuppressLint("NewApi")
public class TGSafBrowserUriRequest {

	private static final String EXTRA_SHOW_ADVANCED = "android.content.extra.SHOW_ADVANCED";

	private TGContext context;
	private TGSafBrowserUriHandler handler;
	private Uri uri;

	public TGSafBrowserUriRequest(TGContext context, TGSafBrowserUriHandler handler, Uri uri) {
		this.context = context;
		this.handler = handler;
		this.uri = uri;
	}

	public TGSafBrowserUriRequest(TGContext context, TGSafBrowserUriHandler handler) {
		this(context, handler, null);
	}

	public void process() {
		if( this.uri == null || !this.hasUriAccess()) {
			this.requestUriAccess();
		} else {
			this.handleOnUriAccessGrantedInNewThread();
		}
	}

	public boolean hasUriAccess() {
		if( this.uri != null ) {
			List<UriPermission> uriPermissions = this.getActivity().getContentResolver().getPersistedUriPermissions();
			for (UriPermission uriPermission : uriPermissions) {
				if (this.uri.equals(uriPermission.getUri()) && uriPermission.isReadPermission()) {
					return true;
				}
			}
		}
		return false;
	}

	public void requestUriAccess() {
		String confirmMessage = this.getActivity().getString(R.string.browser_settings_saf_uri_request_msg);
		this.callConfirmableAction(confirmMessage, new Runnable() {
			public void run() {
				requestUriAccessIntentInNewThread();
			}
		});
	}

	public void requestUriAccessIntentInNewThread() {
		new Thread(new Runnable() {
			public void run() {
				requestUriAccessIntentInCurrentThread();
			}
		}).start();
	}

	public void requestUriAccessIntentInCurrentThread() {
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
		intent.putExtra(EXTRA_SHOW_ADVANCED, true);
		TGSafBrowserUriResult handler = new TGSafBrowserUriResult(this);

		this.callStartActivityForResult(intent, handler.getRequestCode());
	}

	public void callConfirmableAction(String confirmMessage, Runnable runnable) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGOpenDialogAction.NAME);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, new TGConfirmDialogController());
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, this.getActivity());
		tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_MESSAGE, confirmMessage);
		tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_RUNNABLE, runnable);
		tgActionProcessor.process();
	}

	public void callStartActivityForResult(Intent intent, Integer requestCode) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGStartActivityForResultAction.NAME);
		tgActionProcessor.setAttribute(TGStartActivityForResultAction.ATTRIBUTE_ACTIVITY, this.getActivity());
		tgActionProcessor.setAttribute(TGStartActivityForResultAction.ATTRIBUTE_INTENT, intent);
		tgActionProcessor.setAttribute(TGStartActivityForResultAction.ATTRIBUTE_REQUEST_CODE, requestCode);
		tgActionProcessor.process();
	}

	public void onUriAccessGranted(Uri uri) {
		this.uri = uri;
		if( this.uri != null ) {
			this.getActivity().getContentResolver().takePersistableUriPermission(this.uri, (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
		}

		this.handleOnUriAccessGrantedInNewThread();
	}

	public void handleOnUriAccessGrantedInNewThread() {
		new Thread(new Runnable() {
			public void run() {
				handleOnUriAccessGrantedInCurrentThread();
			}
		}).start();
	}

	public void handleOnUriAccessGrantedInCurrentThread() {
		if( this.handler != null ) {
			this.handler.onUriAccessGranted(this.uri);
		}
	}

	public TGActivity getActivity() {
		return TGActivityController.getInstance(this.context).getActivity();
	}
}
