package top.felixu.platform.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import top.felixu.platform.model.entity.Contactor;

/**
 * @author felixu
 * @since 2021.11.05
 */
@Data
public class ContactorDTO extends Contactor {

    @ApiModelProperty("分组名称")
    private String groupName;
}
