package org.geochapm.atg.util;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * 
 * @author geovanni.chapman
 *
 */
public class ConfigurationUtil {

	private static String NODE = "org.geochapm.atg.preferences";
	private static String KEY_ATG_ROOT = "atgRoot";
	public static String DEFAULT_PREFERENCE_VALUE = "";
	
	/**
	 * Save the plugin configuration in InstanceScope
	 * 
	 * @param key
	 * @param value
	 */
	private static void saveConfiguration(String key, String value) {
		try {
	    	Preferences preferences = InstanceScope.INSTANCE.getNode(NODE);
	    	preferences.put(key, value);
	    	// forces the application to save the preferences
    		preferences.flush();
    	} catch (Error | BackingStoreException e) {
    		e.printStackTrace();
    	}
    }
	
	/**
	 * Get the plugin configuration from InstanceScope
	 * 
	 * @param key
	 * @return
	 */
	public static String getConfiguration(String key) {
		try {
			Preferences preferences = InstanceScope.INSTANCE.getNode(NODE);
			return preferences.get(key, DEFAULT_PREFERENCE_VALUE);
		} catch (Error e) {
			e.printStackTrace();
		}
		return DEFAULT_PREFERENCE_VALUE;
	}
	
	/**
	 * Save the ATG root path in InstanceScope
	 * 
	 * @param path
	 */
	public static void saveAtgRoot(String value) {
		saveConfiguration(KEY_ATG_ROOT, value);
		CommonUtil.printMessageConsoleStream("ATG ROOT has been saved.");
    }
	
	/**
	 * Return the ATG root path from InstanceScope configuration file
	 * 
	 * @return String of ATG root path
	 */
	public static String getAtgRoot() {
		return getConfiguration(KEY_ATG_ROOT);
    }
}
