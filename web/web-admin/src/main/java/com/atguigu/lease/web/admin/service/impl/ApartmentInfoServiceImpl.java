package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.ApartmentInfoMapper;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {


    @Autowired
    private GraphInfoService graphInfoService;    //可能涉及列表操作，因为通用mapper 不支持批量插入 这里注入service

    @Autowired
    private ApartmentFacilityService apartmentFacilityService;

    @Autowired
    private ApartmentLabelService apartmentLabelService;
    @Autowired
    private ApartmentFeeValueService apartmentFeeValueService;
    @Override
    public void saveOrUpdateApartment(ApartmentSubmitVo apartmentSubmitVo) {
        boolean isUpdate = apartmentSubmitVo.getId()!=null;
        super.saveOrUpdate(apartmentSubmitVo);

        if(isUpdate){
            //1,删除图片列表
            LambdaQueryWrapper<GraphInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT);
            queryWrapper.eq(GraphInfo::getItemId,apartmentSubmitVo.getId());
            graphInfoService.remove(queryWrapper);
            //2,删除配套列表
            LambdaQueryWrapper<ApartmentFacility> facilityQueryWrapper =new LambdaQueryWrapper<>();
            facilityQueryWrapper.eq(ApartmentFacility::getApartmentId,apartmentSubmitVo.getId());
            apartmentFacilityService.remove(facilityQueryWrapper);
            //3,删除标签列表
            LambdaQueryWrapper<ApartmentLabel> apartmentLabelQueryWrapper = new LambdaQueryWrapper<>();
            apartmentLabelQueryWrapper.eq(ApartmentLabel::getApartmentId,apartmentSubmitVo.getId());
            apartmentLabelService.remove(apartmentLabelQueryWrapper);
            //4,删除杂费列表
            LambdaQueryWrapper<ApartmentFeeValue> apartmentFeeValueQueryWrapper = new LambdaQueryWrapper<>();
            apartmentFeeValueQueryWrapper.eq(ApartmentFeeValue::getApartmentId,apartmentSubmitVo.getId());
            apartmentFeeValueService.remove(apartmentFeeValueQueryWrapper);

        }

            //1,插入图片列表
            List<GraphVo> graphVoList = apartmentSubmitVo.getGraphVoList();
            if (!CollectionUtils.isEmpty(graphVoList)){
                ArrayList<GraphInfo> graphInfoList = new ArrayList<>();
                for (GraphVo graphVo : graphVoList) {
                    GraphInfo graphInfo = new GraphInfo();
                    graphInfo.setItemType(ItemType.APARTMENT);
                    graphInfo.setItemId(apartmentSubmitVo.getId());
                    graphInfo.setName(graphVo.getName());
                    graphInfo.setUrl(graphVo.getUrl());
                    graphInfoList.add(graphInfo);

                }
                graphInfoService.saveBatch(graphInfoList);
            }
            // 2,插入配套列表
             List<Long> facilityInfoIdList = apartmentSubmitVo.getFacilityInfoIds();
            ArrayList<ApartmentFacility> facilityList = new ArrayList<>();
             if (!CollectionUtils.isEmpty(facilityInfoIdList)){
                    for (Long facilityid : facilityInfoIdList) {
                        ApartmentFacility apartmentFacility = ApartmentFacility
                                                              .builder()
                                                              .apartmentId(apartmentSubmitVo.getId())
                                                              .facilityId(facilityid)
                                                              .build();

                        facilityList.add(apartmentFacility);
                    }
                    apartmentFacilityService.saveBatch(facilityList);
                }
        //3.插入标签列表
        List<Long> labelIds = apartmentSubmitVo.getLabelIds();
        if (!CollectionUtils.isEmpty(labelIds)) {
            List<ApartmentLabel> apartmentLabelList = new ArrayList<>();
            for (Long labelId : labelIds) {
                ApartmentLabel apartmentLabel = ApartmentLabel.builder()
                                                .apartmentId(apartmentSubmitVo.getId())
                                                .labelId(labelId)
                                                .build();

                apartmentLabelList.add(apartmentLabel);
            }
            apartmentLabelService.saveBatch(apartmentLabelList);
        }


        //4.插入杂费列表
        List<Long> feeValueIds = apartmentSubmitVo.getFeeValueIds();
        if (!CollectionUtils.isEmpty(feeValueIds)) {
            ArrayList<ApartmentFeeValue> apartmentFeeValueList = new ArrayList<>();
            for (Long feeValueId : feeValueIds) {
                ApartmentFeeValue apartmentFeeValue =  ApartmentFeeValue.builder().build();
                apartmentFeeValue.setApartmentId(apartmentSubmitVo.getId());
                apartmentFeeValue.setFeeValueId(feeValueId);
                apartmentFeeValueList.add(apartmentFeeValue);
            }
            apartmentFeeValueService.saveBatch(apartmentFeeValueList);
        }
    }
}




