$(function () {
    $('#imageBeginTime').datetimepicker({
        format: 'Y-m-d H:i'
    });
    $.datetimepicker.setLocale('zh');
    getPageContents(1);
})

function getPageContents(page) {
    // 获取当前的筛选条件
    // 时间
    let begin = $("#imageBeginTime").val();
    // 关键词
    let name = $("#imageName").val();
    // 当前状态
    let process = $("#imageStatusMenuButton").html();
    if (process === '检测中') {
        process = 'DETECTING';
    } else if (process === '已完成') {
        process = 'FINISHED';
    }else{
        process = 'ALL'
    }
    let url = "/common/image/list/" + page;
    url += "?begin=" + begin;
    url += "&name=" + name;
    url += "&process=" + process;
    console.log("url: " + url);
    // 发送请求,获取第page页的图像列表,并将ID和图像名称,检测时间,渲染卡片上
    $.get(url, function (result) {
        // 将result中的内容用json解析
        let index = 0;
        let next = false;
        if (result.success) {
            // 请求成功,获取其中的data
            let images = result.data
            let len = images.length;
            if (len > 0){
                $("#row-1").attr('hidden', false);
            }
            if (len > 4){
                $("#row-2").attr('hidden', false);
            }
            if (len > 8){
                $("#row-3").attr('hidden', false);
            }
            next = result.next
            for (let i in images) {
                const card = $("#card-" + index)
                card.attr("hidden", false);
                let image = images[i];
                // 修改图像的src属性
                const detectImg = $("#imag-" + index);
                detectImg.attr('src', '/common/image/thumbnail/' + image.id);
                // 修改标题
                $("#title-" + index).html(image.name);
                // 修改检测时间
                $("#text-" + index).html("上传于" + image.gmtCreate);
                // 判断一下当前的状态,然后修改卡片的类别
                card.attr("class", "card bg-light")
                // 先解除按钮上的绑定
                const btn = $("#image-detail-" + index);
                btn.unbind('click')
                btn.click(function () {
                    showImageModal(image.id, image.name);
                });

                index += 1;
            }
        }
        while (index < 12) {
            // 说明存在图像剩余
            $("#card-" + index).attr("hidden", true);
            index += 1;
        }

        // 修改分页部分
        if (next) {
            $("#nextPage").removeAttr("disabled");
        } else {
            $("#nextPage").attr("disabled", "disabled");
        }
        if (page === 1) {
            // 第一页没有上一页
            $("#prePage").attr("disabled", "disabled");
        } else {
            $("#prePage").removeAttr("disabled");
        }
        // 当前页
        $("#curPage").html(page);
    });
}

function nextPage() {
    getPageContents(getCurPage() + 1);
}

function prePage() {
    getPageContents(getCurPage() - 1);
}

function getCurPage() {
    // 获取当前页码
    let curPage = $('#curPage').html();
    return Number(curPage);
}

function showImageModal(id, name) {
    $("#detectImageDetail").modal("show");
    // 发送异步请求,获取具体数据
    $.get("/common/image/detail/" + id, function (result) {
        if (result.success) {
            let imageData = result.data;
            // 获取其检测时间
            let cap = "上传于";
            cap += imageData.gmtCreate + " ";
            // 当前状态
            cap += "当前状态: ";
            cap += imageData.process === 'QUANTIFIED' ? '已完成' : '检测中';
            // 替换
            $("#detectImageCaption").html(cap);

            // 获取其损伤种类
            console.log(imageData);
            let crack = imageData.crack;
            let spall = imageData.spall;
            let rebar = imageData.rebar;
            if (crack !== 0 || spall !== 0 || rebar !== 0) {
                $("#damageNumRow").removeAttr("hidden");
                const crackBtn = $("#crackTag");
                if (crack !== 0) {
                    $("#crackNumCap").html(crack);
                    crackBtn.removeAttr("hidden");
                    crackBtn.unbind('click');
                    crackBtn.click(function () {
                        Swal.fire('共有' + crack + '处裂缝');
                    });
                } else {
                    crackBtn.attr('hidden', true);
                }
                const spallBtn = $("#spallTag")
                if (spall !== 0) {
                    $("#spallNumCap").html(spall);
                    spallBtn.removeAttr("hidden");
                    spallBtn.click(function () {
                        Swal.fire('共有' + spall + '处破损');
                    });
                } else {
                    spallBtn.attr('hidden', true);
                }
                const rebarBtn = $("#rebarTag")
                if (rebar !== 0) {
                    $("#rebarNumCap").html(rebar);
                    rebarBtn.removeAttr("hidden");
                    rebarBtn.click(function () {
                        Swal.fire('共有' + rebar + '处钢筋锈蚀');
                    });
                } else {
                    rebarBtn.attr('hidden', true);
                }
            } else {
                $("#damageNumRow").attr("hidden", true);
            }
            // 显示图像
            $("#detectImage").attr("src", "/common/image/blob/" + id);
            // 下载图像
            const downloadBtn = $("#downloadImageBtn");
            downloadBtn.unbind('click')
            downloadBtn.click(function () {
                download("/common/image/blob/" + id, name);
            });
            // 删除图像
            $("#deleteImageBtn").click(function () {
                Swal.fire({
                    title: '确认删除图像吗',
                    text: '删除后将无法找回',
                    showCancelButton: true,
                    confirmButtonText: '确认',
                }).then((result) => {
                    if (result.isConfirmed) {
                        // 发送删除图像请求
                        deleteImage(id);
                    }
                })
            })
        } else {
            error(result.errMsg);
        }
    })
}

function getDamageNum(damages) {
    if (damages == null || damages.length === 0) {
        return null;
    }

    let res = new Map();
    for (let i in damages) {
        let damage = damages[i];
        if (res.get(damage.type) != null) {
            let num = res.get(damage.type);
            res.set(damage.type, num + 1);
        } else {
            res.set(damage.type, 1);
        }
    }
    return res;
}

function searchImage() {
    getPageContents(1);
}

function deleteImage(id) {
    // 删除图像
    $.ajax({
        url: '/common/image/delete/' + id,
        type: 'DELETE',
        success: function (result) {
            if (result.success) {
                Swal.fire("成功", "图像已删除", "success").then(() =>{
                    window.location.reload();
                })
            } else {
                error(result.errMsg);
            }
            hideModal("detectImageDetail");
            getPageContents(1);
        }
    });
}

function hideModal(id) {
    $("#" + id).modal("hide");
}