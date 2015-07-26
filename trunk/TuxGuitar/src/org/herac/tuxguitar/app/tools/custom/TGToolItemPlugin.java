package org.herac.tuxguitar.app.tools.custom;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionAdapterManager;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGToolItemPlugin implements TGPlugin{
	
	private boolean loaded;
	private TGContext context;
	private TGCustomTool tool;
	private TGCustomToolAction toolAction;
	
	protected abstract void doAction();
	
	protected abstract String getItemName() throws TGPluginException ;
	
	public void init(TGContext context) throws TGPluginException {
		this.context = context;
		this.initCustomTool();
	}
	
	public void initCustomTool() {
		String name = getItemName();
		this.tool = new TGCustomTool(name,name);
		this.toolAction = new TGCustomToolAction(this.getContext(), this.tool.getName());
	}
	
	public void close() throws TGPluginException {
		this.removePlugin();
	}
	
	public TGContext getContext() {
		return this.context;
	}
	
	public void setEnabled(boolean enabled) throws TGPluginException {
		if(enabled){
			addPlugin();
		}else{
			removePlugin();
		}
	}
	
	protected void addPlugin() throws TGPluginException {
		if(!this.loaded){
			TuxGuitar.getInstance().getActionManager().mapAction(this.tool.getAction(), this.toolAction);
			
			TGActionAdapterManager tgActionAdapterManager = TGActionAdapterManager.getInstance(this.getContext());
			tgActionAdapterManager.getKeyBindingActionIds().addActionId(this.toolAction.getName());
//			tgActionAdapterManager.getLockableActionListener().addLockableAction(this.toolAction.getName());
			tgActionAdapterManager.getSyncThreadInterceptor().addActionId(this.toolAction.getName());
			
			TGCustomToolManager.instance().addCustomTool(this.tool);
			TuxGuitar.getInstance().getItemManager().createMenu();
			this.loaded = true;
		}
	}
	
	protected void removePlugin() throws TGPluginException {
		if(this.loaded){
			TGCustomToolManager.instance().removeCustomTool(this.tool);
			TuxGuitar.getInstance().getActionManager().unmapAction(this.tool.getAction());
			TuxGuitar.getInstance().getItemManager().createMenu();
			this.loaded = false;
		}
	}

	protected class TGCustomToolAction extends TGActionBase {
		
		public TGCustomToolAction(TGContext context, String name) {
			super(context, name);
		}
		
		protected void processAction(TGActionContext context) {
			doAction();
		}
	}
}