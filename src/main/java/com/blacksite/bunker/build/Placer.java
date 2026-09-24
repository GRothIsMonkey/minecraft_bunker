package com.blacksite.bunker.build;

import com.blacksite.bunker.design.B;
import com.blacksite.bunker.design.Dir;
import com.blacksite.bunker.design.EntitySpec;
import com.blacksite.bunker.design.ItemSpec;
import com.blacksite.bunker.design.TileSpec;
import org.bukkit.Art;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Painting;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** Low level world writes: blocks, tile data and entities. All methods run on the main thread. */
public final class Placer {
    private Placer() {
    }

    /** Places a block if it differs. Returns true when the world was changed. */
    @SuppressWarnings("deprecation")
    public static boolean place(World w, int x, int y, int z, int b) {
        Block blk = w.getBlockAt(x, y, z);
        int id = B.id(b), data = B.data(b);
        int oldId = blk.getTypeId();
        if (oldId == id && blk.getData() == data) {
            return false;
        }
        if (oldId != id && oldId != 0 && B.hasTile(B.of(oldId, 0))) {
            // Swapping one tile block for another (e.g. hopper -> chest) in a single write leaves CraftBukkit
            // with a stale tile entity; empty it (no item spill) and go through air first.
            BlockState st = blk.getState();
            Inventory inv = st instanceof Chest ? ((Chest) st).getBlockInventory()
                    : st instanceof InventoryHolder ? ((InventoryHolder) st).getInventory() : null;
            if (inv != null) {
                inv.clear();
            }
            blk.setTypeIdAndData(0, (byte) 0, false);
        }
        blk.setTypeIdAndData(id, (byte) data, false);
        return true;
    }

    /**
     * True when a correctly placed light source has not lit the air beside it (light data saved before the
     * block was finished, e.g. after a crash). Such blocks are re-seated so the lighting engine spreads again.
     */
    @SuppressWarnings("deprecation")
    public static boolean staleLight(World w, int x, int y, int z, int b) {
        int e = B.emission(b);
        if (e < 8) {
            return false;
        }
        Block blk = w.getBlockAt(x, y, z);
        for (BlockFace f : new BlockFace[] {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH,
                BlockFace.EAST, BlockFace.WEST}) {
            Block nb = blk.getRelative(f);
            if (nb.getTypeId() == 0 && nb.getLightFromBlocks() < e - 1) {
                return true;
            }
        }
        return false;
    }

    /** Removes and re-places a block so the lighting engine recomputes around it. */
    @SuppressWarnings("deprecation")
    public static void reseat(World w, int x, int y, int z, int b) {
        Block blk = w.getBlockAt(x, y, z);
        blk.setTypeIdAndData(0, (byte) 0, false);
        blk.setTypeIdAndData(B.id(b), (byte) B.data(b), false);
    }

    @SuppressWarnings("deprecation")
    public static int current(World w, int x, int y, int z) {
        Block blk = w.getBlockAt(x, y, z);
        return B.of(blk.getTypeId(), blk.getData());
    }

    /**
     * Equivalence used by repair and verification: tolerates state bits that change legitimately when the
     * facility is used (doors opened, levers flipped, lamps toggled, crops growing, fluids settling...).
     */
    public static boolean equivalent(int expected, int actual) {
        if (expected == actual) {
            return true;
        }
        int ei = B.id(expected), ai = B.id(actual), ed = B.data(expected), ad = B.data(actual);
        if (norm(ei) != norm(ai)) {
            return false;
        }
        switch (ei) {
            case B.LAMP_ON: case B.LAMP_OFF: case B.WATER: case B.WATER_FLOW: case B.LAVA: case B.LAVA_FLOW:
            case B.FARMLAND: case B.WHEAT: case B.CARROTS: case B.POTATOES: case B.REDSTONE_WIRE:
            case B.STONE_PLATE: case B.WOOD_PLATE: case B.GOLD_PLATE: case B.IRON_PLATE: case B.DAYLIGHT_SENSOR:
                return true;
            case B.LIT_FURNACE: case B.FURNACE:
                return ed == ad;
            case B.OAK_DOOR: case B.IRON_DOOR: case B.SPRUCE_DOOR: case B.BIRCH_DOOR: case B.DARK_OAK_DOOR:
                return (ed & 8) != 0 ? (ed & 9) == (ad & 9) : (ed & 3) == (ad & 3) && (ad & 8) == 0;
            case B.LEVER: case B.STONE_BUTTON: case B.WOOD_BUTTON: case B.HOPPER: case B.DISPENSER: case B.DROPPER:
            case B.POWERED_RAIL: case B.DETECTOR_RAIL: case B.ACTIVATOR_RAIL: case B.COMPARATOR:
                return (ed & 7) == (ad & 7);
            case B.TRAPDOOR: case B.IRON_TRAPDOOR: case B.FENCE_GATE: case B.SPRUCE_FENCE_GATE:
            case B.DARK_OAK_FENCE_GATE:
                return (ed & ~4) == (ad & ~4);
            case B.LEAVES: case B.LEAVES2:
                return (ed & 3) == (ad & 3);
            case B.REPEATER: case B.REDSTONE_TORCH:
                return ed == ad;
            default:
                return false;
        }
    }

    private static int norm(int id) {
        switch (id) {
            case B.LAMP_OFF: return B.LAMP_ON;
            case B.WATER_FLOW: return B.WATER;
            case B.LAVA_FLOW: return B.LAVA;
            case B.LIT_FURNACE: return B.FURNACE;
            case 94: return B.REPEATER;
            case 150: return B.COMPARATOR;
            case B.REDSTONE_TORCH_OFF: return B.REDSTONE_TORCH;
            default: return id;
        }
    }

    // ---------------------------------------------------------------------------------------------
    /** Writes tile data. Returns null on success, else a short error. */
    @SuppressWarnings("deprecation")
    public static String applyTile(World w, TileSpec t, int dx, int dy, int dz) {
        Block blk = w.getBlockAt(t.x + dx, t.y + dy, t.z + dz);
        BlockState st = blk.getState();
        try {
            if (t instanceof TileSpec.SignText) {
                if (!(st instanceof Sign)) {
                    return "no sign";
                }
                Sign s = (Sign) st;
                String[] l = ((TileSpec.SignText) t).lines;
                for (int i = 0; i < 4; i++) {
                    s.setLine(i, l[i]);
                }
                s.update(true, false);
            } else if (t instanceof TileSpec.Inventory) {
                Inventory inv;
                if (st instanceof Chest) {
                    inv = ((Chest) st).getBlockInventory();
                } else if (st instanceof InventoryHolder) {
                    inv = ((InventoryHolder) st).getInventory();
                } else {
                    return "no inventory";
                }
                ItemSpec[] slots = ((TileSpec.Inventory) t).slots;
                for (int i = 0; i < inv.getSize(); i++) {
                    ItemStack s = i < slots.length ? Items.stack(slots[i]) : null;
                    inv.setItem(i, s);
                }
            } else if (t instanceof TileSpec.Head) {
                if (!(st instanceof Skull)) {
                    return "no skull";
                }
                TileSpec.Head h = (TileSpec.Head) t;
                Skull sk = (Skull) st;
                sk.setSkullType(SkullType.values()[Math.max(0, Math.min(4, h.type))]);
                sk.setRotation(ROT[h.rotation & 15]);
                sk.update(true, false);
            } else if (t instanceof TileSpec.Banner) {
                if (!(st instanceof Banner)) {
                    return "no banner";
                }
                TileSpec.Banner bt = (TileSpec.Banner) t;
                Banner bn = (Banner) st;
                bn.setBaseColor(DyeColor.getByWoolData((byte) bt.baseColor));
                List<Pattern> pats = new ArrayList<Pattern>();
                for (String[] p : bt.patterns) {
                    PatternType type = PatternType.getByIdentifier(p[0]);
                    if (type != null) {
                        pats.add(new Pattern(DyeColor.valueOf(p[1].toUpperCase()), type));
                    }
                }
                bn.setPatterns(pats);
                bn.update(true, false);
            }
        } catch (RuntimeException e) {
            return e.getClass().getSimpleName() + ": " + e.getMessage();
        }
        return null;
    }

    static final BlockFace[] ROT = {BlockFace.NORTH, BlockFace.NORTH_NORTH_EAST, BlockFace.NORTH_EAST,
            BlockFace.EAST_NORTH_EAST, BlockFace.EAST, BlockFace.EAST_SOUTH_EAST, BlockFace.SOUTH_EAST,
            BlockFace.SOUTH_SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_SOUTH_WEST, BlockFace.SOUTH_WEST,
            BlockFace.WEST_SOUTH_WEST, BlockFace.WEST, BlockFace.WEST_NORTH_WEST, BlockFace.NORTH_WEST,
            BlockFace.NORTH_NORTH_WEST};

    public static BlockFace face(Dir d) {
        switch (d) {
            case NORTH: return BlockFace.NORTH;
            case SOUTH: return BlockFace.SOUTH;
            case WEST: return BlockFace.WEST;
            case EAST: return BlockFace.EAST;
            case UP: return BlockFace.UP;
            default: return BlockFace.DOWN;
        }
    }

    static float yaw(Dir d) {
        switch (d) {
            case SOUTH: return 0f;
            case WEST: return 90f;
            case NORTH: return 180f;
            default: return 270f;
        }
    }

    /** Height of the collision top of the block at the entity's cell (so stands stand on slabs/carpets). */
    @SuppressWarnings("deprecation")
    static double standOffset(Block b) {
        int id = b.getTypeId(), d = b.getData();
        if (id == B.SLAB || id == B.WOOD_SLAB || id == B.RED_SANDSTONE_SLAB) {
            return (d & 8) != 0 ? 1.0 : 0.5;
        }
        if (id == B.CARPET) {
            return 0.0625;
        }
        return 0.0;
    }

    /**
     * Spawns (or re-spawns) a design entity. Existing entities of the same kind at the same spot are removed
     * first, so re-running a build never duplicates entities. Returns null on success, else an error.
     */
    public static String spawn(World w, EntitySpec e, int dx, int dy, int dz) {
        int x = e.x + dx, y = e.y + dy, z = e.z + dz;
        try {
            switch (e.kind) {
                case ARMOR_STAND: {
                    Block cell = w.getBlockAt(x, y, z);
                    Location loc = new Location(w, x + 0.5, y + standOffset(cell), z + 0.5, yaw(e.facing), 0f);
                    ArmorStand as = null;
                    for (Entity en : w.getNearbyEntities(loc, 0.6, 1.0, 0.6)) {
                        if (en instanceof ArmorStand && !en.isDead()) {
                            if (as == null) {
                                as = (ArmorStand) en;
                            } else {
                                en.remove(); // duplicate
                            }
                        }
                    }
                    if (as == null) {
                        as = w.spawn(loc, ArmorStand.class);
                    } else {
                        as.teleport(loc);
                    }
                    as.setGravity(false);
                    as.setBasePlate(e.basePlate);
                    as.setArms(e.arms);
                    as.setSmall(e.small);
                    as.setHelmet(Items.stack(e.helmet));
                    as.setChestplate(Items.stack(e.chest));
                    as.setLeggings(Items.stack(e.legs));
                    as.setBoots(Items.stack(e.boots));
                    as.setItemInHand(Items.stack(e.hand));
                    return null;
                }
                case ITEM_FRAME:
                case PAINTING: {
                    Block cell = w.getBlockAt(x, y, z);
                    boolean frame = e.kind == EntitySpec.Kind.ITEM_FRAME;
                    Class<? extends Hanging> cls = frame ? ItemFrame.class : Painting.class;
                    BlockFace f = face(e.facing);
                    Hanging h = findHanging(w, cell, cls, f);
                    if (h == null) {
                        Location loc = new Location(w, x, y, z);
                        try {
                            h = w.spawn(loc, cls);
                        } catch (RuntimeException ex) {
                            // CraftBukkit picks the first solid neighbour; build it directly facing the right way
                            h = nmsHanging(w, x, y, z, f, frame);
                            if (h == null) {
                                return "no valid wall for " + e.kind + " facing " + f;
                            }
                        }
                    }
                    if (h instanceof Painting && ((Painting) h).getArt() != Art.valueOf(e.art)) {
                        ((Painting) h).setArt(Art.valueOf(e.art), true);
                    }
                    if (h.getFacing() != f && !h.setFacingDirection(f, true)) {
                        h.remove();
                        return "cannot face " + f;
                    }
                    if (h instanceof ItemFrame) {
                        ((ItemFrame) h).setItem(Items.stack(e.frameItem));
                    }
                    return null;
                }
                case MINECART: {
                    Location loc = new Location(w, x + 0.5, y + 0.1, z + 0.5);
                    boolean have = false;
                    for (Entity en : w.getNearbyEntities(loc, 1.2, 1.0, 1.2)) {
                        if (en instanceof Minecart && !en.isDead()) {
                            if (have) {
                                en.remove();
                            } else if (en.getPassenger() == null) {
                                // back onto its planned rail cell, at rest (a rebuild resets the station)
                                en.setVelocity(new org.bukkit.util.Vector(0, 0, 0));
                                en.teleport(loc);
                            }
                            have = true;
                        }
                    }
                    if (!have) {
                        w.spawn(loc, Minecart.class);
                    }
                    return null;
                }
                default:
                    return "unknown kind";
            }
        } catch (RuntimeException ex) {
            return ex.getClass().getSimpleName() + ": " + ex.getMessage();
        }
    }

    /** 1.8 NMS fallback: construct the hanging entity in the given cell with an explicit direction. */
    static Hanging nmsHanging(World w, int x, int y, int z, BlockFace f, boolean frame) {
        try {
            String ver = org.bukkit.Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            String p = "net.minecraft.server." + ver + ".";
            Object nmsWorld = w.getClass().getMethod("getHandle").invoke(w);
            Class<?> worldCls = Class.forName(p + "World");
            Class<?> bp = Class.forName(p + "BlockPosition");
            Class<?> enumDir = Class.forName(p + "EnumDirection");
            Class<?> ent = Class.forName(p + (frame ? "EntityItemFrame" : "EntityPainting"));
            Class<?> entityCls = Class.forName(p + "Entity");
            Object pos = bp.getConstructor(int.class, int.class, int.class).newInstance(x, y, z);
            Object dir = null;
            for (Object c : enumDir.getEnumConstants()) {
                if (((Enum<?>) c).name().equals(f.name())) {
                    dir = c;
                }
            }
            Object e = ent.getConstructor(worldCls, bp, enumDir).newInstance(nmsWorld, pos, dir);
            if (!(Boolean) ent.getMethod("survives").invoke(e)) {
                return null;
            }
            nmsWorld.getClass().getMethod("addEntity", entityCls).invoke(nmsWorld, e);
            return (Hanging) e.getClass().getMethod("getBukkitEntity").invoke(e);
        } catch (Exception ex) {
            return null;
        }
    }

    /** Existing live hanging entity of this class in the cell (paintings: within a block, same facing). */
    static Hanging findHanging(World w, Block cell, Class<? extends Hanging> cls, BlockFace f) {
        Location c = cell.getLocation().add(0.5, 0.5, 0.5);
        Hanging found = null;
        for (Entity en : w.getNearbyEntities(c, 1.5, 1.5, 1.5)) {
            if (!cls.isInstance(en) || en.isDead()) {
                continue;
            }
            Block at = en.getLocation().getBlock();
            boolean same = at.getX() == cell.getX() && at.getZ() == cell.getZ()
                    && (at.getY() == cell.getY() || (en instanceof Painting && Math.abs(at.getY() - cell.getY()) <= 1
                    && ((Hanging) en).getFacing() == f));
            if (same) {
                if (found == null) {
                    found = (Hanging) en;
                } else {
                    en.remove();
                }
            }
        }
        return found;
    }

    static void removeHanging(World w, Block cell, Class<? extends Hanging> cls) {
        Location c = cell.getLocation().add(0.5, 0.5, 0.5);
        for (Entity en : w.getNearbyEntities(c, 1.5, 1.5, 1.5)) {
            if (cls.isInstance(en)) {
                Block at = en.getLocation().getBlock();
                if (at.getX() == cell.getX() && at.getY() == cell.getY() && at.getZ() == cell.getZ()) {
                    en.remove();
                }
            }
        }
    }

    /** Is a design entity present at its spot? */
    public static boolean present(World w, EntitySpec e, int dx, int dy, int dz) {
        int x = e.x + dx, y = e.y + dy, z = e.z + dz;
        Location c = new Location(w, x + 0.5, y + 0.5, z + 0.5);
        for (Entity en : w.getNearbyEntities(c, 1.2, 1.2, 1.2)) {
            switch (e.kind) {
                case ARMOR_STAND:
                    if (en instanceof ArmorStand) {
                        return true;
                    }
                    break;
                case ITEM_FRAME:
                    if (en instanceof ItemFrame && en.getLocation().getBlock().getX() == x
                            && en.getLocation().getBlock().getZ() == z) {
                        return true;
                    }
                    break;
                case PAINTING:
                    if (en instanceof Painting) {
                        return true;
                    }
                    break;
                case MINECART:
                    if (en instanceof Minecart) {
                        return true;
                    }
                    break;
                default:
                    break;
            }
        }
        return false;
    }
}
