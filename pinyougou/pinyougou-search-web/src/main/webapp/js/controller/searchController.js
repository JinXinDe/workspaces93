app.controller("searchController", function ($scope, searchService) {

    //定义提交到后台的对象
    $scope.searchMap = {"keywords":""};

    //搜索
    $scope.search = function () {

        searchService.search($scope.searchMap).success(function (reponse) {
            $scope.resultMap = reponse;
        });

    };

});