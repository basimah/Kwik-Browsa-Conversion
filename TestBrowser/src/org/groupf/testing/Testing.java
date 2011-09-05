package org.groupf.testing;

import java.net.URL;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import swing2swt.layout.BorderLayout;


public class Testing {

	protected Shell shell;
	private Text text;
	private Label lblNewLabel;
	

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Testing window = new Testing();
			
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() throws Exception {
		/*try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
		} catch(Exception e) {
			System.err.println("ERROR: failed to load JDBC driver");
			e.printStackTrace();
			System.exit(0);
		}
		
		Connection con = DriverManager.getConnection("jdbc:hsqldb:data:" + "res/data/history.db");
		Statement stat = con.createStatement();
		
		stat.close();
		con.close();*/
		
		/*URL url = getClass().getResource("data/history.db");
		System.out.println(url.toString());

		Class.forName("org.sqlite.JDBC");		
		Connection con = DriverManager.getConnection("jdbc:sqlite:" + url.toString());
		Statement stat = con.createStatement();
		
		stat.executeUpdate("drop table if exists people;");
		stat.executeUpdate("drop table if exists history;");
		stat.executeUpdate("create table people (name, occupation);");
		PreparedStatement prep = con.prepareStatement("insert into people values (?, ?);");
		prep.setString(1, "Cat");
		prep.setString(2, "Lounging around");
		prep.addBatch();
		
		con.setAutoCommit(false);
		prep.executeBatch();
		con.setAutoCommit(true);
		
		ResultSet rs = stat.executeQuery("select * from people");
		while(rs.next()) {
			System.out.println("name = " + rs.getString("name"));
			System.out.println("occupation = " + rs.getString("occupation"));
		}
		rs.close();
		con.close();*/
		
		
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new BorderLayout(0, 0));
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(BorderLayout.NORTH);
		RowLayout rl_composite = new RowLayout(SWT.HORIZONTAL);
		rl_composite.fill = true;
		composite.setLayout(rl_composite);
		
		Label lblUrl = new Label(composite, SWT.NONE);
		lblUrl.setText("URL:");
		
		final Browser browser = new Browser(shell, SWT.NONE);
		browser.setLayoutData(BorderLayout.CENTER);
		
		browser.addOpenWindowListener(new OpenWindowListener() {
			//Override generic behaviour when dealing with popup windows
			@Override
			public void open(WindowEvent e) {
				Shell shell = new Shell(Display.getDefault());
				shell.setText("New window");
				shell.setLayout(new FillLayout());
				Browser browser = new Browser(shell, SWT.NONE);
				e.browser = browser;
				shell.open();
				
			}
		});

		
		text = new Text(composite, SWT.BORDER);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == '\r') {
					//If enter/return is pressed go to the page
					browser.setUrl(text.getText());
				}
			}
		});
		text.setLayoutData(new RowData(321, SWT.DEFAULT));
		
		Button btnGo = new Button(composite, SWT.NONE);
		btnGo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browser.setUrl(text.getText());
			}
		});
		btnGo.setText("Go");
		

		
		lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setLayoutData(BorderLayout.SOUTH);

		browser.addStatusTextListener(new StatusTextListener() {
			@Override
			public void changed(StatusTextEvent e) {
				lblNewLabel.setText(e.text);
			}
		});
	}
}
