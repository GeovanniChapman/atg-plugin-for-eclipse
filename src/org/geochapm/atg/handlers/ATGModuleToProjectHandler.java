package org.geochapm.atg.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.geochapm.atg.windows.ATGModuleToProjectWindows;

/**
 * 
 * @author geovanni.chapman
 *
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ATGModuleToProjectHandler extends AbstractHandler {
	
	protected Shell shlAtgModuleTo;
	
	/**
	 * The constructor.
	 */
	public ATGModuleToProjectHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			ATGModuleToProjectWindows window = new ATGModuleToProjectWindows();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
