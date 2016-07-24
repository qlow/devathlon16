package net.laby.application;

import net.laby.utils.Utils;
import net.laby.utils.ValueCallback;
import net.laby.utils.ValueChangeFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@SuppressWarnings( "serial" )
public class TableControlFrame extends JFrame {

    private JPanel contentPane;
    private JTextField motd1;
    private JTextField motd2;

    private JTable serverTable;
    private JTable daemonTable;

    private JButton buttonSetMotd;
    private JButton buttonLogout;
    private JButton buttonKillAll;
    private JButton buttonSetStandby;

    private JButton buttonSetMaxRam;

    /**
     * Launch the application.
     */
    public static TableControlFrame open( ) {
        try {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        } catch ( Exception e1 ) {
            e1.printStackTrace();
        }
        TableControlFrame frame = new TableControlFrame();
        frame.setVisible( true );
        return frame;
    }

    /**
     * Create the frame.
     */
    public TableControlFrame( ) {
        setResizable( false );
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        setIconImage( Toolkit.getDefaultToolkit().getImage( MainConnectionsFrame.class.getResource( "/assets/mainIcon.png" ) ) );
        setBounds( 100, 100, 720, 404 );
        contentPane = new JPanel();
        contentPane.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        setTitle( "Connected to <server ip>" );
        setContentPane( contentPane );
        contentPane.setLayout( null );
        Utils.centreWindow( this );

        // Left side
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds( 10, 72, 434, 261 );
        getContentPane().add( scrollPane );

        serverTable = new JTable();
        refreshServerTable();
        scrollPane.setViewportView( serverTable );

        motd1 = new JTextField();
        motd1.setBounds( 10, 11, 328, 23 );
        motd1.setColumns( 10 );
        motd1.setEnabled( false );
        motd1.addKeyListener( new KeyListener() {
            public void keyTyped( KeyEvent e ) {}
            public void keyPressed( KeyEvent e ) {}
            public void keyReleased( KeyEvent e ) {
                buttonSetMotd.setEnabled( !motd1.getText().replace( " ", "" ).isEmpty() );
            }
        } );
        contentPane.add( motd1 );

        buttonSetMotd = new JButton( "Set MOTD" );
        buttonSetMotd.setBounds( 348, 11, 96, 50 );
        buttonSetMotd.setEnabled( false );
        buttonSetMotd.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent arg0 ) {
                String motd = motd1.getText() + "\n" + motd2.getText();

                // SET MOTD
            }
        } );
        contentPane.add( buttonSetMotd );

        buttonLogout = new JButton( "Logout" );
        buttonLogout.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent arg0 ) {
                Application.getInstance().disconnect();
            }
        } );
        buttonLogout.setBounds( 10, 344, 89, 23 );
        contentPane.add( buttonLogout );

        motd2 = new JTextField();
        motd2.setBounds( 10, 38, 328, 23 );
        motd2.setColumns( 10 );
        motd2.setEnabled( false );
        contentPane.add( motd2 );

        buttonKillAll = new JButton( "Kill all" );
        buttonKillAll.setBounds( 387, 344, 57, 23 );
        buttonKillAll.setEnabled( false );
        buttonKillAll.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent arg0 ) {
                boolean selected = serverTable.getSelectedRow() != -1;
                if(selected) {
                    // KILLALL
                }
            }
        } );
        contentPane.add( buttonKillAll );

        buttonSetStandby = new JButton( "Set standby" );
        buttonSetStandby.setBounds( 281, 344, 96, 23 );
        buttonSetStandby.setEnabled( false );
        buttonSetStandby.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent arg0 ) {
                boolean selected = serverTable.getSelectedRow() != -1;
                if(selected) {
                    // STANDBY
                }
            }
        } );
        contentPane.add( buttonSetStandby );


        // Right side
        JScrollPane scrollPaneDaemon = new JScrollPane();
        scrollPaneDaemon.setBounds( 454, 11, 253, 322 );
        getContentPane().add( scrollPaneDaemon );

        daemonTable = new JTable();
        refreshDaemonTable();
        scrollPaneDaemon.setViewportView( daemonTable );

        buttonSetMaxRam = new JButton( "Set max RAM" );
        buttonSetMaxRam.setBounds( 586, 344, 118, 23 );
        buttonSetMaxRam.setEnabled( false );
        buttonSetMaxRam.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent arg0 ) {
                boolean selected = daemonTable.getSelectedRow() != -1;
                if(selected) {
                    // MAX RAM
                    // Important: Use daemonTable!


                    new ValueChangeFrame( "Set max RAM for <server ip>", "*currentvalue*", new ValueCallback() {
                        public void change( Object obj ) {
                            // Save changes
                            refreshDaemonTable();
                        }
                        public void cancelled( ) {
                            // Nothing
                        }
                    } );
                }
            }
        } );
        contentPane.add( buttonSetMaxRam );
        daemonTable.getTableHeader().setReorderingAllowed( false );
    }


    public void refreshServerTable( ) {
        DefaultTableModel model = new DefaultTableModel( new String[]{ "Type", "Started", "Max", "Standby" }, 0 ) {
            public boolean isCellEditable( int row, int column ) {
                return false;
            }
        };

        model.addRow( new String[]{ "BungeeCord", "0", "0", "false" } );


        serverTable.setModel( model );
        /*model.addTableModelListener( new TableModelListener() {
            public void tableChanged( TableModelEvent e ) {
                TableModel model = serverTable.getModel();
                boolean selected = serverTable.getSelectedRow() != -1;
                buttonSetMotd.setEnabled( selected );
                buttonKillAll.setEnabled( selected );
                buttonSetStandby.setEnabled( selected );
            }
        } );*/
        serverTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                boolean selected = serverTable.getSelectedRow() != -1;
                buttonKillAll.setEnabled( selected );
                buttonSetStandby.setEnabled( selected );
                motd1.setEnabled( selected );
                motd2.setEnabled( selected );


            }
        });
    }

    public void refreshDaemonTable( ) {
        DefaultTableModel model = new DefaultTableModel( new String[]{ "IP of Daemon", "Max RAM", "Current RAM" }, 0 ) {
            public boolean isCellEditable( int row, int column ) {
                return false;
            }
        };

        model.addRow( new String[]{ "127.0.0.1", "50%", "30" } );

        daemonTable.setModel( model );
        /*model.addTableModelListener( new TableModelListener() {
            public void tableChanged( TableModelEvent e ) {
                TableModel model = daemonTable.getModel();

            }
        } );*/
        daemonTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                boolean selected = daemonTable.getSelectedRow() != -1;
                buttonSetMaxRam.setEnabled( selected );
            }
        });
    }
}
