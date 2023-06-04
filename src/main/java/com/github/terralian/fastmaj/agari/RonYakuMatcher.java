package com.github.terralian.fastmaj.agari;

import com.github.terralian.fastmaj.game.context.PlayerGameContext;
import com.github.terralian.fastmaj.hai.IHai;
import com.github.terralian.fastmaj.tehai.ITehai;
import com.github.terralian.fastmaj.yaku.IYaku;
import com.github.terralian.fastmaj.yaku.IYakuMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * 可以荣和的役匹配，默认实现
 *
 * @author terra.lian
 */
public class RonYakuMatcher implements IRonYakuMatcher {

    /**
     * 役匹配器，有值时使用指定的役匹配器。若为指定，则使用默认的
     * <p/>
     * 通过更改役匹配器，可以决定规则使用什么役种，比如是否启用人和，古役。
     */
    private IYakuMatcher yakuMatcher;
    /**
     * 和了时，手牌分割器
     */
    private ITehaiAgariDivider tehaiAgariDivider;

    /**
     * 根据{@link IYakuMatcher}和{@link ITehaiAgariDivider}构建荣和役牌匹配器
     *
     * @param yakuMatcher
     * @param divider
     */
    public RonYakuMatcher(IYakuMatcher yakuMatcher, ITehaiAgariDivider divider) {
        this.yakuMatcher = yakuMatcher;
        this.tehaiAgariDivider = divider;
    }

    @Override
    public List<IYaku> match(ITehai source, IHai agariHai, PlayerGameContext context) {
        ITehai tehai = source.deepClone();
        tehai.draw(agariHai);
        List<DivideInfo> divideInfos = tehaiAgariDivider.divide(tehai);

        List<IYaku> yakus = new ArrayList<>();
        int ban = 0;
        for (DivideInfo divideInfo : divideInfos) {
            List<IYaku> tmpYakus = yakuMatcher.match(tehai, divideInfo, context);
            int tmpBan = tmpYakus.stream().mapToInt(k -> k.getHan(context.isNaki())).sum();
            if (ban == 0 || tmpBan > ban) {
                yakus = tmpYakus;
                ban = tmpBan;
            }
        }
        return yakus;
    }

    public IYakuMatcher getYakuMatcher() {
        return yakuMatcher;
    }

    public void setYakuMatcher(IYakuMatcher yakuMatcher) {
        this.yakuMatcher = yakuMatcher;
    }

    public ITehaiAgariDivider getTehaiAgariDivider() {
        return tehaiAgariDivider;
    }

    public void setTehaiAgariDivider(ITehaiAgariDivider tehaiAgariDivider) {
        this.tehaiAgariDivider = tehaiAgariDivider;
    }
}
