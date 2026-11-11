package app.tuxguitar.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.tuxguitar.util.TGExpressionResolver.MapPropertyResolver;
import app.tuxguitar.util.TGExpressionResolver.PropertyResolver;

/**
 * Pins how TGExpressionResolver replaces "${name}" expressions in a text: the value comes from the custom resolver,
 * then the added resolvers in order, then system properties, then environment variables. An optional quoted prefix
 * and suffix ("${'pre'name'suf'}") are written around the value only when there is one; an expression without a
 * value is removed.
 */
public class TestExpressionResolver {

	private static final String SYSTEM_PROPERTY = "tuxguitar.test.expression";

	private final Map<String, Object> variables = new HashMap<String, Object>();

	private TGExpressionResolver resolver() {
		TGExpressionResolver resolver = new TGExpressionResolver();
		resolver.addResolver(new MapPropertyResolver(this.variables));
		return resolver;
	}

	@BeforeEach
	public void addVariables() {
		this.variables.put("user", "ana");
		this.variables.put("count", 3);
		this.variables.put("dir", "home");
		this.variables.put("price", "$5 \\ unit");
		this.variables.put("nothing", null);
		this.variables.put("nameless", new Object() {
			public String toString() {
				return null;
			}
		});
	}

	@AfterEach
	public void clearSystemProperty() {
		System.clearProperty(SYSTEM_PROPERTY);
	}

	@Test
	public void replacesEachExpressionWithItsValue() {
		assertEquals("ana has 3 songs", resolver().resolve("${user} has ${count} songs"));
		assertEquals("no expression", resolver().resolve("no expression"));
		// dollars and backslashes of a value are kept
		assertEquals("cost: $5 \\ unit", resolver().resolve("cost: ${price}"));
		// an expression without value is removed
		assertEquals("a  b  c", resolver().resolve("a ${missing} b ${nothing} c${nameless}"));
		// the prefix and suffix are written only around a value
		assertEquals("/root/home/sub", resolver().resolve("/root${'/'dir'/'}sub"));
		assertEquals("/rootsub", resolver().resolve("/root${'/'missing'/'}sub"));
		// only the outer quote is removed
		assertEquals("'[home]'", resolver().resolve("${''['dir']''}"));
	}

	@Test
	public void looksForTheValueInOrder() {
		Map<String, Object> second = new HashMap<String, Object>();
		second.put("a", "second-a");
		second.put("b", "second-b");
		second.put(SYSTEM_PROPERTY, "second-system");
		TGExpressionResolver resolver = resolver();
		resolver.addResolver(new MapPropertyResolver(second));
		this.variables.put("a", "first-a");
		PropertyResolver custom = property -> ("c".equals(property) ? "custom-c" : null);
		System.setProperty("tuxguitar.test.system", "system");

		try {
			assertEquals("first-a second-b custom-c second-system system",
				resolver.resolve("${a} ${b} ${c} ${" + SYSTEM_PROPERTY + "} ${tuxguitar.test.system}", custom));
		} finally {
			System.clearProperty("tuxguitar.test.system");
		}
	}

	@Test
	public void theCustomResolverComesFirst() {
		this.variables.put("a", "added");
		assertEquals("custom", resolver().resolve("${a}", property -> "custom"));
	}

	@Test
	public void fallsBackToSystemPropertiesThenEnvironment() {
		System.setProperty(SYSTEM_PROPERTY, "from-system");
		assertEquals("from-system", resolver().resolve("${" + SYSTEM_PROPERTY + "}"));
		assertEquals(System.getenv("PATH"), resolver().resolve("${PATH}"));
	}

	@Test
	public void resolvesNothingFromNothing() {
		assertNull(resolver().resolve(null));
	}
}
