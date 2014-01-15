/*
 * Syncany, www.syncany.org
 * Copyright (C) 2011-2013 Philipp C. Heckel <philipp.heckel@gmail.com> 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.syncany.gui.plugin;

import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTPClient;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.syncany.gui.ApplicationResourcesManager;
import org.syncany.gui.SWTUtil;
import org.syncany.gui.UserInput;
import org.syncany.gui.WidgetDecorator;
import org.syncany.gui.panel.PluginPanel;
import org.syncany.util.I18n;

/**
 * @author Vincent Wiencek <vwiencek@gmail.com>
 *
 */
public class FtpPluginPanel extends PluginPanel {
	private static final Logger log = Logger.getLogger(FtpPluginPanel.class.getSimpleName());
	private static final int TIMEOUT_CONNECT = 5000;
	
	private Text hostText;
	private Text usernameText;
	private Text passwordText;
	private Text pathText;
	private Spinner spinner;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public FtpPluginPanel(Composite parent, int style) {
		super(parent, style);
		initComposite();
	}
	
	public void initComposite(){
		setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_composite = new GridLayout(4, false);
		setLayout(gl_composite);
		
		Label introductionTitleLabel = WidgetDecorator.label(this, SWT.WRAP).bold();
		introductionTitleLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
		introductionTitleLabel.setText(I18n.getString("plugin.ftp.introduction.title"));
		
		Label introductionLabel = WidgetDecorator.label(this, SWT.WRAP).normal();
		introductionLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 4, 1));
		introductionLabel.setText(I18n.getString("plugin.ftp.introduction"));
		
		Label hostLabel = WidgetDecorator.label(this, SWT.NONE).normal();
		GridData gd_hostLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_hostLabel.verticalIndent = ApplicationResourcesManager.VERTICAL_INDENT;
		hostLabel.setLayoutData(gd_hostLabel);
		hostLabel.setText(I18n.getString("plugin.ftp.host", true));
		
		hostText = WidgetDecorator.text(this, SWT.BORDER).normal();
		
		GridData gd_hostText = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_hostText.verticalIndent = ApplicationResourcesManager.VERTICAL_INDENT;
		hostText.setLayoutData(gd_hostText);
		
		Label portLabel = WidgetDecorator.label(this, SWT.NONE).normal();
		GridData gd_portLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_portLabel.verticalIndent = ApplicationResourcesManager.VERTICAL_INDENT;
		portLabel.setLayoutData(gd_portLabel);
		portLabel.setText(I18n.getString("plugin.ftp.port", true));
		
		spinner = new Spinner(this, SWT.BORDER);
		spinner.setPageIncrement(1);
		spinner.setMaximum(100000);
//		WidgetDecorator.decorateControl(spinner, FontDecorator.NORMAL);
		spinner.setSelection(21);
		GridData gd_spinner = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_spinner.verticalIndent = ApplicationResourcesManager.VERTICAL_INDENT;
		gd_spinner.widthHint = 50;
		gd_spinner.heightHint = 15;
		spinner.setLayoutData(gd_spinner);
		
		Label usernameLabel = WidgetDecorator.label(this, SWT.NONE).normal();
		usernameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		usernameLabel.setText(I18n.getString("plugin.ftp.username", true));
		
		usernameText = WidgetDecorator.text(this, SWT.BORDER).normal();
		usernameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		
		Label passwordLabel = WidgetDecorator.label(this, SWT.NONE).normal();
		passwordLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		passwordLabel.setText(I18n.getString("plugin.ftp.password", true));
		
		passwordText = WidgetDecorator.text(this, SWT.BORDER | SWT.PASSWORD).normal();
		passwordText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		
		Label pathLabel = WidgetDecorator.label(this, SWT.NONE).normal();
		pathLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		pathLabel.setText(I18n.getString("plugin.ftp.path", true));
		
		pathText = WidgetDecorator.text(this, SWT.BORDER).normal();
		pathText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));

		Composite buttonComposite = new Composite(this, SWT.NONE);
		GridLayout gl_buttonComposite = new GridLayout(2, false);
		gl_buttonComposite.horizontalSpacing = 0;
		gl_buttonComposite.verticalSpacing = 0;
		gl_buttonComposite.marginWidth = 0;
		gl_buttonComposite.marginHeight = 0;
		buttonComposite.setLayout(gl_buttonComposite);
		GridData gd_buttonComposite = new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1);
		gd_buttonComposite.minimumHeight = 30;
		buttonComposite.setLayoutData(gd_buttonComposite);
		
		final Label testResultLabel = new Label(buttonComposite, SWT.NONE);
		testResultLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		testResultLabel.setAlignment(SWT.CENTER);
		
		final Button testFtpButton = WidgetDecorator.button(buttonComposite, SWT.NONE).normal();

		GridData gd_testFtpButton = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_testFtpButton.heightHint = 30;
		gd_testFtpButton.widthHint = 100;
		testFtpButton.setLayoutData(gd_testFtpButton);
		testFtpButton.setText(I18n.getString("plugin.ftp.test"));
		testFtpButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				testFtpButton.setEnabled(false);
				final boolean test = testFtpConnection();
				testFtpButton.setEnabled(true);
				
				Display.getCurrent().syncExec(new Runnable() {
				    public void run() {
				    	if (test){
				    		testResultLabel.setText(I18n.getString("plugin.ftp.testSucceed"));
				    	}
				    	else{
				    		testResultLabel.setText(I18n.getString("plugin.ftp.testFails"));
				    	}
				    }
				});
			}
		});
	}

	protected boolean testFtpConnection() {
		FTPClient ftp = new FTPClient();

		ftp.setConnectTimeout(TIMEOUT_CONNECT);

		try{
			ftp.connect(hostText.getText(), Integer.parseInt(spinner.getText()));
			boolean success = ftp.login(usernameText.getText(), passwordText.getText());
			ftp.disconnect();
			return success;
		}
		catch (NumberFormatException e){
			log.warning("NumberFormatException "+e.toString());
		}
		catch (SocketException e) {
			log.warning("SocketException "+e.toString());
		}
		catch (IOException e) {
			log.warning("IOException "+e.toString());
		}
		return false;
	}

	@Override
	public UserInput getUserSelection() {
		UserInput parameters = new UserInput();
		parameters.put(SyncanyFTPParameters.FTP_HOST, hostText.getText());
		parameters.put(SyncanyFTPParameters.FTP_USERNAME, usernameText.getText());
		parameters.put(SyncanyFTPParameters.FTP_PASSWORD, passwordText.getText());
		parameters.put(SyncanyFTPParameters.FTP_PATH, pathText.getText());
		parameters.put(SyncanyFTPParameters.FTP_PORT, spinner.getText());
		return parameters;
	}
	
	@Override
	public boolean isValid() {
		boolean valid = true;
		
		// && order matters cause java uses lazy evaluation
		valid = SWTUtil.checkTextLength(hostText, 0) && valid;
		valid = SWTUtil.checkTextLength(usernameText, 0) && valid;
		valid = SWTUtil.checkTextLength(passwordText, 0) && valid;
		valid = SWTUtil.checkTextLength(pathText, 0) && valid;
		valid = SWTUtil.checkNumberBetween(spinner, 0, 65535+1) && valid;
			
		return valid;
	}
}
