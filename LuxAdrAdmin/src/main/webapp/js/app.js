
var CONF = {
    ADMIN_ACCESS_CHECK: {key: "ADMIN_ACCESS_CHECK"},
    ADMIN_ACCESS_KEY: {key: "ADMIN_ACCESS_KEY"}
};


Vue.use(VueMaterial.default);


Vue.component('log-entry', {
    data: function () {
        return {
            count: 0
        };
    },
    props: ['item'],
    computed: {
        time: function () {

            var d = new Date(this.item.created);
            return d.getHours() + ":" +
                    (d.getMinutes() < 10 ? "0" : "") + d.getMinutes() + ":" +
                    (d.getSeconds() < 10 ? "0" : "") + d.getSeconds() + "." +
                    d.getMilliseconds();
        }
    },
    template: '<div class="logrow"><div class="logstep">{{ time }}</div><div class="logstep">{{ item.step }}</div>&nbsp;&gt;&nbsp;{{ item.log }}</div>'
});

new Vue({
    el: '#app',
    data: {
        url: "http://localhost:8080/LuxAdrService",
        key: "ufnDMuLS7dOO51JlAtuSoszHxl0Wico4sPqR96FiNdf3reLClQNp6QFhIVPbS6Vi",
        secured: false,
        connected: false,
        progressMode: "determinate",
        testConText: "Test",
        log: [],
        working: false,
        config: [],
        steps: {zero: false, first: false, second: false, third: false, fourth: false}
    },
    created: function () {
        setInterval(this.refreshLog, 10000);
        this.refreshLog();
    },
    methods: {
        ping: function () {
            if (this.working) {
                return;
            }
            this.setWorking(true);
            var _this = this;
            fetch(_this.url + '/admin/api/action/ping', {
                method: 'POST',
                headers: {
                    'AccessKey': _this.key
                },
                mode: "cors"
            }).then(function (response) {
                if (response.ok) {
                    _this.connected = true;
                    _this.setWorking(false);
                    _this.testConText = "Test successful";
                    _this.steps.zero = true;
                } else {
                    _this.connected = false;
                    _this.setWorking(false);
                    _this.testConText = "Test failed";
                }
            }).catch(function (ex) {
                _this.connected = false;
                _this.setWorking(false);
                _this.testConText = "Test failed";
            });
        },
        setWorking: function (val) {
            this.working = val;
            if (val) {
                this.progressMode = "indeterminate";
            } else {
                this.progressMode = "determinate";
            }
        },
        refreshLog: function () {
            var _this = this;
            fetch(_this.url + '/admin/api/log', {
                headers: {
                    'AccessKey': _this.key
                },
                mode: "cors"
            }).then(function (response) {
                return response.json();
            }).then(function (l) {
                _this._data.log = l;
            });
        },
        doAction: function (action) {
            if (this.working) {
                return;
            }
            this.setWorking(true);
            var _this = this;
            fetch(_this.url + '/admin/api/action/' + action, {
                method: 'POST',
                headers: {
                    'AccessKey': _this.key
                },
                mode: "cors"
            }).then(function (response) {
                if (response.ok) {
                    _this.setWorking(false);
                } else {
                    _this.setWorking(false);
                }
            }).catch(function (ex) {
                _this.setWorking(false);
            });
        },
        importData: function () {
            this.doAction('importAddr');
            this.steps.first = true;
        },
        importGeoData: function () {
            this.doAction('importGeo');
            this.steps.second = true;
        },
        indexData: function () {
            this.doAction('index');
            this.steps.third = true;
        },
        wipeIndex: function () {
            this.doAction('wipeIndex');
        },
        getConfig: function () {
            var _this = this;
            fetch(_this.url + '/admin/api/config', {
                headers: {
                    'AccessKey': _this.key
                },
                mode: "cors"
            }).then(function (response) {
                return response.json();
            }).then(function (l) {
                _this._data.config = l;
                _this._data.secured = _this.getConfigObject(CONF.ADMIN_ACCESS_CHECK).value;
            });
        },
        getConfigObject: function (key) {
            for (var i = 0; i < this.config.length; i++) {
                var c = this.config[i];
                if (c.type === key.key) {
                    return c;
                }
            }
            return null;
        },
        saveSecure: function () {
            if (this.config.length === 0) {
                return;
            }
            this.getConfigObject(CONF.ADMIN_ACCESS_CHECK).value = this.secured === 'on' ? 'true' : 'false';
            this.getConfigObject(CONF.ADMIN_ACCESS_KEY).value = this.key;
            this.writeConfig();
        },
        writeConfig: function () {
            var _this = this;
            fetch(_this.url + '/admin/api/config/', {
                method: 'POST',
                mode: "cors",
                headers: {
                    'AccessKey': _this.key,
                    "Content-Type": "application/json; charset=utf-8"
                },
                body: JSON.stringify(_this.config)
            }).then(function (response) {
                if (response.ok) {
                    _this.setWorking(false);
                } else {
                    _this.setWorking(false);
                }
            }).catch(function (ex) {
                _this.setWorking(false);
            });
        }
    }
});

