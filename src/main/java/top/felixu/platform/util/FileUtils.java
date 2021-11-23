package top.felixu.platform.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import top.felixu.common.json.JsonUtils;
import top.felixu.platform.enums.HttpMethodEnum;
import top.felixu.platform.model.entity.CaseInfo;
import top.felixu.platform.model.entity.Dependency;
import top.felixu.platform.model.entity.Expected;

import java.io.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author : zhan9yn
 * @version : 1.0
 * @date : 2021/11/8 2:49 下午
 */
@Slf4j
public class FileUtils {
    public static void zipFile(String filePath, String fileName, String outPath) throws IOException {
            //创建Test.zip文件
            OutputStream is = new FileOutputStream(outPath);
            //检查输出流,采用CRC32算法，保证文件的一致性
            CheckedOutputStream cos = new CheckedOutputStream(is, new CRC32());
            //创建zip文件的输出流
            ZipOutputStream zos = new ZipOutputStream(cos);
            //需要压缩的文件或文件夹对象
            File file = new File(filePath);
            //压缩文件的具体实现函数
            zipFilePost(zos,file,filePath,fileName,outPath);
            zos.close();
            cos.close();
            is.close();
            System.out.println("压缩完成");
    }

    /**
     * 压缩文件
     * @param zos       zip文件的输出流
     * @param file      需要压缩的文件或文件夹对象
     * @param filePath  压缩的文件路径
     * @param fileName  需要压缩的文件夹名
     * @param outPath   缩完成后保存为Test.zip文件
     */
    private static void zipFilePost(ZipOutputStream zos, File file, String filePath, String fileName, String outPath) throws IOException{

        String path = file.getPath();
        String zosName = "";
        if(!StringUtils.isEmpty(path)){
            zosName = path.substring(path.indexOf(fileName));
        }
        File[] files = file.listFiles();
        if(file.isDirectory() && files != null && files.length > 0) {
            // 创建压缩文件的目录结构
            zos.putNextEntry(new ZipEntry(zosName + File.separator));
            for(File f : files) {
                zipFilePost(zos, f, filePath, fileName, outPath);
            }
        } else {
            log.info("正在压缩文件:{}",file.getName());
            // 创建压缩文件
            zos.putNextEntry(new ZipEntry(zosName));
            // 用字节方式读取源文件
            InputStream is = new FileInputStream(file.getPath());
            // 创建一个缓存区
            BufferedInputStream bis = new BufferedInputStream(is);
            // 字节数组,每次读取1024个字节
            byte [] b = new byte[1024];
            // 循环读取，边读边写
            while(bis.read(b)!=-1) {
                // 写入压缩文件
                zos.write(b);
            }
            //关闭流
            bis.close();
            is.close();
        }

    }

    /**
     * @description 检查文件是否为excel
     * @param fileName
     * @author zhangyn
     * @date 2021/6/3 5:39 下午
     * @return boolean
     */
    public static boolean checkExcelFile(String fileName) {
        if (null == fileName || "".equals(fileName)) {
            return false;
        }
        return fileName.toLowerCase().matches(".*(\\.xls$|\\.xlsx$)");
    }


    /**
     * @description extract data
     * @param sheet
     * @author zhangyn
     * @date 2021/6/3 5:38 下午
     */
    public static List<CaseInfo> parseExcelSheet(Sheet sheet) {
        Row columnRow = sheet.getRow(5);
        if (null == columnRow) {
            throw new IllegalArgumentException("Sheet=[" + sheet.getSheetName() + "] - 数据格式不对，数据必须从第六行开始");
        }
        List<CaseInfo> res = new ArrayList<>();
        Iterator<Row> rowIterator = sheet.rowIterator();
        int index = 5;
        while (index > 0) {
            rowIterator.next();
            index--;
        }
        int ii = 0;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
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
            log.info("第{}行", ii++);
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
            caseInfo.setSort(ii - 6);
            res.add(caseInfo);
        }
        return res;
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

    /**
     * @description 迭代sheet
     * @param inputStream
     * @author zhangyn
     * @date 2021/6/3 5:48 下午
     * @return java.util.List<com.isyscore.bigdatagroup.udmp.data.api.vo.ExcelDataVo>
     */
    public static List<CaseInfo> parseExcel(InputStream inputStream) throws Exception {
        List<CaseInfo> resultList = new ArrayList<>();
        try (Workbook wb = WorkbookFactory.create(inputStream)) {
            Iterator<Sheet> iterator = wb.sheetIterator();
            while (iterator.hasNext()) {
                resultList.addAll(parseExcelSheet(iterator.next()));
            }
        }
        return resultList;
    }
}
