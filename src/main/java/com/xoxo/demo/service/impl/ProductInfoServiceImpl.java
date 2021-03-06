package com.xoxo.demo.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.xoxo.demo.common.constants.RedisConstant;
import com.xoxo.demo.common.enums.ExceptionEnum;
import com.xoxo.demo.common.exception.BuizException;
import com.xoxo.demo.dto.ProductInfoDTO;
import com.xoxo.demo.entity.ProductInfo;
import com.xoxo.demo.mapper.generic.ProductInfoMapper;
import com.xoxo.demo.redis.IRedisService;
import com.xoxo.demo.service.ProductInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Package com.xoxo.demo.service.impl
 * @Description
 * @Author xiehua@zhongshuheyi.com
 * @Date 2019-01-08 15:45
 */
@Slf4j
@Service
public class ProductInfoServiceImpl implements ProductInfoService {

    @Resource
    private ProductInfoMapper productInfoMapper;
    @Resource
    private IRedisService redisService;

    @Override
    public List<ProductInfoDTO> findProductInfos() {
        String productInfoListStr;
        List<ProductInfoDTO> productInfoDTOS;
        List<ProductInfo> productInfos;
        // 异常捕获测试
        if("".equals("")){
            throw new BuizException(ExceptionEnum.buiz_ex001);
        }
        //redis有就取出，没有就去数据库查询
        if (StringUtils.isEmpty((productInfoListStr = redisService.get(RedisConstant.Product.PRODUCT_INFOS)))) {
            productInfos = productInfoMapper.selectByExample(null);
            //存入redis
            redisService.set(RedisConstant.Product.PRODUCT_INFOS, JSONArray.toJSONString(productInfos));
        } else {
            productInfos = JSONArray.parseArray(productInfoListStr, ProductInfo.class);
        }
        log.info("查询商品信息，productInfos=={}",productInfos.toString());
        //数据转换，entity->dto
        productInfoDTOS = productInfos.stream().
                map(e->{
                    ProductInfoDTO productInfoDTO = new ProductInfoDTO();
                    BeanUtils.copyProperties(e,productInfoDTO);
                    return productInfoDTO;
                }).collect(Collectors.toList());

        return productInfoDTOS;
    }
}
