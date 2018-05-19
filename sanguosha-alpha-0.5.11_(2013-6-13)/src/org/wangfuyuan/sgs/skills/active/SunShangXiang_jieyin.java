package org.wangfuyuan.sgs.skills.active;

import java.util.List;

import javax.swing.SwingUtilities;

import org.wangfuyuan.sgs.card.AbstractCard;
import org.wangfuyuan.sgs.data.constant.Const_Game;
import org.wangfuyuan.sgs.data.enums.ErrorMessageType;
import org.wangfuyuan.sgs.gui.main.Panel_Control;
import org.wangfuyuan.sgs.gui.main.Panel_HandCards;
import org.wangfuyuan.sgs.gui.main.Panel_Player;
import org.wangfuyuan.sgs.player.AbstractPlayer;
import org.wangfuyuan.sgs.service.MessageManagement;
import org.wangfuyuan.sgs.service.ViewManagement;
import org.wangfuyuan.sgs.skills.SkillIF;

/**
 * 孙尚香【结姻】
 * 
 * @author user
 * 
 */
public class SunShangXiang_jieyin implements Runnable, SkillIF {
	final int CARDNUMBER = 2;
	AbstractPlayer player;
	boolean isUsed;
	Panel_Control pc;
	Panel_HandCards ph;
	// 丢弃的牌
	AbstractCard[] cards = new AbstractCard[CARDNUMBER];
	// 结婚的对象
	AbstractPlayer target;

	public SunShangXiang_jieyin(AbstractPlayer p) {
		this.player = p;
	}

	@Override
	public void run() {
		if (isUsed) {
			MessageManagement.printErroMsg(ErrorMessageType.hasUsed);
			synchronized (player.getProcess()) {
				player.refreshView();
				player.getProcess().notify();
			}
			return;
		}

		pc = (Panel_Control) player.getPanel();
		ph = pc.getHand();
		// 界面开放
		SwingUtilities.invokeLater(run);
		while (true) {
			if (player.getState().getRes() == Const_Game.OK) {
				if (!ph.getTarget().isEmpty()
						&& ph.getSelectedList().size() == CARDNUMBER) {
					target = ph.getTarget().getList().get(0);
					for (int i = 0; i < cards.length; i++) {
						cards[i] = ph.getSelectedList().get(i).getCard();
					}
					execute();

					player.getState().setRes(0);
					break;
				} else {
					player.getState().setRes(0);
					continue;
				}
			}
			if (player.getState().getRes() == Const_Game.CANCEL) {
				player.getState().setRes(0);
				break;
			}
		}
		pc.playersRefresh();
		ViewManagement.getInstance().getPrompt().clear();
		// 解锁
		synchronized (player.getProcess()) {
			player.refreshView();
			player.getProcess().notify();
		}
	}

	/*
	 * 技能执行
	 */
	private void execute() {
		// 弃牌
		for (int i = 0; i < cards.length; i++) {
			cards[i].throwIt(player);
		}
		// 加血
		player.getAction().taoSave(player);
		target.getAction().taoSave(player);
		player.refreshView();
		target.refreshView();
		isUsed = true;
	}

	@Override
	public String getName() {
		return "结姻";
	}

	@Override
	public void init() {
		isUsed = false;
	}

	@Override
	public boolean isEnableUse() {
		// TODO Auto-generated method stub
		return false;
	}

	Runnable run = new Runnable() {
		@Override
		public void run() {

			ph.remindToUseALL();
			ph.setSelectLimit(2);
			ph.setTargetCheck(false);
			ph.disableClick();
			ph.enableOKAndCancel();
			// 异性且受伤玩家开放
			List<Panel_Player> list = ph.getMain().getPlayers();
			for (Panel_Player pp : list) {
				if (!pp.getPlayer().getState().isDead()
						&& pp.getPlayer().getInfo().isSex() == !player
								.getInfo().isSex()
						&& !pp.getPlayer().getFunction().isFullHP()) {
					pp.enableToUse();
				} else {
					pp.disableToUse();
				}
			}
			ViewManagement.getInstance().getPrompt().show_Message(
					"请选择目标,并选择丢弃的手牌");
		}
	};
}
