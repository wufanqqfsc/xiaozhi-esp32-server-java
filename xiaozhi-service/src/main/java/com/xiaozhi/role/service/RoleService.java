package com.xiaozhi.role.service;

import com.xiaozhi.common.model.bo.RoleBO;
import com.xiaozhi.common.model.resp.PageResp;
import com.xiaozhi.common.model.resp.RoleResp;

import java.util.List;

public interface RoleService {

    /** 角色缓存名称（RoleServiceImpl 读缓存、RoleRepositoryImpl 写后失效均使用此常量） */
    String CACHE_NAME = "XiaoZhi:Role";

    // ===================== 查询操作 =====================

    PageResp<RoleResp> page(int pageNo, int pageSize, Integer roleId, String roleName,
                            String isDefault, String state, Integer userId);

    RoleBO getBO(Integer roleId);

    List<RoleBO> listBO(Integer userId, int limit);

    RoleBO getDefaultOrFirstBO(Integer userId);

    // ===================== 写操作（待迁移到 RoleAppService） =====================

    Integer copyDefaultRole(Integer sourceUserId, Integer targetUserId);

    /**
     * 热更新角色配置：清除角色缓存，使新配置在下次请求时生效。
     * 注意：已建立会话的设备/Web聊天需要重新连接才能加载新配置。
     *
     * @param roleId 角色ID
     */
    void hotReloadRole(Integer roleId);
}
