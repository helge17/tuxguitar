package org.herac.tuxguitar.app.view.dialog.about;

import java.io.IOException;
import java.io.InputStream;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.util.TGContext;

public class TGAboutContentReader {
	
	private static final String PREFIX = "about_";
	private static final String EXTENSION = ".dist";
	
	public static final String DESCRIPTION = "description";
	public static final String AUTHORS = "authors";
	public static final String LICENSE = "license";
	
	private TGContext context;
	
	public TGAboutContentReader(TGContext context){
		this.context = context;
	}
	
	public StringBuffer read(String doc){
		String lang = TuxGuitar.getInstance().getLanguageManager().getLanguage();
		InputStream is = TGResourceManager.getInstance(this.context).getResourceAsStream(PREFIX + doc + "_" + lang + EXTENSION);
		if(is == null){
			is = TGResourceManager.getInstance(this.context).getResourceAsStream(PREFIX + doc + EXTENSION);
		}
		if(is != null){
			return read(is);
		}
		System.out.println(doc + ".txt");
		
		return new StringBuffer();
	}
	
	public StringBuffer read(InputStream is){
		StringBuffer sb = new StringBuffer();
		try {
			int length = 0;
			byte[] buffer = new byte[1024];
			while((length = is.read(buffer)) != -1){
				sb.append(new String(buffer,0,length));
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb;
	}
}
