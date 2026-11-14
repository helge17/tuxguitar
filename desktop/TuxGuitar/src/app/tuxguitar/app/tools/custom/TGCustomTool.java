package app.tuxguitar.app.tools.custom;

import java.util.function.BooleanSupplier;

public class TGCustomTool {

	private String name;
	private String action;
	private BooleanSupplier enabled;

	public TGCustomTool(String name, String action) {
		this(name, action, () -> true);
	}

	// enabled: whether its Tools menu item can be chosen, asked each time the menus update
	public TGCustomTool(String name, String action, BooleanSupplier enabled) {
		this.name = name;
		this.action = action;
		this.enabled = enabled;
	}

	public String getName() {
		return this.name;
	}

	public String getAction() {
		return this.action;
	}

	public boolean isEnabled() {
		return this.enabled.getAsBoolean();
	}

}
