package your.app.gwt.eo;
// this must be in a gwt translatable package

import java.util.Map;

//This class can be serialized from server to client and back
public class StudioClient extends _StudioClient {

	public StudioClient() {	
		super();
	}
	
	public StudioClient(Map map) {	
		super(map);
	}
	  
}
