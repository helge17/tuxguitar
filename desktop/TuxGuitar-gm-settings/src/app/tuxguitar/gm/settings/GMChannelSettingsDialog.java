package app.tuxguitar.gm.settings;

import java.util.Iterator;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.dialog.channel.TGChannelSettingsDialog;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.gm.GMChannelRoute;
import app.tuxguitar.gm.GMChannelRouter;
import app.tuxguitar.gm.GMChannelRouterConfigurator;
import app.tuxguitar.gm.port.GMSynthesizer;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGChannelParameter;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.widget.UIDropDownSelect;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UILegendPanel;
import app.tuxguitar.ui.widget.UISelectItem;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class GMChannelSettingsDialog implements TGChannelSettingsDialog{

	public static final String CHANNELS_DATA = "channels";

	private TGContext context;
	private TGSong song;
	private TGChannel channel;
	private GMChannelRouter router;
	private UIWindow dialog;
	private UIDropDownSelect<Integer> gmChannel1Combo;
	private UIDropDownSelect<Integer> gmChannel2Combo;

	public GMChannelSettingsDialog(TGContext context, TGChannel channel, TGSong song, GMSynthesizer synthesizer){
		this.context = context;
		this.song = song;
		this.channel = channel;
		this.router = synthesizer.getRouter();
	}

	public void open(final UIWindow parent) {
		this.configureRouter(true);

		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UITableLayout dialogLayout = new UITableLayout();

		this.dialog = uiFactory.createWindow(parent, false, false);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setText(TuxGuitar.getProperty("gm.settings.dialog.title"));

		// ----------------------------------------------------------------------
		UITableLayout groupLayout = new UITableLayout();
		UILegendPanel group = uiFactory.createLegendPanel(this.dialog);
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

		TGDialogUtil.openDialog(this.dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	public void close() {
		if( this.isOpen()) {
			this.dialog.dispose();
		}
	}

	public boolean isOpen(){
		return (this.dialog != null && !this.dialog.isDisposed());
	}

	private void configureRouter(boolean updateChannel){
		GMChannelRouterConfigurator gmChannelRouterConfigurator = new GMChannelRouterConfigurator(this.router);
		gmChannelRouterConfigurator.configureRouter(this.song.getChannels());

		if( updateChannel ) {
			this.updateChannelFromRouter();
		}
	}

	public void updateChannelFromRouter(){
		GMChannelRoute route = this.router.getRoute(this.channel.getChannelId());

		this.setChannelParameter(this.channel, GMChannelRoute.PARAMETER_GM_CHANNEL_1, Integer.toString(route.getChannel1()));
		this.setChannelParameter(this.channel, GMChannelRoute.PARAMETER_GM_CHANNEL_2, Integer.toString(route.getChannel2()));
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

		configureRouter(false);
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
