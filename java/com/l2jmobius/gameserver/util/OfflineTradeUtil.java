/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jmobius.gameserver.util;

import java.util.logging.Logger;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.data.sql.impl.OfflineTradersTable;
import com.l2jmobius.gameserver.instancemanager.PlayerCountManager;
import com.l2jmobius.gameserver.model.actor.L2Summon;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.olympiad.OlympiadManager;
import com.l2jmobius.gameserver.model.zone.ZoneId;
import com.l2jmobius.gameserver.network.L2GameClient;

/**
 * @author lord_rex
 */
public final class OfflineTradeUtil
{
	protected static final Logger LOGGER_ACCOUNTING = Logger.getLogger("accounting");
	
	private OfflineTradeUtil()
	{
		// utility class
	}
	
	/**
	 * Check whether player is able to enter offline mode.
	 * @param player the player to be check.
	 * @return {@code true} if the player is allowed to remain as off-line shop.
	 */
	private static boolean offlineMode(L2PcInstance player)
	{
		if ((player == null) || player.isInOlympiadMode() || player.isBlockedFromExit() || player.isJailed() || (player.getVehicle() != null))
		{
			return false;
		}
		
		boolean canSetShop = false;
		switch (player.getPrivateStoreType())
		{
			case SELL:
			case PACKAGE_SELL:
			case BUY:
			{
				canSetShop = Config.OFFLINE_TRADE_ENABLE;
				break;
			}
			case MANUFACTURE:
			{
				canSetShop = Config.OFFLINE_TRADE_ENABLE;
				break;
			}
			default:
			{
				canSetShop = Config.OFFLINE_CRAFT_ENABLE && player.isCrafting();
				break;
			}
		}
		
		if (Config.OFFLINE_MODE_IN_PEACE_ZONE && !player.isInsideZone(ZoneId.PEACE))
		{
			canSetShop = false;
		}
		
		// Check whether client is null or player is already in offline mode.
		final L2GameClient client = player.getClient();
		if ((client == null) || client.isDetached())
		{
			return false;
		}
		
		return canSetShop;
	}
	
	/**
	 * Manages the disconnection process of offline traders.
	 * @param player
	 * @return {@code true} when player entered offline mode, otherwise {@code false}
	 */
	public static boolean enteredOfflineMode(L2PcInstance player)
	{
		if (!offlineMode(player))
		{
			return false;
		}
		
		PlayerCountManager.getInstance().incOfflineTradeCount();
		
		final L2GameClient client = player.getClient();
		client.close(true);
		client.setDetached(true);
		
		player.leaveParty();
		OlympiadManager.getInstance().unRegisterNoble(player);
		
		// If the L2PcInstance has Pet, unsummon it
		L2Summon pet = player.getSummon();
		if (pet != null)
		{
			pet.setRestoreSummon(true);
			
			pet.unSummon(player);
			pet = player.getSummon();
			// Dead pet wasn't unsummoned, broadcast npcinfo changes (pet will be without owner name - means owner offline)
			if (pet != null)
			{
				pet.broadcastNpcInfo(0);
			}
		}
		
		if (Config.OFFLINE_SET_NAME_COLOR)
		{
			player.getAppearance().setNameColor(Config.OFFLINE_NAME_COLOR);
			player.broadcastUserInfo();
		}
		
		if (player.getOfflineStartTime() == 0)
		{
			player.setOfflineStartTime(System.currentTimeMillis());
		}
		
		// Store trade on exit, if realtime saving is enabled.
		if (Config.STORE_OFFLINE_TRADE_IN_REALTIME)
		{
			OfflineTradersTable.onTransaction(player, false, true);
		}
		
		player.storeMe();
		LOGGER_ACCOUNTING.info("Entering offline mode, " + client);
		return true;
	}
}
