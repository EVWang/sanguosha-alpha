package org.wangfuyuan.sgs.card.kits;

import java.util.List;

import org.wangfuyuan.sgs.card.AbstractCard;
import org.wangfuyuan.sgs.card.DelayKitIF;
import org.wangfuyuan.sgs.data.enums.Colors;
import org.wangfuyuan.sgs.player.AbstractPlayer;
import org.wangfuyuan.sgs.service.ModuleManagement;
import org.wangfuyuan.sgs.service.ViewManagement;

/**
 * 闪电
 * 
 * @author user
 * 
 */
public class Card_ShanDian extends AbstractKitCard implements DelayKitIF {
	// 伤害
	final int DAMAGE = 3;
	// 当前拥有者
	AbstractPlayer owner;

	public Card_ShanDian() {

	}

	/**
	 * 重写use
	 */
	@Override
	public void use(final AbstractPlayer p, List<AbstractPlayer> players) {
		super.use(p, players);
		owner = p;
		// 牌堆收回
		ModuleManagement.getInstance().getGcList().remove(this);
		// 如果自身已经有闪电，就往下家传
		if (p.getState().hasDelayKit(type)) {
			pass();
			return;
		}
		// 目标判定区添加
		p.getState().getCheckedCardList().add(this);
		p.refreshView();
	}

	/**
	 * 技能发动
	 */
	@Override
	public void doKit() {
		askWuXieKeJi(owner, null);
		if (isWuXie) {
			ViewManagement.getInstance().printBattleMsg(getName() + "无效");
			ViewManagement.getInstance().refreshAll();
			pass();
			return;
		}
		AbstractCard cc = ModuleManagement.getInstance().showOneCheckCard();
		boolean flag = owner.getFunction().checkRollCard(cc, Colors.HEITAO)
				&& owner.getFunction().checkRollCard(cc, 9, 2);
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (flag) {
			ViewManagement.getInstance().printBattleMsg(getName() + "生效");
			owner.getAction().loseHP(DAMAGE, null);
			owner.getState().getCheckedCardList().remove(this);
			gc();
		} else {
			ViewManagement.getInstance().printBattleMsg(getName() + "失效");
			pass();
		}
	}

	/*
	 * 传递
	 */
	private void pass() {
		owner.getState().getCheckedCardList().remove(this);
		AbstractPlayer next = owner.getNextPlayer();
		// 若下家有闪电则给下下家
		if (next.getState().hasDelayKit(this.type))
			next = next.getNextPlayer();
		next.getState().getCheckedCardList().add(this);
		owner.refreshView();
		next.refreshView();
		owner = next;
	}

	@Override
	public String getShowNameInPanel() {
		return "电";
	}

	@Override
	public int getKitCardType() {
		return type;
	}
}
