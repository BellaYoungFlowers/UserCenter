package com.example.myusercenterback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.myusercenterback.model.domain.UserTeam;
import com.example.myusercenterback.model.vo.UserVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 用户队伍 Mapper
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public interface UserTeamMapper extends BaseMapper<UserTeam> {

    List<UserVO> getUsersByTeam(@Param("teamId") Long teamId);
}




