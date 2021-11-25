package top.felixu.platform.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import top.felixu.common.json.JsonUtils;
import top.felixu.platform.enums.HttpMethodEnum;
import top.felixu.platform.model.entity.CaseInfo;
import top.felixu.platform.model.entity.Dependency;
import top.felixu.platform.model.entity.Expected;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zhan9yn
 * @version : 1.0
 * @description : TODO
 * @date : 2021/11/24 10:35 上午
 */
public class CaseInfoConvoter implements CellConvoter{

    @Override
    public Object convoterTo(Row row) {
        CaseInfo caseInfo = new CaseInfo();
        //获取null的时候可以为空字符串
        String desc = getVal(row.getCell(0), "");
        String step = row.getCell(1).getStringCellValue();
        caseInfo.setRemark(desc);
        caseInfo.setName(desc + ":" + step);
        caseInfo.setRun("NO".equalsIgnoreCase(getVal(row.getCell(2), "YES")) ? false : true);
        caseInfo.setMethod(HttpMethodEnum.valueOf(getVal(row.getCell(3), "GET").toUpperCase()));
        caseInfo.setHost(null);
        caseInfo.setPath(getVal(row.getCell(5), ""));
        caseInfo.setDelay(Integer.parseInt(getVal(row.getCell(6), "0")));
        final String params = getVal(row.getCell(7), "");
        caseInfo.setParams(StringUtils.isEmpty(params)? null : JsonUtils.fromJsonToMap(params));

        if (StringUtils.isEmpty(getVal(row.getCell(8),""))) {
            caseInfo.setDependencies(null);
        }else {
            String[] exKeys = row.getCell(8).getStringCellValue().split(",");
            String[] exValues = row.getCell(9).getStringCellValue().split(",");
            List<Dependency> dependencies = new ArrayList<>(16);
            for (int i = 0; i < exKeys.length; i++) {
                Dependency dependency = new Dependency();
                dependency.setDependKey(exKeys[i].split("\\."));
                final String depend = exValues[i].split(":")[0];
                final String value = exValues[i].split(":")[1];
                dependency.setDependValue(Dependency.DependValue.builder().depend(Integer.parseInt(depend) - 5).steps(value.split(",")).build());
                dependencies.add(dependency);
            }
            caseInfo.setDependencies(dependencies);
        }
        final String headers = getVal(row.getCell(10), "");
        caseInfo.setHeaders(StringUtils.isEmpty(headers) ? null :
                JsonUtils.fromJsonToMap(headers, String.class, String.class));

        //11列和12列必定有值
        String[] expKey = getVal(row.getCell(11), "").split(",");
        List<Expected> expecteds = new ArrayList<>();
        if (row.getCell(12).getCellType().equals(CellType.NUMERIC)) {
            //证明只有一个值
            expecteds.add(new Expected(expKey, Expected.ExpectValue.builder().value(new Double(row.getCell(12).getNumericCellValue()).intValue()).fixed(true).build()));
        } else {
            //证明有多个值，用 , 隔开
            String[] expVal = row.getCell(12).getStringCellValue().split(",");
            for (int i = 0; i < expKey.length; i++) {
                final String key = expKey[i];
                final String val = expVal[i];
                Expected expected = new Expected();
                expected.setExpectKey(key.split("\\."));
                //带有依赖
                if (val.contains(":") && val.contains("\\.")) {
                    final String depend = val.split(":")[0];
                    expected.setExpectValue(
                            Expected.ExpectValue.builder()
                                    .depend(Integer.parseInt(depend) - 5)
                                    .steps(val.split(":")[1].split("."))
                                    .fixed(false)
                                    .build());
                } else {
                    expected.setExpectValue(Expected.ExpectValue.builder()
                            .fixed(true)
                            .value(val)
                            .build());
                }
                expecteds.add(expected);
            }
        }
        caseInfo.setExpects(expecteds);
        // 11/23，借助sort来设置依赖的索引
        caseInfo.setSort(row.getRowNum() - 6);

        return caseInfo;
    }

    private static String getVal(Cell cell, String defaultVal) {
        String str = defaultVal;
        if (cell != null) {
            if (!StringUtils.isEmpty(cell.getStringCellValue())) {
                str = cell.getStringCellValue();;
            }
        }
        return str;
    }
}
