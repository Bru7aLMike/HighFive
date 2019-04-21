package com.vert.fakeplayer.ai.occupations;

import com.l2jmobius.gameserver.enums.ShotType;
import com.vert.fakeplayer.FakePlayer;
import com.vert.fakeplayer.ai.CombatAI;
import com.vert.fakeplayer.helpers.FakeHelpers;
import com.vert.fakeplayer.models.HealingSpell;
import com.vert.fakeplayer.models.OffensiveSpell;
import com.vert.fakeplayer.models.SupportSpell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author vert
 */
public class GhostHunterAI extends CombatAI {
    public GhostHunterAI(FakePlayer character)
    {
        super(character);
    }

    @Override
    public void thinkAndAct()
    {
        super.thinkAndAct();
        setBusyThinking(true);
        applyDefaultBuffs();
        handleShots();
        tryTargetRandomCreatureByTypeInRadius(FakeHelpers.getTestTargetClass(), FakeHelpers.getTestTargetRange());
        tryAttackingUsingFighterOffensiveSkill();
        setBusyThinking(false);
    }

    @Override
    protected ShotType getShotType()
    {
        return ShotType.SOULSHOTS;
    }

    @Override
    public List<OffensiveSpell> getOffensiveSpells()
    {
        List<OffensiveSpell> _offensiveSpells = new ArrayList<>();
        _offensiveSpells.add(new OffensiveSpell(263, 1));
        _offensiveSpells.add(new OffensiveSpell(122, 1));
        _offensiveSpells.add(new OffensiveSpell(11, 1));
        _offensiveSpells.add(new OffensiveSpell(410, 1));
        _offensiveSpells.add(new OffensiveSpell(12, 1));
        _offensiveSpells.add(new OffensiveSpell(321, 1));
        _offensiveSpells.add(new OffensiveSpell(344, 1));
        _offensiveSpells.add(new OffensiveSpell(358, 1));
        return _offensiveSpells;
    }

    @Override
    protected int[][] getBuffs()
    {
        return FakeHelpers.getFighterBuffs();
    }

    @Override
    protected List<HealingSpell> getHealingSpells()
    {
        return Collections.emptyList();
    }

    @Override
    protected List<SupportSpell> getSelfSupportSpells() {
        return Collections.emptyList();
    }
}
