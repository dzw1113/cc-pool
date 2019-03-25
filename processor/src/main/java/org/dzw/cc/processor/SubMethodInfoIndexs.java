//package org.dzw.cc.processor;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @description:
// * @author: dzw
// * @date: 2019/03/24 00:53
// **/
//public class SubMethodInfoIndexs implements Indexs {
//
//    private static final Map<String, List<SubMethodInfo>> SUBSCRIBER_INDEX;
//
//    static {
//        SUBSCRIBER_INDEX = new HashMap<String, List<SubMethodInfo>>();
//
//        SUBSCRIBER_INDEX.put("key", Arrays.asList(new SubMethodInfo[]{new SubMethodInfo("ss")}));
//    }
//
//    @Override
//    public List<SubMethodInfo> getSubMethodInfo(String key) {
//        List<SubMethodInfo> info = SUBSCRIBER_INDEX.get(key);
//        if (info != null) {
//            return info;
//        } else {
//            return null;
//        }
//    }
//}
