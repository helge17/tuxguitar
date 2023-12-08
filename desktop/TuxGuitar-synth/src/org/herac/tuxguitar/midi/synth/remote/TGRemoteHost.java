package org.herac.tuxguitar.midi.synth.remote;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.midi.synth.TGSynthSettings;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGRemoteHost {
	
	private TGContext context;
	private Object lock;
	private ServerSocket serverSocket;
	private Map<Integer, TGSession> connections;
	
	public TGRemoteHost(TGContext context) {
		this.context = context;
		this.lock = new Object();
		this.connections = new HashMap<Integer, TGSession>();
	}
	
	public TGSession getSession(Integer sessionId) {
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
	
	public TGSession createSession(TGClientStarter starter) throws TGRemoteException {
		Integer sessionId = this.getNextSessionId();
		
		this.fireServerSocket();
		
		TGClientProcess tgClientProcess = new TGClientProcess(this.context);
		tgClientProcess.startSession(starter, sessionId, this.serverSocket.getLocalPort());
		
		while(this.getSession(sessionId) == null ) {
			Thread.yield();
			
			if(!tgClientProcess.isAlive()) {
				throw new TGRemoteException("Process ended!");
			}
		}
		return this.getSession(sessionId);
	}
	
	public void fireServerSocket() throws TGRemoteException {
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
								throw new TGRemoteException(e);
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
			throw new TGRemoteException(e);
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
			TGConnection connection = new TGConnection(clientSocket);
			TGStartSessionCommand startSessionCommand = new TGStartSessionCommand(connection);
			TGSession session = startSessionCommand.safeProcess();
			if( session != null ) {
				if(!this.connections.containsKey(session.getId())) {
					this.connections.put(session.getId(), session);
				}
				
				while(!session.isClosed()) {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						session.close();
					}
				}
				
				this.connections.remove(session.getId());
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
    	return new TGSynthSettings(this.context).getRemoteHostServerPort();
    }
    
	public static TGRemoteHost getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGRemoteHost.class.getName(), new TGSingletonFactory<TGRemoteHost>() {
			public TGRemoteHost createInstance(TGContext context) {
				return new TGRemoteHost(context);
			}
		});
	}
}
