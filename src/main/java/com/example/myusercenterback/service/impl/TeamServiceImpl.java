package com.example.myusercenterback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.myusercenterback.common.ErrorCode;
import com.example.myusercenterback.exception.BusinessException;
import com.example.myusercenterback.mapper.TeamMapper;
import com.example.myusercenterback.mapper.UserTeamMapper;
import com.example.myusercenterback.model.domain.Team;
import com.example.myusercenterback.model.domain.User;
import com.example.myusercenterback.model.domain.UserTeam;
import com.example.myusercenterback.model.dto.TeamQuery;
import com.example.myusercenterback.model.enums.TeamStatusEnum;
import com.example.myusercenterback.model.vo.TeamUserVo;
import com.example.myusercenterback.model.vo.UserVO;
import com.example.myusercenterback.service.TeamService;
import com.example.myusercenterback.service.UserService;
import com.example.myusercenterback.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 队伍服务实现类
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserTeamMapper userTeamMapper;

    @Resource
    private UserService userService;


    @Override
    @Transactional(rollbackFor = Exception.class)//新增队伍和新增队伍和用户的对应关系必须同步
    public long addTeam(Team team, User loginUser) {
        /**
         * 1. 请求参数是否为空？
         * 2. 是否登录，未登录不允许创建
         * 3. 校验信息
         *    1. 队伍人数 > 1 且 <= 20
         *    2. 队伍标题 <= 20
         *    3. 描述 <= 512
         *    4. status 是否公开（int）不传默认为 0（公开）
         *    5. 如果 status 是加密状态，一定要有密码，且密码 <= 32
         *    6. 超时时间 > 当前时间
         *    7. 校验用户最多创建 5 个队伍
         * 4. 插入队伍信息到队伍表
         * 5. 插入用户  => 队伍关系到关系表
         */


        if(team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"新增队伍数据为空");
        }
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        Long userId = loginUser.getId();
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if(maxNum<1 || maxNum>20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数错误");
        }
        String name = team.getName();
        if(StringUtils.isBlank(name) || name.length()>20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍名称错误");
        }
        String description = team.getDescription();
        if(StringUtils.isNoneBlank(description) && description.length()>512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述错误");
        }
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getTeamStatusEnumByValue(status);
        if(statusEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态错误");
        }
        String password = team.getPassword();
        if(TeamStatusEnum.Secret.equals(statusEnum)){
            if(StringUtils.isBlank(password) || password.length()>32){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍密码设置错误");
            }
        }
        Date expireTime = team.getExpireTime();
        if(new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍过期时间设置错误");
        }
        // 7. 校验用户最多创建 5 个队伍
        // todo 有 bug，可能同时创建 100 个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        long count = this.count(queryWrapper);
        if(count>5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"该账号拥有队伍数量过多");
        }
        // 8. 插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean saveTeam = this.save(team);
        if(!saveTeam){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"新建队伍失败");
        }
        // 9. 插入用户  => 队伍关系到关系表
        Long teamId = team.getId();
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        boolean saveUserTeam = userTeamService.save(userTeam);
        if(!saveUserTeam){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"新建队伍失败");
        }
        return teamId;
    }

    @Override
    public List<TeamUserVo> getTeamList(TeamQuery teamQuery, boolean admin) {
        //可以根据上下文搜索
        String description = teamQuery.getDescription();
        String name = teamQuery.getName();
        String searchText = teamQuery.getSearchText();
        QueryWrapper<Team> query = new QueryWrapper<Team>();
        query.eq("name", name);
        query.eq("description", description);
        query.and(qw->qw.like("name",searchText).or().like("description",searchText));


        Integer status = teamQuery.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getTeamStatusEnumByValue(status);
        //如果没有状态查询条件 则查询公开的队伍
        if (statusEnum == null){
            statusEnum = TeamStatusEnum.Public;
        }
        //只有管理员可以查看私密的队伍
        if(!admin && statusEnum == TeamStatusEnum.Private){
            throw new BusinessException(ErrorCode.NOT_AUTH);
        }
        query.eq("status", statusEnum.getValue());

        //不展示过期的队伍
        query.and(qw->qw.gt("expireTime",new Date()).or().isNull("expireTime"));

        List<Team> teamList = this.list(query);
        if(CollectionUtils.isEmpty(teamList)){
            return new ArrayList<>();
        }
        List<TeamUserVo> teamUserVoList = new ArrayList<>();
        for (Team team : teamList) {
            TeamUserVo teamUserVo = new TeamUserVo();

            Long userId = team.getUserId();
            if(userId == null){
                continue;
            }
            //队长信息
            User user = userService.getById(userId);
            if(user != null){
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user,userVO);
                teamUserVo.setCreateUser(userVO);
            }

            //队伍信息
            BeanUtils.copyProperties(team, teamUserVo);

            //所有队员信息
            Long teamId = team.getId();
            //关联查询加入队伍的用户信息
            List<UserVO> userVOList = userTeamMapper.getUsersByTeam(teamId);
            teamUserVo.setAllUsers(userVOList);

            teamUserVoList.add(teamUserVo);

        }
        return teamUserVoList;
    }
}




