package app.tuxguitar.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.tuxguitar.event.TGEvent;
import app.tuxguitar.util.TGContext;

/**
 * Pins how TGActionManager executes an action: an unmapped id is ignored; an interceptor that claims the id stops
 * it; otherwise the action runs between a pre-execution and a post-execution event. When it fails, an error event
 * carries the failure, and the failure is thrown as a TGActionException (wrapped when it is not one already).
 *
 * The journal reads "pre id", "run name" (the "name" attribute of the action context), "post id",
 * "intercept id", "error id message".
 */
public class TestActionManager {

	private final TGActionManager manager = TGActionManager.getInstance(new TGContext());
	private final List<String> journal = new ArrayList<String>();
	private final TGActionContext context = new TGActionContext() {};

	@BeforeEach
	public void listen() {
		this.context.setAttribute("name", "given");
		this.manager.addPreExecutionListener(event -> this.journal.add("pre " + id(event)));
		this.manager.addPostExecutionListener(event -> this.journal.add("post " + id(event)));
		this.manager.addErrorListener(event -> this.journal.add("error " + id(event) + " " + error(event).getMessage()));
	}

	private static String id(TGEvent event) {
		return event.getAttribute(TGActionEvent.ATTRIBUTE_ACTION_ID);
	}

	private static Throwable error(TGEvent event) {
		return event.getAttribute(TGActionErrorEvent.PROPERTY_ACTION_ERROR);
	}

	private void mapRecordingAction(String id) {
		this.manager.mapAction(id, actionContext -> this.journal.add("run " + actionContext.getAttribute("name")));
	}

	private void mapFailingAction(String id, RuntimeException failure) {
		this.manager.mapAction(id, actionContext -> {
			throw failure;
		});
	}

	private void intercept(boolean claimed) {
		this.manager.addInterceptor((id, actionContext) -> this.journal.add("intercept " + id) && claimed);
	}

	@Test
	public void runsAMappedActionBetweenItsPreAndPostExecutionEvents() {
		mapRecordingAction("play");

		this.manager.execute("missing", this.context);
		this.manager.execute("play", this.context);

		assertEquals(List.of("pre play", "run given", "post play"), this.journal);
	}

	@Test
	public void runsAnActionTheInterceptorsLetThrough() {
		mapRecordingAction("play");
		intercept(false);

		this.manager.execute("play", this.context);

		assertEquals(List.of("intercept play", "pre play", "run given", "post play"), this.journal);
	}

	@Test
	public void anInterceptedActionDoesNotRun() {
		mapRecordingAction("play");
		intercept(true);

		this.manager.execute("play", this.context);

		assertEquals(List.of("intercept play"), this.journal);
	}

	@Test
	public void anActionFailureIsReportedAndThrownAsIs() {
		TGActionException failure = new TGActionException("broken");
		mapFailingAction("play", failure);

		assertSame(failure, assertThrows(TGActionException.class, () -> this.manager.execute("play", this.context)));
		assertEquals(List.of("pre play", "error play broken"), this.journal);
	}

	@Test
	public void anyOtherFailureIsReportedAndThrownWrapped() {
		IllegalStateException failure = new IllegalStateException("broken");
		mapFailingAction("play", failure);

		TGActionException thrown = assertThrows(TGActionException.class, () -> this.manager.execute("play", this.context));

		assertSame(failure, thrown.getCause());
		assertEquals(List.of("pre play", "error play broken"), this.journal);
	}

	@Test
	public void executesWithAContextFromTheFactory() {
		mapRecordingAction("play");
		this.manager.setActionContextFactory(() -> this.context);

		this.manager.execute("play");

		assertEquals(List.of("pre play", "run given", "post play"), this.journal);
	}
}
