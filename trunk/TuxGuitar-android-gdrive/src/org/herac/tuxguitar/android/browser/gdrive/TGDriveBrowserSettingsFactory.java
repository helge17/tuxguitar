package org.herac.tuxguitar.android.browser.gdrive;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.common.AccountPicker;
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;

import org.herac.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.activity.TGActivityController;
import org.herac.tuxguitar.android.activity.TGActivityResultHandler;
import org.herac.tuxguitar.android.browser.TGBrowserManager;
import org.herac.tuxguitar.android.browser.model.TGBrowserFactorySettingsHandler;
import org.herac.tuxguitar.android.browser.model.TGBrowserSettings;
import org.herac.tuxguitar.android.gdrive.R;
import org.herac.tuxguitar.android.view.dialog.message.TGMessageDialogController;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;

public class TGDriveBrowserSettingsFactory implements TGActivityResultHandler {

	private TGContext context;
	private TGActivity activity;
	private TGBrowserFactorySettingsHandler handler;
	private int requestCode;
	
	public TGDriveBrowserSettingsFactory(TGContext context, TGBrowserFactorySettingsHandler handler) {
		this.context = context;
		this.handler = handler;
		this.activity = TGActivityController.getInstance(this.context).getActivity();
		this.requestCode = this.activity.getResultManager().createRequestCode();
	}

	public void createSettings() {
		Intent chooseAccountIntent = AccountPicker.newChooseAccountIntent(null, null, new String[] {GoogleAccountManager.ACCOUNT_TYPE}, true, null, null, null, null);
		
		this.activity.getResultManager().addHandler(this.requestCode, this);
		this.activity.startActivityForResult(chooseAccountIntent, this.requestCode);
	}
	
	public void onActivityResult(int resultCode, Intent data) {
		this.activity.getResultManager().removeHandler(this.requestCode, this);
		
		if( Activity.RESULT_OK == resultCode ) {
			String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
			if( accountName != null ) {
				TGBrowserSettings settings = new TGDriveBrowserSettings(this.activity.getString(R.string.gdrive_settings_title, accountName), accountName).toBrowserSettings();
				if( TGBrowserManager.getInstance(this.context).getCollection(TGDriveBrowserFactory.BROWSER_TYPE, settings) == null ) {
					this.handler.onCreateSettings(settings);
				} else {
					this.showErrorMessage(this.activity.getString(R.string.gdrive_settings_error_msg_exists, settings.getTitle()));
				}
			}
		}
	}
	
	public void showErrorMessage(String message) {
		this.showErrorMessage(this.activity.getString(R.string.gdrive_settings_error_title), message);
	}
	
	public void showErrorMessage(String title, String message) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGOpenDialogAction.NAME);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, this.activity);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, new TGMessageDialogController());
		tgActionProcessor.setAttribute(TGMessageDialogController.ATTRIBUTE_TITLE, title);
		tgActionProcessor.setAttribute(TGMessageDialogController.ATTRIBUTE_MESSAGE, message);
		tgActionProcessor.process();
	}
}
