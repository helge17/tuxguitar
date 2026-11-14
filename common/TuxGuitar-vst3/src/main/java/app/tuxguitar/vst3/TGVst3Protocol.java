package app.tuxguitar.vst3;

/**
 * The requests TuxGuitar sends the VST3 host process (native/host/main.cpp is the other side). A request is one of these
 * operations and its fields; its reply is a status (0 done, 1 refused, then the reason), the operation's fields, then
 * what the plugins told the host since the last reply. All of it big-endian, strings and bytes as a length and the
 * bytes (UTF-8 for strings).
 */
final class TGVst3Protocol {

	static final int VERSION = 2;

	// -> version, process id
	static final int HELLO = 1;
	// module path -> count, then for each: class id, name, instrument (1) or effect (0)
	static final int SCAN = 2;
	// module path, class id, sample rate (double), maximum block, shared file path -> instance, input and output
	// channels, editor (1 or 0), parameters (count, then id, title, plain value, plain values at either end of the
	// range, steps, flags), latency, tail
	static final int OPEN = 3;
	// instance
	static final int CLOSE = 4;
	// instance, frames, transport (playing, tempo, numerator, denominator, quarters, samples), events (count, then type,
	// offset, pitch, note id, value) -> latency, tail
	static final int PROCESS = 5;
	// instance, parameter, plain value -> plain value, latency, tail
	static final int SET_PARAMETER = 6;
	// instance, parameter -> plain value
	static final int GET_PARAMETER = 7;
	// instance -> component state, controller state
	static final int GET_STATE = 8;
	// instance, component state, controller state -> latency, tail
	static final int SET_STATE = 9;
	// instance
	static final int RESET = 10;
	// process id -> network sockets it listens on (-1: unknown)
	static final int LISTENERS = 11;
	// instance -> opened (1) or not (0)
	static final int OPEN_EDITOR = 12;
	// instance
	static final int CLOSE_EDITOR = 13;
	// instance, message id, integers (count, then key and long), binaries (count, then key and bytes) -> report, latency,
	// tail
	static final int MESSAGE = 14;

	private TGVst3Protocol() {
	}

	// the host's process id, once its greeting says it speaks this version
	static long greeted(int version, long pid) throws TGVst3Exception {
		if (version != VERSION) {
			throw new TGVst3Exception("the VST3 host speaks version " + version + ", not " + VERSION);
		}
		return pid;
	}
}
