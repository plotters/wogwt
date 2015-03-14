**Q: What is GWT?**

A: GWT is _mainly_ a Java-to-Javascript compiler, allowing you to write Javascript without the pain of writing Javascript.

**Q: How does GWT integrate with WebObjects?**

A: The GWT compiler produces static Javascript files that are included in your application as WebServerResources.  The Javascript can interact with your server through regular ajax HTTP requests and through RPC calls to Java services that you create.  RPC requests are handled by the special WOGWT request handler.

**Q: If I use GWT do I have to rewrite my whole application?**

A: No, GWT can be used as little or as much as you need, in the same way as you would use Javascript.  You can use GWT to wrap existing HTML elements on a page to add dynamic behavior; or you can use GWT to create entirely new elements and widgets, or even construct your entire UI with it.

**Q: What dependencies are necessary to use WOGWT?**

A: A recent version Project Wonder.  Both WebObjects 5.3 and 5.4 are supported.  If you're not using WO 5.4 you can use Wonder's Ajax request handler instead WO 5.4's Ajax handler.

**Q: It's not working, why not?**

A: Errors that occur in GWT Development Mode (aka Hosted Mode) are shown in the Development Mode view/tab in Eclipse - look there for more details about what went wrong.

**Q: I get the error "No source code is available for type wogwt.translatable.WOGWTClientUtil; did you forget to inherit a required module?"**

A: You are using the WOGWT source project and not the installed framework.  That's fine, but to do this you have to add the WOGWT/Source folder to the classpath in your GWT Development Mode (aka Hosted Mode) launch configuration.  Go to the Debug menu and select Debug Configurations.  Select your run configuration and click the "Classpath" tab.  Select "User Entries".  Click "Advanced".  Select "Add Folders".  Select WOGWT/Sources.  Click OK.

**Q: I get the error "GWT module 'your.app.gwt.Application' may need to be (re)compiled"**

A: Every page you load in GWT Development Mode must include in the url "?gwt.codesvr=localhost:9997" as a query parameter.  WOGWT can take care of this for you; in your Application constructor add these lines:
```
if (wogwt.components.WOGWTScript.isHostedMode()) {
     setContextClassName(wogwt.GWTContext.class.getName());
}
```