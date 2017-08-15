package com.shulianxunying.dao.impldao;

import com.alibaba.fastjson.JSON;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import com.shulianxunying.cache.CommonParams;
import com.shulianxunying.dao.MBaseDao;
import com.shulianxunying.entity.PMUser;
import org.apache.commons.lang3.StringUtils;
import org.bson.BsonDateTime;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SuChang on 2017/4/25 10:25.
 */
@Repository
public class MPMUserDao extends MBaseDao<PMUser> {
    public static String collectionName = CommonParams.PM_USER;

    public static String getCollectionName() {
        return collectionName;
    }

}
