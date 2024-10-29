package cn.mvp.mlibs.other;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import cn.mvp.mlibs.R;

public class TestUtils {

    private static Random random = new Random();

    public static final boolean TRUE = true;
    public static final boolean FALSE = false;
    /**
     * 返回手机号码
     */
    private static String[] telFirst = "134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153".split(",");
    private static int mClickNum;

    /**
     * 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false
     */
    public static boolean isBlank(String value) {
        return value == null || "".equalsIgnoreCase(value.trim()) || "null"
                .equalsIgnoreCase(value.trim());
    }

    private static void creatArgs(String arg, StringBuffer stringBuffer) {
        arg = arg.substring(1, arg.length() - 1).replace(" ", "");
        String[] keyBody = arg.split(",");
        for (String keyAndValue : keyBody) {
            String key = keyAndValue.substring(0, keyAndValue.indexOf("="));
            String val = keyAndValue.substring(keyAndValue.indexOf("=") + 1);
            stringBuffer.append("        <").append(key).append(">").append(val)
                    .append("</").append(key).append(">").append("\n");
        }
    }

    public static String getRandomName() {
        Random random = new Random();
        String[] Surname = {"赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "褚", "卫", "蒋", "沈", "韩",
                "杨", "朱", "秦", "尤", "许",
                "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏", "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水",
                "窦", "章", "云", "苏", "潘", "葛", "奚", "范", "彭", "郎",
                "鲁", "韦", "昌", "马", "苗", "凤", "花", "方", "俞", "任", "袁", "柳", "酆", "鲍", "史", "唐", "费", "廉",
                "岑", "薛", "雷", "贺", "倪", "汤", "滕", "殷",
                "罗", "毕", "郝", "邬", "安", "常", "乐", "于", "时", "傅", "皮", "卞", "齐", "康", "伍", "余", "元", "卜",
                "顾", "孟", "平", "黄", "和",
                "穆", "萧", "尹", "姚", "邵", "湛", "汪", "祁", "毛", "禹", "狄", "米", "贝", "明", "臧", "计", "伏", "成",
                "戴", "谈", "宋", "茅", "庞", "熊", "纪", "舒",
                "屈", "项", "祝", "董", "梁", "杜", "阮", "蓝", "闵", "席", "季"};
        String girl = "秀娟英华慧巧美娜静淑惠珠翠雅芝玉萍红娥玲芬芳燕彩春菊兰凤洁梅琳素云莲真环雪荣爱妹霞香月莺媛艳瑞凡佳嘉琼勤珍贞莉桂娣叶璧璐娅琦晶妍茜秋珊莎锦黛青倩婷姣婉娴瑾颖露瑶怡婵雁蓓纨仪荷丹蓉眉君琴蕊薇菁梦岚苑婕馨瑗琰韵融园艺咏卿聪澜纯毓悦昭冰爽琬茗羽希宁欣飘育滢馥筠柔竹霭凝晓欢霄枫芸菲寒伊亚宜可姬舒影荔枝思丽 ";
        String boy = "伟刚勇毅俊峰强军平保东文辉力明永健世广志义兴良海山仁波宁贵福生龙元全国胜学祥才发武新利清飞彬富顺信子杰涛昌成康星光天达安岩中茂进林有坚和彪博诚先敬震振壮会思群豪心邦承乐绍功松善厚庆磊民友裕河哲江超浩亮政谦亨奇固之轮翰朗伯宏言若鸣朋斌梁栋维启克伦翔旭鹏泽晨辰士以建家致树炎德行时泰盛雄琛钧冠策腾楠榕风航弘";
        int index = random.nextInt(Surname.length - 1);
        String name = Surname[index]; //获得一个随机的姓氏
        int i = random.nextInt(3);//可以根据这个数设置产生的男女比例
        if (i == 2) {
            int j = random.nextInt(girl.length() - 2);
            if (j % 2 == 0) {
                name = "" + name + girl.substring(j, j + 2);
            } else {
                name = "" + name + girl.substring(j, j + 1);
            }

        } else {
            int j = random.nextInt(girl.length() - 2);
            if (j % 2 == 0) {
                name = "" + name + boy.substring(j, j + 2);
            } else {
                name = "" + name + boy.substring(j, j + 1);
            }

        }

        return name;
    }

    /**
     * 包含起始值 `start` 和结束值 `end`
     *
     * @param start 开始
     * @param end   结束
     * @return 获取范围内随机数字
     */
    public static int getRandomNum(int start, int end) {
        return (int) (Math.random() * (end - start + 1) + start);
    }

    /**
     * 包含起始值 `start` 不包含结束值 `end`
     *
     * @param start 开始
     * @param end   结束
     * @return 获取范围内随机数字
     */
    public static int getRandomNum1(int start, int end) {
        return (int) (Math.random() * (end - start) + start);
    }

    /**
     * @return 获取随机手机号
     */
    public static String getRandomPhoneNumber() {
        int index = getRandomNum(0, telFirst.length - 1);
        String first = telFirst[index];
        String second = String.valueOf(getRandomNum(1, 888) + 10000).substring(1);
        String third = String.valueOf(getRandomNum(1, 9100) + 10000).substring(1);
        return first + second + third;
    }

    /**
     * @return 获取随机姓名性别
     */
    public static String getNameAndSex() {
        Random random = new Random();
        String[] Surname = {"赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "褚", "卫", "蒋", "沈", "韩",
                "杨", "朱", "秦", "尤", "许",
                "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏", "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水",
                "窦", "章", "云", "苏", "潘", "葛", "奚", "范", "彭", "郎",
                "鲁", "韦", "昌", "马", "苗", "凤", "花", "方", "俞", "任", "袁", "柳", "酆", "鲍", "史", "唐", "费", "廉",
                "岑", "薛", "雷", "贺", "倪", "汤", "滕", "殷",
                "罗", "毕", "郝", "邬", "安", "常", "乐", "于", "时", "傅", "皮", "卞", "齐", "康", "伍", "余", "元", "卜",
                "顾", "孟", "平", "黄", "和",
                "穆", "萧", "尹", "姚", "邵", "湛", "汪", "祁", "毛", "禹", "狄", "米", "贝", "明", "臧", "计", "伏", "成",
                "戴", "谈", "宋", "茅", "庞", "熊", "纪", "舒",
                "屈", "项", "祝", "董", "梁", "杜", "阮", "蓝", "闵", "席", "季"};
        String girl = "秀娟英华慧巧美娜静淑惠珠翠雅芝玉萍红娥玲芬芳燕彩春菊兰凤洁梅琳素云莲真环雪荣爱妹霞香月莺媛艳瑞凡佳嘉琼勤珍贞莉桂娣叶璧璐娅琦晶妍茜秋珊莎锦黛青倩婷姣婉娴瑾颖露瑶怡婵雁蓓纨仪荷丹蓉眉君琴蕊薇菁梦岚苑婕馨瑗琰韵融园艺咏卿聪澜纯毓悦昭冰爽琬茗羽希宁欣飘育滢馥筠柔竹霭凝晓欢霄枫芸菲寒伊亚宜可姬舒影荔枝思丽 ";
        String boy = "伟刚勇毅俊峰强军平保东文辉力明永健世广志义兴良海山仁波宁贵福生龙元全国胜学祥才发武新利清飞彬富顺信子杰涛昌成康星光天达安岩中茂进林有坚和彪博诚先敬震振壮会思群豪心邦承乐绍功松善厚庆磊民友裕河哲江超浩亮政谦亨奇固之轮翰朗伯宏言若鸣朋斌梁栋维启克伦翔旭鹏泽晨辰士以建家致树炎德行时泰盛雄琛钧冠策腾楠榕风航弘";
        int index = random.nextInt(Surname.length - 1);
        String name = Surname[index]; //获得一个随机的姓氏
        int i = random.nextInt(3);//可以根据这个数设置产生的男女比例
        if (i == 2) {
            int j = random.nextInt(girl.length() - 2);
            if (j % 2 == 0) {
                name = "女-" + name + girl.substring(j, j + 2);
            } else {
                name = "女-" + name + girl.substring(j, j + 1);
            }

        } else {
            int j = random.nextInt(girl.length() - 2);
            if (j % 2 == 0) {
                name = "男-" + name + boy.substring(j, j + 2);
            } else {
                name = "男-" + name + boy.substring(j, j + 1);
            }

        }

        return name;
    }

    /**
     * @return 随机性别
     */
    public static String getRandomSex() {
        return nextBoolean() ? "男" : "女";
    }


    /**
     * @return 随机获取Boolean
     */
    public static boolean nextBoolean() {
        return random.nextBoolean();
    }

    /**
     * @return 返回true
     */
    public static boolean reutrnTrue() {
        return true;
    }

    /**
     * @return 返回False
     */
    public static boolean reutrnFalse() {
        return false;
    }

    /**
     * @return 获取随机ip
     */
    public static String getRandomIp() {

        // ip范围
        int[][] range = {
                {607649792, 608174079}, // 36.56.0.0-36.63.255.255
                {1038614528, 1039007743}, // 61.232.0.0-61.237.255.255
                {1783627776, 1784676351}, // 106.80.0.0-106.95.255.255
                {2035023872, 2035154943}, // 121.76.0.0-121.77.255.255
                {2078801920, 2079064063}, // 123.232.0.0-123.235.255.255
                {-1950089216, -1948778497}, // 139.196.0.0-139.215.255.255
                {-1425539072, -1425014785}, // 171.8.0.0-171.15.255.255
                {-1236271104, -1235419137}, // 182.80.0.0-182.92.255.255
                {-770113536, -768606209}, // 210.25.0.0-210.47.255.255
                {-569376768, -564133889}, // 222.16.0.0-222.95.255.255
        };

        Random random = new Random();
        int index = random.nextInt(10);
        String ip = num2ip(range[index][0] + new Random().nextInt(range[index][1] - range[index][0]));
        return ip;
    }

    /*
     * @return 将十进制转换成IP地址
     */
    public static String num2ip(int ip) {
        int[] b = new int[4];
        String ipStr = "";
        b[0] = (int) ((ip >> 24) & 0xff);
        b[1] = (int) ((ip >> 16) & 0xff);
        b[2] = (int) ((ip >> 8) & 0xff);
        b[3] = (int) (ip & 0xff);
        ipStr =
                Integer.toString(b[0]) + "." + Integer.toString(b[1]) + "." + Integer.toString(b[2]) + "."
                        + Integer.toString(b[3]);

        return ipStr;
    }

    /**
     * @return 获取随机地址
     */
    public static String getRandomAddress() {

        String[] province = {"河北省", "山西省", "辽宁省", "吉林省", "黑龙江省", "江苏省", "浙江省", "安徽省", "福建省", "江西省",
                "山东省", "河南省", "湖北省", "湖南省", "广东省", "海南省", "四川省", "贵州省", "云南省", "陕西省", "甘肃省", "青海省", "台湾省",};
        String[] city = {"安康市", "安庆市", "安顺市", "安阳市", "鞍山市", "巴彦淖尔市", "巴中市", "白城市", "白山市", "白银市", "百色市",
                "蚌埠市", "包头市", "宝鸡市", "保定市", "保山市", "北海市", "本溪市", "滨州市", "沧州市", "昌都地区", "长春市", "长沙市", "长治市",
                "常德市", "常州市", "巢湖市", "朝阳市", "潮州市", "郴州市", "成都市", "承德市", "池州市", "赤峰市", "崇左市", "滁州市", "达州市",
                "大连市", "大庆市", "大同市", "丹东市", "德阳市", "德州市", "定西市", "东莞市", "东营市", "鄂尔多斯市", "鄂州市", "防城港市",
                "佛山市", "福州市", "抚顺市", "抚州市", "阜新市", "阜阳市", "甘南州", "赣州市", "固原市", "广安市", "广元市", "广州市", "贵港市",
                "贵阳市", "桂林市", "哈尔滨市", "哈密地区", "海北藏族自治州", "海东地区", "海口市", "邯郸市", "汉中市", "杭州市", "毫州市", "合肥市",
                "河池市", "河源市", "菏泽市", "贺州市", "鹤壁市", "鹤岗市", "黑河市", "衡水市", "衡阳市", "呼和浩特市", "呼伦贝尔市", "湖州市",
                "葫芦岛市", "怀化市", "淮安市", "淮北市", "淮南市", "黄冈市", "黄山市", "黄石市", "惠州市", "鸡西市", "吉安市", "吉林市", "济南市",
                "济宁市", "佳木斯市", "嘉兴市", "嘉峪关市", "江门市", "焦作市", "揭阳市", "金昌市", "金华市", "锦州市", "晋城市", "晋中市", "荆门市",
                "荆州市", "景德镇市", "九江市", "酒泉市", "开封市", "克拉玛依市", "昆明市", "拉萨市", "来宾市", "莱芜市", "兰州市", "廊坊市",
                "乐山市", "丽江市", "丽水市", "连云港市", "辽阳市", "辽源市", "聊城市", "临沧市", "临汾市", "临沂市", "柳州市", "六安市", "六盘水市",
                "龙岩市", "陇南市", "娄底市", "泸州市", "吕梁市", "洛阳市", "漯河市", "马鞍山市", "茂名市", "眉山市", "梅州市", "绵阳市", "牡丹江市",
                "内江市", "南昌市", "南充市", "南京市", "南宁市", "南平市", "南通市", "南阳市", "宁波市", "宁德市", "攀枝花市", "盘锦市", "平顶山市",
                "平凉市", "萍乡市", "莆田市", "濮阳市", "普洱市", "七台河市", "齐齐哈尔市", "钦州市", "秦皇岛市", "青岛市", "清远市", "庆阳市",
                "曲靖市", "衢州市", "泉州市", "日照市", "三门峡市", "三明市", "三亚市", "汕头市", "汕尾市", "商洛市", "商丘市", "上饶市", "韶关市",
                "邵阳市", "绍兴市", "深圳市", "沈阳市", "十堰市", "石家庄市", "石嘴山市", "双鸭山市", "朔州市", "四平市", "松原市", "苏州市",
                "宿迁市", "宿州市", "绥化市", "随州市", "遂宁市", "台州市", "太原市", "泰安市", "泰州市", "唐山市", "天水市", "铁岭市", "通化市",
                "通辽市", "铜川市", "铜陵市", "铜仁市", "吐鲁番地区", "威海市", "潍坊市", "渭南市", "温州市", "乌海市", "乌兰察布市", "乌鲁木齐市",
                "无锡市", "吴忠市", "芜湖市", "梧州市", "武汉市", "武威市", "西安市", "西宁市", "锡林郭勒盟", "厦门市", "咸宁市", "咸阳市", "湘潭市",
                "襄樊市", "孝感市", "忻州市", "新乡市", "新余市", "信阳市", "兴安盟", "邢台市", "徐州市", "许昌市", "宣城市", "雅安市", "烟台市",
                "延安市", "盐城市", "扬州市", "阳江市", "阳泉市", "伊春市", "伊犁哈萨克自治州", "宜宾市", "宜昌市", "宜春市", "益阳市", "银川市",
                "鹰潭市", "营口市", "永州市", "榆林市", "玉林市", "玉溪市", "岳阳市", "云浮市", "运城市", "枣庄市", "湛江市", "张家界市", "张家口市",
                "张掖市", "漳州市", "昭通市", "肇庆市", "镇江市", "郑州市", "中山市", "中卫市", "舟山市", "周口市", "株洲市", "珠海市", "驻马店市",
                "资阳市", "淄博市", "自贡市", "遵义市",};
        String[] area = {"伊春区", "带岭区", "南岔区", "金山屯区", "西林区", "美溪区", "乌马河区", "翠峦区", "友好区", "新青区", "上甘岭区",
                "五营区", "红星区", "汤旺河区", "乌伊岭区", "榆次区"};
        String[] road = {"爱国路", "安边路", "安波路", "安德路", "安汾路", "安福路", "安国路", "安化路", "安澜路", "安龙路", "安仁路",
                "安顺路", "安亭路", "安图路", "安业路", "安义路", "安远路", "鞍山路", "鞍山支路", "澳门路", "八一路", "巴林路", "白城路", "白城南路",
                "白渡路", "白渡桥", "白兰路", "白水路", "白玉路", "百安路（方泰镇）", "百官街", "百花街", "百色路", "板泉路", "半淞园路", "包头路",
                "包头南路", "宝安公路", "宝安路", "宝昌路", "宝联路", "宝林路", "宝祁路", "宝山路", "宝通路", "宝杨路", "宝源路", "保德路", "保定路",
                "保屯路", "保屯路", "北艾路",};
        String[] home = {"金色家园", "耀江花园", "阳光翠竹苑", "东新大厦", "溢盈河畔别墅", "真新六街坊", "和亭佳苑", "协通公寓", "博泰新苑",
                "菊园五街坊", "住友嘉馨名园", "复华城市花园", "爱里舍花园"};

        Random random = new Random();
        int randomProvinceNum = random.nextInt(province.length);
        int randomCityNum = random.nextInt(city.length);
        int randomAreaNum = random.nextInt(area.length);
        int randomRoadNum = random.nextInt(road.length);
        int randomHomeNum = random.nextInt(home.length);
        int num = random.nextInt(200);
        return province[randomProvinceNum] + city[randomAreaNum] + area[randomAreaNum]
                + road[randomRoadNum] + num + "号" + home[randomHomeNum];
    }


    // 18位身份证号码各位的含义:
    // 1-2位省、自治区、直辖市代码；
    // 3-4位地级市、盟、自治州代码；
    // 5-6位县、县级市、区代码；
    // 7-14位出生年月日，比如19670401代表1967年4月1日；
    // 15-17位为顺序号，其中17位（倒数第二位）男为单数，女为双数；
    // 18位为校验码，0-9和X。
    // 作为尾号的校验码，是由把前十七位数字带入统一的公式计算出来的，
    // 计算的结果是0-10，如果某人的尾号是0－9，都不会出现X，但如果尾号是10，那么就得用X来代替，
    // 因为如果用10做尾号，那么此人的身份证就变成了19位。X是罗马数字的10，用X来代替10

    public static String getRandomIdCard() {
        String id = "";
        // 随机生成省、自治区、直辖市代码 1-2
        String provinces[] = {"11", "12", "13", "14", "15", "21", "22", "23",
                "31", "32", "33", "34", "35", "36", "37", "41", "42", "43",
                "44", "45", "46", "50", "51", "52", "53", "54", "61", "62",
                "63", "64", "65", "71", "81", "82"};
        String province = provinces[new Random().nextInt(provinces.length - 1)];
        // 随机生成地级市、盟、自治州代码 3-4
        String citys[] = {"01", "02", "03", "04", "05", "06", "07", "08",
                "09", "10", "21", "22", "23", "24", "25", "26", "27", "28"};
        String city = citys[new Random().nextInt(citys.length - 1)];
        // 随机生成县、县级市、区代码 5-6
        String countys[] = {"01", "02", "03", "04", "05", "06", "07", "08",
                "09", "10", "21", "22", "23", "24", "25", "26", "27", "28",
                "29", "30", "31", "32", "33", "34", "35", "36", "37", "38"};
        String county = countys[new Random().nextInt(countys.length - 1)];
        // 随机生成出生年月 7-14
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMMdd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE,
                date.get(Calendar.DATE) - new Random().nextInt(365 * 100));
        String birth = dft.format(date.getTime());
        // 随机生成顺序号 15-17
        String no = new Random().nextInt(999) + "";
        // 随机生成校验码 18
        String checks[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "X"};
        String check = checks[new Random().nextInt(checks.length - 1)];
        // 拼接身份证号码
        id = province + city + county + birth + no + check;

        return id;
    }

    /**
     * 生成随机时间
     *
     * @return
     */
    public static String getRandomDate() {
        return getRandomDate("2012-09-20", "2023-09-22");
    }

    /**
     * 生成随机时间
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public static String getRandomDate(String beginDate, String endDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date start = format.parse(beginDate);// 构造开始日期
            Date end = format.parse(endDate);// 构造结束日期
            // getTime()表示返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。
            if (start.getTime() >= end.getTime()) {
                return null;
            }
            long date = random(start.getTime(), end.getTime());
            return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static long random(long begin, long end) {
        long rtn = begin + (long) (Math.random() * (end - begin));
        // 如果返回的是开始时间和结束时间，则递归调用本函数查找随机值
        if (rtn == begin || rtn == end) {
            return random(begin, end);
        }
        return rtn;
    }

    /**
     * @return 获取随机部门
     */
    public static String getRandomDepartment() {
        String[] home = {"研发部 ", "生产部 ", "销售部 ", "市场部 ", "人力资源部 ", "公共关系部 ", "财务部 ", "产品部 ", "管理部 ", "采购部 ", "总经办 ", "战略研究部 ", "国际部 ", "广告部 ", "企划部 ", "秘书室 ", "总务部 ", "质量管理部 ", "工程部 ", "客户服务部"};
        Random random = new Random();
        return home[random.nextInt(home.length)];
    }


    public static String getBooleStr(String val1, String val2) {
        return nextBoolean() ? val1 : val2;
    }

    private static boolean nextBoole;

    /**
     * @return 第一次返回true
     */
    public static boolean getBoole() {
        return nextBoole = !nextBoole;
    }

    public static int setClickNum() {
        return ++mClickNum;
    }

    public static int reSetClickNum() {
        return mClickNum = 0;
    }

    public String getRandmeStr(String val1, String val2, String val3) {
        int randomNum = getRandomNum(0, 9);
        if (randomNum < 3) {
            return val1;
        } else if (randomNum < 6) {
            return val2;
        } else if (randomNum < 9) {
            return val3;
        }
        return val1;
    }


    public static int getImgIcon() {
        return R.drawable.icon_img;
    }

    public static String getImgIconUrl() {
        return "https://img-blog.csdnimg.cn/6d0464a9d745452594988ea871ce1399.png";
    }


    /**
     * 生成范围内随机数(包含最大值,最小值)
     *
     * @param min 最小值
     * @param max 最大值
     * @return 随机数
     */
    public static int randomInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    /**
     * 随机图片 图床
     * https://imgimg.cc/upload
     */
    public static String randomImageUrl() {
        String[] imageUrls = {
                "https://cdn-fusion.imgimg.cc/i/2024/9c14c366570fef7f.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/1cc0d033f7f6b43b.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/b06fadeb1c249689.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/f4c9bfba532225b4.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/12d42f805ba75c34.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/039b79813690b80b.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/80b23a9d82fdc052.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/5daaee15e4ee282e.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/a0ca7ec414c34f97.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/1f8d808894d3d6fa.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/ee903970249d10b6.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/e96a561aeaaaa84f.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/ea11e434b2ffc98f.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/5c81ac9041f05d2c.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/0938ee2d6f67a135.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/b3a2ee741b22aa4c.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/d7b9c8a60ff9f625.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/8f87d7202a87840c.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/6604126d15a1f05a.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/5fbe0dea0643bbf5.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/5d11c60eed9e0909.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/a3d1d72f2f281df3.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/68b38e43483e9fe8.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/c6dba0b3c086eca7.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/5941fde4256a7e60.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/07f4ca2bf8c0ce8a.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/f2740d7b40ac8dce.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/8810a161f5873fe1.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/2f6178fa7e21d9f4.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/da9a89a78f74cebc.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/6e55f7c3d0971e7c.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/4b0e200e5d48e2fa.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/b148113efdd054a0.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/4dac85081c35d079.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/9776246b9018e115.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/e93714648c035e42.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/a864f1ab308e98aa.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/8b2f7cadf91055e4.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/cfb7c03c17f839ce.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/1a52f1a38316fb60.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/827c240460a00d2a.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/fa2674d55bde3aff.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/5bb2347b66f67814.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/2693413bec6e8890.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/3cb3e46eaba98198.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/77f14c5acda5e1ad.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/16ebe0770231eccc.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/fd84b9510558a238.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/dee6b4656b545d8f.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/923307ad4109d56b.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/c51e5ff80a059954.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/b9884cdb6162455c.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/cd84a93368042646.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/c5041a6bea4defb2.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/b084735f9f786f54.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/63b9d7c1be27d97e.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/6108d3c45776a0ee.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/d5a125d8e9f810f7.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/027c17f9fd007b1b.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/e807e68d3411fe8e.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/ca01f52b0212f1f5.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/5f9ff82aba45e17c.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/2d520fbd26a87b35.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/a9bc53a32cbd2881.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/729224a20db2cd9a.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/823ba44291f5c14a.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/307dcaf2c1f22c15.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/59d6bc0bc9f1c7fa.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/1bf49a35106e9ada.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/6ef2b3ecd3c2d389.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/d5dc05458a01cbb4.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/543a930f2b20e597.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/9a3e81c02371a709.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/caf90b43a666fb74.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/adc42a09fd7883be.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/5f8ea9c07328765a.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/751ec4a4eaa7fd17.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/6f6a59f7238396b5.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/436074966ed2ea0b.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/4d4e1c36abad9be7.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/293a0fb80a995b61.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/5e5b90f2646ec437.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/fc3cde636a63208f.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/1d713377a33035e5.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/924eb46452da491d.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/e2268b9e0f3c28f3.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/7b2ff31aef79e683.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/ea899b5becd9db46.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/df77d31718fb8bc9.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/658992b538532e43.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/87e8e10b2c90352a.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/68bb898f6f721135.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/94aa2b3778fd81e3.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/42e28ccccc228216.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/5b25865cf1586af4.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/e242dfe6f636823f.jpg",
                "https://cdn-fusion.imgimg.cc/i/2024/02e8fc07a3a2e650.jpg"
        };

        // 随机生成一个索引
        Random random = new Random();
        int index = random.nextInt(imageUrls.length);

        // 获取随机的图片链接
        String randomImageUrl = imageUrls[index];
        System.out.println("随机获取的图片链接：" + randomImageUrl);
        return randomImageUrl;
    }

    /**
     * 生成指定长度的随机数字字符串
     *
     * @param length 随机数字字符串的长度
     * @return 指定长度的随机数字字符串
     */
    public static String generateRandomNumber(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be a positive integer");
        }
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // 生成0到9之间的随机整数
        }
        return sb.toString();
    }

}