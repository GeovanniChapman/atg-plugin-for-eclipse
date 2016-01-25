package org.geochapm.atg.constant;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author geovanni.chapman
 *
 */
public class DefaultSetting {

	public static final String RELATIVE_SRC_DIR =  "src" + File.separator + "Java";
	
	public static final String RELATIVE_CONFIG_DIR =  "src" + File.separator + "config";
	
	public static final String BUILD_CONFIG_DIR =  "config";
	
	public static final String CLASSES_DIR =  "classes";
	
	public static final String CLASSES_JAR = "lib" + File.separator + "classes.jar";
	
	public static final String CONFIG_JAR = BUILD_CONFIG_DIR + File.separator + "config.jar";
	
	public static final List<String> ATG_REQUIRED_DEPENDENCE_MODULES = Arrays.asList("DCS.ADC", "DAF.Endeca.Assembler", "DAF.Endeca.Index", "DPS-UI", "SiteAdmin.Versioned");
	
}
