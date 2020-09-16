package mvc;

interface LanguageModule {

    /**
     * Interface zum Erstellen von Klassen für andere UI-Sprachen.
     * Beispiel:
     * <code>
     * class English implements LanguageModule {
     *     @Override
     *     public String getAttack() {
     *         return "AT";
     *     }
     * }
     * </code>
     */

    String getAttack();
    String getParry();
    String getDodge();
    String getWait();
    String getMove();
    String getSprint();
    String getPosition();
    String getOrientate();
    String getDrawWeapon();
    String getLoadBow();
    String getAim();
    String getUseMagic();
    String getLongAction();
    String getElseAction();
    String getModification();
    String getActionPoints();
    String getUnarmed();
    String getFreeAction();
    String getNewFighter();
    String getEditFighter();
    String getDeleteFighter();

}
