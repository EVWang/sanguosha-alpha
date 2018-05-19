package org.wangfuyuan.sgs.skills.active;

import javax.swing.SwingUtilities;

import org.wangfuyuan.sgs.data.constant.Const_Game;
import org.wangfuyuan.sgs.data.enums.Colors;
import org.wangfuyuan.sgs.data.enums.ErrorMessageType;
import org.wangfuyuan.sgs.gui.main.Panel_Control;
import org.wangfuyuan.sgs.gui.main.Panel_HandCards;
import org.wangfuyuan.sgs.player.AbstractPlayer;
import org.wangfuyuan.sgs.service.MessageManagement;
import org.wangfuyuan.sgs.skills.ChangeCardIF;
import org.wangfuyuan.sgs.skills.SkillIF;

/**
 * 华佗【急救】
 * @author user
 *
 */
public class HuaTuo_jijiu implements Runnable ,SkillIF,ChangeCardIF{
	AbstractPlayer player;
	//界面相关组件的引用
	Panel_Control pc;
	
	public HuaTuo_jijiu(AbstractPlayer p){
		this.player = p;
	}
	
	@Override
	public void run() {
		pc = (Panel_Control) player.getPanel();
		//未到发动时机
		if(!player.getState().isRequest()){
			MessageManagement.printErroMsg(ErrorMessageType.cannotUseNow);
			synchronized (player.getProcess()) {
				player.getState().setRes(0);
				player.getProcess().notify();
			}
			return;
		}
		//显示红牌
		SwingUtilities.invokeLater(showRedCards);

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

	@Override
	public String getName() {
		return "急救";
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isEnableUse() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getResultType() {
		return Const_Game.TAO;
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
		}
	};
}
