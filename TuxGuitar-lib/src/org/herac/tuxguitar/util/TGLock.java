package org.herac.tuxguitar.util;

import java.util.HashMap;
import java.util.Map;

public class TGLock {
	
	private Object lock;
	private Thread lockThread;
	private int lockCount;
	
	private TGLockTimeOutController timeOutController;
	
	public TGLock(){
		this.lock = new Object();
		this.lockThread = null;
		this.lockCount = 0;
		
		this.timeOutController = new TGLockTimeOutController(this);
	}
	
	public void lock() {		
		Thread thread = Thread.currentThread();
		
		boolean lockSuccess = false;
		
		synchronized( this.lock ){
			if( ( lockSuccess = !this.isLocked( thread ) ) ){
				this.lockThread = thread;
				this.lockCount ++;
				
				this.timeOutController.onLockGranted();
			}
		}
		
		if( !lockSuccess ){
			while( isLocked(thread) ){
				this.timeOutController.onWaitingForLock();
				
				Thread.yield();
			}
			this.lock();
		}
	}
	
	public void unlock(boolean force) {
		synchronized( this.lock ){
			this.lockCount --;
			if( this.lockCount == 0 || force ) {
				this.lockCount = 0;
				this.lockThread = null;
			}
		}
	}
	
	public void unlock() {
		this.unlock(true);
	}
	
	public boolean tryLock() {
		synchronized( this.lock ){
			if( this.isLocked() ) {
				return false;
			}
			this.lock();
			
			return true;
		}
	}
	
	public boolean isLocked(Thread thread) {
		synchronized( this.lock ){
			return (this.lockThread != null && this.lockThread != thread);
		}
	}
	
	public boolean isLocked() {
		return isLocked( Thread.currentThread() );
	}
	
	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
	
	private class TGLockTimeOutController {
		
		private static final long TIME_OUT = 20000;
		
		private TGLock lock;
		private Map<Thread, Long> timeStamps;
		private StackTraceElement[] stackTraceElements;
		
		public TGLockTimeOutController(TGLock lock) {
			this.lock = lock;
			this.timeStamps = new HashMap<Thread, Long>();
		}
		
		public void onWaitingForLock() {
			Thread thread = Thread.currentThread();
			
			long currentTimeStamp = System.currentTimeMillis();
			if(!this.timeStamps.containsKey(thread)) {
				this.timeStamps.put(thread, Long.valueOf(currentTimeStamp));
			} else {
				Long timeStamp = (Long)this.timeStamps.get(thread);
				if( timeStamp != null ) {
					if( currentTimeStamp - (timeStamp.longValue()) > TIME_OUT ) {
						throw new RuntimeException(getErrorMessage());
					}
				}
			}
		}
		
		public void onLockGranted() {
			Thread thread = Thread.currentThread();
			if( this.timeStamps.containsKey(thread)) {
				this.timeStamps.remove(thread);
			}
			this.stackTraceElements = thread.getStackTrace();
			
			if( this.lock.lockCount > 1 && "main".equals(thread.getName())) {
				throw new RuntimeException("To many locks on main thread");
			}
		}
		
		public String getErrorMessage() {
			StringBuffer sb = new StringBuffer();
			sb.append("TGLock: Dead Lock ");
			sb.append(this.lock.lockCount);
			sb.append("\n\n\n\n");
			sb.append("Lock Granted Stack Trace:\n");
			sb.append(getStackTrace(this.stackTraceElements));
			sb.append("\n\n");
			sb.append("Lock Granted Thread Current Stack Trace:\n");
			sb.append(getStackTrace(this.lock.lockThread));
			sb.append("\n\n");
			sb.append("Lock Required Stack Trace:\n");
			sb.append(getStackTrace(Thread.currentThread()));
			sb.append("\n\n****\n\n");
			return sb.toString();
		}
		
		public String getStackTrace(Thread thread) {
			return getStackTrace(thread.getStackTrace());
		}
		
		public String getStackTrace(StackTraceElement[] stackTraceElements) {
			StringBuffer sb = new StringBuffer();
			if( stackTraceElements != null ) {
				for(int i = 0 ; i < stackTraceElements.length ; i ++ ) {
					sb.append(stackTraceElements[i].toString());
					sb.append("\n");
				}
			} else {
				sb.append("null stackTrace?");
			}
			return sb.toString();
		}
	}
}
