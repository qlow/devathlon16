package net.laby.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import net.laby.application.Application;

import java.io.*;
import java.util.ArrayList;

public class ConnectionsLoader {

    private File configFile;
    private static ArrayList<Connection> connections = new ArrayList<Connection>();
    private Application main;

    public ConnectionsLoader( Application main, File file ) {
        this.main = main;
        this.configFile = file;
    }

    public static ArrayList<Connection> getConnections( ) {
        return connections;
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

            ArrayList<LinkedTreeMap> map = new Gson().fromJson( json, ArrayList.class );

            map.

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
            w.print( gson.toJson( connections ) );
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
