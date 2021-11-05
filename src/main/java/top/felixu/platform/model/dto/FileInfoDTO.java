package top.felixu.platform.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import top.felixu.platform.model.entity.FileInfo;

/**
 * @author felixu
 * @since 2021.11.05
 */
@Data
public class FileInfoDTO extends FileInfo {

    @ApiModelProperty("分组名称")
    private String groupName;
}
