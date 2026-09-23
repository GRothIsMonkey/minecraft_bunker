package com.blacksite.bunker.design;

/** An entity placed as part of the design (armor stands, item frames, paintings, minecarts). */
public final class EntitySpec {
    public enum Kind { ARMOR_STAND, ITEM_FRAME, PAINTING, MINECART }

    public final Kind kind;
    /** block coordinates of the entity's cell */
    public final int x, y, z;
    /** facing: for hanging entities the direction the front faces; for stands the direction they look. */
    public final Dir facing;
    public ItemSpec helmet, chest, legs, boots, hand, frameItem;
    public String art;
    public int frameRotation;
    public boolean arms = true, basePlate = false, small = false;
    /** arm pose (radians) for stands: x,y,z */
    public double[] rightArm;
    public String label;

    public EntitySpec(Kind kind, int x, int y, int z, Dir facing) {
        this.kind = kind;
        this.x = x;
        this.y = y;
        this.z = z;
        this.facing = facing;
    }
}
