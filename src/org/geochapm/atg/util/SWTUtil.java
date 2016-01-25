package org.geochapm.atg.util;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * @author geovanni.chapman
 *
 */
public class SWTUtil {
	
	public static void setCenterLocationToShell(Shell shell) {
		Monitor primary = getCurrentDisplay().getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = shell.getBounds();
	    
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    
	    shell.setLocation(x, y);
	}
	
	public static Display getCurrentDisplay() {
	  Display display = Display.getCurrent();
	  //may be null if outside the UI thread
	  if (display == null)
	     display = Display.getDefault();
	  return display;		
	}
}
