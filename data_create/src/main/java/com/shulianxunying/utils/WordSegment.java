package com.shulianxunying.utils;

import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 19866 on 2017/3/24.
 */
public class WordSegment {
    public void test() throws IOException {
        String s = "最希望从企业得到的是独家的内容或销售信息，获得打折或促销信息等；最不希望企业进行消息或广告轰炸及访问用户的个人信息等。这值得使用社会化媒体的企业研究";
        queryWords(s);
    }
    public static ArrayList<String> queryWords(String line) throws IOException {
        return queryWords(line,true);
    }
    static String regex = "\\d+";

    public static ArrayList<String> queryWords(String line,Boolean isUseSmart) throws IOException {
        ArrayList<String> resultList = new ArrayList<>();
        Configuration cfg = DefaultConfig.getInstance();
        cfg.setUseSmart(isUseSmart); // true 用智能分词 ，false细粒度
        StringReader input = new StringReader(line.trim());
        IKSegmenter ikSeg = new IKSegmenter(input, cfg);
        Pattern pattern = Pattern.compile(regex);

        for (Lexeme lexeme = ikSeg.next(); lexeme != null; lexeme = ikSeg.next()) {
            String word = lexeme.getLexemeText();
            Matcher matcher = pattern.matcher(word);
            if(word.length()>1 && !matcher.find())
                resultList.add(word);
        }
        return resultList;
    }
    public static void main(String[] args) throws IOException {
        new WordSegment().test();
    }
}
