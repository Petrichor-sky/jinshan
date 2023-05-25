package com.haizhi.empower.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.haizhi.empower.base.resp.ObjectRestResponse;
import com.haizhi.empower.base.resp.TableResultResponse;
import com.haizhi.empower.entity.vo.CarVo;
import com.haizhi.empower.entity.vo.CaseInfoVo;
import com.haizhi.empower.entity.vo.JjdInfoVo;
import com.haizhi.empower.entity.vo.PersonVo;
import com.haizhi.empower.util.okhttp.EasyOkClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author zhuhongyang
 * @date 2023/4/6 11:33
 * @description 接警、案件、人员、车辆接口
 */
@Slf4j
@Service
public class ApiService {
    @Value("${api.police-situation-url}")
    private String policeSituationUrl;
    @Value("${api.case-url}")
    private String caseUrl;
    @Value("${api.person-url}")
    private String personUrl;
    @Value("${api.car-url}")
    private String carUrl;
    @Resource
    private Environment environment;
    @Value("${api.police-situation-token}")
    private String policeSituationToken;
    @Value("${api.case-token}")
    private String caseToken;
    @Value("${api.person-token}")
    private String personToken;
    @Value("${api.car-token}")
    private String carToken;
    @Resource
    private EasyOkClient easyOkClient;

    /**
     * 关联要素类型-警情
     */
    public static final String EVENT_TYPE_INFORMING = "informing";

    /**
     * 关联要素类型-案件
     */
    public static final String EVENT_TYPE_CASE = "case";

    /**
     * 关联要素类型-人员
     */
    public static final String EVENT_TYPE_MAN = "man";

    /**
     * 关联要素类型-车辆
     */
    public static final String EVENT_TYPE_VEHICLE = "vehicle";

    /**
     * 接警单列表查询
     * @param jjbh 接警单编号
     * @param bjsj 报警时间
     * @param bjnr 报警内容
     * @param jjlx 报警类型
     * @param pageNum
     * @param pageSize
     * @return
     */
    public TableResultResponse<JjdInfoVo> getJjdInfoList(String jjbh, String bjsj, String bjnr, String jjlx, int pageNum, int pageSize){
        List<JjdInfoVo> jjdInfoVoList = new ArrayList<>();
        TableResultResponse<JjdInfoVo> response =  new TableResultResponse<>();
        String[] profiles = environment.getActiveProfiles();
        if(Arrays.asList(profiles).contains("test")){
            JjdInfoVo jjdInfoVo = JjdInfoVo.builder()
                    .jjbh("J3211126023030100004")
                    .bjnr("测试报警内容")
                    .bjrlxdh("15195992511")
                    .bjr("张三")
                    .bjsj("2023-03-01 18:14:56")
                    .jjdw("丹徒分局长山派出所")
                    .jjsfdz("镇江技师学院篮球场门口")
                    .jjlxmc("案件")
                    .build();
            jjdInfoVoList.add(jjdInfoVo);
            response.setTotal(1);
            response.setData(jjdInfoVoList);
            response.setPages(1);
        }else{
            Map<String, Object> params = Maps.newHashMap();
            params.put("page_num",pageNum);
            params.put("page_size",pageSize);
            if(!StringUtils.isEmpty(jjbh)){
                params.put("jjbh",jjbh);
            }
            if(!StringUtils.isEmpty(bjsj)){
                params.put("jjbjsj",bjsj);
            }
            if(!StringUtils.isEmpty(jjlx)){
                params.put("jjlx_mc",jjlx);
            }
            if(!StringUtils.isEmpty(bjnr)){
                params.put("jjbjnr",bjnr);
            }
            JSONObject jsonObject = callApi(policeSituationUrl,policeSituationToken,params);
            if(jsonObject.getIntValue("status") == 200){
                JSONObject dataResult = jsonObject.getJSONObject("data").getJSONObject("result");
                JSONArray array = dataResult.getJSONArray("values");
                for(int i = 0; i < array.size(); i++){
                    String[] data = array.getString(i).split(",");
                    JjdInfoVo jjdInfoVo = JjdInfoVo.builder()
                            .jjbh(data[0].split("\\[")[1])
                            .bjnr(data[47])
                            .bjrlxdh(data[8])
                            .bjr(data[5])
                            .bjsj(data[3])
                            .jjdw(data[88])
                            .jjsfdz(data[37])
                            .jjlxmc(data[41])
                            .build();
                    jjdInfoVoList.add(jjdInfoVo);
                    response.setTotal(dataResult.getIntValue("total_size"));
                    response.setData(jjdInfoVoList);
                    response.setPages(dataResult.getIntValue("total_size") / pageSize + 1);
                }
            }
        }
        return response;
    }

    /**
     * 获取单个接警单
     * @param jjbh 接警单
     * @return
     */
    public JjdInfoVo getJjdInfo(String jjbh){
        TableResultResponse<JjdInfoVo> tableResultResponse = getJjdInfoList(jjbh,null,null,null,1,1);
        List<JjdInfoVo> jjdInfoVoList = (List<JjdInfoVo>)tableResultResponse.getData();
        if(!CollectionUtils.isEmpty(jjdInfoVoList)){
            return jjdInfoVoList.get(0);
        }
        return new JjdInfoVo();
    }

    /**
     * 案件列表查询
     * @param ajbh 案件编号
     * @param ajlb 案件类别
     * @param jyaq 简要案情
     * @param pageNum
     * @param pageSize
     * @return
     */
    public TableResultResponse<CaseInfoVo> getCaseInfoList(String ajbh, String ajlb, String jyaq, int pageNum, int pageSize){
        List<CaseInfoVo> caseInfoVoList = new ArrayList<>();
        TableResultResponse<CaseInfoVo> response =  new TableResultResponse<>();
        String[] profiles = environment.getActiveProfiles();
        if(Arrays.asList(profiles).contains("test")){
            CaseInfoVo caseInfoVo = CaseInfoVo.builder()
                    .ajbh("A3211126023030100004")
                    .ajlb("刑事案件")
                    .ajmc("测试案件名称")
                    .lasj("2023-03-01 18:14:56")
                    .jyaq("这里是简要案情这里是简要案情这里是简要案情这里是简要案情这里是简要案情这里是简要案情这里是简要案情这里是简要案情这里是简要案情这里是简要案情")
                    .ajzt("侦办中")
                    .ajxxdzmc("镇江技师学院篮球场门口")
                    .slsj("2022-03-01 18:14:56")
                    .zbrxm("张三")
                    .zbdwmc("镇江市公安局")
                    .build();
            caseInfoVoList.add(caseInfoVo);
            response.setTotal(1);
            response.setData(caseInfoVoList);
            response.setPages(1);
        }else{
            Map<String, Object> params = Maps.newHashMap();
            params.put("page_num",pageNum);
            params.put("page_size",pageSize);
            if(!StringUtils.isEmpty(ajbh)){
                params.put("ajbh",ajbh);
            }
            if(!StringUtils.isEmpty(ajlb)){
                params.put("ajlbmc",ajlb);
            }
            if(!StringUtils.isEmpty(jyaq)){
                params.put("jyaq",jyaq);
            }
            JSONObject jsonObject = callApi(caseUrl,caseToken,params);
            if(jsonObject.getIntValue("status") == 200){
                JSONObject dataResult = jsonObject.getJSONObject("data").getJSONObject("result");
                JSONArray array = dataResult.getJSONArray("values");
                for(int i = 0; i < array.size(); i++){
                    String[] data = array.getString(i).split(",");
                    CaseInfoVo caseInfoVo = CaseInfoVo.builder()
                            .ajbh(data[1])
                            .ajlb(data[5])
                            .ajmc(data[12])
                            .lasj(data[73])
                            .jyaq(data[60])
                            .ajzt(data[18])
                            .ajxxdzmc(data[45])
                            .zbrxm(data[68])
                            .zbdwmc(data[62])
                            .slsj(data[53])
                            .build();
                    caseInfoVoList.add(caseInfoVo);
                    response.setTotal(dataResult.getIntValue("total_size"));
                    response.setData(caseInfoVoList);
                    response.setPages(dataResult.getIntValue("total_size") / pageSize + 1);
                }
            }
        }
        return response;
    }

    /**
     * 获取单个案件
     * @param ajbh 案件编号
     * @return
     */
    public CaseInfoVo getCaseInfo(String ajbh){
        TableResultResponse<CaseInfoVo> tableResultResponse = getCaseInfoList(ajbh,null,null,1,1);
        List<CaseInfoVo> caseInfoVoList = (List<CaseInfoVo>)tableResultResponse.getData();
        if(!CollectionUtils.isEmpty(caseInfoVoList)){
            return caseInfoVoList.get(0);
        }
        return new CaseInfoVo();
    }

    /**
     * 案件列表查询
     * @param gmsfzhm 身份证号
     * @param xm 姓名
     * @param hjd 户籍地
     * @param pageNum
     * @param pageSize
     * @return
     */
    public TableResultResponse<PersonVo> getPersonList(String gmsfzhm, String xm, String hjd, int pageNum, int pageSize){
        List<PersonVo> personVoList = new ArrayList<>();
        TableResultResponse<PersonVo> response =  new TableResultResponse<>();
        String[] profiles = environment.getActiveProfiles();
        if(Arrays.asList(profiles).contains("test")){
            PersonVo personVo = PersonVo.builder()
                    .gmsfzhm("321112602303010004")
                    .xm("张三")
                    .xbMc("男")
                    .mzMc("汉")
                    .xxMc("B型")
                    .sg("173cm")
                    .zy("工程师")
                    .zw("高级")
                    .whcdMc("本科")
                    .hyzkMc("已婚")
                    .djpcsmc("镇江派出所")
                    .hjdxxdz("镇江市润州区")
                    .xzzxxdz("镇江市润州区万达广场")
                    .dhhm("16195992433")
                    .build();
            personVoList.add(personVo);
            response.setTotal(1);
            response.setData(personVoList);
            response.setPages(1);
        }else{
            Map<String, Object> params = Maps.newHashMap();
            params.put("page_num",pageNum);
            params.put("page_size",pageSize);
            if(!StringUtils.isEmpty(gmsfzhm)){
                params.put("gmsfzhm",gmsfzhm);
            }
            if(!StringUtils.isEmpty(xm)){
                params.put("xm",xm);
            }
            if(!StringUtils.isEmpty(hjd)){
                params.put("hjdxxdz",hjd);
            }
            JSONObject jsonObject = callApi(personUrl,personToken,params);
            if(jsonObject.getIntValue("status") == 200){
                JSONArray dataResult = jsonObject.getJSONObject("data").getJSONArray("res");
                for(int i = 0; i < dataResult.size(); i++){
                    JSONObject data = dataResult.getJSONObject(i);
                    PersonVo personVoInfoVo = PersonVo.builder()
                            .gmsfzhm(data.getString("gmsfzhm"))
                            .xm(data.getString("xm"))
                            .xbMc(data.getString("xb_mc"))
                            .mzMc(data.getString("mz_mc"))
                            .xxMc(data.getString("xx_mc"))
                            .sg(data.getString("sg"))
                            .zy(data.getString("zy"))
                            .zw(data.getString("zw"))
                            .whcdMc(data.getString("whcd_mc"))
                            .hyzkMc(data.getString("hyzk_mc"))
                            .djpcsmc(data.getString("djpcsmc"))
                            .hjdxxdz(data.getString("hjdxxdz"))
                            .xzzxxdz(data.getString("xzzxxdz"))
                            .dhhm(data.getString("dhhm"))
                            .build();
                    personVoList.add(personVoInfoVo);
                    response.setTotal(data.getIntValue("total_size"));
                    response.setData(personVoList);
                    response.setPages(data.getIntValue("total_size") / pageSize + 1);
                }
            }
        }
        return response;
    }

    /**
     * 获取单个人员
     * @param sfzh 身份证号
     * @return
     */
    public PersonVo getPersonInfo(String sfzh){
        TableResultResponse<PersonVo> tableResultResponse = getPersonList(sfzh,null,null,1,1);
        List<PersonVo> personVoList = (List<PersonVo>)tableResultResponse.getData();
        if(!CollectionUtils.isEmpty(personVoList)){
            return personVoList.get(0);
        }
        return new PersonVo();
    }

    /**
     * 车辆列表查询
     * @param carNo 车牌号码
     * @param pageNum
     * @param pageSize
     * @return
     */
    public TableResultResponse<CarVo> getCarList(String carNo, int pageNum, int pageSize){
        List<CarVo> carVoList = new ArrayList<>();
        TableResultResponse<CarVo> response =  new TableResultResponse<>();
        String[] profiles = environment.getActiveProfiles();
        if(Arrays.asList(profiles).contains("test")){
            CarVo carVo = CarVo.builder()
                    .jdchphm("苏A8888")
                    .zwppmc("雪佛兰")
                    .jdcfdjddjh("A3130508")
                    .jdccllxmc("小型轿车")
                    .jdccsysmc("灰")
                    .jdcsyr("张三")
                    .clytmc("普通汽车")
                    .jdcsyrLxdh("15195992433")
                    .sfzh("370405198907244032")
                    .build();
            carVoList.add(carVo);
            response.setTotal(1);
            response.setData(carVoList);
            response.setPages(1);
        }else {
            Map<String, Object> params = Maps.newHashMap();
            params.put("page_num", pageNum);
            params.put("page_size", pageSize);
            if (!StringUtils.isEmpty(carNo)) {
                params.put("jdchphm", carNo);
            }
            JSONObject jsonObject = callApi(carUrl, carToken,params);
            JSONObject dataResult = jsonObject.getJSONObject("data").getJSONObject("result");
            JSONArray array = dataResult.getJSONArray("values");
            for (int i = 0; i < array.size(); i++) {
                String[] data = array.getString(i).split(",");
                CarVo carVo = CarVo.builder()
                        .jdchphm(data[4])
                        .zwppmc(data[5])
                        .jdcfdjddjh(data[15])
                        .jdccllxmc(data[19])
                        .jdccsysmc(data[21])
                        .jdcsyr(data[26])
                        .clytmc(data[98])
                        .jdcsyrLxdh(data[30])
                        .sfzh(data[29])
                        .build();
                carVoList.add(carVo);
                response.setTotal(dataResult.getIntValue("total_size"));
                response.setData(carVoList);
                response.setPages(dataResult.getIntValue("total_size") / pageSize + 1);
            }
        }
        return response;
    }

    /**
     * 获取单个车辆
     * @param carNo 车牌号
     * @return
     */
    public CarVo getCarInfo(String carNo){
        TableResultResponse<CarVo> tableResultResponse = getCarList(carNo,1,1);
        List<CarVo> carVoList = (List<CarVo>)tableResultResponse.getData();
        if(!CollectionUtils.isEmpty(carVoList)){
            return carVoList.get(0);
        }
        return new CarVo();
    }

    /**
     * 查询关联要素详情
     * @param eventIds 要素id
     * @param eventType 要素类型
     * @return
     */
    public ObjectRestResponse getEventDetail(String[] eventIds,String eventType){
        if(EVENT_TYPE_INFORMING.equals(eventType)){
            List<JjdInfoVo> jjdInfoVoList = new ArrayList<>(eventIds.length);
            for(String eventId : eventIds){
                JjdInfoVo jjdInfoVo = getJjdInfo(eventId);
                jjdInfoVoList.add(jjdInfoVo);
            }
            return new ObjectRestResponse().data(jjdInfoVoList);
        }else if(EVENT_TYPE_CASE.equals(eventType)){
            List<CaseInfoVo> caseInfoVoList = new ArrayList<>(eventIds.length);
            for(String eventId : eventIds){
                CaseInfoVo caseInfoVo = getCaseInfo(eventId);
                caseInfoVoList.add(caseInfoVo);
            }
            return new ObjectRestResponse().data(caseInfoVoList);
        }else if(EVENT_TYPE_MAN.equals(eventType)){
            List<PersonVo> personVoList = new ArrayList<>(eventIds.length);
            for(String eventId : eventIds){
                PersonVo personVo = getPersonInfo(eventId);
                personVoList.add(personVo);
            }
            return new ObjectRestResponse().data(personVoList);
        }else if(EVENT_TYPE_VEHICLE.equals(eventType)){
            List<CarVo> carVoList = new ArrayList<>(eventIds.length);
            for(String eventId : eventIds){
                CarVo carVo = getCarInfo(eventId);
                carVoList.add(carVo);
            }
            return new ObjectRestResponse().data(carVoList);
        }
        return new ObjectRestResponse().data(null);
    }

    /**
     * 调用平台api
     * @param url
     * @param params
     * @return
     */
    private JSONObject callApi(String url,String token,Map<String, Object> params){
        Map<String, Object> headers = Maps.newHashMap();
        headers.put("Authorization", token);
        return JSON.parseObject(easyOkClient.get(url,params,headers,String.class));
    }
}
