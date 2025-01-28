package acavailhez.optget.casts;

public enum CastMode {
    // only cast when the sub-value is of the correct class
    STRICT,
    // Attempt to parse Strings, etc
    PARSE,
    // Attempt to clean input (trim, etc)
    CLEAN,
    // Make assumptions (eg convert Double 2.1 to Int 2)
    UNSAFE_BEST_EFFORT,
}
