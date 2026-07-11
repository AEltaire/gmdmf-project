package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.ManageDTO;
import net.zjitc.entity.Manage;
import net.zjitc.enums.Rule;
import net.zjitc.exception.BusinessException;
import net.zjitc.mapper.ManageMapper;
import net.zjitc.service.ManageService;
import net.zjitc.utils.AliOssUtil;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.utils.Md5Util;
import net.zjitc.utils.ThreadLocalUtil;
import net.zjitc.vo.ManageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ManageServiceImpl extends ServiceImpl<ManageMapper, Manage> implements ManageService {

    @Override
    public Manage findByUsername(String username) {
        return baseMapper.findByUsername(username);
    }

    @Override
    public ManageVO getManageInfo() {

        CheckEmptyOrRuleUtil.isManager();
        Manage manage = lambdaQuery().eq(Manage::getId, ThreadLocalUtil.getId()).one();
        return BeanUtil.copyProperties(manage, ManageVO.class);
    }

    @Override
    public List<ManageVO> findAll() {

        CheckEmptyOrRuleUtil.isManager();
        List<Manage> manages = lambdaQuery().list();
        return BeanUtil.copyToList(manages, ManageVO.class);
    }

    @Override
    public boolean saveOrUpdateManage(ManageDTO manageDTO) throws Exception {

        System.out.println(manageDTO);
        CheckEmptyOrRuleUtil.isManager();
        if (manageDTO.getId() == null && ThreadLocalUtil.getId() != 1)
            throw new BusinessException(Code.BUSINESS_ERR, "非 admin 管理不能添加");
        if (!ThreadLocalUtil.getId().equals(manageDTO.getId()) && ThreadLocalUtil.getId() != 1)
            throw new BusinessException(Code.BUSINESS_ERR, "不能修改其他管理员信息");
        Manage m = lambdaQuery().eq(Manage::getUsername, manageDTO.getUsername()).ne(manageDTO.getId() != null, Manage::getId, manageDTO.getId()).one();
        if (m != null) throw new BusinessException(Code.BUSINESS_ERR, "用户名已存在");

        // 更新头像
        if (manageDTO.getId() != null) {
            Manage manage = getById(manageDTO.getId());
            if (manage != null && StrUtil.isNotBlank(manage.getPicture())) {
                String replace = manage.getPicture().replace("https://axier-spring.oss-cn-hangzhou.aliyuncs.com/", "");
                AliOssUtil.deleteFile(replace);
            }
        }

        Manage manage = BeanUtil.copyProperties(manageDTO, Manage.class);
        if (StrUtil.isNotBlank(manage.getPassword())) manage.setPassword(Md5Util.getMD5String(manage.getPassword()));
        return saveOrUpdate(manage);
    }

    @Override
    public boolean deleteManage(Integer id) {
        try {
            return removeById(id);
        } catch (Exception e) {
            throw new BusinessException(Code.BUSINESS_ERR, "删除失败");
        }
    }
}
