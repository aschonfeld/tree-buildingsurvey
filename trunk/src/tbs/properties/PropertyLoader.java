package tbs.properties;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class PropertyLoader {

	public static Class<?> loaderLocation;
	private static Map<String, Properties> propertiesMap;
	private static String propertyLocation = "/tbs/properties/{0}.properties";

	public PropertyLoader() {
		propertiesMap = new TreeMap<String, Properties>();
	}

	public static Properties getProperties(String filename) {
		Properties returnProperties;
		if (propertiesMap == null || !propertiesMap.containsKey(filename)) {
			if (propertiesMap == null)
				propertiesMap = new HashMap<String, Properties>();
			returnProperties = loadPropertyFile(filename);
			propertiesMap.put(filename, returnProperties);
		} else
			returnProperties = propertiesMap.get(filename);
		return returnProperties;
	}

	public static Properties loadPropertyFile(String filename) {
		Properties props = new Properties();
		try {
			props.load(loaderLocation.getResource(
					MessageFormat.format(propertyLocation, filename)).openStream());
			return props;
		} catch (Exception e) {
			System.out.println(new StringBuffer("Unable to load ").append(
					filename).append(": ").append(e).toString());
			return new Properties();
		}
	}
}
