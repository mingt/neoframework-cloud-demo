
package com.neoframework.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 常用格式工具类.
 *
 * <p>more: https://github.com/cheng2016/developNote/blob/master/android/util/RegexConstants.java</p>
 */
public class RegexUtils {

    /**
     * {before: 正则：用户名，取值范围为a-z,A-Z,0-9,"_",汉字，不能以"_"结尾,用户名必须是2-32位}
     *
     * <p>现在：登录名应由4-50位字母和数字及"_"组成，至少有一位字母，并且不能以"_"结尾，不能为汉字</p>
     *
     * @param username username
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isUsername(String username) {
        String regex = "^(?!\\d+$)[\\w]{4,50}(?<!_)$"; // {4,20}
        return Pattern.matches(regex, username);
    }

    /**
     * 验证是否为少于12位的纯数字
     *
     * @param digit 7位到11位，0-9之间的整数，开头零
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isDigitLessThan12(String digit) {
        String regex = "^[0-9]{2,11}$";
        return Pattern.matches(regex, digit);
    }

    /**
     * 验证Email
     *
     * @param email email地址，格式：zhang@gmail.com，zhang@xxx.com.cn，xxx代表邮件服务商
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isEmail(String email) {
        String regex = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        return Pattern.matches(regex, email);
    }

    // public static boolean isEmail2(String email) {
    // String regex = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)";
    // return Pattern.matches(regex, email);
    // }

    /**
     * 正则：国内手机号（精确）
     *
     * <p>
     * 移动：134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、178、182、183、184、187、188
     * </p>
     * <p>
     * 联通：130、131、132、145、155、156、171、175、176、185、186
     * </p>
     * <p>
     * 电信：133、153、173、177、180、181、189
     * </p>
     * <p>
     * 全球星：1349
     * </p>
     * <p>
     * 虚拟运营商：170
     * </p>
     *
     * <p>http://www.jihaoba.com/news/show/11146?7101
     * TODO： 新增的 198 199 166 字段未加入</p>
     *
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isMobile(String mobile) {
        // String regex = "(\\+\\d+)?1[34578]\\d{9}$";
        String regex = "^(0|86|17951)?(13[0-9]|15[012356789]|17[0-9]|18[0-9]|14[57]|19[0-9]|16[0-9])[0-9]{8}$";
        return Pattern.matches(regex, mobile);
    }

    /**
     * 验证手机号码（支持国际格式，+86135xxxx...（中国内地），+00852137xxxx...（中国香港））
     *
     * @param mobile 移动、联通、电信运营商的号码段
     *        移动的号段：134(0-8)、135、136、137、138、139、147（预计用于TD上网卡）
     *        、150、151、152、157（TD专用）、158、159、187（未启用）、188（TD专用）
     *        联通的号段：130、131、132、155、156（世界风专用）、185（未启用）、186（3g）
     *        电信的号段：133、153、180（未启用）、189
     * @return 验证成功返回true，验证失败返回false
     *
     * @deprecated 仅参考
     */
    public static boolean isI18nMobile(String mobile) {
        String regex = "(\\+\\d+)?1[3458]\\d{9}$";
        return Pattern.matches(regex, mobile);
    }

    /**
     * 验证身份证号码
     *
     * @param idCard 居民身份证号码15位或18位，最后一位可能是数字或字母
     * @return 验证成功返回true，验证失败返回false
     */

    public static boolean isIdCard(String idCard) {
        String regex = "[1-9]\\d{13,16}[a-zA-Z0-9]{1}";
        return Pattern.matches(regex, idCard);
    }

    /**
     * 验证固定电话号码
     *
     * @param phone 电话号码，格式：国家（地区）电话代码 + 区号（城市代码） + 电话号码，如：+8602085588447
     *        国家（地区） 代码 ：标识电话号码的国家（地区）的标准国家（地区）代码。它包含从 0 到 9 的一位或多位数字，
     *        数字之后是空格分隔的国家（地区）代码。
     *        区号（城市代码）：这可能包含一个或多个从 0 到 9 的数字，地区或城市代码放在圆括号——
     *        对不使用地区或城市代码的国家（地区），则省略该组件。
     *        电话号码：这包含从 0 到 9 的一个或多个数字
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isPhone(String phone) {
        String regex = "(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$";
        return Pattern.matches(regex, phone);
    }

    /**
     * 验证整数（正整数和负整数）
     *
     * @param digit 一位或多位0-9之间的整数
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isDigit(String digit) {
        String regex = "\\-?[1-9]\\d+";
        return Pattern.matches(regex, digit);
    }

    /**
     * 验证整数和浮点数（正负整数和正负浮点数）
     *
     * @param decimals 一位或多位0-9之间的浮点数，如：1.23，233.30
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isDecimals(String decimals) {
        String regex = "\\-?[1-9]\\d+(\\.\\d+)?";
        return Pattern.matches(regex, decimals);
    }

    /**
     * 验证空白字符
     *
     * @param blankSpace 空白字符，包括：空格、\t、\n、\r、\f、\x0B
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isBlankSpace(String blankSpace) {
        String regex = "\\s+";
        return Pattern.matches(regex, blankSpace);
    }

    /**
     * 验证中文
     *
     * @param chinese 中文字符
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isChinese(String chinese) {
        // CHECKSTYLE:OFF
        String regex = "^[\u4E00-\u9FA5]+$";
        // CHECKSTYLE:ON
        return Pattern.matches(regex, chinese);
    }

    /**
     * 验证日期（年月日）
     *
     * @param birthday 日期，格式：1992-09-03，或1992.09.03
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isBirthday(String birthday) {
        String regex = "[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}";
        return Pattern.matches(regex, birthday);
    }

    /**
     * 验证URL地址
     *
     * @param url 格式：http://blog.csdn.net:80/xyang81/article/details/7705960? 或 http://www.csdn.net:80
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isUrl(String url) {
        String regex = "(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?";
        return Pattern.matches(regex, url);
    }

    /**
     * 获取网址 URL 的一级域名
     * http://detail.tmall.com/item.htm?spm=a230r.1.10.44.1xpDSH&id=15453106243&_u=f4ve1uq1092 ->> tmall.com
     *
     *
     * @param url 网址
     * @return
     */
    public static String getDomain(String url) {
        Pattern p =
                Pattern.compile("(?<=http://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
        // 获取完整的域名
        // Pattern p=Pattern.compile("[^//]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(url);
        matcher.find();
        return matcher.group();
    }

    /**
     * 匹配中国邮政编码
     *
     * @param postcode 邮政编码
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isPostcode(String postcode) {
        String regex = "[1-9]\\d{5}";
        return Pattern.matches(regex, postcode);
    }

    /**
     * 匹配IP地址(简单匹配，格式，如：192.168.1.1，127.0.0.1，没有匹配IP段的大小)
     *
     * @param ipAddress IPv4标准地址
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isIpAddress(String ipAddress) {
        String regex = "[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))";
        return Pattern.matches(regex, ipAddress);
    }

    // 是否包含 . 号
    public static boolean isContainsDot(String username) {
        return username.contains(".");
    }

    // 是否包含连词符
    public static boolean isContainsHyphen(String username) {
        return username.contains("-");
    }

    // 密码长度 6-20
    public static boolean isUserPasswordLength(String pwd) {
        return pwd.length() > 5 && pwd.length() < 21;
    }

    public static boolean isValidUserName(String un) {
        String regex = "([A-Z0-9a-z-]|[\\u4e00-\\u9fa5])+";
        return Pattern.matches(regex, un);
    }

    /**
     * 测试main.
     *
     * @param args Args
     */
    public static void main(String[] args) {
        // 下面测试针对 String regex = "^(?!\\d+$)[\\w]{4,20}(?<!_)$"; 现在可能实际已改为其他长度
        System.out.println(RegexUtils.isUsername("a1234567890123456789")); // true
        System.out.println(RegexUtils.isUsername("1a234567890123456789")); // true
        System.out.println(RegexUtils.isUsername("123456789012345678")); // false 不含字母
        System.out.println(RegexUtils.isUsername("01234567890123456789")); // false 不含字母
        System.out.println(RegexUtils.isUsername("a12345678901234567890")); // false 超出
        System.out.println("-----");
        System.out.println(RegexUtils.isUsername("abc")); // false
        System.out.println(RegexUtils.isUsername("abcd")); // true
        System.out.println(RegexUtils.isUsername("abcd_")); // false
        System.out.println(RegexUtils.isUsername("ab_d")); // true
        System.out.println(RegexUtils.isUsername("abcd?")); // false
        System.out.println(RegexUtils.isUsername("abcd+")); // false
        System.out.println(RegexUtils.isUsername("abc?+d")); // false
        System.out.println("-----");
        System.out.println(RegexUtils.isUsername("爱情")); // false
        System.out.println(RegexUtils.isUsername("爱情吧")); // false
        System.out.println(RegexUtils.isUsername("爱情吧吧")); // false
        System.out.println("----------------");

        System.out.println(RegexUtils.isMobile("13500001111")); // true
        System.out.println(RegexUtils.isMobile("16000001111"));
        System.out.println("----------------");

        System.out.println(RegexUtils.isDigitLessThan12("1100034"));
        System.out.println(RegexUtils.isDigitLessThan12("11000341234"));
        System.out.println(RegexUtils.isDigitLessThan12("00"));
        System.out.println(RegexUtils.isDigitLessThan12("110003412345")); // false
        System.out.println(RegexUtils.isDigitLessThan12("0100a034")); // false
        System.out.println("----------------");
        System.out.println(RegexUtils.isEmail("ddd5@loveeeeeeeeeeeeee,gggg.com"));
    }
}
