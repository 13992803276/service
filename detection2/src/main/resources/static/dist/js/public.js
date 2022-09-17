function register(){
    let msg = beforeRegister()
    if (msg !== ''){
        error(msg);
        return;
    }
    let data = {
        nick: $("#nick").val(),
        email: $("#email").val(),
        password: $("#password").val(),
    }

    $.ajax({
        url: "/common/register",
        data: data,
        dataType: 'json',
        method: 'POST',
        success: function (result){
            if (result.success){
                Swal.fire("成功", "恭喜您注册成功,点击确定跳转到登录页面!", "success").then(() =>{
                    window.location.href = "/common/login";
                })
            }else{
                Swal.fire("失败", result.errMsg, "error").then(() =>{
                    window.location.href = "/common/register";
                })
            }
        }
    })
}

function login() {
    let dt = {
        email: $("#email").val(),
        password: $("#password").val()
    }
    // 登录
    $.ajax({
        type: 'POST',
        url: "/common/login",
        data: dt,
        dataType: 'json',
        success: function (result) {
            if(result.success){
                Swal.fire(
                    '成功',
                    '您已成功登录',
                    'success'
                ).then(()=>{
                    window.location.href = "/common/index";
                })
            }else{
                error(result.errMsg);
            }
        }
    })
}

function beforeRegister() {
    // 昵称
    let nick = $("#nick").val();
    if (!validField(nick, 3, 10)) {
        return '请输入3个字符及以上,10个字符以下的昵称,支持中英文!';
    }
    let email = $("#email").val();
    if (!validEmail(email)) {
        return "请输入正确格式的邮箱!";
    }
    let password = $("#password").val();
    if (isEmptyStr(password) || !validField(password, 6, 16)) {
        return "请输入最短6位,最长16位,数字和英文字母组合的密码!";
    }
    if (password !== $("#password2").val()){
        return "两次输入的密码不一致!";
    }
    return "";
}

function isEmptyStr(str) {
    return str === undefined || str.trim() === '';
}


function validField(obj, minLength, maxLength){
    let len = obj.trim().length;
    console.log(len)
    return len <= maxLength && len >= minLength;
}

function validEmail(str) {
    let pattern = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;
    return pattern.test(str.trim())
}

function showModal(id) {
    const m = $("#" + id)
    m.modal({keyboard: false, backdrop: "static"})
    m.modal("show");
}

function forget(){
    const data = {
        email: $("#forgetEmail").val()
    }
    $.ajax({
        url: "/common/forget",
        data: data,
        dataType: 'json',
        method: 'POST',
        success: function (result) {
            if (result.success) {
                // 跳转到首页
                Swal.fire("成功", "请检查邮箱通过重置密码链接修改密码", "success").then(() =>{
                    window.location.href = "/common/reset";
                })
            } else {
                error(result.errMsg);
            }
        }
    })
}

function reset(){
    let pass1 = $("#resetPass1").val();
    let pass2 = $("#resetPass2").val();
    if (pass1 !== pass2){
        error("两次输入的密码不一致");
        return;
    }
    const data = {
        pass: pass1,
        code: $("#resetVerifyCode").val(),
    }
    $.ajax({
        type: 'POST',
        url: "/common/reset",
        data: data,
        dataType: 'json',
        success: function (result) {
            if (result.success) {
                // 跳转到登录
                Swal.fire("成功", "您已成功修改密码,请使用新密码登录", "success").then(() =>{
                    window.location.href = "/common/login";
                })
            } else {
                error(result.errMsg);
            }
        }
    })
}

function logout(){
    Swal.fire({
        title: '',
        text: '确定退出登录吗?',
        showCancelButton: true,
        confirmButtonText: '确定',
        cancelButtonText: `取消`,
    }).then((result) => {
        if (result.isConfirmed) {
            doLogout()
        }
    })
}

function activate(element){
    element.setAttribute("class", "list-group-item list-group-item-action active");
}

function unActivate(element){
    element.setAttribute("class", "list-group-item list-group-item-action");
}

// 显示选中图像的文件名
function showFileNameInLabel(id){
    const headerInput = $("#" + id);
    headerInput.change(function () {
        // 获取文件
        let f = headerInput.prop('files')[0];
        $(this).siblings('.custom-file-label').html(f.name);
    })
}

function sendSingleImage(){
    // 获取拍摄距离和拍摄焦距
    let distance = $("#imageShotDistance").val();
    let length = $("#imageFocalLength").val();
    // 获取文件
    const file = document.getElementById('detect-image').files[0];
    if (file === undefined || file.size === 0){
        Swal.fire({
            icon: 'error',
            title: '错误',
            text: '选择一张图像上传',
        })
    }else{
        let formData = new FormData();
        formData.append('file', $("#detect-image")[0].files[0]);
        formData.append('shotDistance', distance);
        formData.append('focalLength', length);
        // 在模态框上显示提醒
        disableDetectImageInput();
        // 发送异步请求
        $.ajax({
            url: "/common/image/detect",
            type: 'POST',
            data: formData,
            contentType: false,
            processData: false,
            success: function (result){
                if (result.success){
                    // 隐藏提示框
                    $("#detect-image-alert").attr('hidden', true);
                    Swal.fire(
                        '成功',
                        '图像已上传,请移步图像列表查看结果',
                        'success'
                    ).then(()=>{
                        window.location.reload();
                    })
                }else{
                    error(result.errMsg);
                }
            }
        })
    }
}

function disableDetectImageInput(){
    $("#detect-image-alert").removeAttr('hidden');
    const modal = $("#imageModal");
    modal.find('input').attr('disabled', true);
    modal.find('button').attr('disabled', true);

}

function success(msg){
    Swal.fire({
            icon: 'success',
            title: '成功',
            text: msg
        })
}

function error(msg){
    Swal.fire({
        icon: 'error',
        title: '错误',
        text: msg,
    })
}

function download(href, title) {
    // 动态生成a标签,自动进行点击
    const a = document.createElement('a');
    a.setAttribute('href', href);
    a.setAttribute('download', title);
    a.click();
}


function sleep(t) {
    return new Promise((resolve) => setTimeout(resolve, t));
}

function doLogout(){
    $.get({
        url: "/common/logout",
        success: function (result) {
            if (result.success) {
                Swal.fire('成功', '您已成功退出登录', 'success').then(() => {
                    window.location.href = "/common/login";
                })
            }
        }
    })
}