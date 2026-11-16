package GUI;

import java.sql.ResultSet;

/**
 * Remembers who is logged in (ADMIN or USER) so the MENU / BACK buttons know
 * whether to open the Admin Panel or the user Menu.
 *
 * The role is kept in memory and also stored in the "menu" table (row id = 1).
 * If that row does not exist yet it is created, so the buttons never fail with
 * a NullPointerException on a fresh database.
 */
public final class UserSession {

    private static String role;

    private UserSession() {
    }

    /** Called by AdminPanel ("ADMIN") and Menu ("USER") when they open. */
    public static void setRole(String newRole) {
        role = newRole;
        try {
            if (Database.DB.c == null) {
                Database.DB.SetUpConnection();
            }
            int updated = Database.DB.c.createStatement()
                    .executeUpdate("UPDATE menu SET status = '" + newRole + "' WHERE id = '1'");
            if (updated == 0) {
                Database.DB.IUD("INSERT INTO menu (id, status) VALUES ('1', '" + newRole + "')");
            }
        } catch (Exception e) {
            // the in-memory role is enough for this run of the program
            System.err.println("Could not save role to the menu table: " + e.getMessage());
        }
    }

    /**
     * True if the logged-in person is an admin.
     *
     * @param statusFromDb value read from the menu table (may be null)
     */
    public static boolean isAdmin(String statusFromDb) {
        if (statusFromDb != null) {
            return "ADMIN".equalsIgnoreCase(statusFromDb.trim());
        }
        if (role != null) {
            return "ADMIN".equals(role);
        }
        try {
            ResultSet r = Database.DB.search("SELECT status FROM menu WHERE id = '1'");
            if (r.next()) {
                return "ADMIN".equalsIgnoreCase(String.valueOf(r.getString("status")).trim());
            }
        } catch (Exception e) {
            // fall through
        }
        return false;
    }
}
