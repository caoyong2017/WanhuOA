package com.hc.activiti.controller;

import com.github.pagehelper.Page;
import com.hc.common.core.controller.BaseController;
import com.hc.common.core.page.PageDomain;
import com.hc.common.core.page.TableDataInfo;
import com.hc.common.core.page.TableSupport;
import com.hc.activiti.domain.ActIdUser;
import com.hc.common.utils.StringUtils;
import com.hc.system.domain.SysUser;
import com.hc.system.service.ISysUserService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程用户Controller
 *
 * @author Xianlu Tech
 * @date 2019-10-02
 */
@Controller
@RequestMapping("/user")
public class ActIdUserController extends BaseController {
    private String prefix = "user";

    @Autowired
    private IdentityService identityService;
    @Autowired
    private ISysUserService userService;

    @GetMapping()
    public String user()
    {
        return prefix + "/user";
    }

    /**
     * 查询流程用户列表
     */
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(ActIdUser query)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();

        UserQuery userQuery = identityService.createUserQuery();
        if (StringUtils.isNotBlank(query.getId())) {
            userQuery.userId(query.getId());
        }
        if (StringUtils.isNotBlank(query.getFirst())) {
            userQuery.userFirstNameLike("%" + query.getFirst() + "%");
        }
        if (StringUtils.isNotBlank(query.getEmail())) {
            userQuery.userEmailLike("%" + query.getEmail() + "%");
        }
        List<User> userList = userQuery.listPage((pageNum - 1) * pageSize, pageSize);
        Page<ActIdUser> list = new Page<>();
        list.setTotal(userQuery.count());
        list.setPageNum(pageNum);
        list.setPageSize(pageSize);
        for (User user: userList) {
            ActIdUser idUser = new ActIdUser();
            idUser.setId(user.getId());
            idUser.setFirst(user.getFirstName());
            idUser.setEmail(user.getEmail());
            list.add(idUser);
        }
        return getDataTable(list);
    }

    /**
     * 选择系统用户
     */
    @GetMapping("/authUser/selectUser")
    public String selectUser(String taskId, ModelMap mmap) {
        mmap.put("taskId", taskId);
        return prefix + "/selectUser";
    }

    @PostMapping("/systemUserList")
    @ResponseBody
    public TableDataInfo systemUserList(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

}
