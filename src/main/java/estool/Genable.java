package estool;

import java.util.List;

/**
 * Created by lanpay on 2017/7/24.
 */
public interface Genable {
    List<XCBuildable> generate(long firstId, long cnt);

}
