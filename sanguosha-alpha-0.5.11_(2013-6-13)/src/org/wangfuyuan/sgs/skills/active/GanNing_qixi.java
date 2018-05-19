package org.wangfuyuan.sgs.skills.active;

import java.util.List;

import javax.swing.SwingUtilities;

import org.wangfuyuan.sgs.card.AbstractCard;
import org.wangfuyuan.sgs.card.changed.Virtual_GuoHeChaiQiao;
import org.wangfuyuan.sgs.data.constant.Const_Game;
import org.wangfuyuan.sgs.data.enums.Colors;
import org.wangfuyuan.sgs.gui.main.Panel_Control;
import org.wangfuyuan.sgs.gui.main.Panel_Player;
import org.wangfuyuan.sgs.player.AbstractPlayer;
import org.wangfuyuan.sgs.player.PlayerIF;
import org.wangfuyuan.sgs.skills.SkillIF;
/**
 * 甘宁技能【奇袭】
 * @author user
 *
 */
public class GanNing_qixi implements Runnable,SkillIF{
	AbstractPlayer player;
	public GanNing_qixi(AbstractPlayer p){
		this.player = p;
	}
	
	@Override
	public void run() {
		final Panel_Control pc = (Panel_Control) player.getPanel();
		//显示所有黑色牌
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				pc.getHand().unableToUseCard();
				pc.getHand().remindToUse(Colors.HEITAO,Colors.MEIHUA);
				pc.getHand().setSelectLimit(1);
				pc.getHand().enableToClick();
				pc.getHand().setTargetCheck(false);
				// 遍历 检测
				List<Panel_Player> list = pc.getHand().getMain().getPlayers();
				for (int i = 0; i < list.size(); i++) {
					Panel_Player pp = list.get(i);
					// 如果无手牌或者装备牌
					if (pp.getPlayer().getState().getCardList().isEmpty()
							&& pp.getPlayer().getState().getEquipment().isEmpty()) {
						pp.disableToUse();
						continue;
					}
					pp.enableToUse();
				}
			}
		});
		//等待选择
		while(true){
			if(player.getState().getRes()==Const_Game.OK){
				AbstractCard c = pc.getHand().getSelectedList().get(0).getCard();
				//原有的牌丢弃
				c.throwIt(player);
				//新出一张虚拟过河拆桥
				new Virtual_GuoHeChaiQiao(c).use(player, pc.getHand().getTarget().getList().get(0));
				break;
			}
			if(player.getState().getRes()==Const_Game.CANCEL){
				player.refreshView();
				break;
			}
		}
		//如果在回合中，就解回合锁
		if(player.getStageNum()==PlayerIF.STAGE_USECARDS){
			synchronized (player.getProcess()) {
				player.getState().setRes(0);
				player.getProcess().notify();
			}
		}		
	}

	@Override
	public String getName() {
		return "奇袭";
	}

	@Override
	public boolean isEnableUse() {
		return false;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
