package org.wangfuyuan.sgs.skills.process;

import org.wangfuyuan.sgs.card.AbstractCard;
import org.wangfuyuan.sgs.data.constant.Const_Game;
import org.wangfuyuan.sgs.data.enums.Colors;
import org.wangfuyuan.sgs.player.AbstractPlayer;
import org.wangfuyuan.sgs.player.impl.P_Process;
import org.wangfuyuan.sgs.service.ModuleManagement;
import org.wangfuyuan.sgs.service.ViewManagement;
import org.wangfuyuan.sgs.skills.LockingSkillIF;

/**
 * 甄姬技能【洛神】
 * 
 * @author user
 * 
 */
public class ZhenJi_luoshen extends P_Process implements LockingSkillIF {

	public ZhenJi_luoshen(AbstractPlayer p) {
		super(p);
	}

	/**
	 * 重写回合开始
	 */
	@Override
	public void stage_begin() {
		if (!player.getState().isAI()) {
			ViewManagement.getInstance().ask(player, getName());
			while (true) {
				if (player.getState().getRes() == Const_Game.OK) {
					ViewManagement.getInstance().printBattleMsg(
							player.getInfo().getName() + "发动" + getName());
					ViewManagement.getInstance().getPrompt().clear();
					player.getState().setRes(0);
					break;
				}
				if (player.getState().getRes() == Const_Game.CANCEL) {
					player.getState().setRes(0);
					return;
				}
			}
		}
		super.stage_begin();
		// 所有黑色判定
		while (true) {
			sleep(1000);
			AbstractCard cc = ModuleManagement.getInstance().showOneCheckCard();
			if (player.getFunction().checkRollCard(cc, Colors.HEITAO,
					Colors.MEIHUA)) {
				player.getAction().addCardToHandCard(cc);
			} else {
				sleep(1000);
				break;
			}
			sleep(1000);
		}
	}

	@Override
	public String getName() {
		return "洛神";
	}

	private void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
