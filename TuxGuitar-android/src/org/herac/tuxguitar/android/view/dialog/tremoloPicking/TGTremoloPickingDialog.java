package org.herac.tuxguitar.android.view.dialog.tremoloPicking;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.effect.TGChangeTremoloPickingAction;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGBeat;
import org.herac.tuxguitar.song.models.TGDuration;
import org.herac.tuxguitar.song.models.TGMeasure;
import org.herac.tuxguitar.song.models.TGNote;
import org.herac.tuxguitar.song.models.TGString;
import org.herac.tuxguitar.song.models.effects.TGEffectTremoloPicking;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class TGTremoloPickingDialog extends TGDialog {

	public TGTremoloPickingDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		final TGSongManager songManager = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
		final TGMeasure measure = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
		final TGBeat beat = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT);
		final TGNote note = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE);
		final TGString string = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING);
		
		final View view = getActivity().getLayoutInflater().inflate(R.layout.view_tremolo_picking_dialog, null);
		
		this.fillDurations(view, note);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.tremolo_picking_dlg_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				processAction(measure, beat, string, createTremoloPicking(view, songManager));
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
	
	public void fillDurations(View view, TGNote note) {
		int duration = TGDuration.EIGHTH;
		if( note != null && note.getEffect().isTremoloPicking() ){
			duration = note.getEffect().getTremoloPicking().getDuration().getValue();
		}
		
		this.fillDuration(view, R.id.tremolo_picking_dlg_duration_8, TGDuration.EIGHTH, duration);
		this.fillDuration(view, R.id.tremolo_picking_dlg_duration_16, TGDuration.SIXTEENTH, duration);
		this.fillDuration(view, R.id.tremolo_picking_dlg_duration_32, TGDuration.THIRTY_SECOND, duration);
	}

	public void fillDuration(View view, int id, int value, int selection) {
		RadioButton radioButton = (RadioButton)view.findViewById(id);
		radioButton.setTag(Integer.valueOf(value));
		radioButton.setChecked(value == selection);
	}
	
	public int findSelectedDuration(View view) {
		RadioGroup optionsGroup = (RadioGroup) view.findViewById(R.id.tremolo_picking_dlg_duration_group);
		
		int radioButtonId = optionsGroup.getCheckedRadioButtonId();
		if( radioButtonId != -1 ) {
			RadioButton radioButton = (RadioButton) optionsGroup.findViewById(radioButtonId);
			if( radioButton != null ) {
				return ((Integer)radioButton.getTag()).intValue();
			}
		}
		return TGDuration.EIGHTH;
	}
	
	public TGEffectTremoloPicking createTremoloPicking(View view, TGSongManager songManager){
		TGEffectTremoloPicking tgEffectTremoloPicking = songManager.getFactory().newEffectTremoloPicking();
		tgEffectTremoloPicking.getDuration().setValue(findSelectedDuration(view));
		return tgEffectTremoloPicking;
	}
	
	public void processAction(TGMeasure measure, TGBeat beat, TGString string, TGEffectTremoloPicking effect) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeTremoloPickingAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);
		tgActionProcessor.setAttribute(TGChangeTremoloPickingAction.ATTRIBUTE_EFFECT, effect);
		tgActionProcessor.process();
	}
}
