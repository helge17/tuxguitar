package org.herac.tuxguitar.app.tools.browser;

import java.io.InputStream;
import java.util.List;

import org.herac.tuxguitar.app.tools.browser.base.TGBrowser;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserCallBack;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserElement;
import org.herac.tuxguitar.util.TGLock;

public class TGBrowserConnection {
	
	private boolean locked;
	private TGLock lock;
	private TGBrowser browser;
	private TGBrowserConnectionHandler handler;
	
	public TGBrowserConnection(TGBrowserConnectionHandler handler){
		this.handler = handler;
		this.lock = new TGLock();
	}
	
	public boolean isOpen(){
		return (getBrowser() != null);
	}
	
	public void open(final TGBrowserCallBack<Object> callback, final TGBrowser browser){
		if( tryLock()) {
			new Thread(new Runnable() {
				public void run() {
					internalClose(new TGBrowserCallBackWrapper<Object>(callback) {
						public void onSuccess(Object data) {
							internalOpen(new TGBrowserCallBackUnlockWrapper<Object>(callback), browser);
						}
					});
				}
			}).start();
		}
	}
	
	public void close(final TGBrowserCallBack<Object> callback, boolean force){
		if( tryLock() || force ) {
			new Thread(new Runnable() {
				public void run() {
					internalClose(new TGBrowserCallBackUnlockWrapper<Object>(callback));
				}
			}).start();
		}
	}
	
	public void cdRoot(final TGBrowserCallBack<Object> callback){
		if( tryLock()) {
			new Thread(new Runnable() {
				public void run() {
					internalCdRoot(new TGBrowserCallBackUnlockWrapper<Object>(callback));
				}
			}).start();
		}
	}
	
	public void cdUp(final TGBrowserCallBack<Object> callback){
		if( tryLock()) {
			new Thread(new Runnable() {
				public void run() {
					internalCdUp(new TGBrowserCallBackUnlockWrapper<Object>(callback));
				}
			}).start();
		}
	}
	
	public void cdElement(final TGBrowserCallBack<Object> callback, final TGBrowserElement element){
		if( tryLock()) {
			new Thread(new Runnable() {
				public void run() {
					internalCdElement(new TGBrowserCallBackUnlockWrapper<Object>(callback), element);
				}
			}).start();
		}
	}
	
	public void listElements(final TGBrowserCallBack<List<TGBrowserElement>> callback){
		if( tryLock()) {
			new Thread(new Runnable() {
				public void run() {
					internalListElements(new TGBrowserCallBackUnlockWrapper<List<TGBrowserElement>>(callback));
				}
			}).start();
		}
	}
	
	public void openStream(final TGBrowserCallBack<InputStream> callback, final TGBrowserElement element) {
		if( tryLock()) {
			new Thread(new Runnable() {
				public void run() {
					internalOpenStream(new TGBrowserCallBackUnlockWrapper<InputStream>(callback), element);
				}
			}).start();
		}
	}
	
	private void internalOpen(TGBrowserCallBack<Object> callback, final TGBrowser browser){
		if( browser != null ){
			browser.open(new TGBrowserCallBackWrapper<Object>(callback) {
				public void onSuccess(Object data) {
					setBrowser(browser);
					
					super.onSuccess(data);
				}
			});
		}
	}
	
	private void internalClose(TGBrowserCallBack<Object> callback){
		if( this.browser != null ){
			this.browser.close(new TGBrowserCallBackWrapper<Object>(callback) {
				public void onSuccess(Object data) {
					setBrowser(null);
					
					super.onSuccess(data);
				}
			});
		} else {
			callback.onSuccess(null);
		}
	}
	
	private void internalCdRoot(final TGBrowserCallBack<Object> callback){
		if( this.isOpen() ){
			this.getBrowser().cdRoot(callback);
		} else {
			callback.onSuccess(null);
		}
	}
	
	private void internalCdUp(final TGBrowserCallBack<Object> callback){
		if( this.isOpen() ){
			this.getBrowser().cdUp(callback);
		} else {
			callback.onSuccess(null);
		}
	}
	
	private void internalCdElement(final TGBrowserCallBack<Object> callback, TGBrowserElement element){
		if( this.isOpen() ){
			this.getBrowser().cdElement(callback, element);
		} else {
			callback.onSuccess(null);
		}
	}
	
	private void internalListElements(final TGBrowserCallBack<List<TGBrowserElement>> callback){
		if( this.isOpen() ){
			this.getBrowser().listElements(callback);
		} else {
			callback.onSuccess(null);
		}
	}
	
	private void internalOpenStream(TGBrowserCallBack<InputStream> callback, TGBrowserElement element){
		if( this.isOpen() && element != null && !element.isFolder() ){
			this.getBrowser().getInputStream(callback, element);
		} else {
			callback.onSuccess(null);
		}
	}
	
	private boolean tryLock() {
		boolean lockAccess = false;
		if( this.lock.tryLock() ) {
			lockAccess = (!this.locked);
			if( lockAccess ) {
				this.locked = true;
				this.handler.notifyLockStatusChanged();
			}
			this.lock.unlock();
		}
		return lockAccess;
	}
	
	private void unlock() {
		this.lock.lock();
		this.locked = false;
		this.handler.notifyLockStatusChanged();
		this.lock.unlock();
	}
	
	public boolean isLocked() {
		return this.locked;
	}
	
	private TGBrowser getBrowser(){
		return this.browser;
	}
	
	private void setBrowser(TGBrowser browser) {
		this.browser = browser;
	}
	
	private class TGBrowserCallBackWrapper<T> implements TGBrowserCallBack<T> {
		
		private TGBrowserCallBack<T> callback;
		
		public TGBrowserCallBackWrapper(TGBrowserCallBack<T> callback) {
			this.callback = callback;
		}
		
		public void handleError(Throwable throwable) {
			this.callback.handleError(throwable);
		}
		
		public void onSuccess(T data) {
			this.callback.onSuccess(data);
		}
	}
	
	private class TGBrowserCallBackUnlockWrapper<T> extends TGBrowserCallBackWrapper<T> {
		
		public TGBrowserCallBackUnlockWrapper(TGBrowserCallBack<T> callback) {
			super(callback);
		}
		
		public void handleError(Throwable throwable) {
			TGBrowserConnection.this.unlock();
			
			super.handleError(throwable);
		}
		
		public void onSuccess(T data) {
			TGBrowserConnection.this.unlock();
			
			super.onSuccess(data);
		}
	}
}
