GWT RPC allows you to transfer objects between client and server in Java.  GWT serizalization is limited to only serializing certain kinds of objects.  Regular EOs aren't able to be serialized so WOGWT includes templates that allow you to generate Data Transfer Objects (DTOs) for your entities.

An RPC service consists of 3 files:

  * MyService.java (interface)
  * MyServiceAsync.java (interface)
  * MyServiceImpl.java (class)

The two interfaces follow regular GWT conventions, so you can read the GWT documentation to find out more.

The implementation class is different from standard GWT because it doesn't extend RemoteServiceSerlvet since we aren't using servlets.  Instead your implementation class should:
  * extend wogwt.server.rpc.WOGWTRpcService
  * have a constructor that takes a WOContext parameter

The implementation class has be associated with the interface classes by setting the property 'wogwt.rpcImplementationPackage' to be the package containing your service implementations.  For example:
```
wogwt.rpcImplementationPackage = your.app.server.rpc
```

So a service with one function that returns an array of EOs would look like this:

your/app/gwt/rpc/EOService.java
```
package your.app.gwt.rpc;

import wogwt.translatable.WOGWTClientUtil;
import your.app.gwt.eo.RootEntityClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.webobjects.foundation.NSArray;

public interface EOService extends RemoteService {
	
	public static class Util {
		public static EOServiceAsync getInstance() {
			EOServiceAsync service = (EOServiceAsync)GWT.create(EOService.class);
			((ServiceDefTarget)service).setServiceEntryPoint(WOGWTClientUtil.rpcUrl());
			return service;
		}
	}

	public NSArray<RootEntityClient> allRootEntities();

}
```

your/app/gwt/rpc/EOServiceAsync.java
```
package your.app.gwt.rpc;

import your.app.gwt.eo.RootEntityClient;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.webobjects.foundation.NSArray;

public interface EOServiceAsync {
	
	public void allRootEntities(AsyncCallback<NSArray<RootEntityClient>> callback);

}
```

your/app/server/rpc/EOServiceImpl.java
```
package your.app.server.rpc;

import wogwt.WOGWTServerUtil;
import wogwt.server.rpc.WOGWTRpcService;
import your.app.eo.RootEntity;
import your.app.gwt.eo.RootEntityClient;
import your.app.gwt.rpc.EOService;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;

public class EOServiceImpl extends WOGWTRpcService implements EOService {
	
	public EOServiceImpl(WOContext context) {
		super(context);
	}
	
	public NSArray<RootEntityClient> allRootEntities() {
		NSArray<RootEntity> eos = RootEntity.fetchAllRootEntities(session().defaultEditingContext());
		return WOGWTServerUtil.toClientEOList(eos);
	}
		
}
```

Notice that the return parameter is the "client" class, the DTO, not the regular EO.