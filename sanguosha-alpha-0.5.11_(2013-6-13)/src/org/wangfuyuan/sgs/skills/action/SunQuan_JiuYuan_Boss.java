package org.wangfuyuan.sgs.skills.action;

import org.wangfuyuan.sgs.player.AbstractPlayer;
import org.wangfuyuan.sgs.player.impl.P_Action;
import org.wangfuyuan.sgs.skills.LockingSkillIF;

/**
 * 孙权【救援】
 * @author user
 *
 */
public class SunQuan_JiuYuan_Boss extends P_Action implements LockingSkillIF{
	
	public SunQuan_JiuYuan_Boss(AbstractPlayer p) {
		super(p);
	}

	/**
	 * 重写被别人治疗
	 */
	@Override
	public void taoSave(AbstractPlayer p) {
		super.taoSave(p);
		if(p!=player && p.getInfo().getCountry()==player.getInfo().getCountry()){
			player.getAction().addHP(1);
		}
	}

	@Override
	public String getName() {
		return "救援";
	}
}
