package org.herac.tuxguitar.android.browser.saf;

import android.net.Uri;
import android.support.v4.provider.DocumentFile;

import org.herac.tuxguitar.android.activity.TGActivity;
import org.herac.tuxguitar.android.activity.TGActivityController;
import org.herac.tuxguitar.android.browser.model.TGBrowser;
import org.herac.tuxguitar.android.browser.model.TGBrowserCallBack;
import org.herac.tuxguitar.android.browser.model.TGBrowserElement;
import org.herac.tuxguitar.android.browser.model.TGBrowserElementComparator;
import org.herac.tuxguitar.android.browser.model.TGBrowserException;
import org.herac.tuxguitar.android.util.TGStreamUtil;
import org.herac.tuxguitar.util.TGContext;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TGSafBrowser implements TGBrowser{
	
	private TGContext context;
	private TGSafBrowserSettings data;
	private TGSafBrowserElement element;
	private DocumentFile root;

	public TGSafBrowser(TGContext context, TGSafBrowserSettings data){
		this.context = context;
		this.data = data;
	}

	public void open(final TGBrowserCallBack<Object> cb){
		try {
			new TGSafBrowserUriRequest(this.context, new TGSafBrowserUriHandler() {
				public void onUriAccessGranted(Uri uri) {
					if( uri != null ) {
						TGSafBrowser.this.root = DocumentFile.fromTreeUri(getActivity(), uri);
						TGSafBrowser.this.element = null;

						cb.onSuccess(TGSafBrowser.this.element);
					} else {
						cb.handleError(new TGBrowserException(getActivity().getString(R.string.browser_settings_saf_error_uri_access)));
					}
				}
			}, this.data.getUri()).process();
		} catch (RuntimeException e) {
			cb.handleError(e);
		}
	}

	public void close(TGBrowserCallBack<Object> cb){
		try {
			this.root = null;
			this.element = null;

			cb.onSuccess(this.element);
		} catch (RuntimeException e) {
			cb.handleError(e);
		}
	}

	public void cdElement(TGBrowserCallBack<Object> cb, TGBrowserElement element) {
		try {
			this.element = (TGSafBrowserElement) element;

			cb.onSuccess(this.element);
		} catch (RuntimeException e) {
			cb.handleError(e);
		}
	}

	public void cdRoot(TGBrowserCallBack<Object> cb) {
		try {
			this.element = new TGSafBrowserElement(this.root, null);

			cb.onSuccess(this.element);
		} catch (RuntimeException e) {
			cb.handleError(e);
		}
	}

	public void cdUp(TGBrowserCallBack<Object> cb) {
		try {
			if( this.element != null && this.element.getParent() != null ){
				this.element = this.element.getParent();
			}
			cb.onSuccess(this.element);
		} catch (RuntimeException e) {
			cb.handleError(e);
		}
	}

	public void listElements(TGBrowserCallBack<List<TGBrowserElement>> cb) {
		try {
			List<TGBrowserElement> elements = new ArrayList<TGBrowserElement>();
			if( this.element != null ) {
				DocumentFile file = this.element.getFile();
				if( file.exists() && file.isDirectory() ) {
					DocumentFile[] children = file.listFiles();
					if( children != null ) {
						for(DocumentFile child : children){
							elements.add(new TGSafBrowserElement(child, this.element));
						}
					}
				}
				if( !elements.isEmpty() ){
					Collections.sort(elements, new TGBrowserElementComparator());
				}
			}

			cb.onSuccess(elements);
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}

	public void createElement(TGBrowserCallBack<TGBrowserElement> cb, String name) {
		try {
			TGBrowserElement element = null;
			if( this.isWritable() ) {
				DocumentFile documentFile = this.element.getFile().createFile("*/*", name);

				element = new TGSafBrowserElement(documentFile, this.element);
			}
			cb.onSuccess(element);
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}

	public void getInputStream(TGBrowserCallBack<InputStream> cb, TGBrowserElement element) {
		try {
			TGSafBrowserElement safElement = (TGSafBrowserElement) element;
			if(!safElement.isFolder()) {
				InputStream inputStream = this.getActivity().getContentResolver().openInputStream(safElement.getFile().getUri());
				InputStream bufferedStream = TGStreamUtil.getInputStream(inputStream);

				cb.onSuccess(bufferedStream);
			}
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}

	public void getOutputStream(TGBrowserCallBack<OutputStream> cb, TGBrowserElement element) {
		try {
			TGSafBrowserElement safElement = (TGSafBrowserElement) element;
			if(!safElement.isFolder() && safElement.isWritable()) {
				OutputStream outputStream = this.getActivity().getContentResolver().openOutputStream(safElement.getFile().getUri());
				cb.onSuccess(outputStream);
			}
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}
	
	public boolean isWritable() {
		return (this.element != null && this.element.isFolder() && this.element.isWritable());
	}

	public TGActivity getActivity() {
		return TGActivityController.getInstance(this.context).getActivity();
	}
}
