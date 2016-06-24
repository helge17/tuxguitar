package org.herac.tuxguitar.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import org.herac.tuxguitar.app.action.impl.file.TGReadURLAction;
import org.herac.tuxguitar.app.util.ArgumentParser;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

public class TGMainSingleton {
	
	private static final Integer DEFAULT_PORT = 50912;
	private static final String PORT_PROPERTY = "tuxguitar.singleton.port";
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
	
	public void launchSingleton(URL url) {
		try {
			TGMainSingleton singleton = new TGMainSingleton();
			
			ServerSocket serverSocket = singleton.fireServerSocket();
			if( serverSocket != null ) {
				launchTuxGuitar(url);
				
				System.exit(0);
			} else {
				singleton.fireClientSocket(url);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public void launchTuxGuitar(URL url) {
		TuxGuitar.getInstance().createApplication(url);
	}
	
	public void joinTuxGuitar(URL url) {
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
	
	public ServerSocket fireServerSocket() {
		try {
			final ServerSocket serverSocket = new ServerSocket(this.getSocketPort(), 10, this.getSocketHost());
			
			new Thread(new Runnable() {
				public void run() {
					try {
						while(!serverSocket.isClosed()) {
							final Socket clientSocket = serverSocket.accept();
							new Thread(new Runnable() {
								public void run() {
									try {
										PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
										BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
										
										String inputLine = in.readLine();
										if( inputLine != null ) {
											joinTuxGuitar(parseUrl(inputLine));
										}
										
										out.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}).start();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
			
			return serverSocket;
		} catch (IOException e) {
			return null;
		}
	}
	
    public void fireClientSocket(URL url) {
        try {
            Socket echoSocket = new Socket(this.getSocketHost(), this.getSocketPort());
        	
            PrintWriter printWriter = new PrintWriter(echoSocket.getOutputStream(), true);
            printWriter.println((url != null ? url.toExternalForm() : EMPTY_URL));
            printWriter.close();
            
            echoSocket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        } 
    }
    
    public InetAddress getSocketHost() throws UnknownHostException {
    	return InetAddress.getByAddress(new byte[] {127, 0, 0, 1});
    }
    
    public Integer getSocketPort() throws UnknownHostException {
    	try {
    		Object port = System.getProperty(PORT_PROPERTY);
    		if( port != null ) {
    			return Integer.valueOf(port.toString());
    		}
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    	return DEFAULT_PORT;
    }
    
    public URL parseUrl(String spec) throws MalformedURLException {
    	if(!EMPTY_URL.endsWith(spec)) {
    		return new URL(spec);
    	}
    	return null;
    }
}
