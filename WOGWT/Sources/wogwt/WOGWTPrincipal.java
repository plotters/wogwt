package wogwt;

import er.extensions.ERXFrameworkPrincipal;

public class WOGWTPrincipal extends ERXFrameworkPrincipal {

	public static final String FRAMEWORK_NAME = "WOGWT";
	
	static {
		setUpFrameworkPrincipalClass(WOGWTPrincipal.class);
	}

	@Override
	public void finishInitialization() {
		log.debug("WOGWT loaded");
	}
	
}
