package org.herac.tuxguitar.app.tools.browser;

import java.io.InputStream;
import java.util.List;

import org.herac.tuxguitar.app.tools.browser.base.TGBrowser;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserCallBack;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserElement;

public class TGBrowserConnection {
	
	private boolean locked;
	private TGBrowser browser;
	private TGBrowserConnectionHandler handler;
	
	public TGBrowserConnection(TGBrowserConnectionHandler handler){
		this.handler = handler;
	}
	
	protected void lock(){
		this.locked = true;
		this.handler.notifyLockStatusChanged();
	}
	
	protected void unlock(){
		this.locked = false;
		this.handler.notifyLockStatusChanged();
	}
	
	public boolean isLocked(){
		return this.locked;
	}
	
	public TGBrowser getBrowser(){
		return this.browser;
	}
	
	public boolean isOpen(){
		return (getBrowser() != null);
	}
	
	public void open(final int callId, final TGBrowser browser){
		if(!isLocked()){
			this.close(callId);
			this.lock();
			this.browser = browser;
			new Thread(new Runnable() {
				public void run() {
					if( getBrowser() != null ){
						getBrowser().open(new TGBrowserOpenHandlerImpl(callId));
					}else{
						notifyClosed(callId);
					}
				}
			}).start();
		}
	}
	
	public void close(final int callId){
		if(!isLocked()){
			this.lock();
			final TGBrowser browser = getBrowser();
			this.browser = null;
			new Thread(new Runnable() {
				public void run() {
					if( browser != null ){
						browser.close(new TGBrowserCloseHandlerImpl(callId));
					}else{
						notifyClosed(callId);
					}
				}
			}).start();
		}
	}
	
	public void cdRoot(final int callId){
		if(!isLocked()){
			this.lock();
			new Thread(new Runnable() {
				public void run() {
					if( isOpen() ){
						getBrowser().cdRoot(new TGBrowserCdHandlerImpl(callId));
					}else{
						notifyClosed(callId);
					}
				}
			}).start();
		}
	}
	
	public void cdUp(final int callId){
		if(!isLocked()){
			this.lock();
			new Thread(new Runnable() {
				public void run() {
					if( isOpen() ){
						getBrowser().cdUp(new TGBrowserCdHandlerImpl(callId));
					}else{
						notifyClosed(callId);
					}
				}
			}).start();
		}
	}
	
	public void cd(final int callId,final TGBrowserElement element){
		if(!isLocked()){
			this.lock();
			new Thread(new Runnable() {
				public void run() {
					if( isOpen() ){
						getBrowser().cdElement(new TGBrowserCdHandlerImpl(callId), element);
					}else{
						notifyClosed(callId);
					}
				}
			}).start();
		}
	}
	
	public void listElements(final int callId){
		if(!isLocked()){
			this.lock();
			new Thread(new Runnable() {
				public void run() {
					if( isOpen() ){
						getBrowser().listElements(new TGBrowserListElementsHandlerImpl(callId));
					}else{
						notifyClosed(callId);
					}
				}
			}).start();
		}
	}
	
	public void openStream(final int callId, final TGBrowserElement element){
		if(!isLocked()){
			this.lock();
			new Thread(new Runnable() {
				public void run() {
					if( element == null ){
						release();
						return;
					}
					if( element.isFolder() ){
						release();
						cd(callId,element);
					}
					else{
						getBrowser().getInputStream(new TGBrowserInputStreamHandlerImpl(callId, element), element);
					}
				}
			}).start();
		}
	}
	
	public void release(){
		this.unlock();
	}
	
	public void notifyCd(final int callId) {
		this.handler.notifyCd(callId);
	}
	
	public void notifyClosed(final int callId) {
		this.handler.notifyClosed(callId);
	}
	
	public void notifyElements(final int callId, List<TGBrowserElement> elements) {
		this.handler.notifyElements(callId, elements);
	}
	
	public void notifyError(final int callId,Throwable throwable) {
		this.handler.notifyError(callId, throwable);
	}
	
	public void notifyOpened(final int callId) {
		this.handler.notifyOpened(callId);
	}
	
	public void notifyStream(final int callId,InputStream stream, TGBrowserElement element) {
		this.handler.notifyStream(callId,stream,element);
	}
	
	private abstract class TGBrowserErrorHandlerImpl<T> implements TGBrowserCallBack<T> {
		
		private int callId;
		
		public TGBrowserErrorHandlerImpl(int callId) {
			this.callId = callId;
		}
		
		public void handleError(Throwable throwable) {
			TGBrowserConnection.this.notifyError(this.callId, throwable);
		}

		public int getCallId() {
			return callId;
		}
	}
	
	private class TGBrowserOpenHandlerImpl extends TGBrowserErrorHandlerImpl<Object> {
		
		public TGBrowserOpenHandlerImpl(int callId) {
			super(callId);
		}
		
		public void onSuccess(Object successData) {
			TGBrowserConnection.this.notifyOpened(this.getCallId());
		}
	}
	
	private class TGBrowserCloseHandlerImpl extends TGBrowserErrorHandlerImpl<Object> {
		
		public TGBrowserCloseHandlerImpl(int callId) {
			super(callId);
		}
		
		public void onSuccess(Object successData) {
			TGBrowserConnection.this.notifyClosed(this.getCallId());
		}
	}
	
	private class TGBrowserCdHandlerImpl extends TGBrowserErrorHandlerImpl<Object> {
		
		public TGBrowserCdHandlerImpl(int callId) {
			super(callId);
		}
		
		public void onSuccess(Object successData) {
			TGBrowserConnection.this.notifyCd(this.getCallId());
		}
	}
	
	private class TGBrowserListElementsHandlerImpl extends TGBrowserErrorHandlerImpl<List<TGBrowserElement>> {

		public TGBrowserListElementsHandlerImpl(int callId) {
			super(callId);
		}
		
		public void onSuccess(List<TGBrowserElement> elements) {
			TGBrowserConnection.this.notifyElements(this.getCallId(), elements);
		}
	}
	
	private class TGBrowserInputStreamHandlerImpl extends TGBrowserErrorHandlerImpl<InputStream> {
		
		private TGBrowserElement element;
		
		public TGBrowserInputStreamHandlerImpl(int callId, TGBrowserElement element) {
			super(callId);
			
			this.element = element;
		}
		
		public void onSuccess(InputStream stream) {
			TGBrowserConnection.this.notifyStream(this.getCallId(), stream, this.element);
		}
	}
}
