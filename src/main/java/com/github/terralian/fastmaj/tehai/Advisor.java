package com.github.terralian.fastmaj.tehai;

import com.github.terralian.fastmaj.encode.Encode34;
import com.github.terralian.fastmaj.encode.EncodeMark;
import com.github.terralian.fastmaj.hai.HaiPool;
import com.github.terralian.fastmaj.hai.IHai;
import javafx.util.Pair;
import lombok.SneakyThrows;

import java.util.*;
import java.util.stream.Collectors;

public class Advisor {
    /**
     * 向听数计算
     */
    private final ISyatenCalculator syatenCalculator;

    /**
     * 构建有效牌计算实例
     */
    public Advisor() {
        this.syatenCalculator = new FastSyatenCalculator();
    }

    /**
     * 构建有效牌计算实例
     */
    public Advisor(ISyatenCalculator syatenCalculator) {
        this.syatenCalculator = syatenCalculator;
    }

    /**
     * 计算最小向听数的有效待牌
     *
     * @param tehai 手牌
     */
    public Set<IHai> calcMin(ITehai tehai) {
        if (tehai.isNaki() || tehai.isAnnkan()) {
            return calcNormal(tehai);
        }

        List<IHai> hand = tehai.getHand();
        int kokusiSyaten = syatenCalculator.calcKokusi(hand);
        int tiitoituSyaten = syatenCalculator.calcTiitoitu(hand);
        int normalSyaten = syatenCalculator.calcNormal(hand);

        int minSyaten = Math.min(kokusiSyaten, Math.min(tiitoituSyaten, normalSyaten));
        Set<IHai> minYuukouhais = new HashSet<>();
        if (kokusiSyaten == minSyaten) {
            minYuukouhais.addAll(calcKokusi(tehai));
        }
        if (tiitoituSyaten == minSyaten) {
            minYuukouhais.addAll(calcTiitoitu(tehai));
        }
        if (normalSyaten == minSyaten) {
            minYuukouhais.addAll(calcNormal(tehai));
        }
        return minYuukouhais;
    }

    /**
     * 计算七对子的有效待牌
     *
     * @param tehai 手牌
     */
    public Set<IHai> calcTiitoitu(ITehai tehai) {
        Set<IHai> yuukouhais = new HashSet<>();
        if (tehai.isNaki() || tehai.isAnnkan()) {
            return yuukouhais;
        }
        int[] value34 = Encode34.toEncode34(tehai.getAll());
        int currentSyaten = syatenCalculator.calcTiitoitu(value34);
        List<IHai> values = tehai.getAll();
        for (IHai hai : values) {
            value34[hai.getValue()]++;
            if (syatenCalculator.calcTiitoitu(value34) < currentSyaten) {
                yuukouhais.add(hai);
            }
            value34[hai.getValue()]--;
        }
        return yuukouhais;
    }

    /**
     * 计算国士的有效待牌
     *
     * @param tehai 手牌
     */
    public Set<IHai> calcKokusi(ITehai tehai) {
        Set<IHai> yuukouhais = new HashSet<>();
        if (tehai.isNaki() || tehai.isAnnkan()) {
            return yuukouhais;
        }
        int[] value34 = Encode34.toEncode34(tehai.getAll());
        int currentSyaten = syatenCalculator.calcKokusi(value34);
        for (int i = 0; i < Encode34.YAOTYU_HAIS.length; i++) {
            int yaotyuIndex = Encode34.YAOTYU_HAIS[i];
            value34[yaotyuIndex]++;
            if (syatenCalculator.calcKokusi(value34) < currentSyaten) {
                yuukouhais.add(HaiPool.getByValue(yaotyuIndex));
            }
            value34[yaotyuIndex]--;
        }
        return yuukouhais;
    }

    /**
     * 计算通常的有效待牌
     * <p/>
     * 即除了国士，七对外的通常手型
     *
     * @param tehai 手牌
     */
    public Set<IHai> calcNormal(ITehai tehai) {
        int[] value34 = Encode34.toEncode34(tehai.getHand());
        int[] notKoritu = calcNotKoritu(tehai.getHand());
        int syaten = syatenCalculator.calcNormal(value34);
        int index = 0;

        Set<IHai> yuukouHais = new HashSet<>();
        while (notKoritu[index] >= 0) {
            // 非孤立牌计算出的结果，不包含手牌已到4枚的信息
            // 若手牌数量已到达4枚，则不会存在第五枚，需要跳过这种情况
            int nIndex = notKoritu[index];
            if (value34[nIndex] < 4) {
                value34[nIndex]++;
                if (syatenCalculator.calcNormal(value34) < syaten) {
                    yuukouHais.add(HaiPool.getByValue(notKoritu[index]));
                }
                value34[nIndex]--;
            }
            index++;
        }
        return yuukouHais;
    }

    @SneakyThrows
    public Map<IHai,List<IHai>> advise(ITehai tehai) {
        int[] value34 = Encode34.toEncode34(tehai.getHand());
        int syaten = syatenCalculator.calcNormal(value34);
        List<List<Cards>>input=new ArrayList<>();
        for(int i=0;i<value34.length;i++){
            if(value34[i]>0){
                List<Cards>list=new ArrayList<>();
                Cards cards=new Cards(syatenCalculator,tehai,i,syaten);
                list.add(cards);
                input.add(list);
            }
        }
        MultiThread multiThread=new MultiThread(input);
        List<Pair> result = multiThread.getResult();
        Map<IHai,List<IHai>>iHaiListMap=new HashMap<>();
        for(Pair pair:result){
            iHaiListMap.put((IHai)pair.getKey(),(List<IHai>)pair.getValue());
        }
        return iHaiListMap;
    }

    public void printResult(ITehai tehai){
        int[] value34 = Encode34.toEncode34(tehai.getHand());
        Map<IHai,List<IHai>>iHaiListMap=advise(tehai);

        int syaten = syatenCalculator.calcNormal(value34);

        Map<IHai, List<IHai>> sortedMap =countPieces(iHaiListMap);

        for(Map.Entry<IHai,List<IHai>> entry:sortedMap.entrySet()){
            StringBuilder sb=new StringBuilder("【");
            IHai key = entry.getKey();
            List<IHai> value = entry.getValue();
            int count =  value.size()*4;
            for(IHai iHai:value){
                count=count-value34[iHai.getValue()];
                sb.append(iHai.toString()+",");
            }
            sb.append("】");

            if(syaten==1){
                System.out.println("听牌!!!");
            }
            System.out.println("打"+key.toString()+"---摸"+sb+"---共"+count+"枚");
        }
    }

    public Map<IHai, List<IHai>>countPieces( Map<IHai,List<IHai>>iHaiListMap){
        return  iHaiListMap.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(entry -> ((Map.Entry<IHai,List<IHai>>)entry).getValue().size()).reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    /**
     * 计算手牌的非孤立牌（指某张牌和手牌中的一枚牌可以形成搭子）
     *
     * @param hais 手牌
     */
    private int[] calcNotKoritu(List<IHai> hais) {
        boolean[] work = new boolean[Encode34.length()];
        Arrays.fill(work, false);
        for (IHai hai : hais) {
            // 字牌
            if (hai.isJiHai()) {
                work[hai.getValue()] = true;
            } else { // 数牌
                int literal = EncodeMark.clearRed(hai.getLiteral());
                int i = hai.getValue();
                if (literal == 1)
                    work[i] = work[i + 1] = work[i + 2] = true;
                else if (literal == 2)
                    work[i] = work[i + 1] = work[i + 2] = work[i - 1] = true;
                else if (literal >= 3 && literal <= 7)
                    work[i] = work[i - 1] = work[i + 1] = work[i - 2] = work[i + 2] = true;
                else if (literal == 8)
                    work[i] = work[i + 1] = work[i - 2] = work[i - 1] = true;
                else if (literal == 9)
                    work[i] = work[i - 1] = work[i - 2] = true;
            }
        }

        int[] notKoritu = new int[Encode34.length()];
        Arrays.fill(notKoritu, -1);
        int j = 0;
        for (int i = 0; i < work.length; i++) {
            if (!work[i])
                continue;
            notKoritu[j] = i;
            j++;
        }
        return notKoritu;
    }
}
