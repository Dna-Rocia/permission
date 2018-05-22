$(function () {
    var  aclModuleList;  //缓存树形权限模块列表
    var aclModuleMap = {}; //缓存最新一次加载过的权限模块信息
    var aclMap = {}; //缓存实际权限点列表
    var optionStr = ""; //选项组成的字符串
    var lastClickAclModuleId = -1; //存储上一次处理的权限模块id

    var aclModuleListTemplate = $('#aclModuleListTemplate').html();
    Mustache.parse(aclModuleListTemplate); //Mustache 引擎

    var aclListTemplate = $('#aclListTemplate').html();
    Mustache.parse(aclListTemplate); //Mustache 引擎


    /**
     * 权限模块的操作
     */

    loadAclModuleTree();


    //加载权限模块树
    function loadAclModuleTree() {
        $.ajax({
            url:"/sys/aclModule/tree.json",
            success:function (data) {
                if (data.ret){
                    aclModuleList = data.data;
                    // 渲染并判断是否显示向下箭头
                    var rendered = Mustache.render(aclModuleListTemplate,
                        {aclModuleList:data.data,
                            "showDownAngle": function() {
                                return function(text,render){
                                    //this.aclModule:子模块
                                    return (this.aclModuleList && this.aclModuleList.length > 0 ? "" : "hidden");
                                }
                            },
                            "displayClass": function () {
                                return "";
                            }
                        }
                    );

                    $("#aclModuleList").html(rendered); //存放容器
                    recursiveRenderAclModule(data.data); //递归渲染
                    bindAclModuleClick(); //绑定权限模块的点击操作
                }else {
                    showMessage("加载权限模块",data.msg,false);
                }
            }
        })
    }


    //递归渲染权限模块
    function recursiveRenderAclModule(aclModuleList) {
        if(aclModuleList && aclModuleList.length > 0) {
            $(aclModuleList).each(function (i, aclModule) {
                aclModuleMap[aclModule.id] = aclModule;

                if (aclModule.aclModuleList && aclModule.aclModuleList.length > 0) {
                    // 渲染并判断是否显示向下箭头
                    var rendered = Mustache.render(aclModuleListTemplate,
                        {aclModuleList:aclModule.aclModuleList,
                            "showDownAngle": function() {
                                return function(text,render){
                                    //this.aclModule:子模块
                                    return (this.aclModuleList && this.aclModuleList.length > 0 ? "" : "hidden");
                                }
                            },
                            "displayClass": function () {
                                return "hidden";
                            }
                        }
                    );
                    $("#aclModule_"+aclModule.id).append(rendered); //存放容器
                    recursiveRenderAclModule(aclModule.aclModuleList);
                }
            })
        }
    }


    //绑定权限模块的点击操作
    function bindAclModuleClick() {
        $(".sub-aclModule").click(function (e) {
            e.preventDefault();
            e.stopPropagation();
            $(this).parent().parent().parent().children().children(".aclModule-name").toggleClass("hidden");
            if($(this).is(".fa-angle-double-down")) {
                $(this).removeClass("fa-angle-double-down").addClass("fa-angle-double-up");
            } else{
                $(this).removeClass("fa-angle-double-up").addClass("fa-angle-double-down");
            }
        });

        $(".aclModule-name").click(function(e) {
            e.preventDefault();
            e.stopPropagation();
            var aclModuleId = $(this).attr("data-id");
            handleAclModuleSelected(aclModuleId);
        });

        // $(".aclModule-delete").click(function (e) {
        //     e.preventDefault();
        //     e.stopPropagation();
        //     var aclModuleId = $(this).attr("data-id");
        //     var aclModuleName = $(this).attr("data-name");
        //     if (confirm("确定要删除权限模块[" + aclModuleName + "]吗?")) {
        //         $.ajax({
        //             url: "/sys/aclModule/delete.json",
        //             data: {
        //                 id: aclModuleId
        //             },
        //             success: function (result) {
        //                 if (result.ret) {
        //                     showMessage("删除权限模块[" + aclModuleName + "]", "操作成功", true);
        //                     loadAclModuleTree();
        //                 } else {
        //                     showMessage("删除权限模块[" + aclModuleName + "]", result.msg, false);
        //                 }
        //             }
        //         });
        //     }
        // });
        //
        $(".aclModule-edit").click(function(e) {
            e.preventDefault(); //阻止默认事件
            e.stopPropagation(); //阻止冒泡事件
            var aclModuleId = $(this).attr("data-id");
            $("#dialog-aclModule-form").dialog({
                model: true,
                title: "编辑权限模块",
                open: function(event, ui) {
                    $(".ui-dialog-titlebar-close", $(this).parent()).hide();
                    optionStr = "<option value=\"0\">-</option>";
                    recursiveRenderAclModuleSelect(aclModuleList, 1);
                    $("#aclModuleForm")[0].reset();
                    $("#parentId").html(optionStr);
                    $("#aclModuleId").val(aclModuleId);
                    var targetAclModule = aclModuleMap[aclModuleId];
                    if (targetAclModule) {
                        $("#parentId").val(targetAclModule.parentId);
                        $("#aclModuleName").val(targetAclModule.name);
                        $("#aclModuleSeq").val(targetAclModule.seq);
                        $("#aclModuleRemark").val(targetAclModule.remark);
                        $("#aclModuleStatus").val(targetAclModule.status);
                    }
                },
                buttons : {
                    "更新": function(e) {
                        e.preventDefault();
                        updateAclModule(false, function (data) {
                            $("#dialog-aclModule-form").dialog("close");
                        }, function (data) {
                            showMessage("编辑权限模块", data.msg, false);
                        })
                    },
                    "取消": function () {
                        $("#dialog-aclModule-form").dialog("close");
                    }
                }
            });
        });
    }


    //递归渲染权限模块选择器
    function recursiveRenderAclModuleSelect(aclModuleList,level) {
        level = level | 0;
        if(aclModuleList && aclModuleList.length >0){
            $(aclModuleList).each(function (i, aclModule) {
                aclModuleMap[aclModule.id] = aclModule;
                var blank = "";
                if (level > 1) {
                    for(var j = 3; j <= level; j++) {
                        blank += "..";
                    }
                    blank += "∟";
                }
                optionStr += Mustache.render("<option value='{{id}}'>{{name}}</option>", {id: aclModule.id, name: blank + aclModule.name});
                if (aclModule.aclModuleList && aclModule.aclModuleList.length > 0) {
                    recursiveRenderAclModuleSelect(aclModule.aclModuleList, level + 1);
                }
            });
        }
    }


    //新增
    $(".aclModule-add").click(function () {
        $("#dialog-aclModule-form").dialog({
            model:true,
            title:"新增权限模块",
            open:function(even,ui){
                $(".ui-dialog-titlebar-close",$(this).parent()).hide();
                optionStr = "<option value=\"0\">-</option>"
                recursiveRenderAclModuleSelect(aclModuleList,1);
                $("#aclModuleForm")[0].reset();
                $("#parentId").html(optionStr);
            },
            buttons:{
                "添加": function(e) {
                    e.preventDefault();
                    updateAclModule(true, function (data) {
                        $("#dialog-aclModule-form").dialog("close");
                    }, function (data) {
                        showMessage("新增权限模块", data.msg, false);
                    })
                },
                "取消": function () {
                    $("#dialog-aclModule-form").dialog("close");
                }
            }
        });
    });


    //save、update 提交路径整合
    function updateAclModule(isCreate, successCallback, failCallback) {
        $.ajax({
            url: isCreate ? "/sys/aclModule/save.json" : "/sys/aclModule/update.json",
            data: $("#aclModuleForm").serializeArray(), //serializeArray自动组装成我们所需要的格式
            type: 'POST',
            success: function(result) {
                if (result.ret) {
                    loadAclModuleTree();
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
     * 点击权限模块选择器
     * @param aclModuleId
     */
    function handleAclModuleSelected(aclModuleId) {
        if (lastClickAclModuleId != -1) {  //不等于-1 表示需要移除高亮显示
            var lastAclModule = $("#aclModule_" + lastClickAclModuleId + " .dd2-content:first");
            lastAclModule.removeClass("btn-yellow");
            lastAclModule.removeClass("no-hover");
        }
        var currentAclModule = $("#aclModule_" + aclModuleId + " .dd2-content:first");
        currentAclModule.addClass("btn-yellow");
        currentAclModule.addClass("no-hover");
        lastClickAclModuleId = aclModuleId;
        loadAclList(lastClickAclModuleId);

    }






    /**
     * 权限的操作
     */


    //加载分页权限列表
    function loadAclList(aclModuleId) {
        var pageSize = $("#pageSize").val();
        var url = "/sys/acl/page.json?aclModuleId="+aclModuleId;
        var pageNo = $("#aclPage .pageNo").val() || 1;
        $.ajax({
            url:url,
            data:{
                pageSize:pageSize,
                pageNo:pageNo
            },
            success:function(result){
                renderAclListAndPage(result, url);
            }
        })
    }


    //渲染分页权限列表（上一页，下一页...）
    function renderAclListAndPage(result, url) {
        if (result.ret){
            if (result.data.total > 0){
                var rendered = Mustache.render(aclListTemplate,{
                    aclList:result.data.data,
                    "showAclModuleName":function () {
                        return aclModuleMap[this.aclModuleId].name;
                    },
                    "showStatus": function () {
                        return this.status == 1 ? "有效" : "无效";
                    },
                    "showType": function () {
                        return this.type == 1 ? "菜单" : (this.type == 2 ? "按钮" : "其他");
                    },
                    "bold" : function () {
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
                $("#aclList").html(rendered);
                bindAclClick();
                $.each(result.data.data,function (i,acl) {
                      aclMap[acl.id] = acl;
                });
            }else {
                $("#aclList").html('');
            }
            var pageSize = $("#pageSize").val();
            var pageNo = $("#aclPage .pageNo").val() || 1;
            renderPage(url, result.data.total, pageNo, pageSize, result.data.total > 0 ? result.data.data.length : 0, "aclPage", renderAclListAndPage);

        }else {
            showMessage("获取权限点列表", result.msg, false);
        }
    }


    //绑定权限点的点击操作
    function bindAclClick() {
        $(".acl-edit").click(function (e) {
            e.preventDefault();
            e.stopPropagation();
            var aclId = $(this).attr("data-id");
            $("#dialog-acl-form").dialog({
                model:true,
                title:"编辑权限",
                open:function(even,ui){
                    $(".ui-dialog-titlebar-close",$(this).parent()).hide(); //去掉关闭按钮
                    optionStr = "";
                    recursiveRenderAclModuleSelect(aclModuleList,1);
                    $("#aclForm")[0].reset();
                    $("#aclModuleSelectId").html(optionStr);
                    var targetAcl = aclMap[aclId];
                    if (targetAcl){
                        $("#aclId").val(aclId);
                        $("#aclModuleSelectId").val(targetAcl.aclModuleId);
                        $("#aclStatus").val(targetAcl.status);
                        $("#aclType").val(targetAcl.type);
                        $("#aclName").val(targetAcl.name);
                        $("#aclUrl").val(targetAcl.url);
                        $("#aclSeq").val(targetAcl.seq);
                        $("#aclRemark").val(targetAcl.remark);
                    }
                },
                buttons:{
                    "更新": function(e) {
                        e.preventDefault();
                        updateAcl(false, function (data) {
                            $("#dialog-acl-form").dialog("close");
                        }, function (data) {
                            showMessage("更新权限", data.msg, false);
                        })
                    },
                    "取消": function () {
                        $("#dialog-acl-form").dialog("close");
                    }
                }
            });
        });
    }


    //新增模块点
    $(".acl-add").click(function () {
        $("#dialog-acl-form").dialog({
            model:true,
            title:"新增权限",
            open:function(even,ui){
                $(".ui-dialog-titlebar-close",$(this).parent()).hide();
                optionStr = "";
                recursiveRenderAclModuleSelect(aclModuleList,1);
                $("#aclForm")[0].reset();
                $("#aclModuleSelectId").html(optionStr);
            },
            buttons:{
                "添加": function(e) {
                    e.preventDefault();
                    updateAcl(true, function (data) {
                        $("#dialog-acl-form").dialog("close");
                    }, function (data) {
                        showMessage("新增权限", data.msg, false);
                    })
                },
                "取消": function () {
                    $("#dialog-acl-form").dialog("close");
                }
            }
        });
    });


    function updateAcl(isCreate, successCallback, failCallback) {
        $.ajax({
            url: isCreate ? "/sys/acl/save.json" : "/sys/acl/update.json",
            data: $("#aclForm").serializeArray(), //serializeArray自动组装成我们所需要的格式
            type: 'POST',
            success: function(result) {
                if (result.ret) {
                    loadAclList(lastClickAclModuleId);
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
