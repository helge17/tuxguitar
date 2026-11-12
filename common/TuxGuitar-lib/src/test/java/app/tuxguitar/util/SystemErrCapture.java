package app.tuxguitar.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Captures what a test prints to System.err, for code that reports errors by printing them.
 * Register it with {@code @RegisterExtension}; System.err is restored after each test.
 */
public class SystemErrCapture implements BeforeEachCallback, AfterEachCallback {

	private final ByteArrayOutputStream printed = new ByteArrayOutputStream();
	private PrintStream systemErr;

	public void beforeEach(ExtensionContext context) {
		this.systemErr = System.err;
		System.setErr(new PrintStream(this.printed, true));
	}

	public void afterEach(ExtensionContext context) {
		System.setErr(this.systemErr);
	}

	// everything printed since the test started, or since the last reset
	public String printed() {
		return this.printed.toString();
	}

	public void reset() {
		this.printed.reset();
	}
}
