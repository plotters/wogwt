package wogwt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Node;

import wogwt.server.rpc.WOGWTServerEO;
import wogwt.translatable.WOGWTClientUtil;
import wogwt.translatable.rpc.WOGWTClientEO;

import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOCustomObject;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOKeyGlobalID;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSComparator;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSComparator.ComparisonException;

import er.extensions.foundation.ERXDictionaryUtilities;

/**
 * Utility functions for the Server ONLY.
 *
 */
public class WOGWTServerUtil {

	private WOGWTServerUtil() {
	}
	
	/**
	 * Reads the updateContainerID from the url and then strips everything from the 
	 * response except the updateContainer's html
	 * 
	 * If the response is already an instance of AjaxResponse this method does nothing.
	 * 
	 * @param request
	 * @param response the fully populated response to extract the update container from
	 */
	public static void onlyIncludeUpdateContainerInResponse(WORequest request, WOResponse response) {
		if (response.getClass().getName().equals("er.ajax.AjaxResponse")) {
			return;
		}
		
    	String updateContainerID = request.stringFormValueForKey(WOGWTClientUtil.UPDATE_CONTAINER_ID_KEY);
    	if (updateContainerID != null) {
	    	Node updateContainer = XMLUtilsServer.xhtmlElementWithID(response.contentString(), updateContainerID);
	    	if (updateContainer != null) {
	    		String partialResponse = XMLUtilsServer.serializeChildren(updateContainer);
	    		//String partialResponse = XMLUtilsServer.childrenToString(updateContainer); 	
	    		response.setContent(partialResponse);
	    	} else {
	    		NSLog.debug.appendln("update container '" + updateContainerID + "' not present in response");
	    		response.appendHeader(updateContainerID, WOGWTClientUtil.WOGWTMissingUpdateContainer);
	    	}
    	}	
	}
	
	/**
	 * Useful if you aren't using Wonder
	 * @param eo
	 * @return the primary key of the EO
	 */
	public static Object primaryKeyValue(EOEnterpriseObject eo) {
		if (eo.editingContext() != null && !eo.editingContext().globalIDForObject( eo ).isTemporary()) {
			return ((EOKeyGlobalID)eo.editingContext().globalIDForObject( eo )).keyValues()[0];
		} else {
			return null;
		}
	}
	  
	/**
	 * Creates a dictionary that can be used to construct a DTO: WOGWTClientEO
	 * @param eo
	 * @return dictionary containing the EOs attribute values, globalID, and isFault value
	 * 
	 */
	public static NSDictionary<String, Object> eoToDictionary(EOCustomObject eo) {
		NSMutableDictionary<String, Object> data = new NSMutableDictionary<String, Object>();
		
		data.setObjectForKey( eo.__globalID() == null ? 
				NSKeyValueCoding.NullValue : 
					eo.__globalID(), 
					"__globalID" );
		data.setObjectForKey( eo.isFault(), "isFault" );
		
		if (!eo.isFault()) {
			for (String attributeKey : eo.attributeKeys()) {
				data.setObjectForKey( 
						eo.valueForKey(attributeKey) == null ? 
								NSKeyValueCoding.NullValue : eo.valueForKey(attributeKey), 
						attributeKey );
			}

		}

		return data;
	}
	
	/**
	 * Converts regular EOs to their Client class versions.
	 * @param serverEOs
	 * @return array of Client EOs
	 */
	public static NSArray<? extends WOGWTClientEO> toClientEOList(List<? extends WOGWTServerEO> serverEOs) {
		return toClientEOList(serverEOs, null);
	}
	
	/**
	 * Converts regular EOs to their Client class versions.
	 * @param serverEOs
	 * @param keyPathsToSerialize a list of keys identifying relationships to include in the serialized objects
	 * @return array of Client EOs
	 */
	public static NSArray<? extends WOGWTClientEO> toClientEOList(List<? extends WOGWTServerEO> serverEOs, List<String> keyPathsToSerialize) {
		NSMutableArray<WOGWTClientEO> result = new NSMutableArray<WOGWTClientEO>(serverEOs.size());	  

		for (int i = 0; i < serverEOs.size(); i++) {
			WOGWTServerEO eo = serverEOs.get(i);
			if (keyPathsToSerialize == null)
				result.add( eo.toClientEO() );
			else
				result.add( eo.toClientEO(keyPathsToSerialize) );
		}
		
		return result.immutableClone();
	}
	
	/** sorts keyPath Strings in order of increasing depth */
	private static class KeyPathComparator extends NSComparator {
		@Override
		public int compare(Object left, Object right) throws ComparisonException {
			if (left == null && right == null)
				return 0;
			else if (left != null && right == null)
				return 1;
			else if (right != null && left == null)
				return -1;
			else if (!(left instanceof String) || !(right instanceof String))
				throw new ComparisonException("KeyPathComparator accepts only string arguments.");
			else {
				String[] leftParts = ((String)left).split("\\.");
				String[] rightParts = ((String)right).split("\\.");
				return new Integer(leftParts.length).compareTo(new Integer(rightParts.length));
			}
		}
	}
	
	private static String restOfKeyPath(String keyPath) {
		int dotIndex = keyPath.indexOf(NSKeyValueCodingAdditions.KeyPathSeparator);
		if (dotIndex == -1 || keyPath.length()-1 <= dotIndex) {
			return null;
		} else {
			return keyPath.substring(dotIndex+1);
		}	
	}
	
	/**
	 * 
	 * @param keyPaths assumes that these are already sorted in order of increasing depth.
	 * @param key
	 * @return
	 */
	private static List<String> keyPathsBelowGivenKey(List<String> keyPaths, String key) {
		List<String> result = new ArrayList<String>();
		
		for (Iterator<String> iterator = keyPaths.iterator(); iterator.hasNext();) {
			String keyPath = (String) iterator.next();
			if (keyPath.startsWith(key + ".")) {
				result.add(restOfKeyPath(keyPath));
			}
		}

		return result;
	}
	
	private static List<String> addIntermediateKeyPaths(List<String> keyPaths) {
		List<String> result = new ArrayList<String>();
		result.addAll(keyPaths);
		
		for (Iterator<String> iterator = keyPaths.iterator(); iterator.hasNext();) {
			String keyPath = iterator.next();
			String[] parts = keyPath.split("\\.");
			
			for (int i = 0; i < parts.length; i++) {
				String keyPathToAdd = "";
				
				// add all the preceding parts together
				for (int j = 0; j < i; j++) {
					keyPathToAdd += parts[j] + ".";
				}
				
				keyPathToAdd += parts[i]; 
				
				if (!result.contains(keyPathToAdd)) {
					result.add(keyPathToAdd);
				}
			}
		}
		
		return result;
	}
		
	/**
	 * 
	 * @param rootEO
	 * @param keyPathsToSerialize
	 * @return a dictionary with a key for each relationship the client EO as the value
	 */
	public static NSDictionary<String, Object> _keyPathsToClientEOs(EOEnterpriseObject rootEO, List<String> keyPathsToSerialize) {
		// sort the list so the keyPaths are in order of increasing depth
		NSArray<String> keyPaths = new NSArray<String>(addIntermediateKeyPaths(keyPathsToSerialize));
		try {
			keyPaths = keyPaths.sortedArrayUsingComparator(new KeyPathComparator());
		} catch (ComparisonException e) {
			throw new RuntimeException(e);
		}
		
		NSMutableDictionary<String, Object> result = new NSMutableDictionary<String, Object>();
		for (int i = 0; i < keyPaths.size(); i++) { 
			String keyPath = keyPaths.get(i);
			
			if (keyPath.indexOf('.') != -1) // skip paths, they are handled recursively
				continue;
		
			Object value = rootEO.valueForKey(keyPath);

			if (rootEO.isToManyKey(keyPath)) {
				NSArray<WOGWTServerEO> relationshipObjects = (NSArray<WOGWTServerEO>)value;
				NSMutableArray<WOGWTClientEO> clientObjects = new NSMutableArray<WOGWTClientEO>();
				for (int j = 0; j < relationshipObjects.size(); j++) {
					WOGWTServerEO eo = (WOGWTServerEO)relationshipObjects.get(j);
					clientObjects.add(eo.toClientEO(keyPathsBelowGivenKey(keyPaths, keyPath)));
				}
				result.setObjectForKey(clientObjects.immutableClone(), keyPath);
				
			} else if (rootEO.toOneRelationshipKeys().contains(keyPath)) {
				WOGWTServerEO serverEO = (WOGWTServerEO)value;
				if (serverEO == null) {
					result.setObjectForKey(NSKeyValueCoding.NullValue, keyPath);
				} else {
					result.setObjectForKey(serverEO.toClientEO(keyPathsBelowGivenKey(keyPaths, keyPath)), keyPath);
				}
			}
		}
		return result.immutableClone();
	}
	
}
