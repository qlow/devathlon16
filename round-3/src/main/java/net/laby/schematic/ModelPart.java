package net.laby.schematic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

/**
 * Class created by LabyStudio
 */
@AllArgsConstructor
@Getter
public class ModelPart {

    private int x;
    private int y;
    private int z;
    private float rotation;

    private Material material;
    private int data;

}
