package app.tuxguitar.nsm;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.action.TGActionInterceptor;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.file.TGReadURLAction;
import app.tuxguitar.app.action.impl.file.TGWriteFileAction;
import app.tuxguitar.app.action.impl.system.TGDisposeAction;
import app.tuxguitar.app.action.listener.save.TGUnsavedDocumentInterceptor;
import app.tuxguitar.app.document.TGDocument;
import app.tuxguitar.app.document.TGDocumentListManager;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.file.TGLoadTemplateAction;
import app.tuxguitar.nsm.osc.OSCMessage;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.error.TGErrorHandler;
import app.tuxguitar.util.error.TGErrorManager;

/*
 * NSM (Non Session Manager) client.
 *
 * Startup sequence:
 *  1. NSMPlugin.earlyInit() calls startCommunication() — starts the UDP receive
 *     thread and sends /nsm/server/announce.
 *  2. earlyInit() calls waitForSessionDirectory() — blocks briefly (≤ timeout)
 *     until the NSM server sends /nsm/client/open.  The session directory is
 *     returned so the plugin can redirect TuxGuitar's config I/O before any
 *     config file is read.
 *  3. earlyInit() calls activateInterceptor() — registers this object as a
 *     TGActionInterceptor so that the default empty-song load is suppressed and
 *     the session file is opened instead.
 *
 * The NSM session directory (= the path argument of /nsm/client/open) is used
 * as a self-contained folder:
 *   {sessionDir}/tuxguitar.tg             — the song file
 *   {sessionDir}/tuxguitar.cfg            — main application config
 *   {sessionDir}/plugins/                 — per-plugin configs
 *   {sessionDir}/tuxguitar-plugin-settings.cfg — plugin enabled/disabled state
 */
public class NSMClient implements TGActionInterceptor {

	private final TGContext context;
	private final DatagramSocket socket;
	private final InetAddress serverAddress;
	private final int serverPort;

	private Thread receiveThread;
	private volatile boolean running;

	// Unblocks earlyInit() as soon as /nsm/client/open is received.
	private final CountDownLatch openLatch = new CountDownLatch(1);

	// guarded by synchronized(this)
	private boolean appReady;
	private String pendingFilePath;
	private String sessionFilePath;   // absolute path to the .tg song file

	public NSMClient(TGContext context, String nsmUrl) throws Exception {
		this.context = context;
		this.appReady = false;
		this.pendingFilePath = null;
		this.sessionFilePath = null;

		// Parse "osc.udp://host:port[/]"
		String spec = nsmUrl.trim();
		if (!spec.startsWith("osc.udp://")) {
			throw new IllegalArgumentException("Unsupported NSM_URL scheme: " + nsmUrl);
		}
		spec = spec.substring("osc.udp://".length());
		if (spec.endsWith("/")) {
			spec = spec.substring(0, spec.length() - 1);
		}
		int colon = spec.lastIndexOf(':');
		String host = spec.substring(0, colon);
		int port = Integer.parseInt(spec.substring(colon + 1));

		this.serverAddress = InetAddress.getByName(host);
		this.serverPort = port;
		this.socket = new DatagramSocket();
	}

	// -------------------------------------------------------------------------
	// Phase 1 — called from earlyInit() before blocking
	// -------------------------------------------------------------------------

	public void startCommunication() {
		this.running = true;
		this.receiveThread = new Thread(new Runnable() {
			public void run() {
				receiveLoop();
			}
		}, "NSM");
		this.receiveThread.setDaemon(true);
		this.receiveThread.start();

		sendAnnounce();
	}

	// Block until /nsm/client/open is received or the timeout expires.
	// Returns the session directory, or null on timeout.
	public String waitForSessionDirectory(long timeoutMs) {
		try {
			if (openLatch.await(timeoutMs, TimeUnit.MILLISECONDS)) {
				synchronized (this) {
					if (this.sessionFilePath != null) {
						return new File(this.sessionFilePath).getParent();
					}
				}
			} else {
				System.err.println("[NSM] Timed out waiting for /nsm/client/open — config will use default location.");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return null;
	}

	// -------------------------------------------------------------------------
	// Phase 2 — called from earlyInit() after config has been redirected
	// -------------------------------------------------------------------------

	public void activateInterceptor() {
		TGActionManager.getInstance(this.context).addInterceptor(this);
	}

	// -------------------------------------------------------------------------
	// Shutdown
	// -------------------------------------------------------------------------

	public void stop() {
		this.running = false;
		TGActionManager.getInstance(this.context).removeInterceptor(this);
		if (!this.socket.isClosed()) {
			this.socket.close();
		}
	}

	// -------------------------------------------------------------------------
	// Outgoing OSC messages
	// -------------------------------------------------------------------------

	private void sendAnnounce() {
		int pid = (int) ProcessHandle.current().pid();
		OSCMessage msg = new OSCMessage("/nsm/server/announce");
		msg.addString("TuxGuitar");
		msg.addString("");          // capabilities: none declared
		msg.addString("tuxguitar");
		msg.addInt(1);              // NSM API major version
		msg.addInt(2);              // NSM API minor version
		msg.addInt(pid);
		sendMessage(msg);
	}

	private void sendReply(String path, String message) {
		OSCMessage msg = new OSCMessage("/reply");
		msg.addString(path);
		msg.addString(message);
		sendMessage(msg);
	}

	private void sendError(String path, int code, String message) {
		OSCMessage msg = new OSCMessage("/error");
		msg.addString(path);
		msg.addInt(code);
		msg.addString(message);
		sendMessage(msg);
	}

	private void sendMessage(OSCMessage msg) {
		try {
			byte[] data = msg.encode();
			DatagramPacket packet = new DatagramPacket(data, data.length, this.serverAddress, this.serverPort);
			this.socket.send(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// -------------------------------------------------------------------------
	// Receive loop
	// -------------------------------------------------------------------------

	private void receiveLoop() {
		byte[] buffer = new byte[65536];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		while (this.running) {
			try {
				this.socket.receive(packet);
				OSCMessage msg = OSCMessage.decode(buffer, packet.getLength());
				handleMessage(msg);
			} catch (SocketException e) {
				break; // socket closed, normal shutdown
			} catch (Exception e) {
				TGErrorManager.getInstance(this.context).handleError(e);
			}
		}
	}

	// -------------------------------------------------------------------------
	// Incoming message dispatch
	// -------------------------------------------------------------------------

	private void handleMessage(OSCMessage msg) {
		String addr = msg.getAddress();
		if ("/nsm/client/open".equals(addr)) {
			if (msg.argCount() >= 1) {
				handleOpen(msg.getStringArg(0));
			}
		} else if ("/nsm/client/save".equals(addr)) {
			handleSave();
		} else if ("/nsm/client/quit".equals(addr)) {
			handleQuit();
		} else if ("/reply".equals(addr) && msg.argCount() >= 2
				&& "/nsm/server/announce".equals(msg.getStringArg(0))) {
			System.out.println("[NSM] session manager: " + msg.getStringArg(1));
		} else if ("/error".equals(addr) && msg.argCount() >= 3) {
			System.err.println("[NSM] error on " + msg.getStringArg(0) + ": " + msg.getStringArg(2));
		}
	}

	// -------------------------------------------------------------------------
	// Open
	// -------------------------------------------------------------------------

	private void handleOpen(String path) {
		// NSM provides a per-client directory path.  All session files live inside it.
		new File(path).mkdirs();
		String filePath = path + File.separator + "tuxguitar.tg";

		synchronized (this) {
			this.sessionFilePath = filePath;
			if (this.appReady) {
				processOpen(filePath);
			} else {
				this.pendingFilePath = filePath;
			}
		}
		// Unblock earlyInit() so config can be redirected before TuxGuitar reads it.
		openLatch.countDown();
		// Acknowledge immediately — the actual file load is asynchronous.
		sendReply("/nsm/client/open", "opened");
	}

	private void processOpen(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			try {
				URL url = file.toURI().toURL();
				TGActionProcessor proc = new TGActionProcessor(this.context, TGReadURLAction.NAME);
				proc.setAttribute(TGReadURLAction.ATTRIBUTE_URL, url);
				proc.process();
			} catch (Exception e) {
				TGErrorManager.getInstance(this.context).handleError(e);
			}
		} else {
			// Brand-new session: load an empty template (interceptor already removed).
			TGActionProcessor proc = new TGActionProcessor(this.context, TGLoadTemplateAction.NAME);
			proc.process();
		}
	}

	// -------------------------------------------------------------------------
	// Save
	// -------------------------------------------------------------------------

	private void handleSave() {
		final String filePath;
		synchronized (this) {
			filePath = this.sessionFilePath;
		}
		if (filePath == null) {
			sendError("/nsm/client/save", 1, "no session file path");
			return;
		}
		File file = new File(filePath);
		if (file.getParentFile() != null) {
			file.getParentFile().mkdirs();
		}
		TGActionProcessor proc = new TGActionProcessor(this.context, TGWriteFileAction.NAME);
		proc.setAttribute(TGWriteFileAction.ATTRIBUTE_FILE_NAME, filePath);
		proc.setOnFinish(new Runnable() {
			public void run() {
				sendReply("/nsm/client/save", "saved");
			}
		});
		proc.setErrorHandler(new TGErrorHandler() {
			public void handleError(Throwable e) {
				sendError("/nsm/client/save", 1, e.getMessage() != null ? e.getMessage() : "save error");
			}
		});
		proc.process();
	}

	// -------------------------------------------------------------------------
	// Quit
	// -------------------------------------------------------------------------

	private void handleQuit() {
		TGActionProcessor proc = new TGActionProcessor(this.context, TGDisposeAction.NAME);
		// If nothing has changed there is nothing to save, so bypass the
		// "save before exit?" dialog — it would block indefinitely under NSM
		// because nobody is there to click it.  When there ARE unsaved changes
		// the dialog appears normally; after the user saves, TGDisposeAction is
		// re-dispatched by TGUnsavedDocumentInterceptor with bypass=true.
		if (!hasUnsavedDocuments()) {
			proc.setAttribute(TGUnsavedDocumentInterceptor.UNSAVED_INTERCEPTOR_BY_PASS, Boolean.TRUE);
		}
		proc.process();
	}

	private boolean hasUnsavedDocuments() {
		try {
			for (TGDocument doc : TGDocumentListManager.getInstance(this.context).getDocuments()) {
				if (doc.isUnsaved()) {
					return true;
				}
			}
		} catch (Exception e) {
			return true; // fail safe: show the dialog rather than silently discard unsaved work
		}
		return false;
	}

	// -------------------------------------------------------------------------
	// TGActionInterceptor — suppress default song loading at startup
	// -------------------------------------------------------------------------

	@Override
	public boolean intercept(String id, TGActionContext actionContext) throws TGActionException {
		if (TGLoadTemplateAction.NAME.equals(id)) {
			// One-shot: remove the interceptor after the first startup template dispatch.
			TGActionManager.getInstance(this.context).removeInterceptor(this);
			final String filePath;
			synchronized (this) {
				this.appReady = true;
				filePath = this.pendingFilePath;
				this.pendingFilePath = null;
			}
			if (filePath != null) {
				processOpen(filePath);
			}
			// Suppress the default template load; processOpen() handles the song.
			return true;
		}
		return false;
	}
}
