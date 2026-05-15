package app.tuxguitar.nsm;

/**
 * JNI bridge to a native SIGTERM handler.
 *
 * registerSigtermPipe() installs a C-level sigaction(SIGTERM) handler and
 * returns the read end of a self-pipe.  waitSigterm(fd) blocks until the
 * pipe receives a byte (i.e. until SIGTERM fires).
 *
 * This is necessary because SWT/GTK (and FluidSynth) replace the JVM's own
 * SIGTERM sigaction after JVM startup, making sun.misc.Signal and JVM shutdown
 * hooks unreachable via SIGTERM.  Installing our handler last (from connect(),
 * after all native plugins have loaded) ensures we are the active handler.
 */
public class NSMSignal {

    static {
        System.loadLibrary("tuxguitar-nsm-jni");
    }

    /** Creates a pipe, installs the C SIGTERM handler, returns the read-end fd. Returns -1 on failure. */
    public static native int registerSigtermPipe();

    /** Blocks until SIGTERM fires (pipe readable). Returns 0 on success, -1 on error. */
    public static native int waitSigterm(int readFd);
}
