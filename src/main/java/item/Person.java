package item;

import estool.XCBuildable;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by lanpay on 2017/7/19.
 */
public class Person implements XCBuildable{
    public Long id;
    public String name;
    public String description;
    public int age;
    public double lat;
    public double lng;

    public Person(Long id, String name, String description, int age) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.age = age;
        lat = 0;
        lng = 0;
    }

    public XContentBuilder toXCB() {
        XContentBuilder contentBuilder = null;
        try {
            contentBuilder = jsonBuilder();

            contentBuilder.startObject()
                    .field("name", name)
                    .field("description", description)
                    .field("age", age)
                    .endObject();

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error occured while creating product gift json document!", ex);
        }
        return contentBuilder;
    }

    public String getId() {
        return String.valueOf(id);
    }

    public String getParent() {
        return null;
    }
}
