package com.example.myusercenterback.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.myusercenterback.common.BaseResponse;
import com.example.myusercenterback.common.ErrorCode;
import com.example.myusercenterback.common.ResultUtils;
import com.example.myusercenterback.exception.BusinessException;
import com.example.myusercenterback.model.domain.Team;
import com.example.myusercenterback.model.domain.User;
import com.example.myusercenterback.model.dto.TeamQuery;
import com.example.myusercenterback.model.request.TeamAddRequest;
import com.example.myusercenterback.model.vo.TeamUserVo;
import com.example.myusercenterback.service.TeamService;
import com.example.myusercenterback.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    public BaseResponse<Long> updateTeam(Team team) {
        if(team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"新增队伍数据为空");
        }
        boolean updateById = teamService.updateById(team);
        if(!updateById){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新队伍数据失败");
        }
        return ResultUtils.success(team.getId());
    }

    //查询list
//    @PostMapping("/list")
//    public BaseResponse<List<Team>> getList(TeamQuery teamQuery){
//        if(teamQuery == null){
//            throw new BusinessException(ErrorCode.NULL_ERROR,"查询条件为空");
//        }
//        Team team = new Team();
//        BeanUtils.copyProperties(teamQuery,team);
//        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<Team>(team);
//        List<Team> resultList = teamService.list(teamQueryWrapper);
//        return ResultUtils.success(resultList);
//    }


    @PostMapping("/list")
    public BaseResponse<List<TeamUserVo>> getList(TeamQuery teamQuery,HttpServletRequest request){
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"查询条件为空");
        }
        boolean admin = userService.isAdmin(request);
        List<TeamUserVo> resultList = teamService.getTeamList(teamQuery, admin);
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
}
