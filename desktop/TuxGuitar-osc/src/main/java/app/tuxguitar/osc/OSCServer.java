package app.tuxguitar.osc;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class OSCServer {

	private static final int BUFFER_SIZE = 65536;

	private final DatagramSocket socket;
	private final OSCMessageHandler handler;
	private volatile boolean running;

	public OSCServer(DatagramSocket socket, OSCMessageHandler handler) {
		this.socket = socket;
		this.handler = handler;
		this.running = false;
	}

	public void start() {
		this.running = true;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				receiveLoop();
			}
		}, "OSC-recv");
		thread.setDaemon(true);
		thread.start();
	}

	public void stop() {
		this.running = false;
	}

	private void receiveLoop() {
		byte[] buffer = new byte[BUFFER_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		while (this.running) {
			try {
				this.socket.receive(packet);
				OSCMessage msg = OSCMessage.decode(buffer, packet.getLength());
				this.handler.handleMessage(msg);
			} catch (SocketException e) {
				break; // socket closed, normal shutdown
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
