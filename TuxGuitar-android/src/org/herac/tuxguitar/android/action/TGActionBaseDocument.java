package org.herac.tuxguitar.android.action;

import org.herac.tuxguitar.document.TGDocumentContextAttributes;
import org.herac.tuxguitar.util.TGContext;

public abstract class TGActionBaseDocument extends TGActionBase {

	public static final String ATTRIBUTE_SONG_MANAGER = TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER;
	
	public static final String ATTRIBUTE_SONG = TGDocumentContextAttributes.ATTRIBUTE_SONG;
	public static final String ATTRIBUTE_TRACK = TGDocumentContextAttributes.ATTRIBUTE_TRACK;
	public static final String ATTRIBUTE_HEADER = TGDocumentContextAttributes.ATTRIBUTE_HEADER;
	public static final String ATTRIBUTE_MEASURE = TGDocumentContextAttributes.ATTRIBUTE_MEASURE;
	public static final String ATTRIBUTE_BEAT = TGDocumentContextAttributes.ATTRIBUTE_BEAT;
	public static final String ATTRIBUTE_VOICE = TGDocumentContextAttributes.ATTRIBUTE_VOICE;
	public static final String ATTRIBUTE_NOTE = TGDocumentContextAttributes.ATTRIBUTE_NOTE;
	public static final String ATTRIBUTE_STRING = TGDocumentContextAttributes.ATTRIBUTE_STRING;
	public static final String ATTRIBUTE_DURATION = TGDocumentContextAttributes.ATTRIBUTE_DURATION;
	public static final String ATTRIBUTE_CHANNEL = TGDocumentContextAttributes.ATTRIBUTE_CHANNEL;
	public static final String ATTRIBUTE_VELOCITY =TGDocumentContextAttributes.ATTRIBUTE_VELOCITY;
	public static final String ATTRIBUTE_POSITION = TGDocumentContextAttributes.ATTRIBUTE_POSITION;
	public static final String ATTRIBUTE_FRET = TGDocumentContextAttributes.ATTRIBUTE_FRET;
	
	public TGActionBaseDocument(TGContext context, String name) {
		super(context, name);
	}
}
