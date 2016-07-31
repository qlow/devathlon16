package net.laby.schematic;

import net.laby.devathlon.DevAthlon;
import org.bukkit.Material;

import java.io.*;

/**
 * Class created by LabyStudio
 */
public class SchematicLoader {

    private File folder;

    public SchematicLoader( File folder ) {
        this.folder = folder;
        load();
    }

    public void load( ) {
        File[] files = this.folder.listFiles();
        for ( File file : files ) {
            if ( file.getName().endsWith( ".schem" ) ) {
                loadSchematic( file );
            }
        }
    }

    private void loadSchematic( File file ) {
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader( new FileReader( file ) );
            try {
                ShipModel model = new ShipModel();
                String line;
                while ( ( line = bufferedReader.readLine() ) != null ) {
                    if ( !line.contains( ";" ) ) {
                        continue;
                    }
                    String[] split = line.split( ";" );
                    int x = Integer.parseInt( split[ 0 ] );
                    int y = Integer.parseInt( split[ 1 ] );
                    int z = Integer.parseInt( split[ 2 ] );
                    float rotation = Float.parseFloat( split[ 3 ] );
                    Material material = Material.valueOf( split[ 4 ].toUpperCase() );
                    int data = Integer.parseInt( split[ 5 ] );
                    model.addPart( new ModelPart( x, y, z, rotation, material, data ) );
                }
                DevAthlon.getInstance().getSchematicModels().put( file.getName().split( "\\." )[ 0 ], model );
                System.out.print( "Loaded " + file.getName() + " (" + model.getModelParts().size() + " blocks)" );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        }
    }

}
