package cn.mvp.mlibs.okhttps.builder;

import cn.mvp.mlibs.okhttps.request.PostStringRequest;
import cn.mvp.mlibs.okhttps.request.RequestCall;

import okhttp3.MediaType;

/**
 * Created by zhy on 15/12/14.
 */
public class PostStringBuilder extends OkHttpRequestBuilder<PostStringBuilder>
{
    private String content;
    private MediaType mediaType;


    public PostStringBuilder content(String content)
    {
        this.content = content;
        return this;
    }

    /**传json参数给服务器*/
    public PostStringBuilder contentJson(String content)
    {
        this.mediaType = MediaType.parse("application/json; charset=utf-8");
        this.content = content;
        return this;
    }

    public PostStringBuilder mediaType(MediaType mediaType)
    {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public RequestCall build()
    {
        return new PostStringRequest(url, tag, params, headers, content, mediaType,id).build();
    }


}
