var app = angular.module('myApp', ['ngRoute']);
app.factory("services", ['$http', function ($http, $location, $scope) {
    var changeLocation = function (url, forceReload) {
        if (url == null) {
            return;
        }
        $scope = $scope || angular.element(document).scope();
        if (forceReload || $scope.$$phase) {
            window.location = url;
        }
        else {
            $location.path(url);
            $scope.$apply();
        }
    };
    var serviceBase = '/users/'
    var obj = {};
    obj.getUsers = function () {
        return $http.get(serviceBase);
    };
    obj.getUser = function (userID) {
        return $http.get(serviceBase + userID);
    };

    obj.insertUser = function (user, redirect) {
        return $http.post(serviceBase, user).then(function (results) {
            changeLocation(redirect);
            return results;
        });
    };

    obj.updateUser = function (id, user, redirect) {
        return $http.put(serviceBase + id, user).then(function (status) {
            changeLocation(redirect);
            return status.data;
        });
    };

    obj.deleteUser = function (id, redirect) {
        return $http.delete(serviceBase + id).then(function (status) {
            changeLocation(redirect);
            return status.data;
        });
    };

    obj.loadUsers = function (redirect) {
        return $http.post(serviceBase + 'load').then(function (results) {
            changeLocation(redirect);
            return results;
        });
    };

    return obj;
}]);

app.controller('listCtrl', function ($scope, services) {
    services.getUsers().then(function (data) {
        $scope.users = data.data;
    });
    $scope.loadUsers = function () {
        services.loadUsers('/');
    };
});

app.controller('editCtrl', function ($scope, $rootScope, $location, $routeParams, services, user) {
    var userID = ($routeParams.userID) ? parseInt($routeParams.userID) : 0;
    $rootScope.title = (userID > 0) ? 'Edit User' : 'Add User';
    $scope.buttonText = (userID > 0) ? 'Update User' : 'Add New User';
    var original = null;
    if(userID > 0) {
        original = user.data;
    original._id = userID;
    $scope.user = angular.copy(original);
    $scope.user._id = userID;
    }

    $scope.isClean = function () {
        return angular.equals(original, $scope.user);
    };

    $scope.deleteUser = function (user) {
        if (confirm("Are you sure to delete user number: " + $scope.user._id) == true)
            services.deleteUser(user.id, '/');
    };

    $scope.saveUser = function (user) {
        if (userID <= 0) {
            services.insertUser(user, '/');
        }
        else {
            services.updateUser(userID, user, '/');
        }
    };
});

app.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/', {
                title: 'Users',
                templateUrl: 'partials/users.html',
                controller: 'listCtrl'
            })
            .when('/edit-user/:userID', {
                title: 'Edit Users',
                templateUrl: 'partials/edit-user.html',
                controller: 'editCtrl',
                resolve: {
                    user: function (services, $route) {
                        var userID = $route.current.params.userID;
                        return userID == 0 ? null : services.getUser(userID);
                    }
                }
            })
            .otherwise({
                redirectTo: '/'
            });
    }]);
app.run(['$location', '$rootScope', function ($location, $rootScope) {
    $rootScope.$on('$routeChangeSuccess', function (event, current, previous) {
        $rootScope.title = (typeof current.$$route === "undefined" ) ? 'Users' : current.$$route.title;
    });
}]);