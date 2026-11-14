package app.tuxguitar.vst3;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A plugin open in the VST3 host process: it processes blocks of up to the maximum block agreed when it opened, keeps
 * the latency and tail it last reported, and hands what it tells its host (edits from its own editor, restarts) to its
 * listener. Its parameters are set and read by plain value.
 */
public final class TGVst3Instance implements AutoCloseable {

	// a tail that never ends (a drone, say)
	public static final int INFINITE_TAIL = -1;
	// the flag of a parameter that chooses the plugin's presets (Vst::ParameterInfo::kIsProgramChange)
	private static final int PROGRAM_CHANGE = 1 << 15;

	private final TGVst3Host host;
	private final TGVst3SharedBlocks blocks;
	private final int id;
	private final int inputs;
	private final int outputs;
	private final boolean editor;
	private final List<TGVst3Parameter> parameters;
	// set by the reader thread of the host when a late reply comes
	private volatile int latency;
	private volatile int tail;
	private TGVst3Listener listener;

	private TGVst3Instance(TGVst3Host host, TGVst3SharedBlocks blocks, int id, int inputs, int outputs, boolean editor, List<TGVst3Parameter> parameters) {
		this.host = host;
		this.blocks = blocks;
		this.id = id;
		this.inputs = inputs;
		this.outputs = outputs;
		this.editor = editor;
		this.parameters = parameters;
	}

	// reads the reply to an open request
	static TGVst3Instance opened(TGVst3Host host, TGVst3SharedBlocks blocks, DataInputStream in) throws IOException {
		int id = in.readInt();
		int inputs = in.readInt();
		int outputs = in.readInt();
		boolean editor = (in.readInt() == 1);
		List<TGVst3Parameter> parameters = new ArrayList<TGVst3Parameter>();
		for (int count = in.readInt(); count > 0; count--) {
			parameters.add(new TGVst3Parameter(in.readInt(), TGVst3Request.readString(in), in.readDouble(), in.readDouble(), in.readDouble(), in.readInt(), (in.readInt() & PROGRAM_CHANGE) != 0));
		}
		TGVst3Instance instance = new TGVst3Instance(host, blocks, id, inputs, outputs, editor, Collections.unmodifiableList(parameters));
		instance.readTiming(in);
		host.opened(id, instance);
		return instance;
	}

	private Void readTiming(DataInputStream in) throws IOException {
		this.latency = in.readInt();
		this.tail = in.readInt();
		return null;
	}

	private TGVst3Request request(int operation) {
		return new TGVst3Request(operation).writeInt(this.id);
	}

	// the host process it runs in
	public TGVst3Host getHost() {
		return this.host;
	}

	// the channels of its main input (none for an instrument) and main output
	public int getInputs() {
		return this.inputs;
	}

	public int getOutputs() {
		return this.outputs;
	}

	public boolean hasEditor() {
		return this.editor;
	}

	// as they were when it opened
	public List<TGVst3Parameter> getParameters() {
		return this.parameters;
	}

	// the id of the parameter with the title, or -1
	public int findParameter(String title) {
		for (TGVst3Parameter parameter : this.parameters) {
			if (parameter.getTitle().equals(title)) {
				return parameter.getId();
			}
		}
		return -1;
	}

	// in samples, as last reported
	public int getLatency() {
		return this.latency;
	}

	// in samples, or INFINITE_TAIL, as last reported
	public int getTail() {
		return this.tail;
	}

	// live: the first frames of the block's two sides go through the plugin, in place, with the events on their samples;
	// false, and the frames silent, when the plugin's reply is late (see TGVst3Host)
	public boolean process(float[][] block, int frames, TGVst3Transport transport, List<TGVst3NoteEvent> events) throws TGVst3Exception {
		return this.process(block, frames, transport, events, false);
	}

	// offline (an export) the block waits for its reply, however late it is, unless the host is taken for stopped
	public boolean process(float[][] block, int frames, TGVst3Transport transport, List<TGVst3NoteEvent> events, boolean offline) throws TGVst3Exception {
		boolean answered = (!this.host.isLate() && this.processInTime(block, frames, transport, events, offline));
		if (!answered) {
			for (float[] side : block) {
				java.util.Arrays.fill(side, 0, frames, 0f);
			}
		}
		return answered;
	}

	private boolean processInTime(float[][] block, int frames, TGVst3Transport transport, List<TGVst3NoteEvent> events, boolean offline) throws TGVst3Exception {
		this.blocks.write(block, frames);
		TGVst3Request request = this.request(TGVst3Protocol.PROCESS).writeInt(frames);
		request.writeInt(transport.isPlaying() ? 1 : 0).writeDouble(transport.getTempo()).writeInt(transport.getNumerator())
			.writeInt(transport.getDenominator()).writeDouble(transport.getQuarters()).writeLong(transport.getSamples());
		request.writeInt(events.size());
		for (TGVst3NoteEvent event : events) {
			request.writeInt(event.getType()).writeInt(event.getOffset()).writeInt(event.getPitch()).writeInt(event.getNoteId()).writeFloat(event.getValue());
		}
		boolean answered = this.host.callInTime(request, this::readTiming, offline);
		if (answered) {
			this.blocks.read(block, frames);
		}
		return answered;
	}

	// the plain value the plugin took
	public double setParameter(int parameter, double plain) throws TGVst3Exception {
		return this.host.call(this.request(TGVst3Protocol.SET_PARAMETER).writeInt(parameter).writeDouble(plain), in -> {
			double taken = in.readDouble();
			this.readTiming(in);
			return taken;
		});
	}

	public double getParameter(int parameter) throws TGVst3Exception {
		return this.host.call(this.request(TGVst3Protocol.GET_PARAMETER).writeInt(parameter), in -> in.readDouble());
	}

	public TGVst3State getState() throws TGVst3Exception {
		return this.host.call(this.request(TGVst3Protocol.GET_STATE), in -> new TGVst3State(TGVst3Request.readBytes(in), TGVst3Request.readBytes(in)));
	}

	public void setState(TGVst3State state) throws TGVst3Exception {
		this.host.call(this.request(TGVst3Protocol.SET_STATE).writeBytes(state.getComponent()).writeBytes(state.getController()), this::readTiming);
	}

	// what still sounds (tails) is dropped
	public void reset() throws TGVst3Exception {
		this.host.call(this.request(TGVst3Protocol.RESET), in -> null);
	}

	// the plugin's own editor, in a window of the host process; false when it has none (or the platform is not supported)
	public boolean openEditor() throws TGVst3Exception {
		return this.host.call(this.request(TGVst3Protocol.OPEN_EDITOR), in -> in.readInt() == 1);
	}

	public void closeEditor() throws TGVst3Exception {
		this.host.call(this.request(TGVst3Protocol.CLOSE_EDITOR), in -> null);
	}

	// a message to the plugin's controller; its answer (the binary attribute "report"), empty when it gave none
	public byte[] message(String messageId, Map<String, Long> integers, Map<String, byte[]> binaries) throws TGVst3Exception {
		TGVst3Request request = this.request(TGVst3Protocol.MESSAGE).writeString(messageId).writeInt(integers.size());
		integers.forEach((key, value) -> request.writeString(key).writeLong(value));
		request.writeInt(binaries.size());
		binaries.forEach((key, value) -> request.writeString(key).writeBytes(value));
		return this.host.call(request, in -> {
			byte[] report = TGVst3Request.readBytes(in);
			this.readTiming(in);
			return report;
		});
	}

	public void setListener(TGVst3Listener listener) {
		this.listener = listener;
	}

	void notice(TGVst3Notice notice) {
		if (this.listener != null) {
			this.listener.notice(notice);
		}
	}

	// closed in the host; a host that stopped, or that still owes this plugin's late reply, has nothing it can close
	public void close() {
		try {
			if (!this.host.isLate()) {
				this.host.call(this.request(TGVst3Protocol.CLOSE), in -> null);
			}
		} catch (TGVst3Exception e) {
			// the host is gone, and the plugin with it
		}
		this.host.closed(this.id);
		this.blocks.close();
	}

	// values as a plugin reads them from a binary attribute: doubles in the machine's byte order
	public static byte[] doubles(double... values) {
		ByteBuffer bytes = ByteBuffer.allocate(values.length * Double.BYTES).order(ByteOrder.nativeOrder());
		for (double value : values) {
			bytes.putDouble(value);
		}
		return bytes.array();
	}
}
