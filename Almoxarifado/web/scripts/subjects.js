const app = Vue.createApp({
    data() {
        return {
            shared: shared,
            error: null,
            newName: '',
            newCourse: '',
            newPeriod: '',
            CourseAdd: '',
            subject: null,
            editCourse: null,
            list: [],
            course: [],
            currentPageSub: 1,
            currentPageCourse: 1,
            totalPagesSub: 0,
            totalPagesCourse: 0,
            itemsPerPageSub: 5,
            directionSub: 0,
            columnSub: 0,
            itemsPerPageCourse: 5,
            directionCourse: 0,
            columnCourse: 0
        };
    },
    computed: {
        isFormValid() {
            return this.newName && this.newCourse && this.newPeriod;
        }
    },
    methods: {
        filterListSub(column){
            if(this.directionSub === 0){
                this.directionSub = 1;
            } else if(this.directionSub === 1){
                this.directionSub = 2;
            } else{
                this.directionSub = 0;
            }
            this.columnSub = column;
            this.loadListSub(this.currentPageSub, this.columnSub, this.directionSub);
        },
        reloadPageSub(){
            this.currentPageSub = 1;
            this.loadListSub(this.currentPageSub, this.columnSub ,this.directionSub);
        },
        filterListCourse(column){
            if(this.directionCourse === 0){
                this.directionCourse = 1;
            } else if(this.directionCourse === 1){
                this.directionCourse = 2;
            } else{
                this.directionCourse = 0;
            }
            this.columnCourse = column;
            this.loadListCourse(this.currentPageCourse, this.columnCourse, this.directionCourse);
        },
        reloadPageCourse(){
            this.currentPageCourse = 1;
            this.loadListCourse(this.currentPageCourse, this.columnCourse ,this.directionCourse);
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
            if (this.subject) {
                await this.updateSubject();
            } else {
                await this.addSubject();
            }
        },
        async insertOrUpdateCourse() {
            if (this.editCourse) {
                await this.updateCourse();
            } else {
                await this.addCourse();
            }
        },
        async addSubject() {
            const data = await this.request("/Almoxarifado/api/subjects", "POST", {
                name: this.newName,
                course: this.newCourse,
                period: this.newPeriod
            });
            this.loadListSub(this.currentPageSub, this.columnSub, this.directionSub);
        },
        async addCourse() {
            const data = await this.request("/Almoxarifado/api/courses", "POST", {
                name: this.CourseAdd
            });
            this.loadListCourse(this.currentPageCourse, this.columnCourse, this.directionCourse);
        },
        async removeSubject(id) {
            try {
                const data = await this.request("/Almoxarifado/api/subjects?id=" + id, "DELETE");
                if (data) {
                    await this.loadListSub(this.currentPageSub, this.columnSub, this.directionSub);
                }
            } catch (error) {
                console.error("Erro ao excluir a Materia:", error);
            }
        },
        async removeCourse(id) {
            try {
                const data = await this.request("/Almoxarifado/api/courses?id=" + id, "DELETE");
                if (data) {
                    await this.loadListCourse(this.currentPageCourse, this.columnCourse, this.directionCourse);
                }
            } catch (error) {
                console.error("Erro ao excluir o Curso:", error);
            }
        },
        async updateSubject() {
            const index = this.list.findIndex(item => item.rowid === this.subject.rowid);
            if (index !== -1) {
                this.list[index] = {
                    ...this.list[index],
                    name: this.newName,
                    course: this.newCourse,
                    period: this.newPeriod
                };
            }
            const data = await this.request(`/Almoxarifado/api/subjects?id=${this.subject.rowid}`, "PUT", {
                name: this.newName,
                course: this.newCourse,
                period: this.newPeriod
            });
            this.resetForm();
            this.subject = null;
            this.loadListSub(this.currentPageSub, this.columnSub, this.directionSub);
        },
        async updateCourse() {
            const index = this.list.findIndex(item => item.rowid === this.editCourse.rowid);
            if (index !== -1) {
                this.list[index] = {
                    ...this.list[index],
                    name: this.CourseAdd
                };
            }
            const data = await this.request(`/Almoxarifado/api/courses?id=` + (this.editCourse.rowid), "PUT", {
                name: this.CourseAdd
            });
            this.resetForm();
            this.editCourse = null;
            this.loadListCourse(this.currentPageCourse, this.columnCourse, this.directionCourse);
        },
        async loadListSub(page = 1, column = 0, sort = 1) {
            const data = await this.request(`/Almoxarifado/api/subjects?page=${page}&items=${this.itemsPerPageSub}&column=${column}&sort=${sort}`, "GET");
            if (data) {
                this.list = data.list;
                this.totalPagesSub = Math.ceil(data.total / this.itemsPerPageSub);
            }
        },
        async loadListCourse(page = 1, column = 0, sort = 1) {
            const dataC = await this.request(`/Almoxarifado/api/courses?page=${page}&items=${this.itemsPerPageCourse}&column=${column}&sort=${sort}`, "GET");
            if (dataC) {
                this.course = dataC.list;
                this.totalPagesCourse = Math.ceil(dataC.total / this.itemsPerPageCourse);
            }
        },
        paginationSub() {
            const pages = [];
            const maxPagesToShow = 7;
            if (this.totalPagesSub <= maxPagesToShow) {
                for (let i = 1; i <= this.totalPagesSub; i++) {
                    pages.push(i);
                }
            } else {
                pages.push(1);
                let start = Math.max(2, this.currentPageSub - 2);
                let end = Math.min(this.totalPagesSub - 1, this.currentPageSub + 2);
                if (this.currentPageSub > this.totalPagesSub - Math.floor(maxPagesToShow / 2)) {
                    start = this.totalPagesSub - maxPagesToShow + 2;
                    end = this.totalPagesSub - 1;
                } else if (this.currentPageSub <= Math.floor(maxPagesToShow / 2)) {
                    start = 2;
                    end = maxPagesToShow - 1;
                }
                if (start > 2) {
                    pages.push('prevJump');
                }
                for (let i = start; i <= end; i++) {
                    pages.push(i);
                }
                if (end < this.totalPagesSub - 1) {
                    pages.push('nextJump');
                }
                pages.push(this.totalPagesSub);
            }
            return pages;
        },
        previousPageSub() {
            if (this.currentPageSub > 1) {
                this.currentPageSub--;
                this.loadListSub(this.currentPageSub, this.columnSub, this.directionSub);
            }
        },
        nextPageSub() {
            if (this.currentPageSub < this.totalPagesSub) {
                this.currentPageSub++;
                this.loadListSub(this.currentPageSub, this.columnSub, this.directionSub);
            }
        },
        goToPageSub(page) {
            this.currentPageSub = page;
            this.loadListSub(page, this.columnSub, this.directionSub);
        },
        jumpPagesSub(pages) {
            this.currentPageSub = Math.min(this.totalPagesSub, Math.max(1, this.currentPageSub +
                    pages));
            this.loadListSub(this.currentPageSub, this.columnSub, this.directionSub);
        },
        paginationCourse() {
            const pages = [];
            const maxPagesToShow = 7;
            if (this.totalPagesCourse <= maxPagesToShow) {
                for (let i = 1; i <= this.totalPagesCourse; i++) {
                    pages.push(i);
                }
            } else {
                pages.push(1);
                let start = Math.max(2, this.currentPageCourse - 2);
                let end = Math.min(this.totalPagesCourse - 1, this.currentPageCourse + 2);
                if (this.currentPageCourse > this.totalPagesCourse - Math.floor(maxPagesToShow / 2)) {
                    start = this.totalPagesCourse - maxPagesToShow + 2;
                    end = this.totalPagesCourse - 1;
                } else if (this.currentPageCourse <= Math.floor(maxPagesToShow / 2)) {
                    start = 2;
                    end = maxPagesToShow - 1;
                }
                if (start > 2) {
                    pages.push('prevJump');
                }
                for (let i = start; i <= end; i++) {
                    pages.push(i);
                }
                if (end < this.totalPagesCourse - 1) {
                    pages.push('nextJump');
                }
                pages.push(this.totalPagesCourse);
            }
            return pages;
        },
        previousPageCourse() {
            if (this.currentPageCourse > 1) {
                this.currentPageCourse--;
                this.loadListCourse(this.currentPageCourse, this.columnCourse, this.directionCourse);
            }
        },
        nextPageCourse() {
            if (this.currentPageCourse < this.totalPagesCourse) {
                this.currentPageCourse++;
                this.loadListCourse(this.currentPageCourse, this.columnCourse, this.directionCourse);
            }
        },
        goToPageCourse(page) {
            this.currentPageCourse = page;
            this.loadListCourse(page, this.columnCourse, this.directionCourse);
        },
        jumpPagesCourse(pages) {
            this.currentPageCourse = Math.min(this.totalPagesCourse, Math.max(1, this.currentPageCourse +
                    pages));
            this.loadListCourse(this.currentPageCourse, this.columnCourse, this.directionCourse);
        },

        setVariables(subject) {
            if (subject) {
                this.subject = {...subject};
                this.newName = this.subject.name;
                this.newCourse = this.subject.course;
                this.newPeriod = this.subject.period;
            } else {
                this.resetForm();
            }
        },
        setVariablesCourse(editCourse) {
            if (editCourse) {
                this.editCourse = {...editCourse};
                this.CourseAdd = this.editCourse.name;
            } else {
                this.resetForm();
            }
        },
        resetForm() {
            this.newName = '';
            this.newCourse = '';
            this.newPeriod = '';
            this.CourseAdd = '';
        }
    },
    mounted() {
        this.loadListSub();
        this.loadListCourse();
    }
});
app.mount('#app');
