package com.springsecurity.springsecurity.constants;

public enum Database {

    /**
     * Default Database.
     */
    DEFAULT_DATABASE("multitenant_master");

    /**
     * DB Name.
     */
    private String dbName;

    /**
     * Default Constructor.
     *
     * @param val -dbName
     */
    Database(final String val) {
        this.dbName = val;
    }

    /**
     * It is a constructor.
     *
     * @return dbName - the db name.
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * It is a constructor.
     *
     * @param val - the db name.
     */
    public void setDbName(final String val) {
        this.dbName = val;
    }
}

