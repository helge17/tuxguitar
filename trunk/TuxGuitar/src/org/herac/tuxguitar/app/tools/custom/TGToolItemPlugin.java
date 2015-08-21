package org.herac.tuxguitar.app.tools.custom;

import org.herac.tuxguitar.action.TGActionContext;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.TGActionAdapterManager;
import org.herac.tuxguitar.editor.action.TGActionBase;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.plugin.TGPlugin;
import org.herac.tuxguitar.util.plugin.TGPluginException;

public abstract class TGToolItemPlugin implements TGPlugin{
	
	private TGCustomTool tool;
	private TGCustomToolAction toolAction;
	
	protected abstract void doAction(TGContext context);
	
	protected abstract String getItemName() throws TGPluginException ;
	
	public void connect(TGContext context) throws TGPluginException {
		try {
			if( this.tool == null && this.toolAction == null ) {
				String name = getItemName();
				
				this.tool = new TGCustomTool(name, name);
				this.toolAction = new TGCustomToolAction(context, this.tool.getName());
				
				TuxGuitar.getInstance().getActionManager().mapAction(this.tool.getAction(), this.toolAction);
				
				TGActionAdapterManager tgActionAdapterManager = TGActionAdapterManager.getInstance(context);
				tgActionAdapterManager.getKeyBindingActionIds().addActionId(this.toolAction.getName());
				tgActionAdapterManager.getSyncThreadInterceptor().addActionId(this.toolAction.getName());
				
				TGCustomToolManager.instance().addCustomTool(this.tool);
				TuxGuitar.getInstance().getItemManager().createMenu();
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
	}
	
	public void disconnect(TGContext context) throws TGPluginException {
		try {
			if( this.tool != null && this.toolAction != null ) {
				TGCustomToolManager.instance().removeCustomTool(this.tool);
				TuxGuitar.getInstance().getActionManager().unmapAction(this.tool.getAction());
				TuxGuitar.getInstance().getItemManager().createMenu();
				
				this.tool = null;
				this.toolAction = null;
			}
		} catch (Throwable throwable) {
			throw new TGPluginException(throwable.getMessage(),throwable);
		}
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