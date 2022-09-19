package com.jymj.mall.admin;

import com.jymj.mall.admin.repository.SysDistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShopAdminApplicationTests {

    @Autowired
    private SysDistrictRepository districtRepository;

//    @Test
//    void addDisTest() throws IOException {
//
//        String pathname = "D:\\Desktop\\sys_xzq.json";
//        File file = new File(pathname);
//        BufferedReader bf = new BufferedReader(new FileReader(file));
//
//        String content = "";
//
//        StringBuilder sb = new StringBuilder();
//
//        while (true) {
//
//            content = bf.readLine();
//
//            if (content == null) {
//
//                break;
//
//            }
//
//            sb.append(content.trim());
//
//        }
//
//        bf.close();
//
//        String jsonStr = sb.toString();
//
//        JSONObject json = JSONObject.parseObject(jsonStr);
//
//        JSONArray jsonArray = json.getJSONArray("RECORDS");
//
//        for (Object obj : jsonArray) {
//            JSONObject jsonObject = (JSONObject) JSON.toJSON(obj);
//            if (jsonObject.getIntValue("parent_id") == 0) {
//                System.out.println(jsonObject.toJSONString());
//
//                System.out.println("- - - - - - - - - - - - - - - - - - - -");
//                for (Object obj2 : jsonArray) {
//                    JSONObject jsonObject2 = (JSONObject) JSON.toJSON(obj2);
//                    if (jsonObject2.getIntValue("parent_id")==jsonObject.getIntValue("xzq_id")){
//                        System.out.println(jsonObject2.toJSONString());
//
//                        System.out.println("-------------------------------");
//                        for (Object obj3 : jsonArray) {
//                            JSONObject jsonObject3 = (JSONObject) JSON.toJSON(obj3);
//                            if (jsonObject3.getIntValue("parent_id")==jsonObject2.getIntValue("xzq_id")){
//                                System.out.println(jsonObject3.toJSONString());
//                            }
//                        }
//
//                        System.out.println("======================================");
//                    }
//                }
//
//            }
//        }
//
//    }
}
