package com.google.gwt.user.client.rpc.core.com.webobjects.foundation;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.core.java.util.Collection_CustomFieldSerializerBase;
import com.webobjects.foundation.NSMutableSet;

public final class NSMutableSet_CustomFieldSerializer {

	public static void deserialize(SerializationStreamReader streamReader, NSMutableSet instance)
        throws SerializationException {
    	Collection_CustomFieldSerializerBase.deserialize(streamReader, instance);
    }
    
    public static void serialize(SerializationStreamWriter streamWriter, NSMutableSet instance)
        throws SerializationException {
    	Collection_CustomFieldSerializerBase.serialize(streamWriter, instance);
    }
    
}