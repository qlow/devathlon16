package net.laby.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.laby.application.Application;

import java.io.*;

public class ConnectionsLoader {

    private File configFile;
    private static ConnectionList list = new ConnectionList();
    private Application main;

    public ConnectionsLoader( Application main, File file ) {
        this.main = main;
        this.configFile = file;
    }

    public static ConnectionList getList( ) {
        return list;
    }

    public void loadConnections( ) {
        if ( !this.configFile.exists() ) {
            saveConnections( );
        }

        System.out.println( "Load!" );
        try {
            String json = Utils.readFile( this.configFile );

            if ( json == null || json.isEmpty() ) {
                saveConnections( );
                error( "Config file is broken." );
                return;
            }

            System.out.println( json );

            list =  new Gson().fromJson( json, ConnectionList.class );

        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public void saveConnections( ) {
        System.out.println( "Save!" );
        if ( !this.configFile.exists() ) {
            if ( this.configFile.getParentFile() != null ) {
                this.configFile.getParentFile().mkdirs();
            }
            try {
                this.configFile.createNewFile();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.setPrettyPrinting().create();
            PrintWriter w;
            w = new PrintWriter( new FileOutputStream( this.configFile ) );
            w.print( gson.toJson( list ) );
            w.flush();
            w.close();
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        }
    }

    private void error( String errorMessage ) {
        System.out.print( errorMessage );
        System.exit( 0 );
    }


}
