const app = Vue.createApp({
    data() {
        return {
            shared: shared,
            error: null,
            newEmployee: '',
            newRoom: '',
            newSubject: "",
            roomName: '',
            roomLocation: '',
            roomStatus: '',
            isReservations: false,
            focusedCard: null,
            room: null,
            list: [],
            rooms: [],
            employees: [],
            subjects: [],
            key: [],
            roomFilters: [],
            filters: [],
            reservations: []
        };
    },
    computed: {
        uniqueLocations() {
            return [...new Set(this.rooms.map(room => room.location))];
        },
        groupedReservationsByRoom() {
            return this.reservations.reduce((group, reservation) => {
                (group[reservation.roomName] = group[reservation.roomName] || []).push(reservation);
                return group;
            }, {});
        }
    },
    methods: {
        limitedReservations(reservations) {
            return reservations.slice(0, 5).sort((a, b) => new Date(b.start) - new Date(a.start));
        },
        ChangeScreen(){
            this.isReservations = !this.isReservations;
            console.log(this.isReservations);
        },
        async request(url = "", method, data) {
            try {
                const response = await fetch(url, {
                    method: method,
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify(data)
                });
                if (response.ok) {
                    return response.json();
                } else {
                    this.error = response.statusText;
                }
            } catch (e) {
                this.error = e.message;
            }
            return null;
        },
        async addKey() {
            if (!this.newEmployee) {
                console.error("Um funcionario é necessario para pegar a chave");
                return;
            }
            const currentDateTime = new Date().toISOString();
            const subjectToSend = this.newSubject || 0;
            const data = await this.request("/Almoxarifado/api/keys", "POST", {
                room: this.newRoom,
                employee: this.newEmployee,
                subject: subjectToSend,
                start: currentDateTime,
                user: this.shared.session.id
            });
            if (data) {
                this.loadList();
            }
        },
        async returnKey(id, room, employee, subject) {
            try {
                const data = await this.request(`/Almoxarifado/api/keys?id=${id}&room=${room}&employee=${employee}&subject=${subject}&user=${this.shared.session.id}`, "DELETE");
                if (data) {
                    await this.loadList();
                }
            } catch (error) {
                console.error("Erro ao devolver chave:", error);
            }
        },
        async updateRoom() {
            const index = this.list.findIndex(item => item.rowid === this.room.rowid);
            if (index !== -1) {
                this.list[index] = {
                    ...this.list[index],
                    name: this.roomName,
                    location: this.roomLocation,
                    status: this.roomStatus
                };
            }
            const data = await this.request(`/Almoxarifado/api/rooms?id=` + (this.room.rowid), "PUT", {
                name: this.roomName,
                location: this.roomLocation,
                status: this.roomStatus
            });
            this.loadList();
            this.resetForm();
            this.room = null;
        },
        async loadList() {
            const dataR = await this.request(`/Almoxarifado/api/rooms`, "GET");
            if (dataR) {
                this.rooms = dataR.list.map(room => ({
                        ...room,
                        returnKey: false
                    }));
            }
            const dataE = await this.request(`/Almoxarifado/api/employees`, "GET");
            if (dataE) {
                this.employees = dataE.list;
            }
            const dataK = await this.request(`/Almoxarifado/api/keys`, "GET");
            if (dataK) {
                this.key = dataK.list;
            }
            const dataRF = await this.request(`/Almoxarifado/api/filters_room?column=${0}&sort=${0}`, "GET");
            if (dataRF) {
                this.roomFilters = dataRF.list;
            }
            const dataRE = await this.request(`/Almoxarifado/api/reservations`, "GET");
            if (dataRE) {
                this.reservations = dataRE.list;
            }
            console.log(this.reservations);
        },
        async getSubjects() {
            const dataS = await this.request(`/Almoxarifado/api/employee_subject`, "GET");
            if (dataS) {
                this.subjects = dataS.list.filter(subject => subject.employee === this.newEmployee);
            }
            console.log(this.subjects);
        },
        getRoomFilters(roomId) {
            this.filters = this.roomFilters.filter(filter => filter.roomid === roomId);
        },
        getKey(id) {
            return this.key.filter(filter => filter.room === id);
        },
        viewRoom(room) {
            this.room = room;
            this.roomid = room.rowid;
            this.roomName = room.name;
            this.roomLocation = room.location;
            this.roomStatus = room.status;
            this.getRoomFilters(room.rowid);
        },
        updateInputName(room) {
            this.roomName = room.name;
            this.newRoom = room.rowid;
        },
        resetKey() {
            this.newRoom = '';
            this.newEmployee = '';
            this.roomName = '';
        },
        resetForm() {
            this.room = null;
            this.roomName = '';
            this.roomLocation = '';
            this.roomStatus = '';
        }
    },
    mounted() {
        this.loadList();
    }
});
app.mount('#app');
