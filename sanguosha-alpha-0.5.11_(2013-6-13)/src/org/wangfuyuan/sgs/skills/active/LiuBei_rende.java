package org.wangfuyuan.sgs.skills.active;

import java.util.List;

import javax.swing.SwingUtilities;

import org.wangfuyuan.sgs.data.constant.Const_Game;
import org.wangfuyuan.sgs.gui.main.Panel_Control;
import org.wangfuyuan.sgs.gui.main.Panel_HandCards;
import org.wangfuyuan.sgs.gui.main.Panel_Player;
import org.wangfuyuan.sgs.player.AbstractPlayer;
import org.wangfuyuan.sgs.skills.SkillIF;

/**
 * 刘备技能【仁德】
 * 
 * @author user
 * 
 */
public class LiuBei_rende implements Runnable, SkillIF {
	AbstractPlayer player;
	int times;
	boolean isTreated;
	Panel_Control pc;
	Panel_HandCards ph;
	public LiuBei_rende(AbstractPlayer player) {
		this.player = player;
	}

	@Override
	public void run() {
		pc = (Panel_Control) player.getPanel();
		ph = pc.getHand();

		SwingUtilities.invokeLater(run);
		// 等待确定
		while (true) {
			if (player.getState().getRes() == Const_Game.OK) {
				//无目标则返回
				if (ph.getTarget().getList().isEmpty()) {
					//SwingUtilities.invokeLater(run);
					player.getState().setRes(0);
					continue;
				}
				AbstractPlayer toP = ph.getTarget().getList().get(0);
				for (int i = 0; i < ph.getSelectedList().size(); i++) {
					ph.getSelectedList().get(i).getCard().passToPlayer(player,
							toP);
					times++;
				}
				toP.refreshView();
				// 加血效果
				if (!isTreated && times >= 2) {
					player.getAction().addHP(1);
					isTreated = true;
				}
				player.getState().setRes(0);
				break;
			}
			if (player.getState().getRes() == Const_Game.CANCEL) {
				//ViewManagement.getInstance().refreshAll();
				break;
			}
		}
		synchronized (player.getProcess()) {
			player.getState().setRes(0);
			player.refreshView();
			player.getProcess().notifyAll();
		}
	}

	// 清空状态
	public void init() {
		isTreated = false;
		times = 0;
	}

	@Override
	public String getName() {
		return "仁德";
	}


	@Override
	public boolean isEnableUse() {
		// TODO Auto-generated method stub
		return false;
	}

	Runnable run = new Runnable() {
		@Override
		public void run() {
			// 显示可选择的牌
			ph.getSelectedList().clear();
			ph.unableToUseCard();
			ph.disableClick();
			ph.enableOKAndCancel();
			ph.remindToUseALL();
			ph.setTargetCheck(false);
			ph.setSelectLimit(ph.getCards().size());
			// 开放选择
			List<Panel_Player> list = ph.getMain().getPlayers();
			for (Panel_Player pp : list) {
				if (!pp.getPlayer().getState().isDead()) {
					pp.enableToUse();
				}
			}
		}
	};
}
