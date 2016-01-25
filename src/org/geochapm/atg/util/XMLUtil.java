package org.geochapm.atg.util;

import java.io.FileWriter;
import java.io.IOException;

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * 
 * @author geovanni.chapman
 *
 */
public class XMLUtil {

	private static final String XMLBODY_PARAM_REQUIRED_MESSAGE = "XMLBody Param is required.";
	private static final String PATH_PARAM_REQUIRED_MESSAGE = "Path Param is required.";
	
	/**
	 * Write xml file.
	 * 
	 * @param XMLBody implementation.
	 * @param path indicating the location.
	 * @throws IOException
	 */
	public static void writeXMLFile(XMLBody xmlBody, String path) throws IOException {
		if (xmlBody == null) {
			throw new IllegalArgumentException(XMLBODY_PARAM_REQUIRED_MESSAGE);
		} 
		if (path == null) {
			throw new IllegalArgumentException(PATH_PARAM_REQUIRED_MESSAGE);
		} 
		XMLOutputter xmlOutput = new XMLOutputter();
		// display nice nice
		xmlOutput.setFormat(Format.getPrettyFormat());
		//create directories if not exit
		FileUtil.createDirectories(path);
		FileUtil.deleteFile(path);
		xmlOutput.output(xmlBody.getXMLBody(), new FileWriter(path));
		CommonUtil.printMessageConsoleStream(String.format("File: %s has been created.", path));
	}
}
