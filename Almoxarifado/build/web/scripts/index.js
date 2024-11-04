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
            searchDate: '',
            searchEmployee: '',
            searchSubject: '',
            isReservations: false,
            focusedCard: null,
            room: null,
            selectedReservation: null,
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
        reservationsGroup() {
            return this.groupedReservations();
        }

    },
    methods: {
        getRoomStatusClass(room) {
            const currentDateTime = new Date();

            // Encontra a reserva ativa para a sala atual
            const activeReservation = this.reservations.find(reservation => {
                // Verifica se a reserva é para o mesmo quarto e está ativa
                if (reservation.roomid !== room.rowid || reservation.active !== 1)
                    return false;

                // Divide a string para obter data e hora de início e de término
                const [startDate, startTime] = reservation.start.split(' - ').slice(1);
                const [endDate, endTime] = reservation.end.split(' - ').slice(1);

                // Converte data e hora de início para objeto Date
                const [startDay, startMonth, startYear] = startDate.split('/').map(Number);
                const [startHour, startMinute] = startTime.split(':').map(Number);
                const startDateTime = new Date(startYear, startMonth - 1, startDay, startHour, startMinute);

                // Converte data e hora de término para objeto Date
                const [endDay, endMonth, endYear] = endDate.split('/').map(Number);
                const [endHour, endMinute] = endTime.split(':').map(Number);
                const endDateTime = new Date(endYear, endMonth - 1, endDay, endHour, endMinute);

                // Retorna true se a data e hora atuais estão dentro do intervalo da reserva
                return currentDateTime >= startDateTime && currentDateTime <= endDateTime;
            });

            if (activeReservation) {
                this.selectedReservation = activeReservation; // Armazena a reserva ativa
                return 'custom-reservation';
            } else if (room.status === 'DISPONIVEL') {
                return 'card-available';
            } else {
                return 'card-unavailable';
            }
        },

        async acceptReservation(reservation) {
            const response = await this.request(`/Almoxarifado/api/reservations?id=${reservation.rowid}`, "PUT", {
                active: 0
            });
            if (response) {
                this.newEmployee = reservation.employeeid;
                this.newRoom = reservation.roomid;
                this.newSubject = reservation.subject;

                await this.addKey();
                await this.loadReservations();
                
                this.reservations = this.reservations.filter(r => r.rowid !== reservation.rowid);
                this.selectedReservation = null;
            } else {
                console.error("Erro ao aceitar a reserva:", this.error);
            }
        },

        async rejectReservation(reservation) {
            const response = await this.request(`/Almoxarifado/api/reservations?id=${reservation.rowid}`, "PUT", {
                active: 0
            });
            if (response) {
                await this.loadReservations();
                
                this.reservations = this.reservations.filter(r => r.rowid !== reservation.rowid);
                this.selectedReservation = null;
            } else {
                console.error("Erro ao recusar a reserva:", this.error);
            }
        },
        groupedReservations() {
            const currentDate = new Date();

            const filteredReservations = this.reservations.filter(reservation => {
                const endDateTimeStr = reservation.end;

                const parts = endDateTimeStr.split(' - ');

                if (parts.length < 3) {
                    console.error("Formato inválido da string de data/hora.");
                    return false;
                }

                const datePart = parts[1];
                const timePart = parts[2];

                const [endDay, endMonth, endYear] = datePart.split('/').map(Number);

                const [endHour, endMinute] = timePart.split(':').map(Number);

                if (isNaN(endDay) || isNaN(endMonth) || isNaN(endYear) || isNaN(endHour) || isNaN(endMinute)) {
                    console.error("Um dos valores é inválido.");
                    return false;
                }

                const endDate = new Date(endYear, endMonth - 1, endDay, endHour || 23, endMinute || 59);

                return currentDate <= endDate;
            });
            const grouped = filteredReservations.reduce((groups, reservation) => {
                if (!groups[reservation.roomName]) {
                    groups[reservation.roomName] = [];
                }
                groups[reservation.roomName].push(reservation);
                return groups;
            }, {});

            Object.keys(grouped).forEach(roomName => {
                grouped[roomName].sort((a, b) => {
                    const [dayA, monthA, yearA] = a.start.split(' - ')[1].split(' ')[0].split('/').map(Number);
                    const timeA = a.start.split(' - ')[1].split(' ')[1] || '00:00';
                    const [hourA, minuteA] = timeA.split(':').map(Number);
                    const dateA = new Date(yearA, monthA - 1, dayA, hourA, minuteA);

                    const [dayB, monthB, yearB] = b.start.split(' - ')[1].split(' ')[0].split('/').map(Number);
                    const timeB = b.start.split(' - ')[1].split(' ')[1] || '00:00';
                    const [hourB, minuteB] = timeB.split(':').map(Number);
                    const dateB = new Date(yearB, monthB - 1, dayB, hourB, minuteB);

                    return dateB - dateA;
                });
            });

            return Object.keys(grouped)
                    .sort((a, b) => a.localeCompare(b))
                    .reduce((sortedGroups, roomName) => {
                        sortedGroups[roomName] = grouped[roomName];
                        return sortedGroups;
                    }, {});
        },
        ChangeScreen() {
            this.isReservations = !this.isReservations;
            console.log(this.isReservations);
        },
        groupByEmployee(reservationsGroup) {
            return reservationsGroup.reduce((groups, reservation) => {
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
        async loadReservation() {
            search = "true";
            console.log(this.searchDate);
            const dataRE = await this.request(`/Almoxarifado/api/reservations?employee=${this.searchEmployee}&subject=${this.searchSubject}&date=${this.searchDate}&search=${search}`, "GET");
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
