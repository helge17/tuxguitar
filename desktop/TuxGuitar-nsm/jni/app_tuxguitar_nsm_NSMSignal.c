#include <jni.h>
#include <signal.h>
#include <unistd.h>
#include <string.h>

/* Write end of the self-pipe; -1 means not initialised. */
static volatile int sigterm_pipe_write = -1;

static void sigterm_handler(int sig) {
    (void)sig;
    int fd = sigterm_pipe_write;
    if (fd != -1) {
        /* write() is async-signal-safe; ignore partial-write — one byte is enough. */
        char b = 1;
        write(fd, &b, 1);
    }
}

static int install_sigterm_handler(void) {
    struct sigaction sa;
    memset(&sa, 0, sizeof(sa));
    sa.sa_handler = sigterm_handler;
    sigemptyset(&sa.sa_mask);
    sa.sa_flags = SA_RESTART;
    return sigaction(SIGTERM, &sa, NULL);
}

/*
 * Class:     app_tuxguitar_nsm_NSMSignal
 * Method:    reinstallSigtermHandler
 * Signature: ()V
 *
 * Re-installs our sigterm_handler without creating a new pipe.  Call this
 * just before sending any NSM reply so our handler is the last one installed
 * (libjack overrides it when FluidSynth connects to JACK).
 */
JNIEXPORT void JNICALL
Java_app_tuxguitar_nsm_NSMSignal_reinstallSigtermHandler(JNIEnv *env, jclass cls) {
    if (sigterm_pipe_write != -1) {
        install_sigterm_handler();
    }
}

/*
 * Class:     app_tuxguitar_nsm_NSMSignal
 * Method:    registerSigtermPipe
 * Signature: ()I
 *
 * Creates a pipe, installs our sigterm_handler for SIGTERM (overriding any
 * handler previously set by SWT/GTK/FluidSynth), and returns the read-end fd.
 * Returns -1 on error.
 */
JNIEXPORT jint JNICALL
Java_app_tuxguitar_nsm_NSMSignal_registerSigtermPipe(JNIEnv *env, jclass cls) {
    int fds[2];
    if (pipe(fds) != 0) {
        return -1;
    }
    sigterm_pipe_write = fds[1];

    if (install_sigterm_handler() != 0) {
        close(fds[0]);
        close(fds[1]);
        sigterm_pipe_write = -1;
        return -1;
    }
    return (jint)fds[0];
}

/*
 * Class:     app_tuxguitar_nsm_NSMSignal
 * Method:    waitSigterm
 * Signature: (I)I
 *
 * Blocks until the pipe read-end fd receives a byte (i.e. SIGTERM fired).
 * Returns 0 on success, -1 on error/EOF.
 */
JNIEXPORT jint JNICALL
Java_app_tuxguitar_nsm_NSMSignal_waitSigterm(JNIEnv *env, jclass cls, jint readFd) {
    char b;
    ssize_t n = read((int)readFd, &b, 1);
    return (n == 1) ? 0 : -1;
}
