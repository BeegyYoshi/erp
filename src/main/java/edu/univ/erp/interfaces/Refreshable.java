package edu.univ.erp.interfaces;

public interface Refreshable {
    /**
     * Called automatically by MainFrame whenever a panel becomes visible.
     * Implement this to reload data, update labels, rebuild tables, etc.
     */
    void refresh();
}
