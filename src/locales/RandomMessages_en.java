package locales;

import java.util.ListResourceBundle;

public class RandomMessages_en extends ListResourceBundle {

    private String[] join, leave, init, attack, attack2, parry, parry2, dodge,
            move, sprint, position, orientate, useMagic, wait, otherAction,
            drawWeapon, loadBow, aim, freeAction, longAction, edited;

    public RandomMessages_en() {
        initialize();
    }

    private void initialize() {
        join = new String[] {
                "joins the action."
        };

        leave = new String[] {
                "retreats."
        };

        init = new String[] {
                "The arena is still empty."
        };

        attack = new String[] {
                "attacks."
        };

        attack2 = new String[] {
                "starts a complex move."
        };

        parry = new String[] {
                "parries."
        };

        parry2 = new String[] {
                "parries spectacularly."
        };

        dodge = new String[] {
                "dodges."
        };

        move = new String[] {
                "starts moving."
        };

        sprint = new String[] {
                "sprints away."
        };

        position = new String[] {
                "gets ready."
        };

        orientate = new String[] {
                "gets an overview."
        };

        useMagic = new String[] {
                "starts casting a spell."
        };

        wait = new String[] {
                "waits."
        };

        otherAction = new String[] {
                "does something ... different."
        };

        loadBow = new String[] {
                "reloads his weapon."
        };

        aim = new String[] {
                "aims."
        };

        drawWeapon = new String[] {
                "readies his weaponry."
        };

        freeAction = new String[] {
                "uses a free action."
        };

        longAction = new String[] {
                "does something that takes longer."
        };

        edited = new String[] {
                "was edited."
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
