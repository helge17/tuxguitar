package app.tuxguitar.vst3;

import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

import app.tuxguitar.midi.synth.TGProgramElement;

/**
 * A VST3 effect or instrument as an element of a sound chain (TGProgramElement): its type, and parameters naming the
 * plugin (module, class id, name), its saved state, and the plain values of parameters set since that state.
 */
public final class TGVst3Element {

	public static final String EFFECT = "vst3-effect";
	public static final String INSTRUMENT = "vst3-instrument";

	static final String MODULE = "vst3.module";
	static final String CLASS_ID = "vst3.class";
	static final String NAME = "vst3.name";
	static final String STATE = "vst3.state";
	static final String CONTROLLER = "vst3.controller";
	static final String PARAMETER = "vst3.parameter.";

	private TGVst3Element() {
	}

	public static TGProgramElement create(String id, TGVst3Plugin plugin) {
		TGProgramElement element = new TGProgramElement();
		element.setId(id);
		element.setType(plugin.isInstrument() ? INSTRUMENT : EFFECT);
		element.setParameter(MODULE, plugin.getModule());
		element.setParameter(CLASS_ID, plugin.getClassId());
		element.setParameter(NAME, plugin.getName());
		return element;
	}

	public static boolean isVst3(TGProgramElement element) {
		return (EFFECT.equals(element.getType()) || INSTRUMENT.equals(element.getType()));
	}

	// the plugin's name, as a sound chain reads it
	public static String getName(TGProgramElement element) {
		return element.getParameters().get(NAME);
	}

	// the plugin the parameters name, or null
	public static TGVst3Plugin plugin(Map<String, String> parameters, boolean instrument) {
		String module = parameters.get(MODULE);
		String classId = parameters.get(CLASS_ID);
		return (module != null && classId != null ? new TGVst3Plugin(module, classId, parameters.get(NAME), instrument) : null);
	}

	// a parameter set to a plain value, after the saved state
	public static void setParameter(TGProgramElement element, int id, double plain) {
		element.setParameter(PARAMETER + id, Double.toString(plain));
	}

	// the plugin's state, which holds every parameter: the parameters set before it are dropped
	public static void setState(TGProgramElement element, TGVst3State state) {
		setState(element.getParameters(), state);
	}

	private static void setState(Map<String, String> parameters, TGVst3State state) {
		parameters.keySet().removeIf(key -> key.startsWith(PARAMETER));
		parameters.put(STATE, Base64.getEncoder().encodeToString(state.getComponent()));
		parameters.put(CONTROLLER, Base64.getEncoder().encodeToString(state.getController()));
	}

	// the parameters set since the state, by id
	static Map<Integer, Double> parameters(Map<String, String> parameters) {
		Map<Integer, Double> set = new TreeMap<Integer, Double>();
		parameters.forEach((key, value) -> {
			if (key.startsWith(PARAMETER)) {
				set.put(Integer.valueOf(key.substring(PARAMETER.length())), Double.valueOf(value));
			}
		});
		return set;
	}

	// the saved state, or null
	static TGVst3State state(Map<String, String> parameters) {
		String component = parameters.get(STATE);
		String controller = parameters.get(CONTROLLER);
		return (component != null ? new TGVst3State(decode(component), decode(controller)) : null);
	}

	// the plugin and its state, which holds every parameter: none is kept apart
	static void store(Map<String, String> parameters, TGVst3Plugin plugin, TGVst3State state) {
		parameters.put(MODULE, plugin.getModule());
		parameters.put(CLASS_ID, plugin.getClassId());
		parameters.put(NAME, plugin.getName());
		setState(parameters, state);
	}

	private static byte[] decode(String value) {
		return (value != null ? Base64.getDecoder().decode(value) : new byte[0]);
	}
}
