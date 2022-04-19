package org.herac.tuxguitar.midi.synth.remote;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TGConnection {
	
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private boolean closed;
	
	public TGConnection(Socket socket) throws IOException {
		this.socket = socket;
		this.socket.setTcpNoDelay(true);
		this.socket.setSoTimeout(1000);
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
