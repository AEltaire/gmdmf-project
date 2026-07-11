package net.zjitc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import net.zjitc.constant.Code;
import net.zjitc.dto.TeamDTO;
import net.zjitc.entity.*;
import net.zjitc.exception.BusinessException;
import net.zjitc.mapper.TeamMapper;
import net.zjitc.query.TeamQuery;
import net.zjitc.service.CourseTeamService;
import net.zjitc.service.TeamService;
import net.zjitc.utils.CheckEmptyOrRuleUtil;
import net.zjitc.utils.ThreadLocalUtil;
import net.zjitc.vo.StudentVO;
import net.zjitc.vo.TeamVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {

    private final CourseTeamService courseTeamService;

    @Override
    public List<TeamVO> findByCourseId(Long courseId) {
        CheckEmptyOrRuleUtil.isEmptyId(courseId);
        List<Team> teamList = courseTeamService.getTeamByCourseId(courseId);
        if (CheckEmptyOrRuleUtil.isEmptyList(teamList)) return Collections.emptyList();
        return BeanUtil.copyToList(teamList, TeamVO.class);
    }

    @Override
    public List<StudentVO> findById(Long id) {
        CheckEmptyOrRuleUtil.isEmptyId(id);
        List<Student> students = Db.lambdaQuery(Student.class)
                .eq(Student::getTeamId, id)
                .orderByAsc(Student::getName).list();
        if (CheckEmptyOrRuleUtil.isEmptyList(students)) return Collections.emptyList();
        return BeanUtil.copyToList(students, StudentVO.class);
    }

    @Override
    public List<TeamVO> findByTeacherId() {
        return courseTeamService.getTeamByTeacherId();
    }

    @Override
    public List<TeamVO> findByTeamQuery(TeamQuery teamQuery) {

        String schoolName = teamQuery.getSchoolName();
        String departmentName = teamQuery.getDepartmentName();
        String majorName = teamQuery.getMajorName();

        if (CheckEmptyOrRuleUtil.isEmptyString(schoolName) || CheckEmptyOrRuleUtil.isEmptyString(departmentName) || CheckEmptyOrRuleUtil.isEmptyString(majorName)) {
            return Collections.emptyList();
        }

        School school = Db.lambdaQuery(School.class).eq(School::getName, schoolName).one();
        if (school == null) throw new BusinessException(Code.BUSINESS_ERR, "学校不存在");

        Department department = Db.lambdaQuery(Department.class).eq(Department::getSchoolId, school.getId()).eq(Department::getName, departmentName).one();
        if (department == null) throw new BusinessException(Code.BUSINESS_ERR, "院系不存在");

        Major major = Db.lambdaQuery(Major.class).eq(Major::getName, majorName).one();
        if (major == null) throw new BusinessException(Code.BUSINESS_ERR, "专业不存在");

        List<Team> teamList = lambdaQuery().eq(Team::getDepartmentId, department.getId()).eq(Team::getMajorId, major.getId()).gt(Team::getEndTime, LocalDateTime.now()).list();
        return BeanUtil.copyToList(teamList, TeamVO.class);
    }

    @Override
    public List<Object> findAll() {

        List<Team> teamList = lambdaQuery().list();

        List<Long> majorIds = teamList.stream().map(Team::getMajorId).toList();
        List<Major> majorList = Db.lambdaQuery(Major.class).in(Major::getId, majorIds).list();

        List<Long> departmentIds = teamList.stream().map(Team::getDepartmentId).toList();
        List<Department> departmentList = Db.lambdaQuery(Department.class).in(Department::getId, departmentIds).list();

        List<Long> schoolIds = departmentList.stream().map(Department::getSchoolId).toList();
        List<School> schoolList = Db.lambdaQuery(School.class).in(School::getId, schoolIds).list();

        Map<Long, List<Major>> majorMap = majorList.stream().collect(Collectors.groupingBy(Major::getId));
        Map<Long, List<Department>> departmentMap = departmentList.stream().collect(Collectors.groupingBy(Department::getId));
        Map<Long, List<School>> schoolMap = schoolList.stream().collect(Collectors.groupingBy(School::getId));

        List<Object> list = new ArrayList<>();

        teamList.forEach(team -> {
            Map<String, Object> map = new HashMap<>();
            map.put("teamId", team.getId());
            map.put("teamName", team.getName());
            map.put("schoolId", schoolMap.get(departmentMap.get(team.getDepartmentId()).get(0).getSchoolId()).get(0).getId());
            map.put("schoolName", schoolMap.get(departmentMap.get(team.getDepartmentId()).get(0).getSchoolId()).get(0).getName());
            map.put("schoolExhibition", schoolMap.get(departmentMap.get(team.getDepartmentId()).get(0).getSchoolId()).get(0).getExhibition());
            map.put("departmentId", departmentMap.get(team.getDepartmentId()).get(0).getId());
            map.put("departmentName", departmentMap.get(team.getDepartmentId()).get(0).getName());
            map.put("departmentExhibition", departmentMap.get(team.getDepartmentId()).get(0).getExhibition());
            map.put("majorName", majorMap.get(team.getMajorId()).get(0).getName());
            map.put("endTime", team.getEndTime().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
            list.add(map);
        });

        return list;
    }

    @Override
    public boolean saveTeam(TeamDTO teamDTO) {

        CheckEmptyOrRuleUtil.isManager();
        if (StrUtil.isBlank(teamDTO.getSchoolName()))
            throw new BusinessException(Code.BUSINESS_ERR, "学校名称不能为空");
        Major major = Db.lambdaQuery(Major.class).eq(Major::getName, teamDTO.getMajorName()).one();

        if (major == null) {
            // 学校和院系
            saveSchoolAndDepartment(teamDTO);
        } else {
            // 班级
            Long id = saveSchoolAndDepartment(teamDTO);
            Team team = new Team().setName(teamDTO.getName()).setMajorId(major.getId()).setDepartmentId(id).setEndTime(teamDTO.getEndTime());
            save(team);
        }

        return true;
    }

    @Override
    public boolean updateList(List<TeamDTO> teamDTOS) {

        CheckEmptyOrRuleUtil.isManager();
        try {
            List<School> schoolList = new ArrayList<>();
            List<Department> departmentList = new ArrayList<>();
            List<Team> teamList = new ArrayList<>();
            teamDTOS.forEach(teamDTO -> {
                School school = Db.lambdaQuery(School.class).eq(School::getName, teamDTO.getSchoolName()).one();
                Department department = Db.lambdaQuery(Department.class).in(Department::getSchoolId, school.getId()).eq(Department::getName, teamDTO.getDepartmentName()).one();
                Major major = Db.lambdaQuery(Major.class).eq(Major::getName, teamDTO.getMajorName()).one();
                Team team = Db.lambdaQuery(Team.class).eq(Team::getId, teamDTO.getId()).one();
                team.setDepartmentId(department.getId()).setEndTime(teamDTO.getEndTime()).setName(teamDTO.getName()).setMajorId(major.getId());
                teamList.add(team);
                if (!school.getExhibition().equals(teamDTO.getSchoolExhibition())) schoolList.add(school.setExhibition(teamDTO.getSchoolExhibition()));
                if (!department.getExhibition().equals(teamDTO.getDepartmentExhibition())) departmentList.add(department.setExhibition(teamDTO.getDepartmentExhibition()));
            });
            if (!CheckEmptyOrRuleUtil.isEmptyList(schoolList)) Db.updateBatchById(schoolList);
            if (!CheckEmptyOrRuleUtil.isEmptyList(departmentList)) Db.updateBatchById(departmentList);
            return updateBatchById(teamList);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteList(List<Long> teamIds) {

        CheckEmptyOrRuleUtil.isManager();
        try {
            List<Team> teams = lambdaQuery().in(Team::getId, teamIds).list();
            List<Long> departmentIds = teams.stream().map(Team::getDepartmentId).toList();
            List<Department> departments = Db.lambdaQuery(Department.class).in(Department::getId, departmentIds).list();
            List<Long> schoolIds = departments.stream().map(Department::getSchoolId).toList();
            removeBatchByIds(teams);
            // 院系是否为空
            departmentIds.forEach(id -> {
                Long count = lambdaQuery().eq(Team::getDepartmentId, id).count();
                if (count == 0) Db.removeById(id, Department.class);
            });
            // 学校是否为空
            schoolIds.forEach(id -> {
                Long count = Db.lambdaQuery(Department.class).eq(Department::getSchoolId, id).count();
                if (count == 0) Db.removeById(id, School.class);
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Long saveSchoolAndDepartment(TeamDTO teamDTO) {
        School school = Db.lambdaQuery(School.class).eq(School::getName, teamDTO.getSchoolName()).one();
        if (school == null) {
            // 学校 + 院系
            school = new School().setName(teamDTO.getSchoolName()).setExhibition(true);
            Db.save(school);
            if (StrUtil.isBlank(teamDTO.getDepartmentName()))
                throw new BusinessException(Code.BUSINESS_ERR, "院系名称不能为空");
            Department department = new Department().setSchoolId(school.getId()).setName(teamDTO.getDepartmentName()).setExhibition(true);
            Db.save(department);
            return department.getId();
        } else {
            // 院系
            if (!school.getExhibition()) throw new BusinessException(Code.BUSINESS_ERR, "该学校已被禁用，无法添加院系");
            Department one = Db.lambdaQuery(Department.class).in(Department::getSchoolId, school.getId()).eq(Department::getName, teamDTO.getDepartmentName()).one();
            if (one == null) {
                Department department = new Department().setSchoolId(school.getId()).setName(teamDTO.getDepartmentName()).setExhibition(true);
                Db.save(department);
                return department.getId();
            }
            return one.getId();
        }
    }
}
