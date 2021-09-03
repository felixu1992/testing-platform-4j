package top.felixu.platform.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.felixu.common.bean.BeanUtils;
import top.felixu.platform.exception.ErrorCode;
import top.felixu.platform.exception.PlatformException;
import top.felixu.platform.model.dto.TreeNodeDTO;
import top.felixu.platform.model.entity.ContactorGroup;
import top.felixu.platform.model.entity.FileGroup;
import top.felixu.platform.model.form.PageRequestForm;
import top.felixu.platform.service.FileGroupService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author felixu
 * @since 2021.09.03
 */
@Service
@RequiredArgsConstructor
public class FileGroupManager {

    private final FileGroupService fileGroupService;

    public FileGroup getFileGroupById(Integer id) {
        return fileGroupService.getFileGroupByIdAndCheck(id);
    }

    public IPage<FileGroup> page(FileGroup group, PageRequestForm form) {
        return fileGroupService.page(form.toPage(), Wrappers.lambdaQuery(group));
    }

    public List<TreeNodeDTO> tree() {
        List<FileGroup> groups = fileGroupService.getFileGroupList();
        if (CollectionUtils.isEmpty(groups))
            return Collections.emptyList();
        Set<Integer> groupIds = new HashSet<>();
        List<TreeNodeDTO> result = groups.stream().map(group -> {
            groupIds.add(group.getId());
            TreeNodeDTO dto = new TreeNodeDTO(group.getName(), group.getId(), group.getId());
            dto.setDisable(Boolean.TRUE);
            return dto;
        }).collect(Collectors.toList());
        // TODO: 09/03 填充子节点
//        Map<Integer, List<Contactor>> childrenMap = contactorService.mapByGroupIds(groupIds);
//        result.parallelStream().forEach(group -> {
//            // 没有判断从 Map 中取值的结果不为 null 的原因是 ContactorService#mapByGroupIds 保证了不会为 null
//            group.setChildren(childrenMap.get(group.getKey()).stream().map(contactor -> {
//                ContactorTreeDTO dto = new ContactorTreeDTO();
//                dto.setTitle(contactor.getName());
//                dto.setValue(contactor.getId());
//                dto.setKey(contactor.getId());
//                return dto;
//            }).collect(Collectors.toList()));
//        });
        return result;
    }

    public FileGroup create(FileGroup group) {
        check(group);
        return fileGroupService.create(group);
    }

    public FileGroup update(FileGroup group) {
        FileGroup original = fileGroupService.getFileGroupByIdAndCheck(group.getId());
        BeanUtils.copyNotEmpty(FileGroup.class, group, FileGroup.class, original);
        check(original);
        return fileGroupService.update(original);
    }

    public void delete(Integer id) {
        FileGroup group = fileGroupService.getFileGroupByIdAndCheck(id);
        // TODO: 09/03 判断是否有文件
//        if (contactorService.countByGroupId(id) > 0)
//            throw new PlatformException(ErrorCode.CONTACTOR_GROUP_USED_BY_CONTACTOR);
        fileGroupService.delete(group);
    }

    private void check(FileGroup group) {
        FileGroup one = fileGroupService.getOne(Wrappers.<FileGroup>lambdaQuery().eq(FileGroup::getName, group.getName()));
        if (one != null && (group.getId() == null || !group.getId().equals(one.getId())))
            throw new PlatformException(ErrorCode.FILE_GROUP_DUPLICATE_NAME);
    }
}
