package net.zjitc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.zjitc.dto.TeamDTO;
import net.zjitc.entity.Team;
import net.zjitc.query.TeamQuery;
import net.zjitc.vo.StudentVO;
import net.zjitc.vo.TeamVO;

import java.util.List;
import java.util.Map;

public interface TeamService extends IService<Team> {

    List<TeamVO> findByCourseId(Long courseId);

    List<StudentVO> findById(Long id);

    List<TeamVO> findByTeacherId();

    List<TeamVO> findByTeamQuery(TeamQuery teamQuery);

    List<Object> findAll();

    boolean saveTeam(TeamDTO teamDTO);

    boolean updateList(List<TeamDTO> teamDTOS);

    boolean deleteList(List<Long> teamIds);
}
