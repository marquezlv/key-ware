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
        groupedReservations() {
            const currentDate = new Date();
            currentDate.setHours(0, 0, 0, 0); // Ignorar tempo, focando apenas na data

            // Filtrando reservas com base no 'start' para serem apenas atuais ou futuras
            const filteredReservations = this.reservations.filter(reservation => {
                const startDateStr = reservation.start.split(' - ')[1]; // "12/09/2024"
                const [day, month, year] = startDateStr.split('/').map(Number);
                const startDate = new Date(year, month - 1, day);
                return startDate >= currentDate;
            });

            // Agrupando as reservas por nome da sala
            const grouped = filteredReservations.reduce((groups, reservation) => {
                if (!groups[reservation.roomName]) {
                    groups[reservation.roomName] = [];
                }
                groups[reservation.roomName].push(reservation);
                return groups;
            }, {});

            // Ordenando as reservas dentro de cada grupo por data em ordem decrescente
            Object.keys(grouped).forEach(roomName => {
                grouped[roomName].sort((a, b) => {
                    // Extraindo as datas de início
                    const [dayA, monthA, yearA] = a.start.split(' - ')[1].split('/').map(Number);
                    const dateA = new Date(yearA, monthA - 1, dayA);

                    const [dayB, monthB, yearB] = b.start.split(' - ')[1].split('/').map(Number);
                    const dateB = new Date(yearB, monthB - 1, dayB);

                    // Ordenando por data decrescente (mais distante primeiro)
                    return dateB - dateA;
                });
            });

            // Ordenando as salas em ordem alfabética
            return Object.keys(grouped)
                    .sort((a, b) => a.localeCompare(b)) // Ordena alfabeticamente pelo nome da sala
                    .reduce((sortedGroups, roomName) => {
                        sortedGroups[roomName] = grouped[roomName];
                        return sortedGroups;
                    }, {});
        }

    },
    methods: {
        ChangeScreen() {
            this.isReservations = !this.isReservations;
            console.log(this.isReservations);
        },
        groupByEmployee(reservationsGroup) {
            return reservationsGroup.reduce((groups, reservation) => {
                // Agrupa reservas por nome do funcionário
                if (!groups[reservation.employee]) {
                    groups[reservation.employee] = [];
                }
                groups[reservation.employee].push(reservation);
                return groups;
            }, {});
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
