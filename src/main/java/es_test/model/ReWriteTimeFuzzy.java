package es_test.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by lanpay on 2017/6/30.
 */
public class ReWriteTimeFuzzy {
    public ReWriteTimeFuzzy() {
    }

    public List<String> getFuzzyTime(Calendar calendar, List<String> args) {
        if(args.size() != 4) {
            return null;
        } else {
            List<String> reTime = new ArrayList();
            boolean isHalf = false;
            String begTime = null;
            String endTime = null;
            String theTime = "1";
            if(((String)args.get(1)).equals("半")) {
                isHalf = true;
            } else {
                //theTime = this.replaceChineseNumber((String)args.get(1));
            }

            String unit = (String)args.get(2);
            String around = (String)args.get(3);
            String begin = (String)args.get(0);
            //int tUnit = false;
            int nUnit = 1;
            byte tUnit;
            if(unit.equals("年")) {
                tUnit = 1;
                if(isHalf) {
                    tUnit = 2;
                    nUnit = 6;
                }
            } else if(unit.equals("月")) {
                tUnit = 2;
                if(isHalf) {
                    tUnit = 5;
                    nUnit = 15;
                }
            } else if(unit.equals("周")) {
                tUnit = 5;
                nUnit = 7;
                if(isHalf) {
                    nUnit = 4;
                }
            } else if(unit.equals("日")) {
                tUnit = 5;
                if(isHalf) {
                    tUnit = 11;
                    nUnit = 12;
                }
            } else if(unit.equals("小时")) {
                tUnit = 11;
                if(isHalf) {
                    tUnit = 12;
                    nUnit = 30;
                }
            } else if(unit.equals("分钟")) {
                tUnit = 12;
                if(isHalf) {
                    tUnit = 13;
                    nUnit = 30;
                }
            } else {
                if(!unit.equals("秒")) {
                    return null;
                }

                tUnit = 13;
            }

            if(around != null && !around.equals("以来") && !around.equals("内") && !around.equals("以后")) {
                if(!around.equals("之前") && !around.equals("以前") && !around.equals("前")) {
                    if(!begin.equals("后")) {
                        return null;
                    }

                    if(around != null && !around.equals("内")) {
                        if(!around.equals("以来") && !around.equals("以后")) {
                            if(around.equals("之前") || around.equals("以前") || around.equals("前")) {
                                calendar.add(tUnit, nUnit * Integer.valueOf(theTime).intValue());
                                endTime = String.valueOf(calendar.getTime().getTime());
                            }
                        } else {
                            begTime = String.valueOf(calendar.getTime().getTime());
                            calendar.add(tUnit, nUnit * Integer.valueOf(theTime).intValue());
                            endTime = String.valueOf(calendar.getTime().getTime());
                        }
                    } else {
                        calendar.add(tUnit, nUnit * Integer.valueOf(theTime).intValue());
                        begTime = String.valueOf(calendar.getTime().getTime());
                    }
                } else {
                    calendar.add(tUnit, 0 - nUnit * Integer.valueOf(theTime).intValue());
                    endTime = String.valueOf(calendar.getTime().getTime());
                }
            } else {
                endTime = String.valueOf(calendar.getTime().getTime());
                calendar.add(tUnit, 0 - nUnit * Integer.valueOf(theTime).intValue());
                begTime = String.valueOf(calendar.getTime().getTime());
            }

            reTime.add(begTime);
            reTime.add(endTime);
            return reTime;
        }
    }

    public String reWriteStr(String inputStr, List<String> args, int start, int end, String exArg) {
        Calendar calendar = Calendar.getInstance();
        List<String> reTime = this.getFuzzyTime(calendar, args);
        if(reTime == null) {
            return inputStr.substring(start, end);
        } else {
            String reString = "";
            if(reTime.get(0) != null) {
                reString = reString + " GE " + (String)reTime.get(0);
            }

            if(reTime.get(1) != null) {
                reString = reString + " LE " + (String)reTime.get(1) + " ";
            }

            if(reTime.get(0) == null && reTime.get(1) == null) {
                return inputStr.substring(start, end);
            } else {
                reString = this.getPropStr(exArg, (String)null, reString);
                return reString;
            }
        }
    }

    public String getPropStr(String prop, String key, String value) {
        String str = " ";
        /*String[] arrStr;
        if(prop.equals(RunReWrite.TIME_ALL)) {
            arrStr = (String[])((String[])RunReWrite.timeProp.toArray(new String[RunReWrite.timeProp.size()]));
            str = this.getOrPropStr(arrStr, key, value);
        } else {
            arrStr = prop.split("\\|");
            if(arrStr.length == 1) {
                str = str + prop + " " + value + " ";
            } else {
                str = this.getOrPropStr(arrStr, key, value);
            }
        }*/

        return str;
    }
}
