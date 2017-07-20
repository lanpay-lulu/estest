package item;

import estool.XCBuildable;
import org.elasticsearch.common.recycler.Recycler;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by lanpay on 2017/7/19.
 */
public class Person implements XCBuildable{
    public Long uid;
    public String id;
    public String name;
    public String desc;
    public String idCardNumber;
    public String mobileNumber;
    public Date birthday;
    public short gender;


    static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    static Random rand = new Random();
    static Calendar calendar = Calendar.getInstance();
    static String[] PNums = {"315","248","702","933","218","686","186","575","291",
            "123","249","567","095","111","798","473","711","004","105","712"};
    static String[] HWord = {"赵","钱","孙","李","周","吴","郑","王","冯","陈","褚","卫",
            "蒋","沈","韩","杨","朱","秦","尤","许","何","吕","施","张","孔","曹","严","华",
            "金","魏","陶","姜","丁","宣","贲","邓","郁","单","杭","洪","包","诸",""};
    static {
        calendar.setTime(new Date());
    }

    public Person(Long uid) {
        this.uid = uid;
        this.id = Long.toHexString(uid);
        int r = rand.nextInt(3000);
        int r1 = rand.nextInt(10000);
        this.desc = genDesc(uid, r);
        this.name = HWord[r%HWord.length] + HWord[r1%HWord.length]+ HWord[(r+r1)%HWord.length];
        this.idCardNumber = "420"+PNums[(r+1)%PNums.length]+PNums[(r1+1)%PNums.length]+
                PNums[(r+2)%PNums.length]+PNums[(r1+2)%PNums.length]+PNums[(r+r1)%PNums.length];

        this.mobileNumber = "13"+PNums[r%PNums.length]+PNums[(r1)%PNums.length]+PNums[(r+r1)%PNums.length];
        this.gender = (short) (r % 2); // 0,1
        if(r % 101 == 1){
            this.gender = 2;
        }
        long days = 86400 * 1000 * r; // r days
        this.birthday = new Date(System.currentTimeMillis()-days);
    }

    public Person(Long uid, String name, String idCardNumber, String mobileNumber, short gender, Date birthday) {
        this.uid = uid;
        this.id = Long.toHexString(uid);
        this.name = name;
        this.desc = genDesc(uid, gender);
        this.idCardNumber = idCardNumber;
        this.mobileNumber = mobileNumber;
        this.gender = gender;
        this.birthday = birthday;
    }

    public XContentBuilder toXCB() {
        XContentBuilder contentBuilder = null;
        try {
            contentBuilder = jsonBuilder();

            contentBuilder.startObject()
                    .field("uid", uid)
                    .field("name", name)
                    .field("idCardNumber", idCardNumber)
                    .field("mobileNumber", mobileNumber)
                    .field("gender", gender)
                    .field("birthday", SDF.format(birthday))
                    .endObject();

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error occured while creating product gift json document!", ex);
        }
        return contentBuilder;
    }

    /*
    {
        "properties" : {
            "uid" : { "type" : "long" },
            "name" : { "type" : "keyword" },
            "idCardNumber" : { "type" : "keyword" },
            "mobileNumber" : { "type" : "keyword" },
            "gender" : { "type" : "short" },
            "desc" : { "type" : "text" },
            "birthday" : { "type": "date", "format": "yyy-MM-dd" }
        }
    }
    */
    public static String getMapping() {
        return "{\n" +
                "        \"properties\" : {\n" +
                "            \"uid\" : { \"type\" : \"long\" },\n" +
                "            \"name\" : { \"type\" : \"keyword\" },\n" +
                "            \"idCardNumber\" : { \"type\" : \"keyword\" },\n" +
                "            \"mobileNumber\" : { \"type\" : \"keyword\" },\n" +
                "            \"gender\" : { \"type\" : \"short\" },\n" +
                "            \"desc\" : { \"type\" : \"text\" },\n" +
                "            \"birthday\" : { \"type\": \"date\", \"format\": \"yyy-MM-dd\" }\n" +
                "        }\n" +
                "    }";
    }

    /*
    {
        "number_of_shards" : 1,
        "number_of_replicas" : 1
    }
    */
    public static String getSetting() {
        return "{\n" +
                "        \"number_of_shards\" : 1,\n" +
                "        \"number_of_replicas\" : 1\n" +
                "    } ";
    }

    public String getId() {
        return id;
    }

    public String getParent() {
        return null;
    }

    public static String genDesc(Long id, int rd) {
        if(rd % 5 == 1){
            return "This is Chinese Person "+id;
        }
        if(rd % 3 == 1){
            return "Hello! My ID is "+id;
        }
        else{
            return "Hi, I am Chinese and my ID is "+id;
        }
    }
}
