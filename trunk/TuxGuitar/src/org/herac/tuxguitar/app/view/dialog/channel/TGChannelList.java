package org.herac.tuxguitar.app.view.dialog.channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UIScrollBarPanelLayout;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIScrollBarPanel;

public class TGChannelList {
	
	private static final int SCROLL_INCREMENT = 10;
	
	private List<TGChannelItem> channelItems;
	private TGChannelManagerDialog dialog;
	
	protected UIScrollBarPanel channelItemAreaSC;
	protected UIPanel channelItemArea;
	
	public TGChannelList(TGChannelManagerDialog dialog){
		this.dialog = dialog;
		this.channelItems = new ArrayList<TGChannelItem>();
	}
	
	public void show(UILayoutContainer parent){
		UIFactory uiFactory = this.dialog.getUIFactory();
		
		this.channelItemAreaSC = uiFactory.createScrollBarPanel(parent, true, false, true);
		this.channelItemAreaSC.setLayout(new UIScrollBarPanelLayout(false, true, true, true, false, true));
		
		this.channelItemAreaSC.getVScroll().setIncrement(SCROLL_INCREMENT);
		this.channelItemAreaSC.getVScroll().addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				TGChannelList.this.channelItemAreaSC.layout();
			}
		});
		
		this.channelItemArea = uiFactory.createPanel(this.channelItemAreaSC, false);
		this.channelItemArea.setLayout(new UITableLayout());
	}
	
	public void removeChannelsAfter( int count ){
		while(!this.channelItems.isEmpty() && this.channelItems.size() > count ){
			TGChannelItem tgChannelItem = (TGChannelItem)this.channelItems.get(0);
			tgChannelItem.dispose();
			
			this.channelItemAreaSC.layout();
			this.channelItems.remove(tgChannelItem);
		}
	}
	
	public TGChannelItem getOrCreateChannelItemAt( int index ){
		while( this.channelItems.size() <= index ){
			TGChannelItem tgChannelItem = new TGChannelItem(this.dialog);
			tgChannelItem.show(this.channelItemArea);
			
			UITableLayout uiLayout = (UITableLayout) this.channelItemArea.getLayout();
			uiLayout.set(tgChannelItem.getComposite(), (this.channelItems.size() + 1), 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, false);
			
			this.channelItemAreaSC.layout();
			this.channelItems.add(tgChannelItem);
		}
		return (TGChannelItem)this.channelItems.get(index);
	}
	
	public void loadProperties(){
		Iterator<TGChannelItem> it = this.channelItems.iterator();
		while( it.hasNext() ){
			TGChannelItem tgChannelItem = (TGChannelItem)it.next();
			tgChannelItem.loadProperties();
		}
	}
	
	public void loadIcons(){
		Iterator<TGChannelItem> it = this.channelItems.iterator();
		while( it.hasNext() ){
			TGChannelItem tgChannelItem = (TGChannelItem)it.next();
			tgChannelItem.loadIcons();
		}
	}
	
	public void updateItems(){
		List<TGChannel> channels = this.dialog.getHandle().getChannels();
		
		this.removeChannelsAfter(channels.size());
		
		for( int i = 0 ; i < channels.size() ; i ++ ){
			TGChannel channel = (TGChannel)channels.get(i);
			TGChannelItem tgChannelItem = getOrCreateChannelItemAt(i);
			tgChannelItem.setChannel(channel);
			tgChannelItem.updateItems();
		}
	}
	
	public UIScrollBarPanel getControl() {
		return this.channelItemAreaSC;
	}
}
