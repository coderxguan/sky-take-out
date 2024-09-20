package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 功能:
 * 作者:
 * 日期:2024/09/19 11:15
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkspaceService workspaceService;

    // 统计指定时间区间内的营业额数据
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 从begin到end的每天的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)) {
            // 计算指定日期后一天
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Double> turnoverList = new ArrayList<>();

        for(LocalDate date : dateList) {
            // 查询date日期对应的营业额数据, 状态为  已完成  的订单金额合
            //select sum(amount) from orders where order_time > ? and order_time < ? status = 5
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover =  orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }



        // 封装返回结果
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }


    // 统计用户数据
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 存放每天对应的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 新增用户  select count(id) from user where create_time < ? and create_time > ?
        List<Integer> newUserList = new ArrayList<>();
        //总用户数量  select count(id) from user where create_time < ?
        List<Integer> totalUserList = new ArrayList<>();

        for(LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("end", endTime);

            // 总用户数量
            Integer totalUser = userMapper.countByMap(map);

            map.put("begin", beginTime);
            // 新增用户数量
            Integer newUser = userMapper.countByMap(map);

            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }


        // 分装结果数据
        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }


    // 统计订单数据
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        // 存放每天对应的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        // 每天订单数
        List<Integer> orderCountList = new ArrayList<>();
        // 每天有效订单
        List<Integer> validOrderList = new ArrayList<>();

        // 遍历日期集合
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 查询每天的订单总数 select count(id) from orders where order_time > ? and order_time < ?
            Integer orderCount = getOrderCount(beginTime, endTime, null);
            orderCountList.add(orderCount);
            // 有效的订单数数  select count(id) from orders where order_time > ? and order_time < ? and status = 5
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            validOrderList.add(validOrderCount);
        }

        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();

        Integer validOrderCount = validOrderList.stream().reduce(Integer::sum).get();

        // 订单完成率
        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0)
            orderCompletionRate =  validOrderCount.doubleValue() / totalOrderCount;

        // 封装结果
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderList, ","))
                .orderCompletionRate(orderCompletionRate)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .build();

    }

    // 销量前十
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);

        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names, ",");

        List<Integer> number = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(number, ",");

        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }


    // 导出数据报表
    @Override
    public void exportBusinessData(HttpServletResponse response)  {
        // 1. 查询数据, 获取营业数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        BusinessDataVO businessDataVO = workspaceService.getBusinessData(
                LocalDateTime.of(dateBegin, LocalTime.MIN),
                LocalDateTime.of(dateEnd, LocalTime.MAX));

        //2. 通过POI将数据写入到Excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            // 基于模版文件创建一个新的文件
            XSSFWorkbook excel = new XSSFWorkbook(in);

            // 获取sheet标签页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            // 填充数据--时间
            sheet.getRow(1).getCell(1).setCellValue("时间: " + dateBegin + "至" + dateEnd);

            // 获取第四行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4 ).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            // 获取第五行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4 ).setCellValue(businessDataVO.getUnitPrice());

            // 填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                // 查询某一天的数据
                businessDataVO = workspaceService.getBusinessData(
                        LocalDateTime.of(date, LocalTime.MIN),
                        LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessDataVO.getTurnover());
                row.getCell(3).setCellValue(businessDataVO.getValidOrderCount());
                row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessDataVO.getUnitPrice());
                row.getCell(6).setCellValue(businessDataVO.getNewUsers());
            }

            //3. 通过输入流将Excel文件下载到浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            //关闭资源
            out.close();
            excel.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);
        return orderMapper.countByMap(map);
    }
}
