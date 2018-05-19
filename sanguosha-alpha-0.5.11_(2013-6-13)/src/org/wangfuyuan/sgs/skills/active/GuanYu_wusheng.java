package org.wangfuyuan.sgs.skills.active;

import java.util.List;

import javax.swing.SwingUtilities;

import org.wangfuyuan.sgs.card.AbstractCard;
import org.wangfuyuan.sgs.card.base.Card_Sha;
import org.wangfuyuan.sgs.card.changed.Virtual_Sha;
import org.wangfuyuan.sgs.data.constant.Const_Game;
import org.wangfuyuan.sgs.data.enums.Colors;
import org.wangfuyuan.sgs.data.enums.ErrorMessageType;
import org.wangfuyuan.sgs.gui.main.Panel_Control;
import org.wangfuyuan.sgs.gui.main.Panel_HandCards;
import org.wangfuyuan.sgs.gui.main.Panel_Player;
import org.wangfuyuan.sgs.player.AbstractPlayer;
import org.wangfuyuan.sgs.player.PlayerIF;
import org.wangfuyuan.sgs.service.MessageManagement;
import org.wangfuyuan.sgs.skills.ChangeCardIF;
import org.wangfuyuan.sgs.skills.SkillIF;

/**
 * 测试关羽技能【武圣】
 * 
 * @author user
 * 
 */
public class GuanYu_wusheng implements Runnable, SkillIF, ChangeCardIF {
	AbstractPlayer player;
	Panel_Control pc;

	public GuanYu_wusheng(AbstractPlayer player) {
		this.player = player;
	}

	@Override
	public void run() {
		pc = (Panel_Control) player.getPanel();
		// 如果是响应
		if (player.getState().isRequest()) {
			useAsRequest();
			//unlock();
			return;
		}
		if (player.getState().isUsedSha()) {
			MessageManagement.printErroMsg(ErrorMessageType.hasUsed_Sha);
			unlock();
			return;
		}
		// 显示所有红色牌
		SwingUtilities.invokeLater(showRedCards);
		// 等待选择
		while (true) {
			if (player.getState().getRes() == Const_Game.OK) {
				AbstractCard c = pc.getHand().getSelectedList().get(0)
						.getCard();
				// 原有的牌丢弃
				c.throwIt(player);
				// 新出一张虚拟杀
				new Virtual_Sha(c).use(player, pc.getHand().getTarget()
						.getList().get(0));
				break;
			}
			if (player.getState().getRes() == Const_Game.CANCEL) {
				player.refreshView();
				break;
			}
		}
		unlock();
	}

	/*
	 * 响应阶段的使用
	 */
	private void useAsRequest() {
		// 锁住响应
		player.getState().setRes(Const_Game.SKILL);
		// 显示所有红色牌
		SwingUtilities.invokeLater(showRedCards);
		// 等待选择
		while (true) {
			if (player.getState().getRes() == Const_Game.OK) {
				/*AbstractCard c = pc.getHand().getSelectedList().get(0)
						.getCard();*/
				// 更新手牌
				pc.getHand().updateCards();
				player.getState().setRes(getResultType());
				System.out.println("click ok!");
				break;
			}
			if (player.getState().getRes() == Const_Game.CANCEL) {
				player.getState().setRes(Const_Game.REDO);
				break;
			}
		}
		synchronized (player.getRequest()) {
			player.getRequest().notify();
		}
	}

	private void unlock() {
		// 如果在回合中，就解回合锁
		if (player.getStageNum() == PlayerIF.STAGE_USECARDS) {
			synchronized (player.getProcess()) {
				player.getState().setRes(0);
				player.getProcess().notify();
			}
		}
	}

	@Override
	public String getName() {
		return "武圣";
	}

	@Override
	public boolean isEnableUse() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getResultType() {
		return Const_Game.SHA;
	}

	Runnable showRedCards = new Runnable() {

		@Override
		public void run() {

			Panel_HandCards ph = pc.getHand();
			ph.unableToUseCard();
			ph.remindToUse(Colors.HONGXIN, Colors.FANGKUAI);
			ph.setSelectLimit(1);
			ph.disableClick();
			ph.enableOKAndCancel();
			ph.setTargetCheck(false);
			if (player.getState().isRequest()) {
				return;
			}
			// 遍历 检测
			List<Panel_Player> list = pc.getMain().getPlayers();
			for (Panel_Player pp : list) {
				if (new Card_Sha().isInRange(player, pp.getPlayer())) {
					pp.enableToUse();
				} else {
					pp.disableToUse();
				}
			}

		}
	};
}
