package app.tuxguitar.song.helpers.tuning;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import app.tuxguitar.resource.TGResourceLoader;
import app.tuxguitar.resource.TGResourceManager;
import app.tuxguitar.song.models.TGTuning;
import app.tuxguitar.util.TGContext;

/**
 * Pins the flat tuning lists TuningManager builds from the bundled tunings file: every tuning is named after the
 * groups holding it ("Guitar / 6-String / Drop D"; a tuning outside any group gets a leading " / "), the tunings of
 * sub-groups come before the group's own. The priority list keeps only tunings with a priority, sorted by it; the
 * full list drops priorities.
 *
 * A tuning is read back as "name priority [notes]".
 */
public class TestTuningManager {

	private static final String TUNINGS = "<?xml version=\"1.0\"?><tunings>"
		+ "<tuning name=\"Top\" notes=\"60\" priority=\"3\"/>"
		+ "<group name=\"Guitar\">"
		+ "  <group name=\"6-String\">"
		+ "    <tuning name=\"E Tuning\" notes=\"64,59,55,50,45,40\" priority=\"2\"/>"
		+ "    <tuning name=\"Drop D\" notes=\"64,59,55,50,45,38\"/>"
		+ "  </group>"
		+ "  <tuning name=\"Low\" notes=\"0,127\" priority=\"1\"/>"
		+ "</group>"
		+ "</tunings>";

	private static TuningManager manager() {
		TGContext context = new TGContext();
		TGResourceManager.getInstance(context).setResourceLoader(new TGResourceLoader() {
			public <T> Class<T> loadClass(String name) {
				return null;
			}

			public InputStream getResourceAsStream(String name) {
				return new ByteArrayInputStream(TUNINGS.getBytes(StandardCharsets.UTF_8));
			}

			public URL getResource(String name) {
				return null;
			}

			public Enumeration<URL> getResources(String name) {
				return null;
			}
		});
		return TuningManager.getInstance(context);
	}

	private static List<String> describe(List<TGTuning> tunings) {
		return tunings.stream()
			.map(tuning -> tuning.getName() + " " + tuning.getPriority() + " " + Arrays.toString(tuning.getValues()))
			.collect(Collectors.toList());
	}

	@Test
	public void listsEveryTuningNamedAfterItsGroups() {
		assertEquals(Arrays.asList(
				"Guitar / 6-String / E Tuning null [64, 59, 55, 50, 45, 40]",
				"Guitar / 6-String / Drop D null [64, 59, 55, 50, 45, 38]",
				"Guitar / Low null [0, 127]",
				" / Top null [60]"),
			describe(manager().getTgTunings()));
	}

	@Test
	public void listsOnlyPrioritizedTuningsInPriorityOrder() {
		assertEquals(Arrays.asList(
				"Guitar / Low 1 [0, 127]",
				"Guitar / 6-String / E Tuning 2 [64, 59, 55, 50, 45, 40]",
				" / Top 3 [60]"),
			describe(manager().getPriorityTgTunings()));
	}
}
