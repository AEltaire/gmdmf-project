package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.TeacherDTO;
import net.zjitc.entity.Teacher;
import net.zjitc.exception.BusinessException;
import net.zjitc.mapper.TeacherMapper;
import net.zjitc.service.TeacherService;
import net.zjitc.utils.AliOssUtil;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.utils.Md5Util;
import net.zjitc.utils.ThreadLocalUtil;
import net.zjitc.vo.TeacherVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Override
    public boolean saveTeacher(TeacherDTO teacherDTO) {

        Teacher teacher = BeanUtil.copyProperties(teacherDTO, Teacher.class);

        String number = String.valueOf(teacherDTO.getIdCard());
        Integer year = Convert.toInt(number.substring(6, 10));
        Integer month = Convert.toInt(number.substring(10, 12));
        Integer day = Convert.toInt(number.substring(12, 14));
        LocalDateTime localDateTime = LocalDateTime.of(year, month, day, 0, 0, 0);

        teacher.setBirth(localDateTime).setRule("教师").setPassword(Md5Util.getMD5String(teacher.getPassword()));
        return save(teacher);
    }

    @Override
    public boolean updateByPassword(String oldPassword, String newPassword) {

        CheckEmptyOrRuleUtil.isTeacher();
        String username = ThreadLocalUtil.getUsername();
        Teacher tea = baseMapper.findByUsername(username);
        if (!tea.getPassword().equals(Md5Util.getMD5String(oldPassword)))
            throw new BusinessException(Code.BUSINESS_ERR, "原密码输入错误");
        Teacher teacher = lambdaQuery().eq(Teacher::getId, ThreadLocalUtil.getId()).one();
        if (teacher == null) throw new BusinessException(Code.BUSINESS_ERR, "用户不存在");
        teacher.setPassword(Md5Util.getMD5String(newPassword));
        return updateById(teacher);
    }


    @Override
    public boolean updateByPicture(String pictureUrl) throws Exception {

        CheckEmptyOrRuleUtil.isTeacher();
        Teacher teacher = getById(ThreadLocalUtil.getId());
        if (teacher!= null && StrUtil.isNotBlank(teacher.getPicture())) {
            String replace = teacher.getPicture().replace("https://axier-spring.oss-cn-hangzhou.aliyuncs.com/", "");
            AliOssUtil.deleteFile(replace);
        }
        return lambdaUpdate().eq(Teacher::getId, ThreadLocalUtil.getId()).set(Teacher::getPicture, pictureUrl).update();
    }

    @Override
    public boolean updateTeacher(TeacherDTO teacherDTO) {
        Teacher teacher = BeanUtil.copyProperties(teacherDTO, Teacher.class);
        return updateById(teacher);
    }

    @Override
    public boolean deleteTeacher(Long id) {
        CheckEmptyOrRuleUtil.isManager();
        try {
            return removeById(id);
        } catch (Exception e) {
            throw new BusinessException(Code.BUSINESS_ERR, "该教师下存在关联数据，无法删除");
        }
    }

    @Override
    public TeacherVO findById(Long id) {
        Teacher teacher = getById(id);
        if (teacher == null) throw new BusinessException(Code.BUSINESS_ERR, "教师数据异常");
        return convertor(teacher);
    }

    @Override
    public Teacher findByUsername(String username) {
        return baseMapper.findByUsername(username);
    }

    @Override
    public boolean deleteTeacherList(List<Long> ids) {

        CheckEmptyOrRuleUtil.isManager();
        if (CheckEmptyOrRuleUtil.isEmptyList(ids)) throw new BusinessException(Code.BUSINESS_ERR, "数据异常");
        try {
            return removeBatchByIds(ids);
        } catch (Exception e) {
            throw new BusinessException(Code.BUSINESS_ERR, "选中教师存在关联数据，无法删除");
        }
    }

    @Override
    public boolean updateTeacherList(List<TeacherDTO> teacherDTOS) {
        CheckEmptyOrRuleUtil.isManager();
        if (CheckEmptyOrRuleUtil.isEmptyList(teacherDTOS)) throw new BusinessException(Code.BUSINESS_ERR, "数据异常");
        List<Teacher> teachers = BeanUtil.copyToList(teacherDTOS, Teacher.class);
        return updateBatchById(teachers);
    }


    @Override
    public TeacherVO getTeacherInfo() {
        CheckEmptyOrRuleUtil.isTeacher();
        Teacher teacher = lambdaQuery().eq(Teacher::getId, ThreadLocalUtil.getId()).one();
        return BeanUtil.copyProperties(teacher, TeacherVO.class);
    }

    @Override
    public List<TeacherVO> findAll() {
        return lambdaQuery().list().stream().map(this::convertor).toList();
    }

    private TeacherVO convertor(Teacher teacher) {
        return BeanUtil.copyProperties(teacher, TeacherVO.class);
    }
}
