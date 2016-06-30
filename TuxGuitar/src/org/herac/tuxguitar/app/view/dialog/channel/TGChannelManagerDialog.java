package org.herac.tuxguitar.app.view.dialog.channel;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.system.icons.TGIconEvent;
import org.herac.tuxguitar.app.system.language.TGLanguageEvent;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.util.TGCursorController;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.app.view.util.TGProcess;
import org.herac.tuxguitar.app.view.util.TGSyncProcess;
import org.herac.tuxguitar.app.view.util.TGSyncProcessLocked;
import org.herac.tuxguitar.editor.event.TGUpdateEvent;
import org.herac.tuxguitar.event.TGEvent;
import org.herac.tuxguitar.event.TGEventListener;
import org.herac.tuxguitar.song.models.TGChannel;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIDisposeEvent;
import org.herac.tuxguitar.ui.event.UIDisposeListener;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UICursor;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIControl;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILayoutContainer;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UIScale;
import org.herac.tuxguitar.ui.widget.UISeparator;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGChannelManagerDialog implements TGEventListener {
	
	private UIWindow dialog;
	
	private TGContext context;
	private TGChannelHandle channelHandle;
	private TGChannelList channelList;
	private TGChannelSettingsHandlerManager channelSettingsHandlerManager;
	private TGCursorController cursorController;
	private TGProcess loadPropertiesProcess;
	private TGProcess loadIconsProcess;
	private TGProcess updateItemsProcess;
	
	private UIButton addChannelButton;
	
	private UIScale volumeScale;
	private UILabel volumeValueLabel;
	private UILabel volumeValueTitleLabel;
	private String volumeTip;
	private int volumeValue;
	
	public TGChannelManagerDialog(TGContext context){
		this.context = context;
		this.channelHandle = new TGChannelHandle(context);
		this.channelSettingsHandlerManager = new TGChannelSettingsHandlerManager();
		this.createSyncProcesses();
	}
	
	public void show(TGViewContext viewContext){
		UIFactory uiFactory = this.getUIFactory();
		UIWindow uiParent = viewContext.getAttribute(TGViewContext.ATTRIBUTE_PARENT2);
		UITableLayout dialogLayout = new UITableLayout();
		
		this.dialog = uiFactory.createWindow(uiParent, false, true);
		this.dialog.setLayout(dialogLayout);
		
		UITableLayout compositeLayout = new UITableLayout();
		UIPanel composite = uiFactory.createPanel(this.dialog, false);
		composite.setLayout(compositeLayout);
		dialogLayout.set(composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UIControl mainControl = this.createChannelList(composite);
		compositeLayout.set(mainControl, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UIControl rightControl = this.createRightComposite(composite);
		compositeLayout.set(rightControl, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, true);
		
		this.updateItems(true);
		this.loadProperties();
		this.loadIcons();
		
		this.addListeners();
		this.dialog.addDisposeListener(new UIDisposeListener() {
			public void onDispose(UIDisposeEvent event) {
				removeListeners();
				TuxGuitar.getInstance().updateCache(true);
			}
		});
		
		TGDialogUtil.openDialog(this.dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public TGContext getContext(){
		return this.context;
	}
	
	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}
	
	public UIWindow getWindow(){
		return this.dialog;
	}
	
	public boolean isDisposed() {
		return (this.dialog == null || this.dialog.isDisposed());
	}
	
	public void dispose() {
		if(!isDisposed()){
			this.dialog.dispose();
		}
	}
	
	public void addListeners(){
		TuxGuitar.getInstance().getIconManager().addLoader(this);
		TuxGuitar.getInstance().getLanguageManager().addLoader(this);
		TuxGuitar.getInstance().getEditorManager().addUpdateListener(this);
	}
	
	public void removeListeners(){
		TuxGuitar.getInstance().getIconManager().removeLoader(this);
		TuxGuitar.getInstance().getLanguageManager().removeLoader(this);
		TuxGuitar.getInstance().getEditorManager().removeUpdateListener(this);
	}
	
	private UIPanel createRightComposite(UILayoutContainer parent){
		UIFactory uiFactory = this.getUIFactory();
		
		UITableLayout rightCompositeLayout = new UITableLayout();
		UIPanel rightComposite = uiFactory.createPanel(parent, true);
		rightComposite.setLayout(rightCompositeLayout);
		
		UITableLayout toolbarCompositeLayout = new UITableLayout();
		UIPanel toolbarComposite = uiFactory.createPanel(rightComposite, true);
		toolbarComposite.setLayout(toolbarCompositeLayout);
		rightCompositeLayout.set(toolbarComposite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_TOP, true, false);
		
		this.addChannelButton = uiFactory.createButton(toolbarComposite);
		this.addChannelButton.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				getHandle().addChannel();
			}
		});
		toolbarCompositeLayout.set(this.addChannelButton, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		UITableLayout volumeCompositeLayout = new UITableLayout();
		UIPanel volumeComposite = uiFactory.createPanel(rightComposite, true);
		volumeComposite.setLayout(volumeCompositeLayout);
		rightCompositeLayout.set(volumeComposite, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		
		this.volumeScale = uiFactory.createVerticalScale(volumeComposite);
		this.volumeScale.setMaximum(10);
		this.volumeScale.setMinimum(0);
		this.volumeScale.setIncrement(1);
		volumeCompositeLayout.set(this.volumeScale, 1, 1, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_FILL, true, true);
		
		UISeparator separator = uiFactory.createHorizontalSeparator(volumeComposite);
		volumeCompositeLayout.set(separator, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		
		UITableLayout volumeValueLayout = new UITableLayout();
		UIPanel volumeValueComposite = uiFactory.createPanel(volumeComposite, false);
		volumeValueComposite.setLayout(volumeValueLayout);
		volumeCompositeLayout.set(volumeValueComposite, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		
		this.volumeValueTitleLabel = uiFactory.createLabel(volumeValueComposite);
		volumeValueLayout.set(this.volumeValueTitleLabel, 1, 1, UITableLayout.ALIGN_LEFT, UITableLayout.ALIGN_CENTER, false, false);
		
		this.volumeValueLabel = uiFactory.createLabel(volumeValueComposite);
		volumeValueLayout.set(this.volumeValueLabel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		this.volumeScale.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				changeVolume();
			}
		});
		
		return rightComposite;
	}
	
	private UIControl createChannelList(UILayoutContainer parent){
		this.channelList = new TGChannelList(this);
		this.channelList.show(parent);
		
		return this.channelList.getControl();
	}
	
	private void changeVolume(){
		int volume = (short)(this.volumeScale.getMaximum() - this.volumeScale.getValue());
		if( volume != TuxGuitar.getInstance().getPlayer().getVolume()){
			TuxGuitar.getInstance().getPlayer().setVolume(volume);
			this.volumeScale.setToolTipText(this.volumeTip + ": " + TuxGuitar.getInstance().getPlayer().getVolume());
			this.volumeValueLabel.setText(Integer.toString(this.volumeScale.getMaximum() - this.volumeScale.getValue()));
			this.volumeValue = volume;
		}
	}
	
	public void updateItems() {
		this.updateItems(false);
	}
	
	public void updateItems(boolean force){
		if(!this.isDisposed()){
			this.loadCursor(UICursor.WAIT);
			
			this.channelList.updateItems();
			
			int volume = TuxGuitar.getInstance().getPlayer().getVolume();
			if( force || this.volumeValue != volume ){
				this.volumeScale.setValue(this.volumeScale.getMaximum() - TuxGuitar.getInstance().getPlayer().getVolume());
				this.volumeValueLabel.setText(Integer.toString(this.volumeScale.getMaximum() - this.volumeScale.getValue()));
				this.volumeValue = volume;
			}
			
			this.loadCursor(UICursor.NORMAL);
		}
	}

	public void loadProperties() {
		if(!this.isDisposed()){
			this.addChannelButton.setText(TuxGuitar.getProperty("add"));
			
			this.volumeValueTitleLabel.setText(TuxGuitar.getProperty("instruments.volume") + ":");
			this.volumeTip = TuxGuitar.getProperty("instruments.volume");
			this.volumeScale.setToolTipText(this.volumeTip + ": " + TuxGuitar.getInstance().getPlayer().getVolume());
			this.dialog.setText(TuxGuitar.getProperty("instruments.dialog-title"));
			
			this.channelList.loadProperties();
		}
	}

	public void loadIcons() {
		if(!this.isDisposed()){
			this.channelList.loadIcons();
		}
	}
	
	public void loadCursor(UICursor cursor) {
		if(!this.isDisposed()) {
			if( this.cursorController == null || !this.cursorController.isControlling(this.dialog) ) {
				this.cursorController = new TGCursorController(this.context, this.dialog);
			}
			this.cursorController.loadCursor(cursor);
		}
	}
	
	public void onUpdateChannel(TGChannel channel){
		if (TuxGuitar.getInstance().getPlayer().isRunning()) {
			TuxGuitar.getInstance().getPlayer().updateControllers();
		}
	}
	
	public TGChannelHandle getHandle(){
		return this.channelHandle;
	}
	
	public TGChannelSettingsHandlerManager getChannelSettingsHandlerManager() {
		return this.channelSettingsHandlerManager;
	}
	
	public void createSyncProcesses() {
		this.loadPropertiesProcess = new TGSyncProcess(this.context, new Runnable() {
			public void run() {
				loadProperties();
			}
		});
		
		this.loadIconsProcess = new TGSyncProcess(this.context, new Runnable() {
			public void run() {
				loadIcons();
			}
		});
		
		this.updateItemsProcess = new TGSyncProcessLocked(this.context, new Runnable() {
			public void run() {
				updateItems();
			}
		});
	}
	
	public void processUpdateEvent(TGEvent event) {
		int type = ((Integer)event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE)).intValue();
		if( type == TGUpdateEvent.SELECTION ){
			this.updateItemsProcess.process();
		}
	}
	
	public void processEvent(final TGEvent event) {
		if( TGIconEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadIconsProcess.process();
		}
		else if( TGLanguageEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.loadPropertiesProcess.process();
		}
		else if( TGUpdateEvent.EVENT_TYPE.equals(event.getEventType()) ) {
			this.processUpdateEvent(event);
		}
	}
	
	public static TGChannelManagerDialog getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGChannelManagerDialog.class.getName(), new TGSingletonFactory<TGChannelManagerDialog>() {
			public TGChannelManagerDialog createInstance(TGContext context) {
				return new TGChannelManagerDialog(context);
			}
		});
	}
}
