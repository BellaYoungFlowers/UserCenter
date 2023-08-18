package com.example.myusercenterback.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.myusercenterback.common.BaseResponse;
import com.example.myusercenterback.common.ErrorCode;
import com.example.myusercenterback.common.ResultUtils;
import com.example.myusercenterback.exception.BusinessException;
import com.example.myusercenterback.model.domain.Team;
import com.example.myusercenterback.model.domain.User;
import com.example.myusercenterback.model.domain.UserTeam;
import com.example.myusercenterback.model.dto.TeamQuery;
import com.example.myusercenterback.model.request.TeamAddRequest;
import com.example.myusercenterback.model.request.TeamJoinRequest;
import com.example.myusercenterback.model.request.TeamQuitRequest;
import com.example.myusercenterback.model.request.TeamUpdateRequest;
import com.example.myusercenterback.model.vo.TeamUserVO;
import com.example.myusercenterback.service.TeamService;
import com.example.myusercenterback.service.UserService;
import com.example.myusercenterback.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author:xxxxx
 * @create: 2023-07-19 14:38
 * @Description: 队伍控制器
 */
@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {
    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;
    @Resource
    private UserTeamService userTeamService;

    //新增
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(TeamAddRequest teamAddRequest, HttpServletRequest httpServletRequest) {
        if(teamAddRequest == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"新增队伍数据为空");
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        long teamId = teamService.addTeam(team, loginUser);
//        boolean save = teamService.save(team);
        return ResultUtils.success(teamId);
    }

    //修改
    @PostMapping("/update")
    public  BaseResponse<Boolean>  updateTeam(TeamUpdateRequest request, HttpServletRequest httpServletRequest) {
        if(request == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"修改队伍数据为空");
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_AUTH,"修改队伍信息必须登录");
        }
        boolean result = teamService.updateTeam(request,loginUser);
        return ResultUtils.success(result);
    }


    //加入队伍
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(TeamJoinRequest request,HttpServletRequest httpServletRequest){
        if(request == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"修改队伍数据为空");
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        boolean result = teamService.joinTeam(request,loginUser);
        return ResultUtils.success(result);
    }

    //退出队伍
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(TeamQuitRequest request, HttpServletRequest httpServletRequest){
        if(request == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"退出队伍数据为空");
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        boolean result = teamService.quitTeam(request,loginUser);
        return ResultUtils.success(result);
    }

    //查询队伍list
    @PostMapping("/list")
    public BaseResponse<List<TeamUserVO>> getList(TeamQuery teamQuery, HttpServletRequest request){
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"查询条件为空");
        }
        boolean admin = userService.isAdmin(request);
        List<TeamUserVO> resultList = teamService.getTeamList(teamQuery, admin);
        return ResultUtils.success(resultList);
    }



    //查询list 分页显示
    @PostMapping("/listByPage")
    public BaseResponse<Page<Team>> getListByPage(TeamQuery teamQuery){
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"查询条件为空");
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery,team);
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<Team>(team);
        Page<Team> resultPage = teamService.page(new Page<>(teamQuery.getPageNum(),teamQuery.getPageSize()), teamQueryWrapper);
        return ResultUtils.success(resultPage);
    }

    /**
     * 获取我创建的队伍
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> listMyCreateTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"查询条件为空");
        }
        if(request == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"查询条件为空");
        }
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Long userId = loginUser.getId();
        if(userId == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        teamQuery.setUserId(userId);
        List<TeamUserVO> teamList = teamService.getTeamList(teamQuery, true);
        return ResultUtils.success(teamList);
    }


    /**
     * 获取我加入的队伍
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"查询条件为空");
        }
        if(request == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"查询条件为空");
        }
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Long userId = loginUser.getId();
        if(userId == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<UserTeam>();
        queryWrapper.eq("userId",userId);

        /**
         * userId teamId
         *  1     2
         *  1     3
         *  1     3
         *
         *  2 =》1
         *  3 =》1,1？
         */

        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        //防止有重的数据 根据队伍分组 获取队伍id
        Map<Long, List<UserTeam>> listMap = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        ArrayList<Long> teamIds = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(teamIds);
        List<TeamUserVO> teamList = teamService.getTeamList(teamQuery, true);
        return ResultUtils.success(teamList);
    }
}
