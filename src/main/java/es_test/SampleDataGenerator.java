package es_test;

import es_test.model.Accident;
import es_test.model.Item;
import es_test.model.Person;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by lanpay on 2017/6/24.
 */
public class SampleDataGenerator {

    private Random rand = new Random();

    private final static String[] names = {
            "wang", "luo", "zhang", "zhao", "li", "gou", "cao", "zeng", "pipi", "pika", "rika",
            "feng", "xiang", "ruo", "xie", "luxixi", "song", "popo", "jeckson", "mich", "lulu",
            "pp", "zz", "hehe", "obama", "trump", "xi", "mao", "keng", "king", "lam", "yuna"
    };

    private final static String[] accs = {
            "loss", "car crash", "air crash", "beaten", "jump", "fired", "sb", "hit face", "pain",
            "war", "robber", "bleeding", "headache", "sick", "unhappy", "fell down", "dog attack",
            "kill1", "kill2", "kill3", "kill4", "kill5", "kill6", "kill7", "kill8", "kill9"
    };

    List<Person> generatePerson(int num) {
        List<Person> list = new ArrayList<Person>();
        for(int i=0; i<num; i++){
            String id = ""+i;
            String name = names[rand.nextInt(names.length)];
            int age = rand.nextInt(40)+10;
            Person p = new Person(id, name, age);
            list.add(p);
        }
        return list;
    }

    List<Accident> generateAccident(int num, List<Person> personList) {
        List<Accident> list = new ArrayList<Accident>();
        for(int i=0; i<num; i++) {
            String id = ""+i;
            String pID = personList.get(rand.nextInt(personList.size())).ID;
            String name = accs[rand.nextInt(accs.length)];
            long ts = System.currentTimeMillis() + rand.nextInt(1000);
            Accident acc = new Accident(id, name, 1, 1, pID, ts);
            list.add(acc);
        }
        return list;
    }

    List<Item> generateSampleData(int base, int num) {
        List<Item> list = new ArrayList<Item>();
        Random rd = new Random();

        for(int i=0; i<num; i++){
            String name = "item_"+i;
            String desc = "this is item "+i;
            if(rd.nextInt(10) == 7){
                desc = "that is body "+i;
            }
            int age = rd.nextInt();
            long id = i+base;
            Item item = new Item(id, name, desc, age);
            list.add(item);
        }

        return list;
    }

    //List<ProductGroup> generateNestedDocumentsSampleData();

    //ProductProperty findProductProperty(String size, String color);
}
