package com.shulianxunying.utils;

import com.esotericsoftware.kryo.Kryo;
import com.shulianxunying.resume.Area;
import com.shulianxunying.resume.ResumeType;
import org.apache.spark.serializer.KryoRegistrator;

/**
 * Created by 19866 on 2017/6/28.
 */
public class MyRegistrator implements KryoRegistrator {
    /* (non-Javadoc)
     * @see org.apache.spark.serializer.KryoRegistrator#registerClasses(com.esotericsoftware.kryo.Kryo)
     */
    public void registerClasses(Kryo arg0) {
        // TODO Auto-generated method stub
        arg0.register(ResumeType.class);
        arg0.register(Area.class);
    }
}
