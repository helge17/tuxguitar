package org.herac.tuxguitar.android.browser.gdrive;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.DriveScopes;

import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.activity.TGActivityResultHandler;
import org.herac.tuxguitar.android.browser.model.TGBrowserCallBack;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.android.gdrive.R;

import java.util.Collections;

public class TGDriveBrowserLogin {

	private int authRequestCode;
	private int accountRequestCode;
	
	private TGActivity activity;
	private TGDriveBrowserSettings settings;
	private TGBrowserCallBack<GoogleAccountCredential> callback;
	
	private TGActivityResultHandler authRequestResultHandler;
	private TGActivityResultHandler accountRequestResultHandler;
	private GoogleAccountCredential credential;
	
	public TGDriveBrowserLogin(TGActivity activity, TGDriveBrowserSettings settings, TGBrowserCallBack<GoogleAccountCredential> callback) {
		this.activity = activity;
		this.settings = settings;
		this.callback = callback;
		this.authRequestCode = this.activity.getResultManager().createRequestCode();
		this.accountRequestCode = this.activity.getResultManager().createRequestCode();
	}

	public void process() {
		this.authRequestResultHandler = this.createAuthRequestResultHandler();
		this.accountRequestResultHandler = this.createAccountRequestResultHandler();
		
		this.activity.getResultManager().addHandler(this.authRequestCode, this.authRequestResultHandler);
    	this.activity.getResultManager().addHandler(this.accountRequestCode, this.accountRequestResultHandler);
    	
		this.credential = GoogleAccountCredential.usingOAuth2(this.activity, Collections.singleton(DriveScopes.DRIVE));
		if(!this.settings.isDefaultAccount()) {
			this.credential.setSelectedAccountName(this.settings.getAccount());
		} else {
			String defaultAccount = this.getDefaultAccount();
			if( defaultAccount != null ) {
				this.credential.setSelectedAccountName(defaultAccount);
			}
		}
		this.createTokenAsyncTask().execute((Void) null);
	}
	
	private void onSuccess() {
		this.removeResultHandlers();
		this.callback.onSuccess(this.credential);
	}
	
	private void handleError(Throwable throwable) {
		this.removeResultHandlers();
		this.callback.handleError(throwable);
	}
	
	private void removeResultHandlers() {
		this.activity.getResultManager().removeHandler(this.authRequestCode, this.authRequestResultHandler);
    	this.activity.getResultManager().removeHandler(this.accountRequestCode, this.accountRequestResultHandler);
	}
	
	private String getDefaultAccount() {
		Account[] accounts = this.credential.getAllAccounts();
		if( accounts != null ) {
			for(Account account : accounts) {
				if( GoogleAccountManager.ACCOUNT_TYPE.equals(account.type) ) {
					return account.name;
				}
			}
		}
		return null;
	}
	
	private void createToken() {
		try {
			if( this.credential.getToken() != null ) {
				this.credential.getGoogleAccountManager().invalidateAuthToken(this.credential.getToken());
			}
			
			if( this.credential.getToken() != null ) {
				this.onSuccess();
			} else {
				this.handleError(new TGBrowserException(this.activity.getString(R.string.gdrive_login_failed)));
			}
	    } catch (UserRecoverableAuthException e) {
	    	this.activity.startActivityForResult(e.getIntent(), this.authRequestCode);
	    } catch (UserRecoverableAuthIOException e) {
	    	this.activity.startActivityForResult(e.getIntent(), this.authRequestCode);
		} catch (Throwable e) {
			this.activity.startActivityForResult(this.credential.newChooseAccountIntent(), this.accountRequestCode);
		}
	}
	
	private void processAccountRequestResult(int resultCode, Intent data) {
		if( Activity.RESULT_OK == resultCode ) {
			String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
			if (accountName != null) {
				this.credential.setSelectedAccountName(accountName);
				this.createTokenAsyncTask().execute((Void) null);
			} else {
				this.handleError(new TGBrowserException(this.activity.getString(R.string.gdrive_login_failed)));
			}
		} else {
			this.handleError(new TGBrowserException(this.activity.getString(R.string.gdrive_login_failed)));
		}
	}
	
	private void processAuthRequestResult(int resultCode) {
		if( Activity.RESULT_OK == resultCode ) {
			this.createTokenAsyncTask().execute((Void) null);
		} else {
			this.handleError(new TGBrowserException(TGDriveBrowserLogin.this.activity.getString(R.string.gdrive_login_failed)));
		}
	}
	
	private AsyncTask<Void, Void, Void> createTokenAsyncTask() {
		return new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
				createToken();
				
				return null;
			}
		};
	}
	
	private TGActivityResultHandler createAccountRequestResultHandler() {
		return new TGActivityResultHandler() {
			public void onActivityResult(int resultCode, Intent data) {
				processAccountRequestResult(resultCode, data);
			}
		};
	}
	
	private TGActivityResultHandler createAuthRequestResultHandler() {
		return new TGActivityResultHandler() {
			public void onActivityResult(int resultCode, Intent data) {
				processAuthRequestResult(resultCode);
			}
		};
	}
}
