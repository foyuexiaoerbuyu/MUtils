//package cn.mvp;
//
//import cn.mvp.mlibs.okhttps.OkHttpUtils;
//import cn.mvp.mlibs.okhttps.callback.StringCallback;
//import cn.mvp.mlibs.okhttps.log.LoggerInterceptor;
//
//import okhttp3.Call;
//import okhttp3.MediaType;
//
///**
// * @author AlanFin
// * @date 2021/5/10
// */
//public class DingDingSendMsgUtils {
//
//
//    /**
//     * @param content 内容
//     */
//    public static void pushLog(String content) {
//        pushMsg("log", content);
//    }
//
//    /**
//     * @param tag     关键词
//     * @param content 内容
//     */
//    public static void pushMsg(String tag, String content) {
//        OkHttpUtils.getInstance().getOkHttpClient().newBuilder().addInterceptor(new LoggerInterceptor("", true));
//        String webhook = "https://oapi.dingtalk.com/robot/send?access_token=67efb4e356cb5c10aa41c7fcfc58102df8432b187cb7441f2e1a7f04ce62a503";
//        OkHttpUtils.postString().mediaType(MediaType.parse("application/json; charset=utf-8"))
//                .url(webhook).content("{\"text\":{\"content\":\"" + tag + ": " + content + "\"},\"msgtype\":\"text\"}")
//                .build().execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int i) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onResponse(String s, int i) {
//                        System.out.println("s = " + s);
//                    }
//                });
//    }
//
//
//    public interface IDDClick {
//        void onClick(String str);
//    }
//}
