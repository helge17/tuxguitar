package org.herac.tuxguitar.io.gervill;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.util.DialogUtils;
import org.herac.tuxguitar.app.util.TGFileChooser;
import org.herac.tuxguitar.app.view.dialog.file.TGFileChooserDialog;
import org.herac.tuxguitar.app.view.dialog.file.TGFileChooserHandler;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.util.TGContext;

public class MidiToAudioSettingsDialog {
	
	private TGContext context;
	protected boolean success;
	
	public MidiToAudioSettingsDialog(TGContext context){
		this.context = context;
	}
	
	public boolean open(final MidiToAudioSettings settings) {
		this.success = false;
		
		final List<MidiToAudioFormat> formats = getAvailableFormats();
		final List<TGFileFormat> soundbankFormats = getSupportedSoundbankFormats();
		
		final Shell dialog = DialogUtils.newDialog(TuxGuitar.getInstance().getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText(TuxGuitar.getProperty("gervill.options"));
		
		//------------------AUDIO FORMAT------------------
		Group audioFormatGroup = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		audioFormatGroup.setLayout(new GridLayout(2,false));
		audioFormatGroup.setLayoutData(getGroupData());
		audioFormatGroup.setText(TuxGuitar.getProperty("gervill.options.audio-format"));
		
		Label eLabel = new Label(audioFormatGroup, SWT.NONE);
		eLabel.setText(TuxGuitar.getProperty("gervill.options.file-encoding") + ":");
		eLabel.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,true));
		
		final Combo eCombo = new Combo(audioFormatGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		eCombo.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true));
		
		Label tLabel = new Label(audioFormatGroup, SWT.NONE);
		tLabel.setText(TuxGuitar.getProperty("gervill.options.file-type") + ":");
		tLabel.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,true));
		
		final Combo tCombo = new Combo(audioFormatGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		tCombo.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true));
		
		int eSelectionIndex = 0;
		for( int i = 0 ; i < formats.size() ; i ++ ) {
			MidiToAudioFormat format = (MidiToAudioFormat)formats.get(i);
			eCombo.add( format.getFormat().getEncoding().toString() );
			if( isSameEncoding(settings.getFormat(), format.getFormat() )){
				eSelectionIndex = i;
			}
		}
		
		if( !formats.isEmpty() ){
			eCombo.select( eSelectionIndex );
			updateTypesCombo( settings, formats, eCombo, tCombo );
		}
		
		eCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateTypesCombo( settings, formats, eCombo, tCombo );
			}
		});
		
		//------------------SOUNDBANK-----------------------
		Group soundbankGroup = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		soundbankGroup.setLayout(new GridLayout());
		soundbankGroup.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		soundbankGroup.setText(TuxGuitar.getProperty("gervill.options.soundbank.tip"));
		
		final Button sbDefault = new Button(soundbankGroup,SWT.RADIO);
		sbDefault.setText(TuxGuitar.getProperty("gervill.options.soundbank.default"));
		sbDefault.setSelection( (settings.getSoundbankPath() == null) );
		
		final Button sbCustom = new Button(soundbankGroup,SWT.RADIO);
		sbCustom.setText(TuxGuitar.getProperty("gervill.options.soundbank.custom"));
		sbCustom.setSelection( (settings.getSoundbankPath() != null) );
		
		Composite chooser = new Composite(soundbankGroup,SWT.NONE);
		chooser.setLayout(new GridLayout(2,false));
		
		final Text sbCustomPath = new Text(chooser,SWT.BORDER);
		sbCustomPath.setLayoutData(new GridData(350,SWT.DEFAULT));
		sbCustomPath.setText( (settings.getSoundbankPath() == null ? new String() : settings.getSoundbankPath())  );
		sbCustomPath.setEnabled( (settings.getSoundbankPath() != null) );
		
		final Button sbCustomChooser = new Button(chooser,SWT.PUSH);
		sbCustomChooser.setImage(TuxGuitar.getInstance().getIconManager().getFileOpen());
		sbCustomChooser.setEnabled( (settings.getSoundbankPath() != null) );
		sbCustomChooser.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TGFileChooser.getInstance(MidiToAudioSettingsDialog.this.context).openChooser(new TGFileChooserHandler() {
					public void updateFileName(String fileName) {
						sbCustomPath.setText(fileName);
					}
				}, soundbankFormats, TGFileChooserDialog.STYLE_OPEN);
			}
		});
		
		SelectionListener sbRadioSelectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sbCustomPath.setEnabled(sbCustom.getSelection());
				sbCustomChooser.setEnabled(sbCustom.getSelection());
			}
		};
		sbDefault.addSelectionListener(sbRadioSelectionListener);
		sbCustom.addSelectionListener(sbRadioSelectionListener);
		
		//------------------BUTTONS--------------------------
		Composite buttons = new Composite(dialog, SWT.NONE);
		buttons.setLayout(new GridLayout(2,false));
		buttons.setLayoutData(new GridData(SWT.END,SWT.FILL,true,true));
		
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 80;
		data.minimumHeight = 25;
		
		final Button buttonOK = new Button(buttons, SWT.PUSH);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setLayoutData(data);
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				String soundbankPath = (sbCustom.getSelection() ? sbCustomPath.getText() : null);
				int tIndex = tCombo.getSelectionIndex();
				int eIndex = eCombo.getSelectionIndex();
				if( eIndex >= 0 && eIndex < formats.size() ){
					MidiToAudioFormat format = (MidiToAudioFormat)formats.get( eIndex );
					if( tIndex >= 0 && tIndex < format.getTypes().length ){
						settings.setType( format.getTypes()[tIndex] );
						settings.setFormat( format.getFormat() );
						settings.setSoundbankPath( soundbankPath );
						MidiToAudioSettingsDialog.this.success = true;
					}
				}
				dialog.dispose();
			}
		});
		
		Button buttonCancel = new Button(buttons, SWT.PUSH);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.setLayoutData(data);
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				dialog.dispose();
			}
		});
		
		dialog.setDefaultButton( buttonOK );
		
		DialogUtils.openDialog(dialog, DialogUtils.OPEN_STYLE_CENTER | DialogUtils.OPEN_STYLE_PACK | DialogUtils.OPEN_STYLE_WAIT);
		
		return this.success;
	}
	
	private GridData getGroupData(){
		GridData data = new GridData(SWT.FILL,SWT.FILL,true,true);
		data.minimumWidth = 300;
		return data;
	}
	
	private void updateTypesCombo( MidiToAudioSettings settings, List<MidiToAudioFormat> encodings, Combo eCombo , Combo tCombo ){
		tCombo.removeAll();
		
		int eIndex = eCombo.getSelectionIndex();		
		if( eIndex >= 0 && eIndex < encodings.size() ){
			MidiToAudioFormat encoding = (MidiToAudioFormat)encodings.get( eIndex );
			AudioFileFormat.Type[] types = encoding.getTypes();
			int tSelectionIndex = 0;
			for( int tIndex = 0 ; tIndex < types.length ; tIndex ++ ) {
				tCombo.add( types[ tIndex ] + " (*." + types[ tIndex ].getExtension() + ")");
				if( settings.getType() != null && settings.getType().equals( types[ tIndex] )){
					tSelectionIndex = tIndex;
				}
			}
			tCombo.select( tSelectionIndex );
		}
	}
	
	public List<MidiToAudioFormat> getAvailableFormats(){
		List<MidiToAudioFormat> list = new ArrayList<MidiToAudioFormat>();
		AudioFormat srcFormat = MidiToAudioSettings.DEFAULT_FORMAT;
		AudioFormat.Encoding[] encodings = AudioSystem.getTargetEncodings(srcFormat);
		for( int i = 0 ; i < encodings.length ; i ++ ){
			AudioFormat dstFormat = new AudioFormat(encodings[i],srcFormat.getSampleRate(),srcFormat.getSampleSizeInBits(),srcFormat.getChannels(),srcFormat.getFrameSize(),srcFormat.getFrameRate(),srcFormat.isBigEndian());
			AudioInputStream dstStream = new AudioInputStream(null, dstFormat, 0);
			AudioFileFormat.Type[] dstTypes = AudioSystem.getAudioFileTypes(dstStream);
			if( dstTypes.length > 0 ){
				list.add( new MidiToAudioFormat( dstFormat , dstTypes ));
			}
		}
		return list;
	}
	
	private List<TGFileFormat> getSupportedSoundbankFormats(){
		List<TGFileFormat> list = new ArrayList<TGFileFormat>();
		list.add(new TGFileFormat("SF2 files", new String[]{"sf2"}));
		list.add(new TGFileFormat("DLS files", new String[]{"dls"}));
		return list;
	}
	
	public boolean isSameEncoding( AudioFormat f1, AudioFormat f2 ){
		if( f1 == null || f2 == null || f1.getEncoding() == null || f2.getEncoding() == null ){
			return false;
		}
		return ( f1.getEncoding().toString().equals( f2.getEncoding().toString() ) );
	}
	
	private class MidiToAudioFormat {
		
		private AudioFormat format;
		private AudioFileFormat.Type[] types;
		
		public MidiToAudioFormat(AudioFormat format, AudioFileFormat.Type[] types){
			this.format = format;
			this.types = types;
		}
		
		public AudioFormat getFormat() {
			return this.format;
		}
		
		public AudioFileFormat.Type[] getTypes() {
			return this.types;
		}
	}
}
