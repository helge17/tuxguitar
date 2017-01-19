package org.herac.tuxguitar.android.browser.gdrive;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.activity.TGActivityController;
import org.herac.tuxguitar.android.browser.model.TGBrowser;
import org.herac.tuxguitar.android.browser.model.TGBrowserCallBack;
import org.herac.tuxguitar.android.browser.model.TGBrowserElement;
import org.herac.tuxguitar.android.browser.model.TGBrowserElementComparator;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.android.gdrive.R;
import org.herac.tuxguitar.util.TGContext;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveRequest;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

public class TGDriveBrowser implements TGBrowser {
	
	private static final String ROOT_FOLDER = "root";
	
	private TGContext context;
	private TGDriveBrowserSettings settings;
	
	private TGDriveBrowserFile folder;
	private HttpTransport httpTransport;
	private Drive drive;
	
	public TGDriveBrowser(TGContext context, TGDriveBrowserSettings settings){
		this.context = context;
		this.settings = settings;
	}
	
	public void open(final TGBrowserCallBack<Object> cb){
		try {
			this.drive = null;
			this.folder = null;
			this.httpTransport = AndroidHttp.newCompatibleTransport();
			
			TGDriveBrowserLogin login = new TGDriveBrowserLogin(this.findActivity(), this.settings, new TGBrowserCallBack<GoogleAccountCredential>() {
				public void onSuccess(GoogleAccountCredential credential) {
					Drive.Builder builder = new Drive.Builder(TGDriveBrowser.this.httpTransport, GsonFactory.getDefaultInstance(), credential);
					builder.setApplicationName(findActivity().getString(R.string.gdrive_application_name));
					TGDriveBrowser.this.drive = builder.build();
					
					cb.onSuccess(null);
				}
				
				public void handleError(Throwable throwable) {
					cb.handleError(throwable);
				}
			});
			login.process();
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}
	
	public void close(TGBrowserCallBack<Object> cb){
		try{
			this.drive = null;
			this.folder = null;
			
			if( this.httpTransport != null ) {
				this.httpTransport.shutdown();
				this.httpTransport = null;
			}
			
			cb.onSuccess(this.folder);
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}
	
	public void cdElement(TGBrowserCallBack<Object> cb, TGBrowserElement element) {
		try{
			if( element.isFolder() ) {
				this.folder = (TGDriveBrowserFile) element;
			}
			cb.onSuccess(this.folder);
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}
	
	public void cdRoot(TGBrowserCallBack<Object> cb) {
		try {
			File file = new File();
			file.setId(ROOT_FOLDER);
			
			this.folder = new TGDriveBrowserFile(file, null);
			
			cb.onSuccess(this.folder);
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}
	
	public void cdUp(TGBrowserCallBack<Object> cb) {
		try {
			if( this.folder != null && this.folder.getParent() != null ){
				this.folder = this.folder.getParent();
			}
			
			cb.onSuccess(this.folder);
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}
	
	public void listElements(final TGBrowserCallBack<List<TGBrowserElement>> cb) {
		try {
			if( this.folder != null ) {
				List<TGBrowserElement> elements = new ArrayList<TGBrowserElement>();
				
				Files.List request = this.drive.files().list();
				request.setQ("'" + this.folder.getFile().getId() + "' in parents");
				request.setOrderBy("title");
				
				do {
					FileList files = request.execute();
					for(File file : files.getItems()) {
						if(!file.getExplicitlyTrashed() ) {
							elements.add(new TGDriveBrowserFile(file, this.folder));
						}
					}
					
					request.setPageToken(files.getNextPageToken());
				} while (request.getPageToken() != null && request.getPageToken().length() > 0);
			    
				if( !elements.isEmpty() ){
					Collections.sort(elements, new TGBrowserElementComparator());
				}
				
				cb.onSuccess(elements);
			} else {
				cb.onSuccess(new ArrayList<TGBrowserElement>());
			}
		} catch (Throwable e) {
			cb.handleError(new TGBrowserException(findActivity().getString(R.string.gdrive_list_children_error), e));
		}
	}

	public void createElement(final TGBrowserCallBack<TGBrowserElement> cb, final String name) {
		try {
			File file = new File();
			file.setTitle(name);
			file.setParents(Arrays.asList(new ParentReference().setId(this.folder.getFile().getId())));
			
			cb.onSuccess(new TGDriveBrowserFile(file, this.folder));
		} catch (RuntimeException e) {
			cb.handleError(e);
		}
	}

	public void getInputStream(final TGBrowserCallBack<InputStream> cb, TGBrowserElement element) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			
		    MediaHttpDownloader downloader = new MediaHttpDownloader(this.httpTransport, this.drive.getRequestFactory().getInitializer());
		    downloader.setDirectDownloadEnabled(true);
		    downloader.download(new GenericUrl(((TGDriveBrowserFile) element).getFile().getDownloadUrl()), outputStream);
		    
			outputStream.flush();
			outputStream.close();
			
			cb.onSuccess(new ByteArrayInputStream(outputStream.toByteArray()));
		} catch (Throwable e) {
			cb.handleError(new TGBrowserException(findActivity().getString(R.string.gdrive_read_file_error), e));
		}
	}
	
	public void getOutputStream(final TGBrowserCallBack<OutputStream> cb, final TGBrowserElement element) {
		try {
			final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			
			cb.onSuccess(new TGDriveBrowserOutputStream(byteStream, new Runnable() {
				public void run() {
					try {
						AbstractInputStreamContent mediaContent = new ByteArrayContent(TGDriveBrowserFile.FILE_MIME_TYPE, byteStream.toByteArray());
						File file = ((TGDriveBrowserFile) element).getFile();
						
						DriveRequest<?> request = null;
						
						if( file.getId() != null ) {
							request = TGDriveBrowser.this.drive.files().update(file.getId(), file, mediaContent);
						} else {
							request = TGDriveBrowser.this.drive.files().insert(file, mediaContent);
						}
						request.getMediaHttpUploader().setDirectUploadEnabled(true);
						request.execute();
					} catch (Throwable e) {
						cb.handleError(new TGBrowserException(findActivity().getString(R.string.gdrive_write_file_error), e));
					}
				}
			}));
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
