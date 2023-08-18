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
import com.example.myusercenterback.model.request.TeamJoinRequest;
import com.example.myusercenterback.model.request.TeamQuitRequest;
import com.example.myusercenterback.model.request.TeamUpdateRequest;
import com.example.myusercenterback.model.vo.TeamUserVO;
import com.example.myusercenterback.model.vo.UserVO;
import com.example.myusercenterback.service.TeamService;
import com.example.myusercenterback.service.UserService;
import com.example.myusercenterback.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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


        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "新增队伍数据为空");
        }
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        Long userId = loginUser.getId();
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数错误");
        }
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名称错误");
        }
        String description = team.getDescription();
        if (StringUtils.isNoneBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述错误");
        }
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getTeamStatusEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态错误");
        }
        String password = team.getPassword();
        if (TeamStatusEnum.Secret.equals(statusEnum)) {
            if (StringUtils.isBlank(password) || password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍密码设置错误");
            }
        }
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍过期时间设置错误");
        }
        // 7. 校验用户最多创建 5 个队伍
        // todo 有 bug，可能同时创建 100 个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long count = this.count(queryWrapper);
        if (count > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该账号拥有队伍数量过多");
        }
        // 8. 插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean saveTeam = this.save(team);
        if (!saveTeam) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新建队伍失败");
        }
        // 9. 插入用户  => 队伍关系到关系表
        Long teamId = team.getId();
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        boolean saveUserTeam = userTeamService.save(userTeam);
        if (!saveUserTeam) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新建队伍失败");
        }
        return teamId;
    }

    @Override
    public List<TeamUserVO> getTeamList(TeamQuery teamQuery, boolean admin) {
        //可以根据上下文搜索
        String description = teamQuery.getDescription();
        String name = teamQuery.getName();
        String searchText = teamQuery.getSearchText();
        List<Long> idList = teamQuery.getIdList();
        QueryWrapper<Team> query = new QueryWrapper<Team>();
        query.eq("name", name);
        query.eq("description", description);
        query.and(qw -> qw.like("name", searchText).or().like("description", searchText));

        //根据ids查询
        query.in("id", idList);

        Integer status = teamQuery.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getTeamStatusEnumByValue(status);
        //如果没有状态查询条件 则查询公开的队伍
        if (statusEnum == null) {
            statusEnum = TeamStatusEnum.Public;
        }
        //只有管理员可以查看私密的队伍
        if (!admin && statusEnum == TeamStatusEnum.Private) {
            throw new BusinessException(ErrorCode.NOT_AUTH);
        }
        Integer val = statusEnum.getValue();
        query.eq("status", val);

        //不展示过期的队伍
        query.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));

        List<Team> teamList = this.list(query);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        for (Team team : teamList) {
            TeamUserVO teamUserVo = new TeamUserVO();
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            //脱敏队长信息
            User user = userService.getById(userId);
            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVo.setCreateUser(userVO);
            }
            //队伍信息
            BeanUtils.copyProperties(team, teamUserVo);

            //所有队员信息
            Long teamId = team.getId();
            //关联查询加入队伍的用户信息
            List<UserVO> userVOList = userTeamMapper.getUsersByTeam(teamId);
            teamUserVo.setAllUsers(userVOList);

            teamUserVOList.add(teamUserVo);
        }
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest request, User loginUser) {
        Long requestId = request.getId();
        if (requestId == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍id为空");
        }
        Team oldTeam = this.getById(requestId);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "原队伍不存在");
        }
        // 只有管理员或者队伍的创建者可以修改
        if (!userService.isAdmin(loginUser) && (oldTeam.getUserId() != loginUser.getId())) {
            throw new BusinessException(ErrorCode.NOT_AUTH, " 只有管理员或者队伍的创建者可以修改");
        }


        //如果修改为加密状态 则必须要有密码
        TeamStatusEnum statusEnum = TeamStatusEnum.getTeamStatusEnumByValue(request.getStatus());
        if (statusEnum.equals(TeamStatusEnum.Private)) {
            if (StringUtils.isBlank(request.getPassword())) {
                throw new BusinessException(ErrorCode.NOT_AUTH, "如果修改为加密状态 则必须要有密码");
            }
        }

        Team newTeam = new Team();
        BeanUtils.copyProperties(oldTeam, newTeam);
        return this.updateById(newTeam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest request, User loginUser) {
        Long userId = loginUser.getId();
        if (userId == null || userId <= 0) {//防止缓存穿透
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long teamId = request.getTeamId();
        if (teamId == null || teamId <= 0) {//防止缓存穿透
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);//校验队伍是否存在
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        QueryWrapper<UserTeam> teamquery = new QueryWrapper<>();
        teamquery.eq("teamId", teamId);
        teamquery.eq("userId", userId);
        long count = userTeamService.count(teamquery);
        if (count == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不在该队伍中");
        }

        QueryWrapper<Team> query = new QueryWrapper<>();
        query.eq("teamId", teamId);
        long hasJoinedUsersCount = this.count(query);//加入该队伍的人数
        if (hasJoinedUsersCount == 1) {
            this.removeById(teamId);//只剩一人，队伍解散
        } else {
            if (team.getUserId() == userId) {//如果是队长退出队伍
                QueryWrapper<UserTeam> teamquery1 = new QueryWrapper<>();
                teamquery1.eq("teamId", teamId);
                teamquery1.eq("userId", userId);
                teamquery1.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(teamquery1);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() < 2) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR);
                }
                UserTeam userTeam = userTeamList.get(1);
                Long newLeaderId = userTeam.getUserId();
                team.setUserId(newLeaderId);
                boolean updateById = this.updateById(team);//关系表中获取新队长的id并更新到队伍表
                if (!updateById) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
            }

        }
        return userTeamService.remove(teamquery);
    }

    @Override
    public boolean joinTeam(TeamJoinRequest request, User loginUser) {
        Long userId = loginUser.getId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "登录用户id为空");
        }

        QueryWrapper<UserTeam> userquery = new QueryWrapper<UserTeam>();
        userquery.eq("userId", userId);
        long count = userTeamService.count(userquery);
        if (count >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多加入五个队伍");
        }

        Long teamId = request.getTeamId();
        if (teamId == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍id为空");
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }

        QueryWrapper<UserTeam> teamquery = new QueryWrapper<>();
        teamquery.eq("teamId", teamId);
        teamquery.eq("userId", userId);
        long hasJoinedUsersCount = userTeamService.count(teamquery);
        if (hasJoinedUsersCount > 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已经加入该队伍");
        }

        //QueryWrapper<UserTeam> 第二种写法
//        UserTeam queryUserTeam = new UserTeam();
//        queryUserTeam.setTeamId(teamId);
//        queryUserTeam.setUserId(userId);
//        QueryWrapper<UserTeam> teamquery = new QueryWrapper<>(queryUserTeam);
//        long hasJoinedUsersCount = userTeamService.count(teamquery);
//        if(hasJoinedUsersCount > 1){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户已经加入该队伍");
//        }

        teamquery = new QueryWrapper<>();
        teamquery.eq("teamId", teamId);
        long teamcount = userTeamService.count(userquery);
        if (team.getMaxNum() <= teamcount) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满");
        }

        if (team.getExpireTime() != null && team.getExpireTime().before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已经过期");
        }
        Integer teamStatus = team.getStatus();
        if (teamStatus == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "要加入的队伍没有状态");
        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getTeamStatusEnumByValue(teamStatus);
        if (TeamStatusEnum.Private.equals(statusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私有的队伍");
        }
        if (TeamStatusEnum.Secret.equals(statusEnum)) {
            String teamPassword = request.getPassword();
            if (StringUtils.isBlank(teamPassword)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加入加密队伍密码为空");
            }
            if (!teamPassword.equals(team.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加入加密队伍密码错误");
            }
        }

        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());

        return userTeamService.save(userTeam);
    }
}




