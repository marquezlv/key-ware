const app = Vue.createApp({
    data() {
        return {
            shared: shared,
            error: null,
            newEmployee: '',
            newRoom: '',
            newLocation: '',
            newSubject: '',
            newDate: '',
            newEnd: '',
            reservation: null,
            list: [],
            rooms: [],
            employees: [],
            subjects: [],
            currentPage: 1,
            totalPages: 0,
            itemsPerPage: 5,
            direction: 0,
            column: 0,
            minDate: this.getCurrentDateTime()
        };
    },
    computed: {
        isFormValid() {
            return this.newEmployee && this.newRoom && this.newDate && this.newEnd;
        }
    },
    methods: {
        getCurrentDateTime() {
            const now = new Date();
            return now.toISOString().slice(0, 16); 
        },
        
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
            this.loadList(this.currentPage, this.column ,this.itemsPerPage);
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
            this.currentPage = Math.min(this.totalPages, Math.max(1, this.currentPage + pages));
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
        async insertOrUpdate() {
            if (this.reservation) {
                await this.updateReservation();
            } else {
                await this.addReservation();
            }
        },
        async addReservation() {
            if (!this.newEmployee) {
                console.error("Um funcionario é necessario para reservar a chave");
                return;
            }
            const subjectValue = this.newSubject ? parseInt(this.newSubject) : 0;
            const data = await this.request("/Almoxarifado/api/reservations", "POST", {
                employee: this.newEmployee,
                room: this.newRoom,
                subject: subjectValue, 
                date: this.newDate,
                end: this.newEnd
            });
            this.loadList(this.currentPage, this.column, this.direction);
        },
        async removeReservation(id) {
            try {
                const data = await this.request("/Almoxarifado/api/reservations?id=" + id, "DELETE");
                if (data) {
                    await this.loadList(this.currentPage, this.column, this.direction);
                }
            } catch (error) {
                console.error("Erro ao excluir a Sala:", error);
            }
        },
        async updateReservation() {
            const index = this.list.findIndex(item => item.rowid === this.reservation.rowid);
            if (index !== -1) {
                this.list[index] = {
                    ...this.list[index],
                    employee: this.newEmployee,
                    room: this.newRoom,
                    subject: this.newSubject,
                    date: this.newDate,
                    end: this.newEnd
                };
            }
            const data = await this.request(`/Almoxarifado/api/reservations?id=` + (this.reservation.rowid), "PUT", {
                employee: this.newEmployee,
                room: this.newRoom,
                subject: this.newSubject,
                date: this.newDate,
                end: this.newEnd
            });
            this.loadList(this.currentPage, this.column, this.direction);
            this.resetForm();
            this.reservation = null;
        },
        async loadList(page = 1, column = 0, sort = 1) {
            const data = await this.request(`/Almoxarifado/api/reservations?page=${page}&items=${this.itemsPerPage}&column=${column}&sort=${sort}`, "GET");
            if (data) {
                this.list = data.list;
                this.totalPages = Math.ceil(data.total / this.itemsPerPage);
            }
            const dataE = await this.request(`/Almoxarifado/api/employees`, "GET");
            if (dataE) {
                this.employees = dataE.list;
            }
            const dataR = await this.request(`/Almoxarifado/api/rooms`, "GET");
            if (dataR) {
                this.rooms = dataR.list;
            }
            
        },
        async getSubjects() {
            const dataS = await this.request(`/Almoxarifado/api/employee_subject`, "GET");
            if (dataS) {
                this.subjects = dataS.list.filter(subject => subject.employee === this.newEmployee);
            }
        },
        setVariables(reservation) {
            if (reservation) {
                this.reservation = {...reservation};
                this.newEmployee = this.reservation.employeeid;
                this.newSubject = this.reservation.subject;
                this.newRoom = this.reservation.roomid;
                this.newLocation = this.reservation.location;

                const formattedDateParts = this.reservation.start.split(' - ');
                const datePart = formattedDateParts[1];
                const [day, month, year] = datePart.split('/');
                const isoDateString = `${year}-${month}-${day}T00:00:00`;

                this.newDate = isoDateString;
            } else {
                this.resetForm();
            }
        },
        updateInputLocation() {
            const selectedRoomId = this.newRoom;
            const selectedRoom = this.rooms.find(room => room.rowid === selectedRoomId);
            if (selectedRoom) {
                this.newLocation = selectedRoom.location;
            }
        },
        resetForm() {
            this.newEmployee = '';
            this.newRoom = '';
            this.newSubject = '';
            this.newLocation = '';
            this.newDate = '';
            this.newEnd = '';
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
        }
    },
    mounted() {
        this.loadList();
    }
});
app.mount('#app');
