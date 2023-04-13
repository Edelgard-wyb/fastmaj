package com.github.terralian.fastmaj.tehai;

import java.util.List;

import com.github.terralian.fastmaj.encode.EncodeMark;
import com.github.terralian.fastmaj.hai.IHai;
import com.github.terralian.fastmaj.player.RivalEnum;
import com.github.terralian.fastmaj.util.Assert;

/**
 * 手牌建造者，用于便捷构建和操作{@link Tehai}
 * <p/>
 * {@link ITehai}设计上仅使用IHai作为传输对象，这么做令接口保持简洁，但也使对人类友好上较差。
 * 这也是该类出现的目的，为了提供更好的构建和操作{@link Tehai}的方法。
 * <p/>
 *
 * @see ITehai
 * @see Tehai
 */
public class TehaiBuilder {

    private final ITehai tehai;

    /**
     * 根据一个记号法串初始化建造者（工厂方法）
     *
     * @param nameMark 记号法串，如“1234s”
     */
    public static TehaiBuilder from(String nameMark) {
        return new TehaiBuilder(nameMark);
    }

    /**
     * 初始化构建一个建造者
     */
    public TehaiBuilder() {
        tehai = new Tehai();
    }

    /**
     * 根据一个已有的手牌初始化建造者
     *
     * @param tehai 手牌
     */
    public TehaiBuilder(ITehai tehai) {
        this.tehai = tehai;
    }

    /**
     * 根据一个记号法串初始化建造者
     *
     * @param nameMark 记号法串，如“1234s”
     */
    public TehaiBuilder(String nameMark) {
        this();
        addAll(nameMark);
    }

    /**
     * 获取构建的{@link ITehai}
     */
    public ITehai get() {
        return tehai;
    }

    // -----------------------------------------------
    // EncodeMark
    // -----------------------------------------------

    /**
     * 为手牌增加不限数量的N个牌
     *
     * @param nameMark 不限数量的记号法
     */
    public TehaiBuilder addAll(String nameMark) {
        List<IHai> hais = EncodeMark.toHai(nameMark);
        for (IHai hai : hais) {
            tehai.draw(hai);
        }
        return this;
    }

    /**
     * 为手牌增加一副面子（由3枚牌组成），如addMenzu("123s")
     * <p/>
     * 该方法不会校验手牌超过了14枚的限制，但是会校验面子参数必须是三枚牌
     *
     * @param menzu 面子，采用{@link EncodeMark}编码
     * @see EncodeMark
     */
    public TehaiBuilder addMenzu(String menzu) {
        List<IHai> hais = EncodeMark.toHai(menzu);
        Assert.isTrue(hais.size() == 3, "面子需要仅为三枚牌组成：" + menzu);
        for (IHai hai : hais)
            tehai.draw(hai);
        return this;
    }

    /**
     * 为手牌增加多张相同的牌，如addSame("1s", 3)
     *
     * @param oneMark 单枚牌记号法
     * @param size 大小，[1, 4]
     */
    public TehaiBuilder addSame(String oneMark, int size) {
        IHai hai = EncodeMark.toHaiOne(oneMark);
        return addSame(hai, size);
    }

    /**
     * 为手牌直接增加一副吃的附露，如addChi("1s", "23s")
     *
     * @param chiHaiMark 吃的牌
     * @param selfHaiMark 自己的牌
     */
    public TehaiBuilder addChi(String chiHaiMark, String selfHaiMark) {
        List<IHai> selfHais = EncodeMark.toHai(selfHaiMark);
        Assert.isTrue(selfHais.size() == 2, "搭子需要仅为2枚牌组成：" + selfHaiMark);

        IHai chiHai = EncodeMark.toHaiOne(chiHaiMark);
        for (IHai hai : selfHais) {
            tehai.draw(hai);
        }
        tehai.chii(chiHai, selfHais.get(0), selfHais.get(1));
        return this;
    }

    /**
     * 为手牌增加一副碰的附露
     *
     * @param oneMark 单枚牌记号法，如1s
     * @param ponFrom 从哪个对手碰的牌，影响形状
     */
    public TehaiBuilder addPon(String oneMark, RivalEnum ponFrom) {
        IHai hai = EncodeMark.toHaiOne(oneMark);
        addSame(hai, 2);
        tehai.pon(hai, ponFrom);
        return this;
    }

    /**
     * 为手牌增加一副碰的附露，默认从对家碰牌
     *
     * @param oneMark 单枚牌记号法，如1s
     */
    public TehaiBuilder addPon(String oneMark) {
        return addPon(oneMark, RivalEnum.OPPO);
    }

    /**
     * 为手牌增加一副明杠附露
     *
     * @param oneMark 单枚牌记号法，如1s
     * @param kanFrom 从哪个对手杠的牌，影响形状
     */
    public TehaiBuilder addMinkan(String oneMark, RivalEnum kanFrom) {
        IHai hai = EncodeMark.toHaiOne(oneMark);
        addSame(hai, 3);
        tehai.minkan(hai, kanFrom);
        return this;
    }

    /**
     * 为手牌增加一副明杠附露，默认从对家杠牌
     *
     * @param oneMark 单枚牌记号法，如1s
     */
    public TehaiBuilder addMinkan(String oneMark) {
        addMinkan(oneMark, RivalEnum.OPPO);
        return this;
    }

    /**
     * 为手牌增加一副暗杠的附露
     *
     * @param oneMark 单枚牌记号法，如1s
     */
    public TehaiBuilder addAnnkan(String oneMark) {
        IHai hai = EncodeMark.toHaiOne(oneMark);
        addSame(hai, 4);
        tehai.annkan(hai);
        return this;
    }

    // -----------------------------------------------
    // IHai
    // -----------------------------------------------

    /**
     * 为手牌增加多张相同的牌
     *
     * @param hai 手牌
     * @param size 数量[1, 4]
     */
    public TehaiBuilder addSame(IHai hai, int size) {
        Assert.isTrue(size >= 0 && size <= 4, "增加的牌数量需要在[1,4]内：" + size);
        for (int i = 0; i < size; i++) {
            tehai.draw(hai);
        }
        return this;
    }
}
