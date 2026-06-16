package app.tuxguitar.osc;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class OSCClient {

	private final InetAddress address;
	private final int port;
	private final DatagramSocket socket;

	public OSCClient(InetAddress address, int port, DatagramSocket socket) {
		this.address = address;
		this.port = port;
		this.socket = socket;
	}

	public void send(OSCMessage message) throws Exception {
		byte[] data = message.encode();
		DatagramPacket packet = new DatagramPacket(data, data.length, this.address, this.port);
		this.socket.send(packet);
	}
}
