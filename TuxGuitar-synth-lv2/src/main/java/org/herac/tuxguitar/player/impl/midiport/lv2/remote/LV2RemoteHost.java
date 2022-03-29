package org.herac.tuxguitar.player.impl.midiport.lv2.remote;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.player.impl.midiport.lv2.LV2Settings;
import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2Plugin;
import org.herac.tuxguitar.player.impl.midiport.lv2.remote.command.LV2StartSessionCommand;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class LV2RemoteHost {
	
	private TGContext context;
	private Object lock;
	private ServerSocket serverSocket;
	private Map<Integer, LV2Session> connections;
	
	public LV2RemoteHost(TGContext context) {
		this.context = context;
		this.lock = new Object();
		this.connections = new HashMap<Integer, LV2Session>();
	}
	
	public LV2Session getSession(Integer sessionId) {
		if( this.connections.containsKey(sessionId)) {
			return this.connections.get(sessionId);
		}
		return null;
	}
	
	public Integer getNextSessionId() {
		synchronized (this.lock) {
			Integer maximum = 0;
			for(Integer sessionId : this.connections.keySet()) {
				if( maximum < sessionId ) {
					maximum = sessionId;
				}
			}
			return (maximum + 1);
		}
	}
	
	public LV2Session createSession(LV2Plugin plugin, int bufferSize) throws LV2RemoteException {
		Integer sessionId = this.getNextSessionId();
		
		this.fireServerSocket();
		
		LV2ClientProcess lv2ClientProcess = new LV2ClientProcess(this.context);
		lv2ClientProcess.startSession(sessionId, this.serverSocket.getLocalPort(), plugin, bufferSize);
		
		while(this.getSession(sessionId) == null ) {
			Thread.yield();
			
			if(!lv2ClientProcess.isAlive()) {
				throw new LV2RemoteException("Process ended!");
			}
		}
		return this.getSession(sessionId);
	}
	
	public void fireServerSocket() throws LV2RemoteException {
		try {
			synchronized (this.lock) {
				if(!this.isServerSocketOpen() ) {
					int tries = 0;
					int defaultPort = this.getDefaultSocketPort();
					
					while(!this.isServerSocketOpen()) {
						try {
							this.serverSocket = new ServerSocket((defaultPort + tries), 10, this.getSocketHost());
						} catch (IOException e) {
							if( tries ++ >= 10) {
								throw new LV2RemoteException(e);
							}
						}
					}
					
					new Thread(new Runnable() {
						public void run() {
							processServerSocket();
						}
					}).start();
				}
			}
		} catch (IOException e) {
			throw new LV2RemoteException(e);
		}
	}
	
	public void processServerSocket() {
		try {
			boolean closed = true;
			synchronized (this.lock) {
				closed = this.serverSocket.isClosed();
			}
			
			while(!closed) {
				final Socket clientSocket = this.serverSocket.accept();
				new Thread(new Runnable() {
					public void run() {
						processClientSocket(clientSocket);
					}
				}).start();
				
				synchronized (this.lock) {
					closed = this.serverSocket.isClosed();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void processClientSocket(Socket clientSocket) {
		try {
			LV2Connection connection = new LV2Connection(clientSocket);
			LV2StartSessionCommand lv2StartSessionCommand = new LV2StartSessionCommand(connection);
			LV2Session lv2Session = lv2StartSessionCommand.safeProcess();
			if( lv2Session != null ) {
				if(!this.connections.containsKey(lv2Session.getId())) {
					this.connections.put(lv2Session.getId(), lv2Session);
				}
				
				while(!lv2Session.isClosed()) {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						lv2Session.close();
					}
				}
				
				this.connections.remove(lv2Session.getId());
			}
			if(!clientSocket.isClosed() ) {
				clientSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isServerSocketOpen() {
		return (this.serverSocket != null && !this.serverSocket.isClosed());
	}
	
    public InetAddress getSocketHost() throws UnknownHostException {
    	return InetAddress.getByAddress(new byte[] {127, 0, 0, 1});
    }
    
    public Integer getDefaultSocketPort() throws UnknownHostException {
    	return new LV2Settings(this.context).getUIServerPort();
    }
    
	public static LV2RemoteHost getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, LV2RemoteHost.class.getName(), new TGSingletonFactory<LV2RemoteHost>() {
			public LV2RemoteHost createInstance(TGContext context) {
				return new LV2RemoteHost(context);
			}
		});
	}
}
