package mvc;

public class German implements LanguageModule {

    /**
     * Basis-Klasse für die Darstellung des deutschen UI.
     */

    @Override
    public String getAttack() {
        return "AT";
    }

    @Override
    public String getParry() {
        return "PA";
    }

    @Override
    public String getDodge() {
        return "Ausweichen";
    }

    @Override
    public String getWait() {
        return "Warten";
    }

    @Override
    public String getMove() {
        return "Bewegung";
    }

    @Override
    public String getSprint() {
        return "Sprint";
    }

    @Override
    public String getPosition() {
        return "Position";
    }

    @Override
    public String getOrientate() {
        return "Orientieren";
    }

    @Override
    public String getDrawWeapon() {
        return "Waffe Ziehen";
    }

    @Override
    public String getLoadBow() {
        return "Bogen Laden";
    }

    @Override
    public String getAim() {
        return "Zielen";
    }

    @Override
    public String getUseMagic() {
        return "Zaubern";
    }

    @Override
    public String getLongAction() {
        return "Langfristig";
    }

    @Override
    public String getElseAction() {
        return "Sonstige";
    }

    @Override
    public String getModification() {
        return "Modifikator";
    }

    @Override
    public String getActionPoints() {
        return "AkP";
    }

    @Override
    public String getUnarmed() {
        return "Unbewaffnet";
    }

    @Override
    public String getFreeAction() {
        return "Freie Aktion";
    }

    @Override
    public String getNewFighter() {
        return "Neu";
    }

    @Override
    public String getEditFighter() {
        return "Bearbeiten";
    }

    @Override
    public String getDeleteFighter() {
        return "Löschen";
    }
}
