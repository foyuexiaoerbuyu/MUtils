package cn.mvp.mlibs.okhttps.builder;

import cn.mvp.mlibs.okhttps.OkHttpUtils;
import cn.mvp.mlibs.okhttps.request.OtherRequest;
import cn.mvp.mlibs.okhttps.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
