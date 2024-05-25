const app = Vue.createApp({
    data() {
        return {
            shared: shared,
            error: null,
            newName: '',
            roomName: '',
            roomId: '',
            newFilter: '',
            newLocation: '',
            newStatus: 'DISPONIVEL',
            room: null,
            list: [],
            filters: [],
            roomFilters: [],
            currentPage: 1,
            totalPages: 0
        };
    },
    methods: {
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
            if (this.room) {
                await this.updateRoom();
            } else {
                await this.addRoom();
            }
        },
        async addRoom() {
            const data = await this.request("/Almoxarifado/api/rooms", "POST", {
                name: this.newName,
                location: this.newLocation,
                status: this.newStatus
            });
            this.loadList(this.currentPage);
        },
        async addRoomFilter() {
            const data = await this.request("/Almoxarifado/api/filters_room", "POST", {
                room: this.roomId,
                filter: this.newFilter
            });
            this.loadList(this.currentPage);
        },
        async removeRoom(id) {
            try {
                const data = await this.request("/Almoxarifado/api/rooms?id=" + id, "DELETE");
                if (data) {
                    await this.loadList(this.currentPage);
                }
            } catch (error) {
                console.error("Erro ao excluir a Sala:", error);
            }
        },
        async removeFilter(id) {
            try {
                const data = await this.request("/Almoxarifado/api/filters_room?id=" + id, "DELETE");
                if (data) {
                    await this.loadList(this.currentPage);
                }
            } catch (error) {
                console.error("Erro ao excluir a Sala:", error);
            }
        },
        async updateRoom() {
            const index = this.list.findIndex(item => item.rowid === this.room.rowid);
            if (index !== -1) {
                this.list[index] = {
                    ...this.list[index],
                    name: this.newName,
                    location: this.newLocation,
                    status: this.newStatus
                };
            }
            const data = await this.request(`/Almoxarifado/api/rooms?id=` + (this.room.rowid), "PUT", {
                name: this.newName,
                location: this.newLocation,
                status: this.newStatus
            });
            this.loadList(this.currentPage);
            this.resetForm();
            this.room = null;
        },
        async loadList(page = 1) {
            const data = await this.request(`/Almoxarifado/api/rooms?page=${page}`, "GET");
            if (data) {
                this.list = data.list;
                this.totalPages = Math.ceil(data.total / 5);
            }
            const dataRF = await this.request(`/Almoxarifado/api/filters_room`, "GET");
            if (dataRF) {
                this.roomFilters = dataRF.list;
            }
            const dataF = await this.request(`/Almoxarifado/api/filters`, "GET");
            if (dataF) {
                this.filters = dataF.list;
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
        getRoomFilters(roomId) {
            return this.roomFilters.filter(filter => filter.roomid === roomId);
        },
        setVariables(room) {
            if (room) {
                this.room = {...room};
                this.newName = this.room.name;
                this.newLocation = this.room.location;
                this.newStatus = this.room.status;
            } else {
                this.resetForm();
            }
        },
        updateInputName(item) {
            this.roomId = item.rowid;
            this.roomName = item.name;
        },
        resetForm() {
            this.newName = '';
            this.newLocation = '';
            this.newStatus = 'DISPONIVEL';
        },
        resetForm2() {
            this.newName = '';
            this.newLocation = '';
            this.newStatus = 'DISPONIVEL';
        }
    },
    mounted() {
        this.loadList();
    }
});
app.mount('#app');
