In order to emulate these classes they have to be rewritten completely.  Then these new classes are translated to Javascript and used on the client/browser.  The server code continues to use the standard classes, not the emulated versions.  However, since there are two implementations of the classes, behavior may differ; if you encounter problems, please file a bug report.

**NOTE: all deprecated methods in WO 5.4 are NOT supported**

**NOTE: all NSCoding methods (classForCoder, encodeWithCoder, decodeObject) are NOT supported.**

SUPPORTED CLASSES:

  * NSArray
    * Except makeObjectsPerformSelector, operatorForKey, setOperatorForKey, removeOperatorForKey
  * NSMutableArray

  * NSDictionary
  * NSMutableDictionary

  * NSSet
  * NSMutableSet

  * NSRange
  * NSMutableRange

  * NSData
    * Except stream, writeToStream, NSData(String, String), NSData(InputStream, int), NSData(URL)

  * NSTimestamp
    * Except NSTimestamp(long, TimeZone), NSTimestamp(long, int, TimeZone)

  * NSKeyValueCoding (limited though, no reflection)
    * Except Utility, DefaultImplementation, MapImplementation, ValueAccessor
  * NSKeyValueCodingAdditions (limited though, no reflection)
    * Except Utility

  * BigDecimal
  * BigInteger