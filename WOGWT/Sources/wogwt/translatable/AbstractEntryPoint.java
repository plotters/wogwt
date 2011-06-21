package wogwt.translatable;

import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.http.client.RequestCallback;

public abstract class AbstractEntryPoint implements EntryPoint {

	protected Logger log = Logger.getLogger(getClass().getName());
    
	public abstract void onModuleLoad();
	
    public boolean hostPageNameEquals(String pageName) {
    	return hostPageName().equals(pageName);
    }

    public String publishedUrlForComponentActionNamed(String actionName) {
    	return WOGWTClientUtil.publishedUrlForComponentActionNamed(actionName);
    }
    
    /**
     * @param actionName
     * @return the direct action url (non-ajax)
     */
    public String directActionUrlForActionNamed(String actionName) {
    	return WOGWTClientUtil.directActionUrlForActionNamed(actionName);
    }
    
    /**
     * Convenience method to fetch a url using a GET request
     */
    public void fetchUrl(String url, RequestCallback callback) {
    	WOGWTClientUtil.fetchUrl(url, callback);
    }

    /**
     * Convenience method to fetch a url using a RequestBuilder
     */
    public void fetchUrl(String url, boolean usePostMethod, Map<String, String> formValues, int timeoutMillis, RequestCallback callback) {
		WOGWTClientUtil.fetchUrl(url, usePostMethod, formValues, timeoutMillis, callback);
    }
    
    /**
     * Convert a regular component (wo) url to an ajax (ja) url with an optional 
     * update container specified for a partial page response.
     * 
     * @param componentActionUrl
     * @param updateContainerID
     * @return
     */
	public String componentUrlToAjaxUrl(final String componentActionUrl,
			final String updateContainerID) {
		return componentUrlToAjaxUrl(componentActionUrl, updateContainerID);
	}
	
	public String hostPageName() {
		return WOGWTClientUtil.hostPageName();
	}
	
	public String rpcUrl() {
		return WOGWTClientUtil.rpcUrl();
	}
	
	public String resourceUrl() {
		return WOGWTClientUtil.resourceUrl();
	}
	
	public String componentRequestHanderKey() {
		return WOGWTClientUtil.componentRequestHanderKey();
	}
 
	public String ajaxRequestHanderKey() {
		return WOGWTClientUtil.ajaxRequestHanderKey();
	}
	
}
