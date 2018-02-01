package org.herac.tuxguitar.player.impl.midiport.vst.remote;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.herac.tuxguitar.player.impl.midiport.vst.remote.command.VSTStartSessionCommand;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class VSTServer {
	
	private static final Integer DEFAULT_PORT = 50982;
	private static final String PORT_PROPERTY = "tuxguitar-synth-vst.singleton.port";
	
	private TGContext context;
	private Object lock;
	private ServerSocket serverSocket;
	private Map<Integer, VSTSession> connections;
	
	public VSTServer(TGContext context) {
		this.context = context;
		this.lock = new Object();
		this.connections = new HashMap<Integer, VSTSession>();
	}
	
	public VSTSession getSession(Integer sessionId) {
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
	
	public VSTSession createSession(String filename) throws VSTException {
		Integer sessionId = this.getNextSessionId();
		
		this.fireServerSocket();
		
		VSTClientProcess vstClientProcess = new VSTClientProcess(this.context);
		vstClientProcess.startSession(sessionId, DEFAULT_PORT, filename);
		
		while(this.getSession(sessionId) == null ) {
			Thread.yield();
			
			if(!vstClientProcess.isAlive()) {
				throw new VSTException("Process ended!");
			}
		}
		return this.getSession(sessionId);
	}
	
	public void fireServerSocket() {
		try {
			synchronized (this.lock) {
				if( this.serverSocket == null || this.serverSocket.isClosed() ) {
					this.serverSocket = new ServerSocket(this.getSocketPort(), 10, this.getSocketHost());
					
					new Thread(new Runnable() {
						public void run() {
							processServerSocket();
						}
					}).start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
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
			VSTConnection connection = new VSTConnection(clientSocket);
			VSTStartSessionCommand vstStartSessionCommand = new VSTStartSessionCommand(connection);
			VSTSession vstSession = vstStartSessionCommand.safeProcess();
			if( vstSession != null ) {
				if(!this.connections.containsKey(vstSession.getId())) {
					this.connections.put(vstSession.getId(), vstSession);
				}
				
				while(!vstSession.isClosed()) {
					Thread.yield();
				}
				
				this.connections.remove(vstSession.getId());
			}
			if(!clientSocket.isClosed() ) {
				clientSocket.close();
			}
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
    
	public static VSTServer getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, VSTServer.class.getName(), new TGSingletonFactory<VSTServer>() {
			public VSTServer createInstance(TGContext context) {
				return new VSTServer(context);
			}
		});
	}
}
