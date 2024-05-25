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
            totalPagesCourse: 0
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
            this.loadListSub(this.currentPageSub);
        },
        async addCourse() {
            const data = await this.request("/Almoxarifado/api/courses", "POST", {
                name: this.CourseAdd
            });
            this.loadListCourse(this.currentPageCourse);
        },
        async removeSubject(id) {
            try {
                const data = await this.request("/Almoxarifado/api/subjects?id=" + id, "DELETE");
                if (data) {
                    await this.loadListSub(this.currentPageSub);
                }
            } catch (error) {
                console.error("Erro ao excluir a Materia:", error);
            }
        },
        async removeCourse(id) {
            try {
                const data = await this.request("/Almoxarifado/api/courses?id=" + id, "DELETE");
                if (data) {
                    await this.loadListCourse(this.currentPageCourse);
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
            this.loadListSub(this.currentPageSub);
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
            this.loadListCourse(this.currentPageCourse);
        },
        async loadListSub(page = 1) {
            const data = await this.request(`/Almoxarifado/api/subjects?page=${page}`, "GET");
            if (data) {
                this.list = data.list;
                this.totalPagesSub = Math.ceil(data.total / 5);
            }
        },
        async loadListCourse(page = 1) {
            const dataC = await this.request(`/Almoxarifado/api/courses?page=${page}`, "GET");
            if (dataC) {
                this.course = dataC.list;
                this.totalPagesCourse = Math.ceil(dataC.total / 5);
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
                this.loadListSub(this.currentPageSub);
            }
        },
        nextPageSub() {
            if (this.currentPageSub < this.totalPagesSub) {
                this.currentPageSub++;
                this.loadListSub(this.currentPageSub);
            }
        },
        goToPageSub(page) {
            this.currentPageSub = page;
            this.loadListSub(page);
        },
        jumpPagesSub(pages) {
            this.currentPageSub = Math.min(this.totalPagesSub, Math.max(1, this.currentPageSub +
                    pages));
            this.loadListSub(this.currentPageSub);
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
                this.loadListCourse(this.currentPageCourse);
            }
        },
        nextPageCourse() {
            if (this.currentPageCourse < this.totalPagesCourse) {
                this.currentPageCourse++;
                this.loadListCourse(this.currentPageCourse);
            }
        },
        goToPageCourse(page) {
            this.currentPageCourse = page;
            this.loadListCourse(page);
        },
        jumpPagesCourse(pages) {
            this.currentPageCourse = Math.min(this.totalPagesCourse, Math.max(1, this.currentPageCourse +
                    pages));
            this.loadListCourse(this.currentPageCourse);
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
