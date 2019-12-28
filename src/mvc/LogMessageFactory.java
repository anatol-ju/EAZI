package mvc;

import java.util.*;

public class LogMessageFactory {

    private Map<String, String[]> data;

    public LogMessageFactory() {
        data = new HashMap<>();
        initialize();
    }

    private void initialize() {
        String[] join = new String[] {
                "tritt dem Kampf bei.",
                "taucht auf.",
                "will mitmischen.",
                "springt aus dem Gebüsch.",
                "sucht nach einem Gegner.",
                "will alle platt machen.",
                "lässt sich das nicht entgehen.",
                "lässt sich nicht lange bitten."
        };

        String[] leave = new String[] {
                "zieht sich zurück.",
                "wurde besiegt.",
                "ist platt.",
                "läuft davon.",
                "gibt auf.",
                "macht sich vom Acker.",
                "wird es zu bunt."
        };

        String[] init = new String[] {
                "Der Schauplatz ist noch ungenutzt.",
                "Die gegnerischen Parteien stehen sich gegenüber.",
                "Der Wind bläst leise zwischen den Opponenten.",
                "Noch ist es still.",
                "Die Anführer erteilen Anweisungen."
        };

        String[] attack = new String[] {
                "greift an.",
                "haut drauf.",
                "holt aus.",
                "schlägt um sich.",
                "attackiert.",
                "setzt zum Manöver an.",
                "schwingt seine Waffe."
        };

        String[] attack2 = new String[] {
                "führt eine komplizierte Attacke aus.",
                "holt weit aus.",
                "zeigt dem Gegner was er kann."
        };

        String[] parry = new String[] {
                "parriert spektakulär.",
                "weiß sich zu wehren.",
                "führt eine komplizierte Parade aus."
        };

        String[] parry2 = new String[] {
                "parriert spektakulär.",
                "weiß sich zu wehren.",
                "führt eine komplizierte Parade aus."
        };

        String[] avoid = new String[] {
                "springt zur Seite.",
                "weicht aus.",
                "lässt sich gekonnt fallen.",
                "lässt den Gegner ins Leere laufen.",
                "zänzelt zur Seite.",
                "springt nach Hinten."
        };

        String[] move = new String[] {
                "setzt sich in Bewegung.",
                "will wo anders hin.",
                "läuft.",
                "ist unterwegs."
        };

        String[] run = new String[] {
                "will den Gegner einholen.",
                "rennt drauf los.",
                "sprintet.",
                "läuft wie der Wind."
        };

        String[] position = new String[] {
                "bereitet sich vor.",
                "macht sich fertig.",
                "nimmt Position ein.",
                "wendet sich dem Gegner zu."
        };

        String[] look = new String[] {
                "verschafft sich einen Überblick.",
                "überblickt den Kampfplatz.",
                "ist wachsam.",
                "sucht nach Gegnern.",
                "sieht den anderen zu.",
                "schaut sich um."
        };

        String[] cast = new String[] {
                "beginnt zu zaubern.",
                "murmelt etwas.",
                "konzentriert sich.",
                "führt eine Geste aus.",
                "bereitet einen Zauber vor.",
                "sieht lächerlich aus."
        };

        String[] wait = new String[] {
                "wartet.",
                "beobachtet den Gegner.",
                "hat nichts zu tun."
        };

        String[] something = new String[] {
                "macht irgendetwas.",
                "tut was er will.",
                "beschäftigt sich anderweitig.",
                "pflückt Blumen." ,
                "dreht einen Stein um.",
                "führt Selbstgespräche.",
                "sammelt Feuerholz ein."
        };

        String[] reload = new String[] {
                "lädt nach."
        };

        String[] aim = new String[] {
                "zielt."
        };

        String[] equip = new String[] {
                "bereitet seine Bewaffnung vor."
        };

        String[] free = new String[] {
                "führt eine freie Aktion durch."
        };

        String[] talent = new String[] {
                "macht etwas, das länger dauert."
        };

        String[] edited = new String[] {
                "wurde bearbeitet."
        };

        data.put("join",join);
        data.put("leave",leave);
        data.put("init",init);
        data.put("attacke",attack);
        data.put("attacke2",attack2);
        data.put("parade",parry);
        data.put("parade2", parry2);
        data.put("ausweichen",avoid);
        data.put("bewegung",move);
        data.put("sprint",run);
        data.put("position",position);
        data.put("orientieren",look);
        data.put("zaubern",cast);
        data.put("warten",wait);
        data.put("sonstiges",something);
        data.put("laden", reload);
        data.put("zielen", aim);
        data.put("ziehen", equip);
        data.put("frei", free);
        data.put("langfristiges", talent);
        data.put("bearbeitet", edited);
    }

    public String getRandomMessage(String identifier) {
        List<String> list = Arrays.asList(data.get(identifier));
        Collections.shuffle(list);

        return list.get(0);
    }

}
