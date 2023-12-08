package org.herac.tuxguitar.android.browser.gdrive.gdaa;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.activity.TGActivityController;
import org.herac.tuxguitar.android.activity.TGActivityResultHandler;
import org.herac.tuxguitar.android.browser.model.TGBrowser;
import org.herac.tuxguitar.android.browser.model.TGBrowserCallBack;
import org.herac.tuxguitar.android.browser.model.TGBrowserElement;
import org.herac.tuxguitar.android.browser.model.TGBrowserElementComparator;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.android.gdrive.gdaa.R;
import org.herac.tuxguitar.util.TGContext;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;

public class TGDriveBrowser implements TGBrowser{
	
	private TGContext context;
	private TGDriveBrowserSettings settings;
	private TGDriveBrowserFolder folder;
	private GoogleApiClient client;
	private int loginRequest;
	
	public TGDriveBrowser(TGContext context, TGDriveBrowserSettings settings){
		this.context = context;
		this.settings = settings;
		this.loginRequest = this.findActivity().getResultManager().createRequestCode();
	}
	
	public void open(final TGBrowserCallBack<Object> cb){
		try {
			GoogleApiClient.Builder builder = new GoogleApiClient.Builder(findActivity());
			builder.addApi(Drive.API);
		    builder.addScope(Drive.SCOPE_FILE);
		    
		    builder.addConnectionCallbacks(
		    	new GoogleApiClient.ConnectionCallbacks() {
					@Override
					public void onConnected(Bundle bundle) {
						cb.onSuccess(null);
					}
					
					@Override
					public void onConnectionSuspended(int cause) {
						cb.handleError(new TGBrowserException(findActivity().getString(R.string.gdrive_connection_suspended)));
					}
				}
		    );
		    builder.addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
				public void onConnectionFailed(ConnectionResult result) {
					if(!result.hasResolution()) {
						cb.handleError(new TGBrowserException(findActivity().getString(R.string.gdrive_connection_failed, result.getErrorCode())));
					} else {
					    try {
					    	final TGActivity tgActivity = findActivity();
					    	tgActivity.getResultManager().addHandler(TGDriveBrowser.this.loginRequest, new TGActivityResultHandler() {
								public void onActivityResult(int resultCode, Intent data) {
									tgActivity.getResultManager().removeHandler(TGDriveBrowser.this.loginRequest, this);
									
									if( Activity.RESULT_OK == resultCode ) {
										TGDriveBrowser.this.client.connect();
									} else {
										cb.handleError(new TGBrowserException(findActivity().getString(R.string.gdrive_login_failed)));
									}
								}
							});
					        
					    	result.startResolutionForResult(tgActivity, TGDriveBrowser.this.loginRequest);
					    } catch (SendIntentException e) {
					    	cb.handleError(new TGBrowserException(findActivity().getString(R.string.gdrive_unexpected_error), e));
					    }
					}
				}
			});
			if(!this.settings.isDefaultAccount()) {
				builder.setAccountName(this.settings.getAccount());
			}
			
			this.folder = null;
			this.client = builder.build();
			this.client.connect();
		} catch (RuntimeException e) {
			cb.handleError(e);
		}
	}
	
	public void close(TGBrowserCallBack<Object> cb){
		try{
			this.folder = null;
			this.client.disconnect();
			
			cb.onSuccess(null);
		} catch (RuntimeException e) {
			cb.handleError(e);
		}
	}
	
	public void cdElement(TGBrowserCallBack<Object> cb, TGBrowserElement element) {
		try{
			if( element.isFolder() ) {
				this.folder = (TGDriveBrowserFolder) element;
			}
			cb.onSuccess(this.folder);
		} catch (RuntimeException e) {
			cb.handleError(e);
		}
	}
	
	public void cdRoot(TGBrowserCallBack<Object> cb) {
		try{
			this.folder = new TGDriveBrowserFolder(null, Drive.DriveApi.getRootFolder(this.client), "/");
			
			cb.onSuccess(this.folder);
		} catch (RuntimeException e) {
			cb.handleError(e);
		}
	}
	
	public void cdUp(TGBrowserCallBack<Object> cb) {
		try {
			if( this.folder != null && this.folder.getParent() != null ){
				this.folder = this.folder.getParent();
			}
			
			cb.onSuccess(this.folder);
		} catch (RuntimeException e) {
			cb.handleError(e);
		}
	}
	
	public void listElements(final TGBrowserCallBack<List<TGBrowserElement>> cb) {
		try {
			if( this.folder != null ) {
				this.folder.getFolder().listChildren(this.client).setResultCallback(new ResultCallback<MetadataBufferResult>() {
					public void onResult(MetadataBufferResult result) {
			            if( result.getStatus().isSuccess() ) {
			            	List<TGBrowserElement> elements = new ArrayList<TGBrowserElement>();
			            	
			            	Iterator<Metadata> it = result.getMetadataBuffer().iterator();
			            	while(it.hasNext()) {
			            		Metadata metadata = it.next();
			            		DriveId driveId = metadata.getDriveId();
			            		String name = metadata.getTitle();
			            		
			            		if(!metadata.isTrashed() && !metadata.isExplicitlyTrashed()) {
				            		if( metadata.isFolder() ) {
				            			elements.add(new TGDriveBrowserFolder(TGDriveBrowser.this.folder, Drive.DriveApi.getFolder(TGDriveBrowser.this.client, driveId), name));
				            		} else {
				            			elements.add(new TGDriveBrowserFile(TGDriveBrowser.this.folder, Drive.DriveApi.getFile(TGDriveBrowser.this.client, driveId), name));
				            		}
			            		}
			            	}
			            	
							if( !elements.isEmpty() ){
								Collections.sort(elements, new TGBrowserElementComparator());
							}
							
			            	cb.onSuccess(elements);
			            } else {
			            	cb.handleError(new TGBrowserException(findActivity().getString(R.string.gdrive_list_children_error)));
			            }
					};
				});
			} else {
				cb.onSuccess(new ArrayList<TGBrowserElement>());
			}
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}

	public void createElement(final TGBrowserCallBack<TGBrowserElement> cb, final String name) {
		try {
			MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(name).build();
			
			this.folder.getFolder().createFile(this.client, changeSet, null).setResultCallback(new ResultCallback<DriveFileResult>() {
				public void onResult(DriveFileResult result) {
					if( result.getStatus().isSuccess() ) {
						cb.onSuccess(new TGDriveBrowserFile(TGDriveBrowser.this.folder, result.getDriveFile(), name));
					} else {
						cb.handleError(new TGBrowserException(findActivity().getString(R.string.gdrive_create_file_error)));
					}
				}
			});
		} catch (RuntimeException e) {
			cb.handleError(e);
		}
	}

	public void getInputStream(final TGBrowserCallBack<InputStream> cb, TGBrowserElement element) {
		try {
			DriveFile driveFile = ((TGDriveBrowserFile) element).getFile();
			driveFile.open(this.client, DriveFile.MODE_READ_ONLY, null).setResultCallback(new ResultCallback<DriveContentsResult>() {
				public void onResult(DriveContentsResult result) {
					try {
						if( result.getStatus().isSuccess() ) {
							cb.onSuccess(TGDriveBrowserUtil.getInputStream(result.getDriveContents().getInputStream()));
						} else {
							cb.handleError(new TGBrowserException(findActivity().getString(R.string.gdrive_read_file_error)));
						}
					} catch (Throwable e) {
						cb.handleError(e);
					}
				}
			});
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}
	
	public void getOutputStream(final TGBrowserCallBack<OutputStream> cb, final TGBrowserElement element) {
		try {
			DriveFile driveFile = ((TGDriveBrowserFile) element).getFile();
			driveFile.open(TGDriveBrowser.this.client, DriveFile.MODE_WRITE_ONLY, null).setResultCallback(new ResultCallback<DriveContentsResult>() {
				public void onResult(final DriveContentsResult result) {
					if( result.getStatus().isSuccess() ) {
						cb.onSuccess(new TGDriveBrowserOutputStream(result.getDriveContents().getOutputStream(), new Runnable() {
							public void run() {
								result.getDriveContents().commit(TGDriveBrowser.this.client, null);
							}
						}));
					} else {
						cb.handleError(new TGBrowserException(findActivity().getString(R.string.gdrive_write_file_error)));
					}
				}
			});
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}
	
	public boolean isWritable() {
		return true;
	}
	
	public TGActivity findActivity() {
		return TGActivityController.getInstance(this.context).getActivity();
	}
}
