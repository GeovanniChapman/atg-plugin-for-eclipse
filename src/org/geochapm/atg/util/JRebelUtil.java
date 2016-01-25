package org.geochapm.atg.util;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.swt.custom.BusyIndicator;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * 
 * @author geovanni.chapman
 *
 */
public class JRebelUtil {

	/**
	 * Generate the rebel.xml files in the current project.
	 */
	public static void generateJRebelXMLInProject() {
		
		BusyIndicator.showWhile(SWTUtil.getCurrentDisplay(), new Runnable() {
			
			@Override
			public void run() {
				try {
					
					//create atg core directories
					ATGUtil.createATGCoreDirectories(CommonUtil.getAbsolutePathCurrentProjet());
					//refresh the current eclipse project to take changes
					CommonUtil.refreshCurrentProject();
					
					Set<IClasspathEntry> classpathEntries = CommonUtil.getClasspathEntriesOfCurrentJavaProjectByEntryKind(IClasspathEntry.CPE_SOURCE);
					
					String defaultOutputLocationAbsolutePath = CommonUtil.getAbsolutePathOfClassPathDefaultOutputLocation();
					IPath outputLocationIPath = null;
					String outputLocation = null;
					
					for (IClasspathEntry entry : classpathEntries) {
						outputLocationIPath = entry.getOutputLocation();
						if (outputLocationIPath == null) {
							outputLocation = defaultOutputLocationAbsolutePath;
						} else {
							outputLocation = CommonUtil.getAbsolutePathByIPath(outputLocationIPath) ;
							if (outputLocation == null) {
								outputLocation = defaultOutputLocationAbsolutePath;
							}
						}
						Element classpath = new Element("classpath");
						Element dir = new Element("dir");
						dir.setAttribute("name", outputLocation);
						classpath.addContent(dir);
						
						XMLUtil.writeXMLFile(getJRebelXMLBody(classpath), CommonUtil.getClasspathEntryAbsolutePaths(entry) + File.separator + "rebel.xml");
					}
					
					for (String path : FileUtil.findDirectories(CommonUtil.getAbsolutePathCurrentProjet(), "WEB-INF")) {
						outputLocation = new File(path).getParent();
						Element web = new Element("web");
						Element link = new Element("link");
						Element dir = new Element("dir");
						link.setAttribute("target", "/");
						dir.setAttribute("name", outputLocation);
						link.addContent(dir);
						web.addContent(link);
						
						XMLUtil.writeXMLFile(getJRebelXMLBody(web), path + File.separator + "classes" + File.separator + "rebel.xml");
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		
	} 
	
	/**
	 * Define the rebel.xml file template and receive the bodyNode Element parameter.
	 * @param node Element that contains the template body.
	 * @return Document obj that contains the base template of the rebel.xml file.
	 */
	public static XMLBody getJRebelXMLBody(Element bodyNode) {
		return new XMLBody() {
			@Override
			public Document getXMLBody() {
				Namespace nsDefauld = Namespace.getNamespace("http://www.zeroturnaround.com");
				Element root = new Element("application", nsDefauld);
				Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
				root.addNamespaceDeclaration(xsi);
				root.setAttribute("schemaLocation","http://www.zeroturnaround.com http://www.zeroturnaround.com/alderaan/rebel-2_0.xsd", xsi);
				Document doc = new Document(root);
				if (bodyNode !=  null) {
					class InnerGetJRebelXMLBody {
						public Element createElement(Element node, Namespace ns) {
							Element newNode = new Element(node.getName(), ns);
							@SuppressWarnings("unchecked")
							List<Attribute> attributes = node.getAttributes();
							for (Attribute attribute : attributes) {
								newNode.setAttribute(attribute.getName(), attribute.getValue());
							}
							@SuppressWarnings("unchecked")
							List<Element> elements = node.getChildren();
							for (Element element : elements) {
								newNode.addContent(createElement(element, nsDefauld));
							}
							return newNode;
						}
					}
					root.addContent(new InnerGetJRebelXMLBody().createElement(bodyNode, nsDefauld));
				}
				return doc;
			}
		};
	}
}
