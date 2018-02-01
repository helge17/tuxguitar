package org.herac.tuxguitar.player.impl.midiport.vst.remote;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class VSTConnection {
	
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private boolean closed;
	
	public VSTConnection(Socket socket) throws IOException {
		this.socket = socket;
		this.socket.setTcpNoDelay(true);
		this.inputStream = socket.getInputStream();
		this.outputStream = socket.getOutputStream();
	}
	
	public Socket getSocket() {
		return socket;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}
	
	public void close() {
		this.closed = true;
	}
	
	public boolean isClosed() {
		return this.closed;
	}
}
