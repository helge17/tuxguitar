package app.tuxguitar.audio;

/**
 * Whether an armed audio track's input is heard through its sound chain: Off never; Auto while stopped, and while
 * recording where it records; Always whenever it is armed, instead of its takes.
 */
public enum TGMonitoring {
	OFF, AUTO, ALWAYS
}
