package wogwt.translatable;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;

public class DigitOnlyKeyPressHandler implements KeyPressHandler {

	public static boolean isNavigationOrModifierKey(char key) {
		if (key == 0) { // fix weird problem with backspace getting prevented in Firefox
			return true;
		}
		
		return key == KeyCodes.KEY_ALT ||
			key == KeyCodes.KEY_BACKSPACE ||
			key == KeyCodes.KEY_CTRL ||
			key == KeyCodes.KEY_DELETE ||
			key == KeyCodes.KEY_DOWN ||
			key == KeyCodes.KEY_END ||
			key == KeyCodes.KEY_ENTER ||
			key == KeyCodes.KEY_ESCAPE ||
			key == KeyCodes.KEY_HOME ||
			key == KeyCodes.KEY_LEFT ||
			key == KeyCodes.KEY_PAGEDOWN ||
			key == KeyCodes.KEY_PAGEUP ||
			key == KeyCodes.KEY_RIGHT ||
			key == KeyCodes.KEY_SHIFT ||
			key == KeyCodes.KEY_TAB ||
			key == KeyCodes.KEY_UP;
	}
	
	public void onKeyPress(KeyPressEvent event) {
		if (!event.isControlKeyDown() && !event.isMetaKeyDown()) {
			if (!Character.isDigit(event.getCharCode()) && 
					!isNavigationOrModifierKey(event.getCharCode())) {
				event.preventDefault();
			} else if (!Character.isDigit(event.getCharCode()) && 
					event.isShiftKeyDown() && 
					event.getCharCode() != KeyCodes.KEY_TAB) {
				event.preventDefault();
			}
		}
	}

}
