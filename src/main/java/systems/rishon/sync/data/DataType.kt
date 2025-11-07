package systems.rishon.sync.data

enum class DataType(var isSynced: Boolean) {
    INVENTORY(true),
    LOCATION(true),
    EXP_POINTS(true),
    EXP_LEVEL(true),
    HEALTH(true),
    MAX_HEALTH(true),
    HUNGER(true),
    GAMEMODE(true),
    POTION_EFFECTS(true)
}