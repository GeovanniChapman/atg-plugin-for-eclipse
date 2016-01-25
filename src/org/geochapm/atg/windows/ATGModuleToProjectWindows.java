package org.geochapm.atg.windows;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.geochapm.atg.constant.DefaultSetting;
import org.geochapm.atg.constant.MessageConstant;
import org.geochapm.atg.exceptions.ATGException;
import org.geochapm.atg.util.ATGUtil;
import org.geochapm.atg.util.CommonUtil;
import org.geochapm.atg.util.ConfigurationUtil;
import org.geochapm.atg.util.SWTUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridLayout;

public class ATGModuleToProjectWindows {

	protected Shell shlAtgModuleTo;
	protected Button btnImportProjectIf;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ATGModuleToProjectWindows window = new ATGModuleToProjectWindows();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlAtgModuleTo.open();
		shlAtgModuleTo.layout();
		while (!shlAtgModuleTo.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlAtgModuleTo = new Shell();
		shlAtgModuleTo.setSize(419, 304);
		shlAtgModuleTo.setText("ATG Module to Project");
		shlAtgModuleTo.setLayout(new FormLayout());
		SWTUtil.setCenterLocationToShell(shlAtgModuleTo);
		
		Label lblSelectATGModule = new Label(shlAtgModuleTo, SWT.NONE);
		FormData fd_lblSelectATGModule = new FormData();
		fd_lblSelectATGModule.bottom = new FormAttachment(100, -41);
		fd_lblSelectATGModule.left = new FormAttachment(0, 10);
		lblSelectATGModule.setLayoutData(fd_lblSelectATGModule);
		lblSelectATGModule.setText("Select the ATG Module");
		
		Button btnSelectATGModule = new Button(shlAtgModuleTo, SWT.NONE);
		btnSelectATGModule.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					ATGUtil.validateExistATGRootPath();
					String atgRoot = ConfigurationUtil.getAtgRoot();
					DirectoryDialog dialog = new DirectoryDialog(new Shell());
				    dialog.setFilterPath(atgRoot); 
				    String modulePath = dialog.open();
				    if (modulePath != null) {
			    		ATGUtil.transformATGModuleToProject(modulePath, btnImportProjectIf.getSelection());
			    		CommonUtil.openInfoDialog(MessageConstant.SUCCESS, String.format(MessageConstant.ATG_MODULE_TO_PROJECT_SUCCESS, ATGUtil.getModuleName(modulePath)));
				    }
				} catch (ATGException | InvocationTargetException | InterruptedException | CoreException | IOException e1) {
					CommonUtil.openErrorDialog(e1);
				}
			}
		});
		FormData fd_btnSelectATGModule = new FormData();
		fd_btnSelectATGModule.bottom = new FormAttachment(100, -34);
		fd_btnSelectATGModule.top = new FormAttachment(lblSelectATGModule, -7, SWT.TOP);
		fd_btnSelectATGModule.left = new FormAttachment(lblSelectATGModule, 6);
		btnSelectATGModule.setLayoutData(fd_btnSelectATGModule);
		btnSelectATGModule.setText("Select");
		
		btnImportProjectIf = new Button(shlAtgModuleTo, SWT.CHECK);
		FormData fd_btnImportProjectIf = new FormData();
		fd_btnImportProjectIf.bottom = new FormAttachment(btnSelectATGModule, -22);
		fd_btnImportProjectIf.left = new FormAttachment(lblSelectATGModule, 0, SWT.LEFT);
		btnImportProjectIf.setLayoutData(fd_btnImportProjectIf);
		btnImportProjectIf.setText("Import the project if it has not been imported yet.");
		
		Group grpConfiguration = new Group(shlAtgModuleTo, SWT.NONE);
		grpConfiguration.setText("Configuration");
		grpConfiguration.setLayout(new GridLayout(2, false));
		FormData fd_grpConfiguration = new FormData();
		fd_grpConfiguration.bottom = new FormAttachment(btnImportProjectIf, -6);
		fd_grpConfiguration.left = new FormAttachment(0, 10);
		fd_grpConfiguration.right = new FormAttachment(100, -10);
		fd_grpConfiguration.top = new FormAttachment(0, 10);
		grpConfiguration.setLayoutData(fd_grpConfiguration);
		
		Label lblSrcDirectory = new Label(grpConfiguration, SWT.NONE);
		lblSrcDirectory.setText("Src Directory:");
		
		Label lblRelativeSrcDir = new Label(grpConfiguration, SWT.NONE);
		lblRelativeSrcDir.setText(DefaultSetting.RELATIVE_SRC_DIR);
		
		Label lblOutputSrcDirectory = new Label(grpConfiguration, SWT.NONE);
		lblOutputSrcDirectory.setText("Output Src Directory:");
		
		Label lblClassesDir = new Label(grpConfiguration, SWT.NONE);
		lblClassesDir.setText(DefaultSetting.CLASSES_DIR);
		
		Label lblClassesjarDirectory = new Label(grpConfiguration, SWT.NONE);
		lblClassesjarDirectory.setText("classes.jar Directory:");
		
		Label lblClassesJar = new Label(grpConfiguration, SWT.NONE);
		lblClassesJar.setText(DefaultSetting.CLASSES_JAR);
		
		Label lblConfigDirectory = new Label(grpConfiguration, SWT.NONE);
		lblConfigDirectory.setText("Config Directory:");
		
		Label lblRelativeConfigDir = new Label(grpConfiguration, SWT.NONE);
		lblRelativeConfigDir.setText(DefaultSetting.RELATIVE_CONFIG_DIR);
		
		Label lblOutputConfigDirectory = new Label(grpConfiguration, SWT.NONE);
		lblOutputConfigDirectory.setText("Output Config Directory:");
		
		Label lblBuildConfigDir = new Label(grpConfiguration, SWT.NONE);
		lblBuildConfigDir.setText(DefaultSetting.RELATIVE_CONFIG_DIR);
		
		Label lblConfigjarDirectory = new Label(grpConfiguration, SWT.NONE);
		lblConfigjarDirectory.setText("config.jar Directory:");
		
		Label lblConfigJar = new Label(grpConfiguration, SWT.NONE);
		lblConfigJar.setText(DefaultSetting.CONFIG_JAR);

	}
}
