package wogwt.components;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import wogwt.translatable.WOGWTClientUtil;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import er.extensions.components.ERXStatelessComponent;

/**
 * This component "publishes" the urls of all the component actions in the
 * host page containing it so that the urls can be used to dynamically create
 * GWT hyperlinks or other widgets that call component actions.  Component
 * action urls have to be generated on the server, but GWT encourages
 * the construction of the UI on the client, so this component allows you to
 * do that and still access the component actions on the server.
 * 
 * This component is invisible, but must be placed in the BODY of the page 
 * because it generates hidden anchor elements.
 * 
 */
public class WOGWTActionPublisher extends ERXStatelessComponent {
	
	public NSArray<Method> actionMethodList;
	public Method actionMethodItem;
	
    public WOGWTActionPublisher(WOContext context) {
        super(context);
    }
    
    @Override
    public boolean isStateless() {
    	return true;
    }
    
    public String actionMethodName() {
    	return actionMethodItem.getName();
    }
    
    public String actionMethodNameAnnotated() {
    	return WOGWTClientUtil.ACTION_ID_PREFIX + actionMethodName();
    }
    
    @Override
    public void appendToResponse(WOResponse response, WOContext context) {
    	// TODO: figure out how to write this content in to the head tag directly instead of placing the component there
    	actionMethodList = findActionMethods();
//    	
//    	StringBuffer script = new StringBuffer();
//    	script.append("if (!WOGWT)\n");
//    	script.append("  WOGWT={};\n");
//    	script.append("WOGWT.actions={};\n");
//    	
//    	for (int i = 0; i < actionMethodList.size(); i++) {
//    		script.append("WOGWT.actions[" + actionMethodList.get(i).getName());
//		}
//    	
//    	ERXResponseRewriter.addScriptCodeInHead(response, context, script.toString());
//    	
    	super.appendToResponse(response, context);
    }

	protected NSArray<Method> findActionMethods() {
		NSMutableArray<Method> actionMethods = new NSMutableArray<Method>();
    	
    	Method[] methods = context().page().getClass().getMethods();
    	for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (method.getParameterTypes().length == 0) {
				if (WOActionResults.class.isAssignableFrom(method.getReturnType()) ||
						method.getReturnType().equals(void.class)) {
					if (!method.getDeclaringClass().equals(WOComponent.class) &&
							!method.getDeclaringClass().equals(Object.class) &&
							!method.getName().equals("reset")) {
						actionMethods.addObject(method);
					}
				}
			}
		}
    	
    	return actionMethods.immutableClone();
	}
    
    public WOActionResults performAction() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method method = context().page().getClass().getDeclaredMethod(actionMethodName(), new Class[0]);
		Object result = method.invoke(context().page(), new Object[0]);
		return (WOActionResults) result;
    }
 
}