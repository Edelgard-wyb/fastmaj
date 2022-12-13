package com.github.terralian.fastmaj.test.yaku.h2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.terralian.fastmaj.agari.DivideInfo;
import com.github.terralian.fastmaj.agari.ITehaiAgariDivider;
import com.github.terralian.fastmaj.encode.EncodeMark;
import com.github.terralian.fastmaj.game.context.PlayerGameContext;
import com.github.terralian.fastmaj.hai.HaiPool;
import com.github.terralian.fastmaj.player.RivalEnum;
import com.github.terralian.fastmaj.tehai.ITehai;
import com.github.terralian.fastmaj.third.mjscore.MjscoreAdapter;
import com.github.terralian.fastmaj.yaku.IYaku;
import com.github.terralian.fastmaj.yaku.h2.Sanankou;

/**
 * {@link Sanankou} 三暗刻测试
 * 
 * @author terra.lian 
 */
public class SanankouTest {

    /**
     * 和了时，手牌分割器
     */
    private ITehaiAgariDivider tehaiAgariDivider;

    @Before
    public void prepare() {
        // 使用mjscore进行手牌分割
        tehaiAgariDivider = new MjscoreAdapter();
    }

    @Test
    public void test() {
        IYaku yaku = new Sanankou();
        // 不需要场况信息
        PlayerGameContext gameContext = null;
        // 手牌
        ITehai tehai = null;
        // 结果
        boolean result = false;

        ITehai ftehai = EncodeMark.toTehai("111222333m123p11z");
        List<DivideInfo> divideInfos = tehaiAgariDivider.divide(ftehai);
        result = divideInfos.stream().anyMatch(k -> yaku.match(ftehai, k, gameContext));
        assertTrue(result);

        // 并不是暗刻
        tehai = EncodeMark.toTehai("11222333m123p11z");
        tehai.pon(HaiPool.m(1), RivalEnum.BOTTOM);
        divideInfos = tehaiAgariDivider.divide(tehai);
        result = yaku.match(tehai, divideInfos.get(0), gameContext);
        assertFalse(result);

        // 四暗刻
        tehai = EncodeMark.toTehai("111333555m444p11z");
        divideInfos = tehaiAgariDivider.divide(tehai);
        result = yaku.match(tehai, divideInfos.get(0), gameContext);
        assertFalse(result);
    }
}
