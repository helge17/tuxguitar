package org.herac.tuxguitar.player.impl.midiport.lv2.ui;

import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGIconManager;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.player.impl.midiport.lv2.LV2PluginValidator;
import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2Plugin;
import org.herac.tuxguitar.player.impl.midiport.lv2.jni.LV2World;
import org.herac.tuxguitar.thread.TGThreadManager;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIMouseDoubleClickListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIListBoxSelect;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.TGSynchronizer;

public class LV2AudioProcessorChooser {
	
	private TGContext context;
	private LV2World world;
	
	public LV2AudioProcessorChooser(TGContext context, LV2World world) {
		this.context = context;
		this.world = world;
	}
	
	public void choose(final UIWindow parent, final LV2PluginValidator validator, final LV2AudioProcessorChooserHandler handler) {
		TGSynchronizer.getInstance(this.context).executeLater(new Runnable() {
			public void run() {
				LV2AudioProcessorChooser.this.chooseInCurrentThread(parent, validator, handler);
			}
		});
	}
	
	public void chooseInCurrentThread(final UIWindow parent, final LV2PluginValidator validator, final LV2AudioProcessorChooserHandler handler) {
		final List<LV2Plugin> plugins = this.world.getPlugins();
		
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		
		final UIWindow dialog = uiFactory.createWindow(parent, true, false);
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("tuxguitar-synth-lv2.chooser.dialog.title"));
		dialog.setImage(TGIconManager.getInstance(this.context).getAppIcon());
		
		// ----------------------------------------------------------------------
		UITableLayout pluginGroupLayout = new UITableLayout();
		UILegendPanel pluginGroup = uiFactory.createLegendPanel(dialog);
		pluginGroup.setLayout(pluginGroupLayout);
		pluginGroup.setText(TuxGuitar.getProperty("tuxguitar-synth-lv2.chooser.tip"));
		dialogLayout.set(pluginGroup, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		final UIListBoxSelect<LV2Plugin> pluginList = uiFactory.createListBoxSelect(pluginGroup);
		pluginGroupLayout.set(pluginList, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		pluginGroupLayout.set(pluginList, UITableLayout.MINIMUM_PACKED_WIDTH, 200f);
		pluginGroupLayout.set(pluginList, UITableLayout.PACKED_HEIGHT, 200f);
		for(LV2Plugin plugin : plugins) {
			if( validator.validate(plugin)) {
				pluginList.addItem(new UISelectItem<LV2Plugin>(plugin.getName(), plugin));
			}
		}
		if( plugins.size() > 0 ){
			pluginList.setSelectedValue(plugins.get(0));
		}
		pluginList.addMouseDoubleClickListener(new UIMouseDoubleClickListener() {
			public void onMouseDoubleClick(UIMouseEvent event) {
				onSelectPlugin(handler, pluginList.getSelectedValue());
				dialog.dispose();
			}
		});
		
		//------------------BUTTONS----------------------------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				onSelectPlugin(handler, pluginList.getSelectedValue());
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
		// ----------------------------------------------------------------------
		
		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void onSelectPlugin(final LV2AudioProcessorChooserHandler handler, final LV2Plugin plugin) {
		TGThreadManager.getInstance(this.context).start(new Runnable() {
			public void run() {
				handler.onSelectPlugin(plugin);
			}
		});
	}
	
	public static interface LV2AudioProcessorChooserHandler {
		void onSelectPlugin(LV2Plugin plugin);
	}
}
