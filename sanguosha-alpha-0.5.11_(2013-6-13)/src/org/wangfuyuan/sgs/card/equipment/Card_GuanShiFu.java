package org.wangfuyuan.sgs.card.equipment;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.wangfuyuan.sgs.data.constant.Const_Game;
import org.wangfuyuan.sgs.data.enums.Colors;
import org.wangfuyuan.sgs.gui.main.Panel_Control;
import org.wangfuyuan.sgs.gui.main.Panel_HandCards;
import org.wangfuyuan.sgs.player.AbstractPlayer;
import org.wangfuyuan.sgs.service.ViewManagement;

/**
 * 贯石斧
 * 
 * @author user
 * 
 */
public class Card_GuanShiFu extends AbstractWeaponCard {
	Panel_Control pc;
	Panel_HandCards ph;
	public Card_GuanShiFu(){}
	public Card_GuanShiFu(int id, int number, Colors color) {
		super(id, number, color);
	}

	/**
	 * 技能--丢弃2张牌强杀 重写被闪后的触发
	 */
	@Override
	public void falseTrigger(AbstractPlayer p, AbstractPlayer target) {
		// super.falseTrigger(p, target);
		pc = (Panel_Control) p.getPanel();
		ph = pc.getHand();
		// 询问发动
		p.refreshView();
		SwingUtilities.invokeLater(ask);
		//锁住主线程
		p.getProcess().setSkilling(true);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 确定还是取消
		check:
		while (true) {
			if (p.getState().getRes() == Const_Game.OK) {
				p.getState().setRes(0);
				// 显示所有牌
//				SwingUtilities.invokeLater(showCards);
				try {
					SwingUtilities.invokeAndWait(showCards);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 等待确定
				while (true) {
					if (p.getState().getRes() == Const_Game.OK) {
						//弃牌
						for (int i = 0; i < ph.getSelectedList().size(); i++) {
							ph.getSelectedList().get(i).getCard().throwIt(p);
						}
						//扣血
						target.getAction().loseHP(1+p.getState().getExtDamage(),p);
						ViewManagement.getInstance().printBattleMsg("贯石斧强杀！" );

						break;
					}
					if (p.getState().getRes() == Const_Game.CANCEL) {
						continue check;
					}
				}
				p.getState().setRes(0);
				p.refreshView();
				p.getProcess().setSkilling(false);
				ViewManagement.getInstance().getPrompt().clear();
				synchronized (p.getProcess()) {
					p.getProcess().notify();
				}
				break;
			}
			if (p.getState().getRes() == Const_Game.CANCEL) {
				p.getState().setRes(0);
				p.getProcess().setSkilling(false);
				ViewManagement.getInstance().getPrompt().clear();
				synchronized (p.getProcess()) {
					p.getProcess().notify();
				}
				return;
			}
		}

	}

	Runnable ask = new Runnable() {
		@Override
		public void run() {
			ph.unableToUseCard();
			ph.disableClick();
			ph.enableOKAndCancel();
			ph.setTargetCheck(false);
			ViewManagement.getInstance().getPrompt().show_Message(
					"是否发动" + getName());

		}
	};

	Runnable showCards = new Runnable() {

		@Override
		public void run() {
			ph.remindToUseALL();
			ph.disableClick();
			ph.enableOKAndCancel();
			ph.setSelectLimit(2);
			ViewManagement.getInstance().getPrompt()
					.show_Message("需要丢弃2张牌，请选择");
		}
	};
}
