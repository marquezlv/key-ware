const app = Vue.createApp({
    data() {
        return {
            shared: shared,
            error: null,
            newRole: 'PROFESSOR',
            newLogin: '',
            newName: '',
            newPassword: '',
            userId: 0,
            list: [],
            user: null,
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
            if (this.user) {
                await this.updateUser();
            } else {
                await this.addUser();
            }
        },
        async loadList(page = 1,  column = 0, sort = 1) {
            const data = await this.request(`/Almoxarifado/api/users?page=${page}&items=${this.itemsPerPage}&column=${column}&sort=${sort}`, "GET");
            if (data) {
                this.list = data.list;
                this.totalPages = Math.ceil(data.total / this.itemsPerPage);
            }
        },
        async addUser() {
            const data = await this.request("/Almoxarifado/api/users", "POST", {
                login: this.newLogin,
                name: this.newName,
                role: this.newRole,
                password: this.newPassword
            });
            this.loadList(this.currentPage, this.column, this.direction);
        },
        async updateUser() {
            const index = this.list.findIndex(item => item.rowid === this.user.rowid);
            if (index !== -1) {
                this.list[index] = {
                    ...this.list[index],
                    name: this.newName,
                    login: this.newLogin,
                    role: this.newRole,
                    password: this.newPassword
                };
            }
            const data = await this.request(`/Almoxarifado/api/users?id=${this.userId}`, "PUT", {
                name: this.newName,
                login: this.newLogin,
                role: this.newRole,
                password: this.newPassword
            });
            this.loadList(this.currentPage, this.column, this.direction);
            this.resetForm();
            this.user = null;
        },
        async removeUser(id) {
            try {
                const confirmDelete = confirm("Tem certeza que deseja excluir este usuário?");
                if (!confirmDelete) {
                    return;
                }

                const data = await this.request("/Almoxarifado/api/users?id=" + id, "DELETE");
                if (data) {
                    await this.loadList(this.currentPage, this.column, this.direction);
                }
            } catch (error) {
                console.error("Erro ao excluir o usuário:", error);
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
                this.loadList(this.currentPage, this.column, this.direction);
            }
        },
        nextPage() {
            if (this.currentPage < this.totalPages) {
                this.currentPage++;
                this.loadList(this.currentPage, this.column, this.direction);
            }
        },
        goToPage(page) {
            this.currentPage = page;
            this.loadList(page, this.column, this.direction);
        },
        jumpPages(pages) {
            this.currentPage = Math.min(this.totalPages, Math.max(1, this.currentPage +
                    pages));
            this.loadList(this.currentPage, this.column, this.direction);
        },
        setVariables(user) {
            if (user) {
                this.user = {...user};
                this.newRole = this.user.role;
                this.newLogin = this.user.login;
                this.newName = this.user.name;
                this.userId = this.user.rowid;
            } else {
                this.resetForm();
            }
        }
        ,
        resetForm() {
            this.newRole = 'PROFESSOR';
            this.newLogin = '';
            this.newName = '';
            this.newPassword = '';
            this.rowId = '';
        }
    },
    mounted() {
        this.loadList();
    }
});
app.mount('#app');
