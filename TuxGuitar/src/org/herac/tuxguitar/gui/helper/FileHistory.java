/*
 * Created on 08-dic-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.gui.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.herac.tuxguitar.gui.TuxGuitar;
import org.herac.tuxguitar.gui.system.config.TGConfigKeys;
import org.herac.tuxguitar.gui.util.TGFileUtils;

/**
 * @author julian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FileHistory {
	
	private static final int URL_LIMIT = TuxGuitar.instance().getConfig().getIntConfigValue(TGConfigKeys.MAX_HISTORY_FILES);
	
	private boolean changed;
	private boolean newFile;
	private boolean localFile;
	private boolean unsavedFile;
	private List urls;
	private String chooserPath;
	
	public FileHistory(){
		this.urls = new ArrayList();
		this.loadHistory();
		this.reset(null);
	}
	
	public void reset(URL url) {
		this.unsavedFile = false;
		this.newFile = (url == null);
		this.localFile = (url != null && isLocalFile(url));
		this.addURL(url);
	}
	
	public boolean isNewFile(){
		return this.newFile;
	}
	
	public boolean isLocalFile(){
		return this.localFile;
	}
	
	public boolean isUnsavedFile() {
		return this.unsavedFile;
	}
	
	public void setUnsavedFile() {
		this.unsavedFile = true;
	}
	
	public void setChooserPath(String chooserPath){
		this.chooserPath = chooserPath;
	}
	
	public void setChooserPath(URL url){
		String path = getFilePath(url);
		if( path != null ){
			this.setChooserPath( path );
		}
	}
	
	public String getCurrentFileName(String defaultName) {
		if(!this.isNewFile()){
			URL url = getCurrentURL();
			if(url != null){
				return decode(new File(url.getFile()).getName());
			}
		}
		return defaultName;
	}
	
	public String getCurrentFilePath() {
		if(!this.isNewFile()){
			URL url = getCurrentURL();
			if(url != null){
				String file = getFilePath(url);
				if(file != null){
					return decode(file);
				}
			}
		}
		return this.chooserPath;
	}
	
	public String getSavePath() {
		String current = getCurrentFilePath();
		return (current != null ? current : this.chooserPath);
	}
	
	public String getOpenPath() {
		return this.chooserPath;
	}
	
	protected String getFilePath(URL url){
		if(isLocalFile(url)){
			return new File(url.getFile()).getParent();
		}
		return null;
	}
	
	protected String decode(String url){
		try {
			return URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	protected boolean isLocalFile(URL url){
		try {
			if(url.getProtocol().equals( new File(url.getFile()).toURI().toURL().getProtocol() ) ){
				return true;
			}
		}catch(Throwable throwable){
			throwable.printStackTrace();
		}
		return false;
	}
	
	protected URL getCurrentURL(){
		if(!this.urls.isEmpty()){
			return (URL)this.urls.get(0);
		}
		return null;
	}
	
	public void addURL(URL url){
		if(url != null){
			removeURL(url);
			this.urls.add(0,url);
			checkLimit();
			setChanged(true);
		}
		saveHistory();
	}
	
	public List getURLs(){
		return this.urls;
	}
	
	private void checkLimit(){
		while(this.urls.size() > URL_LIMIT){
			this.urls.remove(this.urls.size() - 1);
		}
	}
	
	private void removeURL(URL url){
		for(int i = 0; i < this.urls.size(); i++){
			URL old = (URL)this.urls.get(i);
			if(old.toString().equals(url.toString())){
				this.urls.remove(i);
				break;
			}
		}
	}
	
	public boolean isChanged() {
		return this.changed;
	}
	
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	public void loadHistory() {
		try {
			this.urls.clear();
			if(new File(getHistoryFileName()).exists()){
				InputStream inputStream = new FileInputStream(getHistoryFileName());
				Properties properties = new Properties();
				properties.load(inputStream);
				
				this.chooserPath = (String)properties.get("history.path");
				
				int count = Integer.parseInt((String)properties.get("history.count"));
				for(int i = 0; i < count;i ++){
					String url = (String)properties.get("history." + i);
					if(URL_LIMIT > i && url != null && url.length() > 0){
						this.urls.add(new URL(url));
					}
				}
				setChanged(true);
			}else{
				this.saveHistory();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveHistory(){
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
			properties.store(new FileOutputStream(getHistoryFileName()),"History Files");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private String getHistoryFileName(){
		return TGFileUtils.PATH_USER_CONFIG + File.separator + "history.properties";
	}
}
