# CustomFieldSerializer #

This is not about WOGWT in particular, but for anyone who wants to customize the way GWT serializes objects - this is done by using a CustomFieldSerializer.


---


A CustomFieldSerializer class for `com.webobjects.foundation.NSRange` would look like this:

```
package com.webobjects.foundation;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.webobjects.foundation.NSRange;

public final class NSRange_CustomFieldSerializer {

    public static void deserialize(SerializationStreamReader streamReader, NSRange instance)
        throws SerializationException {
    	...
    }

    public static NSRange instantiate(SerializationStreamReader streamReader)
    	throws SerializationException {
    	...
    }

    public static void serialize(SerializationStreamWriter streamWriter, NSRange instance)
        throws SerializationException {
        ...
    }
    
}
```

The class needs to be in the same package as the class it is serializing (`com.webobjects.foundation`) OR in the package `com.google.gwt.user.client.rpc.core.com.webobjects.foundation`.


---


Things to notice about this class:
  * The class is named with the name of the class being serialized plus "_CustomFieldSerializer"
  * The seralize and deserialize methods are required
  * The instantiate method is optional.  If you don't provide it, then GWT will invoke the zero-arg constructor for the class._

The methods are called in this order: serialize, instantiate, deseralize.


---


The CustomFieldSerializer class is two-way:
  * it is invoked to transfer an object from the server to the client
    * serialize (on the server)
    * instantiate (on the client)
    * deserialize (on the client)

  * it is invoked to transfer an object from the client to the server
    * serialize (on the client)
    * instantiate (on the server)
    * deserialize (on the server)

**Because the class is used on the both the client and server, the classpath must include BOTH the binary (.class) and the source (.java) for the CustomFieldSerializer.**


---


What does the implementation of these methods look like?  Here's an example for NSRange.

NSRange has two instance variables:
```
    protected int location;
    protected int length;
```

So the CustomFieldSerializer looks like this:

```
package com.webobjects.foundation;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.webobjects.foundation.NSRange;

public final class NSRange_CustomFieldSerializer {

    public static void deserialize(SerializationStreamReader streamReader, NSRange instance)
        throws SerializationException {
    	
    }

    public static NSRange instantiate(SerializationStreamReader streamReader)
    	throws SerializationException {
    	int location = streamReader.readInt();
    	int length = streamReader.readInt();
    	return new NSRange(location, length);
    }

    public static void serialize(SerializationStreamWriter streamWriter, NSRange instance)
        throws SerializationException {
        streamWriter.writeInt(instance.location());
        streamWriter.writeInt(instance.length());
    }
    
}
```

In this case, NSRange is immutable, so we do all the reading from the stream in the instantiate method, before the object is constructed.  Usually the reading with occur in the deserialize method instead.


---


A CustomFieldSerializer relates one-to-one to a serializable class.  Each serializable class must have it's own specific CustomFieldSerializer.  So suppose you have a base class `ParentClass` and two descendants `ChildClassOne` and `ChildClassTwo`.  You also have a class called `ParentClass_CustomFieldSerializer`.  Even if the child classes introduce no additional fields and can be serialized the same way as `ParentClass`, you still must define an explicit `ChildClassOne_CustomFieldSerializer` and a `ChildClassTwo_CustomFieldSerializer`.  However, you can stick the implementation of the methods in a common class and call that, so this allows for some code sharing, but you will still end up with a lot of classes.


---


**Inner Classes**

It is theorically possible to have a CustomFieldSerializer for an inner class, but a bug in GWT currently prevents this from working.  In theory though, you would need to name your CustomFieldSerializer such that you have a file like `com/yourcompany/OuterClass$InnerClass.java`.  So the name of the class is the outer class name plus a dollar sign plus the inner class name.