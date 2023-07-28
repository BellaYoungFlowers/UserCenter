package com.example.myusercenterback.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.myusercenterback.model.domain.Team;
import com.example.myusercenterback.model.domain.User;
import com.example.myusercenterback.model.dto.TeamQuery;
import com.example.myusercenterback.model.vo.TeamUserVo;


import java.util.List;

/**
 * 队伍服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public interface TeamService extends IService<Team> {
    public long addTeam(Team team,User loginUser);


    List<TeamUserVo> getTeamList(TeamQuery teamQuery, boolean admin);
}
