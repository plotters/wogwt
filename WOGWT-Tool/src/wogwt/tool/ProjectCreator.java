package wogwt.tool;


import java.io.File;
import java.io.FileWriter;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class ProjectCreator {

	public static void main(String[] args) throws Exception {
		String projectDir = args[0];
		
		String projectName = new File(projectDir).getName();
		
		String applicationClassFileName = findFile(projectDir, "Application.java");
		if (applicationClassFileName == null)
			throw new RuntimeException("Can't find Application.java");
		
		String applicationClassText = FileUtils.readFileToString(new File(applicationClassFileName));
		
		int packageIndex = applicationClassText.indexOf("package");
		String applicationClassPackage = 
			applicationClassText.substring(
				packageIndex + "package".length(),
				applicationClassText.indexOf(";", packageIndex)).trim();
		
		String applicationClassWithPackage = applicationClassPackage + ".Application";
		
		String gwtPackage = applicationClassPackage + ".gwt";
		
		String sourceDir = applicationClassFileName.replace(applicationClassWithPackage.replace('.', '/') + ".java", "");
		
		String mainClass = findFile(sourceDir, "Main.java");
		if (mainClass == null)
			throw new RuntimeException("Can't find Main.java");
		mainClass = mainClass.replace(sourceDir, "").replace('/', '.').replace(".java", "");
		
		
		String gwtSourceLocation = sourceDir + "/" + gwtPackage.replace('.', '/') + "/";
		new File(gwtSourceLocation).mkdirs();
		new File(gwtSourceLocation + "client/").mkdirs();
		
		FileWriter writer;
		
		if (!new File(gwtSourceLocation + "client/MainScript.java").exists()) {
			writer = new FileWriter(gwtSourceLocation + "client/MainScript.java");
			writer.append(
					"package " + gwtPackage + ".client;\n" +
					"\n" +
					"import wogwt.translatable.WOGWTClientUtil;\n" +
					"\n" + 
					"import com.google.gwt.core.client.EntryPoint;\n" +
					"import com.google.gwt.user.client.Window;\n" +
					"import com.google.gwt.gen2.logging.shared.Level;\n" +
					"import com.google.gwt.gen2.logging.shared.Log;\n" +
					"\n" +
					"public class MainScript implements EntryPoint {\n" +
					"\n" +
					"   /* This class must be declared as an entry-point in the module (Application.gwt.xml). */\n" +
					"	public void onModuleLoad() {\n" +
					"		if (!WOGWTClientUtil.hostPageNameEquals(\"Main\")) {\n" +
					"			return;\n" +
					"		}\n" + 
					"\n" +		
					"		Log.installUncaughtExceptionHandler(); // this won't take affect until the next event loop\n" + 
					"		Log.setDefaultLevel(Level.ALL);\n" + 
					"		Log.finest(getClass().getName() + \": onModuleLoad\");\n" +
					"\n" +
					"		Window.alert(\"WOGWT is active!!\");\n" +
					"	}\n" + 
					"\n"+ 
					"}\n"
					);
			writer.flush();
			writer.close();
		}
		
		if (!new File(gwtSourceLocation + "Application.gwt.xml").exists()) {
			writer = new FileWriter(gwtSourceLocation + "Application.gwt.xml");
			writer.append(
					"<module>\n" +
					"	<inherits name=\"com.google.gwt.user.User\"/>\n" +
					"	<inherits name=\"wogwt.translatable.WOGWT\"/>\n" +
					"\n" +
					"	<set-property name=\"gwt.logging\" value=\"disabled\"/>\n" +
				  	"\n" +
					"	<entry-point class=\"" + gwtPackage + ".client.MainScript\"/>\n" +
					"\n" +
					"</module>\n"
					);
			writer.flush();
			writer.close();
		}
		
		if (!new File(gwtSourceLocation + "Development.gwt.xml").exists()) {
			writer = new FileWriter(gwtSourceLocation + "Development.gwt.xml");
			writer.append(
					"<module rename-to=\"" + gwtPackage + ".Application\">\n" +
					"	<inherits name=\"" + gwtPackage + ".Application\"/>\n" +
					"\n" +
					"	<set-property name=\"gwt.logging\" value=\"enabled\"/>\n" +
					"\n" +
				  	"	<set-property name=\"user.agent\" value=\"safari\"/>\n" +
				  	"	<set-property name=\"locale\" value=\"default\"/>\n" +
				  	"</module>"
					);
			writer.flush();
			writer.close();
		}
		
		String addedCode = 
			    "		registerRequestHandler(new wogwt.server.rpc.GWTRPCRequestHandler(), wogwt.server.rpc.GWTRPCRequestHandler.KEY);\n" +
			    "\n" +
				"		if (wogwt.components.WOGWTScript.isHostedMode()) {\n" + 
				"			setContextClassName(wogwt.GWTContext.class.getName());\n" +
				"		}\n" +
				"\n" +
				"		// needed for proper class loading in GWT's Hosted Mode shell\n" +
				"		com.webobjects.foundation._NSUtilities.setClassForName( " + mainClass + ".class, \"Main\" );\n";
		
		String applicationClassTextMod = applicationClassText.replaceFirst(
				"public\\s+Application\\(\\)\\s+\\{\\s+super\\(\\);",
				"public Application() {\n" +
				"		super();\n" + addedCode);
		if (applicationClassTextMod.equals(applicationClassText)) {
			applicationClassTextMod = applicationClassText.replaceFirst(
					"public\\s*Application\\(\\)\\s*\\{",
					"public Application() {\n" + addedCode);
			
			if (applicationClassTextMod.equals(applicationClassText)) {
				throw new RuntimeException("Couldn't modify Application constructor");
			}
		}
		
		FileUtils.writeStringToFile(new File(applicationClassFileName), applicationClassTextMod, "UTF-8");
		
		
		String webXMLFileName = projectDir + "/WebServerResources/WEB-INF/web.xml";
		if (!new File(webXMLFileName).exists()) {
			String webXML = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<web-app>\n" +
				"\n" +
				"<context-param>\n" +
				"	<param-name>WOAppMode</param-name>\n" +
				"	<param-value>Development</param-value>\n" +
				"</context-param>\n" +
				"\n" +
				"<context-param>\n" +
				"	<param-name>WOClasspath</param-name>\n" +
				"	<param-value>\n" +
				"		WOAINSTALLROOT/" + projectName + ".woa/Contents/Resources/Java/" + applicationClassWithPackage.replace('.', '/') + ".class\n" +
				"		WOAINSTALLROOT/" + projectName + ".woa/Contents/Resources/Java\n" +
				"	</param-value>\n" +
				"</context-param>\n" +
				"\n" +
				"<context-param>\n" +
				"	<param-name>WOApplicationClass</param-name>\n" +
				"	<param-value>" + applicationClassWithPackage + "</param-value>\n" +
				"</context-param>\n" +
				"\n" +
			    "<context-param>\n" +
			    "   <param-name>org.mortbay.jetty.servlet.SessionURL</param-name>\n" + 
			    "   <param-value>none</param-value>\n" + 
			    "</context-param>\n" + 
			    "\n" +
				"<servlet>\n" +
				"   <servlet-name>woServlet</servlet-name>\n" +
				"   <servlet-class>er.extensions.jspservlet.ERXServletAdaptor</servlet-class>\n" +
			    "</servlet>\n" +
			    "\n" +
			    "<servlet-mapping>\n" +
				"   <servlet-name>woServlet</servlet-name>\n" +
				"   <url-pattern>/" + projectName + "/WebObjects/*</url-pattern>\n" +
			    "</servlet-mapping>\n" +
				"</web-app>\n";
			
			new File(webXMLFileName).getParentFile().mkdirs();
			FileUtils.writeStringToFile(new File(webXMLFileName), webXML, "UTF-8");
		}
		
		if (new File(projectDir + "/woproject/wsresources.include.patternset").exists()) {
			String wsResourcesIncludeText = FileUtils.readFileToString(new File(projectDir + "/woproject/wsresources.include.patternset"));
			if (wsResourcesIncludeText.indexOf("WebServerResources/**/*") == -1) {
				System.out.println(projectDir + "/woproject/wsresources.include.patternset should have the line: 'WebServerResources/**/*'");
			}
		} else {
			System.out.println("Can't find file: wsresources.include.patternset");
		}
		
		if (new File(projectDir + "/build.xml").exists()) {
			String buildXMLText = FileUtils.readFileToString(new File(projectDir + "/build.xml"));
			String buildXMLAdditions = 
				"	<property name=\"gwtpackage\" value=\"" + gwtPackage + "\"/>\n" +
				"\n" +
				"	<import file=\"/Library/Frameworks/WOGWT.framework/Resources/wogwt-build.xml\"/>\n" +
				"\n" +
				"	<target name=\"gwt-compile-dev\" depends=\"init.properties\">\n" +
				"		<gwtCompile module=\"${gwtpackage}.Development\"\n" +
				"			renamedto=\"${gwtpackage}.Application\" \n" +
				"			style=\"PRETTY\" \n" +
				"			draftCompile=\"true\" />\n" +
				"	</target>\n" +
				"\n" +
				"	<target name=\"gwt-compile\" depends=\"init.properties\">\n" +
				"		<gwtCompile module=\"${gwtpackage}.Application\" forceBuild=\"true\"/>\n" +
				"	</target>\n\n";
			
			int lastTagIndex = buildXMLText.indexOf("</project>");
			buildXMLText = 
				buildXMLText.substring(0, lastTagIndex) + "\n" +
				buildXMLAdditions +
				buildXMLText.substring(lastTagIndex);
			
			
			String buildXMLTextMod = buildXMLText.replaceFirst(
					"<target\\s+name\\s*=\\s*\"install\"\\s+depends\\s*=\\s*\"(.*)build.woapp", 
					"<target name=\"install\" depends=\"$1gwt-compile,build.woapp");
			if (buildXMLTextMod.equals(buildXMLText)) {
				System.out.println("Unabled to add gwt-compile task as dependency to install target in build.xml");
			}
			
			FileUtils.writeStringToFile(new File(projectDir + "/build.xml"), buildXMLTextMod, "UTF-8");
		} else {
			System.out.println("Can't find build.xml file");
		}
		
		String modelRelativePath = findFile(projectDir, "index.eomodeld");
		if (modelRelativePath != null) {
			modelRelativePath = new File(modelRelativePath).getParent();
			
			String clientEogenFileName = new File(modelRelativePath).getParent() + "/" + projectName + "-Client.eogen";

			modelRelativePath = modelRelativePath.replace(projectDir + "/", "");
			String clientEogenFileText = " -destination Sources -extension java -javaclient -javaTemplate _EntityDTO.java -model " + modelRelativePath + " -packagedirs -subclassDestination Sources -subclassJavaTemplate EntityDTO.java -templatedir /Library/Frameworks/WOGWT.framework/Resources -verbose -loadModelGroup";
			if (!new File(clientEogenFileName).exists()) {
				FileUtils.writeStringToFile(new File(clientEogenFileName), clientEogenFileText, "UTF-8");
			}
			
			String serverEogenFileName = new File(clientEogenFileName).getParent() + "/" + projectName + "-Server.eogen";
			String serverEogenFileText = " -destination Sources -extension java -java -javaTemplate _EntityServer.java -model " + modelRelativePath + " -packagedirs -subclassDestination Sources -subclassJavaTemplate EntityServer.java -templatedir /Library/Frameworks/WOGWT.framework/Resources -verbose -loadModelGroup";
			if (!new File(serverEogenFileName).exists()) {
				FileUtils.writeStringToFile(new File(serverEogenFileName), serverEogenFileText, "UTF-8");
			}
		}
		
		String serviceInterfaceFileName = gwtSourceLocation + "client/RPCService.java";
		String serviceInterfaceText = 
			"package " + gwtPackage + ".client;\n" +
			"\n" +
			"import wogwt.translatable.WOGWTClientUtil;\n" +
			"\n" +
			"import com.google.gwt.core.client.GWT;\n" +
			"import com.google.gwt.user.client.rpc.RemoteService;\n" +
			"import com.google.gwt.user.client.rpc.ServiceDefTarget;\n" +
			"import com.webobjects.foundation.NSArray;\n" +
			"\n" +
			"public interface RPCService extends RemoteService {\n" +
			"	\n" +
			"	public static class Util {\n" +
			"		public static RPCServiceAsync getInstance() {\n" +
			"		RPCServiceAsync service = (RPCServiceAsync)GWT.create(RPCService.class);\n" +
			"		((ServiceDefTarget)service).setServiceEntryPoint(WOGWTClientUtil.rpcUrl());\n" +
			"		return service;\n" +
			"		}\n" +
			"	}\n" +
			"\n" +
			"	// put your service methods here\n" +
			"	//public NSArray<YourEntityClient> allYourEntities();\n" +
			"\n" +
			"}";
		if (!new File(serviceInterfaceFileName).exists()) {
			FileUtils.writeStringToFile(new File(serviceInterfaceFileName), serviceInterfaceText, "UTF-8");
		}
		
		String serviceAsyncFileName = gwtSourceLocation + "client/RPCServiceAsync.java";
		String serviceAsyncText = 
			"package " + gwtPackage + ".client;\n" +
			"\n" +
			"import com.google.gwt.user.client.rpc.AsyncCallback;\n" +
			"import com.webobjects.foundation.NSArray;\n" +
			"\n" +
			"public interface RPCServiceAsync {\n" +
			"        \n" +
			"	//public void allYourEntities(AsyncCallback<NSArray<YourEntityClient>> callback);\n" +
			"\n" +
			"}";
		if (!new File(serviceAsyncFileName).exists()) {
			FileUtils.writeStringToFile(new File(serviceAsyncFileName), serviceAsyncText, "UTF-8");
		}
		
		String serviceImplPackage = applicationClassPackage + ".server.rpc";
		String serviceImplFileName = new File(applicationClassFileName).getParent() + "/server/rpc/RPCServiceImpl.java";
		String serviceImplText = 
			"package " + serviceImplPackage + ";\n" +
			"\n" +
			"import wogwt.WOGWTServerUtil;\n" +
			"import wogwt.server.rpc.WOGWTRpcService;\n" +
			"import " + gwtPackage + ".client.RPCService;\n" +
			"\n" +
			"import com.webobjects.appserver.WOContext;\n" +
			"import com.webobjects.foundation.NSArray;\n" +
			"\n" +
			"public class RPCServiceImpl extends WOGWTRpcService implements RPCService {\n" +
			"	\n" +
			"	public RPCServiceImpl(WOContext context) {\n" +
			"		super(context);\n" +
			"	}\n" +
			"	\n" +
			"	//public NSArray<YourEntityClient> allYourEntities() {\n" +
			"	//        NSArray<YourEntity> eos = YourEntity.fetchAllYourEntities(editingContext());\n" +
			"	//        return WOGWTServerUtil.toClientEOList(eos);\n" +
			"	//}\n" +
			"\n" +
			"}";
		if (!new File(serviceImplFileName).exists()) {
			FileUtils.writeStringToFile(new File(serviceImplFileName), serviceImplText, "UTF-8");
		}
		
		String propertiesFileName = findFile(projectDir, "Properties");
		if (propertiesFileName != null) {
			String propertiesText = FileUtils.readFileToString(new File(propertiesFileName));
			propertiesText = 
				"\n" +
				"wogwt.rpcImplementationPackage = " + serviceImplPackage + 
				"\n\n" + 
				propertiesText;
			
			FileUtils.writeStringToFile(new File(propertiesFileName), propertiesText, "UTF-8");
		} else {
			System.out.println("Unable to locate Properties file");
		}
		
		
		String mainHTMLFileName = findFile(projectDir, "Main.html");
		if (mainHTMLFileName != null) {
			
			String mainHTMLText = FileUtils.readFileToString(new File(mainHTMLFileName));
			if (mainHTMLText.indexOf("</body>") != -1) {
				mainHTMLText = mainHTMLText.replace(
						"</body>", 
						"<webobject name=\"WOGWTScript\"/>\n" +
						"</body>");
				FileUtils.writeStringToFile(new File(mainHTMLFileName), mainHTMLText, "UTF-8");
			}
			
			String mainWODFileName = findFile(projectDir, "Main.wod");
			if (mainWODFileName != null) {
				String mainWODText = FileUtils.readFileToString(new File(mainWODFileName));
				mainWODText += 
					"\n" +
					"WOGWTScript : WOGWTScript {\n" +
					"	module = \"" + gwtPackage + ".Application\";\n" +
					"	enableHistorySupport = \"true\";\n" +
					"}\n";
				FileUtils.writeStringToFile(new File(mainWODFileName), mainWODText, "UTF-8");
			}
			
		} else {
			System.out.println("Unable to locate Main.html");
		}
		
		String launcherFileName = projectDir + "/" + projectName + "_GWTDevMode.launch";
		String launcherText = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
			"<launchConfiguration type=\"com.google.gdt.eclipse.suite.webapp\">\n" +
			"<stringAttribute key=\"com.google.gdt.eclipse.suiteMainTypeProcessor.PREVIOUSLY_SET_MAIN_TYPE_NAME\" value=\"com.google.gwt.dev.DevMode\"/>\n" +
			"<booleanAttribute key=\"com.google.gdt.eclipse.suiteWarArgumentProcessor.IS_WAR_FROM_PROJECT_PROPERTIES\" value=\"false\"/>\n" +
			"<listAttribute key=\"com.google.gwt.eclipse.core.ENTRY_POINT_MODULES\">\n" +
			"<listEntry value=\"" + gwtPackage + ".Application\"/>\n" +
			"</listAttribute>\n" + 
			"<stringAttribute key=\"com.google.gwt.eclipse.core.URL\" value=\"http://localhost:8888/" + projectName + "/WebObjects/" + projectName + ".woa\"/>\n" + 
			"<listAttribute key=\"org.eclipse.debug.core.MAPPED_RESOURCE_PATHS\">\n" +
			"<listEntry value=\"/" + projectName + "\"/>\n" +
			"</listAttribute>\n" +
			"<listAttribute key=\"org.eclipse.debug.core.MAPPED_RESOURCE_TYPES\">\n" +
			"<listEntry value=\"4\"/>\n" +
			"</listAttribute>\n" +
			"<stringAttribute key=\"org.eclipse.jdt.launching.CLASSPATH_PROVIDER\" value=\"com.google.gwt.eclipse.core.moduleClasspathProvider\"/>\n" +
			"<stringAttribute key=\"org.eclipse.jdt.launching.MAIN_TYPE\" value=\"wogwt.GwtDevMode\"/>\n" +
			"<stringAttribute key=\"org.eclipse.jdt.launching.PROGRAM_ARGUMENTS\" value=\"-remoteUI &quot;${gwt_remote_ui_server_port}:${unique_id}&quot; -startupUrl http://localhost:8888/" + projectName + "/WebObjects/" + projectName + ".woa -logLevel INFO -port 8888 -war &quot;${workspace_loc:" + projectName + "/WebServerResources}&quot;  " + gwtPackage + ".Application\"/>\n" +
			"<stringAttribute key=\"org.eclipse.jdt.launching.PROJECT_ATTR\" value=\"" + projectName + "\"/>\n" +
			"<stringAttribute key=\"org.eclipse.jdt.launching.VM_ARGUMENTS\" value=\"-XstartOnFirstThread -d32 -Xmx256M -Dwogwt.isHostedMode=&quot;true&quot; -DWOAINSTALLROOT=&quot;${workspace_loc:" + projectName + "/build}&quot; -DWOIDE=&quot;WOLips&quot;\"/>\n" +
			"</launchConfiguration>";

		if (!new File(launcherFileName).exists()) {
			FileUtils.writeStringToFile(new File(launcherFileName), launcherText, "UTF-8");
		}
		
		String gdtPrefsFilename =  projectDir + "/.settings/com.google.gdt.eclipse.core.prefs";
		String gdtPrefsText = 
			"#Sat Apr 24 21:38:37 CDT 2010\n" +
			"eclipse.preferences.version=1\n" +
			"jarsExcludedFromWebInfLib=\n" +
			"warSrcDir=WebServerResources\n" +
			"warSrcDirIsOutput=false\n" +
			"com.google.gdt.eclipse.core.prefs\n";
		if (!new File(gdtPrefsFilename).exists()) {
			FileUtils.writeStringToFile(new File(gdtPrefsFilename), gdtPrefsText, "UTF-8");
		}
		
		
		String gwtPrefsFilename =  projectDir + "/.settings/com.google.gwt.eclipse.core.prefs";
		String gwtPrefsText = 
			"#Sat Dec 19 22:43:32 CST 2009\n" +
			"eclipse.preferences.version=1\n" +
			"entryPointModules=\n" +
			"filesCopiedToWebInfLib=gwt-servlet.jar\n";
		if (!new File(gwtPrefsFilename).exists()) {
			FileUtils.writeStringToFile(new File(gwtPrefsFilename), gwtPrefsText, "UTF-8");
		}
		
		// TODO: add JavaWOJSPServlet and WOGWT to project classpath
	}
	
	public static String findFile(String rootDir, final String filename) {
		Collection files = FileUtils.listFiles(new File(rootDir), new IOFileFilter() {
			public boolean accept(File file) {
				if (file.getAbsolutePath().indexOf("/build/") != -1)
					return false;
				return file.getName().equals(filename);
			}
			public boolean accept(File dir, String name) {
				if (dir.getAbsolutePath().indexOf("/build/") != -1)
					return false;
				return name.equals(filename);
			}
		}, TrueFileFilter.INSTANCE);
		
		if (files.isEmpty())
			return null;
		else
			return ((File) files.iterator().next()).getAbsolutePath();
	}
	
}
