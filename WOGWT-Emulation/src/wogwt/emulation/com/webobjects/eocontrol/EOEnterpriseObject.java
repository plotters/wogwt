package com.webobjects.eocontrol;

import java.io.Serializable;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

public interface EOEnterpriseObject extends 
	Serializable, EOKeyValueCodingAdditions, 
	EORelationshipManipulation, EOValidation, EODeferredFaulting {

	public void addObjectToPropertyWithKey(Object eo, String key);
	
	public NSArray<String> allPropertyKeys();
	
	public NSArray<String> attributeKeys();
	
	public void awakeFromClientUpdate(EOEditingContext ec);
	
	public void awakeFromFetch(EOEditingContext ec);
	
	public void awakeFromInsertion(EOEditingContext ec);
	
	
	public NSDictionary changesFromSnapshot(NSDictionary<String, Object> snapshot);
	
	public EOClassDescription classDescription();
	
	public EOClassDescription classDescriptionForDestinationKey(String detailKey);
	
	public void clearFault();
	
	public void clearProperties();
	
	public int deleteRuleForRelationshipKey(String relationshipKey);
	
	public EOEditingContext editingContext();
	
	public String entityName();
	
	public String eoDescription();
	
	public String eoShallowDescription();
	
	public String inverseForRelationshipKey(String relationshipKey);
	
	public boolean isFault();
	
	public boolean isReadOnly();
	
	public boolean isToManyKey(String key);
	
	public Object opaqueState();
	
	public boolean ownsDestinationObjectsForRelationshipKey(String relationshipKey);
	
	public void prepareValuesForClient();
	
	public void propagateDeleteWithEditingContext(EOEditingContext ec);
	
	public void reapplyChangesFromDictionary(NSDictionary<String, Object> changes);
	
	public void removeObjectFromPropertyWithKey(Object eo, String key);
	
	public NSDictionary<String, Object> snapshot();
	
	public NSArray<String> toManyRelationshipKeys();
	
	public NSArray<String> toOneRelationshipKeys();
	
	public void updateFromSnapshot(NSDictionary<String, Object> snapshot);
	
	public String userPresentableDescription();
	
	public void willChange();
	public void willRead();
	
	
}
