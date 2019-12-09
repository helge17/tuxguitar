package org.herac.tuxguitar.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.herac.tuxguitar.app.action.impl.file.TGReadURLAction;
import org.herac.tuxguitar.app.util.ArgumentParser;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGMainSingleton {
	
	private static final Long CHECK_DELAY = 100L;
	private static final Long LOCK_FILE_TIMEOUT = (CHECK_DELAY * 10);
	private static final String EMPTY_URL = "url:empty";
	
	public static void main(String[] args) {
		try {
			ArgumentParser argumentParser = new ArgumentParser(args);
			if(argumentParser.processAndExit()){
				return;
			}
			
			TGMainSingleton tgMainSingleton = new TGMainSingleton();
			tgMainSingleton.launchSingleton(argumentParser.getURL());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	private void launchSingleton(URL url) {
		try {
			if( this.tryLock() ) {
				this.launchLockThread();
				this.launchTuxGuitar(url);
			} else {
				this.launchClientUrl(url);
			}
			System.exit(0);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	private void launchTuxGuitar(URL url) {
		TuxGuitar.getInstance().createApplication(url);
	}
	
	private void joinTuxGuitar(URL url) {
		final TGContext context = TuxGuitar.getInstance().getContext();
		TGSynchronizer.getInstance(context).executeLater(new Runnable() {
			public void run() {
				TGWindow.getInstance(context).moveToTop();
			}
		});
		if( url != null ) {
			TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGReadURLAction.NAME);
			tgActionProcessor.setAttribute(TGReadURLAction.ATTRIBUTE_URL, url);
			tgActionProcessor.process();
		}
	}
	
	private boolean tryLock() {
		try{
			File lockFile = this.getLockFile();
			
			if(!lockFile.exists()) {
				lockFile.getParentFile().mkdirs();
				
				return lockFile.createNewFile();
			}
			return (new Date().getTime()  > (lockFile.lastModified() + LOCK_FILE_TIMEOUT));
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return false;
	}
	
	private void launchLockThread() {
		final Object lock = new Object();
		final File lockFile = this.getLockFile();
		final File urlFolder = this.getUrlFolder();
		
		new Thread(new Runnable() {
			public void run() {
				while( true ) {
					try {
						if( TuxGuitar.getInstance().isInitialized() ) {
							lockFile.setLastModified(new Date().getTime());
							
							List<File> processedFiles = new ArrayList<File>();
							
							File[] urlFiles = urlFolder.listFiles();
							for(File urlFile : urlFiles) {
								BufferedReader in = new BufferedReader(new FileReader(urlFile));
								
								String inputLine = in.readLine();
								if( inputLine != null ) {
									joinTuxGuitar(parseUrl(inputLine));
								}
								
								in.close();
								
								processedFiles.add(urlFile);
							}
							while(!processedFiles.isEmpty()) {
								processedFiles.remove(0).delete();
							}
						}
						synchronized (lock) {
							lock.wait(CHECK_DELAY);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
    public void launchClientUrl(URL url) {
        try {
        	String urlForm = (url != null ? url.toExternalForm() : EMPTY_URL);
        	File urlFile = this.getUrlFile(new Date().getTime() + "-" + urlForm.hashCode());
        	
            PrintWriter printWriter = new PrintWriter(urlFile);
            printWriter.println(urlForm);
            printWriter.close();
        } catch (IOException e) {
        	e.printStackTrace();
        } 
    }
    
    public URL parseUrl(String spec) throws MalformedURLException {
    	if(!EMPTY_URL.endsWith(spec)) {
    		return new URL(spec);
    	}
    	return null;
    }

	private File getTemporaryFolder() {
		return new File(System.getProperty("java.io.tmpdir") + File.separator + "tuxguitar");
	}
	
	private File getUrlFolder() {
		return new File(this.getTemporaryFolder(), "url");
	}
	
	private File getUrlFile(String hash) {
		return new File(this.getUrlFolder(), "tuxguitar-url-" + hash + ".url");
	}
	
	private File getLockFile() {
		return new File(this.getTemporaryFolder(), "tuxguitar.lock");
	}
}
