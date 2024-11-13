const app = Vue.createApp({
    data() {
        return {
            shared: shared,
            error: null,
            newType: '',
            newDesc: '',
            search: '',
            filter: null,
            list: [],
            currentPage: 1,
            totalPages: 0,
            itemsPerPage: 5,
            direction: 0,
            column: 0
        };
    },
    methods: {
        filterList(column){
            if(this.direction === 0){
                this.direction = 1;
            } else if(this.direction === 1){
                this.direction = 2;
            } else{
                this.direction = 0;
            }
            this.column = column;
            this.loadList(this.currentPage, this.column, this.direction);
        },
        reloadPage(){
            this.currentPage = 1;
            this.loadList(this.currentPage, this.column ,this.direction);
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
            this.loadList(this.currentPage, this.column ,this.direction);
        },
        async removeFilter(id) {
            try {
                const data = await this.request("/Almoxarifado/api/filters?id=" + id, "DELETE");
                if (data) {
                    await this.loadList(this.currentPage, this.column ,this.direction);
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
            this.loadList(this.currentPage, this.column ,this.direction); 
            this.resetForm();
            this.filter = null;
        },
        async loadList(page = 1, column = 0, sort = 1) {
            const data = await this.request(`/Almoxarifado/api/filters?page=${page}&items=${this.itemsPerPage}&column=${column}&sort=${sort}&search=${this.search}`, "GET");
            if (data) {
                this.list = data.list;
                this.totalPages = Math.ceil(data.total / this.itemsPerPage);
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
                this.loadList(this.currentPage, this.column ,this.direction);
            }
        },
        nextPage() {
            if (this.currentPage < this.totalPages) {
                this.currentPage++;
                this.loadList(this.currentPage, this.column ,this.direction);
            }
        },
        goToPage(page) {
            this.currentPage = page;
            this.loadList(page, this.column ,this.direction);
        },
        jumpPages(pages) {
            this.currentPage = Math.min(this.totalPages, Math.max(1, this.currentPage +
                    pages));
            this.loadList(this.currentPage, this.column ,this.direction);
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
