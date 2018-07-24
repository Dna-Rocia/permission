$(function () {
    var  deptList;  //缓存树形部门列表
    var deptMap = {}; //缓存最新一次加载过的部门信息
    var userMap = {}; //缓存用户列表
    var optionStr = ""; //选项组成的字符串
    var lastClickDeptId = -1; //存储上一次处理的部门id

    var deptListTemplate = $('#deptListTemplate').html();
    Mustache.parse(deptListTemplate); //Mustache 引擎
    var userListTemplate = $('#userListTemplate').html();
    Mustache.parse(userListTemplate); //Mustache 引擎

    loadDeptTree();

    /**
     *
     * 部门操作
     *
     */

    //加载部门树
    function loadDeptTree() {
        $.ajax({
            url:"/sys/dept/tree.json",
            success:function (data) {
                if (data.ret){
                    deptList = data.data;
                    var rendered = Mustache.render(deptListTemplate,{deptList:data.data});//之渲染上层
                    $("#deptList").html(rendered); //存放容器
                    recursiveRenderDept(data.data); //递归渲染
                    bindDeptClick(); //绑定部门的点击操作
                }else {
                    showMessage("加载部门列表",data.msg,false);
                }
            }
        })
    }

    //递归渲染部门树（子项）
    function recursiveRenderDept(deptList) {
        if(deptList && deptList.length > 0) {
            $(deptList).each(function (i, dept) {
                deptMap[dept.id] = dept;

                if (dept != null && dept.deptList.length > 0) {
                    var rendered = Mustache.render(deptListTemplate, {deptList: dept.deptList});
                    $("#dept_" + dept.id).append(rendered);
                    recursiveRenderDept(dept.deptList);
                }
            })
        }
    }

    //部门选择器的点击事件 、进行选染高亮、 刷新当前部门下的用户列表
    function handleDepSelected(deptId) {
        if (lastClickDeptId != -1) {  //不等于-1 表示移除高亮显示
            var lastDept = $("#dept_" + lastClickDeptId + " .dd2-content:first");
            lastDept.removeClass("btn-yellow");
            lastDept.removeClass("no-hover");
        }
        var currentDept = $("#dept_" + deptId + " .dd2-content:first");
        currentDept.addClass("btn-yellow");
        currentDept.addClass("no-hover");
        lastClickDeptId = deptId;
        loadUserList(deptId);
    }

    //递归渲染部门选择器
    function recursiveRenderDeptSelect(deptList,level) {
        level = level | 0;
        if(deptList && deptList.length >0){
            $(deptList).each(function (i, dept) {
                deptMap[dept.id] = dept;
                var blank = "";
                if (level > 1) {
                    for(var j = 3; j <= level; j++) {
                        blank += "..";
                    }
                    blank += "∟";
                }
                optionStr += Mustache.render("<option value='{{id}}'>{{name}}</option>", {id: dept.id, name: blank + dept.name});
                if (dept.deptList && dept.deptList.length > 0) {
                    recursiveRenderDeptSelect(dept.deptList, level + 1);
                }
            });
        }
    }

    //新增部门操作
    $(".dept-add").click(function () {
        $("#dialog-dept-form").dialog({
            model:true,
            title:"新增部门",
            open:function(even,ui){
                $(".ui-dialog-titlebar-close",$(this).parent()).hide();
                optionStr = "<option value=\"0\">-</option>"
                recursiveRenderDeptSelect(deptList,1);
                $("#deptForm")[0].reset();
                $("#parentId").html(optionStr);
            },
            buttons:{
                "添加": function(e) {
                    e.preventDefault();
                    updateDept(true, function (data) {
                        $("#dialog-dept-form").dialog("close");
                    }, function (data) {
                        showMessage("新增部门", data.msg, false);
                    })
                },
                "取消": function () {
                    $("#dialog-dept-form").dialog("close");
                }
            }
        });
    });

    //绑定部门的点击操作 :删除/编辑部门
    function bindDeptClick() {

        $(".dept-name").click(function(e) {
            e.preventDefault();
            e.stopPropagation();
            var deptId = $(this).attr("data-id");
            handleDepSelected(deptId);
        });

        /**
         * 删除部门
         **/

        $(".dept-delete").click(function (e) {
            e.preventDefault();
            e.stopPropagation();
            var deptId = $(this).attr("data-id");
            var deptName = $(this).attr("data-name");
            if (confirm("确定要删除部门[" + deptName + "]吗?")) {
                $.ajax({
                     url: "/sys/dept/delete.json",
                     data: {
                         id: deptId
                     },
                    type:'post',
                     success: function (result) {
                         if (result.ret) {
                             showMessage("删除部门[" + deptName + "]", "操作成功", true);
                             loadDeptTree();
                         } else {
                             showMessage("删除部门[" + deptName + "]", result.msg, false);
                         }
                     }
                 });
            }
        });

        /**
         * 部门编辑
         * 拿到当前部门的ID，来渲染实际中那个部门的属性
         **/
        $(".dept-edit").click(function(e) {
            e.preventDefault(); //拦截默认的点击事件，点击之后的操作全部是我们自己处理
            e.stopPropagation(); //不允许它执行冒泡
            var deptId = $(this).attr("data-id");
            $("#dialog-dept-form").dialog({
                model: true,
                title: "编辑部门",
                open: function(event, ui) {
                    $(".ui-dialog-titlebar-close", $(this).parent()).hide();
                    optionStr = "<option value=\"0\">-</option>";
                    recursiveRenderDeptSelect(deptList, 1);
                    $("#deptForm")[0].reset();
                    $("#parentId").html(optionStr);
                    $("#deptId").val(deptId);
                    var targetDept = deptMap[deptId];
                    if (targetDept) {
                        $("#parentId").val(targetDept.parentId);
                        $("#deptName").val(targetDept.name);
                        $("#deptSeq").val(targetDept.seq);
                        $("#deptRemark").val(targetDept.remark);
                    }
                },
                buttons : {
                    "更新": function(e) {
                        e.preventDefault();
                        updateDept(false, function (data) {
                            $("#dialog-dept-form").dialog("close");
                        }, function (data) {
                            showMessage("更新部门", data.msg, false);
                        })
                    },
                    "取消": function () {
                        $("#dialog-dept-form").dialog("close");
                    }
                }
            });
        })
    }

    //save、update 提交路径整合
    function updateDept(isCreate, successCallback, failCallback) {
        $.ajax({
            url: isCreate ? "/sys/dept/save.json" : "/sys/dept/update.json",
            data: $("#deptForm").serializeArray(), //serializeArray自动组装成我们所需要的格式
            type: 'POST',
            success: function(result) {
                if (result.ret) {
                    loadDeptTree();
                    if (successCallback) {
                        successCallback(result);
                    }
                } else {
                    if (failCallback) {
                        failCallback(result);
                    }
                }
            }
        })
    }



    /**
     *
     * 用户操作
     *
     */

    //加载用户列表
    function loadUserList(deptId) {
        var pageSize = $("#pageSize").val();
        var url = "/sys/user/page.json?deptId="+deptId;
        var pageNo = $("#userPage .pageNo").val() || 1;
        $.ajax({
            url:url,
            data:{
                pageSize:pageSize,
                pageNo:pageNo
            },
            success:function(result){
                renderUserListAndPage(result, url);
            }
        })
    }

    //渲染用户列表以及分页信息
    function renderUserListAndPage(result,url) {
        if (result.ret){
            if (result.data.total > 0){
                var rendered = Mustache.render(userListTemplate,{
                    userList:result.data.data,
                    "showDeptName":function () {
                        return deptMap[this.deptId].name;
                    } ,
                    "showStatus":function () {
                        return this.status == 1 ? '有效' : (this.status == 0 ? '无效' : '删除');
                    },
                    "bold": function () {
                        return function (text,render) {
                            var status = render(text);
                            if (status == '有效'){
                                return "<span class='label label-sm label-success'>有效</span>";
                            }else if (status == '无效'){
                                return "<span class='label label-sm label-warning'>无效</span>";
                            }else {
                                return "<span class='label'>删除</span>";
                            }
                        }
                    }
                });
                $("#userList").html(rendered);
                bindUserClick();
                $.each(result.data.data,function (i,user) {
                    userMap[user.id] = user;
                })
            }else {
                $("#userList").html('');
            }
            var pageSize = $("#pageSize").val();
            var pageNo = $("#userPage .pageNo").val() || 1;
            renderPage(url, result.data.total, pageNo, pageSize, result.data.total > 0 ? result.data.data.length : 0, "userPage", renderUserListAndPage);
        }else {
            showMessage("获取部门下用户列表", result.msg, false);
        }
    }

    //编辑用户
    function bindUserClick() {
        $(".user-acl").click(function (e) {
            e.preventDefault(); //拦截默认的点击事件，点击之后的操作全部是我们自己处理
            e.stopPropagation(); //不允许它执行冒泡
            var userId = $(this).attr("data-id");
            $.ajax({
                url: "/sys/user/acls.json",
                data: {
                    userId:userId
                },
                type:'post',
                success: function(result) {
                    if (result.ret) {
                        console.log(result);
                    } else {
                        showMessage("获取用户权限数据", result.msg, false);
                    }
                }
            });
        });

        $(".user-edit").click(function (e) {
            e.preventDefault(); //拦截默认的点击事件，点击之后的操作全部是我们自己处理
            e.stopPropagation(); //不允许它执行冒泡
            var userId = $(this).attr("data-id");
            $("#dialog-user-form").dialog({
                model: true,
                title: "编辑用户",
                open: function(event, ui) {
                    $(".ui-dialog-titlebar-close", $(this).parent()).hide();
                    optionStr = "";
                    recursiveRenderDeptSelect(deptList, 1);
                    $("#userForm")[0].reset();
                    $("#deptSelectId").html(optionStr);
                    var targetUser = userMap[userId];
                    if (targetUser) {
                        $("#deptSelectId").val(targetUser.deptId);
                        $("#userName").val(targetUser.username);
                        $("#userMail").val(targetUser.mail);
                        $("#userTelephone").val(targetUser.telephone);
                        $("#userStatus").val(targetUser.status);
                        $("#userRemark").val(targetUser.remark);
                        $("#userId").val(targetUser.id);
                    }
                },
                buttons : {
                    "更新": function(e) {
                        e.preventDefault();
                        updateUser(false, function (data) {
                            $("#dialog-user-form").dialog("close");
                            loadUserList(lastClickDeptId);
                        }, function (data) {
                            showMessage("更新用户", data.msg, false);
                        })
                    },
                    "取消": function () {
                        $("#dialog-user-form").dialog("close");
                    }
                }
            });
        })
    }

    //新增用户操作
    $(".user-add").click(function () {
        $("#dialog-user-form").dialog({
            model:true,
            title:"新增用户",
            open:function(even,ui){
                $(".ui-dialog-titlebar-close",$(this).parent()).hide();
                optionStr = ""
                recursiveRenderDeptSelect(deptList,1);
                $("#userForm")[0].reset();
                $("#deptSelectId").html(optionStr);
            },
            buttons:{
                "添加": function(e) {
                    e.preventDefault();
                    updateUser(true, function (data) {
                        $("#dialog-user-form").dialog("close");
                        loadUserList(lastClickDeptId);
                    }, function (data) {
                        showMessage("新增用户", data.msg, false);
                    })
                },
                "取消": function () {
                    $("#dialog-user-form").dialog("close");
                }
            }
        });
    });

    //用户请求 save、update 提交路径整合
    function updateUser(isCreate, successCallback, failCallback) {
        $.ajax({
            url: isCreate ? "/sys/user/save.json" : "/sys/user/update.json",
            data: $("#userForm").serializeArray(), //serializeArray自动组装成我们所需要的格式
            type: 'POST',
            success: function(result) {
                if (result.ret) {
                    loadDeptTree();
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




});