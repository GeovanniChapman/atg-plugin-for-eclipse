package org.geochapm.atg.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.geochapm.atg.windows.GeneralSettingWindows;

/**
 * 
 * @author geovanni.chapman
 *
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class GeneralSettingHandler extends AbstractHandler {
	
	/**
	 * The constructor.
	 */
	public GeneralSettingHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			GeneralSettingWindows window = new GeneralSettingWindows();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
