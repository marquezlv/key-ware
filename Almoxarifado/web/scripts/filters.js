const app = Vue.createApp({
    data() {
        return {
            shared: shared,
            error: null,
            newType: '',
            newDesc: '',
            filter: null,
            list: [],
            currentPage: 1,
            totalPages: 0
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
        async insertOrUpdate() {
            if (this.filter) {
                await this.updateFilter();
            } else {
                await this.addFilter();
            }
        },
        async addFilter() {
            const data = await this.request("/Almoxarifado/api/filters", "POST", {
                type: this.newType,
                desc: this.newDesc
            });
            this.loadList(this.currentPage);
        },
        async removeFilter(id) {
            try {
                const data = await this.request("/Almoxarifado/api/filters?id=" + id, "DELETE");
                if (data) {
                    await this.loadList(this.currentPage);
                }
            } catch (error) {
                console.error("Erro ao excluir o Filtro:", error);
            }
        },
        async updateFilter() {
            const index = this.list.findIndex(item => item.rowid === this.filter.rowid);
            if (index !== -1) {
                this.list[index] = {
                    ...this.list[index],
                    type: this.newType,
                    desc: this.newDesc
                };
            }
            const data = await this.request(`/Almoxarifado/api/filters?id=` + (this.filter.rowid), "PUT", {
                type: this.newType,
                desc: this.newDesc
            });
            this.loadList(this.currentPage); 
            this.resetForm();
            this.filter = null;
        },
        async loadList(page = 1) {
            const data = await this.request(`/Almoxarifado/api/filters?page=${page}`, "GET");
            if (data) {
                this.list = data.list;
                this.totalPages = Math.ceil(data.total / 5);
        }
        },

        pagination() {
            const pages = [];
            const maxPagesToShow = 7;
            if (this.totalPages <= maxPagesToShow) {
                for (let i = 1; i <= this.totalPages; i++) {
                    pages.push(i);
                }
            } else {
                pages.push(1);
                let start = Math.max(2, this.currentPage - 2);
                let end = Math.min(this.totalPages - 1, this.currentPage + 2);
                if (this.currentPage > this.totalPages - Math.floor(maxPagesToShow / 2)) {
                    start = this.totalPages - maxPagesToShow + 2;
                    end = this.totalPages - 1;
                } else if (this.currentPage <= Math.floor(maxPagesToShow / 2)) {
                    start = 2;
                    end = maxPagesToShow - 1;
                }
                if (start > 2) {
                    pages.push('prevJump');
                }
                for (let i = start; i <= end; i++) {
                    pages.push(i);
                }
                if (end < this.totalPages - 1) {
                    pages.push('nextJump');
                }
                pages.push(this.totalPages);
            }
            return pages;
        },

        previousPage() {
            if (this.currentPage > 1) {
                this.currentPage--;
                this.loadList(this.currentPage);
            }
        },
        nextPage() {
            if (this.currentPage < this.totalPages) {
                this.currentPage++;
                this.loadList(this.currentPage);
            }
        },
        goToPage(page) {
            this.currentPage = page;
            this.loadList(page);
        },
        jumpPages(pages) {
            this.currentPage = Math.min(this.totalPages, Math.max(1, this.currentPage +
                    pages));
            this.loadList(this.currentPage);
        },

        setVariables(filter) {
            if (filter) {
                this.filter = {...filter};
                this.newType = this.filter.type;
                this.newDesc = this.filter.desc;
            } else {
                this.resetForm();
            }
        },
        resetForm() {
            this.newType = '';
            this.newDesc = '';
        }
    },
    mounted() {
        this.loadList();
    }
});
app.mount('#app');
