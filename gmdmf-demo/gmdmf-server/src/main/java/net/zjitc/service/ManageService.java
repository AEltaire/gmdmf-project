package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.ManageDTO;
import net.zjitc.entity.Manage;
import net.zjitc.vo.ManageVO;

import java.util.List;

public interface ManageService extends IService<Manage> {

    Manage findByUsername(String username);

    ManageVO getManageInfo();

    List<ManageVO> findAll();

    boolean saveOrUpdateManage(ManageDTO manageDTO) throws Exception;

    boolean deleteManage(Integer id);
}
