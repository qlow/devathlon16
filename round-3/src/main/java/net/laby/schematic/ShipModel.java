package net.laby.schematic;

import java.util.ArrayList;

/**
 * Class created by LabyStudio
 */
public class ShipModel {
    private ArrayList<ModelPart> modelParts = new ArrayList<ModelPart>( );

    public ArrayList<ModelPart> getModelParts( ) {
        return modelParts;
    }

    public void addPart(ModelPart part) {
        this.modelParts.add( part );
    }
}
