package your.app.gwt;

import wogwt.translatable.WOGWTClientUtil;
import wogwt.translatable.rpc.LogOnErrorAsyncCallback;
import your.app.gwt.eo.MovieClient;
import your.app.gwt.rpc.EOService;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.webobjects.foundation.NSArray;

public class AutoCompletionExampleScript implements EntryPoint {

	/* This class must be declared as an entry-point in the module. */
	public void onModuleLoad() {
		if (!WOGWTClientUtil.hostPageNameEquals("AutoCompletionExample")) {
			return;
		}
		
		Log.debug(getClass().getName() + ": onModuleLoad");
		
		EOService.Util.getInstance().allMovies(
				new LogOnErrorAsyncCallback<NSArray<MovieClient>>() {
					public void onSuccess(NSArray<MovieClient> response) {
						final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
						final SuggestBox box = new SuggestBox(oracle);
						
						NSArray<String> titles = (NSArray<String>) response.valueForKey(MovieClient.TITLE_KEY);
						oracle.addAll(titles);
						
						RootPanel.get("container").add(box);
					}
		});
	}
	
}
