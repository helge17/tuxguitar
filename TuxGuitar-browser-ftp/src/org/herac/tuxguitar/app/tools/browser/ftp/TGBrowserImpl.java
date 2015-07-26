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
import org.herac.tuxguitar.app.tools.browser.base.TGBrowserElement;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserCdElementHandler;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserCdRootHandler;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserCdUpHandler;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserCloseHandler;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserListElementsHandler;
import org.herac.tuxguitar.app.tools.browser.base.handler.TGBrowserOpenHandler;

public class TGBrowserImpl implements TGBrowser {
	
	private TGBrowserDataImpl data;
	private String root; 
	private String path; 
	private TGBrowserFTPClient client;
	
	public TGBrowserImpl(TGBrowserDataImpl data){
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
	
	public void open(TGBrowserOpenHandler handler) {
		try {
			checkForProxy();
			this.client = new TGBrowserFTPClient();
			this.client.open(this.data.getHost(), TGBrowserFTPClient.DEFAULT_PORT);
			this.client.login(this.data.getUsername(),this.data.getPassword());
			this.client.cd(getRoot());
			this.path = this.client.pwd();
			
			handler.onSuccess();
		} catch(Throwable throwable) {
			handler.handleError(throwable);
		}
	}

	
	public void close(TGBrowserCloseHandler handler) {
		try {
			this.closeProxy();
			this.client.close();
			
			handler.onSuccess();
		} catch(Throwable throwable) {
			handler.handleError(throwable);
		}
	}
	
	public void cdElement(TGBrowserElement element, TGBrowserCdElementHandler handler) {
		try {
			boolean isCDSuccess = this.client.cd(element.getName());
			if(!isCDSuccess) {
				throw new TGBrowserException("could not cd to "+element.getName());
			}
			this.path = this.client.pwd();
			
			handler.onSuccess();
		} catch (Throwable throwable) {
			handler.handleError(throwable);
		}
	}
	
	public void cdRoot(TGBrowserCdRootHandler handler) {
		try {
			this.client.cd(getRoot());
			this.path = this.client.pwd();
			
			handler.onSuccess();
		} catch(Throwable throwable) {
			handler.handleError(throwable);
		}
	}
	
	public void cdUp(TGBrowserCdUpHandler handler) {
		try {
			this.client.cdUp();
			this.path = this.client.pwd();
			
			handler.onSuccess();
		} catch (Throwable throwable) {
			handler.handleError(throwable);
		}
	}
	
	public void listElements(TGBrowserListElementsHandler handler) {
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
								elements.add(new TGBrowserElementImpl(this,name,info,this.path));
								break;
							}
						}
					}
				}
			}
			if( !elements.isEmpty() ){
				Collections.sort(elements, new TGBrowserElementComparator());
			}
			
			handler.onSuccess(elements);
		} catch (Throwable throwable) {
			handler.handleError(throwable);
		}
	}
	
	public InputStream getInputStream(String path,TGBrowserElement element)throws TGBrowserException {
		try {
			this.client.cd(path);
			this.client.binary();
			
			byte[] bytes = this.client.get(element.getName());
			
			return new ByteArrayInputStream( bytes );
		} catch (Throwable throwable) {
			throw new TGBrowserException(throwable);
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
