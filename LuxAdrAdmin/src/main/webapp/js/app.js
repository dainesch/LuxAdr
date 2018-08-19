
var CONF = {
    ADMIN_ACCESS_CHECK: {key: "ADMIN_ACCESS_CHECK"},
    ADMIN_ACCESS_KEY: {key: "ADMIN_ACCESS_KEY"}
};
var MODES = {
    HOME: {title: "Welcome to LuxAdr", key: "home"},
    FIRST: {title: "First time setup", key: "first"},
    DEMO: {title: "Public API demo", key: "demo"},
    SETTINGS: {title: "Settings", key: "sett"}
};
var DEFAULT_URL = "http://localhost:8080/LuxAdrService";
var DEFAULT_KEY = "ufnDMuLS7dOO51JlAtuSoszHxl0Wico4sPqR96FiNdf3reLClQNp6QFhIVPbS6Vi";




Vue.use(VueMaterial.default);


Vue.component('log-entry', {
    props: ['item'],
    computed: {
        time: function () {

            var d = new Date(this.item.created.replace('[UTC]', ''));
            return d.getHours() + ":" +
                    (d.getMinutes() < 10 ? "0" : "") + d.getMinutes() + ":" +
                    (d.getSeconds() < 10 ? "0" : "") + d.getSeconds() + "." +
                    d.getMilliseconds();
        }
    },
    template: '<div class="logrow"><div class="logstep">{{ time }}</div><div class="logstep centerText">{{ item.step }}</div>&nbsp;&gt;&nbsp;{{ item.log }}</div>'
});



new Vue({
    el: '#app',
    data: {
        title: MODES.HOME.title,
        mode: MODES.HOME.key,
        url: DEFAULT_URL,
        key: DEFAULT_KEY,
        secured: false,
        connected: false,
        progressMode: "determinate",
        testConText: "Test",
        log: [],
        serverStatus: {},
        working: false,
        config: [],
        steps: {zero: false, first: false, second: false, third: false, fourth: false},
        settSaved: false,
        //
        corrId: 0,
        searchResults: [],
        value: '',
        searchDetails: ""
    },
    created: function () {
        // url

        if (typeof (localStorage) !== "undefined" && localStorage.getItem('url') !== null) {
            this.url = localStorage.getItem('url');
            this.key = localStorage.getItem('key');
        } else {
            this.url = location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : '') + '/LuxAdrService';
        }


        // timers
        setInterval(this.refreshLog, 10000);
        this.refreshLog();
        setInterval(this.refreshStatus, 10000);
        this.refreshStatus();
    },
    methods: {
        page: function (p) {
            this.title = p.title;
            this.mode = p.key;
        },
        toHome: function () {
            this.page(MODES.HOME);
            this.$refs.homeMenu.className = 'md-selected';
            this.$refs.firstMenu.className = '';
            this.$refs.demoMenu.className = '';
            this.$refs.settMenu.className = '';
        },
        toFirst: function () {
            this.page(MODES.FIRST);
            this.$refs.homeMenu.className = '';
            this.$refs.firstMenu.className = 'md-selected';
            this.$refs.demoMenu.className = '';
            this.$refs.settMenu.className = '';
        },
        toDemo: function () {
            this.page(MODES.DEMO);
            this.$refs.homeMenu.className = '';
            this.$refs.firstMenu.className = '';
            this.$refs.demoMenu.className = 'md-selected';
            this.$refs.settMenu.className = '';

        },
        toSett: function () {
            this.page(MODES.SETTINGS);
            this.$refs.homeMenu.className = '';
            this.$refs.firstMenu.className = '';
            this.$refs.demoMenu.className = '';
            this.$refs.settMenu.className = 'md-selected';
        },
        ping: function () {
            if (this.working) {
                return;
            }
            if (this.url.endsWith('/')) {
                this.url = this.url.substring(0, this.url.length - 2);
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
                    if (typeof (localStorage) !== "undefined") {
                        localStorage.setItem('url', _this.url);
                        localStorage.setItem('key', _this.key);
                    }
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
        refreshStatus: function () {
            var _this = this;
            fetch(_this.url + '/admin/api/config/status', {
                headers: {
                    'AccessKey': _this.key
                },
                mode: "cors"
            }).then(function (response) {
                return response.json();
            }).then(function (l) {
                _this._data.serverStatus = l;
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
            this.settSaved = false;
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
                    _this.settSaved = true;
                }
                _this.setWorking(false);

            }).catch(function (ex) {
                _this.setWorking(false);
            });
        },
        // demo
        searchAddr: function (text) {
            this.searchResults = new Promise(resolve => {

                this.corrId++;

                var _this = this;
                fetch(_this.url + '/api/v1/search', {
                    method: "POST",
                    mode: "cors",
                    headers: {
                        "Content-Type": "application/json; charset=utf-8"
                    },
                    body: JSON.stringify({
                        corrId: _this.corrId,
                        maxResults: 20,
                        value: text
                    })
                }).then(function (response) {

                    if (response.ok) {
                        return response.json();
                    }
                    resolve([]);
                }).then(function (res) {

                    if (_this.corrId === res.corrId) {
                        resolve(res.results);
                    }
                    resolve([]);
                }).catch(function (ex) {
                    resolve([]);
                });

            });
        },
        selectAddr: function (item) {
            this.value = item.value;
            this.getBuilding(item.buildingId);
        },
        getBuilding: function (id) {
            var _this = this;
            fetch(_this.url + '/api/v1/building/' + id, {
                headers: {
                    'AccessKey': _this.key
                },
                mode: "cors"
            }).then(function (response) {
                return response.json();
            }).then(function (l) {
                _this._data.searchDetails = JSON.stringify(l, null, '\t');
            });
        }
    }
});

