package app.tuxguitar.nsm;

import java.net.InetAddress;

/**
 * Parses an NSM_URL of the form {@code osc.udp://host:port[/]}.
 */
class NSMUrl {

	final InetAddress address;
	final int port;

	NSMUrl(String url) throws Exception {
		String spec = url.trim();
		if (!spec.startsWith("osc.udp://")) {
			throw new IllegalArgumentException("Unsupported NSM_URL scheme: " + url);
		}
		spec = spec.substring("osc.udp://".length());
		if (spec.endsWith("/")) {
			spec = spec.substring(0, spec.length() - 1);
		}
		int colon = spec.lastIndexOf(':');
		if (colon < 0) {
			throw new IllegalArgumentException("Missing port in NSM_URL: " + url);
		}
		this.port = Integer.parseInt(spec.substring(colon + 1));
		this.address = InetAddress.getByName(spec.substring(0, colon));
	}
}
