package com.blacksite.bunker.design;

/**
 * Written books found around SITE-7. Together they tell the story of Project OMEGA and hint at the hidden areas.
 * Pages are kept short for the 1.8 book renderer.
 */
public final class Lore {
    private Lore() {
    }

    private static ItemSpec b(String title, String author, String... pages) {
        return ItemSpec.book(title, author, pages);
    }

    public static ItemSpec welcome() {
        return b("Visitor Brief", "SITE-7 Security",
                "§lSITE-7§r\n\"DEEPWATCH\"\n\nWelcome, cleared personnel.\n\nThis installation does not exist. Neither do you, while you are here.",
                "LEVEL GUIDE\n\n1 Security/Command\n2 Operations/Living\n3 Science/Medical\n4 Storage/Engineering\n5 Reactor/Power\n6 Containment",
                "Use the main stairwell or the personnel lift beside it.\n\nRight-click a §1[Lift Up]§0 or §1[Lift Down]§0 sign inside the lift.",
                "Heavy cargo: freight lift in Loading Bay B goes straight to the Level 4 hangar.");
    }

    public static ItemSpec gateLog() {
        return b("Gate Log", "Sgt. Harlow",
                "0600 shift change.\n0712 fuel truck.\n0940 two scientists, no badges. Waved through on Director's order. AGAIN.",
                "2215 the light from the north exhaust stack came on by itself.\n\nEngineering says it is \"the conduit\". Nobody will tell me what that means.");
    }

    public static ItemSpec directive7() {
        return b("Directive 7", "Office of the Director",
                "§lDIRECTIVE 7§r\n\nThe OMEGA artefact is to remain in the Core under all circumstances.\n\nThe reactor exists to feed its cage, not the other way round.",
                "Should containment fail, seal Level 6 and use the private egress route.\n\nOnly the Director and the Warden know where it starts.",
                "Reminder: the war room is not on any map. Keep it that way. Keep the Wanderer on the wall.");
    }

    public static ItemSpec warRoom() {
        return b("War Room Notes", "Director",
                "If you are reading this you found the Wanderer.\n\nGood. Somebody should know.",
                "The Core lies beneath the turbine hall. The way in is through the Warden's office on Level 6. Look at the bookcase that is one shelf too short.",
                "The egress rail starts in my private station on Level 6. It climbs all the way to the old pump house north-east of the gate.");
    }

    public static ItemSpec officerDiary() {
        return b("Diary", "Lt. Moreau",
                "Day 212. The hum from below is louder at night. The engineers laugh about it. The scientists do not.",
                "Day 219. Found the Director standing in front of the painting in his office again. Just standing there.",
                "Day 230. Someone moved the records. The archive clerk says there is a room behind the room. Maybe on Level 3.");
    }

    public static ItemSpec labNotes() {
        return b("Lab Notebook 14", "Dr. Iyer",
                "Sample 14-C reacts to the conduit light. Growth rate x40 when exposed.\n\nDo not tell the Director yet.",
                "Sample 14-F is gone. The jar was sealed. The observation log shows nothing.\n\nI am requesting a transfer.");
    }

    public static ItemSpec specimenLog() {
        return b("Specimen Log", "Containment",
                "S-01 Wither skeleton cranium: STABLE\nS-02 Creeper tissue: STABLE\nS-03 Zombie cranium: STABLE\nS-04 ????: MOVED ITSELF",
                "S-04 has been relocated to the Core. Level 6 access only.\n\nIt is warm to the touch. It is always warm.");
    }

    public static ItemSpec projectOmega() {
        return b("Project OMEGA", "Records Office",
                "§lPROJECT OMEGA§r\nClassification: BLACK\n\nRecovered from the End expedition. Designated S-04, 'the Egg'.",
                "The artefact emits a field that the reactor core can amplify. The beam you see through the facility is that field made visible.",
                "Current status: contained in the Core, Level 6.\nAccess: Warden, Director.");
    }

    public static ItemSpec personnel() {
        return b("Personnel Roster", "HR",
                "Director: [REDACTED]\nWarden: M. Voss\nChief Engineer: A. Petrov\nChief Science Officer: R. Iyer\nMedical: Dr. Sato",
                "Security: 2 squads, 24 staff\nEngineering: 11\nScience: 9\nMedical: 4\nSupport: 16");
    }

    public static ItemSpec reactorLog() {
        return b("Reactor Log", "A. Petrov",
                "Core output nominal. Conduit beam steady, colour: light blue.\n\nIf the beam ever changes colour, SCRAM first and ask later.",
                "The SCRAM lever is in the shutdown room on Level 5. It will not stop what is in the Core. Nothing will.");
    }

    public static ItemSpec annex() {
        return b("Annex Inventory", "Armourer",
                "Special issue weapons. Signed out only on the Director's word.\n\nYou are clearly not the Director.");
    }

    public static ItemSpec confiscated() {
        return b("Confiscated Diary", "Unknown",
                "They say the base is empty now. They say that about every base.\n\nI saw the light from the hill. I came to look. That was my mistake.");
    }

    public static ItemSpec vaultLedger() {
        return b("Vault Ledger", "Quartermaster",
                "Operating reserve.\nGold, emerald and diamond held against the day the supply convoys stop.",
                "The vault door answers only to the button behind the Warden's bust.");
    }

    public static ItemSpec blacksiteTruth() {
        return b("The Truth", "R. Iyer",
                "The facility was not built to study OMEGA. It was built around it. Level 6 was dug first.",
                "Every level above is a lid on a jar.\n\nThe records room is hidden because the Director asked me to forget. I did not.");
    }

    public static ItemSpec evacuation() {
        return b("Evacuation Order", "Warden",
                "IN CASE OF CONTAINMENT FAILURE\n\n1. Seal Level 6\n2. Reach the private rail station\n3. Ride north-east, then up",
                "The rail climbs to Pump Station No. 3. Walk south along the gravel path to the road.\n\nDo not look back at the stack.");
    }

    public static ItemSpec omegaFinal() {
        return b("Final Entry", "Director",
                "It is not an egg.\n\nIt is a key.\n\nWhatever you do, do not let it leave this room.",
                "If it does leave, it will not go far. It never goes far. It just goes somewhere you are not looking.");
    }
}
