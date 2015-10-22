package org.herac.tuxguitar.android.view.dialog.info;

import org.herac.tuxguitar.android.R;
import org.herac.tuxguitar.android.view.dialog.TGDialog;
import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.editor.action.composition.TGChangeInfoAction;
import org.herac.tuxguitar.song.models.TGSong;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;

public class TGSongInfoDialog extends TGDialog {

	public TGSongInfoDialog() {
		super();
	}
	
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog() {
		final TGSong song = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		final View view = getActivity().getLayoutInflater().inflate(R.layout.view_song_info, null);
		
		this.fillSongInfo(view, song);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.song_info_dlg_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.global_button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				updateSongInfo(view, song);
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

	public void setTextFieldValue(View view, int textFieldId, String value) {
		((EditText) view.findViewById(textFieldId)).getText().append(value);
	}
	
	public String getTextFieldValue(View view, int textFieldId) {
		return ((EditText) view.findViewById(textFieldId)).getText().toString();
	}
	
	public void fillSongInfo(View view, TGSong song) {
		setTextFieldValue(view, R.id.song_info_dlg_name_value, song.getName());
		setTextFieldValue(view, R.id.song_info_dlg_artist_value, song.getArtist());
		setTextFieldValue(view, R.id.song_info_dlg_album_value, song.getAlbum());
		setTextFieldValue(view, R.id.song_info_dlg_author_value, song.getAuthor());
		setTextFieldValue(view, R.id.song_info_dlg_date_value, song.getDate());
		setTextFieldValue(view, R.id.song_info_dlg_copyright_value, song.getCopyright());
		setTextFieldValue(view, R.id.song_info_dlg_writer_value, song.getWriter());
		setTextFieldValue(view, R.id.song_info_dlg_transcriber_value, song.getTranscriber());
		setTextFieldValue(view, R.id.song_info_dlg_comments_value, song.getComments());
	}
	
	public void updateSongInfo(View view, TGSong song) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(findContext(), TGChangeInfoAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, song);
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_NAME, getTextFieldValue(view, R.id.song_info_dlg_name_value));
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_ARTIST, getTextFieldValue(view, R.id.song_info_dlg_artist_value));
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_ALBUM, getTextFieldValue(view, R.id.song_info_dlg_album_value));
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_AUTHOR, getTextFieldValue(view, R.id.song_info_dlg_author_value));
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_DATE, getTextFieldValue(view, R.id.song_info_dlg_date_value));
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_COPYRIGHT, getTextFieldValue(view, R.id.song_info_dlg_copyright_value));
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_WRITER, getTextFieldValue(view, R.id.song_info_dlg_writer_value));
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_TRANSCRIBER, getTextFieldValue(view, R.id.song_info_dlg_transcriber_value));
		tgActionProcessor.setAttribute(TGChangeInfoAction.ATTRIBUTE_COMMENTS, getTextFieldValue(view, R.id.song_info_dlg_comments_value));
		tgActionProcessor.processOnNewThread();
	}
}
