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

    function loadRoleList() {
        $.ajax({
           url : "/sys/role/list.json",
           success:function (result) {
               if (result.ret){
                   var render = Mustache.render(roleListTemplate,{roleList:result.data});
                   $("#roleList").html(render);
                   //绑定点击事件
                   bindRoleClick();

                    $.each(result.data, function (i,role) {
                        roleMap[role.id] = role;  //全局的角色
                    });
               }else {
                   showMessage("加载角色列表",result.msg,false);
               }
           }
        });
    }


    function  bindRoleClick() {
        $(".role-edit").click(function (e) {
            e.preventDefault();//拦截默认操作
            e.stopPropagation();//拦截冒泡事件
            var roleId = $(this).attr("data-id");

            $("#dialog-role-form").dialog({
                model:true,
                title:"修改角色",
                open:function(event,ui){
                    $(".ui-dialog-titlebar-close",$(this).parent()).hide();
                    $("#roleForm")[0].reset();
                    var targetRole = roleMap[roleId];
                    if (targetRole){
                        $("#roleId").val(roleId);
                        $("#roleName").val(targetRole.name);
                        $("#roleStatus").val(targetRole.status);
                        $("#roleRemark").val(targetRole.remark);
                    }
                },
                buttons:{
                    "修改": function(e) {
                        e.preventDefault();
                        updateRole(false, function (data) {
                            $("#dialog-role-form").dialog("close");
                        }, function (data) {
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

    function handleRoleSelected(roleId){
        if (lastRoleId != -1){
            var lastRole = $("#role_"+lastRoleId+" .dd2-content:first");
            
        }



    }



    $(".role-add").click(function () {
        $("#dialog-role-form").dialog({
            model:true,
            title:"新增角色",
            open:function(event,ui){
                $(".ui-dialog-titlebar-close",$(this).parent()).hide();
                $("#roleForm")[0].reset();
            },
            buttons:{
                "添加": function(e) {
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




    function updateRole(isCreate, successCallback, failCallback) {
        $.ajax({
            url: isCreate ? "/sys/role/save.json" : "/sys/role/update.json",
            data: $("#roleForm").serializeArray(), //serializeArray自动组装成我们所需要的格式
            type: 'POST',
            success: function(result) {
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






});