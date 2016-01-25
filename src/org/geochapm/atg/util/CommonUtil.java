package org.geochapm.atg.util;

import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.handlers.HandlerUtil;
import org.geochapm.atg.exceptions.NotClasspathEntryFoundException;

/**
 * 
 * @author geovanni.chapman
 *
 */
@SuppressWarnings("restriction")
public class CommonUtil {

	private static final String IClASSPATH_ENTRY_PARAM_REQUIRED_MESSAGE = "IClasspathEntry Param is required.";

	/**
	 * this method is not used.
	 */
	@Deprecated
	public static IProject getCurrentProject(ExecutionEvent event) {
		TreeSelection selection = (TreeSelection) HandlerUtil.getActiveMenuSelection(event);
		if (selection != null && selection instanceof TreeSelection) {
			if (selection.getFirstElement() != null) {
				if (selection.getFirstElement() instanceof Project) {
					return ((Project) selection.getFirstElement()).getProject();
				} else if (selection.getFirstElement() instanceof JavaProject) {
					return ((JavaProject) selection.getFirstElement()).getProject();
				}
			}
		}
		return null;
	}
	
	/**
	 * Get the IProject obj of the current Project.
	 * 
	 * @return IProject of the current project.
	 */
	public static IProject getCurrentProject() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	    if (window != null)
	    {
	        IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
	        Object firstElement = selection.getFirstElement();
	        if (firstElement instanceof IAdaptable)
	        {
	            return (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
	            
	        }
	    }
	    return null;
	}
	
	/**
	 * Get IJavaProject obj of the current project.
	 * 
	 * @return IJavaProject of the current project.
	 */
	public static IJavaProject getCurrentJavaProject() {
		return JavaCore.create(CommonUtil.getCurrentProject());
	}
	
	/**
	 * Get an array of IClasspathEntry from the classpath file of the current project.
	 * 
	 * @return IClasspathEntry[] of the classpath of the current project.
	 * @throws JavaModelException
	 * @throws NotClasspathEntryFoundException if there is not entry in the classpath of the current project.
	 */
	public static IClasspathEntry[] getClasspathEntriesCurrentJavaProject()
			throws JavaModelException, NotClasspathEntryFoundException {
		IClasspathEntry[] classpathEntriesArray = getCurrentJavaProject().getRawClasspath();
		if (classpathEntriesArray.length == 0) {
			throw new NotClasspathEntryFoundException(NotClasspathEntryFoundException.DEFAULT_MESSAGE);
		}
		return classpathEntriesArray;
	}
	
	/**
	 * Get entries that matching with the entryKind parameter from classpath of the current project .
	 * 
	 * @param entryKind kind of entry
	 * @return Set<IClasspathEntry> of entries that matching with the entryKind parameter. 
	 * @throws JavaModelException
	 * @throws NotClasspathEntryFoundException if does not find or match any entries in the classpath of the current project.
	 */
	public static Set<IClasspathEntry> getClasspathEntriesOfCurrentJavaProjectByEntryKind(int entryKind) throws JavaModelException, NotClasspathEntryFoundException {
		IClasspathEntry[] classpathEntriesArray = CommonUtil.getClasspathEntriesCurrentJavaProject();
		Set<IClasspathEntry> classpathEntrySet = new HashSet<>();
		for (IClasspathEntry classpathEntry : classpathEntriesArray) {
			if (classpathEntry.getEntryKind() == entryKind) {
				classpathEntrySet.add(classpathEntry);
			}
		}
		if (classpathEntrySet.size() == 0) {
			throw new NotClasspathEntryFoundException(String.format(
					NotClasspathEntryFoundException.NOT_CLASSPATH_ENTRY_FOUND_BY_ENTRY_KIND_MESSAGE, entryKind));
		}
		return classpathEntrySet;
	}
	
	/**
	 * Get the absolute path of the classpath entry.
	 * 
	 * @param classpathEntry IClasspathEntry obj
	 * @return String with absolute path.
	 */
	public static String getClasspathEntryAbsolutePaths(IClasspathEntry classpathEntry) {
		if (classpathEntry == null) {
			throw new IllegalArgumentException(IClASSPATH_ENTRY_PARAM_REQUIRED_MESSAGE);
		}
		return getAbsolutePathByIPath(classpathEntry.getPath());
	}

	/**
	 * Get absolute path of IPath obj.
	 * 
	 * @param IPath obj.
	 * @return String with absolute path.
	 */
	public static String getAbsolutePathByIPath(IPath cpPath) {
		String path = null;
		if (cpPath != null) {
			org.eclipse.core.resources.IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			IResource res = workspaceRoot.findMember(cpPath);
			// If res is null, the path is absolute (it's an external jar)
			if (res == null) {
				if (cpPath.toFile().exists()) {
					path = cpPath.toOSString();
				}
			} else {
				// It's relative
				path = res.getLocation().toOSString();
			}
		}
		return path;
	}
	
	/**
	 * @deprecated
	 * this method is not used.
	 */
	public static String getAbsolutePathWorkspace() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
	}
	
	/**
	 * Return String with default output location of the classpath.
	 * 
	 * @return String with default output location of the classpath.
	 * @throws JavaModelException
	 */
	public static String getAbsolutePathOfClassPathDefaultOutputLocation() throws JavaModelException {
		return CommonUtil.getAbsolutePathByIPath(CommonUtil.getCurrentJavaProject().getOutputLocation());
	}

	/**
	 * Return String with absolute path of the current project.
	 * 
	 * @return String with absolute path of the current project.
	 * @throws JavaModelException
	 */
	public static String getAbsolutePathCurrentProjet() throws JavaModelException {
		return CommonUtil.getAbsolutePathByIPath(CommonUtil.getCurrentJavaProject().getPath());
	}

	/**
	 * Return the MessageConsole obj that matches with name parameter.
	 * <ol>
	 * 	<li>Find console by name</li>
	 *  <li>If there is not console by that name create a new one</li>
	 * </ol>
	 * 
	 * @param name of console.
	 * @return MessageConsole
	 */
	private static MessageConsole findConsole(String name) {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView(IConsoleConstants.ID_CONSOLE_VIEW, null, IWorkbenchPage.VIEW_VISIBLE);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	/**
	 * Return MessageConsoleStream obj specialized for each message type ex: Info, Error...  
	 * 
	 * @param MessageType kind of MessageConsoleStream
	 * @return MessageConsoleStream
	 */
	private static MessageConsoleStream getConsoleStream(MessageType type) {
		int color = SWT.COLOR_BLACK;
		int font = SWT.NORMAL;

		switch (type) {
		case ERROR:
			color = SWT.COLOR_RED;
			break;
		case WARNING:
			color = SWT.COLOR_DARK_MAGENTA;
			break;
		case INFO:
			color = SWT.COLOR_DARK_GRAY;
			font = SWT.BOLD | SWT.ITALIC;
			break;
		default:
			break;
		}

		MessageConsole myConsole = findConsole("Console");
		MessageConsoleStream msgConsoleStream = myConsole.newMessageStream();
		msgConsoleStream.setFontStyle(font);
		msgConsoleStream.setColor(Display.getDefault().getSystemColor(color));
		return msgConsoleStream;
	}
	
	/**
	 * Print an error message on the eclipse console.
	 * 
	 * @param message String
	 */
	public static void printErrorMessageConsoleStream(String message) {
		MessageConsoleStream out = getConsoleStream(MessageType.ERROR);
		out.println(message);
	}

	/**
	 * Print a warning message on the eclipse console.
	 * 
	 * @param message String
	 */
	public static void printWarningMessageConsoleStream(String message) {
		MessageConsoleStream out = getConsoleStream(MessageType.WARNING);
		out.println(message);
	}

	/**
	 * Print an info message on the eclipse console.
	 * 
	 * @param message String
	 */
	public static void printInfoMessageConsoleStream(String message) {
		MessageConsoleStream out = getConsoleStream(MessageType.INFO);
		out.println(message);
	}

	/**
	 * Print a general message on the eclipse console.
	 * 
	 * @param message String
	 */
	public static void printMessageConsoleStream(String message) {
		MessageConsoleStream out = getConsoleStream(MessageType.NONE);
		out.println(message);
	}

	/**
	 * Open an info dialog.
	 * 
	 * @param title String with title
	 * @param message String with message
	 */
	public static void openInfoDialog(String title, String message) {
		Shell shell = new Shell();
		SWTUtil.setCenterLocationToShell(shell);
		MessageDialog.openInformation(shell, title, message);
	}
	
	/**
	 * Open an error dialog.
	 * 
	 * @param t Throwable
	 */
	public static void openErrorDialog(Throwable t) {
		List<Status> childStatuses = new ArrayList<>();
		StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();

		for (StackTraceElement stackTrace : stackTraces) {
			Status status = new Status(IStatus.ERROR, "ATG_Commerce", stackTrace.toString());
			childStatuses.add(status);
		}

		MultiStatus ms = new MultiStatus("ATG_Commerce", IStatus.ERROR, childStatuses.toArray(new Status[] {}),
				t.getMessage(), t);
		Shell shell = new Shell();
		SWTUtil.setCenterLocationToShell(shell);
		ErrorDialog.openError(shell, "Error", "This is an error", ms);
	}

	/**
	 * Refresh the current project to take the changes ex: create a new file one.
	 * 
	 * @throws CoreException
	 */
	public static void refreshCurrentProject() throws CoreException {
		IProject project = getCurrentProject();
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
	}
	
	/**
	 * this method is not used.
	 * 
	 * @param projectName
	 * @param moduleSet
	 * @throws CoreException
	 */
	@Deprecated
	public static void createProject(String projectName, Set<Module> moduleSet) throws CoreException {

		IProgressMonitor progressMonitor = new NullProgressMonitor();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		project.create(progressMonitor);
		project.open(progressMonitor);

		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });

		project.setDescription(description, progressMonitor);
		IJavaProject javaProject = JavaCore.create(project);

		Set<IClasspathEntry> classpathEntries = new HashSet<>();

		// default class path
		classpathEntries.add(JavaCore.newSourceEntry(project.getFullPath().append("src")));
		classpathEntries.add(JavaRuntime.getDefaultJREContainerEntry());

		// atg class path library
		for (Module module : moduleSet) {
			List<String> libs = module.getLibs();
			if (libs != null) {
				for (String lib : libs) {
					String pathCP = module.getPath() + File.separator + lib;
					System.out.println("Classpath " + pathCP);
					if (FileUtil.existsPath(pathCP)) {
						classpathEntries.add(JavaCore.newLibraryEntry(new Path(pathCP), null, null));
					}
				}
			}
		}
		// set the build path
		IClasspathEntry[] buildPath = classpathEntries.toArray(new IClasspathEntry[classpathEntries.size()]);
		/*
		 * { JavaCore.newSourceEntry(project.getFullPath().append("src")),
		 * JavaRuntime.getDefaultJREContainerEntry() };
		 */

		javaProject.setRawClasspath(buildPath, project.getFullPath().append("bin"), progressMonitor);

		// if not exist
		IFolder sourceFolder = project.getFolder("src");
		sourceFolder.create(true, true, progressMonitor);
	}
}
