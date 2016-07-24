package net.laby.application;

import net.laby.protocol.JabyBootstrap;
import net.laby.protocol.packet.PacketChangeMaxRam;
import net.laby.protocol.packet.PacketUpdateDaemons;
import net.laby.protocol.packet.PacketUpdateType;
import net.laby.protocol.packet.PacketUpdateTypes;
import net.laby.protocol.utils.JabyUtils;
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
import java.util.UUID;

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
    public static TableControlFrame open() {
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
    public TableControlFrame() {
        setResizable( false );
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        setIconImage( Toolkit.getDefaultToolkit().getImage( MainConnectionsFrame.class.getResource( "/assets/mainIcon.png" ) ) );
        setBounds( 100, 100, 720, 404 );
        contentPane = new JPanel();
        contentPane.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        setTitle( "Connected to " + JabyUtils.getHostString( JabyBootstrap.getClientHandler().getChannel().remoteAddress() ) );
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
            public void keyTyped( KeyEvent e ) {
            }

            public void keyPressed( KeyEvent e ) {
            }

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
                String type = ( String ) serverTable.getModel().getValueAt( serverTable.getSelectedRow(), 0 );
                String motd = motd1.getText() + "\n" + motd2.getText();

                JabyBootstrap.getClientHandler().sendPacket( new PacketUpdateType( type, 2, motd ) );
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
                if ( selected ) {
                    String type = ( String ) serverTable.getModel().getValueAt( serverTable.getSelectedRow(), 0 );
                    JabyBootstrap.getClientHandler().sendPacket( new PacketUpdateType( type, 1, null ) );
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
                if ( selected ) {
                    String type = ( String ) serverTable.getModel().getValueAt( serverTable.getSelectedRow(), 0 );
                    JabyBootstrap.getClientHandler().sendPacket( new PacketUpdateType( type, 0, null ) );
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
                if ( selected ) {
                    String ip = ( String ) daemonTable.getModel().getValueAt( daemonTable.getSelectedRow(), 0 );
                    int currentMaxRam = Integer.parseInt(
                            (( String ) daemonTable.getModel().getValueAt( daemonTable.getSelectedRow(), 1 )) );
                    UUID uuid = Application.getInstance().getUpdateDaemons().getUuids()[daemonTable.getSelectedRow()];

                    new ValueChangeFrame( "Set max RAM for " + ip, String.valueOf( currentMaxRam ), new ValueCallback() {
                        public void change( Object obj ) {
                            try {
                                JabyBootstrap.getClientHandler().getChannel().writeAndFlush(
                                        new PacketChangeMaxRam( uuid, Integer.parseInt( (( String ) obj) ) ) );
                            } catch ( NumberFormatException ex ) {
                                ex.printStackTrace();
                            }
                        }

                        public void cancelled() {
                            // Nothing
                        }
                    } );
                }
            }
        } );
        contentPane.add( buttonSetMaxRam );
        daemonTable.getTableHeader().setReorderingAllowed( false );
    }


    public void refreshServerTable() {
        DefaultTableModel model = new DefaultTableModel( new String[]{"Type", "Started", "Max", "Standby"}, 0 ) {
            public boolean isCellEditable( int row, int column ) {
                return false;
            }
        };

        PacketUpdateTypes updateTypes = Application.getInstance().getUpdateTypes();

        if ( updateTypes == null )
            return;

        for ( int i = 0; i < updateTypes.getCount(); i++ ) {
            model.addRow( new String[]{
                    updateTypes.getTypesNames()[i],
                    String.valueOf( updateTypes.getStartedServers()[i] ),
                    String.valueOf( updateTypes.getMaxServers()[i] ),
                    String.valueOf( updateTypes.getTypesStandby()[i] )
            } );
        }

        int oldRowIndex = serverTable.getSelectedRow();
        int oldColumnIndex = serverTable.getSelectedColumn();

        serverTable.setModel( model );

        serverTable.changeSelection( oldRowIndex, oldColumnIndex, false, false );

        serverTable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent event ) {
                boolean selected = serverTable.getSelectedRow() != -1;
                buttonKillAll.setEnabled( selected );
                buttonSetStandby.setEnabled( selected );
                motd1.setEnabled( selected );
                motd2.setEnabled( selected );

                if ( selected ) {
                    String motd = Application.getInstance().getUpdateTypes().getMotds()[serverTable.getSelectedRow()];
                    String[] splitedMotd = motd.split( "\n" );

                    motd1.setText( splitedMotd[0] );
                    motd2.setText( (splitedMotd.length > 1 ? splitedMotd[1] : "") );
                } else {
                    motd1.setText( "" );
                    motd2.setText( "" );
                }

            }
        } );
    }

    public void refreshDaemonTable() {
        DefaultTableModel model = new DefaultTableModel( new String[]{"IP of Daemon", "Max RAM", "Current RAM"}, 0 ) {
            public boolean isCellEditable( int row, int column ) {
                return false;
            }
        };

        PacketUpdateDaemons updateDaemons = Application.getInstance().getUpdateDaemons();

        if ( updateDaemons == null )
            return;

        for ( int i = 0; i < updateDaemons.getCount(); i++ ) {
            model.addRow( new String[]{
                    updateDaemons.getIps()[i],
                    String.valueOf( updateDaemons.getDaemonsMaxRam()[i] ),
                    String.valueOf( updateDaemons.getDaemonsCurrentRam()[i] ),
            } );
        }

        int oldRowIndex = daemonTable.getSelectedRow();
        int oldColumnIndex = daemonTable.getSelectedColumn();

        daemonTable.setModel( model );

        daemonTable.changeSelection( oldRowIndex, oldColumnIndex, false, false );

        daemonTable.getSelectionModel().addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent event ) {
                boolean selected = daemonTable.getSelectedRow() != -1;
                buttonSetMaxRam.setEnabled( selected );
            }
        } );
    }
}
