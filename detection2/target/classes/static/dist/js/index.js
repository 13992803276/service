$(function () {
    addListener();
})

function addListener() {
    // 支持检测的病害类型
    $("#damageClasses").click(function () {
        showModal('infoModal');
    });
    // 点击检测时跳出的选择
    $("#detect").click(function () {
        choose();
    });
    $("#toImageBtn").click(function (){
        window.location.href = "/common/images";
    })
    // 日期组件的初始化
    $("#bridgeBeginTime").datetimepicker({
        format: 'Y-m-d H:i'
    });
    $.datetimepicker.setLocale('zh');
    showFileNameInLabel('detect-image');
    $("#upload").click(function (){
        sendSingleImage()
    });
    // 请求首页用到的数据并填充
    $.get('/common/indexData').done(function (result){
        if (result != null){
            $("#imageNumTag").html(result.imageNum);
            $("#bridgeNumTag").html(result.bridgeNum)
        }
    })
}


function choose(){
    const swalWithBootstrapButtons = Swal.mixin({
        customClass: {
            confirmButton: 'btn btn-primary',
            cancelButton: 'btn btn-success'
        },
        buttonsStyling: true
    })

    swalWithBootstrapButtons.fire({
        title: '想要进行哪种类型的检测?',
        text: "当前系统支持单张图像的检测和整座桥梁的检测",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: '单张检测',
        cancelButtonText: '桥梁检测',
    }).then((result) => {
        if (result.isConfirmed) {
            showModal('imageModal');
        } else if (
            result.dismiss === Swal.DismissReason.cancel
        ) {
            window.location.href = "/common/detect"
        }
    })
}
function replaceText(id, str) {
    $("#" + id).html(str);
}