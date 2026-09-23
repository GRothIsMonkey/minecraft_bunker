package com.blacksite.bunker.design;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Thematic chest contents. Deliberately modest so the bunker stays a survival base, not a loot piñata. When
 * {@link #enabled} is false only the lore documents are kept.
 */
public final class Loot {
    private Loot() {
    }

    public static boolean enabled = true;

    static ItemSpec i(int id, int n) {
        return ItemSpec.of(id, n);
    }

    static ItemSpec i(int id, int n, int dmg) {
        return ItemSpec.of(id, n, dmg);
    }

    private static ItemSpec[] L(ItemSpec... items) {
        if (!enabled) {
            List<ItemSpec> keep = new ArrayList<ItemSpec>();
            for (ItemSpec s : items) {
                if (s != null && (s.bookTitle != null || s.id == 339)) {
                    keep.add(s);
                }
            }
            return keep.toArray(new ItemSpec[0]);
        }
        return items;
    }

    // leather colours for uniforms
    public static final int OLIVE = 0x4B5320, TAN = 0xA08A5F, BLACK = 0x1A1A1A, HAZMAT = 0xE6C229, WHITE = 0xEDEDED,
            NAVY = 0x1F2A44, RED = 0x8E1B1B;

    public static ItemSpec leather(int id, int color, String name) {
        return ItemSpec.of(id, 1).leather(color).named(name);
    }

    public static ItemSpec[] generatorShed() {
        return L(i(263, 24), i(265, 3), i(331, 12), i(325, 1), i(280, 8));
    }

    public static ItemSpec[] guardTower() {
        return L(i(262, 16), i(297, 3), i(345, 1), i(50, 8), Lore.gateLog());
    }

    public static ItemSpec[] checkpointDesk() {
        return L(i(339, 12), i(345, 1), i(347, 1), i(395, 2), Lore.welcome());
    }

    public static ItemSpec[] guardRoom() {
        return L(i(272, 2), i(261, 1), i(262, 24), i(364, 4), leather(298, OLIVE, "Guard Helmet"),
                leather(299, OLIVE, "Guard Jacket"));
    }

    public static ItemSpec[] loadingBay() {
        return L(i(65, 16), i(66, 32), i(328, 2), i(342, 1), i(280, 16), i(4, 64));
    }

    public static ItemSpec[] armoryWeapons() {
        return L(i(267, 2), i(272, 4), i(261, 2), i(262, 64), i(262, 32));
    }

    public static ItemSpec[] armoryArmor() {
        return L(i(306, 1), i(307, 1), i(308, 1), i(309, 1), leather(299, OLIVE, "Field Jacket"),
                leather(300, OLIVE, "Field Trousers"));
    }

    public static ItemSpec[] armoryExplosives() {
        return L(i(46, 6), i(289, 8), i(76, 4));
    }

    public static ItemSpec[] armoryAnnex() {
        return L(i(276, 1).named("§6Director's Sabre").ench(16, 2, 34, 2), i(261, 1).named("§6Longshot").ench(48, 2),
                i(262, 64), i(322, 2), i(373, 2, 8229), Lore.annex());
    }

    public static ItemSpec[] rations() {
        return L(i(297, 16), i(364, 8), i(320, 8), i(393, 12), i(260, 6));
    }

    public static ItemSpec[] kitchen() {
        return L(i(296, 16), i(344, 8), i(353, 12), i(335, 1), i(325, 2), i(281, 8));
    }

    public static ItemSpec[] pantry() {
        return L(i(297, 32), i(391, 16), i(392, 16), i(260, 12), i(357, 24), i(400, 4), i(366, 8));
    }

    public static ItemSpec[] freezer() {
        return L(i(363, 12), i(365, 12), i(319, 12), i(349, 8), i(332, 16));
    }

    public static ItemSpec[] lounge() {
        return L(i(2256, 1), i(2258, 1), i(2261, 1), i(357, 16), i(373, 3, 0), i(346, 1));
    }

    public static ItemSpec[] officer(int n) {
        switch (n % 4) {
            case 0: return L(i(339, 6), i(347, 1), i(386, 1), Lore.officerDiary());
            case 1: return L(i(345, 1), i(266, 3), i(340, 2));
            case 2: return L(i(262, 12), i(297, 4), i(339, 3));
            default: return L(i(357, 8), i(353, 4), i(340, 1));
        }
    }

    public static ItemSpec[] crewLocker(int n) {
        switch (n % 5) {
            case 0: return L(leather(299, OLIVE, "Crew Shirt"), i(297, 2));
            case 1: return L(i(50, 12), i(280, 4));
            case 2: return L(leather(301, BLACK, "Boots"), i(339, 2));
            case 3: return L(i(357, 6));
            default: return L(i(287, 4), i(352, 3));
        }
    }

    public static ItemSpec[] laundry() {
        return L(i(35, 16, 0), i(35, 8, 13), i(287, 12), i(334, 6));
    }

    public static ItemSpec[] workshop() {
        return L(i(256, 1), i(257, 1), i(258, 1), i(359, 1), i(280, 32), i(265, 6));
    }

    public static ItemSpec[] labChem() {
        return L(i(374, 12), i(372, 8), i(353, 8), i(376, 3), i(348, 16), i(331, 16), Lore.labNotes());
    }

    public static ItemSpec[] labAnalysis() {
        return L(i(339, 16), i(340, 3), i(375, 4), i(341, 4), i(378, 2), i(370, 1));
    }

    public static ItemSpec[] specimens() {
        return L(i(367, 12), i(352, 12), i(375, 6), i(368, 2), i(341, 3), Lore.specimenLog());
    }

    public static ItemSpec[] medical() {
        return L(i(373, 4, 8197), i(373, 2, 8193), i(322, 1), i(335, 2), i(35, 12, 0), i(339, 4));
    }

    public static ItemSpec[] pharmacy() {
        return L(i(373, 3, 8197), i(373, 2, 16389), i(382, 4), i(376, 2), i(374, 8), i(377, 4));
    }

    public static ItemSpec[] archive(int n) {
        switch (n % 4) {
            case 0: return L(i(339, 32), Lore.projectOmega());
            case 1: return L(i(339, 24), i(340, 6), Lore.personnel());
            case 2: return L(i(339, 18), i(386, 2), Lore.reactorLog());
            default: return L(i(339, 20), i(395, 3));
        }
    }

    public static ItemSpec[] hiddenRecords() {
        return L(Lore.blacksiteTruth(), Lore.evacuation(), i(339, 12), i(388, 6), i(384, 8));
    }

    public static ItemSpec[] engineering() {
        return L(i(331, 32), i(76, 12), i(356, 6), i(404, 3), i(33, 2), i(29, 2), i(154, 4));
    }

    public static ItemSpec[] redstoneLab() {
        return L(i(331, 64), i(76, 16), i(356, 8), i(404, 4), i(29, 4), i(33, 4), i(123, 6), i(69, 8), i(77, 8));
    }

    public static ItemSpec[] repairBay() {
        return L(i(265, 12), i(42, 1), i(280, 16), i(287, 8), i(334, 6));
    }

    public static ItemSpec[] smelterFuel() {
        return L(i(263, 64), i(263, 32));
    }

    public static ItemSpec[] reactorControl() {
        return L(i(331, 16), i(348, 16), Lore.reactorLog(), i(345, 1));
    }

    public static ItemSpec[] generator() {
        return L(i(263, 32), i(173, 2), i(331, 8));
    }

    public static ItemSpec[] evidence(int n) {
        switch (n % 5) {
            case 0: return L(i(266, 4), i(345, 1), i(347, 1), i(339, 3));
            case 1: return L(i(368, 2), i(381, 1), i(339, 2));
            case 2: return L(i(261, 1), i(262, 9), i(283, 1));
            case 3: return L(i(386, 1), Lore.confiscated());
            default: return L(i(388, 2), i(351, 6, 4), i(406, 8));
        }
    }

    public static ItemSpec[] vault() {
        return L(i(264, 8), i(388, 16), i(266, 24), i(265, 32), i(322, 3), i(384, 16), i(368, 4), Lore.vaultLedger());
    }

    public static ItemSpec[] core() {
        return L(Lore.omegaFinal(), i(399 - 31, 4), i(381, 2), i(264, 4));
    }

    public static ItemSpec[] commandDesk() {
        return L(Lore.directive7(), i(345, 1), i(347, 1), i(339, 8));
    }

    public static ItemSpec[] warRoom() {
        return L(Lore.warRoom(), i(264, 3), i(322, 1), i(339, 6), i(368, 3));
    }

    public static ItemSpec[] escapeKit() {
        return L(i(328, 1), i(297, 6), i(50, 16), i(345, 1), i(373, 2, 8197), Lore.evacuation());
    }

    public static ItemSpec[] quartermaster(int n) {
        switch (n % 4) {
            case 0: return L(i(50, 32), i(65, 16), i(297, 8));
            case 1: return L(i(256, 2), i(257, 1), i(258, 1));
            case 2: return L(leather(298, OLIVE, "Issue Helmet"), leather(299, OLIVE, "Issue Jacket"),
                    leather(300, OLIVE, "Issue Trousers"), leather(301, OLIVE, "Issue Boots"));
            default: return L(i(325, 2), i(346, 1), i(420, 2));
        }
    }

    public static ItemSpec[] enchanting() {
        return L(i(351, 32, 4), i(340, 8), i(384, 12));
    }

    public static ItemSpec[] brewing() {
        return L(i(372, 16), i(374, 16), i(377, 6), i(348, 16), i(331, 16), i(376, 4), i(382, 4), i(396, 2));
    }

    public static ItemSpec[] farm() {
        return L(i(295, 32), i(391, 8), i(392, 8), i(361, 4), i(362, 4), i(351, 16, 15), i(292, 1));
    }

    public static List<ItemSpec> all(ItemSpec[]... groups) {
        List<ItemSpec> out = new ArrayList<ItemSpec>();
        for (ItemSpec[] g : groups) {
            out.addAll(Arrays.asList(g));
        }
        return out;
    }
}
