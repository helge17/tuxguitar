package org.herac.tuxguitar.app.view.dialog.channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.herac.tuxguitar.song.models.TGChannel;

public class TGChannelList {
	
	private List<TGChannelItem> channelItems;
	private TGChannelManagerDialog dialog;
	
	protected ScrolledComposite channelItemAreaSC;
	protected Composite channelItemArea;
	
	public TGChannelList(TGChannelManagerDialog dialog){
		this.dialog = dialog;
		this.channelItems = new ArrayList<TGChannelItem>();
	}
	
	public void show(final Composite parent){
		this.channelItemAreaSC = new ScrolledComposite(parent, SWT.NONE | SWT.V_SCROLL);
		this.channelItemAreaSC.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.channelItemAreaSC.setExpandHorizontal(true);
		this.channelItemAreaSC.setExpandVertical(true);
		
		this.channelItemArea = new Composite(this.channelItemAreaSC, SWT.NONE);
		this.channelItemArea.setLayout(this.dialog.createGridLayout(1,false, true, false));
		this.channelItemArea.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		this.channelItemAreaSC.setContent(this.channelItemArea);
	}
	
	public void removeChannelsAfter( int count ){
		while(!this.channelItems.isEmpty() && this.channelItems.size() > count ){
			TGChannelItem tgChannelItem = (TGChannelItem)this.channelItems.get(0);
			tgChannelItem.dispose();
			
			this.channelItemAreaSC.setMinSize(this.channelItemArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			this.channelItemArea.layout(true,true);
			this.channelItems.remove(tgChannelItem);
		}
	}
	
	public TGChannelItem getOrCreateChannelItemAt( int index ){
		while( this.channelItems.size() <= index ){
			TGChannelItem tgChannelItem = new TGChannelItem(this.dialog);
			tgChannelItem.show(this.channelItemArea, new GridData(SWT.FILL,SWT.TOP,true,false));
			
			this.channelItemAreaSC.setMinSize(this.channelItemArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			this.channelItemArea.layout(true,true);
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
}
