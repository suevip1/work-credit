package com.anjbo.service.yntrust;

import com.anjbo.bean.yntrust.YntrustMappingDto;

/**
 * Created by Administrator on 2018/3/8.
 */
public interface YntrustMappingService {

    YntrustMappingDto select(YntrustMappingDto obj);

    void delete(YntrustMappingDto obj);

    Integer insert(YntrustMappingDto obj);

    Integer update(YntrustMappingDto obj);
}
