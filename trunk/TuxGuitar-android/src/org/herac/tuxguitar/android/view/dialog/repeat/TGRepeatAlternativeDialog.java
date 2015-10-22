package org.herac.tuxguitar.android.view.dialog.repeat;

import java.util.Iterator;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGRepeatAlternativeAction;
import org.herac.tuxguitar.song.models.TGMeasureHeader;
import org.herac.tuxguitar.song.models.TGSong;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;

public class TGRepeatAlternativeDialog extends TGDialog {

	public TGRepeatAlternativeDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		View view = getActivity().getLayoutInflater().inflate(R.layout.view_repeat_alternative, null);
		
		final TGSong song = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final TGMeasureHeader header = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER);
		final int existentEndings = getExistentEndings(song, header);
		final int selectedEndings = (header.getRepeatAlternative() > 0) ? header.getRepeatAlternative() : getDefaultEndings(existentEndings);
		
		final CheckBox[] selections = new CheckBox[] {
			(CheckBox) view.findViewById(R.id.repeat_alternative_dlg_alt_1),
			(CheckBox) view.findViewById(R.id.repeat_alternative_dlg_alt_2),
			(CheckBox) view.findViewById(R.id.repeat_alternative_dlg_alt_3),
			(CheckBox) view.findViewById(R.id.repeat_alternative_dlg_alt_4),
			(CheckBox) view.findViewById(R.id.repeat_alternative_dlg_alt_5),
			(CheckBox) view.findViewById(R.id.repeat_alternative_dlg_alt_6),
			(CheckBox) view.findViewById(R.id.repeat_alternative_dlg_alt_7),
			(CheckBox) view.findViewById(R.id.repeat_alternative_dlg_alt_8),
		};
		
		this.updateSelections(selections, existentEndings, selectedEndings);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.repeat_alternative_dlg_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				changeRepeatAlternative(song, header, parseRepeatAlternative(selections));
				dialog.dismiss();
			}
		});
		builder.setNeutralButton(R.string.global_button_clean, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				changeRepeatAlternative(song, header, 0);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(R.string.global_button_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		
		return builder.create();
	}
	
	protected int getExistentEndings(TGSong song, TGMeasureHeader measureHeader){
		int existentEndings = 0;
		Iterator<TGMeasureHeader> it = song.getMeasureHeaders();
		while(it.hasNext()){
			TGMeasureHeader header = it.next();
			if(header.getNumber() == measureHeader.getNumber()){
				break;
			}
			if(header.isRepeatOpen()){
				existentEndings = 0;
			}
			existentEndings |= header.getRepeatAlternative();
		}
		return existentEndings;
	}
	
	protected int getDefaultEndings(int existentEndings){
		for(int i = 0; i < 8; i ++){
			if((existentEndings & (1 << i)) == 0){
				return (1 << i);
			}
		}
		return -1;
	}
	
	public void updateSelections(CheckBox[] selections, int existentEndings, int selectedEndings) {
		for(int i = 0; i < selections.length; i ++){
			boolean enabled = ((existentEndings & (1 << i)) == 0);
			selections[i].setEnabled(enabled);
			selections[i].setChecked(enabled && ((selectedEndings & (1 << i)) != 0));
		}
	}
	
	public Integer parseRepeatAlternative(CheckBox[] selections) {
		int repeatAlternative = 0;
		for(int i = 0; i < selections.length; i ++){
			repeatAlternative |= ( (selections[i].isChecked()) ? (1 << i) : 0 );
		}
		return Integer.valueOf(repeatAlternative);
	}
	
	public void changeRepeatAlternative(TGSong song, TGMeasureHeader header, Integer repeatAlternative) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGRepeatAlternativeAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, header);
		tgActionProcessor.setAttribute(TGRepeatAlternativeAction.ATTRIBUTE_REPEAT_ALTERNATIVE, repeatAlternative);
		tgActionProcessor.processOnNewThread();
	}
}
