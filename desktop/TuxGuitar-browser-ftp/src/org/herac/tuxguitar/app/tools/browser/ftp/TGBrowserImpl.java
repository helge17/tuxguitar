package org.herac.tuxguitar.app.tools.browser.ftp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.herac.tuxguitar.app.tools.browser.TGBrowserException;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowser;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserCallBack;
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserElement;

public class TGBrowserImpl implements TGBrowser {
	
	private TGBrowserSettingsModel data;
	private String root; 
	private String path; 
	private TGBrowserFTPClient client;
	
	public TGBrowserImpl(TGBrowserSettingsModel data){
		this.data = data;
	}
	
	private String getRoot(){
		if( this.root == null ){
			this.root = "/";
			if( this.data.getPath() != null && this.data.getPath().length() > 0 ){
				this.root = this.data.getPath();
				if( this.root.indexOf("/") != 0 ){
					this.root = ("/" + this.root);
				}
			}
		}
		return this.root;
	}
	
	public void open(TGBrowserCallBack<Object> cb) {
		try {
			checkForProxy();
			this.client = new TGBrowserFTPClient();
			this.client.open(this.data.getHost(), TGBrowserFTPClient.DEFAULT_PORT);
			this.client.login(this.data.getUsername(),this.data.getPassword());
			this.client.cd(getRoot());
			this.path = this.client.pwd();
			
			cb.onSuccess(null);
		} catch(Throwable throwable) {
			cb.handleError(throwable);
		}
	}

	
	public void close(TGBrowserCallBack<Object> cb) {
		try {
			this.closeProxy();
			this.client.close();
			
			cb.onSuccess(null);
		} catch(Throwable throwable) {
			cb.handleError(throwable);
		}
	}
	
	public void cdElement(TGBrowserCallBack<Object> cb, TGBrowserElement element) {
		try {
			boolean isCDSuccess = this.client.cd(element.getName());
			if(!isCDSuccess) {
				throw new TGBrowserException("could not cd to "+element.getName());
			}
			this.path = this.client.pwd();
			
			cb.onSuccess(null);
		} catch (Throwable throwable) {
			cb.handleError(throwable);
		}
	}
	
	public void cdRoot(TGBrowserCallBack<Object> cb) {
		try {
			this.client.cd(getRoot());
			this.path = this.client.pwd();
			
			cb.onSuccess(null);
		} catch(Throwable throwable) {
			cb.handleError(throwable);
		}
	}
	
	public void cdUp(TGBrowserCallBack<Object> cb) {
		try {
			this.client.cdUp();
			this.path = this.client.pwd();
			
			cb.onSuccess(null);
		} catch (Throwable throwable) {
			cb.handleError(throwable);
		}
	}
	
	public void listElements(TGBrowserCallBack<List<TGBrowserElement>> cb) {
		try {
			List<TGBrowserElement> elements = new ArrayList<TGBrowserElement>();
			
			this.client.binary();
			String[] names = this.client.listNames();
			String[] infos = this.client.listDetails();
			
			if(names.length > 0 && infos.length > 0){
				for(int i = 0;i < names.length;i++){
					String name = names[i].trim();
					
					if(name.indexOf(this.path) == 0 && name.length() > this.path.length()){
						name = name.substring(this.path.length());
					}
					while(name.indexOf("/") == 0){
						name = name.substring(1);
					}
					if( name.length() > 0 ){
						for(int j = 0;j < infos.length;j++){
							String info = infos[j].trim();
							if(info.indexOf(name) > 0){
								elements.add(new TGBrowserElementImpl(name, info, this.path));
								break;
							}
						}
					}
				}
			}
			if( !elements.isEmpty() ){
				Collections.sort(elements, new TGBrowserElementComparator());
			}
			
			cb.onSuccess(elements);
		} catch (Throwable throwable) {
			cb.handleError(throwable);
		}
	}
	
	public void getInputStream(TGBrowserCallBack<InputStream> cb, TGBrowserElement element) {
		try {
			this.client.cd(((TGBrowserElementImpl) element).getPath());
			this.client.binary();
			
			byte[] bytes = this.client.get(element.getName());
			
			cb.onSuccess(new ByteArrayInputStream(bytes));
		} catch (Throwable e) {
			cb.handleError(e);
		}
	}
	
	private void checkForProxy() {
		if (this.data.getProxyHost() != null && this.data.getProxyPort() > 0) {
			System.setProperty("socksProxyHost", this.data.getProxyHost());
			System.setProperty("socksProxyPort", String.valueOf(this.data.getProxyPort()));
			if (this.data.getProxyUser() != null && this.data.getProxyUser().trim().length() > 0) {
				System.setProperty("java.net.socks.username", this.data.getProxyUser());
				System.setProperty("java.net.socks.password", this.data.getProxyPwd());
				Authenticator.setDefault(new ProxyAuthenticator(this.data.getProxyUser(), this.data.getProxyPwd()));
			}
		} else {
			closeProxy();
		}
	}

	private void closeProxy() {
		Properties sysProperties = System.getProperties();
		sysProperties.remove("socksProxyHost");
		sysProperties.remove("socksProxyPort");
		sysProperties.remove("java.net.socks.username");
		sysProperties.remove("java.net.socks.password");
	}

	private final class ProxyAuthenticator extends Authenticator {
		private PasswordAuthentication auth;

		protected ProxyAuthenticator(String user, String pass) {
			this.auth = new PasswordAuthentication(user, pass.toCharArray());
		}

		protected PasswordAuthentication getPasswordAuthentication() {
			return this.auth;
		}
	}
}
