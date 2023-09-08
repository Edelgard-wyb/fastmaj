package com.github.terralian.fastmaj.tehai;

import com.github.terralian.fastmaj.encode.EncodeMark;
import com.github.terralian.fastmaj.hai.IHai;
import org.junit.Test;



public class AdvisorTest {

    @Test
    public void testAdvise() {
        Advisor advisor = new Advisor(new FastSyatenCalculator());
        ITehai tehai = EncodeMark.toTehai("123m1234789p3388s");
//        Map<IHai, List<IHai>> advise = advisor.advise(tehai);
        advisor.printResult(tehai);
    }

}
