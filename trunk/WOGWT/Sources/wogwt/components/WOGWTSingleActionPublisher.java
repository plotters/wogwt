package wogwt.components;

import wogwt.translatable.WOGWTClientUtil;

import com.webobjects.appserver.*;

// Generated by the WOLips Templateengine Plug-in at Nov 25, 2008 4:57:47 PM
public class WOGWTSingleActionPublisher extends com.webobjects.appserver.WOComponent {
	
    public WOGWTSingleActionPublisher(WOContext context) {
        super(context);
    }
    
    @Override
    public boolean isStateless() {
    	return true;
    }
    
    public String actionNameAnnotated() {
    	return WOGWTClientUtil.ACTION_ID_PREFIX + valueForBinding("action");
    }
    
}