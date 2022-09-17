const $table = $("#bridge_table");
let chart;
$(function () {
    load();
});

function load() {
    $table.bootstrapTable({ // 对应table标签的id
        url: "/common/bridge/list", // 获取表格数据的url
        method: "get",
        cache: false, // 设置为 false 禁用 AJAX 数据缓存， 默认为true
        striped: true,  //表格显示条纹，默认为false
        pagination: true, // 在表格底部显示分页组件，默认false
        pageList: [10, 15, 20], // 设置页面可以显示的数据条数
        pageSize: 10, // 页面数据条数
        pageNumber: 1, // 首页页码
        sidePagination: 'client', // 客户端分页
        showRefresh: false,
        queryParams: function (params) { // 请求服务器数据时发送的参数，可以在这里添加额外的查询参数，返回false则终止请求
            return {
                ex: new Date() * 1, // 额外添加的参数
                keyword: $('#keyword').val(),
                start: $("#bridgeBeginTime").val(),
                process: getProcess()
            }
        },
        sortName: 'id', // 要排序的字段
        sortOrder: 'desc', // 排序规则
        columns: [
            // {
            //     checkbox: true, // 显示一个勾选框
            //     align: 'center' // 居中显示
            // },
            {
                field: 'id', // 返回json数据中的name
                title: '编号', // 表格表头显示文字
                align: 'center', // 左右居中
                valign: 'middle',// 上下居中
                visible: false,
            }, {
                field: 'name',
                title: '桥梁名称',
                align: 'center',
                valign: 'middle'
            }, {
                field: 'span',
                title: '跨径组合',
                align: 'center',
                valign: 'middle',
            }, {
                field: "structure",
                title: "结构形式",
                align: 'center',
                valign: 'middle',
                width: 160, // 定义列的宽度，单位为像素px
            }, {
                field: "structNum",
                title: "构件数量",
                align: "center",
                valign: "middle",
                width: 100,
            }, {
                field: "imageNum",
                title: "图像数量",
                align: "center",
                valign: "middle",
            }, {
                field: "gmtCreate",
                title: "检测时间",
                align: "center",
                valign: "middle",
            }, {
                field: "process",
                title: "状态",
                align: "center",
                valign: "middle",
                formatter: function (value, row, index) { // 单元格格式化函数
                    if (row.exp === 1) {
                        return "<span style='color: #993333'>流程中出错</span>"
                    }
                    let matchedStr = getMatchedStr(value);
                    return "<span style='color: #009966'> " + matchedStr + "</span>"

                }
            }, {
                field: "status",
                title: "桥梁详情",
                align: "center",
                valign: "middle",
                formatter: function (value, row, index) {
                    return "<button class='btn btn-primary btn-sm' onclick='detail(" + row.id + ")'>详情</button>"
                }
            }
        ],
        onLoadError: function () {  //加载失败时执行
            console.info("加载数据失败");
        }
    });
}

function searchBridges() {
    // 刷新表格
    reload()
}

function reload() {
    $table.bootstrapTable('destroy');
    load();
}

function downloadReport() {
    hideModal('detailModal');
    const id = $("#bridgeId").html()
    const name = $("#bridgeName").html()
    let url = "/common/report/download/" + id;
    download(url, name + '病害检测报告.docx')
}

function getProcess(){
    let text = $("#process_text").html();
    if (text === "检测中"){
        return "DETECTING";
    }
    else if (text === "已完成"){
        return "FINISHED";
    }
    return "ALL";

}




function deleteBridge() {
    // 获取选择的行
    let infos = getSelectIds("bridge_table");

    if (infos == null || infos.length === 0) {
        error("至少选择一条记录!");
        return;
    }
    let ids = [];
    for (let i = 0; i < infos.length; i++) {
        let info = infos[i];
        let p = info.process;
        if (p === 'FINISHED' || info.exp === 1) {
            // 完成了或者出现异常了
            ids[i] = infos[i].id;
        } else {
            error('无法删除正在流程中的桥梁!');
            return;
        }
    }
    // 提交post任务，删除ids，获取删除信息，显示模态框
    swal({
        title: "确认弹框",
        text: "确认要删除数据吗?",
        icon: "warning",
        buttons: true,
        dangerMode: true,
    }).then((flag) => {
            if (flag) {
                $.ajax({
                    type: "POST",
                    url: "/common/bridge/delete",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function (result) {
                        if (result.success) {
                            swal("删除成功", {
                                icon: "success",
                            });
                            reload()
                        } else {
                            swal(result.message, {
                                icon: "error",
                            });
                        }
                    }
                });
            }
        }
    );

}

function batchDownloadReport() {
    let infos = getSelectIds("bridge_table");
    if (infos != null && infos.length !== 0) {
        // 存在选择,遍历,然后调用下载
        for (let index in infos) {
            let info = infos[index];
            let id = info.id;
            // 判断当前的状态
            let process = info.process;
            if (process !== 'FINISHED') {
                swal("错误", info.name + "的检测报告尚未生成!无法下载!", "error");
                return;
            }
        }

        for (let i in infos) {
            let id = infos[i].id;
            // 拼接下载的URL,然后完成下载
            let downloadUrl = "/common/bridge/report/" + id;
            download(downloadUrl, "检测报告" + id);
        }
    } else {
        error("至少选择一条记录!");
    }
}

function detail(id) {
    $.get({url: "/common/bridge/detail/" + id, dataType: 'json'}).done(function (result) {
        let counts = result.data.counts;
        let process = result.data.process;
        if (process === 'LOADING' || process === 'DETECTING' || process === 'GENERATING'){
            counts = [1, 1, 1]
        }
        const res = {
            labels: ['裂缝', '混凝土剥落', '钢筋锈蚀'],
            datasets: [{
                label: '',
                data: counts,
                backgroundColor: ['#FFCC33', '#009966', '#3399CC'],
                borderWidth: 4,
            }]
        };

        const ctx = document.getElementById('pieChart').getContext('2d');
        chart = new Chart(ctx, {
            type: 'pie',
            data: res,
            options: {
                tooltips: {
                    intersect: false,
                    mode: 'index'
                }
            }
        });
        const bridge = result.data;
        // 从result中获取需要展示的值
        $("#bridgeName").html(bridge.name);
        $("#struct").html('结构形式:   ' + bridge.structure);
        $("#span").html('跨径组合:   ' + bridge.span);
        $("#structNum").html('构件数量:   ' + bridge.structNum);
        $("#imageNum").html('图像数量:   ' + bridge.imageNum);
        $("#detectTime").html('检测时间:   ' + bridge.gmtCreate);
        $("#bridgeId").html(bridge.id)
        const statusElement = $("#status")
        let status;
        if (bridge.exp === 1) {
            // 出现异常了
            status = "发生错误";
        } else {
            status = getMatchedStr(bridge.process);
        }
        if (status === "已完成") {
            // 字体颜色变绿
            statusElement.css("color", "#339999");
            $("#downloadBtn").removeAttr('disabled')
        } else if (status === "发生错误") {
            statusElement.css("color", "#CC3333");
            $("#downloadBtn").attr("disabled", "disabled");
        } else {
            statusElement.css("color", "#CCCCCC");
            $("#downloadBtn").attr("disabled", "disabled");
        }

        statusElement.html("当前状态:   " + status)
        // 模态框展示
        showModal('detailModal');
    })
}


function getMatchedStr(process){
    if (process === 'DETECTING'){
        return "检测中";
    }
    if (process === "LOADING"){
        return "检测中";
    }
    if (process === "FINISHED"){
        return "已完成";
    }
    return "未知";
}



function hideModal(id) {
    $("#" + id).modal("hide");
    if (id === 'detailModal'){
        chart.destroy();
    }
}



