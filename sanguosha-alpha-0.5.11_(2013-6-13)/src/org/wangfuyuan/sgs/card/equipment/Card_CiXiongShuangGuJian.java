package org.wangfuyuan.sgs.card.equipment;

import javax.swing.SwingUtilities;

import org.wangfuyuan.sgs.card.AbstractCard;
import org.wangfuyuan.sgs.data.constant.Const_Game;
import org.wangfuyuan.sgs.data.enums.Colors;
import org.wangfuyuan.sgs.gui.main.Panel_Control;
import org.wangfuyuan.sgs.gui.main.Panel_HandCards;
import org.wangfuyuan.sgs.player.AbstractPlayer;
import org.wangfuyuan.sgs.service.ViewManagement;

/**
 * 雌雄双股剑
 * @author user
 *
 */
public class Card_CiXiongShuangGuJian extends AbstractWeaponCard{
	//两者选择，true为摸牌，false为弃牌
	final boolean MOPAI = true;
	final boolean QIPAI = false;
	boolean flag ;
	AbstractCard card;
	
	Panel_Control pc;
	Panel_HandCards ph;
	
	public Card_CiXiongShuangGuJian(){
		
	}
	public Card_CiXiongShuangGuJian(int id, int number, Colors color) {
		super(id, number, color);
	}

	/**
	 * 重写杀前技能
	 */
	@Override
	public void useSkillBeforeSha(AbstractPlayer p, AbstractPlayer target) {
		super.useSkillBeforeSha(p, target);
		//异性触发
		if(p.getFunction().isSameSex(target)){
			return;
		}else{
			ViewManagement.getInstance().printBattleMsg(p.getInfo().getName()+"发动"+getName());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//目标无手牌直接摸牌
			if(target.getState().getCardList().size()==0){
				execute(p,target,MOPAI);
				return;
			}else{
				//默认AI只让玩家摸牌
				if(target.getState().isAI()){					
					execute(p,target,MOPAI);
				}else{
					pc = (Panel_Control) target.getPanel();
					ph = pc.getHand();
					SwingUtilities.invokeLater(ask);
					while(true){
						if(target.getState().getRes()==Const_Game.OK){
							if(ph.getSelectedList().isEmpty()){
								target.getState().setRes(0);
								continue;
							}else{
								card =ph.getSelectedList().get(0).getCard();
								target.getState().setRes(0);
								execute(p, target, QIPAI);
								break ;
							}
						}
						if(target.getState().getRes()==Const_Game.CANCEL){
							target.getState().setRes(0);
							execute(p, target, MOPAI);
							break ;
						}
					}
				}
				ViewManagement.getInstance().getPrompt().clear();
				return;
			}
		}
	}
	
	private void execute(AbstractPlayer p, AbstractPlayer target,boolean flag){
		if(flag){
			p.getAction().addOneCardFromList();
		}else{
			//target.getState().getCardList().get(0).throwIt(target);
			card.throwIt(target);
		}
	}
	Runnable ask = new Runnable() {
		
		@Override
		public void run() {
			
			ph.remindToUseALL();
			ph.disableClick();
			ph.enableOKAndCancel();
			ph.setTargetCheck(false);
			ViewManagement.getInstance().getPrompt().show_Message(
					"选1张牌丢弃,或取消则对方摸1张牌" );

		}
		
	};
}
