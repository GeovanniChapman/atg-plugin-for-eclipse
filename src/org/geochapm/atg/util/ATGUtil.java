package org.geochapm.atg.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.geochapm.atg.constant.DefaultSetting;
import org.geochapm.atg.exceptions.ATGException;
import org.geochapm.atg.exceptions.NotPassValidationATGException;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

/**
 * 
 * @author geovanni.chapman
 *
 */
public class ATGUtil {

	private static Set<String> MODULE_BASE_PATH_SET;
	
	/**
	 * It is used for test
	 * @param args
	 * @throws NotPassValidationATGException
	 * @throws CoreException
	 * @throws IOException
	 */
	public static void main(String[] args) throws NotPassValidationATGException, CoreException, IOException {
		
		try {
			ATGUtil.validateExistATGRootPath();
			String atgRoot = ConfigurationUtil.getAtgRoot();
			DirectoryDialog dialog = new DirectoryDialog(new Shell());
		    dialog.setFilterPath(atgRoot); 
		    String path = dialog.open();
		    if (path != null) {
	    		ATGUtil.transformATGModuleToProject(path, false);
		    }
		} catch (ATGException | InvocationTargetException | InterruptedException e1) {
			CommonUtil.openErrorDialog(e1);
		}
	}
	
	/**
	 * Transform the ATG module to eclipse project
	 * 
	 * @param modulePath the absolute path where is located the ATG module.
	 * @param isImportProjectChecked if this option is true the eclipse project will be imported.
	 * @throws NotPassValidationATGException
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 * @throws CoreException
	 * @throws IOException
	 */
	static public void transformATGModuleToProject(String modulePath, boolean isImportProjectChecked) throws NotPassValidationATGException, InvocationTargetException, InterruptedException, CoreException, IOException {
		
		validateExistATGRootPath();
		validateATGModulePath(modulePath);
		indexingATGModules();
		createProject(modulePath, getDependeceModules(getModuleName(modulePath)));
		if (isImportProjectChecked) {
			importProject(modulePath);
		}
		
	}
	
	/**
	 * Run the task that allows us to get the roots modules path.
	 * 
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	public static void indexingATGModules() throws InvocationTargetException, InterruptedException {
		
		if (!isRequiredIndexing()) return;
		
		class InnerIndexingATGModules implements IRunnableWithProgress {
			private final String[] TASK_NAMES = new String[] { "Indexing ATG Modules" };
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				monitor.beginTask("Indexing ATG Modules", TASK_NAMES.length);
				
	            for(int i = 0; i < TASK_NAMES.length; i++)
	            {
	                monitor.subTask(String.format("%s %s of %s...", TASK_NAMES[i], (i+1), TASK_NAMES.length));
	                if (TASK_NAMES[0].equals(TASK_NAMES[i])) {
	                	try {
							getModuleBasePaths();
						} catch (NotPassValidationATGException e) {
							throw new InterruptedException(e.getMessage());
						}
	                }
	                // Tell the monitor that you successfully finished one item of "workload"-many
	                monitor.worked(1);
	                // Check if the user pressed "cancel"
	                /*if(monitor.isCanceled())
	                {
	                    monitor.done();
	                    return;
	                }*/
	            }
				monitor.done();
			}
		}
		Shell shell = new Shell();
		SWTUtil.setCenterLocationToShell(shell);
        new ProgressMonitorDialog(shell).run(true, false, new InnerIndexingATGModules());
         
	}
	
	/**
	 * Given a moduleName get recursively all the classpath dependencies of modules required
	 * 
	 * @param moduleName ATG module name 
	 * @return
	 * @throws NotPassValidationATGException
	 */
	public static Set<Module> getDependeceModules(String moduleName) throws NotPassValidationATGException {
		Set<Module> dependeceModules = new HashSet<Module>();
		class InnerDependeceModules {
			public void calculateDependeceModules(String moduleName, Set<Module> dependeceModules) throws NotPassValidationATGException {
				
				String modulePath = getModulePathByModuleName(moduleName);
				if (modulePath != null) {
					Attributes manifestAttributes = getManifestAttributes(modulePath);
					if (manifestAttributes != null) {
						String atgRequired = manifestAttributes.getValue("ATG-Required");
						String libs = manifestAttributes.getValue("ATG-Class-Path");
						List<String> libsList = ((libs != null) ? Arrays.asList(libs.split(" ")) : null);
						Module module = new Module(moduleName, modulePath, libsList);
						if (!dependeceModules.contains(module)) {
							dependeceModules.add(module);
							if (atgRequired != null) {
								for (String moduleN : atgRequired.split(" ")) {
									calculateDependeceModules(moduleN, dependeceModules);
								}
							}
						}
					}
				}
			}
		}
		new InnerDependeceModules().calculateDependeceModules(moduleName, dependeceModules);
		for (String module : DefaultSetting.ATG_REQUIRED_DEPENDENCE_MODULES) {
			new InnerDependeceModules().calculateDependeceModules(module, dependeceModules);
		}
		return dependeceModules;
	}
	
	/**
	 * Validate if the ATG root path is set.
	 * 
	 * @throws NotPassValidationATGException if the ATG root path is not set.
	 */
	static public void validateExistATGRootPath () throws NotPassValidationATGException {
		String atgRoot = ConfigurationUtil.getAtgRoot();
		if (ConfigurationUtil.DEFAULT_PREFERENCE_VALUE.equals(atgRoot)) {
			throw new NotPassValidationATGException(String.format(NotPassValidationATGException.ATG_ROOT_REQUIRED));
		}
		validateATGRootPath(atgRoot);
	}
	
	/**
	 * Validate if path is a valid ATG root path.
	 * 
	 * @param path ATG root path
	 * @throws NotPassValidationATGException if is not a valid ATG root path.
	 */
	static public void validateATGRootPath (String path) throws NotPassValidationATGException {
		if (path != null) {
			File file = new File(path);
			if (file.exists() && file.list().length > 0) {
				List<String> lists = Arrays.asList(file.list());
				 if (lists.contains("home") && lists.contains("DAS")) {
					 return;
				 }
			}
		}
		throw new NotPassValidationATGException(String.format(NotPassValidationATGException.PATH_IS_NOT_VALID_ATG_ROOT, path));
	}
	
	/**
	 * Validate if the ATG module path is valid
	 * 
	 * @param modulePath ATG module path
	 * @throws NotPassValidationATGException if is not a valid ATG module path.
	 */
	static public void validateATGModulePath (String modulePath) throws NotPassValidationATGException {
		if (modulePath != null) {
			File file = new File(modulePath + File.separator + "META-INF" + File.separator + "MANIFEST.MF");
			if (file.exists()) {
				return;
			}
		}
		throw new NotPassValidationATGException(String.format(NotPassValidationATGException.PATH_IS_NOT_VALID_ATG_MODULE, modulePath));
	}
	
	/**
	 * Get the module name ex: Store.EStore by the ATG module path.
	 * 
	 * @param The module Path 
	 * @return The resulting module name String
	 * @throws NotPassValidationATGException 
	 * 		   if not exist ATG Root path or ATG Module path is not valid
	 */
	static public String getModuleName(String modulePath) throws NotPassValidationATGException {
		validateExistATGRootPath();
		validateATGModulePath(modulePath);
		String atgRoot = ConfigurationUtil.getAtgRoot();
		return modulePath.replace(atgRoot + File.separator, "").replace(File.separator, ".");
	}
	/**
	 * Get the module path by passing the module name
	 * 
	 * @param moduleName
	 * @return String with module path
	 * @throws NotPassValidationATGException
	 * 		   if there is no ATG Root Path	
	 */
	static public String getModulePathByModuleName (String moduleName) throws NotPassValidationATGException {
		Set<String> moduleBasePaths = getModuleBasePaths();
		String module = moduleName.replaceAll("\\.", File.separator);
		File file = null;
		for (String basePath : moduleBasePaths) {
			file = new File(basePath + File.separator + module);
			if (file.exists()) {
				return file.getAbsolutePath();
			}
		}
		return null;
	}
	
	/**
	 * Return a Set of String with all possible base paths of the modules
	 * @return Set of String
	 * 		   With all possible base path of the modules
	 * @throws NotPassValidationATGException
	 * 		   if there is no ATG Root Path	   
	 */
	static public Set<String> getModuleBasePaths() throws NotPassValidationATGException {
		if (isRequiredIndexing() ) {
			synchronized (ATGUtil.class) {
				if (isRequiredIndexing() ) {
					validateExistATGRootPath();
					MODULE_BASE_PATH_SET =  new HashSet<>();
					String atgRoot = ConfigurationUtil.getAtgRoot();
					MODULE_BASE_PATH_SET.add(atgRoot);
					Set<File> files = FileUtil.findFiles(atgRoot, "MANIFEST.MF");
				    for (File file :  files) {
				    	String atgInstallUnit = getAttributeValueFromManifest(file, "ATG-Install-Unit");
				    	if (atgInstallUnit != null) {
				    		MODULE_BASE_PATH_SET.add(file.getParentFile().getParent());
				    	}
					}
				}
			}
		}
		return MODULE_BASE_PATH_SET;
	}
	
	/**
	 * Return true if indexing is required
	 * 
	 * @return true if indexing is required
	 */
	public static boolean isRequiredIndexing() {
		return MODULE_BASE_PATH_SET == null;
	}
	
	/**
	 * Get Attributes object from Manifest file of module path
	 * @param modulePath
	 * @return (Attributes object from Manifest) or (null if there is a IOException or there is not MANIFEST.MF file in the module)
	 * @throws NotPassValidationATGException
	 * 		   if the module path is not valid
	 */
	public static Attributes getManifestAttributes(String modulePath) throws NotPassValidationATGException {
		validateATGModulePath(modulePath);
		try {
			Manifest manifest = new Manifest(new FileInputStream(new File(modulePath + File.separator + "META-INF" + File.separator + "MANIFEST.MF")));
			return manifest.getMainAttributes();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
	}
	
	/**
	 * Return the attribute value of Manifest file
	 * 
	 * @param file of Manifest
	 * @param name of the attribute
	 * @return String with attribute value.
	 */
	public static String getAttributeValueFromManifest(File file, String name) {
		try {
			Manifest manifest = new Manifest(new FileInputStream(file));
			Attributes attrs = manifest.getMainAttributes();
			return attrs.getValue(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
	}

	/**
	 * Create .project and .classpath files to build the ATG module eclipse project in the modulePath path.
	 * 
	 * @param modulePath path
	 * @param moduleSet list of classpath dependencies
	 * @throws IOException
	 */
	private static void createProject(String modulePath, Set<Module> moduleSet) throws IOException {
		
		String projectFilePath = modulePath + File.separator + ".project";
		String classpathFilePath = modulePath + File.separator + ".classpath";
		try {
			//Create .project
			XMLUtil.writeXMLFile(new XMLBody() {
				@Override
				public Document getXMLBody() {
					Element root = new Element("projectDescription");
					Document doc = new Document(root);
					try {
						root.addContent(new Element("name").setText(getModuleName(modulePath)));
					} catch (NotPassValidationATGException e) {
						e.printStackTrace();
					}
					root.addContent(new Element("comment"));
					root.addContent(new Element("projects"));
					
					Element buildSpec = new Element("buildSpec");
					Element buildCommand = new Element("buildCommand");
					buildCommand.addContent(new Element("name").setText(JavaCore.BUILDER_ID));
					buildCommand.addContent(new Element("arguments"));
					buildSpec.addContent(buildCommand);
					root.addContent(buildSpec);
						
					Element natures = new Element("natures");	
					natures.addContent(new Element("nature").setText(JavaCore.NATURE_ID));
					root.addContent(natures);
					return doc;
				}
			}, projectFilePath);
			
			//Create .project
			XMLUtil.writeXMLFile(new XMLBody() {
				@Override
				public Document getXMLBody() {
					Element root = new Element("classpath");
					Document doc = new Document(root);
					
					
					Element classpathentry = new Element("classpathentry").setAttributes(Arrays.asList(new Attribute("kind", "src"), new Attribute("path", DefaultSetting.RELATIVE_SRC_DIR), new Attribute("output", DefaultSetting.CLASSES_DIR)));
					root.addContent(classpathentry);
					classpathentry = new Element("classpathentry").setAttributes(Arrays.asList(new Attribute("kind", "src"), new Attribute("path", DefaultSetting.RELATIVE_CONFIG_DIR), new Attribute("output", DefaultSetting.RELATIVE_CONFIG_DIR)));
					root.addContent(classpathentry);
					classpathentry = new Element("classpathentry").setAttributes(Arrays.asList(new Attribute("kind", "con"), new Attribute("path", JavaRuntime.getDefaultJREContainerEntry().getPath().toString())));
					root.addContent(classpathentry);
					classpathentry = new Element("classpathentry").setAttributes(Arrays.asList(new Attribute("kind", "output"), new Attribute("path", DefaultSetting.CLASSES_DIR)));
					root.addContent(classpathentry);
					
					// atg class path library
					for (Module module : moduleSet) {
						List<String> libs = module.getLibs();
						if (libs != null) {
							for (String lib : libs) {
								String pathCP = module.getPath() + File.separator + lib;
								System.out.println("Classpath " + pathCP);
								if (FileUtil.existsPath(pathCP)) {
									classpathentry = new Element("classpathentry").setAttributes(Arrays.asList(new Attribute("kind", "lib"), new Attribute("path", pathCP)));
									root.addContent(classpathentry);
								}
							}
						}
					}
						
					Element natures = new Element("natures");	
					natures.addContent(new Element("nature").setText(JavaCore.NATURE_ID));
					return doc;
				}
			}, classpathFilePath);
			
			//create atg core directory
			createATGCoreDirectories(modulePath);
			
		} catch (Exception e) {
			FileUtil.deleteFile(projectFilePath);
			FileUtil.deleteFile(classpathFilePath);
			if (e instanceof IOException) {
				throw e;
			}
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Import the eclipse project in modulePath path to the eclipse workspace.
	 * 
	 * @param modulePath ATG module path
	 * @throws NotPassValidationATGException
	 */
	public static void importProject(String modulePath) throws NotPassValidationATGException {
		
		validateATGModulePath(modulePath);
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		 
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					IPath projectDotProjectFile = new Path(modulePath + File.separator + ".project");
					IProjectDescription projectDescription = workspace.loadProjectDescription(projectDotProjectFile);
					IProject project = workspace.getRoot().getProject(projectDescription.getName());
					JavaCapabilityConfigurationPage.createProject(project, projectDescription.getLocationURI(),	new NullProgressMonitor());
					//project.create(null);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		};
		 
		// and now get the workbench to do the work
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().syncExec(runnable);
		
	}
	
	/**
	 * Create the default ATG module directories in midulePath path.
	 * 
	 * @param modulePath ATG module path
	 */
	public static void createATGCoreDirectories(String modulePath) {
		FileUtil.createDirectories(modulePath + File.separator + DefaultSetting.RELATIVE_SRC_DIR);
		FileUtil.createDirectories(modulePath + File.separator + DefaultSetting.RELATIVE_CONFIG_DIR);
		FileUtil.createDirectories(modulePath + File.separator + DefaultSetting.CLASSES_DIR);
		FileUtil.createDirectories(modulePath + File.separator + DefaultSetting.BUILD_CONFIG_DIR);
	}
}

/**
 * 
 * @author geovanni.chapman
 *
 *	Bean that represent an ATG module.
 */
class Module {

	private String name;
	private String path;
	private List<String> libs;
	
	public Module(String name, String path, List<String> list) {
		super();
		this.name = name;
		this.path = path;
		this.libs = list;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public List<String> getLibs() {
		return libs;
	}
	public void setLibs(List<String> libs) {
		this.libs = libs;
	}

	@Override
	public String toString() {
		return String.format("Name: %s, Libs: %s, Path: %s", name, libs, path);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Module other = (Module) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}

