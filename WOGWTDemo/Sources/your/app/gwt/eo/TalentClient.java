package your.app.gwt.eo;
// this must be in a gwt translatable package

import java.util.Map;

//This class can be serialized from server to client and back
public class TalentClient extends _TalentClient {

	public TalentClient() {	
		super();
	}
	
	public TalentClient(Map map) {	
		super(map);
	}
	  
}