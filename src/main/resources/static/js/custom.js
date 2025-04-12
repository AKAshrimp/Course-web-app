// 添加自定义验证和提示方法
document.addEventListener('DOMContentLoaded', function() {
    // 替换掉浏览器的默认alert
    var originalAlert = window.alert;
    window.alert = function(message) {
        // 这里使用更优雅的方式显示警告，例如Bootstrap toast或modal
        // 为简单起见，这里仍使用原生alert，但实际应用中可以使用Bootstrap组件
        originalAlert(message);
    };
}); 