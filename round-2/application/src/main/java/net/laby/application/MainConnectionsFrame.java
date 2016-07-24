package net.laby.application;

import net.laby.protocol.utils.JabyUtils;
import net.laby.utils.CellRenderer;
import net.laby.utils.Connection;
import net.laby.utils.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

@SuppressWarnings( "serial" )
public class MainConnectionsFrame extends JFrame {

    private JPanel contentPane;

    private JTextField addressInput;
    private JPasswordField passwordInput;
    private JSpinner spinnerPort;

    private JButton btnSave;
    private JButton btnConnect;

    @SuppressWarnings( "rawtypes" )
    private JList list;

    /**
     * Launch the application.
     */
    public static MainConnectionsFrame open( ) {
        try {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        } catch ( Exception e1 ) {
            e1.printStackTrace();
        }
        MainConnectionsFrame frame = new MainConnectionsFrame();
        frame.setVisible( true );
        return frame;
    }

    /**
     * Create the frame.
     */
    public MainConnectionsFrame( ) {
        setIconImage( Toolkit.getDefaultToolkit().getImage( MainConnectionsFrame.class.getResource( "/assets/mainIcon.png" ) ) );
        setResizable( false );
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        setBounds( 100, 100, 664, 339 );
        setTitle( "Connect to server" );

        // Panel
        contentPane = new JPanel();
        contentPane.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        setContentPane( contentPane );
        contentPane.setLayout( null );

        Utils.centreWindow( this );

        // Spinner Port
        spinnerPort = new JSpinner();
        spinnerPort.setModel( new SpinnerNumberModel( 0, null, 65535, 1 ) );
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor( spinnerPort );
        editor.getFormat().setGroupingUsed( false );
        spinnerPort.setEditor( editor );
        spinnerPort.setBounds( 532, 40, 104, 30 );
        contentPane.add( spinnerPort );

        // Labels
        JLabel lblAddress = new JLabel( "Address" );
        lblAddress.setBounds( 308, 21, 46, 14 );
        contentPane.add( lblAddress );

        JLabel lblPort = new JLabel( "Port" );
        lblPort.setBounds( 532, 21, 46, 14 );
        contentPane.add( lblPort );

        JLabel lblPassword = new JLabel( "Password" );
        lblPassword.setBounds( 308, 99, 127, 14 );
        contentPane.add( lblPassword );

        // Textfields
        addressInput = new JTextField();
        addressInput.setBounds( 308, 40, 214, 30 );
        contentPane.add( addressInput );
        addressInput.setColumns( 10 );

        passwordInput = new JPasswordField();
        passwordInput.setColumns( 10 );
        passwordInput.setBounds( 308, 118, 330, 30 );
        contentPane.add( passwordInput );

        // Buttons
        btnConnect = new JButton( "Connect" );
        btnConnect.setBounds( 476, 262, 162, 30 );
        btnConnect.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( e.getModifiers() == 16 ) {
                    if ( list.getSelectedIndex() <= 0) {
                        Application.getInstance().connect( createConnection() );
                    } else {
                        Application.getInstance().connect( Application.getInstance().getConnections().get( list.getSelectedIndex() - 1 ) );
                    }
                }
            }
        } );
        contentPane.add( btnConnect );

        btnSave = new JButton( "Save" );
        btnSave.setBounds( 308, 262, 158, 30 );
        btnSave.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( e.getModifiers() == 16 ) {
                    if ( list.getSelectedIndex() != -1 ) {
                        if ( list.getSelectedIndex() == 0 ) {
                            saveConnection();
                        } else {
                            Application.getInstance().getConnections().remove( list.getSelectedIndex() - 1 );
                            refreshList();
                            list.setSelectedIndex( 0 );
                        }
                    }
                }
            }
        } );
        contentPane.add( btnSave );

        // Selection list
        setupList();
    }

    @SuppressWarnings( { "unchecked" } )
    private void setupList( ) {
        list = new JList();
        list.setCellRenderer( new CellRenderer() );
        list.setFont( new Font( "Dialog", Font.PLAIN, 13 ) );
        list.setBounds( 10, 10, 284, 282 );
        list.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent event ) {
                JList jl = ( JList ) event.getSource();
                if ( jl.getSelectedIndex() != -1 ) {
                    if ( jl.getSelectedIndex() == 0 ) {
                        fillForm( Connection.defaultConnection() );
                        btnSave.setText( "Save" );
                        addressInput.setEnabled( true );
                        passwordInput.setEnabled( true );
                        spinnerPort.setEnabled( true );
                    } else {
                        fillForm( Application.getInstance().getConnections().get( jl.getSelectedIndex() - 1 ) );
                        btnSave.setText( "Delete" );
                        addressInput.setEnabled( false );
                        passwordInput.setEnabled( false );
                        spinnerPort.setEnabled( false );
                    }
                }
            }
        } );
        refreshList();
        this.list.setSelectedIndex( 0 );
        contentPane.add( list );
    }

    @SuppressWarnings( { "unchecked" } )
    private void refreshList( ) {
        DefaultListModel dListModel = new DefaultListModel();
        dListModel.addElement( "New connection" );
        for ( Connection connection : Application.getInstance().getConnections() ) {
            dListModel.addElement( connection.getAddress() + ":" + connection.getPort() );
        }
        this.list.setListData( dListModel.toArray() );
    }

    private void fillForm( Connection connection ) {
        if ( connection != null && connection.getAddress() != null ) {
            this.addressInput.setText( connection.getAddress() );
            this.spinnerPort.setValue( connection.getPort() );
            this.passwordInput.setText( connection.getPassword().isEmpty() ? "" : "******" );
        }
    }

    private Connection createConnection( ) {
        if ( addressInput.getText().replace( " ", "" ).isEmpty() || passwordInput.getText().replace( " ", "" ).isEmpty() ) {
            try {
                System.out.println( "Error: Invalid connection" );
                Utils.showDialog( this, "Invalid connection", "Address or password is empty!", new ImageIcon( ImageIO.read( this.getClass().getResource( "/assets/connectionIcon.png" ) ) ) );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
            return null;
        }

        return new Connection( addressInput.getText(), ( int ) spinnerPort.getValue(), JabyUtils.convertToMd5( passwordInput.getText() ) );
    }

    private void saveConnection( ) {
        Application.getInstance().getConnections().add( createConnection() );
        Application.getInstance().getConnectionsLoader().saveConnections();

        refreshList();
        this.list.setSelectedIndex( this.list.getLastVisibleIndex() );
        btnSave.setText( "Delete" );
        addressInput.setEnabled( false );
        passwordInput.setEnabled( false );
        spinnerPort.setEnabled( false );

        System.out.println( "Created new connection " + addressInput.getText() );
    }

}
