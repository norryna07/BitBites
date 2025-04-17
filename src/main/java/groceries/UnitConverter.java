package main.java.groceries;

class UnitConverter {
    /**
     * Convert from a unit to another.
     * @param quantity the quantity in the first unit
     * @param forUnit the first unit
     * @param toUnit the second unit
     * @return the quantity in the second unit
     * @throws IllegalArgumentException if the units are incompatible
     */
    static double convert(double quantity, Unit forUnit, Unit toUnit) throws IllegalArgumentException {
        if (!forUnit.isCompatibleWith(toUnit)) {
            throw new IllegalArgumentException(String.format("%s is not compatible with %s", toUnit, forUnit));
        }
        return switch (forUnit) {
            case GRAM -> switch (toUnit) {
                case GRAM -> quantity;
                case KILOGRAM -> quantity / 1000.0;
                case SPOON -> quantity / 20.0;
                case CUP -> quantity / 200.0;
                default -> throw new IllegalArgumentException(String.format("%s is not compatible with %s", toUnit, forUnit));
            };
            case KILOGRAM -> switch(toUnit) {
                case KILOGRAM -> quantity;
                case GRAM -> quantity * 1000.0;
                case SPOON -> quantity * 50.0;
                case CUP -> quantity * 5.0;
                default -> throw new IllegalArgumentException(String.format("%s is not compatible with %s", toUnit, forUnit));
            };
            case LITER -> switch (toUnit) {
                case LITER -> quantity;
                case MILLILITER -> quantity * 1000.0;
                case CUP -> quantity * 5.0;
                case SPOON -> quantity * 50.0;
                default -> throw new IllegalArgumentException(String.format("%s is not compatible with %s", toUnit, forUnit));
            };
            case MILLILITER -> switch(toUnit) {
                case MILLILITER -> quantity;
                case LITER -> quantity / 1000.0;
                case CUP -> quantity / 200.0;
                case SPOON -> quantity / 20.0;
                default -> throw new IllegalArgumentException(String.format("%s is not compatible with %s", toUnit, forUnit));
            };
            case CUP -> switch(toUnit) {
                case CUP -> quantity;
                case GRAM, MILLILITER -> quantity * 200.0;
                case KILOGRAM, LITER -> quantity / 5.0;
                case SPOON -> quantity * 10.0;
                default -> throw new IllegalArgumentException(String.format("%s is not compatible with %s", toUnit, forUnit));
            };
            case SPOON -> switch(toUnit) {
                case SPOON -> quantity;
                case GRAM, MILLILITER -> quantity * 20.0;
                case KILOGRAM, LITER -> quantity / 50.0;
                case CUP -> quantity / 10.0;
                default -> throw new IllegalArgumentException(String.format("%s is not compatible with %s", toUnit, forUnit));
            };
            case PIECE -> switch (toUnit) {
                case PIECE -> quantity;
                default -> throw new IllegalArgumentException(String.format("%s is not compatible with %s", toUnit, forUnit));
            };

        };
    }
}
