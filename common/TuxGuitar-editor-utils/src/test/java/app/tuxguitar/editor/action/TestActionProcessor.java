package app.tuxguitar.editor.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import app.tuxguitar.action.TGActionContext;

/**
 * Pins TGActionProcessor run on the current thread: the action runs on the given context, holding the attributes set
 * on the processor and then the ones given for this run, which win; then the finish callback runs. When the action
 * fails, the error handler gets the error instead.
 *
 * The action writes the attributes "a", "b" and "c" it sees to the log, then fails when "fail" is set.
 */
public class TestActionProcessor {

	private final SongActionFixture fixture = new SongActionFixture();
	private final List<String> log = new ArrayList<String>();
	private final TGActionProcessor processor = new TGActionProcessor(fixture.context, "test.record");

	public TestActionProcessor() {
		fixture.map(new TGActionBase(fixture.context, "test.record") {
			protected void processAction(TGActionContext context) {
				log.add("a=" + context.getAttribute("a") + " b=" + context.getAttribute("b") + " c=" + context.getAttribute("c"));
				if (context.getAttribute("fail") != null) {
					throw new IllegalStateException("failed");
				}
			}
		});
		processor.setAttribute("a", "1");
		processor.setAttribute("b", "2");
		processor.setOnFinish(() -> log.add("finished"));
		processor.setErrorHandler(error -> log.add("error: " + error.getCause().getMessage()));
	}

	private String process(Map<String, Object> given) {
		processor.processOnCurrentThread(fixture.actionContext, given);
		return String.join(", ", log);
	}

	@Test
	public void theGivenAttributesComeAfterTheProcessorOnes() {
		Map<String, Object> given = new HashMap<String, Object>();
		given.put("b", "given");
		given.put("c", "3");

		assertEquals("a=1 b=given c=3, finished", process(given));
	}

	@Test
	public void withoutGivenAttributesTheProcessorOnesAreUsed() {
		assertEquals("a=1 b=2 c=null, finished", process(null));
	}

	@Test
	public void aFailingActionGoesToTheErrorHandler() {
		processor.setAttribute("fail", Boolean.TRUE);

		assertEquals("a=1 b=2 c=null, error: failed", process(null));
	}
}
