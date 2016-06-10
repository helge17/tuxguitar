package org.herac.tuxguitar.gm.settings;

import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.dialog.channel.TGChannelSettingsDialog;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.gm.GMChannelRoute;
import org.herac.tuxguitar.gm.GMChannelRouter;
import org.herac.tuxguitar.gm.GMChannelRouterConfigurator;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.song.models.TGChannelParameter;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class GMChannelSettingsDialog implements TGChannelSettingsDialog{
	
	public static final String CHANNELS_DATA = "channels";
	
	private TGContext context;
	private TGSong song;
	private TGChannel channel;
	private GMChannelRouter router;
	private UIDropDownSelect<Integer> gmChannel1Combo;
	private UIDropDownSelect<Integer> gmChannel2Combo;
	
	public GMChannelSettingsDialog(TGContext context, TGChannel channel, TGSong song){
		this.context = context;
		this.song = song;
		this.channel = channel;
		this.router = new GMChannelRouter();
	}
	
	public void show(final UIWindow parent) {
		this.configureRouter();
		
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();
		
		final UIWindow dialog = uiFactory.createWindow(parent, false, false);
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("gm.settings.dialog.title"));
		
		// ----------------------------------------------------------------------
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(dialog);
		group.setLayout(groupLayout);
		group.setText(TuxGuitar.getProperty("gm.settings.dialog.tip"));
		dialogLayout.set(group, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UILabel gmChannel1Label = uiFactory.createLabel(group);
		gmChannel1Label.setText(TuxGuitar.getProperty("gm.settings.channel.label-1") + ":");
		groupLayout.set(gmChannel1Label, 1, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		this.gmChannel1Combo = uiFactory.createDropDownSelect(group);
		this.gmChannel1Combo.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				updateChannel();
			}
		});
		groupLayout.set(this.gmChannel1Combo, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		UILabel gmChannel2Label = uiFactory.createLabel(group);
		gmChannel2Label.setText(TuxGuitar.getProperty("gm.settings.channel.label-2") + ":");
		groupLayout.set(gmChannel2Label, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_CENTER, false, false);
		
		this.gmChannel2Combo = uiFactory.createDropDownSelect(group);
		this.gmChannel2Combo.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				updateChannel();
			}
		});
		groupLayout.set(this.gmChannel2Combo, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		updateChannelCombos();
		
		TGDialogUtil.openDialog(dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	private void configureRouter(){
		GMChannelRouterConfigurator gmChannelRouterConfigurator = new GMChannelRouterConfigurator(this.router);
		gmChannelRouterConfigurator.configureRouter(this.song.getChannels());
	}
	
	private void updateChannelCombos(){
		GMChannelRoute route = this.router.getRoute(this.channel.getChannelId());
		
		List<Integer> channels = this.router.getFreeChannels(route);
		
		this.reloadChannelCombo(this.gmChannel1Combo, channels, route.getChannel1(), "gm.settings.channel.value-1");
		this.reloadChannelCombo(this.gmChannel2Combo, channels, route.getChannel2(), "gm.settings.channel.value-2");
		
		boolean playerRunning = TuxGuitar.getInstance().getPlayer().isRunning();
		
		this.gmChannel1Combo.setEnabled(!playerRunning && !this.channel.isPercussionChannel() && this.gmChannel1Combo.getItemCount() > 0);
		this.gmChannel2Combo.setEnabled(!playerRunning && !this.channel.isPercussionChannel() && this.gmChannel2Combo.getItemCount() > 0);
	}
	
	@SuppressWarnings("unchecked")
	private void reloadChannelCombo(UIDropDownSelect<Integer> combo, List<Integer> channels, int selected, String valueKey){
		if(!(combo.getData(CHANNELS_DATA) instanceof List) || isDifferentList(channels, (List<Integer>)combo.getData(CHANNELS_DATA))){
			combo.removeItems();
			combo.setData(CHANNELS_DATA, channels);
			for(Integer channel : channels){
				combo.addItem(new UISelectItem<Integer>(TuxGuitar.getProperty(valueKey, new String[]{channel.toString()}), channel));
			}
		}
		combo.setSelectedValue(selected);
	}
	
	public void updateChannel() {
		Integer channel1Selection = this.gmChannel1Combo.getSelectedValue();
		Integer channel2Selection = this.gmChannel2Combo.getSelectedValue();
		
		int channel1 = (channel1Selection != null ? channel1Selection : -1);
		int channel2 = (channel2Selection != null ? channel2Selection : -1);
		
		setChannelParameter(this.channel, GMChannelRoute.PARAMETER_GM_CHANNEL_1, Integer.toString(channel1));
		setChannelParameter(this.channel, GMChannelRoute.PARAMETER_GM_CHANNEL_2, Integer.toString(channel2));
		
		configureRouter();
	}
	
	private void setChannelParameter( TGChannel tgChannel, String key, String value ){
		TGChannelParameter tgChannelParameter = findOrCreateChannelParameter(tgChannel, key);
		tgChannelParameter.setValue(value);
	}
	
	private TGChannelParameter findChannelParameter( TGChannel tgChannel, String key ){
		Iterator<TGChannelParameter> it = tgChannel.getParameters();
		while( it.hasNext() ){
			TGChannelParameter parameter = (TGChannelParameter)it.next();
			if( parameter.getKey().equals( key ) ){
				return parameter;
			}
		}
		return null;
	}
	
	private TGChannelParameter findOrCreateChannelParameter( TGChannel tgChannel, String key ){
		TGChannelParameter tgChannelParameter = findChannelParameter(tgChannel, key);
		if( tgChannelParameter == null ){
			tgChannelParameter = TuxGuitar.getInstance().getSongManager().getFactory().newChannelParameter();
			tgChannelParameter.setKey(key);
			tgChannel.addParameter(tgChannelParameter);
		}
		return tgChannelParameter;
	}
	
	private boolean isDifferentList(List<?> list1, List<?> list2){
		if( list1.size() != list2.size() ){
			return true;
		}
		for( int i = 0 ; i < list1.size() ; i ++ ){
			if(!list1.get(i).equals(list2.get(i)) ){
				return true;
			}
		}
		return false;
	}
}
