const parts = ['top', 'bottom', 'deck'];
$(function () {
    init();
    globalDefault();
})

function init() {
    // 文件上传组件
    $(".dir").fileinput({
        language: 'zh',
        showPreview: false,
        theme: 'fa',
        showUpload: false,
        showCancel: false,
        showUploadedThumbs: false,
        fileSizeGetter: true,
        browseLabel: '选择文件夹',
    });
    // 生成按钮的事件监听
    generateStructCardListener();
    // 添加按钮的事件监听
    addStructCardListener();

    $("#detectBtn").click(() => {
        let res = beforeSend();
        console.log(res);
        if (!res.success) {
            // 说明校验失败了
            return;
        }
        // 开始发送桥梁数据
        sendBridge(res);
    });

}

/**
 *  获取三个组中的构件卡片数量
 */
function cardNum() {
    let cards = $("#topStructGroup").find('.card');
    console.log(cards.length);
}

/**
 *  向ID为id的组件中添加构件卡片
 * @param part
 */
function appendStruct(part) {
    // 先设置焦距和拍摄距离
    let shotDistance = $("#" + part + "ShotDistance").val();
    let focalLength = $("#" + part + "FocalLength").val();
    $("#srcShotDistance").attr('value', shotDistance);
    $("#srcFocalLength").attr('value', focalLength);

    console.log(shotDistance, focalLength);
    // 获取待复制的内容
    let srcData = $("#srcCard").html();
    // 获取其父标签
    let extra = '<div class="col-6">' + srcData + '</div>';
    const group = $("#" + part + "StructGroup");
    group.append(extra);
    let lengths = group.find('.focal-length');
    console.log(lengths.length);
    $(lengths[lengths.length - 1]).attr('value', focalLength);
    destroy();
    fileInputListener();
}

/**
 * 根据ID查找组件,删除其内容
 * @param id 组件的ID
 */
function clear(id) {
    $("#" + id).empty();
}

/**
 *  构件卡片右上角的X,可以直接将当前构件从其父容器中删除
 */
function destroy() {
    $(".destroy-btn").click(function () {
        let t = $(this).parents('.struct').parent();
        t.fadeToggle(800);
        sleep(800).then(() => {
            t.remove();
        });
    })
}

/**
 *  发送桥梁数据
 *  1. 桥梁名称
 *  2. 桥梁的结构形式
 *  3. 桥梁的跨境组合
 *  2. 三个部分中,构件的个数
 */
function sendBridge(prepareData) {
    let data = prepareData.bridge;
    console.log(data);
    disableInput(data.name);
    $.ajax({
        url: "/common/bridge/init",
        data: data,
        dataType: 'json',
        method: 'POST',
        success: function (result) {
            if (result.success) {
                let bridge = result.data;
                // 继续去发送每个构件
                sendImages(prepareData.images, bridge.id);
            } else {
                // 报错误
                error(result.errMsg);
            }
        },
    });
}

/**
 *  发送图像,需要发送的信息有
 *  图像所属桥梁的ID
 *  构件的名称,
 *  焦距和拍摄距离
 */
function sendImages(images, bridgeId) {
    limitedBatchSendImages(images, bridgeId, 10).then(res => {
        console.log(res);
        afterSend();
    })
}

/**
 *  在发送文件之前,先校验一下前端面板的输入
 *  校验前端面板中的输入
 */
function beforeSend() {
    let res = {success: false};
    let bridgeData = {};
    let name = $("#detect-bridge-name").val();
    bridgeData['name'] = name;
    let structure = $("#detect-bridge-structure").val();
    bridgeData['structure'] = structure;
    let span = $("#detect-bridge-span").val();
    bridgeData['span'] = span;
    if (isEmptyStr(name)) {
        error("桥梁名称不能为空!")
        return res;
    }
    if (isEmptyStr(structure)) {
        error("结构形式不能为空!")
        return res;
    }
    if (isEmptyStr(span)) {
        error("跨径组合不能为空!")
        return res;
    }
    let imageInfos = [];
    // 用于保存构件的编号,构件编号是无法重复的
    let structSerialNumberSet = new Set();
    // 校验各个部分是否合法
    for (let idx in parts) {
        let part = parts[idx];
        console.log(part);
        // 首先获取两个距离
        let distance = $("#" + part + "ShotDistance").val();
        let length = $("#" + part + "FocalLength").val();

        if (distance <= 100 || distance >= 1000) {
            error("仅支持100毫米到1000毫米的拍摄距离!")
            return res;
        }
        if (length <= 0) {
            error("请输入正确拍摄焦距")
            return res;
        }
        // 校验输入的各个构件
        const partGroup = $("#" + part + "StructGroup");
        let structs = partGroup.find('.struct');
        let structNum = structs.length;
        if (structNum === 0) {
            // 说明没有构件,不合法
            error("每个部分至少要包含一个构件!");
            return res;
        }
        for (let index = 0; index < structNum; index++) {
            let struct = structs[index];
            // 获取标题
            let serialNumber = $(struct).find(".serialNumber").val();
            if (isEmptyStr(serialNumber)) {
                error("构件必须编号!");
                return res;
            }
            // 构件编号需要满足构件的命名规则,看也就是必须包含#且不在最前和最后
            if (!serialNumber.includes('#')) {
                error("构件编号错误,请按照规则进行编号!");
                return res;
            }
            if (structSerialNumberSet.has(serialNumber)) {
                error("构件命名不能重复!");
                return res;
            } else {
                structSerialNumberSet.add(serialNumber);
            }
            // 获取它的两个距离
            let shotDistance = $(struct).find(".shot-distance").val();
            let focalLength = $(struct).find(".focal-length").val();
            if (shotDistance <= 100 || shotDistance >= 1000) {
                error("仅支持100毫米到1000毫米的拍摄距离!");
                return res;
            }
            if (focalLength <= 0) {
                error("请输入正确拍摄焦距!");
                return res;
            }
            // 校验一下文件输入框中的文件数量
            let files = $(struct).find('.custom-file-input').prop('files');
            if (files.length === 0) {
                // 说明文件的数量为0
                error("构件中至少有一张图像!");
                return res;
            }
            // 先遍历一下,留下文件夹中的图像文件
            let images = [];
            for (let imgIdx = 0; imgIdx < files.length; imgIdx++) {
                let name = files[imgIdx].name.toLowerCase();
                if (name.endsWith('png') || name.endsWith('jpg') || name.endsWith('jpeg')) {
                    images.push(files[imgIdx]);
                }
            }
            for (let i = 0; i < images.length; i++) {
                let imageInfo = {
                    image: images[i],
                    serialNumber: serialNumber,
                    shotDistance: shotDistance,
                    focalLength: focalLength,
                    imageNum: images.length,
                    part: part
                };
                imageInfos.push(imageInfo);
            }
        }
    }
    bridgeData['imageNum'] = imageInfos.length;
    res['bridge'] = bridgeData;
    res['images'] = imageInfos;
    res['success'] = true;
    return res;

}


/**
 *  生成按钮的监听
 */
function generateStructCardListener() {
    // 三个部分
    for (let idx in parts) {
        let part = parts[idx];
        let btnId = part + "GenerateBtn";
        $("#" + btnId).click(function () {
            // 获取构件数量
            let structNum = $("#" + part + "StructNum").val();
            // 生成,填充
            structNum = Number(structNum);
            // 清除内容
            let groupId = part + 'StructGroup';
            clear(groupId);
            for (let i = 0; i < structNum; i++) {
                appendStruct(part);
            }
        })
    }
}

/**
 * 添加按钮的监听
 */
function addStructCardListener() {
    // 三个部分
    for (let idx in parts) {
        let part = parts[idx];
        let partBtnId = part + "AddBtn";
        $("#" + partBtnId).click(function () {
            appendStruct(part);
        });
    }
}

/**
 *  构件默认是该部分的拍摄距离和焦距
 *  部分默认是该用户设定的默认拍摄距离和焦距
 */
function globalDefault() {
    let shotDistance = 500;
    let focalLength = 35
    for (let idx in parts) {
        let part = parts[idx];
        $("#" + part + "ShotDistance").val(shotDistance);
        $("#" + part + "FocalLength").val(focalLength);
    }
}

/**
 *  获取文件中的内容,现在在label上
 */
function fileInputListener() {
    $('.custom-file-input').change(function () {
        let fileInput = $(this);
        let count = 0;
        let files = fileInput.prop('files');
        for (let i = 0; i < files.length; i++) {
            let name = files[i].name.toLowerCase();
            if (name.endsWith('png') || name.endsWith('jpg') || name.endsWith('jpeg')) {
                count += 1;
            }
        }
        // 获取其label
        fileInput.siblings('.custom-file-label').html('已选择' + count + "张图像");
    })
}

function updateProgressAndMessage(message, percent) {
    let percentStr = percent + "%";
    // 然后更新里面的文本
    $("#processMsg").html(message);
    let process = $("#processBar");
    process.width(percentStr);
    process.html(percent.toFixed(2) + "%");
}

/**
 *
 * @param images 图像的数量
 * @param bridgeId 控制的最大并发数
 * @param n 最大并发数
 * @returns {Promise<unknown>}
 */
function limitedBatchSendImages(images, bridgeId, n) {
    const url = "/common/image/upload";
    const result = []
    let flag = 0 // 控制进度，表示当前位置
    let sum = 0 // 记录请求完成总数
    let imageNum = images.length;
    const config = {
        headers: {"Content-Type": "multipart/form-data"}
    };
    return new Promise((resolve, reject) => {
        // 先连续调用n次，就代表最大并发数量
        while (flag < n) {
            next()
        }

        function next() {
            const cur = flag++ // 利用闭包保存当前位置，以便结果能顺序存储
            if (cur >= imageNum) {
                return;
            }
            // 封装请求数据
            let image = images[cur];
            // 使用formData的方式来发送文件
            let formData = new FormData();
            let file = image['image'];
            let serialNumber = image['serialNumber'];
            formData.append('file', file);
            formData.append('serialNumber', image['serialNumber']);
            formData.append('bridgeId', bridgeId);
            formData.append('shotDistance', image['shotDistance']);
            formData.append('focalLength', image['focalLength']);
            formData.append('part', image['part']);
            formData.append('imageNum', image['imageNum']);
            axios.post(url, formData, config).then(res => {
                result[cur] = cur // 保存结果。为了体现顺序，这里保存索引值
                sum += 1;
                let message = "正在上传「" + serialNumber + "」构件下的图像「" + file.name + "」 (" + sum + "/" + imageNum + ")";
                let percent = sum / imageNum * 100;
                updateProgressAndMessage(message, percent);
                if (sum >= imageNum) {
                    resolve(result)
                } else {
                    next()
                }
            }).catch(reject)
        }
    })
}

function disableInput(bridgeName) {
    // 展示警告栏
    $("#bridgeName").html(bridgeName);
    $("#processAlertBlock").removeAttr('hidden');
    // 关闭所有的输入
    $('input').attr('disabled', true);
    // 关闭所有生成构件和添加构件的btn
    for (let i in parts) {
        let part = parts[i];
        $("#" + part + "GenerateBtn").attr('disabled', true);
        $("#" + part + "AddBtn").attr('disabled', true);
    }
    window.location.href = "#top";
}

function afterSend() {
    // 弹出提示框,提示已经发送完毕,点击确定之后,跳转到桥梁管理页面
    Swal.fire("成功", "上传桥梁数据成功! 点击确定到桥梁管理页面查看检测进度", "success").then(() => {
        window.location.href = "/common/index";
    })
}


function change(part) {
    let id = '#' + part + 'ToolBtn';
    const btn = $(id)
    let th = btn.attr('class')
    btn.removeAttr('class')
    if (th.indexOf('minus') !== -1) {
        btn.attr('class', 'fa fa-plus')
    } else {
        btn.attr('class', 'fa fa-minus')
    }
}



