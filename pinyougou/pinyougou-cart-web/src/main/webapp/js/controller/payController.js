app.controller("payController", function ($scope, $location, cartService, payService) {

    //获取用户信息
    $scope.getUsername = function () {
        cartService.getUsername().success(function (response) {
            $scope.username = response.username;
        });
    };

    //生成二维码
    $scope.createNative = function () {
        //1、获取浏览器地址栏中的交易号
        $scope.outTradeNo = $location.search()["outTradeNo"];
        //2、发送请求到后台获取交易结果、总金额、二维码链接地址、outTradeNo
        payService.createNative($scope.outTradeNo).success(function (response) {
            if ("SUCCESS"==response.result_code) {
                //3、生成二维码图片
                //本次支付的总金额
                $scope.totalFee = (response.totalFee / 100).toFixed(2);

                var qr = new QRious({
                    element:document.getElementById("qrious"),
                    size:250,
                    level:"M",
                    value:response.code_url
                });
            } else {
                alert("生成二维码失败!");
            }
        });
    };

});