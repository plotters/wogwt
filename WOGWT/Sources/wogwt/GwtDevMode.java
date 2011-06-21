package wogwt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import com.google.gwt.dev.DevMode;

import er.extensions.foundation.ERXFileUtilities;

/**
 * A wrapper around the standard Dev Mode launcher that makes things a little nicer
 * for WO apps.  Specify this class as the main class in your "Web Application" launch config to use it.
 * 
 * This class does a few things:
 * 1) ensure the correct working directory at launch
 * 2) ensure the JavaWOJSPServlet framework has been added to the classpath
 * 3) auto-generates the web.xml file if missing
 * 4) prints the initial URL for getting to the app
 * 
 * @author john
 *
 */
public class GwtDevMode {

	public static void main(String[] args) throws IOException {
		// set some necessary properties
		File projectFolder = new File("..").getCanonicalFile(); // GWT initially starts in the "war" folder which is WebServerResources

		File buildPropertiesFile = new File(projectFolder, "build.properties");
		Properties buildProperties = new Properties();
		
		if (buildPropertiesFile.exists()) {
			InputStream buildPropertiesInputStream = new FileInputStream(buildPropertiesFile);
			try {
				buildProperties.load(buildPropertiesInputStream);
			} finally {
				buildPropertiesInputStream.close();
			}
		} else {
			System.err.println("File not found: " + buildPropertiesFile.getCanonicalPath() + 
					".\nThe application should be launched from the \"war\" directory, which is WebServerResources in your project folder.\n");
			return;
		}
		
		try {
			Class.forName("com.webobjects.jspservlet.WOServletAdaptor");
		} catch (ClassNotFoundException e) {
			System.err.println("You must add the framework JavaWOJSPServlet to your project (only necessary for development)");
			return;
		}
		
		System.setProperty("WOAINSTALLROOT", new File(projectFolder, "build").getCanonicalPath());
		System.setProperty("WOIDE", "WOLips");
		String appName = buildProperties.getProperty("project.name");
		
		System.out.println("The url for GWT Development Mode connect is (probably):\n\n" + 
				"http://localhost:8888/" + appName + "/WebObjects/" + appName + ".woa?gwt.codesvr=localhost:9997 \n\n" +
				"You must open a brower manually and navigate to this url.\n");
	
		// delete the module directory from WebServerResources in case a compile was done because otherwise dev mode won't connect
		File wsr = new File(projectFolder, "WebServerResources");
//		if (args.length != 0) {
//			String module = args[args.length-1];
//			File moduleDirectary = new File(wsr, module);
//			if (moduleDirectary.exists()) {
//				ERXFileUtilities.deleteDirectory(moduleDirectary);
//			}
//		}

		File webInf = new File(wsr, "WEB-INF");
		if (!webInf.exists()) {
			webInf.mkdir();
		}
		
		File webXml = new File(webInf, "web.xml");
		if (!webXml.exists()) {
			String template = loadTextResource("web.xml", GwtDevMode.class);
			template = template.replace("${APP_NAME}", appName);
			template = template.replace("${MAIN_CLASS}", buildProperties.getProperty("principalClass"));
			String mainClassFilePath = buildProperties.getProperty("principalClass").replace('.', '/') + ".class";
			template = template.replace("${MAIN_CLASS_FILE_PATH}", mainClassFilePath);
			
			ERXFileUtilities.stringToFile(template, webXml, "UTF-8");
		}
		
		// resume normal dev mode startup
		DevMode.main(args);
	}
	
	private static String loadTextResource(String filename, Class<?> clazz) throws IOException {
        StringBuffer buffer = new StringBuffer();
        
        InputStream stream = clazz.getResourceAsStream(filename);
        try {
            Reader reader = new InputStreamReader(stream, "UTF-8");
            try {
                int character;
                while ((character = reader.read()) != -1) {
                    buffer.append((char)character);
                }
            } finally {
                reader.close();
            }
        } finally {
            stream.close();
        }
        
        return buffer.toString();
    }

}
