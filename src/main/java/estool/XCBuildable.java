package estool;

import org.elasticsearch.common.xcontent.XContentBuilder;

/**
 * Created by lanpay on 2017/7/19.
 */
public interface XCBuildable {
    XContentBuilder toXCB();
    String getId();
    String getParent(); // null if has no parent

}
