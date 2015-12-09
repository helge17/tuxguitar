package org.herac.tuxguitar.debug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Set;

import org.herac.tuxguitar.app.util.TGFileUtils;
import org.herac.tuxguitar.editor.TGEditorManager;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGDeadLockDebugThread implements Runnable {
	
	private static final long THREAD_DELAY = 10000;
	private static final long TIMEOUT = 60000;
	
	private TGContext context;
	private boolean running;
	
	public TGDeadLockDebugThread(TGContext context){
		this.context = context;
	}
	
	public void stop() {
		this.running = false;
	}
	
	public void start() {
		this.running = true;
		
		new Thread(this).start();
	}
	
	public void run() {
		while( this.running ) {
			try {
				new TGLockTest().doTest();
				new TGSyncThreadTest().doTest();
				
				Thread.sleep(THREAD_DELAY);
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}
		}
	}
	
	public void deadLockDetected(String ownerTest) {
		Exception exception = new RuntimeException(ownerTest + ": dead lock detected");
		exception.printStackTrace();
		
		new TGThreadLogger().logThreads();
	}
	
	public static TGDeadLockDebugThread getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGDeadLockDebugThread.class.getName(), new TGSingletonFactory<TGDeadLockDebugThread>() {
			public TGDeadLockDebugThread createInstance(TGContext context) {
				return new TGDeadLockDebugThread(context);
			}
		});
	}
	
	private final class TGLockTest {
		
		private long startTime;
		private boolean success;
		
		public void doTest() {
			this.success = false;
			this.startTime = System.currentTimeMillis();
			
			new Thread(new Runnable() {
				public void run() {
					doLock();
				}
			}).start();
			
			new Thread(new Runnable() {
				public void run() {
					Thread.yield();
					
					while(!TGLockTest.this.success) {
						if( System.currentTimeMillis() > (TGLockTest.this.startTime + TIMEOUT)) {
							TGDeadLockDebugThread.this.deadLockDetected(TGLockTest.class.getSimpleName());
							
							return;
						}
						Thread.yield();
					}
				}
			}).start();
		}
		
		public void doLock() {
			TGEditorManager tgEditorManager = TGEditorManager.getInstance(TGDeadLockDebugThread.this.context);
			tgEditorManager.lock();
			try {
				this.success = true;
			} finally {
				tgEditorManager.unlock();
			}
		}
	}
	
	private final class TGSyncThreadTest {
		
		private long startTime;
		private boolean success;
		
		public void doTest() {
			this.success = false;
			this.startTime = System.currentTimeMillis();
			
			new Thread(new Runnable() {
				public void run() {
					doSyncProcess();
				}
			}).start();
			
			new Thread(new Runnable() {
				public void run() {
					Thread.yield();
					
					while(!TGSyncThreadTest.this.success) {
						if( System.currentTimeMillis() > (TGSyncThreadTest.this.startTime + TIMEOUT)) {
							TGDeadLockDebugThread.this.deadLockDetected(TGSyncThreadTest.class.getSimpleName());
							
							return;
						}
						Thread.yield();
					}
				}
			}).start();
		}
		
		public void doSyncProcess() {
			TGSynchronizer.getInstance(TGDeadLockDebugThread.this.context).executeLater(new Runnable() {
				public void run() {
					TGSyncThreadTest.this.success = true;
				}
			});
		}
	}
	
	private final class TGThreadLogger {
		
		public void logThreads() {
			String logId = Long.toString(new Date().getTime());
			Set<Thread> threads = Thread.getAllStackTraces().keySet();
			for(Thread thread : threads) {
				this.logThread(thread, logId);
			}
		}
		
		public void logThread(Thread thread, String logId) {
			try {
				File logFile = new File(getLogDir(logId), getThreadLabel(thread) + ".log");
				BufferedWriter buffer = new BufferedWriter(new FileWriter(logFile, true));
				buffer.append(this.getThreadBodyMessage(thread));
				buffer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private File getLogDir(String logId){
			File path = new File(TGFileUtils.PATH_USER_DIR + File.separator + "log" + File.separator + logId);
			
			if(!path.exists()){
				path.mkdirs();
			}
			return path;
		}
		
		public String getThreadLabel(Thread thread) {
			String label = (thread.getId() + "-" + thread.getName());
			if(!TGEditorManager.getInstance(TGDeadLockDebugThread.this.context).getLockControl().isLocked(thread) ) {
				label += " (lock access)";
			}
			return label;
		}
		
		public String getThreadBodyMessage(Thread thread) {
			StringBuffer sb = new StringBuffer();
			sb.append("Thread: ");
			sb.append(getThreadLabel(thread));
			sb.append("\n\n");
			sb.append(new Date().toString());
			sb.append("\n\n");
			sb.append(getStackTrace(thread));
			sb.append("\n");
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
