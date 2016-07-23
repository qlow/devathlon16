package net.laby.application;

import net.laby.utils.CellRenderer;
import net.laby.utils.Connection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

@SuppressWarnings("serial")
public class ControlFrame extends JFrame {

	private JPanel contentPane;
	
	private JTextField addressInput;
	private JPasswordField passwordInput;
	private JSpinner spinnerPort;
	
	@SuppressWarnings("rawtypes")
	private JList list;

	/**
	 * Launch the application.
	 */
	public static void openGUI() {
		try {
	      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch (Exception e1) {
	      e1.printStackTrace();
	    }
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ControlFrame frame = new ControlFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ControlFrame() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(ControlFrame.class.getResource("/assets/icon.png")));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 664, 339);
		
		// Panel
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Buttons
		JButton btnConnect = new JButton("Connect");
		btnConnect.setBounds(476, 262, 162, 30);
		contentPane.add(btnConnect);
		
		JButton btnSave = new JButton("Save");
		btnSave.setBounds(308, 262, 158, 30);
		contentPane.add(btnSave);
		
		// Selection list
		setupList();
		
		// Spinner Port
		spinnerPort = new JSpinner();
		spinnerPort.setModel(new SpinnerNumberModel(0, null, 65535, 1));
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinnerPort);
		editor.getFormat().setGroupingUsed(false);
		spinnerPort.setEditor(editor);
		spinnerPort.setBounds(532, 40, 104, 30);
		contentPane.add(spinnerPort);
		
		// Labels
		JLabel lblAddress = new JLabel("Address");
		lblAddress.setBounds(308, 21, 46, 14);
		contentPane.add(lblAddress);
		
		JLabel lblPort = new JLabel("Port");
		lblPort.setBounds(532, 21, 46, 14);
		contentPane.add(lblPort);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(308, 99, 127, 14);
		contentPane.add(lblPassword);
		
		// Textfields
		addressInput = new JTextField();
		addressInput.setBounds(308, 40, 214, 30);
		contentPane.add(addressInput);
		addressInput.setColumns(10);
		
		passwordInput = new JPasswordField();
		passwordInput.setColumns(10);
		passwordInput.setBounds(308, 118, 330, 30);
		contentPane.add(passwordInput);
	
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setupList() {
		DefaultListModel dListModel = new DefaultListModel();
		
		dListModel.addElement("New connection");


		for(Object connection : Application.getInstance().getConnectionList()) {
			dListModel.addElement(connection);
		}
		
		list = new JList(dListModel);
		list.setCellRenderer(new CellRenderer());
		list.setFont(new Font("Dialog", Font.PLAIN, 13));
		list.setBounds(10, 10, 284, 282);
		contentPane.add(list);
		
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				JList jl = (JList) event.getSource();
				if(jl.getSelectedIndex() == 0) {
					fillForm(Connection.defaultConnection());
				} else {
					fillForm(Application.getInstance().getConnectionList().get(jl.getSelectedIndex() - 1));
				}
			}
		});
	}
	
	private void fillForm(Connection connection) {
		this.addressInput.setText(connection.getAddress());
		this.spinnerPort.setValue(connection.getPort());
		this.passwordInput.setText(connection.getPassword());
	}
}
