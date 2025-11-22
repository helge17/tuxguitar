package app.tuxguitar.app.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.config.TGConfigKeys;
import app.tuxguitar.app.util.TGFileUtils;
import app.tuxguitar.io.base.TGFileFormatManager;
import app.tuxguitar.io.base.TGFileFormatUtils;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGFileHistory {

	private static final int URL_LIMIT = TuxGuitar.getInstance().getConfig().getIntegerValue(TGConfigKeys.MAX_HISTORY_FILES);

	private TGContext context;
	private List<URL> urls;
	private String chooserPath;
	private boolean changed;

	private TGFileHistory(TGContext context) {
		this.context = context;
		this.urls = new ArrayList<URL>();
		this.loadHistory();
		this.reset(null);
	}

	public void reset(URL url) {
		this.addURL(url);
	}

	public String getFilePath(URL url) {
		if( TGFileUtils.isLocalFile(url) ){
			return new File(TGFileUtils.getDecodedPath(url.getFile())).getParent();
		}
		return null;
	}

	public boolean isReadable(URL url) {
		if( url != null ) {
			String formatCode = TGFileFormatUtils.getFileFormatCode(url.getFile());
			if( formatCode != null ) {
				return (TGFileFormatManager.getInstance(this.context).findReaderFileFormatByCode(formatCode) != null);
			}
		}
		return false;
	}

	public void addURL(URL url) {
		if( this.isReadable(url) ) {
			this.removeURL(url);
			this.urls.add(0, url);
			this.checkLimit();
			this.setChanged(true);
			this.saveHistory();
		}
	}

	public List<URL> getURLs() {
		return this.urls;
	}

	private void checkLimit() {
		while(this.urls.size() > URL_LIMIT){
			this.urls.remove(this.urls.size() - 1);
		}
	}

	private void removeURL(URL url) {
		for(int i = 0; i < this.urls.size(); i++){
			URL old = this.urls.get(i);
			if(old.toString().equals(url.toString())){
				this.urls.remove(i);
				break;
			}
		}
	}

	public void loadHistory() {
		try {
			this.urls.clear();
			if( new File(getHistoryFileName()).exists() ){
				InputStream inputStream = new FileInputStream(getHistoryFileName());
				Properties properties = new Properties();
				properties.load(new InputStreamReader(inputStream,StandardCharsets.UTF_8));

				this.chooserPath = properties.getProperty("history.path");

				int count = Integer.valueOf(properties.getProperty("history.count", "0"));
				for(int i = 0; i < count;i ++){
					String url = properties.getProperty("history." + i);
					if( URL_LIMIT > i && url != null && url.length() > 0 ){
						this.urls.add(new URL(url));
					}
				}
				this.setChanged(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveHistory() {
		try {
			Properties properties = new Properties();

			int count = this.urls.size();
			for(int i = 0;i < count;i ++){
				properties.put("history." + i,this.urls.get(i).toString());
			}
			properties.put("history.count",Integer.toString(count));
			if(this.chooserPath != null){
				properties.put("history.path",this.chooserPath);
			}
			properties.store(new OutputStreamWriter(new FileOutputStream(getHistoryFileName()),StandardCharsets.UTF_8),"History Files");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private String getHistoryFileName() {
		return TGFileUtils.PATH_USER_CONFIG + File.separator + "history.properties";
	}

	public void setChooserPath(URL url) {
		String path = getFilePath(url);
		if( path != null ){
			this.setChooserPath( path );
		}
	}

	public String getChooserPath() {
		return this.chooserPath;
	}

	public void setChooserPath(String chooserPath) {
		this.chooserPath = chooserPath;
	}

	public boolean isChanged() {
		return this.changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public static TGFileHistory getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGFileHistory.class.getName(), new TGSingletonFactory<TGFileHistory>() {
			public TGFileHistory createInstance(TGContext context) {
				return new TGFileHistory(context);
			}
		});
	}
}
