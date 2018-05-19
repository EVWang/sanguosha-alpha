package org.wangfuyuan.sgs.skills.active;

import java.util.List;

import javax.swing.SwingUtilities;

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
 * 貂蝉【离间】
 * 
 * @author user
 * 
 */
public class DiaoChan_lijian implements Runnable, SkillIF {
	AbstractPlayer player;
	boolean	isUsed;
	Panel_Control pc;
	Panel_HandCards ph;

	public DiaoChan_lijian(AbstractPlayer p) {
		this.player = p;
	}

	/**
	 * 禁用所有牌 ; 开放玩家选择2，先进先出 ; 等待OK
	 */
	@Override
	public void run() {
		if(isUsed){
			MessageManagement.printErroMsg(ErrorMessageType.hasUsed);
			//解锁
			synchronized (player.getProcess()) {
				player.getState().setRes(0);
				player.getProcess().notify();
			}
			return;
		}
		 pc = (Panel_Control) player.getPanel();
		 ph = pc.getHand();
		SwingUtilities.invokeLater(run);
		//当满足2个目标，开放确定按钮
		while(true){
			if(ph.getTarget().getList().size()==2 && !ph.getSelectedList().isEmpty()){
				ph.enableOKAndCancel();
				break;
			}
			if(player.getState().getRes() == Const_Game.CANCEL){
				break;
			}
		}
		//等待确定
		while(true){
			if(player.getState().getRes() == Const_Game.OK){
				AbstractPlayer p1 = ph.getTarget().getList().get(0);
				AbstractPlayer p2 = ph.getTarget().getList().get(1);
				ViewManagement.getInstance().printBattleMsg(p1.getInfo().getName()+"决斗"+p2.getInfo().getName());
				
				ph.getSelectedList().get(0).getCard().throwIt(player);
				player.refreshView();
				while(p1.getAction().jueDou(p2)){
					AbstractPlayer tmp = p1;
					p1=p2;
					p2 = tmp;
				}
				player.getState().setRes(0);
				isUsed = true;
				break;
			}
			if(player.getState().getRes() == Const_Game.CANCEL){
				//player.refreshView();
				ViewManagement.getInstance().refreshAll();
				break;
			}
		}
		//解锁
		synchronized (player.getProcess()) {
			player.getState().setRes(0);
			player.getProcess().notify();
		}
	}

	@Override
	public String getName() {
		return "离间";
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

			ph.getTarget().setLimit(2);
			//ph.getTarget().getList().clear();
			ph.disableClick();
			ph.enableCancel();
			ph.remindToUseALL();
			ph.setTargetCheck(false);
			List<Panel_Player> list = ph.getMain().getPlayers();
			for (Panel_Player pp : list) {
				if (!pp.getPlayer().getState().isDead()) {
					if(pp.getPlayer().getInfo().isSex()==player.getInfo().isSex()){
						pp.disableToUse();
						continue;
					}
				}
				pp.enableToUse();
			}
		}
	};
}
