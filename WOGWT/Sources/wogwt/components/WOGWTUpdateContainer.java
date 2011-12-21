package wogwt.components;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSDictionary;

import er.ajax.AjaxUpdateContainer;
import er.ajax.AjaxUtils;

/**
 * This class can used to explicitly indicate areas you want update with ajax calls.
 * This is not GWT-specific and could be used with any JS library.<br>
 * <br>
 * To use this you need to add some code into your Application.dispatchRequest method:<br>
 * <br>
 * <pre>     
	public WOResponse dispatchRequest(WORequest request) {
    	if (request.requestHandlerKey().equals(ajaxRequestHandlerKey())) {
    		AjaxUtils.updateMutableUserInfoWithAjaxInfo(request);
    	}
    	WOResponse response = super.dispatchRequest( request );
    	return response;
    }
  	</pre><br>
 * This will ensure that only the update container is included in Ajax requests.
 */
public class WOGWTUpdateContainer extends AjaxUpdateContainer {

	public WOGWTUpdateContainer(String name, NSDictionary associations, WOElement children) {
		super(name, associations, children);
	}
	
	@Override
	protected void addRequiredWebResources(WOResponse response,
			WOContext context) {
		// Don't add any scripts
		//super.addRequiredWebResources(response, context);
	}
	
	public void appendToResponse(WOResponse response, WOContext context) {
		String previousUpdateContainerID = AjaxUpdateContainer.currentUpdateContainerID();
		try {
			AjaxUpdateContainer.setCurrentUpdateContainerID(_containerID(context));
			WOComponent component = context.component();
			String elementName = (String) valueForBinding("elementName", "div", component);
			String id = _containerID(context);
			response.appendContentString("<" + elementName + " ");
			appendTagAttributeToResponse(response, "id", id);
			appendTagAttributeToResponse(response, "class", valueForBinding("class", component));
			appendTagAttributeToResponse(response, "style", valueForBinding("style", component));		
			response.appendContentString(">");
			if (hasChildrenElements()) {
				appendChildrenToResponse(response, context);
			}
			response.appendContentString("</" + elementName + ">");
	
			// Don't call super
			//super.appendToResponse(response, context);
		}
		finally {
			AjaxUpdateContainer.setCurrentUpdateContainerID(previousUpdateContainerID);
		}
	}
	
}
