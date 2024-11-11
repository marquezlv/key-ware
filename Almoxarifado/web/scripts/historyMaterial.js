const app = Vue.createApp({
    data() {
        return {
            shared: shared,
            error: null,
            searchEmployee: "",
            searchMaterial:"",
            dateStart: "",
            dateEnd: "",
            list: [],
            currentPage: 1,
            totalPages: 0,
            itemsPerPage: 5,
            direction: 0,
            column: 0
        };
    },
    methods: {
        exportToCSV() {
            const headers = ['Funcionário', 'Material' ,'Data', 'Tipo', 'Usuário'];

            const escapeCSVValue = (value) => {
                if (!value)
                    return '';
                return `"${String(value).replace(/"/g, '""')}"`;
            };

            const rows = this.list.map(item => [
                    escapeCSVValue(item.employeeName),
                    escapeCSVValue(item.materialName),
                    escapeCSVValue(item.date),
                    escapeCSVValue(item.type),
                    escapeCSVValue(item.userName)
                ]);

            let firstDate = this.list.length > 0 ? this.list[0].date : '';
            let year = '';

            const dateMatch = firstDate.match(/\d{2}\/\d{2}\/(\d{4})/);
            if (dateMatch) {
                year = dateMatch[1];
            }

            let csvContent = headers.join(';')
                    + '\n'
                    + rows.map(row => row.join(';')).join('\n');

            const blob = new Blob([`\uFEFF${csvContent}`], {type: 'text/csv;charset=utf-8;'});

            const fileName = `history - ${year || 'sem_ano'}.csv`;

            const link = document.createElement('a');
            const url = URL.createObjectURL(blob);
            link.setAttribute('href', url);
            link.setAttribute('download', fileName);
            document.body.appendChild(link);

            link.click();

            document.body.removeChild(link);
            URL.revokeObjectURL(url);
        },
        filterList(column) {
            if (this.direction === 0) {
                this.direction = 1;
            } else if (this.direction === 1) {
                this.direction = 2;
            } else {
                this.direction = 0;
            }
            this.column = column;
            this.loadList(this.currentPage, this.column, this.direction);
        },
        reloadPage() {
            this.currentPage = 1;
            this.loadList(this.currentPage, this.column, this.direction);
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
        async loadList(page = 1, column = 0, sort = 1) {
            const data = await this.request(`/Almoxarifado/api/history_material?page=${page}&items=${this.itemsPerPage}&column=${column}&sort=${sort}&employee=${this.searchEmployee}&material=${this.searchMaterial}&dateStart=${this.dateStart}&dateEnd=${this.dateEnd}`, "GET");
            if (data) {
                this.list = data.list;
                this.totalPages = Math.ceil(data.total / this.itemsPerPage);
            }
            console.log(this.list);
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
        }
    },
    mounted() {
        this.loadList();
    }
});
app.mount('#app');
