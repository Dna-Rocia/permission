$(function () {
    var roleMap = {};
    var lastRoleId = -1;
    //当前的关系维护：角色与用户关系，角色与权限关系
    var selectFirstTab = true;
    //是否有多选的情况
    var hasMultiSelect = false;

    var roleListTemplate = $("#roleListTemplate").html();
    Mustache.parse(roleListTemplate);

    loadRoleList();

    /*ztree 树结构相关 开始*/
    var zTreeObj = [];
    var modulePrefix = 'm_'; //权限模块
    var aclPrefix = 'a_'; //权限点
    var nodeMap = {}; //所有节点数据

    /**
     * ztree 的配置
     */
    var setting = {
        check:{
            enable:true,
            chkDisabledInherit:true,
            chkboxType:{"Y":"ps","N":"ps"},
            autoCheckTrigger:true
        },
        data:{
            simpleData:{
                enable:true,
                rootPId:0
            }

        },
        callback:{
            onclick:onClickTreeNode //绑定单击事件
        }
    };

    //点击之后不停展开叠起
    function onClickTreeNode(e,treeId,treeNode) {
        var zTree = $.fn.ztree.getZTreeObj("roleAclTree");
        zTree.expandNode(treeNode);
    }


    /**
     * 角色列表
     */
    function loadRoleList() {
        $.ajax({
            url: "/sys/role/list.json",
            success: function (result) {
                if (result.ret) {
                    var render = Mustache.render(roleListTemplate, {roleList: result.data});
                    $("#roleList").html(render);
                    //绑定点击事件
                    bindRoleClick();

                    $.each(result.data, function (i, role) {
                        roleMap[role.id] = role;  //全局的角色
                    });
                } else {
                    showMessage("加载角色列表", result.msg, false);
                }
            }
        });
    }

    /**
     * 修改角色
     */
    function bindRoleClick() {
        $(".role-edit").click(function (e) {
            e.preventDefault();//拦截默认操作
            e.stopPropagation();//拦截冒泡事件
            var roleId = $(this).attr("data-id");

            $("#dialog-role-form").dialog({
                model: true,
                title: "修改角色",
                open: function (event, ui) {
                    $(".ui-dialog-titlebar-close", $(this).parent()).hide();
                    $("#roleForm")[0].reset();
                    var targetRole = roleMap[roleId];
                    if (targetRole) {
                        $("#roleId").val(roleId);
                        $("#roleName").val(targetRole.name);
                        $("#roleStatus").val(targetRole.status);
                        $("#roleRemark").val(targetRole.remark);
                    }
                },
                buttons: {
                    "修改": function (e) {
                        e.preventDefault();
                        updateRole(
                            false,
                            function (data) {
                                $("#dialog-role-form").dialog("close");
                            },
                            function (data) {
                                showMessage("修改角色", data.msg, false);
                            })
                    },
                    "取消": function () {
                        $("#dialog-role-form").dialog("close");
                    }
                }
            });
        });

        $(".role-name").click(function (e) {
            e.preventDefault();
            e.stopPropagation();
            var roleId = $(this).attr("data-id");
            handleRoleSelected(roleId);
        })


    }

    /**
     * 角色的选择器样式设置
     */
    function handleRoleSelected(roleId) {
        if (lastRoleId != -1) {
            var lastRole = $("#role_" + lastRoleId + " .dd2-content:first");
            lastRole.removeClass("btn-yellow");
            lastRole.removeClass("no-hover");
        }
        var currentRole = $("#role_" + roleId + " .dd2-content:first");
        currentRole.addClass("btn-yellow");
        currentRole.addClass("no-hover");
        lastRoleId = roleId;
        $('#roleTab a:first').trigger('click');
        if (selectFirstTab){
            loadRoleAcl(roleId);
        }
    }

    /**
     * 新增角色
     */
    $(".role-add").click(function () {
        $("#dialog-role-form").dialog({
            model: true,
            title: "新增角色",
            open: function (event, ui) {
                $(".ui-dialog-titlebar-close", $(this).parent()).hide();
                $("#roleForm")[0].reset();
            },
            buttons: {
                "添加": function (e) {
                    e.preventDefault();
                    updateRole(true, function (data) {
                        $("#dialog-role-form").dialog("close");
                    }, function (data) {
                        showMessage("新增角色", data.msg, false);
                    })
                },
                "取消": function () {
                    $("#dialog-role-form").dialog("close");
                }
            }
        });
    });


    /**
     * 角色的新增/编辑 最后发起的请求
     * @param isCreate true：新增 ， false：更新
     * @param successCallback  成功回调
     * @param failCallback  失败回调
     */
    function updateRole(isCreate, successCallback, failCallback) {
        $.ajax({
            url: isCreate ? "/sys/role/save.json" : "/sys/role/update.json",
            data: $("#roleForm").serializeArray(), //serializeArray自动组装成我们所需要的格式
            type: 'POST',
            success: function (result) {
                if (result.ret) {
                    loadRoleList();
                    if (successCallback) {
                        successCallback(result);
                    }
                } else {
                    if (failCallback) {
                        failCallback(result);
                    }
                }
            }
        });
    }



    /**
     * 加载第一个tab (角色与权限)
     */
    function loadRoleAcl(selectRoleId){
        if(selectRoleId == -1){  //一个都没有被选中，则不予处理
            return;
        }
        $.ajax({
            url:"/sys/role/roleTree.json",
            data:{
                roleId : selectRoleId
            },
            type:'post',
            success:function (result) {
                if (result.ret){
                    renderRoleTree(result.data);
                }else {
                    showMessage("加载角色权限数据",result.msg,false);
                }
            }
        });
    }

    /**
     * 获取选中的权限点id
     * @returns {string}
     */
    function getTreeSelectedId() {
        var treeObj = $.fn.zTree.getZTreeObj("roleAclTree");
        var nodes = treeObj.getCheckedNodes(true);
        var v = "";
        for (var i = 0; i < nodes.length; i++){
            if (nodes[i].id.startsWith(aclPrefix)){
                v += ","+nodes[i].dataId;
            }
        }
        return v.length > 0 ? v.substring(1) : v;
    }


    /**
     * 渲染角色的权限树
     * @param aclModuleList
     */
    function renderRoleTree(aclModuleList){
        zTreeObj = []; //把权限点放置到zTreeObj中
        recursivePrepareTreeData(aclModuleList);
        for (var key in nodeMap){ //把权限模块的信息放到nodeMap
            zTreeObj.push(nodeMap[key]);
        }
        $.fn.zTree.init($("#roleAclTree"),setting,zTreeObj);
    }


    /**
     * 递归渲染权限模块树列表  它下边有哪些权限
     * @param aclModuleList 权限模块列表
     */
    function recursivePrepareTreeData(aclModuleList){
        //prepare nodeMap 准备数据
        if (aclModuleList && aclModuleList.length > 0){
            $(aclModuleList).each(function (i,aclModule) {
                var hasChecked = false;
                if (aclModule.aclList && aclModule.aclList.length >0 ){
                    $(aclModule.aclList).each(function (i,acl) {
                        zTreeObj.push({
                            id: aclPrefix+acl.id,
                            pId:modulePrefix+acl.aclModuleId,
                            name:acl.name + ((acl.type == 1) ? '（菜单）':''),
                            chkDisabled:!acl.hasAcl,
                            checked:acl.checked,
                            dataId:acl.id
                        });
                        //判断默认的是否被选中
                        if (acl.checked) hasChecked = true;
                    });
                }
                //判断当前权限模块下是不是有子模块
                if ((aclModule.aclModuleList && aclModule.aclModuleList.length > 0) ||
                    //是不是有权限点
                    (aclModule.aclList && aclModule.aclList.length > 0)){
                    nodeMap[modulePrefix+aclModule.id] = {
                        id:modulePrefix+aclModule.id,
                        pId:modulePrefix+aclModule.parentId,
                        name:aclModule.name,
                        open:hasChecked
                    };
                    //当下一层权限checked =true ,递归是的上一层的checked也为true
                    var tempAclModule = nodeMap[modulePrefix +aclModule.id];
                    while(hasChecked && tempAclModule){
                        //因为tempAclModule值一直在变
                        if (tempAclModule){
                            nodeMap[tempAclModule.id] = {
                                id:tempAclModule.id,
                                pId:tempAclModule.pId,
                                name:tempAclModule.name,
                                open:true
                            };
                        }
                        //返回上一层
                        tempAclModule = nodeMap[tempAclModule.pId];
                    }
                }
                recursivePrepareTreeData(aclModule.aclModuleList);
            })
        }
    }


    /**
     * 给角色进行勾选授权的保存操作
     */
    $(".saveRoleAcl").click(function (e) {
        e.preventDefault();
        if (lastRoleId == -1){
            showMessage("保存角色与权限点的关系","请现在左侧选择需要操作的角色",false);
            return;
        }
        $.ajax({
            url:"/sys/role/changeAcls.json",
            data:{
                roleId:lastRoleId,
                aclIds: getTreeSelectedId()
            },
            type:'post',
            success:function (result) {
                if (result.ret){
                    showMessage("保存角色与权限点的关系","操作成功",false);
                }else {
                    showMessage("保存角色与权限点的关系",result.msg,false);
                }
            }
        });
    });


});