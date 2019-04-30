package com.vert.fakeplayer.models;

/**
 * @author vert
 */
public class OffensiveSpell extends BotSkill {
    public OffensiveSpell (int skillId, SpellUsageCondition condition, int conditionValue, int delay, int priority) {
        super(skillId, condition, conditionValue, delay, priority);
    }

    public OffensiveSpell (int skillId, int priority) {
        super(skillId, SpellUsageCondition.NONE, 0, priority);
    }

    public OffensiveSpell (int skillId) {
        super(skillId);
    }

    public OffensiveSpell (int skillId, int delay, int priority) {
        super(skillId, SpellUsageCondition.NONE, 0, delay, priority);
    }
}
