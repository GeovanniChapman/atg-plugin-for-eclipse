package org.geochapm.atg.handlers;

import org.eclipse.core.commands.AbstractHandler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.geochapm.atg.constant.MessageConstant;
import org.geochapm.atg.util.CommonUtil;
import org.geochapm.atg.util.JRebelUtil;

/**
 * 
 * @author geovanni.chapman
 * 
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class JRebelGenerateXMLHandler extends AbstractHandler {
	
	/**
	 * The constructor.
	 */
	public JRebelGenerateXMLHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			JRebelUtil.generateJRebelXMLInProject();
		    CommonUtil.openInfoDialog(MessageConstant.SUCCESS, MessageConstant.JREBEL_GENERATE_XML_SUCCESS);
		} catch (Exception e) {
			CommonUtil.openErrorDialog(e);
			e.printStackTrace();
		}
		return null;
	}
}
