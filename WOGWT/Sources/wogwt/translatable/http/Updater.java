package wogwt.translatable.http;

import java.util.HashMap;
import java.util.Map;

import wogwt.translatable.WOGWTClientUtil;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.gen2.logging.shared.Log;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;

/** 
 * This class will do an ajax request to the url specified by the updateContainer's
 * "updateUrl" attribute and replace the contents of the updateContainer
 * with the response.
 */
public class Updater {

	private String updateContainerID;
	private AfterDOMUpdateCallback afterDOMUpdateCallback;
	private String actionName;
	private String lastUrl;
	private Object lastSender;
	
	/**
	 * If you don't specify an updateContainer, then the URL will be fetched,
	 * but the response will just be ignored.  That could be useful if you just
	 * need to change something on the server and don't need a response.
	 * Or, if you leave out the updateContainer, updateDOM will do nothing, 
	 * but you will still be able to access the response and do something in
	 * the afterDOMUpdateCallback.
	 */
	public Updater() {
		super();
	}
	
	public Updater(String updateContainerID) {
		super();
		this.updateContainerID = updateContainerID;
	}
	
	/**
	 * 
	 * @param updateContainerID the DOM element replace with the response
	 * @param callback used mainly to re-attach any event listeners to the DOM elements 
	 * in the updateContainer that were replaced.  This is called even if there is no
	 * updateContainerID specified to allow for custom response handling.
	 */
	public Updater(String updateContainerID, AfterDOMUpdateCallback callback) {
		this(updateContainerID);
		this.afterDOMUpdateCallback = callback;
	}
	
	public Updater(String actionName, String updateContainerID, AfterDOMUpdateCallback callback) {
		this(updateContainerID, callback);
		this.actionName = actionName;
	}
	
	/**
	 * Sends the request and if successful the callback will update the DOM
	 */
	public void fireUpdate() {
		saveObjectsForCallback();
		
		WOGWTClientUtil.fetchUrl( 
				getUrl(), 
				usePostRequestMethod(),
				getFormValues(), 
				getRequestTimeoutMilliseconds(),
				getRequestCallback() );
	}

	protected void saveObjectsForCallback() {
		lastSender = getSender();
		lastUrl = getUrl();
	}
		
	/**
	 * @return false to use the GET method for the request, true to use POST
	 * uses GET by default.
	 */
	protected boolean usePostRequestMethod() {
		return false;
	}
	
	/**
	 * @return the form values to be submitted via post.
	 * returns an empty Map by default.
	 */
	protected Map<String, String> getFormValues() {
		return new HashMap<String, String>(0);
	}
	
	/**
	 * @return number of milliseconds before the request times out
	 * returns 60 seconds by default.
	 */
	protected int getRequestTimeoutMilliseconds() {
		return 60000;
	}
	
	/**
	 * @return the default request callback, which just calls updateDOM and
	 * the afterDOMUpdateCallback on success.  Any errors are just logged.
	 * 
	 */
	protected RequestCallback getRequestCallback() {
		return new LogOnErrorRequestCallback() {
			public void onSuccess(Request request, Response response) {
				updateDOM(response);

				if (getAfterDOMUpdateCallback() != null) {
					getAfterDOMUpdateCallback().afterDOMUpdate(lastSender, lastUrl, response);
				}
			}
		};
	}
	
	/**
	 * Replaces the contents of updateContainer (if there is one) with the response.
	 * 
	 * If the server returns the header AnchorWithHistory.WOGWTMissingUpdateContainer
	 * and the response is a whole page, the body element of the page is replaced with
	 * the response because it probably an error page.
	 * 
	 * @param response
	 */
	protected void updateDOM(Response response) {
		if (response.getHeader(WOGWTClientUtil.WOGWTMissingUpdateContainer) == null) {
			if (getUpdateContainerID() != null && getUpdateContainerID().length() > 0) {
				DOM.getElementById(updateContainerID).setInnerHTML(response.getText());
				/* if you want to return a whole page from the server and parse out the 
				   update container on the client, you could use this instead */
				//XMLUtilsClient.replaceContents(response.getText(), containerID);
			} 
		} else {
			if (response.getText().indexOf("<html") != -1) {
				// the updateContainer is missing and the response is an entire page - probably an error page,
				// so replace the whole page with the response
				Document.get().getBody().setInnerHTML(response.getText());
				History.newItem("updateError");
			} else {
				Log.severe("the update container " + getUpdateContainerID() + " is missing.");
			}
		}
	}
	
	/**
	 * 
	 * @param sender must have an attribute called "updateUrl" with a 
	 * component action url (/wo/) that returns null
	 * @return url to get
	 */
	protected String getUrl() {
		String url;
		if (getActionName() == null) {
			url = ((Element)getSender()).getAttribute("updateUrl");
			if (url == null || url.length() == 0) {
				throw new RuntimeException("You must specifiy an updateUrl attribute");
			}
		} else {
			url = WOGWTClientUtil.publishedUrlForComponentActionNamed(getActionName());
		}
		return WOGWTClientUtil.componentUrlToAjaxUrl(url, getUpdateContainerID());
	}
	
	protected String getUpdateContainerID() {
		return updateContainerID;
	}

	protected AfterDOMUpdateCallback getAfterDOMUpdateCallback() {
		return afterDOMUpdateCallback;
	}

	/**
	 * @return com.google.gwt.dom.client.Element
	 * The DOM element for the updateContainerID
	 */
	protected Object getSender() {
		return DOM.getElementById(getUpdateContainerID());
	}
	
	protected Object getLastSender() {
		return lastSender;
	}
	
	protected String getLastUrl() {
		return lastUrl;
	}
	
	protected String getActionName() {
		return actionName;
	}
}
