package com.anjbo.dao.yntrust;

import com.anjbo.bean.yntrust.YntrustRequestFlowDto;

import java.util.List;

/**
 * Created by Administrator on 2018/3/8.
 */
public interface YntrustRequestFlowMapper{

    List<YntrustRequestFlowDto> list(YntrustRequestFlowDto obj);

    void delete(YntrustRequestFlowDto obj);

    Integer insert(YntrustRequestFlowDto obj);

    Integer update(YntrustRequestFlowDto obj);
}