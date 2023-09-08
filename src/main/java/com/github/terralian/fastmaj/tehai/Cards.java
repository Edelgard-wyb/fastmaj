package com.github.terralian.fastmaj.tehai;

import com.github.terralian.fastmaj.encode.Encode34;
import com.github.terralian.fastmaj.hai.HaiPool;
import com.github.terralian.fastmaj.hai.IHai;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Cards {
    public int[] value34;
    public int syaten;
    public int index;
    public ISyatenCalculator syatenCalculator;

    public Cards(ISyatenCalculator syatenCalculator,ITehai tehai,int index,int syaten){
        value34 = Encode34.toEncode34(tehai.getHand());
        this.syaten = syaten;
        this.index=index;
        this.syatenCalculator=syatenCalculator;
    }

    public Pair<IHai,List<IHai>> onExecute(){
        List<IHai>iHais=new ArrayList<>();
        Pair result=new Pair(HaiPool.getByValue(index),iHais);
        value34[index]--;
        for(int i=0;i<value34.length;i++){
            if(i==index){
                continue;
            }

            value34[i]++;
            if(syatenCalculator.calcNormal(value34)<syaten){
                iHais.add(HaiPool.getByValue(i));
            }
            value34[i]--;
        }
        return result;
    }
}
