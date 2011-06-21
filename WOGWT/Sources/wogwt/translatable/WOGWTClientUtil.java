package wogwt.translatable;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.NodeCollection;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.user.client.DOM;

/**
 * Utility methods for CLIENT (gwt) code.
 *
 */
public class WOGWTClientUtil {
	
	public static final String UPDATE_CONTAINER_ID_KEY = "_u";
	public static final String WOGWTMissingUpdateContainer = "WOGWTMissingUpdateContainer";
	public static final String ACTION_ID_PREFIX = "wogwt_action_";
	public static int DEFAULT_TIMEOUT_MILLISECONDS = 30000; // 30 seconds

    public static MetaElement metaTagWithName(String name) {
    	NodeList<Element> metas = Document.get().getElementsByTagName("meta");
    	for (int i = 0; i < metas.getLength(); i++) {
    		MetaElement element = MetaElement.as(metas.getItem(i));
			if (element != null &&
					element.getName() != null &&
					element.getName().equals(name)) {
				return element;
			}
    	}
    	return null;
    }
    
//    public static String metaContent(String name, String defaultValue) {
//    	MetaElement element = metaTagWithName(name);
//    	if (element != null) {
//    		return element.getContent();
//    	} else {
//    		return defaultValue;
//    	}
//    }
    
    public static boolean hostPageNameEquals(String pageName) {
    	return hostPageName().equals(pageName);
    }

    public static String publishedUrlForComponentActionNamed(String actionName) {
    	Element element = DOM.getElementById(ACTION_ID_PREFIX + actionName);
    	if (element != null) {
    		return AnchorElement.as(element).getHref();
    	} else {
    		return null;
    	}
    }
    
    /**
     * @param actionName
     * @return the direct action url (non-ajax)
     */
    public static String directActionUrlForActionNamed(String actionName) {
    	String url = directActionDefaultActionUrl();
    	
    	// replace "default" action name with actionName if there is a query dictionary in the url
    	String fixedUrl = url.replaceFirst("/default\\?", "/" + actionName + "?");
    	
    	if (fixedUrl.equals(url)) {
    		// replace "default" action name with actionName (no query dictionary)
    		fixedUrl = url.replaceFirst("/default$", "/" + actionName);
    	}
    	
    	return fixedUrl;
    }
    
    /**
     * Convenience method to fetch a direct action url using a RequestBuilder (not ajax)
     */
    public static void executeDirectAction(String actionName, RequestCallback callback) {
		WOGWTClientUtil.fetchUrl(WOGWTClientUtil.directActionUrlForActionNamed(actionName), callback);
    }
    
    /**
     * Convenience method to fetch a url using a GET request
     */
    public static void fetchUrl(String url, RequestCallback callback) {
    	fetchUrl(url, false, null, DEFAULT_TIMEOUT_MILLISECONDS, callback);
    }

    /**
     * Convenience method to fetch a url using a RequestBuilder
     */
    public static void fetchUrl(String url, boolean usePostMethod, Map<String, String> formValues, int timeoutMillis, RequestCallback callback) {
		Method method = usePostMethod ? RequestBuilder.POST : RequestBuilder.GET;
    	RequestBuilder requestBuilder = new RequestBuilder(method, url);
		requestBuilder.setTimeoutMillis(timeoutMillis);

		if (method.equals(RequestBuilder.POST)) {
			requestBuilder.setHeader("Content-type", "application/x-www-form-urlencoded");
		}
		
		StringBuffer postData = new StringBuffer();
		if (formValues != null) {
			for (Iterator keyIterator = formValues.keySet().iterator(); keyIterator.hasNext();) {
				String key = (String)keyIterator.next();
				postData.append(URL.encodeComponent(key));
				postData.append("=");
				postData.append(URL.encodeComponent(formValues.get(key)));
				if (keyIterator.hasNext()) {
					postData.append("&");
				}
			}
		}
		
		try {
			requestBuilder.sendRequest(postData.toString(), callback);
		} catch (RequestException e) {
			if (callback != null)
				callback.onError(null, e);
		}
    }
    
    public static boolean hasParent(Element elementToCheck, Element possibleParent) {
    	Element element = elementToCheck;
    	
    	while (element.getParentElement() != null) {
    		if (element.getParentElement().equals(possibleParent)) {
    			return true;
    		}
    		element = element.getParentElement();
    	}
    	
    	return false;
    }

	public static String componentUrlToAjaxUrl(final String componentActionUrl,
			final String updateContainerID) {
		
		StringBuffer urlBuffer = new StringBuffer();
		urlBuffer.append(componentActionUrl);
		
		String separator = (componentActionUrl.indexOf("?") == -1) ? "?" : "&";
		urlBuffer.append(separator);
		
		if (updateContainerID != null && updateContainerID.length() > 0) {
			urlBuffer.append(WOGWTClientUtil.UPDATE_CONTAINER_ID_KEY);
			urlBuffer.append("=");
			urlBuffer.append(updateContainerID);
			urlBuffer.append("&");
		}
		
		urlBuffer.append(new Date().getTime()); // add the time to prevent browser caching
		
		
		final String woHandlerKey = componentRequestHanderKey(); 
		final String ajaxHandlerKey = ajaxRequestHanderKey();
		
		final String url = urlBuffer.toString()  
				.replace("/" + woHandlerKey + "/", "/" + ajaxHandlerKey + "/");
		return url;
	}
	
	public static native String hostPageName() /*-{
		return $wnd.WOGWT.hostPageName;
	}-*/;
	
	public static native String rpcUrl() /*-{
		return $wnd.WOGWT.rpcUrl;
	}-*/;
	
	public static native String directActionDefaultActionUrl() /*-{
		return $wnd.WOGWT.directActionDefaultActionUrl;
	}-*/;
	
	public static native String resourceUrl() /*-{
		return $wnd.WOGWT.resourceUrl;
	}-*/;
	
	public static native String componentRequestHanderKey() /*-{
		return $wnd.WOGWT.componentRequestHandlerKey;
	}-*/;
 
	public static native String ajaxRequestHanderKey() /*-{
		return $wnd.WOGWT.ajaxRequestHandlerKey;
	}-*/;
	
	public static FormElement formContainingElement(Element element) {
		Element result = element;
		while (result != null) {			
			if (result.getTagName().equalsIgnoreCase("form")) {
				return FormElement.as(result);
			}
			result = result.getParentElement();
		}
		return null;
	}
	
	public static String selectElementValue(SelectElement element) {
		return element.getOptions().getItem(element.getSelectedIndex()).getValue();
	}
	
	public static String formFieldValue(Element element) {
		String value = null;
		if (element.getTagName().equalsIgnoreCase("INPUT")) {
			InputElement input = InputElement.as(element);
			if (input.getType().equalsIgnoreCase("TEXT") ||
					input.getType().equalsIgnoreCase("PASSWORD") ||
					input.getType().equalsIgnoreCase("FILE") ||
					input.getType().equalsIgnoreCase("HIDDEN")) {
				value = input.getValue();
			} else if (input.getType().equalsIgnoreCase("RADIO") ||
					input.getType().equalsIgnoreCase("CHECKBOX")) {
				value = input.isChecked() ? input.getValue() : null;
			}
		} else if (element.getTagName().equalsIgnoreCase("SELECT")) {
			SelectElement selectElement = SelectElement.as(element);
			value = selectElementValue(selectElement);
		} else if (element.getTagName().equalsIgnoreCase("TEXTAREA")) {
			TextAreaElement textArea = TextAreaElement.as(element);
			value = textArea.getValue();
		}
		return value;
	}
	
	/**
	 * @param elementInForm any element contained in the form, or the form itself
	 * @return a map with the names and values for all the form elements inside the form
	 */
	public static Map<String, String> formValuesForAllFields(Element elementInForm) {
		HashMap<String, String> formValues = new HashMap<String, String>();
		
		FormElement form = WOGWTClientUtil.formContainingElement(elementInForm);
		NodeCollection<Element> formFields = form.getElements();
		
		for (int i = 0; i < formFields.getLength(); i++) {
			Element field = formFields.getItem(i);

//			if (field.getTagName().equalsIgnoreCase("BUTTON") ||
//					(field.getTagName().equalsIgnoreCase("INPUT") &&
//					 field.getAttribute("type").equalsIgnoreCase("SUBMIT"))) {
//				continue;
//			}
			
			String name = field.getAttribute("name");
			String value = WOGWTClientUtil.formFieldValue(field);
			if (value != null)
				formValues.put(name, value);
		}

		return formValues;
	}
	
}
