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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.util.DialogUtils;

public class MidiToAudioSettingsDialog {
	
	protected boolean success;
	
	public MidiToAudioSettingsDialog(){
		super();
	}
	
	public boolean open(final MidiToAudioSettings settings) {
		this.success = false;
		
		final List formats = getAvailableFormats();
		
		final Shell dialog = DialogUtils.newDialog(TuxGuitar.instance().getShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		dialog.setLayout(new GridLayout());
		dialog.setText("Options");
		
		//------------------TRACK SELECTION------------------
		Group trackGroup = new Group(dialog,SWT.SHADOW_ETCHED_IN);
		trackGroup.setLayout(new GridLayout(2,false));
		trackGroup.setLayoutData(getGroupData());
		trackGroup.setText("Audio Format");
		
		//------------------TRANSPOSE----------------------
		Label eLabel = new Label(trackGroup, SWT.NONE);
		eLabel.setText("File Encoding:");
		eLabel.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,true));
		
		final Combo eCombo = new Combo(trackGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		eCombo.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,true));
		
		Label tLabel = new Label(trackGroup, SWT.NONE);
		tLabel.setText("File Type:");
		tLabel.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true,true));
		
		final Combo tCombo = new Combo(trackGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
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
				int tIndex = tCombo.getSelectionIndex();
				int eIndex = eCombo.getSelectionIndex();
				if( eIndex >= 0 && eIndex < formats.size() ){
					MidiToAudioFormat format = (MidiToAudioFormat)formats.get( eIndex );
					if( tIndex >= 0 && tIndex < format.getTypes().length ){
						settings.setType( format.getTypes()[tIndex] );
						settings.setFormat( format.getFormat() );
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
	
	private void updateTypesCombo( MidiToAudioSettings settings, List encodings, Combo eCombo , Combo tCombo ){
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
	
	public List getAvailableFormats(){
		List list = new ArrayList();
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
