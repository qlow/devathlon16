package net.laby.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.laby.application.Application;

import java.io.*;

public class ConnectionsLoader {

    private Gson gson = new Gson();
    private File configFile;
    public static Settings settings;

    private Application main;

    public ConnectionsLoader( Application main, File file ) {
        this.main = main;
        this.configFile = file;
    }

    public void loadConnections( ) {
        if ( !this.configFile.exists() ) {
            saveConnections( true );
        }

        try {
            String json = Utils.readFile( this.configFile );

            if(json == null || json.isEmpty()) {
                saveConnections( true );
                error("Config file is broken.");
                return;
            }

            settings = (Settings) gson.fromJson( json, Settings.class );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveConnections( ) {
        saveConnections(false);
    }

    private void saveConnections( boolean defaultSettings ) {
        if ( !this.configFile.exists() ) {
            this.configFile.getParentFile().mkdirs();
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
            if ( defaultSettings ) {
                w.print( gson.toJson( new Gson().toJson(new Settings()) ) );
            } else {
                w.print( gson.toJson( settings ) );
            }
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
