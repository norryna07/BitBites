package main.java.groceries;

/**
 * Enum to get the unit used in grocery item. Different types of units will be separate.
 */
public enum Unit {
    GRAM("mass"),
    KILOGRAM("mass"),
    LITER("volume"),
    MILLILITER("volume"),
    CUP("count"),
    SPOON("count"),
    PIECE("count");

    private final String type;

    /**
     * Constructor for Unit enum.
     * @param type the type of unit of measure
     */
    Unit(String type) {
        this.type = type;
    }

    String getType() {
        return type;
    }

    /**
     * Check if 2 units are compatible
     * @param other the second unit
     * @return 1 - if units can be converted from one to other
     *         0 - if not
     */
    boolean isCompatibleWith(Unit other) {
        // also the count type will be compatible with all other types
        if (this.type.equals("count") || other.type.equals("count")) {
            return true;
        }
        return this.type.equals(other.type);
    }

    public String toString() {
        return this.name().toLowerCase();
    }
}
