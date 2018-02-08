package org.herac.tuxguitar.io.synth;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.main.TGWindow;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UISelectionEvent;
import org.herac.tuxguitar.ui.event.UISelectionListener;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.widget.UIButton;
import org.herac.tuxguitar.ui.widget.UIDropDownSelect;
import org.herac.tuxguitar.ui.widget.UILabel;
import org.herac.tuxguitar.ui.widget.UILegendPanel;
import org.herac.tuxguitar.ui.widget.UIPanel;
import org.herac.tuxguitar.ui.widget.UISelectItem;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGContext;

public class TGSynthSettingsDialog {
	
	private TGContext context;
	
	public TGSynthSettingsDialog(TGContext context){
		this.context = context;
	}
	
	public void open(final TGSynthAudioSettings settings, final Runnable onSuccess) {		
		final List<MidiToAudioFormat> formats = getAvailableFormats();
		
		final UIFactory uiFactory = TGApplication.getInstance(this.context).getFactory();
		final UIWindow uiParent = TGWindow.getInstance(this.context).getWindow();
		final UITableLayout dialogLayout = new UITableLayout();
		
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);
		dialog.setLayout(dialogLayout);
		dialog.setText(TuxGuitar.getProperty("tuxguitar-synth-export.options"));
		
		//------------------AUDIO FORMAT------------------
		UITableLayout audioFormatLayout = new UITableLayout();
		UILegendPanel audioFormatGroup = uiFactory.createLegendPanel(dialog);
		audioFormatGroup.setLayout(audioFormatLayout);
		audioFormatGroup.setText(TuxGuitar.getProperty("tuxguitar-synth-export.options.audio-format"));
		dialogLayout.set(audioFormatGroup, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 400f, null, null);
		
		UILabel eLabel = uiFactory.createLabel(audioFormatGroup);
		eLabel.setText(TuxGuitar.getProperty("tuxguitar-synth-export.options.file-encoding") + ":");
		audioFormatLayout.set(eLabel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UIDropDownSelect<MidiToAudioFormat> eCombo = uiFactory.createDropDownSelect(audioFormatGroup);
		audioFormatLayout.set(eCombo, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		UILabel tLabel = uiFactory.createLabel(audioFormatGroup);
		tLabel.setText(TuxGuitar.getProperty("tuxguitar-synth-export.options.file-type") + ":");
		audioFormatLayout.set(tLabel, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);
		
		final UIDropDownSelect<AudioFileFormat.Type> tCombo = uiFactory.createDropDownSelect(audioFormatGroup);
		audioFormatLayout.set(tCombo, 2, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, true, false);
		
		MidiToAudioFormat selectedFormat = null;
		for(MidiToAudioFormat format : formats) {
			eCombo.addItem(new UISelectItem<MidiToAudioFormat>(format.getFormat().getEncoding().toString(), format));
			if( isSameEncoding(settings.getFormat(), format.getFormat())){
				selectedFormat = format;
			}
		}
		
		if( selectedFormat != null ) {
			eCombo.setSelectedValue(selectedFormat);
			updateTypesCombo(settings, formats, eCombo, tCombo);
		}
		
		eCombo.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				updateTypesCombo(settings, formats, eCombo, tCombo);
			}
		});
		
		//------------------BUTTONS--------------------------
		UITableLayout buttonsLayout = new UITableLayout(0f);
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);
		
		UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				AudioFileFormat.Type type = tCombo.getSelectedValue();
				MidiToAudioFormat format = eCombo.getSelectedValue();
				
				dialog.dispose();
				
				if( format != null && type != null ) {
					settings.setType(type);
					settings.setFormat(format.getFormat());
					onSuccess.run();
				}
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		
		UIButton buttonCancel = uiFactory.createButton(buttons);
		buttonCancel.setText(TuxGuitar.getProperty("cancel"));
		buttonCancel.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonCancel, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);
		buttonsLayout.set(buttonCancel, UITableLayout.MARGIN_RIGHT, 0f);
		
		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	private void updateTypesCombo(TGSynthAudioSettings settings, List<MidiToAudioFormat> encodings, UIDropDownSelect<MidiToAudioFormat> eCombo, UIDropDownSelect<AudioFileFormat.Type> tCombo){
		tCombo.removeItems();
		
		MidiToAudioFormat encoding = eCombo.getSelectedValue();
		if( encoding != null ){
			for(AudioFileFormat.Type type : encoding.getTypes()) {
				tCombo.addItem(new UISelectItem<AudioFileFormat.Type>(type.toString() + " (*." + type.getExtension() + ")", type));
			}
			tCombo.setSelectedValue(settings.getType());
		}
	}
	
	public List<MidiToAudioFormat> getAvailableFormats(){
		List<MidiToAudioFormat> list = new ArrayList<MidiToAudioFormat>();
		AudioFormat srcFormat = TGSynthAudioSettings.DEFAULT_FORMAT;
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
