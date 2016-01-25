package org.geochapm.atg.windows;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.geochapm.atg.constant.MessageConstant;
import org.geochapm.atg.exceptions.ATGException;
import org.geochapm.atg.util.ATGUtil;
import org.geochapm.atg.util.CommonUtil;
import org.geochapm.atg.util.ConfigurationUtil;
import org.geochapm.atg.util.SWTUtil;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class GeneralSettingWindows {

	protected Shell shlGeneralSetting;
	private Text txtAtgRoot;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			GeneralSettingWindows window = new GeneralSettingWindows();
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
		shlGeneralSetting.open();
		shlGeneralSetting.layout();
		while (!shlGeneralSetting.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlGeneralSetting = new Shell();
		shlGeneralSetting.setSize(443, 117);
		shlGeneralSetting.setText("General Setting");
		shlGeneralSetting.setLayout(new FormLayout());
		SWTUtil.setCenterLocationToShell(shlGeneralSetting);

		Label lblAtgRoot = new Label(shlGeneralSetting, SWT.NONE);
		lblAtgRoot.setFont(SWTResourceManager.getFont(".SF NS Text", 11, SWT.NORMAL));
		FormData fd_lblAtgRoot = new FormData();
		fd_lblAtgRoot.top = new FormAttachment(0, 30);
		lblAtgRoot.setLayoutData(fd_lblAtgRoot);
		lblAtgRoot.setText("ATG ROOT");
		
		txtAtgRoot = new Text(shlGeneralSetting, SWT.BORDER);
		fd_lblAtgRoot.right = new FormAttachment(100, -349);
		txtAtgRoot.setEditable(false);
		txtAtgRoot.setFont(SWTResourceManager.getFont(".SF NS Text", 11, SWT.NORMAL));
		FormData fd_txtAtgRoot = new FormData();
		fd_txtAtgRoot.left = new FormAttachment(lblAtgRoot, 6);
		fd_txtAtgRoot.top = new FormAttachment(lblAtgRoot, -3, SWT.TOP);
		txtAtgRoot.setLayoutData(fd_txtAtgRoot);
		txtAtgRoot.setText(ConfigurationUtil.getAtgRoot());
		
		Button btnAtgRoot = new Button(shlGeneralSetting, SWT.NONE);
		btnAtgRoot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shlGeneralSetting);
				String path = dialog.open();
				if (path != null) {
					try {
						ATGUtil.validateATGRootPath(path);
						txtAtgRoot.setText(path);
						ConfigurationUtil.saveAtgRoot(path);
						CommonUtil.openInfoDialog(MessageConstant.SUCCESS, String.format(MessageConstant.GENERAL_SETTING_SUCCESS, path));
					} catch (ATGException e1) {
						CommonUtil.openErrorDialog(e1);
					}
				}
			}
		});
		fd_txtAtgRoot.right = new FormAttachment(100, -127);
		btnAtgRoot.setFont(SWTResourceManager.getFont(".SF NS Text", 11, SWT.NORMAL));
		FormData fd_btnAtgRoot = new FormData();
		fd_btnAtgRoot.top = new FormAttachment(lblAtgRoot, -5, SWT.TOP);
		fd_btnAtgRoot.left = new FormAttachment(txtAtgRoot, 6);
		fd_btnAtgRoot.right = new FormAttachment(100, -46);
		btnAtgRoot.setLayoutData(fd_btnAtgRoot);
		btnAtgRoot.setText("Select");

	}
}
