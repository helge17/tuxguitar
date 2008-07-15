package org.herac.tuxguitar.player.impl.midiport.fluidsynth;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.system.config.TGConfigManager;
import org.herac.tuxguitar.gui.system.plugins.TGPluginConfigManager;
import org.herac.tuxguitar.gui.util.DialogUtils;

public class MidiPortSettings {
	
	private static final int TABLE_WIDTH = 350;
	private static final int TABLE_HEIGHT = 200;
	
	private TGConfigManager config;
	private MidiPortProviderImpl provider;
	
	public MidiPortSettings(MidiPortProviderImpl provider){
		this.provider = provider;
	}
	
	public TGConfigManager getConfig(){
		if(this.config == null){
			this.config = new TGPluginConfigManager("tuxguitar-fluidsynth");
			this.config.init();
		}
		return this.config;
	}
	
	public String getDriver(){
		return getConfig().getStringConfigValue("audio.driver");
	}
	
	public void setDriver(String driver){
		TGConfigManager config = getConfig();
		if(driver == null ){
			config.removeProperty("audio.driver");
		}else{
			config.setProperty("audio.driver", driver );
		}
	}
	
	public List getSoundfonts(){
		List ports = new ArrayList();
		TGConfigManager config = getConfig();
		
		int count = config.getIntConfigValue("soundfont.count");
		for(int i = 0; i < count;i ++){
			String path = config.getStringConfigValue("soundfont.path" + i);
			if(path != null && path.length() > 0 ){
				ports.add( path );
			}
		}
		return ports;
	}
	
	public void setSoundfonts(List soundfonts){
		TGConfigManager config = getConfig();
		config.setProperty("soundfont.count", soundfonts.size() );
		for( int i = 0 ; i < soundfonts.size() ; i ++ ){
			String path = (String)soundfonts.get( i );
			config.setProperty("soundfont.path" + i, path );
		}
	}
	
	public void configure(Shell parent) {
		final Shell dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setText(TuxGuitar.getProperty("fluidsynth.settings"));
		dialog.setLayout(new GridLayout());
		dialog.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		// ----------------------------------------------------------------------		
		final List drivers = this.provider.getSynth().getDrivers();
		String driverSelected = getDriver();
		
		Composite compositeDriver = new Composite(dialog, SWT.NONE);
		compositeDriver.setLayout(new GridLayout(2, false));
		compositeDriver.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label driverLabel = new Label(compositeDriver, SWT.NONE);
		driverLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true));
		driverLabel.setText(TuxGuitar.getProperty("fluidsynth.settings.driver"));
		
		final Combo driverCombo = new Combo(compositeDriver, SWT.DROP_DOWN | SWT.READ_ONLY);
		driverCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		driverCombo.add(TuxGuitar.getProperty("fluidsynth.settings.driver.default"));
		int driverSelectionIndex = 0; 
		for(int i = 0 ; i < drivers.size(); i ++){
			String name = (String)drivers.get(i);
			driverCombo.add( name );
			if( driverSelected != null && driverSelected.equals( name )){
				driverSelectionIndex = ( i + 1 );
			}
		}
		driverCombo.select( driverSelectionIndex );
		
		// ----------------------------------------------------------------------
		Composite compositeSoundfonts = new Composite(dialog, SWT.NONE);
		compositeSoundfonts.setLayout(getGridLayout( 2 ));
		compositeSoundfonts.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		Composite compositeTable = new Composite(compositeSoundfonts, SWT.NONE);
		compositeTable.setLayout(new GridLayout());
		compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		final Table table = new Table(compositeTable, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		table.setLayoutData(new GridData(TABLE_WIDTH,TABLE_HEIGHT));
		table.setHeaderVisible(true);
		
		TableColumn column = new TableColumn(table, SWT.NONE);
		column.setWidth(TABLE_WIDTH);
		column.setText(TuxGuitar.getProperty("fluidsynth.settings.soundfont-list"));
		
		// ------------------BUTTONS--------------------------
		Composite compositeButtons = new Composite(compositeSoundfonts, SWT.NONE);
		compositeButtons.setLayout(new GridLayout());
		compositeButtons.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		Button buttonAdd = new Button(compositeButtons, SWT.PUSH);
		buttonAdd.setLayoutData(getButtonData(SWT.FILL,SWT.TOP, true,false));
		buttonAdd.setText(TuxGuitar.getProperty("add"));
		buttonAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addMidiPort(table);
			}
		});
		
		Button buttonDelete = new Button(compositeButtons, SWT.PUSH);
		buttonDelete.setText(TuxGuitar.getProperty("remove"));
		buttonDelete.setLayoutData(getButtonData(SWT.FILL,SWT.TOP, true,false));
		buttonDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				removeMidiPort(table);
			}
		});
		
		final Button buttonOK = new Button(compositeButtons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(getButtonData(SWT.FILL,SWT.BOTTOM,true, true));
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				final List selectedSoundfonts = getSelectedMidiPorts( table );
				final String selectedDriver = getSelectedDriver( drivers, driverCombo.getSelectionIndex() );
				new Thread( new Runnable() {
					public void run() {
						update(selectedDriver, selectedSoundfonts);
					}
				
				}).start();
				dialog.dispose();
			}
		});
		
		Button buttonCancel = new Button(compositeButtons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(getButtonData(SWT.FILL,SWT.BOTTOM,true, false));
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
			}
		});
		
		this.addMidiPorts(table);
		
		dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(dialog,DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
	}
	
	protected GridLayout getGridLayout(int columns){
		GridLayout layout = new GridLayout(columns, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		return layout;
	}
	
	protected GridData getButtonData(int hAlignment,int vAlignment,boolean grabExcessHSpace,boolean grabExcessVSpace){
		GridData data = new GridData(hAlignment,vAlignment,grabExcessHSpace,grabExcessVSpace);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		return data;
	}
	
	protected void addMidiPorts(Table table){
		Iterator it = getSoundfonts().iterator();
		while(it.hasNext()){
			String path = (String)it.next();
			this.addMidiPort(table, path );
		}
	}
	
	protected void addMidiPort(final Table table) {
		FileDialog chooser = new FileDialog(table.getShell());
		String path = chooser.open();
		if(path != null && path.length() > 0){
			addMidiPort(table, path);
		}
	}
	
	protected void addMidiPort(Table table, String path){
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText( path );
		item.setData( path );
	}
	
	protected void removeMidiPort(Table table){
		int index = table.getSelectionIndex();
		if(index >= 0 && index < table.getItemCount()){
			table.remove( index );
		}
	}
	
	protected List getSelectedMidiPorts(Table table){
		List ports = new ArrayList();
		int count = table.getItemCount();
		for( int i = 0 ; i < count; i ++ ){
			TableItem item = table.getItem( i );
			if( item.getData() instanceof String ){
				ports.add( item.getData() );
			}
		}
		return ports;
	}
	
	protected String getSelectedDriver(List drivers, int index){
		return ( (index > 0 && index <= drivers.size() ) ? (String)drivers.get(index - 1) : null );
	}
	
	protected void update(String driver, List soundfonts){
		this.setDriver( driver );
		this.setSoundfonts( soundfonts );
		this.getConfig().save();
		
		// Reload synth driver
		this.provider.getSynth().loadDriver( driver );
	}
	
}
