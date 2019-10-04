package org.soraworld.ranklist.core;

/**
 * The enum Monster type.
 *
 * @author Himmelt
 */
public enum MonsterType {
    /**
     * Normal monster type.
     */
    NORMAL,
    /**
     * Monster monster type.
     */
    MONSTER,
    /**
     * Boss monster type.
     */
    BOSS;

    public boolean isMonster() {
        return this == MONSTER || this == BOSS;
    }
}
