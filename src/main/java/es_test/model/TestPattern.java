package es_test.model;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lanpay on 2017/6/30.
 */
public class TestPattern {

    public static void main(String[] args) {
        testPattern1();
        testPattern2();
        testPattern3();
        testPattern4();
        testPattern5();
    }

    private Properties properties = null;
    public List<String> replaceReg = new ArrayList();
    private int regNum;
    private List<RegUnit> regs = new ArrayList();
    private String regStr;
    private Pattern patall;
    Map<String, Object> mapFunc = new HashMap();

    public TestPattern() {
        init();
    }

    private void init() {
        this.properties = new Properties();
        InputStream in = null;
        File file = new File("conf/rewrite.properties");
        if(!file.exists()) {
            file = new File("rewrite.properties");
        }

        try {
            if(file.exists()) {
                in = new BufferedInputStream(new FileInputStream(file));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(in == null) {
            in = TestPattern.class.getResourceAsStream("/rewrite.properties");
        }

        if(in != null) {
            try {
                BufferedReader bf = new BufferedReader(new InputStreamReader(in));
                this.properties.load(bf);
                int replaceNum = Integer.valueOf(this.properties.getProperty("REPLACE_NUM")).intValue();

                int synNum;
                for (synNum = 0; synNum < replaceNum; ++synNum) {
                    String tmpStr = this.properties.getProperty("REPLACE_" + synNum);
                    this.replaceReg.add(tmpStr);
                }

                this.regNum = Integer.valueOf(this.properties.getProperty("REG_NUM")).intValue();
                String reg;
                for(int i = 0; i < this.regNum; ++i) {
                    String strMatch = this.properties.getProperty("REG_" + i);
                    String[] arrStr = strMatch.split("\t");
                    if(arrStr.length < 6) {
                        System.out.println("warn: initConfig:get REG_" + i + "length is " + arrStr.length + "<5");
                    } else {
                        reg = this.getReplaceReg(arrStr[0]);
                        RegUnit regtmp = this.addReg(reg, arrStr[1], arrStr[2], arrStr[3], arrStr[4], arrStr[5]);
                        if(regtmp != null) {
                            this.regs.add(regtmp);
                        }
                    }
                }
                this.patall = Pattern.compile(this.regStr);

                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getReplaceReg(String strline) {
        for(int i = this.replaceReg.size() - 1; i >= 0; --i) {
            strline = strline.replace("REPLACE_" + i, (CharSequence)this.replaceReg.get(i));
        }

        return strline;
    }

    public RegUnit addReg(String reg, String func, String argList, String type, String exArg, String isSeg) {
        RegUnit regtmp = new RegUnit();
        regtmp.reg = reg;
        regtmp.func = func;
        regtmp.exArg = exArg;
        regtmp.isSeg = Integer.valueOf(isSeg).intValue();
        if (!this.mapFunc.containsKey(regtmp.func)) {
            //logger.warn("initConfig:init func faild:" + regtmp.func);
            System.out.println("initConfig:init func faild:" + regtmp.func);
            return null;
        } else {
            String[] var8 = argList.split(",");
            int var9 = var8.length;

            for (int var10 = 0; var10 < var9; ++var10) {
                String str = var8[var10];
                regtmp.args.add(Integer.valueOf(str));
            }

            regtmp.argsNum = this.parseRegNum(reg);
            if (this.regStr == null) {
                regtmp.pos = 1;
                this.regStr = "(" + reg + ")";
            } else {
                regtmp.pos = ((RegUnit) this.regs.get(this.regs.size() - 1)).pos + ((RegUnit) this.regs.get(this.regs.size() - 1)).argsNum + 1;
                this.regStr = this.regStr + "|(" + reg + ")";
            }

            if (type.equals("1")) {
                regtmp.startGroupId = regtmp.endGroupId = regtmp.pos;
            } else {
                regtmp.startGroupId = regtmp.pos + ((Integer) regtmp.args.get(0)).intValue();
                regtmp.endGroupId = regtmp.pos + ((Integer) regtmp.args.get(regtmp.args.size() - 1)).intValue();
            }

            return regtmp;
        }
    }

    public int parseRegNum(String regStr) {
        int pos = 0;
        int numPos = 0;

        int numOutPos;
        for(numOutPos = 0; (pos = regStr.indexOf("(", pos)) >= 0; ++numPos) {
            ++pos;
        }

        for(pos = 0; (pos = regStr.indexOf("\\(", pos)) >= 0; ++numOutPos) {
            pos += 2;
        }

        for(pos = 0; (pos = regStr.indexOf("(?", pos)) >= 0; ++numOutPos) {
            pos += 2;
        }

        numPos -= numOutPos;
        return numPos;
    }





    /**
     * Removes whitespace between a word character and . or ？
     * */
    public static void testPattern1() {
        String regex = "(\\w)(\\s+)([\\.?])";
        String str = "This is a big mistake . What do you think ?";
        Pattern pattern = Pattern.compile(regex);
        System.out.println("> testPattern1");
        System.out.println(str.replaceAll(regex, "$1$3"));
        System.out.println("");
    }

    /**
     * Removes all <title>
     * */
    public static void testPattern2() {
        String regex = "(?i)(<title.*?>)(.+?)()";
        String str = "This is a big <title>mistake<title>. <title>What do you think ?";
        String str1 = "<title> hello world.";
        Pattern pattern = Pattern.compile(regex);
        System.out.println("> testPattern2");
        System.out.println(str1.replaceAll(regex, "$2"));
        System.out.println("");
    }

    /**
     * replace "a" to "666" if "a" is not followed by "b"
     * */
    public static void testPattern3() {
        String regex = "a(?!b)";
        String str = "ax ay ab";
        System.out.println("> testPattern3");
        System.out.println(str.replaceAll(regex, "666"));
        System.out.println("");
    }

    /**
     * replace "a" to "666" if "a" is not followed by "b"
     * */
    public static void testPattern4() {
        String regex = "[（）,。，、？ 　’＇；;\r\n\t!！的]+";
        String str = "你好、他好， （开玩笑） ！！ 真的吗";
        System.out.println("> testPattern4");
        System.out.println(str.replaceAll(regex, " "));
        System.out.println("");
    }

    /**
     * replace "a" to "666" if "a" is not followed by "b"
     * */
    public static void testPattern5() {
        String regex = "\\w+";
        String str = "how i met your mother.";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        System.out.println("> testPattern5");
        while(matcher.find()){
            System.out.print("Start index: " + matcher.start());
            System.out.print(" End index: " + matcher.end() + " ");
            System.out.println(matcher.group());
            matcher.group();
        }
        System.out.println("");
    }
}
