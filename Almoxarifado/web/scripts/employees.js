const app = Vue.createApp({
    data() {
        return {
            shared: shared,
            error: null,
            newType: '',
            newName: '',
            newSubject: '',
            newPeriod: '',
            employeeId: '',
            employeeName: '',
            search: '',
            searchSubject: '',
            subjects: [],
            employeeSubject: [],
            list: [],
            employee: null,
            currentPage: 1,
            totalPages: 0,
            itemsPerPage: 5,
            direction: 0,
            column: 0
        };
    },
    computed: {
        isFormValid() {
            return this.newName && this.newType;
        }
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
            if (this.employee) {
                await this.updateEmployee();
            } else {
                await this.addEmployee();
            }
        },
        async addEmployee() {
            const data = await this.request("/Almoxarifado/api/employees", "POST", {
                name: this.newName,
                type: this.newType
            });
            this.loadList(this.currentPage, this.column, this.direction);
        },
        async addEmployeeSubject() {
            const data = await this.request("/Almoxarifado/api/employee_subject", "POST", {
                employee: this.employeeId,
                subject: this.newSubject,
                period: this.newPeriod
            });
            this.loadList(this.currentPage, this.column, this.direction);
        },
        async removeEmployee(id) {
            try {
                const data = await this.request("/Almoxarifado/api/employees?id=" + id, "DELETE");
                if (data) {
                    await this.loadList(this.currentPage, this.column, this.direction);
                }
            } catch (error) {
                console.error("Erro ao excluir o Funcionario:", error);
            }
        },
        async removeSubject(id) {
            try {
                const data = await this.request("/Almoxarifado/api/employee_subject?id=" + id, "DELETE");
                if (data) {
                    await this.loadList(this.currentPage, this.column, this.direction);
                }
            } catch (error) {
                console.error("Erro ao excluir a Sala:", error);
            }
        },
        async updateEmployee() {
            const index = this.list.findIndex(item => item.rowid === this.employee.rowid);
            if (index !== -1) {
                this.list[index] = {
                    ...this.list[index],
                    name: this.newName,
                    type: this.newType
                };
            }
            const data = await this.request(`/Almoxarifado/api/employees?id=` + (this.employee.rowid), "PUT", {
                name: this.newName,
                type: this.newType
            });
            this.loadList(this.currentPage, this.column, this.direction);
            this.resetForm();
            this.employee = null;
        },
        async loadList(page = 1,  column = 0, sort = 1) {
            const data = await this.request(`/Almoxarifado/api/employees?page=${page}&items=${this.itemsPerPage}&column=${column}&sort=${sort}&search=${this.search}&subject=${this.searchSubject}`, "GET");
            if (data) {
                this.list = data.list;
                this.totalPages = Math.ceil(data.total / this.itemsPerPage);
            }
            const dataS = await this.request("/Almoxarifado/api/subjects", "GET");
            if (dataS) {
                this.subjects = dataS.list;
            }
            const dataES = await this.request("/Almoxarifado/api/employee_subject", "GET");
            if (dataES) {
                this.employeeSubject = dataES.list;
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
        getEmployeesSubjects(employeeId) {
            console.log(this.employeeSubject.filter(employee => employee.employee === employeeId));
            return this.employeeSubject.filter(employee => employee.employee === employeeId);
        },
        updateInputName(item) {
            this.employeeId = item.rowid;
            this.employeeName = item.name;
        },
        setVariables(employee) {
            if (employee) {
                this.employee = {...employee};
                this.newName = this.employee.name;
                this.newType = this.employee.type;
            } else {
                this.resetForm();
            }
        },
        resetForm() {
            this.newName = '';
            this.newType = '';
            this.employee = null;
        },
        resetForm2() {
            this.employeeId = '';
            this.employeeName = '';
            this.newSubject = '';
        }
    },
    mounted() {
        this.loadList();
    }
});
app.mount('#app');
