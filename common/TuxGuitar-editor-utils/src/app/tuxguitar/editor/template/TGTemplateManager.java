package app.tuxguitar.editor.template;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.io.base.TGFileFormatManager;
import app.tuxguitar.io.base.TGSongReaderHandle;
import app.tuxguitar.resource.TGResourceManager;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGMessagesManager;
import app.tuxguitar.util.TGUserFileUtils;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGTemplateManager {

	private static final String TEMPLATE_DEFAULT_RESOURCE = "template-default";
	private static final String TEMPLATE_EXTENSION = ".tg";

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

	public TGSong getDefaultTemplateAsSong() {
		TGSong song = null;
		TGTemplate tgTemplate = new TGTemplate();
		tgTemplate.setName(new String());
		if (TGUserFileUtils.isUserTemplateReadable()) {
			tgTemplate.setResource(TGUserFileUtils.PATH_USER_TEMPLATE);
			tgTemplate.setUserTemplate();
			song = getTemplateAsSong(tgTemplate);
			if (song != null) {
				return song;
			} else {
				// user template readable, but cannot be used to create a new song. Corrupted?
				TGUserFileUtils.deleteUserTemplate();
			}
		}
		// no user-defined template, try to load language-specific template
		// default
		String templateFileName = TEMPLATE_DEFAULT_RESOURCE + TEMPLATE_EXTENSION;
		tgTemplate.setResource(templateFileName);
		song = getTemplateAsSong(tgTemplate);

		// locale-specific if it exists
		Locale locale = TGMessagesManager.getInstance().getLocale();
		String baseName = TEMPLATE_DEFAULT_RESOURCE + "_" + locale.getLanguage();
		tgTemplate.setResource(baseName + TEMPLATE_EXTENSION);
		TGSong localeSong = getTemplateAsSong(tgTemplate);
		song = (localeSong==null ? song : localeSong);

		if (!"".equals(locale.getCountry())) {
			baseName = baseName + "_" + locale.getCountry();
			tgTemplate.setResource(baseName + TEMPLATE_EXTENSION);
			localeSong = getTemplateAsSong(tgTemplate);
			song = (localeSong==null ? song : localeSong);
			if (!"".equals(locale.getVariant())) {
				baseName = baseName + "_" + locale.getVariant();
				tgTemplate.setResource(baseName + TEMPLATE_EXTENSION);
				localeSong = getTemplateAsSong(tgTemplate);
				song = (localeSong==null ? song : localeSong);
			}
		}
		return song;
	}

	public TGSong getTemplateAsSong(TGTemplate tgTemplate){
		try{
			if( tgTemplate != null && tgTemplate.getResource() != null ){
				InputStream stream;
				if (tgTemplate.isUserTemplate()) {
					stream = new FileInputStream(tgTemplate.getResource());
				} else {
					stream = TGResourceManager.getInstance(this.context).getResourceAsStream(TEMPLATES_PREFIX + tgTemplate.getResource());
				}
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
