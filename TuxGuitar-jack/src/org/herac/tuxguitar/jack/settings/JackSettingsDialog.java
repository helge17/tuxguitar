package org.herac.tuxguitar.jack.settings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.util.DialogUtils;
import org.herac.tuxguitar.jack.synthesizer.JackOutputPortRouter;

public class JackSettingsDialog {
	
	private static final int TAB_WIDTH = 550;
	private static final int TAB_HEIGHT = 350;
	
	private JackSettings settings;
	
	public JackSettingsDialog( JackSettings settings ){
		this.settings = settings;
	}
	
	public void open( Shell parent ){
		final int[][] channelRouting = getChannelRoutingSettings();
		final int[][] programRouting = getProgramRoutingSettings();
		
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setText(TuxGuitar.getProperty("jack.settings.dialog"));
		dialog.setLayout(new GridLayout());
		dialog.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		final TabFolder tabFolder = new TabFolder(dialog, SWT.TOP);
		tabFolder.setLayout( new FormLayout() );
		
		final TabItem[] routingTabs = new TabItem[2];
		
		// ----------------------------------------------------------------------
		int synthRouteType = this.settings.getConfig().getIntConfigValue("jack.midi.ports.type", JackOutputPortRouter.CREATE_UNIQUE_PORT );
		
		Composite tabControl = new Composite( tabFolder, SWT.NONE);
		tabControl.setLayout(new GridLayout());
		tabControl.setLayoutData(new FormData(TAB_WIDTH,TAB_HEIGHT));
		
		TabItem tabItem = new TabItem( tabFolder  , SWT.None ); 
		tabItem.setText(TuxGuitar.getProperty("jack.settings.dialog.options"));
		tabItem.setControl(tabControl);
		
		Group groupSynth = new Group(tabControl,SWT.SHADOW_ETCHED_IN);
		groupSynth.setLayout(new GridLayout(1,false));
		groupSynth.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		groupSynth.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port"));
		
		final Button buttonSynthRouteType1 = new Button(groupSynth, SWT.RADIO);
		final Button buttonSynthRouteType2 = new Button(groupSynth, SWT.RADIO);
		final Button buttonSynthRouteType3 = new Button(groupSynth, SWT.RADIO);
		
		buttonSynthRouteType1.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.type.single"));
		buttonSynthRouteType2.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.type.multiple-by-channel") );
		buttonSynthRouteType3.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.type.multiple-by-program") );
		
		if( synthRouteType == JackOutputPortRouter.CREATE_MULTIPLE_PORTS_BY_CHANNEL ){
			buttonSynthRouteType2.setSelection( true );
			routingTabs[0] = openChannelRoutingTab(tabFolder, channelRouting);
		}else if( synthRouteType == JackOutputPortRouter.CREATE_MULTIPLE_PORTS_BY_PROGRAM ){
			buttonSynthRouteType3.setSelection( true );
			routingTabs[1] = openProgramRoutingTab(tabFolder, programRouting);
		}else{
			buttonSynthRouteType1.setSelection( true );
		}
		
		final SelectionListener routeTypeListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				boolean channelRoutingTabsEnabled = buttonSynthRouteType2.getSelection();
				boolean programRoutingTabsEnabled = buttonSynthRouteType3.getSelection();
				
				if( channelRoutingTabsEnabled && routingTabs[0] == null ){
					routingTabs[0] = openChannelRoutingTab(tabFolder, channelRouting);
				}else if( !channelRoutingTabsEnabled && routingTabs[0] != null ){
					routingTabs[0].dispose();
					routingTabs[0] = null;
				}
				if( programRoutingTabsEnabled && routingTabs[1] == null ){
					routingTabs[1] = openProgramRoutingTab(tabFolder, programRouting);
				}else if( !programRoutingTabsEnabled && routingTabs[1] != null ){
					routingTabs[1].dispose();
					routingTabs[1] = null;
				}
			}
		};
		buttonSynthRouteType1.addSelectionListener(routeTypeListener);
		buttonSynthRouteType2.addSelectionListener(routeTypeListener);
		buttonSynthRouteType3.addSelectionListener(routeTypeListener);
		
		// ------------------BUTTONS--------------------------
		Composite compositeButtons = new Composite(dialog, SWT.NONE);
		compositeButtons.setLayout(new GridLayout(2,false));
		compositeButtons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(compositeButtons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				int type = JackOutputPortRouter.CREATE_UNIQUE_PORT;
				if( buttonSynthRouteType2.getSelection() ){
					type = JackOutputPortRouter.CREATE_MULTIPLE_PORTS_BY_CHANNEL;
				}
				else if( buttonSynthRouteType3.getSelection() ){
					type = JackOutputPortRouter.CREATE_MULTIPLE_PORTS_BY_PROGRAM;
				}
				saveChanges( type , channelRouting , programRouting );
				dialog.dispose();
			}
		});
		
		Button buttonCancel = new Button(compositeButtons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(getButtonData());
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
			}
		});
		
		dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
	}
	
	protected GridData getButtonData(){
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	protected void saveChanges( int type , int[][] channelRouting, int[][] programRouting){
		if( type == JackOutputPortRouter.CREATE_MULTIPLE_PORTS_BY_CHANNEL ){
			this.setChannelRoutingSettings(channelRouting);
		}
		else if( type == JackOutputPortRouter.CREATE_MULTIPLE_PORTS_BY_PROGRAM ){
			this.setProgramRoutingSettings(programRouting);
		}
		this.settings.getConfig().setProperty("jack.midi.ports.type", type );
		this.settings.notifyChanges();
	}
	
	protected int[][] getChannelRoutingSettings(){
		int[][] channelsRoute = new int[16][];
		for( int i = 0 ; i < channelsRoute.length ; i ++ ){
			channelsRoute[i] = new int[3];
			channelsRoute[i][0] = i;
			channelsRoute[i][1] = this.settings.getConfig().getIntConfigValue("jack.midi.port.channel-routing.to-channel-" + i , -1 );
			channelsRoute[i][2] = this.settings.getConfig().getIntConfigValue("jack.midi.port.channel-routing.to-program-" + i , -1 );
		}
		return channelsRoute;
	}
	
	protected void setChannelRoutingSettings(int[][] channelsRoute){
		for( int i = 0 ; i < channelsRoute.length ; i ++ ){
			if( channelsRoute[i].length == 3 ){
				this.settings.getConfig().setProperty("jack.midi.port.channel-routing.to-channel-" + channelsRoute[i][0] , channelsRoute[i][1] );
				this.settings.getConfig().setProperty("jack.midi.port.channel-routing.to-program-" + channelsRoute[i][0] , channelsRoute[i][2] );
			}
		}
	}
	
	protected int[][] getProgramRoutingSettings(){
		int[][] routing = new int[129][];
		for( int i = 0 ; i < routing.length ; i ++ ){
			routing[i] = new int[4];
			routing[i][0] = i;
			routing[i][1] = this.settings.getConfig().getIntConfigValue("jack.midi.port.program-routing.port-" + i , 0 );
			routing[i][2] = this.settings.getConfig().getIntConfigValue("jack.midi.port.program-routing.to-channel-" + i , -1 );
			routing[i][3] = this.settings.getConfig().getIntConfigValue("jack.midi.port.program-routing.to-program-" + i , -1 );
		}
		return routing;
	}
	
	protected void setProgramRoutingSettings(int[][] routing){
		for( int i = 0 ; i < routing.length ; i ++ ){
			if( routing[i].length == 4 ){
				this.settings.getConfig().setProperty("jack.midi.port.program-routing.port-" + routing[i][0] , routing[i][1] );
				this.settings.getConfig().setProperty("jack.midi.port.program-routing.to-channel-" + routing[i][0] , routing[i][2] );
				this.settings.getConfig().setProperty("jack.midi.port.program-routing.to-program-" + routing[i][0] , routing[i][3] );
			}
		}
	}
	
	// ----------------------------------------------------------------------------------------------------//
	// PROGRAM ROUTING ------------------------------------------------------------------------------------//
	// ----------------------------------------------------------------------------------------------------//
	
	public TabItem openProgramRoutingTab( TabFolder tabFolder , int[][] routing ){
		Composite tabControl = new Composite( tabFolder, SWT.NONE);
		tabControl.setLayout(new GridLayout());
		tabControl.setLayoutData(new FormData(TAB_WIDTH,TAB_HEIGHT));
		
		TabItem tabItem = new TabItem( tabFolder  , SWT.None ); 
		tabItem.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.options"));
		tabItem.setControl(tabControl);
		
		Composite composite = new Composite( tabControl, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		final Table table = new Table( composite, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setHeaderVisible(true);
		
		TableColumn srcChannelColumn = new TableColumn(table, SWT.LEFT);
		TableColumn dstPortColumn = new TableColumn(table, SWT.LEFT);
		TableColumn dstChannelColumn = new TableColumn(table, SWT.LEFT);
		TableColumn dstProgramColumn = new TableColumn(table, SWT.LEFT);
		
		srcChannelColumn.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.src-program"));
		dstPortColumn.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.dst-port"));
		dstChannelColumn.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.dst-channel"));
		dstProgramColumn.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.dst-program"));
		
		srcChannelColumn.setWidth(125);
		dstPortColumn.setWidth(125);
		dstChannelColumn.setWidth(125);
		dstProgramColumn.setWidth(125);
		
		loadProgramRoutingItems( table , routing );
		
		//-------------------------------------------------------//
		Composite buttons = new Composite( composite , SWT.NONE);
		buttons.setLayout(new GridLayout(1,false));
		buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.BOTTOM,true,false));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("edit"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				int index = table.getSelectionIndex();
				if( index >= 0 && index < table.getItemCount() ){
					editProgramRoutingItem( table.getShell(),  table.getItem( index ) );
				}
			}
		});
		//-------------------------------------------------------//
		return tabItem;
	}
	
	protected void loadProgramRoutingItems( Table table , int[][] routing ){
		for( int i = 0 ; i < routing.length ; i ++ ){
			this.addProgramRoutingItem(table, routing[i]);
		}
	}
	
	protected void addProgramRoutingItem( Table table, int[] route){
		TableItem item = new TableItem(table, SWT.NONE );
		this.addProgramRoutingItem(item, route);
	}
	
	protected void addProgramRoutingItem( TableItem item, int[] route){
		if( route.length == 4 ){
			String[] text = new String[4];
			if( route[0] < 128 ){
				text[0] = (TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.src-program.item", new String[]{ Integer.toString(route[0]) }));
			}else{
				text[0] = (TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.src-program.percussion"));
			}
			if( route[1] > 0 ){
				text[1] = (TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.dst-port.dedicated"));
			}else{
				text[1] = (TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.dst-port.default"));
			}
			if( route[2] >= 0 ){
				text[2] = (TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.dst-channel.item", new String[]{ Integer.toString(route[2]) }));
			}else{
				text[2] = (TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.dst-channel.default"));
			}
			if( route[3] >= 0 ){
				text[3] = (TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.dst-program.item", new String[]{ Integer.toString(route[3]) }));
			}else{
				text[3] = (TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.dst-program.default"));
			}
			item.setText( text );
			item.setData( route );
		}
	}
	
	public void editProgramRoutingItem( Shell parent , final TableItem item){
		final int[] route = (int [])item.getData();
		
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM);
		dialog.setLayout(new GridLayout(1,false));
		dialog.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		dialog.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.options"));
		// ----------------------------------------------------------------------
		Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout(2,false));
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.options"));
		
		Label program = new Label(group, SWT.NULL);
		program.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,true));
		program.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.src-program") + ":");
		
		Label programValue = new Label(group, SWT.NULL);
		programValue.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true));
		if( route[0] < 128 ){
			programValue.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.src-program.item", new String[]{ Integer.toString(route[0]) }));
		}else{
			programValue.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.src-program.percussion"));
		}
		
		Label programPort = new Label(group, SWT.NULL);
		programPort.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,true));
		programPort.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.dst-port") + ":");
		
		final Combo programPortValue = new Combo(group,SWT.DROP_DOWN | SWT.READ_ONLY);
		programPortValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		programPortValue.add(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.dst-port.default"));
		programPortValue.add(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.dst-port.dedicated"));
		programPortValue.select( route[1] );
		
		Label channelRoute = new Label(group, SWT.NULL);
		channelRoute.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,true));
		channelRoute.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.dst-channel") + ":");
		
		final Combo channelRouteValue = new Combo(group,SWT.DROP_DOWN | SWT.READ_ONLY);
		channelRouteValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		channelRouteValue.add(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.dst-channel.default"));
		for( int i = 0 ; i < 16 ; i ++ ){
			channelRouteValue.add(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.dst-channel.item", new String[]{Integer.toString(i)}));
		}
		channelRouteValue.select( route[2] + 1 );
		
		Label programRoute = new Label(group, SWT.NULL);
		programRoute.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,true));
		programRoute.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.dst-program") + ":");
		
		final Combo programRouteValue = new Combo(group,SWT.DROP_DOWN | SWT.READ_ONLY);
		programRouteValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		programRouteValue.add(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.dst-program.default"));
		for( int i = 0 ; i < 128 ; i ++ ){
			programRouteValue.add(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.program-router.dst-program.item", new String[]{Integer.toString(i)}));
		}
		programRouteValue.select( route[3] + 1 );
		
		// ------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				route[1] = programPortValue.getSelectionIndex();
				route[2] = channelRouteValue.getSelectionIndex() - 1;
				route[3] = programRouteValue.getSelectionIndex() - 1;
				addProgramRoutingItem(item, route);
				dialog.dispose();
			}
		});
		
		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(getButtonData());
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
			}
		});
		
		dialog.setDefaultButton( buttonCancel );
		
		DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
	
	// ----------------------------------------------------------------------------------------------------//
	// CHANNEL ROUTING ------------------------------------------------------------------------------------//
	// ----------------------------------------------------------------------------------------------------//
	
	public TabItem openChannelRoutingTab( TabFolder tabFolder , int[][] routing ){
		Composite tabControl = new Composite( tabFolder, SWT.NONE);
		tabControl.setLayout(new GridLayout());
		tabControl.setLayoutData(new FormData(TAB_WIDTH,TAB_HEIGHT));
		
		TabItem tabItem = new TabItem( tabFolder  , SWT.None ); 
		tabItem.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.channel-router.options"));
		tabItem.setControl(tabControl);
		
		Composite composite = new Composite( tabControl, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		final Table table = new Table( composite, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setHeaderVisible(true);
		
		TableColumn srcChannelColumn = new TableColumn(table, SWT.LEFT);
		TableColumn dstChannelColumn = new TableColumn(table, SWT.LEFT);
		TableColumn dstProgramColumn = new TableColumn(table, SWT.LEFT);
		
		srcChannelColumn.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.channel-router.src-channel"));
		dstChannelColumn.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.channel-router.dst-channel"));
		dstProgramColumn.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.channel-router.dst-program"));
		
		srcChannelColumn.setWidth(166);
		dstChannelColumn.setWidth(166);
		dstProgramColumn.setWidth(166);
		
		loadChannelRoutingItems( table , routing );
		
		//-------------------------------------------------------//
		Composite buttons = new Composite( composite , SWT.NONE);
		buttons.setLayout(new GridLayout(1,false));
		buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.BOTTOM,true,false));
		
		final Button buttonEdit = new Button(buttons, SWT.PUSH);
		buttonEdit.setText(TuxGuitar.getProperty("edit"));
		buttonEdit.setLayoutData(getButtonData());
		buttonEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				int index = table.getSelectionIndex();
				if( index >= 0 && index < table.getItemCount() ){
					editChannelRoutingItem( table.getShell(),  table.getItem( index ) );
				}
			}
		});
		//-------------------------------------------------------//
		
		return tabItem;
	}
	
	protected void loadChannelRoutingItems( Table table , int[][] routing ){
		for( int i = 0 ; i < routing.length ; i ++ ){
			this.addChannelRoutingItem(table, routing[i]);
		}
	}
	
	protected void addChannelRoutingItem( Table table, int[] route){
		TableItem item = new TableItem(table, SWT.NONE );
		this.addChannelRoutingItem(item, route);
	}
	
	protected void addChannelRoutingItem( TableItem item, int[] route){
		if( route.length == 3 ){
			String[] text = new String[3];
			
			text[0] = (TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.channel-router.src-channel.item", new String[]{ Integer.toString(route[0]) }));
			if( route[1] >= 0 ){
				text[1] = (TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.channel-router.dst-channel.item", new String[]{ Integer.toString(route[1]) }));
			}else{
				text[1] = (TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.channel-router.dst-channel.default"));
			}
			if( route[2] >= 0 ){
				text[2] = (TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.channel-router.dst-program.item", new String[]{ Integer.toString(route[2]) }));
			}else{
				text[2] = (TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.channel-router.dst-program.default"));
			}
			item.setText( text );
			item.setData( route );
		}
	}
	
	public void editChannelRoutingItem( Shell parent , final TableItem item){
		final int[] route = (int [])item.getData();
		
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM);
		dialog.setLayout(new GridLayout(1,false));
		dialog.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		dialog.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.channel-router.options"));
		// ----------------------------------------------------------------------
		Group group = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout(2,false));
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.channel-router.options"));
		
		Label channel = new Label(group, SWT.NULL);
		channel.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,true));
		channel.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.channel-router.src-channel") + ":");
		
		Label channelValue = new Label(group, SWT.NULL);
		channelValue.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true));
		channelValue.setText( TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.channel-router.src-channel.item", new String[]{ Integer.toString(route[0]) }) );
		
		Label channelRoute = new Label(group, SWT.NULL);
		channelRoute.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,true));
		channelRoute.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.channel-router.dst-channel") + ":");
		
		final Combo channelRouteValue = new Combo(group,SWT.DROP_DOWN | SWT.READ_ONLY);
		channelRouteValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		channelRouteValue.add(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.channel-router.dst-channel.default"));
		for( int i = 0 ; i < 16 ; i ++ ){
			channelRouteValue.add(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.channel-router.dst-channel.item", new String[]{Integer.toString(i)}));
		} 
		channelRouteValue.select( route[1] + 1 );
		
		Label programRoute = new Label(group, SWT.NULL);
		programRoute.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,true));
		programRoute.setText(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.channel-router.dst-program") + ":");
		
		final Combo programRouteValue = new Combo(group,SWT.DROP_DOWN | SWT.READ_ONLY);
		programRouteValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		programRouteValue.add(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.channel-router.dst-program.default"));
		for( int i = 0 ; i < 127 ; i ++ ){
			programRouteValue.add(TuxGuitar.getProperty("jack.settings.dialog.options.midi-port.channel-router.dst-program.item", new String[]{Integer.toString(i)}));
		}
		programRouteValue.select( route[2] + 1 );
		
		// ------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,true,true));
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData());
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				route[1] = channelRouteValue.getSelectionIndex() - 1;
				route[2] = programRouteValue.getSelectionIndex() - 1;
				addChannelRoutingItem(item, route);
				dialog.dispose();
			}
		});
		
		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(getButtonData());
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
			}
		});
		
		dialog.setDefaultButton( buttonCancel );
		
		DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK);
	}
}
