package es_test.model;

/**
 * Created by lanpay on 2017/6/30.
 */


import java.util.ArrayList;
import java.util.List;


public class RegUnit {
    public String reg;
    public String func;
    public int pos;
    public int argsNum;
    public int isSeg;
    public List<Integer> args = new ArrayList();
    public int startGroupId;
    public int endGroupId;
    public String exArg;

    public RegUnit() {
    }
}

