package app.tuxguitar.app.tools.custom;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.TGActionAdapterManager;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.plugin.TGPlugin;
import app.tuxguitar.util.plugin.TGPluginException;

public abstract class TGToolItemPlugin implements TGPlugin{

	private TGCustomTool tool;
	private TGCustomToolAction toolAction;

	protected abstract void doAction(TGContext context);
	protected abstract String getItemName() throws TGPluginException ;

	// whether the item can be chosen now, asked each time the menus update (on the UI thread); always, unless a tool says
	protected boolean isEnabled(TGContext context) {
		return true;
	}

	public void connect(TGContext context) throws TGPluginException {
		if( this.hasNoTool() ) {
			pluginStep(() -> this.addTool(context));
		}
	}

	public void disconnect(TGContext context) throws TGPluginException {
		if( this.hasTool() ) {
			pluginStep(this::removeTool);
		}
	}

	private boolean hasNoTool() {
		return this.tool == null && this.toolAction == null;
	}

	private boolean hasTool() {
		return this.tool != null && this.toolAction != null;
	}

	private void addTool(TGContext context) throws Throwable {
		String name = getItemName();

		this.tool = new TGCustomTool(name, name, () -> isEnabled(context));
		this.toolAction = new TGCustomToolAction(context, this.tool.getName());

		TuxGuitar.getInstance().getActionManager().mapAction(this.tool.getAction(), this.toolAction);

		TGActionAdapterManager tgActionAdapterManager = TGActionAdapterManager.getInstance(context);
		tgActionAdapterManager.getKeyBindingActionIds().addActionId(this.toolAction.getName());
		tgActionAdapterManager.getSyncThreadInterceptor().addActionId(this.toolAction.getName());

		TGCustomToolManager.instance().addCustomTool(this.tool);
		TuxGuitar.getInstance().getItemManager().createMenu();
	}

	private void removeTool() throws Throwable {
		TGCustomToolManager.instance().removeCustomTool(this.tool);
		TuxGuitar.getInstance().getActionManager().unmapAction(this.tool.getAction());
		TuxGuitar.getInstance().getItemManager().createMenu();

		this.tool = null;
		this.toolAction = null;
	}

	// whatever a step throws, the plugin's connect or disconnect throws as a plugin exception
	private static void pluginStep(PluginStep step) throws TGPluginException {
		try {
			step.run();
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}

	private interface PluginStep {
		void run() throws Throwable;
	}

	protected class TGCustomToolAction extends TGActionBase {

		public TGCustomToolAction(TGContext context, String name) {
			super(context, name);
		}

		protected void processAction(TGActionContext context) {
			doAction(getContext());
		}
	}
}
