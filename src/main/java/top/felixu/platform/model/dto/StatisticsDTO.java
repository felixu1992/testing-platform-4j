package top.felixu.platform.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author felixu
 * @since 2021.11.09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO {

    @ApiModelProperty("项目数目")
    private Integer projectNum;

    @ApiModelProperty("用例数目")
    private Integer caseNum;

    @ApiModelProperty("记录数目")
    private Integer recordNum;
}
