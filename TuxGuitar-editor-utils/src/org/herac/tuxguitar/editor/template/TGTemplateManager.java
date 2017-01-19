package org.herac.tuxguitar.editor.template;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.document.TGDocumentManager;
import org.herac.tuxguitar.io.base.TGFileFormatManager;
import org.herac.tuxguitar.io.base.TGSongReaderHandle;
import org.herac.tuxguitar.resource.TGResourceManager;
import org.herac.tuxguitar.song.managers.TGSongManager;
import org.herac.tuxguitar.song.models.TGSong;
import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGTemplateManager {
	
	private static final String TEMPLATE_DEFAULT_RESOURCE = "template-default.tg";
	
	private static final String TEMPLATES_PREFIX = "templates/";
	private static final String TEMPLATES_CONFIG_PATH = (TEMPLATES_PREFIX + "templates.xml");
	
	private TGContext context;
	private List<TGTemplate> templates;
	
	public TGTemplateManager(TGContext context){
		this.context = context;
		this.templates = new ArrayList<TGTemplate>();
		this.loadTemplates();
	}
	
	public int countTemplates(){
		return this.templates.size();
	}
	
	public Iterator<TGTemplate> getTemplates(){
		return this.templates.iterator();
	}
	
	public void loadTemplates(){
		try{
			InputStream templateInputStream = TGResourceManager.getInstance(this.context).getResourceAsStream(TEMPLATES_CONFIG_PATH);
			if( templateInputStream != null ){
				TGTemplateReader tgTemplateReader = new TGTemplateReader();
				tgTemplateReader.loadTemplates(this.templates, templateInputStream);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public TGTemplate getDefaultTemplate(){
		TGTemplate tgTemplate = new TGTemplate();
		tgTemplate.setName(new String());
		tgTemplate.setResource(TEMPLATE_DEFAULT_RESOURCE);
		return tgTemplate;
	}
	
	public TGSong getTemplateAsSong(TGTemplate tgTemplate){
		try{
			if( tgTemplate != null && tgTemplate.getResource() != null ){
				InputStream stream = TGResourceManager.getInstance(this.context).getResourceAsStream(TEMPLATES_PREFIX + tgTemplate.getResource());
				
				if( stream != null ) {
					TGSongManager tgSongManager = TGDocumentManager.getInstance(this.context).getSongManager();
					TGSongReaderHandle tgSongLoaderHandle = new TGSongReaderHandle();
					tgSongLoaderHandle.setFactory(tgSongManager.getFactory());
					tgSongLoaderHandle.setInputStream(stream);
					
					TGFileFormatManager.getInstance(this.context).read(tgSongLoaderHandle);
					
					return tgSongLoaderHandle.getSong();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static TGTemplateManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGTemplateManager.class.getName(), new TGSingletonFactory<TGTemplateManager>() {
			public TGTemplateManager createInstance(TGContext context) {
				return new TGTemplateManager(context);
			}
		});
	}
}
