const $table = $("#traffic_table");
$(function () {
    load();
});

function load() {
    $table.bootstrapTable({ // 对应table标签的id
        url: "/traffic/list", // 获取表格数据的url
        method: "get",
        cache: false, // 设置为 false 禁用 AJAX 数据缓存， 默认为true
        striped: true,  //表格显示条纹，默认为false
        pagination: false, // 在表格底部显示分页组件，默认false
        pageList: [10, 15, 20], // 设置页面可以显示的数据条数
        pageSize: 10, // 页面数据条数
        pageNumber: 1, // 首页页码
        sidePagination: 'client', // 客户端分页
        showRefresh: false,
        queryParams: function (params) { // 请求服务器数据时发送的参数，可以在这里添加额外的查询参数，返回false则终止请求
            return {
                rootNo: $('#txt_root_no').val()
            }
        },
        //sortName: 'year', // 要排序的字段
        //sortOrder: 'asc', // 排序规则
        columns: [
             {
                field: 'year', // 返回json数据中的name
                title: '年份', // 表格表头显示文字
                align: 'center', // 左右居中
                valign: 'middle'// 上下居中
            }, {
                field: 'type',
                title: '类型',
                align: 'center',
                valign: 'middle'
            }, {
                field: 'xxhc',
                title: '小型货车',
                align: 'center',
                valign: 'middle',
            }, {
                field: "zxhc",
                title: "中型货车",
                align: 'center',
                valign: 'middle',
                //width: 160, // 定义列的宽度，单位为像素px
            }, {
                field: "dxhc",
                title: "大型货车",
                align: "center",
                valign: "middle",
                // width: 100,
            }, {
                field: "tdhc",
                title: "特大货车",
                align: "center",
                valign: "middle",
            }, {
                field: "jzxc",
                title: "集装箱车",
                align: "center",
                valign: "middle",
            }, {
                field: "zxkc",
                title: "中小客车",
                align: "center",
                valign: "middle",
                // formatter: function (value, row, index) { // 单元格格式化函数
                //     if (row.exp === 1) {
                //         return "<span style='color: #993333'>流程中出错</span>"
                //     }
                //     let matchedStr = getMatchedStr(value);
                //     return "<span style='color: #009966'> " + matchedStr + "</span>"
                //
                // }
            }, {
                field: "dkc",
                title: "大客车",
                align: "center",
                valign: "middle"
            }, {
                field: "total",
                title: "合计",
                align: "center",
                valign: "middle",
                // formatter: function (value, row, index) {
                //     return row.dkc + row.zxkc
                // }
            }
        ],
        onLoadError: function () {  //加载失败时执行
            console.info("加载数据失败");
        }
    });
}

function searchTraffic() {
    // 刷新表格
    reload()
}

function reload() {
    $table.bootstrapTable('destroy');
    load();
}

function importTraffic() {
    let msg = beforeImport()
    if (msg !== '') {
        error(msg);
        return;
    }
    let data = {
        rootNo: $("#rootNo").val(),
        fileName: document.getElementById("fileName").files[0].name
    }

    $.ajax({
        url: "/traffic/import",
        data: data,
        dataType: 'json',
        method: 'POST',
        success: function (result) {
            if (result.success) {
                Swal.fire("成功", "导入成功!", "success")
            } else {
                Swal.fire("失败", result.errMsg, "error")
            }
        }
    })
}

function error(msg) {
    Swal.fire({
        icon: 'error',
        title: '错误',
        text: msg,
    })
}

function beforeImport() {
    //道路编号
    let rootNo = $("#rootNo").val();
    if (rootNo === '') {
        return "请输入道路编号!";
    }
    // 文件名
    let fileName = $("#fileName").val();
    if (fileName === '') {
        return '请选择需要上传的文件';
    }
    return "";
}