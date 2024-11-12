const app = Vue.createApp({
    data() {
        return {
            error: null,
            list: [],
            currentPage: 1,
            totalPages: 0,
            autoPaginationInterval: null
        };
    },
    methods: {
        startAutoPagination() {
            this.stopAutoPagination();
            this.autoPaginationInterval = setInterval(() => {
                if (this.currentPage === this.totalPages) {
                    this.currentPage = 1;
                } else {
                    this.currentPage++;
                }
                this.loadList(this.currentPage);
            }, 3000);
        },
        stopAutoPagination() {
            clearInterval(this.autoPaginationInterval);
        },
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
        async loadList(page = 1) {
            const data = await this.request(`/Almoxarifado/api/keys?page=${page}`, "GET");
            if (data) {
                this.list = data.list;
                this.totalPages = Math.ceil(data.total / 5)
            }
        }
    },
    beforeUnmount() {
        this.stopAutoPagination();
        },
    mounted() {
        this.loadList();
        this.startAutoPagination();
    }

});
app.mount('#app');
