package com.github.terralian.fastmaj.test.paifu.tenhou;

import com.github.terralian.fastmaj.paifu.IPaifuParser;
import com.github.terralian.fastmaj.paifu.domain.PaifuGame;
import com.github.terralian.fastmaj.paifu.tenhou.TenhouPaifuGameParseHandler;
import com.github.terralian.fastmaj.paifu.tenhou.TenhouPaifuParser;
import com.github.terralian.fastmaj.util.TestResourceUtil;
import org.junit.Test;

public class TenhouPaifuParserTest {

    @Test
    public void isGeneric_Success() throws Exception {
        IPaifuParser<PaifuGame> paifuParser = new TenhouPaifuParser(new TenhouPaifuGameParseHandler());
        PaifuGame paifuGame = paifuParser.parseFile(TestResourceUtil.readToFile("tenhou", "2021100323gm-00a9-0000-d0ac1302&tw=1.mjlog"));
        System.out.println(paifuGame.getSeed());
    }
}
