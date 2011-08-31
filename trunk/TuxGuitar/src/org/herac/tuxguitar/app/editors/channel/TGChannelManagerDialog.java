package org.herac.tuxguitar.app.editors.channel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.editors.TGUpdateListener;
import org.herac.tuxguitar.app.system.icons.IconLoader;
import org.herac.tuxguitar.app.system.language.LanguageLoader;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.song.models.TGChannel;

public class TGChannelManagerDialog implements TGUpdateListener,IconLoader,LanguageLoader{
	
	protected Shell dialog;
	
	private TGChannelHandle channelHandle;
	private TGChannelList channelList;
	
	private Button addChannelButton;
	
	private Scale volumeScale;
	private Label volumeValueLabel;
	private Label volumeValueTitleLabel;
	private String volumeTip;
	private int volumeValue;
	
	public TGChannelManagerDialog(){
		this.channelHandle = new TGChannelHandle();
	}
	
	public void show(){
		show(TuxGuitar.instance().getShell());
	}
	
	public void show(Shell parent){
		this.dialog = DialogUtils.newDialog(parent, SWT.DIALOG_TRIM | SWT.RESIZE);
		this.dialog.setLayout(createGridLayout(1,false, true, true));
		
		this.createWindow(this.dialog, new GridData(SWT.FILL,SWT.FILL,true,true));
		
		this.addListeners();
		this.dialog.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				removeListeners();
				TuxGuitar.instance().updateCache(true);
			}
		});
		
		DialogUtils.openDialog(this.dialog, DialogUtils.OPEN_STYLE_CENTER );
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
		TuxGuitar.instance().getIconManager().addLoader(this);
		TuxGuitar.instance().getLanguageManager().addLoader(this);
		TuxGuitar.instance().getEditorManager().addUpdateListener(this);
	}
	
	public void removeListeners(){
		TuxGuitar.instance().getIconManager().removeLoader(this);
		TuxGuitar.instance().getLanguageManager().removeLoader(this);
		TuxGuitar.instance().getEditorManager().removeUpdateListener(this);
	}
	
	private void createWindow(Composite parent, Object layoutData){
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(createGridLayout(2,false,true,true));
		composite.setLayoutData(layoutData);
		
		createChannelList(composite);
		createRightComposite(composite);
		
		updateItems();
		loadProperties();
	}
	
	private void createRightComposite(Composite composite){
		Composite rightComposite = new Composite(composite, SWT.NONE);
		rightComposite.setLayout(createGridLayout(1,false, true, false));
		rightComposite.setLayoutData(new GridData(SWT.RIGHT,SWT.FILL,false,true));
		
		Composite toolbarComposite = new Composite(rightComposite, SWT.BORDER);
		toolbarComposite.setLayout(createGridLayout(1,false, true, true));
		toolbarComposite.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		
		this.addChannelButton = new Button(toolbarComposite, SWT.PUSH);
		this.addChannelButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		this.addChannelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getHandle().addChannel();
			}
		});
		
		
		Composite volumeComposite = new Composite(rightComposite, SWT.BORDER);
		volumeComposite.setLayout(createGridLayout(1,false, true, true));
		volumeComposite.setLayoutData(new GridData(SWT.CENTER,SWT.FILL,true,true));
		
		this.volumeScale = new Scale(volumeComposite, SWT.VERTICAL);
		this.volumeScale.setMaximum(10);
		this.volumeScale.setMinimum(0);
		this.volumeScale.setIncrement(1);
		this.volumeScale.setPageIncrement(1);
		this.volumeScale.setLayoutData(new GridData(SWT.CENTER,SWT.FILL,true,true));
		
		Label separator = new Label(volumeComposite, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));
		
		Composite volumeValueComposite = new Composite(volumeComposite, SWT.NONE);
		volumeValueComposite.setLayout(createGridLayout(2,false, true, true));
		
		this.volumeValueTitleLabel = new Label(volumeValueComposite, SWT.NONE);
		
		this.volumeValueLabel = new Label(volumeValueComposite, SWT.CENTER);
		this.volumeValueLabel.setLayoutData(createGridData(SWT.CENTER,SWT.NONE,true,false,1,1,40,0));
		
		this.volumeScale.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				changeVolume();
			}
		});
	}
	
	private void createChannelList(Composite composite){
		this.channelList = new TGChannelList(this);
		this.channelList.show(composite);
	}
	
	public GridLayout createGridLayout(int numColumns, boolean makeColumnsEqualWidth, boolean addSpacings, boolean addMargins) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = numColumns;
		gridLayout.makeColumnsEqualWidth = makeColumnsEqualWidth;
		gridLayout.horizontalSpacing = (addSpacings ? gridLayout.horizontalSpacing : 0);
		gridLayout.verticalSpacing = (addSpacings ? gridLayout.verticalSpacing : 0);
		gridLayout.marginWidth = (addMargins ? gridLayout.marginWidth : 0);
		gridLayout.marginHeight = (addMargins ? gridLayout.marginHeight : 0);
		gridLayout.marginLeft = (addMargins ? gridLayout.marginLeft : 0);
		gridLayout.marginTop = (addMargins ? gridLayout.marginTop : 0);
		gridLayout.marginRight = (addMargins ? gridLayout.marginRight : 0);
		gridLayout.marginBottom = (addMargins ? gridLayout.marginBottom : 0);
		return gridLayout;
	}
	
	private GridData createGridData(int hAlign, int vAlign, boolean grabExcessHSpace, boolean grabExcessVSpace, int hSpan, int vSpan,int mWidth, int mHeight){
		GridData gridData = new GridData();
		gridData.horizontalAlignment = hAlign;
		gridData.verticalAlignment = vAlign;
		gridData.grabExcessHorizontalSpace = grabExcessHSpace;
		gridData.grabExcessVerticalSpace = grabExcessVSpace;
		gridData.horizontalSpan = hSpan;
		gridData.verticalSpan = vSpan;
		gridData.minimumWidth = mWidth;
		gridData.minimumHeight = mHeight;
		return gridData;
	}
	
	protected void changeVolume(){
		int volume = (short)(this.volumeScale.getMaximum() - this.volumeScale.getSelection());
		if(volume != TuxGuitar.instance().getPlayer().getVolume()){
			TuxGuitar.instance().getPlayer().setVolume(volume);
			this.volumeScale.setToolTipText(this.volumeTip + ": " + TuxGuitar.instance().getPlayer().getVolume());
			this.volumeValueLabel.setText(Integer.toString(this.volumeScale.getMaximum() - this.volumeScale.getSelection()));
			this.volumeValue = volume;
		}
	}
	
	private void updateItems(){
		if(!isDisposed()){
			this.channelList.updateItems();
			
			int volume = TuxGuitar.instance().getPlayer().getVolume();
			if(this.volumeValue != volume){
				this.volumeScale.setSelection(this.volumeScale.getMaximum() - TuxGuitar.instance().getPlayer().getVolume());
				this.volumeValueLabel.setText(Integer.toString(this.volumeScale.getMaximum() - this.volumeScale.getSelection()));
				this.volumeValue = volume;
			}
		}
	}

	public void loadProperties() {
		if(!isDisposed()){
			this.addChannelButton.setText(TuxGuitar.getProperty("add"));
			
			this.volumeValueTitleLabel.setText(TuxGuitar.getProperty("instruments.volume") + ":");
			this.volumeTip = TuxGuitar.getProperty("instruments.volume");
			this.volumeScale.setToolTipText(this.volumeTip + ": " + TuxGuitar.instance().getPlayer().getVolume());
			this.dialog.setText(TuxGuitar.getProperty("instruments.dialog-title"));
			
			this.channelList.loadProperties();
		}
	}

	public void loadIcons() {
		// TODO Auto-generated method stub
	}

	public void doUpdate(int type) {
		if( type == TGUpdateListener.SELECTION ){
			this.updateItems();
		}
	}
	
	public void onUpdateChannel(TGChannel channel){
		if (TuxGuitar.instance().getPlayer().isRunning()) {
			TuxGuitar.instance().getPlayer().updateControllers();
		}
	}
	
	public TGChannelHandle getHandle(){
		return this.channelHandle;
	}
}
