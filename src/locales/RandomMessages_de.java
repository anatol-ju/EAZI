package locales;

import java.util.ListResourceBundle;

public class RandomMessages_de extends ListResourceBundle {

    private String[] join, leave, init, attack, attack2, parry, parry2, dodge,
            move, sprint, position, orientate, useMagic, wait, otherAction,
            drawWeapon, loadBow, aim, freeAction, longAction, edited;

    public RandomMessages_de() {
        initialize();
    }

    private void initialize() {
        join = new String[] {
                "tritt dem Kampf bei.",
                "taucht auf.",
                "will mitmischen.",
                "springt aus dem Gebüsch.",
                "sucht nach einem Gegner.",
                "will alle platt machen.",
                "lässt sich das nicht entgehen.",
                "lässt sich nicht lange bitten."
        };

        leave = new String[] {
                "zieht sich zurück.",
                "wurde besiegt.",
                "ist platt.",
                "läuft davon.",
                "gibt auf.",
                "macht sich vom Acker.",
                "wird es zu bunt."
        };

        init = new String[] {
                "Der Schauplatz ist noch ungenutzt.",
                "Die gegnerischen Parteien stehen sich gegenüber.",
                "Der Wind bläst leise zwischen den Opponenten.",
                "Noch ist es still.",
                "Die Anführer erteilen Anweisungen."
        };

        attack = new String[] {
                "greift an.",
                "haut drauf.",
                "holt aus.",
                "schlägt um sich.",
                "attackiert.",
                "setzt zum Manöver an.",
                "schwingt seine Waffe."
        };

        attack2 = new String[] {
                "führt eine komplizierte Attacke aus.",
                "holt weit aus.",
                "zeigt dem Gegner was er kann."
        };

        parry = new String[] {
                "parriert spektakulär.",
                "weiß sich zu wehren.",
                "führt eine komplizierte Parade aus."
        };

        parry2 = new String[] {
                "parriert spektakulär.",
                "weiß sich zu wehren.",
                "führt eine komplizierte Parade aus."
        };

        dodge = new String[] {
                "springt zur Seite.",
                "weicht aus.",
                "lässt sich gekonnt fallen.",
                "lässt den Gegner ins Leere laufen.",
                "zänzelt zur Seite.",
                "springt nach Hinten."
        };

        move = new String[] {
                "setzt sich in Bewegung.",
                "will wo anders hin.",
                "läuft.",
                "ist unterwegs."
        };

        sprint = new String[] {
                "will den Gegner einholen.",
                "rennt drauf los.",
                "sprintet.",
                "läuft wie der Wind."
        };

        position = new String[] {
                "bereitet sich vor.",
                "macht sich fertig.",
                "nimmt Position ein.",
                "wendet sich dem Gegner zu."
        };

        orientate = new String[] {
                "verschafft sich einen Überblick.",
                "überblickt den Kampfplatz.",
                "ist wachsam.",
                "sucht nach Gegnern.",
                "sieht den anderen zu.",
                "schaut sich um."
        };

        useMagic = new String[] {
                "beginnt zu zaubern.",
                "murmelt etwas.",
                "konzentriert sich.",
                "führt eine Geste aus.",
                "bereitet einen Zauber vor.",
                "sieht lächerlich aus."
        };

        wait = new String[] {
                "wartet.",
                "beobachtet den Gegner.",
                "hat nichts zu tun."
        };

        otherAction = new String[] {
                "macht irgendetwas.",
                "tut was er will.",
                "beschäftigt sich anderweitig.",
                "pflückt Blumen." ,
                "dreht einen Stein um.",
                "führt Selbstgespräche.",
                "sammelt Feuerholz ein."
        };

        loadBow = new String[] {
                "lädt nach."
        };

        aim = new String[] {
                "zielt."
        };

        drawWeapon = new String[] {
                "bereitet seine Bewaffnung vor."
        };

        freeAction = new String[] {
                "führt eine freie Aktion durch."
        };

        longAction = new String[] {
                "macht etwas, das länger dauert."
        };

        edited = new String[] {
                "wurde bearbeitet."
        };
    }

    @Override
    protected Object[][] getContents() {
        return new Object[][] {
                {"join", join},
                {"leave", leave},
                {"init", init},
                {"attack", attack},
                {"attack2", attack2},
                {"parry", parry},
                {"parry2", parry2},
                {"dodge", dodge},
                {"move", move},
                {"sprint", sprint},
                {"position", position},
                {"orientate", orientate},
                {"useMagic", useMagic},
                {"wait", wait},
                {"otherAction", otherAction},
                {"drawWeapon", drawWeapon},
                {"loadBow", loadBow},
                {"aim", aim},
                {"freeAction", freeAction},
                {"longAction", longAction},
                {"edited", edited}
        };
    }
}
