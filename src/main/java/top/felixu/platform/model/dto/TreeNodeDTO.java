package top.felixu.platform.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据 VUE 使用的树节点内容封装，前端拿到直接可用
 *
 * @author felixu
 * @since 2021.08.31
 */
@Data
@NoArgsConstructor
@ApiModel("联系人分组树节点")
public class TreeNodeDTO {

    @ApiModelProperty("显示内容")
    private String title;

    @ApiModelProperty("选择传递的值")
    private Integer value;

    @ApiModelProperty("选项的唯一键")
    private Integer key;

    @ApiModelProperty("是否可以选中")
    private Boolean disable = Boolean.FALSE;

    @ApiModelProperty("子节点")
    private List<TreeNodeDTO> children = new ArrayList<>();

    public TreeNodeDTO(String title, Integer key, Integer value) {
        this.title = title;
        this.key = key;
        this.value = value;
    }
}
