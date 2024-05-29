const app = Vue.createApp({
    data() {
        return {
            shared: shared,
            error: null,
            list: []
        };
    },
    methods: {
        async request(url = "", method, data) {
            try {
                const response = await fetch(url, {
                    method: method,
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify(data)
                });
                if (response.status === 200) {
                    return response.json();
                } else {
                    this.error = response.statusText;
                }
            } catch (e) {
                this.error = e;
                return null;
        }
        },
        async loadList() {
            const data = await this.request(`/Almoxarifado/api/keys`, "GET");
            if (data) {
                this.list = data.list;
            }
            console.log(this.list);
        }
    },
    mounted() {
        this.loadList();
    }
});
app.mount('#app');
